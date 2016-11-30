// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#include "formater.hh"
#include <iostream>

namespace spot
{
  void
  formater::prime(const char* fmt)
  {
    for (const char* pos = fmt; *pos; ++pos)
      if (*pos == '%')
	{
	  char c = *++pos;
	  has_[c] = true;
	  if (!c)
	    break;
	}
  }

  std::ostream&
  formater::format(const char* fmt)
    {
      for (const char* pos = fmt; *pos; ++pos)
	if (*pos != '%')
	  {
	    *output_ << *pos;
	  }
	else
	  {
	    char c = *++pos;
	    call_[c]->print(*output_, pos);
	    if (!c)
	      break;
	  }
      return *output_;
    }
}
