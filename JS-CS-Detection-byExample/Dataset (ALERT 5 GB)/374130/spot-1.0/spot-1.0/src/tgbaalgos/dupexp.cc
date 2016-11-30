// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "dupexp.hh"
#include <sstream>
#include <string>
#include <map>
#include "reachiter.hh"

namespace spot
{
  namespace
  {
    template <class T>
    class dupexp_iter: public T
    {
    public:
      dupexp_iter(const tgba* a)
	: T(a), out_(new tgba_explicit_number(a->get_dict()))
      {
	out_->copy_acceptance_conditions_of(a);
      }

      tgba_explicit_number*
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
	out_->add_acceptance_conditions(t, si->current_acceptance_conditions());
      }

    private:
      tgba_explicit_number* out_;
    };

  } // anonymous

  tgba_explicit_number*
  tgba_dupexp_bfs(const tgba* aut)
  {
    dupexp_iter<tgba_reachable_iterator_breadth_first> di(aut);
    di.run();
    return di.result();
  }

  tgba_explicit_number*
  tgba_dupexp_dfs(const tgba* aut)
  {
    dupexp_iter<tgba_reachable_iterator_depth_first> di(aut);
    di.run();
    return di.result();
  }

}
