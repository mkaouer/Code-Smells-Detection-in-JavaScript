// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2012, 2013, 2014 Laboratoire de Recherche et
// Développement de l'Epita (LRDE)
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

/// \file ltlast/automatop.hh
/// \brief ELTL automaton operators
#ifndef SPOT_LTLAST_AUTOMATOP_HH
# define SPOT_LTLAST_AUTOMATOP_HH

# include "refformula.hh"
# include <vector>
# include <iosfwd>
# include <map>
# include "nfa.hh"

namespace spot
{
  namespace ltl
  {
    /// \ingroup eltl_ast
    /// \brief Automaton operators.
    ///
    class SPOT_API automatop : public ref_formula
    {
    public:
      /// List of formulae.
      typedef std::vector<const formula*> vec;

      /// \brief Build a spot::ltl::automatop with many children.
      ///
      /// This vector is acquired by the spot::ltl::automatop class,
      /// the caller should allocate it with \c new, but not use it
      /// (especially not destroy it) after it has been passed to
      /// spot::ltl::automatop.
      static const automatop*
      instance(const nfa::ptr nfa, vec* v, bool negated);

      virtual void accept(visitor& v) const;

      /// Get the number of arguments.
      unsigned size() const
      {
	return children_->size();
      }

      /// \brief Get the nth argument.
      ///
      /// Starting with \a n = 0.
      const formula* nth(unsigned n) const
      {
	return (*children_)[n];
      }

      /// Get the NFA of this operator.
      const spot::ltl::nfa::ptr get_nfa() const
      {
	return nfa_;
      }

      /// Whether the automaton is negated.
      bool is_negated() const
      {
	return negated_;
      }

      /// Return a canonic representation of the atomic proposition
      std::string dump() const;

      /// Number of instantiated multop operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);


    protected:
      typedef std::pair<std::pair<nfa::ptr, bool>, vec*> triplet;
      /// Comparison functor used internally by ltl::automatop.
      struct tripletcmp
      {
	bool
	operator()(const triplet& p1, const triplet& p2) const
	{
	  if (p1.first.first != p2.first.first)
	    return p1.first.first < p2.first.first;
	  if (p1.first.second != p2.first.second)
	    return p1.first.second < p2.first.second;
	  return *p1.second < *p2.second;
	}
      };
      typedef std::map<triplet, const automatop*, tripletcmp> map;
      static map instances;

      automatop(const nfa::ptr, vec* v, bool negated);
      virtual ~automatop();

    private:
      const nfa::ptr nfa_;
      vec* children_;
      bool negated_;
    };
  }
}

#endif // SPOT_LTLAST_AUTOMATOP_HH
