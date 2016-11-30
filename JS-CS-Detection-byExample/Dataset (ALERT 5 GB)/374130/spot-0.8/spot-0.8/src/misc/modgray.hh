// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_MODGRAY_HH
# define SPOT_MISC_MODGRAY_HH

namespace spot
{

  /// \brief Loopless modular mixed radix Gray code iteration.
  /// \ingroup misc_tools
  ///
  /// This class is based on the loopless modular mixed radix gray
  /// code algorithm described in exercise 77 of "The Art of Computer
  /// Programming", Pre-Fascicle 2A (Draft of section 7.2.1.1:
  /// generating all n-tuples) by Donald E. Knuth.
  ///
  /// The idea is to enumerate the set of all n-tuples
  /// (a<sub>0</sub>,a<sub>1</sub>,...,a<sub>n-1</sub>) where each
  /// a<sub>j</sub> range over a distinct set (this is the <i>mixed
  /// radix</i> part), so that only one a<sub>j</sub> changes between
  /// two successive tuples of the iteration (that is the <i>Gray
  /// code</i> part), and that this changes occurs always in the same
  /// direction, cycling over the set a<sub>j</sub> must cover (i.e.,
  /// <i>modular</i>).  The algorithm is <i>loopless</i> in that
  /// computing the next tuple done without any loop, i.e., in
  /// constant time.
  ///
  /// This class does not need to know the type of the a<sub>j</sub>,
  /// it will handle them indirectly through three methods: a_first(),
  /// a_next(), and a_last().  These methods need to be implemented
  /// in a subclass for the particular type of a<sub>j</sub> at hand.
  ///
  /// The class itself offers four functions to control the iteration
  /// over the set of all the (a<sub>0</sub>,a<sub>1</sub>,...,
  /// a<sub>n-1</sub>) tuples: first(), next(), last(), and done().
  /// These functions are usually used as follows:
  /// \code
  ///    for (g.first(); !g.done(); g.next())
  ///       use the tuple
  /// \endcode
  /// How to use the tuple of course depends on the way
  /// it as been stored in the subclass.
  ///
  /// Finally, let's mention two differences between this algorithm
  /// and the one in Knuth's book.  This version of the algorithm does
  /// not need to know the radixes (i.e., the size of set of each
  /// a<sub>j</sub>) beforehand: it will discover them on-the-fly when
  /// a_last(j) first return true.  It will also work with
  /// a<sub>j</sub> that cannot be changed.  (This is achieved by
  /// reindexing the elements through \c non_one_radixes_, to consider
  /// only the elements with a non-singleton range.)
  class loopless_modular_mixed_radix_gray_code
  {
  public:
    /// Constructor.
    ///
    /// \param n The size of the tuples to enumerate.
    loopless_modular_mixed_radix_gray_code(int n);

    virtual ~loopless_modular_mixed_radix_gray_code();

    /// \name iteration over an element in a tuple
    ///
    /// The class does not know how to modify the elements of the
    /// tuple (Knuth's a<sub>j</sub>s).  These changes are therefore
    /// abstracted using the a_first(), a_next(), and a_last()
    /// abstract functions.  These need to be implemented in
    /// subclasses as appropriate.
    ///
    /// @{

    /// Reset a<sub>j</sub> to its initial value.
    virtual void a_first(int j) = 0;
    /// \brief Advance a<sub>j</sub> to its next value.
    ///
    /// This will never be called if a_last(j) is true.
    virtual void a_next(int j) = 0;
    /// Whether a<sub>j</sub> is on its last value.
    virtual bool a_last(int j) const = 0;
    /// @}

    /// \name iteration over all the tuples
    /// @{

    /// \brief Reset the iteration to the first tuple.
    ///
    /// This must be called before calling any of next(), last(), or done().
    void first();

    /// \brief Whether this the last tuple.
    ///
    /// At this point it is still OK to call next(), and then done() will
    /// become true.
    bool
    last() const
    {
      return f_[0] == n_;
    }

    /// Whether all tuple have been explored.
    bool
    done() const
    {
      return done_;
    }

    /// \brief Update one item of the tuple and return its position.
    ///
    /// next() should never be called if done() is true.  If it is
    /// called on the last tuple (i.e., last() is true), it will return
    /// -1.  Otherwise it will update one a<sub>j</sub> of the tuple
    /// through one the a<sub>j</sub> handling functions, and return j.
    int next();
    /// @}

  protected:
    int n_;
    bool done_;
    int* a_;
    int* f_;
    int* m_;
    int* s_;
    int* non_one_radixes_;
  };

} // spot

# endif // SPOT_MISC_MODGRAY_HH
