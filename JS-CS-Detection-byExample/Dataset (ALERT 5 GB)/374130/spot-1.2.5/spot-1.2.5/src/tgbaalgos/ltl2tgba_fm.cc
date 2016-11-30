// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012, 2013, 2014 Laboratoire
// de Recherche et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006 Laboratoire
// d'Informatique de Paris 6 (LIP6), département Systèmes Répartis
// Coopératifs (SRC), Université Pierre et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include "misc/hash.hh"
#include "misc/bddlt.hh"
#include "misc/minato.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/postfix.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlvisit/mark.hh"
#include "ltlvisit/tostring.hh"
#include <cassert>
#include <memory>
#include "ltl2tgba_fm.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/scc.hh"
//#include "tgbaalgos/dotty.hh"

namespace spot
{
  using namespace ltl;

  namespace
  {


    // This should only be called on And formulae and return
    // the set of subformula that are implied by the formulas
    // already in the And.
    // If f = Ga & (b R c) & G(d & (e R (g R h)) & Xj) & Xk this
    // returns the set {a,  # implied by Ga
    //                  c,  # implied by b R c
    //                  d, e R (g R h), g R h, h, Xj # implied by G(d & ...)
    //                 }
    // Leave recurring to false on first call.
    typedef std::set<const formula*, formula_ptr_less_than> formula_set;
    void
    implied_subformulae(const formula* in, formula_set& rec,
			bool recurring = false)
    {
      const multop* f = is_And(in);
      if (!f)
	{
	  // Only recursive calls should be made with an operator that
	  // is not And.
	  assert(recurring);
	  rec.insert(in);
	  return;
	}
      unsigned s = f->size();
      for (unsigned n = 0; n < s; ++n)
	{
	  const formula* sub = f->nth(n);
	  // Recurring is set if we are under "G(...)" or "0 R (...)"
	  // or (...) W 0".
	  if (recurring)
	    rec.insert(sub);
	  if (const unop* g = is_G(sub))
	    {
	      implied_subformulae(g->child(), rec, true);
	    }
	  else if (const binop* w = is_W(sub))
	    {
	      // f W 0 = Gf
	      if (w->second() == constant::false_instance())
		implied_subformulae(w->first(), rec, true);
	    }
	  else
	    while (const binop* b = is_binop(sub, binop::R, binop::M))
	      {
		// in 'f R g' and 'f M g' always evaluate 'g'.
		sub = b->second();
		if (b->first() == constant::false_instance())
		  {
		    assert(b->op() == binop::R); // because 0 M g = 0
		    // 0 R f = Gf
		    implied_subformulae(sub, rec, true);
		    break;
		  }
		rec.insert(sub);
	      }
	}
    }

    class translate_dict;

    class ratexp_to_dfa
    {
    public:
      ratexp_to_dfa(translate_dict& dict);
      tgba_succ_iterator* succ(const formula* f);
      const formula* get_label(const formula* f, const state* s) const;
      ~ratexp_to_dfa();

    protected:
      tgba_explicit_formula* translate(const formula* f);

    private:
      translate_dict& dict_;
      std::vector<tgba_explicit_formula*> automata_;
      typedef Sgi::hash_map<const formula*, tgba_explicit_formula*,
			    formula_ptr_hash> f2a_t;
      f2a_t f2a_;
    };

    // Helper dictionary.  We represent formulae using BDDs to
    // simplify them, and then translate BDDs back into formulae.
    //
    // The name of the variables are inspired from Couvreur's FM paper.
    //   "a" variables are promises (written "a" in the paper)
    //   "next" variables are X's operands (the "r_X" variables from the paper)
    //   "var" variables are atomic propositions.
    class translate_dict
    {
    public:

      translate_dict(bdd_dict* dict, ltl_simplifier* ls, bool exprop,
		     bool single_acc)
	: dict(dict),
	  ls(ls),
	  a_set(bddtrue),
	  var_set(bddtrue),
	  next_set(bddtrue),
	  transdfa(*this),
	  exprop(exprop),
	  single_acc(single_acc)
      {
      }

      ~translate_dict()
      {
	fv_map::iterator i;
	for (i = next_map.begin(); i != next_map.end(); ++i)
	  i->first->destroy();
	dict->unregister_all_my_variables(this);

	flagged_formula_to_bdd_map::iterator j = ltl_bdd_.begin();
	// Advance the iterator before destroying previous value.
	while (j != ltl_bdd_.end())
	  j++->first.f->destroy();
      }

      bdd_dict* dict;
      ltl_simplifier* ls;
      mark_tools mt;

      typedef bdd_dict::fv_map fv_map;
      typedef std::vector<const formula*> vf_map;

      fv_map next_map;	       ///< Maps "Next" variables to BDD variables
      vf_map next_formula_map; ///< Maps BDD variables to "Next" variables

      bdd a_set;
      bdd var_set;
      bdd next_set;

      ratexp_to_dfa transdfa;
      bool exprop;
      bool single_acc;

      enum translate_flags
	{
	  flags_none = 0,
	  // Keep these bits slightly apart as we will use them as-is
	  // in the hash function for flagged_formula.
	  flags_mark_all = (1<<10),
	  flags_recurring = (1<<14),
	};

      struct flagged_formula
      {
	const formula* f;
	unsigned flags;		// a combination of translate_flags

	bool
	operator==(const flagged_formula& other) const
	{
	  return this->f == other.f && this->flags == other.flags;
	}
      };

      struct flagged_formula_hash:
	public std::unary_function<flagged_formula, size_t>
      {
	size_t
	operator()(const flagged_formula& that) const
	{
	  return that.f->hash() ^ size_t(that.flags);
	}
      };

      struct translated
      {
	bdd symbolic;
	bool has_rational:1;
	bool has_marked:1;
      };

      typedef Sgi::hash_map<flagged_formula, translated, flagged_formula_hash>
              flagged_formula_to_bdd_map;
    private:
      flagged_formula_to_bdd_map ltl_bdd_;

    public:

      int
      register_proposition(const formula* f)
      {
	int num = dict->register_proposition(f, this);
	var_set &= bdd_ithvar(num);
	return num;
      }

      int
      register_a_variable(const formula* f)
      {
	if (single_acc)
	  {
	    int num = dict->register_acceptance_variable
	      (ltl::constant::true_instance(), this);
	    a_set &= bdd_ithvar(num);
	    return num;
	  }
	// A promise of 'x', noted P(x) is pretty much like the F(x)
	// LTL formula, it ensure that 'x' will be fulfilled (= not
	// promised anymore) eventually.
	// So   a U b = ((a&Pb) W b)
	//      a U (b U c) = (a&P(b U c)) W (b&P(c) W c)
	// the latter encoding may be simplified to
	//      a U (b U c) = (a&P(c)) W (b&P(c) W c)
	//
	// Similarly
	//      a M b = (a R (b&P(a)))
	//      (a M b) M c = (a R (b & Pa)) R (c & P(a M b))
	//                  = (a R (b & Pa)) R (c & P(a & b))
	//
	// The code below therefore implement the following
	// rules:
	// P(a U b) = P(b)
	// P(F(a))  = P(a)
	// P(a M b) = P(a & b)
	//
	// The latter rule INCORRECTLY appears as P(a M b)=P(a)
	// in section 3.5 of
	//    "LTL translation improvements in Spot 1.0",
	//     A. Duret-Lutz. IJCCBS 5(1/2):31-54, March 2014.
	// and was unfortunately implemented this way until Spot
	// 1.2.4.  A counterexample is given by the formula
	//    G(Fa & ((a M b) U ((c U !d) M d)))
	// that was found by Joachim Klein.  Here P((c U !d) M d)
	// and P(c U !d) should not both be simplified to P(!d).
	for (;;)
	  {
	    if (const binop* b = is_binop(f))
	      {
		binop::type op = b->op();
		if (op == binop::U)
		  {
		    // P(a U b) = P(b)
		    f = b->second();
		  }
		else if (op == binop::M)
		  {
		    // P(a M b) = P(a & b)
		    const formula* g =
		      multop::instance(multop::And,
				       b->first()->clone(),
				       b->second()->clone());
		    int num = dict->register_acceptance_variable(g, this);
		    a_set &= bdd_ithvar(num);
		    g->destroy();
		    return num;
		  }
		else
		  {
		    break;
		  }
	      }
	    else if (const unop* u = is_unop(f, unop::F))
	      {
		// P(F(a)) = P(a)
		f = u->child();
	      }
	    else
	      {
		break;
	      }
	  }
	int num = dict->register_acceptance_variable(f, this);
	a_set &= bdd_ithvar(num);
	return num;
      }

