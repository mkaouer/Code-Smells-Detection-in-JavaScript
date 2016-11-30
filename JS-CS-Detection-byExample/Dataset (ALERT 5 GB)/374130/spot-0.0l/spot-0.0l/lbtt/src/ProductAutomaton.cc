/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#ifdef __GNUC__
#pragma implementation
#endif /* __GNUC__ */

#include <config.h>
#include "ProductAutomaton.h"
#include "SccIterator.h"

namespace Graph
{

/******************************************************************************
 *
 * Function definitions for class ProductAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
void ProductAutomaton::clear()
/* ----------------------------------------------------------------------------
 *
 * Description:   Makes the automaton empty.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  buchi_automaton = 0;
  statespace_size = 0;

#ifdef HAVE_OBSTACK_H
  for (vector<Node*, ALLOC(Node*) >::iterator state = nodes.begin();
       state != nodes.end();
       ++state)
    static_cast<ProductState*>(*state)->~ProductState();

  if (!nodes.empty())
  {
    store.free(*nodes.begin());
    nodes.clear();
    nodes.reserve(0);
  }

#endif /* HAVE_OBSTACK_H */

  Graph<GraphEdgeContainer>::clear();
}

/* ========================================================================= */
ProductAutomaton::size_type ProductAutomaton::expand(size_type node_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts states to a ProductAutomaton.
 *
 * Argument:      node_count  --  Number of states to be inserted.
 *
 * Returns:       The index of the last state inserted.
 *
 * ------------------------------------------------------------------------- */
{
  nodes.reserve(nodes.size() + node_count);

  while (node_count > 0)
  {
#ifdef HAVE_OBSTACK_H
    void* state_storage = store.alloc(sizeof(ProductState));
    ProductState* new_product_state = new(state_storage) ProductState();
#else
    ProductState* new_product_state = new ProductState();
#endif /* HAVE_OBSTACK_H */

    try
    {
      nodes.push_back(new_product_state);
    }
    catch (...)
    {
#ifdef HAVE_OBSTACK_H
      new_product_state->~ProductState();
      store.free(state_storage);
#else
      delete new_product_state;
#endif /* HAVE_OBSTACK_H */
      throw;
    }
    node_count--;
  }

  return size() - 1;
}

/* ========================================================================= */
void ProductAutomaton::connect(const size_type father, const size_type child)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects two states of the product automaton.
 *
 * Arguments:     father, child  --  State identifiers.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Edge* edge = operator[](child).incoming_edge;

  if (edge != 0)
  {
    nodes[father]->outgoing_edges.insert(edge);
    return;
  }

#ifdef HAVE_OBSTACK_H
  void* edge_storage = store.alloc(sizeof(Edge));
  edge = new(edge_storage) Edge(child);
#else
  edge = new Edge(child);
#endif /* HAVE_OBSTACK_H */

  try
  {
    nodes[father]->outgoing_edges.insert(edge);
  }
  catch (...)
  {
#ifdef HAVE_OBSTACK_H
    edge->~Edge();
    store.free(edge_storage);
#else
    delete edge;
#endif /* HAVE_OBSTACK_H */
    throw;
  }

  operator[](child).incoming_edge = edge;
}

/* ========================================================================= */
void ProductAutomaton::disconnect
  (const size_type father, const size_type child)
/* ----------------------------------------------------------------------------
 *
 * Description:   Disconnects two states of the product automaton.
 *
 * Arguments:     father, child  --  Identifiers for two states.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Edge e(child);

  /*
   *  Scan the set of `father''s outgoing transitions for a transition to the
   *  given target state and remove it if such a transition exists.
   */

  GraphEdgeContainer::iterator search_edge
    = nodes[father]->outgoing_edges.find(&e);

  if (search_edge != nodes[father]->outgoing_edges.end())
    nodes[father]->outgoing_edges.erase(search_edge);
}

