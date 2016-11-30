// Copyright (C) 2011 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE)
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


#ifndef SPOT_KRIPKEPARSE_PUBLIC_HH
# define SPOT_KRIPKEPARSE_PUBLIC_HH

# include "kripke/kripkeexplicit.hh"
// Unfortunately Bison 2.3 uses the same guards in all parsers :(
# undef BISON_LOCATION_HH
# undef BISON_POSITION_HH
# include "kripkeparse/location.hh"
# include "ltlenv/defaultenv.hh"
# include <string>
# include <list>
# include <utility>
# include <iosfwd>

namespace spot
{

  /// \brief A parse diagnostic with its location.
  typedef std::pair<kripkeyy::location, std::string> kripke_parse_error;
  /// \brief A list of parser diagnostics, as filled by parse.
  typedef std::list<kripke_parse_error> kripke_parse_error_list;



  kripke_explicit*
  kripke_parse(const std::string& name,
               kripke_parse_error_list& error_list,
               bdd_dict* dict,
               ltl::environment& env
               = ltl::default_environment::instance(),
               bool debug = false);


  /// \brief Format diagnostics produced by spot::kripke_parse.
  /// \param os Where diagnostics should be output.
  /// \param filename The filename that should appear in the diagnostics.
  /// \param error_list The error list filled by spot::ltl::parse while
  ///        parsing \a ltl_string.
  /// \return \c true if any diagnostic was output.
  bool format_kripke_parse_errors(std::ostream& os,
                                  const std::string& filename,
                                  kripke_parse_error_list& error_list);

}


#endif /* !SPOT_KRIPKEPARSE_PUBLIC_HH_ */
