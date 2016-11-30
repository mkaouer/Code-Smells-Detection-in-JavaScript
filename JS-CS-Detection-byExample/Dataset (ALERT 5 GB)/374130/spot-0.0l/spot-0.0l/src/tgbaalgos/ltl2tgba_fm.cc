// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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
#include "misc/minato.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/destroy.hh"
#include "ltlvisit/tostring.hh"
#include <cassert>

#include "tgba/tgbabddconcretefactory.hh"
#include "ltl2tgba_fm.hh"

namespace spot
{
  using namespace ltl;

  namespace
  {

    // Helper dictionary.  We represent formula using a BDD to simplify
    // them, and them translate the BDD back into formulae.
    //
    // The name of the variables are inspired from Couvreur's FM paper.
    //   "a" variables are promises (written "a" in the paper)
    //   "next" variables are X's operands (the "r_X" variables from the paper)
    //   "var" variables are atomic propositions.
    class translate_dict: public bdd_allocator
    {
    public:

      translate_dict()
	: bdd_allocator(),
	  a_set(bddtrue),
	  var_set(bddtrue),
	  next_set(bddtrue)
      {
      }

      ~translate_dict()
      {
	fv_map::iterator i;
	for (i = a_map.begin(); i != a_map.end(); ++i)
	  destroy(i->first);
	for (i = var_map.begin(); i != var_map.end(); ++i)
	  destroy(i->first);
	for (i = next_map.begin(); i != next_map.end(); ++i)
	  destroy(i->first);
      }

      /// Formula-to-BDD-variable maps.
      typedef Sgi::hash_map<const formula*, int,
			    ptr_hash<formula> > fv_map;
      /// BDD-variable-to-formula maps.
      typedef Sgi::hash_map<int, const formula*> vf_map;

      fv_map a_map;	       ///< Maps formulae to "a" BDD variables
      vf_map a_formula_map;    ///< Maps "a" BDD variables to formulae
      fv_map var_map;	       ///< Maps atomic propisitions to BDD variables
      vf_map var_formula_map;  ///< Maps BDD variables to atomic propisitions
      fv_map next_map;	       ///< Maps "Next" variables to BDD variables
      vf_map next_formula_map; ///< Maps BDD variables to "Next" variables

      bdd a_set;
      bdd var_set;
      bdd next_set;

      int
      register_proposition(const formula* f)
      {
	int num;
	// Do not build a variable that already exists.
	fv_map::iterator sii = var_map.find(f);
	if (sii != var_map.end())
	  {
	    num = sii->second;
	  }
	else
	  {
	    f = clone(f);
	    num = allocate_variables(1);
	    var_map[f] = num;
	    var_formula_map[num] = f;
	  }
	var_set &= bdd_ithvar(num);
	return num;
      }

      int
      register_a_variable(const formula* f)
      {
	int num;
	// Do not build an acceptance variable that already exists.
	fv_map::iterator sii = a_map.find(f);
	if (sii != a_map.end())
	  {
	    num = sii->second;
	  }
	else
	  {
	    f = clone(f);
	    num = allocate_variables(1);
	    a_map[f] = num;
	    a_formula_map[num] = f;
	  }
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
	    num = allocate_variables(1);
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
	os << "Atomic Propositions:" << std::endl;
	for (fi = var_map.begin(); fi != var_map.end(); ++fi)
	  {
	    os << "  " << fi->second << ": ";
	    to_string(fi->first, os) << std::endl;
	  }
	os << "a Variables:" << std::endl;
	for (fi = a_map.begin(); fi != a_map.end(); ++fi)
	{
	  os << "  " << fi->second << ": a[";
	  to_string(fi->first, os) << "]" << std::endl;
	}
	os << "Next Variables:" << std::endl;
	for (fi = next_map.begin(); fi != next_map.end(); ++fi)
	{
	  os << "  " << fi->second << ": Next[";
	  to_string(fi->first, os) << "]" << std::endl;
	}
	return os;
      }