/* ========================================================================= */
void ProductAutomaton::print
  (ostream& stream, const int indent, const GraphOutputFormat) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a product automaton to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *
 *                The third (dummy) parameter is needed to support the
 *                function interface defined in the base class.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (nodes.empty())
    estream << string(indent, ' ') + "The product automaton is empty.\n";
  else
  {
    pair<size_type, unsigned long int> statistics = stats();
    bool first_printed;

    estream << string(indent, ' ') + "The product automaton consists of\n"
               + string(indent + 4, ' ')
            << statistics.first
            << " states and\n" + string(indent + 4, ' ')
            << statistics.second
            << " transitions.\n";

    size_type s = nodes.size();
    for (size_type state = 0; state < s; ++state)
    {
      estream << string(indent, ' ') + "State " << state << ":\n"
	         + string(indent + 4, ' ') + "Automaton state: "
	      << buchiState(state)
	      << " (acceptance sets: {";

      for (unsigned long int acceptance_set = 0;
	   acceptance_set < buchi_automaton->numberOfAcceptanceSets();
	   acceptance_set++)
      {
	if ((*buchi_automaton)[buchiState(state)].acceptanceSets().
	      test(acceptance_set))
	{
	  if (first_printed)
	    estream << ", ";
	  else
	    first_printed = true;
	  estream << acceptance_set;
	}
      }

      estream << "}\n" + string(indent + 4, ' ') + "System state:    "
	      << systemState(state)
	      << '\n';

      operator[](state).print(stream, indent + 4);
    }
  }

  estream.flush();
}

