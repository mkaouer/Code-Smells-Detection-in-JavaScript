// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
// de l'Epita.
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

#include <iostream>
#include <fstream>
#include <sstream>
#include "dtgbasat.hh"
#include "reachiter.hh"
#include <map>
#include <utility>
#include "scc.hh"
#include "tgba/bddprint.hh"
#include "ltlast/constant.hh"
#include "stats.hh"
#include "ltlenv/defaultenv.hh"
#include "misc/satsolver.hh"
#include "misc/timer.hh"
#include "isweakscc.hh"
#include "dotty.hh"

// If you set the SPOT_TMPKEEP environment variable the temporary
// file used to communicate with the sat solver will be left in
// the current directory.
//
// Additionally, if the following DEBUG macro is set to 1, the CNF
// file will be output with a comment before each clause, and an
// additional output file (dtgba-sat.dbg) will be created with a list
// of all positive variables in the result and their meaning.

#define DEBUG 0
#if DEBUG
#define dout out << "c "
#define trace std::cerr
#else
#define dout while (0) std::cout
#define trace dout
#endif

namespace spot
{
  namespace
  {
    static bdd_dict* debug_dict = 0;

    struct transition
    {
      int src;
      bdd cond;
      int dst;

      transition(int src, bdd cond, int dst)
	: src(src), cond(cond), dst(dst)
      {
      }

      bool operator<(const transition& other) const
      {
	if (this->src < other.src)
	  return true;
	if (this->src > other.src)
	  return false;
	if (this->dst < other.dst)
	  return true;
	if (this->dst > other.dst)
	  return false;
	return this->cond.id() < other.cond.id();
      }

      bool operator==(const transition& other) const
      {
	return (this->src == other.src
		&& this->dst == other.dst
		&& this->cond.id() == other.cond.id());
      }
    };

    struct src_cond
    {
      int src;
      bdd cond;

      src_cond(int src, bdd cond)
	: src(src), cond(cond)
      {
      }

      bool operator<(const src_cond& other) const
      {
	if (this->src < other.src)
	  return true;
	if (this->src > other.src)
	  return false;
	return this->cond.id() < other.cond.id();
      }

      bool operator==(const src_cond& other) const
      {
	return (this->src == other.src
		&& this->cond.id() == other.cond.id());
      }
    };

    struct transition_acc
    {
      int src;
      bdd cond;
      bdd acc;
      int dst;

      transition_acc(int src, bdd cond, bdd acc, int dst)
	: src(src), cond(cond), acc(acc), dst(dst)
      {
      }

      bool operator<(const transition_acc& other) const
      {
	if (this->src < other.src)
	  return true;
	if (this->src > other.src)
	  return false;
	if (this->dst < other.dst)
	  return true;
	if (this->dst > other.dst)
	  return false;
	if (this->cond.id() < other.cond.id())
	  return true;
	if (this->cond.id() > other.cond.id())
	  return false;
	return this->acc.id() < other.acc.id();
      }

      bool operator==(const transition_acc& other) const
      {
	return (this->src == other.src
		&& this->dst == other.dst
		&& this->cond.id() == other.cond.id()
		&& this->acc.id() == other.acc.id());
      }
    };

    struct path
    {
      int src_cand;
      int src_ref;
      int dst_cand;
      int dst_ref;
      bdd acc_cand;
      bdd acc_ref;

      path(int src_cand, int src_ref)
	: src_cand(src_cand), src_ref(src_ref),
	  dst_cand(src_cand), dst_ref(src_ref),
	  acc_cand(bddfalse), acc_ref(bddfalse)
      {
      }

      path(int src_cand, int src_ref,
	   int dst_cand, int dst_ref,
	   bdd acc_cand, bdd acc_ref)
	: src_cand(src_cand), src_ref(src_ref),
	  dst_cand(dst_cand), dst_ref(dst_ref),
	  acc_cand(acc_cand), acc_ref(acc_ref)
      {
      }

