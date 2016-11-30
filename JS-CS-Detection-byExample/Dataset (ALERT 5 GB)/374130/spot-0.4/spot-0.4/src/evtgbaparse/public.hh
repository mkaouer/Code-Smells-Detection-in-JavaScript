// Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#ifndef SPOT_EVTGBAPARSE_PUBLIC_HH
# define SPOT_EVTGBAPARSE_PUBLIC_HH

# include "evtgba/explicit.hh"
// Unfortunately Bison 2.3 uses the same guards in all parsers :(
# undef BISON_LOCATION_HH
# undef BISON_POSITION_HH
# include "location.hh"
# include <string>
# include <list>
# include <utility>
# include <iosfwd>

namespace spot
{
  /// \brief A parse diagnostic with its location.
  typedef std::pair<evtgbayy::location, std::string> evtgba_parse_error;
  /// \brief A list of parser diagnostics, as filled by parse.
  typedef std::list<evtgba_parse_error> evtgba_parse_error_list;

  /// \brief Build a spot::evtgba_explicit from a text file.
  /// \param filename The name of the file to parse.
  /// \param error_list A list that will be filled with
  ///        parse errors that occured during parsing.
  /// \param debug When true, causes the parser to trace its execution.
  /// \return A pointer to the evtgba built from \a filename, or
  ///        0 if the file could not be opened.
  ///
  /// Note that the parser usually tries to recover from errors.  It can
  /// return an non zero value even if it encountered error during the
  /// parsing of \a filename.  If you want to make sure \a filename
  /// was parsed succesfully, check \a error_list for emptiness.
  ///
  /// \warning This function is not reentrant.
  evtgba_explicit* evtgba_parse(const std::string& filename,
				evtgba_parse_error_list& error_list,
				bool debug = false);

  /// \brief Format diagnostics produced by spot::evtgba_parse.
  /// \param os Where diagnostics should be output.
  /// \param filename The filename that should appear in the diagnostics.
  /// \param error_list The error list filled by spot::ltl::parse while
  ///        parsing \a ltl_string.
  /// \return \c true iff any diagnostic was output.
  bool format_evtgba_parse_errors(std::ostream& os,
				  const std::string& filename,
				  evtgba_parse_error_list& error_list);
}

#endif // SPOT_EVTGBAPARSE_PUBLIC_HH
