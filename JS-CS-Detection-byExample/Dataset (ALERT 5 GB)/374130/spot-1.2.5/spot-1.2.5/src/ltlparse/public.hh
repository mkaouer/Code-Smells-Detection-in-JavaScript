// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2011, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#ifndef SPOT_LTLPARSE_PUBLIC_HH
# define SPOT_LTLPARSE_PUBLIC_HH

# include "ltlast/formula.hh"
# include "misc/location.hh"
# include "ltlenv/defaultenv.hh"
# include <string>
# include <list>
# include <utility>
# include <iosfwd>

namespace spot
{
  namespace ltl
  {
    /// \addtogroup ltl_io
    /// @{

#ifndef SWIG
    /// \brief A parse diagnostic with its location.
    typedef std::pair<location, std::string> parse_error;
    /// \brief A list of parser diagnostics, as filled by parse.
    typedef std::list<parse_error> parse_error_list;
#else
    // Turn parse_error_list into an opaque type for Swig.
    struct parse_error_list {};
#endif

    /// \brief Build a formula from an LTL string.
    /// \param ltl_string The string to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \param lenient When true, parenthesized blocks that cannot be
    ///                parsed as subformulas will be considered as
    ///                atomic propositions.
    /// \return A pointer to the formula built from \a ltl_string, or
    ///        0 if the input was unparsable.
    ///
    /// Note that the parser usually tries to recover from errors.  It can
    /// return a non zero value even if it encountered error during the
    /// parsing of \a ltl_string.  If you want to make sure \a ltl_string
    /// was parsed succesfully, check \a error_list for emptiness.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse(const std::string& ltl_string,
			 parse_error_list& error_list,
			 environment& env = default_environment::instance(),
			 bool debug = false,
			 bool lenient = false);

    /// \brief Build a Boolean formula from a string.
    /// \param ltl_string The string to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \param lenient When true, parenthesized blocks that cannot be
    ///                parsed as subformulas will be considered as
    ///                atomic propositions.
    /// \return A pointer to the formula built from \a ltl_string, or
    ///        0 if the input was unparsable.
    ///
    /// Note that the parser usually tries to recover from errors.  It can
    /// return a non zero value even if it encountered error during the
    /// parsing of \a ltl_string.  If you want to make sure \a ltl_string
    /// was parsed succesfully, check \a error_list for emptiness.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse_boolean(const std::string& ltl_string,
				 parse_error_list& error_list,
				 environment& env =
				 default_environment::instance(),
				 bool debug = false,
				 bool lenient = false);

    /// \brief Build a formula from an LTL string in LBT's format.
    /// \param ltl_string The string to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \return A pointer to the formula built from \a ltl_string, or
    ///        0 if the input was unparsable.
    ///
    /// Note that the parser usually tries to recover from errors.  It can
    /// return an non zero value even if it encountered error during the
    /// parsing of \a ltl_string.  If you want to make sure \a ltl_string
    /// was parsed succesfully, check \a error_list for emptiness.
    ///
    /// The LBT syntax, also used by the lbtt and scheck tools, is
    /// extended to support W, and M operators (as done in lbtt), and
    /// double-quoted atomic propositions that do not start with 'p'.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse_lbt(const std::string& ltl_string,
			     parse_error_list& error_list,
			     environment& env = default_environment::instance(),
			     bool debug = false);

    /// \brief Build a formula from a string representing a SERE.
    /// \param sere_string The string to parse.
    /// \param error_list A list that will be filled with
    ///        parse errors that occured during parsing.
    /// \param env The environment into which parsing should take place.
    /// \param debug When true, causes the parser to trace its execution.
    /// \param lenient When true, parenthesized blocks that cannot be
    ///                parsed as subformulas will be considered as
    ///                atomic propositions.
    /// \return A pointer to the formula built from \a sere_string, or
    ///        0 if the input was unparsable.
    ///
    /// Note that the parser usually tries to recover from errors.  It can
    /// return an non zero value even if it encountered error during the
    /// parsing of \a ltl_string.  If you want to make sure \a ltl_string
    /// was parsed succesfully, check \a error_list for emptiness.
    ///
    /// \warning This function is not reentrant.
    SPOT_API
    const formula* parse_sere(const std::string& sere_string,
			      parse_error_list& error_list,
			      environment& env =
			      default_environment::instance(),
			      bool debug = false,
			      bool lenient = false);

    /// \brief Format diagnostics produced by spot::ltl::parse
    ///        or spot::ltl::ratexp
    ///
    /// If the string is utf8 encoded, spot::ltl::fix_utf8_locations()
    /// will be used to report correct utf8 locations (assuming the
    /// output is utf8 aware).  Nonetheless, the supplied \a
    /// error_list will not be modified.
    ///
    /// \param os Where diagnostics should be output.
    /// \param input_string The string that were parsed.
    /// \param error_list The error list filled by spot::ltl::parse
    ///        or spot::ltl::parse_sere while parsing \a input_string.
    /// \return \c true iff any diagnostic was output.
    SPOT_API
    bool format_parse_errors(std::ostream& os,
			     const std::string& input_string,
			     const parse_error_list& error_list);

    /// \brief Fix location of diagnostics assuming the input is utf8.
    ///
    /// The spot::ltl::parse() and spot::ltl::parse_sere() function
    /// return a parse_error_list that contain locations specified at
    /// the byte level.  Although these parser recognize some
    /// utf8 characters they only work byte by byte and will report
    /// positions by counting byte.
    ///
    /// This function fixes the positions returned by the parser to
    /// look correct when the string is interpreted as a utf8-encoded
    /// string.
    ///
    /// It is invalid to call this function on a string that is not
    /// valid utf8.
    ///
    /// You should NOT call this function before calling
    /// spot::ltl::format_parse_errors() because it is already called
    /// inside if needed.  You may need this function only if you want
    /// to write your own error reporting code.
    ///
    /// \param input_string The string that were parsed.
    /// \param error_list The error list filled by spot::ltl::parse
    ///        or spot::ltl::parse_sere while parsing \a input_string.
    SPOT_API
    void
    fix_utf8_locations(const std::string& input_string,
		       parse_error_list& error_list);

    /// @}
  }
}

#endif // SPOT_LTLPARSE_PUBLIC_HH
