// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <ltlvisit/clone.hh>
#include <ltlvisit/destroy.hh>
#include <ltlvisit/tostring.hh>
#include <cassert>
#include "bdddict.hh"

namespace spot
{
  bdd_dict::bdd_dict()
    : bdd_allocator(),
      next_to_now(bdd_newpair()),
      now_to_next(bdd_newpair())
  {
  }

  bdd_dict::~bdd_dict()
  {
    assert_emptiness();
    bdd_freepair(next_to_now);
    bdd_freepair(now_to_next);
  }

  int
  bdd_dict::register_proposition(const ltl::formula* f, const void* for_me)
  {
    int num;
    // Do not build a variable that already exists.
    fv_map::iterator sii = var_map.find(f);
    if (sii != var_map.end())
      {
	num = sii->second;
      }
    else
      {
	f = clone(f);
	num = allocate_variables(1);
	var_map[f] = num;
	var_formula_map[num] = f;
      }
    var_refs[num].insert(for_me);
    return num;
  }

  void
  bdd_dict::register_propositions(bdd f, const void* for_me)
  {
    if (f == bddtrue || f == bddfalse)
      return;

    vf_map::iterator i = var_formula_map.find(bdd_var(f));
    assert(i != var_formula_map.end());
    var_refs[i->first].insert(for_me);

    register_propositions(bdd_high(f), for_me);
    register_propositions(bdd_low(f), for_me);
  }

  int
  bdd_dict::register_state(const ltl::formula* f, const void* for_me)
  {
    int num;
    // Do not build a state that already exists.
    fv_map::iterator sii = now_map.find(f);
    if (sii != now_map.end())
      {
	num = sii->second;
      }
    else
      {
	f = ltl::clone(f);
	num = allocate_variables(2);
	now_map[f] = num;
	now_formula_map[num] = f;
	// Record that num+1 should be renamed as num when
	// the next state becomes current.
	bdd_setpair(next_to_now, num + 1, num);
	bdd_setpair(now_to_next, num, num + 1);
      }
    var_refs[num].insert(for_me);
    return num;
  }

  int
  bdd_dict::register_acceptance_variable(const ltl::formula* f,
					const void* for_me)
  {
    int num;
    // Do not build an acceptance variable that already exists.
    fv_map::iterator sii = acc_map.find(f);
    if (sii != acc_map.end())
      {
	num = sii->second;
      }
    else
      {
	f = clone(f);
	num = allocate_variables(1);
	acc_map[f] = num;
	acc_formula_map[num] = f;
      }
    var_refs[num].insert(for_me);
    return num;
  }

  void
  bdd_dict::register_acceptance_variables(bdd f, const void* for_me)
  {
    if (f == bddtrue || f == bddfalse)
      return;

    vf_map::iterator i = acc_formula_map.find(bdd_var(f));
    assert(i != acc_formula_map.end());
    var_refs[i->first].insert(for_me);

    register_acceptance_variables(bdd_high(f), for_me);
    register_acceptance_variables(bdd_low(f), for_me);
  }


  void
  bdd_dict::register_all_variables_of(const void* from_other,
				      const void* for_me)
  {
    vr_map::iterator i;
    for (i = var_refs.begin(); i != var_refs.end(); ++i)
      {
	ref_set& s = i->second;
	if (s.find(from_other) != s.end())
	  s.insert(for_me);
      }
  }

  void
  bdd_dict::unregister_all_my_variables(const void* me)
  {
    vr_map::iterator i;
    for (i = var_refs.begin(); i != var_refs.end();)
      {
	// Increment i++ now, we will possibly erase
	// the current node (which would invalidate the iterator).
	vr_map::iterator cur = i++;

	ref_set& s = cur->second;
	ref_set::iterator si = s.find(me);
	if (si == s.end())
	  continue;
	s.erase(si);
	if (! s.empty())
	  continue;
	// ME was the last user of this variable.
	// Let's free it.  First, we need to find
	// if this is a Now, a Var, or an Acc variable.
	int var = cur->first;
	int n = 1;
	const ltl::formula* f;
	vf_map::iterator vi = var_formula_map.find(var);
	if (vi != var_formula_map.end())
	  {
	    f = vi->second;
	    var_map.erase(f);
	    var_formula_map.erase(vi);
	  }
	else
	  {
	    vi = now_formula_map.find(var);
	    if (vi != now_formula_map.end())
	      {
		f = vi->second;
		now_map.erase(f);
		now_formula_map.erase(vi);
		n = 2;
		bdd_setpair(next_to_now, var + 1, var + 1);
		bdd_setpair(now_to_next, var, var);
	      }
	    else
	      {
		vi = acc_formula_map.find(var);
		f = vi->second;
		assert(vi != now_formula_map.end());
		acc_map.erase(f);
		acc_formula_map.erase(vi);
	      }
	  }
	// Actually release the associated BDD variables, and the
	// formula itself.
	release_variables(var, n);
	ltl::destroy(f);
	var_refs.erase(cur);
      }
  }

