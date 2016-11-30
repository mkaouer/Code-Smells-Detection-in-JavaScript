// Copyright (C) 2008, 2011 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006, 2007  Laboratoire
// d'Informatique de Paris 6 (LIP6), département Systèmes Répartis
// Coopératifs (SRC), Université Pierre et Marie Curie.
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

#include <cstring>
#include <map>
#include <cassert>
#include <gspnlib.h>
#include "ssp.hh"
#include "misc/bddlt.hh"
#include "misc/hash.hh"
#include <bdd.h>
#include "tgbaalgos/gtec/explscc.hh"
#include "tgbaalgos/gtec/nsheap.hh"

namespace spot
{

  namespace
  {
    static bdd*
    bdd_realloc(bdd* t, int size, int new_size)
    {
      assert(new_size);
      bdd* tmp = new bdd[new_size];

      for (int i = 0; i < size; i++)
	tmp[i] = t[i];

      delete[] t;
      return tmp;
    }

    static bool doublehash;
    static bool pushfront;
  }

  // state_gspn_ssp
  //////////////////////////////////////////////////////////////////////

  class state_gspn_ssp: public state
  {
  public:
    state_gspn_ssp(State left, const state* right)
      : left_(left), right_(right)
    {
    }

    virtual
    ~state_gspn_ssp()
    {
      right_->destroy();
    }

    virtual int
    compare(const state* other) const
    {
      const state_gspn_ssp* o = down_cast<const state_gspn_ssp*>(other);
      assert(o);
      if (o->left() == left())
	return right_->compare(o->right());
      if (o->left() < left())
	return -1;
      else
	return 1;
    }

    virtual size_t
    hash() const
    {
      return ((reinterpret_cast<char*>(left())
	       - static_cast<char*>(0)) << 10) + right_->hash();
    }

    virtual state_gspn_ssp* clone() const
    {
      return new state_gspn_ssp(left(), right()->clone());
    }

    State
    left() const
    {
      return left_;
    }

    const state*
    right() const
    {
      return right_;
    }

  private:
    State left_;
    const state* right_;
  }; // state_gspn_ssp

  // tgba_gspn_ssp_private_
  //////////////////////////////////////////////////////////////////////

  struct tgba_gspn_ssp_private_
  {
    int refs;			// reference count

    bdd_dict* dict;
    typedef std::map<int, AtomicProp> prop_map;
    prop_map prop_dict;

    signed char* all_props;

    size_t prop_count;
    const tgba* operand;

    tgba_gspn_ssp_private_(bdd_dict* dict,
			   const ltl::declarative_environment& env,
			   const tgba* operand)
      : refs(1), dict(dict), all_props(0),
	operand(operand)
    {
      const ltl::declarative_environment::prop_map& p = env.get_prop_map();

      try
	{
	  AtomicProp max_prop = 0;

	  for (ltl::declarative_environment::prop_map::const_iterator i
		 = p.begin(); i != p.end(); ++i)
	    {
	      int var = dict->register_proposition(i->second, this);
	      AtomicProp index;
	      int err = prop_index(i->first.c_str(), &index);
	      if (err)
		throw gspn_exception("prop_index(" + i->first + ")", err);

	      prop_dict[var] = index;

	      max_prop = std::max(max_prop, index);
	    }
	  prop_count = 1 + max_prop;
	  all_props = new signed char[prop_count];
	}
      catch (...)
	{
	  // If an exception occurs during the loop, we need to clean
	  // all BDD variables which have been registered so far.
	  dict->unregister_all_my_variables(this);
	  throw;
	}
    }

    ~tgba_gspn_ssp_private_()
    {
      dict->unregister_all_my_variables(this);
      delete[] all_props;
    }
  };


  // tgba_succ_iterator_gspn_ssp
  //////////////////////////////////////////////////////////////////////

  class tgba_succ_iterator_gspn_ssp: public tgba_succ_iterator
  {
  public:
    tgba_succ_iterator_gspn_ssp(Succ_* succ_tgba,
				size_t size_tgba,
				bdd* bdd_array,
				state** state_array,
				size_t size_states,
				Props_* prop,
				int size_prop)
      : successors_(succ_tgba),
	size_succ_(size_tgba),
	current_succ_(0),
	bdd_array_(bdd_array),
	state_array_(state_array),
	size_states_(size_states),
	props_(prop),
	size_prop_(size_prop)
    {
    }

