// Copyright (C) 2008, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_STATS_HH
# define SPOT_TGBAALGOS_STATS_HH

#include "tgba/tgba.hh"
#include <iosfwd>
#include "misc/formater.hh"

namespace spot
{

  /// \addtogroup tgba_misc
  /// @{

  struct tgba_statistics
  {
    unsigned transitions;
    unsigned states;

    tgba_statistics() { transitions = 0; states = 0; }
    std::ostream& dump(std::ostream& out) const;
  };

  struct tgba_sub_statistics: public tgba_statistics
  {
    unsigned sub_transitions;

    tgba_sub_statistics() { sub_transitions = 0; }
    std::ostream& dump(std::ostream& out) const;
  };

  /// \brief Compute statistics for an automaton.
  tgba_statistics stats_reachable(const tgba* g);
  /// \brief Compute subended statistics for an automaton.
  tgba_sub_statistics sub_stats_reachable(const tgba* g);


  class printable_formula: public printable_value<const ltl::formula*>
  {
  public:
    printable_formula&
    operator=(const ltl::formula* new_val)
    {
      val_ = new_val;
      return *this;
    }

    virtual void
    print(std::ostream& os, const char*) const;
  };

  /// \brief prints various statistics about a TGBA
  ///
  /// This object can be configured to display various statistics
  /// about a TGBA.  Some %-sequence of characters are interpreted in
  /// the format string, and replaced by the corresponding statistics.
  class stat_printer: protected formater
  {
  public:
    stat_printer(std::ostream& os, const char* format);

    /// \brief print the configured statistics.
    ///
    /// The \a f argument is not needed if the Formula does not need
    /// to be output.
    std::ostream&
    print(const tgba* aut, const ltl::formula* f = 0);

  private:
    const char* format_;

    printable_formula form_;
    printable_value<unsigned> states_;
    printable_value<unsigned> edges_;
    printable_value<unsigned> trans_;
    printable_value<unsigned> acc_;
    printable_value<unsigned> scc_;
    printable_value<unsigned> nondetstates_;
    printable_value<unsigned> deterministic_;
  };

  /// @}
}

#endif // SPOT_TGBAALGOS_STATS_HH
