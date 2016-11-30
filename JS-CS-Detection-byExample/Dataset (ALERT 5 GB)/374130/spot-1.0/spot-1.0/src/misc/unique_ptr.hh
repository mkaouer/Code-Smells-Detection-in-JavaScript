// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_MISC_UNIQUE_PTR_HH
# define SPOT_MISC_UNIQUE_PTR_HH

namespace spot
{
  /// \brief Take ownership of a pointer at its construction, and
  /// destroy it at the end of the scope.
  template <typename T>
  class unique_ptr
  {
    typedef T* pointer;
  public:
    unique_ptr(pointer ptr)
      : ptr_(ptr)
    {
    }

    ~unique_ptr()
    {
      delete ptr_;
    }

    operator pointer()
    {
      return ptr_;
    }

    pointer
    operator->()
    {
      return ptr_;
    }

  private:
    pointer ptr_;

    // This copy gives the ownership of the pointer to the new copy.
    // Can only be used by make_unique.
    unique_ptr(const unique_ptr& up)
    {
      unique_ptr& non_const_up = const_cast<unique_ptr&>(up);
      ptr_ = non_const_up.ptr_;
      non_const_up.ptr_ = 0;
    }

    // Allow `make_unique' to have an access to the private copy.
    template <typename V> friend unique_ptr<V> make_unique(V* ptr);

    unique_ptr& operator=(const unique_ptr&);
  };


  /// \brief Change a pointer into a unique_ptr.
  template <typename T>
  inline unique_ptr<T> make_unique(T* ptr)
  {
    return unique_ptr<T>(ptr);
  }
}


#endif // !SPOT_MISC_UNIQUE_PTR_HH
