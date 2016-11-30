// Copyright (C) 2013 Laboratoire de Recherche et Développement de
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

#include <sstream>
#include <deque>
#include "public.hh"
#include "tgbaalgos/sccfilter.hh"
#include "ltlenv/defaultenv.hh"

namespace spot
{
  // Christof Löding's Diploma Thesis: Methods for the
  // Transformation of ω-Automata: Complexity and Connection to
  // Second Order Logic.  Section 3.4.3, gives a transition
  // from Streett with |Q| states to BA with |Q|*(4^n-3^n+2)
  // states, if n is the number of acceptance pairs.
  //
  // Duret-Lutz et al. (ATVA'2009): On-the-fly Emptiness Check of
  // Transition-based Streett Automata.  Section 3.3 contains a
  // conversion from transition-based Streett Automata to TGBA using
  // the generalized Büchi acceptance to limit the explosion.  It goes
  // from Streett with |Q| states to (T)GBA with |Q|*(2^n+1) states.
  // However the definition of the number of acceptance sets in that
  // paper is suboptimal: only n are needed, not 2^n.
  //
  // This implements this second version.

  namespace
  {
    // A state in the resulting automaton corresponds is either a
    // state of the original automaton (in which case bv == 0) or a
    // state of the original automaton associated to a set of pending
    // acceptance represented by a bitvect.

    struct build_state
    {
      int s;
      const bitvect* pend;

      build_state(int st, const bitvect* bv = 0):
	s(st),
	pend(bv)
      {
      }
    };

    typedef std::pair<const state*, build_state> degen_state;

    struct build_state_hash
    {
      size_t
      operator()(const build_state& s) const
      {
	if (!s.pend)
	  return s.s;
	else
	  return s.s ^ s.pend->hash();
      }
    };

    struct build_state_equal
    {
      bool
      operator()(const build_state& left,
                 const build_state& right) const
      {
	if (left.s != right.s)
	  return false;
	if (left.pend == right.pend)
	  return true;
	if (!right.pend || !left.pend)
	  return false;
        return *left.pend == *right.pend;
      }
    };

    // Associate the build state to its number.
    typedef Sgi::hash_map<build_state, int,
                          build_state_hash, build_state_equal> bs2num_map;

    // Queue of state to be processed.
    typedef std::deque<build_state> queue_t;

  }

  int label(const tgba_explicit_number* aut, state* s)
  {
    int label = aut->get_label(s);
    s->destroy();
    return label;
  }

  SPOT_API
  tgba* nsa_to_tgba(const dstar_aut* nsa)
  {
    assert(nsa->type == Streett);
    tgba_explicit_number* a = nsa->aut;
    bdd_dict* dict = a->get_dict();

    tgba_explicit_number* res = new tgba_explicit_number(dict);
    dict->register_all_variables_of(a, res);

    // Create accpair_count acceptance sets for tge TGBA.
    size_t npairs = nsa->accpair_count;
    std::vector<bdd> acc_b(npairs);
    {
      ltl::environment& envacc = ltl::default_environment::instance();
      std::vector<const ltl::formula*> acc_f(npairs);
      for (unsigned n = 0; n < npairs; ++n)
	{
	  std::ostringstream s;
	  s << n;
	  const ltl::formula* af = acc_f[n] = envacc.require(s.str());
	  res->declare_acceptance_condition(af->clone());
	}
      bdd allacc = bddfalse;
      for (unsigned n = 0; n < npairs; ++n)
	{
	  const ltl::formula* af = acc_f[n];
	  allacc |= acc_b[n] = res->get_acceptance_condition(af);
	}
    }

    // These maps make it possible to convert build_state to number
    // and vice-versa.
    bs2num_map bs2num;

    queue_t todo;

    build_state s(label(a, a->get_init_state()));

    bs2num[s] = 0;
    todo.push_back(s);

    while (!todo.empty())
      {
        s = todo.front();
        todo.pop_front();
        int src = bs2num[s];

        tgba_succ_iterator* i = a->succ_iter(a->get_state(s.s));
        for (i->first(); !i->done(); i->next())
	  {
	    int dlabel = label(a, i->current_state());

	    bitvect* pend = 0;
	    bdd acc = bddfalse;
	    if (s.pend)
	      {
		pend = s.pend->clone();
		*pend |= nsa->accsets->at(2 * dlabel); // L
		*pend -= nsa->accsets->at(2 * dlabel + 1); // U

		for (size_t i = 0; i < npairs; ++i)
		  if (!pend->get(i))
		    acc |= acc_b[i];
	      }


	    build_state d(dlabel, pend);
            // Have we already seen this destination?
            int dest;
	    std::pair<bs2num_map::iterator, bool> dres =
	      bs2num.insert(bs2num_map::value_type(d, 0));
            if (!dres.second)
              {
                dest = dres.first->second;
		delete d.pend;
              }
            else
              {
		dest = dres.first->second = bs2num.size() - 1;
                todo.push_back(d);
	      }
	    state_explicit_number::transition* t =
	      res->create_transition(src, dest);
	    t->condition = i->current_condition();
	    t->acceptance_conditions = acc;

	    // Jump to level ∅
	    if (s.pend == 0)
	      {
		bitvect* pend = make_bitvect(npairs);
		build_state d(label(a, i->current_state()), pend);
		// Have we already seen this destination?
		int dest;
		std::pair<bs2num_map::iterator, bool> dres =
		  bs2num.insert(bs2num_map::value_type(d, 0));
		if (!dres.second)
		  {
		    dest = dres.first->second;
		    delete d.pend;
		  }
		else
		  {
		    dest = dres.first->second = bs2num.size() - 1;
		    todo.push_back(d);
		  }
		state_explicit_number::transition* t =
		  res->create_transition(src, dest);
		t->condition = i->current_condition();
	      }
	  }
	delete i;
      }


    // {
    //   bs2num_map::iterator i = bs2num.begin();
    //   while (i != bs2num.end())
    // 	{
    // 	  std::cerr << i->second << ": (" << i->first.s << ",";
    // 	  if (i->first.pend)
    // 	    std::cerr << *i->first.pend << ")\n";
    // 	  else
    // 	    std::cerr << "-)\n";
    // 	  ++i;
    // 	}
    // }

    // Cleanup the bs2num map.
    bs2num_map::iterator i = bs2num.begin();
    while (i != bs2num.end())
      delete (i++)->first.pend;

    return res;
  }

}

