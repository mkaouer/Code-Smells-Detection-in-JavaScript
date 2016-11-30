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

#include <sstream>
#include "rundotdec.hh"
#include "tgba/succiter.hh"

namespace spot
{

  tgba_run_dotty_decorator::tgba_run_dotty_decorator(const tgba_run* run)
    : run_(run)
  {
    int n = 1;
    for (tgba_run::steps::const_iterator i = run->prefix.begin();
	 i != run->prefix.end(); ++i, ++n)
      map_[i->s].first.push_back(step_num(i, n));
    for (tgba_run::steps::const_iterator i = run->cycle.begin();
	 i != run->cycle.end(); ++i, ++n)
      map_[i->s].second.push_back(step_num(i, n));
  }

  tgba_run_dotty_decorator::~tgba_run_dotty_decorator()
  {
  }

  std::string
  tgba_run_dotty_decorator::state_decl(const tgba*, const state* s, int,
				       tgba_succ_iterator*,
				       const std::string& label)
  {
    step_map::const_iterator i = map_.find(s);
    if (i == map_.end())
      return "[label=\"" + label + "\"]";

    std::ostringstream os;
    std::string sep = "(";
    bool in_prefix = false;
    bool in_cycle = false;
    for (step_set::const_iterator j = i->second.first.begin();
	 j != i->second.first.end(); ++j)
      {
	os << sep << j->second;
	sep = ", ";
	in_prefix = true;
      }
    if (sep == ", ")
      sep = "; ";
    for (step_set::const_iterator j = i->second.second.begin();
	 j != i->second.second.end(); ++j)
      {
	os << sep << j->second;
	sep = ", ";
	in_cycle = true;
      }
    assert(in_cycle || in_prefix);
    os << ")\\n" << label;
    std::string color = in_prefix ? (in_cycle ? "violet" : "blue") : "red";
    return "[label=\"" + os.str() + "\", style=bold, color=" + color + "]";
  }

  std::string
  tgba_run_dotty_decorator::link_decl(const tgba*,
				      const state* in_s, int,
				      const state* out_s, int,
				      const tgba_succ_iterator* si,
				      const std::string& label)
  {
    step_map::const_iterator i = map_.find(in_s);
    if (i != map_.end())
      {
	std::ostringstream os;
	std::string sep = "(";
	bool in_prefix = false;
	bool in_cycle = false;
	for (step_set::const_iterator j = i->second.first.begin();
	     j != i->second.first.end(); ++j)
	  if (j->first->label == si->current_condition()
	      && j->first->acc == si->current_acceptance_conditions())
	    {
	      tgba_run::steps::const_iterator j2 = j->first;
	      ++j2;
	      if (j2 == run_->prefix.end())
		j2 = run_->cycle.begin();
	      if (out_s->compare(j2->s))
		continue;

	      os << sep << j->second;
	      sep = ", ";
	      in_prefix = true;
	    }
	if (sep == ", ")
	  sep = "; ";
	for (step_set::const_iterator j = i->second.second.begin();
	     j != i->second.second.end(); ++j)
	  if (j->first->label == si->current_condition()
	      && j->first->acc == si->current_acceptance_conditions())
	    {
	      tgba_run::steps::const_iterator j2 = j->first;
	      ++j2;
	      if (j2 == run_->cycle.end())
		j2 = run_->cycle.begin();
	      if (out_s->compare(j2->s))
		continue;

	      os << sep << j->second;
	      sep = ", ";
	      in_cycle = true;
	    }
	os << ")\\n";
	if (in_prefix || in_cycle)
	  {
	    std::string
	      color = in_prefix ? (in_cycle ? "violet" : "blue") : "red";
	    return ("[label=\"" + os.str() + label
		    + "\", style=bold, color=" + color + "]");

	  }
      }
    return "[label=\"" + label + "\"]";
  }



}
