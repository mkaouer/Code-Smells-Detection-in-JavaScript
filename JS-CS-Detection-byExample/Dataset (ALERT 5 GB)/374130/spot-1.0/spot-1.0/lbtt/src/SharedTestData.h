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

#ifndef SHAREDTESTDATA_H
#define SHAREDTESTDATA_H

#include <vector>
#include "LbttAlloc.h"
#include "Exception.h"
#include "TestRoundInfo.h"
#include "TestStatistics.h"

/******************************************************************************
 *
 * Declarations of variables for storing test results and maintaining test
 * state information.
 *
 *****************************************************************************/

namespace SharedTestData
{

extern TestRoundInfo round_info;                    /* Data structure for
						     * storing information
						     * about the current test
						     * round.
						     */

extern vector<AlgorithmTestResults> test_results;   /* Test results for each
                                                     * implementation.
						     */

extern vector<TestStatistics> final_statistics;     /* Overall test
                                                     * statistics for each
                                                     * implementation.
						     */
}

#endif /* !SHAREDTESTDATA_H */
