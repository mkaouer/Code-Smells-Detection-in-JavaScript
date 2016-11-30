// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita.
// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#ifndef SPOT_TGBA_TGBAEXPLICIT_HH
# define SPOT_TGBA_TGBAEXPLICIT_HH

#include <sstream>
#include <list>

#include "tgba.hh"
#include "sba.hh"
#include "tgba/formula2bdd.hh"
#include "misc/hash.hh"
#include "misc/bddop.hh"
#include "ltlast/formula.hh"
#include "ltlvisit/tostring.hh"

namespace spot
{
  // How to destroy the label of a state.
  template<typename T>
  struct destroy_key
  {
    void destroy(T t)
    {
      (void) t;
    }
  };

  template<>
  struct destroy_key<const ltl::formula*>
  {
    void destroy(const ltl::formula* t)
    {
      t->destroy();
    }
  };

  /// States used by spot::explicit_graph implementation
  /// \ingroup tgba_representation
  template<typename Label, typename label_hash>
  class state_explicit: public spot::state
  {
  public:
    state_explicit()
    {
    }

    state_explicit(const Label& l):
      label_(l)
    {
    }

    virtual ~state_explicit()
    {
    }

    virtual void destroy() const
    {
    }

    typedef Label label_t;
    typedef label_hash label_hash_t;

    struct transition
    {
      bdd condition;
      bdd acceptance_conditions;
      const state_explicit<Label, label_hash>* dest;
    };

    typedef std::list<transition> transitions_t;
    transitions_t successors;

    const Label& label() const
    {
      return label_;
    }

    bool empty() const
    {
      return successors.empty();
    }

    virtual int compare(const state* other) const
    {
      const state_explicit<Label, label_hash>* s =
	down_cast<const state_explicit<Label, label_hash>*>(other);
      assert (s);

      // Do not simply return "o - this", it might not fit in an int.
      if (s < this)
	return -1;
      if (s > this)
	return 1;
      return 0;
    }

    virtual size_t hash() const
    {
      return
	reinterpret_cast<const char*>(this) - static_cast<const char*>(0);
    }

    virtual state_explicit<Label, label_hash>*
    clone() const
    {
      return const_cast<state_explicit<Label, label_hash>*>(this);
    }

  protected:
    Label label_;
  };

  /// States labeled by an int
  /// \ingroup tgba_representation
  class state_explicit_number:
    public state_explicit<int, identity_hash<int> >
  {
  public:
    state_explicit_number()
      : state_explicit<int, identity_hash<int> >()
    {
    }

    state_explicit_number(int label)
      : state_explicit<int, identity_hash<int> >(label)
    {
    }

    static const int default_val;
  };

  /// States labeled by a string
  /// \ingroup tgba_representation
  class state_explicit_string:
    public state_explicit<std::string, string_hash>
  {
  public:
    state_explicit_string():
      state_explicit<std::string, string_hash>()
    {
    }

    state_explicit_string(const std::string& label)
      : state_explicit<std::string, string_hash>(label)
    {
    }

    static const std::string default_val;
  };

  /// States labeled by a formula
  /// \ingroup tgba_representation
  class state_explicit_formula:
    public state_explicit<const ltl::formula*, ltl::formula_ptr_hash>
  {
  public:
    state_explicit_formula():
      state_explicit<const ltl::formula*, ltl::formula_ptr_hash>()
    {
    }

    state_explicit_formula(const ltl::formula* label)
      : state_explicit<const ltl::formula*, ltl::formula_ptr_hash>(label)
    {
    }

    static const ltl::formula* default_val;
  };

  /// Successor iterators used by spot::tgba_explicit.
  /// \ingroup tgba_representation
  template<typename State>
  class tgba_explicit_succ_iterator: public tgba_succ_iterator
  {
  public:
    tgba_explicit_succ_iterator(const State* start,
				bdd all_acc)
      : start_(start),
	all_acceptance_conditions_(all_acc)
    {
    }

    virtual void first()
    {
      it_ = start_->successors.begin();
    }

