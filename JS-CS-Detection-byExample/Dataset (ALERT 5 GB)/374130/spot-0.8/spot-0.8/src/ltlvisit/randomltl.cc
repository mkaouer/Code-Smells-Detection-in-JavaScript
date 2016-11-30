// Copyright (C) 2008, 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2005 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
// Pierre et Marie Curie.
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
#include <algorithm>
#include "randomltl.hh"
#include "ltlast/allnodes.hh"
#include "misc/random.hh"
#include <iostream>
#include <cstring>

namespace spot
{
  namespace ltl
  {
    namespace
    {
      formula*
      ap_builder(const random_ltl* rl, int n)
      {
	assert(n == 1);
	(void) n;
	atomic_prop_set::const_iterator i = rl->ap()->begin();
	std::advance(i, mrand(rl->ap()->size()));
	return (*i)->clone();
      }

      formula*
      true_builder(const random_ltl*, int n)
      {
	assert(n == 1);
	(void) n;
	return constant::true_instance();
      }

      formula*
      false_builder(const random_ltl*, int n)
      {
	assert(n == 1);
	(void) n;
	return constant::false_instance();
      }

      template <unop::type Op>
      formula*
      unop_builder(const random_ltl* rl, int n)
      {
	assert(n >= 2);
	return unop::instance(Op, rl->generate(n - 1));
      }

      template <binop::type Op>
      formula*
      binop_builder(const random_ltl* rl, int n)
      {
	assert(n >= 3);
	--n;
	int l = rrand(1, n - 1);
	return binop::instance(Op, rl->generate(l), rl->generate(n - l));
      }

      template <multop::type Op>
      formula*
      multop_builder(const random_ltl* rl, int n)
      {
	assert(n >= 3);
	--n;
	int l = rrand(1, n - 1);
	return multop::instance(Op, rl->generate(l), rl->generate(n - l));
      }

    } // anonymous

    void
    random_ltl::op_proba::setup(const char* name, int min_n, builder build)
    {
      this->name = name;
      this->min_n = min_n;
      this->proba = 1.0;
      this->build = build;
    }

    namespace
    {
      const int proba_size = 16;
    }

    random_ltl::random_ltl(const atomic_prop_set* ap)
    {
      ap_ = ap;
      proba_ = new op_proba[proba_size];
      proba_[0].setup("ap",      1, ap_builder);
      proba_[1].setup("false",   1, false_builder);
      proba_[2].setup("true",    1, true_builder);
      proba_2_ = proba_ + 3;
      proba_[3].setup("not",     2, unop_builder<unop::Not>);
      proba_[4].setup("F",       2, unop_builder<unop::F>);
      proba_[5].setup("G",       2, unop_builder<unop::G>);
      proba_[6].setup("X",       2, unop_builder<unop::X>);
      proba_[7].setup("equiv",   3, binop_builder<binop::Equiv>);
      proba_[8].setup("implies", 3, binop_builder<binop::Implies>);
      proba_[9].setup("xor",     3, binop_builder<binop::Xor>);
      proba_[10].setup("R",      3, binop_builder<binop::R>);
      proba_[11].setup("U",      3, binop_builder<binop::U>);
      proba_[12].setup("W",      3, binop_builder<binop::W>);
      proba_[13].setup("M",      3, binop_builder<binop::M>);
      proba_[14].setup("and",    3, multop_builder<multop::And>);
      proba_[15].setup("or",     3, multop_builder<multop::Or>);

      proba_[0].proba = ap_->size();

      update_sums();
    }

    random_ltl::~random_ltl()
    {
      delete[] proba_;
    }

    void
    random_ltl::update_sums()
    {
      total_1_ = 0.0;
      total_2_ = 0.0;
      total_2_and_more_ = 0.0;
      for (int i = 0; i < proba_size; ++i)
	{
	  if (proba_[i].min_n == 1)
	    total_1_ += proba_[i].proba;
	  else if (proba_[i].min_n == 2)
	    total_2_ += proba_[i].proba;
	  else if (proba_[i].min_n > 2)
	    total_2_and_more_ += proba_[i].proba;
	  else
	    assert(!"unexpected max_n");
	}
      total_2_and_more_ += total_2_;
      assert(total_1_ != 0.0);
      assert(total_2_ != 0.0);
      assert(total_2_and_more_ != 0.0);
    }

    formula*
    random_ltl::generate(int n) const
    {
      assert(n > 0);
      if (n == 1)
	{
	  double r = drand() * total_1_;
	  op_proba* p = proba_;
	  double s = p->proba;
	  while (s < r)
	    {
	      ++p;
	      s += p->proba;
	    }
	  assert(p->min_n == 1);
	  return p->build(this, n);
	}
      else if (n == 2)
	{
	  double r = drand() * total_2_;
	  op_proba* p = proba_2_;
	  double s = p->proba;
	  while (s < r)
	    {
	      ++p;
	      s += p->proba;
	    }
	  assert(p->min_n == 2);
	  return p->build(this, n);
	}
      else
	{
	  double r = drand() * total_2_and_more_;
	  op_proba* p = proba_2_;
	  double s = p->proba;
	  while (s < r)
	    {
	      ++p;
	      s += p->proba;
	    }
	  assert(p->min_n >= 2);
	  return p->build(this, n);
	}
    }

    const char*
    random_ltl::parse_options(char* options)
    {
      char* key = strtok(options, "=\t, :;");
      while (key)
	{
	  char* value = strtok(0, "=\t, :;");
	  if (value == 0)
	    return key;

	  char* endptr;
	  double res = strtod(value, &endptr);
	  if (*endptr)
	    return value;

	  int i;
	  for (i = 0; i < proba_size; ++i)
	    {
	      if (('a' <= *proba_[i].name && *proba_[i].name <= 'z'
		   && !strcasecmp(proba_[i].name, key))
		  || !strcmp(proba_[i].name, key))
		{
		  proba_[i].proba = res;
		  break;
		}
	    }
	  if (i == proba_size)
	    return key;

	  key = strtok(0, "=\t, :;");
	}
      update_sums();
      return 0;
    }

    std::ostream&
    random_ltl::dump_priorities(std::ostream& os) const
    {
      for (int i = 0; i < proba_size; ++i)
	os << proba_[i].name << "\t" << proba_[i].proba << std::endl;
      return os;
    }

  } // ltl
} // spot
