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

#ifndef SPOT_MISC_RANDOM_HH
# define SPOT_MISC_RANDOM_HH

# include <cmath>

namespace spot
{
  /// \addtogroup random Random functions
  /// \ingroup misc_tools
  /// @{

  /// \brief Reset the seed of the pseudo-random number generator.
  ///
  /// \see drand, mrand, rrand
  void srand(unsigned int seed);

  /// \brief Compute a pseudo-random integer value between \a min and
  /// \a max included.
  ///
  /// \see drand, mrand, srand
  int rrand(int min, int max);

  /// \brief Compute a pseudo-random integer value between 0 and
  /// \a max-1 included.
  ///
  /// \see drand, rrand, srand
  int mrand(int max);

  /// \brief Compute a pseudo-random double value
  /// between 0.0 and 1.0 (1.0 excluded).
  ///
  /// \see mrand, rrand, srand
  double drand();

  /// \brief Compute a pseudo-random double value
  /// following a standard normal distribution.  (Odeh & Evans)
  ///
  /// This uses a polynomial approximation of the inverse cumulated
  /// density function from Odeh & Evans, Journal of Applied
  /// Statistics, 1974, vol 23, pp 96-97.
  double nrand();

  /// \brief Compute a pseudo-random double value
  /// following a standard normal distribution.  (Box-Muller)
  ///
  /// This uses the polar form of the Box-Muller transform
  /// to generate random values.
  double bmrand();

  /// \brief Compute pseudo-random integer value between 0
  /// and \a n included, following a binomial distribution
  /// for probability \a p.
  ///
  /// \a gen must be a random function computing a pseudo-random
  /// double value following a standard normal distribution.
  /// Use nrand() or bmrand().
  ///
  /// Usually approximating a binomial distribution using a normal
  /// distribution and is accurate only if <code>n*p</code> and
  /// <code>n*(1-p)</code> are greater than 5.
  template<double (*gen)()>
  class barand
  {
  public:
    barand(int n, double p)
      : n_(n), m_(n * p), s_(sqrt(n * p * (1 - p)))
    {
    }

    int
    rand() const
    {
      int res;

      for (;;)
	{
	  double x = gen() * s_ + m_;
	  if (x < 0.0)
	    continue;
	  res = static_cast<int> (x);
          if (res <= n_)
	    break;
        }
      return res;
    }
  protected:
    const int n_;
    const double m_;
    const double s_;
  };

  /// \brief Return a pseudo-random positive integer value
  /// following a Poisson distribution with parameter \a p.
  ///
  /// \pre <code>p > 0</code>
  int prand(double p);
  /// @}
}

#endif // SPOT_MISC_RANDOM_HH