/* ========================================================================= */
void ProductAutomaton::computeProduct
  (const BuchiAutomaton& automaton, const StateSpace& statespace,
   const bool global_product)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes the synchronous product of a Büchi automaton and
 *                a state space.
 *
 * Arguments:     automaton       --  A reference to a constant BuchiAutomaton.
 *                statespace      --  A reference to a constant StateSpace.
 *                global_product  --  Controls whether the synchronous product
 *                                    of the automaton and the statespace is
 *                                    computed `globally' (i.e., with respect
 *                                    to all states in the state space) or
 *                                    `locally' (i.e., with respect only to the
 *                                    initial state of the state space).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  clear();

  buchi_automaton = &automaton;
  statespace_size = statespace.size();

  /*
   *  If either the Büchi automaton or the state space is empty, their product
   *  is also empty.
   */

  if (automaton.empty() || statespace.empty())
    return;

  /*
   *  If the (worst-case) product of the size of the automaton and the state
   *  space size exceeds the maximum size of a product automaton, throw an
   *  exception (if this holds, the simple product state hashing technique
   *  used below will not work correctly).
   */

  if (automaton.size() > (nodes.max_size() / statespace.size()))
    throw ProductSizeException();

  /*
   *  Product states will be numerically encoded using the equation
   *
   *    #P = #S + |S| * #B
   *
   *  where
   *     |S|  is the number of states in the state space,
   *     #P   is an identifier of a product state,
   *     #S   is an identifier of a state in the state space (0...|S|-1),
   *     #B   is an identifier of a state in the Büchi automaton (0...|B|-1)
   *          and
   *
   *  From this encoding we obtain
   *     #S  = #P % |S|,  and
   *     #B  = #P / |S|.
   */

  map<size_type, size_type, less<size_type>, ALLOC(size_type) >
    product_state_mapping;

  /*
   *  Initialize the product automaton. If the product is to be computed
   *  globally, initialize the result with state pairs (q_0, s) where q_0 is
   *  the initial state of the Büchi automaton and s goes through all states
   *  in the state space. (These states will have the lowest indices in the
   *  product automaton, i.e. covering the index interval
   *  0 ... [statespace.size() - 1] .) In the case of a local product,
   *  initialize the result with the single state (q_0, s_0), a pair consisting
   *  of the initial states of the two other structures.
   *
   *  The final product automaton is obtained by computing the closure of this
   *  set of states under the product transition relation. For this purpose,
   *  a depth-first search will be used (with `mapping_stack' as the search
   *  stack; initially, it contains the same states as described above).
   */

  expand(global_product ? statespace.size() : 1);

  stack<size_type, deque<size_type, ALLOC(size_type) > > mapping_stack;

  pair<size_type, size_type> state_map_entry
    = make_pair(automaton.initialState() * statespace_size, 0);

  for (state_map_entry.second = 0;
       state_map_entry.second < (global_product ? statespace_size : 1);
       ++state_map_entry.first, ++state_map_entry.second)
  {
    product_state_mapping.insert(state_map_entry);
    mapping_stack.push(state_map_entry.first);

    operator[](state_map_entry.second).hashValue() = state_map_entry.first;
  }

  /*
   *  Compute the product automaton by using a depth-first search.
   */

  const GraphEdgeContainer* automaton_transitions;
  const GraphEdgeContainer* system_transitions;

  BuchiAutomaton::size_type current_automaton_state;
  StateSpace::size_type current_system_state;
  size_type current_product_state;
  bool current_product_state_valid;
  size_type current_mapping;

  try
  {
    while (!mapping_stack.empty())
    {
      if (::user_break)
	throw UserBreakException();

      /*
       *  Pop a state mapping off the stack.
       */

      current_product_state_valid = false;
      current_mapping = mapping_stack.top();
      mapping_stack.pop();
      
      current_automaton_state = current_mapping / statespace_size;
      current_system_state = current_mapping % statespace_size;

      /*
       *  Go through the transitions of the original Büchi automaton. For all
       *  transitions enabled in the current state of the state space:
       *
       *     - Compute all product states that can be reached by firing the
       *       transition. These are the states (q', s') where q' is the
       *       state of the automaton after firing the transition and s'
       *       is a successor of the current state of the system.
       *
       *     - If there is no corresponding product state for the state pair
       *       (q', s'), create a new product state and insert it into the
       *       product automaton. Push the mapping also on the stack so that
       *       the newly created product state will be eventually processed.
       *
       *     - Connect the target product state to the current product state
       *       (in effect, storing information only about the _predecessors_ of
       *       each product state). This will result in a product space in
       *       which all edges are reversed. However, this is all that is
       *       needed since no information is needed about the successors of
       *       any product state when searching for the accepting cycles in the
       *       product graph (this will be performed using a backward search,
       *       see the functions `emptinessCheck' and
       *       `findAcceptingExecution').
       */

      automaton_transitions = &automaton[current_automaton_state].edges();

      for (GraphEdgeContainer::const_iterator
	     automaton_transition = automaton_transitions->begin();
	   automaton_transition != automaton_transitions->end();
	   ++automaton_transition)
      {
	if ((static_cast<const BuchiAutomaton::BuchiTransition*>
	     (*automaton_transition))->
                enabled(statespace[current_system_state].positiveAtoms(),
			statespace.numberOfPropositions()))
	{
	  if (!current_product_state_valid)
	  {
	    current_product_state = product_state_mapping[current_mapping];
	    current_product_state_valid = true;
	    system_transitions = &statespace[current_system_state].edges();
	  }

	  pair<map<size_type, size_type, less<size_type>, ALLOC(size_type) >
	         ::iterator,
	       bool>
	    check_state;
        
	  for (GraphEdgeContainer::const_iterator
		 system_transition = system_transitions->begin();
	       system_transition != system_transitions->end();
	       ++system_transition)
          {
	    /*
	     *  Compute a hash value for the target product state and test
	     *  whether it has already been included in the product.
	     */

	    state_map_entry.first =
	      (*system_transition)->targetNode()
	      + statespace_size * (*automaton_transition)->targetNode();

	    check_state = product_state_mapping.insert(state_map_entry);

	    if (check_state.second)   /* insertion occurred */
            {
	      /*
	       *  Create a new product state and adjust its hash value.
	       *  `state_map_entry.second' holds the next free identifier for a
	       *  new product state.
	       */

	      expand();

	      operator[](state_map_entry.second).hashValue()
		= state_map_entry.first;

	      connect(state_map_entry.second, current_product_state);
	      state_map_entry.second++;

	      mapping_stack.push(state_map_entry.first);
	    }
	    else
	    {
	      size_type existing_state = (check_state.first)->second;
	      connect(existing_state, current_product_state);
	    }
	  }
	}
      }
    }
  }
  catch (...)
  {
    clear();
    throw;
  }
}

