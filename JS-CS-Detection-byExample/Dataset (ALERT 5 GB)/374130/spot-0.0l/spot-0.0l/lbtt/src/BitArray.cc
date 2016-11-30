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

#ifdef __GNUC__
#pragma implementation
#endif /* __GNUC__ */

#include <config.h>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include "BitArray.h"
#include "Exception.h"

/******************************************************************************
 *
 * `bit_counts[i]' tells the number of 1-bits in the 8-bit integer `i'.
 *
 *****************************************************************************/

const unsigned char BitArray::bit_counts[] =
  {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2,
   3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3,
   3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3,
   4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4,
   3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5,
   6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4,
   4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5,
   6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5,
   3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3,
   4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6,
   6, 7, 6, 7, 7, 8};



/******************************************************************************
 *
 * Function definitions for class BitArray.
 *
 *****************************************************************************/

/* ========================================================================= */
BitArray::BitArray(const unsigned long int size)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BitArray. Creates a bit array of a
 *                given size.
 *
 * Argument:      size  --  Number of bits to allocate.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int bsize = (size + 7) >> 3;

  bits = new unsigned char[bsize];
}

/* ========================================================================= */
BitArray::~BitArray()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BitArray. Deallocates the memory used by
 *                the bit array.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  delete[] bits;
}

