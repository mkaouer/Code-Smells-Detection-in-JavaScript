/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
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

#ifndef BITARRAY_H
#define BITARRAY_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <cstring>
#include <string>
#include <iostream>

using namespace std;

class Bitset;

/******************************************************************************
 *
 * A class for arrays of bits.
 *
 *****************************************************************************/

class BitArray
{
public:
  explicit BitArray                                 /* Constructor. */
    (const unsigned long int size = 0);

  ~BitArray();                                      /* Destructor. */

private:
  BitArray(const BitArray& bitarray);               /* Prevent copying and   */
  BitArray& operator=(const BitArray& bitarray);    /* assignment of
						     * BitArray objects.
						     * (Copying and
						     * assignment requires
						     * information about
						     * the number of bits in
						     * the bit array; this
						     * information is not
						     * included in the
						     * object itself.)
						     */

public:
  bool operator[](const unsigned long int index)    /* Tell the value of a  */
    const;                                          /* bit in a given array */
  bool test(const unsigned long int index) const;   /* index.
						     */


  void copy                                         /* Duplicate a bit */
    (const BitArray& bitarray,                      /* array.          */
     const unsigned long int bit_count);

  void copy(const Bitset& bitset);                  /* Copy bits from a */
  BitArray& operator=(const Bitset& bitset);        /* Bitset.          */

  bool equal                                        /* Test whether the      */
    (const BitArray& bitarray,                      /* first `bit_count'     */
     const unsigned long int bit_count) const;      /* bits of two BitArrays
						     * are equal.
						     */

  bool equal(const Bitset& bitset) const;           /* Compare a BitArray to */
  bool operator==(const Bitset& bitset) const;      /* a Bitset.             */

  BitArray& bitwiseOr                               /* Compute the bitwise */
    (const BitArray& bitarray,  		    /* disjunction of the  */
     const unsigned long int bit_count);            /* first `bit_count'
						     * bits of two
						     * BitArrays.
						     */

  BitArray& bitwiseAnd                              /* Compute the bitwise */
    (const BitArray& bitarray,                      /* conjunction of the  */
     const unsigned long int bit_count);            /* first `bit_count'
						     * bits of two
						     * BitArrays.
						     */

  BitArray& bitwiseXor                              /* Compute the bitwise   */
    (const BitArray& bitarray,                      /* "exclusive or" of the */
     const unsigned long int bit_count);            /* first `bit_count'
						     * bits of two
						     * BitArrays.
						     */

  bool subset                                       /* Test whether the      */
    (const BitArray& bitarray,                      /* first `bit_count'     */
     const unsigned long int bit_count) const;      /* bits of `this'
						     * BitArray are included
						     * in another BitArray.
						     */

  bool subset(const Bitset& bitset) const;          /* Test for inclusion */
  bool operator<=(const Bitset& bitset) const;      /* into a Bitset.     */

  unsigned long int count                           /* Compute the number of */
    (const unsigned long int bit_count) const;      /* 1-bits in the first
						     * `bit_count' bits of
						     * the BitArray.
						     */

  unsigned long int hammingDistance                 /* Compute the Hamming   */
    (const BitArray& bitarray,                      /* distance between two  */
     const unsigned long int bit_count) const;      /* bit vectors
						     * consisting of the
						     * first `bit_count'
						     * bits of two
						     * BitArrays.
						     */

  unsigned long int hammingDistance                 /* Compute the Hamming */
    (const Bitset& bitset) const;                   /* distance between a
						     * BitArray and a
						     * Bitset.
						     */
                                        
  void set(const unsigned long int bit_count);      /* Set a given number of
                                                     * bits in the bit array.
						     */

  void setBit(const unsigned long int index);       /* Set a single bit in the
						     * bit array.
						     */

  void clear(const unsigned long int bit_count);    /* Clear the first
                                                     * `bit_count' bits in the
						     * bit array.
						     */

  void clearBit(const unsigned long int index);     /* Clear a single bit in
						     * the bit array.
						     */

  void flip(const unsigned long int bit_count);     /* Flip the first
                                                     * `bit_count' bits in the
						     * bit array.
						     */

  void flipBit(const unsigned long int index);      /* Flip a single bit in the
                                                     * bit array.
						     */

  unsigned long int find                            /* Return the index of   */
    (const unsigned long int max_count) const;      /* the first 1-bit in
                                                     * the bit array with a
						     * bit index less than
						     * `max_count'.
						     */

  void print                                        /* Print the first     */
    (const unsigned long int bit_count,             /* `bit_count' bits of */
     ostream& stream = cout) const;                 /* the bit array.      */

