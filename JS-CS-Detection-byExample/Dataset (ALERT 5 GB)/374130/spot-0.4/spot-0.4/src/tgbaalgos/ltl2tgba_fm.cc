// Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

#include "misc/hash.hh"
#include "misc/bddalloc.hh"
#include "misc/bddlt.hh"
#include "misc/minato.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/destroy.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/postfix.hh"
#include "ltlvisit/apcollect.hh"
#include <cassert>
#include <memory>
#include "ltl2tgba_fm.hh"
#include "ltlvisit/contain.hh"

namespace spot
{
  using namespace ltl;

  namespace
  {

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

      translate_dict(bdd_dict* dict)
	: dict(dict),
	  a_set(bddtrue),
	  var_set(bddtrue),
	  next_set(bddtrue)
      {
      }

      ~translate_dict()
      {
	fv_map::iterator i;
	for (i = next_map.begin(); i != next_map.end(); ++i)
	  destroy(i->first);
	dict->unregister_all_my_variables(this);
      }

      bdd_dict* dict;

      typedef bdd_dict::fv_map fv_map;
      typedef bdd_dict::vf_map vf_map;

      fv_map next_map;	       ///< Maps "Next" variables to BDD variables
      vf_map next_formula_map; ///< Maps BDD variables to "Next" variables

      bdd a_set;
      bdd var_set;
      bdd next_set;

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
	    f = clone(f);
	    num = dict->register_anonymous_variables(1, this);
	    next_map[f] = num;
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

      formula*
      var_to_formula(int var) const
      {
	vf_map::const_iterator isi = next_formula_map.find(var);
	if (isi != next_formula_map.end())
	  return clone(isi->second);
	isi = dict->acc_formula_map.find(var);
	if (isi != dict->acc_formula_map.end())
	  return clone(isi->second);
	isi = dict->var_formula_map.find(var);
	if (isi != dict->var_formula_map.end())
	  return clone(isi->second);
	assert(0);
	// Never reached, but some GCC versions complain about
	// a missing return otherwise.
	return 0;
      }

