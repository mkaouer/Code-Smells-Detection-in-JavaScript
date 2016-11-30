// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include <cassert>
#include <utility>
#include <algorithm>
#include <iostream>
#include "multop.hh"
#include "constant.hh"
#include "bunop.hh"
#include "visitor.hh"

namespace spot
{
  namespace ltl
  {
    multop::multop(type op, vec* v)
      : ref_formula(MultOp), op_(op), children_(v)
    {
      unsigned s = v->size();
      assert(s > 1);

      props = (*v)[0]->get_props();

      switch (op)
	{
	case Fusion:
	  is.accepting_eword = false;
	case Concat:
	case AndNLM:
	case AndRat:
	  // Note: AndNLM(p1,p2) and AndRat(p1,p2) are Boolean
	  // formulae, but there are actually rewritten as And(p1,p2)
	  // by trivial identities before this constructor is called.
	  // So at this point, AndNLM/AndRat are always used with at
	  // most one Boolean argument, and the result is therefore
	  // NOT Boolean.
	  is.boolean = false;
	  is.ltl_formula = false;
	  is.eltl_formula = false;
	  is.psl_formula = false;
	  is.eventual = false;
	  is.universal = false;
	case And:
	  for (unsigned i = 1; i < s; ++i)
	    props &= (*v)[i]->get_props();
	  break;
	case OrRat:
	  // Note: OrRat(p1,p2) is a Boolean formula, but its is
	  // actually rewritten as Or(p1,p2) by trivial identities
	  // before this constructor is called.  So at this point,
	  // AndNLM is always used with at most one Boolean argument,
	  // and the result is therefore NOT Boolean.
	  is.boolean = false;
	  is.ltl_formula = false;
	  is.eltl_formula = false;
	  is.psl_formula = false;
	  is.eventual = false;
	  is.universal = false;
	case Or:
	  {
	    bool ew = (*v)[0]->accepts_eword();
	    for (unsigned i = 1; i < s; ++i)
	      {
		ew |= (*v)[i]->accepts_eword();
		props &= (*v)[i]->get_props();
	      }
	    is.accepting_eword = ew;
	    break;
	  }
	}
    }

    multop::~multop()
    {
      // Get this instance out of the instance map.
      pair p(op(), children_);
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      // Dereference children.
      for (unsigned n = 0; n < size(); ++n)
	nth(n)->destroy();

      delete children_;
    }

    std::string
    multop::dump() const
    {
      std::string r = "multop(";
      r += op_name();
      unsigned max = size();
      for (unsigned n = 0; n < max; ++n)
	r += ", " + nth(n)->dump();
      r += ")";
      return r;
    }

    void
    multop::accept(visitor& v) const
    {
      v.visit(this);
    }

    unsigned
    multop::size() const
    {
      return children_->size();
    }

    const formula*
    multop::nth(unsigned n) const
    {
      return (*children_)[n];
    }

    const formula*
    multop::all_but(unsigned n) const
    {
      unsigned s = size();
      vec* v = new vec;
      v->reserve(s - 1);
      for (unsigned pos = 0; pos < n; ++pos)
	v->push_back(nth(pos)->clone());
      for (unsigned pos = n + 1; pos < s; ++pos)
	v->push_back(nth(pos)->clone());
      return instance(op_, v);
    }

    multop::type
    multop::op() const
    {
      return op_;
    }

