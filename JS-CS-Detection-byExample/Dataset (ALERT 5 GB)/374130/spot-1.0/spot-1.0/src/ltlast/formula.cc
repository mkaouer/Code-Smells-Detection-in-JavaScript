// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2005 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "formula.hh"
#include "misc/hash.hh"
#include <iostream>

namespace spot
{
  namespace ltl
  {
    size_t formula::max_count = 0;

    const formula*
    formula::clone() const
    {
      this->ref_();
      return this;
    }

    formula::~formula()
    {
    }

    void
    formula::destroy() const
    {
      if (this->unref_())
	delete this;
    }

    void
    formula::ref_() const
    {
      // Not reference counted by default.
    }

    bool
    formula::unref_() const
    {
      // Not reference counted by default.
      return false;
    }


#define printprops							\
      proprint(is_boolean, "B", "Boolean formula");			\
      proprint(is_sugar_free_boolean, "&", "without Boolean sugar");	\
      proprint(is_in_nenoform, "!", "in negative normal form");		\
      proprint(is_X_free, "x", "without X operator");			\
      proprint(is_sugar_free_ltl, "f", "without LTL sugar");		\
      proprint(is_ltl_formula, "L", "LTL formula");			\
      proprint(is_eltl_formula, "E", "ELTL formula");			\
      proprint(is_psl_formula, "P", "PSL formula");			\
      proprint(is_sere_formula, "S", "SERE formula");			\
      proprint(is_finite, "F", "finite");				\
      proprint(is_eventual, "e", "pure eventuality");			\
      proprint(is_universal, "u", "purely universal");			\
      proprint(is_syntactic_safety, "s", "syntactic safety");		\
      proprint(is_syntactic_guarantee, "g", "syntactic guarantee");	\
      proprint(is_syntactic_obligation, "o", "syntactic obligation");	\
      proprint(is_syntactic_persistence, "p", "syntactic persistence");	\
      proprint(is_syntactic_recurrence, "r", "syntactic recurrence");	\
      proprint(is_marked, "+", "marked");				\
      proprint(accepts_eword, "0", "accepts the empty word");		\
      proprint(has_lbt_atomic_props, "l", "as LBT-style atomic props");


    std::list<std::string>
    list_formula_props(const formula* f)
    {
      std::list<std::string> res;
#define proprint(m, a, l)			\
      if (f->m())				\
	res.push_back(std::string(l));
      printprops;
#undef proprint
      return res;
    }

    std::ostream&
    print_formula_props(std::ostream& out, const formula* f, bool abbr)
    {
      const char* comma = abbr ? "" : ", ";
      const char* sep = "";

#define proprint(m, a, l)			\
      if (f->m())				\
	{					\
	  out << sep; out << (abbr ? a : l);	\
	  sep = comma;				\
	}
      printprops;
#undef proprint

      return out;
    }
  }
}