    virtual
    ~tgba_succ_iterator_gspn_ssp()
    {

      for (size_t i = 0; i < size_states_; i++)
	state_array_[i]->destroy();

      delete[] bdd_array_;
      free(state_array_);

      if (props_)
	{
	  for (int i = 0; i < size_prop_; i++)
	    free(props_[i].arc);
	  free(props_);
	}

      if (size_succ_ != 0)
	succ_free(successors_);

    }

    virtual void
    first()
    {
      if (!successors_)
	return;
      current_succ_=0;
    }

    virtual void
    next()
    {
      ++current_succ_;
    }

    virtual bool
    done() const
    {
      return current_succ_ + 1 > size_succ_;
    }

    virtual state*
    current_state() const
    {
      state_gspn_ssp* s =
	new state_gspn_ssp(successors_[current_succ_].succ_,
			   (state_array_[successors_[current_succ_]
					 .arc->curr_state])->clone());
      return s;
    }

    virtual bdd
    current_condition() const
    {
      return bddtrue;
    }

    virtual bdd
    current_acceptance_conditions() const
    {
      // There is no acceptance conditions in GSPN systems, so we just
      // return those from operand_.
      return bdd_array_[successors_[current_succ_].arc->curr_acc_conds];
    }
  private:

    // All successors of STATE matching a selection conjunctions from
    // ALL_CONDS.
    Succ_* successors_;		/// array of successors
    size_t size_succ_;		/// size of successors_
    size_t current_succ_;	/// current position in successors_

    bdd * bdd_array_;
    state** state_array_;
    size_t size_states_;
    Props_* props_;
    int size_prop_;
  }; // tgba_succ_iterator_gspn_ssp


  // tgba_gspn_ssp
  //////////////////////////////////////////////////////////////////////

