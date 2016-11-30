// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2010, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_ELTLPARSE_PUBLIC_HH
# define SPOT_ELTLPARSE_PUBLIC_HH

# include "ltlast/formula.hh"
# include "ltlenv/defaultenv.hh"
# include "ltlast/nfa.hh"
# include "misc/location.hh"
# include <string>
# include <list>
# include <map>
# include <utility>
# include <iosfwd>

namespace spot
{
  using namespace ltl;

  namespace eltl
  {
    /// \addtogroup ltl_io
    /// @{

    typedef std::pair<std::string, std::string> spair;
    /// \brief A parse diagnostic <location, <file, message>>.
    typedef std::pair<spot::location, spair> parse_error;
    /// \brief A list of parser diagnostics, as filled by parse.
    typedef std::list<parse_error> parse_error_list;

    /// \brief Build a formula from a text file.
    /// \param filename The name of the file to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \return A pointer to the tgba built from \a filename, or
    ///        0 if the file could not be opened.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse_file(const std::string& filename,
			      parse_error_list& error_list,
			      environment& env =
			      default_environment::instance(),
			      bool debug = false);

    /// \brief Build a formula from an ELTL string.
    /// \param eltl_string The string to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \return A pointer to the formula built from \a eltl_string, or
    ///        0 if the input was unparsable.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse_string(const std::string& eltl_string,
				parse_error_list& error_list,
				environment& env =
				default_environment::instance(),
				bool debug = false);

    /// \brief Format diagnostics produced by spot::eltl::parse.
    /// \param os Where diagnostics should be output.
    /// \param error_list The error list filled by spot::eltl::parse while
    ///        parsing \a eltl_string.
    /// \return \c true iff any diagnostic was output.
    SPOT_API
    bool
    format_parse_errors(std::ostream& os,
			parse_error_list& error_list);

    /// @}
  }
}

#endif // SPOT_ELTLPARSE_PUBLIC_HH
