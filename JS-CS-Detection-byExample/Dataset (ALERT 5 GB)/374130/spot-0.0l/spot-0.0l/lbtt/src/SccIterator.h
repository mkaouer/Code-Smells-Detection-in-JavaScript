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

#ifndef SCCITERATOR_H
#define SCCITERATOR_H

#include <config.h>
#include <deque>
#include <set>
#include <stack>
#include <vector>
#include "Alloc.h"
#include "Graph.h"

using namespace std;

namespace Graph
{

/******************************************************************************
 *
 *  A template iterator class for computing the maximal strongly connected
 *  components of a graph represented as an object of class
 *  Graph<EdgeContainer>.
 *
 *  The iterator class has three template arguments:
 *      class EdgeContainer  --  Container class storing the edges in the
 *                               graph with which the iterator is associated.
 *
 *      class SccContainer   --  Container for storing the identifiers of the
 *                               nodes belonging to some maximal strongly
 *                               connected component. The container class
 *                               must be able to store elements of type
 *                               Graph<EdgeContainer>::size_type. The container
 *                               class interface must support the following
 *                               operations:
 *
 *                                   Default constructor which can be called
 *                                       without any arguments
 *                                   Copy constructor
 *                                   Assignment operator
 *                                   clear()
 *                                       [makes the container empty]
 *                                   insert(Graph<EdgeContainer>::size_type s)
 *                                       [inserts an element into the
 *                                       container]
 *
 *                               If the container class is left unspecified,
 *                               it defaults to
 *                                 set<Graph<EdgeContainer>::size_type,
 *                                     less<Graph<EdgeContainer>::size_type>,
 *                                     ALLOC(Graph<EdgeContainer>::size_type)>.
 *
 *      class Filter         --  Class for representing function objects that
 *                               can be used to restrict the iterator
 *                               dereferencing operators to return only
 *                               those nodes of a strongly connected component
 *                               which satisfy a certain condition that can be
 *                               tested using Filter::operator(). This function
 *                               has to accept a single parameter of type
 *                               Graph<EdgeContainer>::Node*. It must return a
 *                               Boolean value. The nodes for which the
 *                               function returns `false' will then not be
 *                               included in the collection of nodes returned
 *                               by the iterator dereferencing operators.
 * 
 *                               If the Filter class is left unspecified, it
 *                               defaults to the NullSccFilter<EdgeContainer>
 *                               class, which does not restrict the set of
 *                               nodes in any way.
 *
 *****************************************************************************/

template<class EdgeContainer>
class NullSccFilter;

template<class EdgeContainer,
         class SccContainer
           = set<typename Graph<EdgeContainer>::size_type,
                 less<typename Graph<EdgeContainer>::size_type>,
                 ALLOC(typename Graph<EdgeContainer>::size_type) >,
         class Filter = NullSccFilter<EdgeContainer> >
class SccIterator
{
public:
  SccIterator(const Graph<EdgeContainer>& g);        /* Constructor. */

  /* default copy constructor */

  ~SccIterator();                                    /* Destructor. */
  
  /* default assignment operator */

  bool operator==                                    /* Equality test for */
    (const SccIterator<EdgeContainer,                /* two iterators.    */
                       SccContainer,
                       Filter>& it) const;

  bool operator!=                                    /* Inequality test for */
    (const SccIterator<EdgeContainer,                /* two iterators.      */
                       SccContainer,
                       Filter>& it) const;

  bool operator<                                     /* `Less than' relation */
    (const SccIterator<EdgeContainer,                /* between two          */
                       SccContainer,                 /* iterators.           */
                       Filter>& it) const;           

  bool operator<=                                    /* `Less than or equal' */
    (const SccIterator<EdgeContainer,                /* relation between two */
                       SccContainer,                 /* iterators.           */
                       Filter>& it) const;           

  bool operator>                                     /* `Greater than'       */
    (const SccIterator<EdgeContainer,                /* relation between two */
                       SccContainer,                 /* iterators.           */
                       Filter>& it) const;           

  bool operator>=                                    /* `Greater than or     */
    (const SccIterator<EdgeContainer,                /* equal' relation      */
                       SccContainer,                 /* between two          */
                       Filter>& it) const;           /* iterators.           */

  const SccContainer& operator*() const;             /* Dereferencing */
  const SccContainer* operator->() const;            /* operators.    */
  
  const SccContainer& operator++();                  /* Prefix and postfix   */
  const SccContainer operator++(int);                /* increment operators. */

