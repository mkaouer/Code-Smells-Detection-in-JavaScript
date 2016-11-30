// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include "stripacc.hh"
#include "reachiter.hh"

namespace spot
{
  namespace
  {
    class strip_iter: public tgba_reachable_iterator_depth_first
    {
    public:
      strip_iter(const tgba* a)
	: tgba_reachable_iterator_depth_first(a),
	  out_(new sba_explicit_number(a->get_dict()))
      {
      }

      sba_explicit_number*
      result()
      {
	return out_;
      }

      void
      process_link(const state*, int in,
		   const state*, int out,
		   const tgba_succ_iterator* si)
      {
	state_explicit_number::transition* t = out_->create_transition(in, out);
	out_->add_conditions(t, si->current_condition());
      }

    private:
      sba_explicit_number* out_;
    };
  }

  sba_explicit_number*
  strip_acceptance(const tgba* a)
  {
    strip_iter si(a);
    si.run();
    return si.result();
  }
}
