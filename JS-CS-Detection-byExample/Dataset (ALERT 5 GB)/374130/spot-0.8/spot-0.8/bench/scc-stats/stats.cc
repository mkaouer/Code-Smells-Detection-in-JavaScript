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
#include <math.h>
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/cutscc.hh"
#include "ltlparse/ltlfile.hh"
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

void compute_and_print(std::vector<double>& v,
		       int count, std::ofstream& output)
{
   int i;
   double sum = 0.;
   double mean;
   double median;
   double variance = 0.;
   sum = 0;
   // Compute mean: sigma(Xi)/n for i=0..n-1.
   for (i = 0; i < count; i++)
     sum += v[i];
   mean = sum / count;
   // Compute variance: sigma((Xi - mean)*(Xi - mean))/n for i=0..n-1.
   for (i = 0; i < count; i++)
     variance += (v[i] - mean)*(v[i] - mean);
   variance = variance / count;
   // Compute median: mean of (n-th/2) value and ((n-th/2)+1) value if n even
   // else (n-th+1) value if n odd.
   if (count % 2 == 0)
     median = float(v[count/2] + v[(count/2)+1])/2;
   else
     median = v[(count+1)/2];
   output << "\tMin = " << v[0] << std::endl;
   output << "\tMax = " << v[count-1] << std::endl;
   output << "\tMean = " << mean << std::endl;
   output << "\tMedian = " << median << std::endl;
   output << "\tStandard Deviation = " << sqrt(variance) << std::endl;
   output << std::endl;
}

int main (int argc, char* argv[])
{
  if (argc != 2)
  {
    std::cout << "Usage : ./stats file_name" << std::endl;
    std::cout << "There must be one LTL formula per line." << std::endl;
    return 1;
  }
  std::ofstream output;
  output.open("results");
  spot::bdd_dict* dict = new spot::bdd_dict();
  unsigned count = 0;
  bool count_even;
  std::vector<double> acc_scc;
  std::vector<double> dead_scc;
  std::vector<double> acc_paths;
  std::vector<double> dead_paths;
  std::vector<double> spanning_paths;
  std::vector<double> self_loops;
  unsigned k = 0;
  // Get each LTL formula.
  spot::ltl::ltl_file formulae(argv[1]);
  spot::ltl::formula* f;
  while((f = formulae.next()))
  {
    ++k;
    spot::tgba* a = ltl_to_tgba_fm(f, dict, /* exprop */ true);
    f->destroy();
    // Get number of spanning paths.
    spot::scc_map m (a);
    m.build_map();
    spot::state* initial_state = a->get_init_state();
    unsigned init = m.scc_of_state(initial_state);
    delete initial_state;
    std::vector<std::vector<spot::sccs_set* > >* paths = find_paths(a, m);
    unsigned spanning_count =spot::max_spanning_paths(&(*paths)[init], m);
    spanning_paths.push_back(double(spanning_count));
    // Get characteristics from automaton.
    spot::scc_stats stat;
    stat = build_scc_stats(a);

    // Add those characteristics to our arrays.
    acc_scc.push_back(double(stat.acc_scc));
    dead_scc.push_back(double(stat.dead_scc));
    acc_paths.push_back(double(stat.acc_paths));
    dead_paths.push_back(double(stat.dead_paths));
    self_loops.push_back(double(stat.self_loops)/tgba_size(a));
    ++count;
    delete a;
    unsigned i;
    unsigned j;
    for (i = 0; i < paths->size(); ++i)
      for (j = 0; j < (*paths)[i].size(); ++j)
	delete (*paths)[i][j];
    delete paths;
  }

  if (count == 0)
    {
      std::cerr << "Nothing read." << std::endl;
      exit(1);
    }

  // We could have inserted at the right place instead of
  // sorting at the end.
  // Sorting allows us to find the extrema and
  // the median of the distribution.
  sort(acc_scc.begin(), acc_scc.end());
  sort(dead_scc.begin(), dead_scc.end());
  sort(acc_paths.begin(), acc_paths.end());
  sort(spanning_paths.begin(), spanning_paths.end());
  sort(dead_paths.begin(), dead_paths.end());
  sort(self_loops.begin(), self_loops.end());
  count_even = (count % 2 == 0);
  output << "Parsed Formulae : " << count << std::endl << std::endl;

  // Accepting SCCs
  output << "Accepting SCCs:" << std::endl;
  compute_and_print(acc_scc, count, output);

  // Dead SCCs
  output << "Dead SCCs:" << std::endl;
  compute_and_print(dead_scc, count, output);

  // Accepting Paths
  output << "Accepting Paths:" << std::endl;
  compute_and_print(acc_paths, count, output);

  // Dead Paths
  output << "Dead Paths:" << std::endl;
  compute_and_print(dead_paths, count, output);

  // Max Effective Splitting
  output << "Max effective splitting:" << std::endl;
  compute_and_print(spanning_paths, count, output);

  // Self loops
  output << "Self loops per State:" << std::endl;
  compute_and_print(self_loops, count, output);

  std::cout << "Statistics generated in file results." << std::endl;
  output.close();
  delete dict;
  return 0;
}
