// Copyright (C) 2009, 2011 Laboratoire de Recherche et Developpement
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

#include <iostream>
#include <string>
#include <queue>
#include "tgba/tgbaexplicit.hh"
#include "cutscc.hh"

namespace spot
{
  tgba* cut_scc(const tgba* a, const scc_map& m,
		const std::set<unsigned>& s)
  {
    tgba_explicit_string* sub_a = new tgba_explicit_string(a->get_dict());
    state* cur = a->get_init_state();
    std::queue<state*> tovisit;
    typedef Sgi::hash_set<const state*,
      state_ptr_hash, state_ptr_equal> hash_type;
    // Setup
    hash_type seen;
    unsigned scc_number;
    std::string cur_format = a->format_state(cur);
    std::set<unsigned>::iterator it;
    // Check if we have at least one accepting SCC.
    for (it = s.begin(); it != s.end() && !m.accepting(*it); it++)
      continue;
    assert(it != s.end());
    tovisit.push(cur);
    seen.insert(cur);
    sub_a->add_state(cur_format);
    sub_a->copy_acceptance_conditions_of(a);
    // If the initial is not part of one of the desired SCC, exit
    assert(s.find(m.scc_of_state(cur)) != s.end());

    // Perform BFS to visit each state.
    while (!tovisit.empty())
    {
      cur = tovisit.front();
      tovisit.pop();
      tgba_succ_iterator* sit = a->succ_iter(cur);
      for (sit->first(); !sit->done(); sit->next())
      {
	cur_format = a->format_state(cur);
	state* dst = sit->current_state();
	std::string dst_format = a->format_state(dst);
	scc_number= m.scc_of_state(dst);
	// Is the successor included in one of the desired SCC ?
	if (s.find(scc_number) != s.end())
	{
	  if (seen.find(dst) == seen.end())
	  {
	    tovisit.push(dst);
	    seen.insert(dst); // has_state?
	  }
	  else
	  {
	    dst->destroy();
	  }
	  tgba_explicit::transition* t =
	    sub_a->create_transition(cur_format, dst_format);
	  sub_a->add_conditions(t, sit->current_condition());
	  sub_a->
	    add_acceptance_conditions(t,
				      sit->current_acceptance_conditions());
	}
	else
	{
	  dst->destroy();
	}
      }
      delete sit;
    }

    hash_type::iterator it2;
    // Free visited states.
    for (it2 = seen.begin(); it2 != seen.end(); it2++)
    {
      (*it2)->destroy();
    }
    return sub_a;
  }

  void print_set(const sccs_set* s)
  {
    std::cout << "set : ";
    std::set<unsigned>::iterator vit;
    for (vit = s->sccs.begin(); vit != s->sccs.end(); ++vit)
      std::cout << *vit << " ";
    std::cout << std::endl;
  }

  unsigned set_distance(const sccs_set* s1,
			const sccs_set* s2,
			const std::vector<unsigned>& scc_sizes)
  {
    // Compute the distance between two sets.
    // Formula is : distance = size(s1) + size(s2) - size(s1 inter s2)
    std::set<unsigned>::iterator it;
    std::set<unsigned> result;
    unsigned inter_sum = 0;
    std::set_intersection(s1->sccs.begin(), s1->sccs.end(),
			  s2->sccs.begin(), s2->sccs.end(),
			  std::inserter(result, result.begin()));
    for (it = result.begin(); it != result.end(); ++it)
      inter_sum += scc_sizes[*it];
    return s1->size + s2->size - 2*inter_sum;
  }

  sccs_set* set_union(sccs_set* s1,
		      sccs_set* s2,
		      const std::vector<unsigned>& scc_sizes)
  {
    // Perform the union of two sets.
    sccs_set* result = new sccs_set;
    set_union(s1->sccs.begin(), s1->sccs.end(),
	      s2->sccs.begin(), s2->sccs.end(),
	      std::inserter(result->sccs, result->sccs.begin()));
    result->size = 0;
    std::set<unsigned>::iterator it;
    for (it = result->sccs.begin(); it != result->sccs.end(); ++it)
      result->size += scc_sizes[*it];
    delete s1;
    return result;
  }

  struct recurse_data
  {
    std::set<unsigned> seen;
    std::vector<std::vector<sccs_set* > >* rec_paths;
  };