/* ========================================================================= */
void ProductAutomaton::emptinessCheck(Bitset& result) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Performs an emptiness check on the product automaton, i.e.
 *                finds the set of system states starting an execution
 *                sequence accepted by the Büchi automaton.
 *
 * Argument:      result  --  A reference to the Bitset in which the result
 *                            is stored. The Bitset must have enough space for
 *                            as many states as there are in the state space.
 *                            A `0' bit in some position n in the result means
 *                            that no accepting executions begin from system
 *                            state n; a `1' bit means the opposite.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  BitArray visited(nodes.size());
  visited.clear(nodes.size());

  result.clear();

  /*
   *  Scan the maximal strongly connected components of the product space to
   *  find any fair MSCCs (nontrivial MSCCs containing an accepting cycle of
   *  the Büchi automaton used for constructing the product). Note that
   *  although the product space contains only back edges (i.e., we are
   *  actually scanning the product space with all edges reversed), the
   *  reversal of the edges does not affect the MSCCs of the product space.
   */

  for (SccIterator<GraphEdgeContainer, ProductScc>
	 strongly_connected_component(*this);
       !strongly_connected_component.atEnd();
       ++strongly_connected_component)
  {
    if (::user_break)
      throw UserBreakException();

    if (strongly_connected_component->fair(*this))
    {
      /*
       *  Search for a previously unvisited state in the strongly connected
       *  component.
       */

      ProductScc::const_iterator st;
      for (st = strongly_connected_component->begin();
	   st != strongly_connected_component->end() && visited[*st];
	   ++st)
	;

      if (st != strongly_connected_component->end())
      {
	/*
	 *  Starting from the unvisited state, perform a backward depth-first
	 *  search to find all states (q_0, s) such that q_0 is the initial
	 *  state of the automaton (the product states (q_0, s) are recognized 
	 *  by the product state numbering scheme, in which these states have
	 *  the lowest indices). Add the corresponding system states to the
	 *  result (these are the system states from which the nontrivial MSCC 
	 *  containing the accepting cycle can be reached).
	 */
	
	size_type state = *st;
	stack<size_type, deque<size_type, ALLOC(size_type) > >
	  backward_search_stack;
	const GraphEdgeContainer* predecessors;

	visited.setBit(state);
	backward_search_stack.push(state);
          
	while (!backward_search_stack.empty())
        {
	  state = backward_search_stack.top();
	  backward_search_stack.pop();

	  if (state < result.capacity() && state < statespace_size)
	    result.setBit(systemState(state));

	  predecessors = &(operator[](state).edges());  /* note that only back
							 * edges are stored in
							 * the product! */
          
	  for (GraphEdgeContainer::const_iterator predecessor
		 = predecessors->begin();
	       predecessor != predecessors->end();
	       ++predecessor)
	  {
	    if (!visited[(*predecessor)->targetNode()])
	    {
	      backward_search_stack.push((*predecessor)->targetNode());
	      visited.setBit((*predecessor)->targetNode());
	    } 
	  }
	}
      }
    }
  }
}

