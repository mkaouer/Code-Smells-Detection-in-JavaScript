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

#ifndef SPOT_TGBAALGOS_TAU03OPT_HH
# define SPOT_TGBAALGOS_TAU03OPT_HH

#include "misc/optionmap.hh"

namespace spot
{
  class tgba;
  class emptiness_check;

  /// \addtogroup emptiness_check_algorithms
  /// @{

  /// \brief Returns an emptiness checker on the spot::tgba automaton \a a.
  ///
  /// \pre The automaton \a a must have at least one acceptance condition.
  ///
  /// During the visit of \a a, the returned checker stores explicitely all
  /// the traversed states. The implemented algorithm is the following:
  ///
  /// \verbatim
  /// procedure check ()
  /// begin
  ///   weight = 0; // the null vector
  ///   call dfs_blue(s0);
  /// end;
  ///
  /// procedure dfs_blue (s)
  /// begin
  ///   s.color = cyan;
  ///   s.acc = emptyset;
  ///   s.weight = weight;
  ///   for all t in post(s) do
  ///     let (s, l, a, t) be the edge from s to t;
  ///     if t.color == white then
  ///       for all b in a do
  ///         weight[b] = weight[b] + 1;
  ///       end for;
  ///       call dfs_blue(t);
  ///       for all b in a do
  ///         weight[b] = weight[b] - 1;
  ///       end for;
  ///     end if;
  ///     Acc = s.acc U a;
  ///     if t.color == cyan &&
  ///               (Acc U support(weight - t.weight) U t.acc) == all_acc then
  ///       report a cycle;
  ///     else if Acc not included in t.acc then
  ///       t.acc := t.acc U Acc;
  ///       call dfs_red(t, Acc);
  ///     end if;
  ///   end for;
  ///   s.color = blue;
  /// end;
  ///
  /// procedure dfs_red(s, Acc)
  /// begin
  ///   for all t in post(s) do
  ///     let (s, l, a, t) be the edge from s to t;
  ///     if t.color == cyan &&
  ///                 (Acc U support(weight - t.weight) U t.acc) == all_acc then
  ///       report a cycle;
  ///     else if t.color != white and Acc not included in t.acc then
  ///       t.acc := t.acc U Acc;
  ///       call dfs_red(t, Acc);
  ///     end if;
  ///   end for;
  /// end;
  /// \endverbatim
  ///
  /// This algorithm is a generalisation to TGBA of the one implemented in
  /// spot::explicit_se05_search. It is based on the acceptance set labelling
  /// of states used in spot::explicit_tau03_search. Moreover, it introduce
  /// a slight optimisation based on vectors of integers counting for each
  /// acceptance condition how many time the condition has been visited in
  /// the path stored in the blue stack. Such a vector is associated to each
  /// state of this stack.
  ///
  emptiness_check* explicit_tau03_opt_search(const tgba *a,
					     option_map o = option_map());

  /// @}
}

#endif // SPOT_TGBAALGOS_TAU03OPT_HH
