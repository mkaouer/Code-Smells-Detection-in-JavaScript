// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include <utility>
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/contain.hh"
#include "ltl2taa.hh"

namespace spot
{
  namespace
  {
    using namespace ltl;

    /// \brief Recursively translate a formula into a TAA.
    class ltl2taa_visitor : public visitor
    {
    public:
      ltl2taa_visitor(taa_tgba_formula* res, language_containment_checker* lcc,
		      bool refined = false, bool negated = false)
	: res_(res), refined_(refined), negated_(negated),
	  lcc_(lcc), init_(), succ_(), to_free_()
      {
      }

      virtual
      ~ltl2taa_visitor()
      {
      }

      taa_tgba_formula*
      result()
      {
	for (unsigned i = 0; i < to_free_.size(); ++i)
	  to_free_[i]->destroy();
	res_->set_init_state(init_);
	return res_;
      }

      void
      visit(const atomic_prop* node)
      {
	const formula* f = node; // Handle negation
	if (negated_)
	{
	  f = unop::instance(unop::Not, node->clone());
	  to_free_.push_back(f);
	}
	init_ = f;
	std::vector<const formula*> dst;
	std::vector<const formula*> a;

	// A little hack to define a sink
	dst.push_back(unop::instance(unop::Finish, constant::true_instance()));
	to_free_.push_back(dst[0]);
	taa_tgba::transition* t = res_->create_transition(init_, dst);
	res_->add_condition(t, f->clone());
	succ_state ss = { dst, f, a };
	succ_.push_back(ss);
      }

      void
      visit(const constant* node)
      {
	init_ = node;
	switch (node->val())
	{
	  case constant::True:
	  {
	    std::vector<const formula*> dst;
	    std::vector<const formula*> a;
	    // A little hack to define a sink
	    dst.push_back(unop::instance(unop::Finish,
					 constant::true_instance()));
	    res_->create_transition(init_, dst);
	    succ_state ss = { dst, node, a };
	    succ_.push_back(ss);
	    to_free_.push_back(dst[0]);
	    return;
	  }
	  case constant::False:
	    return;
	  case constant::EmptyWord:
	    assert(!"unsupported operator");
	    return;
	}
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const unop* node)
      {
	negated_ = node->op() == unop::Not;
	ltl2taa_visitor v = recurse(node->child());

	init_ = node;
	switch (node->op())
	{
	  case unop::X:
	  {
	    std::vector<const formula*> dst;
	    std::vector<const formula*> a;
	    if (v.succ_.empty()) // Handle X(0)
	      return;
	    dst.push_back(v.init_);
	    res_->create_transition(init_, dst);
	    succ_state ss =
	      { dst, constant::true_instance(), a };
	    succ_.push_back(ss);
	    return;
	  }
	  case unop::F:
	  case unop::G:
	    assert(0); // TBD
	    return;
	  case unop::Not:
	    // Done in recurse
	    succ_ = v.succ_;
	    return;
	  case unop::Finish:
	  case unop::Closure:
	  case unop::NegClosure:
	  case unop::NegClosureMarked:
	    assert(!"unsupported operator");
	    return;
	}
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const bunop*)
      {
	assert(!"unsupported operator");
      }