  bool
  bdd_dict::is_registered_proposition(const ltl::formula* f, const void* by_me)
  {
    fv_map::iterator fi = var_map.find(f);
    if (fi == var_map.end())
      return false;
    ref_set& s = var_refs[fi->second];
    return s.find(by_me) != s.end();
  }

  bool
  bdd_dict::is_registered_state(const ltl::formula* f, const void* by_me)
  {
    fv_map::iterator fi = now_map.find(f);
    if (fi == now_map.end())
      return false;
    ref_set& s = var_refs[fi->second];
    return s.find(by_me) != s.end();
  }

  bool
  bdd_dict::is_registered_acceptance_variable(const ltl::formula* f,
					     const void* by_me)
  {
    fv_map::iterator fi = acc_map.find(f);
    if (fi == acc_map.end())
      return false;
    ref_set& s = var_refs[fi->second];
    return s.find(by_me) != s.end();
  }

  std::ostream&
  bdd_dict::dump(std::ostream& os) const
  {
    fv_map::const_iterator fi;
    os << "Atomic Propositions:" << std::endl;
    for (fi = var_map.begin(); fi != var_map.end(); ++fi)
      {
	os << "  " << fi->second << " (x"
	   << var_refs.find(fi->second)->second.size() << "): ";
	to_string(fi->first, os) << std::endl;
      }
    os << "States:" << std::endl;
    for (fi = now_map.begin(); fi != now_map.end(); ++fi)
      {
	int refs = var_refs.find(fi->second)->second.size();
	os << "  " << fi->second << " (x" << refs << "): Now[";
	to_string(fi->first, os) << "]" << std::endl;
	os << "  " << fi->second + 1 << " (x" << refs << "): Next[";
	to_string(fi->first, os) << "]" << std::endl;
      }
    os << "Accepting Conditions:" << std::endl;
    for (fi = acc_map.begin(); fi != acc_map.end(); ++fi)
      {
	os << "  " << fi->second << " (x"
	   << var_refs.find(fi->second)->second.size() << "): Acc[";
	to_string(fi->first, os) << "]" << std::endl;
      }
    os << "Free list:" << std::endl;
    free_list_type::const_iterator i;
    for (i = free_list.begin(); i != free_list.end(); ++i)
      os << "  (" << i->first << ", " << i->second << ")";
    os << std::endl;
    return os;
  }

  void
  bdd_dict::assert_emptiness() const
  {
    bool fail = false;
    if (var_map.empty()
	&& now_map.empty()
	&& acc_map.empty())
      {
	if (! var_formula_map.empty())
	  {
	    std::cerr << "var_map is empty but var_formula_map is not"
		      << std::endl;
	    fail = true;
	  }
	if (! now_formula_map.empty())
	  {
	    std::cerr << "now_map is empty but now_formula_map is not"
		      << std::endl;
	    fail = true;
	  }
	if (! acc_formula_map.empty())
	  {
	    std::cerr << "acc_map is empty but acc_formula_map is not"
		      << std::endl;
	    fail = true;
	  }
	if (! var_refs.empty())
	  {
	    std::cerr << "maps are empty but var_refs is not" << std::endl;
	    fail = true;
	    vr_map::const_iterator i;
	    for (i = var_refs.begin(); i != var_refs.end(); ++i)
	      std::cerr << " " << i->first << ":" << i->second.size();
	    std::cerr << std::endl;
	  }
	if (! fail)
	  return;
      }
    else
      {
	std::cerr << "some maps are not empty" << std::endl;
      }
    dump(std::cerr);
    assert(0);
  }
}
