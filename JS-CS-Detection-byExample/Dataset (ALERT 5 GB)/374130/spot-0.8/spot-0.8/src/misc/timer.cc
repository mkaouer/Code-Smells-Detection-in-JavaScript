// Copyright (C) 2009 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
// Copyright (C) 2004, 2005 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "timer.hh"
#include <iostream>
#include <iomanip>

namespace spot
{

  std::ostream&
  timer_map::print(std::ostream& os) const
  {
    std::ios::fmtflags old = std::cout.flags();
    std::cout << std::right << std::fixed << std::setprecision(1);

    time_info total;
    for (tm_type::const_iterator i = tm.begin(); i != tm.end(); ++i)
      {
	total.utime += i->second.first.utime();
	total.stime += i->second.first.stime();
      }
    clock_t grand_total = total.utime + total.stime;

    os << std::setw(23) << ""
       << "|    user time   |    sys. time   |      total     |"
       << std::endl
       << std::setw(23) << "name "
       << "| ticks        % | ticks        % | ticks        % |   n"
       << std::endl
       << std::setw(79) << std::setfill('-') << "" << std::setfill(' ')
       << std::endl;
    for (tm_type::const_iterator i = tm.begin(); i != tm.end(); ++i)
      {
	// truncate long keys
	std::string name = i->first;
	if (name.size() > 22)
	  name.erase(22);

	const spot::timer& t = i->second.first;
	const char* sep = t.is_running() ? "+|" : " |";

	os << std::setw(22) << name << sep
	   << std::setw(6) << t.utime() << " "
	   << std::setw(8) << (total.utime ?
			       100.0 * t.utime() / total.utime : 0.)
	   << sep
	   << std::setw(6) << t.stime() << " "
	   << std::setw(8) << (total.stime ?
			       100.0 * t.stime() / total.stime : 0.)
	   << sep
	   << std::setw(6) << t.utime() + t.stime() << " "
	   << std::setw(8) << (grand_total ?
			       (100.0 * (t.utime() + t.stime()) /
				grand_total) : 0.)
	   << sep
	   << std::setw(4) << i->second.second
	   << std::endl;
      }
    os << std::setw(79) << std::setfill('-') << "" << std::setfill(' ')
       << std::endl
       << std::setw(22) << "TOTAL" << " |"
       << std::setw(6) << total.utime << " "
       << std::setw(8) << 100.
       << " |"
       << std::setw(6) << total.stime << " "
       << std::setw(8) << 100.
       << " |"
       << std::setw(6) << grand_total << " "
       << std::setw(8) << 100.
       << " |"
       << std::endl;

    std::cout << std::setiosflags(old);
    return os;
  }

}
