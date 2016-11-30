// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE)
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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
// or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include <cassert>
#include <sstream>
#include <ctype.h>
#include <ostream>
#include <cstring>
#include "ltlast/allnodes.hh"
#include "ltlast/visitor.hh"
#include "lunabbrev.hh"
#include "wmunabbrev.hh"
#include "tostring.hh"


namespace spot
{
  namespace ltl
  {
    namespace
    {
      enum keyword {
	KFalse = 0,
	KTrue = 1,
	KEmptyWord = 2,
	KXor,
	KImplies,
	KEquiv,
	KU,
	KR,
	KW,
	KM,
	KEConcat,
	KEConcatNext,
	KEConcatMarked,
	KEConcatMarkedNext,
	KUConcat,
	KUConcatNext,
	KNot,
	KX,
	KF,
	KG,
	KOr,
	KOrRat,
	KAnd,
	KAndRat,
	KAndNLM,
	KConcat,
	KFusion,
      };

      const char* spot_kw[] = {
	"0",
	"1",
	"[*0]",
	" xor ",
	" -> ",
	" <-> ",
	" U ",
	" R ",
	" W ",
	" M ",
	"<>-> ",
	"<>=> ",
	"<>+> ",
	"<>=+> ",
	"[]-> ",
	"[]=> ",
	"!",
	"X",
	"F",
	"G",
	" | ",
	" | ",
	" & ",
	" && ",
	" & ",
	";",
	":",
      };

      const char* spin_kw[] = {
	"false", // 0 doesn't work from the command line
	"true",  // 1 doesn't work from the command line
	"[*0]",			// not supported
	" xor ",		// rewritten
	" -> ",			// rewritten, although supported
	" <-> ",		// rewritten, although supported
	" U ",
	" V ",
	" W ",			// rewritten, although supported
	" M ",			// rewritten
	"<>-> ",		// not supported
	"<>=> ",		// not supported
	"<>+> ",		// not supported
	"<>=+> ",		// not supported
	"[]-> ",		// not supported
	"[]=> ",		// not supported
	"!",
	"X",
	"<>",
	"[]",
	" || ",
	" || ",
	" && ",
	" && ",			// not supported
	" & ",			// not supported
	";",			// not supported
	":",			// not supported
      };

      const char* wring_kw[] = {
	"FALSE",
	"TRUE",
	"[*0]",			// not supported
	" ^ ",
	" -> ",
	" <-> ",
	" U ",
	" R ",
	" W ",			// rewritten
	" M ",			// rewritten
	"<>-> ",		// not supported
	"<>=> ",		// not supported
	"<>+> ",		// not supported
	"<>=+> ",		// not supported
	"[]-> ",		// not supported
	"[]=> ",		// not supported
	"!",
	"X",
	"F",
	"G",
	" + ",
	" | ",			// not supported
	" * ",
	" && ",			// not supported
	" & ",			// not supported
	";",			// not supported
	":",			// not supported
      };

      const char* utf8_kw[] = {
	"0",
	"1",
	"[*0]",
	"⊕",
	" → ",
	" ↔ ",
	" U ",
	" R ",
	" W ",
	" M ",
	"◇→ ",
	"◇⇒ ",
	"◇→̃ ",
	"◇⇒̃ ",
	"□→ ",
	"□⇒ ",
	"¬",
	"○",
	"◇",
	"□",
	"∨",
	" | ",
	"∧",
	" ∩ ",
	" & ",
	";",
	":",
      };

      static bool
      is_bare_word(const char* str)
      {
	// Bare words cannot be empty, start with the letter of a unary
	// operator, or be the name of an existing constant.  Also they
	// should start with an letter.
	if (!*str
	    || *str == 'F'
	    || *str == 'G'
	    || *str == 'X'
	    || !(isalpha(*str) || *str == '_')
	    || !strcasecmp(str, "true")
	    || !strcasecmp(str, "false"))
	  return false;
	// The remaining of the word must be alphanumeric.
	while (*++str)
	  if (!(isalnum(*str) || *str == '_'))
	    return false;
	return true;
      }

