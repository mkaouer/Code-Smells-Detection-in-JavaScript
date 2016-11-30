// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLENV_DECLENV_HH
# define SPOT_LTLENV_DECLENV_HH

# include "environment.hh"
# include <string>
# include <map>
# include "ltlast/atomic_prop.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief A declarative environment.
    /// \ingroup ltl_environment
    ///
    /// This environment recognizes all atomic propositions
    /// that have been previously declared.  It will reject other.
    class declarative_environment : public environment
    {
    public:
      declarative_environment();
      ~declarative_environment();

      /// Declare an atomic proposition.  Return false iff the
      /// proposition was already declared.
      bool declare(const std::string& prop_str);

      virtual ltl::formula* require(const std::string& prop_str);

      /// Get the name of the environment.
      virtual const std::string& name();

      typedef std::map<const std::string, ltl::atomic_prop*> prop_map;

      /// Get the map of atomic proposition known to this environment.
      const prop_map& get_prop_map() const;

    private:
      prop_map props_;
    };
  }
}

#endif // SPOT_LTLENV_DECLENV_HH