    const char*
    multop::op_name() const
    {
      switch (op_)
	{
	case And:
	  return "And";
	case AndRat:
	  return "AndRat";
	case AndNLM:
	  return "AndNLM";
	case Or:
	  return "Or";
	case OrRat:
	  return "OrRat";
	case Concat:
	  return "Concat";
	case Fusion:
	  return "Fusion";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    namespace
    {
      static void
      gather_bool(multop::vec* v, multop::type op)
      {
	// Gather all boolean terms.
	multop::vec* b = new multop::vec;
	multop::vec::iterator i = v->begin();
	while (i != v->end())
	  {
	    if ((*i)->is_boolean())
	      {
		b->push_back(*i);
		i = v->erase(i);
	      }
	    else
	      {
		++i;
	      }
	  }
	// - AndNLM(Exps1...,Bool1,Exps2...,Bool2,Exps3...) =
	//    AndNLM(Exps1...,Exps2...,Exps3...,And(Bool1,Bool2))
	// - AndRat(Exps1...,Bool1,Exps2...,Bool2,Exps3...) =
	//    AndRat(Exps1...,Exps2...,Exps3...,And(Bool1,Bool2))
	// - OrRat(Exps1...,Bool1,Exps2...,Bool2,Exps3...) =
	//    AndRat(Exps1...,Exps2...,Exps3...,Or(Bool1,Bool2))
	if (!b->empty())
	  v->push_back(multop::instance(op, b));
	else
	  delete b;
      }
    }

    multop::map multop::instances;

    // We match equivalent formulae modulo "ACI rules"
    // (i.e. associativity, commutativity and idempotence of the
    // operator).  For instance if `+' designates the OR operator and
    // `0' is false (the neutral element for `+') , then `f+f+0' is
    // equivalent to `f'.
    const formula*
    multop::instance(type op, vec* v)
    {
      // Inline children of same kind.
      //
      // When we construct a formula such as Multop(Op,X,Multop(Op,Y,Z))
      // we will want to inline it as Multop(Op,X,Y,Z).
      {
	vec inlined;
	vec::iterator i = v->begin();
	while (i != v->end())
	  {
	    // Some simplification routines erase terms using null
	    // pointers that we must ignore.
	    if ((*i) == 0)
	      {
		// FIXME: For commutative operators we should replace
		// the pointer by the first non-null value at the end
		// of the array instead of calling erase.
		i = v->erase(i);
		continue;
	      }
	    if (const multop* p = is_multop(*i))
	      {
		if (p->op() == op)
		  {
		    unsigned ps = p->size();
		    for (unsigned n = 0; n < ps; ++n)
		      inlined.push_back(p->nth(n)->clone());
		    (*i)->destroy();
		    // FIXME: Do not use erase.  See previous FIXME.
		    i = v->erase(i);
		    continue;
		  }
	      }
	    // All operator except "Concat" and "Fusion" are
	    // commutative, so we just keep a list of the inlined
	    // arguments that should later be added to the vector.
	    // For concat we have to keep track of the order of
	    // all the arguments.
	    if (op == Concat || op == Fusion)
	      inlined.push_back(*i);
	    ++i;
	  }
	if (op == Concat || op == Fusion)
	  *v = inlined;
	else
	  v->insert(v->end(), inlined.begin(), inlined.end());
      }

      if (op != Concat && op != Fusion)
	std::sort(v->begin(), v->end(), formula_ptr_less_than());

      unsigned orig_size = v->size();

      const formula* neutral;
      const formula* neutral2;
      const formula* abs;
      const formula* abs2;
      const formula* weak_abs;
      switch (op)
	{
	case And:
	  neutral = constant::true_instance();
	  neutral2 = 0;
	  abs = constant::false_instance();
	  abs2 = 0;
	  weak_abs = 0;
	  break;
	case AndRat:
	  neutral = bunop::one_star();
	  neutral2 = 0;
	  abs = constant::false_instance();
	  abs2 = 0;
	  weak_abs = constant::empty_word_instance();
	  gather_bool(v, And);
	  break;
	case AndNLM:
	  neutral = constant::empty_word_instance();
	  neutral2 = 0;
	  abs = constant::false_instance();
	  abs2 = 0;
	  weak_abs = constant::true_instance();
	  gather_bool(v, And);
	  break;
	case Or:
	  neutral = constant::false_instance();
	  neutral2 = 0;
	  abs = constant::true_instance();
	  abs2 = 0;
	  weak_abs = 0;
	  break;
	case OrRat:
	  neutral = constant::false_instance();
	  neutral2 = 0;
	  abs = bunop::one_star();
	  abs2 = 0;
	  weak_abs = 0;
	  gather_bool(v, Or);
	  break;
	case Concat:
	  neutral = constant::empty_word_instance();
	  neutral2 = 0;
	  abs = constant::false_instance();
	  abs2 = 0;
	  weak_abs = 0;

	  // - Concat(Exps1...,FExps2...,1[*],FExps3...,Exps4) =
	  //     Concat(Exps1...,1[*],Exps4)
	  // If FExps2... and FExps3 all accept [*0].
	  {
	    vec::iterator i = v->begin();
	    const formula* os = bunop::one_star();
	    while (i != v->end())
	      {
		while (i != v->end() && !(*i)->accepts_eword())
		  ++i;
		if (i == v->end())
		  break;
		vec::iterator b = i;
		// b is the first expressions that accepts [*0].
		// let's find more, and locate the position of
		// 1[*] at the same time.
		bool os_seen = false;
		do
		  {
		    os_seen |= (*i == os);
		    ++i;
		  }
		while (i != v->end() && (*i)->accepts_eword());

		if (os_seen) // [b..i) is a range that contains [*].
		  {
		    // Place [*] at the start of the range, and erase
		    // all other formulae.
		    (*b)->destroy();
		    *b++ = os->clone();
		    for (vec::iterator c = b; c < i; ++c)
		      (*c)->destroy();
		    i = v->erase(b, i);
		  }
	      }
	  }

	  break;
	case Fusion:
	  neutral = constant::true_instance();
	  neutral2 = 0;
	  abs = constant::false_instance();
	  abs2 = constant::empty_word_instance();
	  weak_abs = 0;

	  // Make a first pass to group adjacent Boolean formulae.
	  // - Fusion(Exps1...,BoolExp1...BoolExpN,Exps2,Exps3...) =
	  //   Fusion(Exps1...,And(BoolExp1...BoolExpN),Exps2,Exps3...)
	  {
	    vec::iterator i = v->begin();
	    while (i != v->end())
	      {
		if ((*i)->is_boolean())
		  {
		    vec::iterator first = i;
		    ++i;
		    if (i == v->end())
		      break;
		    if (!(*i)->is_boolean())
		      {
			++i;
			continue;
		      }
		    do
		      ++i;
		    while (i != v->end() && (*i)->is_boolean());
		    // We have at least two adjacent Boolean formulae.
		    // Replace the first one by the conjunction of all.
		    vec* b = new vec;
		    b->insert(b->begin(), first, i);
		    i = v->erase(first + 1, i);
		    *first = instance(And, b);
		  }
		else
		  {
		    ++i;
		  }
	      }
	  }

	  break;

	default:
	  neutral = 0;
	  neutral2 = 0;
	  abs = 0;
	  abs2 = 0;
	  weak_abs = 0;
	  break;
	}

      // Remove duplicates (except for Concat and Fusion).  We can't use
      // std::unique(), because we must destroy() any formula we drop.
      // Also ignore neutral elements and handle absorbent elements.
      {
	const formula* last = 0;
	vec::iterator i = v->begin();
	bool weak_abs_seen = false;
	while (i != v->end())
	  {
	    if ((*i == neutral) || (*i == neutral2) || (*i == last))
	      {
		(*i)->destroy();
		i = v->erase(i);
	      }
	    else if (*i == abs || *i == abs2)
	      {
		for (i = v->begin(); i != v->end(); ++i)
		  (*i)->destroy();
		delete v;
		return abs->clone();
	      }
	    else
	      {
		weak_abs_seen |= (*i == weak_abs);
		if (op != Concat && op != Fusion) // Don't remove duplicates
		  last = *i;
		++i;
	      }
	  }

	if (weak_abs_seen)
	  {
	    if (op == AndRat)
	      {
		// We have    a* && [*0] && c  = 0
		//     and    a* && [*0] && c* = [*0]
		// So if [*0] has been seen, check if alls term
		// recognize the empty word.
		bool acc_eword = true;
		for (i = v->begin(); i != v->end(); ++i)
		  {
		    acc_eword &= (*i)->accepts_eword();
		    (*i)->destroy();
		  }
		delete v;
		if (acc_eword)
		  return weak_abs;
		else
		  return abs;
	      }
	    else
	      {
		// Similarly,  a* & 1 & (c;d) = c;d
		//             a* & 1 & c* = 1
		assert(op == AndNLM);
		multop::vec tmp;
		for (i = v->begin(); i != v->end(); ++i)
		  {
		    if (*i == weak_abs)
		      continue;
		    if ((*i)->accepts_eword())
		      {
			(*i)->destroy();
			continue;
		      }
		    tmp.push_back(*i);
		  }
		if (tmp.empty())
		  tmp.push_back(weak_abs);
		v->swap(tmp);
	      }
	  }
	else if (op == Concat)
	  {
	    // Perform an extra loop to merge starable items.
	    //   f;f -> f[*2]
	    //   f;f[*i..j] -> f[*i+1..j+1]
	    //   f[*i..j];f -> f[*i+1..j+1]
	    //   f[*i..j];f[*k..l] -> f[*i+k..j+l]
	    i = v->begin();
	    while (i != v->end())
	      {
		vec::iterator fpos = i;
		const formula* f;
		unsigned min;
		unsigned max;
		bool changed = false;
		if (const bunop* is = is_Star(*i))
		  {
		    f = is->child();
		    min = is->min();
		    max = is->max();
		  }
		else
		  {
		    f = *i;
		    min = max = 1;
		  }

		++i;
		while (i != v->end())
		  {
		    const formula* f2;
		    unsigned min2;
		    unsigned max2;
		    if (const bunop* is = is_Star(*i))
		      {
			f2 = is->child();
			if (f2 != f)
			  break;
			min2 = is->min();
			max2 = is->max();
		      }
		    else
		      {
			f2 = *i;
			if (f2 != f)
			  break;
			min2 = max2 = 1;
		      }
		    if (min2 == bunop::unbounded)
		      min = bunop::unbounded;
		    else if (min != bunop::unbounded)
		      min += min2;
		    if (max2 == bunop::unbounded)
		      max = bunop::unbounded;
		    else if (max != bunop::unbounded)
		      max += max2;
		    (*i)->destroy();
		    i = v->erase(i);
		    changed = true;
		  }
		if (changed)
		  {
		    const formula* newfs =
		      bunop::instance(bunop::Star, f->clone(), min, max);
		    (*fpos)->destroy();
		    *fpos = newfs;
		  }
	      }
	  }
      }

      vec::size_type s = v->size();
      if (s == 0)
	{
	  delete v;
	  assert(neutral != 0);
	  return neutral->clone();
	}
      else if (s == 1)
	{
	  // Simply replace Multop(Op,X) by X.
	  // Except we should never reduce the
	  // arguments of a Fusion operator to
	  // a list with a single formula that
	  // accepts [*0].
	  const formula* res = (*v)[0];
	  if (op != Fusion || orig_size == 1
	      || !res->accepts_eword())
	    {
	      delete v;
	      return res;
	    }
	  // If Fusion(f, ...) reduce to Fusion(f), emit Fusion(1,f).
	  // to ensure that [*0] is not accepted.
	  v->insert(v->begin(), constant::true_instance());
	}
      // The hash key.
      pair p(op, v);

      const multop* res;
      // FIXME: Use lower_bound or hash_map.
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // The instance already exists.
	  for (vec::iterator vi = v->begin(); vi != v->end(); ++vi)
	    (*vi)->destroy();
	  delete v;
	  res = i->second;
	}
      else
	{
	  // This is the first instance of this formula.
	  // Record the instance in the map,
	  res = instances[p] = new multop(op, v);
	}
      res->clone();
      return res;
    }

    const formula*
    multop::instance(type op, const formula* first, const formula* second)
    {
      vec* v = new vec;
      v->push_back(first);
      v->push_back(second);
      return instance(op, v);
    }

    unsigned
    multop::instance_count()
    {
      return instances.size();
    }

    std::ostream&
    multop::dump_instances(std::ostream& os)
    {
      for (map::iterator i = instances.begin(); i != instances.end(); ++i)
	{
	  os << i->second << " = "
	     << i->second->ref_count_() << " * "
	     << i->second->dump()
	     << std::endl;
	}
      return os;
    }

  }
}
