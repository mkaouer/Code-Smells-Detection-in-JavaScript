// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#ifndef SPOT_TGBAALGOS_WEIGHT_HH
# define SPOT_TGBAALGOS_WEIGHT_HH

#include <iosfwd>
#include <map>
#include <bdd.h>

namespace spot
{
  /// \brief Manage for a given automaton a vector of counter indexed by
  /// its acceptance condition

  class weight
  {
  public:
    /// Construct a empty vector (all counters set to zero).
    ///
    /// \param neg_all_cond : negation of all the acceptance conditions of
    /// the automaton (the bdd returned by tgba::neg_acceptance_conditions()).
    weight(const bdd& neg_all_cond);
    /// Increment by one the counters of each acceptance condition in \a acc.
    weight& operator+=(const bdd& acc);
    /// Decrement by one the counters of each acceptance condition in \a acc.
    weight& operator-=(const bdd& acc);
    /// Return the set of each acceptance condition such that its counter is
    /// strictly greatest than the corresponding counter in w.
    ///
    /// \pre For each acceptance condition, its counter is greatest or equal to
    /// the corresponding counter in w.
    bdd operator-(const weight& w) const;
    friend std::ostream& operator<<(std::ostream& os, const weight& w);

  private:
    typedef std::map<int, int> weight_vector;
    weight_vector m;
    bdd neg_all_acc;
    static weight_vector* pm;
    static void inc_weight_handler(char* varset, int size);
    static void dec_weight_handler(char* varset, int size);
  };
};

#endif // SPOT_TGBAALGOS_WEIGHT_HH
