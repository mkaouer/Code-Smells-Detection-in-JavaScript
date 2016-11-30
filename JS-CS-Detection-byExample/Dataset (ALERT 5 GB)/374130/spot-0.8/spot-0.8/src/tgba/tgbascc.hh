// Copyright (C) 2009  Laboratoire de recherche et développement de l'Epita.
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

#ifndef SPOT_TGBA_TGBASCC_HH
# define SPOT_TGBA_TGBASCC_HH

#include "tgba.hh"
#include "tgbaalgos/scc.hh"

namespace spot
{

  /// \brief Wrap a tgba to offer information about strongly connected
  /// components.
  /// \ingroup tgba
  ///
  /// This class is a spot::tgba wrapper that simply add a new method
  /// scc_of_state() to retrieve the number of a SCC a state belongs to.
  class tgba_scc : public tgba
  {
  public:
    /// \brief Create a tgba_scc wrapper for \a aut.
    ///
    /// If \a show is set to true, then the format_state() method will
    /// include the SCC number computed for the given state in its
    /// output string.
    tgba_scc(const tgba* aut, bool show = false);
    virtual ~tgba_scc();

    /// Returns the number of the SCC \a s belongs to.
    unsigned scc_of_state(const spot::state* s) const;

    /// \brief Format a state for output.
    ///
    /// If the constructor was called with \a show set to true, then
    /// this method will include the SCC number computed for \a
    /// state in the output string.
    virtual std::string format_state(const state* state) const;

    // The following methods simply delegate their work to the wrapped
    // tgba.

    virtual state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;

    virtual std::string
    transition_annotation(const tgba_succ_iterator* t) const;
    virtual state* project_state(const state* s, const tgba* t) const;
    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

  protected:
    const tgba* aut_;		// The wrapped TGBA.
    scc_map scc_map_;		// SCC informations.
    bool show_;		        // Wether to show future conditions
				// in the output of format_state().
  };

}

#endif // SPOT_TGBA_TGBASCC_HH
