// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "emptiness.hh"
#include "tgba/tgba.hh"
#include "tgba/bddprint.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/gv04.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/se05.hh"
#include "tgbaalgos/tau03.hh"
#include "tgbaalgos/tau03opt.hh"

namespace spot
{

  // tgba_run
  //////////////////////////////////////////////////////////////////////

  tgba_run::~tgba_run()
  {
    for (steps::const_iterator i = prefix.begin(); i != prefix.end(); ++i)
      delete i->s;
    for (steps::const_iterator i = cycle.begin(); i != cycle.end(); ++i)
      delete i->s;
  }

  tgba_run::tgba_run(const tgba_run& run)
  {
    for (steps::const_iterator i = run.prefix.begin();
	 i != run.prefix.end(); ++i)
      {
	step s = { s.s->clone(), i->label, i->acc };
	prefix.push_back(s);
      }
    for (steps::const_iterator i = run.cycle.begin();
	 i != run.cycle.end(); ++i)
      {
	step s = { s.s->clone(), i->label, i->acc };
	cycle.push_back(s);
      }
  }

  tgba_run&
  tgba_run::operator=(const tgba_run& run)
  {
    if (&run != this)
      {
	this->~tgba_run();
	new(this) tgba_run(run);
      }
    return *this;
  }

  // print_tgba_run
  //////////////////////////////////////////////////////////////////////

  std::ostream&
  print_tgba_run(std::ostream& os,
		 const tgba* a,
		 const tgba_run* run)
  {
    bdd_dict* d = a->get_dict();
    os << "Prefix:" << std::endl;
    for (tgba_run::steps::const_iterator i = run->prefix.begin();
	 i != run->prefix.end(); ++i)
      {
	os << "  " << a->format_state(i->s) << std::endl;
	os << "  |  ";
	bdd_print_formula(os, d, i->label);
	os << "\t";
	bdd_print_accset(os, d, i->acc);
	os << std::endl;
      }
    os << "Cycle:" << std::endl;
    for (tgba_run::steps::const_iterator i = run->cycle.begin();
	 i != run->cycle.end(); ++i)
      {
	os << "  " << a->format_state(i->s) << std::endl;
	os << "  |  ";
	bdd_print_formula(os, d, i->label);
	os << "\t";
	bdd_print_accset(os, d, i->acc);
	os << std::endl;
      }
    return os;
  }

  // emptiness_check_result
  //////////////////////////////////////////////////////////////////////

  tgba_run*
  emptiness_check_result::accepting_run()
  {
    return 0;
  }

  const unsigned_statistics*
  emptiness_check_result::statistics() const
  {
    return dynamic_cast<const unsigned_statistics*>(this);
  }

  const char*
  emptiness_check_result::parse_options(char* options)
  {
    option_map old(o_);
    const char* s = o_.parse_options(options);
    options_updated(old);
    return s;
  }

  void
  emptiness_check_result::options_updated(const option_map&)
  {
  }


  // emptiness_check
  //////////////////////////////////////////////////////////////////////

  emptiness_check::~emptiness_check()
  {
  }

  const unsigned_statistics*
  emptiness_check::statistics() const
  {
    return dynamic_cast<const unsigned_statistics*>(this);
  }

  const char*
  emptiness_check::parse_options(char* options)
  {
    option_map old(o_);
    const char* s = o_.parse_options(options);
    options_updated(old);
    return s;
  }

  void
  emptiness_check::options_updated(const option_map&)
  {
  }

  bool
  emptiness_check::safe() const
  {
    return true;
  }

  std::ostream&
  emptiness_check::print_stats(std::ostream& os) const
  {
    return os;
  }

  // emptiness_check_instantiator
  //////////////////////////////////////////////////////////////////////

  namespace
  {

    spot::emptiness_check*
    couvreur99_cons(const spot::tgba* a, spot::option_map o)
    {
      return spot::couvreur99(a, o);
    }

    struct ec_algo
    {
      const char* name;
      spot::emptiness_check* (*construct)(const spot::tgba*,
					  spot::option_map);
      unsigned int min_acc;
      unsigned int max_acc;
    };

    ec_algo ec_algos[] =
      {
	{ "Cou99",     couvreur99_cons,                     0, -1U },
	{ "CVWY90",    spot::magic_search,                  0,   1 },
	{ "GV04",      spot::explicit_gv04_check,           0,   1 },
	{ "SE05",      spot::se05,                          0,   1 },
	{ "Tau03",     spot::explicit_tau03_search,         1, -1U },
	{ "Tau03_opt", spot::explicit_tau03_opt_search,     0, -1U },
      };
  }

