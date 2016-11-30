// Copyright (C) 2010, 2012 Laboratoire de Recherche et Développement
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

#include "mark.hh"
#include "ltlast/allnodes.hh"
#include <cassert>
#include <algorithm>
#include <set>
#include <vector>
#include "ltlvisit/tostring.hh"
#include "misc/casts.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class simplify_mark_visitor : public visitor
      {
	const formula* result_;
	mark_tools* tools_;

      public:
	simplify_mark_visitor(mark_tools* t)
	  : tools_(t)
	{
	}

	~simplify_mark_visitor()
	{
	}

	const formula*
	result()
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ao)
	{
	  result_ = ao->clone();
	}

	void
	visit(const constant* c)
	{
	  result_ = c->clone();
	}

	void
	visit(const bunop* bo)
	{
	  result_ = bo->clone();
	}

	void
	visit(const unop* uo)
	{
	  result_ = uo->clone();
	}

	void
	visit(const automatop* ao)
	{
	  result_ = ao->clone();
	}

	void
	visit(const multop* mo)
	{
	  unsigned mos = mo->size();
	  multop::vec* res = new multop::vec;
	  switch (mo->op())
	    {
	    case multop::OrRat:
	    case multop::AndNLM:
	    case multop::AndRat:
	    case multop::Concat:
	    case multop::Fusion:
	      assert(!"unexpected operator");
	    case multop::Or:
	      for (unsigned i = 0; i < mos; ++i)
		res->push_back(recurse(mo->nth(i)));
	      break;
	    case multop::And:
	      {
		typedef std::set<std::pair<const formula*,
					   const formula*> > pset;
		pset empairs;
		typedef std::set<const formula*> sset;
		sset nmset;
		typedef std::vector<const binop*> unbinop;
		unbinop elist;
		typedef std::vector<const unop*> ununop;
		ununop nlist;

		for (unsigned i = 0; i < mos; ++i)
		  {
		    const formula* f = mo->nth(i);
		    if (const binop* bo = is_binop(f))
		      {
			switch (bo->op())
			  {
			  case binop::EConcatMarked:
			    empairs.insert(std::make_pair(bo->first(),
							  bo->second()));
			    // fall through
			  case binop::Xor:
			  case binop::Implies:
			  case binop::Equiv:
			  case binop::U:
			  case binop::W:
			  case binop::M:
			  case binop::R:
			  case binop::UConcat:
			    res->push_back(recurse(f));
			    break;
			  case binop::EConcat:
			    elist.push_back(bo);
			    break;
			  }
		      }
		    if (const unop* uo = is_unop(f))
		      {
			switch (uo->op())
			  {
			  case unop::NegClosureMarked:
			    nmset.insert(uo->child());
			    // fall through
			  case unop::Not:
			  case unop::X:
			  case unop::F:
			  case unop::G:
			  case unop::Finish:
			  case unop::Closure:
			    res->push_back(recurse(f));
			    break;
			  case unop::NegClosure:
			    nlist.push_back(uo);
			    break;
			  }
		      }
		    else
		      {
			res->push_back(recurse(f));
		      }
		  }
		// Keep only the non-marked EConcat for which we
		// have not seen a similar EConcatMarked.
		for (unbinop::const_iterator i = elist.begin();
		     i != elist.end(); ++i)
		  if (empairs.find(std::make_pair((*i)->first(),
						  (*i)->second()))
		      == empairs.end())
		    res->push_back((*i)->clone());
		// Keep only the non-marked NegClosure for which we
		// have not seen a similar NegClosureMarked.
		for (ununop::const_iterator i = nlist.begin();
		     i != nlist.end(); ++i)
		  if (nmset.find((*i)->child()) == nmset.end())
		    res->push_back((*i)->clone());
	      }
	    }
	  result_ = multop::instance(mo->op(), res);
	}

	void
	visit(const binop* bo)
	{
	  result_ = bo->clone();
	}

	const formula*
	recurse(const formula* f)
	{
	  return tools_->simplify_mark(f);
	}
      };


      class mark_visitor : public visitor
      {
	const formula* result_;
	mark_tools* tools_;

      public:
	mark_visitor(mark_tools* t)
	  : tools_(t)
	{
	}
	~mark_visitor()
	{
	}

	const formula*
	result()
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ap)
	{
	  result_ = ap->clone();
	}

	void
	visit(const constant* c)
	{
	  result_ = c->clone();
	}

	void
	visit(const bunop* bo)
	{
	  result_ = bo->clone();
	}

	void
	visit(const unop* uo)
	{
	  switch (uo->op())
	    {
	    case unop::Not:
	    case unop::X:
	    case unop::F:
	    case unop::G:
	    case unop::Finish:
	    case unop::Closure:
	    case unop::NegClosureMarked:
	      result_ = uo->clone();
	      return;
	    case unop::NegClosure:
	      result_ = unop::instance(unop::NegClosureMarked,
				       uo->child()->clone());
	      return;
	    }
	  /* Unreachable code. */
	  assert(0);
	}

	void
	visit(const automatop* ao)
	{
	  result_ = ao->clone();
	}

	void
	visit(const multop* mo)
	{
	  multop::vec* res = new multop::vec;
	  unsigned mos = mo->size();
	  for (unsigned i = 0; i < mos; ++i)
	    res->push_back(recurse(mo->nth(i)));
	  result_ = multop::instance(mo->op(), res);
	}

	void
	visit(const binop* bo)
	{
	  switch (bo->op())
	    {
	    case binop::Xor:
	    case binop::Implies:
	    case binop::Equiv:
	      assert(!"mark not defined on logic abbreviations");
	    case binop::U:
	    case binop::W:
	    case binop::M:
	    case binop::R:
	    case binop::UConcat:
	    case binop::EConcatMarked:
	      result_ = bo->clone();
	      return;
	    case binop::EConcat:
	      {
		const formula* f1 = bo->first()->clone();
		const formula* f2 = bo->second()->clone();
		result_ = binop::instance(binop::EConcatMarked, f1, f2);
		return;
	      }
	    }
	  /* Unreachable code. */
	  assert(0);
	}

	const formula*
	recurse(const formula* f)
	{
	  return tools_->mark_concat_ops(f);
	}
      };

    }

    mark_tools::mark_tools()
      : simpvisitor_(new simplify_mark_visitor(this)),
	markvisitor_(new mark_visitor(this))
    {
    }


    mark_tools::~mark_tools()
    {
      delete simpvisitor_;
      delete markvisitor_;
      {
	f2f_map::iterator i = simpmark_.begin();
	f2f_map::iterator end = simpmark_.end();
	while (i != end)
	  {
	    f2f_map::iterator old = i++;
	    old->second->destroy();
	    old->first->destroy();
	  }
      }
      {
	f2f_map::iterator i = markops_.begin();
	f2f_map::iterator end = markops_.end();
	while (i != end)
	  {
	    f2f_map::iterator old = i++;
	    old->second->destroy();
	    old->first->destroy();
	  }
      }
    }

    const formula*
    mark_tools::mark_concat_ops(const formula* f)
    {
      f2f_map::iterator i = markops_.find(f);
      if (i != markops_.end())
	return i->second->clone();

      f->accept(*markvisitor_);

      const formula* r = down_cast<mark_visitor*>(markvisitor_)->result();
      markops_[f->clone()] = r->clone();
      return r;
    }

    const formula*
    mark_tools::simplify_mark(const formula* f)
    {
      if (!f->is_marked())
	return f->clone();

      f2f_map::iterator i = simpmark_.find(f);
      if (i != simpmark_.end())
	return i->second->clone();

      f->accept(*simpvisitor_);

      const formula* r =
	down_cast<simplify_mark_visitor*>(simpvisitor_)->result();
      simpmark_[f->clone()] = r->clone();
      return r;
    }

  }
}