/* ========================================================================= */
void ProductAutomaton::findAcceptingExecution
  (const StateSpace::size_type initial_state,
   pair<deque<StateIdPair, ALLOC(StateIdPair) >,                     
        deque<StateIdPair, ALLOC(StateIdPair) > >& execution) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Extracts an execution (beginning from a given system
 *                state) accepted by the Büchi automaton from the product
 *                space. The function behaves basically like
 *                ProductAutomaton::emptinessCheck, but it keeps additional
 *                information about the path of processed states during the
 *                backward search. This information is then used to extract
 *                the desired system execution from the graph.
 *
 * Arguments:     initial_state  --  Identifier of the system state.
 *                execution      --  A reference to a pair of deques for
 *                                   storing the result. The `first' component
 *                                   of the pair represents an execution
 *                                   `prefix' (a sequence of
 *                                   <automaton state id, system state id>
 *                                   pairs) leading from `initial_state'
 *                                   to an accepting cycle. The `second'
 *                                   component of the pair contains the cycle
 *                                   itself.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  BitArray visited(nodes.size());
  visited.clear(nodes.size());

  deque<StateIdPair, ALLOC(StateIdPair) >& prefix = execution.first;
  deque<StateIdPair, ALLOC(StateIdPair) >& cycle = execution.second;

  prefix.clear();
  cycle.clear();
  
  /*
   *  Scan the non-trivial maximal strongly connected components of the
   *  product space to find the fair MSCCs (non-trivial MSCCs containing an
   *  accepting cycle of the Büchi automaton used for constructing the
   *  product). Note that although the product space contains only back edges
   *  (i.e., we are actually scanning the product space with all edges
   *  reversed), the reversal of the edges does not affect the MSCCs of the
   *  product space.
   */

  for (SccIterator<GraphEdgeContainer, ProductScc> nmscc(*this);
       !nmscc.atEnd();
       ++nmscc)
  {
    if (nmscc->fair(*this))
    {
      const unsigned long int num_accept_sets
	= buchi_automaton->numberOfAcceptanceSets();

      /*
       *  Search the fair non-trivial maximal strongly connected component for 
       *  a state belonging to some acceptance set of the Büchi automaton from 
       *  which the product was constructed (or if the automaton has no
       *  acceptance sets, any state in the non-trivial MSCC).
       */
    
      ProductScc::const_iterator st = nmscc->begin();

      if (buchi_automaton->numberOfAcceptanceSets() > 0)
      {
	while (st != nmscc->end())
	{
	  if ((*buchi_automaton)[buchiState(*st)].acceptanceSets().
	                                            find(num_accept_sets)
	      < num_accept_sets)
	    break;

	  ++st;
	}
      }

      if (st != nmscc->end())
      {
	/*
	 *  Try to find a (backward) path from the state in the MSCC back to
	 *  the caller-given `initial state'.
	 */

	size_type search_start_state = *st;

	if (search_start_state != initial_state)
	{
	  typedef pair<size_type, GraphEdgeContainer::const_iterator>
	    BackwardSearchStackElement;

	  deque<BackwardSearchStackElement, ALLOC(BackwardSearchStackElement) >
	    backward_search_stack;

	  visited.clear(nodes.size());
	  visited.setBit(search_start_state);
	  backward_search_stack.push_back
	    (make_pair(search_start_state,
		       operator[](search_start_state).edges().begin()));

	  size_type predecessor;
	  
	  while (!backward_search_stack.empty())
          {
	    /*
	     *  If it may be possible to find a shorter path to the initial
	     *  state by still extending the current path (or if no path to
	     *  the initial state has yet been found), scan through the
	     *  predecessors of the `current' state (the last state inserted
	     *  onto `backward_search_stack').
	     */

	    while ((backward_search_stack.size() < prefix.size()
		    || prefix.empty())
		   && backward_search_stack.back().second
		        != operator[](backward_search_stack.back().first)
		                        .edges().end())
	    {
	      predecessor
		= (*backward_search_stack.back().second)->targetNode();

	      backward_search_stack.back().second++;

	      /*
	       *  If the given `initial state' is a predecessor of the current
	       *  state, extract the path from the initial state to the state
	       *  from where the backward search was started.
	       */

	      if (buchiState(predecessor) == buchi_automaton->initialState()
		  && systemState(predecessor) == initial_state)
	      {
		prefix.clear();
		for (deque<BackwardSearchStackElement,
		           ALLOC(BackwardSearchStackElement) >
		       ::const_iterator
		       state = backward_search_stack.begin();
		     state != backward_search_stack.end();
		     ++state)
	        {
		  prefix.push_front(make_pair(buchiState((*state).first),
					      systemState((*state).first)));
	        }
		prefix.push_front(make_pair(buchiState(predecessor),
					    systemState(predecessor)));
	      }

	      /*
	       *  If some predecessor of the current state has not yet been
	       *  visited, push it onto the backward search stack, then proceed
	       *  with checking its predecessors.
	       */

	      else if (!visited[predecessor])
	      {
		visited.setBit(predecessor);
		backward_search_stack.push_back
		  (make_pair(predecessor,
			     operator[](predecessor).edges().begin()));
	      }
	    }

	    /*
	     *  If all predecessors of a state have been processed, backtrack
	     *  to the previous state on the path.
	     */

	    backward_search_stack.pop_back();
	  }
	}

	/*
	 *  If a path was found from the `initial state' to the state in the
	 *  MSCC, construct an accepting cycle by performing a breadth-first
	 *  search in the MSCC.
	 */

	if (!prefix.empty() || search_start_state == initial_state)
	{
	  BitArray in_nmscc(nodes.size());
	  in_nmscc.clear(nodes.size());

	  for (ProductScc::const_iterator state = nmscc->begin();
	       state != nmscc->end();
	       ++state)
	    in_nmscc.setBit(*state);

	  BitArray collected_acc_sets;
	  collected_acc_sets.copy
	    ((*buchi_automaton)[buchiState(search_start_state)]
	       .acceptanceSets(),
	     num_accept_sets);
	  bool all_acceptance_sets_on_path
	    = (collected_acc_sets.count(num_accept_sets) == num_accept_sets);

	  deque<size_type, ALLOC(size_type) > backward_search_queue;
	  map<size_type, size_type, less<size_type>, ALLOC(size_type) >
	    shortest_path_predecessor;

	  size_type bfs_root = search_start_state;
	  const GraphEdgeContainer* predecessors;
	  size_type state;

	  visited.clear(nodes.size());
	  visited.setBit(bfs_root);
	  backward_search_queue.push_back(bfs_root);

	  bool cycle_finished = false;

	  while (!cycle_finished)
	  {
	    predecessors = &operator[](backward_search_queue.front()).edges();

	    for (GraphEdgeContainer::const_iterator
		   predecessor = predecessors->begin();
		 predecessor != predecessors->end();
		 ++predecessor)
	    {
	      state = (*predecessor)->targetNode();

	      if (in_nmscc[state])
	      {
		/*
		 *  If all acceptance sets have been collected and the search
		 *  finds the first state of the cycle again, the cycle is
		 *  complete.
		 */
	      
		if (all_acceptance_sets_on_path && state == search_start_state)
		{
		  cycle_finished = true;
		  state = backward_search_queue.front();
		  break;
		}
		else if (!visited[state])
		{
		  /*
		   *  Update information about the breadth-first predecessor of
		   *  an unvisited state.
		   */

		  shortest_path_predecessor[state]
		    = backward_search_queue.front();

		  /*
		   *  If the unvisited state does not cover any `new'
		   *  acceptance conditions, prepare to continue the search in 
		   *  that state by inserting the state into the search queue.
		   */

		  if (all_acceptance_sets_on_path
		      || (*buchi_automaton)[buchiState(state)].acceptanceSets()
		           .subset(collected_acc_sets, num_accept_sets))
		  {
		    visited.setBit(state);
		    backward_search_queue.push_back(state);
		  }

		  /*
		   *  If the search finds an unvisited state which covers new
		   *  acceptance sets, begin a new breadth-first search in
		   *  that state.
		   */

		  else
                  {
		    all_acceptance_sets_on_path = true;

		    for (unsigned long int accept_set = 0;
			 accept_set
			   < buchi_automaton->numberOfAcceptanceSets();
			 accept_set++)
		    {
		      if ((*buchi_automaton)[buchiState(state)]
			    .acceptanceSets().test(accept_set))
			collected_acc_sets.setBit(accept_set);
		      else if (!collected_acc_sets.test(accept_set))
			all_acceptance_sets_on_path = false;
		    }
		    
		    deque<StateIdPair, ALLOC(StateIdPair) > cycle_fragment;
		    while (state != bfs_root)
		    {
		      cycle_fragment.push_back(make_pair(buchiState(state),
							 systemState(state)));
		      state = shortest_path_predecessor[state];
		    }
		    cycle.insert(cycle.begin(), cycle_fragment.begin(),
				 cycle_fragment.end());

		    bfs_root = (*predecessor)->targetNode();
		    visited.clear(nodes.size());
		    visited.setBit(bfs_root);
		    backward_search_queue.clear();
		    backward_search_queue.push_back(bfs_root);
		    backward_search_queue.push_back(bfs_root);

		    break;
		  }
		}
	      }
	    }
	      
	    backward_search_queue.pop_front();
	  }

	  deque<StateIdPair, ALLOC(StateIdPair) > cycle_fragment;
	  while (state != bfs_root)
	  {
	    cycle_fragment.push_back(make_pair(buchiState(state),
					       systemState(state)));
	    state = shortest_path_predecessor[state];
	  }

	  cycle.insert(cycle.begin(), cycle_fragment.begin(),
		       cycle_fragment.end());
	  cycle.push_back(make_pair(buchiState(search_start_state),
				    systemState(search_start_state)));

	  /*
	   *  "Synchronize" the prefix of the witness execution with its cycle
	   *  by removing from the end of the prefix the longest subsequence of
	   *  states which occurs in the end of the cycle. The states in the
	   *  cycle must be "rotated" accordingly to align the first state of
	   *  the cycle correctly.
	   */

	  while (!prefix.empty() && prefix.back() == cycle.back())
	  {
	    cycle.push_front(cycle.back());
	    prefix.pop_back();
	    cycle.pop_back();
	  }

          return;
	}
      }
    }
  }

  /*
   *  The result will be empty if no accepting execution beginning from the
   *  given initial state could be found.
   */
}