  void find_paths_sub(unsigned init_scc,
		      const scc_map& m,
		      recurse_data& d,
		      const std::vector<unsigned>& scc_sizes)
  {
    // Find all the paths from the initial states to an accepting SCC
    // We need two stacks, one to track the current state, the other to track
    // the current iterator of this state.
    std::stack<scc_map::succ_type::const_iterator> it_stack;
    std::stack<unsigned> scc_stack;
    std::vector<const scc_map::succ_type*> scc_succ;
    unsigned scc_count = m.scc_count();
    scc_succ.reserve(scc_count);
    d.seen.insert(init_scc);
    unsigned i;
    for (i = 0; i < scc_count; ++i)
      scc_succ.push_back(&(m.succ(i)));
    // Setup the two stacks with the initial SCC.
    scc_stack.push(init_scc);
    it_stack.push(scc_succ[init_scc]->begin());
    while (!scc_stack.empty())
    {
      unsigned cur_scc = scc_stack.top();
      scc_stack.pop();
      d.seen.insert(cur_scc);
      scc_map::succ_type::const_iterator it;
      // Find the next unvisited successor.
      for (it = it_stack.top(); it != scc_succ[cur_scc]->end()
	     && d.seen.find(it->first) != d.seen.end(); ++it)
	continue;
      it_stack.pop();
      // If there are no successors and if the SCC is not accepting, this is
      // an useless path. Throw it away.
      if (scc_succ[cur_scc]->begin() == scc_succ[cur_scc]->end()
	  && !m.accepting(cur_scc))
	continue;
      std::vector<std::vector<sccs_set* > >* rec_paths = d.rec_paths;
      // Is there a successor to process ?
      if (it != scc_succ[cur_scc]->end())
      {
	// Yes, add it to the stack for later processing.
	unsigned dst = it->first;
	scc_stack.push(cur_scc);
	++it;
	it_stack.push(it);
	if (d.seen.find(dst) == d.seen.end())
	{
	  scc_stack.push(dst);
	  it_stack.push(scc_succ[dst]->begin());
	}
      }
      else
      {
	// No, all successors have been processed, update the current SCC.
	for (it = scc_succ[cur_scc]->begin();
	     it != scc_succ[cur_scc]->end(); ++it)
	{
	  unsigned dst = it->first;
	  std::vector<sccs_set*>::iterator lit;
	  // Extend all the reachable paths by adding the current SCC.
	  for (lit = (*rec_paths)[dst].begin();
	       lit != (*rec_paths)[dst].end(); ++lit)
	  {
	    sccs_set* path = new sccs_set;
	    path->sccs = (*lit)->sccs;
	    path->size = (*lit)->size + scc_sizes[cur_scc];
	    path->sccs.insert(path->sccs.begin(), cur_scc);
	    (*rec_paths)[cur_scc].push_back(path);
	  }
	}
	bool has_succ = false;
	for (it = scc_succ[cur_scc]->begin();
	     it != scc_succ[cur_scc]->end() && !has_succ; ++it)
	{
	  has_succ = !(*rec_paths)[it->first].empty();
	}
	// Create a new path iff the SCC is accepting and not included
	// in another path.
	if (m.accepting(cur_scc) && !has_succ)
	{
	  sccs_set* path = new sccs_set;
	  path->size = scc_sizes[cur_scc];
	  path->sccs.insert(path->sccs.begin(), cur_scc);
	  (*rec_paths)[cur_scc].push_back(path);
	}
      }

    }
    return;
  }

  std::vector<std::vector<sccs_set* > >* find_paths(tgba* a, const scc_map& m)
  {
    unsigned scc_count = m.scc_count();
    unsigned i;
    recurse_data d;
    d.rec_paths = new std::vector<std::vector<sccs_set* > >;
    for (i = 0; i < scc_count; i++)
    {
      std::vector<sccs_set*> list_set;
      d.rec_paths->push_back(list_set);
    }
    // We use a vector to recall the size of all SCCs.
    std::vector<unsigned> scc_sizes(scc_count, 0);
    for (i = 0; i < scc_count; ++i)
      scc_sizes[i] = m.states_of(i).size();
    state* initial_state = a->get_init_state();
    unsigned init = m.scc_of_state(initial_state);
    initial_state->destroy();
    // Find all interesting pathes in our automaton.
    find_paths_sub(init, m, d, scc_sizes);

    return d.rec_paths;
  }

