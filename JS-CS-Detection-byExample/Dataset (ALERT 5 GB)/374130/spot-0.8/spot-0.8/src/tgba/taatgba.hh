// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement
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

#ifndef SPOT_TGBA_TAATGBA_HH
# define SPOT_TGBA_TAATGBA_HH

#include <set>
#include <iosfwd>
#include <vector>
#include "misc/hash.hh"
#include "ltlast/formula.hh"
#include "bdddict.hh"
#include "tgba.hh"

namespace spot
{
  /// \brief A self-loop Transition-based Alternating Automaton (TAA)
  /// which is seen as a TGBA (abstract class, see below).
  class taa_tgba : public tgba
  {
  public:
    taa_tgba(bdd_dict* dict);

    struct transition;
    typedef std::list<transition*> state;
    typedef std::set<state*> state_set;

    /// Explicit transitions.
    struct transition
    {
      bdd condition;
      bdd acceptance_conditions;
      const state_set* dst;
    };

    void add_condition(transition* t, const ltl::formula* f);

    /// TGBA interface.
    virtual ~taa_tgba();
    virtual spot::state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const spot::state* local_state,
	      const spot::state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const spot::state* state) const = 0;
    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;

    typedef std::vector<taa_tgba::state_set*> ss_vec;

    bdd_dict* dict_;
    mutable bdd all_acceptance_conditions_;
    mutable bool all_acceptance_conditions_computed_;
    bdd neg_acceptance_conditions_;
    taa_tgba::state_set* init_;
    ss_vec state_set_vec_;