      formula*
      conj_bdd_to_formula(bdd b)
      {
	if (b == bddfalse)
	  return constant::false_instance();
	multop::vec* v = new multop::vec;
	while (b != bddtrue)
	  {
	    int var = bdd_var(b);
	    formula* res = var_to_formula(var);
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
	return multop::instance(multop::And, v);
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

      void
      conj_bdd_to_acc(tgba_explicit* a, bdd b, tgba_explicit::transition* t)
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
		formula* ac = var_to_formula(var);

		if (!a->has_acceptance_condition(ac))
		  a->declare_acceptance_condition(clone(ac));
		a->add_acceptance_condition(t, ac);
		b = high;
	      }
	    assert(b != bddfalse);
	  }
      }
    };



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
      doit(unop* node)
      {
	if (node->op() == unop::F)
	  res_ &= bdd_ithvar(dict_.register_a_variable(node->child()));
      }

      virtual void
      doit(binop* node)
      {
	if (node->op() == binop::U)
	  res_ &= bdd_ithvar(dict_.register_a_variable(node->second()));
      }

    private:
      translate_dict& dict_;
      bdd res_;
    };


    // The rewrite rules used here are adapted from Jean-Michel
    // Couvreur's FM paper.
    class ltl_trad_visitor: public const_visitor
    {
    public:
      ltl_trad_visitor(translate_dict& dict)
	: dict_(dict)
      {
      }

      virtual
      ~ltl_trad_visitor()
      {
      }

      bdd
      result() const
      {
	return res_;
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
	    {
	      // r(Fy) = r(y) + a(y)r(XFy)
	      const formula* child = node->child();
	      bdd y = recurse(child);
	      int a = dict_.register_a_variable(child);
	      int x = dict_.register_next_variable(node);
	      res_ = y | (bdd_ithvar(a) & bdd_ithvar(x));
	      return;
	    }
	  case unop::G:
	    {
	      // The paper suggests that we optimize GFy
	      // as
	      //   r(GFy) = (r(y) + a(y))r(XGFy)
	      // instead of
	      //   r(GFy) = (r(y) + a(y)r(XFy)).r(XGFy)
	      // but this is just a particular case
	      // of the "merge all states with the same
	      // symbolic rewriting" optimization we do later.
	      // (r(Fy).r(GFy) and r(GFy) have the same symbolic
	      // rewriting.)  Let's keep things simple here.

	      // r(Gy) = r(y)r(XGy)
	      const formula* child = node->child();
	      int x = dict_.register_next_variable(node);
	      bdd y = recurse(child);
	      res_ = y & bdd_ithvar(x);
	      return;
	    }
	  case unop::Not:
	    {
	      // r(!y) = !r(y)
	      res_ = bdd_not(recurse(node->child()));
	      return;
	    }
	  case unop::X:
	    {
	      // r(Xy) = Next[y]
	      int x = dict_.register_next_variable(node->child());
	      res_ = bdd_ithvar(x);
	      return;
	    }
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const binop* node)
      {
	bdd f1 = recurse(node->first());
	bdd f2 = recurse(node->second());

	switch (node->op())
	  {
	    // r(f1 logical-op f2) = r(f1) logical-op r(f2)
	  case binop::Xor:
	    res_ = bdd_apply(f1, f2, bddop_xor);
	    return;
	  case binop::Implies:
	    res_ = bdd_apply(f1, f2, bddop_imp);
	    return;
	  case binop::Equiv:
	    res_ = bdd_apply(f1, f2, bddop_biimp);
	    return;
	  case binop::U:
	    {
	      // r(f1 U f2) = r(f2) + a(f2)r(f1)r(X(f1 U f2))
	      int a = dict_.register_a_variable(node->second());
	      int x = dict_.register_next_variable(node);
	      res_ = f2 | (bdd_ithvar(a) & f1 & bdd_ithvar(x));
	      return;
	    }
	  case binop::R:
	    {
	      // r(f1 R f2) = r(f1)r(f2) + r(f2)r(X(f1 U f2))
	      int x = dict_.register_next_variable(node);
	      res_ = (f1 & f2) | (f2 & bdd_ithvar(x));
	      return;
	    }
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const multop* node)
      {
	int op = -1;
	switch (node->op())
	  {
	  case multop::And:
	    op = bddop_and;
	    res_ = bddtrue;
	    break;
	  case multop::Or:
	    op = bddop_or;
	    res_ = bddfalse;
	    break;
	  }
	assert(op != -1);
	unsigned s = node->size();
	for (unsigned n = 0; n < s; ++n)
	  {
	    res_ = bdd_apply(res_, recurse(node->nth(n)), op);
	  }
      }

      bdd
      recurse(const formula* f)
      {
	ltl_trad_visitor v(dict_);
	f->accept(v);
	return v.result();
      }


    private:
      translate_dict& dict_;
      bdd res_;
    };


    // Check whether a formula has a R or G operator at its top-level
    // (preceding logical operators do not count).
    class ltl_possible_fair_loop_visitor: public const_visitor
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
	    return;
	  case binop::R:
	    res_ = true;
	    return;
	  }
	/* Unreachable code.  */
	assert(0);
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
			bool fair_loop_approx, bdd all_promises,
			language_containment_checker* lcc)
	: v_(d),
	  fair_loop_approx_(fair_loop_approx),
	  all_promises_(all_promises),
	  lcc_(lcc)
      {
	// For cosmetics, register 1 initially, so the algorithm will
	// not register an equivalent formula first.
	b2f_[bddtrue] = constant::true_instance();
      }

      ~formula_canonizer()
      {
	while (!f2b_.empty())
	  {
	    formula_to_bdd_map::iterator i = f2b_.begin();
	    const formula* f = i->first;
	    f2b_.erase(i);
	    destroy(f);
	  }
      }

      bdd
      translate(const formula* f, bool* new_flag = 0)
      {
	// Use the cached result if available.
	formula_to_bdd_map::const_iterator i = f2b_.find(f);
	if (i != f2b_.end())
	  return i->second;

	if (new_flag)
	  *new_flag = true;

	// Perform the actual translation.
	f->accept(v_);
	bdd res = v_.result();

	// Apply the fair-loop approximation if requested.
	if (fair_loop_approx_)
	  {
	    // If the source cannot possibly be part of a fair
	    // loop, make all possible promises.
	    if (fair_loop_approx_
		&& f != constant::true_instance()
		&& !pflc_.check(f))
	      res &= all_promises_;
	  }

	f2b_[clone(f)] = res;

	// Register the reverse mapping if it is not already done.
	if (b2f_.find(res) == b2f_.end())
	  b2f_[res] = f;
	return res;
      }

      const formula*
      canonize(const formula* f)
      {
	bool new_variable = false;
	bdd b = translate(f, &new_variable);

	bdd_to_formula_map::iterator i = b2f_.find(b);
	// Since we have just translated the formula, it is
	// necessarily in b2f_.
	assert(i != b2f_.end());

	if (i->second != f)
	  {
	    // The translated bdd maps to an already seen formula.
	    destroy(f);
	    f = clone(i->second);
	  }
	else if (new_variable && lcc_)
	  {
	    // It's a new bdd for a new formula.  Let's see if we can
	    // find an equivalent formula with language containment
	    // checks.
	    for (formula_to_bdd_map::const_iterator j = f2b_.begin();
		 j != f2b_.end(); ++j)
	      if (f != j->first && lcc_->equal(f, j->first))
		{
		  f2b_[f] = j->second;
		  i->second = j->first;
		  destroy(f);
		  f = clone(i->second);
		  break;
		}
	  }
	return f;
      }

    private:
      ltl_trad_visitor v_;
      // Map each formula to its associated bdd.  This speed things up when
      // the same formula is translated several times, which especially
      // occurs when canonize() is called repeatedly inside exprop.
      typedef std::map<bdd, const formula*, bdd_less_than> bdd_to_formula_map;
      bdd_to_formula_map b2f_;
      // Map a representation of successors to a canonical formula.
      // We do this because many formulae (such as `aR(bRc)' and
      // `aR(bRc).(bRc)') are equivalent, and are trivially identified
      // by looking at the set of successors.
      typedef std::map<const formula*, bdd> formula_to_bdd_map;
      formula_to_bdd_map f2b_;

      possible_fair_loop_checker pflc_;
      bool fair_loop_approx_;
      bdd all_promises_;
      language_containment_checker* lcc_;
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
	destroy(dest);
      }
  }


  tgba_explicit*
  ltl_to_tgba_fm(const formula* f, bdd_dict* dict,
		 bool exprop, bool symb_merge, bool branching_postponement,
		 bool fair_loop_approx, const atomic_prop_set* unobs,
		 int reduce_ltl, bool containment_checks)
  {
    symb_merge |= containment_checks;

    // Normalize the formula.  We want all the negations on
    // the atomic propositions.  We also suppress logic
    // abbreviations such as <=>, =>, or XOR, since they
    // would involve negations at the BDD level.
    formula* f1 = unabbreviate_logic(f);
    formula* f2 = negative_normal_form(f1);
    destroy(f1);

    // Simplify the formula, if requested.
    if (reduce_ltl)
      {
	formula* tmp = reduce(f2, reduce_ltl);
	destroy(f2);
	f2 = tmp;
      }

    typedef std::set<const formula*, formula_ptr_less_than> set_type;
    set_type formulae_seen;
    set_type formulae_to_translate;

    translate_dict d(dict);

    // Compute the set of all promises that can possibly occurre
    // inside the formula.
    bdd all_promises = bddtrue;
    if (fair_loop_approx || unobs)
      {
	ltl_promise_visitor pv(d);
	f2->accept(pv);
	all_promises = pv.result();
      }

    language_containment_checker lcc(dict, exprop, symb_merge,
				     branching_postponement,
				     fair_loop_approx);

    formula_canonizer fc(d, fair_loop_approx, all_promises,
			 containment_checks ? &lcc : 0);

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

    formulae_seen.insert(f2);
    formulae_to_translate.insert(f2);

    tgba_explicit* a = new tgba_explicit(dict);

    a->set_init_state(to_string(f2));

    while (!formulae_to_translate.empty())
      {
	// Pick one formula.
	const formula* f = *formulae_to_translate.begin();
	formulae_to_translate.erase(formulae_to_translate.begin());

	// Translate it into a BDD to simplify it.
	bdd res = fc.translate(f);

	// Handle exclusive events.
	if (unobs)
	  {
	    res &= observable_events;
	    int n = d.register_next_variable(f);
	    res |= unobservable_events & bdd_ithvar(n) & all_promises;
	  }

	std::string now = to_string(f);

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
	    bdd one_prop_set =
	      exprop ? bdd_satoneset(all_props, var_set, bddtrue) : bddtrue;
	    all_props -= one_prop_set;

	    typedef std::map<bdd, const formula*, bdd_less_than> succ_map;
	    succ_map succs;

	    minato_isop isop(res & one_prop_set);
	    bdd cube;
	    while ((cube = isop.next()) != bddfalse)
	      {
		bdd label = bdd_exist(cube, d.next_set);
		bdd dest_bdd = bdd_existcomp(cube, d.next_set);
		const formula* dest = d.conj_bdd_to_formula(dest_bdd);

		// Simplify the formula, if requested.
		if (reduce_ltl)
		  {
		    formula* tmp = reduce(dest, reduce_ltl);
		    destroy(dest);
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
			multop::instance(multop::Or,
					 const_cast<formula*>(si->second),
					 const_cast<formula*>(dest));
		  }
	      }
	    if (branching_postponement)
	      for (succ_map::const_iterator si = succs.begin();
		   si != succs.end(); ++si)
		fill_dests(d, dests, si->first, si->second);
	  }

	// Check for an arc going to 1 (True).  Register it first, that
	// way it will be explored before the other during the model
	// checking.
	dest_map::const_iterator i = dests.find(constant::true_instance());
	// COND_FOR_TRUE is the conditions of the True arc, so when
	// can remove them from all other arcs.  It might sounds that
	// this is not needed when exprop is used, but in fact it is
	// complementary.
	//
	// Consider
	//   f = r(X(1) R p) = p.(1 + r(X(1) R p))
	// with exprop the two outgoing arcs would be
        //         p                  p
	//     f ----> 1       f ----------> 1
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
	    if (unobs && f == constant::true_instance())
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
	    tgba_explicit::transition* t =
	      a->create_transition(now, constant::true_instance()->val_name());
	    a->add_condition(t, d.bdd_to_formula(cond_for_true));
	  }
	// Register other transitions.
	for (i = dests.begin(); i != dests.end(); ++i)
	  {
	    const formula* dest = i->first;
	    // The cond_for_true optimization can cause some
	    // transitions to be removed.  So we have to remember
	    // whether a formula is actually reachable.
	    bool reachable = false;

	    if (dest != constant::true_instance())
	      {
		std::string next = to_string(dest);
		for (prom_map::const_iterator j = i->second.begin();
		     j != i->second.end(); ++j)
		  {
		    bdd cond = j->second - cond_for_true;
		    if (cond == bddfalse) // Skip false transitions.
		      continue;
		    tgba_explicit::transition* t =
		      a->create_transition(now, next);
		    a->add_condition(t, d.bdd_to_formula(cond));
		    d.conj_bdd_to_acc(a, j->first, t);
		    reachable = true;
		  }
	      }
	    else
	      {
		// "1" is reachable.
		reachable = true;
	      }
	    if (reachable
		&& formulae_seen.find(dest) == formulae_seen.end())
	      {
		formulae_seen.insert(dest);
		formulae_to_translate.insert(dest);
	      }
	    else
	      {
		destroy(dest);
	      }
	  }
      }

    // Free all formulae.
    for (std::set<const formula*>::iterator i = formulae_seen.begin();
	 i != formulae_seen.end(); ++i)
      destroy(*i);

    // Turn all promises into real acceptance conditions.
    a->complement_all_acceptance_conditions();
    return a;
  }

}