      bool operator<(const path& other) const
      {
	if (this->src_cand < other.src_cand)
	  return true;
	if (this->src_cand > other.src_cand)
	  return false;
	if (this->src_ref < other.src_ref)
	  return true;
	if (this->src_ref > other.src_ref)
	  return false;
	if (this->dst_cand < other.dst_cand)
	  return true;
	if (this->dst_cand > other.dst_cand)
	  return false;
	if (this->dst_ref < other.dst_ref)
	  return true;
	if (this->dst_ref > other.dst_ref)
	  return false;
	if (this->acc_ref.id() < other.acc_ref.id())
	  return true;
	if (this->acc_ref.id() > other.acc_ref.id())
	  return false;
	if (this->acc_cand.id() < other.acc_cand.id())
	  return true;
	if (this->acc_cand.id() > other.acc_cand.id())
	  return false;

	return false;
      }

    };

    std::ostream& operator<<(std::ostream& os, const transition& t)
    {
      os << "<" << t.src << ","
	 << bdd_format_formula(debug_dict, t.cond)
	 << "," << t.dst << ">";
      return os;
    }


    std::ostream& operator<<(std::ostream& os, const transition_acc& t)
    {
      os << "<" << t.src << ","
	 << bdd_format_formula(debug_dict, t.cond) << ","
	 << bdd_format_accset(debug_dict, t.acc)
	 << "," << t.dst << ">";
      return os;
    }

    std::ostream& operator<<(std::ostream& os, const path& p)
    {
      os << "<"
	 << p.src_cand << ","
	 << p.src_ref << ","
	 << p.dst_cand << ","
	 << p.dst_ref << ", "
	 << bdd_format_accset(debug_dict, p.acc_cand) << ", "
	 << bdd_format_accset(debug_dict, p.acc_ref) << ">";
      return os;
    }

    struct dict
    {
      dict(const tgba* a)
	: aut(a)
      {
      }

      const tgba* aut;
      typedef std::map<transition, int> trans_map;
      typedef std::map<transition_acc, int> trans_acc_map;
      trans_map transid;
      trans_acc_map transaccid;
      typedef std::map<int, transition> rev_map;
      typedef std::map<int, transition_acc> rev_acc_map;
      rev_map revtransid;
      rev_acc_map revtransaccid;

      std::map<path, int> pathid;
      int nvars;
      typedef Sgi::hash_map<const state*, int,
			    state_ptr_hash, state_ptr_equal> state_map;
      typedef Sgi::hash_map<int, const state*> int_map;
      state_map state_to_int;
      int_map int_to_state;
      int cand_size;
      unsigned int cand_nacc;
      std::vector<bdd> cand_acc; // size cand_nacc

      std::vector<bdd> all_cand_acc;
      std::vector<bdd> all_ref_acc;

      bdd cand_all_acc;
      bdd ref_all_acc;

      ~dict()
      {
	state_map::const_iterator s = state_to_int.begin();
	while (s != state_to_int.end())
	  // Always advance the iterator before deleting the key.
	  s++->first->destroy();

	aut->get_dict()->unregister_all_my_variables(this);
      }
    };


    class filler_dfs: public tgba_reachable_iterator_depth_first
    {
    protected:
      dict& d;
      int size_;
      bdd ap_;
      bool state_based_;
      scc_map& sm_;
    public:
      filler_dfs(const tgba* aut, dict& d, bdd ap, bool state_based,
		 scc_map& sm)
	: tgba_reachable_iterator_depth_first(aut), d(d), ap_(ap),
	  state_based_(state_based), sm_(sm)
      {
	d.nvars = 0;

	bdd_dict* bd = aut->get_dict();
	ltl::default_environment& env = ltl::default_environment::instance();

	d.cand_acc.resize(d.cand_nacc);
	d.all_cand_acc.push_back(bddfalse);

	bdd allneg = bddtrue;
	for (unsigned n = 0; n < d.cand_nacc; ++n)
	  {
	    std::ostringstream s;
	    s << n;
	    const ltl::formula* af = env.require(s.str());
	    int v = bd->register_acceptance_variable(af, &d);
	    af->destroy();
	    d.cand_acc[n] = bdd_ithvar(v);
	    allneg &= bdd_nithvar(v);
	  }
	for (unsigned n = 0; n < d.cand_nacc; ++n)
	  {
	    bdd c = bdd_exist(allneg, d.cand_acc[n]) & d.cand_acc[n];
	    d.cand_acc[n] = c;

	    size_t s = d.all_cand_acc.size();
	    for (size_t i = 0; i < s; ++i)
	      d.all_cand_acc.push_back(d.all_cand_acc[i] | c);
	  }
	d.cand_all_acc = bdd_support(allneg);
	d.ref_all_acc = bdd_support(aut->all_acceptance_conditions());

	bdd refall = d.ref_all_acc;
	bdd refnegall = aut->neg_acceptance_conditions();

	d.all_ref_acc.push_back(bddfalse);
	while (refall != bddtrue)
	  {
	    bdd v = bdd_ithvar(bdd_var(refall));
	    bdd c = bdd_exist(refnegall, v) & v;

	    size_t s = d.all_ref_acc.size();
	    for (size_t i = 0; i < s; ++i)
	      d.all_ref_acc.push_back(d.all_ref_acc[i] | c);

	    refall = bdd_high(refall);
	  }
      }

