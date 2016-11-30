/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
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

#ifndef SCCCOLLECTION_H
#define SCCCOLLECTION_H

#include <config.h>
#include <deque>
#include <map>
#include <set>
#include <utility>
#include <vector>
#include "EdgeContainer.h"
#include "LbttAlloc.h"
#include "Graph.h"

using namespace std;

namespace Graph
{

/******************************************************************************
 *
 * Interface for a "node visitor" object that provides implementations of
 * callback functions to be used during a depth-first search for strongly
 * connected components.  The VisitorInterface class provides empty
 * implementations for the operations; visitor objects can be defined by making
 * them inherit the VisitorInterface and then overriding some (or all) of the
 * interface functions.
 *
 * A "node visitor" object must always implement the following type definition
 * and operation:
 *
 *    SccType
 *        A type definition for storing data associated with a maximal strongly
 *        connected component.  This data type should support copying and
 *        assignment; no other assumptions are made about the internals of this
 *        data type.  It is intended that a node visitor object has a member of
 *        type SccType (which need not be public); this member can then be
 *        manipulated freely during the search for strongly connected
 *        components by giving implementations for the other interface
 *        functions listed below.
 *
 *    const SccType& operator()()
 *        Function for accessing the data associated with a maximal strongly
 *        connected component.  This function is called when dereferencing a
 *        strongly connected component iterator (see below).
 *
 * In addition, a node visitor can overload the default (empty) implementations
 * for the following operations:
 *
 *    void enter(typename GraphType::size_type node_id)
 *        This function is called whenever the search enters a new node of the
 *        graph, using the identifier of the node as a parameter.
 *
 *    void backtrack
 *      (typename GraphType::size_type source_id,
 *       const typename GraphType::Edge& edge,
 *       typename GraphType::size_type target_id)
 *        This function is called when the search backtracks from a node with
 *        the identifier `target_id' to the node `source_id'. `edge' is a
 *        reference to the edge between graph nodes identified by `source_id'
 *        and `target_id'.
 *
 *    void touch
 *      (typename GraphType::size_type source_id,
 *       const typename GraphType::Edge& edge,
 *       typename GraphType::size_type target_id)
 *        This function is called when the search (in node `source_id')
 *        encounters an edge (`edge') with a target node (with identifier
 *        `target_id') that has already been visited during the search.
 *
 *    void leave(typename GraphType::size_type node_id)
 *        This function is called when the search leaves a node with the
 *        identifier `node_id'.
 *
 *    void addEdgeToComponent
 *      (const typename GraphType::Edge& edge,
 *       typename GraphType::size_type component_id)
 *        This function is called when the search finds an edge belonging to a
 *        strongly connected component of the graph (i.e., an edge between two
 *        states in the component).  The function is called with a constant
 *        reference to the edge and a strongly connected component identifier
 *        as parameters.
 *
 *    void addNodeToComponent
 *      (typename GraphType::size_type node_id,
 *       typename GraphType::size_type component_id)
 *        This function is called when the search finds a state belonging to a
 *        strongly connected component of the graph.
 *
 *    void beginComponent
 *      (typename GraphType::size_type component_id,
 *       typename GraphType::size_type component_root_id)
 *        This function is called when the SCC algorithm is about to extract a
 *        new maximal strongly connected component from the graph.  Here,
 *        `component_id' corresponds to the identifier of the component, and
 *        `component_root_id' is the identifier of the node in the component
 *        which was first encountered during the search (the depth-first search
 *        is about to backtrack from this node).
 *
 *    void insert(typename GraphType::size_type node_id)
 *        After a call to `beginComponent', the SCC search algorithm will call
 *        `insert' for each identifier of a node in the maximal strongly
 *        connected component.
 *
 *    void endComponent(typename GraphType::size_type component_id)
 *        This function is called after the SCC algorithm has finished
 *        `insert''ing nodes into a maximal strongly connected component.  The
 *        component identifier is given as a parameter.
 *
 *****************************************************************************/

template <class GraphType>
class VisitorInterface
{
public:
  VisitorInterface();                               /* Constructor. */

  /* default copy constructor */

  virtual ~VisitorInterface();                      /* Destructor. */