      void
      visit(const binop* node)
      {
	ltl2taa_visitor v1 = recurse(node->first());
	ltl2taa_visitor v2 = recurse(node->second());

	init_ = node;
	std::vector<succ_state>::iterator i1;
	std::vector<succ_state>::iterator i2;
	taa_tgba::transition* t = 0;
	bool contained = false;
	bool strong = false;

	switch (node->op())
	{
	case binop::U:
	  strong = true;
	  // fall thru
	case binop::W:
	  if (refined_)
	    contained = lcc_->contained(node->second(), node->first());
	  for (i1 = v1.succ_.begin(); i1 != v1.succ_.end(); ++i1)
	    {
	      // Refined rule
	      if (refined_ && contained)
		i1->Q.erase
		  (remove(i1->Q.begin(), i1->Q.end(), v1.init_), i1->Q.end());

	      i1->Q.push_back(init_); // Add the initial state
	      if (strong)
		i1->acc.push_back(node->second());
	      t = res_->create_transition(init_, i1->Q);
	      res_->add_condition(t, i1->condition->clone());
	      if (strong)
		res_->add_acceptance_condition(t, node->second()->clone());
	      else
		for (unsigned i = 0; i < i1->acc.size(); ++i)
		  res_->add_acceptance_condition(t, i1->acc[i]->clone());
	      succ_.push_back(*i1);
	    }
	  for (i2 = v2.succ_.begin(); i2 != v2.succ_.end(); ++i2)
	    {
	      t = res_->create_transition(init_, i2->Q);
	      res_->add_condition(t, i2->condition->clone());
	      succ_.push_back(*i2);
	    }
	  return;
	case binop::M: // Strong Release
	  strong = true;
	case binop::R: // Weak Release
	  if (refined_)
	    contained = lcc_->contained(node->first(), node->second());

	  for (i2 = v2.succ_.begin(); i2 != v2.succ_.end(); ++i2)
	    {
	      for (i1 = v1.succ_.begin(); i1 != v1.succ_.end(); ++i1)
		{
		  std::vector<const formula*> u; // Union
		  std::vector<const formula*> a; // Acceptance conditions
		  std::copy(i1->Q.begin(), i1->Q.end(), ii(u, u.end()));
		  const formula* f = i1->condition->clone(); // Refined rule
		  if (!refined_ || !contained)
		    {
		      std::copy(i2->Q.begin(), i2->Q.end(), ii(u, u.end()));
		      f = multop::instance(multop::And, f,
					   i2->condition->clone());
		    }
		  to_free_.push_back(f);
		  t = res_->create_transition(init_, u);
		  res_->add_condition(t, f->clone());
		  succ_state ss = { u, f, a };
		  succ_.push_back(ss);
		}

	      if (refined_) // Refined rule
		i2->Q.erase
		  (remove(i2->Q.begin(), i2->Q.end(), v2.init_), i2->Q.end());


	      i2->Q.push_back(init_); // Add the initial state
	      t = res_->create_transition(init_, i2->Q);
	      res_->add_condition(t, i2->condition->clone());

	      if (strong)
		{
		  i2->acc.push_back(node->first());
		  res_->add_acceptance_condition(t, node->first()->clone());
		}
	      else if (refined_)
		for (unsigned i = 0; i < i2->acc.size(); ++i)
		  res_->add_acceptance_condition(t, i2->acc[i]->clone());
	      succ_.push_back(*i2);
	    }
	  return;
	case binop::Xor:
	case binop::Implies:
	case binop::Equiv:
	  assert(0); // TBD
	case binop::UConcat:
	case binop::EConcat:
	case binop::EConcatMarked:
	  assert(!"unsupported operator");
	}
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const multop* node)
      {
	bool ok = true;
	std::vector<ltl2taa_visitor> vs;
	for (unsigned n = 0; n < node->size(); ++n)
	{
	  vs.push_back(recurse(node->nth(n)));
	  if (vs[n].succ_.empty()) // Handle 0
	    ok = false;
	}

	init_ = node;
	std::vector<succ_state>::iterator i;
	taa_tgba::transition* t = 0;
	switch (node->op())
	{
	  case multop::And:
	  {
	    if (!ok)
	      return;
	    std::vector<succ_state> p = all_n_tuples(vs);
	    for (unsigned n = 0; n < p.size(); ++n)
	    {
	      if (refined_)
	      {
		std::vector<const formula*> v; // All sub initial states.
		sort(p[n].Q.begin(), p[n].Q.end());
		for (unsigned m = 0; m < node->size(); ++m)
		{
		  if (!binary_search(p[n].Q.begin(), p[n].Q.end(), vs[m].init_))
		    break;
		  v.push_back(vs[m].init_);
		}

		if (v.size() == node->size())
		{
		  std::vector<const formula*> Q;
		  sort(v.begin(), v.end());
		  for (unsigned m = 0; m < p[n].Q.size(); ++m)
		    if (!binary_search(v.begin(), v.end(), p[n].Q[m]))
		      Q.push_back(p[n].Q[m]);
		  Q.push_back(init_);
		  t = res_->create_transition(init_, Q);
		  res_->add_condition(t, p[n].condition->clone());
		  for (unsigned i = 0; i < p[n].acc.size(); ++i)
		    res_->add_acceptance_condition(t, p[n].acc[i]->clone());
		  succ_.push_back(p[n]);
		  continue;
		}
	      }
	      t = res_->create_transition(init_, p[n].Q);
	      res_->add_condition(t, p[n].condition->clone());
	      succ_.push_back(p[n]);
	    }
	    return;
	  }
	  case multop::Or:
	    for (unsigned n = 0; n < node->size(); ++n)
	      for (i = vs[n].succ_.begin(); i != vs[n].succ_.end(); ++i)
	      {
		t = res_->create_transition(init_, i->Q);
		res_->add_condition(t, i->condition->clone());
		succ_.push_back(*i);
	      }
	    return;
	  case multop::Concat:
	  case multop::Fusion:
	  case multop::AndNLM:
	  case multop::AndRat:
	  case multop::OrRat:
	    assert(!"unsupported operator");
	    return;
	}
	/* Unreachable code.  */
	assert(0);
      }

