// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include <queue>
#include <limits>
#include <sys/time.h>
#include "tgbaalgos/scc.hh"
#include "ltlparse/ltlfile.hh"
#include "ltlvisit/tostring.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/randomgraph.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/replayrun.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include "ltlvisit/apcollect.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/cutscc.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"

namespace spot
{
  unsigned tgba_size(const tgba* a)
  {
    typedef Sgi::hash_set<const state*,
      state_ptr_hash, state_ptr_equal> hash_type;
    hash_type seen;
    std::queue<state*> tovisit;
    // Perform breadth-first search.
    state* init = a->get_init_state();
    tovisit.push(init);
    seen.insert(init);
    unsigned count = 0;
    // While there are still states to visit.
    while (!tovisit.empty())
    {
      ++count;
      state* cur = tovisit.front();
      tovisit.pop();
      tgba_succ_iterator* sit = a->succ_iter(cur);
      for (sit->first(); !sit->done(); sit->next())
      {
	state* dst = sit->current_state();
	// Is it a new state ?
	if (seen.find(dst) == seen.end())
	{
	  // Yes, register the successor for later processing.
	  tovisit.push(dst);
	  seen.insert(dst);
	}
	else
	  // No, free dst.
	  delete dst;
      }
      delete sit;
    }
    hash_type::iterator it2;
    // Free visited states.
    for (it2 = seen.begin(); it2 != seen.end(); it2++)
    {
      delete *it2;
    }
    return count;
  }
}

void TimerReset(struct timeval& start)
{
  // Initialize timer with the current time.
  gettimeofday(&start, 0);
}

double TimerGetElapsedTime(struct timeval& start)
{
  // Compute the diferrence between now and the time when start was initialized.
  double t1, t2;
  struct timeval end;
  TimerReset(end);
  t1 = (double)start.tv_sec + (double)start.tv_usec*0.000001;
  t2 = (double)end.tv_sec + (double)end.tv_usec*0.000001;

  return t2 - t1;
}

bool accepting_path(spot::tgba* a, std::ostream& output, bool do_print)
{
  const char* err;
  spot::emptiness_check_instantiator* inst;
  inst = spot::emptiness_check_instantiator::construct("Cou99", &err);
  spot::emptiness_check* ec = inst->instantiate(a);
  spot::emptiness_check_result* acc;
  acc = ec->check();
  spot::tgba_run* run;

  if (acc)
  {
    run = acc->accepting_run();
    //const spot::unsigned_statistics* stats = acc->statistics();
    //spot::Replay_tgba_run(output, acc->automaton(), run);
    if (do_print)
      ec->print_stats(output);
    delete run;
    delete acc;
  }
  else if (do_print)
    std::cout << "No accepting path." << std::endl;
  delete inst;
  delete ec;
  return acc != 0;
}