/* ========================================================================= */
void BitArray::copy
  (const BitArray& bitarray, const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Copy the first `bit_count' bits from `bitarray' to `this'
 *                BitArray object. (As a side effect, the capacity of `this'
 *                bit array is set to `bit_count'.)
 *
 * Argument:      bitarray   --  Reference to a constant bit array which should
 *                               be copied.
 *                bit_count  --  Number of bits to copy.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (this != &bitarray)
  {
    delete[] bits;

    unsigned long int bsize = (bit_count + 7) >> 3;

    bits = new unsigned char[bsize];
    memcpy(static_cast<void*>(bits), static_cast<const void*>(bitarray.bits),
	   bsize);
  }
}

/* ========================================================================= */
bool BitArray::equal
  (const BitArray& bitarray, const unsigned long int bit_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Test whether the first `bit_count' bits of `this' BitArray
 *                are equal to the correspoding bits of `bitarray'.
 *
 * Argument:      bitarray   --  Target of the comparison.
 *                bit_count  --  Number of bits to compare.
 *
 * Returns:       `true' if the compared bits agree and `false' otherwise.
 *
 * ------------------------------------------------------------------------- */
{
  if (bit_count > 0)
  {
    unsigned long int bsize = bit_count >> 3;

    for (unsigned long int i = 0; i < bsize; ++i)
    {
      if (bits[i] != bitarray.bits[i])
      return false;
    }

    if ((bit_count & 0x07) == 0)
      return true;
    
    unsigned char mask = (1 << (bit_count & 0x07)) - 1;

    if (((bits[bsize] ^ bitarray.bits[bsize]) & mask) != 0)
      return false;
  }

  return true;
}

/* ========================================================================= */
BitArray& BitArray::bitwiseOr
  (const BitArray& bitarray, const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Compute the bitwise disjunction of the first `bit_count' bits
 *                of two BitArrays (storing the result in `this' BitArray).
 *
 * Arguments:     bitarray   --  A reference to a constant BitArray.
 *                bit_count  --  Number of bits to include in the computation.
 *
 * Returns:       A reference to `this' BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int bsize = bit_count >> 3;

  for (unsigned long int i = 0; i < bsize; ++i)
    bits[i] |= bitarray.bits[i];

  if ((bit_count & 0x07) != 0)
    bits[bsize] |= (bitarray.bits[bsize] & ((1 << (bit_count & 7)) - 1));

  return *this;
}

/* ========================================================================= */
BitArray& BitArray::bitwiseAnd
  (const BitArray& bitarray, const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Compute the bitwise conjunction of the first `bit_count' bits
 *                of two BitArrays (storing the result in `this' BitArray).
 *
 * Arguments:     bitarray   --  A reference to a constant BitArray.
 *                bit_count  --  Number of bits to include in the computation.
 *
 * Returns:       A reference to `this' BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int bsize = bit_count >> 3;

  for (unsigned long int i = 0; i < bsize; ++i)
    bits[i] &= bitarray.bits[i];

  if ((bit_count & 0x07) != 0)
    bits[bsize] &= (bitarray.bits[bsize] | ~((1 << (bit_count & 7)) - 1));

  return *this;
}

/* ========================================================================= */
BitArray& BitArray::bitwiseXor
  (const BitArray& bitarray, const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Compute the bitwise "exclusive or" of the first `bit_count'
 *                bits of two BitArrays (storing the result in `this'
 *                BitArray).
 *
 * Arguments:     bitarray   --  A reference to a constant BitArray.
 *                bit_count  --  Number of bits to include in the computation.
 *
 * Returns:       A reference to `this' BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int bsize = bit_count >> 3;

  for (unsigned long int i = 0; i < bsize; ++i)
    bits[i] ^= bitarray.bits[i];

  if ((bit_count & 0x07) != 0)
    bits[bsize] ^= (bitarray.bits[bsize] & ((1 << (bit_count & 7)) - 1));

  return *this;
}

/* ========================================================================= */
bool BitArray::subset
  (const BitArray& bitarray, const unsigned long int bit_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the first `bit_count' bits of `this' BitArray
 *                are set also in `bitarray'.
 *
 * Argument       bitarray   --  Target of the comparison.
 *                bit_count  --  Number of bits to test.
 *
 * Returns:       Truth value depending on the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  if (bit_count > 0)
  {
    unsigned long int bsize = bit_count >> 3;

    for (unsigned long int i = 0; i < bsize; ++i)
    {
      if ((bits[i] & ~bitarray.bits[i]) != 0)
	return false;
    }

    if ((bit_count & 0x07) == 0)
      return true;

    unsigned char mask = (1 << (bit_count & 7)) - 1;

    if (((bits[bsize] & ~bitarray.bits[bsize]) & mask) != 0)
      return false;
  }

  return true;
}

/* ========================================================================= */
unsigned long int BitArray::count(const unsigned long int bit_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Counts the number of 1-bits in the first `bit_count' bits of
 *                the BitArray.
 *
 * Arguments:     bit_count  --  Number of bits to include in the computation.
 *
 * Returns:       Number of 1-bits in the first `bit_count' bits of the
 *                BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  if (bit_count == 0)
    return 0;

  unsigned long int bsize = bit_count >> 3;
  unsigned long int result = 0;

  for (unsigned long int i = 0; i < bsize; ++i)
    result += bit_counts[bits[i]];

  if ((bit_count & 0x07) == 0)
    return result;

  unsigned char mask = (1 << (bit_count & 7)) - 1;

  result += bit_counts[bits[bsize] & mask];

  return result;
}

/* ========================================================================= */
unsigned long int BitArray::hammingDistance
  (const BitArray& bitarray, const unsigned long int bit_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the Hamming distance (the number of bit positions in
 *                which two bit vectors differ) between two bit vectors
 *                comprising the first `bit_count' bits of two bit arrays.
 *
 * Argument:      bitarray   --  A reference to a constant BitArray.
 *                bit_count  --  Number of bits to include in the computation.
 *
 * Returns:       The Hamming distance between the leftmost `bit_count' bits of
 *                two BitArrays.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int result = 0;
  unsigned long int bsize = bit_count >> 3;

  for (unsigned long int i = 0; i < bsize; ++i)
    result += bit_counts[bits[i] ^ bitarray.bits[i]];

  if ((bit_count & 0x07) == 0)
    return result;

  unsigned char mask = (1 << (bit_count & 7)) - 1;

  result += bit_counts[(bits[bsize] ^ bitarray.bits[bsize]) & mask];

  return result;
}

/* ========================================================================= */
void BitArray::flip(const unsigned long int bit_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Changes the state of the first `bit_count' bits in the
 *                bit array.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long bsize = (bit_count + 7) >> 3;

  for (unsigned long int i = 0; i < bsize; ++i)
    bits[i] ^= 0xFF;
}

/* ========================================================================= */
unsigned long int BitArray::find(const unsigned long int max_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Finds the first 1-bit in the bit array with an index less
 *                than `max_count'.
 *
 * Arguments:     max_count  --  Upper bound for the bit index.
 *
 * Returns:       Index of the first 1-bit with an index less than `max_count'
 *                or `max_count' if there are no 1-bits with such an index in
 *                the array.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int bsize = (max_count + 7) >> 3;
  unsigned long int i;
  for (i = 0; i < bsize && bits[i] == 0; ++i)
    ;

  if (i == bsize)
    return max_count;

  unsigned char c = bits[i];
  i <<= 3;
  
  while ((c & 0x01) == 0 && i < max_count)
  {
    c >>= 1;
    ++i;
  }

  return i;
}

/* ========================================================================= */
string BitArray::toString(const unsigned long int bit_count) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts the first `bit_count' bits of the bit array to a
 *                string (a sequence of characters `0' and `1'). The leftmost
 *                bit in the string corresponds to the first bit in the array.
 *
 * Arguments:     bit_count  --  Number of bits to convert into a string.
 *
 * Returns:       String representation of the first `bit_count' bits of the
 *                bit array.
 *
 * ------------------------------------------------------------------------- */
{
#ifdef HAVE_SSTREAM
  ostringstream bitstring;
  print(bit_count, bitstring);
  return bitstring.str();
#else
  ostrstream bitstring;
  print(bit_count, bitstring);
  bitstring << ends;
  string result(bitstring.str());
  bitstring.freeze(0);
  return result;
#endif /* HAVE_SSTREAM */
}

/* ========================================================================= */
void BitArray::print(const unsigned long int bit_count, ostream& stream) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Prints the first `bit_count' bits of the bit array as a
 *                sequence of characters `0' and `1'. The bits are printed in
 *                the same order as they are in the array; thus, the first
 *                printed bit corresponds to the bit with the lowest index in
 *                the array.
 *
 * Arguments:     bit_count  --  Number of bits to print.
 *                stream     --  A reference to the output stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int i, j, bsize = (bit_count + 7) >> 3;
  unsigned short int maskbit;
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  j = 0;
  for (i = 0; i < bsize; ++i)
  {
    maskbit = 1;
    while (j < bit_count && maskbit <= 0x80)
    {
      estream << ((bits[i] & maskbit) ? '1' : '0');
      ++j;
      maskbit <<= 1;
    }
  }
}
