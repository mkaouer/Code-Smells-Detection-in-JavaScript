// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "tgba/tgbabddconcretefactory.hh"
#include <cassert>

#include "ltl2tgba_lacim.hh"

namespace spot
{
  namespace
  {
    using namespace ltl;

    /// \brief Recursively translate a formula into a BDD.
    ///
    /// The algorithm used here is adapted from Jean-Michel Couvreur's
    /// Probataf tool.
    class ltl_trad_visitor: public visitor
    {
    public:
      ltl_trad_visitor(tgba_bdd_concrete_factory& fact, bool root = false)
	: fact_(fact), root_(root)
      {
      }

      virtual
      ~ltl_trad_visitor()
      {
      }

      bdd
      result()
      {
	return res_;
      }

      void
      visit(const atomic_prop* node)
      {
	res_ = bdd_ithvar(fact_.create_atomic_prop(node));
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
	    assert(!"unsupported operator");
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
	      /*
		     Fx  <=> x | XFx
		 In other words:
		     now <=> x | next
	      */
	      int v = fact_.create_state(node);
	      bdd now = bdd_ithvar(v);
	      bdd next = bdd_ithvar(v + 1);
	      bdd x = recurse(node->child());
	      fact_.constrain_relation(bdd_apply(now, x | next, bddop_biimp));
	      /*
		`x | next', doesn't actually encode the fact that x
		should be fulfilled eventually.  We ensure this by
		creating a new generalized Büchi acceptance set, Acc[x],
		and leave out of this set any transition going off NOW
		without checking X.  Such acceptance conditions are
		checked for during the emptiness check.
	      */
	      fact_.declare_acceptance_condition(x | !now, node->child());
	      res_ = now;
	      return;
	    }
	  case unop::G:
	    {
	      bdd child = recurse(node->child());
	      // If G occurs at the top of the formula we don't
	      // need Now/Next variables.  We just constrain
	      // the relation so that the child always happens.
	      // This saves 2 BDD variables.
	      if (root_)
		{
		  fact_.constrain_relation(child);
		  res_ = child;
		  return;
		}
	      // Gx  <=>  x && XGx
	      int v = fact_.create_state(node);
	      bdd now = bdd_ithvar(v);
	      bdd next = bdd_ithvar(v + 1);
	      fact_.constrain_relation(bdd_apply(now, child & next,
						 bddop_biimp));
	      res_ = now;
	      return;
	    }
	  case unop::Not:
	    {
	      res_ = bdd_not(recurse(node->child()));
	      return;
	    }
	  case unop::X:
	    {
	      int v = fact_.create_state(node->child());
	      bdd now = bdd_ithvar(v);
	      bdd next = bdd_ithvar(v + 1);
	      fact_.constrain_relation(bdd_apply(now, recurse(node->child()),
						 bddop_biimp));
	      res_ = next;
	      return;
	    }
	  case unop::Finish:
	  case unop::Closure:
	  case unop::NegClosure:
	  case unop::NegClosureMarked:
	    assert(!"unsupported operator");
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const bunop*)
      {
	assert(!"unsupported operator");
      }

      void
      visit(const binop* node)
      {
	bdd f1 = recurse(node->first());
	bdd f2 = recurse(node->second());

	binop::type op = node->op();
	switch (op)
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
	  case binop::W:
	    {
	      // f1 U f2 <=> f2 | (f1 & X(f1 U f2))
	      //   In other words:
	      // now <=> f2 | (f1 & next)
	      int v = fact_.create_state(node);
	      bdd now = bdd_ithvar(v);
	      bdd next = bdd_ithvar(v + 1);
	      fact_.constrain_relation(bdd_apply(now, f2 | (f1 & next),
						 bddop_biimp));
	      if (op == binop::U)
		{
		  // The rightmost conjunction, f1 & next, doesn't
		  // actually encode the fact that f2 should be
		  // fulfilled eventually.  We declare an acceptance
		  // condition for this purpose (see the comment in
		  // the unop::F case).
		  fact_.declare_acceptance_condition(f2 | !now, node->second());
		}
	      res_ = now;
	      return;
	    }
	  case binop::R:
	  case binop::M:
	    {
	      // f1 R f2 <=> f2 & (f1 | X(f1 R f2))
	      //   In other words:
	      // now <=> f2 & (f1 | next)
	      int v = fact_.create_state(node);
	      bdd now = bdd_ithvar(v);
	      bdd next = bdd_ithvar(v + 1);
	      fact_.constrain_relation(bdd_apply(now, f2 & (f1 | next),
						 bddop_biimp));
	      if (op == binop::M)
		{
		  // f2 & next, doesn't actually encode the fact that
		  // f1 should be fulfilled eventually.  We declare an
		  // acceptance condition for this purpose (see the
		  // comment in the unop::F case).
		  fact_.declare_acceptance_condition(f1 | !now, node->second());
		}
	      res_ = now;
	      return;
	    }
	  case binop::UConcat:
	  case binop::EConcat:
	  case binop::EConcatMarked:
	    assert(!"unsupported operator");
	    break;
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
      visit(const multop* node)
      {
	int op = -1;
	bool root = false;
	switch (node->op())
	  {
	  case multop::And:
	    op = bddop_and;
	    res_ = bddtrue;
	    // When the root formula is a conjunction it's ok to
	    // consider all children as root formulae.  This allows the
	    // root-G trick to save many more variable.  (See the
	    // translation of G.)
	    root = root_;
	    break;
	  case multop::Or:
	    op = bddop_or;
	    res_ = bddfalse;
	    break;
	  case multop::Concat:
	  case multop::Fusion:
	  case multop::AndNLM:
	  case multop::AndRat:
	  case multop::OrRat:
	    assert(!"unsupported operator");
	  }
	assert(op != -1);
	unsigned s = node->size();
	for (unsigned n = 0; n < s; ++n)
	  {
	    res_ = bdd_apply(res_, recurse(node->nth(n), root), op);
	  }
      }

      bdd
      recurse(const formula* f, bool root = false)
      {
	ltl_trad_visitor v(fact_, root);
	f->accept(v);
	return v.result();
      }

    private:
      bdd res_;
      tgba_bdd_concrete_factory& fact_;
      bool root_;
    };
  } // anonymous

  tgba_bdd_concrete*
  ltl_to_tgba_lacim(const ltl::formula* f, bdd_dict* dict)
  {
    // Normalize the formula.  We want all the negations on
    // the atomic propositions.  We also suppress logic
    // abbreviations such as <=>, =>, or XOR, since they
    // would involve negations at the BDD level.
    const ltl::formula* f1 = ltl::unabbreviate_logic(f);
    const ltl::formula* f2 = ltl::negative_normal_form(f1);
    f1->destroy();

    // Traverse the formula and draft the automaton in a factory.
    tgba_bdd_concrete_factory fact(dict);
    ltl_trad_visitor v(fact, true);
    f2->accept(v);
    f2->destroy();
    fact.finish();

    // Finally setup the resulting automaton.
    return new tgba_bdd_concrete(fact, v.result());
  }
}