    virtual void next()
    {
      ++it_;
    }

    virtual bool done() const
    {
      return it_ == start_->successors.end();
    }

    virtual State* current_state() const
    {
      assert(!done());
      const State* res = down_cast<const State*>(it_->dest);
      assert(res);
      return const_cast<State*>(res);
    }

    virtual bdd current_condition() const
    {
      assert(!done());
      return it_->condition;
    }

    virtual bdd current_acceptance_conditions() const
    {
      assert(!done());
      return it_->acceptance_conditions;
    }

    typename State::transitions_t::const_iterator
    get_iterator() const
    {
      return it_;
    }

  private:
    const State* start_;
    typename State::transitions_t::const_iterator it_;
    bdd all_acceptance_conditions_;
  };

  /// Graph implementation for explicit automaton
  /// \ingroup tgba_representation
  template<typename State, typename Type>
  class explicit_graph: public Type
  {
  public:
    typedef typename State::label_t label_t;
    typedef typename State::label_hash_t label_hash_t;
    typedef typename State::transitions_t transitions_t;
    typedef typename State::transition transition;
    typedef State state;
  protected:
    typedef Sgi::hash_map<label_t, State, label_hash_t> ls_map;
    typedef Sgi::hash_map<label_t, State*, label_hash_t> alias_map;
    typedef Sgi::hash_map<const State*, label_t, ptr_hash<State> > sl_map;

  public:

    explicit_graph(bdd_dict* dict)
      : ls_(),
	sl_(),
	init_(0),
	dict_(dict),
	all_acceptance_conditions_(bddfalse),
	all_acceptance_conditions_computed_(false),
	neg_acceptance_conditions_(bddtrue)
    {
    }

    State* add_default_init()
    {
      return add_state(State::default_val);
    }

    size_t num_states() const
    {
      return sl_.size();
    }

    transition*
    create_transition(State* source, const State* dest)
    {
      transition t;

      t.dest = dest;
      t.condition = bddtrue;
      t.acceptance_conditions = bddfalse;

      typename transitions_t::iterator i =
	source->successors.insert(source->successors.end(), t);

      return &*i;
    }

    transition*
    create_transition(const label_t& source, const label_t& dest)
    {
      // It's important that the source be created before the
      // destination, so the first source encountered becomes the
      // default initial state.
      State* s = add_state(source);
      return create_transition(s, add_state(dest));
    }

    transition*
    get_transition(const tgba_explicit_succ_iterator<State>* si)
    {
      return const_cast<transition*>(&(*(si->get_iterator())));
    }

    void add_condition(transition* t, const ltl::formula* f)
    {
      t->condition &= formula_to_bdd(f, dict_, this);
      f->destroy();
    }

    /// This assumes that all variables in \a f are known from dict.
    void add_conditions(transition* t, bdd f)
    {
      dict_->register_propositions(f, this);
      t->condition &= f;
    }

    bool has_acceptance_condition(const ltl::formula* f) const
    {
      return dict_->is_registered_acceptance_variable(f, this);
    }

    //old tgba explicit labelled interface
    bool has_state(const label_t& name)
    {
      return ls_.find(name) != ls_.end();
    }

    const label_t& get_label(const State* s) const
    {
      typename sl_map::const_iterator i = sl_.find(s);
      assert(i != sl_.end());
      return i->second;
    }

    const label_t& get_label(const spot::state* s) const
    {
      const State* se = down_cast<const State*>(s);
      assert(se);
      return get_label(se);
    }

    void
    complement_all_acceptance_conditions()
    {
      bdd all = this->all_acceptance_conditions();
      typename ls_map::iterator i;
      for (i = ls_.begin(); i != ls_.end(); ++i)
	{
	  typename transitions_t::iterator i2;
	  for (i2 = i->second.successors.begin();
	       i2 != i->second.successors.end(); ++i2)
	    i2->acceptance_conditions = all - i2->acceptance_conditions;
	}
    }

