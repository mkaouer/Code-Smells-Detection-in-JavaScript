// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "lbtt.hh"
#include <map>
#include <string>
#include <ostream>
#include <sstream>
#include "tgba/bddprint.hh"
#include "reachiter.hh"
#include "misc/bddlt.hh"

namespace spot
{
  namespace
  {
    // At some point we'll need to print an acceptance set into LBTT's
    // format.  LBTT expects numbered acceptance sets, so first we'll
    // number each acceptance condition, and later when we have to print
    // them we'll just have to look up each of them.
    class acceptance_cond_splitter
    {
    public:
      acceptance_cond_splitter(bdd all_acc)
      {
	unsigned count = 0;
	while (all_acc != bddfalse)
	  {
	    bdd acc = bdd_satone(all_acc);
	    all_acc -= acc;
	    sm[acc] = count++;
	  }
      }

      std::ostream&
      split(std::ostream& os, bdd b)
      {
	while (b != bddfalse)
	  {
	    bdd acc = bdd_satone(b);
	    b -= acc;
	    os << sm[acc] << " ";
	  }
	return os;
      }

      unsigned
      count() const
      {
	return sm.size();
      }

    private:
      typedef std::map<bdd, unsigned, bdd_less_than> split_map;
      split_map sm;
    };

    // Convert a BDD formula to the syntax used by LBTT's transition guards.
    // Conjunctions are printed by bdd_format_sat, so we just have
    // to handle the other cases.
    static std::string
    bdd_to_lbtt(bdd b, const bdd_dict* d)
    {
      if (b == bddfalse)
	return "f";
      else if (b == bddtrue)
	return "t";
      else
	{
	  bdd cube = bdd_satone(b);
	  b -= cube;
	  if (b != bddfalse)
	    {
	      return "| " + bdd_to_lbtt(b, d) + " " + bdd_to_lbtt(cube, d);
	    }
	  else
	    {
	      std::string res = "";
	      for (int count = bdd_nodecount(cube); count > 1; --count)
		res += "& ";
	      return res + bdd_format_sat(d, cube);
	    }
	}

    }

    class lbtt_bfs : public tgba_reachable_iterator_breadth_first
    {
    public:
      lbtt_bfs(const tgba* a, std::ostream& os)
	: tgba_reachable_iterator_breadth_first(a),
	  os_(os),
	  acc_count_(a->number_of_acceptance_conditions()),
	  acs_(a->all_acceptance_conditions())
      {
      }

      void
      process_state(const state*, int n, tgba_succ_iterator*)
      {
	--n;
	if (n == 0)
	  body_ << "0 1" << std::endl;
	else
	  body_ << "-1" << std::endl << n << " 0" << std::endl;
      }

      void
      process_link(const state*, int,
		   const state*, int out, const tgba_succ_iterator* si)
      {
	body_ << out - 1 << " ";
	acs_.split(body_, si->current_acceptance_conditions());
	body_ << "-1 " << bdd_to_lbtt(si->current_condition(),
				      automata_->get_dict()) << std::endl;
      }

      void
      end()
      {
	os_ << seen.size() << " " << acc_count_ << "t" << std::endl
	    << body_.str() << "-1" << std::endl;
      }

    private:
      std::ostream& os_;
      std::ostringstream body_;
      unsigned acc_count_;
      acceptance_cond_splitter acs_;
    };

  } // anonymous

  std::ostream&
  lbtt_reachable(std::ostream& os, const tgba* g)
  {
    lbtt_bfs b(g, os);
    b.run();
    return os;
  }
}
