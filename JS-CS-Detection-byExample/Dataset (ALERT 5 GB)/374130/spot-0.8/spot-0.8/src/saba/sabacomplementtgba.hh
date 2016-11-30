// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_SABA_SABACOMPLEMENTTGBA_HH
#define SPOT_SABA_SABACOMPLEMENTTGBA_HH

#include <tgba/tgba.hh>
#include <tgba/tgbatba.hh>
#include "saba.hh"

namespace spot
{
  /// \brief Complement a TGBA and produce a SABA.
  /// \ingroup saba
  ///
  /// The original TGBA is transformed into a States-based
  /// Büchi Automaton.
  ///
  /// Several techniques are supposed to by applied on the resulting
  /// automaton before its transformation into a TGBA.  Those techniques
  /// are expected to reduce the size of the automaton.
  ///
  /// This algorithm comes from:
  /// \verbatim
  /// @Article{         gurumurthy.03.lncs,
  ///   title         = {On complementing nondeterministic {B\"uchi} automata},
  ///   author        = {Gurumurthy, S. and Kupferman, O. and Somenzi, F. and
  ///                   Vardi, M.Y.},
  ///   journal       = {Lecture Notes in Computer Science},
  ///   pages         = {96--110},
  ///   year          = {2003},
  ///   publisher     = {Springer-Verlag}
  /// }
  /// \endverbatim
  ///
  /// The construction is done on-the-fly, by the
  /// \c saba_complement_succ_iterator class.
  /// \see saba_complement_succ_iterator
  class saba_complement_tgba : public saba
  {
  public:
    saba_complement_tgba(const tgba* a);
    virtual ~saba_complement_tgba();

    // tgba interface
    virtual saba_state* get_init_state() const;
    virtual saba_succ_iterator*
    succ_iter(const saba_state* local_state) const;

    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const saba_state* state) const;
    virtual bdd all_acceptance_conditions() const;
  private:
    const tgba_sba_proxy* automaton_;
    bdd the_acceptance_cond_;
    unsigned nb_states_;
  }; // end class tgba_saba_complement.

} // end namespace spot.


#endif  // SPOT_SABA_SABACOMPLEMENTTGBA_HH