    void
    merge_transitions()
    {
      typedef typename transitions_t::iterator trans_t;
      typedef std::map<int, trans_t> acc_map;
      typedef Sgi::hash_map<const spot::state*, acc_map,
			    ptr_hash<spot::state> > dest_map;

      typename ls_map::iterator i;
      for (i = ls_.begin(); i != ls_.end(); ++i)
	{
	  const spot::state* last_dest = 0;
	  dest_map dm;
	  typename dest_map::iterator dmi = dm.end();
	  typename transitions_t::iterator t1 = i->second.successors.begin();

	  // Loop over all outgoing transitions (cond,acc,dest), and
	  // store them into dest_map[dest][acc] so that we can merge
	  // conditions.
	  while (t1 != i->second.successors.end())
	    {
	      const spot::state* dest = t1->dest;
	      if (dest != last_dest)
		{
		  last_dest = dest;
		  dmi = dm.find(dest);
		  if (dmi == dm.end())
		    dmi = dm.insert(std::make_pair(dest, acc_map())).first;
		}
	      int acc = t1->acceptance_conditions.id();
	      typename acc_map::iterator it = dmi->second.find(acc);
	      if (it == dmi->second.end())
		{
		  dmi->second[acc] = t1;
		  ++t1;
		}
	      else
		{
		  it->second->condition |= t1->condition;
		  t1 = i->second.successors.erase(t1);
		}
	    }
	}
    }

    /// Return the state_explicit for \a name, creating the state if
    /// it does not exist.
    State* add_state(const label_t& name)
    {
      typename ls_map::iterator i = ls_.find(name);
      if (i == ls_.end())
	{
	  typename alias_map::iterator j = alias_.find(name);
	  if (j != alias_.end())
	    return j->second;

	  State s(name);
	  State* res =
	    &(ls_.insert(std::make_pair(name, State(name))).first->second);
	  sl_[res] = name;
	  // The first state we add is the initial state.
	  // It can also be overridden with set_init_state().
	  if (!init_)
	    init_ = res;
	  return res;
	}
      return &(i->second);
    }

    State*
    set_init_state(const label_t& state)
    {
      State* s = add_state(state);
      init_ = s;
      return s;
    }

    // tgba interface
    virtual ~explicit_graph()
    {
      typename ls_map::iterator i = ls_.begin();

      while (i != ls_.end())
      {
	label_t s = i->first;
	i->second.destroy();
	++i;
	destroy_key<label_t> dest;
	dest.destroy(s);
      }

      this->dict_->unregister_all_my_variables(this);
      // These have already been destroyed by subclasses.
      // Prevent destroying by tgba::~tgba.
      this->last_support_conditions_input_ = 0;
      this->last_support_variables_input_ = 0;
    }

    virtual State* get_init_state() const
    {
      if (!init_)
	const_cast<explicit_graph<State, Type>*>(this)->add_default_init();

      return init_;
    }

    virtual tgba_explicit_succ_iterator<State>*
    succ_iter(const spot::state* state,
	      const spot::state* global_state = 0,
	      const tgba* global_automaton = 0) const
    {
      const State* s = down_cast<const State*>(state);
      assert(s);

      (void) global_state;
      (void) global_automaton;

      return
	new tgba_explicit_succ_iterator<State>(s,
					       this
					       ->all_acceptance_conditions());
    }


    typedef std::string (*to_string_func_t)(const label_t& t);

    void set_to_string_func(to_string_func_t f)
    {
      to_string_func_ = f;
    }

    to_string_func_t get_to_string_func() const
    {
      return to_string_func_;
    }

    virtual std::string format_state(const spot::state* state) const
    {
      const State* se = down_cast<const State*>(state);
      assert(se);
      typename sl_map::const_iterator i = sl_.find(se);
      assert(i != sl_.end());
      assert(to_string_func_);
      return to_string_func_(i->second);
    }

    /// Create an alias for a state.  Any reference to \a alias_name
    /// will act as a reference to \a real_name.
    void add_state_alias(const label_t& alias, const label_t& real)
    {
      alias_[alias] = add_state(real);
    }


