// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "minato.hh"
#include <utility>
#include <cassert>

namespace spot
{

  minato_isop::minato_isop(bdd input)
    : ret_(bddfalse)

  {
    todo_.push(local_vars(input, input, bdd_support(input)));
    cube_.push(bddtrue);
  }

  minato_isop::minato_isop(bdd input, bdd vars)
    : ret_(bddfalse)
  {
    todo_.push(local_vars(input, input, vars));
    cube_.push(bddtrue);
  }

  bdd
  minato_isop::next()
  {
    while (todo_.size())
      {
	local_vars& l = todo_.top();
	switch (l.step)
	  {
	  case local_vars::FirstStep:
	  next_var:
	    {
	      if (l.f_min == bddfalse)
		{
		  ret_ = bddfalse;
		  todo_.pop();
		  continue;
		}
	      if (l.vars == bddtrue || l.f_max == bddtrue)
		{
		  ret_ = l.f_max;
		  todo_.pop();
		  return cube_.top() & ret_;
		}
	      assert(l.vars != bddfalse);

	      // Pick the first variable in VARS that is used by F_MIN
	      // or F_MAX.  We know that VARS, F_MIN or F_MAX are not
	      // constants (bddtrue or bddfalse) because one of the
	      // two above `if' would have matched; so it's ok to call
	      // bdd_var().
	      int v = bdd_var(l.vars);
	      l.vars = bdd_high(l.vars);
	      int v_min = bdd_var(l.f_min);
	      int v_max = bdd_var(l.f_max);
	      if (v < v_min && v < v_max)
		// Do not use a while() for this goto, because we want
		// `continue' to be relative to the outermost while().
		goto next_var;

	      l.step = local_vars::SecondStep;

	      bdd v0 = bdd_nithvar(v);
	      l.v1 = bdd_ithvar(v);

	      // All the following should be equivalent to
	      //   f0_min = bdd_restrict(f_min, v0);
	      //   f0_max = bdd_restrict(f_max, v0);
	      //   f1_min = bdd_restrict(f_min, v1);
	      //   f1_max = bdd_restrict(f_max, v1);
	      // but we try to avoid bdd_restrict when possible.
	      if (v == v_min)
		{
		  l.f0_min = bdd_low(l.f_min);
		  l.f1_min = bdd_high(l.f_min);
		}
	      else if (v_min < v)
		{
		  l.f0_min = bdd_restrict(l.f_min, v0);
		  l.f1_min = bdd_restrict(l.f_min, l.v1);
		}
	      else
		{
		  l.f1_min = l.f0_min = l.f_min;
		}
	      if (v == v_max)
		{
		  l.f0_max = bdd_low(l.f_max);
		  l.f1_max = bdd_high(l.f_max);
		}
	      else if (v_max < v)
		{
		  l.f0_max = bdd_restrict(l.f_max, v0);
		  l.f1_max = bdd_restrict(l.f_max, l.v1);
		}
	      else
		{
		  l.f1_max = l.f0_max = l.f_max;
		}

	      cube_.push(cube_.top() & v0);
	      todo_.push(local_vars(l.f0_min - l.f1_max, l.f0_max, l.vars));
	    }
	    continue;

	  case local_vars::SecondStep:
	    l.step = local_vars::ThirdStep;
	    l.g0 = ret_;
	    cube_.pop();
	    cube_.push(cube_.top() & l.v1);
	    todo_.push(local_vars(l.f1_min - l.f0_max, l.f1_max, l.vars));
	    continue;

	  case local_vars::ThirdStep:
	    l.step = local_vars::FourthStep;
	    l.g1 = ret_;
	    cube_.pop();
	    {
	      bdd f0s_min = l.f0_min - l.g0;
	      bdd f1s_min = l.f1_min - l.g1;
	      bdd fs_max = l.f0_max & l.f1_max;
	      bdd fs_min = fs_max & (f0s_min | f1s_min);
	      todo_.push(local_vars(fs_min, fs_max, l.vars));
	    }
	    continue;

	  case local_vars::FourthStep:
	    ret_ |= (l.g0 - l.v1) | (l.g1 & l.v1);
	    todo_.pop();
	    continue;
	  }
	// Unreachable code.
	assert(0);
      }
    return bddfalse;
  }

}
