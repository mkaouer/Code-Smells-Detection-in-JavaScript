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

#ifndef PRODUCTAUTOMATON_H
#define PRODUCTAUTOMATON_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <deque>
#include <iostream>
#include <vector>
#include "Alloc.h"
#include "BitArray.h"
#include "BuchiAutomaton.h"
#include "EdgeContainer.h"
#include "Exception.h"
#include "Graph.h"
#include "StateSpace.h"

using namespace std;

extern bool user_break;

namespace UserCommands
{
  extern void printAutomatonAnalysisResults
    (ostream&, int, unsigned long int, unsigned long int);
}

namespace Graph 
{

/******************************************************************************
 *
 * A class for representing the synchronous product of a Büchi automaton and
 * a state space.
 *
 *****************************************************************************/

class ProductAutomaton : public Graph<GraphEdgeContainer>
{
private:
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ProductState :                              /* A class for           */
    public Graph<GraphEdgeContainer>::Node          /* representing the      */
  {                                                 /* states of the product
                                                     * automaton.
                                                     */
  public:
    explicit ProductState                           /* Constructor. */
      (const size_type hash_val = 0);

    ~ProductState();                                /* Destructor. */
  
    /* `edges' inherited from Graph<GraphEdgeContainer>::Node */
    
    size_type hashValue() const;                    /* Get or set the hash   */
    size_type& hashValue();                         /* value for the product
                                                     * state (this value can
                                                     * be used to extract
						     * the identifiers of
						     * the original state
						     * space and the Büchi
						     * automaton with which
						     * the product state is
						     * associated).
						     */

    void print                                       /* Writes information */
      (ostream& stream = cout,                       /* about the product  */
       const int indent = 0,                         /* state to a stream. */
       const GraphOutputFormat fmt = NORMAL) const;

  private:
    friend class ProductAutomaton;

    ProductState(const ProductState&);              /* Prevent copying and   */
    ProductState& operator=(const ProductState&);   /* assignment of
						     * ProductState objects.
						     */

    size_type hash_value;                           /* Hash value for the
                                                     * product state (can be
                                                     * used to extract the
                                                     * identifiers of the
						     * original state space and
						     * the Büchi automaton with
						     * which the product state 
						     * is associated).
						     */

    Edge* incoming_edge;                            /* The unique edge pointing
						     * to `this' ProductState.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ProductScc :                                /* A class for storing  */
    public vector<ProductAutomaton::size_type,      /* maximal strongly     */
               ALLOC(ProductAutomaton::size_type) > /* connected components */
  {                                                 /* of the product.
						     */
  public:                        
    ProductScc();                                   /* Constructor. */

    /* default copy constructor */

    ~ProductScc();                                  /* Destructor. */

    /* default assignment operator */

    bool fair                                       /* Tests whether the    */
      (const ProductAutomaton& product_automaton)   /* component is fair,   */
      const;                                        /* i.e. it is a
						     * nontrivial component
						     * with a state from
						     * each acceptance set
						     * of the Büchi
						     * automaton used for
						     * constructing a
						     * given product.
						     */
    
    void insert                                     /* Inserts a state into */
      (const ProductAutomaton::size_type            /* the container.       */
         product_state);
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

public:
  class ProductSizeException;                       /* An exception class for
						     * reporting the situation
						     * where the size of the
						     * product automaton may
						     * be too big.
						     */

  friend class ProductScc;
  friend void UserCommands::printAutomatonAnalysisResults
    (ostream&, int, unsigned long int, unsigned long int);

  ProductAutomaton();                               /* Constructor. */

  ~ProductAutomaton();                              /* Destructor. */

  ProductState& operator[](const size_type index)   /* Indexing operator. No */
    const;                                          /* range check is performed
						     * on the argument.
						     */

  ProductState& node(const size_type index) const;  /* Synonym for the indexing
                                                     * operator. This function
						     * also checks the range of
						     * the argument.
                                                     */

  /* `size' inherited from Graph<GraphEdgeContainer> */

  /* `empty' inherited from Graph<GraphEdgeContainer> */

  void clear();                                     /* Makes the automaton
						     * empty.
						     */

  void connect                                      /* Connects two states */
    (const size_type father,                        /* of the product      */ 
     const size_type child);                        /* automaton.          */

  void disconnect                                   /* Disconnects two       */
    (const size_type father,                        /* states of the product */
     const size_type child);                        /* automaton.	     */

  /* `connected' inherited from Graph<GraphEdgeContainer> */

  /* `stats' inherited from Graph<GraphEdgeContainer> */

  /* `subgraphStats' inherited from Graph<GraphEdgeContainer> */

  void computeProduct                               /* Function for       */
    (const BuchiAutomaton& automaton,               /* initializing the   */
     const StateSpace& statespace,                  /* product automaton. */
     const bool global_product);

  StateSpace::size_type systemState                 /* Returns the           */
    (const size_type state) const;                  /* identifier of the
						     * state of the original
						     * state space with
						     * which a given product
						     * state is associated.
						     */

  BuchiAutomaton::size_type buchiState              /* Returns the           */
    (const size_type state) const;                  /* identifier of the
						     * state of the original
						     * automaton with which
						     * a given product state
						     * is associated.
						     */

  void emptinessCheck(Bitset& result) const;        /* Performs an emptiness
                                                     * check on the product.
                                                     */

  void findAcceptingExecution                       /* Extracts an accepting */
    (const StateSpace::size_type initial_state,     /* execution from the    */
     pair<deque<StateIdPair, ALLOC(StateIdPair) >,  /* product automaton.    */
          deque<StateIdPair,
                ALLOC(StateIdPair) > >&
       execution) const;

  void print                                        /* Writes information */
    (ostream& stream = cout,                        /* about the product  */
     const int indent = 0,                          /* automaton to a     */
     const GraphOutputFormat fmt = NORMAL) const;   /* stream.            */

private:
  ProductAutomaton(const ProductAutomaton&);        /* Prevent copying and */
  ProductAutomaton& operator=                       /* assignment of       */
    (const ProductAutomaton&);                      /* ProductAutomaton
						     * objects.
						     */

  size_type expand(size_type node_count = 1);       /* Inserts states to the
						     * product automaton.
						     */

  const BuchiAutomaton* buchi_automaton;            /* A pointer to the
						     * Büchi automaton used for
						     * constructing the
						     * product.
						     */

  StateSpace::size_type statespace_size;            /* Size of the state space
                                                     * used for constructing
                                                     * the product automaton.
                                                     */

#ifdef HAVE_OBSTACK_H                               /* Storage for product */
  ObstackAllocator store;                           /* states and          */
#endif /* HAVE_OBSTACK_H */                         /* transitions.        */
};



/******************************************************************************
 *
 * An exception class for reporting the situation where the product may be too
 * big to compute.
 *
 *****************************************************************************/

class ProductAutomaton::ProductSizeException : public Exception
{
public:
  ProductSizeException();                           /* Constructor. */

  /* default copy constructor */

  ~ProductSizeException() throw();                  /* Destructor. */

  ProductSizeException&                             /* Assignment operator. */
    operator=(const ProductSizeException& e);    

  /* `what' inherited from class Exception */
};



/******************************************************************************
 *
 * Inline function definitions for class ProductAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ProductAutomaton::ProductAutomaton() :
  buchi_automaton(0), statespace_size(0)
#ifdef HAVE_OBSTACK_H
, store()
#endif /* HAVE_OBSTACK_H */
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ProductAutomaton. Initializes a
 *                new object for storing the product of a Büchi automaton and a
 *                state space. The product must then be explicitly initialized
 *                by calling the function `computeProduct' on the object.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomaton::~ProductAutomaton()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ProductAutomaton.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  clear();
}

/* ========================================================================= */
inline ProductAutomaton::ProductState&
ProductAutomaton::operator[](const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Indexing operator for class ProductAutomaton. Can be used to
 *                refer to the individual states of the product automaton. No
 *                range check will be performed on the argument.
 *
 * Argument:      index  --  Index of a state of the product automaton.
 *
 * Returns:       A reference to the product state corresponding to the index.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<ProductState&>(*nodes[index]);
}

/* ========================================================================= */
inline ProductAutomaton::ProductState&
ProductAutomaton::node(const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for referring to a single state of a
 *                ProductAutomaton. This function will also check the range
 *                argument.
 *
 * Argument:      index  --  Index of a state of the product automaton.
 *
 * Returns:       A reference to the corresponding product state.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<ProductState&>(Graph<GraphEdgeContainer>::node(index));
}

/* ========================================================================= */
inline StateSpace::size_type ProductAutomaton::systemState
  (const size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the identifier of the system state with which a
 *                given product state is associated. This function will perform
 *                no range checks on its argument.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Identifier of a state in a state space.
 *
 * ------------------------------------------------------------------------- */
{
  return operator[](state).hashValue() % statespace_size;
}

/* ========================================================================= */
inline StateSpace::size_type ProductAutomaton::buchiState
  (const size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the identifier of the state of the Büchi automaton
 *                with which a given product state is associated. This function
 *                will perform no range checks on its argument.
 *
 * Argument:      state  --  Identifier of a product state.
 *
 * Returns:       Identifier of a state in a Büchi automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return operator[](state).hashValue() / statespace_size;
}



/******************************************************************************
 *
 * Inline function definitions for class ProductAutomaton::ProductState.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ProductAutomaton::ProductState::ProductState(const size_type hash_val) :
  Graph<GraphEdgeContainer>::Node(), hash_value(hash_val), incoming_edge(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ProductAutomaton::ProductState. Creates
 *                a new object representing a synchronous product of a state of
 *                a Büchi automaton with a state of a state space.
 *
 * Arguments:     hash_val   --  Hash value for the product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomaton::ProductState::~ProductState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ProductAutomaton::ProductState.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (incoming_edge != 0)
  {
#ifdef HAVE_OBSTACK_H
    incoming_edge->~Edge();
#else
    delete incoming_edge;
#endif /* HAVE_OBSTACK_H */
  }
  outgoing_edges.clear();
}

/* ========================================================================= */
inline ProductAutomaton::size_type ProductAutomaton::ProductState::hashValue() 
  const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the product state's hash value by value.
 *
 * Arguments:     None.
 *
 * Returns:       The hash value of the product state.
 *
 * ------------------------------------------------------------------------- */
{
  return hash_value;
}

/* ========================================================================= */
inline ProductAutomaton::size_type& ProductAutomaton::ProductState::hashValue()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the product state's hash value by reference. (This
 *                function can therefore be used to change the value.)
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the hash value of the product state.
 *
 * ------------------------------------------------------------------------- */
{
  return hash_value;
}

    

/******************************************************************************
 *
 * Inline function definitions for class ProductAutomaton::ProductScc.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ProductAutomaton::ProductScc::ProductScc()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ProductAutomaton::ProductScc. Creates a
 *                new container for storing a maximal strongly connected
 *                component of a ProductAutomaton.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomaton::ProductScc::~ProductScc()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ProductAutomaton::ProductScc.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void ProductAutomaton::ProductScc::insert
  (const ProductAutomaton::size_type product_state)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new product state identifier to the container.
 *
 * Argument:      product_state  --  Identifier of a product state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  push_back(product_state);
}



/******************************************************************************
 *
 * Inline function definitions for class
 * ProductAutomaton::ProductSizeException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ProductAutomaton::ProductSizeException::ProductSizeException() :
  Exception("product may be too large")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ProductAutomaton::ProductSizeException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomaton::ProductSizeException::~ProductSizeException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ProductAutomaton::ProductSizeException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomaton::ProductSizeException&
ProductAutomaton::ProductSizeException::operator=
  (const ProductAutomaton::ProductSizeException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                ProductAutomaton::ProductSizeException. Assigns the value of
 *                another ProductAutomaton::ProductSizeException to `this' one.
 *
 * Arguments:     e  --  A reference to a constant
 *                       ProductAutomaton::ProductSizeException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !PRODUCTAUTOMATON_H */
