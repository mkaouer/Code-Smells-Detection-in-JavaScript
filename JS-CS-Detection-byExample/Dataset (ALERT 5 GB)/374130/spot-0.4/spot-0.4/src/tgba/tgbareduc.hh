// Copyright (C) 2004, 2005, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_TGBA_TGBAREDUC_HH
# define SPOT_TGBA_TGBAREDUC_HH

#include "tgbaexplicit.hh"
#include "tgbaalgos/reachiter.hh"
#include "tgbaalgos/gtec/explscc.hh"
#include "tgbaalgos/gtec/nsheap.hh"

#include <list>

namespace spot
{
  typedef std::pair<const spot::state*, const spot::state*> state_couple;
  typedef std::vector<state_couple*> simulation_relation;

  /*
    typedef std::vector<state_couple*> direct_simulation_relation;
    typedef std::vector<state_couple*> delayed_simulation_relation;
  */

  class direct_simulation_relation: public simulation_relation
  {
  };
  class delayed_simulation_relation: public simulation_relation
  {
  };


  /// Explicit automata used in reductions.
  /// \ingroup tgba_representation
  class tgba_reduc:
    public tgba_explicit, public tgba_reachable_iterator_breadth_first
  {
  public:
    tgba_reduc(const tgba* a,
	       const numbered_state_heap_factory* nshf
	       = numbered_state_heap_hash_map_factory::instance());

    ~tgba_reduc();

    /// Reduce the automata using a relation simulation
    /// Do not call this method with a delayed simulation relation.
    void quotient_state(direct_simulation_relation* rel);

    /// Build the quotient automata. Call this method
    /// when use to a delayed simulation relation.
    void quotient_state(delayed_simulation_relation* rel);

    /// \brief Delete some transitions with help of a simulation
    /// relation.
    void delete_transitions(simulation_relation* rel);

    /// Remove all state which not lead to an accepting cycle.
    void prune_scc();

    /// Remove some useless acceptance condition.
    void prune_acc();

    /// Compute the maximal SCC of the automata.
    void compute_scc();

    /// Add the SCC index to the display of the state \a state.
    virtual std::string format_state(const spot::state* state) const;

    // For Debug
    void display_rel_sim(simulation_relation* rel, std::ostream& os);
    void display_scc(std::ostream& os);

  protected:
    bool scc_computed_;
    scc_stack root_;
    numbered_state_heap* h_;

    std::stack<const spot::state*> state_scc_;
    Sgi::hash_map<int, const spot::state*> state_scc_v_;

    typedef Sgi::hash_map<const tgba_explicit::state*,
			  std::list<state*>*,
			  ptr_hash<tgba_explicit::state> > sp_map;
    sp_map state_predecessor_map_;

    // For reduction using scc.
    typedef Sgi::hash_map<const spot::state*, int,
			  state_ptr_hash, state_ptr_equal> seen_map;
    seen_map si_;
    seen_map* seen_;
    bdd acc_;

    // Interface of tgba_reachable_iterator_breadth_first
    void start();
    void end();
    void process_state(const spot::state* s, int n, tgba_succ_iterator* si);
    void process_link(int in, int out, const tgba_succ_iterator* si);

    /// Create a transition using two state of a TGBA.
    transition* create_transition(const spot::state* source,
				  const spot::state* dest);


    /// Remove all the transition from the state q, predecessor
    /// of both \a s and \a simul, which can be removed.
    void redirect_transition(const spot::state* s,
			     const spot::state* simul);

    /// Remove p of the predecessor of s.
    void remove_predecessor_state(const state* s, const state* p);

    /// Remove all the transition leading to s.
    /// s is then unreachable and can be consider as remove.
    void remove_state(const spot::state* s);

    /// Redirect all transition leading to s1 to s2.
    /// Note that we can do the reverse because
    /// s1 and s2 belong to a co-simulate relation.
    void merge_state(const spot::state* s1,
		     const spot::state* s2);

    /// Redirect all transition leading to s1 to s2.
    /// Note that we can do the reverse because
    /// s1 and s2 belong to a co-simulate relation.
    void merge_state_delayed(const spot::state* s1,
			     const spot::state* s2);

    /// Remove all the scc which are terminal and doesn't
    /// contains all the acceptance conditions.
    void delete_scc();

    /// Return true if the scc which contains \a s
    /// is an fixed-formula alpha-ball.
    /// this is explain in
    /// \verbatim
    /// @InProceedings{	  etessami.00.concur,
    /// author  	= {Kousha Etessami and Gerard J. Holzmann},
    /// title		= {Optimizing {B\"u}chi Automata},
    /// booktitle	= {Proceedings of the 11th International Conference on
    /// 		  Concurrency Theory (Concur'2000)},
    /// pages		= {153--167},
    /// year		= {2000},
    /// editor  	= {C. Palamidessi},
    /// volume  	= {1877},
    /// series  	= {Lecture Notes in Computer Science},
    ///  publisher	= {Springer-Verlag}
    /// }
    /// \endverbatim
    // bool is_alpha_ball(const spot::state* s,
    // bdd label = bddfalse,
    // int n = -1);

    // Return true if we can't reach a state with
    // an other value of scc.
    bool is_terminal(const spot::state* s,
		     int n = -1);

    // Return false if the scc contains all the acceptance conditions.
    bool is_not_accepting(const spot::state* s,
			  int n = -1);

    /// If a scc maximal do not contains all the acceptance conditions
    /// we can remove all the acceptance conditions in this scc.
    void remove_acc(const spot::state* s);

    /// Remove all the state which belong to the same scc that s.
    void remove_scc(spot::state* s);

    /// Same as remove_scc but more efficient.
    // void remove_scc_depth_first(spot::state* s, int n = -1);

    /// For compute_scc.
    void remove_component(const spot::state* from);

    int nb_set_acc_cond() const;

  };
}

#endif // SPOT_TGBA_TGBAREDUC_HH
