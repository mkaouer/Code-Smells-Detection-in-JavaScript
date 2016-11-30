// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_DOTTYDEC_HH
# define SPOT_TGBAALGOS_DOTTYDEC_HH

#include <string>

namespace spot
{
  class state;
  class tgba;
  class tgba_succ_iterator;

  /// \addtogroup tgba_dotty Decorating the dot output
  /// \ingroup tgba_io

  /// \brief Choose state and link styles for spot::dotty_reachable.
  /// \ingroup tgba_dotty
  class dotty_decorator
  {
  public:
    virtual ~dotty_decorator();

    /// \brief Compute the style of a state.
    ///
    /// This function should output a string of the form
    /// <code>[label="foo", style=bar, ...]</code>.  The
    /// default implementation will simply output <code>[label="LABEL"]</code>
    /// with <code>LABEL</code> replaced by the value of \a label.
    ///
    /// \param a the automaton being drawn
    /// \param s the state being drawn (owned by the caller)
    /// \param n a unique number for this state
    /// \param si an iterator over the successors of this state (owned by the
    ///           caller, but can be freely iterated)
    /// \param label the computed name of this state
    virtual std::string state_decl(const tgba* a, const state* s, int n,
				   tgba_succ_iterator* si,
				   const std::string& label);

    /// \brief Compute the style of a link.
    ///
    /// This function should output a string of the form
    /// <code>[label="foo", style=bar, ...]</code>.  The
    /// default implementation will simply output <code>[label="LABEL"]</code>
    /// with <code>LABEL</code> replaced by the value of \a label.
    ///
    /// \param a the automaton being drawn
    /// \param in_s the source state of the transition being drawn
    ///             (owned by the caller)
    /// \param in the unique number associated to \a in_s
    /// \param out_s the destination state of the transition being drawn
    ///             (owned by the caller)
    /// \param out the unique number associated to \a out_s
    /// \param si an iterator over the successors of \a in_s, pointing to
    ///          the current transition (owned by the caller and cannot
    ///        be iterated)
    /// \param label the computed name of this state
    virtual std::string link_decl(const tgba* a,
				  const state* in_s, int in,
				  const state* out_s, int out,
				  const tgba_succ_iterator* si,
				  const std::string& label);

    /// Get the unique instance of the default dotty_decorator.
    static dotty_decorator* instance();
  protected:
    dotty_decorator();
  };
}

#endif // SPOT_TGBAALGOS_DOTTYDEC_HH
