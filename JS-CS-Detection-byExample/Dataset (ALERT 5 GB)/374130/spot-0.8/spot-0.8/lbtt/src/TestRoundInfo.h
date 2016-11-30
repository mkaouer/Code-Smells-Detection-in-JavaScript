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

#ifndef TESTROUNDINFO_H
#define TESTROUNDINFO_H

#include <config.h>
#include <cstdio>
#include <fstream>
#include <iostream>
#include <vector>
#include "LbttAlloc.h"
#include "Exception.h"
#include "LtlFormula.h"
#include "PathIterator.h"
#include "StateSpace.h"
#include "TempFsysName.h"

using namespace std;

namespace SharedTestData
{

/******************************************************************************
 *
 * A data structure for storing test round control information and some test-
 * related data.
 *
 *****************************************************************************/

class TestRoundInfo
{
public:
  TestRoundInfo();                                  /* Constructor. */

  ~TestRoundInfo();                                 /* Destructor. */

  Exceptional_ostream cout;                         /* Exception-guarded output
						     * stream for messages.
						     */

  istream* formula_input_stream;                    /* Stream for reading input
						     * formulae.
						     */

  ifstream formula_input_file;                      /* File for reading input
						     * formulae.
						     */

  ofstream transcript_file;                         /* Output stream for
						     * logging operations.
						     */

  unsigned long int number_of_translators;          /* Number of translators.
						     */

  unsigned long int current_round;                  /* Number of current round.
						     */

  unsigned long int next_round_to_run;              /* Indicates the next
						     * round to run.
						     */

  unsigned long int next_round_to_stop;             /* Indicates the next
						     * test round after which
						     * to pause and wait for
						     * user commands.
						     */

  unsigned long int error_report_round;             /* Number of the last round
						     * in which something was
						     * written to the error
						     * transcript file.
						     */

  bool error;                                       /* True if an error
						     * occurred during the
						     * current round.
						     */

  bool all_tests_successful;                        /* True if no errors have
						     * occurred during testing.
						     */

  bool skip;                                        /* True if the current
						     * round is to be skipped.
						     */

  bool abort;                                       /* True if the testing is
						     * to be aborted.
						     */

  unsigned long int num_generated_statespaces;      /* State space */
  unsigned long int total_statespace_states;        /* statistics. */
  unsigned long int total_statespace_transitions;

  unsigned long int num_processed_formulae;         /* Number of processed LTL
						     * formulae.
						     */

  bool fresh_statespace;                            /* True if a new state
						     * space was generated in
						     * the current test round.
						     */
  
  const Graph::StateSpace* statespace;              /* Pointer to the state
						     * space used in the
						     * current test round.
						     */

  Graph::PathIterator* path_iterator;               /* Pointer to a "path
						     * iterator" needed
						     * when using enumerated
						     * paths as state spaces.
						     */

  unsigned long int real_emptiness_check_size;      /* Number of states in the
						     * state space where the
						     * emptiness check should
						     * be performed.
						     */

  unsigned long int                                 /* Number of the round  */
    next_round_to_change_statespace;                /* in which to generate
						     * a new state space.
						     */

  unsigned long int                                 /* Number of the round  */
    next_round_to_change_formula;                   /* in which to generate
						     * (or read) a new LTL
						     * formula.
						     */

  bool fresh_formula;                               /* True is a new formula
						     * was generated (or read
						     * from a file) in the
						     * current round.
						     */

  vector<class ::Ltl::LtlFormula*> formulae;        /* Formulae used in the
                                                     * current round:
                                                     * formulae[0]:
						     *   positive formula in
						     *   negation normal
						     *   form
						     * formulae[1]:
						     *   negated formula in
						     *   negation normal
						     *   form
						     * formulae[2]:
						     *   positive formula as
						     *   generated
						     * formulae[3]:
						     *   negative formula as
						     *   generated
						     */

  vector<bool> formula_in_file;                     /* The values in this
						     * vector will be set to
						     * true when the
						     * corresponding
						     * formulae have been
						     * written to files
						     * successfully. Index
						     * 0 corresponds to the
						     * positive formula and
						     * 1 to the negative
						     * formula.
						     */

  TempFsysName* formula_file_name[2];               /* Names for temporary */
  TempFsysName* automaton_file_name;                /* files.              */
  TempFsysName* cout_capture_file;
  TempFsysName* cerr_capture_file;

private:
  TestRoundInfo(const TestRoundInfo& info);         /* Prevent copying and */
  TestRoundInfo& operator=                          /* assignment of       */
    (const TestRoundInfo& info);		    /* TestRoundInfo
						     * objects.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class TestRoundInfo.
 *
 *****************************************************************************/

/* ========================================================================= */
inline TestRoundInfo::TestRoundInfo() :
  cout(&std::cout, ios::failbit | ios::badbit), number_of_translators(0),
  current_round(1), next_round_to_run(1), next_round_to_stop(1),
  error_report_round(0), error(false), all_tests_successful(true),
  skip(false), abort(false), num_generated_statespaces(0),
  total_statespace_states(0), total_statespace_transitions(0),
  num_processed_formulae(0), fresh_statespace(false), statespace(0),
  path_iterator(0), real_emptiness_check_size(0),
  next_round_to_change_statespace(1), next_round_to_change_formula(1),
  fresh_formula(false), formulae(4, static_cast<class ::Ltl::LtlFormula*>(0)),
  formula_in_file(2, false), automaton_file_name(0), cout_capture_file(0),
  cerr_capture_file(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class TestRoundInfo. Creates a new
 *                TestRoundInfo object.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  formula_file_name[0] = formula_file_name[1] = 0;
}

/* ========================================================================= */
inline TestRoundInfo::~TestRoundInfo()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class TestRoundInfo.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

}

#endif /* !TESTROUNDINFO_H */
