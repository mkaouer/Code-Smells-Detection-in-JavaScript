// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2006, 2007 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "contain.hh"
#include "simplify.hh"
#include "tunabbrev.hh"
#include "ltlast/unop.hh"
#include "ltlast/binop.hh"
#include "ltlast/multop.hh"
#include "ltlast/constant.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/save.hh"

namespace spot
{
  namespace ltl
  {

    language_containment_checker::language_containment_checker
      (bdd_dict* dict, bool exprop, bool symb_merge,
       bool branching_postponement, bool fair_loop_approx)
      : dict_(dict), exprop_(exprop), symb_merge_(symb_merge),
      branching_postponement_(branching_postponement),
      fair_loop_approx_(fair_loop_approx)
    {
    }

    language_containment_checker::~language_containment_checker()
    {
      clear();
    }

    void
    language_containment_checker::clear()
    {
      while (!translated_.empty())
	{
	  trans_map::iterator i = translated_.begin();
	  delete i->second.translation;
	  const formula* f = i->first;
	  translated_.erase(i);
	  f->destroy();
	}
    }

    bool
    language_containment_checker::incompatible_(record_* l, record_* g)
    {
      record_::incomp_map::const_iterator i = l->incompatible.find(g);
      if (i != l->incompatible.end())
	return i->second;

      const tgba* p = new tgba_product(l->translation, g->translation);
      emptiness_check* ec = couvreur99(p);
      emptiness_check_result* ecr = ec->check();
      if (!ecr)
	{
	  l->incompatible[g] = true;
	  g->incompatible[l] = true;
	}
      else
	{
	  l->incompatible[g] = false;
	  g->incompatible[l] = false;
	  delete ecr;
	}
      delete ec;
      delete p;
      return !ecr;
    }


    // Check whether L(l) is a subset of L(g).
    bool
    language_containment_checker::contained(const formula* l,
					    const formula* g)
    {
      if (l == g)
	return true;
      record_* rl = register_formula_(l);
      const formula* ng = unop::instance(unop::Not, g->clone());
      record_* rng = register_formula_(ng);
      ng->destroy();
      return incompatible_(rl, rng);
    }

    // Check whether L(!l) is a subset of L(g).
    bool
    language_containment_checker::neg_contained(const formula* l,
						const formula* g)
    {
      if (l == g)
	return false;
      const formula* nl = unop::instance(unop::Not, l->clone());
      record_* rnl = register_formula_(nl);
      const formula* ng = unop::instance(unop::Not, g->clone());
      record_* rng = register_formula_(ng);
      nl->destroy();
      ng->destroy();
      if (nl == g)
	return true;
      return incompatible_(rnl, rng);
    }

    // Check whether L(l) is a subset of L(!g).
    bool
    language_containment_checker::contained_neg(const formula* l,
						const formula* g)
    {
      if (l == g)
	return false;
      record_* rl = register_formula_(l);
      record_* rg = register_formula_(g);
      return incompatible_(rl, rg);
    }

    // Check whether L(l) = L(g).
    bool
    language_containment_checker::equal(const formula* l, const formula* g)
    {
      return contained(l, g) && contained(g, l);
    }

    language_containment_checker::record_*
    language_containment_checker::register_formula_(const formula* f)
    {
      trans_map::iterator i = translated_.find(f);
      if (i != translated_.end())
	return &i->second;

      const tgba_explicit_formula* e =
	ltl_to_tgba_fm(f, dict_, exprop_, symb_merge_,
		       branching_postponement_,
		       fair_loop_approx_);

      record_& r = translated_[f->clone()];
      r.translation = e;
      return &r;
    }


    const formula*
    reduce_tau03(const formula* f, bool stronger)
    {
      if (!f->is_psl_formula())
	return f->clone();

      ltl_simplifier_options opt(false, false, false,
				 true, stronger);
      ltl_simplifier simpl(opt);
      return simpl.simplify(f);
    }
  }
}