  bool atEnd() const;                                /* Tests whether the
						      * iterator has scanned
						      * through all the
						      * strongly connected
						      * components of the
						      * graph.
						      */

private:
  const Graph<EdgeContainer>& graph;                 /* Reference to the graph
						      * with which the iterator
						      * is associated.
						      */

  typename Graph<EdgeContainer>::size_type           /* Number of graph    */
    dfs_number;                                      /* nodes processed by
						      * the iterator.
						      */

  vector<typename Graph<EdgeContainer>::size_type,   /* dfs_ordering[i]      */
         ALLOC(typename Graph<EdgeContainer>         /* indicates the        */
	                  ::size_type) >             /* position of graph    */
    dfs_ordering;                                    /* node i in the depth-
                                                      * first search order.
						      * (If the node has not
						      * yet been visited,
						      * dfs_ordering[i]==0.)
						      */

  vector<typename Graph<EdgeContainer>::size_type,   /* lowlink[i] indicates */
         ALLOC(typename Graph<EdgeContainer>         /* the least graph node */
                          ::size_type) >             /* (in the depth-first  */
    lowlink;                                         /* search order) that
                                                      * is reachable from
                                                      * graph node i and 
						      * does not belong to
						      * any strongly
						      * connected component
						      * which has already been
						      * processed.
						      */

  typedef pair<typename Graph<EdgeContainer>::size_type,
               typename EdgeContainer::const_iterator>
    NodeStackElement;

  stack<NodeStackElement,                            /* Depth-first search  */
        deque<NodeStackElement,                      /* backtracking stack. */
              ALLOC(NodeStackElement) > >
    node_stack;

  typename Graph<EdgeContainer>::size_type           /* Current graph node  */
    current_node;                                    /* the depth-first
						      * search.
						      */

  typename EdgeContainer::const_iterator edge;       /* Iterator to scan
						      * through the successors
						      * of the current node.
						      */

  stack<typename Graph<EdgeContainer>::size_type,    /* Stack used for       */
        deque<typename Graph<EdgeContainer>          /* collecting the nodes */
                         ::size_type,                /* in a strongly        */
              ALLOC(typename Graph<EdgeContainer>    /* connected component. */
                               ::size_type)
             >
       >
    scc_stack;                                       

  SccContainer current_scc;                          /* Container of nodes
						      * forming the maximal
						      * strongly connected
						      * graph component
						      * currently `pointed to'
						      * by the iterator.
						      */

  Filter cond;                                       /* Function object for
						      * filtering out a subset
						      * of nodes in the
						      * strongly connected
						      * components.
						      */
  
  void reset();                                      /* Initializes the
						      * iterator to point to
						      * the first strongly
						      * connected component of
						      * the graph.
						      */