      void
      visit(const automatop* node)
      {
	(void) node;
	assert(!"unsupported operator");
      }

      ltl2taa_visitor
      recurse(const formula* f)
      {
	ltl2taa_visitor v(res_, lcc_, refined_, negated_);
	f->accept(v);
	for (unsigned i = 0; i < v.to_free_.size(); ++i)
	  to_free_.push_back(v.to_free_[i]);
	return v;
      }

    private:
      taa_tgba_formula* res_;
      bool refined_;
      bool negated_;
      language_containment_checker* lcc_;

      typedef std::insert_iterator<
	std::vector<const formula*>
      > ii;

      struct succ_state
      {
	std::vector<const formula*> Q; // States
	const formula* condition;
	std::vector<const formula*> acc;
      };

      const formula* init_;
      std::vector<succ_state> succ_;

      std::vector<const formula*> to_free_;

    public:
      std::vector<succ_state>
      all_n_tuples(const std::vector<ltl2taa_visitor>& vs)
      {
	std::vector<succ_state> product;

	std::vector<int> pos(vs.size());
	for (unsigned i = 0; i < vs.size(); ++i)
	  pos[i] = vs[i].succ_.size();

	while (pos[0] != 0)
	{
	  std::vector<const formula*> u; // Union
	  std::vector<const formula*> a; // Acceptance conditions
	  const formula* f = constant::true_instance();
	  for (unsigned i = 0; i < vs.size(); ++i)
	  {
	    if (vs[i].succ_.empty())
	      continue;
	    const succ_state& ss(vs[i].succ_[pos[i] - 1]);
	    std::copy(ss.Q.begin(), ss.Q.end(), ii(u, u.end()));
	    f = multop::instance(multop::And, ss.condition->clone(), f);
	    for (unsigned i = 0; i < ss.acc.size(); ++i)
	    {
	      const formula* g = ss.acc[i]->clone();
	      a.push_back(g);
	      to_free_.push_back(g);
	    }
	  }
	  to_free_.push_back(f);
	  succ_state ss = { u, f, a };
	  product.push_back(ss);

	  for (int i = vs.size() - 1; i >= 0; --i)
	  {
	    if (vs[i].succ_.empty())
	      continue;
	    if (pos[i] > 1 || (i == 0 && pos[0] == 1))
	    {
	      --pos[i];
	      break;
	    }
	    else
	      pos[i] = vs[i].succ_.size();
	  }
	}
	return product;
      }
    };
  } // anonymous

  taa_tgba*
  ltl_to_taa(const ltl::formula* f, bdd_dict* dict, bool refined_rules)
  {
    // TODO: s/unabbreviate_ltl/unabbreviate_logic/
    const ltl::formula* f1 = ltl::unabbreviate_ltl(f);
    const ltl::formula* f2 = ltl::negative_normal_form(f1);
    f1->destroy();

    spot::taa_tgba_formula* res = new spot::taa_tgba_formula(dict);
    bdd_dict b;
    language_containment_checker* lcc =
      new language_containment_checker(&b, false, false, false, false);
    ltl2taa_visitor v(res, lcc, refined_rules);
    f2->accept(v);
    taa_tgba* taa = v.result(); // Careful: before the destroy!
    f2->destroy();
    delete lcc;
    return taa;
  }
}
