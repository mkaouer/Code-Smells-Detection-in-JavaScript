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

#include "modgray.hh"

namespace spot
{

  loopless_modular_mixed_radix_gray_code::
  loopless_modular_mixed_radix_gray_code(int n)
    : n_(n), a_(0), f_(0), m_(0), s_(0), non_one_radixes_(0)
  {
  }

  loopless_modular_mixed_radix_gray_code::
  ~loopless_modular_mixed_radix_gray_code()
  {
    delete[] a_;
    delete[] f_;
    delete[] m_;
    delete[] s_;
    delete[] non_one_radixes_;
  }

  void
  loopless_modular_mixed_radix_gray_code::first()
  {
    if (!non_one_radixes_)
      {
	// This computation needs to be done only once, but we cannot
	// do it in the constructor because it involves virtual
	// functions.

	// non_one_radixes_ needs to hold at most n_ integer, maybe less
	// we do not know yet and do not care (saving these extra bytes
	// would require two loops over a_last(j); this doesn't seem
	// worth the hassle).  We update n_ before allocating the other
	// arrays.
	non_one_radixes_ = new int[n_];
	int k = 0;
	for (int j = 0; j < n_; ++j)
	  {
	    a_first(j);
	    if (!a_last(j))
	      non_one_radixes_[k++] = j;
	  }
	n_ = k;
	a_ = new int[k];
	f_ = new int[k + 1];
	m_ = new int[k];
	s_ = new int[k];

	for (int j = 0; j < n_; ++j)
	  m_[j] = -1;
	f_[n_] = n_;
      }

    // Reset everything except m_[j] (let's preserve discovered
    // radixes between runs) and f_[n_] (never changes).
    for (int j = 0; j < n_; ++j)
      {
	a_[j] = 0;
	a_first(non_one_radixes_[j]);
	s_[j] = m_[j];
	f_[j] = j;
      }
    done_ = false;
  }

  // Update one item of the tuple and return its position.
  int
  loopless_modular_mixed_radix_gray_code::next()
  {
    int j = f_[0];

    if (j == n_)
      {
	done_ = true;
	return -1;
      }

    f_[0] = 0;

    int real_j = non_one_radixes_[j];

    if (a_[j] == m_[j])
      {
	a_[j] = 0;
	a_first(real_j);
      }
    else
      {
	++a_[j];
	a_next(real_j);
	// Discover the radix on-the-fly.
	if (m_[j] == -1 && a_last(real_j))
	  s_[j] = m_[j] = a_[j];
      }

    if (a_[j] == s_[j])
      {
	--s_[j];
	if (s_[j] < 0)
	  s_[j] = m_[j];
	f_[j] = f_[j + 1];
	f_[j + 1] = j + 1;
      }

    return real_j;
  }

} // spot
