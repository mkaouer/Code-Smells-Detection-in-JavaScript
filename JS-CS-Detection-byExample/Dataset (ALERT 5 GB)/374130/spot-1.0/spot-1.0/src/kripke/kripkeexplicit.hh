// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE)
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


#ifndef SPOT_KRIPKE_KRIPKEEXPLICIT_HH
# define SPOT_KRIPKE_KRIPKEEXPLICIT_HH

# include <iosfwd>
# include "kripke.hh"
# include "ltlast/formula.hh"
# include "kripkeprint.hh"

namespace spot
{
  /// \brief Concrete class for kripke states.
  class state_kripke : public state
  {
    friend class kripke_explicit;
    friend class kripke_explicit_succ_iterator;
  private:
    state_kripke();

    /// \brief Compare two states.
    ///
    /// This method returns an integer less than, equal to, or greater
    /// than zero if \a this is found, respectively, to be less than, equal
    /// to, or greater than \a other according to some implicit total order.
    ///
    /// For moment, this method only compare the adress on the heap of the
    /// twice pointers.
    virtual int compare (const state* other) const;

    /// \brief Hash a state
    virtual size_t hash() const;

    /// \brief Duplicate a state.
    virtual state_kripke* clone() const;

    /// \brief Add a condition to the conditions already in the state.
    /// \param f The condition to add.
    void add_conditions(bdd f);

    /// \brief Add a new successor in the list.
    /// \param succ The successor state to add.
    void add_succ(state_kripke* succ);

    virtual bdd
    as_bdd() const
    {
      return bdd_;
    }

    /// \brief Release a state.
    ///
    virtual void
    destroy() const
    {
    }

    virtual
    ~state_kripke ()
    {
    }

    ////////////////////////////////
    // Management for succ_iterator

    const std::list<state_kripke*>& get_succ() const;

    bdd bdd_;
    std::list<state_kripke*> succ_;
  };


  /// \class kripke_explicit_succ_iterator
  /// \brief Implement iterator pattern on successor of a state_kripke.
  class kripke_explicit_succ_iterator : public kripke_succ_iterator
  {
  public:
    kripke_explicit_succ_iterator(const state_kripke*, bdd);

    ~kripke_explicit_succ_iterator();

    virtual void first();
    virtual void next();
    virtual bool done() const;

    virtual state_kripke* current_state() const;

    private:
    const state_kripke* s_;
    std::list<state_kripke*>::const_iterator it_;
  };


  /// \class kripke_explicit
  /// \brief Kripke Structure.
  class kripke_explicit : public kripke
  {
  public:
    kripke_explicit(bdd_dict*);
    kripke_explicit(bdd_dict*, state_kripke*);
    ~kripke_explicit();

    bdd_dict* get_dict() const;
    state_kripke* get_init_state() const;

    /// \brief Allow to get an iterator on the state we passed in
    /// parameter.
    kripke_explicit_succ_iterator*
    succ_iter(const spot::state* local_state,
	      const spot::state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    /// \brief Get the condition on the state
    bdd state_condition(const state* s) const;
    /// \brief Get the condition on the state
    bdd state_condition(const std::string) const;

    /// \brief Return the name of the state.
    std::string format_state(const state*) const;


    /// \brief Create state, if it does not already exists.
    ///
    /// Used by the parser.
    void add_state(std::string);

    /// \brief Add a transition between two states.
    void add_transition(std::string source,
			std::string dest);

    /// \brief Add a BDD condition to the state
    ///
    /// \param add the condition.
    /// \param on_me where add the condition.
    void add_conditions(bdd add,
			std::string on_me);

    /// \brief Add a formula to the state corresponding to the name.
    ///
    /// \param f the formula to add.
    /// \param on_me the state where to add.
    void add_condition(const ltl::formula* f,
		       std::string on_me);

    /// \brief Return map between states and their names.
    const std::map<const state_kripke*, std::string>&
    sn_get() const;

  private:
    /// \brief Add a state in the two maps.
    void add_state(std::string, state_kripke*);

    void add_conditions(bdd add,
			state_kripke* on_me);

    void add_transition(std::string source,
			const state_kripke* dest);

    void add_transition(state_kripke* source,
			const state_kripke* dest);

    bdd_dict* dict_;
    state_kripke* init_;
    std::map<const std::string, state_kripke*> ns_nodes_;
    std::map<const state_kripke*, std::string> sn_nodes_;
  };
}
#endif /* !SPOT_KRIPKEEXPLICIT_HH_ */
