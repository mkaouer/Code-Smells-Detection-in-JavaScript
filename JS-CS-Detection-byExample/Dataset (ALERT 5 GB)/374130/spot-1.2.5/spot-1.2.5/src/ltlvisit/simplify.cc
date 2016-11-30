// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2013, 2014 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
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

#include <iostream>
//#define TRACE
#ifdef TRACE
#define trace std::cerr
#else
#define trace while (0) std::cerr
#endif

#include "simplify.hh"
#include "misc/hash.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/visitor.hh"
#include "ltlvisit/contain.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/snf.hh"
#include "tgba/formula2bdd.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {


    // The name of this class is public, but not its contents.
    class ltl_simplifier_cache
    {
      typedef Sgi::hash_map<const formula*, const formula*,
			    ptr_hash<formula> > f2f_map;
      typedef Sgi::hash_map<const formula*, bdd,
			    ptr_hash<formula> > f2b_map;
      typedef std::pair<const formula*, const formula*> pairf;
      typedef std::map<pairf, bool> syntimpl_cache_t;
    public:
      bdd_dict* dict;
      ltl_simplifier_options options;
      language_containment_checker lcc;

      ~ltl_simplifier_cache()
      {
	{
	  f2f_map::iterator i = simplified_.begin();
	  f2f_map::iterator end = simplified_.end();
	  while (i != end)
	    {
	      f2f_map::iterator old = i++;
	      old->second->destroy();
	      old->first->destroy();
	    }
	}
	{
	  f2f_map::iterator i = nenoform_.begin();
	  f2f_map::iterator end = nenoform_.end();
	  while (i != end)
	    {
	      f2f_map::iterator old = i++;
	      old->second->destroy();
	      old->first->destroy();
	    }
	}
	{
	  f2b_map::iterator i = as_bdd_.begin();
	  f2b_map::iterator end = as_bdd_.end();
	  while (i != end)
	    {
	      f2b_map::iterator old = i++;
	      old->first->destroy();
	    }
	}
	{
	  syntimpl_cache_t::iterator i = syntimpl_.begin();
	  syntimpl_cache_t::iterator end = syntimpl_.end();
	  while (i != end)
	    {
	      syntimpl_cache_t::iterator old = i++;
	      old->first.first->destroy();
	      old->first.second->destroy();
	    }
	}
	{
	  snf_cache::iterator i = snf_cache_.begin();
	  snf_cache::iterator end = snf_cache_.end();
	  while (i != end)
	    {
	      snf_cache::iterator old = i++;
	      old->second->destroy();
	      old->first->destroy();
	    }
	}
	{
	  snf_cache::iterator i = snfb_cache_.begin();
	  snf_cache::iterator end = snfb_cache_.end();
	  while (i != end)
	    {
	      snf_cache::iterator old = i++;
	      old->second->destroy();
	      old->first->destroy();
	    }
	}
	{
	  f2f_map::iterator i = bool_isop_.begin();
	  f2f_map::iterator end = bool_isop_.end();
	  while (i != end)
	    {
	      f2f_map::iterator old = i++;
	      old->second->destroy();
	      old->first->destroy();
	    }
	}

	dict->unregister_all_my_variables(this);
      }

      ltl_simplifier_cache(bdd_dict* d)
	: dict(d), lcc(d, true, true, false, false)
      {
      }

      ltl_simplifier_cache(bdd_dict* d, const ltl_simplifier_options& opt)
	: dict(d), options(opt), lcc(d, true, true, false, false)
      {
	options.containment_checks |= options.containment_checks_stronger;
	options.event_univ |= options.favor_event_univ;
      }

      void
      print_stats(std::ostream& os) const
      {
	os << "simplified formulae:    " << simplified_.size() << " entries\n"
	   << "negative normal form:   " << nenoform_.size() << " entries\n"
	   << "syntactic implications: " << syntimpl_.size() << " entries\n"
	   << "boolean to bdd:         " << as_bdd_.size() << " entries\n"
	   << "star normal form:       " << snf_cache_.size() << " entries\n"
	   << "boolean isop:           " << bool_isop_.size() << " entries\n";
      }

      void
      clear_as_bdd_cache()
      {
	f2b_map::iterator i = as_bdd_.begin();
	f2b_map::iterator end = as_bdd_.end();
	while (i != end)
	  {
	    f2b_map::iterator old = i++;
	    old->first->destroy();
	  }
	as_bdd_.clear();
      }

      // Convert a Boolean formula into a BDD for easier comparison.
      bdd
      as_bdd(const formula* f)
      {
	// Lookup the result in case it has already been computed.
	f2b_map::const_iterator it = as_bdd_.find(f);
	if (it != as_bdd_.end())
	  return it->second;

	bdd result = bddfalse;

	switch (f->kind())
	  {
	  case formula::Constant:
	    if (f == constant::true_instance())
	      result = bddtrue;
	    else if (f == constant::false_instance())
	      result = bddfalse;
	    else
	      assert(!"Unsupported operator");
	    break;
	  case formula::AtomicProp:
	    result = bdd_ithvar(dict->register_proposition(f, this));
	    break;
	  case formula::UnOp:
	    {
	      const unop* uo = static_cast<const unop*>(f);
	      assert(uo->op() == unop::Not);
	      result = !as_bdd(uo->child());
	      break;
	    }
	  case formula::BinOp:
	    {
	      const binop* bo = static_cast<const binop*>(f);
	      int op = 0;
	      switch (bo->op())
		{
		case binop::Xor:
		  op = bddop_xor;
		  break;
		case binop::Implies:
		  op = bddop_imp;
		  break;
		case binop::Equiv:
		  op = bddop_biimp;
		  break;
		default:
		  assert(!"Unsupported operator");
		}
	      result = bdd_apply(as_bdd(bo->first()), as_bdd(bo->second()), op);
	      break;
	    }
	  case formula::MultOp:
	    {
	      const multop* mo = static_cast<const multop*>(f);
	      switch (mo->op())
		{
		case multop::And:
		  {
		    result = bddtrue;
		    unsigned s = mo->size();
		    for (unsigned n = 0; n < s; ++n)
		      result &= as_bdd(mo->nth(n));
		    break;
		  }
		case multop::Or:
		  {
		    result = bddfalse;
		    unsigned s = mo->size();
		    for (unsigned n = 0; n < s; ++n)
		      result |= as_bdd(mo->nth(n));
		    break;
		  }
		case multop::AndNLM:
		case multop::AndRat:
		case multop::OrRat:
		case multop::Concat:
		case multop::Fusion:
		  assert(!"Unsupported operator");
		  break;
		}
	      break;
	    }
	  case formula::BUnOp:
	  case formula::AutomatOp:
	    assert(!"Unsupported operator");
	    break;
	  }

	// Cache the result before returning.
	as_bdd_[f->clone()] = result;
	return result;
      }

      const formula*
      lookup_nenoform(const formula* f)
      {
	f2f_map::const_iterator i = nenoform_.find(f);
	if (i == nenoform_.end())
	  return 0;
	return i->second->clone();
      }

      void
      cache_nenoform(const formula* orig, const formula* nenoform)
      {
	nenoform_[orig->clone()] = nenoform->clone();
      }

      // Return true iff the option set (syntactic implication
      // or containment checks) allow to prove that f1 => f2.
      bool
      implication(const formula* f1, const formula* f2)
      {
	trace << "[->] does " << to_string(f1) << " implies "
	      << to_string(f2) << " ?" << std::endl;
	if ((options.synt_impl && syntactic_implication(f1, f2))
	    || (options.containment_checks && contained(f1, f2)))
	  {
	    trace << "[->] Yes" << std::endl;
	    return true;
	  }
	trace << "[->] No" << std::endl;
	return false;
      }

      // Return true if f1 => f2 syntactically
      bool
      syntactic_implication(const formula* f1, const formula* f2);
      bool
      syntactic_implication_aux(const formula* f1, const formula* f2);

      // Return true if f1 => f2
      bool
      contained(const formula* f1, const formula* f2)
      {
	if (!f1->is_psl_formula() || !f2->is_psl_formula())
	  return false;
	return lcc.contained(f1, f2);
      }

      // If right==false, true if !f1 => f2, false otherwise.
      // If right==true, true if f1 => !f2, false otherwise.
      bool
      syntactic_implication_neg(const formula* f1, const formula* f2,
				bool right);

      // Return true if f1 => !f2
      bool contained_neg(const formula* f1, const formula* f2)
      {
	if (!f1->is_psl_formula() || !f2->is_psl_formula())
	  return false;
	trace << "[CN] Does (" << to_string(f1) << ") implies !("
	      << to_string(f2) << ") ?" << std::endl;
	if (lcc.contained_neg(f1, f2))
	  {
	    trace << "[CN] Yes" << std::endl;
	    return true;
	  }
	else
	  {
	    trace << "[CN] No" << std::endl;
	    return false;
	  }
      }

      // Return true if f1 => !f2
      bool neg_contained(const formula* f1, const formula* f2)
      {
	if (!f1->is_psl_formula() || !f2->is_psl_formula())
	  return false;
	trace << "[NC] Does (" << to_string(f1) << ") implies !("
	      << to_string(f2) << ") ?" << std::endl;
	if (lcc.neg_contained(f1, f2))
	  {
	    trace << "[NC] Yes" << std::endl;
	    return true;
	  }
	else
	  {
	    trace << "[NC] No" << std::endl;
	    return false;
	  }
      }

      // Return true iff the option set (syntactic implication
      // or containment checks) allow to prove that
      //   - !f1 => f2   (case where right=false)
      //   - f1 => !f2   (case where right=true)
      bool
      implication_neg(const formula* f1, const formula* f2, bool right)
      {
	trace << "[IN] Does " << (right ? "(" : "!(")
	      << to_string(f1) << ") implies "
	      << (right ? "!(" : "(") << to_string(f2) << ") ?"
	      << std::endl;
	if ((options.synt_impl && syntactic_implication_neg(f1, f2, right))
	    || (options.containment_checks && right && contained_neg(f1, f2))
	    || (options.containment_checks && !right && neg_contained(f1, f2)))
	  {
	    trace << "[IN] Yes" << std::endl;
	    return true;
	  }
	else
	  {
	    trace << "[IN] No" << std::endl;
	    return false;
	  }
      }

      const formula*
      lookup_simplified(const formula* f)
      {
	f2f_map::const_iterator i = simplified_.find(f);
	if (i == simplified_.end())
	  return 0;
	return i->second->clone();
      }

      void
      cache_simplified(const formula* orig, const formula* simplified)
      {
	simplified_[orig->clone()] = simplified->clone();
      }

      const formula*
      star_normal_form(const formula* f)
      {
	return ltl::star_normal_form(f, &snf_cache_);
      }

      const formula*
      star_normal_form_bounded(const formula* f)
      {
	return ltl::star_normal_form_bounded(f, &snfb_cache_);
      }


      const formula*
      boolean_to_isop(const formula* f)
      {
	f2f_map::const_iterator it = bool_isop_.find(f);
	if (it != bool_isop_.end())
	  return it->second->clone();

	assert(f->is_boolean());
	const formula* res = bdd_to_formula(as_bdd(f), dict);
	bool_isop_[f->clone()] = res->clone();
	return res;
      }

    private:
      f2b_map as_bdd_;
      f2f_map simplified_;
      f2f_map nenoform_;
      syntimpl_cache_t syntimpl_;
      snf_cache snf_cache_;
      snf_cache snfb_cache_;
      f2f_map bool_isop_;
    };


    namespace
    {
      //////////////////////////////////////////////////////////////////////
      //
      //  NEGATIVE_NORMAL_FORM_VISITOR
      //
      //////////////////////////////////////////////////////////////////////

      // Forward declaration.
      const formula*
      nenoform_recursively(const formula* f,
			   bool negated,
			   ltl_simplifier_cache* c);

      class negative_normal_form_visitor: public visitor
      {
      public:
	negative_normal_form_visitor(bool negated, ltl_simplifier_cache* c)
	  : negated_(negated), cache_(c)
	{
	}

	virtual
	~negative_normal_form_visitor()
	{
	}

	const formula* result() const
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ap)
	{
	  const formula* f = ap->clone();
	  if (negated_)
	    result_ = unop::instance(unop::Not, f);
	  else
	    result_ = f;
	}

	void
	visit(const constant* c)
	{
	  // Negation of constants is taken care of in the constructor
	  // of unop::Not, so these cases should be caught by
	  // nenoform_recursively().
	  assert(!negated_);
	  result_ = c;
	  return;
	}

	void
	visit(const unop* uo)
	{
	  const formula* f = uo->child();
	  unop::type op = uo->op();
	  switch (op)
	    {
	    case unop::Not:
	      // "Not"s should be caught by nenoform_recursively().
	      assert(!"Not should not occur");
	      //result_ = recurse_(f, negated_ ^ true);
	      return;
	    case unop::X:
	      /* !Xa == X!a */
	      result_ = unop::instance(unop::X, recurse(f));
	      return;
	    case unop::F:
	      /* !Fa == G!a */
	      result_ = unop::instance(negated_ ? unop::G : unop::F,
				       recurse(f));
	      return;
	    case unop::G:
	      /* !Ga == F!a */
	      result_ = unop::instance(negated_ ? unop::F : unop::G,
				       recurse(f));
	      return;
	    case unop::Closure:
	      result_ = unop::instance(negated_ ?
				       unop::NegClosure : unop::Closure,
				       recurse_(f, false));
	      return;
	    case unop::NegClosure:
	    case unop::NegClosureMarked:
	      result_ = unop::instance(negated_ ?
				       unop::Closure : op,
				       recurse_(f, false));
	      return;
	      /* !Finish(x), is not simplified */
	    case unop::Finish:
	      result_ = unop::instance(uo->op(), recurse_(f, false));
	      if (negated_)
		result_ = unop::instance(unop::Not, result_);
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const bunop* bo)
	{
	  // !(a*) should never occur.
	  assert(!negated_);
	  result_ = bunop::instance(bo->op(), recurse_(bo->child(), false),
				    bo->min(), bo->max());
	}

	const formula* equiv_or_xor(bool equiv,
				    const formula* f1,
				    const formula* f2)
	{
	  // Rewrite a<=>b as (a&b)|(!a&!b)
	  if (equiv)
	    return
	      multop::instance(multop::Or,
			       multop::instance(multop::And,
						recurse_(f1, false),
						recurse_(f2, false)),
			       multop::instance(multop::And,
						recurse_(f1, true),
						recurse_(f2, true)));
	  else
	    // Rewrite a^b as (a&!b)|(!a&b)
	    return
	      multop::instance(multop::Or,
			       multop::instance(multop::And,
						recurse_(f1, false),
						recurse_(f2, true)),
			       multop::instance(multop::And,
						recurse_(f1, true),
						recurse_(f2, false)));
	}

	void
	visit(const binop* bo)
	{
	  const formula* f1 = bo->first();
	  const formula* f2 = bo->second();
	  switch (bo->op())
	    {
	    case binop::Xor:
	      // !(a ^ b) == a <=> b
	      result_ = equiv_or_xor(negated_, f1, f2);
	      return;
	    case binop::Equiv:
	      // !(a <=> b) == a ^ b
	      result_ = equiv_or_xor(!negated_, f1, f2);
	      return;
	    case binop::Implies:
	      if (negated_)
		// !(a => b) == a & !b
		result_ = multop::instance(multop::And,
					   recurse_(f1, false),
					   recurse_(f2, true));
	      else // a => b == !a | b
		result_ = multop::instance(multop::Or,
					   recurse_(f1, true),
					   recurse_(f2, false));
	      return;
	    case binop::U:
	      // !(a U b) == !a R !b
	      result_ = binop::instance(negated_ ? binop::R : binop::U,
					recurse(f1), recurse(f2));
	      return;
	    case binop::R:
	      // !(a R b) == !a U !b
	      result_ = binop::instance(negated_ ? binop::U : binop::R,
					recurse(f1), recurse(f2));
	      return;
	    case binop::W:
	      // !(a W b) == !a M !b
	      result_ = binop::instance(negated_ ? binop::M : binop::W,
					recurse(f1), recurse(f2));
	      return;
	    case binop::M:
	      // !(a M b) == !a W !b
	      result_ = binop::instance(negated_ ? binop::W : binop::M,
					recurse(f1), recurse(f2));
	      return;
	    case binop::UConcat:
	      // !(a []-> b) == a<>-> !b
	      result_ = binop::instance(negated_ ?
					binop::EConcat : binop::UConcat,
					recurse_(f1, false), recurse(f2));
	      return;
	    case binop::EConcat:
	      // !(a <>-> b) == a[]-> !b
	      result_ = binop::instance(negated_ ?
					binop::UConcat : binop::EConcat,
					recurse_(f1, false), recurse(f2));
	      return;
	    case binop::EConcatMarked:
	      // !(a <>-> b) == a[]-> !b
	      result_ = binop::instance(negated_ ?
					binop::UConcat :
					binop::EConcatMarked,
					recurse_(f1, false), recurse(f2));
	      return;
	    }
	  // Unreachable code.
	  assert(0);
	}

	void
	visit(const automatop* ao)
	{
	  bool negated = negated_;
	  negated_ = false;
	  automatop::vec* res = new automatop::vec;
	  unsigned aos = ao->size();
	  for (unsigned i = 0; i < aos; ++i)
	    res->push_back(recurse(ao->nth(i)));
	  result_ = automatop::instance(ao->get_nfa(), res, negated);
	}

	void
	visit(const multop* mo)
	{
	  multop::type op = mo->op();
	  /* !(a & b & c) == !a | !b | !c  */
	  /* !(a | b | c) == !a & !b & !c  */
	  if (negated_)
	    switch (op)
	      {
	      case multop::And:
		op = multop::Or;
		break;
	      case multop::Or:
		op = multop::And;
		break;
	      case multop::Concat:
	      case multop::Fusion:
	      case multop::AndNLM:
	      case multop::OrRat:
	      case multop::AndRat:
		break;
	      }
	  multop::vec* res = new multop::vec;
	  unsigned mos = mo->size();
	  switch (op)
	    {
	    case multop::And:
	    case multop::Or:
	      {
		for (unsigned i = 0; i < mos; ++i)
		  res->push_back(recurse(mo->nth(i)));
		result_ = multop::instance(op, res);
		break;
	      }
	    case multop::Concat:
	    case multop::Fusion:
	    case multop::AndNLM:
	    case multop::AndRat:
	    case multop::OrRat:
	      {
		for (unsigned i = 0; i < mos; ++i)
		  res->push_back(recurse_(mo->nth(i), false));
		result_ = multop::instance(op, res);
		assert(!negated_);
	      }
	    }
	}

	const formula*
	recurse_(const formula* f, bool negated)
	{
	  return nenoform_recursively(f, negated, cache_);
	}

	const formula*
	recurse(const formula* f)
	{
	  return recurse_(f, negated_);
	}

      protected:
	const formula* result_;
	bool negated_;
	ltl_simplifier_cache* cache_;
      };


      const formula*
      nenoform_recursively(const formula* f,
			   bool negated,
			   ltl_simplifier_cache* c)
      {
	if (const unop* uo = is_Not(f))
	  {
	    negated = !negated;
	    f = uo->child();
	  }

	const formula* key = f;
	if (negated)
	  key = unop::instance(unop::Not, f->clone());
	const formula* result = c->lookup_nenoform(key);
	if (result)
	  goto done;

	if (key->is_in_nenoform()
	    || (c->options.nenoform_stop_on_boolean && key->is_boolean()))
	  {
	    result = key->clone();
	  }
	else
	  {
	    negative_normal_form_visitor v(negated, c);
	    f->accept(v);
	    result = v.result();
	  }

	c->cache_nenoform(key, result);
      done:
	if (negated)
	  key->destroy();

	return result;
      }

      //////////////////////////////////////////////////////////////////////
      //
      //  SIMPLIFY_VISITOR
      //
      //////////////////////////////////////////////////////////////////////

      // Forward declaration.
      const formula*
      simplify_recursively(const formula* f, ltl_simplifier_cache* c);



      // X(a) R b   or   X(a) M b
      // This returns a.
      const formula*
      is_XRM(const formula* f)
      {
	const binop* bo = is_binop(f, binop::R, binop::M);
	if (!bo)
	  return 0;
	const unop* uo = is_X(bo->first());
	if (!uo)
	  return 0;
	return uo->child();
      }

      // X(a) W b   or   X(a) U b
      // This returns a.
      const formula*
      is_XWU(const formula* f)
      {
	const binop* bo = is_binop(f, binop::W, binop::U);
	if (!bo)
	  return 0;
	const unop* uo = is_X(bo->first());
	if (!uo)
	  return 0;
	return uo->child();
      }

      // b & X(b W a)  or   b & X(b U a)
      // This returns (b W a) or (b U a).
      const binop*
      is_bXbWU(const formula* f)
      {
	const multop* mo = is_multop(f, multop::And);
	if (!mo)
	  return 0;
	unsigned s = mo->size();
	for (unsigned pos = 0; pos < s; ++pos)
	  {
	    const unop* u = is_X(mo->nth(pos));
	    if (!u)
	      continue;
	    const binop* bo = is_binop(u->child(), binop::U, binop::W);
	    if (!bo)
	      continue;
	    const formula* b = mo->all_but(pos);
	    bool result = (b == bo->first());
	    b->destroy();
	    if (result)
	      return bo;
	  }
	return 0;
      }

      // b | X(b R a)  or   b | X(b M a)
      // This returns (b R a) or (b M a).
      const binop*
      is_bXbRM(const formula* f)
      {
	const multop* mo = is_multop(f, multop::Or);
	if (!mo)
	  return 0;
	unsigned s = mo->size();
	for (unsigned pos = 0; pos < s; ++pos)
	  {
	    const unop* u = is_X(mo->nth(pos));
	    if (!u)
	      continue;
	    const binop* bo = is_binop(u->child(), binop::R, binop::M);
	    if (!bo)
	      continue;
	    const formula* b = mo->all_but(pos);
	    bool result = (b == bo->first());
	    b->destroy();
	    if (result)
	      return bo;
	  }
	return 0;
      }

      const formula*
      unop_multop(unop::type uop, multop::type mop, multop::vec* v)
      {
	return unop::instance(uop, multop::instance(mop, v));
      }

      const formula*
      unop_unop_multop(unop::type uop1, unop::type uop2, multop::type mop,
		       multop::vec* v)
      {
	return unop::instance(uop1, unop_multop(uop2, mop, v));
      }

      const formula*
      unop_unop(unop::type uop1, unop::type uop2, const formula* f)
      {
	return unop::instance(uop1, unop::instance(uop2, f));
      }

      struct mospliter
      {
	enum what { Split_GF = (1 << 0),
		    Strip_GF = (1 << 1) | (1 << 0),
		    Split_FG = (1 << 2),
		    Strip_FG = (1 << 3) | (1 << 2),
		    Split_F = (1 << 4),
		    Strip_F = (1 << 5) | (1 << 4),
		    Split_G = (1 << 6),
		    Strip_G = (1 << 7) | (1 << 6),
		    Strip_X = (1 << 8),
		    Split_U_or_W = (1 << 9),
		    Split_R_or_M = (1 << 10),
		    Split_EventUniv = (1 << 11),
		    Split_Event = (1 << 12),
		    Split_Univ = (1 << 13),
		    Split_Bool = (1 << 14)
	};

	void init()
	{
	  res_GF = (split_ & Split_GF) ? new multop::vec : 0;
	  res_FG = (split_ & Split_FG) ? new multop::vec : 0;
	  res_F = (split_ & Split_F) ? new multop::vec : 0;
	  res_G = (split_ & Split_G) ? new multop::vec : 0;
	  res_X = (split_ & Strip_X) ? new multop::vec : 0;
	  res_U_or_W = (split_ & Split_U_or_W) ? new multop::vec : 0;
	  res_R_or_M = (split_ & Split_R_or_M) ? new multop::vec : 0;
	  res_EventUniv = (split_ & Split_EventUniv) ? new multop::vec : 0;
	  res_Event = (split_ & Split_Event) ? new multop::vec : 0;
	  res_Univ = (split_ & Split_Univ) ? new multop::vec : 0;
	  res_Bool = (split_ & Split_Bool) ? new multop::vec : 0;
	  res_other = new multop::vec;
	}

	void process(const formula* f)
	{
	  bool e = f->is_eventual();
	  bool u = f->is_universal();
	  bool eu = res_EventUniv && e & u && c_->options.favor_event_univ;
	  switch (f->kind())
	    {
	    case formula::UnOp:
	      {
		const unop* uo = static_cast<const unop*>(f);
		const formula* c = uo->child();
		switch (uo->op())
		  {
		  case unop::X:
		    if (res_X && !eu)
		      {
			res_X->push_back(c->clone());
			return;
		      }
		    break;
		  case unop::F:
		    if (res_FG && u)
		      if (const unop* cc = is_G(c))
			{
			  res_FG->push_back(((split_ & Strip_FG) == Strip_FG
					     ? cc->child() : f)->clone());
			  return;
			}
		    if (res_F && !eu)
		      {
			res_F->push_back(((split_ & Strip_F) == Strip_F
					  ? c : f)->clone());
			return;
		      }
		    break;
		  case unop::G:
		    if (res_GF && e)
		      if (const unop* cc = is_F(c))
			{
			  res_GF->push_back(((split_ & Strip_GF) == Strip_GF
					     ? cc->child() : f)->clone());
			  return;
			}
		    if (res_G && !eu)
		      {
			res_G->push_back(((split_ & Strip_G) == Strip_G
					  ? c : f)->clone());
			return;
		      }
		    break;
		  default:
		    break;
		  }
	      }
	      break;
	    case formula::BinOp:
	      {
		const binop* bo = static_cast<const binop*>(f);
		switch (bo->op())
		  {
		  case binop::U:
		  case binop::W:
		    if (res_U_or_W)
		      {
			res_U_or_W->push_back(bo->clone());
			return;
		      }
		    break;
		  case binop::R:
		  case binop::M:
		    if (res_R_or_M)
		      {
			res_R_or_M->push_back(bo->clone());
			return;
		      }
		    break;
		  default:
		    break;
		  }
	      }
	      break;
	    default:
	      if (res_Bool && f->is_boolean())
		{
		  res_Bool->push_back(f->clone());
		  return;
		}
	      break;
	    }
	  if (c_->options.event_univ)
	    {
	      if (res_EventUniv && e && u)
		{
		  res_EventUniv->push_back(f->clone());
		  return;
		}
	      if (res_Event && e)
		{
		  res_Event->push_back(f->clone());
		  return;
		}
	      if (res_Univ && u)
		{
		  res_Univ->push_back(f->clone());
		  return;
		}
	    }

	  res_other->push_back(f->clone());
	}

	mospliter(unsigned split, multop::vec* v, ltl_simplifier_cache* cache)
	  : split_(split), c_(cache)
	{
	  init();
	  multop::vec::const_iterator end = v->end();
	  for (multop::vec::const_iterator i = v->begin(); i < end; ++i)
	    {
	      if (*i) // skip null pointers left by previous simplifications
		{
		  process(*i);
		  (*i)->destroy();
		}
	    }
	  delete v;
	}

	mospliter(unsigned split, const multop* mo,
		  ltl_simplifier_cache* cache)
	  : split_(split), c_(cache)
	{
	  init();
	  unsigned mos = mo->size();
	  for (unsigned i = 0; i < mos; ++i)
	    {
	      const formula* f = simplify_recursively(mo->nth(i), cache);
	      process(f);
	      f->destroy();
	    }
	  mo->destroy();
	}

	multop::vec* res_GF;
	multop::vec* res_FG;
	multop::vec* res_F;
	multop::vec* res_G;
	multop::vec* res_X;
	multop::vec* res_U_or_W;
	multop::vec* res_R_or_M;
	multop::vec* res_Event;
	multop::vec* res_Univ;
	multop::vec* res_EventUniv;
	multop::vec* res_Bool;
	multop::vec* res_other;
	unsigned split_;
	ltl_simplifier_cache* c_;
      };

      class simplify_visitor: public visitor
      {
      public:

	simplify_visitor(ltl_simplifier_cache* cache)
	  : c_(cache), opt_(cache->options)
	{
	}

	virtual ~simplify_visitor()
	{
	}

	const formula*
	result() const
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ap)
	{
	  result_ = ap->clone();
	}

	void
	visit(const constant* c)
	{
	  result_ = c;
	}

	void
	visit(const bunop* bo)
	{
	  bunop::type op = bo->op();
	  unsigned min = bo->min();
	  const formula* h = recurse(bo->child());
	  switch (op)
	    {
	    case bunop::Star:
	      if (h->accepts_eword())
		min = 0;
	      if (min == 0)
		{
		  const formula* s =
		    bo->max() == bunop::unbounded ?
		    c_->star_normal_form(h) :
		    c_->star_normal_form_bounded(h);
		  h->destroy();
		  h = s;
		}
	      result_ = bunop::instance(op, h, min, bo->max());
	      break;
	    }
	}

	// if !neg build c&X(c&X(...&X(tail))) with n occurences of c
	// if neg build !c|X(!c|X(...|X(tail))).
	const formula*
	dup_b_x_tail(bool neg, const formula* c,
		     const formula* tail, unsigned n)
	{
	  c->clone();
	  multop::type mop;
	  if (neg)
	    {
	      c = unop::instance(unop::Not, c);
	      mop = multop::Or;
	    }
	  else
	    {
	      mop = multop::And;
	    }
	  while (n--)
	    {
	      tail = unop::instance(unop::X, tail);
	      tail = // b&X(tail) or !b|X(tail)
		multop::instance(mop, c->clone(), tail);
	    }
	  c->destroy();
	  return tail;
	}

	void
	visit(const unop* uo)
	{
	  result_ = recurse(uo->child());

	  unop::type op = uo->op();
	  switch (op)
	    {
	    case unop::Not:
	      break;

	    case unop::X:
	      // X(constant) = constant is a trivial identity, but if
	      // the constant has been constructed by recurse() this
	      // identity has not been applied.
	      if (is_constant(result_))
		  return;

	      // Xf = f if f is both eventual and universal.
	      if (result_->is_universal() && result_->is_eventual())
		{
		  if (opt_.event_univ)
		    return;
		  // If EventUniv simplification is disabled, use
		  // only the following basic rewriting rules:
		  //   XGF(f) = GF(f) and XFG(f) = FG(f)
		  // The former comes from Somenzi&Bloem (CAV'00).
		  // It's not clear why they do not list the second.
		  if (opt_.reduce_basics &&
		      (is_GF(result_) || is_FG(result_)))
		    return;
		}


	      // If Xa = a, keep only a.
	      if (opt_.containment_checks_stronger
		  && c_->lcc.equal(result_, uo))
		return;

	      // X(f1 & GF(f2)) = X(f1) & GF(f2)
	      // X(f1 | GF(f2)) = X(f1) | GF(f2)
	      // X(f1 & FG(f2)) = X(f1) & FG(f2)
	      // X(f1 | FG(f2)) = X(f1) | FG(f2)
	      //
	      // The above usually make more sense when reversed (see
	      // them in the And and Or rewritings), except when we
	      // try to maximaze the size of subformula that do not
	      // have EventUniv formulae.
	      if (opt_.favor_event_univ)
		if (const multop* mo = is_multop(result_,
						 multop::Or, multop::And))
		  {
		    mospliter s(mospliter::Split_EventUniv, mo, c_);
		    multop::type op = mo->op();
		    s.res_EventUniv->push_back(unop_multop(unop::X, op,
							   s.res_other));
		    result_ = multop::instance(op, s.res_EventUniv);
		    if (result_ != uo)
		      result_ = recurse_destroy(result_);
		    return;
		  }
	      break;

	    case unop::F:
	      // F(constant) = constant is a trivial identity, but if
	      // the constant has been constructed by recurse() this
	      // identity has not been applied.
	      if (is_constant(result_))
		  return;

	      // If f is a pure eventuality formula then F(f)=f.
	      if (opt_.event_univ && result_->is_eventual())
		return;

	      if (opt_.reduce_basics)
		{
		  // F(a U b) = F(b)
		  const binop* bo = is_U(result_);
		  if (bo)
		    {
		      const formula* r =
			unop::instance(unop::F, bo->second()->clone());
		      bo->destroy();
		      result_ = recurse_destroy(r);
		      return;
		    }

		  // F(a M b) = F(a & b)
		  bo = is_M(result_);
		  if (bo)
		    {
		      const formula* r =
			unop::instance(unop::F,
				       multop::instance(multop::And,
							bo->first()->clone(),
							bo->second()->clone()));
		      bo->destroy();
		      result_ = recurse_destroy(r);
		      return;
		    }

		  // FX(a) = XF(a)
		  if (const unop* u = is_X(result_))
		    {
		      const formula* res =
			unop_unop(unop::X, unop::F, u->child()->clone());
		      u->destroy();
		      // FXX(a) = XXF(a) ...
		      // FXG(a) = XFG(a) = FG(a) ...
		      result_ = recurse_destroy(res);
		      return;
		    }

		  // FG(a & Xb) = FG(a & b)
		  // FG(a & Gb) = FG(a & b)
		  if (const unop* g = is_G(result_))
		    if (const multop* m = is_And(g->child()))
		      if (!m->is_boolean())
			{
			  m->clone();
			  mospliter s(mospliter::Strip_G | mospliter::Strip_X,
				      m, c_);
			  if (!s.res_G->empty() || !s.res_X->empty())
			    {
			      result_->destroy();
			      s.res_other->insert(s.res_other->begin(),
						  s.res_G->begin(),
						  s.res_G->end());
			      delete s.res_G;
			      s.res_other->insert(s.res_other->begin(),
						  s.res_X->begin(),
						  s.res_X->end());
			      delete s.res_X;
			      const formula* in =
				multop::instance(multop::And, s.res_other);
			      result_ =
				recurse_destroy(unop_unop(unop::F, unop::G,
							  in));
			      return;
			    }
			  else
			    {
			      for (multop::vec::iterator j =
				     s.res_other->begin();
				   j != s.res_other->end(); ++j)
				if (*j)
				  (*j)->destroy();
			      delete s.res_other;
			      delete s.res_G;
			      delete s.res_X;
			      // and continue...
			    }
			}
		}

	      // if Fa => a, keep a.
	      if (opt_.containment_checks_stronger
		  && c_->lcc.contained(uo, result_))
		return;

	      // Disabled by default:
	      //     F(f1 & GF(f2)) = F(f1) & GF(f2)
	      //
	      // As is, these two formulae are translated into
	      // equivalent Büchi automata so the rewriting is
	      // useless.
	      //
	      // However when taken in a larger formula such as F(f1
	      // & GF(f2)) | F(a & GF(b)), this rewriting used to
	      // produce (F(f1) & GF(f2)) | (F(a) & GF(b)), missing
	      // the opportunity to apply the F(E1)|F(E2) = F(E1|E2)
	      // rule which really helps the translation. F((f1 &
	      // GF(f2)) | (a & GF(b))) is indeed easier to translate.
	      //
	      // So we do not consider this rewriting rule by default.
	      // However if favor_event_univ is set, we want to move
	      // the GF out of the F.
	      if (opt_.favor_event_univ)
		// F(f1&f2&FG(f3)&FG(f4)&f5&f6) =
		//                        F(f1&f2) & FG(f3&f4) & f5 & f6
		// if f5 and f6 are both eventual and universal.
		if (const multop* mo = is_And(result_))
		  {
		    mo->clone();
		    mospliter s(mospliter::Strip_FG |
				mospliter::Split_EventUniv,
				mo, c_);
		    s.res_EventUniv->
		      push_back(unop_multop(unop::F, multop::And,
					    s.res_other));
		    s.res_EventUniv->
		      push_back(unop_unop_multop(unop::F, unop::G,
						 multop::And, s.res_FG));
		    result_ = multop::instance(multop::And, s.res_EventUniv);
		    if (result_ != uo)
		      {
			mo->destroy();
			result_ = recurse_destroy(result_);
			return;
		      }
		    else
		      {
			// Revert to the previous value of result_,
			// for the next simplification.
			result_->destroy();
			result_ = mo;
		      }
		  }
	      // If u3 and u4 are universal formulae and h is not:
	      // F(f1 | f2 | Fu3 | u4 | FGg | Fh)
	      //    = F(f1 | f2 | u3 | u4 | Gg | h)
	      // or
	      // F(f1 | f2 | Fu3 | u4 | FGg | Fh)
	      //    = F(f1 | f2 | h) | F(u3 | u4 | Gg)
	      // depending on whether favor_event_univ is set.
	      if (const multop* mo = is_Or(result_))
		{
		  mo->clone();
		  int w = mospliter::Strip_F;
		  if (opt_.favor_event_univ)
		    w |= mospliter::Split_Univ;
		  mospliter s(w, mo, c_);
		  s.res_other->insert(s.res_other->end(),
				      s.res_F->begin(), s.res_F->end());
		  delete s.res_F;
		  result_ = unop_multop(unop::F, multop::Or, s.res_other);
		  if (s.res_Univ)
		    {
		      // Strip any F.
		      for (multop::vec::iterator i = s.res_Univ->begin();
			   i != s.res_Univ->end(); ++i)
			if (const unop* u = is_F(*i))
			  {
			    *i = u->child()->clone();
			    u->destroy();
			  }
		      const formula* fu =
			unop_multop(unop::F, multop::Or, s.res_Univ);
		      result_ = multop::instance(multop::Or, result_, fu);
		    }
		  if (result_ != uo)
		    {
		      mo->destroy();
		      result_ = recurse_destroy(result_);
		      return;
		    }
		  else
		    {
		      // Revert to the previous value of result_,
		      // for the next simplification.
		      result_->destroy();
		      result_ = mo;
		    }
		}
	      break;

	    case unop::G:
	      // G(constant) = constant is a trivial identity, but if
	      // the constant has been constructed by recurse() this
	      // identity has not been applied.
	      if (is_constant(result_))
		  return;

	      // If f is a pure universality formula then G(f)=f.
	      if (opt_.event_univ && result_->is_universal())
		return;

	      if (opt_.reduce_basics)
		{
		  // G(a R b) = G(b)
		  const binop* bo = is_R(result_);
		  if (bo)
		    {
		      const formula* r =
			unop::instance(unop::G, bo->second()->clone());
		      bo->destroy();
		      result_ = recurse_destroy(r);
		      return;
		    }

		  // G(a W b) = G(a | b)
		  bo = is_W(result_);
		  if (bo)
		    {
		      const formula* r =
			unop::instance(unop::G,
				       multop::instance(multop::Or,
							bo->first()->clone(),
							bo->second()->clone()));
		      bo->destroy();
		      result_ = recurse_destroy(r);
		      return;
		    }

		  // GX(a) = XG(a)
		  if (const unop* u = is_X(result_))
		    {
		      const formula* res =
			unop_unop(unop::X, unop::G, u->child()->clone());
		      u->destroy();
		      // GXX(a) = XXG(a) ...
		      // GXF(a) = XGF(a) = GF(a) ...
		      result_ = recurse_destroy(res);
		      return;
		    }

		  // G(f1|f2|GF(f3)|GF(f4)|f5|f6) =
		  //                        G(f1|f2) | GF(f3|f4) | f5 | f6
		  // if f5 and f6 are both eventual and universal.
		  if (const multop* mo = is_Or(result_))
		    {
		      mo->clone();
		      mospliter s(mospliter::Strip_GF |
				  mospliter::Split_EventUniv,
				  mo, c_);
		      s.res_EventUniv->
			push_back(unop_multop(unop::G, multop::Or,
					      s.res_other));
		      s.res_EventUniv->
			push_back(unop_unop_multop(unop::G, unop::F,
						   multop::Or, s.res_GF));
		      result_ = multop::instance(multop::Or,
						 s.res_EventUniv);
		      if (result_ != uo)
			{
			  mo->destroy();
			  result_ = recurse_destroy(result_);
			  return;
			}
		      else
			{
			  // Revert to the previous value of result_,
			  // for the next simplification.
			  result_->destroy();
			  result_ = mo;
			}
		    }
		  // If e3 and e4 are eventual formulae and h is not:
		  // G(f1 & f2 & Ge3 & e4 & GFg & Gh)
		  //    = G(f1 & f2 & e3 & e4 & Fg & h)
		  // or
		  // G(f1 & f2 & Ge3 & e4 & GFg & Gh)
		  //    = G(f1 & f2 & h) & G(e3 & e4 & Fg)
		  // depending on whether favor_event_univ is set.
		  else if (const multop* mo = is_And(result_))
		    {
		      mo->clone();
		      int w = mospliter::Strip_G;
		      if (opt_.favor_event_univ)
			w |= mospliter::Split_Event;
		      mospliter s(w, mo, c_);
		      s.res_other->insert(s.res_other->end(),
					  s.res_G->begin(), s.res_G->end());
		      delete s.res_G;
		      result_ = unop_multop(unop::G, multop::And, s.res_other);
		      if (s.res_Event)
			{
			  // Strip any G.
			  for (multop::vec::iterator i = s.res_Event->begin();
			       i != s.res_Event->end(); ++i)
			    if (const unop* u = is_G(*i))
			      {
				*i = u->child()->clone();
				u->destroy();
			      }
			  const formula* ge =
			    unop_multop(unop::G, multop::And, s.res_Event);
			  result_ = multop::instance(multop::And, result_, ge);
			}
		      if (result_ != uo)
			{
			  mo->destroy();
			  result_ = recurse_destroy(result_);
			  return;
			}
		      else
			{
			  // Revert to the previous value of result_,
			  // for the next simplification.
			  result_->destroy();
			  result_ = mo;
			}
		    }

		  // GF(a | Xb) = GF(a | b)
		  // GF(a | Fb) = GF(a | b)
		  if (const unop* f = is_F(result_))
		    if (const multop* m = is_Or(f->child()))
		      if (!m->is_boolean())
			{
			  m->clone();
			  mospliter s(mospliter::Strip_F | mospliter::Strip_X,
				      m, c_);
			  if (!s.res_F->empty() || !s.res_X->empty())
			    {
			      result_->destroy();
			      s.res_other->insert(s.res_other->begin(),
						  s.res_F->begin(),
						  s.res_F->end());
			      delete s.res_F;
			      s.res_other->insert(s.res_other->begin(),
						  s.res_X->begin(),
						  s.res_X->end());
			      delete s.res_X;
			      const formula* in =
				multop::instance(multop::Or, s.res_other);
			      result_ =
				recurse_destroy(unop_unop(unop::G, unop::F,
							  in));
			      return;
			    }
			  else
			    {
			      for (multop::vec::iterator j =
				     s.res_other->begin();
				   j != s.res_other->end(); ++j)
				if (*j)
				  (*j)->destroy();
			      delete s.res_other;
			      delete s.res_F;
			      delete s.res_X;
			      // and continue...
			    }
			}
		}
	      // if a => Ga, keep a.
	      if (opt_.containment_checks_stronger
		  && c_->lcc.contained(result_, uo))
		return;
	      break;
	    case unop::Closure:
	    case unop::NegClosure:
	    case unop::NegClosureMarked:
	      // {e[*]} = {e}
	      // !{e[*]} = !{e}
	      if (result_->accepts_eword())
		if (const bunop* bo = is_Star(result_))
		  {
		    result_ =
		      recurse_destroy(unop::instance(op,
						     bo->child()->clone()));
		    bo->destroy();
		    return;
		  }
	      if (!opt_.reduce_size_strictly)
		if (const multop* mo = is_OrRat(result_))
		  {
		    //  {a₁|a₂} =  {a₁}| {a₂}
		    // !{a₁|a₂} = !{a₁}&!{a₂}
		    unsigned s = mo->size();
		    multop::vec* v = new multop::vec;
		    for (unsigned n = 0; n < s; ++n)
		      v->push_back(unop::instance(op, mo->nth(n)->clone()));
		    mo->destroy();
		    result_ =
		      recurse_destroy(multop::instance(op == unop::Closure
						       ? multop::Or
						       : multop::And, v));
		    return;
		  }
	      if (const multop* mo = is_Concat(result_))
		{
		  if (mo->accepts_eword())
		    {
		      if (opt_.reduce_size_strictly)
			break;
		      // If all terms accept the empty word, we have
		      // {e₁;e₂;e₃} =  {e₁}|{e₂}|{e₃}
		      // !{e₁;e₂;e₃} = !{e₁}&!{e₂}&!{e₃}
		      multop::vec* v = new multop::vec;
		      unsigned end = mo->size();
		      v->reserve(end);
		      for (unsigned i = 0; i < end; ++i)
			v->push_back(unop::instance(op, mo->nth(i)->clone()));
		      mo->destroy();
		      result_ = multop::instance(op == unop::Closure ?
						 multop::Or : multop::And, v);
		      result_ = recurse_destroy(result_);
		      return;
		    }

		  // Some term does not accept the empty word.
		  unsigned end = mo->size() - 1;
		  // {b₁;b₂;b₃*;e₁;f₁;e₂;f₂;e₂;e₃;e₄}
		  //    = b₁&X(b₂&X(b₃ W {e₁;f₁;e₂;f₂}))
		  // !{b₁;b₂;b₃*;e₁;f₁;e₂;f₂;e₂;e₃;e₄}
		  //    = !b₁|X(!b₂|X(!b₃ M !{e₁;f₁;e₂;f₂}))
		  // if e denotes a term that accepts [*0]
		  // and b denotes a Boolean formula.
		  while (mo->nth(end)->accepts_eword())
		    --end;
		  unsigned start = 0;
		  while (start <= end)
		    {
		      const formula* r = mo->nth(start);
		      const bunop* es = is_KleenStar(r);
		      if ((r->is_boolean() && !opt_.reduce_size_strictly)
			  || (es && es->child()->is_boolean()))
			++start;
		      else
			break;
		    }
		  unsigned s = end + 1 - start;
		  if (s != mo->size())
		    {
		      bool doneg = op != unop::Closure;
		      const formula* tail;
		      if (s > 0)
			{
			  multop::vec* v = new multop::vec;
			  v->reserve(s);
			  for (unsigned n = start; n <= end; ++n)
			    v->push_back(mo->nth(n)->clone());
			  tail = multop::instance(multop::Concat, v);
			  tail = unop::instance(op, tail);
			}
		      else
			{
			  if (doneg)
			    tail = constant::false_instance();
			  else
			    tail = constant::true_instance();
			}

		      for (unsigned n = start; n > 0;)
			{
			  --n;
			  const formula* e = mo->nth(n);
			  // {b;f} = b & X{f}
			  // !{b;f} = !b | X!{f}
			  if (e->is_boolean())
			    {
			      tail = unop::instance(unop::X, tail);
			      e->clone();
			      if (doneg)
				tail =
				  multop::instance(multop::Or,
						   unop::instance(unop::Not, e),
						   tail);
			      else
				tail =
				  multop::instance(multop::And, e, tail);
			    }
			  // {b*;f} = b W {f}
			  // !{b*;f} = !b M !{f}
			  else
			    {
			      const bunop* es = is_KleenStar(e);
			      assert(es);
			      const formula* c = es->child()->clone();
			      if (doneg)
				tail =
				  binop::instance(binop::M,
						  unop::instance(unop::Not, c),
						  tail);
			      else
				tail = binop::instance(binop::W, c, tail);
			    }
			}
		      mo->destroy();
		      result_ = recurse_destroy(tail);
		      return;
		    }

		  // {b[*i..j];c} = b&X(b&X(... b&X{b[*0..j-i];c}))
		  // !{b[*i..j];c} = !b&X(!b&X(... !b&X!{b[*0..j-i];c}))
		  if (!opt_.reduce_size_strictly)
		    if (const bunop* s = is_Star(mo->nth(0)))
		      {
			const formula* c = s->child();
			unsigned min = s->min();
			if (c->is_boolean() && min > 0)
			  {
			    unsigned max = s->max();
			    if (max != bunop::unbounded)
			      max -= min;
			    unsigned ss = mo->size();
			    multop::vec* v = new multop::vec;
			    v->reserve(ss);
			    v->push_back(bunop::instance(bunop::Star,
							 c->clone(),
							 0, max));
			    for (unsigned n = 1; n < ss; ++n)
			      v->push_back(mo->nth(n)->clone());
			    const formula* tail =
			      multop::instance(multop::Concat, v);
			    tail = // {b[*0..j-i]} or !{b[*0..j-i]}
			      unop::instance(op, tail);
			    tail =
			      dup_b_x_tail(op != unop::Closure,
					   c, tail, min);
			    mo->destroy();
			    result_ = recurse_destroy(tail);
			    return;
			  }
			}
		}
	      // {b[*i..j]} = b&X(b&X(... b))  with i occurences of b
	      // !{b[*i..j]} = !b&X(!b&X(... !b))
	      if (!opt_.reduce_size_strictly)
		if (const bunop* s = is_Star(result_))
		  {
		    const formula* c = s->child();
		    if (c->is_boolean())
		      {
			unsigned min = s->min();
			assert(min > 0);
			const formula* tail;
			if (op == unop::Closure)
			  tail =
			    dup_b_x_tail(false,
					 c, constant::true_instance(), min);
			else
			  tail =
			    dup_b_x_tail(true,
					 c, constant::false_instance(), min);
			result_->destroy();
			result_ = recurse_destroy(tail);
			return;
		      }
		  }
	      break;
	    case unop::Finish:
	      // No simplification
	      break;
	    }
	  result_ = unop::instance(op, result_);
	}

	// Return true iff reduction occurred.
	bool
	reduce_sere_ltl(binop::type bindop, const formula* a, const formula* b)
	{
	  // All this function is documented assuming bindop ==
	  // UConcat, but by changing the following variable it can
	  // perform the rules for EConcat as well.
	  unop::type op_g;
	  binop::type op_w;
	  binop::type op_r;
	  multop::type op_and;
	  bool doneg;
	  if (bindop == binop::UConcat)
	    {
	      op_g = unop::G;
	      op_w = binop::W;
	      op_r = binop::R;
	      op_and = multop::And;
	      doneg = true;
	    }
	  else // EConcat & EConcatMarked
	    {
	      op_g = unop::F;
	      op_w = binop::M;
	      op_r = binop::U;
	      op_and = multop::Or;
	      doneg = false;
	    }

	  if (!opt_.reduce_basics)
	    return false;
	  if (const bunop* bu = is_Star(a))
	    {
	      // {[*]}[]->b = Gb
	      if (a == bunop::one_star())
		{
		  a->destroy();
		  result_ = recurse_destroy(unop::instance(op_g, b));
		  return true;
		}
	      const formula* s = bu->child();
	      unsigned min = bu->min();
	      unsigned max = bu->max();
	      // {s[*]}[]->b = b W !s   if s is Boolean.
	      // {s[+]}[]->b = b W !s   if s is Boolean.
	      if (s->is_boolean() && max == bunop::unbounded && min <= 1)
		{
		  const formula* ns = // !s
		    doneg ? unop::instance(unop::Not, s->clone()) : s->clone();
		  result_ = // b W !s
		    binop::instance(op_w, b, ns);
		  bu->destroy();
		  result_ = recurse_destroy(result_);
		  return true;
		}
	      if (opt_.reduce_size_strictly)
		return false;
	      // {s[*i..j]}[]->b = {s;s;...;s[*1..j-i+1]}[]->b
	      // = {s}[]->X({s}[]->X(...[]->X({s[*1..j-i+1]}[]->b)))
	      // if i>0 and s does not accept the empty word
	      if (min == 0 || s->accepts_eword())
		return false;
	      --min;
	      if (max != bunop::unbounded)
		max -= min; // j-i+1
	      // Don't rewrite s[1..].
	      if (min == 0)
		return false;
	      const formula* tail = // {s[*1..j-i]}[]->b
		binop::instance(bindop,
				bunop::instance(bunop::Star,
						s->clone(), 1, max),
				b);
	      for (unsigned n = 0; n < min; ++n)
		tail = // {s}[]->X(tail)
		  binop::instance(bindop,
				  s->clone(),
				  unop::instance(unop::X, tail));
	      result_ = tail;
	      bu->destroy();
	      result_ = recurse_destroy(result_);
	      return true;
	    }
	  else if (const multop* mo = is_Concat(a))
	    {
	      unsigned s = mo->size() - 1;
	      const formula* last = mo->nth(s);
	      // {r;[*]}[]->b = {r}[]->Gb
	      if (last == bunop::one_star())
		{
		  result_ =
		    binop::instance(bindop,
				    mo->all_but(s), unop::instance(op_g, b));
		  mo->destroy();
		  result_ = recurse_destroy(result_);
		  return true;
		}

	      const formula* first = mo->nth(0);
	      // {[*];r}[]->b = G({r}[]->b)
	      if (first == bunop::one_star())
		{
		  result_ =
		    unop::instance(op_g,
				   binop::instance(bindop, mo->all_but(0), b));
		  mo->destroy();
		  result_ = recurse_destroy(result_);
		  return true;
		}

	      if (opt_.reduce_size_strictly)
		return false;

	      // {r;s[*]}[]->b = {r}[]->(b & X(b W !s))
	      // if s is Boolean and r does not accept [*0];
	      if (const bunop* l = is_KleenStar(last)) // l = s[*]
		if (l->child()->is_boolean())
		  {
		    const formula* r = mo->all_but(s);
		    if (!r->accepts_eword())
		      {
			const formula* ns = // !s
			  doneg
			  ? unop::instance(unop::Not, l->child()->clone())
			  : l->child()->clone();
			const formula* w = // b W !s
			  binop::instance(op_w, b->clone(), ns);
			const formula* x = // X(b W !s)
			  unop::instance(unop::X, w);
			const formula* d = // b & X(b W !s)
			  multop::instance(op_and, b, x);
			result_ = // {r}[]->(b & X(b W !s))
			  binop::instance(bindop, r, d);
			mo->destroy();
			result_ = recurse_destroy(result_);
			return true;
		      }
		    r->destroy();
		  }
	      // {s[*];r}[]->b = !s R ({r}[]->b)
	      // if s is Boolean and r does not accept [*0];
	      if (const bunop* l = is_KleenStar(first))
		if (l->child()->is_boolean())
		  {
		    const formula* r = mo->all_but(0);
		    if (!r->accepts_eword())
		      {
			const formula* ns = // !s
			  doneg
			  ? unop::instance(unop::Not, l->child()->clone())
			  : l->child()->clone();
			const formula* u = // {r}[]->b
			  binop::instance(bindop, r, b);
			result_ = // !s R ({r}[]->b)
			  binop::instance(op_r, ns, u);
			mo->destroy();
			result_ = recurse_destroy(result_);
			return true;
		      }
		    r->destroy();
		  }

	      // {r₁;r₂;r₃}[]->b = {r₁}[]->X({r₂}[]->X({r₃}[]->b))
	      // if r₁, r₂, r₃ do not accept [*0].
	      if (!mo->accepts_eword())
		{
		  unsigned count = 0;
		  for (unsigned n = 0; n <= s; ++n)
		    count += !mo->nth(n)->accepts_eword();
		  assert(count > 0);
		  if (count == 1)
		    return false;
		  // Let e denote a term that accepts [*0]
		  // and let f denote a term that do not.
		  // A formula such as {e₁;f₁;e₂;e₃;f₂;e₄}[]->b
		  // in which count==2 will be grouped
		  // as follows:  r₁ = e₁;f₁;e₂;e₃
		  //              r₂ = f₂;e₄
		  // this way we have
		  // {e₁;f₁;e₂;e₃;f₂;e₄}[]->b = {r₁;r₂;r₃}[]->b
		  // where r₁ and r₂ do not accept [*0].
		  unsigned pos = s + 1;

		  // We compute the r formulas from the right
		  // (i.e., r₂ before r₁.)
		  multop::vec* r = new multop::vec;
		  do
		    r->insert(r->begin(), mo->nth(--pos)->clone());
		  while (r->front()->accepts_eword());
		  const formula* tail = // {r₂}[]->b
		    binop::instance(bindop,
				    multop::instance(multop::Concat, r),
				    b);
		  while (--count)
		    {
		      multop::vec* r = new multop::vec;
		      do
			r->insert(r->begin(), mo->nth(--pos)->clone());
		      while (r->front()->accepts_eword());
		      // If it's the last block, take all leading
		      // formulae as well.
		      if (count == 1)
			while (pos > 0)
			  {
			    r->insert(r->begin(), mo->nth(--pos)->clone());
			    assert(r->front()->accepts_eword());
			  }

		      tail = // X({r₂}[]->b)
			unop::instance(unop::X, tail);
		      tail = // {r₁}[]->X({r₂}[]->b)
			binop::instance(bindop,
					multop::instance(multop::Concat, r),
					tail);
		    }
		  mo->destroy();
		  result_ = recurse_destroy(tail);
		  return true;
		}
	    }
	  else if (opt_.reduce_size_strictly)
	    {
	      return false;
	    }
	  else if (const multop* mo = is_Fusion(a))
	    {
	      // {r₁:r₂:r₃}[]->b = {r₁}[]->({r₂}[]->({r₃}[]->b))
	      unsigned s = mo->size();
	      const formula* tail = b;
	      do
		{
		  --s;
		  tail = binop::instance(bindop,
					 mo->nth(s)->clone(), tail);
		}
	      while (s != 0);
	      mo->destroy();
	      result_ = recurse_destroy(tail);
	      return true;
	    }
	  else if (const multop* mo = is_OrRat(a))
	    {
	      // {r₁|r₂|r₃}[]->b = ({r₁}[]->b)&({r₂}[]->b)&({r₃}[]->b)
	      unsigned s = mo->size();
	      multop::vec* v = new multop::vec;
	      for (unsigned n = 0; n < s; ++n)
		{
		  const formula* x = // {r₁}[]->b
		    binop::instance(bindop,
				    mo->nth(n)->clone(), b->clone());
		  v->push_back(x);
		}
	      mo->destroy();
	      b->destroy();
	      result_ = recurse_destroy(multop::instance(op_and, v));
	      return true;
	    }
	  return false;
	}

	void
	visit(const binop* bo)
	{
	  binop::type op = bo->op();

	  const formula* b = recurse(bo->second());

	  if (opt_.event_univ)
	    {
	      trace << "bo: trying eventuniv rules" << std::endl;
	      /* If b is a pure eventuality formula then a U b = b.
		 If b is a pure universality formula a R b = b. */
	      if ((b->is_eventual() && (op == binop::U))
		  || (b->is_universal() && (op == binop::R)))
		{
		  result_ = b;
		  return;
		}
	    }

	  const formula* a = recurse(bo->first());

	  if (opt_.event_univ)
	    {
	      /* If a is a pure eventuality formula then a M b = a & b.
		 If a is a pure universality formula a W b = a|b. */
	      if (a->is_eventual() && (op == binop::M))
		{
		  result_ =
		    recurse_destroy(multop::instance(multop::And, a, b));
		  return;
		}
	      if (a->is_universal() && (op == binop::W))
		{
		  result_ =
		    recurse_destroy(multop::instance(multop::Or, a, b));
		  return;
		}

	      // e₁ W e₂ = Ge₁ | e₂
	      // u₁ M u₂ = Fu₁ & u₂
	      if (!opt_.reduce_size_strictly)
		{
		  if (op == binop::W && a->is_eventual() && b->is_eventual())
		    {
		      result_ =
			recurse_destroy(multop::instance
					(multop::Or,
					 unop::instance(unop::G, a), b));
		      return;
		    }
		  if (op == binop::M && a->is_universal() && b->is_universal())
		    {
		      result_ =
			recurse_destroy(multop::instance
					(multop::And,
					 unop::instance(unop::F, a), b));
		      return;
		    }
		}

	      // In the following rewritings we assume that
	      // - e is a pure eventuality
	      // - u is purely universal
	      // - q is purely universal pure eventuality
	      // (a U (b|e)) = (a U b)|e
	      // (a W (b|e)) = (a W b)|e
	      // (a U (b&q)) = (a U b)&q
	      // ((a&q) M b) = (a M b)&q
	      // (a R (b&u)) = (a R b)&u
	      // (a M (b&u)) = (a M b)&u
	      if (opt_.favor_event_univ)
		{
		  if (op == binop::U || op == binop::W)
		    if (const multop* mo = is_Or(b))
		      {
			b->clone();
			mospliter s(mospliter::Split_Event, mo, c_);
			const formula* b2 =
			  multop::instance(multop::Or, s.res_other);
			if (b2 != b)
			  {
			    b->destroy();
			    s.res_Event->push_back(binop::instance(op, a, b2));
			    result_ =
			      recurse_destroy(multop::instance(multop::Or,
							       s.res_Event));
			    return;
			  }
			b2->destroy();
			delete s.res_Event;
		      }
		  if (op == binop::U)
		    if (const multop* mo = is_And(b))
		      {
			b->clone();
			mospliter s(mospliter::Split_EventUniv, mo, c_);
			const formula* b2 =
			  multop::instance(multop::And, s.res_other);
			if (b2 != b)
			  {
			    b->destroy();
			    s.res_EventUniv->push_back(binop::instance(op,
								       a, b2));
			    result_ = recurse_destroy
			      (multop::instance(multop::And, s.res_EventUniv));
			    return;
			  }
			b2->destroy();
			delete s.res_Event;
		      }
		  if (op == binop::M)
		    if (const multop* mo = is_And(a))
		      {
			a->clone();
			mospliter s(mospliter::Split_EventUniv, mo, c_);
			const formula* a2 =
			  multop::instance(multop::And, s.res_other);
			if (a2 != a)
			  {
			    a->destroy();
			    s.res_EventUniv->push_back(binop::instance(op,
								       a2, b));
			    result_ = recurse_destroy
			      (multop::instance(multop::And, s.res_EventUniv));
			    return;
			  }
			a2->destroy();
			delete s.res_EventUniv;
		      }
		  if (op == binop::R || op == binop::M)
		    if (const multop* mo = is_And(b))
		      {
			b->clone();
			mospliter s(mospliter::Split_Univ, mo, c_);
			const formula* b2 =
			  multop::instance(multop::And, s.res_other);
			if (b2 != b)
			  {
			    b->destroy();
			    s.res_Univ->push_back(binop::instance(op, a, b2));
			    result_ = recurse_destroy
			      (multop::instance(multop::And, s.res_Univ));
			    return;
			  }
			b2->destroy();
			delete s.res_Univ;
		      }
		}

	      trace << "bo: no eventuniv rule matched" << std::endl;
	    }

	  // Inclusion-based rules
	  if (opt_.synt_impl | opt_.containment_checks)
	    {
	      trace << "bo: trying inclusion-based rules" << std::endl;
	      switch (op)
		{
		case binop::Xor:
		case binop::Equiv:
		case binop::Implies:
		  assert(!"operator not supported for implication rules");
		  return;
		case binop::UConcat:
		case binop::EConcat:
		case binop::EConcatMarked:
		  break;

		case binop::U:
		  // if a => b, then a U b = b
		  // if (a U b) => b, then a U b = b (for stronger containment)
		  if (c_->implication(a, b)
		      || (opt_.containment_checks_stronger
			  && c_->contained(bo, b)))
		    {
		      a->destroy();
		      result_ = b;
		      return;
		    }
		  // if !a => b, then a U b = Fb
		  if (c_->implication_neg(a, b, false))
		    {
		      a->destroy();
		      result_ =
			recurse_destroy(unop::instance(unop::F, b));
		      return;
		    }
		  // if a => b, then a U (b U c) = (b U c)
		  // if a => b, then a U (b W c) = (b W c)
		  // if b => a, then a U (b U c) = (a U c)
		  // if a => c, then a U (b R (c U d)) = (b R (c U d))
		  // if a => c, then a U (b R (c W d)) = (b R (c W d))
		  // if a => c, then a U (b M (c U d)) = (b M (c U d))
		  // if a => c, then a U (b M (c W d)) = (b M (c W d))
		  if (const binop* bo = is_binop(b))
		    {
		      // if a => b, then a U (b U c) = (b U c)
		      // if a => b, then a U (b W c) = (b W c)
		      if ((bo->op() == binop::U || bo->op() == binop::W)
			  && c_->implication(a, bo->first()))
			{
			  a->destroy();
			  result_ = b;
			  return;
			}
		      // if b => a, then a U (b U c) = (a U c)
		      if (bo->op() == binop::U
			  && c_->implication(bo->first(), a))
			{
			  result_ = recurse_destroy
			    (binop::instance(binop::U,
					     a, bo->second()->clone()));
			  b->destroy();
			  return;
			}
		      // if a => c, then a U (b R (c U d)) = (b R (c U d))
		      // if a => c, then a U (b R (c W d)) = (b R (c W d))
		      // if a => c, then a U (b M (c U d)) = (b M (c U d))
		      // if a => c, then a U (b M (c W d)) = (b M (c W d))
		      if ((bo->op() == binop::R || bo->op() == binop::M)
			  && bo->second()->kind() == formula::BinOp)
			{
			  const binop* cd =
			    static_cast<const binop*>(bo->second());
			  if ((cd->op() == binop::U || cd->op() == binop::W)
			      && c_->implication(a, cd->first()))
			    {
			      a->destroy();
			      result_ = b;
			      return;
			    }
			}
		    }
		  // if a => b, then (a U c) U b = c U b
		  // if a => b, then (a W c) U b = c U b
		  // if c => b, then (a U c) U b = (a U c) | b
		  if (const binop* bo = is_binop(a))
		    {
		      if ((bo->op() == binop::U || bo->op() == binop::W)
			  && c_->implication(bo->first(), b))
			{
			  result_ = recurse_destroy
			    (binop::instance(binop::U,
					     bo->second()->clone(),
					     b));
			  a->destroy();
			  return;
			}
		      else if ((bo->op() == binop::U)
			       && c_->implication(bo->second(), b))
			{
			  result_ = recurse_destroy
			    (multop::instance(multop::Or, a, b));
			  return;
			}
		    }
		  break;

		case binop::R:
		  // if b => a, then a R b = b
		  if (c_->implication(b, a))
		    {
		      a->destroy();
		      result_ = b;
		      return;
		    }
		  // if b => !a, then a R b = Gb
		  if (c_->implication_neg(b, a, true))
		    {
		      a->destroy();
		      result_ = recurse_destroy(unop::instance(unop::G, b));
		      return;
		    }
		  if (b->kind() == formula::BinOp)
		    {
		      // if b => a, then a R (b R c) = b R c
		      // if b => a, then a R (b M c) = b M c
		      const binop* bo = static_cast<const binop*>(b);
		      if ((bo->op() == binop::R || bo->op() == binop::M)
			  && c_->implication(bo->first(), a))
			{
			  a->destroy();
			  result_ = b;
			  return;
			}

		      // if a => b, then a R (b R c) = a R c
		      if (bo->op() == binop::R
			  && c_->implication(a, bo->first()))
			{
			  result_ = recurse_destroy
			    (binop::instance(binop::R, a,
					     bo->second()->clone()));
			  b->destroy();
			  return;
			}
		    }

		  // if b => a, then (a R c) R b = c R b
		  // if b => a, then (a M c) R b = c R b
		  // if c => b, then (a R c) R b = (a & c) R b
		  // if c => b, then (a M c) R b = (a & c) R b
		  if (const binop* bo = is_binop(a))
		    {
		      if (bo->op() == binop::M || bo->op() == binop::R)
			{
			  if (c_->implication(b, bo->first()))
			    {
			      result_ = recurse_destroy
				(binop::instance(binop::R,
						 bo->second()->clone(),
						 b));
			      a->destroy();
			      return;
			    }
			  else if (c_->implication(bo->second(), b))
			    {
			      const formula* ac =
				multop::instance(multop::And,
						 bo->first()->clone(),
						 bo->second()->clone());
			      a->destroy();
			      result_ = recurse_destroy
				(binop::instance(binop::R, ac, b));
			      return;
			    }
			}
		    }

		  break;

		case binop::W:
		  // if a => b, then a W b = b
		  // if a W b => b, then a W b = b (for stronger containment)
		  if (c_->implication(a, b)
		      || (opt_.containment_checks_stronger
			  && c_->contained(bo, b)))
		    {
		      a->destroy();
		      result_ = b;
		      return;
		    }
		  // if !a => b then a W b = 1
		  if (c_->implication_neg(a, b, false))
		    {
		      a->destroy();
		      b->destroy();
		      result_ = constant::true_instance();
		      return;
		    }
		  // if a => b, then a W (b W c) = (b W c)
		  // (Beware: even if a => b we do not have a W (b U c) = b U c)
		  // if b => a, then a W (b U c) = (a W c)
		  // if b => a, then a W (b W c) = (a W c)
		  if (b->kind() == formula::BinOp)
		    {
		      const binop* bo = static_cast<const binop*>(b);
		      // if a => b, then a W (b W c) = (b W c)
		      if (bo->op() == binop::W
			  && c_->implication(a, bo->first()))
			{
			  a->destroy();
			  result_ = b;
			  return;
			}
		      // if b => a, then a W (b U c) = (a W c)
		      // if b => a, then a W (b W c) = (a W c)
		      if ((bo->op() == binop::U || bo->op() == binop::W)
			  && c_->implication(bo->first(), a))
			{
			  result_ = recurse_destroy
			    (binop::instance(binop::W,
					     a, bo->second()->clone()));
			  b->destroy();
			  return;
			}
		    }
		  // if a => b, then (a U c) W b = c W b
		  // if a => b, then (a W c) W b = c W b
		  // if c => b, then (a W c) W b = (a W c) | b
		  // if c => b, then (a U c) W b = (a U c) | b
		  if (const binop* bo = is_binop(a))
		    {
		      if ((bo->op() == binop::U || bo->op() == binop::W))
			{
			  if (c_->implication(bo->first(), b))
			    {
			      result_ = recurse_destroy
				(binop::instance(binop::W,
						 bo->second()->clone(),
						 b));
			      a->destroy();
			      return;
			    }
			  else if (c_->implication(bo->second(), b))
			    {
			      result_ = recurse_destroy
				(multop::instance(multop::Or, a, b));
			      return;
			    }
			}
		    }
		  break;

		case binop::M:
		  // if b => a, then a M b = b
		  if (c_->implication(b, a))
		    {
		      a->destroy();
		      result_ = b;
		      return;
		    }
		  // if b => !a, then a M b = 0
		  if (c_->implication_neg(b, a, true))
		    {
		      a->destroy();
		      b->destroy();
		      result_ = constant::false_instance();
		      return;
		    }
		  if (b->kind() == formula::BinOp)
		    {
		      // if b => a, then a M (b M c) = b M c
		      const binop* bo = static_cast<const binop*>(b);
		      if (bo->op() == binop::M
			  && c_->implication(bo->first(), a))
			{
			  result_ = b;
			  a->destroy();
			  return;
			}

		      // if a => b, then a M (b M c) = a M c
		      // if a => b, then a M (b R c) = a M c
		      if ((bo->op() == binop::M || bo->op() == binop::R)
			  && c_->implication(a, bo->first()))
			{
			  b->destroy();
			  result_ = recurse_destroy
			    (binop::instance(binop::M, a,
					     bo->second()->clone()));
			  return;
			}
		    }

		  // if b => a, then (a R c) M b = c M b
		  // if b => a, then (a M c) M b = c M b
		  // if c => b, then (a M c) M b = (a & c) M b
		  if (const binop* bo = is_binop(a))
		    {
		      if ((bo->op() == binop::M || bo->op() == binop::R)
			  && c_->implication(b, bo->first()))
			{
			  result_ = recurse_destroy
			    (binop::instance(binop::M,
					     bo->second()->clone(),
					     b));
			  a->destroy();
			  return;
			}
		      else if ((bo->op() == binop::M)
			       && c_->implication(bo->second(), b))
			{
			  const formula* ac =
			    multop::instance(multop::And,
					     bo->first()->clone(),
					     bo->second()->clone());
			  a->destroy();
			  result_ = recurse_destroy
			    (binop::instance(binop::M, ac, b));
			  return;
			}
		    }
		  break;
		}
	      trace << "bo: no inclusion-based rules matched" << std::endl;
	    }

	  if (!opt_.reduce_basics)
	    {
	      trace << "bo: basic reductions disabled" << std::endl;
	      result_ = binop::instance(op, a, b);
	      return;
	    }

	  trace << "bo: trying basic reductions" << std::endl;
	  // Rewrite U,R,W,M as F or G when possible.
	  switch (op)
	    {
	    case binop::U:
	      // true U b == F(b)
	      if (a == constant::true_instance())
		{
		  result_ = recurse_destroy(unop::instance(unop::F, b));
		  return;
		}
	      break;
	    case binop::R:
	      // false R b == G(b)
	      if (a == constant::false_instance())
		{
		  result_ = recurse_destroy(unop::instance(unop::G, b));
		  return;
		}
	      break;
	    case binop::W:
	      // a W false == G(a)
	      if (b == constant::false_instance())
		{
		  result_ = recurse_destroy(unop::instance(unop::G, a));
		  return;
		}
	      break;
	    case binop::M:
	      // a M true == F(a)
	      if (b == constant::true_instance())
		{
		  result_ = recurse_destroy(unop::instance(unop::F, a));
		  return;
		}
	      break;
	    default:
	      break;
	    }

	  switch (op)
	    {
	    case binop::W:
	    case binop::M:
	    case binop::U:
	    case binop::R:
	      {
		// These are trivial identities:
		// a U false = false
		// a U true = true
		// a R false = false
		// a R true = true
		// a W true = true
		// a M false = false
		if (is_constant(b))
		  {
		    result_ = b;
		    a->destroy();
		    return;
		  }

		const unop* fu1 = is_unop(a);
		const unop* fu2 = is_unop(b);

		// X(a) U X(b) = X(a U b)
		// X(a) R X(b) = X(a R b)
		// X(a) W X(b) = X(a W b)
		// X(a) M X(b) = X(a M b)
		if (fu1 && fu2
		    && fu1->op() == unop::X
		    && fu2->op() == unop::X)
		  {
		    const formula* bin =
		      binop::instance(op,
				      fu1->child()->clone(),
				      fu2->child()->clone());
		    a->destroy();
		    b->destroy();
		    result_ = recurse_destroy(unop::instance(unop::X, bin));
		    return;
		  }

		if (op == binop::U || op == binop::W)
		  {
		    // a U Ga = Ga
		    // a W Ga = Ga
		    if (fu2 && fu2->op() == unop::G && fu2->child() == a)
		      {
			a->destroy();
			result_ = b;
			return;
		      }

		    // a U (b | c | G(a)) = a W (b | c)
		    // a W (b | c | G(a)) = a W (b | c)
		    // a U (a & b & c) = (b & c) M a
		    // a W (a & b & c) = (b & c) R a
		    if (const multop* fm2 = is_multop(b))
		      {
			multop::type bt = fm2->op();
			// a U (b | c | G(a)) = a W (b | c)
			// a W (b | c | G(a)) = a W (b | c)
			if (bt == multop::Or)
			  {
			    int s = fm2->size();
			    for (int i = 0; i < s; ++i)
			      {
				const unop* c = is_G(fm2->nth(i));
				if (!c || c->child() != a)
				  continue;
				result_ =
				  recurse_destroy(binop::instance
						  (binop::W, a,
						   fm2->all_but(i)));
				b->destroy();
				return;
			      }
			  }
			// a U (b & a & c) == (b & c) M a
			// a W (b & a & c) == (b & c) R a
			if (bt == multop::And)
			  {
			    int s = fm2->size();
			    for (int i = 0; i < s; ++i)
			      {
				if (fm2->nth(i) != a)
				  continue;
				result_ = recurse_destroy(binop::instance
				  (op == binop::U ? binop::M : binop::R,
				   fm2->all_but(i), a));
				b->destroy();
				return;
			      }
			  }
		      }
		    // If b is Boolean:
		    // (Xc) U b = b | X(b M c)
		    // (Xc) W b = b | X(b R c)
		    if (!opt_.reduce_size_strictly
			&& fu1 && fu1->op() == unop::X && b->is_boolean())
		      {
			const formula* c = fu1->child()->clone();
			fu1->destroy();
			const formula* x =
			  unop::instance(unop::X,
					 binop::instance(op == binop::U
							 ? binop::M
							 : binop::R,
							 b->clone(), c));
			result_ =
			  recurse_destroy(multop::instance(multop::Or, b, x));
			return;
		      }
		  }
		else if (op == binop::M || op == binop::R)
		  {
		    // a R Fa = Fa
		    // a M Fa = Fa
		    if (fu2 && fu2->op() == unop::F && fu2->child() == a)
		      {
			a->destroy();
			result_ = b;
			return;
		      }

		    // a R (b & c & F(a)) = a M (b & c)
		    // a M (b & c & F(a)) = a M (b & c)
		    // a M (b | a | c) == (b | c) U a
		    // a R (b | a | c) == (b | c) W a
		    if (const multop* fm2 = is_multop(b))
		      {
			multop::type bt = fm2->op();

			// a R (b & c & F(a)) = a M (b & c)
			// a M (b & c & F(a)) = a M (b & c)
			if (bt == multop::And)
			  {
			    int s = fm2->size();
			    for (int i = 0; i < s; ++i)
			      {
				const unop* c = is_F(fm2->nth(i));
				if (!c || c->child() != a)
				  continue;
				result_ =
				  recurse_destroy(binop::instance
						  (binop::M, a,
						   fm2->all_but(i)));
				b->destroy();
				return;
			      }
			  }
			// a M (b | a | c) == (b | c) U a
			// a R (b | a | c) == (b | c) W a
			if (bt == multop::Or)
			  {
			    int s = fm2->size();
			    for (int i = 0; i < s; ++i)
			      {
				if (fm2->nth(i) != a)
				  continue;
				result_ = recurse_destroy(binop::instance
				  (op == binop::M ? binop::U : binop::W,
				   fm2->all_but(i), a));
				b->destroy();
				return;
			      }
			  }
		      }
		    // If b is Boolean:
		    // (Xc) R b = b & X(b W c)
		    // (Xc) M b = b & X(b U c)
		    if (!opt_.reduce_size_strictly
			&& fu1 && fu1->op() == unop::X && b->is_boolean())
		      {
			const formula* c = fu1->child()->clone();
			fu1->destroy();
			const formula* x =
			  unop::instance(unop::X,
					 binop::instance(op == binop::M
							 ? binop::U
							 : binop::W,
							 b->clone(), c));
			result_ =
			  recurse_destroy(multop::instance(multop::And, b, x));
			return;
		      }
		  }
	      }
	    case binop::UConcat:
	    case binop::EConcat:
	    case binop::EConcatMarked:
	      if (reduce_sere_ltl(op, a, b))
		return;
	      else
		break;
	    case binop::Xor:
	    case binop::Equiv:
	    case binop::Implies:
	      // No simplification... Yet?
	      break;
	    }

	  result_ = binop::instance(op, a, b);
	}

	void
	visit(const automatop* aut)
	{
	  result_ = aut->clone();
	}

	void
	visit(const multop* mo)
	{
	  unsigned mos = mo->size();
	  multop::vec* res = new multop::vec;

	  for (unsigned i = 0; i < mos; ++i)
	    res->push_back(recurse(mo->nth(i)));

	  multop::type op = mo->op();

	  if ((opt_.synt_impl | opt_.containment_checks)
	      && (op != multop::AndRat)
	      && (op != multop::AndNLM)
	      && (op != multop::OrRat)
	      && (op != multop::Concat)
	      && (op != multop::Fusion))
	    {
	      bool is_and = op == multop::And;
	      constant* neutral = is_and
		? constant::false_instance() : constant::true_instance();

	      result_ = multop::instance(op, res);
	      const multop* check = is_multop(result_, op);
	      if (!check)
		return;

	      unsigned s = check->size();
	      unsigned i = 0;
	      res = new multop::vec;
	      res->reserve(s);
	      while (i < s)
		{
		  const formula* fi = check->nth(i);
		  const formula* fo = check->all_but(i);
		  // if fi => fo, then fi | fo = fo
		  // if fo => fi, then fi & fo = fo
		  if ((op == multop::Or && c_->implication(fi, fo))
		      || (op == multop::And && c_->implication(fo, fi)))
		    {
		      check->destroy();
		      check = is_multop(fo, op);
		      if (!check)
			{
			  result_ = fo;
			  for (unsigned j = 0; j < i; ++j)
			    (*res)[j]->destroy();
			  delete res;
			  return;
			}
		      --s;
		    }
		  // if fi => !fo, then fi & fo = false
		  // if fo => !fi, then fi & fo = false
		  // if !fi => fo, then fi | fo = true
		  // if !fo => fi, then fi | fo = true
		  else if (c_->implication_neg(fi, fo, is_and)
			   || c_->implication_neg(fo, fi, is_and))
		    {
		      fo->destroy();
		      check->destroy();
		      result_ = neutral;
		      for (unsigned j = 0; j < i; ++j)
			(*res)[j]->destroy();
		      delete res;
		      return;
		    }
		  else
		    {
		      fo->destroy();
		      res->push_back(fi->clone());
		      ++i;
		    }
		}
	      check->destroy();
	    }

	  assert(!res->empty());

	  // basics reduction do not concern Boolean formulas,
	  // so don't waste time trying to apply them.
	  if (opt_.reduce_basics && !mo->is_boolean())
	    {
	      switch (op)
		{
		case multop::And:
		  assert(!mo->is_sere_formula());
		  {
		    // a & X(G(a&b...) & c...) = Ga & X(G(b...) & c...)
		    // a & (Xa W b) = b R a
		    // a & (Xa U b) = b M a
		    // a & (b | X(b R a)) = b R a
		    // a & (b | X(b M a)) = b M a
		    if (!mo->is_X_free())
		      {
			typedef Sgi::hash_set<const formula*,
					      ptr_hash<formula> > fset_t;
			typedef Sgi::hash_map<const formula*,
					      std::set<unsigned>,
					      ptr_hash<formula> > fmap_t;
			fset_t xgset; // XG(...)
			fset_t xset;  // X(...)
			fmap_t wuset; // (X...)W(...) or (X...)U(...)

			unsigned s = res->size();
			std::vector<bool> tokill(s);

			// Make a pass to search for subterms
			// of the form XGa or  X(... & G(...&a&...) & ...)
			for (unsigned n = 0; n < s; ++n)
			  {
			    if (!(*res)[n])
			      continue;
			    if ((*res)[n]->is_X_free())
			      continue;

			    const formula* xarg = is_XWU((*res)[n]);
			    if (xarg)
			      {
				wuset[xarg].insert(n);
				continue;
			      }

			    // Now we are looking for
			    // - X(...)
			    // - b | X(b R ...)
			    // - b | X(b M ...)

			    const binop* barg = is_bXbRM((*res)[n]);
			    if (barg)
			      {
				wuset[barg->second()].insert(n);
				continue;
			      }

			    const unop* uo = is_X((*res)[n]);
			    if (!uo)
			      continue;

			    const formula* c = uo->child();
			    const multop* a;
			    const unop* g;
			    if ((g = is_G(c)))
			      {
#define HANDLE_G                const multop* a2;			\
				if ((a2 = is_And(g->child())))		\
				  {					\
				    unsigned y = a2->size();		\
				    for (unsigned n = 0; n < y; ++n)	\
				      {					\
				        const formula* sub = a2->nth(n); \
					if (xgset.insert(sub).second)	\
					  sub->clone();			\
			              }					\
				  }					\
				else					\
				  {					\
				  const formula* sub = g->child();	\
				  if (xgset.insert(sub).second)		\
				    sub->clone();			\
				  }
				HANDLE_G;
			      }
			    else if ((a = is_And(c)))
			      {
				unsigned z = a->size();
				for (unsigned m = 0; m < z; ++m)
				  {
				    const formula* x = a->nth(m);
				    if ((g = is_G(x)))
				      {
					HANDLE_G;
				      }
				    else
				      {
					if (xset.insert(x).second)
					  x->clone();
				      }
				  }
			      }
			    else
			      {
				if (xset.insert(c).second)
				  c->clone();
			      }
			    (*res)[n]->destroy();
			    (*res)[n] = 0;
			  }
			// Make a second pass to check if the "a"
			// terms can be used to simplify "Xa W b",
			// "Xa U b", "b | X(b R a)", or "b | X(b M a)".
			for (unsigned n = 0; n < s; ++n)
			  {
			    if (!(*res)[n])
			      continue;
			    fmap_t::const_iterator gs =
			      wuset.find((*res)[n]);
			    if (gs == wuset.end())
			      continue;

			    const std::set<unsigned>& s = gs->second;
			    std::set<unsigned>::const_iterator g;
			    for (g = s.begin(); g != s.end(); ++g)
			      {
				unsigned pos = *g;
				const binop* wu = is_binop((*res)[pos]);
				if (wu)
				  {
				    // a & (Xa W b) = b R a
				    // a & (Xa U b) = b M a
				    binop::type t = (wu->op() == binop::U)
				      ? binop::M : binop::R;
				    const unop* xa =
				      down_cast<const unop*>(wu->first());
				    const formula* a = xa->child()->clone();
				    const formula* b = wu->second()->clone();
				    wu->destroy();
				    (*res)[pos] = binop::instance(t, b, a);
				  }
				else
				  {
				    // a & (b | X(b R a)) = b R a
				    // a & (b | X(b M a)) = b M a
				    wu = is_bXbRM((*res)[pos]);
				    assert(wu);
				    wu->clone();
				    (*res)[pos]->destroy();
				    (*res)[pos] = wu;
				  }
				// Remember to kill "a".
				tokill[n] = true;
			      }
			  }
			// Make third pass to search for terms 'a'
			// that also appears as 'XGa'.  Replace them
			// by 'Ga' and delete XGa.
			for (unsigned n = 0; n < s; ++n)
			  {
			    const formula* x = (*res)[n];
			    if (!x)
			      continue;
			    fset_t::const_iterator g = xgset.find(x);
			    if (g != xgset.end())
			      {
				// x can appear only once.
				const formula* gf = *g;
				xgset.erase(g);
				gf->destroy();
				(*res)[n] = unop::instance(unop::G, x);
			      }
			    else if (tokill[n])
			      {
				(*res)[n]->destroy();
				(*res)[n] = 0;
			      }
			  }

			multop::vec* xv = new multop::vec;
			size_t xgs = xgset.size();
			xv->reserve(xset.size() + 1);
			if (xgs > 0)
			  {
			    multop::vec* xgv = new multop::vec;
			    xgv->reserve(xgs);
			    fset_t::iterator i;
			    for (i = xgset.begin(); i != xgset.end(); ++i)
			      xgv->push_back(*i);
			    const formula* gv =
			      multop::instance(multop::And, xgv);
			    xv->push_back(unop::instance(unop::G, gv));
			  }
			fset_t::iterator j;
			for (j = xset.begin(); j != xset.end(); ++j)
			  xv->push_back(*j);
			const formula* av =
			  multop::instance(multop::And, xv);
			res->push_back(unop::instance(unop::X, av));
		      }

		    // Gather all operands by type.
		    mospliter s(mospliter::Strip_X |
				mospliter::Strip_FG |
				mospliter::Strip_G |
				mospliter::Split_F |
				mospliter::Split_U_or_W |
				mospliter::Split_R_or_M |
				mospliter::Split_EventUniv,
				res, c_);

		    // FG(a) & FG(b) = FG(a & b)
		    const formula* allFG =
		      unop_unop_multop(unop::F, unop::G, multop::And,
				       s.res_FG);

		    // Xa & Xb = X(a & b)
		    // Xa & Xb & FG(c) = X(a & b & FG(c))
		    // For Universal&Eventual formulae f1...fn we also have:
		    // Xa & Xb & f1...fn = X(a & b & f1...fn)
		    if (!s.res_X->empty() && !opt_.favor_event_univ)
		      {
			s.res_X->push_back(allFG);
			allFG = 0;
			s.res_X->insert(s.res_X->begin(),
					s.res_EventUniv->begin(),
					s.res_EventUniv->end());
		      }
		    else
		      // If f1...fn are event&univ formulae, with at least
		      // one formula of the form G(...),
		      // Rewrite  g & f1...fn  as  g & G(f1..fn) while
		      // stripping any leading G from f1...fn.
		      // This gathers eventual&universal formulae
		      // under the same term.
		      {
			multop::vec* eu = new multop::vec;
			bool seen_g = false;
			for (multop::vec::const_iterator
			       i = s.res_EventUniv->begin();
			     i != s.res_EventUniv->end(); ++i)
			  {
			    if ((*i)->is_eventual() && (*i)->is_universal())
			      {
				if (const unop* g = is_G(*i))
				  {
				    seen_g = true;
				    eu->push_back(g->child()->clone());
				    g->destroy();
				  }
				else
				  {
				    eu->push_back(*i);
				  }
			      }
			    else
			      s.res_other->push_back(*i);
			  }
			if (seen_g)
			  {
			    eu->push_back(allFG);
			    allFG = 0;
			    s.res_other->push_back(unop_multop(unop::G,
							       multop::And,
							       eu));
			  }
			else
			  {
			    s.res_other->insert(s.res_other->end(),
						eu->begin(), eu->end());
			    delete eu;
			  }
		      }
		    delete s.res_EventUniv;

		    // Xa & Xb & f1...fn = X(a & b & f1...fn)
		    // is built at the end of this multop::And case.
		    // G(a) & G(b) = G(a & b)
		    // is built at the end of this multop::And case.

		    // The following three loops perform these rewritings:
		    // (a U b) & (c U b) = (a & c) U b
		    // (a U b) & (c W b) = (a & c) U b
		    // (a W b) & (c W b) = (a & c) W b
		    // (a R b) & (a R c) = a R (b & c)
		    // (a R b) & (a M c) = a M (b & c)
		    // (a M b) & (a M c) = a M (b & c)
		    // F(a) & (a R b) = a M b
		    // F(a) & (a M b) = a M b
		    // F(b) & (a W b) = a U b
		    // F(b) & (a U b) = a U b
		    typedef Sgi::hash_map<const formula*,
					  multop::vec::iterator,
					  ptr_hash<formula> > fmap_t;
		    fmap_t uwmap; // associates "b" to "a U b" or "a W b"
		    fmap_t rmmap; // associates "a" to "a R b" or "a M b"
		    // (a U b) & (c U b) = (a & c) U b
		    // (a U b) & (c W b) = (a & c) U b
		    // (a W b) & (c W b) = (a & c) W b
		    for (multop::vec::iterator i = s.res_U_or_W->begin();
			 i != s.res_U_or_W->end(); ++i)
		      {
			const binop* bo = static_cast<const binop*>(*i);
			const formula* b = bo->second();
			fmap_t::iterator j = uwmap.find(b);
			if (j == uwmap.end())
			  {
			    // First occurrence.
			    uwmap[b] = i;
			    continue;
			  }
			// We already have one occurrence.  Merge them.
			const binop* old =
			  static_cast<const binop*>(*j->second);
			binop::type op = binop::W;
			if (bo->op() == binop::U
			    || old->op() == binop::U)
			  op = binop::U;
			const formula* fst_arg =
			  multop::instance(multop::And,
					   old->first()->clone(),
					   bo->first()->clone());
			*j->second = binop::instance(op, fst_arg, b->clone());
			assert((*j->second)->kind() == formula::BinOp);
			*i = 0;
			old->destroy();
			bo->destroy();
		      }
		    // (a R b) & (a R c) = a R (b & c)
		    // (a R b) & (a M c) = a M (b & c)
		    // (a M b) & (a M c) = a M (b & c)
		    for (multop::vec::iterator i = s.res_R_or_M->begin();
			 i != s.res_R_or_M->end(); ++i)
		      {
			const binop* bo = static_cast<const binop*>(*i);
			const formula* a = bo->first();
			fmap_t::iterator j = rmmap.find(a);
			if (j == rmmap.end())
			  {
			    // First occurrence.
			    rmmap[a] = i;
			    continue;
			  }
			// We already have one occurrence.  Merge them.
			const binop* old =
			  static_cast<const binop*>(*j->second);
			binop::type op = binop::R;
			if (bo->op() == binop::M
			    || old->op() == binop::M)
			  op = binop::M;
			const formula* snd_arg =
			  multop::instance(multop::And,
					   old->second()->clone(),
					   bo->second()->clone());
			*j->second = binop::instance(op, a->clone(), snd_arg);
			assert((*j->second)->kind() == formula::BinOp);
			*i = 0;
			old->destroy();
			bo->destroy();
		      }
		    // F(a) & (a R b) = a M b
		    // F(a) & (a M b) = a M b
		    // F(b) & (a W b) = a U b
		    // F(b) & (a U b) = a U b
		    for (multop::vec::iterator i = s.res_F->begin();
			 i != s.res_F->end(); ++i)
		      {
			bool superfluous = false;
			const unop* uo = static_cast<const unop*>(*i);
			const formula* c = uo->child();

			fmap_t::iterator j = uwmap.find(c);
			if (j != uwmap.end())
			  {
			    superfluous = true;
			    const binop* bo =
			      static_cast<const binop*>(*j->second);
			    if (bo->op() == binop::W)
			      {
				*j->second =
				  binop::instance(binop::U,
						  bo->first()->clone(),
						  bo->second()->clone());
				assert((*j->second)->kind()
				       == formula::BinOp);
				bo->destroy();
			      }
			  }
			j = rmmap.find(c);
			if (j != rmmap.end())
			  {
			    superfluous = true;
			    const binop* bo =
			      static_cast<const binop*>(*j->second);
			    if (bo->op() == binop::R)
			      {
				*j->second =
				  binop::instance(binop::M,
						  bo->first()->clone(),
						  bo->second()->clone());
				assert((*j->second)->kind()
				       == formula::BinOp);
				bo->destroy();
			      }
			  }
			if (superfluous)
			  {
			    (*i)->destroy();
			    *i = 0;
			  }
		      }

		    s.res_other->reserve(s.res_other->size()
					 + s.res_F->size()
					 + s.res_U_or_W->size()
					 + s.res_R_or_M->size()
					 + 3);
		    s.res_other->insert(s.res_other->end(),
					s.res_F->begin(),
					s.res_F->end());
		    delete s.res_F;
		    s.res_other->insert(s.res_other->end(),
					s.res_U_or_W->begin(),
					s.res_U_or_W->end());
		    delete s.res_U_or_W;
		    s.res_other->insert(s.res_other->end(),
					s.res_R_or_M->begin(),
					s.res_R_or_M->end());
		    delete s.res_R_or_M;

		    // Those "G" formulae that are eventual can be
		    // postponed inside the X term if there is one.
		    //
		    // In effect we rewrite
		    //   Xa&Xb&GFc&GFd&Ge as X(a&b&G(Fc&Fd))&Ge
		    if (!s.res_X->empty() && !opt_.favor_event_univ)
		      {
			multop::vec* event = new multop::vec;
			for (multop::vec::iterator i = s.res_G->begin();
			     i != s.res_G->end(); ++i)
			  if ((*i)->is_eventual())
			    {
			      event->push_back(*i);
			      *i = 0; // Remove it from res_G.
			    }
			s.res_X->push_back(unop_multop(unop::G,
						       multop::And, event));
		      }

		    // G(a) & G(b) & ... = G(a & b & ...)
		    const formula* allG =
		      unop_multop(unop::G, multop::And, s.res_G);
		    // Xa & Xb & ... = X(a & b & ...)
		    const formula* allX =
		      unop_multop(unop::X, multop::And, s.res_X);

		    s.res_other->push_back(allX);
		    s.res_other->push_back(allG);
		    s.res_other->push_back(allFG);
		    result_ = multop::instance(multop::And, s.res_other);
		    // If we altered the formula in some way, process
		    // it another time.
		    if (result_ != mo)
		      result_ = recurse_destroy(result_);
		    return;
		  }
		  break;
		case multop::AndRat:
		  {
		    mospliter s(mospliter::Split_Bool, res, c_);
		    if (!s.res_Bool->empty())
		      {
			// b1 & b2 & b3 = b1 ∧ b2 ∧ b3
			const formula* b =
			  multop::instance(multop::And, s.res_Bool);

			multop::vec* ares = new multop::vec;
			for (multop::vec::iterator i = s.res_other->begin();
			     i != s.res_other->end(); ++i)
			  switch ((*i)->kind())
			    {
			    case formula::BUnOp:
			      {
				const bunop* r = down_cast<const bunop*>(*i);
				// b && r[*i..j] = b & r  if i<=1<=j
				//               = 0      otherwise
				switch (r->op())
				  {
				  case bunop::Star:
				    if (r->min() > 1 || r->max() < 1)
				      goto returnfalse;
				    ares->push_back(r->child()->clone());
				    r->destroy();
				    *i = 0;
				    break;
				  }
				break;
			      }
			    case formula::MultOp:
			      {
				const multop* r = down_cast<const multop*>(*i);
				unsigned rs = r->size();
				switch (r->op())
				  {
				  case multop::Fusion:
				    //b && {r1:..:rn} = b && r1 && .. && rn
				    for (unsigned j = 0; j < rs; ++j)
				      ares->push_back(r->nth(j)->clone());
				    r->destroy();
				    *i = 0;
				    break;
				  case multop::Concat:
				    // b && {r1;...;rn} =
				    // - b && ri if there is only one ri
				    //           that does not accept [*0]
				    // - b && (r1|...|rn) if all ri
				    //           do not accept [*0]
				    // - 0 if more than one ri accept [*0]
				    {
				      const formula* ri = 0;
				      unsigned nonempty = 0;
				      for (unsigned j = 0; j < rs; ++j)
					{
					  const formula* jf = r->nth(j);
					  if (!jf->accepts_eword())
					    {
					      ri = jf;
					      ++nonempty;
					    }
					}
				      if (nonempty == 1)
					{
					  ares->push_back(ri->clone());
					}
				      else if (nonempty == 0)
					{
					  multop::vec* sum = new multop::vec;
					  for (unsigned j = 0; j < rs; ++j)
					    sum->push_back(r->nth(j)
							   ->clone());
					  const formula* sumf =
					    multop::instance(multop::OrRat,
							     sum);
					  ares->push_back(sumf);
					}
				      else
					{
					  goto returnfalse;
					}
				      r->destroy();
				      *i = 0;
				      break;
				    }
				  default:
				    goto common;
				  }
				break;
			      }
			    default:
			    common:
			      ares->push_back(*i);
			      *i = 0;
			      break;
			    }
			delete s.res_other;
			ares->push_back(b);
			result_ = multop::instance(multop::AndRat, ares);
			// If we altered the formula in some way, process
			// it another time.
			if (result_ != mo)
			  result_ = recurse_destroy(result_);
			return;
		      returnfalse:
			b->destroy();
			for (multop::vec::iterator i = s.res_other->begin();
			     i != s.res_other->end(); ++i)
			  if (*i)
			    (*i)->destroy();
			delete s.res_other;
			for (multop::vec::iterator i = ares->begin();
			     i != ares->end(); ++i)
			  (*i)->destroy();
			delete ares;
			result_ = constant::false_instance();
			return;
		      }
		    else
		      {
			// No Boolean as argument of &&.
			delete s.res_Bool;

			// Look for occurrences of {b;r} or {b:r}.  We have
			// {b1;r1}&&{b2;r2} = {b1&&b2};{r1&&r2}
			//                     head1    tail1
			// {b1:r1}&&{b2:r2} = {b1&&b2}:{r1&&r2}
			//                     head2    tail2

			multop::vec* head1 = new multop::vec;
			multop::vec* tail1 = new multop::vec;
			multop::vec* head2 = new multop::vec;
			multop::vec* tail2 = new multop::vec;
			for (multop::vec::iterator i = s.res_other->begin();
			     i != s.res_other->end(); ++i)
			  {
			    if (!*i)
			      continue;
			    if ((*i)->kind() != formula::MultOp)
			      continue;
			    const multop* f = down_cast<const multop*>(*i);
			    const formula* h = f->nth(0);
			    if (!h->is_boolean())
			      continue;
			    multop::type op = f->op();
			    if (op == multop::Concat)
			      {
				head1->push_back(h->clone());
				tail1->push_back(f->all_but(0));
				(*i)->destroy();
				*i = 0;
			      }
			    else if (op == multop::Fusion)
			      {
				head2->push_back(h->clone());
				tail2->push_back(f->all_but(0));
				(*i)->destroy();
				*i = 0;
			      }
			    else
			      {
				continue;
			      }
			  }
			if (!head1->empty())
			  {
			    const formula* h =
			      multop::instance(multop::And, head1);
			    const formula* t =
			      multop::instance(multop::AndRat, tail1);
			    const formula* f =
			      multop::instance(multop::Concat, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head1;
			    delete tail1;
			  }
			if (!head2->empty())
			  {
			    const formula* h =
			      multop::instance(multop::And, head2);
			    const formula* t =
			      multop::instance(multop::AndRat, tail2);
			    const formula* f =
			      multop::instance(multop::Fusion, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head2;
			    delete tail2;
			  }

			// {r1;b1}&&{r2;b2} = {r1&&r2};{b1∧b2}
			//                     head3    tail3
			// {r1:b1}&&{r2:b2} = {r1&&r2}:{b1∧b2}
			//                     head4    tail4
			multop::vec* head3 = new multop::vec;
			multop::vec* tail3 = new multop::vec;
			multop::vec* head4 = new multop::vec;
			multop::vec* tail4 = new multop::vec;
			for (multop::vec::iterator i = s.res_other->begin();
			     i != s.res_other->end(); ++i)
			  {
			    if (!*i)
			      continue;
			    if ((*i)->kind() != formula::MultOp)
			      continue;
			    const multop* f = down_cast<const multop*>(*i);
			    unsigned s = f->size() - 1;
			    const formula* t = f->nth(s);
			    if (!t->is_boolean())
			      continue;
			    multop::type op = f->op();
			    if (op == multop::Concat)
			      {
				tail3->push_back(t->clone());
				head3->push_back(f->all_but(s));
				(*i)->destroy();
				*i = 0;
			      }
			    else if (op == multop::Fusion)
			      {
				tail4->push_back(t->clone());
				head4->push_back(f->all_but(s));
				(*i)->destroy();
				*i = 0;
			      }
			    else
			      {
				continue;
			      }
			  }
			if (!head3->empty())
			  {
			    const formula* h =
			      multop::instance(multop::AndRat, head3);
			    const formula* t =
			      multop::instance(multop::And, tail3);
			    const formula* f =
			      multop::instance(multop::Concat, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head3;
			    delete tail3;
			  }
			if (!head4->empty())
			  {
			    const formula* h =
			      multop::instance(multop::AndRat, head4);
			    const formula* t =
			      multop::instance(multop::And, tail4);
			    const formula* f =
			      multop::instance(multop::Fusion, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head4;
			    delete tail4;
			  }

			result_ =
			  multop::instance(multop::AndRat, s.res_other);
			// If we altered the formula in some way, process
			// it another time.
			if (result_ != mo)
			  result_ = recurse_destroy(result_);
			return;
		      }
		  }
		case multop::Or:
		  {
		    // a | X(F(a) | c...) = Fa | X(c...)
		    // a | (Xa R b) = b W a
		    // a | (Xa M b) = b U a
		    // a | (b & X(b W a)) = b W a
		    // a | (b & X(b U a)) = b U a
		    if (!mo->is_X_free())
		      {
			typedef Sgi::hash_set<const formula*,
					      ptr_hash<formula> > fset_t;
			typedef Sgi::hash_map<const formula*,
					      std::set<unsigned>,
					      ptr_hash<formula> > fmap_t;
			fset_t xfset; // XF(...)
			fset_t xset;  // X(...)
			fmap_t rmset; // (X...)R(...) or (X...)M(...) or
				      // b & X(b W ...) or b & X(b U ...)

			unsigned s = res->size();
			std::vector<bool> tokill(s);

			// Make a pass to search for subterms
			// of the form XFa or  X(... | F(...|a|...) | ...)
			for (unsigned n = 0; n < s; ++n)
			  {
			    if (!(*res)[n])
			      continue;
			    if ((*res)[n]->is_X_free())
			      continue;

			    const formula* xarg = is_XRM((*res)[n]);
			    if (xarg)
			      {
				rmset[xarg].insert(n);
				continue;
			      }

			    // Now we are looking for
			    // - X(...)
			    // - b & X(b W ...)
			    // - b & X(b U ...)

			    const binop* barg = is_bXbWU((*res)[n]);
			    if (barg)
			      {
				rmset[barg->second()].insert(n);
				continue;
			      }

			    const unop* uo = is_X((*res)[n]);
			    if (!uo)
			      continue;

			    const formula* c = uo->child();
			    const multop* o;
			    const unop* f;
			    if ((f = is_F(c)))
			      {
#define HANDLE_F                const multop* o2;			\
				if ((o2 = is_Or(f->child())))		\
				  {					\
				    unsigned y = o2->size();		\
				    for (unsigned n = 0; n < y; ++n)	\
				      {					\
				        const formula* sub = o2->nth(n); \
					if (xfset.insert(sub).second)	\
					  sub->clone();			\
				      }					\
				    }					\
				else					\
				  {					\
				    const formula* sub = f->child();	\
				    if (xfset.insert(sub).second)	\
				      sub->clone();			\
				  }
				HANDLE_F;
			      }
			    else if ((o = is_Or(c)))
			      {
				unsigned z = o->size();
				for (unsigned m = 0; m < z; ++m)
				  {
				    const formula* x = o->nth(m);
				    if ((f = is_F(x)))
				      {
					HANDLE_F;
				      }
				    else
				      {
					if (xset.insert(x).second)
					  x->clone();
				      }
				  }
			      }
			    else
			      {
				if (xset.insert(c).second)
				  c->clone();
			      }
			    (*res)[n]->destroy();
			    (*res)[n] = 0;
			  }
			// Make a second pass to check if we can
			// remove all instance of XF(a).
			unsigned allofthem = xfset.size();
			for (unsigned n = 0; n < s; ++n)
			  {
			    const formula* x = (*res)[n];
			    if (!x)
			      continue;
			    fset_t::const_iterator f = xfset.find(x);
			    if (f != xfset.end())
			      --allofthem;
			    assert(allofthem != -1U);
			    // At the same time, check if "a" can also
			    // be used to simplify "Xa R b", "Xa M b".
			    // "b & X(b W a)", or "b & X(b U a)".
			    fmap_t::const_iterator gs = rmset.find(x);
			    if (gs == rmset.end())
			      continue;
			    const std::set<unsigned>& s = gs->second;
			    std::set<unsigned>::const_iterator g;
			    for (g = s.begin(); g != s.end(); ++g)
			      {
				unsigned pos = *g;
				const binop* rm = is_binop((*res)[pos]);
				if (rm)
				  {
				    // a | (Xa R b) = b W a
				    // a | (Xa M b) = b U a
				    binop::type t = (rm->op() == binop::M)
				      ? binop::U : binop::W;
				    const unop* xa =
				      down_cast<const unop*>(rm->first());
				    const formula* a = xa->child()->clone();
				    const formula* b = rm->second()->clone();
				    rm->destroy();
				    (*res)[pos] = binop::instance(t, b, a);
				  }
				else
				  {
				    // a | (b & X(b W a)) = b W a
				    // a | (b & X(b U a)) = b U a
				    rm = is_bXbWU((*res)[pos]);
				    assert(rm);
				    rm->clone();
				    (*res)[pos]->destroy();
				    (*res)[pos] = rm;
				  }
				// Remember to kill "a".
				tokill[n] = true;
			      }
			  }
			// If we can remove all of them...
			if (allofthem == 0)
			  // Make third pass to search for terms 'a'
			  // that also appears as 'XFa'.  Replace them
			  // by 'Fa' and delete XFa.
			  for (unsigned n = 0; n < s; ++n)
			    {
			      const formula* x = (*res)[n];
			      if (!x)
				continue;
			      fset_t::const_iterator f = xfset.find(x);
			      if (f != xfset.end())
				{
				  // x can appear only once.
				  const formula* ff = *f;
				  xfset.erase(f);
				  ff->destroy();
				  (*res)[n] = unop::instance(unop::F, x);
				  // We don't need to kill "a" anymore.
				  tokill[n] = false;
				}
			    }
			// Kill any remaining "a", used to simplify Xa R b
			// or Xa M b.
			for (unsigned n = 0; n < s; ++n)
			  if (tokill[n] && (*res)[n])
			    {
			      (*res)[n]->destroy();
			      (*res)[n] = 0;
			    }

			// Now rebuild the formula that remains.
			multop::vec* xv = new multop::vec;
			size_t xfs = xfset.size();
			xv->reserve(xset.size() + 1);
			if (xfs > 0)
			  {
			    // Group all XF(a)|XF(b|c|...)|... as XF(a|b|c|...)
			    multop::vec* xfv = new multop::vec;
			    xfv->reserve(xfs);
			    fset_t::iterator i;
			    for (i = xfset.begin(); i != xfset.end(); ++i)
			      xfv->push_back(*i);
			    const formula* fv =
			      multop::instance(multop::Or, xfv);
			    xv->push_back(unop::instance(unop::F, fv));
			  }
			// Also gather the remaining Xa | X(b|c) as X(b|c).
			fset_t::iterator j;
			for (j = xset.begin(); j != xset.end(); ++j)
			  xv->push_back(*j);
			const formula* ov = multop::instance(multop::Or, xv);
			res->push_back(unop::instance(unop::X, ov));
		      }

		    // Gather all operand by type.
		    mospliter s(mospliter::Strip_X |
				mospliter::Strip_GF |
				mospliter::Strip_F |
				mospliter::Split_G |
				mospliter::Split_U_or_W |
				mospliter::Split_R_or_M |
				mospliter::Split_EventUniv,
				res, c_);
		    // GF(a) | GF(b) = GF(a | b)
		    const formula* allGF =
		      unop_unop_multop(unop::G, unop::F, multop::Or, s.res_GF);
		    // Xa | Xb = X(a | b)
		    // Xa | Xb | GF(c) = X(a | b | GF(c))
		    // For Universal&Eventual formula f1...fn we also have:
		    // Xa | Xb | f1...fn = X(a | b | f1...fn)
		    if (!s.res_X->empty() && !opt_.favor_event_univ)
		      {
			s.res_X->push_back(allGF);
			allGF = 0;
			s.res_X->insert(s.res_X->end(),
					s.res_EventUniv->begin(),
					s.res_EventUniv->end());
		      }
		    else if (!opt_.favor_event_univ
			     && !s.res_F->empty()
			     && s.res_G->empty()
			     && s.res_U_or_W->empty()
			     && s.res_R_or_M->empty()
			     && s.res_other->empty())
		      {
			// If there is no X but some F and only
			// eventual&universal formulae f1...fn|GF(c), do:
			// Fa|Fb|f1...fn|GF(c) = F(a|b|f1...fn|GF(c))
			//
			// The reasoning here is that if we should
			// move f1...fn|GF(c) inside the "F" only
			// if it allows us to move all terms under F,
			// allowing a nice initial self-loop.
			//
			// For instance:
			//   F(a|GFb)  3st.6tr. with initial self-loop
			//   Fa|GFb    4st.8tr. without initial self-loop
			//
			// However, if other terms are presents they will
			// prevent the formation of a self-loop, and the
			// rewriting is unwelcome:
			//   F(a|GFb)|Gc  5st.11tr.  without initial self-loop
			//   Fa|GFb|Gc    5st.10tr.  without initial self-loop
			// (counting the number of "subtransitions"
			// or, degeneralizing the automaton amplifies
			// these differences)
			s.res_F->push_back(allGF);
			allGF = 0;
			s.res_F->insert(s.res_F->end(),
					s.res_EventUniv->begin(),
					s.res_EventUniv->end());
		      }
		    else if (opt_.favor_event_univ)
		      {
			s.res_EventUniv->push_back(allGF);
			allGF = 0;
			bool seen_f = false;
			if (s.res_EventUniv->size() > 1)
			  {
			    // If some of the EventUniv formulae start
			    // with an F, Gather them all under the
			    // same F.  Striping any leading F.
			    for (multop::vec::iterator i =
				   s.res_EventUniv->begin();
				 i != s.res_EventUniv->end(); ++i)
			      if (const unop* u = is_F(*i))
				{
				  *i = u->child()->clone();
				  u->destroy();
				  seen_f = true;
				}
			    if (seen_f)
			      {
				const formula* eu =
				  unop_multop(unop::F, multop::Or,
					      s.res_EventUniv);
				s.res_EventUniv = 0;
				s.res_other->push_back(eu);
			      }
			  }
			if (!seen_f)
			  {
			    s.res_other->insert(s.res_other->end(),
						s.res_EventUniv->begin(),
						s.res_EventUniv->end());
			  }
		      }
		    else
		      {
			s.res_other->insert(s.res_other->end(),
					    s.res_EventUniv->begin(),
					    s.res_EventUniv->end());
		      }
		    delete s.res_EventUniv;
		    // Xa | Xb | f1...fn = X(a | b | f1...fn)
		    // is built at the end of this multop::Or case.
		    // F(a) | F(b) = F(a | b)
		    // is built at the end of this multop::Or case.

		    // The following three loops perform these rewritings:
		    // (a U b) | (a U c) = a U (b | c)
		    // (a W b) | (a U c) = a W (b | c)
		    // (a W b) | (a W c) = a W (b | c)
		    // (a R b) | (c R b) = (a | c) R b
		    // (a R b) | (c M b) = (a | c) R b
		    // (a M b) | (c M b) = (a | c) M b
		    // G(a) | (a U b) = a W b
		    // G(a) | (a W b) = a W b
		    // G(b) | (a R b) = a R b.
		    // G(b) | (a M b) = a R b.
		    typedef Sgi::hash_map<const formula*,
					  multop::vec::iterator,
					  ptr_hash<formula> > fmap_t;
		    fmap_t uwmap; // associates "a" to "a U b" or "a W b"
		    fmap_t rmmap; // associates "b" to "a R b" or "a M b"
		    // (a U b) | (a U c) = a U (b | c)
		    // (a W b) | (a U c) = a W (b | c)
		    // (a W b) | (a W c) = a W (b | c)
		    for (multop::vec::iterator i = s.res_U_or_W->begin();
			 i != s.res_U_or_W->end(); ++i)
		      {
			const binop* bo = static_cast<const binop*>(*i);
			const formula* a = bo->first();
			fmap_t::iterator j = uwmap.find(a);
			if (j == uwmap.end())
			  {
			    // First occurrence.
			    uwmap[a] = i;
			    continue;
			  }
			// We already have one occurrence.  Merge them.
			const binop* old =
			  static_cast<const binop*>(*j->second);
			binop::type op = binop::U;
			if (bo->op() == binop::W
			    || old->op() == binop::W)
			  op = binop::W;
			const formula* snd_arg =
			  multop::instance(multop::Or,
					   old->second()->clone(),
					   bo->second()->clone());
			*j->second = binop::instance(op, a->clone(), snd_arg);
			assert((*j->second)->kind() == formula::BinOp);
			*i = 0;
			old->destroy();
			bo->destroy();
		      }
		    // (a R b) | (c R b) = (a | c) R b
		    // (a R b) | (c M b) = (a | c) R b
		    // (a M b) | (c M b) = (a | c) M b
		    for (multop::vec::iterator i = s.res_R_or_M->begin();
			 i != s.res_R_or_M->end(); ++i)
		      {
			const binop* bo = static_cast<const binop*>(*i);
			const formula* b = bo->second();
			fmap_t::iterator j = rmmap.find(b);
			if (j == rmmap.end())
			  {
			    // First occurrence.
			    rmmap[b] = i;
			    continue;
			  }
			// We already have one occurrence.  Merge them.
			const binop* old =
			  static_cast<const binop*>(*j->second);
			binop::type op = binop::M;
			if (bo->op() == binop::R
			    || old->op() == binop::R)
			  op = binop::R;
			const formula* fst_arg =
			  multop::instance(multop::Or,
					   old->first()->clone(),
					   bo->first()->clone());
			*j->second = binop::instance(op, fst_arg, b->clone());
			assert((*j->second)->kind() == formula::BinOp);
			*i = 0;
			old->destroy();
			bo->destroy();
		      }
		    // G(a) | (a U b) = a W b
		    // G(a) | (a W b) = a W b
		    // G(b) | (a R b) = a R b.
		    // G(b) | (a M b) = a R b.
		    for (multop::vec::iterator i = s.res_G->begin();
			 i != s.res_G->end(); ++i)
		      {
			bool superfluous = false;
			const unop* uo = static_cast<const unop*>(*i);
			const formula* c = uo->child();

			fmap_t::iterator j = uwmap.find(c);
			if (j != uwmap.end())
			  {
			    superfluous = true;
			    const binop* bo =
			      static_cast<const binop*>(*j->second);
			    if (bo->op() == binop::U)
			      {
				*j->second =
				  binop::instance(binop::W,
						  bo->first()->clone(),
						  bo->second()->clone());
				assert((*j->second)->kind() == formula::BinOp);
				bo->destroy();
			      }
			  }
			j = rmmap.find(c);
			if (j != rmmap.end())
			  {
			    superfluous = true;
			    const binop* bo =
			      static_cast<const binop*>(*j->second);
			    if (bo->op() == binop::M)
			      {
				*j->second =
				  binop::instance(binop::R,
						  bo->first()->clone(),
						  bo->second()->clone());
				assert((*j->second)->kind() == formula::BinOp);
				bo->destroy();
			      }
			  }
			if (superfluous)
			  {
			    (*i)->destroy();
			    *i = 0;
			  }
		      }

		    s.res_other->reserve(s.res_other->size()
					 + s.res_G->size()
					 + s.res_U_or_W->size()
					 + s.res_R_or_M->size()
					 + 3);
		    s.res_other->insert(s.res_other->end(),
					s.res_G->begin(),
					s.res_G->end());
		    delete s.res_G;
		    s.res_other->insert(s.res_other->end(),
					s.res_U_or_W->begin(),
					s.res_U_or_W->end());
		    delete s.res_U_or_W;
		    s.res_other->insert(s.res_other->end(),
					s.res_R_or_M->begin(),
					s.res_R_or_M->end());
		    delete s.res_R_or_M;

		    // Those "F" formulae that are universal can be
		    // postponed inside the X term if there is one.
		    //
		    // In effect we rewrite
		    //   Xa|Xb|FGc|FGd|Fe as X(a|b|F(Gc|Gd))|Fe
		    if (!s.res_X->empty())
		      {
			multop::vec* univ = new multop::vec;
			for (multop::vec::iterator i = s.res_F->begin();
			     i != s.res_F->end(); ++i)
			  if ((*i)->is_universal())
			    {
			      univ->push_back(*i);
			      *i = 0; // Remove it from res_F.
			    }
			s.res_X->push_back(unop_multop(unop::F,
						       multop::Or, univ));
		      }

		    // F(a) | F(b) | ... = F(a | b | ...)
		    const formula* allF =
		      unop_multop(unop::F, multop::Or, s.res_F);
		    // Xa | Xb | ... = X(a | b | ...)
		    const formula* allX =
		      unop_multop(unop::X, multop::Or, s.res_X);

		    s.res_other->push_back(allX);
		    s.res_other->push_back(allF);
		    s.res_other->push_back(allGF);
		    result_ = multop::instance(multop::Or, s.res_other);
		    // If we altered the formula in some way, process
		    // it another time.
		    if (result_ != mo)
		      result_ = recurse_destroy(result_);
		    return;
		  }
		case multop::OrRat:
		  // FIXME: No simplifications yet.
		  break;
		case multop::AndNLM:
		  {
		    mospliter s(mospliter::Split_Bool, res, c_);
		    if (!s.res_Bool->empty())
		      {
			// b1 & b2 & b3 = b1 ∧ b2 ∧ b3
			const formula* b =
			  multop::instance(multop::And, s.res_Bool);

			// now we just consider  b & rest
			const formula* rest =
			  multop::instance(multop::AndNLM,
					   s.res_other);

			// We have  b & rest = b : rest  if rest does not
			// accept [*0]. Otherwise  b & rest = b | (b : rest)
			// FIXME: It would be nice to remove [*0] from rest.
			if (rest->accepts_eword())
			  {
			    // The b & rest = b | (b : rest) rewriting
			    // augment the size, so do that only when
			    // explicitly requested.
			    if (!opt_.reduce_size_strictly)
			      {
				const formula* brest =
				  multop::instance(multop::Fusion, b->clone(),
						   rest);
				result_ =
				  multop::instance(multop::OrRat, b, brest);
			      }
			    else
			      {
				result_ = multop::instance(multop::AndNLM,
							   b, rest);
			      }
			  }
			else
			  {
			    result_ = multop::instance(multop::Fusion,
						       b, rest);
			  }
			// If we altered the formula in some way, process
			// it another time.
			if (result_ != mo)
			  result_ = recurse_destroy(result_);
			return;
		      }
		    else
		      {
			// No Boolean as argument of &&.
			delete s.res_Bool;

			// Look for occurrences of {b;r} or {b:r}.  We have
			// {b1;r1}&{b2;r2} = {b1∧b2};{r1&r2}
			//                    head1   tail1
			// {b1:r1}&{b2:r2} = {b1∧b2}:{r1&r2}
			//                    head2   tail2
			// BEWARE: The second rule is correct only when
			// both r1 and r2 do not accept [*0].

			multop::vec* head1 = new multop::vec;
			multop::vec* tail1 = new multop::vec;
			multop::vec* head2 = new multop::vec;
			multop::vec* tail2 = new multop::vec;
			for (multop::vec::iterator i = s.res_other->begin();
			     i != s.res_other->end(); ++i)
			  {
			    if (!*i)
			      continue;
			    if ((*i)->kind() != formula::MultOp)
			      continue;
			    const multop* f = down_cast<const multop*>(*i);
			    const formula* h = f->nth(0);
			    if (!h->is_boolean())
			      continue;
			    multop::type op = f->op();
			    if (op == multop::Concat)
			      {
				head1->push_back(h->clone());
				tail1->push_back(f->all_but(0));
				(*i)->destroy();
				*i = 0;
			      }
			    else if (op == multop::Fusion)
			      {
				const formula* t = f->all_but(0);
				if (t->accepts_eword())
				  {
				    t->destroy();
				    continue;
				  }
				head2->push_back(h->clone());
				tail2->push_back(t);
				(*i)->destroy();
				*i = 0;
			      }
			    else
			      {
				continue;
			      }
			  }
			if (!head1->empty())
			  {
			    const formula* h =
			      multop::instance(multop::And, head1);
			    const formula* t =
			      multop::instance(multop::AndNLM, tail1);
			    const formula* f =
			      multop::instance(multop::Concat, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head1;
			    delete tail1;
			  }
			if (!head2->empty())
			  {
			    const formula* h =
			      multop::instance(multop::And, head2);
			    const formula* t =
			      multop::instance(multop::AndNLM, tail2);
			    const formula* f =
			      multop::instance(multop::Fusion, h, t);
			    s.res_other->push_back(f);
			  }
			else
			  {
			    delete head2;
			    delete tail2;
			  }

			result_ = multop::instance(multop::AndNLM, s.res_other);
			// If we altered the formula in some way, process
			// it another time.
			if (result_ != mo)
			  result_ = recurse_destroy(result_);
			return;
		      }
		    break;
		  }
		case multop::Concat:
		case multop::Fusion:
		  break;
		}
	    }
	  result_ = multop::instance(mo->op(), res);
	}

	const formula*
	recurse(const formula* f)
	{
	  return simplify_recursively(f, c_);
	}

	const formula*
	recurse_destroy(const formula* f)
	{
	  const formula* tmp = recurse(f);
	  f->destroy();
	  return tmp;
	}

      protected:
	const formula* result_;
	ltl_simplifier_cache* c_;
	const ltl_simplifier_options& opt_;
      };


      const formula*
      simplify_recursively(const formula* f,
			   ltl_simplifier_cache* c)
      {
#ifdef TRACE
	static int srec = 0;
	for (int i = srec; i; --i)
	  trace << ' ';
	trace << "** simplify_recursively(" << to_string(f) << ")";
#endif

	const formula* result = c->lookup_simplified(f);
	if (result)
	  {
	    trace << " cached: " << to_string(result) << std::endl;
	    return result;
	  }
	else
	  {
	    trace << " miss" << std::endl;
	  }

#ifdef TRACE
	++srec;
#endif

	if (f->is_boolean() && c->options.boolean_to_isop)
	  {
	    result = c->boolean_to_isop(f);
	  }
	else
	  {
	    simplify_visitor v(c);
	    f->accept(v);
	    result = v.result();
	  }

#ifdef TRACE
	--srec;
	for (int i = srec; i; --i)
	  trace << ' ';
        trace << "** simplify_recursively(" << to_string(f) << ") result: "
	      << to_string(result) << std::endl;
#endif

	c->cache_simplified(f, result);
	return result;
      }

    }

    //////////////////////////////////////////////////////////////////////
    // ltl_simplifier_cache


    // This implements the recursive rules for syntactic implication.
    // (To follow this code please look at the table given as an
    // appendix in the documentation for temporal logic operators.)
    inline
    bool
    ltl_simplifier_cache::syntactic_implication_aux(const formula* f,
						    const formula* g)
    {
      formula::opkind fk = f->kind();
      formula::opkind gk = g->kind();

      // We first process all lines from the table except the
      // first two, and then we process the first two as a fallback.
      //
      // However for Boolean formulas we skip the bottom lines
      // (keeping only the first one) to prevent them from being
      // further split.
      if (!f->is_boolean())
	// Deal with all lines of the table except the first two.
	switch (fk)
	  {
	  case formula::Constant:
	  case formula::AtomicProp:
	  case formula::BUnOp:
	  case formula::AutomatOp:
	    break;

	  case formula::UnOp:
	    {
	      const unop* f_ = down_cast<const unop*>(f);
	      unop::type fo = f_->op();

	      if ((fo == unop::X || fo == unop::F) && g->is_eventual()
		  && syntactic_implication(f_->child(), g))
		return true;
	      if (gk == formula::UnOp)
		{
		  const unop* g_ = down_cast<const unop*>(g);
		  unop::type go = g_->op();
		  if (fo == unop::X)
		    {
		      if (go == unop::X
			  && syntactic_implication(f_->child(), g_->child()))
			return true;
		    }
		}
	      else if (gk == formula::BinOp && fo == unop::G)
		{
		  const binop* g_ = down_cast<const binop*>(g);
		  binop::type go = g_->op();
		  const formula* g1 = g_->first();
		  const formula* g2 = g_->second();
		  if ((go == binop::U || go == binop::R)
		      && syntactic_implication(f_->child(), g2))
		    return true;
		  else if (go == binop::W
			   && (syntactic_implication(f_->child(), g1)
			       || syntactic_implication(f_->child(), g2)))
		    return true;
		  else if (go == binop::M
			   && (syntactic_implication(f_->child(), g1)
			       && syntactic_implication(f_->child(), g2)))
		    return true;
		}
	      // First column.
	      if (fo == unop::G && syntactic_implication(f_->child(), g))
		return true;
	      break;
	    }

	  case formula::BinOp:
	    {
	      const binop* f_ = down_cast<const binop*>(f);
	      binop::type fo = f_->op();
	      const formula* f1 = f_->first();
	      const formula* f2 = f_->second();

	      if (gk == formula::UnOp)
		{
		  const unop* g_ = down_cast<const unop*>(g);
		  unop::type go = g_->op();
		  if (go == unop::F)
		    {
		      if (fo == binop::U)
			{
			  if (syntactic_implication(f2, g_->child()))
			    return true;
			}
		      else if (fo == binop::W)
			{
			  if (syntactic_implication(f1, g_->child())
			      && syntactic_implication(f2, g_->child()))
			    return true;
			}
		      else if (fo == binop::R)
			{
			  if (syntactic_implication(f2, g_->child()))
			    return true;
			}
		      else if (fo == binop::M)
			{
			  if (syntactic_implication(f1, g_->child())
			      || syntactic_implication(f2, g_->child()))
			    return true;
			}
		    }
		}
	      else if (gk == formula::BinOp)
		{
		  const binop* g_ = down_cast<const binop*>(g);
		  binop::type go = g_->op();
		  const formula* g1 = g_->first();
		  const formula* g2 = g_->second();

		  if ((fo == binop::U && (go == binop::U || go == binop::W))
		      || (fo == binop::W && go == binop::W)
		      || (fo == binop::R && go == binop::R)
		      || (fo == binop::M && (go == binop::R
					     || go == binop::M)))
		    {
		      if (syntactic_implication(f1, g1)
			  && syntactic_implication(f2, g2))
			return true;
		    }
		  else if (fo == binop::W && go == binop::U)
		    {
		      if (syntactic_implication(f1, g2)
			  && syntactic_implication(f2, g2))
			return true;
		    }
		  else if (fo == binop::R && go == binop::M)
		    {
		      if (syntactic_implication(f2, g1)
			  && syntactic_implication(f2, g2))
			return true;
		    }
		  else if ((fo == binop::U
			    && (go == binop::R || go == binop::M))
			   || (fo == binop::W && go == binop::R))
		    {
		      if (syntactic_implication(f1, g2)
			  && syntactic_implication(f2, g1)
			  && syntactic_implication(f2, g2))
			return true;
		    }
		  else if ((fo == binop::M
			    && (go == binop::U || go == binop::W))
			   || (fo == binop::R && go == binop::W))
		    {
		      if (syntactic_implication(f1, g2)
			  && syntactic_implication(f2, g1))
			return true;
		    }
		}

	      // First column.
	      if (fo == binop::U || fo == binop::W)
		{
		  if (syntactic_implication(f1, g)
		      && syntactic_implication(f2, g))
		    return true;
		}
	      else if (fo == binop::R || fo == binop::M)
		{
		  if (syntactic_implication(f2, g))
		    return true;
		}
	      break;
	    }
	  case formula::MultOp:
	    {
	      const multop* f_ = down_cast<const multop*>(f);
	      multop::type fo = f_->op();
	      unsigned fs = f_->size();

	      // First column.
	      switch (fo)
		{
		case multop::Or:
		  {
		    if (f->is_boolean())
		      break;
		    unsigned i = 0;
		    // If we are checking something like
		    //   (a | b | Xc) => g,
		    // split it into
		    //   (a | b) => g
		    //   Xc      => g
		    if (const formula* bops = f_->boolean_operands(&i))
		      {
			bool r = syntactic_implication(bops, g);
			bops->destroy();
			if (!r)
			  break;
		      }
		    bool b = true;
		    for (; i < fs; ++i)
		      if (!syntactic_implication(f_->nth(i), g))
			{
			  b = false;
			  break;
			}
		    if (b)
		      return true;
		    break;
		  }
		case multop::And:
		  {
		    if (f->is_boolean())
		      break;
		    unsigned i = 0;
		    // If we are checking something like
		    //   (a & b & Xc) => g,
		    // split it into
		    //   (a & b) => g
		    //   Xc      => g
		    if (const formula* bops = f_->boolean_operands(&i))
		      {
			bool r = syntactic_implication(bops, g);
			bops->destroy();
			if (r)
			  return true;
		      }
		    for (; i < fs; ++i)
		      if (syntactic_implication(f_->nth(i), g))
			return true;
		    break;
		  }
		case multop::Concat:
		case multop::Fusion:
		case multop::AndNLM:
		case multop::AndRat:
		case multop::OrRat:
		  break;
		}
	      break;
	    }
	  }
      // First two lines of the table.
      // (Don't check equality, it has already be done.)
      if (!g->is_boolean())
	switch (gk)
	  {
	  case formula::Constant:
	  case formula::AtomicProp:
	  case formula::BUnOp:
	  case formula::AutomatOp:
	    break;

	  case formula::UnOp:
	    {
	      const unop* g_ = down_cast<const unop*>(g);
	      unop::type go = g_->op();
	      if (go == unop::F)
		{
		  if (syntactic_implication(f, g_->child()))
		    return true;
		}
	      else if (go == unop::G || go == unop::X)
		{
		  if (f->is_universal()
		      && syntactic_implication(f, g_->child()))
		    return true;
		}
	      break;
	    }
	  case formula::BinOp:
	    {
	      const binop* g_ = down_cast<const binop*>(g);
	      binop::type go = g_->op();
	      const formula* g1 = g_->first();
	      const formula* g2 = g_->second();

	      if (go == binop::U || go == binop::W)
		{
		  if (syntactic_implication(f, g2))
		    return true;
		}
	      else if (go == binop::M || go == binop::R)
		{
		  if (syntactic_implication(f, g1)
		      && syntactic_implication(f, g2))
		    return true;
		}
	      break;
	    }
	  case formula::MultOp:
	    {
	      const multop* g_ = down_cast<const multop*>(g);
	      multop::type go = g_->op();
	      unsigned gs = g_->size();

	      switch (go)
		{
		case multop::And:
		  {
		    unsigned i = 0;
		    // If we are checking something like
		    //   f => (a & b & Xc),
		    // split it into
		    //   f => (a & b)
		    //   f => Xc
		    if (const formula* bops = g_->boolean_operands(&i))
		      {
			bool r = syntactic_implication(f, bops);
			bops->destroy();
			if (!r)
			  break;
		      }
		    bool b = true;
		    for (; i < gs; ++i)
		      if (!syntactic_implication(f, g_->nth(i)))
			{
			  b = false;
			  break;
			}
		    if (b)
		      return true;
		    break;
		  }
		case multop::Or:
		  {
		    unsigned i = 0;
		    // If we are checking something like
		    //   f => (a | b | Xc),
		    // split it into
		    //   f => (a | b)
		    //   f => Xc
		    if (const formula* bops = g_->boolean_operands(&i))
		      {
			bool r = syntactic_implication(f, bops);
			bops->destroy();
			if (r)
			  return true;
		      }
		    for (; i < gs; ++i)
		      if (syntactic_implication(f, g_->nth(i)))
			return true;
		    break;
		  }
		case multop::Concat:
		case multop::Fusion:
		case multop::AndNLM:
		case multop::AndRat:
		case multop::OrRat:
		  break;
		}
	      break;
	    }
	  }
      return false;
    }

    // Return true if f => g syntactically
    bool
    ltl_simplifier_cache::syntactic_implication(const formula* f,
						const formula* g)
    {
      // We cannot run syntactic_implication on SERE formulae,
      // except on Boolean formulae.
      if (f->is_sere_formula() && !f->is_boolean())
	return false;
      if (g->is_sere_formula() && !g->is_boolean())
	return false;

      if (f == g)
	return true;
      if (g == constant::true_instance()
	  || f == constant::false_instance())
	return true;
      if (g == constant::false_instance()
	  || f == constant::true_instance())
	return false;

      // Often we compare a literal (an atomic_prop or its negation)
      // to another literal.  The result is necessarily false. To be
      // true, the two literals would have to be equal, but we have
      // already checked that.
      if (is_literal(f) && is_literal(g))
	return false;

      // Cache lookup
      {
	pairf p(f, g);
	syntimpl_cache_t::const_iterator i = syntimpl_.find(p);
	if (i != syntimpl_.end())
	  return i->second;
      }

      bool result;

      if (f->is_boolean() && g->is_boolean())
	result = bdd_implies(as_bdd(f), as_bdd(g));
      else
	result = syntactic_implication_aux(f, g);

      // Cache result
      {
	pairf p(f->clone(), g->clone());
	syntimpl_[p] = result;
	// std::cerr << to_string(f) << (result ? " ==> " : " =/=> ")
	//           << to_string(g) << std::endl;
      }

      return result;
    }

    // If right==false, true if !f1 => f2, false otherwise.
    // If right==true, true if f1 => !f2, false otherwise.
    bool
    ltl_simplifier_cache::syntactic_implication_neg(const formula* f1,
						    const formula* f2,
						    bool right)
    {
      // We cannot run syntactic_implication_neg on SERE formulae,
      // except on Boolean formulae.
      if (f1->is_sere_formula() && !f1->is_boolean())
	return false;
      if (f2->is_sere_formula() && !f2->is_boolean())
	return false;

      if (right)
	f2 = nenoform_recursively(f2, true, this);
      else
	f1 = nenoform_recursively(f1, true, this);

      bool result = syntactic_implication(f1, f2);

      (right ? f2 : f1)->destroy();

      return result;
    }


    /////////////////////////////////////////////////////////////////////
    // ltl_simplifier

    ltl_simplifier::ltl_simplifier(bdd_dict* d)
    {
      if (!d)
	{
	  d = new bdd_dict;
	  owndict = true;
	}
      else
	{
	  owndict = false;
	}
      cache_ = new ltl_simplifier_cache(d);
    }

    ltl_simplifier::ltl_simplifier(const ltl_simplifier_options& opt,
				   bdd_dict* d)
    {
      if (!d)
	{
	  d = new bdd_dict;
	  owndict = true;
	}
      else
	{
	  owndict = false;
	}
      cache_ = new ltl_simplifier_cache(d, opt);
    }

    ltl_simplifier::~ltl_simplifier()
    {
      bdd_dict* todelete = 0;
      if (owndict)
	todelete = cache_->dict;
      delete cache_;
      // It has to be deleted after the cache.
      delete todelete;
    }

    const formula*
    ltl_simplifier::simplify(const formula* f)
    {
      const formula* neno = 0;
      if (!f->is_in_nenoform())
	f = neno = negative_normal_form(f, false);
      const formula* res = simplify_recursively(f, cache_);
      if (neno)
	neno->destroy();
      return res;
    }

    const formula*
    ltl_simplifier::negative_normal_form(const formula* f, bool negated)
    {
      return nenoform_recursively(f, negated, cache_);
    }

    bool
    ltl_simplifier::syntactic_implication(const formula* f1, const formula* f2)
    {
      return cache_->syntactic_implication(f1, f2);
    }

    bool
    ltl_simplifier::syntactic_implication_neg(const formula* f1,
					      const formula* f2, bool right)
    {
      return cache_->syntactic_implication_neg(f1, f2, right);
    }

    bool
    ltl_simplifier::are_equivalent(const formula* f, const formula* g)
    {
      return cache_->lcc.equal(f, g);
    }

    bool
    ltl_simplifier::implication(const formula* f, const formula* g)
    {
      return cache_->lcc.contained(f, g);
    }

    bdd
    ltl_simplifier::as_bdd(const formula* f)
    {
      return cache_->as_bdd(f);
    }

    const formula*
    ltl_simplifier::star_normal_form(const formula* f)
    {
      return cache_->star_normal_form(f);
    }

    const formula*
    ltl_simplifier::boolean_to_isop(const formula* f)
    {
      return cache_->boolean_to_isop(f);
    }

    bdd_dict*
    ltl_simplifier::get_dict() const
    {
      return cache_->dict;
    }

    void
    ltl_simplifier::print_stats(std::ostream& os) const
    {
      cache_->print_stats(os);
    }

    void
    ltl_simplifier::clear_as_bdd_cache()
    {
      cache_->clear_as_bdd_cache();
      cache_->lcc.clear();
    }
  }
}
