// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

#include "random.hh"
#include <cstdlib>

namespace spot
{
  void
  srand(unsigned int seed)
  {
#if HAVE_SRAND48 && HAVE_DRAND48
    ::srand48(seed);
#else
    ::srand(seed);
#endif
  }

  double
  drand()
  {
#if HAVE_SRAND48 && HAVE_DRAND48
    return ::drand48();
#else
    double r = ::rand();
    return r / (1.0 + RAND_MAX);
#endif
  }

  int
  mrand(int max)
  {
    return static_cast<int>(max * drand());
  }

  int
  rrand(int min, int max)
  {
    return min + static_cast<int>((max - min + 1) * drand());
  }

  double
  nrand()
  {
    const double r = drand();

    const double lim = 1.e-20;
    if (r < lim)
      return -1./lim;
    if (r > 1.0 - lim)
      return 1./lim;

    double t;
    if (r < 0.5)
      t = sqrt(-2.0 * log(r));
    else
      t = sqrt(-2.0 * log(1.0 - r));

    const double p0 = 0.322232431088;
    const double p1 = 1.0;
    const double p2 = 0.342242088547;
    const double p3 = 0.204231210245e-1;
    const double p4 = 0.453642210148e-4;
    const double q0 = 0.099348462606;
    const double q1 = 0.588581570495;
    const double q2 = 0.531103462366;
    const double q3 = 0.103537752850;
    const double q4 = 0.385607006340e-2;
    const double p = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
    const double q = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));

    if (r < 0.5)
      return (p / q) - t;
    else
      return t - (p / q);
  }

  double
  bmrand()
  {
    static double next;
    static bool has_next = false;

    if (has_next)
      {
	has_next = false;
	return next;
      }

    double x;
    double y;
    double r;
    do
      {
	x = 2.0 * drand() - 1.0;
	y = 2.0 * drand() - 1.0;
	r = x * x + y * y;
      }
    while (r >= 1.0 || r == 0.0);
    r = sqrt(-2 * log(r) / r);
    next = y * r;
    has_next = true;
    return x * r;
  }

  int
  prand(double p)
  {
    double s = 0.0;
    long x = 0;

    while (s < p)
      {
	s -= log(1.0 - drand());
	++x;
      }
    return x - 1;
  }

}
