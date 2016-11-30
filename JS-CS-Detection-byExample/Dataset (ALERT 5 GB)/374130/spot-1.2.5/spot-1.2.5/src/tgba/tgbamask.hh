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

#ifndef SPOT_TGBA_TGBAMASK_HH
# define SPOT_TGBA_TGBAMASK_HH

#include "tgbaproxy.hh"

namespace spot
{

  /// \ingroup tgba_on_the_fly_algorithms
  /// \brief A masked TGBA (abstract).
  ///
  /// Ignores some states from a TGBA.  What state are preserved or
  /// ignored is controlled by the wanted() method.
  ///
  /// This is an abstract class. You should inherit from it and
  /// supply a wanted() method to specify which states to keep.
  class SPOT_API tgba_mask: public tgba_proxy
  {
  protected:
    /// \brief Constructor.
    /// \param masked The automaton to mask
    /// \param init Any state to use as initial state. This state will be
    /// destroyed by the destructor.
    tgba_mask(const tgba* masked, const state* init = 0);

  public:
    virtual ~tgba_mask();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bool wanted(const state* s) const = 0;

  protected:
    const state* init_;
  };

  /// \ingroup tgba_on_the_fly_algorithms
  /// \brief Mask a TGBA, keeping a given set of states.
  ///
  /// Mask the TGBA \a to_mask, keeping only the
  /// states from \a to_keep.  The initial state
  /// can optionally be reset to \a init.
  SPOT_API const tgba*
  build_tgba_mask_keep(const tgba* to_mask,
		       const state_set& to_keep,
		       const state* init = 0);

  /// \ingroup tgba_on_the_fly_algorithms
  /// \brief Mask a TGBA, rejecting a given set of states.
  ///
  /// Mask the TGBA \a to_mask, keeping only the states that are not
  /// in \a to_ignore.  The initial state can optionally be reset to
  /// \a init.
  SPOT_API const tgba*
  build_tgba_mask_ignore(const tgba* to_mask,
			 const state_set& to_ignore,
			 const state* init = 0);

}

#endif // SPOT_TGBA_TGBAMASK_HH