      // If the formula has the form (!b)[*], return b.
      static
      const formula*
      strip_star_not(const formula* f)
      {
	if (const bunop* s = is_Star(f))
	  if (const unop* n = is_Not(s->child()))
	    return n->child();
	return 0;
      }

      // If the formula as position i in multop mo has the form
      // (!b)[*];b with b being a Boolean formula, return b.
      static
      const formula*
      match_goto(const multop *mo, unsigned i)
      {
	assert(i + 1 < mo->size());
	const formula* b = strip_star_not(mo->nth(i));
	if (!b || !b->is_boolean())
	  return 0;
	if (mo->nth(i + 1) == b)
	  return b;
	return 0;
      }

      class to_string_visitor: public visitor
      {
      public:
	to_string_visitor(std::ostream& os,
			  bool full_parent = false,
			  bool ratexp = false,
			  const char** kw = spot_kw)
	  : os_(os), top_level_(true),
	    full_parent_(full_parent), in_ratexp_(ratexp),
	    kw_(kw)
	{
	}

	virtual
	~to_string_visitor()
	{
	}

	void
	openp() const
	{
	  os_ << (in_ratexp_ ? "{" : "(");
	}

	void
	closep() const
	{
	  os_ << (in_ratexp_ ? "}" : ")");
	}

	std::ostream&
	emit(int symbol)
	{
	  return os_ << kw_[symbol];
	}

	void
	visit(const atomic_prop* ap)
	{
	  std::string str = ap->name();
	  if (full_parent_)
	    os_ << "(";
	  if (!is_bare_word(str.c_str()))
	    {
	      // Spin 6 supports atomic propositions such as (a == 0)
	      // as long as they are enclosed in parentheses.
	      if (kw_ != spin_kw)
		os_ << '"' << str << '"';
	      else if (!full_parent_)
		os_ << '(' << str << ')';
	      else
		os_ << str;
	    }
	  else
	    {
	      os_ << str;
	    }
	  if (kw_ == wring_kw)
	    os_ << "=1";
	  if (full_parent_)
	    os_ << ")";
	}

	void
	visit(const constant* c)
	{
	  if (full_parent_)
	    openp();
	  switch (c->val())
	    {
	    case constant::False:
	      emit(KFalse);
	      break;
	    case constant::True:
	      emit(KTrue);
	      break;
	    case constant::EmptyWord:
	      emit(KEmptyWord);
	      break;
	    }
	  if (full_parent_)
	    closep();
	}

	void
	visit(const binop* bo)
	{
	  bool top_level = top_level_;
	  top_level_ = false;
	  if (!top_level)
	    openp();

	  bool onelast = false;

	  switch (bo->op())
	    {
	    case binop::UConcat:
	    case binop::EConcat:
	    case binop::EConcatMarked:
	      os_ << "{";
	      in_ratexp_ = true;
	      top_level_ = true;
	      {
		const multop* m = is_multop(bo->first(), multop::Concat);
		if (m)
		  {
		    unsigned s = m->size();
		    if (m->nth(s - 1) == constant::true_instance())
		      {
			const formula* tmp = m->all_but(s - 1);
			tmp->accept(*this);
			tmp->destroy();
			onelast = true;
			break;
		      }
		  }
	      }
	      // fall through
	    default:
	      bo->first()->accept(*this);
	      break;
	    }

	  switch (bo->op())
	    {
	    case binop::Xor:
	      emit(KXor);
	      break;
	    case binop::Implies:
	      emit(KImplies);
	      break;
	    case binop::Equiv:
	      emit(KEquiv);
	      break;
	    case binop::U:
	      emit(KU);
	      break;
	    case binop::R:
	      emit(KR);
	      break;
	    case binop::W:
	      emit(KW);
	      break;
	    case binop::M:
	      emit(KM);
	      break;
	    case binop::UConcat:
	      os_ << "}";
	      emit(onelast ? KUConcatNext : KUConcat);
	      in_ratexp_ = false;
	      top_level_ = top_level;
	      break;
	    case binop::EConcat:
	      if (bo->second() == constant::true_instance())
		{
		  os_ << "}!";
		  in_ratexp_ = false;
		  goto second_done;
		}
	      os_ << "}";
	      emit(onelast ? KEConcatNext : KEConcat);
	      in_ratexp_ = false;
	      top_level_ = false;
	      break;
	    case binop::EConcatMarked:
	      os_ << "}";
	      emit(onelast ? KEConcatMarkedNext : KEConcatMarked);
	      in_ratexp_ = false;
	      top_level_ = false;
	      break;
	    }

	  bo->second()->accept(*this);
	second_done:
	  if (!top_level)
	    closep();
	}

