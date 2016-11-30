// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#ifndef SPOT_TGBA_TGBATBA_HH
# define SPOT_TGBA_TGBATBA_HH

#include <list>
#include "tgba.hh"
#include "misc/bddlt.hh"

namespace spot
{

  /// \brief Degeneralize a spot::tgba on the fly, producing a TBA.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// This class acts as a proxy in front of a spot::tgba, that should
  /// be degeneralized on the fly.  The result is still a spot::tgba,
  /// but it will always have exactly one acceptance condition so
  /// it could be called TBA (without the G).
  ///
  /// The degeneralization is done by synchronizing the input
  /// automaton with a "counter" automaton such as the one shown in
  /// "On-the-fly Verification of Linear Temporal Logic" (Jean-Michel
  /// Couveur, FME99).
  ///
  /// If the input automaton uses N acceptance conditions, the output
  /// automaton can have at most max(N,1) times more states and
  /// transitions.
  ///
  /// \see tgba_sba_proxy
  class tgba_tba_proxy : public tgba
  {
  public:
    tgba_tba_proxy(const tgba* a);

    virtual ~tgba_tba_proxy();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;

    virtual std::string format_state(const state* state) const;

    virtual state* project_state(const state* s, const tgba* t) const;

    virtual std::string
    transition_annotation(const tgba_succ_iterator* t) const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    typedef std::list<bdd> cycle_list;
  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

    cycle_list acc_cycle_;
  private:
    const tgba* a_;
    bdd the_acceptance_cond_;
    // Disallow copy.
    tgba_tba_proxy(const tgba_tba_proxy&);
    tgba_tba_proxy& operator=(const tgba_tba_proxy&);
  };

  /// \brief Degeneralize a spot::tgba on the fly, producing an SBA.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// This class acts as a proxy in front of a spot::tgba, that should
  /// be degeneralized on the fly.
  ///
  /// This is similar to tgba_tba_proxy, except that automata produced
  /// with this algorithms can also been see as State-based Büchi
  /// Automata (SBA).  See tgba_sba_proxy::state_is_accepting().  (An
  /// SBA is a TBA, and a TBA is a TGBA.)
  ///
  /// This extra property has a small cost in size: if the input
  /// automaton uses N acceptance conditions, the output automaton can
  /// have at most max(N,1)+1 times more states and transitions.
  /// (This is only max(N,1) for tgba_tba_proxy.)
  class tgba_sba_proxy : public tgba_tba_proxy
  {
  public:
    tgba_sba_proxy(const tgba* a);

    /// \brief Whether the state is accepting.
    ///
    /// A particularity of a spot::tgba_sba_proxy automaton is that
    /// when a state has an outgoing accepting arc, all its outgoing
    /// arcs are accepting.  The state itself can therefore be
    /// considered accepting.  This is useful in algorithms working on
    /// degeneralized automata with state acceptance conditions.
    bool state_is_accepting(const state* state) const;
  };

}
#endif // SPOT_TGBA_TGBATBA_HH
