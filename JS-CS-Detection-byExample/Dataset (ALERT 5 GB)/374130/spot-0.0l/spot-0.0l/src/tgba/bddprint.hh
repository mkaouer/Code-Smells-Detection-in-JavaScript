// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_BDDPRINT_HH
# define SPOT_TGBA_BDDPRINT_HH

#include <string>
#include <iostream>
#include "bdddict.hh"
#include <bdd.h>

namespace spot
{

  /// \brief Print a BDD as a list of literals.
  ///
  /// This assumes that \a b is a conjunction of literals.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  std::ostream& bdd_print_sat(std::ostream& os,
			      const bdd_dict* dict, bdd b);

  /// \brief Format a BDD as a list of literals.
  ///
  /// This assumes that \a b is a conjunction of literals.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  /// \return The BDD formated as a string.
  std::string bdd_format_sat(const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a list of acceptance conditions.
  ///
  /// This is used when saving a TGBA.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  /// \return The BDD formated as a string.
  std::ostream& bdd_print_acc(std::ostream& os,
			      const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a set of acceptance conditions.
  ///
  /// This is used when saving a TGBA.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  /// \return The BDD formated as a string.
  std::ostream& bdd_print_accset(std::ostream& os,
				 const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a set.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  std::ostream& bdd_print_set(std::ostream& os,
			      const bdd_dict* dict, bdd b);
  /// \brief Format a BDD as a set.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  /// \return The BDD formated as a string.
  std::string bdd_format_set(const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a formula.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  std::ostream& bdd_print_formula(std::ostream& os,
				  const bdd_dict* dict, bdd b);
  /// \brief Format a BDD as a formula.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  /// \return The BDD formated as a string.
  std::string bdd_format_formula(const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a diagram in dotty format.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  std::ostream& bdd_print_dot(std::ostream& os,
			      const bdd_dict* dict, bdd b);

  /// \brief Print a BDD as a table.
  /// \param os The output stream.
  /// \param dict The dictionary to use, to lookup variables.
  /// \param b The BDD to print.
  std::ostream& bdd_print_table(std::ostream& os,
				const bdd_dict* dict, bdd b);

}

#endif // SPOT_TGBA_BDDPRINT_HH
