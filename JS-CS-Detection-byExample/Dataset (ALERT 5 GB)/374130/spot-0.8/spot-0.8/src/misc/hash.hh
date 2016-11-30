// Copyright (C) 2008, 2011 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
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

#ifndef SPOT_MISC_HASH_HH
#  define SPOT_MISC_HASH_HH

#  include <string>
#  include <functional>
#  include "hashfunc.hh"

// See the G++ FAQ for details about the following.
#  ifdef __GNUC__
#  if __GNUC__ < 3
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
#    if (__GNUC__ == 4 && __GNUC_MINOR__ >= 3) || __GNUC__ > 4
#      include <tr1/unordered_set>         // GCC 4.3
#      include <tr1/unordered_map>
       namespace Sgi = std::tr1;
#      define hash_map unordered_map
#      define hash_multimap unordered_multimap
#      define hash_set unordered_set
#    else
#      include <ext/hash_map>
#      include <ext/hash_set>
#      if __GNUC__ == 3 && __GNUC_MINOR__ == 0
        namespace Sgi = std;               // GCC 3.0
#      else
        namespace Sgi = ::__gnu_cxx;       // GCC 3.1 to 4.2
#      endif
#    endif
#  endif
#  else      // ...  there are other compilers, right?
#   include <hash_map>
#   include <hash_set>
    namespace Sgi = std;
#  endif

namespace spot
{

  /// \brief A hash function for pointers.
  /// \ingroup hash_funcs
  template <class T>
  struct ptr_hash :
    public std::unary_function<const T*, size_t>
  {
    size_t operator()(const T* p) const
    {
      return knuth32_hash(reinterpret_cast<const char*>(p)
			  - static_cast<const char*>(0));
    }
  };

  /// \brief A hash function for strings.
  /// \ingroup hash_funcs
  /// @{
#  if (__GNUC__ == 4 && __GNUC_MINOR__ >= 3) || __GNUC__ > 4
  typedef std::tr1::hash<std::string> string_hash;
#  else  // GCC < 4.3
  struct string_hash:
    public Sgi::hash<const char*>,
    public std::unary_function<const std::string&, size_t>
  {
    size_t operator()(const std::string& s) const
    {
      // We are living dangerously.  Be sure to call operator()
      // from the super-class, not this one.
      return Sgi::hash<const char*>::operator()(s.c_str());
    }
  };
  /// @}
#  endif

  /// \brief A hash function that returns identity
  /// \ingroup hash_funcs
  template<typename T>
  struct identity_hash:
    public std::unary_function<const T&, size_t>
  {
    size_t operator()(const T& s) const
    {
      return s;
    }
  };
}

#endif // SPOT_MISC_HASH_HH