      int size()
      {
	return size_;
      }

      void end()
      {
	size_ = seen.size();

	if (d.cand_size == -1)
	  d.cand_size = size_ - 1;

	for (dict::state_map::const_iterator i2 = seen.begin();
	     i2 != seen.end(); ++i2)
	  d.int_to_state[i2->second] = i2->first;

	for (int i = 1; i <= size_; ++i)
	  {
	    unsigned i_scc = sm_.scc_of_state(d.int_to_state[i]);
	    bool is_weak = is_weak_scc(sm_, i_scc);

	    for (int j = 1; j <= d.cand_size; ++j)
	      {
		for (int k = 1; k <= size_; ++k)
		  {
		    if (sm_.scc_of_state(d.int_to_state[k]) != i_scc)
		      continue;
		    for (int l = 1; l <= d.cand_size; ++l)
		      {
			size_t sfp = is_weak ? 1 : d.all_ref_acc.size();
			for (size_t fp = 0; fp < sfp; ++fp)
			  {
			    size_t sf = d.all_cand_acc.size();
			    for (size_t f = 0; f < sf; ++f)
			      {
				path p(j, i, l, k,
				       d.all_cand_acc[f],
				       d.all_ref_acc[fp]);
				d.pathid[p] = ++d.nvars;
			      }

			  }
		      }
		  }
	      }
	  }

	std::swap(d.state_to_int, seen);

	if (!state_based_)
	  {
	    for (int i = 1; i <= d.cand_size; ++i)
	      for (int j = 1; j <= d.cand_size; ++j)
		{
		  bdd all = bddtrue;
		  while (all != bddfalse)
		    {
		      bdd one = bdd_satoneset(all, ap_, bddfalse);
		      all -= one;

		      transition t(i, one, j);
		      d.transid[t] = ++d.nvars;
		      d.revtransid.insert(dict::rev_map::
					  value_type(d.nvars, t));

		      // Create the variable for the accepting transition
		      // immediately afterwards.  It helps parsing the
		      // result.
		      for (unsigned n = 0; n < d.cand_nacc; ++n)
			{
			  transition_acc ta(i, one, d.cand_acc[n], j);
			  d.transaccid[ta] = ++d.nvars;
			  d.revtransaccid.insert(dict::rev_acc_map::
						 value_type(d.nvars, ta));
			}
		    }
		}
	  }
	else // state based
	  {
	    for (int i = 1; i <= d.cand_size; ++i)
	      for (unsigned n = 0; n < d.cand_nacc; ++n)
		{
		  ++d.nvars;
		  for (int j = 1; j <= d.cand_size; ++j)
		    {
		      bdd all = bddtrue;
		      while (all != bddfalse)
			{
			  bdd one = bdd_satoneset(all, ap_, bddfalse);
			  all -= one;

			  transition_acc ta(i, one, d.cand_acc[n], j);
			  d.transaccid[ta] = d.nvars;
			  d.revtransaccid.insert(dict::rev_acc_map::
						 value_type(d.nvars, ta));
			}
		    }
		}
	    for (int i = 1; i <= d.cand_size; ++i)
	      for (int j = 1; j <= d.cand_size; ++j)
		{
		  bdd all = bddtrue;
		  while (all != bddfalse)
		    {
		      bdd one = bdd_satoneset(all, ap_, bddfalse);
		      all -= one;

		      transition t(i, one, j);
		      d.transid[t] = ++d.nvars;
		      d.revtransid.insert(dict::rev_map::
					  value_type(d.nvars, t));
		    }
		}
	  }
      }
    };

    typedef std::pair<int, int> sat_stats;

