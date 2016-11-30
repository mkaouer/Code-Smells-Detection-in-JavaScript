// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE).
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

#include "snf.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/visitor.hh"
#include "ltlvisit/tostring.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      // E°
      class snf_visitor: public visitor
      {
	const formula* result_;
	snf_cache* cache_;
      public:

	snf_visitor(snf_cache* c): cache_(c)
	{
	}

	const formula*
	result() const
	{
	  return result_;
	}

	void
	visit(const atomic_prop*)
	{
	  assert(!"unexpected operator");
	}

	void
	visit(const constant* c)
	{
	  assert(c == constant::empty_word_instance());
	  result_ = constant::false_instance();
	}

	void
	visit(const bunop* bo)
	{
	  bunop::type op = bo->op();
	  switch (op)
	    {
	    case bunop::Star:
	      assert(bo->accepts_eword());
	      // Strip the star.
	      result_ = recurse(bo->child());
	      break;
	    }
	}

	void
	visit(const unop*)
	{
	  assert(!"unexpected operator");
	}

	void
	visit(const binop*)
	{
	  assert(!"unexpected operator");
	}

	void
	visit(const automatop*)
	{
	  assert(!"unexpected operator");
	}

	void
	visit(const multop* mo)
	{
	  multop::type op = mo->op();
	  switch (op)
	    {
	    case multop::And:
	    case multop::Or:
	    case multop::Fusion:
	      assert(!"unexpected operator");
	      break;
	    case multop::Concat:
	    case multop::AndNLM:
	      // Let F designate expressions that accept [*0],
	      // and G designate expressions that do not.

	      // (G₁;G₂;G₃)° = G₁;G₂;G₃
	      // (G₁;F₂;G₃)° = (G₁°);F₂;(G₃°) = G₁;F₂;G₃
	      // because there is nothing to do recursively on a G.
	      //
	      // AndNLM can be dealt with similarly.
	      //
	      // This case is already handled in recurse().
	      // if we reach this switch, we only have to
	      // deal with...
	      //
	      // (F₁;F₂;F₃)° = (F₁°)|(F₂°)|(F₃°)
	      // (F₁&F₂&F₃)° = (F₁°)|(F₂°)|(F₃°)
	      // so we fall through to the OrRat case...
	    case multop::OrRat:
	      assert(mo->accepts_eword());
	      {
		unsigned s = mo->size();
		multop::vec* v = new multop::vec;
		v->reserve(s);
		for (unsigned pos = 0; pos < s; ++pos)
		  v->push_back(recurse(mo->nth(pos)));
		result_ = multop::instance(multop::OrRat, v);
	      }
	      break;
	    case multop::AndRat:
	      // FIXME: Can we deal with AndRat in a better way
	      // when it accepts [*0]?
	      result_ = mo->clone();
	      break;
	    }
	}

	const formula*
	recurse(const formula* f)
	{
	  if (!f->accepts_eword())
	    return f->clone();

	  if (cache_)
	    {
	      snf_cache::const_iterator i = cache_->find(f);
	      if (i != cache_->end())
		return i->second->clone();
	    }

	  f->accept(*this);

	  if (cache_)
	    (*cache_)[f->clone()] = result_->clone();
	  return result_;
	}
      };

    }


    const formula*
    star_normal_form(const formula* sere, snf_cache* cache)
    {
      snf_visitor v(cache);
      return v.recurse(sere);
    }

  }
}