  emptiness_check_instantiator::emptiness_check_instantiator(option_map o,
							     void* i)
    : o_(o), info_(i)
  {
  }

  unsigned int
  emptiness_check_instantiator::min_acceptance_conditions() const
  {
    return static_cast<ec_algo*>(info_)->min_acc;
  }

  unsigned int
  emptiness_check_instantiator::max_acceptance_conditions() const
  {
    return static_cast<ec_algo*>(info_)->max_acc;
  }

  emptiness_check*
  emptiness_check_instantiator::instantiate(const tgba* a) const
  {
    return static_cast<ec_algo*>(info_)->construct(a, o_);
  }

  emptiness_check_instantiator*
  emptiness_check_instantiator::construct(const char* name, const char** err)
  {
    // Skip spaces.
    while (*name && strchr(" \t\n", *name))
      ++name;

    const char* opt_str = strchr(name, '(');
    option_map o;
    if (opt_str)
      {
	const char* opt_start = opt_str + 1;
	const char* opt_end = strchr(opt_start, ')');
	if (!opt_end)
	  {
	    *err = opt_start;
	    return 0;
	  }
	std::string opt(opt_start, opt_end);

	const char* res = o.parse_options(opt.c_str());
	if (res)
	  {
	    *err  = opt.c_str() - res + opt_start;
	    return 0;
	  }
      }

    if (!opt_str)
      opt_str = name + strlen(name);

    // Ignore spaces before `(' (or trailing spaces).
    while (opt_str > name && strchr(" \t\n", *--opt_str))
      continue;
    std::string n(name, opt_str + 1);


    ec_algo* info = ec_algos;
    for (unsigned i = 0; i < sizeof(ec_algos)/sizeof(*ec_algos); ++i, ++info)
      if (n == info->name)
	return new emptiness_check_instantiator(o, info);
    *err = name;
    return 0;
  }

  // tgba_run_to_tgba
  //////////////////////////////////////////////////////////////////////

  namespace
  {
    std::string format_state(const tgba* a, const state* s, int n)
    {
      std::ostringstream os;
      os << a->format_state(s) << " (" << n << ")";
      return os.str();
    }
  }

  tgba*
  tgba_run_to_tgba(const tgba* a, const tgba_run* run)
  {
    tgba_explicit* res = new tgba_explicit(a->get_dict());
    res->copy_acceptance_conditions_of(a);

    const state* s = a->get_init_state();
    int number = 1;
    tgba_explicit::state* source;
    tgba_explicit::state* dest;
    const tgba_run::steps* l;
    bdd seen_acc = bddfalse;

    typedef Sgi::hash_map<const state*, tgba_explicit::state*,
                          state_ptr_hash, state_ptr_equal> state_map;
    state_map seen;

    if (run->prefix.empty())
        l = &run->cycle;
    else
        l = &run->prefix;

    tgba_run::steps::const_iterator i = l->begin();

    assert(s->compare(i->s) == 0);
    source = res->set_init_state(format_state(a, i->s, number));
    ++number;
    seen.insert(std::make_pair(i->s, source));

    for (; i != l->end();)
      {
        // expected outgoing transition
        bdd label = i->label;
        bdd acc = i->acc;

        // compute the next expected state
        const state* next;
        ++i;
        if (i != l->end())
          {
            next = i->s;
          }
        else
          {
            if (l == &run->prefix)
              {
                l = &run->cycle;
                i = l->begin();
              }
            next = l->begin()->s;
          }

        // browse the actual outgoing transitions
        tgba_succ_iterator* j = a->succ_iter(s);
        delete s;
        for (j->first(); !j->done(); j->next())
          {
            if (j->current_condition() != label
                || j->current_acceptance_conditions() != acc)
              continue;

            const state* s2 = j->current_state();
            if (s2->compare(next) != 0)
              {
                delete s2;
                continue;
              }
            else
              {
                s = s2;
                break;
              }
          }
        assert(!j->done());
        delete j;

        state_map::const_iterator its = seen.find(next);
        if (its == seen.end())
          {
            dest = res->add_state(format_state(a, next, number));
            ++number;
            seen.insert(std::make_pair(next, dest));
          }
        else
          dest = its->second;

        tgba_explicit::transition* t = res->create_transition(source, dest);
        res->add_conditions(t, label);
        res->add_acceptance_conditions(t, acc);
        source = dest;

        // Sum acceptance conditions.
        if (l == &run->cycle && i != l->begin())
            seen_acc |= acc;
      }
    delete s;

    assert(seen_acc == a->all_acceptance_conditions());

    res->merge_transitions();

    return res;
  }

}