    static
    sat_stats dtgba_to_sat(std::ostream& out, const tgba* ref,
			   dict& d, bool state_based)
    {
      clause_counter nclauses;
      int ref_size = 0;

      scc_map sm(ref);
      sm.build_map();
      bdd ap = sm.aprec_set_of(sm.initial());

      // Count the number of atomic propositions
      int nap = 0;
      {
	bdd cur = ap;
	while (cur != bddtrue)
	  {
	    ++nap;
	    cur = bdd_high(cur);
	  }
	nap = 1 << nap;
      }

      // Number all the SAT variable we may need.
      {
	filler_dfs f(ref, d, ap, state_based, sm);
	f.run();
	ref_size = f.size();
      }

      // empty automaton is impossible
      if (d.cand_size == 0)
	{
	  out << "p cnf 1 2\n-1 0\n1 0\n";
	  return std::make_pair(1, 2);
	}

      // An empty line for the header
      out << "                                                 \n";

#if DEBUG
      debug_dict = ref->get_dict();
      dout << "ref_size: " << ref_size << "\n";
      dout << "cand_size: " << d.cand_size << "\n";
#endif

      dout << "symmetry-breaking clauses\n";
      int j = 0;
      bdd all = bddtrue;
      while (all != bddfalse)
 	{
 	  bdd s = bdd_satoneset(all, ap, bddfalse);
 	  all -= s;
 	  for (int i = 1; i < d.cand_size; ++i)
 	    for (int k = (i - 1) * nap + j + 3; k <= d.cand_size; ++k)
	      {
		transition t(i, s, k);
		int ti = d.transid[t];
		dout << "¬" << t << "\n";
		out << -ti << " 0\n";
		++nclauses;
	      }
 	  ++j;
 	}
      if (!nclauses.nb_clauses())
 	dout << "(none)\n";

      dout << "(8) the candidate automaton is complete\n";
      for (int q1 = 1; q1 <= d.cand_size; ++q1)
	{
	  bdd all = bddtrue;
	  while (all != bddfalse)
	    {
	      bdd s = bdd_satoneset(all, ap, bddfalse);
	      all -= s;

#if DEBUG
	      dout;
	      for (int q2 = 1; q2 <= d.cand_size; q2++)
		{
		  transition t(q1, s, q2);
		  out << t << "δ";
		  if (q2 != d.cand_size)
		    out << " ∨ ";
		}
	      out << "\n";
#endif

	      for (int q2 = 1; q2 <= d.cand_size; q2++)
		{
		  transition t(q1, s, q2);
		  int ti = d.transid[t];

		  out << ti << " ";
		}
	      out << "0\n";
	      ++nclauses;
	    }
	}

      dout << "(9) the initial state is reachable\n";
      dout << path(1, 1) << "\n";
      out << d.pathid[path(1, 1)] << " 0\n";
      ++nclauses;

      for (int q1 = 1; q1 <= d.cand_size; ++q1)
	for (int q1p = 1; q1p <= ref_size; ++q1p)
	{
	  dout << "(10) augmenting paths based on Cand[" << q1
	       << "] and Ref[" << q1p << "]\n";
	  path p1(q1, q1p);
	  int p1id = d.pathid[p1];

	  tgba_succ_iterator* it = ref->succ_iter(d.int_to_state[q1p]);
	  for (it->first(); !it->done(); it->next())
	    {
	      const state* dps = it->current_state();
	      int dp = d.state_to_int[dps];
	      dps->destroy();

	      bdd all = it->current_condition();
	      while (all != bddfalse)
		{
		  bdd s = bdd_satoneset(all, ap, bddfalse);
		  all -= s;

		  for (int q2 = 1; q2 <= d.cand_size; q2++)
		    {
		      transition t(q1, s, q2);
		      int ti = d.transid[t];

		      path p2(q2, dp);
		      int succ = d.pathid[p2];

		      if (p1id == succ)
			continue;

		      dout << p1 << " ∧ " << t << "δ → " << p2 << "\n";
		      out << -p1id << " " << -ti << " " << succ << " 0\n";
		      ++nclauses;
		    }
		}
	    }
	  delete it;
	}

      bdd all_acc = ref->all_acceptance_conditions();

      // construction of constraints (11,12,13)
      for (int q1p = 1; q1p <= ref_size; ++q1p)
	{
	  unsigned q1p_scc = sm.scc_of_state(d.int_to_state[q1p]);
	  for (int q2p = 1; q2p <= ref_size; ++q2p)
	    {
	      // We are only interested in transition that can form a
	      // cycle, so they must belong to the same SCC.
	      if (sm.scc_of_state(d.int_to_state[q2p]) != q1p_scc)
		continue;
	      bool is_weak = is_weak_scc(sm, q1p_scc);
	      bool is_acc = sm.accepting(q1p_scc);

	      for (int q1 = 1; q1 <= d.cand_size; ++q1)
		for (int q2 = 1; q2 <= d.cand_size; ++q2)
		  {
		    size_t sf = d.all_cand_acc.size();
		    size_t sfp = is_weak ? 1 : d.all_ref_acc.size();
		    for (size_t f = 0; f < sf; ++f)
		      for (size_t fp = 0; fp < sfp; ++fp)
			{
			  path p(q1, q1p, q2, q2p,
				 d.all_cand_acc[f], d.all_ref_acc[fp]);

			  dout << "(11&12&13) paths from " << p << "\n";

			  int pid = d.pathid[p];

			  tgba_succ_iterator* it =
			    ref->succ_iter(d.int_to_state[q2p]);

			  for (it->first(); !it->done(); it->next())
			    {
			      const state* dps = it->current_state();
			      // Skip destinations not in the SCC.
			      if (sm.scc_of_state(dps) != q1p_scc)
				{
				  dps->destroy();
				  continue;
				}
			      int dp = d.state_to_int[dps];
			      dps->destroy();

			      for (int q3 = 1; q3 <= d.cand_size; ++q3)
				{
				  bdd all = it->current_condition();
				  bdd curacc =
				    it->current_acceptance_conditions();

				  while (all != bddfalse)
				    {
				      bdd l = bdd_satoneset(all, ap, bddfalse);
				      all -= l;

				      transition t(q2, l, q3);
				      int ti = d.transid[t];

				      if (dp == q1p && q3 == q1) // (11,12) loop
					{
					  if ((!is_acc) ||
					      (!is_weak &&
					       (curacc |
						d.all_ref_acc[fp]) != all_acc))
					    {
#if DEBUG
					      dout << "(11) " << p << " ∧ "
						   << t << "δ → ¬(";

					      bdd all_ = d.all_cand_acc.back();
					      all_ -= d.all_cand_acc[f];
					      bool notfirst = false;
					      while (all_ != bddfalse)
						{
						  bdd one = bdd_satone(all_);
						  all_ -= one;

						  transition_acc ta(q2, l,
								    one, q1);
						  if (notfirst)
						    out << " ∧ ";
						  else
						    notfirst = true;
						  out << ta << "FC";
						}
					      out << ")\n";
#endif // DEBUG
					      out << -pid << " " << -ti;

					      // 11
					      bdd all_f = d.all_cand_acc.back();
					      all_f -= d.all_cand_acc[f];
					      while (all_f != bddfalse)
						{
						  bdd one = bdd_satone(all_f);
						  all_f -= one;

						  transition_acc ta(q2, l,
								    one, q1);
						  int tai = d.transaccid[ta];
						  assert(tai != 0);
						  out << " " << -tai;
						}
					      out << " 0\n";
					      ++nclauses;
					    }
					  else
					    {
#if DEBUG
					      dout << "(12) " << p << " ∧ "
						   << t << "δ → (";

					      bdd all_ = d.all_cand_acc.back();
					      all_ -= d.all_cand_acc[f];
					      bool notfirst = false;
					      while (all_ != bddfalse)
						{
						  bdd one = bdd_satone(all_);
						  all_ -= one;

						  transition_acc ta(q2, l,
								    one, q1);
						  if (notfirst)
						    out << " ∧ ";
						  else
						    notfirst = true;
						  out << ta << "FC";
						}
					      out << ")\n";
#endif // DEBUG
					      // 12
					      bdd all_f = d.all_cand_acc.back();
					      all_f -= d.all_cand_acc[f];
					      while (all_f != bddfalse)
						{
						  bdd one = bdd_satone(all_f);
						  all_f -= one;

						  transition_acc ta(q2, l,
								    one, q1);
						  int tai = d.transaccid[ta];
						  assert(tai != 0);

						  out << -pid << " " << -ti
						      << " " << tai << " 0\n";
						  ++nclauses;
						}
					    }
					}
				      // (13) augmenting paths (always).
				      {
					size_t sf = d.all_cand_acc.size();
					for (size_t f = 0; f < sf; ++f)
					  {

					    bdd f2 = p.acc_cand |
					      d.all_cand_acc[f];
					    bdd f2p = is_weak ? bddfalse
					      : p.acc_ref | curacc;

					    path p2(p.src_cand, p.src_ref,
						    q3, dp, f2, f2p);
					    int p2id = d.pathid[p2];
					    if (pid == p2id)
					      continue;
#if DEBUG
					    dout << "(13) " << p << " ∧ "
						 << t << "δ ";

					    bdd biga_ = d.all_cand_acc[f];
					    while (biga_ != bddfalse)
					      {
						bdd a = bdd_satone(biga_);
						biga_ -= a;

						transition_acc ta(q2, l, a, q3);
						out <<  " ∧ " << ta << "FC";
					      }
					    biga_ = d.all_cand_acc.back()
					      - d.all_cand_acc[f];
					    while (biga_ != bddfalse)
					      {
						bdd a = bdd_satone(biga_);
						biga_ -= a;

						transition_acc ta(q2, l, a, q3);
						out << " ∧ ¬" << ta << "FC";
					      }
					    out << " → " << p2 << "\n";
#endif
					    out << -pid << " " << -ti << " ";
					    bdd biga = d.all_cand_acc[f];
					    while (biga != bddfalse)
					      {
						bdd a = bdd_satone(biga);
						biga -= a;

						transition_acc ta(q2, l, a, q3);
						int tai = d.transaccid[ta];
						out << -tai << " ";
					      }
					    biga = d.all_cand_acc.back()
					      - d.all_cand_acc[f];
					    while (biga != bddfalse)
					      {
						bdd a = bdd_satone(biga);
						biga -= a;

						transition_acc ta(q2, l, a, q3);
						int tai = d.transaccid[ta];
						out << tai << " ";
					      }

					    out << p2id << " 0\n";
					    ++nclauses;
					  }
				      }
				    }
				}
			    }
			  delete it;
			}
		  }
	    }
	}
      out.seekp(0);
      out << "p cnf " << d.nvars << " " << nclauses.nb_clauses();
      return std::make_pair(d.nvars, nclauses.nb_clauses());
    }

