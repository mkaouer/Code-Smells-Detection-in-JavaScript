// Copyright (C) 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_TGBA_TGBAPROXY_HH
# define SPOT_TGBA_TGBAPROXY_HH

#include "tgba.hh"

namespace spot
{
  /// \ingroup tgba_on_the_fly_algorithms
  /// \brief A TGBA proxy.
  ///
  /// This implements a simple proxy to an existing
  /// TGBA, forwarding all methods to the original.
  /// By itself this class is pointless: better use the
  /// original automaton right away.  However it is useful
  /// to inherit from this class and override some of its
  /// methods to implement some on-the-fly algorithm.
  class SPOT_API tgba_proxy: public tgba
  {
  protected:
    tgba_proxy(const tgba* original);

  public:
    virtual ~tgba_proxy();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;

    virtual std::string format_state(const state* state) const;

    virtual std::string
    transition_annotation(const tgba_succ_iterator* t) const;

    virtual state* project_state(const state* s, const tgba* t) const;

    virtual bdd all_acceptance_conditions() const;

    virtual bdd neg_acceptance_conditions() const;

  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

    const tgba* original_;
  };
}

#endif // SPOT_TGBA_TGBA_HH