  class tgba_gspn_ssp: public tgba
  {
  public:
    tgba_gspn_ssp(bdd_dict* dict, const ltl::declarative_environment& env,
		  const tgba* operand);
    tgba_gspn_ssp(const tgba_gspn_ssp& other);
    tgba_gspn_ssp& operator=(const tgba_gspn_ssp& other);
    virtual ~tgba_gspn_ssp();
    virtual state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const state* state) const;
    virtual state* project_state(const state* s, const tgba* t) const;
    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;
  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;
  private:
    tgba_gspn_ssp_private_* data_;
  };

  tgba_gspn_ssp::tgba_gspn_ssp(bdd_dict* dict,
			       const ltl::declarative_environment& env,
			       const tgba* operand)
  {
    data_ = new tgba_gspn_ssp_private_(dict, env, operand);
  }

  tgba_gspn_ssp::tgba_gspn_ssp(const tgba_gspn_ssp& other)
    : tgba()
  {
    data_ = other.data_;
    ++data_->refs;
  }

  tgba_gspn_ssp::~tgba_gspn_ssp()
  {
    if (--data_->refs == 0)
      delete data_;
  }

  tgba_gspn_ssp&
  tgba_gspn_ssp::operator=(const tgba_gspn_ssp& other)
  {
    if (&other == this)
      return *this;
    this->~tgba_gspn_ssp();
    new (this) tgba_gspn_ssp(other);
    return *this;
  }

  state* tgba_gspn_ssp::get_init_state() const
  {
    // Use 0 as initial state for the SSP side.  State 0 does not
    // exists, but when passed to succ() it will produce the list
    // of initial states.
    return new state_gspn_ssp(0, data_->operand->get_init_state());
  }

  tgba_succ_iterator*
  tgba_gspn_ssp::succ_iter(const state* state_,
			   const state* global_state,
			   const tgba* global_automaton) const
  {
    const state_gspn_ssp* s = down_cast<const state_gspn_ssp*>(state_);
    assert(s);
    (void) global_state;
    (void) global_automaton;

    bdd all_conds_;
    bdd outside_;
    bdd cond;

    Props_* props_ = 0;
    int nb_arc_props = 0;
    bdd* bdd_array = 0;
    int size_bdd = 0;
    state** state_array = 0;
    size_t size_states = 0;

    tgba_succ_iterator* i = data_->operand->succ_iter(s->right());

    for (i->first(); !i->done(); i->next())
      {
	all_conds_ = i->current_condition();
	outside_ = !all_conds_;

	if (all_conds_ != bddfalse)
	  {
	    props_ = (Props_*) realloc(props_,
				       (nb_arc_props + 1) * sizeof(Props_));

	    props_[nb_arc_props].nb_conj = 0;
	    props_[nb_arc_props].prop = 0;
	    props_[nb_arc_props].arc =
	      (Arc_Ident_*) malloc(sizeof(Arc_Ident_));

	    bdd_array = bdd_realloc(bdd_array, size_bdd, size_bdd + 1);
	    bdd_array[size_bdd] = i->current_acceptance_conditions();
	    props_[nb_arc_props].arc->curr_acc_conds = size_bdd;
	    ++size_bdd;

            state_array =
	      (state**) realloc(state_array,
				(size_states + 1) * sizeof(state*));
	    state_array[size_states] = i->current_state();
	    props_[nb_arc_props].arc->curr_state = size_states;
            ++size_states;

	    while (all_conds_ != bddfalse)
	      {
		cond = bdd_satone(all_conds_);
		cond = bdd_simplify(cond, cond | outside_);
		all_conds_ -= cond;

		props_[nb_arc_props].prop =
		  (signed char **) realloc(props_[nb_arc_props].prop,
					   (props_[nb_arc_props].nb_conj + 1)
					   * sizeof(signed char *));

		props_[nb_arc_props].prop[props_[nb_arc_props].nb_conj]
		  = (signed char*) calloc(data_->prop_count,
					  sizeof(signed char));
		memset(props_[nb_arc_props].prop[props_[nb_arc_props].nb_conj],
		       -1, data_->prop_count);

		while (cond != bddtrue)
		  {
		    int var = bdd_var(cond);
		    bdd high = bdd_high(cond);
		    int res;

		    if (high == bddfalse)
		      {
			cond = bdd_low(cond);
			res = 0;
		      }
		    else
		      {
			cond = high;
			res = 1;
		      }

		    tgba_gspn_ssp_private_::prop_map::iterator k
		      = data_->prop_dict.find(var);

		    if (k != data_->prop_dict.end())
		      props_[nb_arc_props]
			.prop[props_[nb_arc_props].nb_conj][k->second] = res;

		    assert(cond != bddfalse);
		  }
		++props_[nb_arc_props].nb_conj;
	      }
	    ++nb_arc_props;
	  }
      }
    Succ_* succ_tgba_ = 0;
    size_t size_tgba_ = 0;
    int j, conj;

    succ(s->left(), props_, nb_arc_props, &succ_tgba_, &size_tgba_);

    for (j = 0; j < nb_arc_props; j++)
      {
	for (conj = 0; conj < props_[j].nb_conj; conj++)
	  free(props_[j].prop[conj]);
	free(props_[j].prop);
      }

    delete i;
    return new tgba_succ_iterator_gspn_ssp(succ_tgba_, size_tgba_,
					   bdd_array, state_array,
					   size_states, props_,
					   nb_arc_props);
  }

  bdd
  tgba_gspn_ssp::compute_support_conditions(const spot::state* state) const
  {
    (void) state;
    return bddtrue;
  }

  bdd
  tgba_gspn_ssp::compute_support_variables(const spot::state* state) const
  {
    (void) state;
    return bddtrue;
  }

  bdd_dict*
  tgba_gspn_ssp::get_dict() const
  {
    return data_->dict;
  }

  std::string
  tgba_gspn_ssp::format_state(const state* state) const
  {
    const state_gspn_ssp* s = down_cast<const state_gspn_ssp*>(state);
    assert(s);
    char* str;
    State gs = s->left();
    if (gs)
      {
	int err = print_state(gs, &str);
	if (err)
	  throw gspn_exception("print_state()", err);
	// Strip trailing \n...
	unsigned len = strlen(str);
	while (str[--len] == '\n')
	  str[len] = 0;
      }
    else
      {
	str = strdup("-1");
      }

    std::string res(str);
    free(str);
    return res + " * " + data_->operand->format_state(s->right());
  }

  state*
  tgba_gspn_ssp::project_state(const state* s, const tgba* t) const
  {
    const state_gspn_ssp* s2 = down_cast<const state_gspn_ssp*>(s);
    assert(s2);
    if (t == this)
      return s2->clone();
    return data_->operand->project_state(s2->right(), t);
  }

  bdd
  tgba_gspn_ssp::all_acceptance_conditions() const
  {
    // There is no acceptance conditions in GSPN systems, they all
    // come from the operand automaton.
    return data_->operand->all_acceptance_conditions();
  }

  bdd
  tgba_gspn_ssp::neg_acceptance_conditions() const
  {
    // There is no acceptance conditions in GSPN systems, they all
    // come from the operand automaton.
    return data_->operand->neg_acceptance_conditions();
  }

  // gspn_ssp_interface
  //////////////////////////////////////////////////////////////////////

  gspn_ssp_interface::gspn_ssp_interface(int argc, char **argv,
					 bdd_dict* dict,
					 const
					 ltl::declarative_environment& env,
					 bool inclusion,
					 bool doublehash_,
					 bool pushfront_)
    : dict_(dict), env_(env)
  {
    doublehash = doublehash_;
    pushfront = pushfront_;
    if (inclusion)
      inclusion_version();

    int res = initialize(argc, argv);
    if (res)
      throw gspn_exception("initialize()", res);
  }

  gspn_ssp_interface::~gspn_ssp_interface()
  {
    int res = finalize();
    if (res)
      throw gspn_exception("finalize()", res);
  }

  tgba*
  gspn_ssp_interface::automaton(const tgba* operand) const
  {
    return new tgba_gspn_ssp(dict_, env_, operand);
  }


  //////////////////////////////////////////////////////////////////////

  class connected_component_ssp: public explicit_connected_component
  {
  public:
    virtual
    ~connected_component_ssp()
    {
    }

    virtual const state*
    has_state(const state* s) const
    {
      set_type::const_iterator i;

      for (i = states.begin(); i != states.end(); ++i)
	{
	  const state_gspn_ssp* old_state = (const state_gspn_ssp*)(*i);
	  const state_gspn_ssp* new_state = (const state_gspn_ssp*)(s);

	  if ((old_state->right())->compare(new_state->right()) == 0
	      && old_state->left()
	      && new_state->left())
	    if (spot_inclusion(new_state->left(), old_state->left()))
	      {
		if (*i != s)
		  s->destroy();
		return *i;
	      }
	}
      return 0;
    }

    virtual void
    insert(const state* s)
    {
      states.insert(s);
    }

  protected:
    typedef Sgi::hash_set<const state*,
			  state_ptr_hash, state_ptr_equal> set_type;
    set_type states;
  };

  class connected_component_ssp_factory :
    public explicit_connected_component_factory
  {
  public:
    virtual connected_component_ssp*
    build() const
    {
      return new connected_component_ssp();
    }

    /// Get the unique instance of this class.
    static const connected_component_ssp_factory*
    instance()
    {
      static connected_component_ssp_factory f;
      return &f;
    }

  protected:
    virtual
    ~connected_component_ssp_factory()
    {
    }
    /// Construction is forbiden.
    connected_component_ssp_factory()
    {
    }
  };

  //////////////////////////////////////////////////////////////////////

  namespace
  {
    inline void*
    container_(const State s)
    {
      return doublehash ? container(s) : 0;
    }
  }

  class numbered_state_heap_ssp_semi : public numbered_state_heap
  {
  public:
    numbered_state_heap_ssp_semi()
      : numbered_state_heap(), inclusions(0)
    {
    }

    virtual
    ~numbered_state_heap_ssp_semi()
    {
      // Free keys in H.
      hash_type::iterator i = h.begin();
      while (i != h.end())
	{
	  // Advance the iterator before deleting the key.
	  const state* s = i->first;
	  ++i;
	  s->destroy();
	}
    }

    virtual numbered_state_heap::state_index
    find(const state* s) const
    {
      const state_gspn_ssp* s_ = down_cast<const state_gspn_ssp*>(s);
      const void* cont = container_(s_->left());
      contained_map::const_iterator i = contained.find(cont);
      if (i != contained.end())
	{
	  f_map::const_iterator k = i->second.find(s_->right());
	  if (k != i->second.end())
	    {
	      const state_list& l = k->second;

	      state_list::const_iterator j;
	      for (j = l.begin(); j != l.end(); ++j)
		{
		  const state_gspn_ssp* old_state =
		    down_cast<const state_gspn_ssp*>(*j);
		  const state_gspn_ssp* new_state =
		    down_cast<const state_gspn_ssp*>(s);
		  assert(old_state);
		  assert(new_state);

		  if (old_state->left() == new_state->left())
		    break;

		  if (old_state->left()
		      && new_state->left()
		      && spot_inclusion(new_state->left(), old_state->left()))
		    {
		      ++inclusions;
		      break;
		    }
		}
	      if (j != l.end())
		{
		  if (s != *j)
		    {
		      s->destroy();
		      s = *j;
		    }
		}
	      else
		{
		  s = 0;
		}
	    }
	  else
	    {
	      s = 0;
	    }
	}
      else
	{
	  s = 0;
	}

      state_index res;

      if (s == 0)
	{
	  res.first = 0;
	  res.second = 0;
	}
      else
	{
	  hash_type::const_iterator i = h.find(s);
	  assert(i != h.end());
	  assert(s == i->first);
	  res.first = i->first;
	  res.second = i->second;
	}
      return res;
    }

    virtual numbered_state_heap::state_index_p
    find(const state* s)
    {
      const state_gspn_ssp* s_ = down_cast<const state_gspn_ssp*>(s);
      const void* cont = container_(s_->left());
      contained_map::const_iterator i = contained.find(cont);
      if (i != contained.end())
	{
	  f_map::const_iterator k = i->second.find(s_->right());
	  if (k != i->second.end())
	    {
	      const state_list& l = k->second;

	      state_list::const_iterator j;
	      for (j = l.begin(); j != l.end(); ++j)
		{
		  const state_gspn_ssp* old_state =
		    down_cast<const state_gspn_ssp*>(*j);
		  const state_gspn_ssp* new_state =
		    down_cast<const state_gspn_ssp*>(s);
		  assert(old_state);
		  assert(new_state);

		  if (old_state->left() == new_state->left())
		    break;

		  if (old_state->left()
		      && new_state->left()
		      && spot_inclusion(new_state->left(), old_state->left()))
		    {
		      ++inclusions;
		      break;
		    }
		}
	      if (j != l.end())
		{
		  if (s != *j)
		    {
		      s->destroy();
		      s = *j;
		    }
		}
	      else
		{
		  s = 0;
		}
	    }
	  else
	    {
	      s = 0;
	    }
	}
      else
	{
	  s = 0;
	}

      state_index_p res;

      if (s == 0)
	{
	  res.first = 0;
	  res.second = 0;
	}
      else
	{
	  hash_type::iterator i = h.find(s);
	  assert(i != h.end());
	  assert(s == i->first);
	  res.first = i->first;
	  res.second = &i->second;
	}
      return res;
    }

    virtual numbered_state_heap::state_index
    index(const state* s) const
    {
      state_index res;
      hash_type::const_iterator i = h.find(s);
      if (i == h.end())
	{
	  res.first = 0;
	  res.second = 0;
	}
      else
	{
	  res.first = i->first;
	  res.second = i->second;

	  if (s != i->first)
	    s->destroy();
	}
      return res;
    }

    virtual numbered_state_heap::state_index_p
    index(const state* s)
    {
      state_index_p res;
      hash_type::iterator i = h.find(s);
      if (i == h.end())
	{
	  res.first = 0;
	  res.second = 0;
	}
      else
	{
	  res.first = i->first;
	  res.second = &i->second;

	  if (s != i->first)
	    s->destroy();
	}
      return res;
    }

    virtual void
    insert(const state* s, int index)
    {
      h[s] = index;

      const state_gspn_ssp* s_ = down_cast<const state_gspn_ssp*>(s);
      State sg = s_->left();
      if (sg)
	{
	  const void* cont = container_(sg);
	  if (pushfront)
	    contained[cont][s_->right()].push_front(s);
	  else
	    contained[cont][s_->right()].push_back(s);
	}
    }

    virtual int
    size() const
    {
      return h.size();
    }

    virtual numbered_state_heap_const_iterator* iterator() const;

  protected:
    typedef Sgi::hash_map<const state*, int,
			  state_ptr_hash, state_ptr_equal> hash_type;
    hash_type h;		///< Map of visited states.

    typedef std::list<const state*> state_list;
    typedef Sgi::hash_map<const state*, state_list,
			  state_ptr_hash, state_ptr_equal> f_map;
    typedef Sgi::hash_map<const void*, f_map,
			  ptr_hash<void> > contained_map;
    contained_map contained;

    friend class numbered_state_heap_ssp_const_iterator;
    friend class couvreur99_check_shy_ssp;
    friend class couvreur99_check_shy_semi_ssp;

    mutable unsigned inclusions;
  };


  class numbered_state_heap_ssp_const_iterator :
    public numbered_state_heap_const_iterator
  {
  public:
    numbered_state_heap_ssp_const_iterator
    (const numbered_state_heap_ssp_semi::hash_type& h)
      : numbered_state_heap_const_iterator(), h(h)
    {
    }

    ~numbered_state_heap_ssp_const_iterator()
    {
    }

    virtual void
    first()
    {
      i = h.begin();
    }

    virtual void
    next()
    {
      ++i;
    }

    virtual bool
    done() const
    {
      return i == h.end();
    }

    virtual const state*
    get_state() const
    {
      return i->first;
    }

    virtual int
    get_index() const
    {
      return i->second;
    }

  private:
    numbered_state_heap_ssp_semi::hash_type::const_iterator i;
    const numbered_state_heap_ssp_semi::hash_type& h;
  };


  numbered_state_heap_const_iterator*
  numbered_state_heap_ssp_semi::iterator() const
  {
    return new numbered_state_heap_ssp_const_iterator(h);
  }


  /// \brief Factory for numbered_state_heap_ssp_semi
  ///
  /// This class is a singleton.  Retrieve the instance using instance().
  class numbered_state_heap_ssp_factory_semi:
    public numbered_state_heap_factory
  {
  public:
    virtual numbered_state_heap_ssp_semi*
    build() const
    {
      return new numbered_state_heap_ssp_semi();
    }

    /// Get the unique instance of this class.
    static const numbered_state_heap_ssp_factory_semi*
    instance()
    {
      static numbered_state_heap_ssp_factory_semi f;
      return &f;
    }

  protected:
    virtual
    ~numbered_state_heap_ssp_factory_semi()
    {
    }

    numbered_state_heap_ssp_factory_semi()
    {
    }
  };


  class couvreur99_check_shy_ssp : public couvreur99_check_shy
  {
  public:
    couvreur99_check_shy_ssp(const tgba* a, bool stack_inclusion,
			     bool double_inclusion,
			     bool reversed_double_inclusion,
			     bool no_decomp)
      : couvreur99_check_shy(a,
			     option_map(),
			     numbered_state_heap_ssp_factory_semi::instance()),
	inclusion_count_heap(0),
	inclusion_count_stack(0),
	stack_inclusion(stack_inclusion),
	double_inclusion(double_inclusion),
	reversed_double_inclusion(reversed_double_inclusion),
	no_decomp(no_decomp)
    {
      onepass_ = true;

      stats["inclusion count heap"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_ssp::get_inclusion_count_heap);
      stats["inclusion count stack"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_ssp::get_inclusion_count_stack);
      stats["contained map size"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_ssp::get_contained_map_size);
    }

  private:
    unsigned inclusion_count_heap;
    unsigned inclusion_count_stack;
    bool stack_inclusion;
    bool double_inclusion;
    bool reversed_double_inclusion;
    bool no_decomp;

  protected:
    unsigned
    get_inclusion_count_heap() const
    {
      return inclusion_count_heap;
    };
    unsigned
    get_inclusion_count_stack() const
    {
      return inclusion_count_stack;
    };
    unsigned
    get_contained_map_size() const
    {
      return
	down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->contained.size();
    }

    // If a new state includes an older state, we may have to add new
    // children to the list of children of that older state.  We cannot
    // to this by sub-classing numbered_state_heap since TODO is not
    // available.  So we override find_state() instead.
    virtual numbered_state_heap::state_index_p
    find_state(const state* s)
    {
      typedef numbered_state_heap_ssp_semi::hash_type hash_type;
      hash_type& h = down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->h;
      typedef numbered_state_heap_ssp_semi::contained_map contained_map;
      typedef numbered_state_heap_ssp_semi::f_map f_map;
      typedef numbered_state_heap_ssp_semi::state_list state_list;
      const contained_map& contained =
	down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->contained;

      const state_gspn_ssp* s_ = down_cast<const state_gspn_ssp*>(s);
      const void* cont = container_(s_->left());
      contained_map::const_iterator i = contained.find(cont);

      if (i != contained.end())
	{
	  f_map::const_iterator k = i->second.find(s_->right());
	  if (k != i->second.end())
	    {
	      const state_list& l = k->second;
	      state_list::const_iterator j;

	      // Make a first pass looking for identical states.
	      for (j = l.begin(); j != l.end(); ++j)
		{
		  const state_gspn_ssp* old_state =
		    down_cast<const state_gspn_ssp*>(*j);
		  const state_gspn_ssp* new_state =
		    down_cast<const state_gspn_ssp*>(s);
		  assert(old_state);
		  assert(new_state);

		  if (old_state->left() == new_state->left())
		    goto found_match;
		}

	      // Now, check for inclusions.
	      for (j = l.begin(); j != l.end(); ++j)
		{
		  const state_gspn_ssp* old_state =
		    down_cast<const state_gspn_ssp*>(*j);
		  const state_gspn_ssp* new_state =
		    down_cast<const state_gspn_ssp*>(s);
		  assert(old_state);
		  assert(new_state);

		  if (old_state->left() && new_state->left())
		    {
		      hash_type::const_iterator i = h.find(*j);
		      assert(i != h.end());
		      if (i->second == -1)
			{
			  if (spot_inclusion(new_state->left(),
					     old_state->left()))
			    {
			      ++inclusion_count_heap;
			      break;
			    }
			}
		      else
			{
			  if (stack_inclusion
			      && double_inclusion
			      && !reversed_double_inclusion
			      && spot_inclusion(new_state->left(),
						old_state->left()))
			    break;
			  if (stack_inclusion
			      && spot_inclusion(old_state->left(),
						new_state->left()))
			    {
			      ++inclusion_count_stack;

			      succ_queue& queue = todo.back().q;
			      succ_queue::iterator old;
			      if (pos == queue.end())
				old = queue.begin();
			      else
				{
				  old = pos;
				  // Should not happen, because onepass_ == 1
				  assert(0);
				}

			      if (no_decomp)
				{
				  queue.push_back  // why not push_front?
				    (successor(old->acc,
					       old_state->clone()));

				  assert(pos == queue.end());

				  inc_depth();

				  // If we had not done the first loop
				  // over the container to check for
				  // equal states, we would have to do
				  // one here to make sure that state
				  // s is not equal to another known
				  // state.  (We risk some intricate
				  // memory corruption if we don't
				  // destroy "clone states" at this
				  // point.)

				  // Since we have that first loop and
				  // we therefore know that state s is
				  // genuinely new, position j so that
				  // we won't destroy it.
				  j = l.end();
				}
			      else
				{
				  State* succ_tgba_ = 0;
				  size_t size_tgba_ = 0;

				  Diff_succ(old_state->left(),
					    new_state->left(),
					    &succ_tgba_, &size_tgba_);

				  for (size_t i = 0; i < size_tgba_; i++)
				    {
				      state_gspn_ssp* s =
					new state_gspn_ssp
					(succ_tgba_[i],
					 old_state->right()->clone());
				      // why not push_front?
				      queue.push_back(successor(old->acc, s));
				      inc_depth();
				    }
				  if (size_tgba_ != 0)
				    diff_succ_free(succ_tgba_);
				}

			      break;
			    }
			  if (stack_inclusion
			      && double_inclusion
			      && reversed_double_inclusion
			      && spot_inclusion(new_state->left(),
						old_state->left()))
			    break;
			}
		    }
		}
	    found_match:
	      if (j != l.end())
		{
		  if (s != *j)
		    {
		      s->destroy();
		      s = *j;
		    }
		}
	      else
		{
		  s = 0;
		}
	    }
	  else
	    {
	      s = 0;
	    }
   	}
      else
	{
	  s = 0;
	}

      // s points to the resulting state, or to 0 if we didn't find
      // the state in the list.

      numbered_state_heap::state_index_p res;
      if (s == 0)
	{
	  res.first = 0;
	  res.second = 0;
	}
      else
	{
	  hash_type::iterator k = h.find(s);
	  assert(k != h.end());
	  assert(s == k->first);
	  res.first = k->first;
	  res.second = &k->second;
	}

      return res;
    }
  };


  // The only purpose of this class is the inclusion_count counter.
  class couvreur99_check_shy_semi_ssp : public couvreur99_check_shy
  {
  public:
    couvreur99_check_shy_semi_ssp(const tgba* a)
      : couvreur99_check_shy(a,
			     option_map(),
			     numbered_state_heap_ssp_factory_semi::instance()),
	find_count(0)
    {
      onepass_ = true;

      stats["find_state count"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_semi_ssp::get_find_count);
      stats["contained map size"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_semi_ssp::get_contained_map_size);
      stats["inclusion count"] =
	static_cast<spot::unsigned_statistics::unsigned_fun>
	(&couvreur99_check_shy_semi_ssp::get_inclusion_count);

      //down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->stats = this;
    }

  private:
    unsigned find_count;

  protected:
    unsigned
    get_find_count() const
    {
      return find_count;
    };

    unsigned
    get_inclusion_count() const
    {
      return
	down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->inclusions;

    };

    unsigned
    get_contained_map_size() const
    {
      return
	down_cast<numbered_state_heap_ssp_semi*>(ecs_->h)->contained.size();
    }

    virtual numbered_state_heap::state_index_p
    find_state(const state* s)
    {
      ++find_count;
      return couvreur99_check_shy::find_state(s);
    }
  };


  couvreur99_check*
  couvreur99_check_ssp_semi(const tgba* ssp_automata)
  {
    assert(dynamic_cast<const tgba_gspn_ssp*>(ssp_automata));
    return
      new couvreur99_check(ssp_automata,
			   option_map(),
			   numbered_state_heap_ssp_factory_semi::instance());
  }

  couvreur99_check*
  couvreur99_check_ssp_shy_semi(const tgba* ssp_automata)
  {
    assert(dynamic_cast<const tgba_gspn_ssp*>(ssp_automata));
    return
      new couvreur99_check_shy_semi_ssp(ssp_automata);
  }

  couvreur99_check*
  couvreur99_check_ssp_shy(const tgba* ssp_automata, bool stack_inclusion,
			   bool double_inclusion,
			   bool reversed_double_inclusion,
			   bool no_decomp)
  {
    assert(dynamic_cast<const tgba_gspn_ssp*>(ssp_automata));
    return new couvreur99_check_shy_ssp(ssp_automata, stack_inclusion,
					double_inclusion,
					reversed_double_inclusion,
					no_decomp);
  }

#if 0
  // I rewrote couvreur99_check_result today, and it no longer uses
  // connected_component_ssp_factory.  So this cannot work anymore.
  // -- adl 2004-12-10.
  couvreur99_check_result*
  counter_example_ssp(const couvreur99_check_status* status)
  {
    return new
      couvreur99_check_result(status,
			      connected_component_ssp_factory::instance());
  }
#endif
}