    static tgba_explicit_number*
    sat_build(const satsolver::solution& solution, dict& satdict,
	      const tgba* aut, bool state_based)
    {
      bdd_dict* autdict = aut->get_dict();
      tgba_explicit_number* a = new tgba_explicit_number(autdict);
      autdict->register_all_variables_of(aut, a);
      autdict->unregister_all_typed_variables(bdd_dict::acc, aut);
      a->set_acceptance_conditions(satdict.all_cand_acc.back());

      for (int s = 1; s < satdict.cand_size; ++s)
	a->add_state(s);

      state_explicit_number::transition* last_aut_trans = 0;
      const transition* last_sat_trans = 0;

#if DEBUG
      std::fstream out("dtgba-sat.dbg",
		       std::ios_base::trunc | std::ios_base::out);
      out.exceptions(std::ifstream::failbit | std::ifstream::badbit);
      std::set<int> positive;
#endif

      dout << "--- transition variables ---\n";
      std::map<int, bdd> state_acc;
      std::set<src_cond> seen_trans;
      for (satsolver::solution::const_iterator i = solution.begin();
	   i != solution.end(); ++i)
	{
	  int v = *i;

	  if (v < 0)  // FIXME: maybe we can have (v < NNN)?
	    continue;

#if DEBUG
	  positive.insert(v);
#endif

	  dict::rev_map::const_iterator t = satdict.revtransid.find(v);

	  if (t != satdict.revtransid.end())
	    {
	      // Skip (s,l,d2) if we have already seen some (s,l,d1).
	      if (seen_trans.insert(src_cond(t->second.src,
					     t->second.cond)).second)
		{
		  last_aut_trans = a->create_transition(t->second.src,
							t->second.dst);
		  last_aut_trans->condition = t->second.cond;
		  last_sat_trans = &t->second;

		  dout << v << "\t" << t->second << "δ\n";

		  if (state_based)
		    {
		      std::map<int, bdd>::const_iterator i =
			state_acc.find(t->second.src);
		      if (i != state_acc.end())
			last_aut_trans->acceptance_conditions = i->second;
		    }
		}
	    }
	  else
	    {
	      dict::rev_acc_map::const_iterator ta;
	      ta = satdict.revtransaccid.find(v);
	      // This assumes that the sat solvers output variables in
	      // increasing order.
	      if (ta != satdict.revtransaccid.end())
		{
		  dout << v << "\t" << ta->second << "F\n";

		  if (last_sat_trans &&
		      ta->second.src == last_sat_trans->src &&
		      ta->second.cond == last_sat_trans->cond &&
		      ta->second.dst == last_sat_trans->dst)
		    {
		      assert(!state_based);
		      last_aut_trans->acceptance_conditions |= ta->second.acc;
		    }
		  else if (state_based)
		    {
		      state_acc[ta->second.src] |= ta->second.acc;
		    }
		}
	    }
	}
#if DEBUG
      dout << "--- pathid variables ---\n";
      for (std::map<path, int>::const_iterator pit =
	     satdict.pathid.begin();
	   pit != satdict.pathid.end(); ++pit)
	if (positive.find(pit->second) != positive.end())
	  dout << pit->second << "\t" << pit->first << "C\n";
#endif

      a->merge_transitions();

      return a;
    }
  }