    /// \brief Copy the acceptance conditions of a tgba.
    ///
    /// If used, this function should be called before creating any
    /// transition.
    void copy_acceptance_conditions_of(const tgba *a)
    {
      assert(neg_acceptance_conditions_ == bddtrue);
      assert(all_acceptance_conditions_computed_ == false);
      bdd f = a->neg_acceptance_conditions();
      this->dict_->register_acceptance_variables(f, this);
      neg_acceptance_conditions_ = f;
    }


    /// Acceptance conditions handling
    void set_acceptance_conditions(bdd acc)
    {
      assert(neg_acceptance_conditions_ == bddtrue);
      assert(all_acceptance_conditions_computed_ == false);

      this->dict_->register_acceptance_variables(bdd_support(acc), this);
      neg_acceptance_conditions_ = compute_neg_acceptance_conditions(acc);
      all_acceptance_conditions_computed_ = true;
      all_acceptance_conditions_ = acc;
    }

    void add_acceptance_condition(transition* t, const ltl::formula* f)
    {
      bdd c = get_acceptance_condition(f);
      t->acceptance_conditions |= c;
    }

    /// This assumes that all acceptance conditions in \a f are known from
    /// dict.
    void add_acceptance_conditions(transition* t, bdd f)
    {
      bdd sup = bdd_support(f);
      this->dict_->register_acceptance_variables(sup, this);
      while (sup != bddtrue)
	{
	  neg_acceptance_conditions_ &= bdd_nithvar(bdd_var(sup));
	  sup = bdd_high(sup);
	}
      t->acceptance_conditions |= f;
    }

    virtual bdd all_acceptance_conditions() const
    {
      if (!all_acceptance_conditions_computed_)
      {
	all_acceptance_conditions_ =
	  compute_all_acceptance_conditions(neg_acceptance_conditions_);
	all_acceptance_conditions_computed_ = true;
      }
      return all_acceptance_conditions_;
    }

    virtual bdd_dict* get_dict() const
    {
      return this->dict_;
    }

    virtual bdd neg_acceptance_conditions() const
    {
      return neg_acceptance_conditions_;
    }

    void
    declare_acceptance_condition(const ltl::formula* f)
    {
      int v = this->dict_->register_acceptance_variable(f, this);
      f->destroy();
      bdd neg = bdd_nithvar(v);
      neg_acceptance_conditions_ &= neg;

      // Append neg to all acceptance conditions.

      // FIXME: Declaring acceptance conditions after the automaton
      // has been constructed is very slow because we traverse the
      // entire automaton for each new acceptance condition.  It would
      // be better to fix the automaton in a single pass after all
      // acceptance conditions have been declared.
      typename ls_map::iterator i;
      for (i = this->ls_.begin(); i != this->ls_.end(); ++i)
	{
	  typename transitions_t::iterator i2;
	  for (i2 = i->second.successors.begin();
	       i2 != i->second.successors.end(); ++i2)
	    i2->acceptance_conditions &= neg;
	}

      all_acceptance_conditions_computed_ = false;
    }

    bdd get_acceptance_condition(const ltl::formula* f)
    {
      bdd_dict::fv_map::iterator i = this->dict_->acc_map.find(f);
      assert(this->has_acceptance_condition(f));
      f->destroy();
      bdd v = bdd_ithvar(i->second);
      v &= bdd_exist(neg_acceptance_conditions_, v);
      return v;
    }

  protected:

    virtual bdd compute_support_conditions(const spot::state* in) const
    {
      const State* s = down_cast<const State*>(in);
      assert(s);
      const transitions_t& st = s->successors;

      bdd res = bddfalse;

      typename transitions_t::const_iterator i;
      for (i = st.begin(); i != st.end(); ++i)
	res |= i->condition;

      return res;
    }

    virtual bdd compute_support_variables(const spot::state* in) const
    {
      const State* s = down_cast<const State*>(in);
      assert(s);
      const transitions_t& st = s->successors;

      bdd res = bddtrue;

      typename transitions_t::const_iterator i;
      for (i = st.begin(); i != st.end(); ++i)
	res &= bdd_support(i->condition);

      return res;
    }

