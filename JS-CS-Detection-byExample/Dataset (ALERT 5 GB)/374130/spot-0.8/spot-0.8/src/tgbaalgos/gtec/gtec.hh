// Copyright (C) 2008 Laboratoire de Recherche et Development de
// l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_GTEC_GTEC_HH
# define SPOT_TGBAALGOS_GTEC_GTEC_HH

#include <stack>
#include "status.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/emptiness_stats.hh"

namespace spot
{
  /// \addtogroup emptiness_check_algorithms
  /// @{

  /// \brief Check whether the language of an automate is empty.
  ///
  /// This is based on the following paper.
  /// \verbatim
  /// @InProceedings{couvreur.99.fm,
  ///   author    = {Jean-Michel Couvreur},
  ///   title     = {On-the-fly Verification of Temporal Logic},
  ///   pages     = {253--271},
  ///   editor    = {Jeannette M. Wing and Jim Woodcock and Jim Davies},
  ///   booktitle = {Proceedings of the World Congress on Formal Methods in
  ///                the Development of Computing Systems (FM'99)},
  ///   publisher = {Springer-Verlag},
  ///   series    = {Lecture Notes in Computer Science},
  ///   volume    = {1708},
  ///   year      = {1999},
  ///   address   = {Toulouse, France},
  ///   month     = {September},
  ///   isbn      = {3-540-66587-0}
  /// }
  /// \endverbatim
  ///
  /// A recursive definition of the algorithm would look as follows,
  /// but the implementation is of course not recursive.
  /// (<code>&lt;Sigma, Q, delta, q, F&gt;</code> is the automaton to
  /// check, H is an associative array mapping each state to its
  /// positive DFS order or 0 if it is dead, SCC is and ACC are two
  /// stacks.)
  ///
  /// \verbatim
  /// check(<Sigma, Q, delta, q, F>, H, SCC, ACC)
  ///   if q is not in H   // new state
  ///       H[q] = H.size + 1
  ///       SCC.push(<H[q], {}>)
  ///       forall <a, s> : <q, _, a, s> in delta
  ///           ACC.push(a)
  ///           res = check(<Sigma, Q, delta, s, F>, H, SCC, ACC)
  ///           if res
  ///               return res
  ///       <n, _> = SCC.top()
  ///       if n = H[q]
  ///           SCC.pop()
  ///           mark_reachable_states_as_dead(<Sigma, Q, delta, q, F>, H$)
  ///       return 0
  ///   else
  ///       if H[q] = 0 // dead state
  ///           ACC.pop()
  ///           return true
  ///       else // state in stack: merge SCC
  ///           all = {}
  ///           do
  ///               <n, a> = SCC.pop()
  ///               all = all union a union { ACC.pop() }
  ///           until n <= H[q]
  ///           SCC.push(<n, all>)
  ///           if all != F
  ///               return 0
  ///           return new emptiness_check_result(necessary data)
  /// \endverbatim
  ///
  /// check() returns 0 iff the automaton's language is empty.  It
  /// returns an instance of emptiness_check_result.  If the automaton
  /// accept a word.  (Use emptiness_check_result::accepting_run() to
  /// extract an accepting run.)
  ///
  /// There are two variants of this algorithm: spot::couvreur99_check and
  /// spot::couvreur99_check_shy.  They differ in their memory usage, the
  /// number for successors computed before they are used and the way
  /// the depth first search is directed.
  ///
  /// spot::couvreur99_check performs a straightforward depth first search.
  /// The DFS stacks store tgba_succ_iterators, so that only the
  /// iterators which really are explored are computed.
  ///
  /// spot::couvreur99_check_shy tries to explore successors which are
  /// visited states first.  this helps to merge SCCs and generally
  /// helps to produce shorter counter-examples.  However this
  /// algorithm cannot stores unprocessed successors as
  /// tgba_succ_iterators: it must compute all successors of a state
  /// at once in order to decide which to explore first, and must keep
  /// a list of all unexplored successors in its DFS stack.
  ///
  /// The couvreur99() function is a wrapper around these two flavors
  /// of the algorithm.  \a options is an option map that specifies
  /// which algorithms should be used, and how.
  ///
  /// The following options are available.
  /// \li \c "shy" : if non zero, then spot::couvreur99_check_shy is used,
  ///                otherwise (and by default) spot::couvreur99_check is used.
  ///
  /// \li \c "poprem" : specifies how the algorithm should handle the
  /// destruction of non-accepting maximal strongly connected
  /// components.  If \c poprem is non null, the algorithm will keep a
  /// list of all states of a SCC that are fully processed and should
  /// be removed once the MSCC is popped.  If \c poprem is null (the
  /// default), the MSCC will be traversed again (i.e. generating the
  /// successors of the root recursively) for deletion.  This is a
  /// choice between memory and speed.
  ///
  /// \li \c "group" : this options is used only by spot::couvreur99_check_shy.
  /// If non null (the default), the successors of all the
  /// states that belong to the same SCC will be considered when
  /// choosing a successor.  Otherwise, only the successor of the
  /// topmost state on the DFS stack are considered.
  emptiness_check*
  couvreur99(const tgba* a,
	     option_map options = option_map(),
	     const numbered_state_heap_factory* nshf
	     = numbered_state_heap_hash_map_factory::instance());


