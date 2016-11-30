// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2011, 2012, 2013, 2014 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_TIMER_HH
# define SPOT_MISC_TIMER_HH

# include "common.hh"
# include "misc/_config.h"
# include <cassert>
# include <iosfwd>
# include <string>
# include <map>
# if SPOT_HAVE_SYS_TIMES_H
#  include <sys/times.h>
# endif
# include <ctime>


namespace spot
{
  /// \addtogroup misc_tools
  /// @{

  /// A structure to record elapsed time in clock ticks.
  struct time_info
  {
    time_info()
      : utime(0), stime(0)
    {
    }
    clock_t utime;
    clock_t stime;
  };

  /// A timekeeper that accumulate interval of time.
  class timer
  {
  public:
    timer()
      : running(false)
    {
    }

    /// Start a time interval.
    void
    start()
    {
      assert(!running);
      running = true;
#ifdef SPOT_HAVE_TIMES
      struct tms tmp;
      times(&tmp);
      start_.utime = tmp.tms_utime + tmp.tms_cutime;
      start_.stime = tmp.tms_stime + tmp.tms_cstime;
#else
      start_.utime = clock();
#endif
    }

    /// Stop a time interval and update the sum of all intervals.
    void
    stop()
    {
#ifdef SPOT_HAVE_TIMES
      struct tms tmp;
      times(&tmp);
      total_.utime += tmp.tms_utime + tmp.tms_cutime - start_.utime;
      total_.stime += tmp.tms_stime + tmp.tms_cstime - start_.stime;
#else
      total_.utime += clock() - start_.utime;
#endif
      assert(running);
      running = false;
    }

    /// \brief Return the user time of all accumulated interval.
    ///
    /// Any time interval that has been start()ed but not stop()ed
    /// will not be accounted for.
    clock_t
    utime() const
    {
      return total_.utime;
    }

    /// \brief Return the system time of all accumulated interval.
    ///
    /// Any time interval that has been start()ed but not stop()ed
    /// will not be accounted for.
    clock_t
    stime() const
    {
      return total_.stime;
    }


    /// \brief Whether the timer is running.
    bool
    is_running() const
    {
      return running;
    }

  protected:
    time_info start_;
    time_info total_;
    bool running;
  };

  /// \brief A map of timer, where each timer has a name.
  ///
  /// Timer_map also keeps track of the number of measures each timer
  /// has performed.
  class timer_map
  {
  public:

    /// \brief Start a timer with name \a name.
    ///
    /// The timer is created if it did not exist already.
    /// Once started, a timer should be either stop()ed or
    /// cancel()ed.
    void
    start(const std::string& name)
    {
      item_type& it = tm[name];
      it.first.start();
      ++it.second;
    }

    /// \brief Stop timer \a name.
    ///
    /// The timer must have been previously started with start().
    void
    stop(const std::string& name)
    {
      tm[name].first.stop();
    }

    /// \brief Cancel timer \a name.
    ///
    /// The timer must have been previously started with start().
    ///
    /// This cancel only the current measure.  (Previous measures
    /// recorded by the timer are preserved.)  When a timer that has
    /// not done any measure is canceled, it is removed from the map.
    void
    cancel(const std::string& name)
    {
      tm_type::iterator i = tm.find(name);
      assert(i != tm.end());
      assert(0 < i->second.second);
      if (0 == --i->second.second)
	tm.erase(i);
    }

    /// Return the timer \a name.
    const spot::timer&
    timer(const std::string& name) const
    {
      tm_type::const_iterator i = tm.find(name);
      assert(i != tm.end());
      return i->second.first;
    }

    /// Return the timer \a name.
    spot::timer&
    timer(const std::string& name)
    {
      return tm[name].first;
    }

    /// \brief Whether there is no timer in the map.
    ///
    /// If empty() return true, then either no timer where ever
    /// started, or all started timers were canceled without
    /// completing any measure.
    bool
    empty() const
    {
      return tm.empty();
    }

    /// Format information about all timers in a table.
    SPOT_API std::ostream&
    print(std::ostream& os) const;

    /// \brief Remove information about all timers.
    void
    reset_all()
    {
      tm.clear();
    }

  protected:
    typedef std::pair<spot::timer, int> item_type;
    typedef std::map<std::string, item_type> tm_type;
    tm_type tm;
  };

  /// @}
}

#endif // SPOT_MISC_TIMER_HH
