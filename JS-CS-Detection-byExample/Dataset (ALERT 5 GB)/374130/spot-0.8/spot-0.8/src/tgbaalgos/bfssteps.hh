// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_BFSSTEPS_HH
# define SPOT_TGBAALGOS_BFSSTEPS_HH

#include <map>
#include "tgba/state.hh"
#include "emptiness.hh"

namespace spot
{
  /// \brief Make a BFS in a spot::tgba to compute a tgba_run::steps.
  /// \ingroup tgba_misc
  ///
  /// This class should be used to compute the shortest path
  /// between a state of a spot::tgba and the first transition or
  /// state that matches some conditions.
  ///
  /// These conditions should be specified by defining bfs_steps::match()
  /// in a subclass.  Also the search can be restricted to some set of
  /// states with a proper definition of bfs_steps::filter().
  class bfs_steps
  {
  public:
    bfs_steps(const tgba* a);
    virtual ~bfs_steps();

    /// \brief Start the search from \a start, and append the
    /// resulting path (if any) to \a l.
    ///
    /// \return the destination state of the last step (not included
    /// in \a l) if a matching path was found, or 0 otherwise.
    const state* search(const state* start, tgba_run::steps& l);

    /// \brief Return a state* that is unique for \a a.
    ///
    /// bfs_steps does not do handle the memory for the states it
    /// generates, this is the job of filter().  Here \a s is a new
    /// state* that search() has just allocated (using
    /// tgba_succ_iterator::current_state()), and the return of this
    /// function should be a state* that does not need to be freed by
    /// search().
    ///
    /// If you already have a map or a set which uses states as keys,
    /// you should probably arrange for filter() to return these keys,
    /// and destroy \a s.  Otherwise you will have to define such a
    /// set, just to be able to destroy all the state* in a subclass.
    ///
    /// This function can return 0 if the given state should not be
    /// explored.
    virtual const state* filter(const state* s) = 0;

    /// \brief Whether a new transition completes a path.
    ///
    /// This function is called immediately after each call to
    /// filter() that does not return 0.
    ///
    /// \param step the source state (as returned by filter()), and the
    ///             labels of the outgoing transition
    /// \param dest the destination state (as returned by filter())
    /// \return true iff a path that included this step should be accepted.
    ///
    /// The search() algorithms stops as soon as match() returns true,
    /// and when this happens the list argument of search() is be
    /// augmented with the shortest past that ends with this
    /// transition.
    virtual bool match(tgba_run::step& step, const state* dest) = 0;

    /// \brief Append the resulting path to the resulting run.
    ///
    /// This is called after match() has returned true, to append the
    /// resulting path to \a l.  This seldom needs to be overridden,
    /// unless you do not want \a l to be updated (in which case an empty
    /// finalize() will do).
    virtual void finalize(const std::map<const state*, tgba_run::step,
			                 state_ptr_less_than>& father,
			  const tgba_run::step& s,
			  const state* start,
			  tgba_run::steps& l);

  protected:
    const tgba* a_;		///< The spot::tgba we are searching into.
  };


}

#endif // SPOT_TGBAALGOS_BFSSTEPS_HH
