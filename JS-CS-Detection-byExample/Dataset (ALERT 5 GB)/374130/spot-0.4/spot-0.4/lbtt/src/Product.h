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

#ifndef PRODUCT_H
#define PRODUCT_H

#include <config.h>
#include <deque>
#include <map>
#include <set>
#include <utility>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "EdgeContainer.h"
#include "Exception.h"
#include "Graph.h"
#include "SccCollection.h"

using namespace std;

extern bool user_break;

namespace Graph 
{

/******************************************************************************
 *
 * A template class for representing the product of two
 * Graph<GraphEdgeContainer> objects (which, in lbtt, are always either two
 * BuchiAutomaton objects, or a BuchiAutomaton and a StateSpace).
 *
 * The class provides functions for checking the products of these objects for
 * emptiness (i.e., for two Büchi automata, whether the intersection of their
 * languages is (non)empty; for a Büchi automaton and a state space, whether
 * some infinite path in the state space is accepted by the automaton).  The
 * functions are as follows:
 *
 *    * bool localEmptinessCheck
 *        (Graph<GraphEdgeContainer>::size_type,
 *         Graph<GraphEdgeContainer>::size_type)
 *              Checks whether the subproduct rooted at a product state
 *              determined by a pair of component state identifiers is not
 *              empty and returns true if this is the case.
 *
 *    * pair<Graph<GraphEdgeContainer>::size_type, unsigned long int>
 *        globalEmptinessCheck
 *          (Graph<GraphEdgeContainer>::size_type state, Bitset&,
 *           unsigned long int emptiness_check_size)
 *              Checks a set of subproducts for emptiness and stores the
 *              results in a bit set that should have room for (at least)
 *              `emptiness_check_size' bits (this number is assumed to be less
 *              than the number of states in the second component of the
 *              product).  The first parameter `state' identifies a state in
 *              the first component of the product.  After the call, the i'th
 *              bit (for all 0 <= i < emptiness_check_size) in the bit set will
 *              then be 1 iff the subproduct rooted at the product state
 *              determined by the pair of state identifiers (state, i) is
 *              nonempty.  The function returns a pair of numbers corresponding
 *              to the number of product states and transitions generated
 *              during the emptiness check.
 *
 *    * void findWitness
 *        (Graph<GraphEdgeContainer>::size_type,
 *         Graph<GraphEdgeContainer>::size_type,
 *         Product<Operations>::Witness&)
 *              Checks whether the subproduct rooted at a product state
 *              determined by a pair of component state identifiers is not
 *              empty.  If this is the case, the function constructs a
 *              certificate (a "witness") for the nonemptiness.  For the
 *              product of two Büchi automata, the witness is an accepting
 *              execution from both automata on the same input; for the product
 *              of a Büchi automaton and a state space, the witness is a path
 *              in the state space that is accepted by the automaton.
 *
 * All of these functions construct the product "on the fly" with the help of
 * operations provided by the class Operations used for instantiating the
 * template. The public interface of this class must support the following
 * operations:
 *
 *    * Operations(const Graph<GraphEdgeContainer>&,
 *                 const Graph<GraphEdgeContainer>&)
 *              Constructor that accepts references to the first and second
 *              component of the product (in this order) as parameters.
 *
 *    * bool empty()
 *              A predicate which returns "true" iff either of the product
 *              components is (trivially) empty, i.e., iff either component has
 *              no states.
 *
 *    * unsigned long int numberOfAcceptanceSets()
 *              Returns the number of acceptance sets associated with a state
 *              or a transition in the product.
 *
 *    * const Graph<GraphEdgeContainer>::Node& firstComponent
 *        (Graph<GraphEdgeContainer>::size_type),
 *      const Graph<GraphEdgeContainer>::Node& secondComponent
 *        (Graph<GraphEdgeContainer>::size_type)
 *              Functions for accessing the states in the individual product
 *              components such that firstComponent(i) (secondComponent(i))
 *              returns a reference to the i'th state of the first (second)
 *              component in the product.
 *
 *    * void mergeAcceptanceInformation
 *        (const Graph<GraphEdgeContainer>::Node&,
 *         const Graph<GraphEdgeContainer>::Node&,
 *         BitArray&)
 *              Updates the acceptance information of a product state
 *              (determined by a state of the first and the second component,
 *              respectively) into a BitArray that is guaranteed to have room
 *              for at least numberOfAcceptanceSets() bits.  The function
 *              should not clear bits in the array.
 *
 *    * void mergeAcceptanceInformation
 *        (const Graph<GraphEdgeContainer>::Edge&,
 *         const Graph<GraphEdgeContainer>::Edge&,
 *         BitArray&
 *              Updates the acceptance information of a product transition
 *              (corresponding to a pair of transitions of the first and second
 *              product component) into a BitArray (guaranteed to have room for
 *              at least numberOfAcceptanceSets() bits).  The function should
 *              not clear bits in the array.
 *
 *    * void validateEdgeIterators
 *        (const Graph<GraphEdgeContainer>::Node& node_1,
 *         const Graph<GraphEdgeContainer>::Node& node_2,
 *         GraphEdgeContainer::const_iterator iterator_1&,
 *         GraphEdgeContainer::const_iterator iterator_2&)
 *              Checks whether a pair of edges determined from a pair of
 *              iterators corresponds to an edge starting from a given state
 *              (node_1, node_2) in the product.  If yes, the function should
 *              leave the iterators intact; otherwise the iterators should be
 *              updated such that they point to a pair of edges corresponding
 *              to an edge in the product (or to `node_1.edges().end()' and
 *              `node_2.edges().end()' if this is not possible).
 *                  Calling the function with the iterators initialized to
 *              `node_1.edges().begin()' and `node_2.edges().begin()' should
 *              update the iterators such that repeated calls to
 *              `incrementEdgeIterators' (see below) result in an enumeration
 *              of all product edges beginning from the product state
 *              (node_1, node_2).
 *
 *    * void incrementEdgeIterators
 *        (const Graph<GraphEdgeContainer>::Node& node_1,
 *         const Graph<GraphEdgeContainer>::Node& node_2,
 *         GraphEdgeContainer::const_iterator iterator_1&,
 *         GraphEdgeContainer::const_iterator iterator_2&)
 *              Updates a pair of edge iterators to point to the "next" edge
 *              starting from a given state (node_1, node_2) in the product
 *              (or to (node_1.edges().end(), node_2.edges().end()) if this is
 *              not possible).
 *
 * See the files BuchiProduct.h and StateSpaceProduct.h for examples of classes
 * used for instantiating the template.
 *
 * Given a class suitable for instantiating the Product template, a product is
 * built with the constructor
 *      Product<Operations>::Product
 *        (const Graph<GraphEdgeContainer>& graph_1,
 *         const Graph<GraphEdgeContainer>& graph_2).
 * The product can be then analyzed by calling one of the emptiness checking
 * functions described above.
 *
 * Note: All emptiness checking functions fail by throwing an exception of type
 *       Product<Operations>::SizeException if
 *       `graph_1.size() * graph_2.size()' exceeds the maximum integer
 *       representables using Graph<GraphEdgeContainer>::size_type.  The
 *       implementation does not support such products.
 *
 * Note: Operations in the Product class are not re-entrant.
 *
 *****************************************************************************/

template <class Operations>
class Product
{
public:
  Product                                           /* Constructor. */
    (const Graph<GraphEdgeContainer>& g1,
     const Graph<GraphEdgeContainer>& g2);

  ~Product();                                       /* Destructor. */

  typedef typename Graph<GraphEdgeContainer>        /* Type of product state */
    ::size_type size_type;                          /* identifiers.          */

  class ProductState;                               /* A class for accessing
						     * states in the product.
						     */

  const ProductState operator[]                     /* Indexing operator. */
    (const size_type index) const;

  size_type stateId                                 /* Constructs a product  */
    (const size_type state_1,                       /* state identifier from */
     const size_type state_2) const;                /* the identifiers of
						     * the state components.
						     */

  const Graph<GraphEdgeContainer>::Node&            /* Functions for   */
    firstComponent(const size_type state) const;    /* accessing the   */
  const Graph<GraphEdgeContainer>::Node&            /* components of a */
    secondComponent(const size_type state) const;   /* product state.  */

  bool empty() const;                               /* Tells whether the
						     * product is (trivially)
						     * empty.
						     */

