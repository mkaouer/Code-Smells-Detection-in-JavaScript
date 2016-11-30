// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "lbtt.hh"
#include <map>
#include <string>
#include <ostream>
#include <sstream>
#include "tgba/bddprint.hh"
#include "tgba/tgbaexplicit.hh"
#include "reachiter.hh"
#include "misc/bddlt.hh"
#include "ltlparse/public.hh"

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
				      aut_->get_dict()) << std::endl;
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

  const tgba*
  lbtt_read_tgba(unsigned num_states, unsigned num_acc,
		 std::istream& is, std::string& error,
		 bdd_dict* dict,
		 ltl::environment& env, ltl::environment& envacc)
  {
    tgba_explicit_number* aut = new tgba_explicit_number(dict);
    std::vector<const ltl::formula*> acc_f(num_acc);
    for (unsigned n = 0; n < num_acc; ++n)
      {
	std::ostringstream s;
	s << n;
	const ltl::formula* af = acc_f[n] = envacc.require(s.str());
	aut->declare_acceptance_condition(af->clone());
      }
    std::map<int, bdd> acc_b;

    for (unsigned n = 0; n < num_states; ++n)
      {
	int src_state = 0;
	int initial = 0;
	is >> src_state >> initial;
	if (initial)
	  aut->set_init_state(src_state);

	// Read the transitions.
	for (;;)
	  {
	    int dst_state = 0;
	    is >> dst_state;
	    if (dst_state == -1)
	      break;

	    // Read the acceptance conditions.
	    bdd acc = bddfalse;
	    for (;;)
	      {
		int acc_n = 0;
		is >> acc_n;
		if (acc_n == -1)
		  break;
		std::map<int, bdd>::const_iterator i = acc_b.find(acc_n);
		if (i != acc_b.end())
		  {
		    acc |= i->second;
		  }
		else
		  {
		    size_t s = acc_b.size();
		    if (s >= num_acc)
		      {
			error += "more acceptance sets used than declared";
			goto fail;
		      }
		    bdd a = aut->get_acceptance_condition(acc_f[s]);
		    acc_b[acc_n] = a;
		    acc |= a;
		  }
	      }

	    std::string guard;
	    std::getline(is, guard);
	    ltl::parse_error_list pel;
	    const ltl::formula* f = parse_lbt(guard, pel, env);
	    if (!f || pel.size() > 0)
	      {
		error += "failed to parse guard: " + guard;
		if (f)
		  f->destroy();
		goto fail;
	      }
	    state_explicit_number::transition* t
	      = aut->create_transition(src_state, dst_state);
	    aut->add_condition(t, f);
	    t->acceptance_conditions |= acc;
	  }
      }
    return aut;
  fail:
    delete aut;
    return 0;
  }

  const tgba*
  lbtt_read_gba(unsigned num_states, unsigned num_acc,
		std::istream& is, std::string& error,
		bdd_dict* dict,
		ltl::environment& env, ltl::environment& envacc)
  {
    tgba_explicit_number* aut = new tgba_explicit_number(dict);
    std::vector<const ltl::formula*> acc_f(num_acc);
    for (unsigned n = 0; n < num_acc; ++n)
      {
	std::ostringstream s;
	s << n;
	const ltl::formula* af = acc_f[n] = envacc.require(s.str());
	aut->declare_acceptance_condition(af->clone());
      }
    std::map<int, bdd> acc_b;

    for (unsigned n = 0; n < num_states; ++n)
      {
	int src_state = 0;
	int initial = 0;
	is >> src_state >> initial;
	if (initial)
	  aut->set_init_state(src_state);

	// Read the acceptance conditions.
	bdd acc = bddfalse;
	for (;;)
	  {
	    int acc_n = 0;
	    is >> acc_n;
	    if (acc_n == -1)
	      break;
	    std::map<int, bdd>::const_iterator i = acc_b.find(acc_n);
	    if (i != acc_b.end())
	      {
		acc |= i->second;
	      }
	    else
	      {
		size_t s = acc_b.size();
		if (s >= num_acc)
		  {
		    error += "more acceptance sets used than declared";
		    goto fail;
		  }
		bdd a = aut->get_acceptance_condition(acc_f[s]);
		acc_b[acc_n] = a;
		acc |= a;
	      }
	  }

	// Read the transitions.
	for (;;)
	  {
	    int dst_state = 0;
	    is >> dst_state;
	    if (dst_state == -1)
	      break;

	    std::string guard;
	    std::getline(is, guard);
	    ltl::parse_error_list pel;
	    const ltl::formula* f = parse_lbt(guard, pel, env);
	    if (!f || pel.size() > 0)
	      {
		error += "failed to parse guard: " + guard;
		if (f)
		  f->destroy();
		goto fail;
	      }
	    state_explicit_number::transition* t
	      = aut->create_transition(src_state, dst_state);
	    aut->add_condition(t, f);
	    t->acceptance_conditions |= acc;
	  }
      }
    return aut;
  fail:
    delete aut;
    return 0;
  }

  const tgba*
  lbtt_parse(std::istream& is, std::string& error, bdd_dict* dict,
	     ltl::environment& env, ltl::environment& envacc)
  {
    is >> std::skipws;

    unsigned num_states = 0;
    is >> num_states;
    if (!is)
      {
	error += "failed to read the number of states";
	return 0;
      }

    // No states?  Read the rest of the line and return an empty automaton.
    if (num_states == 0)
      {
	std::string header;
	std::getline(is, header);
	return new sba_explicit_number(dict);
      }

    unsigned num_acc = 0;
    is >> num_acc;

    int type;
    type = is.peek();
    if (type == 't' || type == 'T' || type == 's' || type == 'S')
      type = is.get();

    if (type == 't' || type == 'T')
      return lbtt_read_tgba(num_states, num_acc, is, error, dict,
			    env, envacc);
    else
      return lbtt_read_gba(num_states, num_acc, is, error, dict,
			   env, envacc);
    return 0;
  }
}