  std::list<tgba*> split_tgba(tgba* a, const scc_map& m,
			      unsigned split_number)
  {
    // Main function to split an automaton tgba in split_number sub automata.
    unsigned i;
    unsigned scc_count = m.scc_count();
    unsigned j;
    std::vector<std::vector<sccs_set* > >* rec_paths = find_paths(a, m);
    state* initial_state = a->get_init_state();
    unsigned init = m.scc_of_state(initial_state);
    initial_state->destroy();
    std::vector<sccs_set*>* final_sets =&(*rec_paths)[init];
    if (rec_paths->empty())
    {
      std::list<tgba*> empty;
      return empty;
    }

    unsigned paths_count = final_sets->size();
    std::vector< std::vector<unsigned> > dist;
    for (i = 0; i < paths_count; ++i)
    {
      std::vector<unsigned> dist_sub(i, 0);
      dist.push_back(dist_sub);
    }

    // We use a vector to recall the size of all SCCs.
    std::vector<unsigned> scc_sizes(scc_count, 0);
    for (i = 0; i < scc_count; ++i)
      scc_sizes[i] = m.states_of(i).size();

    // Compute the distance between all pairs of pathes.
    for (i = 0; i < paths_count; ++i)
      for (j = 0; j < i; ++j)
      {
	dist[i][j] = set_distance((*final_sets)[i],
				  (*final_sets)[j], scc_sizes);
      }

    std::vector<bool> is_valid(paths_count, true);
    unsigned remaining_paths = paths_count;
    // While the number of subsets is strictly superior to split_number,
    // merge the two sets with the lowest distance.
    while (remaining_paths > split_number)
    {
      --remaining_paths;
      unsigned min_i = 1;
      unsigned min_j = 0;
      // Initialize with max value.
      unsigned min = (unsigned)(-1);
      // Find the two sets with the lowest distance.
      for (i = 0; i < paths_count; ++i)
	for (j = 0; j < i; ++j)
	  if (is_valid[i] && is_valid[j])
	  {
	    if (dist[i][j] < min)
	    {
	      min_i = i;
	      min_j = j;
	      min = dist[min_i][min_j];
	    }
	  }

      // Merge these sets.
      (*final_sets)[min_i] = set_union((*final_sets)[min_i],
				       (*final_sets)[min_j],
				       scc_sizes);
      // The second set is now unused.
      is_valid[min_j] = false;

      // Update the distances with other sets.
      for (j = 0; j < min_i; ++j)
	if (is_valid[min_i] && is_valid[j])
	  dist[min_i][j] = set_distance((*final_sets)[min_i],
					(*final_sets)[j], scc_sizes);
      for (i = min_i + 1; i < dist.size(); ++i)
	if (is_valid[i] && is_valid[min_i])
	  dist[i][min_i] = set_distance((*final_sets)[min_i],
					(*final_sets)[i], scc_sizes);
    }
    std::list<tgba*> result;
    // Final sets.
    for (i = 0; i < final_sets->size(); ++i)
      if (is_valid[i] == true)
      {
	//print_set((*final_sets)[i]);
	result.push_back(cut_scc(a, m, (*final_sets)[i]->sccs));
      }

    // Free everything.
    for (i = 0; i < rec_paths->size(); ++i)
      for (j = 0; j < (*rec_paths)[i].size(); ++j)
      {
	delete (*rec_paths)[i][j];
      }
    delete rec_paths;
    return result;
  }

  unsigned max_spanning_paths(std::vector<sccs_set* >* paths, scc_map& m)
  {
    unsigned scc_count = m.scc_count();
    std::vector<bool> sccs_marked (scc_count, false);
    std::vector<bool> paths_marked (paths->size(), false);
    bool done = false;
    unsigned iter_count = 0;
    while (!done)
    {
      unsigned max = 0;
      unsigned max_index = 0;
      unsigned i;
      for (i = 0; i < paths->size(); ++i)
      {
	if (paths_marked[i])
	  continue;
	unsigned unmarked_sccs = 0;
	std::set<unsigned>* cur_path = &(*paths)[i]->sccs;
	std::set<unsigned>::iterator it;
	for (it = cur_path->begin(); it != cur_path->end(); ++it)
	{
	  if (!sccs_marked[*it])
	    ++unmarked_sccs;
	}
	if (unmarked_sccs > max)
	{
	  max = unmarked_sccs;
	  max_index = i;
	}
      }
      if (max == 0)
      {
	done = true;
	continue;
      }
      ++iter_count;
      paths_marked[max_index] = true;
      std::set<unsigned>* cur_path = &(*paths)[max_index]->sccs;
      std::set<unsigned>::iterator it;
      for (it = cur_path->begin(); it != cur_path->end(); ++it)
      {
	sccs_marked[*it] = true;
      }
    }
    return iter_count;
  }
}