  struct Witness                                    /* Structure for        */
  {                                                 /* representing witness */
    pair<Graph<GraphEdgeContainer>::Path,           /* paths for            */
	 Graph<GraphEdgeContainer>::Path>           /* the nonemptiness of  */
      prefix;                                       /* the product.         */
    pair<Graph<GraphEdgeContainer>::Path,
	 Graph<GraphEdgeContainer>::Path>
      cycle;
  };

  bool localEmptinessCheck                          /* Checks whether the    */
    (const typename Graph<GraphEdgeContainer>       /* subproduct rooted at  */
       ::size_type s1_id,                           /* a product state       */
     const typename Graph<GraphEdgeContainer>       /* determined by a pair  */
       ::size_type s2_id);                          /* of component state
						     * identifiers is empty.
						     */

  const pair<size_type, unsigned long int>          /* Checks a set of */
    globalEmptinessCheck                            /* subproducts for */
    (const typename Graph<GraphEdgeContainer>       /* emptiness (see  */
       ::size_type state_id,                        /* above).         */
     Bitset& result,
     const unsigned long int emptiness_check_size);

  void findWitness
    (const size_type s1_id, const size_type s2_id,  /* Checks whether the    */
     Witness& witness);                             /* subproduct rooted at
						     * a product state
						     * determined by a pair
						     * of component state
						     * identifiers is empty.
						     * If this is the case,
						     * constructs also a
						     * witness for the
						     * nonemptiness.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ProductEdge;                                /* Classes for        */
  class ProductEdgePointer;                         /* representing
						     * transitions in the
						     * product and
						     * "pointer-like"
						     * objects to them.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ProductEdgeCollection                       /* A class that mimics
						     * a container for
						     * transitions starting
						     * from a product state.
						     * (The container does
						     * not actually store the
						     * transitions; instead,
						     * it provides functions
						     * for constructing
						     * iterators that can be
						     * used to generate the
						     * transitions.)
						     */
  {
  public:
    explicit ProductEdgeCollection                  /* Constructor. */
      (const size_type state);

    /* default copy constructor */

    ~ProductEdgeCollection();                       /* Destructor. */

    /* default assignment operator */

    class const_iterator                            /* Iterator for generating
                                                     * the transitions starting
						     * from a product state.
						     */
    {
    public:
      const_iterator();                             /* Default constructor. */

      const_iterator
	(const size_type state,                     /* Constructor. */
	 const GraphEdgeContainer::const_iterator&
	   e1,
	 const GraphEdgeContainer::const_iterator&
	   e2);

      /* default copy constructor */

      ~const_iterator();                            /* Destructor. */

      /* default assignment operator */

      bool operator==(const const_iterator& it)     /* Equality test between */
	const;                                      /* iterators.            */

      bool operator!=(const const_iterator& it)     /* Inequality test    */
	const;                                      /* between iterators. */

      const ProductEdgePointer operator*() const;   /* Dereferencing */
      const ProductEdge operator->() const;         /* operators.    */

      const ProductEdgePointer operator++();        /* Prefix and postfix   */
      const ProductEdgePointer operator++(int);     /* increment operators. */

    private:
      size_type product_state;                      /* Product state       */
                                                    /* associated with the
						     * iterator.
						     */

      GraphEdgeContainer::const_iterator edge_1;    /* Pair of iterators     */
      GraphEdgeContainer::const_iterator edge_2;    /* from which product
						     * edges are determined.
						     */
    };

    const const_iterator begin() const;             /* Returns an iterator to
						     * the "beginning" of the
						     * list of transitions
						     * starting from the
						     * product state
						     * `this->product_state'.
						     */

    const const_iterator end() const;               /* Returns an iterator to
						     * the "end" of the list of
						     * transitions starting
						     * from the product state
						     * `this->product_state'.
						     */

  private:
    size_type product_state;                        /* Product state associated
						     * with the transition
						     * container.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class SizeException : public Exception            /* An exception class to */
                                                    /* be used in cases      */
                                                    /* where `size_type'     */
                                                    /* cannot hold values    */
                                                    /* large enough to       */
                                                    /* accommodate the       */
                                                    /* largest identifier    */
                                                    /* for a product state.  */
  {               
  public:
    SizeException();                                /* Constructor. */

    /* default copy constructor */

    ~SizeException() throw();                       /* Destructor. */

    SizeException& operator=                        /* Assignment operator. */
      (const SizeException& e);    

    /* `what' inherited from class Exception */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  typedef ProductEdge Edge;                         /* Type definitions    */
  typedef ProductEdgeCollection EdgeContainerType;  /* required for making */
						    /* Product<Operations> */
  struct PathElement;				    /* suitable for        */
  typedef deque<PathElement> Path;                  /* instantiating the
                                                     * SccCollection
						     * template (see
						     * SccCollection.h).
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

private:
  Product(const Product&);                          /* Prevent copying and   */
  Product& operator=(const Product&);               /* assignment of Product
						     * objects.
						     */

  class AcceptanceTracker;                          /* Callback operations */
  class SimpleEmptinessChecker;                     /* used when searching */
  class AcceptanceReachabilityTracker;              /* the product for     */
  class AcceptingComponentFinder;                   /* strongly connected
						     * components.
						     */

  void addCycleSegment                              /* Helper function for  */
    (pair<Graph<GraphEdgeContainer>::Path,          /* constructing a       */
          Graph<GraphEdgeContainer>::Path >& cycle, /* segment of the cycle */
     size_type source_state_id, Edge transition,    /* in a witness for the */
     const size_type root_id,                       /* nonemptiness of the  */
     const map<size_type, PathElement>&             /* product.             */
       predecessor) const;

  Operations operations;                            /* Operations for
						     * building the product
						     * on the fly.
						     */

  bool too_large;                                   /* Will be set to true
						     * if `size_type' cannot
						     * hold the maximum value
						     * that may be required for
						     * product state
						     * identifiers.  Calling
						     * one of the emptiness
						     * checking operations on
						     * such a product results
						     * in a run-time exception.
						     */

  const size_type state_id_multiplier;              /* Size of the "second"
						     * component of the
						     * product.
						     */

  static Product<Operations>* product;              /* Pointer to the "current"
						     * product (i.e., the
						     * product for which one
						     * of the emptiness
						     * checking operations was
						     * last called) to allow
						     * accessing it from
						     * member classes.
						     */
};



/******************************************************************************
 *
 * A template class for providing a Graph<>::Node-like interface to the states
 * in a product (needed for accessing the transitions leaving from a state).
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::ProductState
{
public:
  ProductState(const size_type state);              /* Constructor. */

  /* default copy constructor */

  ~ProductState();                                  /* Destructor. */

  /* default assignment operator */

  const EdgeContainerType& edges() const;           /* Returns an object for
						     * generating the
						     * transitions starting
						     * from the state.
						     */

private:
  EdgeContainerType outgoing_edges;                 /* Object for generating
						     * the transitions starting
						     * from the state.
						     */
};



/******************************************************************************
 *
 * A template class for providing a Graph<>::Edge-like interface to the
 * transitions in a product.
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::ProductEdge
{
public:
  ProductEdge                                       /* Constructor. */
    (const GraphEdgeContainer::const_iterator& e1,
     const GraphEdgeContainer::const_iterator& e2);

    /* default copy constructor */

    ~ProductEdge();                                 /* Destructor. */

    /* default assignment operator */

    const Graph<GraphEdgeContainer>::Edge&          /* Functions for       */
      firstComponent() const;                       /* accessing the       */
    const Graph<GraphEdgeContainer>::Edge&          /* components of a     */
      secondComponent() const;                      /* product transition. */

    size_type targetNode() const;                   /* Returns the target state
						     * of the transition.
						     */

private:
  GraphEdgeContainer::const_iterator edge_1;        /* Components of the */
  GraphEdgeContainer::const_iterator edge_2;        /* transition.       */
};



/******************************************************************************
 *
 * A template class for providing a constant pointer -like interface to
 * ProductEdge objects.
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::ProductEdgePointer
{
public:
  ProductEdgePointer                               /* Constructor. */
    (const GraphEdgeContainer::const_iterator& e1,
     const GraphEdgeContainer::const_iterator& e2);

  /* default copy constructor */

  ~ProductEdgePointer();                           /* Destructor. */

  /* default assignment operator */

  const ProductEdge& operator*() const;            /* Dereferencing */
  const ProductEdge* operator->() const;           /* operators.    */

private:
  ProductEdge edge;                                /* The product transition.
						    */
};



