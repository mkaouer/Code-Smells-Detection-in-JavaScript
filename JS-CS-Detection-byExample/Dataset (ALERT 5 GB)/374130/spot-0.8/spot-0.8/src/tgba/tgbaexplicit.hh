// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#ifndef SPOT_TGBA_TGBAEXPLICIT_HH
# define SPOT_TGBA_TGBAEXPLICIT_HH

#include "misc/hash.hh"
#include <list>
#include "tgba.hh"
#include "ltlast/formula.hh"
#include <cassert>

namespace spot
{
  // Forward declarations.  See below.
  class state_explicit;
  class tgba_explicit_succ_iterator;
  class tgba_explicit;

  /// States used by spot::tgba_explicit.
  /// \ingroup tgba_representation
  class state_explicit: public spot::state
  {
  public:
    state_explicit()
    {
    }

    virtual int compare(const spot::state* other) const;
    virtual size_t hash() const;

    virtual state_explicit* clone() const
    {
      return const_cast<state_explicit*>(this);
    }

    bool empty() const
    {
      return successors.empty();
    }

    virtual
    void destroy() const
    {
    }

    /// Explicit transitions.
    struct transition
    {
      bdd condition;
      bdd acceptance_conditions;
      const state_explicit* dest;
    };

    typedef std::list<transition> transitions_t;
    transitions_t successors;
  private:
    state_explicit(const state_explicit& other);
    state_explicit& operator=(const state_explicit& other);

    virtual ~state_explicit()
    {
    }
    friend class tgba_explicit_string;
    friend class tgba_explicit_formula;
    friend class tgba_explicit_number;
  };


  /// Explicit representation of a spot::tgba.
  /// \ingroup tgba_representation
  class tgba_explicit: public tgba
  {
  public:
    typedef state_explicit state;
    typedef state_explicit::transition transition;

    tgba_explicit(bdd_dict* dict);

    /// Add a default initial state.
    virtual state* add_default_init() = 0;

    transition*
    create_transition(state* source, const state* dest);

    void add_condition(transition* t, const ltl::formula* f);
    /// This assumes that all variables in \a f are known from dict.
    void add_conditions(transition* t, bdd f);

    /// \brief Copy the acceptance conditions of a tgba.
    ///
    /// If used, this function should be called before creating any
    /// transition.
    void copy_acceptance_conditions_of(const tgba *a);

    /// The the acceptance conditions.
    void set_acceptance_conditions(bdd acc);

    bool has_acceptance_condition(const ltl::formula* f) const;
    void add_acceptance_condition(transition* t, const ltl::formula* f);
    /// This assumes that all acceptance conditions in \a f are known from dict.
    void add_acceptance_conditions(transition* t, bdd f);

    // tgba interface
    virtual ~tgba_explicit();
    virtual spot::state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const spot::state* local_state,
	      const spot::state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    virtual std::string format_state(const spot::state* s) const = 0;

  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;

    bdd get_acceptance_condition(const ltl::formula* f);

    bdd_dict* dict_;
    state_explicit* init_;
    mutable bdd all_acceptance_conditions_;
    bdd neg_acceptance_conditions_;
    mutable bool all_acceptance_conditions_computed_;

  private:
    // Disallow copy.
    tgba_explicit(const tgba_explicit& other);
    tgba_explicit& operator=(const tgba_explicit& other);
  };



  /// Successor iterators used by spot::tgba_explicit.
  /// \ingroup tgba_representation
  class tgba_explicit_succ_iterator: public tgba_succ_iterator
  {
  public:
    tgba_explicit_succ_iterator(const state_explicit::transitions_t* s,
				bdd all_acc);

    virtual void first();
    virtual void next();
    virtual bool done() const;

    virtual state_explicit* current_state() const;
    virtual bdd current_condition() const;
    virtual bdd current_acceptance_conditions() const;

  private:
    const state_explicit::transitions_t* s_;
    state_explicit::transitions_t::const_iterator i_;
    bdd all_acceptance_conditions_;
  };

  /// A tgba_explicit instance with states labeled by a given type.
  template<typename label, typename label_hash>
  class tgba_explicit_labelled: public tgba_explicit
  {
  protected:
    typedef label label_t;
    typedef Sgi::hash_map<label, state_explicit*,
			  label_hash> ns_map;
    typedef Sgi::hash_map<const state_explicit*, label,
			  ptr_hash<state_explicit> > sn_map;
    ns_map name_state_map_;
    sn_map state_name_map_;
  public:
    tgba_explicit_labelled(bdd_dict* dict) : tgba_explicit(dict) {};

    bool has_state(const label& name)
    {
      return name_state_map_.find(name) != name_state_map_.end();
    }

    const label& get_label(const state_explicit* s) const
    {
      typename sn_map::const_iterator i = state_name_map_.find(s);
      assert(i != state_name_map_.end());
      return i->second;
    }

    const label& get_label(const spot::state* s) const
    {
      const state_explicit* se = down_cast<const state_explicit*>(s);
      assert(se);
      return get_label(se);
    }

