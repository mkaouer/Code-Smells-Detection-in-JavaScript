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

#ifndef USERCOMMANDS_H
#define USERCOMMANDS_H

#include <config.h>
#include <deque>
#include <iostream>
#include <map>
#include <string>
#include <vector>
#include <utility>
#include "LbttAlloc.h"
#include "BuchiAutomaton.h"
#include "Configuration.h"
#include "EdgeContainer.h"
#include "Graph.h"
#include "IntervalList.h"
#include "Product.h"
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

unsigned long int parseAlgorithmId                  /* Parses an      */
  (const string& id);                               /* implementation
						     * identifier.
						     */

void parseAlgorithmId                               /* Parses a list of */
  (const string& ids, IntervalList& algorithms);    /* implementation
						     * identifiers.
						     */

void printAlgorithmList                             /* Displays a list of */
  (ostream& stream, int indent);                    /* algorithms used in
                                                     * the tests.    
						     */

void printCrossComparisonAnalysisResults            /* Analyzes a            */
  (ostream& stream, int indent,                     /* contradiction between */
   bool formula_type,                               /* test results of two   */
   const vector<string>& input_tokens);             /* implementations.      */

void printConsistencyAnalysisResults                /* Analyzes a            */
  (ostream& stream, int indent,                     /* contradicition in the */
   const vector<string>& input_tokens);             /* model checking result
                                                     * consistency check for
						     * an implementation.
						     */

void printAutomatonAnalysisResults                  /* Analyzes a           */
  (ostream& stream, int indent,                     /* contradiction in the */
   const vector<string>& input_tokens);             /* Büchi automata
                                                     * intersection
                                                     * emptiness check.
						     */

void printPath                                      /* Displays information */
  (ostream& stream, int indent,                     /* about a single       */
   const StateSpace::Path& prefix,                  /* system execution.    */
   const StateSpace::Path& cycle,
   const StateSpace& path);

void printAcceptingCycle                            /* Displays information */
  (ostream& stream, int indent,                     /* a single automaton   */
   vector<Configuration::AlgorithmInformation>      /* execution.           */
     ::size_type
     algorithm_id,
   const BuchiAutomaton::Path& aut_prefix,
   const BuchiAutomaton::Path& aut_cycle,
   const BuchiAutomaton& automaton,
   const StateSpace::Path& path_prefix,
   const StateSpace::Path& path_cycle,
   const StateSpace& statespace);

void printBuchiAutomaton                            /* Displays information */
  (ostream& stream, int indent,                     /* about a Büchi        */
   bool formula_type,                               /* automaton.           */
   vector<string>& input_tokens,
   Graph::GraphOutputFormat fmt);

void evaluateFormula                                /* Displays information */
  (ostream& stream, int indent,                     /* about existence of   */
   bool formula_type,                               /* accepting system     */
   vector<string>& input_tokens);                   /* executions.          */

void printFormula                                   /* Displays a formula */
  (ostream& stream, int indent,                     /* used for testing.  */
   bool formula_type,
   const vector<string>& input_tokens);

void printCommandHelp                               /* Displays help about */
  (ostream& stream, int indent,                     /* user commands.      */
   const vector<string>& input_tokens);

void printInconsistencies                           /* Lists the system   */
  (ostream& stream, int indent,                     /* states failing the */
   vector<string>& input_tokens);                   /* consistency check
                                                     * for an algorihm.
						     */

void printTestResults                               /* Displays the test   */
  (ostream& stream, int indent,                     /* results of the last */  
   vector<string>& input_tokens);                   /* round performed.    */

void printStateSpace                                /* Displays information */
  (ostream& stream, int indent,                     /* about a state space. */
   vector<string>& input_tokens,
   Graph::GraphOutputFormat fmt);

void changeVerbosity                                /* Displays or changes */
  (const vector<string>& input_tokens);             /* the verbosity of
                                                     * output.
						     */

void changeAlgorithmState                           /* Enables or disables a */
  (vector<string>& input_tokens, bool enable);      /* set of algorithms
                                                     * used in the tests.
						     */

}

#endif /* !USERCOMMANDS_H */