/******************************************************************************
 *
 * A template class for representing (product state, product transition) pairs
 * in a path in the product.
 *
 *****************************************************************************/

template <class Operations>
struct Product<Operations>::PathElement
{
  PathElement(const size_type s, const Edge& t);    /* Constructor. */

  /* default copy constructor */

  ~PathElement();                                   /* Destructor. */

  /* default assignment operator */

  size_type state;                                  /* Product state and */
  Edge transition;                                  /* transition.       */
};



/******************************************************************************
 *
 * A template class for tracking acceptance information in strongly connected
 * product components by (essentially) recording the information into roots of
 * the components.  This is done using the method described by Couvreur in
 *     [J.-M. Couvreur.  On-the-fly verification of linear temporal logic.
 *      In Proceedings of the FM'99 World Congress on Formal Methods in the
 *      Development of Computing Systems, Volume I, LNCS 1708, pp. 253--271.
 *      Springer-Verlag, 1999].
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::AcceptanceTracker :
  public VisitorInterface<Product<Operations> >
{
public:
  explicit AcceptanceTracker                        /* Constructor. */
    (const unsigned long int num_accept_sets);

  virtual ~AcceptanceTracker();                     /* Destructor. */

  /* `enter' inherited */

  /* `backtrack' inherited */

  /* `touch' inherited */

  /* `leave' inherited */

  virtual void addEdgeToComponent                   /* Adds the acceptance  */
    (const Edge& t, const size_type scc_id);        /* sets associated with
                                                     * a product transition
                                                     * to a nontrivial
						     * strongly connected
						     * component of the
						     * product.
						     */

  virtual void addNodeToComponent                   /* Adds the acceptance  */
    (const size_type state_id,                      /* sets associated with */
     const size_type scc_id);                       /* a product state to a */
                                                    /* nontrivial strongly
						     * connected component
						     * of the product.
						     */

  /* `beginComponent' inherited */

  /* `insert' inherited */

  virtual void endComponent                         /* Removes the           */
    (const size_type scc_id);                       /* association between a
						     * nontrivial strongly
						     * connected product
						     * component and a set
						     * of acceptance sets
						     * when the component is
						     * not needed any
						     * longer.
						     */

protected:
  typedef pair<size_type, BitArray*>                /* Association between  */
    AcceptanceStackElement;                         /* a strongly connected 
						     * component identifier
						     * and a collection of
						     * acceptance sets.
						     */

  typedef deque<AcceptanceStackElement>             /* Stack formed from */
    AcceptanceStack;                                /* the above
                                                     * associations.
						     */

  AcceptanceStack acceptance_stack;                 /* Stack for storing the
						     * dfs numbers of roots
						     * of strongly connected
						     * components and
						     * acceptance sets
						     * associated with them.
						     */

  BitArray* acceptance_sets;                        /* Used for manipulating
						     * the stack.
						     */

  const unsigned long int                           /* Number of acceptance */
    number_of_acceptance_sets;                      /* sets in the product. */

private:
  AcceptanceTracker(const AcceptanceTracker&);      /* Prevent copying and */
  AcceptanceTracker& operator=                      /* assignment of       */
    (const AcceptanceTracker&);                     /* AcceptanceTracker
						     * objects.
						     */
};



/******************************************************************************
 *
 * A template class for checking a product for emptiness.
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::SimpleEmptinessChecker : public AcceptanceTracker
{
public:
  explicit SimpleEmptinessChecker                   /* Constructor. */
    (const unsigned long int num_accept_sets);

  ~SimpleEmptinessChecker();                        /* Destructor. */

  typedef int SccType;                              /* Dummy type definition
						     * required for supporting
						     * the expected class
						     * interface.
						     */

  const SccType& operator()() const;                /* Dummy function required
						     * for supporting the
						     * expected class
						     * interface.
						     */

  /* `enter' inherited */

  /* `backtrack' inherited */

  /* `touch' inherited */

  /* `leave' inherited */

  void addEdgeToComponent                           /* Adds the acceptance  */
    (const Edge& t, const size_type scc_id);        /* sets associated with
                                                     * a product transition
                                                     * to a nontrivial
						     * strongly connected
						     * component of the
						     * product and aborts
						     * the emptiness check
						     * if an accepting
						     * strongly connected
						     * component is
						     * detected.
						     */

  void addNodeToComponent                           /* Adds the acceptance   */
    (const size_type state,                         /* sets associated with  */
     const size_type scc_id);                       /* a product state to a
						     * nontrivial strongly
						     * connected component
						     * of the product and
						     * aborts the emptiness
						     * check if an accepting
						     * strongly connected
						     * component is
						     * detected.
						     */

  /* `beginComponent' inherited */

  /* `insert' inherited */

  /* `endComponent' inherited */

private:
  SimpleEmptinessChecker                            /* Prevent copying and */
    (const SimpleEmptinessChecker&);                /* assignment of       */
  SimpleEmptinessChecker&                           /* SimpleEmptiness-    */
    operator=(const SimpleEmptinessChecker&);       /* Checker objects.    */

  void abortIfNonempty() const;                     /* Aborts the search when
						     * an accepting strongly
						     * connected component is
						     * found.
						     */

  SccType dummy;                                    /* Dummy variable needed
						     * for implementing the
						     * operator() function.
						     */
};



/******************************************************************************
 *
 * A template class for tracking the reachability of accepting strongly
 * connected components in a product.
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::AcceptanceReachabilityTracker
  : public Product<Operations>::AcceptanceTracker
{
public:
  explicit AcceptanceReachabilityTracker            /* Constructor. */
    (const unsigned long int num_accept_sets);

  ~AcceptanceReachabilityTracker();                 /* Destructor. */

  typedef int SccType;                              /* Dummy type definition
						     * required for supporting
						     * the expected class
						     * interface.
						     */

  const SccType& operator()() const;                /* Dummy function required
						     * for supporting the
						     * expected class
						     * interface.
						     */

  void enter(const size_type);                      /* Function called when
						     * entering a new product
						     * state.
						     */

  void backtrack                                    /* Function called when */
    (const size_type source, const Edge&,           /* backtracking from a  */
     const size_type target);                       /* product state.       */

  void touch                                        /* Function called when  */
    (const size_type source, const Edge& edge,      /* processing an edge    */
     const size_type target);                       /* with a target state
                                                     * that has already been
						     * visited during the
						     * search.
						     */

  /* `leave' inherited */

  /* `addEdgeToComponent' inherited */

  /* `addNodeToComponent' inherited */

  void beginComponent                               /* Tests whether the     */
    (const size_type, const size_type state_id);    /* strongly connected
                                                     * component about to
						     * be extracted from the
						     * product is an
						     * accepting component,
						     * or if it contains a
						     * state from which such
						     * a component is known
						     * to be reachable.
						     */

  void insert(const size_type state);               /* Function used for
						     * updating accepting
						     * component reachability
						     * information while
						     * extracting states from
						     * a product component.
						     */

  /* `endComponent' inherited */

  bool isMarked(const size_type state) const;       /* Tests whether an
						     * accepting component is
						     * known to be reachable
						     * from a product state.
						     */

  size_type numberOfStates() const;                 /* Tells the number of
						     * product states explored
						     * during the search.
						     */

  unsigned long int numberOfTransitions() const;    /* Tells the number of
                                                     * product transitions
 						     * explored during the
						     * search.
						     */

private:
  AcceptanceReachabilityTracker                     /* Prevent copying and */
    (const AcceptanceReachabilityTracker&);         /* assignment of       */
  AcceptanceReachabilityTracker&                    /* AcceptanceSet-      */
    operator=                                       /* ReachabilityTracker */
      (const AcceptanceReachabilityTracker&);       /* objects.            */

  void markState(const size_type state);            /* Adds a product state to
						     * the set of states from
						     * which an accepting
						     * component is known to be
						     * reachable.
						     */

  set<size_type> reachability_info;                 /* Set of states from
                                                     * which an accepting
                                                     * component is known to
						     * be reachable in the
						     * product.
						     */

  size_type number_of_states;                       /* Number of states
						     * explored during the
						     * search.
						     */

  unsigned long int number_of_transitions;          /* Number of transitions
						     * explored during the
						     * search.
						     */

  bool mark_scc;                                    /* Used for determining
						     * whether to insert states
						     * into `this->
						     * reachability_info' while
						     * extracting a strongly
						     * connected component from
						     * the product.
						     */

  SccType dummy;                                    /* Dummy variable needed
						     * for implementing the
						     * operator() function.
						     */
};



