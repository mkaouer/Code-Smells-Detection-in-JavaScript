// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
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

#ifndef SPOT_LTLVISIT_TOSTRING_HH
# define SPOT_LTLVISIT_TOSTRING_HH

#include <ltlast/formula.hh>
#include <iosfwd>

namespace spot
{
  namespace ltl
  {
    /// \addtogroup ltl_io
    /// @{

    /// \brief Output a formula as a string which is parsable unless the formula
    /// contains automaton operators (used in ELTL formulae).
    /// \param f The formula to translate.
    /// \param os The stream where it should be output.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    /// \param ratexp Whether we are printing a SERE.
    std::ostream&
    to_string(const formula* f, std::ostream& os, bool full_parent = false,
	      bool ratexp = false);

    /// \brief Output a formula as a string which is parsable unless the formula
    /// contains automaton operators (used in ELTL formulae).
    /// \param f The formula to translate.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    /// \param ratexp Whether we are printing a SERE.
    std::string
    to_string(const formula* f, bool full_parent = false, bool ratexp = false);

    /// \brief Output a formula as an utf8 string which is parsable unless
    /// the formula contains automaton operators (used in ELTL formulae).
    /// \param f The formula to translate.
    /// \param os The stream where it should be output.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    /// \param ratexp Whether we are printing a SERE.
    std::ostream&
    to_utf8_string(const formula* f, std::ostream& os, bool full_parent = false,
		   bool ratexp = false);

    /// \brief Output a formula as an utf8 string which is parsable
    /// unless the formula contains automaton operators (used in ELTL formulae).
    /// \param f The formula to translate.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    /// \param ratexp Whether we are printing a SERE.
    std::string
    to_utf8_string(const formula* f, bool full_parent = false,
		   bool ratexp = false);

    /// \brief Output a formula as a string parsable by Spin.
    /// \param f The formula to translate.
    /// \param os The stream where it should be output.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    std::ostream& to_spin_string(const formula* f, std::ostream& os,
				 bool full_parent = false);

    /// \brief Convert a formula into a string parsable by Spin.
    /// \param f The formula to translate.
    /// \param full_parent Whether or not the string should by fully
    ///			   parenthesized.
    std::string to_spin_string(const formula* f, bool full_parent = false);

    /// \brief Output a formula as a string parsable by Wring.
    /// \param f The formula to translate.
    /// \param os The stream where it should be output.
    std::ostream& to_wring_string(const formula* f, std::ostream& os);

    /// \brief Convert a formula into a string parsable by Wring
    /// \param f The formula to translate.
    std::string to_wring_string(const formula* f);

    /// @}
  }
}

#endif // SPOT_LTLVISIT_TOSTRING_HH