  private:
    // Disallow copy.
    taa_tgba(const taa_tgba& other);
    taa_tgba& operator=(const taa_tgba& other);
  };

  /// Set of states deriving from spot::state.
  class state_set : public spot::state
  {
  public:
    state_set(const taa_tgba::state_set* s, bool delete_me = false)
      : s_(s), delete_me_(delete_me)
    {
    }

    virtual int compare(const spot::state*) const;
    virtual size_t hash() const;
    virtual state_set* clone() const;

    virtual ~state_set()
    {
      if (delete_me_)
	delete s_;
    }

    const taa_tgba::state_set* get_state() const;
  private:
    const taa_tgba::state_set* s_;
    bool delete_me_;
  };

  class taa_succ_iterator : public tgba_succ_iterator
  {
  public:
    taa_succ_iterator(const taa_tgba::state_set* s, bdd all_acc);
    virtual ~taa_succ_iterator();

    virtual void first();
    virtual void next();
    virtual bool done() const;

    virtual state_set* current_state() const;
    virtual bdd current_condition() const;
    virtual bdd current_acceptance_conditions() const;

  private:
    /// Those typedefs are used to generate all possible successors in
    /// the constructor using a cartesian product.
    typedef taa_tgba::state::const_iterator iterator;
    typedef std::pair<iterator, iterator> iterator_pair;
    typedef std::vector<iterator_pair> bounds_t;
    typedef Sgi::hash_map<
      const spot::state_set*, std::vector<taa_tgba::transition*>,
      state_ptr_hash, state_ptr_equal> seen_map;

    struct distance_sort :
      public std::binary_function<const iterator_pair&,
				  const iterator_pair&, bool>
    {
      bool
      operator()(const iterator_pair& lhs, const iterator_pair& rhs) const
      {
	return std::distance(lhs.first, lhs.second) <
	       std::distance(rhs.first, rhs.second);
      }
    };

    std::vector<taa_tgba::transition*>::const_iterator i_;
    std::vector<taa_tgba::transition*> succ_;
    bdd all_acceptance_conditions_;
    seen_map seen_;
  };

  /// A taa_tgba instance with states labeled by a given type.
  /// Still an abstract class, see below.
  template<typename label, typename label_hash>
  class taa_tgba_labelled : public taa_tgba
  {
  public:
    taa_tgba_labelled(bdd_dict* dict) : taa_tgba(dict) {};

    void set_init_state(const label& s)
    {
      std::vector<label> v(1);
      v[0] = s;
      set_init_state(v);
    }
    void set_init_state(const std::vector<label>& s)
    {
      init_ = add_state_set(s);
    }

    transition*
    create_transition(const label& s,
		      const std::vector<label>& d)
    {
      state* src = add_state(s);
      state_set* dst = add_state_set(d);
      transition* t = new transition;
      t->dst = dst;
      t->condition = bddtrue;
      t->acceptance_conditions = bddfalse;
      src->push_back(t);
      return t;
    }
    transition*
    create_transition(const label& s, const label& d)
    {
      std::vector<std::string> vec;
      vec.push_back(d);
      return create_transition(s, vec);
    }

    void add_acceptance_condition(transition* t, const ltl::formula* f)
    {
      if (dict_->acc_map.find(f) == dict_->acc_map.end())
      {
	int v = dict_->register_acceptance_variable(f, this);
	bdd neg = bdd_nithvar(v);
	neg_acceptance_conditions_ &= neg;

	// Append neg to all acceptance conditions.
	typename ns_map::iterator i;
	for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
	{
	  taa_tgba::state::iterator i2;
	  for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	    (*i2)->acceptance_conditions &= neg;
	}

	all_acceptance_conditions_computed_ = false;
      }

      bdd_dict::fv_map::iterator i = dict_->acc_map.find(f);
      assert(i != dict_->acc_map.end());
      f->destroy();
      bdd v = bdd_ithvar(i->second);
      t->acceptance_conditions |= v & bdd_exist(neg_acceptance_conditions_, v);
    }

    /// \brief Format the state as a string for printing.
    ///
    /// If state is a spot::state_set of only one element, then the
    /// string corresponding to state->get_state() is returned.
    ///
    /// Otherwise a string composed of each string corresponding to
    /// each state->get_state() in the spot::state_set is returned,
    /// e.g. like {string_1,...,string_n}.
    virtual std::string format_state(const spot::state* s) const
    {
      const spot::state_set* se = down_cast<const spot::state_set*>(s);
      assert(se);
      const state_set* ss = se->get_state();
      return format_state_set(ss);
    }

    /// \brief Output a TAA in a stream.
    void output(std::ostream& os) const
    {
      typename ns_map::const_iterator i;
      for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
      {
	taa_tgba::state::const_iterator i2;
	os << "State: " << label_to_string(i->first) << std::endl;
	for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	{
	  os << " " << format_state_set((*i2)->dst)
	     << ", C:" << (*i2)->condition
	     << ", A:" << (*i2)->acceptance_conditions << std::endl;
	}
      }
    }

  protected:
    typedef label label_t;

    typedef Sgi::hash_map<
      const label, taa_tgba::state*, label_hash
    > ns_map;
    typedef Sgi::hash_map<
      const taa_tgba::state*, label, ptr_hash<taa_tgba::state>
    > sn_map;

    ns_map name_state_map_;
    sn_map state_name_map_;

    /// \brief Return a label as a string.
    virtual std::string label_to_string(const label_t& lbl) const = 0;

    /// \brief Clone the label if necessary to assure it is owned by
    /// this, avoiding memory issues when label is a pointer.
    virtual label_t clone_if(const label_t& lbl) const = 0;

  private:
    /// \brief Return the taa_tgba::state for \a name, creating it
    /// when it does not exist already.
    taa_tgba::state* add_state(const label& name)
    {
      typename ns_map::iterator i = name_state_map_.find(name);
      if (i == name_state_map_.end())
      {
	const label& name_ = clone_if(name);
	taa_tgba::state* s = new taa_tgba::state;
	name_state_map_[name_] = s;
	state_name_map_[s] = name_;
	return s;
      }
      return i->second;
    }

    /// \brief Return the taa::state_set for \a names.
    taa_tgba::state_set* add_state_set(const std::vector<label>& names)
    {
      state_set* ss = new state_set;
      for (unsigned i = 0; i < names.size(); ++i)
	ss->insert(add_state(names[i]));
      state_set_vec_.push_back(ss);
      return ss;
    }

    std::string format_state_set(const taa_tgba::state_set* ss) const
    {
      state_set::const_iterator i1 = ss->begin();
      typename sn_map::const_iterator i2;
      if (ss->empty())
	return std::string("{}");
      if (ss->size() == 1)
      {
	i2 = state_name_map_.find(*i1);
	assert(i2 != state_name_map_.end());
	return "{" + label_to_string(i2->second) + "}";
      }
      else
      {
	std::string res("{");
	while (i1 != ss->end())
	{
	  i2 = state_name_map_.find(*i1++);
	  assert(i2 != state_name_map_.end());
	  res += label_to_string(i2->second);
	  res += ",";
	}
	res[res.size() - 1] = '}';
	return res;
      }
    }
  };

  class taa_tgba_string :
    public taa_tgba_labelled<std::string, string_hash>
  {
  public:
    taa_tgba_string(bdd_dict* dict) :
      taa_tgba_labelled<std::string, string_hash>(dict) {};
    ~taa_tgba_string();
  protected:
    virtual std::string label_to_string(const std::string& label) const;
    virtual std::string clone_if(const std::string& label) const;
  };

  class taa_tgba_formula :
    public taa_tgba_labelled<const ltl::formula*, ltl::formula_ptr_hash>
  {
  public:
    taa_tgba_formula(bdd_dict* dict) :
      taa_tgba_labelled<const ltl::formula*, ltl::formula_ptr_hash>(dict) {};
    ~taa_tgba_formula();
  protected:
    virtual std::string label_to_string(const label_t& label) const;
    virtual ltl::formula* clone_if(const label_t& label) const;
  };
}

#endif // SPOT_TGBA_TAATGBA_HH