      int
      register_next_variable(const formula* f)
      {
	int num;
	// Do not build a Next variable that already exists.
	fv_map::iterator sii = next_map.find(f);
	if (sii != next_map.end())
	  {
	    num = sii->second;
	  }
	else
	  {
	    f = f->clone();
	    num = dict->register_anonymous_variables(1, this);
	    next_map[f] = num;
	    next_formula_map.resize(bdd_varnum());
	    next_formula_map[num] = f;
	  }
	next_set &= bdd_ithvar(num);
	return num;
      }

      std::ostream&
      dump(std::ostream& os) const
      {
	fv_map::const_iterator fi;
	os << "Next Variables:" << std::endl;
	for (fi = next_map.begin(); fi != next_map.end(); ++fi)
	{
	  os << "  " << fi->second << ": Next[";
	  to_string(fi->first, os) << "]" << std::endl;
	}
	os << "Shared Dict:" << std::endl;
	dict->dump(os);
	return os;
      }

      const formula*
      var_to_formula(int var) const
      {
	const bdd_dict::bdd_info& i = dict->bdd_map[var];
	if (i.type != bdd_dict::anon)
	  {
	    assert(i.type == bdd_dict::acc || i.type == bdd_dict::var);
	    return i.f->clone();
	  }
	const formula* f = next_formula_map[var];
	assert(f);
	return f->clone();
      }

      bdd
      boolean_to_bdd(const formula* f)
      {
	bdd res = ls->as_bdd(f);
	var_set &= bdd_support(res);
	return res;
      }

      const formula*
      conj_bdd_to_formula(bdd b, multop::type op = multop::And) const
      {
	if (b == bddfalse)
	  return constant::false_instance();
	multop::vec* v = new multop::vec;
	while (b != bddtrue)
	  {
	    int var = bdd_var(b);
	    const formula* res = var_to_formula(var);
	    bdd high = bdd_high(b);
	    if (high == bddfalse)
	      {
		res = unop::instance(unop::Not, res);
		b = bdd_low(b);
	      }
	    else
	      {
		assert(bdd_low(b) == bddfalse);
		b = high;
	      }
	    assert(b != bddfalse);
	    v->push_back(res);
	  }
	return multop::instance(op, v);
      }

      const formula*
      conj_bdd_to_sere(bdd b) const
      {
	return conj_bdd_to_formula(b, multop::AndRat);
      }

      const formula*
      bdd_to_formula(bdd f)
      {
	if (f == bddfalse)
	  return constant::false_instance();

	multop::vec* v = new multop::vec;

	minato_isop isop(f);
	bdd cube;
	while ((cube = isop.next()) != bddfalse)
	  v->push_back(conj_bdd_to_formula(cube));

	return multop::instance(multop::Or, v);
      }

      const formula*
      bdd_to_sere(bdd f)
      {
	if (f == bddfalse)
	  return constant::false_instance();

	multop::vec* v = new multop::vec;

	minato_isop isop(f);
	bdd cube;
	while ((cube = isop.next()) != bddfalse)
	  v->push_back(conj_bdd_to_sere(cube));

	return multop::instance(multop::OrRat, v);
      }

      void
      conj_bdd_to_acc(tgba_explicit_formula* a, bdd b,
		      state_explicit_formula::transition* t)
      {
	assert(b != bddfalse);
	while (b != bddtrue)
	  {
	    int var = bdd_var(b);
	    bdd high = bdd_high(b);
	    if (high == bddfalse)
	      {
		// Simply ignore negated acceptance variables.
		b = bdd_low(b);
	      }
	    else
	      {
		const formula* ac = var_to_formula(var);

		if (!a->has_acceptance_condition(ac))
		  a->declare_acceptance_condition(ac->clone());
		a->add_acceptance_condition(t, ac);
		b = high;
	      }
	    assert(b != bddfalse);
	  }
      }

      const translated&
      ltl_to_bdd(const formula* f, bool mark_all, bool recurring = false);

    };

#ifdef __GNUC__
#  define unused __attribute__((unused))
#else
#  define unused
#endif

    // Debugging function.
    static unused
    std::ostream&
    trace_ltl_bdd(const translate_dict& d, bdd f)
    {
      std::cerr << "Displaying BDD ";
      bdd_print_set(std::cerr, d.dict, f) << ":" << std::endl;

      minato_isop isop(f);
      bdd cube;
      while ((cube = isop.next()) != bddfalse)
	{
	  bdd label = bdd_exist(cube, d.next_set);
	  bdd dest_bdd = bdd_existcomp(cube, d.next_set);
	  const formula* dest = d.conj_bdd_to_formula(dest_bdd);
	  bdd_print_set(std::cerr, d.dict, label) << " => ";
	  bdd_print_set(std::cerr, d.dict, dest_bdd) << " = "
						     << to_string(dest)
						     << std::endl;
	  dest->destroy();
	}
      return std::cerr;
    }



    // Gather all promises of a formula.  These are the
    // right-hand sides of U or F operators.
    class ltl_promise_visitor: public postfix_visitor
    {
    public:
      ltl_promise_visitor(translate_dict& dict)
	: dict_(dict), res_(bddtrue)
      {
      }

      virtual
      ~ltl_promise_visitor()
      {
      }

      bdd
      result() const
      {
	return res_;
      }

      using postfix_visitor::doit;

      virtual void
      doit(const unop* node)
      {
	if (node->op() == unop::F)
	  res_ &= bdd_ithvar(dict_.register_a_variable(node->child()));
      }

      virtual void
      doit(const binop* node)
      {
	if (node->op() == binop::U)
	  res_ &= bdd_ithvar(dict_.register_a_variable(node->second()));
      }

    private:
      translate_dict& dict_;
      bdd res_;
    };

    bdd translate_ratexp(const formula* f, translate_dict& dict,
			 const formula* to_concat = 0);

    // Rewrite rule for rational operators.
    class ratexp_trad_visitor: public visitor
    {
    public:
      // negated should only be set for constants or atomic properties
      ratexp_trad_visitor(translate_dict& dict,
			  const formula* to_concat = 0)
	: dict_(dict), to_concat_(to_concat)
      {
      }

      virtual
      ~ratexp_trad_visitor()
      {
	if (to_concat_)
	  to_concat_->destroy();
      }

      bdd
      result() const
      {
	return res_;
      }

      bdd next_to_concat()
      {
	// Encoding X[*0] when there is nothing to concatenate is a
	// way to ensure that we distinguish the rational formula "a"
	// (encoded as "a&X[*0]") from the rational formula "a;[*]"
	// (encoded as "a&X[*]").
	//
	// It's important that when we do "a && (a;[*])" we do not get
	// "a;[*]" as it would occur if we had simply encoded "a" as
	// "a".
	if (!to_concat_)
	  to_concat_ = constant::empty_word_instance();
	int x = dict_.register_next_variable(to_concat_);
	return bdd_ithvar(x);
      }

      bdd now_to_concat()
      {
	if (to_concat_ && to_concat_ != constant::empty_word_instance())
	  return recurse(to_concat_);

	return bddfalse;
      }

      // Append to_concat_ to all Next variables in IN.
      bdd
      concat_dests(bdd in)
      {
	if (!to_concat_)
	  return in;
	minato_isop isop(in);
	bdd cube;
	bdd out = bddfalse;
	while ((cube = isop.next()) != bddfalse)
	  {
	    bdd label = bdd_exist(cube, dict_.next_set);
	    bdd dest_bdd = bdd_existcomp(cube, dict_.next_set);
	    const formula* dest = dict_.conj_bdd_to_sere(dest_bdd);
	    if (dest == constant::empty_word_instance())
	      {
		out |= label & next_to_concat();
	      }
	    else
	      {
		const formula* dest2 = multop::instance(multop::Concat, dest,
							to_concat_->clone());
		if (dest2 != constant::false_instance())
		  {
		    int x = dict_.register_next_variable(dest2);
		    dest2->destroy();
		    out |= label & bdd_ithvar(x);
		  }
	      }
	  }
	return out;
      }

      void
      visit(const atomic_prop* node)
      {
	res_ = bdd_ithvar(dict_.register_proposition(node));
	res_ &= next_to_concat();
      }

