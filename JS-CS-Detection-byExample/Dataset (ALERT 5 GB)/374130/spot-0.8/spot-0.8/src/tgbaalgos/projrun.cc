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

#include "projrun.hh"
#include "tgba/tgba.hh"
#include "tgbaalgos/emptiness.hh"

namespace spot
{

  tgba_run*
  project_tgba_run(const tgba* a_run, const tgba* a_proj, const tgba_run* run)
  {
    tgba_run* res = new tgba_run;
    for (tgba_run::steps::const_iterator i = run->prefix.begin();
	 i != run->prefix.end(); ++i)
      {
	tgba_run::step s = { a_run->project_state(i->s, a_proj),
			     i->label,
			     i->acc };
	res->prefix.push_back(s);
      }
    for (tgba_run::steps::const_iterator i = run->cycle.begin();
	 i != run->cycle.end(); ++i)
      {
	tgba_run::step s = { a_run->project_state(i->s, a_proj),
			     i->label,
			     i->acc };
	res->cycle.push_back(s);
      }
    return res;
  }
}