  void computeNextScc();                             /* Updates the iterator to
						      * point to the next
						      * strongly connected
						      * component.
						      */
};



/******************************************************************************
 *
 * Default test for collecting the nodes in a strongly connected component.
 * (See documentation on class SccIterator for information about the purpose
 * of the class.)
 *
 *****************************************************************************/

template<class EdgeContainer>
class NullSccFilter
{
public:
  bool operator()(const typename Graph<EdgeContainer>::Node*) const;
};



/******************************************************************************
 *
 * Inline function definitions for template class
 * SccIterator<EdgeContainer, SccContainer, Filter>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline SccIterator<EdgeContainer, SccContainer, Filter>::SccIterator
  (const Graph<EdgeContainer>& g) :
  graph(g), dfs_ordering(graph.size()), lowlink(graph.size())
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                SccIterator<EdgeContainer, SccContainer, Filter>.
 *                Initializes a new iterator for scanning the maximal strongly
 *                connected components of a graph.
 *
 * Arguments:     g  --  The graph with which the iterator is to be associated
 *                       (a Graph<EdgeContainer> object).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  reset();
  computeNextScc();
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline SccIterator<EdgeContainer, SccContainer, Filter>::~SccIterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                SccIterator<EdgeContainer, SccContainer, Filter>.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator==
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equality test for two SccIterators. Two SccIterators are
 *                `equal' if and only if both of them are associated with
 *                exactly the same graph object in memory and the iterators
 *                have processed the same amount of graph nodes.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the equality test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph == &(it.graph) && dfs_number == it.dfs_number);
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator!=
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Inequality test for two SccIterators. Two SccIterators are
 *                not equal if and only if they are associated with different
 *                graphs or they are associated with the same graph object in
 *                memory but the iterators have processed a different number of
 *                graph nodes.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the inequality test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph != &(it.graph) || dfs_number != it.dfs_number);
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator<
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   `Less than' relation between two SccIterators. An
 *                SccIterator is `less than' another if and only if the
 *                iterators relate to the same graph object in memory and
 *                the first iterator has processed a smaller number of nodes
 *                than the second one.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph == &(it.graph) && dfs_number < it.dfs_number);
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator<=
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   `Less than or equal' relation between two SccIterators. An
 *                SccIterator is `less than or equal to' another if and only
 *                if the iterators relate to the same graph object in memory
 *                and the first iterator has processed a number of nodes not
 *                exceeding the number of nodes the second iterator has
 *                processed.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph == &(it.graph) && dfs_number <= it.dfs_number);
}
  
/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator>
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   `Greater than' relation between two SccIterators. An
 *                SccIterator is `greater than' another if and only if the
 *                iterators relate to the same graph object in memory and
 *                the first iterator has processed a greater number of nodes
 *                than the second one.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph == &(it.graph) && dfs_number > it.dfs_number);
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::operator>=
  (const SccIterator<EdgeContainer, SccContainer, Filter>& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   `Greater than or equal' relation between two SccIterators. An
 *                SccIterator is `greater than or equal to' another if and
 *                only if the iterators relate to the same graph object in
 *                memory and the first iterator has processed at least as many
 *                nodes as the second iterator has processed.
 *
 * Arguments:     it  --  A constant reference to another SccIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return (&graph == &(it.graph) && dfs_number >= it.dfs_number);
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline const SccContainer&
SccIterator<EdgeContainer, SccContainer, Filter>::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for a SccIterator. Returns the
 *                collection of nodes which belong to the maximal strongly
 *                connected component that the iterator currently points to.
 *
 * Arguments:     None.
 *
 * Returns:       A collection of nodes representing some maximal strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  return current_scc;
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline const SccContainer*
SccIterator<EdgeContainer, SccContainer, Filter>::operator->() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for a SccIterator. Returns the
 *                collection of nodes which belong to the maximal strongly
 *                connected component that the iterator currently points to.
 *
 * Arguments:     None.
 *
 * Returns:       A collection of nodes representing some maximal strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  return &current_scc;
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline const SccContainer&
SccIterator<EdgeContainer, SccContainer, Filter>::operator++()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prefix increment operator for a SccIterator. Computes the
 *                next maximal strongly connected component of the graph and
 *                then returns it.
 *
 * Arguments:     None.
 *
 * Returns:       A collection of nodes representing some maximal strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  computeNextScc();
  return current_scc;
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline const SccContainer
SccIterator<EdgeContainer, SccContainer, Filter>::operator++(int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Postfix increment operator for a SccIterator. Effectively
 *                returns the maximal strongly connected component of the graph
 *                currently pointed to by the iterator and then updates the
 *                iterator to point to the next strongly connected component.
 *
 * Arguments:     None (the `int' is only required to distinguish this operator
 *                from the prefix increment operator).
 *
 * Returns:       A collection of nodes representing some maximal strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  SccContainer old_scc = current_scc;
  computeNextScc();
  return old_scc;
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
inline bool SccIterator<EdgeContainer, SccContainer, Filter>::atEnd() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether there are still more strongly connected
 *                components in the graph for the iterator to process.
 *
 * Arguments:     None.
 *
 * Returns:       A truth value.
 *
 * ------------------------------------------------------------------------- */
{
  return (current_node == graph.size());
}
  


/******************************************************************************
 *
 * Function definitions for template class
 * SccIterator<EdgeContainer, SccContainer, Filter>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
void SccIterator<EdgeContainer, SccContainer, Filter>::reset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes the iterator to point to the first maximal
 *                strongly connected component of the graph with which the
 *                iterator it associated.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  dfs_number = 0;
  
  for (typename vector<typename Graph<EdgeContainer>::size_type,
                       ALLOC(typename Graph<EdgeContainer>::size_type) >
	 ::iterator node = dfs_ordering.begin();
       node != dfs_ordering.end();
       ++node)
    *node = 0;
  
  while (!node_stack.empty())
    node_stack.pop();
  
  while (!scc_stack.empty())
    scc_stack.pop();
  
  current_scc.clear();
}

/* ========================================================================= */
template<class EdgeContainer, class SccContainer, class Filter>
void SccIterator<EdgeContainer, SccContainer, Filter>::computeNextScc()
/* ----------------------------------------------------------------------------
 *
 * Description:   Updates the state of the iterator to `point to' the next
 *                maximal strongly connected component of the graph, using the
 *                algorithm due to Tarjan [R. Tarjan. Depth-first search and
 *                linear graph algorithms. SIAM Journal on Computing,
 *                1(2):146--160, June 1972] for computing the next maximal
 *                strongly connected component of the graph.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  current_scc.clear();

  if (scc_stack.empty() && node_stack.empty())
  {
    /*
     *  If both `scc_stack' and `node_stack' are empty (this holds if we have
     *  recently finished processing some component of the graph), try to find
     *  a graph node that has not yet been visited. If no such node is found,
     *  all nodes have been visited and there are no more strongly connected
     *  components to be found in the graph.
     */