/******************************************************************************
 *
 * Function definitions for class ProductAutomaton::ProductState.
 *
 *****************************************************************************/

/* ========================================================================= */
void ProductAutomaton::ProductState::print
  (ostream& stream, const int indent, const GraphOutputFormat) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about the ProductState to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *
 *                The third (dummy) argument is needed to support the function
 *                interface defined in the base class.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (edges().empty())
    estream << string(indent,' ') + "The product state has no predecessors.\n";
  else
  {
    bool first_printed = false; 
    estream << string(indent, ' ') + "Predecessor states: {";

    for (GraphEdgeContainer::const_iterator predecessor = edges().begin();
         predecessor != edges().end(); ++predecessor)
    {
      if (first_printed)
        estream << ", ";
      else
        first_printed = true;
      estream << (*predecessor)->targetNode();
    }
    estream << "}\n";
  }

  estream.flush();
}



/******************************************************************************
 *
 * Function definitions for class ProductAutomaton::ProductScc.
 *
 *****************************************************************************/

/* ========================================================================= */
bool ProductAutomaton::ProductScc::fair
  (const ProductAutomaton& product_automaton) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether a strongly connected component is fair in
 *                a product automaton. A strongly connected component is
 *                fair if and only if it is nontrivial (i.e. empty or
 *                containing a single state with a self-loop) and contains a
 *                state corresponding to a state from every acceptance set of
 *                the Büchi automaton used for constructing the given product.
 *
 * Arguments:     product_automaton  --  A constant reference to a
 *                                       ProductAutomaton.
 *
 * Returns:       A truth value telling whether the strongly connected
 *                component is fair.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   *  A maximal strongly connected component is not fair if it is trivial.
   */

  if (empty()
      || (size() == 1 && !product_automaton.connected(*begin(), *begin())))
    return false;

  /*
   *  Check whether the strongly connected component contains a state from each
   *  acceptance set of the Büchi automaton used for constructing the product
   *  (in this case, the component contains an accepting cycle of the
   *  automaton and is therefore fair).
   */

  const BuchiAutomaton* buchi_automaton = product_automaton.buchi_automaton;
  const BitArray* acceptance_sets;
  const unsigned long int number_of_acceptance_sets
    = buchi_automaton->numberOfAcceptanceSets();

  BitArray acceptance_sets_in_scc(number_of_acceptance_sets);
  acceptance_sets_in_scc.clear(number_of_acceptance_sets);

  unsigned long int accept_set;
  unsigned long int acceptance_set_counter = 0;

  for (const_iterator st = begin();
       st != end() && acceptance_set_counter < number_of_acceptance_sets;
       ++st)
  {
    acceptance_sets = &(*buchi_automaton)[product_automaton.buchiState(*st)].
                        acceptanceSets();

    accept_set = acceptance_set_counter;
    while (accept_set < number_of_acceptance_sets)
    {
      if (acceptance_sets->test(accept_set))
      {
	acceptance_sets_in_scc.setBit(accept_set);
	if (accept_set == acceptance_set_counter)
	{
	  do
	    acceptance_set_counter++;
	  while (acceptance_set_counter < number_of_acceptance_sets
		 && acceptance_sets_in_scc[acceptance_set_counter]);
	  accept_set = acceptance_set_counter;
	  continue;
	}
      }

      accept_set++;
    }
  }

  return (acceptance_set_counter == number_of_acceptance_sets);
}

}