      void
      visit(const constant* node)
      {
	switch (node->val())
	  {
	  case constant::True:
	    res_ = next_to_concat();
	    return;
	  case constant::False:
	    res_ = bddfalse;
	    return;
	  case constant::EmptyWord:
	    res_ = now_to_concat();
	    return;
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const unop* node)
      {
	switch (node->op())
	  {
	  case unop::F:
	  case unop::G:
	  case unop::X:
	  case unop::Finish:
	  case unop::Closure:
	  case unop::NegClosure:
	  case unop::NegClosureMarked:
	    assert(!"not a rational operator");
	    return;
	  case unop::Not:
	    {
	      // Not can only appear in front of Boolean
	      // expressions.
	      const formula* f = node->child();
	      assert(f->is_boolean());
	      res_ = !recurse(f);
	      res_ &= next_to_concat();
	      return;
	    }
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const bunop* bo)
      {
	const formula* f;
	unsigned min = bo->min();
	unsigned max = bo->max();

	assert(max > 0);

	unsigned min2 = (min == 0) ? 0 : (min - 1);
	unsigned max2 =
	  (max == bunop::unbounded) ? bunop::unbounded : (max - 1);

	bunop::type op = bo->op();
	switch (op)
	  {
	  case bunop::Star:
	    f = bunop::instance(op, bo->child()->clone(), min2, max2);

	    if (to_concat_)
	      f = multop::instance(multop::Concat, f, to_concat_->clone());

	    if (!bo->child()->accepts_eword())
	      {
		//   f*;g  ->  f;f*;g | g
		//
		// If f does not accept the empty word, we can easily
		// add "f*;g" as to_concat_ when translating f.
		res_ = recurse(bo->child(), f);
		if (min == 0)
		  res_ |= now_to_concat();
	      }
	    else
	      {
		// if "f" accepts the empty word, doing the above would
		// lead to an infinite loop:
		//   f*;g -> f;f*;g | g
		//   f;f*;g -> f*;g | ...
		//
		// So we do it in three steps:
		//  1. translate f,
		//  2. append f*;g to all destinations
		//  3. add |g
		res_ = recurse(bo->child());

		//   f*;g  ->  f;f*;g
		minato_isop isop(res_);
		bdd cube;
		res_ = bddfalse;
		while ((cube = isop.next()) != bddfalse)
		  {
		    bdd label = bdd_exist(cube, dict_.next_set);
		    bdd dest_bdd = bdd_existcomp(cube, dict_.next_set);
		    const formula* dest = dict_.conj_bdd_to_sere(dest_bdd);
		    int x;
		    if (dest == constant::empty_word_instance())
		      {
			x = dict_.register_next_variable(f);
			res_ |= label & bdd_ithvar(x);
		      }
		    else
		      {
			const formula*
			  dest2 = multop::instance(multop::Concat, dest,
						   f->clone());
			if (dest2 != constant::false_instance())
			  {
			    x = dict_.register_next_variable(dest2);
			    dest2->destroy();
			    res_ |= label & bdd_ithvar(x);
			  }
		      }
		  }
		f->destroy();
		res_ |= now_to_concat();
	      }
	    return;
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const binop*)
      {
	assert(!"not a rational operator");
      }

      void
      visit(const automatop*)
      {
	assert(!"not a rational operator");
      }

      void
      visit(const multop* node)
      {
	multop::type op = node->op();
	switch (op)
	  {
	  case multop::AndNLM:
	    {
	      unsigned s = node->size();
	      multop::vec* final = new multop::vec;
	      multop::vec* non_final = new multop::vec;

	      for (unsigned n = 0; n < s; ++n)
		{
		  const formula* f = node->nth(n);
		  if (f->accepts_eword())
		    final->push_back(f->clone());
		  else
		    non_final->push_back(f->clone());
		}

	      if (non_final->empty())
		{
		  delete non_final;
		  // (a* & b*);c = (a*|b*);c
		  const formula* f = multop::instance(multop::OrRat, final);
		  res_ = recurse_and_concat(f);
		  f->destroy();
		  break;
		}
	      if (!final->empty())
		{
		  // let F_i be final formulae
		  //     N_i be non final formula
		  // (F_1 & ... & F_n & N_1 & ... & N_m)
		  // =   (F_1 | ... | F_n);[*] && (N_1 & ... & N_m)
		  //   | (F_1 | ... | F_n) && (N_1 & ... & N_m);[*]
		  const formula* f =
		    multop::instance(multop::OrRat, final);
		  const formula* n =
		    multop::instance(multop::AndNLM, non_final);
		  const formula* t =
		    bunop::instance(bunop::Star, constant::true_instance());
		  const formula* ft =
		    multop::instance(multop::Concat, f->clone(), t->clone());
		  const formula* nt =
		    multop::instance(multop::Concat, n->clone(), t);
		  const formula* ftn =
		    multop::instance(multop::AndRat, ft, n);
		  const formula* fnt =
		    multop::instance(multop::AndRat, f, nt);
		  const formula* all =
		    multop::instance(multop::OrRat, ftn, fnt);
		  res_ = recurse_and_concat(all);
		  all->destroy();
		  break;
		}
	      // No final formula.
	      delete final;
	      for (unsigned n = 0; n < s; ++n)
		(*non_final)[n]->destroy();
	      delete non_final;
	      // Translate N_1 & N_2 & ... & N_n into
	      //   N_1 && (N_2;[*]) && ... && (N_n;[*])
	      // | (N_1;[*]) && N_2 && ... && (N_n;[*])
	      // | (N_1;[*]) && (N_2;[*]) && ... && N_n
	      const formula* star =
		bunop::instance(bunop::Star, constant::true_instance());
	      multop::vec* disj = new multop::vec;
	      for (unsigned n = 0; n < s; ++n)
		{
		  multop::vec* conj = new multop::vec;
		  for (unsigned m = 0; m < s; ++m)
		    {
		      const formula* f = node->nth(m)->clone();
		      if (n != m)
			f = multop::instance(multop::Concat,
					     f, star->clone());
		      conj->push_back(f);
		    }
		  disj->push_back(multop::instance(multop::AndRat, conj));
		}
	      star->destroy();
	      const formula* all = multop::instance(multop::OrRat, disj);
	      res_ = recurse_and_concat(all);
	      all->destroy();
	      break;
	    }
	  case multop::AndRat:
	    {
	      unsigned s = node->size();

	      res_ = bddtrue;
	      for (unsigned n = 0; n < s; ++n)
		{
		  bdd res = recurse(node->nth(n));
		  // trace_ltl_bdd(dict_, res);
		  res_ &= res;
		}

	      //std::cerr << "Pre-Concat:" << std::endl;
	      //trace_ltl_bdd(dict_, res_);

	      // If we have translated (a* && b*) in (a* && b*);c, we
	      // have to append ";c" to all destinations.
	      res_ = concat_dests(res_);

	      if (node->accepts_eword())
		res_ |= now_to_concat();

	      if (op == multop::AndNLM)
		node->destroy();
	      break;
	    }
	  case multop::OrRat:
	    {
	      res_ = bddfalse;
	      unsigned s = node->size();
	      for (unsigned n = 0; n < s; ++n)
		res_ |= recurse_and_concat(node->nth(n));
	      break;
	    }
	  case multop::Concat:
	    {
	      multop::vec* v = new multop::vec;
	      unsigned s = node->size();
	      v->reserve(s);
	      for (unsigned n = 1; n < s; ++n)
		v->push_back(node->nth(n)->clone());
	      if (to_concat_)
		v->push_back(to_concat_->clone());
	      res_ = recurse(node->nth(0),
			     multop::instance(multop::Concat, v));
	      break;
	    }
	  case multop::Fusion:
	    {
	      assert(node->size() >= 2);

	      // the head
	      bdd res = recurse(node->nth(0));

	      // the tail
	      const formula* tail = node->all_but(0);
	      bdd tail_bdd;
	      bool tail_computed = false;

	      //trace_ltl_bdd(dict_, res);

	      minato_isop isop(res);
	      bdd cube;
	      res_ = bddfalse;
	      while ((cube = isop.next()) != bddfalse)
		{
		  bdd label = bdd_exist(cube, dict_.next_set);
		  bdd dest_bdd = bdd_existcomp(cube, dict_.next_set);
		  const formula* dest = dict_.conj_bdd_to_sere(dest_bdd);

		  if (dest->accepts_eword())
		    {
		      // The destination is a final state.  Make sure we
		      // can also exit if tail is satisfied.
		      if (!tail_computed)
			{
			  tail_bdd = recurse(tail);
			  tail_computed = true;
			}
		      res_ |= concat_dests(label & tail_bdd);

		    }

		  // If the destination is not 0 or [*0], it means it
		  // can have successors.  Fusion the tail and append
		  // anything to concatenate.
		  if (dest->kind() != formula::Constant
		      || dest == ltl::constant::true_instance())
		    {
		      const formula* dest2 =
			multop::instance(multop::Fusion, dest, tail->clone());
		      if (to_concat_)
			 dest2 = multop::instance(multop::Concat, dest2,
						  to_concat_->clone());
		      if (dest2 != constant::false_instance())
			{
			  int x = dict_.register_next_variable(dest2);
			  dest2->destroy();
			  res_ |= label & bdd_ithvar(x);
			}
		    }
		}

	      tail->destroy();
	      break;
	    }
	  case multop::And:
	  case multop::Or:
	    assert(!"not a rational operator");
	  }
      }

      bdd
      recurse(const formula* f, const formula* to_concat = 0)
      {
	return translate_ratexp(f, dict_, to_concat);
      }

      bdd
      recurse_and_concat(const formula* f)
      {
	return translate_ratexp(f, dict_,
				to_concat_ ? to_concat_->clone() : 0);
      }

    private:
      translate_dict& dict_;
      bdd res_;
      const formula* to_concat_;
    };

    bdd
    translate_ratexp(const formula* f, translate_dict& dict,
		     const formula* to_concat)
    {
      // static unsigned indent = 0;
      // for (unsigned i = indent; i > 0; --i)
      // 	std::cerr << "| ";
      // std::cerr << "translate_ratexp[" << to_string(f);
      // if (to_concat)
      // 	std::cerr << ", " << to_string(to_concat);
      // std::cerr << "]" << std::endl;
      // ++indent;
      bdd res;
      if (!f->is_boolean())
	{
	  ratexp_trad_visitor v(dict, to_concat);
	  f->accept(v);
	  res = v.result();
	}
      else
	{
	  res = dict.boolean_to_bdd(f);
	  // See comment for similar code in next_to_concat.
	  if (!to_concat)
	    to_concat = constant::empty_word_instance();
	  int x = dict.register_next_variable(to_concat);
	  res &= bdd_ithvar(x);
	  to_concat->destroy();
	}
      // --indent;
      // for (unsigned i = indent; i > 0; --i)
      // 	std::cerr << "| ";
      // std::cerr << "\\ ";
      // bdd_print_set(std::cerr, dict.dict, res) << std::endl;
      return res;
    }


    ratexp_to_dfa::ratexp_to_dfa(translate_dict& dict)
      : dict_(dict)
    {
    }

    ratexp_to_dfa::~ratexp_to_dfa()
    {
      std::vector<tgba_explicit_formula*>::const_iterator it;
      for (it = automata_.begin(); it != automata_.end(); ++it)
	delete *it;
    }

    tgba_explicit_formula*
    ratexp_to_dfa::translate(const formula* f)
    {
      assert(f->is_in_nenoform());

      tgba_explicit_formula* a = new tgba_explicit_formula(dict_.dict);

      typedef std::set<const formula*, formula_ptr_less_than> set_type;
      set_type formulae_to_translate;

      f->clone();
      formulae_to_translate.insert(f);
      a->set_init_state(f);


      while (!formulae_to_translate.empty())
	{
	  // Pick one formula.
	  const formula* now = *formulae_to_translate.begin();
	  formulae_to_translate.erase(formulae_to_translate.begin());

	  // Translate it
	  bdd res = translate_ratexp(now, dict_);

	  // Generate (deterministic) successors
	  bdd var_set = bdd_existcomp(bdd_support(res), dict_.var_set);
	  bdd all_props = bdd_existcomp(res, dict_.var_set);
	  while (all_props != bddfalse)
	    {
	      bdd label = bdd_satoneset(all_props, var_set, bddtrue);
	      all_props -= label;

	      const formula* dest =
		dict_.bdd_to_sere(bdd_exist(res & label, dict_.var_set));

	      f2a_t::const_iterator i = f2a_.find(dest);
	      if (i != f2a_.end() && i->second == 0)
		{
		  // This state is useless.  Ignore it.
		  dest->destroy();
		  continue;
		}

	      bool seen = a->has_state(dest);
	      state_explicit_formula::transition* t =
		a->create_transition(now, dest);
	      t->condition = label;

	      if (!seen)
		formulae_to_translate.insert(dest);
	      else
		dest->destroy();
	    }
	}

      // Register all known propositions for a. This may contain
      // proposition from other parts of the formula being translated,
      // but this is not really important as this automaton will be
      // short-lived.  (Maybe it would even work without this line.)
      dict_.dict->register_propositions(dict_.var_set, a);

      //dotty_reachable(std::cerr, a);

      // The following code trims the automaton in a crude way by
      // eliminating SCCs that are not coaccessible.  It does not
      // actually remove the states, it simply marks the corresponding
      // formulae as associated to the null pointer in the f2a_ map.
      // The method succ() and get_label() interpret this as False.

      scc_map* sm = new scc_map(a);
      sm->build_map();
      unsigned scc_count = sm->scc_count();
      // Remember whether each SCC is coaccessible.
      std::vector<bool> coaccessible(scc_count);
      // SCC are numbered in topological order
      for (unsigned n = 0; n < scc_count; ++n)
	{
	  bool coacc = false;
	  const std::list<const state*>& st = sm->states_of(n);
	  // The SCC is coaccessible if any of its states
	  // is final (i.e., it accepts [*0])...
	  std::list<const state*>::const_iterator it;
	  for (it = st.begin(); it != st.end(); ++it)
	    if (a->get_label(*it)->accepts_eword())
	      {
		coacc = true;
		break;
	      }
	  if (!coacc)
	    {
	      // ... or if any of its successors is coaccessible.
	      const scc_map::succ_type& succ = sm->succ(n);
	      for (scc_map::succ_type::const_iterator i = succ.begin();
		   i != succ.end(); ++i)
		if (coaccessible[i->first])
		  {
		    coacc = true;
		    break;
		  }
	    }
	  if (!coacc)
	    {
	      // Mark all formulas of this SCC as useless.
	      for (it = st.begin(); it != st.end(); ++it)
		f2a_[a->get_label(*it)] = 0;
	    }
	  else
	    {
	      for (it = st.begin(); it != st.end(); ++it)
		f2a_[a->get_label(*it)] = a;
	    }
	  coaccessible[n] = coacc;
	}
      delete sm;
      if (coaccessible[scc_count - 1])
	{
	  automata_.push_back(a);
	  return a;
	}
      else
	{
	  delete a;
	  return 0;
	}
    }

    tgba_succ_iterator*
    ratexp_to_dfa::succ(const formula* f)
    {
      f2a_t::const_iterator it = f2a_.find(f);
      tgba_explicit_formula* a;
      if (it != f2a_.end())
	a = it->second;
      else
	a = translate(f);

      // If a is nul, f has an empty language.
      if (!a)
	return 0;

      assert(a->has_state(f));
      // This won't create a new state.
      const state* s = a->add_state(f);

      return a->succ_iter(s);
    }

    const formula*
    ratexp_to_dfa::get_label(const formula* f, const state* s) const
    {
      f2a_t::const_iterator it = f2a_.find(f);
      assert(it != f2a_.end());
      tgba_explicit_formula* a = it->second;
      assert(a != 0);

      const formula* f2 = a->get_label(s);
      f2a_t::const_iterator it2 = f2a_.find(f2);
      assert(it2 != f2a_.end());
      if (it2->second == 0)
	return constant::false_instance();

      return f2->clone();
    }


    // The rewrite rules used here are adapted from Jean-Michel
    // Couvreur's FM paper, augmented to support rational operators.
    class ltl_trad_visitor: public visitor
    {
    public:
      ltl_trad_visitor(translate_dict& dict, bool mark_all = false,
		       bool exprop = false, bool recurring = false)
	: dict_(dict), rat_seen_(false), has_marked_(false),
	  mark_all_(mark_all), exprop_(exprop), recurring_(recurring)
      {
      }

      virtual
      ~ltl_trad_visitor()
      {
      }

      void
      reset(bool mark_all)
      {
	rat_seen_ = false;
	has_marked_ = false;
	mark_all_ = mark_all;
      }

      bdd
      result() const
      {
	return res_;
      }

      const translate_dict&
      get_dict() const
      {
	return dict_;
      }

      bool
      has_rational() const
      {
	return rat_seen_;
      }

      bool
      has_marked() const
      {
	return has_marked_;
      }

      void
      visit(const atomic_prop* node)
      {
	res_ = bdd_ithvar(dict_.register_proposition(node));
      }

      void
      visit(const constant* node)
      {
	switch (node->val())
	  {
	  case constant::True:
	    res_ = bddtrue;
	    return;
	  case constant::False:
	    res_ = bddfalse;
	    return;
	  case constant::EmptyWord:
	    assert(!"Not an LTL operator");
	    return;
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const unop* node)
      {
	unop::type op = node->op();

	switch (op)
	  {
	  case unop::F:
	    {
	      // r(Fy) = r(y) + a(y)X(Fy)   if not recurring
	      // r(Fy) = r(y) + a(y)        if recurring (see comment in G)
	      const formula* child = node->child();
	      bdd y = recurse(child);
	      bdd a = bdd_ithvar(dict_.register_a_variable(child));
	      if (!recurring_)
		a &= bdd_ithvar(dict_.register_next_variable(node));
	      res_ = y | a;
	      break;
	    }
	  case unop::G:
	    {
	      // Couvreur's paper suggests that we optimize GFy
	      // as
	      //   r(GFy) = (r(y) + a(y))X(GFy)
	      // instead of
	      //   r(GFy) = (r(y) + a(y)X(Fy)).X(GFy)
	      // but this is just a particular case
	      // of the "merge all states with the same
	      // symbolic rewriting" optimization we do later.
	      // (r(Fy).r(GFy) and r(GFy) have the same symbolic
	      // rewriting, see Fig.6 in Duret-Lutz's VECOS'11
	      // paper for an illustration.)
	      //
	      // We used to keep things simple and not implement this
	      // step, that does not change the result.  However it
	      // turns out that this extra optimization significantly
	      // speeds up (≈×2) the translation of formulas of the
	      // form GFa & GFb & ... GFz
	      //
	      // Unfortunately, our rewrite rules will put such a
	      // formula as G(Fa & Fb & ... Fz) which has a different
	      // form.  We could encode specifically
	      // r(G(Fa & Fb & c)) =
	      //   (r(a)+a(a))(r(b)+a(b))r(c)X(G(Fa & Fb & c))
	      // but that would be lots of special cases for G.
	      // And if we do it for G, why not for R?
	      //
	      // Here we generalize this trick by propagating
	      // to "recurring" information to subformulas
	      // and letting them decide.

	      // r(Gy) = r(y)X(Gy)
	      int x = dict_.register_next_variable(node);
	      bdd y = recurse(node->child(), /* recurring = */ true);
	      res_ = y & bdd_ithvar(x);
	      break;
	    }
	  case unop::Not:
	    {
	      // r(!y) = !r(y)
	      res_ = bdd_not(recurse(node->child()));
	      break;
	    }
	  case unop::X:
	    {
	      // r(Xy) = Next[y]
	      // r(X(a&b&c)) = Next[a]&Next[b]&Next[c]
	      // r(X(a|b|c)) = Next[a]|Next[b]|Next[c]
	      //
	      // The special case for And is to that
	      // (p&XF!p)|(!p&XFp)|X(Fp&F!p)      (1)
	      // get translated as
	      // (p&XF!p)|(!p&XFp)|XFp&XF!p       (2)
	      // and then automatically reduced to
	      // (p&XF!p)|(!p&XFp)
	      //
	      // Formula (2) appears as an example of Boolean
	      // simplification in Wring, but our LTL rewriting
	      // rules tend to rewrite it as (1).
	      //
	      // The special case for Or follows naturally, but it's
	      // effect is less clear.  Benchmarks show that it
	      // reduces the number of states and transitions, but it
	      // increases the number of non-deterministic states...
	      const formula* y = node->child();
	      if (const multop* m = is_And(y))
		{
		  res_ = bddtrue;
		  unsigned s = m->size();
		  for (unsigned n = 0; n < s; ++n)
		    {
		      int x = dict_.register_next_variable(m->nth(n));
		      res_ &= bdd_ithvar(x);
		    }
		}
#if 0
	      else if (const multop* m = is_Or(y))
		{
		  res_ = bddfalse;
		  unsigned s = m->size();
		  for (unsigned n = 0; n < s; ++n)
		    {
		      int x = dict_.register_next_variable(m->nth(n));
		      res_ |= bdd_ithvar(x);
		    }
		}
#endif
	      else
		{
		  int x = dict_.register_next_variable(y);
		  res_ = bdd_ithvar(x);
		}
	      break;
	    }
	  case unop::Closure:
	    {
	      // rat_seen_ = true;
	      const formula* f = node->child();
	      tgba_succ_iterator* i = dict_.transdfa.succ(f);
	      res_ = bddfalse;

	      if (!i)
		break;
	      for (i->first(); !i->done(); i->next())
		{
		  bdd label = i->current_condition();
		  state* s = i->current_state();
		  const formula* dest = dict_.transdfa.get_label(f, s);
		  s->destroy();

		  if (dest->accepts_eword())
		    {
		      dest->destroy();
		      res_ |= label;
		    }
		  else
		    {
		      const formula* dest2 = unop::instance(op, dest);
		      if (dest2 == constant::false_instance())
			continue;
		      int x = dict_.register_next_variable(dest2);
		      dest2->destroy();
		      res_ |= label & bdd_ithvar(x);
		    }
		}
	      delete i;
	    }
	    break;

	  case unop::NegClosureMarked:
	    has_marked_ = true;
	  case unop::NegClosure:
	    rat_seen_ = true;
	    {
	      if (mark_all_)
		{
		  op = unop::NegClosureMarked;
		  has_marked_ = true;
		}

	      const formula* f = node->child();
	      tgba_succ_iterator* i = dict_.transdfa.succ(f);
	      res_ = bddtrue;

	      if (!i)
		break;

	      res_ = bddfalse;
	      bdd missing = bddtrue;
	      for (i->first(); !i->done(); i->next())
		{
		  bdd label = i->current_condition();
		  state* s = i->current_state();
		  const formula* dest = dict_.transdfa.get_label(f, s);
		  s->destroy();

		  missing -= label;

		  if (dest->accepts_eword())
		    {
		      dest->destroy();
		    }
		  else
		    {
		      const formula* dest2 = unop::instance(op, dest);
		      if (dest2 == constant::false_instance())
			continue;
		      int x = dict_.register_next_variable(dest2);
		      dest2->destroy();
		      res_ |= label & bdd_ithvar(x);
		    }
		}
	      delete i;

	      res_ |= missing &
		// stick X(1) to preserve determinism.
		bdd_ithvar(dict_.register_next_variable
			   (constant::true_instance()));
	      //trace_ltl_bdd(dict_, res_);
	    }
	    break;

	  case unop::Finish:
	    assert(!"unsupported operator");
	    break;
	  }
      }

      void
      visit(const bunop*)
      {
	assert(!"Not an LTL operator");
      }

      void
      visit(const binop* node)
      {
	binop::type op = node->op();

	switch (op)
	  {
	    // r(f1 logical-op f2) = r(f1) logical-op r(f2)
	  case binop::Xor:
	  case binop::Implies:
	  case binop::Equiv:
	    // These operators should only appear in Boolean formulas,
	    // which must have been dealt with earlier (in
	    // translate_dict::ltl_to_bdd()).
	    assert(!"unexpected operator");
	    break;
	  case binop::U:
	    {
	      bdd f1 = recurse(node->first());
	      bdd f2 = recurse(node->second());
	      // r(f1 U f2) = r(f2) + a(f2)r(f1)X(f1 U f2) if not recurring
	      // r(f1 U f2) = r(f2) + a(f2)r(f1)           if recurring
	      f1 &= bdd_ithvar(dict_.register_a_variable(node->second()));
	      if (!recurring_)
		f1 &= bdd_ithvar(dict_.register_next_variable(node));
	      res_ = f2 | f1;
	      break;
	    }
	  case binop::W:
	    {
	      // r(f1 W f2) = r(f2) + r(f1)X(f1 W f2) if not recurring
	      // r(f1 W f2) = r(f2) + r(f1)           if recurring
	      //
	      // also f1 W 0 = G(f1), so we can enable recurring on f1
	      bdd f1 = recurse(node->first(),
			       node->second() == constant::false_instance());
	      bdd f2 = recurse(node->second());
	      if (!recurring_)
		f1 &= bdd_ithvar(dict_.register_next_variable(node));
	      res_ = f2 | f1;
	      break;
	    }
	  case binop::R:
	    {
	      // r(f2) is in factor, so we can propagate the recurring_ flag.
	      // if f1=false, we can also turn it on (0 R f = Gf).
	      res_ = recurse(node->second(),
			     recurring_
			     || node->first() == constant::false_instance());
	      // r(f1 R f2) = r(f2)(r(f1) + X(f1 R f2))  if not recurring
	      // r(f1 R f2) = r(f2)                      if recurring
	      if (recurring_)
		break;
	      bdd f1 = recurse(node->first());
	      res_ &= f1 | bdd_ithvar(dict_.register_next_variable(node));
	      break;
	    }
	  case binop::M:
	    {
	      res_ = recurse(node->second(), recurring_);
	      bdd f1 = recurse(node->first());
	      // r(f1 M f2) = r(f2)(r(f1) + a(f1&f2)X(f1 M f2)) if not recurring
	      // r(f1 M f2) = r(f2)(r(f1) + a(f1&f2))           if recurring
	      //
	      // Note that the rule above differs from the one given
	      // in Figure 2 of
	      //    "LTL translation improvements in Spot 1.0",
	      //     A. Duret-Lutz. IJCCBS 5(1/2):31-54, March 2014.
	      // Both rules should be OK, but this one is a better fit
	      // to the promises simplifications performed in
	      // register_a_variable() (see comments in this function).
	      // We do not want a U (c M d) to generate two different
	      // promises.  Generating c&d also makes the output similar
	      // to what we would get with the equivalent a U (d U (c & d)).
	      //
	      // Here we just appear to emit a(f1 M f2) and the conversion
	      // to a(f1&f2) is done  by register_a_variable().
	      bdd a = bdd_ithvar(dict_.register_a_variable(node));
	      if (!recurring_)
		a &= bdd_ithvar(dict_.register_next_variable(node));
	      res_ &= f1 | a;
	      break;
	    }
	  case binop::EConcatMarked:
	    has_marked_ = true;
	    /* fall through */
	  case binop::EConcat:
	    rat_seen_ = true;
	    {
	      // Recognize f2 on transitions going to destinations
	      // that accept the empty word.
	      bdd f2 = recurse(node->second());
	      bdd f1 = translate_ratexp(node->first(), dict_);
	      res_ = bddfalse;

	      if (mark_all_)
		{
		  op = binop::EConcatMarked;
		  has_marked_ = true;
		}

	      if (exprop_)
		{
		  bdd var_set = bdd_existcomp(bdd_support(f1), dict_.var_set);
		  bdd all_props = bdd_existcomp(f1, dict_.var_set);
		  while (all_props != bddfalse)
		    {
		      bdd label = bdd_satoneset(all_props, var_set, bddtrue);
		      all_props -= label;

		      const formula* dest =
			dict_.bdd_to_sere(bdd_exist(f1 & label,
						    dict_.var_set));

		      const formula* dest2 =
			binop::instance(op, dest, node->second()->clone());

		      if (dest2 != constant::false_instance())
			{
			  int x = dict_.register_next_variable(dest2);
			  dest2->destroy();
			  res_ |= label & bdd_ithvar(x);
			}
		      if (dest->accepts_eword())
			res_ |= label & f2;
		    }
		}
	      else
		{
		  minato_isop isop(f1);
		  bdd cube;
		  while ((cube = isop.next()) != bddfalse)
		    {
		      bdd label = bdd_exist(cube, dict_.next_set);
		      bdd dest_bdd = bdd_existcomp(cube, dict_.next_set);
		      const formula* dest = dict_.conj_bdd_to_sere(dest_bdd);

		      if (dest == constant::empty_word_instance())
			{
			  res_ |= label & f2;
			}
		      else
			{
			  const formula* dest2 =
			    binop::instance(op, dest, node->second()->clone());
			  if (dest2 != constant::false_instance())
			    {
			      int x = dict_.register_next_variable(dest2);
			      dest2->destroy();
			      res_ |= label & bdd_ithvar(x);
			    }
			  if (dest->accepts_eword())
			    res_ |= label & f2;
			}
		    }
		}
	    }
	    break;

	  case binop::UConcat:
	    {
	      // Transitions going to destinations accepting the empty
	      // word should recognize f2, and the automaton for f1
	      // should be understood as universal.
	      //
	      // The crux of this translation (the use of implication,
	      // and the interpretation as a universal automaton) was
	      // explained to me (adl) by Felix Klaedtke.
	      bdd f2 = recurse(node->second());
	      bdd f1 = translate_ratexp(node->first(), dict_);
	      res_ = bddtrue;

	      bdd var_set = bdd_existcomp(bdd_support(f1), dict_.var_set);
	      bdd all_props = bdd_existcomp(f1, dict_.var_set);
	      while (all_props != bddfalse)
		{

		  bdd one_prop_set = bddtrue;
		  if (exprop_)
		    one_prop_set = bdd_satoneset(all_props, var_set, bddtrue);
		  all_props -= one_prop_set;

		  minato_isop isop(f1 & one_prop_set);
		  bdd cube;
		  while ((cube = isop.next()) != bddfalse)
		    {
		      bdd label = bdd_exist(cube, dict_.next_set);
		      bdd dest_bdd = bdd_existcomp(cube, dict_.next_set);
		      const formula* dest = dict_.conj_bdd_to_sere(dest_bdd);
		      const formula* dest2 =
			binop::instance(op, dest, node->second()->clone());

		      bdd udest =
			bdd_ithvar(dict_.register_next_variable(dest2));

		      if (dest->accepts_eword())
			udest &= f2;

		      dest2->destroy();

		      res_ &= bdd_apply(label, udest, bddop_imp);
		    }
		}
	    }
	    break;
	  }
      }

      void
      visit(const automatop*)
      {
	assert(!"unsupported operator");
      }

      void
      visit(const multop* node)
      {
	switch (node->op())
	  {
	  case multop::And:
	    {
	      formula_set implied;
	      implied_subformulae(node, implied);

	      // std::cerr << "---" << std::endl;
	      // for (formula_set::const_iterator i = implied.begin();
	      // 	   i != implied.end(); ++i)
	      // 	std::cerr << to_string(*i) << std::endl;

	      res_ = bddtrue;
	      unsigned s = node->size();
	      for (unsigned n = 0; n < s; ++n)
		{
		  const formula* sub = node->nth(n);
		  // Skip implied subformula.  For instance
		  // when translating Fa & GFa, we should not
		  // attempt to translate Fa.
		  //
		  // This optimization combines nicely with the
		  // "recurring" optimization whereby GFp will be
		  // translated as r(GFp) = (r(p) | a(p))X(GFp)
		  // without showing Fp instead of r(GFp) =
		  // r(Fp)X(GFp).  See the comment for the translation
		  // of G.
		  if (implied.find(sub) != implied.end())
		    continue;
		  // Propagate the recurring_ flag so that
		  // G(Fa & Fb) get optimized.  See the comment in
		  // the case handling G.
		  bdd res = recurse(sub, recurring_);
		  //std::cerr << "== in And (" << to_string(sub)
		  // << ")" << std::endl;
		  // trace_ltl_bdd(dict_, res);
		  res_ &= res;
		}
	      //std::cerr << "=== And final" << std::endl;
	      // trace_ltl_bdd(dict_, res_);
	      break;
	    }
	  case multop::Or:
	    {
	      res_ = bddfalse;
	      unsigned s = node->size();
	      for (unsigned n = 0; n < s; ++n)
		res_ |= recurse(node->nth(n));
	      break;
	    }
	  case multop::Concat:
	  case multop::Fusion:
	  case multop::AndNLM:
	  case multop::AndRat:
	  case multop::OrRat:
	    assert(!"Not an LTL operator");
	    break;
	  }

      }

      bdd
      recurse(const formula* f, bool recurring = false)
      {
	const translate_dict::translated& t =
	  dict_.ltl_to_bdd(f, mark_all_, recurring);
	rat_seen_ |= t.has_rational;
	has_marked_ |= t.has_marked;
	return t.symbolic;
      }


    private:
      translate_dict& dict_;
      bdd res_;
      bool rat_seen_;
      bool has_marked_;
      bool mark_all_;
      bool exprop_;
      bool recurring_;
    };

    const translate_dict::translated&
    translate_dict::ltl_to_bdd(const formula* f, bool mark_all, bool recurring)
    {
      flagged_formula ff;
      ff.f = f;
      ff.flags =
	((mark_all || f->is_ltl_formula()) ? flags_mark_all : flags_none)
	| (recurring ? flags_recurring : flags_none);

      flagged_formula_to_bdd_map::const_iterator i = ltl_bdd_.find(ff);

      if (i != ltl_bdd_.end())
	return i->second;

      translated t;
      if (f->is_boolean())
	{
	  t.symbolic = boolean_to_bdd(f);
	  t.has_rational = false;
	  t.has_marked = false;
	}
      else
	{
	  ltl_trad_visitor v(*this, mark_all, exprop, recurring);
	  f->accept(v);
	  t.symbolic = v.result();
	  t.has_rational = v.has_rational();
	  t.has_marked = v.has_marked();
	}

      f->clone();
      return ltl_bdd_.insert(std::make_pair(ff, t)).first->second;
    }


    // Check whether a formula has a R, W, or G operator at its
    // top-level (preceding logical operators do not count).
    class ltl_possible_fair_loop_visitor: public visitor
    {
    public:
      ltl_possible_fair_loop_visitor()
	: res_(false)
      {
      }

      virtual
      ~ltl_possible_fair_loop_visitor()
      {
      }

      bool
      result() const
      {
	return res_;
      }

      void
      visit(const atomic_prop*)
      {
      }

      void
      visit(const constant*)
      {
      }

      void
      visit(const unop* node)
      {
	if (node->op() == unop::G)
	  res_ = true;
      }

      void
      visit(const binop* node)
      {
	switch (node->op())
	  {
	    // r(f1 logical-op f2) = r(f1) logical-op r(f2)
	  case binop::Xor:
	  case binop::Implies:
	  case binop::Equiv:
	    node->first()->accept(*this);
	    if (!res_)
	      node->second()->accept(*this);
	    return;
	  case binop::U:
	  case binop::M:
	    return;
	  case binop::R:
	  case binop::W:
	    res_ = true;
	    return;
	  case binop::UConcat:
	  case binop::EConcat:
	  case binop::EConcatMarked:
	    node->second()->accept(*this);
	    // FIXME: we might need to add Acc[1]
	    return;
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const automatop*)
      {
	assert(!"unsupported operator");
      }

      void
      visit(const bunop*)
      {
	assert(!"unsupported operator");
      }

      void
      visit(const multop* node)
      {
	unsigned s = node->size();
	for (unsigned n = 0; n < s && !res_; ++n)
	  {
	    node->nth(n)->accept(*this);
	  }
      }

    private:
      bool res_;
    };

    // Check whether a formula can be part of a fair loop.
    // Cache the result for efficiency.
    class possible_fair_loop_checker
    {
    public:
      bool
      check(const formula* f)
      {
	pfl_map::const_iterator i = pfl_.find(f);
	if (i != pfl_.end())
	  return i->second;
	ltl_possible_fair_loop_visitor v;
	f->accept(v);
	bool rel = v.result();
	pfl_[f] = rel;
	return rel;
      }

    private:
      typedef Sgi::hash_map<const formula*, bool, formula_ptr_hash> pfl_map;
      pfl_map pfl_;
    };

    class formula_canonizer
    {
    public:
      formula_canonizer(translate_dict& d,
			bool fair_loop_approx, bdd all_promises)
	: fair_loop_approx_(fair_loop_approx),
	  all_promises_(all_promises),
	  d_(d)
      {
	// For cosmetics, register 1 initially, so the algorithm will
	// not register an equivalent formula first.
	b2f_[bddtrue] = constant::true_instance();
      }

      ~formula_canonizer()
      {
	formula_to_bdd_map::iterator i = f2b_.begin();
	while (i != f2b_.end())
	  // Advance the iterator before destroying previous value.
	  i++->first->destroy();
      }

      // This wrap translate_dict::ltl_to_bdd() for top-level formulas.
      // In case the formula contains SERE operators, we need to decide
      // if we have to mark unmarked operators, and more
      const translate_dict::translated&
      translate(const formula* f, bool* new_flag = 0)
      {
	// Use the cached result if available.
	formula_to_bdd_map::const_iterator i = f2b_.find(f);
	if (i != f2b_.end())
	  return i->second;

	if (new_flag)
	  *new_flag = true;

	// Perform the actual translation.
	translate_dict::translated t = d_.ltl_to_bdd(f, !f->is_marked());

	// std::cerr << "-----" << std::endl;
	// std::cerr << "Formula: " << to_string(f) << std::endl;
	// std::cerr << "Rational: " << t.has_rational << std::endl;
	// std::cerr << "Marked: " << t.has_marked << std::endl;
	// std::cerr << "Mark all: " << !f->is_marked() << std::endl;
	// std::cerr << "Transitions:" << std::endl;
	// trace_ltl_bdd(d_, t.symbolic);
	// std::cerr << "-----" << std::endl;

	if (t.has_rational)
	  {
	    bdd res = bddfalse;

	    minato_isop isop(t.symbolic);
	    bdd cube;
	    while ((cube = isop.next()) != bddfalse)
	      {
		bdd label = bdd_exist(cube, d_.next_set);
		bdd dest_bdd = bdd_existcomp(cube, d_.next_set);
		const formula* dest =
		  d_.conj_bdd_to_formula(dest_bdd);

		// Handle a Miyano-Hayashi style unrolling for
		// rational operators.  Marked nodes correspond to
		// subformulae in the Miyano-Hayashi set.
		const formula* tmp =  d_.mt.simplify_mark(dest);
		dest->destroy();
		dest = tmp;

		if (dest->is_marked())
		  {
		    // Make the promise that we will exit marked sets.
		    int a =
		      d_.register_a_variable(constant::true_instance());
		    label &= bdd_ithvar(a);
		  }
		else
		  {
		    // We have no marked operators, but still
		    // have other rational operator to check.
		    // Start a new marked cycle.
		    const formula* dest2 = d_.mt.mark_concat_ops(dest);
		    dest->destroy();
		    dest = dest2;
		  }
		// Note that simplify_mark may have changed dest.
		dest_bdd = bdd_ithvar(d_.register_next_variable(dest));
		dest->destroy();
		res |= label & dest_bdd;
	      }
	    t.symbolic = res;
//	    std::cerr << "Marking rewriting:" << std::endl;
//	    trace_ltl_bdd(v_.get_dict(), t.symbolic);
	  }

	// Apply the fair-loop approximation if requested.
	if (fair_loop_approx_)
	  {
	    // If the source cannot possibly be part of a fair
	    // loop, make all possible promises.
	    if (fair_loop_approx_
		&& f != constant::true_instance()
		&& !pflc_.check(f))
	      t.symbolic &= all_promises_;
	  }

	// Register the reverse mapping if it is not already done.
	if (b2f_.find(t.symbolic) == b2f_.end())
	  b2f_[t.symbolic] = f;

	return f2b_.insert(std::make_pair(f->clone(), t)).first->second;
      }

      const formula*
      canonize(const formula* f)
      {
	bool new_variable = false;
	bdd b = translate(f, &new_variable).symbolic;

	bdd_to_formula_map::iterator i = b2f_.find(b);
	// Since we have just translated the formula, it is
	// necessarily in b2f_.
	assert(i != b2f_.end());

	if (i->second != f)
	  {
	    // The translated bdd maps to an already seen formula.
	    f->destroy();
	    f = i->second->clone();
	  }
	return f;
      }

      bdd used_vars()
      {
	return d_.var_set;
      }

    private:
      // Map a representation of successors to a canonical formula.
      // We do this because many formulae (such as `aR(bRc)' and
      // `aR(bRc).(bRc)') are equivalent, and are trivially identified
      // by looking at the set of successors.
      typedef Sgi::hash_map<bdd, const formula*, bdd_hash> bdd_to_formula_map;
      bdd_to_formula_map b2f_;
      // Map each formula to its associated bdd.  This speed things up when
      // the same formula is translated several times, which especially
      // occurs when canonize() is called repeatedly inside exprop.
      typedef Sgi::hash_map<const formula*, translate_dict::translated,
			    ptr_hash<formula> > formula_to_bdd_map;
      formula_to_bdd_map f2b_;

      possible_fair_loop_checker pflc_;
      bool fair_loop_approx_;
      bdd all_promises_;
      translate_dict& d_;
    };

  }

  typedef std::map<bdd, bdd, bdd_less_than> prom_map;
  typedef Sgi::hash_map<const formula*, prom_map, formula_ptr_hash> dest_map;

  static void
  fill_dests(translate_dict& d, dest_map& dests, bdd label, const formula* dest)
  {
    bdd conds = bdd_existcomp(label, d.var_set);
    bdd promises = bdd_existcomp(label, d.a_set);

    dest_map::iterator i = dests.find(dest);
    if (i == dests.end())
      {
	dests[dest][promises] = conds;
      }
    else
      {
	i->second[promises] |= conds;
	dest->destroy();
      }
  }


  tgba_explicit_formula*
  ltl_to_tgba_fm(const formula* f, bdd_dict* dict,
		 bool exprop, bool symb_merge, bool branching_postponement,
		 bool fair_loop_approx, const atomic_prop_set* unobs,
		 ltl_simplifier* simplifier)
  {
    const formula* f2;
    ltl_simplifier* s = simplifier;

    // Simplify the formula, if requested.
    if (s)
      {
	// This will normalize the formula regardless of the
	// configuration of the simplifier.
	f2 = s->simplify(f);
      }
    else
      {
	// Otherwise, at least normalize the formula.  We want all the
	// negations on the atomic propositions.  We also suppress
	// logic abbreviations such as <=>, =>, or XOR, since they
	// would involve negations at the BDD level.
	s = new ltl_simplifier(dict);
	f2 = s->negative_normal_form(f, false);
      }

    typedef std::set<const formula*, formula_ptr_less_than> set_type;
    set_type formulae_to_translate;

    assert(dict == s->get_dict());

    translate_dict d(dict, s, exprop, f->is_syntactic_persistence());

    // Compute the set of all promises that can possibly occur
    // inside the formula.
    bdd all_promises = bddtrue;
    if (fair_loop_approx || unobs)
      {
	ltl_promise_visitor pv(d);
	f2->accept(pv);
	all_promises = pv.result();
      }

    formula_canonizer fc(d, fair_loop_approx, all_promises);

    // These are used when atomic propositions are interpreted as
    // events.  There are two kinds of events: observable events are
    // those used in the formula, and unobservable events or other
    // events that can occur at anytime.  All events exclude each
    // other.
    bdd observable_events = bddfalse;
    bdd unobservable_events = bddfalse;
    if (unobs)
      {
	bdd neg_events = bddtrue;
	std::auto_ptr<atomic_prop_set> aps(atomic_prop_collect(f));
	for (atomic_prop_set::const_iterator i = aps->begin();
	     i != aps->end(); ++i)
	  {
	    int p = d.register_proposition(*i);
	    bdd pos = bdd_ithvar(p);
	    bdd neg = bdd_nithvar(p);
	    observable_events = (observable_events & neg) | (neg_events & pos);
	    neg_events &= neg;
	  }
	for (atomic_prop_set::const_iterator i = unobs->begin();
	     i != unobs->end(); ++i)
	  {
	    int p = d.register_proposition(*i);
	    bdd pos = bdd_ithvar(p);
	    bdd neg = bdd_nithvar(p);
	    unobservable_events = ((unobservable_events & neg)
				   | (neg_events & pos));
	    observable_events &= neg;
	    neg_events &= neg;
	  }
      }
    bdd all_events = observable_events | unobservable_events;


    tgba_explicit_formula* a = new tgba_explicit_formula(dict);

    // This is in case the initial state is equivalent to true...
    if (symb_merge)
      f2 = fc.canonize(f2);

    formulae_to_translate.insert(f2);
    a->set_init_state(f2);

    while (!formulae_to_translate.empty())
      {
	// Pick one formula.
	const formula* now = *formulae_to_translate.begin();
	formulae_to_translate.erase(formulae_to_translate.begin());

	// Translate it into a BDD to simplify it.
	const translate_dict::translated& t = fc.translate(now);
	bdd res = t.symbolic;

	// Handle exclusive events.
	if (unobs)
	  {
	    res &= observable_events;
	    int n = d.register_next_variable(now);
	    res |= unobservable_events & bdd_ithvar(n) & all_promises;
	  }

	// We used to factor only Next and A variables while computing
	// prime implicants, with
	//    minato_isop isop(res, d.next_set & d.a_set);
	// in order to obtain transitions with formulae of atomic
	// proposition directly, but unfortunately this led to strange
	// factorizations.  For instance f U g was translated as
	//     r(f U g) = g + a(g).r(X(f U g)).(f + g)
	// instead of just
	//     r(f U g) = g + a(g).r(X(f U g)).f
	// Of course both formulae are logically equivalent, but the
	// latter is "more deterministic" than the former, so it should
	// be preferred.
	//
	// Therefore we now factor all variables.  This may lead to more
	// transitions than necessary (e.g.,  r(f + g) = f + g  will be
	// coded as two transitions), but we later merge all transitions
	// with same source/destination and acceptance conditions.  This
	// is the goal of the `dests' hash.
	//
	// Note that this is still not optimal.  For instance it is
	// better to encode `f U g' as
	//     r(f U g) = g + a(g).r(X(f U g)).f.!g
	// because that leads to a deterministic automaton.  In order
	// to handle this, we take the conditions of any transition
	// going to true (it's `g' here), and remove it from the other
	// transitions.
	//
	// In `exprop' mode, considering all possible combinations of
	// outgoing propositions generalizes the above trick.
	dest_map dests;

	// Compute all outgoing arcs.

	// If EXPROP is set, we will refine the symbolic
	// representation of the successors for all combinations of
	// the atomic properties involved in the formula.
	// VAR_SET is the set of these properties.
	bdd var_set = bdd_existcomp(bdd_support(res), d.var_set);
	// ALL_PROPS is the combinations we have yet to consider.
	// We used to start with `all_props = bddtrue', but it is
	// more efficient to start with the set of all satisfiable
	// variables combinations.
	bdd all_props = bdd_existcomp(res, d.var_set);
	while (all_props != bddfalse)
	  {
	    bdd one_prop_set = bddtrue;
	    if (exprop)
	      one_prop_set = bdd_satoneset(all_props, var_set, bddtrue);
	    all_props -= one_prop_set;

	    typedef std::map<bdd, const formula*, bdd_less_than> succ_map;
	    succ_map succs;

	    // Compute prime implicants.
	    // The reason we use prime implicants and not bdd_satone()
	    // is that we do not want to get any negation in front of Next
	    // or Acc variables.  We wouldn't know what to do with these.
	    // We never added negations in front of these variables when
	    // we built the BDD, so prime implicants will not "invent" them.
	    //
	    // FIXME: minato_isop is quite expensive, and I (=adl)
	    // don't think we really care that much about getting the
	    // smalled sum of products that minato_isop strives to
	    // compute.  Given that Next and Acc variables should
	    // always be positive, maybe there is a faster way to
	    // compute the successors?  E.g. using bdd_satone() and
	    // ignoring negated Next and Acc variables.
	    minato_isop isop(res & one_prop_set);
	    bdd cube;
	    while ((cube = isop.next()) != bddfalse)
	      {
		bdd label = bdd_exist(cube, d.next_set);
		bdd dest_bdd = bdd_existcomp(cube, d.next_set);
		const formula* dest = d.conj_bdd_to_formula(dest_bdd);

		// Simplify the formula, if requested.
		if (simplifier)
		  {
		    const formula* tmp = simplifier->simplify(dest);
		    dest->destroy();
		    dest = tmp;
		    // Ignore the arc if the destination reduces to false.
		    if (dest == constant::false_instance())
		      continue;
		  }

		// If we already know a state with the same
		// successors, use it in lieu of the current one.
		if (symb_merge)
		  dest = fc.canonize(dest);

		// If we are not postponing the branching, we can
		// declare the outgoing transitions immediately.
		// Otherwise, we merge transitions with identical
		// label, and declare the outgoing transitions in a
		// second loop.
		if (!branching_postponement)
		  {
		    fill_dests(d, dests, label, dest);
		  }
		else
		  {
		    succ_map::iterator si = succs.find(label);
		    if (si == succs.end())
		      succs[label] = dest;
		    else
		      si->second =
			multop::instance(multop::Or, si->second, dest);
		  }
	      }
	    if (branching_postponement)
	      for (succ_map::const_iterator si = succs.begin();
		   si != succs.end(); ++si)
		fill_dests(d, dests, si->first, si->second);
	  }

	// Check for an arc going to 1 (True).  Register it first, that
	// way it will be explored before others during model checking.
	dest_map::const_iterator i = dests.find(constant::true_instance());
	// COND_FOR_TRUE is the conditions of the True arc, so we
	// can remove them from all other arcs.  It might sounds that
	// this is not needed when exprop is used, but in fact it is
	// complementary.
	//
	// Consider
	//   f = r(X(1) R p) = p.(1 + r(X(1) R p))
	// with exprop the two outgoing arcs would be
        //         p               p
	//     f ----> 1       f ----> f
	//
	// where in fact we could output
        //         p
	//     f ----> 1
	//
	// because there is no point in looping on f if we can go to 1.
	bdd cond_for_true = bddfalse;
	if (i != dests.end())
	  {
	    // When translating LTL for an event-based logic with
	    // unobservable events, the 1 state should accept all events,
	    // even unobservable events.
	    if (unobs && now == constant::true_instance())
	      cond_for_true = all_events;
	    else
	      {
		// There should be only one transition going to 1 (true) ...
		assert(i->second.size() == 1);
		prom_map::const_iterator j = i->second.begin();
		// ... and it is not expected to make any promises (unless
		// fair loop approximations are used).
		assert(fair_loop_approx || j->first == bddtrue);
		cond_for_true = j->second;
	      }
	    if (!a->has_state(constant::true_instance()))
	      formulae_to_translate.insert(constant::true_instance());
	    state_explicit_formula::transition* t =
	      a->create_transition(now, constant::true_instance());
	    t->condition = cond_for_true;
	  }
	// Register other transitions.
	for (i = dests.begin(); i != dests.end(); ++i)
	  {
	    const formula* dest = i->first;
	    // The cond_for_true optimization can cause some
	    // transitions to be removed.  So we have to remember
	    // whether a formula is actually reachable.
	    bool reachable = false;

	    // Will this be a new state?
	    bool seen = a->has_state(dest);

	    if (dest != constant::true_instance())
	      {
		for (prom_map::const_iterator j = i->second.begin();
		     j != i->second.end(); ++j)
		  {
		    bdd cond = j->second - cond_for_true;
		    if (cond == bddfalse) // Skip false transitions.
		      continue;
		    state_explicit_formula::transition* t =
		      a->create_transition(now, dest);
		    t->condition = cond;
		    d.conj_bdd_to_acc(a, j->first, t);
		    reachable = true;
		  }
	      }
	    else
	      {
		// "1" is reachable.
		reachable = true;
	      }
	    if (reachable && !seen)
	      formulae_to_translate.insert(dest);
	    else
	      dest->destroy();
	  }
      }

    dict->register_propositions(fc.used_vars(), a);
    // Turn all promises into real acceptance conditions.
    a->complement_all_acceptance_conditions();

    if (!simplifier)
      // This should not be deleted before we have registered all propositions.
      delete s;

    return a;
  }

}
