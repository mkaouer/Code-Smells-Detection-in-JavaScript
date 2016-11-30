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

#ifndef STATDISPLAY_H
#define STATDISPLAY_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <iostream>
#include <vector>
#include "Alloc.h"
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

void printBuchiAutomatonStats                       /* Displays information */
  (ostream& stream,                                 /* about a Büchi        */
   int indent,                                      /* automaton.           */
   vector<AlgorithmTestResults,
          ALLOC(AlgorithmTestResults) >::size_type
     algorithm,
   int result_id);

void printProductAutomatonStats                     /* Displays information */
  (ostream& stream,                                 /* about a product      */
   int indent,                                      /* automaton.           */
   vector<AlgorithmTestResults,
          ALLOC(AlgorithmTestResults) >::size_type
     algorithm,
   int result_id);

void printAcceptanceCycleStats                      /* Displays information */
  (ostream& stream,                                 /* about the acceptance */
   int indent,                                      /* cycles of a product  */
   vector<AlgorithmTestResults,                     /* automaton.           */
          ALLOC(AlgorithmTestResults) >::size_type
     algorithm,
   int result_id);

void printConsistencyCheckStats                     /* Displays the result */
  (ostream& stream,                                 /* of the consistency  */
   int indent,                                      /* check for a given   */
   vector<AlgorithmTestResults,                     /* algorithm.          */
          ALLOC(AlgorithmTestResults) >::size_type
     algorithm);

void printCrossComparisonStats                      /* Displays information */
  (ostream& stream,                                 /* about the model      */
   int indent,                                      /* checking result      */
   vector<AlgorithmTestResults,                     /* cross-comparison     */
          ALLOC(AlgorithmTestResults) >::size_type  /* check.               */
     algorithm);

void printBuchiIntersectionCheckStats               /* Displays the results  */
  (ostream& stream, int indent,                     /* of the Büchi automata */
   vector<AlgorithmTestResults,                     /* intersection          */
          ALLOC(AlgorithmTestResults) >::size_type  /* emptiness checks.     */
     algorithm);
 
void printAllStats                                  /* A shorthand for       */
  (ostream& stream,                                 /* showing all the       */
   int indent,                                      /* information displayed */
   vector<TestStatistics,                           /* by the previous five  */
          ALLOC(TestStatistics)>::size_type         /* functions.            */
     algorithm);  

void printCollectiveCrossComparisonStats            /* Displays a single     */
  (ostream& stream,                                 /* `cell' of the final   */
   vector<TestStatistics,                           /* result cross-         */
          ALLOC(TestStatistics) >::size_type        /* comparison table.     */
     algorithm_y,                                 
   vector<TestStatistics,
          ALLOC(TestStatistics) >::size_type
     algorithm_x,
   int data_type);

void printCollectiveStats                           /* Displays average test */
  (ostream& stream, int indent);                    /* data over all the
                                                     * test rounds
						     * performed so far.
						     */
}

#endif /* !STATDISPLAY_H */
