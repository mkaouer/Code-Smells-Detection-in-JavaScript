// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2011, 2014 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
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

#ifndef SPOT_MISC_HASH_HH
#  define SPOT_MISC_HASH_HH

#  include <string>
#  include <functional>
#  include "misc/hashfunc.hh"
#  include "misc/_config.h"

#ifdef SPOT_HAVE_UNORDERED_MAP
#  include <unordered_map>
#  include <unordered_set>
   namespace Sgi = std;
#  define hash_map unordered_map
#  define hash_multimap unordered_multimap
#  define hash_set unordered_set
#else
#ifdef SPOT_HAVE_TR1_UNORDERED_MAP
#  include <tr1/unordered_map>
#  include <tr1/unordered_set>
   namespace Sgi = std::tr1;
#  define hash_map unordered_map
#  define hash_multimap unordered_multimap
#  define hash_set unordered_set
#else
#ifdef SPOT_HAVE_EXT_HASH_MAP
#  include <ext/hash_map>
#  include <ext/hash_set>
#  if __GNUC__ == 3 && __GNUC_MINOR__ == 0
     namespace Sgi = std;               // GCC 3.0
#  else
     namespace Sgi = ::__gnu_cxx;       // GCC 3.1 to 4.2
#  endif
#else
#  if defined(__GNUC__) && (__GNUC__ < 3)
#    include <hash_map.h>
#    include <hash_set.h>
    namespace Sgi
    { // inherit globals
      using ::hash_map;
      using ::hash_multimap;
      using ::hash_set;
      using ::hash;
    }
#  else
#   include <hash_map>
#   include <hash_set>
    namespace Sgi = std;
#  endif
#endif
#endif
#endif

namespace spot
{

  /// \ingroup hash_funcs
  /// \brief A hash function for pointers.
  template <class T>
  struct ptr_hash :
    public std::unary_function<const T*, size_t>
  {
    // A default constructor is needed if the ptr_hash object is
    // stored in a const member.  This occur with the clang version
    // installed by OS X 10.9.
    ptr_hash()
    {
    }

    size_t operator()(const T* p) const
    {
      return knuth32_hash(reinterpret_cast<const char*>(p)
			  - static_cast<const char*>(0));
    }
  };

  /// \ingroup hash_funcs
  /// \brief A hash function for strings.
  /// @{
#if defined(SPOT_HAVE_UNORDERED_MAP) || defined(SPOT_HAVE_TR1_UNORDERED_MAP)
  typedef Sgi::hash<std::string> string_hash;
#else // e.g. GCC < 4.3
  struct string_hash:
    public Sgi::hash<const char*>,
    public std::unary_function<const std::string&, size_t>
  {
    // A default constructor is needed if the string_hash object is
    // stored in a const member.
    string_hash()
    {
    }

    size_t operator()(const std::string& s) const
    {
      // We are living dangerously.  Be sure to call operator()
      // from the super-class, not this one.
      return Sgi::hash<const char*>::operator()(s.c_str());
    }
  };
  /// @}
#endif

  /// \ingroup hash_funcs
  /// \brief A hash function that returns identity
  template<typename T>
  struct identity_hash:
    public std::unary_function<const T&, size_t>
  {
    // A default constructor is needed if the string_hash object is
    // stored in a const member.
    identity_hash()
    {
    }

    size_t operator()(const T& s) const
    {
      return s;
    }
  };
}

#endif // SPOT_MISC_HASH_HH
