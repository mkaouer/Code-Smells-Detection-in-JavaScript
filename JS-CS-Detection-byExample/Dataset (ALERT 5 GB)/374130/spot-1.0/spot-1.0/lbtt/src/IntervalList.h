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

#ifndef INTERVALLIST_H
#define INTERVALLIST_H

#include <config.h>
#include <list>
#include <string>
#include <utility>
#include "LbttAlloc.h"

using namespace std;

/******************************************************************************
 *
 *  The IntervalList class represents a list of disjoint closed intervals
 *  formed from pairs of unsigned long integers. The class supports merging
 *  a new interval with a list of intervals, removing an interval from a list
 *  of intervals and checking whether the interval list covers a given element
 *  (or a given interval). The elements of the intervals can also be accessed
 *  in increasing order via IntervalList::const_iterator.
 *
 *****************************************************************************/

class IntervalList
{
private:
  typedef pair<unsigned long int,
               unsigned long int>
    Interval;

public:
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class const_iterator                              /* A class for iterating */
  {						    /* over the elements of  */
						    /* an IntervalList.      */
  public:
    const_iterator();                               /* Constructor. */

    /* default copy constructor */

    ~const_iterator();                              /* Destructor. */

    /* default assignment operator */

    bool operator==(const const_iterator& it)       /* Comparison operators. */
      const;

    bool operator!=(const const_iterator& it)
      const;

    unsigned long int operator*() const;            /* Dereference operator. */

    unsigned long int operator++();                 /* Prefix increment. */

    unsigned long int operator++(int);              /* Postfix increment. */

  private:
    const list<Interval>* interval_list;            /* The interval list
                                                     * associated with the
                                                     * iterator.
						     */

    list<Interval>::const_iterator interval;        /* An iterator pointing at
                                                     * the current intrerval  
                                                     * list.
						     */

    unsigned long int element;                      /* Element currently
						     * pointed to by the
						     * iterator.
						     */

    friend class IntervalList;
  };
  
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  IntervalList();                                   /* Constructor. */

  ~IntervalList();                                  /* Destructor. */

  /* default copy constructor */

  /* default assignment operator */

  void merge(unsigned long int element);            /* Merges a point interval
						     * with the list of
						     * intervals.
						     */

  void merge                                        /* Merges a new interval */
    (unsigned long int min, unsigned long int max); /* with the list of
						     * intervals.
						     */

  void remove(unsigned long int element);           /* Removes an element from
						     * the list of intervals.
						     */

  void remove                                       /* Removes an interval */
    (unsigned long int min, unsigned long int max); /* from the list of
						     * intervals.
						     */

  bool covers(unsigned long int element) const;     /* Tests whether the
						     * interval list covers an
						     * element.
						     */

  bool covers                                       /* Tests whether the    */
    (unsigned long int min, unsigned long int max)  /* interval list covers */
    const;                                          /* an interval.         */

  const_iterator begin() const;                     /* Returns an iterator to
						     * the beginning of the
						     * interval list.
						     */

  const_iterator end() const;                       /* Returns an iterator to
						     * the end of the interval
						     * list.
						     */

  typedef const_iterator iterator;                  /* The interval list
						     * cannot be modified with
						     * iterators.
						     */

  typedef list<Interval>::size_type size_type;      /* Size type. */

  size_type size() const;                           /* Tell the number of
						     * disjoint intervals in
						     * the interval list.
						     */

  size_type max_size() const;                       /* Tell the maximum
						     * allowable number of
						     * disjoint intervals in
						     * the interval list.
						     */

  bool empty() const;                               /* Tell whether the
						     * interval list is empty.
						     */

  void clear();                                     /* Makes the interval list
						     * empty.
						     */

  string toString() const;                          /* Converts the interval
						     * list to a string.
						     */

private:
  list<Interval> intervals;                         /* List of intervals. */

  friend class const_iterator;
};



/******************************************************************************
 *
 * Inline function definitions for class IntervalList.
 *
 *****************************************************************************/

/* ========================================================================= */
inline IntervalList::IntervalList()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class IntervalList. Creates an empty list of
 *                intervals.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline IntervalList::~IntervalList()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class IntervalList.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void IntervalList::merge(unsigned long int element)
/* ----------------------------------------------------------------------------
 *
 * Description:    Merges an element (a point interval) with an IntervalList.
 *
 * Arguments:      element  --  Element to merge.
 *
 * Returns:        Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  merge(element, element);
}

/* ========================================================================= */
inline bool IntervalList::covers(unsigned long int element) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether an interval list covers an element.
 *
 * Arguments:     element  --  Element to test.
 *
 * Returns:       True if the IntervalList covers the element.
 *
 * ------------------------------------------------------------------------- */
{
  return covers(element, element);
}

