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

#ifndef SPOT_TGBA_FUTURECONDCOL_HH
# define SPOT_TGBA_FUTURECONDCOL_HH

#include "tgbascc.hh"

namespace spot
{

  /// \brief Wrap a tgba to offer information about upcoming conditions.
  /// \ingroup tgba
  ///
  /// This class is a spot::tgba wrapper that simply add a new method,
  /// future_conditions(), to any spot::tgba.
  ///
  /// This new method returns a set of conditions that can be
  /// seen on a transitions accessible (maybe indirectly) from
  /// the given state.
  class future_conditions_collector : public tgba_scc
  {
  public:
    typedef scc_map::cond_set cond_set;
    typedef std::vector<cond_set> fc_map;

    /// \brief Create a future_conditions_collector wrapper for \a aut.
    ///
    /// If \a show is set to true, then the format_state() method will
    /// include the set of conditions computed for the given state in
    /// its output string.
    future_conditions_collector(const tgba* aut, bool show = false);
    virtual ~future_conditions_collector();

    /// Returns the set of future conditions visible after \a s
    const cond_set& future_conditions(const spot::state* s) const;

    /// \brief Format a state for output.
    ///
    /// If the constructor was called with \a show set to true, then
    /// this method will include the set of conditions computed for \a
    /// state by future_conditions() in the output string.
    virtual std::string format_state(const state* state) const;

  protected:
    void map_builder_(unsigned s);

    fc_map future_conds_;	// The map of future conditions for each
				// strongly connected component.
  };

}

#endif // SPOT_TGBA_FUTURECONDCOL_HH
