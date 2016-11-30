/*
 *  Copyright (C) 2004, 2005
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#include "IntervalList.h"
#include "StringUtil.h"

/******************************************************************************
 *
 * Function definitions for class IntervalList.
 *
 *****************************************************************************/

/* ========================================================================= */
void IntervalList::merge(unsigned long int min, unsigned long int max)
/* ----------------------------------------------------------------------------
 *
 * Description:   Merges a new interval with a list of intervals.
 *
 * Arguments:     min, max  --  Upper and lower bound of the new interval.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (min > max)
    return;

  list<Interval>::iterator interval;
  for (interval = intervals.begin();
       interval != intervals.end() && interval->second + 1 < min;
       ++interval)
    ;

  if (interval == intervals.end())
  {
    intervals.insert(interval, make_pair(min, max));
    return;
  }

  if (interval->first <= min && max <= interval->second)
    return;

  if (max + 1 < interval->first)
  {
    intervals.insert(interval, make_pair(min, max));
    return;
  }

  if (min < interval->first)
    interval->first = min;

  if (interval->second < max)
  {
    interval->second = max;
    list<Interval>::iterator interval2 = interval;
    ++interval2;
    while (interval2 != intervals.end()
	   && interval2->first <= interval->second + 1)
    {
      if (interval->second < interval2->second)
	interval->second = interval2->second;
      list<Interval>::iterator interval_to_erase = interval2;
      ++interval2;
      intervals.erase(interval_to_erase);
    }
  }
}

/* ========================================================================= */
void IntervalList::remove(unsigned long int min, unsigned long int max)
/* ----------------------------------------------------------------------------
 *
 * Description:   Removes a closed interval from an interval list.
 *
 * Arguments:     min, max  --  Bounds for the interval to remove.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (min > max)
    return;

  list<Interval>::iterator interval;
  for (interval = intervals.begin();
       interval != intervals.end() && interval->second < min;
       ++interval)
    ;

  while (interval != intervals.end())
  {
    if (max < interval->first) /* min <= max < imin <= imax */
      return;

    if (interval->first < min)
    {
      if (max < interval->second) /* imin < min <= max < imax */
      {
	intervals.insert(interval, make_pair(interval->first, min - 1));
	interval->first = max + 1;
	return;
      }
      interval->second = min - 1; /* imin < min <= imax <= max */
      ++interval;
    }
    else if (max < interval->second) /* min <= imin <= max < imax */
    {
      interval->first = max + 1;
      return;
    }
    else /* min <= imin <= imax <= max */
    {
      list<Interval>::iterator interval_to_erase = interval;
      ++interval;
      intervals.erase(interval_to_erase);
    }
  }
}

/* ========================================================================= */
bool IntervalList::covers(unsigned long int min, unsigned long int max) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Test whether an interval list covers a given interval.
 *
 * Arguments:     min, max  --  Upper and lower bound for the interval to test.
 *
 * Returns:       True if the IntervalList covers the given interval.
 *
 * ------------------------------------------------------------------------- */
{
  if (min > max)
    return true; /* empty interval is always covered */

  list<Interval>::const_iterator interval;
  for (interval = intervals.begin();
       interval != intervals.end() && min > interval->second;
       ++interval)
    ;

  if (interval == intervals.end())
    return false;

  return (min >= interval->first && max <= interval->second);
}

/* ========================================================================= */
string IntervalList::toString() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts the interval list to a string.
 *
 * Arguments:     None.
 *
 * Returns:       A string listing the intervals in the interval list.
 *
 * ------------------------------------------------------------------------- */
{
  string s;
  for (list<Interval>::const_iterator interval = intervals.begin();
       interval != intervals.end();
       ++interval)
  {
    if (interval != intervals.begin())
      s += ',';
    s += StringUtil::toString(interval->first) + "..."
         + StringUtil::toString(interval->second);
  }
  return s;
}
