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

#ifndef BITSET_H
#define BITSET_H

#include <config.h>
#include <string>
#include <iostream>
#include "Exception.h"

using namespace std;

/******************************************************************************
 *
 * A class for sets of bits.
 *
 *****************************************************************************/

class Bitset
{
public:
  explicit Bitset                                   /* Creates an empty bit */
    (const unsigned long int capacity = 0);         /* set.                 */

  explicit Bitset                                   /* Creates a bit set */
    (const BitArray& bitarray,                      /* from a bit array. */
     const unsigned long int capacity);

  Bitset(const Bitset& bitset);                     /* Copy constructor. */

  ~Bitset();                                        /* Destructor. */

  operator const BitArray&() const;                 /* A Bitset can always be
						     * cast into a constant
						     * BitArray.
						     */

  Bitset& operator=(const Bitset& bitset);          /* Assignment operator. */

  bool operator[](const unsigned long int index)    /* Tell the value of a   */
    const;                                          /* bit in a given index. */
  bool test(const unsigned long int index) const;

  bool operator==(const Bitset& bitset) const;      /* Test whether two   */
  bool equal(const Bitset& bitset) const;	    /* Bitsets are equal. */

  bool operator<=(const Bitset& bitset) const;      /* Test whether `this' */
  bool subset(const Bitset& bitset) const;          /* Bitset is a subset
                                                     * of another Bitset.
						     */

  unsigned long int capacity() const;               /* Tells the capacity of
						     * the bit set.
						     */

  unsigned long int count() const;                  /* Compute the number of
						     * 1-bits in the Bitset.
						     */

  unsigned long int hammingDistance                 /* Compute the Hamming   */
    (const Bitset& bitset) const;                   /* distance between two
                                                     * Bitsets.
						     */

  void set();                                       /* Set all bits in the bit
                                                     * set.
						     */

  void setBit(const unsigned long int index);       /* Set a single bit in the
						     * bit set.
						     */

  void clear();                                     /* Clear all bits in the
						     * bit set.
						     */

  void clearBit(const unsigned long int index);     /* Clear a single bit in
						     * the bit set.
						     */

  void flip();                                      /* Flip all bits in the
						     * bit set.
						     */

  void flipBit(const unsigned long int index);      /* Flip a single bit in the
                                                     * bit set.
						     */

  unsigned long int find() const;                   /* Return the index of the
                                                     * first 1-bit in the bit
                                                     * set.
						     */

  void print(ostream& stream = cout) const;         /* Print the bit set. */

  string toString() const;                          /* Convert the Bitset into
                                                     * a string if 0's and 1's.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class BitIndexException;                          /* An exception class for
						     * reporting indexing
						     * errors.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

private:
  unsigned long int number_of_bits;                 /* Capacity of the bit
						     * set.
						     */

  BitArray bits;                                    /* Storage for the bits. */
};



/******************************************************************************
 *
 * An exception class for reporting bitset indexing errors.
 *
 *****************************************************************************/

class Bitset::BitIndexException : public Exception
{
public:
  BitIndexException();                              /* Constructor. */

  /* default copy constructor */

  ~BitIndexException() throw();                     /* Destructor. */

  BitIndexException&                                /* Assignment operator. */
    operator=(const BitIndexException& e);

  /* `what' inherited from class Exception */
};



/******************************************************************************
 *
 * Inline function definitions for class Bitset.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Bitset::Bitset(const unsigned long int capacity) :
  number_of_bits(capacity), bits(capacity)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Bitset.
 *
 * Argument:      capacity  --  Number of bits to allocate.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Bitset::Bitset
  (const BitArray& bitarray, const unsigned long int capacity) :
  number_of_bits(capacity), bits(capacity)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Bitset. Creates a Bitset from the
 *                first `capacity' bits of a BitArray.
 *
 * Arguments:     bitarray  --  A reference to a constant BitArray.
 *                capacity  --  Capacity of the new bit set.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  memcpy(static_cast<void*>(bits.bits),
	 static_cast<const void*>(bitarray.bits),
	 (capacity + 7) >> 3);
}

/* ========================================================================= */
inline Bitset::Bitset(const Bitset& bitset) :
  number_of_bits(bitset.number_of_bits), bits(bitset.number_of_bits)
/* ----------------------------------------------------------------------------
 *
 * Description:   Copy constructor for class Bitset.
 *
 * Argument:      bitset  --  Bitset to be copied.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  memcpy(static_cast<void*>(bits.bits),
	 static_cast<const void*>(bitset.bits.bits),
	 (number_of_bits + 7) >> 3);
}

/* ========================================================================= */
inline Bitset::~Bitset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Bitset.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Bitset::operator const BitArray&() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts a Bitset into a BitArray.
 *
 * Arguments:     None.
 *
 * Returns:       The bits associated with the Bitset object as a reference to
 *                a constant BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  return bits;
}

/* ========================================================================= */
inline Bitset& Bitset::operator=(const Bitset& bitset)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class Bitset.
 *
 * Arguments:     bitset  --  A reference to a Bitset to be assigned to `this'
 *                            Bitset.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (&bitset != this)
  {
    if ((number_of_bits >> 3) != (bitset.number_of_bits >> 3))
      bits.copy(bitset.bits, number_of_bits);
    else
      memcpy(static_cast<void*>(bits.bits),
	     static_cast<const void*>(bitset.bits.bits),
	     (bitset.number_of_bits + 7) >> 3);
  
    number_of_bits = bitset.number_of_bits;
  }
  return *this;
}

/* ========================================================================= */
inline bool Bitset::operator[](const unsigned long int index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the state of a bit in a bit set.
 *
 * Argument:      index  --  Index of the bit to test.
 *
 * Returns:       Truth value according to the state of the bit.
 *
 * ------------------------------------------------------------------------- */
{
  if (index >= number_of_bits)
    throw BitIndexException();

  return bits[index];
}

/* ========================================================================= */
inline bool Bitset::test(const unsigned long int index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the state of a bit in a bit set.
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
inline bool Bitset::operator==(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Compares two bitsets for equality.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       `true' if the bit sets have the same capacity and contents.
 *
 * ------------------------------------------------------------------------- */
{
  return (number_of_bits == bitset.number_of_bits
	  && bits.equal(bitset.bits, number_of_bits));
}

