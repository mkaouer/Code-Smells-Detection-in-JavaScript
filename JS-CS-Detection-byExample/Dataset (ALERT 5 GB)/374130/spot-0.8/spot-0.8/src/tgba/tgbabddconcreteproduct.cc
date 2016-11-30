// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <cassert>
#include "tgbabddconcreteproduct.hh"

namespace spot
{
  namespace
  {
    /// \brief Helper class for product().
    ///
    /// As both automata are encoded using BDD, we just have
    /// to homogenize the variable numbers before ANDing the
    /// relations and initial states.
    class tgba_bdd_product_factory: public tgba_bdd_factory
    {
    public:
      tgba_bdd_product_factory(const tgba_bdd_concrete* left,
			       const tgba_bdd_concrete* right)
	: dict_(left->get_dict()),
	  left_(left),
	  right_(right),
	  data_(left_->get_core_data(), right_->get_core_data()),
	  init_(left_->get_init_bdd() & right_->get_init_bdd())
      {
	assert(dict_ == right->get_dict());
      }

      virtual
      ~tgba_bdd_product_factory()
      {
      }

      const tgba_bdd_core_data&
      get_core_data() const
      {
	return data_;
      }

      bdd
      get_init_state() const
      {
	return init_;
      }

    private:
      bdd_dict* dict_;
      const tgba_bdd_concrete* left_;
      const tgba_bdd_concrete* right_;
      tgba_bdd_core_data data_;
      bdd init_;
    };
  }

  tgba_bdd_concrete*
  product(const tgba_bdd_concrete* left, const tgba_bdd_concrete* right)
  {
    tgba_bdd_product_factory p(left, right);
    return new tgba_bdd_concrete(p, p.get_init_state());
  }
}
