// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2012, 2013 Laboratoire de Recherche et
// Dévelopment de l'Epita (LRDE).
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

#ifndef SPOT_TAALGOS_EMPTINESSTA_HH
# define SPOT_TAALGOS_EMPTINESSTA_HH

#include "ta/taproduct.hh"
#include "misc/optionmap.hh"
#include "tgbaalgos/gtec/nsheap.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include <stack>
#include <queue>

namespace spot
{

  namespace
  {
    typedef std::pair<spot::state*, ta_succ_iterator_product*> pair_state_iter;
  }

  /// \addtogroup ta_emptiness_check Emptiness-checks
  /// \ingroup ta_algorithms
  ///
  /// \brief Check whether the language of a product (spot::ta_product) between
  ///  a Kripke structure and a TA  is empty. It works also for the product
  /// using Generalized TA (GTA and SGTA).
  ///
  /// you should call spot::ta_check::check() to check the product automaton.
  /// If spot::ta_check::check() returns false, then the product automaton
  /// was found empty.  Otherwise the automaton accepts some run.
  ///
  /// This is based on the following paper.
  /** \verbatim
       @InProceedings{        geldenhuys.06.spin,
       author        = {Jaco Geldenhuys and Henri Hansen},
       title         = {Larger Automata and Less Work for {LTL} Model Checking},
       booktitle     = {Proceedings of the 13th International SPIN Workshop
                       (SPIN'06)},
       year          = {2006},
       pages         = {53--70},
       series        = {Lecture Notes in Computer Science},
       volume        = {3925},
       publisher     = {Springer}
      }
      \endverbatim */
  ///
  /// the implementation of spot::ta_check::check() is inspired from the
  /// two-pass algorithm of the paper above:
  /// - the fist-pass detect all Buchi-accepting cycles and includes
  ///  the heuristic proposed in the paper to detect some
  /// livelock-accepting cycles.
  /// - the second-pass detect all livelock-accepting cycles.
  /// In addition, we add some optimizations to the fist pass:
  /// 1- Detection of all cycles containing a least
  /// one state that is both livelock and Buchi accepting states
  /// 2- Detection of all livelock-accepting cycles containing a least
  /// one state (k,t) such as its "TA component" t is a livelock-accepting
  /// state that has no successors in the TA automaton.
  ///
  /// The implementation of the algorithm of each pass is a SCC-based algorithm
  /// inspired from spot::gtec.hh.
  /// @{

  /// \brief An implementation of the emptiness-check algorithm for a product
  /// between a TA and a Kripke structure
  ///
  /// See the paper cited above.
  class SPOT_API ta_check : public ec_statistics
  {
  public:
    ta_check(const ta_product* a, option_map o = option_map());
    virtual
    ~ta_check();

    /// \brief Check whether the TA product automaton contains an accepting run:
    /// it detects the two kinds of accepting runs: Buchi-accepting runs
    /// and livelock-accepting runs. This emptiness check algorithm can also
    /// check a product using the generalized form of TA.
    ///
    /// Return false if the product automaton accepts no run, otherwise true
    ///
    /// \param disable_second_pass is used to disable the second pass when
    /// when it is not necessary, for example when all the livelock-accepting
    /// states of the TA automaton have no successors, we call this kind of
    /// TA as STA (Single-pass Testing Automata)
    /// (see spot::tgba2ta::add_artificial_livelock_accepting_state() for an
    /// automatic transformation of any TA automaton into STA automaton
    ///
    /// \param disable_heuristic_for_livelock_detection disable the heuristic
    /// used in the first pass to detect livelock-accepting runs,
    /// this heuristic is described in the paper cited above
    virtual bool
    check(bool disable_second_pass = false,
	  bool disable_heuristic_for_livelock_detection = false);

    /// \brief Check whether the product automaton contains
    /// a livelock-accepting run
    /// Return false if the product automaton accepts no livelock-accepting run,
    /// otherwise true
    virtual bool
    livelock_detection(const ta_product* t);

    /// Print statistics, if any.
    virtual std::ostream&
    print_stats(std::ostream& os) const;

  protected:
    void
    clear(numbered_state_heap* h, std::stack<pair_state_iter> todo, std::queue<
        spot::state*> init_set);

    void
    clear(numbered_state_heap* h, std::stack<pair_state_iter> todo,
        spot::ta_succ_iterator* init_states_it);

    /// the heuristic for livelock-accepting runs detection, it's described
    /// in the paper cited above
    bool
    heuristic_livelock_detection(const state * stuttering_succ,
        numbered_state_heap* h, int h_livelock_root, std::set<const state*,
            state_ptr_less_than> liveset_curr);

    const ta_product* a_; ///< The automaton.
    option_map o_; ///< The options

    // Force the second pass
    bool is_full_2_pass_;

    // scc: a stack of strongly connected components (SCC)
    scc_stack_ta scc;

    // sscc: a stack of strongly stuttering-connected components (SSCC)
    scc_stack_ta sscc;

  };

  /// @}

    /// \addtogroup ta_emptiness_check_algorithms Emptiness-check algorithms
    /// \ingroup ta_emptiness_check
}

#endif // SPOT_TAALGOS_EMPTINESSTA_HH
