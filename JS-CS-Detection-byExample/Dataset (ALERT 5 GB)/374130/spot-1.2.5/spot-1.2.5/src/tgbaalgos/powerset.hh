// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2013 Laboratoire de Recherche et Développement
// de l'Epita.
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_POWERSET_HH
# define SPOT_TGBAALGOS_POWERSET_HH

# include <list>
# include <map>
# include "tgba/tgbaexplicit.hh"

namespace spot
{

  struct SPOT_API power_map
  {
    typedef std::set<const state*, state_ptr_less_than> power_state;
    typedef std::map<int, power_state> power_map_data;
    typedef Sgi::hash_set<const state*, state_ptr_hash,
			  state_ptr_equal> state_set;

    ~power_map()
    {
      // Release all states.
      state_set::const_iterator i = states.begin();
      while (i != states.end())
	{
	  // Advance the iterator before deleting the key.
	  const state* s = *i;
	  ++i;
	  s->destroy();
	}
    }

    const power_state&
    states_of(int s) const
    {
      return map_.find(s)->second;
    }

    const state*
    canonicalize(const state* s)
    {
      state_set::const_iterator i = states.find(s);
      if (i != states.end())
	{
	  s->destroy();
	  s = *i;
	}
      else
	{
	  states.insert(s);
	}
      return s;
    }

    power_map_data map_;
    state_set states;
  };


  /// \ingroup tgba_misc
  /// \brief Build a deterministic automaton, ignoring acceptance conditions.
  ///
  /// This create a deterministic automaton that recognizes the
  /// same language as \a aut would if its acceptance conditions
  /// were ignored.  This is the classical powerset algorithm.
  ///
  /// If \a pm is supplied it will be filled with the set of original states
  /// associated to each state of the deterministic automaton.
  /// The \a merge argument can be set to false to prevent merging of
  /// transitions.
  //@{
  SPOT_API tgba_explicit_number*
  tgba_powerset(const tgba* aut, power_map& pm, bool merge = true);
  SPOT_API tgba_explicit_number*
  tgba_powerset(const tgba* aut);
  //@}


  /// \brief Determinize a TBA using the powerset construction.
  ///
  /// The input automaton should have at most one acceptance
  /// condition.  Beware that not all Büchi automata can be
  /// determinized, and this procedure does not ensure that the
  /// produced automaton is equivalent to \a aut.
  ///
  /// The construction is adapted from Section 3.2 of:
  /// \verbatim
  /// @InProceedings{	  dax.07.atva,
  ///   author	  = {Christian Dax and Jochen Eisinger and Felix Klaedtke},
  ///   title     = {Mechanizing the Powerset Construction for Restricted
  /// 		     Classes of {$\omega$}-Automata},
  ///   year      = 2007,
  ///   series	  = {Lecture Notes in Computer Science},
  ///   publisher = {Springer-Verlag},
  ///   volume	  = 4762,
  ///   booktitle = {Proceedings of the 5th International Symposium on
  /// 		     Automated Technology for Verification and Analysis
  /// 		     (ATVA'07)},
  ///   editor	  = {Kedar S. Namjoshi and Tomohiro Yoneda and Teruo Higashino
  /// 		     and Yoshio Okamura},
  ///   month     = oct
  /// }
  /// \endverbatim
  /// only adapted to work on TBA rather than BA.
  ///
  /// If \a threshold_states is non null, abort the construction
  /// whenever it would build an automaton that is more than \a
  /// threshold_states time bigger (in term of states) than the
  /// original automaton.
  ///
  /// If \a threshold_cycles is non null, abort the construction
  /// whenever an SCC of the constructed automaton has more than \a
  /// threshold_cycles cycles.
  SPOT_API tgba_explicit_number*
  tba_determinize(const tgba* aut,
		  unsigned threshold_states = 0,
		  unsigned threshold_cycles = 0);

  /// \brief Determinize a TBA and make sure it is correct.
  ///
  /// Apply tba_determinize(), then check that the result is
  /// equivalent.  If it isn't, return the original automaton.
  ///
  /// Only one of \a f or \a neg_aut needs to be supplied.  If
  /// \a neg_aut is not given, it will be built from \a f.
  ///
  /// \param aut the automaton to minimize
  ///
  /// \param threshold_states if non null, abort the construction
  /// whenever it would build an automaton that is more than \a
  /// threshold time bigger (in term of states) than the original
  /// automaton.
  ///
  /// \param threshold_cycles can be used to abort the construction
  /// if the number of cycles in a SCC of the constructed automaton
  /// is bigger than the supplied value.
  ///
  /// \param f the formula represented by the original automaton
  ///
  /// \param neg_aut an automaton representing the negation of \a aut
  ///
  /// \return a new tgba if the automaton could be determinized, \a aut if
  /// the automaton cannot be determinized, 0 if we do not know if the
  /// determinization is correct because neither \a f nor \a neg_aut
  /// were supplied.
  SPOT_API tgba*
  tba_determinize_check(const tgba* aut,
			unsigned threshold_states = 0,
			unsigned threshold_cycles = 0,
			const ltl::formula* f = 0,
			const tgba* neg_aut = 0);

}

#endif // SPOT_TGBAALGOS_POWERSET_HH
