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

#ifndef USERCOMMANDS_H
#define USERCOMMANDS_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <deque>
#include <iostream>
#include <string>
#include <vector>
#include <utility>
#include "Alloc.h"
#include "BuchiAutomaton.h"
#include "Configuration.h"
#include "ProductAutomaton.h"
#include "StateSpace.h"

using namespace std;

extern Configuration configuration;

/******************************************************************************
 *
 * Implementations for user commands.
 *
 *****************************************************************************/

namespace UserCommands
{

void computeProductAutomaton                        /* Computes a product */
  (ProductAutomaton*& product_automaton,            /* automaton.         */
   const BuchiAutomaton& buchi_automaton,
   pair<unsigned long int, bool>& last_automaton,
   const pair<unsigned long int, bool>&
     new_automaton);

void printAlgorithmList                             /* Displays a list of */
  (ostream& stream, int indent);                    /* algorithms used in
                                                     * the tests.    
						     */

void synchronizePrefixAndCycle                      /* Synchronizes a prefix */
  (deque<Graph::Graph<GraphEdgeContainer>           /* with a cycle in a     */
           ::size_type,                             /* sequence of graph     */
         ALLOC(Graph::Graph<GraphEdgeContainer>     /* state identifiers.    */
                 ::size_type) >&
     prefix,
   deque<Graph::Graph<GraphEdgeContainer>
           ::size_type,
         ALLOC(Graph::Graph<GraphEdgeContainer>
	         ::size_type) >&
     cycle);

void printCrossComparisonAnalysisResults            /* Analyzes a            */
  (ostream& stream, int indent,                     /* contradiction between */
   bool formula_type,                               /* test results of two   */
   const vector<string, ALLOC(string) >&            /* implementations.      */
     input_tokens,
   ProductAutomaton*& product_automaton,
   pair<unsigned long int, bool>&
     last_product_automaton);

void printConsistencyAnalysisResults                /* Analyzes a            */
  (ostream& stream, int indent,                     /* contradicition in the */
   const vector<string, ALLOC(string) >&            /* model checking result */
     input_tokens);                                 /* consistency check for
						     * an implementation.
						     */

void printAutomatonAnalysisResults                  /* Analyzes a           */
  (ostream& stream, int indent,                     /* contradiction in the */
   unsigned long int algorithm1,                    /* Büchi automata       */
   unsigned long int algorithm2);                   /* intersection
                                                     * emptiness check.
						     */

void printPath                                      /* Displays information */
  (ostream& stream, int indent,                     /* about a single       */
   const deque<StateSpace::size_type,               /* system execution.    */
               ALLOC(StateSpace::size_type) >&
     prefix,
   const deque<StateSpace::size_type,
               ALLOC(StateSpace::size_type) >&
     cycle,
   const StateSpace& path);

void printAcceptingCycle                            /* Displays information */
  (ostream& stream, int indent,                     /* a single automaton   */
   vector<Configuration::AlgorithmInformation,      /* execution.           */
          ALLOC(Configuration::AlgorithmInformation) >
     ::size_type
     algorithm_id,
   const deque<StateSpace::size_type,
               ALLOC(StateSpace::size_type) >&
     prefix,
   const deque<StateSpace::size_type,
               ALLOC(StateSpace::size_type) >&
     cycle,
   const BuchiAutomaton& automaton);

void printBuchiAutomaton                            /* Displays information */
  (ostream& stream, int indent,                     /* about a Büchi        */
   bool formula_type,                               /* automaton.           */
   vector<string, ALLOC(string) >& input_tokens,
   Graph::GraphOutputFormat fmt);

void evaluateFormula                                /* Displays information */
  (ostream& stream, int indent,                     /* about existence of   */
   bool formula_type,                               /* accepting system     */
   vector<string, ALLOC(string) >& input_tokens);   /* executions.          */

void printFormula                                   /* Displays a formula */
  (ostream& stream, int indent,                     /* used for testing.  */
   bool formula_type);

void printCommandHelp                               /* Displays help about */
  (ostream& stream, int indent,                     /* user commands.      */
   const vector<string, ALLOC(string) >&
     input_tokens);           

void printInconsistencies                           /* Lists the system   */
  (ostream& stream, int indent,                     /* states failing the */
   vector<string, ALLOC(string) >& input_tokens);   /* consistency check
                                                     * for an algorihm.
						     */

void printTestResults                               /* Displays the test   */
  (ostream& stream, int indent,                     /* results of the last */  
   vector<string, ALLOC(string) >& input_tokens);   /* round performed.    */

void printStateSpace                                /* Displays information */
  (ostream& stream, int indent,                     /* about a state space. */
   vector<string, ALLOC(string) >& input_tokens,
   Graph::GraphOutputFormat fmt);

void changeVerbosity                                /* Displays or changes */
  (const vector<string, ALLOC(string) >&            /* the verbosity of    */
     input_tokens);                                 /* output.             */

void changeAlgorithmState                           /* Enables or disables a */
  (vector<string, ALLOC(string) >& input_tokens,    /* set of algorithms     */
   bool enable);                                    /* used in the tests.    */

}

#endif /* !USERCOMMANDS_H */