	void
	emit_bunop_child(const formula* b)
	{
	  // b[*] is OK, no need to print {b}[*].  However want braces
	  // for {!b}[*], the only unary operator that can be nested
	  // with [*] or any other BUnOp like [->i..j] or [=i..j].
	  formula::opkind ck = b->kind();
	  bool need_parent = (full_parent_
			      || ck == formula::UnOp
			      || ck == formula::BinOp
			      || ck == formula::MultOp);
	  if (need_parent)
	    openp();
	  b->accept(*this);
	  if (need_parent)
	    closep();
	}

	void
	visit(const bunop* bo)
	{
	  const formula* c = bo->child();
	  enum { Star, Goto, Equal } sugar = Star;
	  unsigned default_min = 0;
	  unsigned default_max = bunop::unbounded;

	  // Abbreviate "1[*]" as "[*]".
	  if (c != constant::true_instance())
	    {
	      bunop::type op = bo->op();
	      switch (op)
		{
		case bunop::Star:
		  if (const multop* mo = is_Concat(c))
		    {
		      unsigned s = mo->size();
		      if (s == 2)
			if (const formula* b = match_goto(mo, 0))
			  {
			    c = b;
			    sugar = Goto;
			  }
		    }
		  break;
		}

	      emit_bunop_child(c);
	    }

	  unsigned min = bo->min();
	  unsigned max = bo->max();
	  switch (sugar)
	    {
	    case Star:
	      if (min == 1 && max == bunop::unbounded)
		{
		  os_ << "[+]";
		  return;
		}
	      os_ << "[*";
	      break;
	    case Equal:
	      os_ << "[=";
	      break;
	    case Goto:
	      os_ << "[->";
	      default_min = 1;
	      default_max = 1;
	      break;
	    }

	  // Beware that the default parameters of the Goto operator are
	  // not the same as Star or Equal:
	  //
	  //   [->]   = [->1..1]
	  //   [->..] = [->1..unbounded]
	  //   [*]    = [*0..unbounded]
	  //   [*..]  = [*0..unbounded]
	  //   [=]    = [=0..unbounded]
	  //   [=..]  = [=0..unbounded]
	  //
	  // Strictly speaking [=] is not specified by PSL, and anyway we
	  // automatically rewrite Exp[=0..unbounded] as
	  // Exp[*0..unbounded], so we should never have to print [=]
	  // here.
	  //
	  // Also
	  //   [*..]  = [*0..unbounded]

	  if (min != default_min || max != default_max)
	    {
	      // Always print the min_, even when it is equal to
	      // default_min, this way we avoid ambiguities (like
	      // when reading [*..3] near [->..2])
	      os_ << min;
	      if (min != max)
		{
		  os_ << "..";
		  if (max != bunop::unbounded)
		    os_ << max;
		}
	    }
	  os_ << "]";
	}