    current_node = 0;
    for (typename vector<typename Graph<EdgeContainer>::size_type,
                         ALLOC(typename Graph<EdgeContainer>::size_type) >
	   ::const_iterator node = dfs_ordering.begin();
         node != dfs_ordering.end() && (*node) != 0;
	 ++node)
      ++current_node;
    
    if (current_node == graph.size())
      return;

    /*
     *  Prepare to continue the depth-first search in the unvisited node.
     */
    
    edge = graph[current_node].edges().begin();
    
    scc_stack.push(current_node);
    ++dfs_number;
    dfs_ordering[current_node] = lowlink[current_node] = dfs_number;
  }
  
  typename Graph<EdgeContainer>::size_type child_node;
  
  while (1)
  {
    /*
     *  If there are still nodes left in the depth-first search backtracking
     *  stack, pop a node and its next unprocessed outgoing edge off the stack.
     *  Before continuing the depth-first search in the popped node, update
     *  its lowlink value if necessary. (This has to be done if the lowlink of
     *  the current node---a successor of the popped node---is less than the
     *  lowlink of the popped node but not equal to zero.)
     */

    if (!node_stack.empty())
    {
      typename Graph<EdgeContainer>::size_type father_node
	= node_stack.top().first;
      edge = node_stack.top().second;
      node_stack.pop();

      if (lowlink[current_node] < lowlink[father_node]
          && lowlink[current_node] != 0)
        lowlink[father_node] = lowlink[current_node];

      current_node = father_node;
    }

    /*
     *  Scan through the successors of the current node.
     *
     *  If the current nodes has an unvisited successor node (a successor i
     *  with dfs_ordering[i] == 0), push the current node and its next
     *  unprocessed edge onto the backtracking stack and then continue the
     *  search in the successor node. Push also the successor node onto the
     *  strongly connected component stack.
     *
     *  Otherwise, update the lowlink of the current node to the lowlink of
     *  its already visited successor if necessary.
     */

    while (edge != graph[current_node].edges().end())
    {
      child_node = (*edge)->targetNode();
      ++edge;

      if (dfs_ordering[child_node] == 0)
      {
	node_stack.push(make_pair(current_node, edge));
	scc_stack.push(child_node);

	++dfs_number;
	dfs_ordering[child_node] = lowlink[child_node] = dfs_number;
	
	current_node = child_node;
	edge = graph[current_node].edges().begin();
      } 
      else if (lowlink[child_node] < lowlink[current_node]
	       && lowlink[child_node] != 0)
	lowlink[current_node] = lowlink[child_node];
    } 

    /*
     *  If the least node in the depth-first search order reachable from the
     *  current node is the current node itself at the end of the previous
     *  loop, we have found a maximal strongly connected component of the
     *  graph. In this case, collect the states satisfying `cond' in the
     *  strongly connected component stack to form the component and exit.
     *  (Otherwise, return to the start of the outermost while loop and
     *  continue by popping a state off the depth-first search backtracking
     *  stack.)
     */

    if (dfs_ordering[current_node] == lowlink[current_node])
    {
      do
      {
        child_node = scc_stack.top();
        scc_stack.pop();
        if (cond(&graph[child_node]))
          current_scc.insert(child_node);
        lowlink[child_node] = 0;
      }
      while (child_node != current_node);

      break;
    }
  }
}



/******************************************************************************
 *
 * Inline function definitions for template class NullSccFilter<EdgeContainer>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class EdgeContainer>
inline bool NullSccFilter<EdgeContainer>::operator()
  (const typename Graph<EdgeContainer>::Node*) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Default test for filtering the nodes in a strongly connected
 *                graph component. The default is to simply include all nodes
 *                in the result.
 *
 * Arguments:     A constant pointer to a Graph<EdgeContainer>::Node (required
 *                only to satisfy the class interface requirements).
 *
 * Returns:       true, so the test will succeed for every node in the
 *                component.
 *
 * ------------------------------------------------------------------------- */
{
  return true;
}

}

#endif /* !SCCITERATOR_H */