  string toString                                   /* Convert a prefix of   */
    (const unsigned long int bit_count) const;      /* a BitArray into a    
						     * string of 0's and
						     * 1's.
						     */

private:
  unsigned char* bits;                              /* Storage for the bits. */

  static const unsigned char bit_counts[];          /* `bit_counts[i]' gives
						     * the number of bits in
						     * the 8-bit integer `i'.
						     * (Used by the member
						     * function `count'.)
						     */

  friend class Bitset;
};



#include "Bitset.h"



/******************************************************************************
 *
 * Inline function definitions for class BitArray.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool BitArray::operator[](const unsigned long int index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the state of a bit in a bit array.
 *
 * Argument:      index  --  Index of the bit to test.
 *
 * Returns:       State of the bit in the bit array.
 *
 * ------------------------------------------------------------------------- */
{
  return ((bits[index >> 3] & (1 << (index & 7))) != 0);
}

/* ========================================================================= */
inline void BitArray::copy(const Bitset& bitset)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes the BitArray with bits in a Bitset.
 *
 * Argument:      bitset  --  Bitset to copy.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return copy(bitset, bitset.capacity());
}

/* ========================================================================= */
inline BitArray& BitArray::operator=(const Bitset& bitset)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes the BitArray with bits in a Bitset.
 *
 * Argument:      bitset  --  Bitset to copy.
 *
 * Returns:       A reference to `this' BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  copy(bitset, bitset.capacity());
  return *this;
}

/* ========================================================================= */
inline bool BitArray::equal(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Compares the `bitset.capacity()' first bits of the BitArray
 *                to `bitset'. The BitArray is assumed to be at least as large
 *                as the Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       Truth value based on the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return equal(bitset, bitset.capacity());
}

/* ========================================================================= */
inline bool BitArray::operator==(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Compares the `bitset.capacity()' first bits of the BitArray
 *                to `bitset'. The BitArray is assumed to be at least as large
 *                as the Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       Truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return equal(bitset, bitset.capacity());
}

/* ========================================================================= */
inline bool BitArray::subset(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether all of the first `bitset.capacity()' bits of
 *                the BitArray are included in `bitset'. The BitArray should
 *                be at least as large as the Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       Truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return subset(bitset, bitset.capacity());
}

/* ========================================================================= */
inline bool BitArray::operator<=(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether all of the first `bitset.capacity()' bits of
 *                the BitArray are included in `bitset'. The BitArray should be
 *                at least as large as the Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       Truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return subset(bitset, bitset.capacity());
}

/* ========================================================================= */
inline unsigned long int BitArray::hammingDistance(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computer the Hamming distance (number of differing bits)
 *                between the first `bitset.capacity()' bits of `bitset' and
 *                the BitArray.
 *
 * Argument:      bitset  --  Target of the computation.
 *
 * Returns:       The Hamming distance between `bitset' and the BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  return hammingDistance(bitset, bitset.capacity());
}

/* ========================================================================= */
inline bool BitArray::test(const unsigned long int index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the state of a bit in a bit array.
 *
 * Argument:      index  --  Index of bit to test.
 *
 * Returns:       Truth value according to the state of the bit.
 *
 * ------------------------------------------------------------------------- */
{
  return operator[](index);
}

/* ========================================================================= */
inline void BitArray::set(const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Fills the first `bit_count' bits of the bit array with
 *                1-bits.
 *
 * Arguments:     bit_count  --  Number of bits to set to 1.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  memset(static_cast<void*>(bits), 0xFF, (bit_count + 7) >> 3);  
}

/* ========================================================================= */
inline void BitArray::setBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Sets a single bit in the bit array.
 *
 * Argument:      index  --  Index of the bit to be set.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits[index >> 3] |= 1 << (index & 7);
}

/* ========================================================================= */
inline void BitArray::clear(const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Fills the first `bit_count' bits of the bit array with
 *                0-bits.
 *
 * Arguments:     bit_count  --  Number of bits to set to 0.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  memset(static_cast<void*>(bits), 0, (bit_count + 7) >> 3);
}

/* ========================================================================= */
inline void BitArray::clearBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Clears a single bit in the bit array.
 *
 * Argument:      index  --  Index of the bit to be cleared.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits[index >> 3] &= ~(1 << (index & 7));
}

/* ========================================================================= */
inline void BitArray::flipBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Switches the state of a single bit in the bit array.
 *
 * Arguments:     index  --  Index of the bit.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits[index >> 3] ^= (1 << (index & 7));
}

#endif /* !BITARRAY_H */
