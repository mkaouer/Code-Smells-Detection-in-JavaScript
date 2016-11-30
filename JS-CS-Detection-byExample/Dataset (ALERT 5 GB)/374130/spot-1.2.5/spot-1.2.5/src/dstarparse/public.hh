// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_DSTARPARSE_PUBLIC_HH
# define SPOT_DSTARPARSE_PUBLIC_HH

# include "tgba/tgbaexplicit.hh"
# include "misc/location.hh"
# include "ltlenv/defaultenv.hh"
# include <string>
# include <list>
# include <utility>
# include <iosfwd>
# include <misc/bitvect.hh>

namespace spot
{
  /// \addtogroup tgba_io
  /// @{

  /// \brief A parse diagnostic with its location.
  typedef std::pair<spot::location, std::string> dstar_parse_error;
  /// \brief A list of parser diagnostics, as filled by parse.
  typedef std::list<dstar_parse_error> dstar_parse_error_list;

  enum dstar_type { Rabin, Streett };

  /// \brief Temporary encoding of an omega automaton produced by
  /// ltl2dstar.
  struct SPOT_API dstar_aut
  {
    // Transition structure of the automaton.
    // This is encoded as a TGBA without acceptance condition.
    tgba_explicit_number* aut;
    /// Type of the acceptance.
    dstar_type type;
    /// Number of acceptance pairs.
    size_t accpair_count;
    /// \brief acceptance sets encoded as 2*num_state bit-vectors of
    /// num_pairs bits
    ///
    /// Assuming F={(L₀,U₀),…,(Lᵢ,Uᵢ),…},
    /// s∈Lᵢ iff <code>accsets->at(s * 2).get(i)</code>,
    /// s∈Uᵢ iff <code>accsets->at(s * 2 + 1).get(i)</code>.
    bitvect_array* accsets;

    ~dstar_aut()
    {
      delete aut;
      delete accsets;
    }
  };


  /// \brief Build a spot::tgba_explicit from ltl2dstar's output.
  /// \param filename The name of the file to parse.
  /// \param error_list A list that will be filled with
  ///        parse errors that occured during parsing.
  /// \param dict The BDD dictionary where to use.
  /// \param env The environment of atomic proposition into which parsing
  ///        should take place.
  /// \param debug When true, causes the parser to trace its execution.
  /// \return A pointer to the tgba built from \a filename, or
  ///        0 if the file could not be opened.
  ///
  /// Note that the parser usually tries to recover from errors.  It can
  /// return an non zero value even if it encountered error during the
  /// parsing of \a filename.  If you want to make sure \a filename
  /// was parsed succesfully, check \a error_list for emptiness.
  ///
  /// \warning This function is not reentrant.
  SPOT_API dstar_aut*
  dstar_parse(const std::string& filename,
	      dstar_parse_error_list& error_list,
	      bdd_dict* dict,
	      ltl::environment& env = ltl::default_environment::instance(),
	      bool debug = false);

  /// \brief Format diagnostics produced by spot::dstar_parse.
  /// \param os Where diagnostics should be output.
  /// \param filename The filename that should appear in the diagnostics.
  /// \param error_list The error list filled by spot::ltl::parse while
  ///        parsing \a ltl_string.
  /// \return \c true iff any diagnostic was output.
  SPOT_API bool
  format_dstar_parse_errors(std::ostream& os,
			    const std::string& filename,
			    dstar_parse_error_list& error_list);


  /// \brief Convert a non-deterministic Rabin automaton into a
  /// non-deterministic Büchi automaton.
  SPOT_API tgba*
  nra_to_nba(const dstar_aut* nra);

  /// \brief Convert a non-deterministic Rabin automaton into a
  /// non-deterministic Büchi automaton.
  ///
  /// This version simply ignores all states in \a ignore.
  SPOT_API tgba*
  nra_to_nba(const dstar_aut* nra, const state_set* ignore);

  /// \brief Convert a deterministic Rabin automaton into a
  /// Büchi automaton, deterministic when possible.
  ///
  /// See "Deterministic ω-automata vis-a-vis Deterministic Büchi
  /// Automata", S. Krishnan, A. Puri, and R. Brayton (ISAAC'94) for
  /// more details about a DRA->DBA construction.
  ///
  /// We essentially apply this method SCC-wise.  If an SCC is
  /// DBA-realizable, we duplicate it in the output, fixing just
  /// the acceptance states.   If an SCC is not DBA-realizable,
  /// then we apply the more usual conversion from Rabin to NBA
  /// for this part.
  ///
  /// If the optional \a dba_output argument is non-null, the
  /// pointed Boolean will be updated to indicate whether the
  /// returned Büchi automaton is deterministic.
  SPOT_API tgba*
  dra_to_ba(const dstar_aut* dra, bool* dba_output = 0);

  /// \brief Convert a non-deterministic Streett automaton into a
  /// non-deterministic tgba.
  SPOT_API tgba*
  nsa_to_tgba(const dstar_aut* nra);

  /// \brief Convert a Rabin or Streett automaton into a TGBA.
  ///
  /// This function calls dra_to_ba() or nsa_to_tgba().
  SPOT_API tgba*
  dstar_to_tgba(const dstar_aut* dstar);


  /// @}
}

#endif // SPOT_DSTARPARSE_PUBLIC_HH
