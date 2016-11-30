// Copyright (C) 2011, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE)
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include <ostream>
#include "save.hh"
#include "tgba/bddprint.hh"
#include "ltlvisit/tostring.hh"
#include "ltlast/atomic_prop.hh"
#include "reachiter.hh"
#include "misc/escape.hh"

namespace spot
{
  namespace
  {
    class save_bfs: public tgba_reachable_iterator_breadth_first
    {
    public:
      save_bfs(const tgba* a, std::ostream& os)
	: tgba_reachable_iterator_breadth_first(a), os_(os)
      {
      }

      void
      start()
      {
	os_ << "acc =";
	print_acc(aut_->all_acceptance_conditions()) << ";\n";
      }

      void
      process_state(const state* s, int, tgba_succ_iterator* si)
      {
	const bdd_dict* d = aut_->get_dict();
	std::string cur = escape_str(aut_->format_state(s));
	for (si->first(); !si->done(); si->next())
	  {
	    state* dest = si->current_state();
	    os_ << "\"" << cur << "\", \"";
	    escape_str(os_, aut_->format_state(dest));
	    os_ << "\", \"";
	    escape_str(os_, bdd_format_formula(d, si->current_condition()));
	    os_ << "\",";
	    print_acc(si->current_acceptance_conditions()) << ";\n";
	    dest->destroy();
	  }
      }

    private:
      std::ostream& os_;

      std::ostream&
      print_acc(bdd acc)
      {
	const bdd_dict* d = aut_->get_dict();
	while (acc != bddfalse)
	  {
	    bdd cube = bdd_satone(acc);
	    acc -= cube;
	    const ltl::formula* f = d->oneacc_to_formula(cube);
	    std::string s = ltl::to_string(f);
	    if (is_atomic_prop(f) && s[0] == '"')
	      {
		// Unquote atomic propositions.
		s.erase(s.begin());
		s.resize(s.size() - 1);
	      }
	    os_ << " \"";
	    escape_str(os_, s) << "\"";
	  }
	return os_;
      }
    };
  }

  std::ostream&
  tgba_save_reachable(std::ostream& os, const tgba* g)
  {
    save_bfs b(g, os);
    b.run();
    return os;
  }
}