int main(int argc, char* argv[])
{
  if (argc > 4)
  {
    std::cout << "Usage ./cutscc file_name split_count [model_file]"
	      << std::endl;
    return 1;
  }
  std::ofstream out_dot ("auto.dot");
  std::cout.setf (std::ios::fixed);
  bool print_size = true;
  bool print_time = true;
  bool print_ec = true;
  int i = 0;
  int split_count = atoi(argv[2]);
  std::vector<bool> accepting_vector;
  accepting_vector.reserve(10000);
  double split_sum = 0.;
  double full_sum = 0.;
  double cut_sum = 0.;
  spot::tgba* r = 0;
  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::bdd_dict* dict = new spot::bdd_dict();
  if (argc == 4)
  {
    spot::tgba_parse_error_list error_list;
    r = spot::tgba_parse(argv[3], error_list, dict, env, env, false);
    if (print_size)
      std::cout << "Reference automaton has " << tgba_size(r)
		<< " states." << std::endl
		<<"Trying to split each automaton in " << split_count
		<< " sub automata." << std::endl;
  }
  // iter_count is the number of iterations done to increase precision.
  unsigned iter_count = 1;
  std::cout << "Each operation is repeated " << iter_count
	    << " times to improve precision." << std::endl << std::endl;

  spot::ltl::ltl_file formulae(argv[1]);
  spot::ltl::formula* f;
  while((f = formulae.next()))
  {
    spot::tgba* a = ltl_to_tgba_fm(f, dict, /* exprop */ true);
    ++i;
    std::cout << "Formula " << i << std::endl;
    std::cout << spot::ltl::to_string(f) << std::endl << std::endl;
    if (argc != 4)
    {
      spot::ltl::atomic_prop_set* s;
      s = spot::ltl::atomic_prop_collect(f, 0);
      spot::ltl::atomic_prop_set::iterator sit;
      delete r;
      r = spot::random_graph(10000, 0.0001, s, dict, 0, 0.15, 0.5);
      delete s;
    }
    spot::scc_map m (a);
    m.build_map();

    spot::tgba* res = 0;

    // Simplify the tgba to delete useless pathes
    if (argc != 4)
    {
      spot::tgba* tmp = a;
      a = spot::split_tgba(a, m, 1).front();
      delete tmp;
    }

    struct timeval start;
    double elapsed = 0.;
    bool full_result;
    unsigned j;
    for (j = 0; j < iter_count; j++)
    {
      // Measure the mean computing time for the product with our full
      // automaton.
      TimerReset(start);
      res = new spot::tgba_product(r, a);
      full_result = accepting_path(res, std::cout, false);
      elapsed += TimerGetElapsedTime(start);
      delete res;
    }
    double full_time = elapsed / iter_count;
    full_sum += full_time;
    // Compute again for printing purposes only.
    res = new spot::tgba_product(r, a);

    unsigned full_size;
    if (print_size)
    {
      full_size = tgba_size(res);
      std::cout << "Full Product : " << full_size << " states";
    }
    if (print_time)
    {
      std::cout << " in " << elapsed / iter_count << "s";
    }
    std::cout << std::endl;
    if (print_ec)
    {
      accepting_path(res, std::cout, true);
      std::cout << std::endl;
    }
    delete res;

    double cut_time = 0.;
    std::list<spot::tgba*> splitted;
    for (j = 0; j < iter_count; j++)
    {
      // Mean computing time to split the automaton in split_count automata
      spot::scc_map m2 (a);
      m2.build_map();
      TimerReset(start);
      splitted = spot::split_tgba(a, m2, split_count);
      cut_time += TimerGetElapsedTime(start);
      std::list<spot::tgba*>::iterator lit;
      for (lit = splitted.begin(); lit != splitted.end(); lit++)
	delete *lit;
    }

    cut_sum = cut_time / iter_count;
    // Compute again for printing purposes only
    spot::scc_map m2 (a);
    m2.build_map();
    //  if (i == 42)
    //	spot::dotty_reachable(out_dot, a);
    splitted = spot::split_tgba(a, m2, split_count);
    if (print_time)
    {
      std::cout << "Splitting in "
		<< cut_time / iter_count << "s" << std::endl;
    }

    double min = std::numeric_limits<double>::max();
    double max = 0.;
    accepting_vector[i] = false;
    unsigned split_size = 0;
    if (print_size)
      std::cout << "Base automaton splitted in " << splitted.size()
		<< " automata." << std::endl;
    std::cout << std::endl;
    unsigned k = 1;
    std::list<spot::tgba*>::iterator it;
    for (it = splitted.begin(); it != splitted.end(); ++it)
    {
      elapsed = 0;
      for (j = 0; j < iter_count; j++)
      {
	// Compute mean computing time for the product with only a part of the
	// full automaton.
	TimerReset(start);
	res = new spot::tgba_product(r, *it);
	bool is_accepting = accepting_path(res, std::cout, false);
	accepting_vector[i] = accepting_vector[i]
	  || is_accepting;
	elapsed += TimerGetElapsedTime(start);
	delete res;
      }
      res = new spot::tgba_product(r, *it);
      elapsed = elapsed / iter_count;
      if (print_size)
      {
	unsigned size = tgba_size(res);
	split_size += size;
	std::cout << "Product " << k << " : " << size
		  << " states in " << elapsed << "s" << std::endl;
      }
      bool is_accepting;
      if (print_ec)
      {
	is_accepting = accepting_path(res, std::cout, true);
	std::cout << std::endl;
      }
      else
	is_accepting = accepting_path(res, std::cout, false);
      ++k;

      delete res;
      if (is_accepting)
	min = (elapsed<min) ? elapsed:min;
      else
	max = (elapsed>max) ? elapsed:max;
    }
    if (print_size)
    {
      std::cout << "Total split products size : " << split_size << std::endl;
      unsigned added_states = 0.;
      added_states = split_size - full_size;
      double time_gain = full_time -
	(((accepting_vector[i]) ? min:max) + (cut_time / iter_count));
      std::cout << "Additionnal states created : " << added_states
		<< std::endl;
      std::cout << "Additionnal states ratio : "
		<< double(added_states) / full_size << std::endl;
      std::cout << "Cutting and computing time : "
		<< ((accepting_vector[i]) ? min:max) + (cut_time / iter_count)
		<< "s" << std::endl;
      std::cout << "Time gain " << time_gain << "s" << std::endl;
      std::cout << std::endl;
    }
    std::list<spot::tgba*>::iterator lit;
    for (lit = splitted.begin(); lit != splitted.end(); lit++)
      delete *lit;
    if (accepting_vector[i] != full_result)
      std::cout << "Disagree !" << std::endl;
    split_sum += (accepting_vector[i]) ? min:max;

    delete a;
    f->destroy();
    std::string next = "----------------------------------------";
    std::cout << next << next << std::endl;
    std::cout << std::endl;
  }
  delete r;
  delete dict;
  std::cout << "Full    " << full_sum << "s" << std::endl
	    << "Cutting " << cut_sum << "s" << std::endl
	    << "Split   " << split_sum << "s" << std::endl;
}
