// Copyright (C) 2009 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_MINATO_HH
# define SPOT_MISC_MINATO_HH

# include <bdd.h>
# include <stack>

namespace spot
{
  /// \brief Generate an irredundant sum-of-products (ISOP) form of a
  /// BDD function.
  /// \ingroup misc_tools
  ///
  /// This algorithm implements a derecursived version the Minato-Morreale
  /// algorithm presented in the following paper.
  /// \verbatim
  /// @InProceedings{   minato.92.sasimi,
  ///   author        = {Shin-ichi Minato},
  ///   title         = {Fast Generation of Irredundant Sum-of-Products Forms
  ///                   from Binary Decision Diagrams},
  ///   booktitle     = {Proceedings of the third Synthesis and Simulation
  ///                   and Meeting International Interchange workshop
  ///                   (SASIMI'92)},
  ///   pages         = {64--73},
  ///   year          = {1992},
  ///   address       = {Kobe, Japan},
  ///   month         = {April}
  /// }
  /// \endverbatim
  class minato_isop
  {
  public:
    /// \brief Conctructor.
    /// \arg input The BDD function to translate in ISOP.
    minato_isop(bdd input);
    /// \brief Conctructor.
    /// \arg input The BDD function to translate in ISOP.
    /// \arg vars  The set of BDD variables to factorize in \a input.
    minato_isop(bdd input, bdd vars);
    /// \brief Compute the next sum term of the ISOP form.
    /// Return \c bddfalse when all terms have been output.
    bdd next();

  private:
    /// Internal variables for minato_isop.
    struct local_vars
    {
      // If you are following the paper, f_min and f_max correspond
      // to the pair of BDD functions used to encode the ternary function f
      // (see Section 3.4).
      // Also note that f0, f0', and f0'' all share the same _max function.
      // Likewise for f1, f1', and f1''.
      bdd f_min, f_max;
      // Because we need a non-recursive version of the algorithm,
      // we had to split it in four steps (each step is separated
      // from the other by a call to ISOP in the original algorithm).
      enum { FirstStep, SecondStep, ThirdStep, FourthStep } step;
      // The list of variables to factorize.  This is an addition to
      // the original algorithm.
      bdd vars;
      bdd v1;
      bdd f0_min, f0_max;
      bdd f1_min, f1_max;
      bdd g0, g1;
      local_vars(bdd f_min, bdd f_max, bdd vars)
	: f_min(f_min), f_max(f_max), step(FirstStep), vars(vars) {}
    };
    std::stack<local_vars> todo_;
    std::stack<bdd> cube_;
    bdd ret_;
  };
}

#endif // SPOT_MISC_MINATO_HH