  /* default assignment operator */

  virtual void enter                                /* Interface operations. */
    (const typename GraphType::size_type);

  virtual void backtrack
    (const typename GraphType::size_type,
     const typename GraphType::Edge&,
     const typename GraphType::size_type);

  virtual void touch
    (const typename GraphType::size_type,
     const typename GraphType::Edge&,
     const typename GraphType::size_type);

  virtual void leave
    (const typename GraphType::size_type);

  virtual void addEdgeToComponent
    (const typename GraphType::Edge&,
     const typename GraphType::size_type);

  virtual void addNodeToComponent
    (const typename GraphType::size_type,
     const typename GraphType::size_type);

  virtual void beginComponent
    (const typename GraphType::size_type,
     const typename GraphType::size_type);

  virtual void insert
    (const typename GraphType::size_type);

  virtual void endComponent
    (const typename GraphType::size_type);
};



/******************************************************************************
 *
 * A template class defining a node visitor for collecting the identifiers of
 * nodes in a maximal strongly connected component into a set.
 *
 *****************************************************************************/

template <class GraphType>
class SccCollector : public VisitorInterface<GraphType>
{
public:
  SccCollector();                                   /* Constructor. */

  /* default copy constructor */

  ~SccCollector();                                  /* Destructor. */

  /* default assignment operator */

  typedef set<typename GraphType::size_type>        /* Type definition for */
    SccType;                                        /* a set of node id's. */

  const SccType& operator()() const;                /* Returns the set of node
						     * identifiers in a
						     * maximal strongly
						     * connected component.
						     */

  /* `enter' inherited */

  /* `backtrack' inherited */

  /* `touch' inherited */

  /* `leave' inherited */

  /* `addEdgeToComponent' inherited */

  /* `addNodeToComponent' inherited */

  void beginComponent                               /* Function called       */
    (const typename GraphType::size_type,           /* before inserting      */
     const typename GraphType::size_type);          /* nodes in a component. */

  void insert                                       /* Function for         */
    (const typename GraphType::size_type node_id);  /* inserting nodes into
						     * a component.
						     */

  /* `endComponent' inherited */

private:
  SccType scc;                                      /* A set of node
						     * identifiers representing
						     * a maximal strongly
						     * connected component.
						     */
};



/******************************************************************************
 *
 * A template class for defining a "container" of maximal strongly connected
 * components of a graph.  The template should be instantiated with two classes
 * GraphType and NodeVisitor, where the NodeVisitor type should support the
 * interface required of a node visitor (see the above documentation of
 * VisitorInterface), and GraphType (which defaults to
 * Graph<GraphEdgeContainer>) should support the following type definitions and
 * operations:
 *
 *     size_type
 *         A type that can be used for identifying nodes (uniquely) in the
 *         graph.  This type should have a constructor taking no parameters,
 *         and it should support copying, assignment, and comparison using the
 *         `less than' operator.
 *
 *     EdgeContainerType
 *         A type that represents a container of objects behaving similarly to
 *         pointers to edges in the graph.  This type is expected to have an
 *         STL-like container interface with `begin()' and `end()' operations
 *         and a const_iterator with a constructor having no parameters and
 *         copying and assignment operations.
 *
 *         The objects in the container should behave similarly to pointers
 *         that can be dereferenced using the * and -> operators to get access
 *         to objects (the edges in the graph, e.g.,
 *         Graph<GraphEdgeContainer>::Edge) that support the operation
 *             size_type targetNode()
 *         that returns the identifier of an edge's target node in the graph.
 *
 *     PathElement
 *         An object for representing (node_id, edge) pairs of the graph.
 *         The object should provide a constructor that can be called with two
 *         parameters: a parameter of type `size_type' and another parameter
 *         corresponding to the type obtained by dereferencing a pointer-like
 *         object stored in an object of type EdgeContainerType.
 *
 *     Path
 *         A type that supports the `clear' operation (with no arguments) and
 *         the `push_front' operation with an argument of type PathElement.
 *
 *     operator[](size_type node_id)
 *         This function should return an object that provides access to the
 *         edges starting from the graph node identified by `node_id' through
 *         the member function
 *             const EdgeContainerType& edges(),
 *         which returns the collection of (pointer-like objects to) edges
 *         beginning from the graph node with the identifier `node_id'.
 *
 *     bool empty()
 *         This function should return true iff the graph contains no nodes.
 *
 * The NodeVisitor type defaults to SccCollector<GraphType>.
 *
 * The SccCollection class provides the following operations:
 *
 *     SccCollection(const GraphType& g, NodeVisitor& node_visitor)
 *         Constructor that binds the SccCollection object to the graph `g'
 *         using the operations defined by `node_visitor' when visiting nodes
 *         of the graph during the search for strongly connected components.
 *
 *     iterator begin(const typename GraphType::size_type initial_node_id)
 *         Returns an iterator to the strongly connected components of the
 *         graph.  The function returns an iterator that points to the "first"
 *         maximal strongly connected component of the graph when starting the
 *         search for maximal strongly connected components from the node with
 *         the identifier `initial_node_id'.
 *
 *         Note: If `initial_node_id' belongs to a strongly connected component
 *         of the graph which has alredy been visited by an iterator obtained
 *         by a previous call to `begin', the returned iterator is equal to
 *         `end()'.  If this is not desired (i.e., if you wish to repeat the
 *         search for strongly connected components in a part of the graph,
 *         initialize a new SccCollection to the graph).
 *
 *     iterator end()
 *         Returns an iterator pointing "past the end" of the collection of
 *         strongly connected components.
 *
 * The class `SccCollection::iterator' provides standard dereferencing and
 * prefix and postfix increment operators.  It also supports comparison for
 * equality and inequality.  See below for more detailed information.
 *
 *****************************************************************************/

template <class GraphType = Graph<GraphEdgeContainer>,
          class NodeVisitor = SccCollector<GraphType> >
class SccCollection
{
public:
  SccCollection                                     /* Constructor. */
    (const GraphType& g,
     NodeVisitor& node_visitor);