    ls_map ls_;
    alias_map alias_;
    sl_map sl_;
    State* init_;

    bdd_dict* dict_;

    mutable bdd all_acceptance_conditions_;
    mutable bool all_acceptance_conditions_computed_;
    bdd neg_acceptance_conditions_;
    to_string_func_t to_string_func_;
  };

  template <typename State>
  class tgba_explicit: public explicit_graph<State, tgba>
  {
  public:
    tgba_explicit(bdd_dict* dict): explicit_graph<State, tgba>(dict)
    {
    }

    virtual ~tgba_explicit()
    {
    }

  private:
    // Disallow copy.
    tgba_explicit(const tgba_explicit<State>& other);
    tgba_explicit& operator=(const tgba_explicit& other);
  };

  template <typename State>
  class sba_explicit: public explicit_graph<State, sba>
  {
  public:
    sba_explicit(bdd_dict* dict): explicit_graph<State, sba>(dict)
    {
    }

    virtual ~sba_explicit()
    {
    }

    virtual bool state_is_accepting(const spot::state* s) const
    {
      const State* st = down_cast<const State*>(s);
      // Assume that an accepting state has only accepting output transitions
      // So we need only to check one to decide
      if (st->successors.empty())
	return false;
      return (st->successors.front().acceptance_conditions
	      == this->all_acceptance_conditions());
    }

  private:
    // Disallow copy.
    sba_explicit(const sba_explicit<State>& other);
    sba_explicit& operator=(const sba_explicit& other);
  };


  // It is tempting to write
  //
  // template<template<typename T>class graph, typename Type>
  // class explicit_conf: public graph<T>
  //
  // to simplify the typedefs at the end of the file, however swig
  // cannot parse this syntax.

  /// Configuration of graph automata
  /// \ingroup tgba_representation
  template<class graph, typename Type>
  class explicit_conf: public graph
  {
  public:
    explicit_conf(bdd_dict* d): graph(d)
    {
      this->set_to_string_func(to_string);
    };

    static std::string to_string(const typename Type::label_t& l)
    {
      std::stringstream ss;
      ss << l;
      return ss.str();
    }
  };

  template<class graph>
  class explicit_conf<graph, state_explicit_string>: public graph
  {
  public:
    explicit_conf(bdd_dict* d): graph(d)
    {
      this->set_to_string_func(to_string);
    };

    static std::string to_string(const std::string& l)
    {
      return l;
    }
  };

  template<class graph>
  class explicit_conf<graph, state_explicit_formula>: public graph
  {
  public:
    explicit_conf(bdd_dict* d): graph(d)
    {
      this->set_to_string_func(to_string);
    };

    // Enable UTF8 output for the formulae that label states.
    void enable_utf8()
    {
      this->set_to_string_func(to_utf8_string);
    }

    static std::string to_string(const ltl::formula* const& l)
    {
      return ltl::to_string(l);
    }

    static std::string to_utf8_string(const ltl::formula* const& l)
    {
      return ltl::to_utf8_string(l);
    }
  };


  // Typedefs for tgba
  typedef explicit_conf<tgba_explicit<state_explicit_string>,
			state_explicit_string> tgba_explicit_string;
  typedef explicit_conf<tgba_explicit<state_explicit_formula>,
			state_explicit_formula> tgba_explicit_formula;
  typedef explicit_conf<tgba_explicit<state_explicit_number>,
			state_explicit_number> tgba_explicit_number;

  // Typedefs for sba
  typedef explicit_conf<sba_explicit<state_explicit_string>,
			state_explicit_string> sba_explicit_string;
  typedef explicit_conf<sba_explicit<state_explicit_formula>,
			state_explicit_formula> sba_explicit_formula;
  typedef explicit_conf<sba_explicit<state_explicit_number>,
			state_explicit_number> sba_explicit_number;
}

#endif // SPOT_TGBA_TGBAEXPLICIT_HH
