// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_TRANSLATE_HH
# define SPOT_TGBAALGOS_TRANSLATE_HH

#include "postproc.hh"
#include "ltlvisit/simplify.hh"

namespace spot
{
  /// \ingroup tgba_ltl
  /// \brief Translate an LTL formula into an optimized spot::tgba.
  ///
  /// This class implements a three-step translation:
  /// - syntactic simplification of the formula
  /// - translation of the formula into TGBA
  /// - postprocessing of the resulting TGBA to minimize it, or
  ///   turn it into the required form.
  ///
  /// Method set_type() may be used to specify the type of
  /// automaton produced (TGBA, BA, Monitor).  The default is TGBA.
  ///
  /// Method set_pref() may be used to specify whether small automata
  /// should be prefered over deterministic automata.
  ///
  /// Method set_level() may be used to specify the optimization level.
  ///
  /// The semantic of these three methods is inherited from the
  /// spot::postprocessor class, but the optimization level is
  /// additionally used to select which LTL simplifications to enable.
  class SPOT_API translator: protected postprocessor
  {
  public:
    translator(ltl::ltl_simplifier* simpl, const option_map* opt = 0)
      : postprocessor(opt), simpl_(simpl), simpl_owned_(0)
    {
      assert(simpl);
      setup_opt(opt);
    }

    translator(bdd_dict* dict, const option_map* opt = 0)
      : postprocessor(opt)
    {
      build_simplifier(dict);
      setup_opt(opt);
    }

    translator(const option_map* opt = 0)
      : postprocessor(opt)
    {
      build_simplifier(0);
      setup_opt(opt);
    }

    ~translator()
    {
      // simpl_owned_ is 0 if simpl_ was supplied to the constructor.
      delete simpl_owned_;
    }

    typedef postprocessor::output_type output_type;

    void
    set_type(output_type type)
    {
      this->postprocessor::set_type(type);
    }

    typedef postprocessor::output_pref output_pref;

    void
    set_pref(output_pref pref)
    {
      this->postprocessor::set_pref(pref);
    }

    typedef postprocessor::optimization_level optimization_level;

    void
    set_level(optimization_level level)
    {
      level_ = level;
    }

    /// \brief Convert \a f into an automaton.
    ///
    /// The formula \a f is simplified internally, but it is not
    /// not destroyed (this is the responsibility of the caller).
    const tgba* run(const ltl::formula* f);

    /// \brief Convert \a f into an automaton, and update f.
    ///
    /// The formula <code>*f</code> is destroyed, and replaced
    /// by the simplified version, which should be destroyed by
    /// the caller.
    const tgba* run(const ltl::formula** f);

  protected:
    void setup_opt(const option_map* opt);
    void build_simplifier(bdd_dict* dict);

  private:
    ltl::ltl_simplifier* simpl_;
    ltl::ltl_simplifier* simpl_owned_;
    int comp_susp_;
    int early_susp_;
    int skel_wdba_;
    int skel_simul_;
  };
  /// @}
}


#endif // SPOT_TGBAALGOS_TRANSLATE_HH