	void
	visit(const unop* uo)
	{
	  top_level_ = false;
	  // The parser treats F0, F1, G0, G1, X0, and X1 as atomic
	  // propositions.  So make sure we output F(0), G(1), etc.
	  bool need_parent = (uo->child()->kind() == formula::Constant);
	  bool top_level = top_level_;
	  bool overline = false;

	  if (full_parent_)
	    {
	      need_parent = false; // These will be printed by each subformula

	      if (!top_level)
		openp();
	    }

	  switch (uo->op())
	    {
	    case unop::Not:
	      need_parent = false;
	      // If we negate a single letter in UTF-8, use a
	      // combining overline.
	      if (!full_parent_ && kw_ == utf8_kw)
		if (const ltl::atomic_prop* ap = is_atomic_prop(uo->child()))
		  if (ap->name().size() == 1
		      && is_bare_word(ap->name().c_str()))
		    {
		      overline = true;
		      break;
		    }
	      // If we negate an atomic proposition for Wring,
	      // output prop=0.
	      if (kw_ == wring_kw)
		if (const ltl::atomic_prop* ap = is_atomic_prop(uo->child()))
		  if (is_bare_word(ap->name().c_str()))
		    {
		      os_ << ap->name() << "=0";
		      goto skiprec;
		    }
	      emit(KNot);
	      break;
	    case unop::X:
	      emit(KX);
	      break;
	    case unop::F:
	      emit(KF);
	      break;
	    case unop::G:
	      emit(KG);
	      break;
	    case unop::Finish:
	      os_ << "finish";
	      need_parent = true;
	      break;
	    case unop::Closure:
	      os_ << "{";
	      in_ratexp_ = true;
	      top_level_ = true;
	      break;
	    case unop::NegClosure:
	      emit(KNot);
	      os_ << "{";
	      in_ratexp_ = true;
	      top_level_ = true;
	      break;
	    case unop::NegClosureMarked:
	      emit(KNot);
	      os_ << (kw_ == utf8_kw ? "̃{": "+{");
	      in_ratexp_ = true;
	      top_level_ = true;
	      break;
	    }

	  if (need_parent)
	    openp();
	  uo->child()->accept(*this);
	  if (need_parent)
	    closep();

	  switch (uo->op())
	    {
	    case unop::Closure:
	    case unop::NegClosure:
	      os_ << "}";
	      in_ratexp_ = false;
	      top_level_ = false;
	      break;
	    default:
	      break;
	    }

	skiprec:

	  if (full_parent_ && !top_level)
	    closep();
	  else if (overline)
	    // The following string contains only the character U+0305
	    // (a combining overline).  It looks better than U+0304 (a
	    // combining overbar).
	    os_ << "̅";
	}

	void
	visit(const automatop* ao)
	{
	  // Warning: this string isn't parsable because the automaton
	  // operators used may not be defined.
	  bool top_level = top_level_;
	  top_level_ = false;
	  if (!top_level)
	    os_ << "(";
	  os_ << ao->get_nfa()->get_name() << "(";
	  unsigned max = ao->size();
	  ao->nth(0)->accept(*this);
	  for (unsigned n = 1; n < max; ++n)
	    {
	      os_ << ",";
	      ao->nth(n)->accept(*this);
	    }
	  os_ << ")";
	  if (!top_level)
	    os_ << ")";
	}

	void
	resugar_concat(const multop* mo)
	{
	  unsigned max = mo->size();

	  for (unsigned i = 0; i < max; ++i)
	    {
	      if (i > 0)
		emit(KConcat);
	      if (i + 1 < max)
		{
		  // Try to match (!b)[*];b
		  const formula* b = match_goto(mo, i);
		  if (b)
		    {
		      emit_bunop_child(b);

		      // Wait... maybe we are looking at (!b)[*];b;(!b)[*]
		      // in which case it's b[=1].
		      if (i + 2 < max && mo->nth(i) == mo->nth(i + 2))
			{
			  os_ << "[=1]";
			  i += 2;
			}
		      else
			{
			  os_ << "[->]";
			  ++i;
			}
		      continue;
		    }
		  // Try to match ((!b)[*];b)[*i..j];(!b)[*]
		  if (const bunop* s = is_Star(mo->nth(i)))
		    if (const formula* b2 = strip_star_not(mo->nth(i + 1)))
		      if (const multop* sc = is_Concat(s->child()))
			if (const formula* b1 = match_goto(sc, 0))
			  if (b1 == b2)
			    {
			      emit_bunop_child(b1);
			      os_ << "[=";
			      unsigned min = s->min();
			      os_ << min;
			      unsigned max = s->max();
			      if (max != min)
				{
				  os_ << "..";
				  if (max != bunop::unbounded)
				    os_ << max;
				}
			      os_ << "]";
			      ++i;
			      continue;
			    }
		}
	      mo->nth(i)->accept(*this);
	    }
	}