      formula*
      var_to_formula(int var) const
      {
	vf_map::const_iterator isi = next_formula_map.find(var);
	if (isi != next_formula_map.end())
	  return clone(isi->second);
	isi = a_formula_map.find(var);
	if (isi != a_formula_map.end())
	  return clone(isi->second);
	isi = var_formula_map.find(var);
	if (isi != var_formula_map.end())
	  return clone(isi->second);
	assert(0);
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

		if (! a->has_acceptance_condition(ac))
		  a->declare_acceptance_condition(clone(ac));
		a->add_acceptance_condition(t, ac);

		atomic_prop::instance_count();
		b = high;
	      }
	    assert(b != bddfalse);
	  }
      }
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

      bdd result() const
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
	      bdd y = recurse(node->child());
	      int a = dict_.register_a_variable(node);
	      int x = dict_.register_next_variable(node);
	      res_ = y | (bdd_ithvar(a) & bdd_ithvar(x));
	      return;
	    }
	  case unop::G:
	    {
	      const formula* child = node->child();
	      int x = dict_.register_next_variable(node);
	      // GFy is pretty frequent and easy to optimize, to we
	      // want to detect it.
	      const unop* Fy = dynamic_cast<const unop*>(child);
	      if (Fy && Fy->op() == unop::F)
		{
		  // r(GFy) = (r(y) + a(y))r(XGFy)
		  const formula* child = Fy->child();
		  bdd y = recurse(child);
		  int a = dict_.register_a_variable(child);
		  res_ = (y | bdd_ithvar(a)) & bdd_ithvar(x);
		}
	      else
		{
		  // r(Gy) = r(y)r(XGy)
		  bdd y = recurse(child);
		  res_ = y & bdd_ithvar(x);
		}
	      return;
	    }
	  case unop::Not:
	    {
	      res_ = bdd_not(recurse(node->child()));
	      return;
	    }
	  case unop::X:
	    {
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

  }

  tgba_explicit*
  ltl_to_tgba_fm(const formula* f, bdd_dict* dict)
  {
    // Normalize the formula.  We want all the negations on
    // the atomic propositions.  We also suppress logic
    // abbreviations such as <=>, =>, or XOR, since they
    // would involve negations at the BDD level.
    formula* f1 = unabbreviate_logic(f);
    formula* f2 = negative_normal_form(f1);
    destroy(f1);

    std::set<formula*> formulae_seen;
    std::set<formula*> formulae_to_translate;

    formulae_seen.insert(f2);
    formulae_to_translate.insert(f2);

    tgba_explicit* a = new tgba_explicit(dict);

    a->set_init_state(to_string(f2));

    while (!formulae_to_translate.empty())
      {
	// Pick one formula.
	formula* f = *formulae_to_translate.begin();
	formulae_to_translate.erase(formulae_to_translate.begin());

	// Translate it into a BDD to simplify it.
	translate_dict d;
	ltl_trad_visitor v(d);
	f->accept(v);
	bdd res = v.result();

	std::string now = to_string(f);

	minato_isop isop(res, d.next_set & d.a_set);
	bdd cube;
	while ((cube = isop.next()) != bddfalse)
	  {
	    formula* dest =
	      d.conj_bdd_to_formula(bdd_existcomp(cube, d.next_set));

	    std::string next = to_string(dest);

	    tgba_explicit::transition* t = a->create_transition(now, next);

	    a->add_condition(t,
			     d.bdd_to_formula(bdd_existcomp(cube, d.var_set)));
	    d.conj_bdd_to_acc(a, bdd_existcomp(cube, d.a_set), t);

	    if (formulae_seen.find(dest) == formulae_seen.end())
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
    for (std::set<formula*>::iterator i = formulae_seen.begin();
	 i != formulae_seen.end(); ++i)
      destroy(*i);

    // Turn all promises into real acceptance conditions.
    a->complement_all_acceptance_conditions();
    return a;
  }

}