  tgba_explicit_number*
  dtgba_sat_synthetize(const tgba* a, unsigned target_acc_number,
		       int target_state_number, bool state_based)
  {
    if (target_state_number == 0)
      return 0;
    trace << "dtgba_sat_synthetize(..., acc = " << target_acc_number
	  << ", states = " << target_state_number
	  << ", state_based = " << state_based << ")\n";

    dict d(a);
    d.cand_size = target_state_number;
    d.cand_nacc = target_acc_number;

    satsolver solver;
    satsolver::solution_pair solution;

    timer_map t;
    t.start("encode");
    sat_stats s = dtgba_to_sat(solver(), a, d, state_based);
    t.stop("encode");
    t.start("solve");
    solution = solver.get_solution();
    t.stop("solve");

    tgba_explicit_number* res = 0;
    if (!solution.second.empty())
      res = sat_build(solution.second, d, a, state_based);

    static const char* log = getenv("SPOT_SATLOG");
    if (log)
      {
	std::fstream out(log,
			 std::ios_base::app | std::ios_base::out);
	out.exceptions(std::ifstream::failbit | std::ifstream::badbit);
	const timer& te = t.timer("encode");
	const timer& ts = t.timer("solve");
	out << target_state_number << ',';
	if (res)
	  {
	    tgba_sub_statistics st = sub_stats_reachable(res);
	    out << st.states << ',' << st.transitions
		<< ',' << st.sub_transitions;
	  }
	else
	  {
	    out << ",,";
	  }
	out << ','
	    << s.first << ',' << s.second << ','
	    << te.utime() << ',' << te.stime() << ','
	    << ts.utime() << ',' << ts.stime() << '\n';
      }
    static const char* show = getenv("SPOT_SATSHOW");
    if (show && res)
      dotty_reachable(std::cout, res);

    trace << "dtgba_sat_synthetize(...) = " << res << "\n";
    return res;
  }