/* ========================================================================= */
inline IntervalList::size_type IntervalList::size() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of disjoint intervals in an IntervalList.
 *
 * Arguments:     None.
 *
 * Returns:       Number of disjoint intervals in the IntervalList.
 *
 * ------------------------------------------------------------------------- */
{
  return intervals.size();
}

/* ========================================================================= */
inline IntervalList::size_type IntervalList::max_size() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the maximum allowable number of disjoint intervals in
 *                an IntervalList.
 *
 * Arguments:     None.
 *
 * Returns:       Maximum allowable number of disjoint intervals in the
 *                IntervalList.
 *
 * ------------------------------------------------------------------------- */
{
  return intervals.max_size();
}

/* ========================================================================= */
inline bool IntervalList::empty() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether an IntervalList is empty.
 *
 * Arguments:     None.
 *
 * Returns:       True if the IntervalList is empty.
 *
 * ------------------------------------------------------------------------- */
{
  return intervals.empty();
}

/* ========================================================================= */
inline void IntervalList::clear()
/* ----------------------------------------------------------------------------
 *
 * Description:   Makes an IntervalList empty.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  intervals.clear();
}

/* ========================================================================= */
inline IntervalList::const_iterator IntervalList::begin() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an IntervalList::const_iterator pointing to the
 *                beginning of an IntervalList.
 *
 * Arguments:     None.
 *
 * Returns:       An IntervalList::const_iterator pointing to the beginning of
 *                the IntervalList.
 *
 * ------------------------------------------------------------------------- */
{
  const_iterator it;
  it.interval_list = &this->intervals;
  it.interval = intervals.begin();
  if (it.interval != intervals.end())
    it.element = it.interval->first;
  else
    it.element = 0;
  return it;
}

/* ========================================================================= */
inline IntervalList::const_iterator IntervalList::end() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns an IntervalList::const_iterator pointing to the end
 *                of an IntervalList.
 *
 * Arguments:     None.
 *
 * Returns:       An IntervalList::const_iterator pointing to the end of the
 *                IntervalList.
 *
 * ------------------------------------------------------------------------- */
{
  const_iterator it;
  it.interval_list = &this->intervals;
  it.interval = intervals.end();
  it.element = 0;
  return it;
}



/******************************************************************************
 *
 * Inline function definitions for class IntervalList::const_iterator.
 *
 *****************************************************************************/

/* ========================================================================= */
inline IntervalList::const_iterator::const_iterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class IntervalList::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline IntervalList::const_iterator::~const_iterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class IntervalList::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline bool IntervalList::const_iterator::operator==
  (const const_iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equality test for two IntervalList::const_iterators. Two
 *                IntervalList::const_iterators are equal if and only if they
 *                point to the same interval of an interval list and the same
 *                element in the interval.
 *
 * Argument:      it  --  A constant reference to another
 *                        IntervalList::const_iterator.
 *
 * Returns:       Result of the equality test (a truth value).
 *
 * ------------------------------------------------------------------------- */
{
  return (interval_list == it.interval_list && interval == it.interval
	  && element == it.element);
}

/* ========================================================================= */
inline bool IntervalList::const_iterator::operator!=
  (const const_iterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Inequality test for two IntervalList::const_iterators. Two
 *                IntervalList::const_iterators are not equal if and only if
 *                they point to different intervals of an interval list or to
 *                different elements of the same interval in the list.
 *
 * Argument:      it  --  A constant reference to another
 *                        IntervalList::const_iterator.
 *
 * Returns:       Result of the inequality test (a truth value).
 *
 * ------------------------------------------------------------------------- */
{
  return (interval_list != it.interval_list || interval != it.interval
	  || element != it.element);
}

/* ========================================================================= */
inline unsigned long int IntervalList::const_iterator::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:    Dereferencing operator for IntervalList::const_iterator.
 *
 * Arguments:      None.
 *
 * Returns:        The element currently pointed to by the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  return element;
}

/* ========================================================================= */
inline unsigned long int IntervalList::const_iterator::operator++()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prefix increment operator for IntervalList::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       The element following the "current" element in the interval
 *                list.
 *
 * ------------------------------------------------------------------------- */
{
  if (element == interval->second)
  {
    ++interval;
    if (interval != interval_list->end())
      element = interval->first;
    else
      element = 0;
  }
  else
    ++element;

  return element;
}

/* ========================================================================= */
inline unsigned long int IntervalList::const_iterator::operator++(int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Postfix increment operator for IntervalList::const_iterator.
 *
 * Arguments:     None.
 *
 * Returns:       The "current" element in the interval list.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int current_element = element;
  if (element == interval->second)
  {
    ++interval;
    if (interval != interval_list->end())
      element = interval->first;
    else
      element = 0;
  }
  else
    ++element;

  return current_element;
}

#endif /* INTERVALLIST_H */
