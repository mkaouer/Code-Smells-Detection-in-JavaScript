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
#include "TestStatistics.h"

/******************************************************************************
 *
 * Function definitions for struct AlgorithmTestResults.
 *
 *****************************************************************************/

/* ========================================================================= */
void AlgorithmTestResults::emptinessReset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Resets the emptiness checking information in an
 *                AlgorithmTestResults structure.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  consistency_check_result = -1;
  consistency_check_comparisons = 0;
  failed_consistency_check_comparisons = 0;

  for (int i = 0; i < 2; i++)
  {
    automaton_stats[i].number_of_product_states = 0;
    automaton_stats[i].number_of_product_transitions = 1;
    automaton_stats[i].emptiness_check_result.clear();
    automaton_stats[i].emptiness_check_performed = false;

    for (vector<AutomatonStats::CrossComparisonStats,
                ALLOC(AutomatonStats::CrossComparisonStats) >::iterator it
	   = automaton_stats[i].cross_comparison_stats.begin();
	 it != automaton_stats[i].cross_comparison_stats.end();
	 ++it)
    {
      it->first = false;
      it->second = 0;
    }
  }
}

/* ========================================================================= */
void AlgorithmTestResults::fullReset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Resets the contents of an AlgorithmTestResults structure.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  emptinessReset();

  for (int i = 0; i < 2; i++)
  {
    if (automaton_stats[i].buchi_automaton != 0)
      delete automaton_stats[i].buchi_automaton;
    automaton_stats[i].buchi_automaton = 0;
    automaton_stats[i].number_of_buchi_states = 0;
    automaton_stats[i].number_of_buchi_transitions = 0;
    automaton_stats[i].number_of_acceptance_sets = 0;
    automaton_stats[i].number_of_msccs = 0;
    automaton_stats[i].buchi_generation_time = 0.0;
    
    for (vector<int, ALLOC(int) >::iterator it
	   = automaton_stats[i].buchi_intersection_check_stats.begin();
	 it != automaton_stats[i].buchi_intersection_check_stats.end();
	 ++it)
      *it = -1;
  }
}