  /// \brief An implementation of the Couvreur99 emptiness-check algorithm.
  ///
  /// See the documentation for spot::couvreur99.
  class couvreur99_check: public emptiness_check, public ec_statistics
  {
  public:
    couvreur99_check(const tgba* a,
		     option_map o = option_map(),
		     const numbered_state_heap_factory* nshf
		     = numbered_state_heap_hash_map_factory::instance());
    virtual ~couvreur99_check();

    /// Check whether the automaton's language is empty.
    virtual emptiness_check_result* check();

    virtual std::ostream& print_stats(std::ostream& os) const;

    /// \brief Return the status of the emptiness-check.
    ///
    /// When check() succeed, the status should be passed along
    /// to spot::counter_example.
    ///
    /// This status should not be deleted, it is a pointer
    /// to a member of this class that will be deleted when
    /// the couvreur99 object is deleted.
    const couvreur99_check_status* result() const;

  protected:
    couvreur99_check_status* ecs_;
    /// \brief Remove a strongly component from the hash.
    ///
    /// This function remove all accessible state from a given
    /// state. In other words, it removes the strongly connected
    /// component that contains this state.
    void remove_component(const state* start_delete);

    /// Whether to store the state to be removed.
    bool poprem_;
    /// Number of dead SCC removed by the algorithm.
    unsigned removed_components;
    unsigned get_removed_components() const;
    unsigned get_vmsize() const;
  };

  /// \brief A version of spot::couvreur99_check that tries to visit
  /// known states first.
  ///
  /// See the documentation for spot::couvreur99.
  class couvreur99_check_shy : public couvreur99_check
  {
  public:
    couvreur99_check_shy(const tgba* a,
			 option_map o = option_map(),
			 const numbered_state_heap_factory* nshf
			 = numbered_state_heap_hash_map_factory::instance());
    virtual ~couvreur99_check_shy();

    virtual emptiness_check_result* check();

  protected:
    struct successor {
      bdd acc;
      const spot::state* s;
      successor(bdd acc, const spot::state* s): acc(acc), s(s) {}
    };

    // We use five main data in this algorithm:
    // * couvreur99_check::root, a stack of strongly connected components (SCC),
    // * couvreur99_check::h, a hash of all visited nodes, with their order,
    //   (it is called "Hash" in Couvreur's paper)
    // * arc, a stack of acceptance conditions between each of these SCC,
    std::stack<bdd> arc;
    // * num, the number of visited nodes.  Used to set the order of each
    //   visited node,
    int num;
    // * todo, the depth-first search stack.  This holds pairs of the
    //   form (STATE, SUCCESSORS) where SUCCESSORS is a list of
    //   (ACCEPTANCE_CONDITIONS, STATE) pairs.
    typedef std::list<successor> succ_queue;

    // Position in the loop seeking known successors.
    succ_queue::iterator pos;

    struct todo_item
    {
      const state* s;
      int n;
      succ_queue q;		// Unprocessed successors of S
      todo_item(const state* s, int n, couvreur99_check_shy* shy);
    };

    typedef std::list<todo_item> todo_list;
    todo_list todo;

    void clear_todo();

    /// Dump the queue for debugging.
    void dump_queue(std::ostream& os = std::cerr);

    /// Whether successors should be grouped for states in the same SCC.
    bool group_;
    // If the "group2" option is set (it implies "group"), we
    // reprocess the successor states of SCC that have been merged.
    bool group2_;
    // If the onepass option is true, do only one pass.  This cancels
    // all the "shyness" of the algorithm, but we need the framework
    // of the implementation when working with GreatSPN.
    bool onepass_;

    /// \brief find the SCC number of a unprocessed state.
    ///
    /// Sometimes we want to modify some of the above structures when
    /// looking up a new state.  This happens for instance when find()
    /// must perform inclusion checking and add new states to process
    /// to TODO during this step.  (Because TODO must be known,
    /// sub-classing spot::numbered_state_heap is not enough.)  Then
    /// overriding this method is the way to go.
    virtual numbered_state_heap::state_index_p find_state(const state* s);
  };


  /// @}
}

#endif // SPOT_TGBAALGOS_GTEC_GTEC_HH
