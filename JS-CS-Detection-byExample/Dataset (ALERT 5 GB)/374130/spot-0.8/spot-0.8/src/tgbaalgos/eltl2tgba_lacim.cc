// Copyright (C) 2008, 2009, 2010 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/formula_tree.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "tgba/tgbabddconcretefactory.hh"
#include <cassert>

#include "eltl2tgba_lacim.hh"

namespace spot
{
  namespace
  {
    using namespace ltl;

    /// \brief Recursively translate a formula into a BDD.
    class eltl_trad_visitor : public const_visitor
    {
    public:
      eltl_trad_visitor(tgba_bdd_concrete_factory& fact, bool root = false)
	  : fact_(fact), root_(root), finish_()
      {
      }

      virtual
      ~eltl_trad_visitor()
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
	  }
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const unop* node)
      {
	switch (node->op())
	  {
	  case unop::Not:
	    {
	      res_ = bdd_not(recurse(node->child()));
	      return;
	    }
	  case unop::Finish:
	    {
	      // Ensure finish_[node->child()] has been computed if
	      // node->child() is an automaton operator.
	      res_ = recurse(node->child());
	      finish_map_::const_iterator it = finish_.find(node->child());
	      if (it != finish_.end())
		res_ = finish_[node->child()];
	      return;
	    }
	  case unop::X:
	  case unop::F:
          case unop::G:
	    assert(!"unsupported operator");
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
	  case binop::R:
	  case binop::W:
	  case binop::M:
	    assert(!"unsupported operator");
	  }
	/* Unreachable code.  */
	assert(0);
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
	  }
	assert(op != -1);
	unsigned s = node->size();
	for (unsigned n = 0; n < s; ++n)
	  res_ = bdd_apply(res_, recurse(node->nth(n), root), op);
      }

      void
      visit(const automatop* node)
      {
	nmap m;
	bdd finish = bddfalse;
	bdd acc = bddtrue;

	std::vector<formula*> v;
	for (unsigned i = 0; i < node->size(); ++i)
	  v.push_back(const_cast<formula*>(node->nth(i)));

	std::pair<int, int> vp =
	  recurse_state(node->get_nfa(),
			node->get_nfa()->get_init_state(), v, m, acc, finish);

	// Update finish_ with finish(node).
	// FIXME: when node is loop, it does not make sense; hence the bddtrue.
	finish_[node] = !node->get_nfa()->is_loop() ? bddtrue : finish;

	bdd tmp = bddtrue;
	for (nmap::iterator it = m.begin(); it != m.end(); ++it)
	  tmp &= bdd_apply(bdd_ithvar(it->second.first  + 1),
			   bdd_ithvar(it->second.second + 1), bddop_biimp);
	fact_.constrain_relation(bdd_apply(acc, tmp, bddop_imp));

	fact_.declare_acceptance_condition(acc, node);
	res_ = node->is_negated() ?
	  bdd_nithvar(vp.first) : bdd_ithvar(vp.first);
      }

      bdd
      recurse(const formula* f, bool root = false)
      {
	eltl_trad_visitor v(fact_, root);
	f->accept(v);
	return v.result();
      }

    private:
      bdd res_;
      tgba_bdd_concrete_factory& fact_;
      bool root_;

      /// BDD associated to each automatop A representing finish(A).
      typedef Sgi::hash_map<const ltl::formula*, bdd,
			    ltl::formula_ptr_hash> finish_map_;

      finish_map_ finish_;

      // Table containing the two now variables associated with each state.
      // TODO: a little documentation about that.
      typedef Sgi::hash_map<
	const nfa::state*, std::pair<int, int>, ptr_hash<nfa::state> > nmap;

      std::pair<int, int>&
      recurse_state(const nfa::ptr& nfa, const nfa::state* s,
		    const std::vector<formula*>& v,
		    nmap& m, bdd& acc, bdd& finish)
      {
	bool is_loop = nfa->is_loop();
	nmap::iterator it;
	it = m.find(s);

	int v1 = 0;
	int v2 = 0;
	if (it != m.end())
	  return it->second;
	else
	{
	  v1 = fact_.create_anonymous_state();
	  v2 = fact_.create_anonymous_state();
	  m[s] = std::make_pair(v1, v2);
	}

	bdd tmp1 = bddfalse;
	bdd tmp2 = bddfalse;
	bdd tmpacc = bddfalse;
	for (nfa::iterator i = nfa->begin(s); i != nfa->end(s); ++i)
	{
	  const formula* lbl = formula_tree::instanciate((*i)->lbl, v);
	  bdd f = recurse(lbl);
	  lbl->destroy();
	  if (nfa->is_final((*i)->dst))
	  {
	    tmp1 |= f;
	    tmp2 |= f;
	    tmpacc |= f;
	    finish |= bdd_ithvar(v1) & f;
	  }
	  else
	  {
	    std::pair<int, int> vp =
	      recurse_state(nfa, (*i)->dst, v, m, acc, finish);
	    tmp1 |= (f & bdd_ithvar(vp.first + 1));
	    tmp2 |= (f & bdd_ithvar(vp.second + 1));
	    if (is_loop)
	      tmpacc |= f;
	  }
	}
	fact_.constrain_relation(bdd_apply(bdd_ithvar(v1), tmp1, bddop_biimp));
	if (is_loop)
	{
	  acc &= bdd_ithvar(v2) | !tmpacc;
	  fact_.constrain_relation(
	    bdd_apply(bdd_ithvar(v2), tmp2, bddop_invimp));
	}
	else
	{
	  acc &= bdd_nithvar(v2) | tmpacc;
	  fact_.constrain_relation(bdd_apply(bdd_ithvar(v2), tmp2, bddop_imp));
	}

	return m[s];
      }
    };
  } // anonymous

  tgba_bdd_concrete*
  eltl_to_tgba_lacim(const ltl::formula* f, bdd_dict* dict)
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
    eltl_trad_visitor v(fact, true);
    f2->accept(v);
    f2->destroy();
    fact.finish();

    // Finally setup the resulting automaton.
    return new tgba_bdd_concrete(fact, v.result());
  }
}
