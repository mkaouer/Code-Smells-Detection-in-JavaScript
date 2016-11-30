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

#ifndef SPOT_TGBAALGOS_RUNDOTDEC_HH
# define SPOT_TGBAALGOS_RUNDOTDEC_HH

#include <map>
#include <utility>
#include <list>
#include "dottydec.hh"
#include "emptiness.hh"

namespace spot
{
  /// \brief Highlight a spot::tgba_run on a spot::tgba.
  /// \ingroup tgba_dotty
  ///
  /// An instance of this class can be passed to spot::dotty_reachable.
  class tgba_run_dotty_decorator: public dotty_decorator
  {
  public:
    tgba_run_dotty_decorator(const tgba_run* run);
    virtual ~tgba_run_dotty_decorator();

    virtual std::string state_decl(const tgba* a, const state* s, int n,
				   tgba_succ_iterator* si,
				   const std::string& label);
    virtual std::string link_decl(const tgba* a,
				  const state* in_s, int in,
				  const state* out_s, int out,
				  const tgba_succ_iterator* si,
				  const std::string& label);
  private:
    const tgba_run* run_;
    typedef std::pair<tgba_run::steps::const_iterator, int> step_num;
    typedef std::list<step_num> step_set;
    typedef std::map<const state*, std::pair<step_set, step_set>,
		     spot::state_ptr_less_than> step_map;
    step_map map_;
  };
}

#endif // SPOT_TGBAALGOS_RUNDOTDEC_HH
