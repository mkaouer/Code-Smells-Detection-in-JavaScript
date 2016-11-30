// Copyright (C) 2004, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_REDUCE_HH
# define SPOT_LTLVISIT_REDUCE_HH

#include "ltlast/formula.hh"
#include "ltlast/visitor.hh"

namespace spot
{
  namespace ltl
  {

    /// \addtogroup ltl_rewriting
    /// @{

    /// Options for spot::ltl::reduce.
    enum reduce_options
      {
	/// No reduction.
	Reduce_None = 0,
	/// Basic reductions.
	Reduce_Basics = 1,
	/// Somenzi & Bloem syntactic implication.
	Reduce_Syntactic_Implications = 2,
	/// Etessami & Holzmann eventuality and universality reductions.
	Reduce_Eventuality_And_Universality = 4,
	/// Tauriainen containment checks.
	Reduce_Containment_Checks = 8,
	/// Tauriainen containment checks (stronger version).
	Reduce_Containment_Checks_Stronger = 16,
	/// All reductions.
	Reduce_All = -1U
      };

    /// \brief Reduce a formula \a f.
    ///
    /// \param f the formula to reduce
    /// \param opt a conjonction of spot::ltl::reduce_options specifying
    ///            which optimizations to apply.
    /// \return the reduced formula
    formula* reduce(const formula* f, int opt = Reduce_All);
    /// @}

    /// \brief Check whether a formula is a pure eventuality.
    /// \ingroup ltl_misc
    ///
    /// Pure eventuality formulae are defined in
    /// \verbatim
    /// @InProceedings{	  etessami.00.concur,
    /// author  	= {Kousha Etessami and Gerard J. Holzmann},
    /// title		= {Optimizing {B\"u}chi Automata},
    /// booktitle	= {Proceedings of the 11th International Conference on
    /// 		  Concurrency Theory (Concur'2000)},
    /// pages		= {153--167},
    /// year		= {2000},
    /// editor  	= {C. Palamidessi},
    /// volume  	= {1877},
    /// series  	= {Lecture Notes in Computer Science},
    /// publisher	= {Springer-Verlag}
    /// }
    /// \endverbatim
    ///
    /// A word that satisfies a pure eventuality can be prefixed by
    /// anything and still satisfies the formula.
    bool is_eventual(const formula* f);

    /// \brief Check whether a formula is purely universal.
    /// \ingroup ltl_misc
    ///
    /// Purely universal formulae are defined in
    /// \verbatim
    /// @InProceedings{	  etessami.00.concur,
    /// author  	= {Kousha Etessami and Gerard J. Holzmann},
    /// title		= {Optimizing {B\"u}chi Automata},
    /// booktitle	= {Proceedings of the 11th International Conference on
    /// 		  Concurrency Theory (Concur'2000)},
    /// pages		= {153--167},
    /// year		= {2000},
    /// editor  	= {C. Palamidessi},
    /// volume  	= {1877},
    /// series  	= {Lecture Notes in Computer Science},
    /// publisher	= {Springer-Verlag}
    /// }
    /// \endverbatim
    ///
    /// Any (non-empty) suffix of a word that satisfies if purely
    /// universal formula also satisfies the formula.
    bool is_universal(const formula* f);
  }
}

#endif //  SPOT_LTLVISIT_REDUCE_HH
