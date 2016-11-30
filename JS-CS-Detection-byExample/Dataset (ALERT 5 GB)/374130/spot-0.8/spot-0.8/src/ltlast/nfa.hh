// Copyright (C) 2008, 2010 Laboratoire de Recherche et Développement
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

/// \file ltlast/nfa.hh
/// \brief NFA interface used by automatop
#ifndef SPOT_LTLAST_NFA_HH
# define SPOT_LTLAST_NFA_HH

# include "misc/hash.hh"
# include <boost/shared_ptr.hpp>
# include <list>
# include <set>
# include <map>

namespace spot
{
  namespace ltl
  {
    /// Forward declaration. See below.
    class succ_iterator;
    /// Forward declaration. NFA's labels are reprensented by nodes
    /// which are defined in formula_tree.hh, included in nfa.cc.
    namespace formula_tree
    {
      struct node;
    }

    /// \brief Nondeterministic Finite Automata used by automata operators.
    ///
    /// States are represented by integers.
    /// Labels are represented by formula_tree's nodes.
    /// Currently, only one initial state is possible.
    class nfa
    {
    public:
      struct transition;
      typedef std::list<transition*> state;
      typedef boost::shared_ptr<formula_tree::node> label;
      /// Iterator over the successors of a state.
      typedef succ_iterator iterator;
      typedef boost::shared_ptr<nfa> ptr;

      /// Explicit transitions.
      struct transition
      {
	label lbl;
      	const state* dst;
      };

      nfa();
      ~nfa();

      void add_transition(int src, int dst, const label lbl);
      void set_init_state(int name);
      void set_final(int name);

      /// \brief Get the initial state of the NFA.
      const state* get_init_state();

      /// \brief Tell whether the given state is final or not.
      bool is_final(const state* s);

      /// \brief Tell whether the NFA is `loop', i.e. without any final state.
      bool is_loop();

      /// \brief Get the `arity' i.e. max t.cost, for each transition t.
      unsigned arity();

      /// \brief Return an iterator on the first succesor (if any) of \a state.
      ///
      /// The usual way to do this with a \c for loop.
      /// \code
      ///    for (nfa::iterator i = a.begin(s); i != a.end(s); ++i);
      /// \endcode
      iterator begin(const state* s) const;

      /// \brief Return an iterator just past the last succesor of \a state.
      iterator end(const state* s) const;

      int format_state(const state* s) const;

      const std::string& get_name() const;
      void set_name(const std::string&);

    private:
      state* add_state(int name);

      typedef Sgi::hash_map<int, state*, Sgi::hash<int> > is_map;
      typedef Sgi::hash_map<const state*, int, ptr_hash<state> > si_map;

      is_map is_;
      si_map si_;

      size_t arity_;
      std::string name_;

      state* init_;
      std::set<int> finals_;

      /// Explicitly disllow use of implicity generated member functions
      /// we don't want.
      nfa(const nfa& other);
      nfa& operator=(const nfa& other);
    };

    class succ_iterator
    {
    public:
      succ_iterator(const nfa::state::const_iterator s)
	: i_(s)
      {
      }

      void
      operator++()
      {
	++i_;
      }

      bool
      operator!=(const succ_iterator& rhs) const
      {
	return i_ != rhs.i_;
      }

      const nfa::transition* operator*() const
      {
	return *i_;
      }

    private:
      nfa::state::const_iterator i_;
    };

  }
}

#endif // SPOT_LTLAST_NFA_HH_
