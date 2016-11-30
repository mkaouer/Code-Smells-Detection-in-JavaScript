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

#ifndef STATDISPLAY_H
#define STATDISPLAY_H

#include <config.h>
#include <iostream>
#include <vector>
#include "LbttAlloc.h"
#include "Configuration.h"
#include "TestStatistics.h"

using namespace std;

extern Configuration configuration;



/******************************************************************************
 *
 * Functions for displaying test statistics.
 *
 *****************************************************************************/

namespace StatDisplay
{                                                    

void printStatTableHeader                           /* Displays a table  */
  (ostream& stream, int indent);                    /* header for test
						     * statistics.
						     */

void printBuchiAutomatonStats                       /* Displays information */
  (ostream& stream,                                 /* about a Büchi        */
   int indent,                                      /* automaton.           */
   vector<AlgorithmTestResults>::size_type
     algorithm,
   int result_id);

void printProductAutomatonStats                     /* Displays information */
  (ostream& stream,                                 /* about a product      */
   int indent,                                      /* automaton.           */
   vector<AlgorithmTestResults>::size_type
     algorithm,
   int result_id);

void printAcceptanceCycleStats                      /* Displays information */
  (ostream& stream,                                 /* about the acceptance */
   int indent,                                      /* cycles of a product  */
   vector<AlgorithmTestResults>::size_type          /* automaton.           */
     algorithm,
   int result_id);

void printConsistencyCheckStats                     /* Displays the result */
  (ostream& stream,                                 /* of the consistency  */
   int indent,                                      /* check for a given   */
   vector<AlgorithmTestResults>::size_type          /* algorithm.          */
     algorithm);

void printCrossComparisonStats                      /* Displays information */
  (ostream& stream, int indent,                     /* about the model      */
   const IntervalList& algorithms);                 /* checking result      */
                                                    /* cross-comparison     */
                                                    /* check.               */

void printBuchiIntersectionCheckStats               /* Displays the results  */
  (ostream& stream, int indent,                     /* of the Büchi automata */
   const IntervalList& algorithms);                 /* intersection          */
                                                    /* emptiness checks.     */
 
void printAllStats                                  /* A shorthand for       */
  (ostream& stream,                                 /* showing all the       */
   int indent,                                      /* information displayed */
   vector<TestStatistics>::size_type algorithm);    /* by the previous five
						     * functions.
						     */

void printCollectiveCrossComparisonStats            /* Displays a single     */
  (ostream& stream,                                 /* `cell' of the final   */
   vector<TestStatistics>::size_type algorithm_y,   /* result cross-         */
   vector<TestStatistics>::size_type algorithm_x,   /* comparison table.     */
   int data_type);

void printCollectiveStats                           /* Displays average test */
  (ostream& stream, int indent);                    /* data over all the
                                                     * test rounds
						     * performed so far.
						     */
}

#endif /* !STATDISPLAY_H */