  tgba_explicit_number*
  dtgba_sat_minimize(const tgba* a, unsigned target_acc_number,
		     bool state_based)
  {
    int n_states = stats_reachable(a).states;

    tgba_explicit_number* prev = 0;
    for (;;)
      {
	tgba_explicit_number* next =
	  dtgba_sat_synthetize(prev ? prev : a, target_acc_number,
			       --n_states, state_based);
	if (next == 0)
	  break;
	else
	  n_states = stats_reachable(next).states;
	delete prev;
	prev = next;
      }
    return prev;
  }

  tgba_explicit_number*
  dtgba_sat_minimize_dichotomy(const tgba* a, unsigned target_acc_number,
			       bool state_based)
  {
    int max_states = stats_reachable(a).states - 1;
    int min_states = 1;

    tgba_explicit_number* prev = 0;
    while (min_states <= max_states)
      {
	int target = (max_states + min_states) / 2;
	tgba_explicit_number* next =
	  dtgba_sat_synthetize(prev ? prev : a, target_acc_number, target,
			       state_based);
	if (next == 0)
	  {
	    min_states = target + 1;
	  }
	else
	  {
	    delete prev;
	    prev = next;
	    max_states = stats_reachable(next).states - 1;
	  }
      }
    return prev;
  }

}
