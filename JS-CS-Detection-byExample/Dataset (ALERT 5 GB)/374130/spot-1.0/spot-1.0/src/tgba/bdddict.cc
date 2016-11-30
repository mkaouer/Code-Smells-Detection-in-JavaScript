// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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
#include <sstream>
#include <cassert>
#include <ltlvisit/tostring.hh>
#include <ltlvisit/tostring.hh>
#include <ltlast/atomic_prop.hh>
#include <ltlenv/defaultenv.hh>
#include "bdddict.hh"

namespace spot
{
  bdd_dict::bdd_dict()
    : bdd_allocator(),
      bdd_map(bdd_varnum()),
      next_to_now(bdd_newpair()),
      now_to_next(bdd_newpair())
  {
    free_anonymous_list_of[0] = anon_free_list(this);
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
	f = f->clone();
	num = allocate_variables(1);
	var_map[f] = num;
	bdd_map.resize(bdd_varnum());
	bdd_map[num].type = var;
	bdd_map[num].f = f;
      }
    bdd_map[num].refs.insert(for_me);
    return num;
  }

  void
  bdd_dict::register_propositions(bdd f, const void* for_me)
  {
    if (f == bddtrue || f == bddfalse)
      return;

    int v = bdd_var(f);
    assert(unsigned(v) < bdd_map.size());
    bdd_info& i = bdd_map[v];
    assert(i.type == var);
    i.refs.insert(for_me);

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
	f = f->clone();
	num = allocate_variables(2);
	now_map[f] = num;
	bdd_map.resize(bdd_varnum());
	bdd_map[num].type = now;
	bdd_map[num].f = f;
	bdd_map[num + 1].type = next;
	bdd_map[num + 1].f = f;
	// Record that num+1 should be renamed as num when
	// the next state becomes current.
	bdd_setpair(next_to_now, num + 1, num);
	bdd_setpair(now_to_next, num, num + 1);
      }
    bdd_map[num].refs.insert(for_me); // Keep only references for now.
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
	f = f->clone();
	num = allocate_variables(1);
	acc_map[f] = num;
	bdd_map.resize(bdd_varnum());
	bdd_info& i = bdd_map[num];
	i.type = acc;
	i.f = f;
	i.clone_counts = 0;
      }
    bdd_map[num].refs.insert(for_me);
    return num;
  }

  void
  bdd_dict::register_acceptance_variables(bdd f, const void* for_me)
  {
    if (f == bddtrue || f == bddfalse)
      return;

    int v = bdd_var(f);
    assert(unsigned(v) < bdd_map.size());
    bdd_info& i = bdd_map[v];
    assert(i.type == acc);
    i.refs.insert(for_me);

    register_acceptance_variables(bdd_high(f), for_me);
    register_acceptance_variables(bdd_low(f), for_me);
  }

  int
  bdd_dict::register_clone_acc(int v, const void* for_me)
  {
    assert(unsigned(v) < bdd_map.size());
    bdd_info& i = bdd_map[v];
    assert(i.type == acc);

    std::ostringstream s;
    // FIXME: We could be smarter and reuse unused "$n" numbers.
    s << ltl::to_string(i.f) << "$" << ++i.clone_counts;
    const ltl::formula* f =
      ltl::atomic_prop::instance(s.str(),
				 ltl::default_environment::instance());
    int res = register_acceptance_variable(f, for_me);
    f->destroy();
    return res;
  }

  const ltl::formula*
  bdd_dict::oneacc_to_formula(int var) const
  {
    assert(unsigned(var) < bdd_map.size());
    const bdd_info& i = bdd_map[var];
    assert(i.type == acc);
    return i.f;
  }

  const ltl::formula*
  bdd_dict::oneacc_to_formula(bdd oneacc) const
  {
    assert(oneacc != bddfalse);
    while (bdd_high(oneacc) == bddfalse)
      {
	oneacc = bdd_low(oneacc);
	assert(oneacc != bddfalse);
      }
    return oneacc_to_formula(bdd_var(oneacc));
  }

  int
  bdd_dict::register_anonymous_variables(int n, const void* for_me)
  {
    free_anonymous_list_of_type::iterator i =
      free_anonymous_list_of.find(for_me);

    if (i == free_anonymous_list_of.end())
      {
 	typedef free_anonymous_list_of_type fal;
	i = (free_anonymous_list_of.insert
	     (fal::value_type(for_me, free_anonymous_list_of[0]))).first;
      }
    int res = i->second.register_n(n);

    bdd_map.resize(bdd_varnum());

    while (n--)
      {
	bdd_map[res + n].type = anon;
	bdd_map[res + n].refs.insert(for_me);
      }

    return res;
  }


  void
  bdd_dict::register_all_variables_of(const void* from_other,
				      const void* for_me)
  {
    bdd_info_map::iterator i;
    for (i = bdd_map.begin(); i != bdd_map.end(); ++i)
      {
	ref_set& s = i->refs;
	if (s.find(from_other) != s.end())
	  s.insert(for_me);
      }

    free_anonymous_list_of_type::const_iterator j =
      free_anonymous_list_of.find(from_other);
    if (j != free_anonymous_list_of.end())
      free_anonymous_list_of[for_me] = j->second;
  }

  void
  bdd_dict::unregister_variable(int v, const void* me)
  {
    assert(unsigned(v) < bdd_map.size());

    ref_set& s = bdd_map[v].refs;
    ref_set::iterator si = s.find(me);
    if (si == s.end())
      return;

    s.erase(si);

    // If var is anonymous, we should reinsert it into the free list
    // of ME's anonymous variables.
    if (bdd_map[v].type == anon)
      free_anonymous_list_of[me].release_n(v, 1);

    if (!s.empty())
      return;

    // ME was the last user of this variable.
    // Let's free it.  First, we need to find
    // if this is a Now, a Var, or an Acc variable.
    int n = 1;
    const ltl::formula* f = 0;
    switch (bdd_map[v].type)
      {
      case var:
	f = bdd_map[v].f;
	var_map.erase(f);
	break;
      case now:
	f = bdd_map[v].f;
	now_map.erase(f);
	bdd_setpair(now_to_next, v, v);
	bdd_setpair(next_to_now, v + 1, v + 1);
	n = 2;
	break;
      case next:
	break;
      case acc:
	f = bdd_map[v].f;
	acc_map.erase(f);
	break;
      case anon:
	{
	  free_anonymous_list_of_type::iterator i;
	  // Nobody use this variable as an anonymous variable
	  // anymore, so remove it entirely from the anonymous
	  // free list so it can be used for something else.
	  for (i = free_anonymous_list_of.begin();
	       i != free_anonymous_list_of.end(); ++i)
	    i->second.remove(v, n);
	  break;
	}
      }
    // Actually release the associated BDD variables, and the
    // formula itself.
    release_variables(v, n);
    if (f)
      f->destroy();
    while (n--)
      bdd_map[v + n].type = anon;
  }

  void
  bdd_dict::unregister_all_my_variables(const void* me)
  {
    unsigned s = bdd_map.size();
    for (unsigned i = 0; i < s; ++i)
      unregister_variable(i, me);
    free_anonymous_list_of.erase(me);
  }

  bool
  bdd_dict::is_registered_proposition(const ltl::formula* f, const void* by_me)
  {
    fv_map::iterator fi = var_map.find(f);
    if (fi == var_map.end())
      return false;
    ref_set& s = bdd_map[fi->second].refs;
    return s.find(by_me) != s.end();
  }

  bool
  bdd_dict::is_registered_state(const ltl::formula* f, const void* by_me)
  {
    fv_map::iterator fi = now_map.find(f);
    if (fi == now_map.end())
      return false;
    ref_set& s = bdd_map[fi->second].refs;
    return s.find(by_me) != s.end();
  }

  bool
  bdd_dict::is_registered_acceptance_variable(const ltl::formula* f,
					      const void* by_me)
  {
    fv_map::iterator fi = acc_map.find(f);
    if (fi == acc_map.end())
      return false;
    ref_set& s = bdd_map[fi->second].refs;
    return s.find(by_me) != s.end();
  }

  std::ostream&
  bdd_dict::dump(std::ostream& os) const
  {
    os << "Variable Map:\n";
    unsigned s = bdd_map.size();
    for (unsigned i = 0; i < s; ++i)
      {
	os << " " << i << " ";
	const bdd_info& r = bdd_map[i];
	switch (r.type)
	  {
	  case anon:
	    os << (r.refs.empty() ? "Free" : "Anon");
	    break;
	  case now:
	    os << "Now[" << to_string(r.f) << "]";
	    break;
	  case next:
	    os << "Next[" << to_string(r.f) << "]";
	    break;
	  case acc:
	    os << "Acc[" << to_string(r.f) << "]";
	    break;
	  case var:
	    os << "Var[" << to_string(r.f) << "]";
	    break;
	  }
	if (!r.refs.empty())
	  {
	    os << " x" << r.refs.size() << " {";
	    for (ref_set::const_iterator si = r.refs.begin();
		 si != r.refs.end(); ++si)
	      os << " " << *si;
	    os << " }";
	  }
	os << "\n";
      }
    os << "Anonymous lists:\n";
    free_anonymous_list_of_type::const_iterator ai;
    for (ai = free_anonymous_list_of.begin();
	 ai != free_anonymous_list_of.end(); ++ai)
      {
	os << "  [" << ai->first << "] ";
	ai->second.dump_free_list(os) << std::endl;
      }
    os << "Free list:\n";
    dump_free_list(os);
    os << std::endl;
    return os;
  }

  void
  bdd_dict::assert_emptiness() const
  {
    bool fail = false;

    bool var_seen = false;
    bool acc_seen = false;
    bool now_seen = false;
    bool next_seen = false;
    bool refs_seen = false;
    unsigned s = bdd_map.size();
    for (unsigned i = 0; i < s; ++i)
      {
	switch (bdd_map[i].type)
	  {
	  case var:
	    var_seen = true;
	    break;
	  case acc:
	    acc_seen = true;
	    break;
	  case now:
	    now_seen = true;
	    break;
	  case next:
	    next_seen = true;
	    break;
	  case anon:
	    break;
	  }
	refs_seen |= !bdd_map[i].refs.empty();
      }
    if (var_map.empty()
	&& now_map.empty()
	&& acc_map.empty())
      {
	if (var_seen)
	  {
	    std::cerr << "var_map is empty but Var in map" << std::endl;
	    fail = true;
	  }
	if (now_seen)
	  {
	    std::cerr << "now_map is empty but Now in map" << std::endl;
	    fail = true;
	  }
	else if (next_seen)
	  {
	    std::cerr << "Next variable seen (without Now) in map" << std::endl;
	    fail = true;
	  }
	if (acc_seen)
	  {
	    std::cerr << "acc_map is empty but Acc in map" << std::endl;
	    fail = true;
	  }
	if (refs_seen)
	  {
	    std::cerr << "maps are empty but var_refs is not" << std::endl;
	    fail = true;
	  }
	if (!fail)
	  return;
      }
    else
      {
	std::cerr << "some maps are not empty" << std::endl;
      }
    dump(std::cerr);
    assert(0);
  }


  bdd_dict::anon_free_list::anon_free_list(bdd_dict* d)
    : dict_(d)
  {
  }

  int
  bdd_dict::anon_free_list::extend(int n)
  {
    assert(dict_);
    int b = dict_->allocate_variables(n);

    free_anonymous_list_of_type::iterator i;
    for (i = dict_->free_anonymous_list_of.begin();
	 i != dict_->free_anonymous_list_of.end(); ++i)
      if (&i->second != this)
	i->second.insert(b, n);
    return b;
  }

}