  ~SccCollection();                                 /* Destructor. */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  typedef map<typename GraphType::size_type,        /* Type definition for a */
              typename GraphType::size_type>        /* mapping between node  */
    DfsOrdering;                                    /* identifiers and the
                                                     * order in which they
                                                     * were encountered in
						     * the search for
						     * strongly connected
						     * components.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class iterator                                    /* Iterator for          */
  {						    /* accessing the maximal
						     * strongly connected
						     * components.
						     */
  public:
    iterator();                                     /* Default constructor. */

    /* default copy constructor */

    ~iterator();                                    /* Destructor. */

    /* default assignment operator */

    bool operator==(const iterator& it) const;      /* Comparison functions. */
    bool operator!=(const iterator& it) const;

    const typename NodeVisitor::SccType&            /* Dereferencing */
      operator*() const;                            /* operators.    */
    const typename NodeVisitor::SccType*
      operator->() const;

    const typename NodeVisitor::SccType&            /* Prefix and postfix   */
      operator++();                                 /* increment operators. */
    const typename NodeVisitor::SccType
      operator++(int);

    void getPath(typename GraphType::Path& path);   /* Function for accessing
						     * the path from the
						     * initial node of the
						     * search to a node in
						     * the most recently found
						     * strongly connected
						     * component.
						     */

  private:
    iterator                                        /* Constructor. */
      (const GraphType& g,
       NodeVisitor& node_visitor,
       DfsOrdering& ordering);

    void initialize                                 /* Instructs the        */
      (const typename GraphType::size_type          /* iterator to continue */
         node_id);                                  /* the search for
						     * strongly connected
						     * components from a
						     * given node.
						     */

    const GraphType& graph;                         /* Reference to the graph
						     * with which the iterator
						     * is associated.
						     */

    NodeVisitor& visitor;                           /* Reference to an object
						     * that provides
						     * implementations for the
						     * functions listed in the
						     * VisitorInterface class.
						     */

    DfsOrdering& dfs_ordering;                      /* Mapping between node
						     * identifiers and their
						     * dfs numbers.
						     */

    typename GraphType::size_type initial_node;     /* Node from which the
						     * search was started.
						     */

    typename GraphType::size_type dfs_number;       /* Number of graph nodes
						     * processed by the
						     * iterator.
						     */

    struct NodeStackElement                         /* Structure for       */
    {                                               /* storing information */
      typename GraphType::size_type id;             /* needed for          */
      typename GraphType::EdgeContainerType         /* backtracking during */
        ::const_iterator edge;                      /* the search.         */
      typename GraphType::size_type lowlink;   
    };

    deque<NodeStackElement> node_stack;             /* Depth-first search
						     * backtracking stack.
						     */


    NodeStackElement* current_node;                 /* Pointer to the top
						     * element of the
						     * backtracking stack.
						     */

    deque<typename GraphType::size_type> scc_stack; /* Stack used for
                                                     * collecting the nodes
                                                     * in a strongly
						     * connected component,
						     * excluding the root
						     * nodes of the
						     * components.
						     */

    void computeNextScc();                          /* Updates the iterator to
						     * point to the next
						     * strongly connected
						     * component.
						     */

    friend class SccCollection;
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  iterator begin                                    /* Returns an iterator */
    (const typename GraphType::size_type            /* pointing to the     */
       node_id);                                    /* "first" maximal
						     * strongly connected
						     * component reachable
						     * from a given node.
						     */

  const iterator& end() const;                      /* Returns an iterator
						     * pointing past the "end"
						     * of the maximal strongly
						     * connected components.
						     */

private:
  SccCollection(const SccCollection&);              /* Prevent copying and */
  SccCollection& operator=(const SccCollection&);   /* assignment of
                                                     * SccCollection
						     * objects.
						     */

  const GraphType& graph;                           /* Reference to the graph
						     * associated with the
						     * container.
						     */

  NodeVisitor& visitor;                             /* Reference to an object
						     * that provides
						     * implementations for
						     * callback operations
						     * needed during the
						     * search for strongly
						     * connected components.
						     */

  const iterator end_iterator;                      /* Iterator pointing past
						     * the "end" of the
						     * collection of strongly
						     * connected components in
						     * the graph.  This
						     * iterator is special by
						     * having both
						     * `initial_node' and
						     * `dfs_number' set to 0.
						     */

  DfsOrdering dfs_ordering;                         /* Mapping between node
						     * identifiers and their
						     * visiting order during
						     * the search for strongly
						     * connected components.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for template class VisitorInterface.  This class
 * provides default empty implementations for node visitor operations.
 *
 *****************************************************************************/

template <class GraphType>
inline VisitorInterface<GraphType>::VisitorInterface()
{
}

template <class GraphType>
inline VisitorInterface<GraphType>::~VisitorInterface()
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::enter
  (const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::backtrack
  (const typename GraphType::size_type, const typename GraphType::Edge&,
   const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::touch
  (const typename GraphType::size_type, const typename GraphType::Edge&,
   const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::leave
  (const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::addEdgeToComponent
  (const typename GraphType::Edge&, const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::addNodeToComponent
  (const typename GraphType::size_type, const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::beginComponent
  (const typename GraphType::size_type, const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::insert
  (const typename GraphType::size_type)
{
}

template <class GraphType>
inline void VisitorInterface<GraphType>::endComponent
  (const typename GraphType::size_type)
{
}



/******************************************************************************
 *
 * Inline function definitions for template class SccCollector.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class GraphType>
inline SccCollector<GraphType>::SccCollector()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class SccCollector.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType>
inline SccCollector<GraphType>::~SccCollector()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class SccCollector.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType>
inline const typename SccCollector<GraphType>::SccType&
SccCollector<GraphType>::operator()() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the set of identifiers of nodes in the maximal
 *                strongly connected component.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the set of identifiers of nodes in
 *                the component.
 *
 * ------------------------------------------------------------------------- */
{
  return scc;
}

/* ========================================================================= */
template <class GraphType>
inline void SccCollector<GraphType>::beginComponent
  (const typename GraphType::size_type, const typename GraphType::size_type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Clears the set of node identifiers to make it empty before
 *                filling it with (identifiers of) nodes of a new maximal
 *                strongly connected component.
 *
 * Arguments:     The arguments are needed to support the expected function
 *                calling interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  scc.clear();
}

/* ========================================================================= */
template <class GraphType>
inline void SccCollector<GraphType>::insert
  (const typename GraphType::size_type node_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts an identifier into the set of node identifiers in a
 *                maximal strongly connected component.
 *
 * Arguments:     node_id  --  Node identifier.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  scc.insert(node_id);
}



/******************************************************************************
 *
 * Inline function definitions for template class SccCollection.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline SccCollection<GraphType, NodeVisitor>::SccCollection
  (const GraphType& g, NodeVisitor& node_visitor) :
  graph(g), visitor(node_visitor), end_iterator(g, node_visitor, dfs_ordering)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class SccCollection.
 *
 * Arguments:     g             --  A constant reference to an object of type
 *                                  GraphType.  See above for the description
 *                                  of the interface that this object should
 *                                  support.
 *                node_visitor  --  A reference to an object that provides
 *                                  node visiting operations.  See the
 *                                  documentation of VisitorInterface for more
 *                                  information.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline SccCollection<GraphType, NodeVisitor>::~SccCollection()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class SccCollection.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline typename SccCollection<GraphType, NodeVisitor>::iterator
SccCollection<GraphType, NodeVisitor>::begin
  (const typename GraphType::size_type node_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an iterator pointing to the "first" strongly
 *                connected component reachable from a given node of
 *                `this->graph'.
 *
 * Argument:      node_id  --  Identifier of the node.
 *
 * Returns:       An iterator pointing to the "first" strongly connected
 *                component reachable from the node, or an iterator equal to
 *                `this->end()' if the node with the identifier `node_id' has
 *                already been included in a strongly connected component
 *                returned by another iterator to the same collection of
 *                strongly connected components.
 *
 * ------------------------------------------------------------------------- */
{
  iterator it(graph, visitor, dfs_ordering);
  it.initialize(node_id);
  return it;
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline const typename SccCollection<GraphType, NodeVisitor>::iterator&
SccCollection<GraphType, NodeVisitor>::end() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an iterator pointing past the "end" of the collection
 *                of strongly connected components in `this->graph'.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to `this->end_iterator'.
 *
 * ------------------------------------------------------------------------- */
{
  return end_iterator;
}



/******************************************************************************
 *
 * Inline function definitions for template class SccCollection::iterator.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline SccCollection<GraphType, NodeVisitor>::iterator::iterator
  (const GraphType& g, NodeVisitor& node_visitor,
   SccCollection<GraphType, NodeVisitor>::DfsOrdering& ordering) :
  graph(g), visitor(node_visitor), dfs_ordering(ordering), initial_node(0),
  dfs_number(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.  Initializes
 *                a new iterator for scanning the maximal strongly connected
 *                components of a graph.
 *
 * Arguments:     g             --  The graph with which the iterator is to be
 *                                  associated.
 *                node_visitor  --  A reference to a object that implements
 *                                  callback functions to be invoked during the
 *                                  search for strongly connected components.
 *                ordering      --  A reference to a mapping between node
 *                                  identifiers and their dfs numbers.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline SccCollection<GraphType, NodeVisitor>::iterator::~iterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline bool SccCollection<GraphType, NodeVisitor>::iterator::operator==
  (const SccCollection<GraphType, NodeVisitor>::iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equality comparison between two SccCollection::iterators. Two
 *                iterators are equal iff they have the same initial state and
 *                they have visited the same amount of graph nodes.
 *
 * Argument:      it  --  A constant reference to an iterator.
 *
 * Returns:       true iff the iterators are equal.
 *
 * ------------------------------------------------------------------------- */
{
  return (it.initial_node == initial_node && it.dfs_number == dfs_number);
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline bool SccCollection<GraphType, NodeVisitor>::iterator::operator!=
  (const SccCollection<GraphType, NodeVisitor>::iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Inequality comparison between two SccCollection::iterators.
 *                Two iterators are not equal iff they have different initial
 *                states or if they have visited different numbers of graph
 *                nodes.
 *
 * Argument:      it  --  A constant reference to an iterator.
 *
 * Returns:       true iff the iterators are not equal.
 *
 * ------------------------------------------------------------------------- */
{
  return (it.initial_node != initial_node || it.dfs_number != dfs_number);
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline const typename NodeVisitor::SccType&
SccCollection<GraphType, NodeVisitor>::iterator::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.  Returns the
 *                data associated with the maximal strongly connected component
 *                currently pointed to by the iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the data associated with the strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  return visitor();
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline const typename NodeVisitor::SccType*
SccCollection<GraphType, NodeVisitor>::iterator::operator->() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.  Returns a
 *                pointer to the data associated with the maximal strongly
 *                connected component currently pointed to by the iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A constant pointer to the data associated with the strongly
 *                connected component.
 *
 * ------------------------------------------------------------------------- */
{
  return &visitor();
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline const typename NodeVisitor::SccType&
SccCollection<GraphType, NodeVisitor>::iterator::operator++()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prefix increment operator for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.  Computes
 *                the next maximal strongly connected component of
 *                `this->graph' and then returns the data associated with it.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the data associated with the maximal strongly
 *                connected component found by incrementing the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  computeNextScc();
  return visitor();
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
inline const typename NodeVisitor::SccType
SccCollection<GraphType, NodeVisitor>::iterator::operator++(int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Postfix increment operator for class
 *                SccCollection<GraphType, NodeVisitor>::iterator.  Computes
 *                the next strongly connected component of
 *                `this->graph', but returns the data associated with the
 *                strongly connected component that the iterator pointed to
 *                _before_ this operation.
 *
 * Arguments:     None (the `int' is only required to distinguish this operator
 *                from the prefix increment operator).
 *
 * Returns:       The data associated with the maximal strongly connected
 *                component pointed to by the iterator before incrementing it.
 *
 * ------------------------------------------------------------------------- */
{
  const typename NodeVisitor::SccType old_scc = visitor();
  computeNextScc();
  return old_scc;
}



/******************************************************************************
 *
 * Function definitions for template class
 * SccCollection<GraphType, NodeVisitor>.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
void SccCollection<GraphType, NodeVisitor>::iterator::getPath
  (typename GraphType::Path& path)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs a path from the initial state of the search for
 *                the strongly connected components to a node in the most
 *                recently found maximal strongly connected graph component.
 *
 * Argument:      path  --  A reference to a GraphType::Path object for storing
 *                          the (node_id, edge) pairs in the path.
 *
 * Returns:       Nothing.  Assuming that `path' is a standard STL container
 *                type object with the `push_front' operation, `*path.begin()'
 *                will correspond to the first element on the path after the
 *                call.
 *
 * ------------------------------------------------------------------------- */
{
  path.clear();

  /*
   * When this function is called after extracting a maximal strongly connected
   * component from the graph, `node_stack.front()' corresponds to the root
   * node of the component.  This node will not be included in the path
   * (that is, the edge component of `path.back()' will point to this node
   * when exiting from this function).
   */

  typename deque<NodeStackElement>::const_iterator n = node_stack.begin();
  if (n != node_stack.end())
  {
    for (++n; n != node_stack.end(); ++n)
    {
      const typename GraphType::PathElement element(n->id, **n->edge);
      path.push_front(element);
    }
  }
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
void SccCollection<GraphType, NodeVisitor>::iterator::initialize
  (const typename GraphType::size_type node_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes a
 *                SccCollection<GraphType, NodeVisitor>::iterator for scanning
 *                the maximal strongly connected components of `this->graph'.
 *
 * Argument:      node_id  --  Identifier of a graph node from which to start
 *                             the search for maximal strongly connected
 *                             components.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  node_stack.clear();
  scc_stack.clear();

  /*
   *  If `node_id' is an identifier of a node that has not yet been visited,
   *  make the iterator point to the next strongly connected component of
   *  `this->graph'.
   */

  if (!graph.empty() && dfs_ordering.find(node_id) == dfs_ordering.end())
  {
    initial_node = node_id;
    visitor.enter(node_id);
    dfs_ordering[node_id] = dfs_number = 1;
    const NodeStackElement element
      = { node_id, graph[node_id].edges().begin(), 1 };
    node_stack.push_front(element);
    current_node = &node_stack.front();
    computeNextScc();
  }
}

/* ========================================================================= */
template <class GraphType, class NodeVisitor>
void SccCollection<GraphType, NodeVisitor>::iterator::computeNextScc()
/* ----------------------------------------------------------------------------
 *
 * Description:   Makes an SccCollection<GraphType, NodeVisitor>::iterator
 *                point to the "next" maximal strongly connected component of
 *                `this->graph', using an algorithm based on the depth-first
 *                search algorithm of Tarjan
 *                    [R. J. Tarjan. Depth-first search and linear graph
 *                     algorithms.  SIAM Journal on Computing 1(2):146--160,
 *                     1972]
 *                for computing the maximal strongly connected components of
 *                the graph.
 *
 *                The implementation includes the optimization in the first
 *                improved algorithm found in
 *                    [E. Nuutila and E. Soisalon-Soininen.  On finding the
 *                     strongly connected components in a directed graph.
 *                     Information Processing Letters 49(1):9--14, 1994].
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (current_node->lowlink == 0)
  {
    /* Backtrack from the root of an SCC that was extracted from the graph */
    const typename GraphType::size_type child_node = current_node->id;
    node_stack.pop_front();
    if (node_stack.empty())
    {
      initial_node = dfs_number = 0;
      return;
    }
    current_node = &node_stack.front();
    visitor.backtrack(current_node->id, **current_node->edge, child_node);
    ++current_node->edge; /* prepare to process the next edge */
  }

next_edge:
  while (current_node->edge != graph[current_node->id].edges().end())
  {
    const typename GraphType::size_type child_node
      = (*current_node->edge)->targetNode();
    const typename DfsOrdering::const_iterator child_dfs_number_finder
      = dfs_ordering.find(child_node);

    if (child_dfs_number_finder == dfs_ordering.end()) /* child not visited */
    {
      ++dfs_number;
      dfs_ordering[child_node] = dfs_number;
      visitor.enter(child_node);
      const NodeStackElement element 
	= { child_node, graph[child_node].edges().begin(), dfs_number };
      node_stack.push_front(element);
      current_node = &node_stack.front();
      goto next_edge;
    }

    visitor.touch(current_node->id, **current_node->edge, child_node);
    if (child_dfs_number_finder->second != 0) /* child in the same SCC */
    { 
      if (child_dfs_number_finder->second < current_node->lowlink)
	current_node->lowlink = child_dfs_number_finder->second;
      visitor.addEdgeToComponent(**current_node->edge, current_node->lowlink);
    }

    ++current_node->edge;
  } 

  visitor.addNodeToComponent(current_node->id, current_node->lowlink);
  visitor.leave(current_node->id);

  if (dfs_ordering.find(current_node->id)->second != current_node->lowlink)
  {
    scc_stack.push_front(current_node->id);

    /* Backtrack from a node into a node in the same SCC */
    const typename GraphType::size_type child_node = current_node->id;
    const typename GraphType::size_type child_lowlink = current_node->lowlink;
    node_stack.pop_front();
    current_node = &node_stack.front();
    if (current_node->lowlink > child_lowlink)
      current_node->lowlink = child_lowlink;
    visitor.backtrack(current_node->id, **current_node->edge, child_node);
    visitor.addEdgeToComponent(**current_node->edge, current_node->lowlink);
    ++current_node->edge; /* prepare to process the next edge */
    goto next_edge;
  }

  /*
   * `current_node' is a root of a maximal strongly connected graph component.
   * Extract the component from the graph.
   */

  visitor.beginComponent(current_node->lowlink, current_node->id);
  visitor.insert(current_node->id);
  dfs_ordering.find(current_node->id)->second = 0;
  while (!scc_stack.empty())
  {
    const typename GraphType::size_type node = scc_stack.front();
    typename GraphType::size_type& node_dfs_number
      = dfs_ordering.find(node)->second;
    if (node_dfs_number > current_node->lowlink)
    {
      scc_stack.pop_front();
      visitor.insert(node);
      node_dfs_number = 0;
    }
    else
      break;
  }
  visitor.endComponent(current_node->lowlink);
  current_node->lowlink = 0;
}

}

#endif /* !SCCCOLLECTION_H */