/******************************************************************************
 *
 * A template class for finding accepting maximal strongly connected components
 * in a product.
 *
 *****************************************************************************/

template <class Operations>
class Product<Operations>::AcceptingComponentFinder :
  public Product<Operations>::AcceptanceTracker
{
public:
  explicit AcceptingComponentFinder                 /* Constructor. */
    (const unsigned long int num_accept_sets);

  ~AcceptingComponentFinder();                      /* Destructor. */

  typedef set<size_type> SccType;                   /* Type definition for
                                                     * the set of product
                                                     * state identifiers in
						     * an accepting
						     * strongly connected
						     * component.
						     */

  const SccType& operator()() const;                /* Returns the last
						     * accepting maximal
						     * strongly connected
						     * component found in the
						     * product.
						     */

  /* `enter' inherited */

  /* `backtrack' inherited */

  /* `touch' inherited */

  /* `leave' inherited */

  /* `addEdgeToComponent' inherited */

  /* `addNodeToComponent' inherited */

  void beginComponent                               /* Tests whether the    */
    (const size_type, const size_type);             /* maximal strongly
						     * connected component
						     * that is about to be
						     * extracted from the
						     * product is an
						     * accepting component.
						     */

  void insert(const size_type state);               /* Inserts a state to an
						     * accepting component.
						     */

  /* `endComponent' inherited */

private:
  AcceptingComponentFinder                          /* Prevent copying and */
    (const AcceptingComponentFinder&);              /* assignment of       */
  AcceptingComponentFinder&                         /* AcceptingComponent- */
    operator=(const AcceptingComponentFinder&);     /* Finder objects.     */

  SccType scc;                                      /* Set of product state
						     * identifiers forming the
						     * last accepting strongly
						     * connected component
						     * found in the product.
						     */

  bool construct_component;                         /* Used for determining
						     * whether the states
						     * extracted from a
						     * strongly connected
						     * component in the product
						     * should be inserted into
						     * `this->scc'.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for template class Product<Operations>.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::Product
  (const Graph<GraphEdgeContainer>& g1, const Graph<GraphEdgeContainer>& g2)
  : operations(g1, g2), state_id_multiplier(g2.size())
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>.
 *
 * Arguments:     g1, g2  --  Constant references to the components of the
 *                            product.
 *
 * Returns:       Nothing.  If `Product<Operations>::size_type' cannot hold
 *                values large enough to accommodate the largest identifier for
 *                a product state, `this->too_large' is set to true to cause
 *                all emptiness checking operations on the product to fail by
 *                throwing a Product<Operations>::SizeException.
 *
 * ------------------------------------------------------------------------- */
{
  too_large = (!g2.empty() &&
	       g1.size() > (static_cast<size_type>(-1) / g2.size()));
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::~Product()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductState
Product<Operations>::operator[]
  (const typename Product<Operations>::size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Indexing operator for class Product<Operations>.
 *
 * Argument:      index  --  Index of a state of the product.
 *
 * Returns:       A ProductState object corresponding to the state with the
 *                given index.
 *
 * ------------------------------------------------------------------------- */
{
  return ProductState(index);
}

/* ========================================================================= */
template <class Operations>
inline typename Product<Operations>::size_type Product<Operations>
  ::stateId
  (const size_type state_1, const size_type state_2) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the product state identifier corresponding to
 *                identifiers of the state components.
 *
 * Arguments:     state_1, state_2  --  Identifiers for the product state
 *                                      components.
 *
 * Returns:       Identifier of the product state corresponding to the
 *                components.
 *
 * ------------------------------------------------------------------------- */
{
  return (state_1 * state_id_multiplier) + state_2;
}

/* ========================================================================= */
template <class Operations>
inline const Graph<GraphEdgeContainer>::Node&
Product<Operations>::firstComponent(const size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing the "first" component state of a
 *                product state.
 *
 * Arguments:     state  --  Identifier of a product state.
 *
 * Returns:       A constant reference to the state corresponding to the
 *                "first" component of the product state.
 *
 * ------------------------------------------------------------------------- */
{
  return operations.firstComponent(state / state_id_multiplier);
}

/* ========================================================================= */
template <class Operations>
inline const Graph<GraphEdgeContainer>::Node&
Product<Operations>::secondComponent(const size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing the "second" component state of a
 *                product state.
 *
 * Arguments:     state  --  Identifier of a product state.
 *
 * Returns:       A constant reference to the state corresponding to the
 *                "second" component of the product state.
 *
 * ------------------------------------------------------------------------- */
{
  return operations.secondComponent(state % state_id_multiplier);
}

/* ========================================================================= */
template <class Operations>
inline bool Product<Operations>::empty() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the product is (trivially) empty, i.e., if
 *                either of its components has no states.
 *
 * Arguments:     None.
 *
 * Returns:       true iff the product is trivially empty.
 *
 * ------------------------------------------------------------------------- */
{
  return operations.empty();
}



/******************************************************************************
 *
 * Function definitions for template class Product<Operations>.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
bool Product<Operations>::localEmptinessCheck
  (const Graph<GraphEdgeContainer>::size_type s1_id,
   const Graph<GraphEdgeContainer>::size_type s2_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether the subproduct rooted at a product state
 *                determined by a pair of component state identifiers is not
 *                empty.
 *
 * Arguments:     s1_id, s2_id  --  Identifiers for the product state
 *                                  components.
 *
 * Returns:       true iff the subproduct rooted at the product state
 *                determined by `s1_id' and `s2_id' is not empty.  Throws a
 *                Product<Operations>::SizeException if the product is too
 *                large to handle.
 *
 * ------------------------------------------------------------------------- */
{
  if (too_large)
    throw SizeException();

  if (empty())
    return false;

  product = this;

  SimpleEmptinessChecker ec(operations.numberOfAcceptanceSets());
  typedef SccCollection<Product<Operations>, SimpleEmptinessChecker>
    ProductSccCollection;

  ProductSccCollection sccs(*this, ec);

  try
  {
    for (typename ProductSccCollection::iterator scc
	   = sccs.begin(stateId(s1_id, s2_id));
	 scc != sccs.end();
	 ++scc)
    {
      if (::user_break)
	throw UserBreakException();
    }
  }
  catch (const int)
  {
    return true;
  }

  return false;
}

/* ========================================================================= */
template <class Operations>
const pair<Graph<GraphEdgeContainer>::size_type, unsigned long int>
Product<Operations>::globalEmptinessCheck
  (const Graph<GraphEdgeContainer>::size_type state_id,
   Bitset& result, const unsigned long int emptiness_check_size)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks a set of subproducts of the product for emptiness.
 *
 * Arguments:     state_id              --  Identifier of a state in the first
 *                                          component of the product.
 *                result                --  A reference to a Bitset for storing
 *                                          the result of the emptiness check.
 *                                          The set should have room for at
 *                                          least `emptiness_check_size' bits.
 *                emptiness_check_size  --  Determines the scope of the
 *                                          emptiness check (see below).
 *
 * Returns:       A pair giving the numbers of product states and transitions
 *                generated during the emptiness check.  The result of the
 *                emptiness check itself is stored into `result' such that
 *                the i'th bit (for all 0 <= i < emptiness_check_size) in the
 *                bit set will be 1 iff the subproduct rooted at the product
 *                state determined by the pair of state identifiers
 *                (state_id, i) is nonempty.
 *
 *                The function throws a Product<Operations>::SizeException if
 *                the product may be too large to handle.
 *
 * ------------------------------------------------------------------------- */
{
  if (too_large)
    throw SizeException();

  result.clear();

  if (empty())
    return make_pair(0, 0);

  product = this;

  AcceptanceReachabilityTracker rt(operations.numberOfAcceptanceSets());

  typedef SccCollection<Product<Operations>, AcceptanceReachabilityTracker>
    ProductSccCollection;

  ProductSccCollection sccs(*this, rt);

  for (Graph<GraphEdgeContainer>::size_type state = 0;
       state < emptiness_check_size;
       ++state)
  {
    for (typename ProductSccCollection::iterator scc
	   = sccs.begin(stateId(state_id, state));
	 scc != sccs.end();
	 ++scc)
    {
      if (::user_break)
	throw UserBreakException();
    }
  }

  for (Graph<GraphEdgeContainer>::size_type state = 0;
       state < emptiness_check_size;
       ++state)
  {
    if (rt.isMarked(stateId(state_id, state)))
      result.setBit(state);
  }

  return make_pair(rt.numberOfStates(), rt.numberOfTransitions());
}

/* ========================================================================= */
template <class Operations>
void Product<Operations>::findWitness
  (const typename Graph<GraphEdgeContainer>::size_type s1_id,
   const typename Graph<GraphEdgeContainer>::size_type s2_id,
   typename Product<Operations>::Witness& witness)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether the subproduct rooted at a product state
 *                determined by a pair of component state identifiers is not
 *                empty.  If this is the case, constructs a witness for the
 *                nonemptiness.
 *
 * Arguments:     s1_id, s2_id  --  Identifiers for the product state
 *                                  components.
 *                witness       --  A reference to an object for storing a
 *                                  witness if such a witness exists.
 *
 * Returns:       Nothing.  A witness was found iff
 *                `!witness.cycle.first.empty()
 *                   && !witness.cycle.second.empty()' holds after the call.
 *
 *                The function throws a Product<Operations>::SizeException if
 *                the product may be too large to handle.
 *
 * ------------------------------------------------------------------------- */
{
  if (too_large)
    throw SizeException();

  witness.prefix.first.clear();
  witness.prefix.second.clear();
  witness.cycle.first.clear();
  witness.cycle.second.clear();

  if (empty())
    return;

  product = this;
  const unsigned long int number_of_acceptance_sets
    = operations.numberOfAcceptanceSets();
  const size_type start_state = stateId(s1_id, s2_id);

  AcceptingComponentFinder acf(number_of_acceptance_sets);
  typedef SccCollection<Product<Operations>, AcceptingComponentFinder>
    ProductSccCollection;

  ProductSccCollection sccs(*this, acf);

  for (typename ProductSccCollection::iterator scc = sccs.begin(start_state);
       scc != sccs.end();
       ++scc)
  {
    if (::user_break)
      throw UserBreakException();

    if (!scc->empty())
    {
      /*
       *  The prefix of the witness consists of a path from the given product
       *  state to a state in an accepting strongly connected product
       *  component.
       */

      Path path;
      scc.getPath(path);

      for (typename Path::const_iterator path_element = path.begin();
	   path_element != path.end();
	   ++path_element)
      {
	witness.prefix.first.push_back
	  (Graph<GraphEdgeContainer>::PathElement
	     (path_element->state / state_id_multiplier,
	      path_element->transition.firstComponent()));
	witness.prefix.second.push_back
	  (Graph<GraphEdgeContainer>::PathElement
	     (path_element->state % state_id_multiplier,
	      path_element->transition.secondComponent()));
      }

      /*
       *  Construct an accepting cycle by performing a breadth-first search
       *  in the MSCC.
       */

      const size_type search_start_state
	= path.empty() ? start_state : path.back().transition.targetNode();

      BitArray collected_acceptance_sets(number_of_acceptance_sets);
      collected_acceptance_sets.clear(number_of_acceptance_sets);
      operations.mergeAcceptanceInformation
	(firstComponent(search_start_state),
	 secondComponent(search_start_state), collected_acceptance_sets);

      unsigned long int number_of_collected_acceptance_sets
	= collected_acceptance_sets.count(number_of_acceptance_sets);

      deque<size_type> search_queue;
      set<size_type> visited;
      map<size_type, PathElement> shortest_path_predecessor;

      size_type bfs_root = search_start_state;

continue_bfs:
      search_queue.clear();
      search_queue.push_back(bfs_root);
      visited.clear();
      visited.insert(bfs_root);
      shortest_path_predecessor.clear();

      while (!search_queue.empty())
      {
	const EdgeContainerType transitions
	  = ProductState(search_queue.front()).edges();

	for (typename EdgeContainerType::const_iterator transition
	       = transitions.begin();
	     transition != transitions.end();
	     ++transition)
	{
	  const size_type target = (*transition)->targetNode();
	  if (scc->find(target) == scc->end())
	    continue;

	  if (visited.find(target) == visited.end())
	  {
	    visited.insert(target);
	    shortest_path_predecessor.insert
	      (make_pair(target, PathElement(search_queue.front(),
					     **transition)));
	    search_queue.push_back(target);

	    if (number_of_collected_acceptance_sets
		  < number_of_acceptance_sets)
	      operations.mergeAcceptanceInformation
		(firstComponent(target), secondComponent(target),
		 collected_acceptance_sets);
	  }

	  if (number_of_collected_acceptance_sets < number_of_acceptance_sets)
	  {
	    /*
	     *  Test whether the current product transition or the target
	     *  state of the transition covers new acceptance sets.  If
	     *  this is the case, construct the next segment of the cycle
	     *  and begin a new breadth-first search in the target state of
	     *  the transition.
	     */

	    operations.mergeAcceptanceInformation
	      ((*transition)->firstComponent(),
	       (*transition)->secondComponent(), collected_acceptance_sets);

	    const unsigned long int num
	      = collected_acceptance_sets.count(number_of_acceptance_sets);
	    if (num > number_of_collected_acceptance_sets)
	    {
	      number_of_collected_acceptance_sets = num;

	      addCycleSegment(witness.cycle, search_queue.front(),
			      **transition, bfs_root,
			      shortest_path_predecessor);

	      if (number_of_collected_acceptance_sets
		    == number_of_acceptance_sets
		  && target == search_start_state)
		return;

	      bfs_root = target;
	      goto continue_bfs;
	    }
	  }
	  else if (target == search_start_state)
	  {
	    /*
	     *  If all acceptance sets have been collected and the current
	     *  product transition points to the first state of the cycle,
	     *  the cycle is complete.
	     */

	    addCycleSegment(witness.cycle, search_queue.front(), **transition,
			    bfs_root, shortest_path_predecessor);
	    return;
	  }
	}
	      
	search_queue.pop_front();
      }

      throw Exception
	      ("Product::findWitness(...): internal error [cycle construction "
	       "failed]");
    }
  }
}

/* ========================================================================= */
template <class Operations>
void Product<Operations>::addCycleSegment
  (pair<Graph<GraphEdgeContainer>::Path, Graph<GraphEdgeContainer>::Path>&
     cycle,
   size_type source_state_id, Edge transition, const size_type root_id,
   const map<size_type, PathElement>& predecessor) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Helper function for constructing a segment of an accepting
 *                cycle in the product.
 *
 * Arguments:     cycle            --  A reference to a pair of paths for
 *                                     storing the result.
 *                source_state_id  --  Identifier of the last product state in
 *                                     the cycle segment.
 *                transition       --  Last product transition in the cycle
 *                                     segment.
 *                root_id          --  Identifier of the first product state in
 *                                     the cycle segment.
 *                predecessor      --  Mapping between states and their
 *                                     predecessors in the cycle segment.
 *
 * Returns:       Nothing.  The segment of the cycle is appended to `cycle'.
 *
 * ------------------------------------------------------------------------- */
{
  Graph<GraphEdgeContainer>::Path first_segment;
  Graph<GraphEdgeContainer>::Path second_segment;

  while (1)
  {
    first_segment.push_front
      (Graph<GraphEdgeContainer>::PathElement
         (source_state_id / state_id_multiplier, transition.firstComponent()));
    second_segment.push_front
      (Graph<GraphEdgeContainer>::PathElement
         (source_state_id % state_id_multiplier,
	  transition.secondComponent()));

    if (source_state_id == root_id)
    {
      cycle.first.insert(cycle.first.end(), first_segment.begin(),
			 first_segment.end());
      cycle.second.insert(cycle.second.end(), second_segment.begin(),
			  second_segment.end());
      return;
    }

    const PathElement& p = predecessor.find(source_state_id)->second;
    source_state_id = p.state;
    transition = p.transition;
  }
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::ProductState.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductState::ProductState
  (const size_type state) : outgoing_edges(state)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>::ProductState.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductState::~ProductState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>::ProductState.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::EdgeContainerType&
Product<Operations>::ProductState::edges() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an object for generating the transitions starting
 *                from a product state.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to an object that can be used for
 *                generating the transitions starting from the state.
 *
 * ------------------------------------------------------------------------- */
{
  return outgoing_edges;
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::ProductEdge.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdge::ProductEdge
  (const GraphEdgeContainer::const_iterator& e1,
   const GraphEdgeContainer::const_iterator& e2)
  : edge_1(e1), edge_2(e2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>::ProductEdge.
 *
 * Arguments:     e1, e2  --  Iterators pointing to the components of the
 *                            product transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdge::~ProductEdge()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>::ProductEdge.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const Graph<GraphEdgeContainer>::Edge&
Product<Operations>::ProductEdge::firstComponent() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing the "first" component of a product
 *                transition.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the "first" component of the
 *                transition.
 *
 * ------------------------------------------------------------------------- */
{
  return **edge_1;
}

/* ========================================================================= */
template <class Operations>
inline const Graph<GraphEdgeContainer>::Edge&
Product<Operations>::ProductEdge::secondComponent() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing the "second" component of a product
 *                transition.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the "second" component of the
 *                transition.
 *
 * ------------------------------------------------------------------------- */
{
  return **edge_2;
}

/* ========================================================================= */
template <class Operations>
inline typename Product<Operations>::size_type
Product<Operations>::ProductEdge::targetNode() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the target state of a product transition.
 *
 * Arguments:     None.
 *
 * Returns:       Identifier of the target state of the product transition.
 *
 * ------------------------------------------------------------------------- */
{
  return product->stateId((*edge_1)->targetNode(), (*edge_2)->targetNode());
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::ProductEdgePointer.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgePointer::ProductEdgePointer
  (const GraphEdgeContainer::const_iterator& e1,
   const GraphEdgeContainer::const_iterator& e2)
  : edge(e1, e2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::ProductEdgePointer.
 *
 * Arguments:     e1, e2  --  Iterators pointing to the components of the
 *                            product transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgePointer::~ProductEdgePointer()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::ProductEdgePointer.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdge&
Product<Operations>::ProductEdgePointer::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                Product<Operations>::ProductEdgePointer.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the product transition associated with the
 *                object.
 *
 * ------------------------------------------------------------------------- */
{
  return edge;
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdge*
Product<Operations>::ProductEdgePointer::operator->() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                Product<Operations>::ProductEdgePointer.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to the product transition associated with the
 *                object.
 *
 * ------------------------------------------------------------------------- */
{
  return &edge;
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::ProductEdgeCollection.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgeCollection::ProductEdgeCollection
  (const size_type state) : product_state(state)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::ProductEdgeCollection.
 *
 * Argument:      state  --  Identifier of a product state.  The
 *                           ProductEdgeCollection object will mimic a
 *                           container for the product transitions starting
 *                           from this state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgeCollection::~ProductEdgeCollection()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::ProductEdgeCollection.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdgeCollection
  ::const_iterator
Product<Operations>::ProductEdgeCollection::begin() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an iterator for generating the transitions starting
 *                from the product state identified by `this->product_state'.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return const_iterator
    (product_state,
     product->firstComponent(product_state).edges().begin(),
     product->secondComponent(product_state).edges().begin());
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdgeCollection
  ::const_iterator
Product<Operations>::ProductEdgeCollection::end() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an iterator pointing to the "end" of the collection
 *                of transitions starting from the product state identified by
 *                `this->product_state'.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return const_iterator
    (product_state,
     product->firstComponent(product_state).edges().end(),
     product->secondComponent(product_state).edges().end());
}

    

/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgeCollection::const_iterator
  ::const_iterator() : product_state(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Default constructor for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgeCollection::const_iterator
  ::const_iterator
  (const size_type state,
   const GraphEdgeContainer::const_iterator& e1,
   const GraphEdgeContainer::const_iterator& e2) :
  product_state(state), edge_1(e1), edge_2(e2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     state   --  Identifier of a product state to associate with
 *                            the iterator.
 *                e1, e1  --  Constant references to a pair of iterators
 *                            pointing to a pair of transitions starting from
 *                            the component states of the product state.  These
 *                            iterators are used to determine where to start
 *                            iterating over the product transitions.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  product->operations.validateEdgeIterators
    (product->firstComponent(product_state),
     product->secondComponent(product_state),
     edge_1, edge_2);
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::ProductEdgeCollection::const_iterator
  ::~const_iterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline bool Product<Operations>::ProductEdgeCollection::const_iterator
  ::operator==
  (const const_iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equality test between two
 *                Product<Operations>::ProductEdgeCollection::const_iterators.
 *
 * Argument:      it  --  A constant reference to an iterator to compare for
 *                        equality.  It is assumed that the iterators are
 *                        associated with the same product state; the result of
 *                        a comparison between iterators associated with
 *                        different product states is undefined.
 *
 * Returns:       true iff `it' and `*this' point to the same transition in the
 *                product.
 *
 * ------------------------------------------------------------------------- */
{
  return (it.edge_1 == edge_1 && it.edge_2 == edge_2);
}

/* ========================================================================= */
template <class Operations>
inline bool Product<Operations>::ProductEdgeCollection::const_iterator
  ::operator!=
  (const const_iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Inequality test between two
 *                Product<Operations>::ProductEdgeCollection::const_iterators.
 *
 * Argument:      it  --  A constant reference to an iterator to compare for
 *                        equality.  It is assumed that the iterators are
 *                        associated with the same product state; the result of
 *                        a comparison between iterators associated with
 *                        different product states is undefined.
 *
 * Returns:       true iff `it' and `*this' point to different transitions in
 *                the product.
 *
 * ------------------------------------------------------------------------- */
{
  return (it.edge_1 != edge_1 || it.edge_2 != edge_2);
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdgePointer
Product<Operations>::ProductEdgeCollection::const_iterator::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       An object of type Product<Operations>::ProductEdgePointer
 *                that allows access to the product transition pointed to by
 *                the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  return ProductEdgePointer(edge_1, edge_2);
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdge
Product<Operations>::ProductEdgeCollection::const_iterator::operator->() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A Product<Operations>::ProductEdge corresponding to the
 *                product transition pointed to by the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  return ProductEdge(edge_1, edge_2);
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdgePointer
Product<Operations>::ProductEdgeCollection::const_iterator::operator++()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prefix increment operator for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       An object of type Product<Operations>::ProductEdgePointer
 *                that behaves like a pointer to the product transition
 *                obtained by advancing the iterator in the sequence of product
 *                transitions.
 *
 * ------------------------------------------------------------------------- */
{
  product->operations.incrementEdgeIterators
    (product->firstComponent(product_state),
     product->secondComponent(product_state),
     edge_1, edge_2);
  return ProductEdgePointer(edge_1, edge_2);
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::ProductEdgePointer
Product<Operations>::ProductEdgeCollection::const_iterator::operator++(int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Postfix increment operator for class
 *                Product<Operations>::ProductEdgeCollection::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       An object of type Product<Operations>::ProductEdgePointer
 *                that behaves like a pointer to the product transition pointed
 *                to by the iterator before advancing it in the sequence of
 *                product transitions.
 *
 * ------------------------------------------------------------------------- */
{
  const typename Product<Operations>::ProductEdgePointer edge(edge_1, edge_2);
  product->operations.incrementEdgeIterators
    (product->firstComponent(product_state),
     product->secondComponent(product_state),
     edge_1, edge_2);
  return edge;
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::PathElement.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::PathElement::PathElement
  (const size_type s, const Edge& t) : state(s), transition(t)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>::PathElement.
 *
 * Arguments:     s, t  --  Product state and transition associated with the
 *                          element.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::PathElement::~PathElement()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>::PathElement.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::SizeException.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::SizeException::SizeException() :
  Exception("product may be too large")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>::SizeException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::SizeException::~SizeException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>::SizeException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline typename Product<Operations>::SizeException&
Product<Operations>::SizeException::operator=
  (const typename Product<Operations>::SizeException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                Product<Operations>::SizeException.  Assigns the value of
 *                another Product<Operations>::SizeException to `this' one.
 *
 * Arguments:     e  --  A reference to a constant object of type
 *                       Product<Operations>SizeException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::AcceptanceTracker.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptanceTracker::AcceptanceTracker
  (const unsigned long int num_accept_sets)
  : number_of_acceptance_sets(num_accept_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Product<Operations>::AcceptanceTracker.
 *
 * Arguments:     num_accept_sets  --  Number of acceptance sets in the
 *                                     product.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  /* Initialize `acceptance_stack' with a sentinel element. */
  acceptance_stack.push_front(make_pair(0, new BitArray(0)));
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptanceTracker::~AcceptanceTracker()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Product<Operations>::AcceptanceTracker.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  for (AcceptanceStack::iterator a = acceptance_stack.begin();
       a != acceptance_stack.end();
       ++a)
    delete a->second;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceTracker::addEdgeToComponent
  (const typename Product<Operations>::Edge& t,
   const typename Product<Operations>::size_type scc_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds the acceptance sets of the product transition `t'
 *                (inside a strongly connected product component) to the
 *                collection of acceptance sets associated with the strongly
 *                connected component identifier (a dfs number of a product
 *                state in Tarjan's algorithm).  (The component is therefore
 *                nontrivial, because it contains a transition.)  This is done
 *                as described by Couvreur
 *                    [J.-M. Couvreur.  On-the-fly verification of linear
 *                     temporal logic.  In Proceedings of the FM'99 World
 *                     Congress on Formal Methods in the Development of
 *                     Computing Systems, Volume I, LNCS 1708, pp. 253--271.
 *                     Springer-Verlag, 1999]
 *                by first collapsing all elements in the top part of
 *                `this->acceptance_stack' (*) with strongly connected
 *                component identifiers greater than or equal to `scc_id' into
 *                a single element by taking the union of their acceptance sets
 *                and then merging the acceptance sets of the transition `t'
 *                with this element.
 *                  After the call, the top element of `this->acceptance_stack'
 *                will have the scc id `scc_id', and `this->acceptance_sets'
 *                points to the acceptance sets associated with this stack
 *                element.
 *
 *                (*)  It is assumed that the contents of
 *                `this->acceptance_stack' form (from top to bottom) a sequence
 *                of elements  (id1,a1), (id2,a2), (id3,a3), ...,  where
 *                id1 > id2 > id3 > ... .  The function maintains this
 *                invariant.
 *
 * Arguments:     t       --  A constant reference to the product transition.
 *                scc_id  --  Identifier of the strongly connected component
 *                            (assumed to be >= 1).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  acceptance_sets = 0;
  while (acceptance_stack.front().first >= scc_id)
  {
    if (acceptance_sets != 0)
    {
      acceptance_stack.front().second->bitwiseOr
	(*acceptance_sets, number_of_acceptance_sets);
      delete acceptance_sets;
    }
    acceptance_sets = acceptance_stack.front().second;
    if (acceptance_stack.front().first == scc_id)
      goto merge_sets;

    acceptance_stack.pop_front();
  }

  if (acceptance_sets == 0)
  {
    acceptance_sets = new BitArray(number_of_acceptance_sets);
    acceptance_sets->clear(number_of_acceptance_sets);
  }
  acceptance_stack.push_front(make_pair(scc_id, acceptance_sets));
merge_sets:
  product->operations.mergeAcceptanceInformation
    (t.firstComponent(), t.secondComponent(), *acceptance_sets);
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceTracker::addNodeToComponent
  (const typename Product<Operations>::size_type state_id,
   const typename Product<Operations>::size_type scc_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds the acceptance sets of the product state `state_id' to
 *                the collection of acceptance sets associated with a
 *                nontrivial strongly connected component identifier (a dfs
 *                number of a product state in Tarjan's algorithm).
 *
 * Arguments:     state_id  --  Identifier of the product state.
 *                scc_id    --  Identifier of the strongly connected component
 *                              (assumed to be >= 1).
 *
 * Returns:       Nothing.  Upon return, `this->acceptance_sets' either points
 *                to the acceptance sets associated with the topmost element of
 *                `this->acceptance_sets', or, if the component is a
 *                trivial strongly connected component,
 *                `this->acceptance_sets == 0'.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   * When this function gets called, then depth-first search guarantees that
   * the strongly connected component identifier of the topmost element (if
   * such an element exists) of `this->acceptance_stack' is <= 'scc_id'.
   * Furthermore, the strongly connected is nontrivial only if equality holds
   * in the above test.
   */

  if (acceptance_stack.front().first < scc_id)
  {
    acceptance_sets = 0;
    return;
  }

  acceptance_sets = acceptance_stack.front().second;
  product->operations.mergeAcceptanceInformation
    (product->firstComponent(state_id), product->secondComponent(state_id),
     *acceptance_sets);
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceTracker::endComponent
  (const typename Product<Operations>::size_type scc_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Removes the association between a nontrivial strongly
 *                connected component identifier and a collection of acceptance
 *                sets when the component is not needed any longer.  (This
 *                function gets called after extracting a maximal strongly
 *                connected component from the product.  It is safe to remove
 *                the association at this point, because the search will not
 *                enter the component afterwards, nor can any state visited
 *                in the future belong to the strongly connected component
 *                identified by `scc_id'.)
 *
 * Argument:      scc_id  --  Identifier of the strongly connected component.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   * Because the depth-first search made a call to `addNodeToComponent' before
   * extracting the component from the product,  the topmost element (if any)
   * of `this->acceptance_stack' is guaranteed to have scc id <= `scc_id' at
   * this point.
   */

  if (acceptance_stack.front().first == scc_id)
  {
    delete acceptance_stack.front().second;
    acceptance_stack.pop_front();
  }
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::SimpleEmptinessChecker.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::SimpleEmptinessChecker::SimpleEmptinessChecker
  (const unsigned long int num_accept_sets) 
  : AcceptanceTracker(num_accept_sets), dummy(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::SimpleEmptinessChecker.
 *
 * Arguments:     num_accept_sets  --  Number of acceptance sets in the
 *                                     product.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::SimpleEmptinessChecker::~SimpleEmptinessChecker()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::SimpleEmptinessChecker.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::SimpleEmptinessChecker::SccType&
Product<Operations>::SimpleEmptinessChecker::operator()() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dummy function required for supporting the expected class
 *                interface.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to `this->dummy'.
 *
 * ------------------------------------------------------------------------- */
{
  return dummy;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::SimpleEmptinessChecker::addEdgeToComponent
  (const typename Product<Operations>::Edge& t,
   const typename Product<Operations>::size_type scc_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds a transition to a nontrivial strongly connected product
 *                component and aborts the search if the addition of the
 *                transition in the component makes the component accepting.
 *
 * Arguments:     t       --  A constant reference to the product transition to
 *                            be added to the component.
 *                scc_id  --  Identifier of the strongly connected component.
 *
 * Returns:       Nothing; aborts the search by throwing the constant 0 if the
 *                addition of the transition in the component makes the
 *                component accepting.
 *
 * ------------------------------------------------------------------------- */
{
  AcceptanceTracker::addEdgeToComponent(t, scc_id);
  /* `this->acceptance_sets' points to the acceptance sets associated with
   * the nontrivial SCC `scc_id' at this point. */
  abortIfNonempty();
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::SimpleEmptinessChecker::addNodeToComponent
  (const typename Product<Operations>::size_type state,
   const typename Product<Operations>::size_type scc_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds a state to a nontrivial strongly connected product
 *                component and aborts the search if the addition of the state
 *                in the component makes the component accepting.
 *
 * Arguments:     state   --  Identifier of a product state to be added to the
 *                            component.
 *                scc_id  --  Identifier of the strongly connected component.
 *
 * Returns:       Nothing; aborts the search by throwing the constant 0 if the
 *                addition of the state in the component makes the component
 *                accepting.
 *
 * ------------------------------------------------------------------------- */
{
  AcceptanceTracker::addNodeToComponent(state, scc_id);
  /* If `this->acceptance_sets != 0', then `this->acceptance_sets' points to
   * the acceptance sets associated with the nontrivial SCC `scc_id';
   * otherwise the component `scc_id' is a trivial SCC. */
  if (this->acceptance_sets != 0)
    abortIfNonempty();
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::SimpleEmptinessChecker::abortIfNonempty()
  const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the strongly connected component with
 *                acceptance sets pointed to by `this->acceptance_sets' is an
 *                accepting component.  This holds if all bits in the
 *                acceptance set bit vector pointed to by
 *                `this->acceptance_sets' are set to 1.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing; throws the constant 0 if the component with
 *                acceptance sets pointed to by `this->acceptance_sets' is an
 *                accepting strongly connected component.
 *
 * ------------------------------------------------------------------------- */
{
  if (this->acceptance_sets->count(this->number_of_acceptance_sets)
        == this->number_of_acceptance_sets)
    throw 0;
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::AcceptanceReachabilityTracker.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptanceReachabilityTracker
  ::AcceptanceReachabilityTracker
  (const unsigned long int num_accept_sets) :
  AcceptanceTracker(num_accept_sets), number_of_states(0),
  number_of_transitions(0), mark_scc(false), dummy(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::AcceptanceReachabilityTracker.
 *
 * Arguments:     num_accept_sets  --  Number of acceptance sets in the
 *                                     product.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptanceReachabilityTracker
  ::~AcceptanceReachabilityTracker()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::AcceptanceReachabilityTracker.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline
const typename Product<Operations>::AcceptanceReachabilityTracker::SccType&
Product<Operations>::AcceptanceReachabilityTracker::operator()() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dummy function required for supporting the expected class
 *                interface.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to `this->dummy'.
 *
 * ------------------------------------------------------------------------- */
{
  return dummy;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::enter
  (const typename Product<Operations>::size_type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function called when entering a new state in the product.
 *                Increments the number of product states that have been
 *                explored.
 *
 * Arguments:     The single argument is required to support the expected class
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ++number_of_states;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::backtrack
  (const typename Product<Operations>::size_type source,
   const typename Product<Operations>::Edge&,
   const typename Product<Operations>::size_type target)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function called when backtracking from a state in the
 *                product.  Increments the number of product edges that have
 *                been explored.  Additionally, if the state from which the
 *                search backtracks belongs to the set of states from which an
 *                accepting strongly connected component is known to be
 *                reachable in the product, adds also the state to which the
 *                search backtracks into this set of states.
 *
 * Arguments:     (source, target) describe the endpoints of the product
 *                transition along which the backtrack occurs (i.e., `source'
 *                is the state _to_ which the search backtracks).  The second
 *                argument is only needed to support the expected function call
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ++number_of_transitions;
  if (isMarked(target))
    markState(source);
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::touch
  (const typename Product<Operations>::size_type source,
   const typename Product<Operations>::Edge& edge,
   const typename Product<Operations>::size_type target)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function called when the search encounters an edge with a
 *                target node that has already been explored.  Increments the
 *                number of explored product transitions and updates accepting
 *                strongly connected reachability information by calling
 *                `this->backtrack()'.  (This function is needed for supporting
 *                the expected class interface.)
 *
 * Arguments:     (source, edge, target) describe the product transition that
 *                "touches" the state `target'.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  backtrack(source, edge, target);
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::beginComponent
  (const typename Product<Operations>::size_type,
   const typename Product<Operations>::size_type state_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the maximal strongly connected component that
 *                is about to be extracted from the product is an accepting
 *                component, or if the component contains a state from which
 *                such a component is known to be reachable.  If either of
 *                these properties holds, `this->mark_scc' is set to true to
 *                cause all states referred to in subsequent calls to
 *                `this->insert' that occur before the next call to
 *                `this->endComponent' to be added into the set of states from
 *                which an accepting strongly connected component in the
 *                product is known to be reachable.
 *
 *                A component is accepting iff `this->acceptance_sets' points
 *                to a bit vector in which all bits are set to 1.
 *
 * Arguments:     state_id  --  Identifier of a product state (in the
 *                              component) that was encountered first during
 *                              the search.
 *                The first argument is needed to support the expected function
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (isMarked(state_id)) /* If the component itself is not accepting, but   */
    mark_scc = true;      /* it contains a state from which such a component */
                          /* is reachable, then the fact that `state_id' is  */
                          /* the first state of the component encountered    */
                          /* during the search and the operation of the      */
                          /* backtrack and touch functions guarantee that    */
                          /* `isMarked(state_id) == true' holds at this      */
                          /* point (the search is about to backtrack from    */
                          /* this state when this function gets called).     */
  else /* test whether the component is an accepting component */
  {
    /*
     * The dfs search guarantees (by having made a call to
     * AcceptanceTracker::addNodeToComponent before calling this function) that
     * `this->acceptance_sets' is either equal to 0 (in which case the
     * component to be extracted is trivial), or it points to the acceptance
     * sets associated with the component to be extracted.
     */
    mark_scc = (this->acceptance_sets != 0
		&& this->acceptance_sets->count
		     (this->number_of_acceptance_sets)
		     == this->number_of_acceptance_sets);
  }
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::insert
  (const typename Product<Operations>::size_type state)
/* ----------------------------------------------------------------------------
 *
 * Description:   If `this->mark_scc == true', inserts a product state
 *                identifier to the set of states from which an accepting
 *                strongly connected component is known to be reachable in the
 *                product.  Discards the state otherwise.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (mark_scc)
    markState(state);
}

/* ========================================================================= */
template <class Operations>
inline bool Product<Operations>::AcceptanceReachabilityTracker::isMarked
  (const typename Product<Operations>::size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether an accepting strongly connected component is
 *                known to be reachable from a state in the product.
 *
 * Argument:      state  --  Identifier of the product state to test.
 *
 * Returns:       true iff an accepting strongly connected component is known
 *                to be reachable from the product state.
 *
 * ------------------------------------------------------------------------- */
{
  return (reachability_info.find(state) != reachability_info.end());
}

/* ========================================================================= */
template <class Operations>
inline typename Product<Operations>::size_type
Product<Operations>::AcceptanceReachabilityTracker::numberOfStates() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of product states explored during the
 *                search.
 *
 * Arguments:     None.
 *
 * Returns:       Number of product states explored.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_states;
}

/* ========================================================================= */
template <class Operations>
inline unsigned long int
Product<Operations>::AcceptanceReachabilityTracker::numberOfTransitions() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of product transitions explored during the
 *                search.
 *
 * Arguments:     None.
 *
 * Returns:       Number of transitions explored.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_transitions;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptanceReachabilityTracker::markState
  (const typename Product<Operations>::size_type state)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds a product state to the set of states from which an
 *                accepting strongly connected component is known to be
 *                reachable in the product.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  reachability_info.insert(state);
}



/******************************************************************************
 *
 * Inline function definitions for template class
 * Product<Operations>::AcceptingComponentFinder.
 *
 *****************************************************************************/

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptingComponentFinder::AcceptingComponentFinder
  (const unsigned long int num_accept_sets)
  : AcceptanceTracker(num_accept_sets), construct_component(false)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                Product<Operations>::AcceptingComponentFinder.
 *
 * Arguments:     num_accept_sets  --  Number of acceptance sets in the
 *                                     product.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline Product<Operations>::AcceptingComponentFinder
  ::~AcceptingComponentFinder()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class
 *                Product<Operations>::AcceptingComponentFinder.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template <class Operations>
inline const typename Product<Operations>::AcceptingComponentFinder::SccType&
Product<Operations>::AcceptingComponentFinder::operator()() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the latest accepting maximal strongly connected
 *                component found in the product.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to a set of states containing the
 *                identifiers of product states forming an accepting maximal
 *                strongly connected component in the product.  This set is
 *                empty if no such component has yet been found during the
 *                emptiness check.
 *
 * ------------------------------------------------------------------------- */
{
  return scc;
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptingComponentFinder::beginComponent
  (const typename Product<Operations>::size_type,
   const typename Product<Operations>::size_type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the maximal strongly connected component that
 *                is about to be extracted from the product is an accepting
 *                component.  If this is the case, `this->construct_component'
 *                is set to true to cause all states referred to in subsequent
 *                calls to `this->insert' that occur before the next call to
 *                `this->endComponent' to be inserted into `this->scc'.
 *
 *                A component is accepting iff `this->acceptance_sets' points
 *                to a bit vector in which all bits are set to 1.
 *
 * Arguments:     The arguments are required to support the expected class
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  scc.clear();
  /*
   * The dfs search guarantees (by having made a call to
   * AcceptanceTracker::addNodeToComponent before calling this function) that
   * `this->acceptance_sets' is either equal to 0 (in which case the component
   * to be extracted is trivial), or it points to the acceptance sets
   * associated with the component to be extracted.
   */
  construct_component = (this->acceptance_sets != 0
			 && this->acceptance_sets->count
			      (this->number_of_acceptance_sets)
			      == this->number_of_acceptance_sets);
}

/* ========================================================================= */
template <class Operations>
inline void Product<Operations>::AcceptingComponentFinder::insert
  (const typename Product<Operations>::size_type state)
/* ----------------------------------------------------------------------------
 *
 * Description:   If `this->construct_component == true', inserts a product
 *                state identifier to the set of identifiers representing an
 *                accepting maximal strongly connected component.  Discards the
 *                state otherwise.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (construct_component)
    scc.insert(state);
}

}

#endif /* !PRODUCT_H */