/* ========================================================================= */
inline bool Bitset::equal(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Compares two bitsets for equality.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       `true' if the bit sets have the same capacity and contents.
 *
 * ------------------------------------------------------------------------- */
{
  return operator==(bitset);
}

/* ========================================================================= */
inline bool Bitset::operator<=(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether `this' bitset is a subset of another Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       `true' if the capacity of `this' bitset is at most equal to
 *                the capacity of the target bitset and all bits set in `this'
 *                Bitset are also set in the other set.
 *
 * ------------------------------------------------------------------------- */
{
  return (number_of_bits <= bitset.number_of_bits
	  && bits.subset(bitset.bits, number_of_bits));
}

/* ========================================================================= */
inline bool Bitset::subset(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether `this' bitset is a subset of another Bitset.
 *
 * Argument:      bitset  --  Target of the comparison.
 *
 * Returns:       `true' if the capacity of `this' bitset is at most equal to
 *                the capacity of the target bitset and all bits set in `this'
 *                Bitset are also set in the other set.
 *
 * ------------------------------------------------------------------------- */
{
  return operator<=(bitset);
}

/* ========================================================================= */
inline unsigned long int Bitset::capacity() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the capacity of the bit set.
 *
 * Arguments:     None.
 *
 * Returns:       Capacity of the bit set.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_bits;
}

/* ========================================================================= */
inline unsigned long int Bitset::count() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the number of 1-bits in the Bitset.
 *
 * Arguments:     None.
 *
 * Returns:       Number of 1-bits in the Bitset.
 *
 * ------------------------------------------------------------------------- */
{
  return bits.count(number_of_bits);
}

/* ========================================================================= */
inline unsigned long int Bitset::hammingDistance(const Bitset& bitset) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the Hamming distance between two Bitsets (or between
 *                maximal prefixes of both Bitsets if the sets are of
 *                different capacity).
 *
 * Argument:      bitset  --  A reference to a constant Bitset.
 *
 * Returns:       The Hamming distance between the bitsets (or their maximal
 *                prefixes of equal length).
 *
 * ------------------------------------------------------------------------- */
{
  return bits.hammingDistance(bitset.bits, 
			      number_of_bits <= bitset.number_of_bits
			      ? number_of_bits
			      : bitset.number_of_bits);
}

/* ========================================================================= */
inline void Bitset::set()
/* ----------------------------------------------------------------------------
 *
 * Description:   Fills the bit set with 1-bits.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits.set(number_of_bits);
}

/* ========================================================================= */
inline void Bitset::setBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Sets a single bit in the bit set.
 *
 * Argument:      index  --  Index of the bit to be set.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (index >= number_of_bits)
    throw BitIndexException();

  bits.setBit(index);
}

/* ========================================================================= */
inline void Bitset::clear()
/* ----------------------------------------------------------------------------
 *
 * Description:   Fills the bit set with 0-bits.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits.clear(number_of_bits);
}

/* ========================================================================= */
inline void Bitset::clearBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Clears a single bit in the bit set.
 *
 * Argument:      index  --  Index of the bit to be cleared.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (index >= number_of_bits)
    throw BitIndexException();

  bits.clearBit(index);
}

/* ========================================================================= */
inline void Bitset::flip()
/* ----------------------------------------------------------------------------
 *
 * Description:   Flips all bits in the bit set.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits.flip(number_of_bits);
}

/* ========================================================================= */
inline void Bitset::flipBit(const unsigned long int index)
/* ----------------------------------------------------------------------------
 *
 * Description:   Switches the state of a single bit in the bit set.
 *
 * Arguments:     index  --  Index of the bit.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (index >= number_of_bits)
    throw BitIndexException();

  bits.flipBit(index);
}

/* ========================================================================= */
inline unsigned long int Bitset::find() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Finds the index of the first 1-bit in the bit set.
 *
 * Arguments:     None.
 *
 * Returns:       Index of the first 1-bit in the bit set (or
 *                `this->number_of_bits' if the bit set is empty).
 *
 * ------------------------------------------------------------------------- */
{
  return bits.find(number_of_bits);
}

/* ========================================================================= */
inline void Bitset::print(ostream& stream) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes the Bitset into a stream.
 *
 * Argument:      stream  --  A reference to an output stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bits.print(number_of_bits, stream);
}

/* ========================================================================= */
inline string Bitset::toString() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts the Bitset into a string.
 *
 * Arguments:     None.
 *
 * Returns:       A string representation of the Bitset.
 *
 * ------------------------------------------------------------------------- */
{
  return bits.toString(number_of_bits);
}



/******************************************************************************
 *
 * Inline function definitions for class Bitset::BitIndexException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Bitset::BitIndexException::BitIndexException() :
  Exception("Index out of range.")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Bitset::BitIndexException. Creates a
 *                new exception object.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Bitset::BitIndexException::~BitIndexException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Bitset::BitIndexException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Bitset::BitIndexException& Bitset::BitIndexException::operator=
  (const Bitset::BitIndexException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class Bitset::BitIndexException.
 *
 * Arguments:     e  --  A reference to another Bitset::BitIndexException.
 *
 * Returns:       A reference to the assigned exception object.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}
    
#endif /* !BITSET_H */
