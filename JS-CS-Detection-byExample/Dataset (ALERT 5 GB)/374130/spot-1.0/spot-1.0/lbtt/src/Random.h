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

#ifndef RANDOM_H
#define RANDOM_H

#include <config.h>
#include <cstdlib>

#ifdef HAVE_RAND48
#define rand lrand48
#define srand srand48
static const double MAXRAND = static_cast<double>(1 << 30) * 2.0;
#else
static const double MAXRAND = RAND_MAX + 1.0;
#endif /* HAVE_RAND48 */



/******************************************************************************
 *
 * Functions for random number generation.  If HAVE_RAND48 is defined, the
 * functions rely on the lrand48() function for generating a random integer
 * between 0 and 2^31 and srand48() for setting the seed for the random
 * number generator; otherwise, the rand() and srand() functions are used,
 * respectively.
 *
 *****************************************************************************/

/* ========================================================================= */
inline void SRAND(unsigned int seed)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes the random number generator with a seed value.
 *
 * Argument:      seed  --  Seed for the random number generator.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  srand (seed);
}

/* ========================================================================= */
inline double DRAND()
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random double.
 *
 * Arguments:     None.
 *
 * Returns:       A random double in the half-open interval [0.0,1.0).
 *
 * ------------------------------------------------------------------------- */
{
  return rand () / MAXRAND;
}

/* ========================================================================= */
inline long int LRAND(long int min, long int max)
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random long integer in a given interval.
 *
 * Arguments:     min, max  --  Bounds for the interval.
 *
 * Returns:       A random long integer in the half-open interval [min,max).
 *
 * ------------------------------------------------------------------------- */
{
  return min + static_cast<long int>(DRAND() * (max - min));
}

#ifdef HAVE_RAND48
#undef rand
#undef srand
#endif /* HAVE_RAND48 */

#endif /* !RANDOM_H */