	void
	visit(const multop* mo)
	{
	  bool top_level = top_level_;
	  top_level_ = false;
	  if (!top_level)
	    openp();
	  multop::type op = mo->op();

	  // Handle the concatenation separately, because we want to
	  // "resugar" some patterns.
	  if (op == multop::Concat)
	    {
	      resugar_concat(mo);
	      if (!top_level)
		closep();
	      return;
	    }

	  mo->nth(0)->accept(*this);
	  keyword k = KFalse;	// Initialize to something to please GCC.
	  switch (op)
	    {
	    case multop::Or:
	      k = KOr;
	      break;
	    case multop::OrRat:
	      k = KOrRat;
	      break;
	    case multop::And:
	      k = in_ratexp_ ? KAndRat : KAnd;
	      break;
	    case multop::AndRat:
	      k = KAndRat;
	      break;
	    case multop::AndNLM:
	      k = KAndNLM;
	      break;
	    case multop::Concat:
	      // Handled by resugar_concat.
	      assert(0);
	      break;
	    case multop::Fusion:
	      k = KFusion;
	      break;
	    }
	  assert(k != KFalse);

	  unsigned max = mo->size();
	  for (unsigned n = 1; n < max; ++n)
	    {
	      emit(k);
	      mo->nth(n)->accept(*this);
	    }
	  if (!top_level)
	    closep();
	}
      protected:
	std::ostream& os_;
	bool top_level_;
	bool full_parent_;
	bool in_ratexp_;
	const char** kw_;
      };

    } // anonymous

    std::ostream&
    to_string(const formula* f, std::ostream& os, bool full_parent,
	      bool ratexp)
    {
      to_string_visitor v(os, full_parent, ratexp, spot_kw);
      f->accept(v);
      return os;
    }

    std::string
    to_string(const formula* f, bool full_parent, bool ratexp)
    {
      std::ostringstream os;
      to_string(f, os, full_parent, ratexp);
      return os.str();
    }

    std::ostream&
    to_utf8_string(const formula* f, std::ostream& os, bool full_parent,
	      bool ratexp)
    {
      to_string_visitor v(os, full_parent, ratexp, utf8_kw);
      f->accept(v);
      return os;
    }

    std::string
    to_utf8_string(const formula* f, bool full_parent, bool ratexp)
    {
      std::ostringstream os;
      to_utf8_string(f, os, full_parent, ratexp);
      return os.str();
    }

    std::ostream&
    to_spin_string(const formula* f, std::ostream& os, bool full_parent)
    {
      // Remove xor, ->, and <-> first.
      const formula* fu = unabbreviate_logic(f);
      // Also remove W and M.
      f = unabbreviate_wm(fu);
      fu->destroy();
      to_string_visitor v(os, full_parent, false, spin_kw);
      f->accept(v);
      f->destroy();
      return os;
    }

    std::string
    to_spin_string(const formula* f, bool full_parent)
    {
      std::ostringstream os;
      to_spin_string(f, os, full_parent);
      return os.str();
    }

    std::ostream&
    to_wring_string(const formula* f, std::ostream& os)
    {
      // Remove W and M.
      f = unabbreviate_wm(f);
      to_string_visitor v(os, true, false, wring_kw);
      f->accept(v);
      f->destroy();
      return os;
    }

    std::string
    to_wring_string(const formula* f)
    {
      std::ostringstream os;
      to_wring_string(f, os);
      return os.str();
    }

  }
}