    /// Return the state_explicit for \a name, creating the state if
    /// it does not exist.
    state* add_state(const label& name)
    {
      typename ns_map::iterator i = name_state_map_.find(name);
      if (i == name_state_map_.end())
	{
	  state_explicit* s = new state_explicit;
	  name_state_map_[name] = s;
	  state_name_map_[s] = name;

	  // The first state we add is the inititial state.
	  // It can also be overridden with set_init_state().
	  if (!init_)
	    init_ = s;

	  return s;
	}
      return i->second;
    }

    state*
    set_init_state(const label& state)
    {
      state_explicit* s = add_state(state);
      init_ = s;
      return s;
    }


    transition*
    create_transition(state* source, const state* dest)
    {
      return tgba_explicit::create_transition(source, dest);
    }

    transition*
    create_transition(const label& source, const label& dest)
    {
      // It's important that the source be created before the
      // destination, so the first source encountered becomes the
      // default initial state.
      state* s = add_state(source);
      return tgba_explicit::create_transition(s, add_state(dest));
    }

    void
    complement_all_acceptance_conditions()
    {
      bdd all = all_acceptance_conditions();
      typename ns_map::iterator i;
      for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
	{
	  state_explicit::transitions_t::iterator i2;
	  for (i2 = i->second->successors.begin();
	       i2 != i->second->successors.end(); ++i2)
	    {
	      i2->acceptance_conditions = all - i2->acceptance_conditions;
	    }
	}
    }

    void
    declare_acceptance_condition(const ltl::formula* f)
    {
      int v = dict_->register_acceptance_variable(f, this);
      f->destroy();
      bdd neg = bdd_nithvar(v);
      neg_acceptance_conditions_ &= neg;

      // Append neg to all acceptance conditions.
      typename ns_map::iterator i;
      for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
	{
	  state_explicit::transitions_t::iterator i2;
	  for (i2 = i->second->successors.begin();
	       i2 != i->second->successors.end(); ++i2)
	    i2->acceptance_conditions &= neg;
	}

      all_acceptance_conditions_computed_ = false;
    }


    void
    merge_transitions()
    {
      typename ns_map::iterator i;
      for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
	{
	  state_explicit::transitions_t::iterator t1;
	  for (t1 = i->second->successors.begin();
	       t1 != i->second->successors.end(); ++t1)
	    {
	      bdd acc = t1->acceptance_conditions;
	      const state* dest = t1->dest;

	      // Find another transition with the same destination and
	      // acceptance conditions.
	      state_explicit::transitions_t::iterator t2 = t1;
	      ++t2;
	      while (t2 != i->second->successors.end())
		{
		  state_explicit::transitions_t::iterator t2copy = t2++;
		  if (t2copy->acceptance_conditions == acc
		      && t2copy->dest == dest)
		    {
		      t1->condition |= t2copy->condition;
		      i->second->successors.erase(t2copy);
		    }
		}
	    }
	}
    }


    virtual
    ~tgba_explicit_labelled()
    {
      // These have already been destroyed by subclasses.
      // Prevent destroying by tgba::~tgba.
      last_support_conditions_input_ = 0;
      last_support_variables_input_ = 0;
    }

  };

#ifndef SWIG
  class tgba_explicit_string:
    public tgba_explicit_labelled<std::string, string_hash>
  {
  public:
    tgba_explicit_string(bdd_dict* dict):
      tgba_explicit_labelled<std::string, string_hash>(dict)
    {};
    virtual ~tgba_explicit_string();
    virtual state* add_default_init();
    virtual std::string format_state(const spot::state* s) const;

    /// Create an alias for a state.  Any reference to \a alias_name
    /// will act as a reference to \a real_name.
    virtual
    void add_state_alias(const std::string& alias_name,
			 const std::string& real_name)
    {
      name_state_map_[alias_name] = add_state(real_name);
    }
  };
#else
  class tgba_explicit_string: public tgba
  {
  };
#endif

#ifndef SWIG
  class tgba_explicit_formula:
    public tgba_explicit_labelled<const ltl::formula*, ltl::formula_ptr_hash>
  {
  public:
    tgba_explicit_formula(bdd_dict* dict):
      tgba_explicit_labelled<const ltl::formula*, ltl::formula_ptr_hash>(dict)
    {};
    virtual ~tgba_explicit_formula();
    virtual state* add_default_init();
    virtual std::string format_state(const spot::state* s) const;
  };
#else
  class tgba_explicit_formula: public tgba
  {
  };
#endif

#ifndef SWIG
  class tgba_explicit_number:
    public tgba_explicit_labelled<int, identity_hash<int> >
  {
  public:
    tgba_explicit_number(bdd_dict* dict):
      tgba_explicit_labelled<int, identity_hash<int> >(dict)
    {};
    virtual ~tgba_explicit_number();
    virtual state* add_default_init();
    virtual std::string format_state(const spot::state* s) const;
  };
#else
  class tgba_explicit_number: public tgba
  {
  };
#endif
}

#endif // SPOT_TGBA_TGBAEXPLICIT_HH
