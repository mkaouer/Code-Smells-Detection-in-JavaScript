// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#ifndef SPOT_LTLVISIT_SYNTIMPL_HH
# define SPOT_LTLVISIT_SYNTIMPL_HH

#include "ltlast/formula.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Syntactic implication.
    /// \ingroup ltl_misc
    ///
    /// This comes from
    /// \verbatim
    /// @InProceedings{	  somenzi.00.cav,
    /// author  	= {Fabio Somenzi and Roderick Bloem},
    /// title		= {Efficient {B\"u}chi Automata for {LTL} Formulae},
    /// booktitle	= {Proceedings of the 12th International Conference on
    /// 		  Computer Aided Verification (CAV'00)},
    /// pages		= {247--263},
    /// year		= {2000},
    /// volume  	= {1855},
    /// series  	= {Lecture Notes in Computer Science},
    /// publisher	= {Springer-Verlag}
    /// }
    /// \endverbatim
    bool syntactic_implication(const formula* f1, const formula* f2);

    /// \brief Syntactic implication.
    /// \ingroup ltl_misc
    ///
    /// If right==false, true if !f1 < f2, false otherwise.
    /// If right==true, true if f1 < !f2, false otherwise.
    ///
    /// \see syntactic_implication
    bool syntactic_implication_neg(const formula* f1, const formula* f2,
				   bool right);
  }
}

#endif // SPOT_LTLVISIT_SYNTIMPL_HH
