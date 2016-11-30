// Copyright (C) 2008, 2010 Laboratoire de Recherche et Développement
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

#ifndef SPOT_ELTLPARSE_PUBLIC_HH
# define SPOT_ELTLPARSE_PUBLIC_HH

# include "ltlast/formula.hh"
// Unfortunately Bison 2.3 uses the same guards in all parsers :(
# undef BISON_LOCATION_HH
# undef BISON_POSITION_HH
# include "ltlenv/defaultenv.hh"
# include "ltlast/nfa.hh"
# include "eltlparse/location.hh"
# include <string>
# include <list>
# include <map>
# include <utility>
# include <iosfwd>

// namespace
// {
//   typedef std::map<std::string, spot::ltl::nfa::ptr> nfamap;
// }

namespace spot
{
  using namespace ltl;

  namespace eltl
  {
    /// \addtogroup ltl_io
    /// @{

    typedef std::pair<std::string, std::string> spair;
    /// \brief A parse diagnostic <location, <file, message>>.
    typedef std::pair<eltlyy::location, spair> parse_error;
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
    formula* parse_file(const std::string& filename,
			parse_error_list& error_list,
			environment& env = default_environment::instance(),
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
    formula* parse_string(const std::string& eltl_string,
			  parse_error_list& error_list,
			  environment& env = default_environment::instance(),
			  bool debug = false);

    /// \brief Format diagnostics produced by spot::eltl::parse.
    /// \param os Where diagnostics should be output.
    /// \param error_list The error list filled by spot::eltl::parse while
    ///        parsing \a eltl_string.
    /// \return \c true iff any diagnostic was output.
    bool
    format_parse_errors(std::ostream& os,
			parse_error_list& error_list);

    /// @}
  }
}

#endif // SPOT_ELTLPARSE_PUBLIC_HH
