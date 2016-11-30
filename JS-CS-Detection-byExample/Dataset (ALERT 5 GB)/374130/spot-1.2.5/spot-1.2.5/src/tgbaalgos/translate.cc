// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include "translate.hh"
#include "ltl2tgba_fm.hh"
#include "compsusp.hh"
#include "misc/optionmap.hh"

namespace spot
{

  void translator::setup_opt(const option_map* opt)
  {
    comp_susp_ = early_susp_ = skel_wdba_ = skel_simul_ = 0;

    if (!opt)
      return;

    comp_susp_ = opt->get("comp-susp", 0);
    if (comp_susp_ == 1)
      {
	early_susp_ = opt->get("early-susp", 0);
	skel_wdba_ = opt->get("skel-wdba", -1);
	skel_simul_ = opt->get("skel-simul", 1);
      }
  }

  void translator::build_simplifier(bdd_dict* dict)
  {
    ltl::ltl_simplifier_options options(false, false, false);
    switch (level_)
      {
      case High:
	options.containment_checks = true;
	options.containment_checks_stronger = true;
	// fall through
      case Medium:
	options.synt_impl = true;
	// fall through
      case Low:
	options.reduce_basics = true;
	options.event_univ = true;
	// fall through
      }
    simpl_owned_ = simpl_ = new ltl::ltl_simplifier(options, dict);
  }

  const tgba* translator::run(const ltl::formula** f)
  {
    const ltl::formula* r = simpl_->simplify(*f);
    (*f)->destroy();
    *f = r;

    // This helps ltl_to_tgba_fm() to order BDD variables in a more
    // natural way (improving the degeneralization).
    simpl_->clear_as_bdd_cache();

    const tgba* aut;
    if (comp_susp_ > 0)
      {
	int skel_wdba = skel_wdba_;
	if (skel_wdba < 0)
	  skel_wdba = (pref_ == spot::postprocessor::Deterministic) ? 1 : 2;

	aut = compsusp(r, simpl_->get_dict(), skel_wdba == 0,
		       skel_simul_ == 0, early_susp_ != 0,
		       comp_susp_ == 2, skel_wdba == 2, false);
      }
    else
      {
	bool exprop = level_ == spot::postprocessor::High;
	aut = ltl_to_tgba_fm(r, simpl_->get_dict(), exprop);
      }
    aut = this->postprocessor::run(aut, r);
    return aut;
  }

  const tgba* translator::run(const ltl::formula* f)
  {
    f->clone();
    const tgba* aut = run(&f);
    f->destroy();
    return aut;
  }

}
