/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#ifdef __GNUC__
#pragma implementation
#endif /* __GNUC__ */

#include <config.h>
#include <deque>
#include <stack>
#include <string>
#include "PathEvaluator.h"

namespace Ltl
{

/******************************************************************************
 *
 * Function definitions for class PathEvaluator.
 *
 *****************************************************************************/

/* ========================================================================= */
void PathEvaluator::reset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prepares the formula evaluator for a new computation.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  current_formula = static_cast<const LtlFormula*>(0);
  current_path = static_cast<const StateSpace*>(0);
  current_loop_state = 0;
  path_states.clear();

  for (map<const LtlFormula*, BitArray*, LtlFormula::ptr_less,
	   ALLOC(BitArray*) >::iterator it
	 = eval_info.begin();
       it != eval_info.end();
       ++it)
    delete it->second;

  eval_info.clear();
}

/* ========================================================================= */
bool PathEvaluator::evaluate
  (const LtlFormula& formula, const StateSpace& statespace,
   const vector<StateSpace::size_type, ALLOC(StateSpace::size_type) >&
     states_on_path,
   StateSpace::size_type loop_state)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates an LTL formula in a state space in which the states
 *                are connected into a non-branching sequence that ends in a
 *                loop.
 *
 * Arguments:     formula         --  Formula to be evaluated.
 *                statespace      --  State space from which the path is
 *                                    extracted.
 *                states_on_path  --  Mapping between states in the path and
 *                                    the states in `statespace' such that
 *                                    `statespace[states_on_path[i]]'
 *                                    corresponds to the ith state of the path.
 *                loop_state      --  Number of the state in the path to which
 *                                    the ``last'' state of the path is
 *                                    connected.
 *
 * Returns:       `true' if and only if the LTL formula holds in the path.
 *
 * ------------------------------------------------------------------------- */
{
  reset();

  if (states_on_path.empty() || loop_state >= states_on_path.size())
    return false;

  current_formula = &formula;
  current_path = &statespace;
  current_loop_state = loop_state;
  path_states = states_on_path;

  return eval();
}

/* ========================================================================= */
bool PathEvaluator::evaluate
  (const LtlFormula& formula, const StateSpace& statespace)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates an LTL formula in a state space in which the states
 *                are connected into a non-branching sequence that ends in a
 *                loop.
 *
 * Arguments:     formula     --  Formula to be evaluated.
 *                statespace  --  State space in which the formula should be
 *                                evaluated.
 *
 * Returns:       `true' if and only if the LTL formula holds in the path.
 *
 * ------------------------------------------------------------------------- */
{
  reset();

  if (statespace.empty())
    return false;

  current_formula = &formula;
  current_path = &statespace;

  map<StateSpace::size_type, StateSpace::size_type,
      less<StateSpace::size_type>, ALLOC(StateSpace::size_type) > ordering;

  StateSpace::size_type state = statespace.initialState();
  StateSpace::size_type state_count = 0;

  /*
   *  Construct a vector of state identifiers representing the path by
   *  traversing the state space until some state is encountered twice.
   */

  while (1)
  {
    path_states.push_back(state);
    ordering[state] = state_count;
    state_count++;

    if (statespace[state].edges().empty())
      throw Exception
	      ("PathEvaluator::compute: not a total transition relation");

    state = (*(statespace[state].edges().begin()))->targetNode();

    if (ordering.find(state) != ordering.end())
      break;
  }

  current_loop_state = ordering[state];

  return eval();
}

/* ========================================================================= */
bool PathEvaluator::eval()
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the model checking algorithm for paths.
 *
 * Arguments:     None.
 *
 * Returns:       `true' if and only if `this->current_formula' holds in
 *                the path described by the contents of `this->path_states'.
 *
 * ------------------------------------------------------------------------- */
{
  stack<const LtlFormula*, deque<const LtlFormula*,
                                 ALLOC(const LtlFormula*) > >
    subformula_stack;

  const LtlFormula* f;
  BitArray* val;

  current_formula->collectSubformulae(subformula_stack);

  try
  {
    while (!subformula_stack.empty())
    {
      if (::user_break)
	throw UserBreakException();

      /*
       *  Pop a formula from the subformula stack.
       */

      f = subformula_stack.top();
      subformula_stack.pop();

      /*
       *  Discard the current formula if its truth value is already known.
       */

      if (eval_info.find(f) != eval_info.end())
	continue;

      /*
       *  Otherwise allocate space for the evaluation results of the current
       *  formula and then evaluate it in all states of the path, using the
       *  truth values of its subformulae which have already been computed.
       */

      val = eval_info[f] = new BitArray(path_states.size());
      val->clear(path_states.size());

      switch (f->what())
      {
        case LTL_UNTIL :
        case LTL_FINALLY :
        case LTL_V :
        case LTL_GLOBALLY :
        case LTL_BEFORE :
	  {
	    StateSpace::size_type marker_state;
	    const LtlFormula* g, *h;
	    bool lfp, check_value_1, check_value_2, marker_valid = false;

	    switch (f->what())
	    {
	      case LTL_UNTIL :
		g = static_cast<const Until*>(f)->subformula1;
		h = static_cast<const Until*>(f)->subformula2;
		check_value_1 = check_value_2 = true;
		lfp = true;
		break;

	      case LTL_FINALLY :
		g = 0;
		h = static_cast<const Finally*>(f)->subformula;
		check_value_1 = check_value_2 = true;
		lfp = true;
		break;

	      case LTL_V :
		val->set(path_states.size());
		g = static_cast<const V*>(f)->subformula1;
		h = static_cast<const V*>(f)->subformula2;
		check_value_1 = check_value_2 = false;
		lfp = false;
		break;

	      case LTL_GLOBALLY :
		val->set(path_states.size());
		g = 0;
		h = static_cast<const Globally*>(f)->subformula;
		check_value_1 = check_value_2 = false;
		lfp = false;
		break;

	      default : /* LTL_BEFORE */
		val->set(path_states.size());
		g = static_cast<const Before*>(f)->subformula1;
		h = static_cast<const Before*>(f)->subformula2;
		check_value_1 = false;
		check_value_2 = true;
		lfp = false;
		break;
	    }

	    for (StateSpace::size_type state = 0; state < path_states.size();
		 state++)
	    {
	      if (eval_info[h]->test(state) == check_value_2)
              {
		val->flipBit(state);

		if (marker_valid)
                {
		  while (marker_state < state)
                  {
		    val->flipBit(marker_state);
		    marker_state++;
		  }
		  marker_valid = false;
		} 
	      }
	      else if (g == 0 || eval_info[g]->test(state) == check_value_1)
	      {
		if (!marker_valid)
                {
		  marker_valid = true;
		  marker_state = state;
		}
	      }
	      else
		marker_valid = false;
	    }

	    if (marker_valid && eval_info[f]->test(current_loop_state) == lfp)
            {
	      while (marker_state < path_states.size())
              {
		val->flipBit(marker_state);
		marker_state++;
	      }
	    }

	    break;
	  }

        case LTL_WEAK_UNTIL :
        case LTL_STRONG_RELEASE :
	  {
	    StateSpace::size_type marker_state;
	    const LtlFormula* g, *h;
	    bool check_value;
	    bool marker_valid = false;

	    if (f->what() == LTL_WEAK_UNTIL)
	    {
	      val->set(path_states.size());
	      h = static_cast<const WeakUntil*>(f)->subformula1;
	      g = static_cast<const WeakUntil*>(f)->subformula2;
	      check_value = false;
	    }
	    else
	    {
	      h = static_cast<const StrongRelease*>(f)->subformula1;
	      g = static_cast<const StrongRelease*>(f)->subformula2;
	      check_value = true;
	    }

	    for (StateSpace::size_type state = 0; state < path_states.size();
		 state++)
	    {
	      if (eval_info[g]->test(state) == check_value
		  && eval_info[h]->test(state) == check_value)
              {
		val->flipBit(state);

		if (marker_valid)
                {
		  while (marker_state < state)
                  {
		    val->flipBit(marker_state);
		    marker_state++;
		  }
		  marker_valid = false;
		} 
	      }
	      else if (eval_info[g]->test(state) == check_value)
	      {
		if (!marker_valid)
                {
		  marker_valid = true;
		  marker_state = state;
		}
	      }
	      else
		marker_valid = false;
	    }

	    if (marker_valid
		&& eval_info[f]->test(current_loop_state) == check_value)
            {
	      while (marker_state < path_states.size())
              {
		val->flipBit(marker_state);
		marker_state++;
	      }
	    }

	    break;
	  }

        case LTL_TRUE :
	  val->set(path_states.size());
	  break;

        case LTL_FALSE :
	  break;

        case LTL_ATOM :
	  {
	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      /*
	       * Note: BitArray operations cannot be used directly, since the
	       * width of the array in `current_path' might be less than the
	       * atomic proposition identifier.
	       */

	      if (f->evaluate((*current_path)[path_states[s]].positiveAtoms(),
			      current_path->numberOfPropositions()))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_NEGATION :
	  {
	    const LtlFormula* g = static_cast<const Not*>(f)->subformula;

	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (!eval_info[g]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_CONJUNCTION :
	  {
	    const LtlFormula* g = static_cast<const And*>(f)->subformula1;
	    const LtlFormula* h = static_cast<const And*>(f)->subformula2;

	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (eval_info[g]->test(s) && eval_info[h]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_DISJUNCTION :
	  {
	    const LtlFormula* g = static_cast<const Or*>(f)->subformula1;
	    const LtlFormula* h = static_cast<const Or*>(f)->subformula2;

	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (eval_info[g]->test(s) || eval_info[h]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_IMPLICATION :
	  {
	    const LtlFormula* g = static_cast<const Imply*>(f)->subformula1;
	    const LtlFormula* h = static_cast<const Imply*>(f)->subformula2;

	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (!eval_info[g]->test(s) || eval_info[h]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_EQUIVALENCE :
	  {
	    const LtlFormula* g = static_cast<const Equiv*>(f)->subformula1;
	    const LtlFormula* h = static_cast<const Equiv*>(f)->subformula2;
		  
	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (eval_info[g]->test(s) == eval_info[h]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        case LTL_XOR :
	  {
	    const LtlFormula* g = static_cast<const Xor*>(f)->subformula1;
	    const LtlFormula* h = static_cast<const Xor*>(f)->subformula2;

	    for (StateSpace::size_type s = 0; s < path_states.size(); s++)
	    {
	      if (eval_info[g]->test(s) != eval_info[h]->test(s))
		val->setBit(s);
	    }

	    break;
	  }

        default :  /* LTL_NEXT */
	  {
	    const LtlFormula* g = static_cast<const Next*>(f)->subformula;
	    StateSpace::size_type s;

	    for (s = 0; (s + 1) < path_states.size(); s++)
	    {
	      if (eval_info[g]->test(s + 1))
		val->setBit(s);
	    }

	    if (eval_info[g]->test(current_loop_state))
	      val->setBit(s);

	    break;
	  }
      }
    }
  }
  catch (...)
  {
    reset();
    throw;
  }

  return eval_info[current_formula]->test(0);
}

/* ========================================================================= */
bool PathEvaluator::getResult(StateSpace::size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the model checking result in the path that begins at
 *                the `state'th state of the path described by the vector
 *                `this->path_states'.
 *
 * Arguments:     state  --  Index of a state in the path.
 *
 * Returns:       `true' if and only if the LTL formula `this->current_formula'
 *                holds in the path that begins at the given index.
 *
 * ------------------------------------------------------------------------- */
{
  if (eval_info.empty())
    return false;

  return eval_info.find(current_formula)->second->test(state);
}

/* ========================================================================= */
void PathEvaluator::print(ostream& stream, const int indent) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays a proof or a refutation for the formula
 *                `this->current_formula' in the state space described by the
 *                contents of `this->path_states'.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave on the left of output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (eval_info.empty())
    return;

  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  recPrint(estream, indent, current_formula, 0);
}

/* ========================================================================= */
void PathEvaluator::recPrint
  (Exceptional_ostream& estream, const int indent, const LtlFormula* f,
   StateSpace::size_type state) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays a recursive proof or a refutation for the formula
 *                `f' in the `state'th state of the state space described by
 *                the contents of `this->path_states'.
 *
 * Arguments:     estream  --  A reference to an exception-aware output
 *                             stream.
 *                indent   --  Number of spaces to leave to the left of output.
 *                state    --  Index of a state in the path.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  static string line_prefix = "";

  const bool prove_true = eval_info.find(f)->second->test(state);

  estream << string(indent, ' ')
             + (line_prefix.size() == 0
		? ""
		: line_prefix.substr(0, line_prefix.size() - 4) + "+-> ")
             + "M,<s"
	  << path_states[state]
          << string(", ...> |") + (prove_true ? '=' : '/') + "= "
          << *f;

  switch (f->what())
  {
    case LTL_ATOM :
    case LTL_TRUE :
    case LTL_FALSE :
      estream << '\n';
      return;

    case LTL_NEGATION :
      /*
       *  To display a proof (refutation) for a negated formula, display a 
       *  refutation (proof) for the non-negated formula.
       */

      line_prefix += "    ";

      estream << " :\n";
      recPrint(estream, indent, static_cast<const Not*>(f)->subformula, state);

      break;

    case LTL_NEXT :
      {
	/*
	 *  To display a proof or refutation for a Next formula, display a
	 *  proof or refutation for the subformula of the Next operator in
	 *  the next state of the path.
	 */

	estream << " :\n" + string(indent, ' ') + line_prefix + "+-> s"
	        << path_states[state]
	        << " --> s";

	state++;
	if (state == path_states.size())
	  state = current_loop_state;

	estream << path_states[state] << '\n';

	line_prefix += "    ";

	recPrint(estream, indent, static_cast<const Next*>(f)->subformula,
		 state);

	break;
      }

    case LTL_CONJUNCTION : case LTL_DISJUNCTION : case LTL_IMPLICATION :
    case LTL_EQUIVALENCE : case LTL_XOR :
      {
	/*
	 *  Formula: f1 OP f2
	 *
	 *   proof/refutation       OP        what will be shown
	 *   ------------------------------------------------------------------
	 *   proof                  /\        proofs for `f1' and `f2'
	 *
	 *   refutation             /\        refutation for `f1' or `f2'
	 *
	 *   proof                  \/        proof for `f1' or `f2'
	 *
	 *   refutation             \/        refutation for `f1 and `f2'
	 *
	 *   proof                  ->        proof for `f2' or refutation for
	 *                                       `f1'
	 *
	 *   refutation             ->        proof for `f1' and refutation for
	 *                                       `f2'
	 *
	 *   proof                 <->        proofs or refutations for both
	 *                                       `f1' and `f2'
	 *
	 *   refutation            <->        proof for `f1' and refutation for
	 *                                       `f2' or vice versa
	 *
	 *   proof                 xor        proof for `f1' and refutation for
	 *                                       `f2' or vice versa
	 *
	 *   refutation            xor        proofs or refutations for both
	 *                                       `f1' and `f2'
	 */

	estream << " :\n";

	line_prefix += "|   ";

	const LtlFormula* f1, *f2;
	bool branch, val1, val2;

	switch (f->what())
	{
	  case LTL_CONJUNCTION :
	    f1 = static_cast<const And*>(f)->subformula1;
	    f2 = static_cast<const And*>(f)->subformula2;
	    branch = prove_true;
	    val1 = val2 = false;
	    break;

	  case LTL_DISJUNCTION :
	    f1 = static_cast<const Or*>(f)->subformula1;
	    f2 = static_cast<const Or*>(f)->subformula2;
	    branch = !prove_true;
	    val1 = val2 = true;
	    break;

	  case LTL_IMPLICATION :
	    f1 = static_cast<const Imply*>(f)->subformula1;
	    f2 = static_cast<const Imply*>(f)->subformula2;
	    branch = !prove_true;
	    val1 = false;
	    val2 = true;
	    break;

	  case LTL_EQUIVALENCE :
	    f1 = static_cast<const Equiv*>(f)->subformula1;
	    f2 = static_cast<const Equiv*>(f)->subformula2;
	    branch = true;
	    break;

	  default :  /* LTL_XOR */
	    f1 = static_cast<const Xor*>(f)->subformula1;
	    f2 = static_cast<const Xor*>(f)->subformula2;
	    branch = true;
	    break;
	}

	if (branch)
	{
	  recPrint(estream, indent, f1, state);
	  line_prefix[line_prefix.size() - 4] = ' ';
	  recPrint(estream, indent, f2, state);
	}
	else
	{
	  line_prefix[line_prefix.size() - 4] = ' ';

	  if (eval_info.find(f2)->second->test(state) != val2)
	    recPrint(estream, indent, f1, state);
	  else if (eval_info.find(f1)->second->test(state) != val1)
	    recPrint(estream, indent, f2, state);
	  else if ((f1->propositional() && !f2->propositional())
		   || (f1->constant() && !f2->constant()))
	    recPrint(estream, indent, f1, state);
	  else if ((f2->propositional() && !f1->propositional())
		   || (f2->constant() && !f1->constant()))
	    recPrint(estream, indent, f2, state);
	  else if (f1->size() <= f2->size())
	    recPrint(estream, indent, f1, state);
	  else
	    recPrint(estream, indent, f2, state);
	}

	break;
      }

    default :  /* LTL_UNTIL || LTL_FINALLY || LTL_V || LTL_GLOBALLY
		  || LTL_BEFORE || LTL_WEAK_UNTIL || LTL_STRONG_RELEASE */
      {
	/*
	 *  Formula: f1 OP f2
	 *
	 *   proof/refutation       OP        what will be shown
	 *   ------------------------------------------------------------------
	 *   proof                  U         proofs for `f1' until finding a
	 *                                       state in which `f2' holds;
	 *                                       then show a proof for `f2'
	 *
	 *   refutation             U         refutations for `f2' until
	 *                                       finding a state in which `f1'
	 *                                       does not hold OR until some
	 *                                       state is visited twice (in
	 *                                       which case `f2' never holds in
	 *                                       the path); if the search ends
	 *                                       in a state where `f1' does not
	 *                                       hold, show a refutation for
	 *                                       `f1' and `f2' in the state
	 *
	 *   proof                  <>        proof for `f2' in the first state
	 *                                       in which `f2' holds
	 *
	 *   refutation             <>        refutation for `f2' in all states
	 *                                       of the path
	 *
	 *   proof                  V         proofs for `f2' until finding a
	 *                                       state in which `f1' holds OR
	 *                                       until some state is visited
	 *                                       twice (in which case `f2'
	 *                                       always holds in the path); if
	 *                                       the search ends in a state
	 *                                       where `f1' holds, show a proof
	 *                                       for `f1' and `f2' in the state
	 *
	 *   refutation             V         refutations for `f1' until
	 *                                       finding a state in which `f2'
	 *                                       does not hold; then show a
	 *                                       refutation for `f2'
	 *
	 *   proof                  []        proof for `f2' in all states of
	 *                                       the path
	 *
	 *   refutation             []        refutation for `f2' in the first
	 *                                       state in which `f2' does not
	 *                                       hold
	 *
	 *   proof                  W         proofs for `f1' until finding a
	 *                                       state in which `f2' holds or
	 *                                       until some state is visited
	 *                                       twice (in which case `f1'
	 *                                       always holds in the path);
	 *                                       if the search ends in a state
	 *                                       in which `f2' holds, show a
	 *                                       proof for `f2'
	 *
	 *   refutation             W         refutations for `f2' until
	 *                                       finding a state in which
	 *                                       neither `f1' and `f2' hold;
	 *                                       then show a refutation for
	 *                                       `f1' and `f2'
	 *
	 *   proof                  M         proofs for `f2' until finding a
	 *                                       state in which both `f1' and
	 *                                       `f2' hold; then show a proof
	 *                                       for `f1' and `f2'
	 *
	 *   refutation             M         refutations for `f1' until
	 *                                       finding a state in which `f2'
	 *                                       does not hold or until some
	 *                                       state is visited twice (in
	 *                                       which case `f1' never holds in
	 *                                       the path); if the search ends
	 *                                       in a state in which `f2' does
	 *                                       not hold, show a refutation
	 *                                       for `f2'
	 *
	 *   proof                  B         refutations for `f2' until
	 *                                       finding a state in which `f1'
	 *                                       holds OR until some state is
	 *                                       visited twice (in which case
	 *                                       `f2' never holds in the path);
	 *                                       if the search ends in a state
	 *                                       where `f1' holds, show a proof
	 *                                       for `f1' and a refutation for
	 *                                       `f2' in the state
	 *
	 *   refutation             B         refutations for `f1' until
	 *                                       finding a state in which `f2'
	 *                                       holds; then show a proof for
	 *                                       `f2'
	 *
	 */

	estream << " :\n";

	line_prefix += "|   ";

	const LtlFormula* f1;
	const LtlFormula* f2;
	bool eventuality, check_value_1, check_value_2;

	switch (f->what())
	{
	  case LTL_UNTIL :
	    f1 = static_cast<const Until*>(f)->subformula1;
	    f2 = static_cast<const Until*>(f)->subformula2;
	    eventuality = true;
	    check_value_1 = check_value_2 = true;
	    break;

	  case LTL_FINALLY :
	    f1 = 0;
	    f2 = static_cast<const Finally*>(f)->subformula;
	    eventuality = true;
	    check_value_1 = check_value_2 = true;
	    break;

	  case LTL_V :
	    f1 = static_cast<const V*>(f)->subformula1;
	    f2 = static_cast<const V*>(f)->subformula2;
	    eventuality = false;
	    check_value_1 = check_value_2 = false;
	    break;

	  case LTL_GLOBALLY :
	    f1 = 0;
	    f2 = static_cast<const Globally*>(f)->subformula;
	    eventuality = false;
	    check_value_1 = check_value_2 = false;
	    break;

	  case LTL_BEFORE :
	    f1 = static_cast<const Before*>(f)->subformula1;
	    f2 = static_cast<const Before*>(f)->subformula2;
	    eventuality = false;
	    check_value_1 = false;
	    check_value_2 = true;
	    break;

	  case LTL_WEAK_UNTIL :  /* note the order of `f1' and `f2' */
	    f1 = static_cast<const WeakUntil*>(f)->subformula2;
	    f2 = static_cast<const WeakUntil*>(f)->subformula1;
	    eventuality = false;
	    check_value_1 = check_value_2 = false;
	    break;

	  default :  /* LTL_STRONG_RELEASE; note the order of `f1' and `f2' */
	    f1 = static_cast<const StrongRelease*>(f)->subformula2;
	    f2 = static_cast<const StrongRelease*>(f)->subformula1;
	    eventuality = true;
	    check_value_1 = check_value_2 = true;
	    break;
	}

	if (prove_true == eventuality)
	{
	  while (eval_info.find(f2)->second->test(state) != check_value_2)
	  {
	    if (f1 != 0)
	      recPrint(estream, indent, f1, state);

	    estream << string(indent, ' ')
	               + line_prefix.substr(0, line_prefix.size() - 4)
                       + "+-> s"
		    << path_states[state]
		    << " --> s";

	    state++;
	    if (state == path_states.size())
	      state = current_loop_state;

	    estream << path_states[state] << '\n';
	  }

	  if (f->what() == LTL_WEAK_UNTIL || f->what() == LTL_STRONG_RELEASE)
	  {
	    recPrint(estream, indent, f2, state);
	    line_prefix[line_prefix.size() - 4] = ' ';
	    recPrint(estream, indent, f1, state);
	  }
	  else
	  {
	    line_prefix[line_prefix.size() - 4] = ' ';
	    recPrint(estream, indent, f2, state);
	  }
	}
	else
	{
	  StateSpace::size_type start_state = state;

	  while (f1 == 0
		 || eval_info.find(f1)->second->test(state) == check_value_1)
	  {
	    recPrint(estream, indent, f2, state);

	    estream << string(indent, ' ')
	               + line_prefix.substr(0, line_prefix.size() - 4)
	               + "+-> s"
		    << path_states[state]
		    << " --> s";

	    state++;
	    if (state == path_states.size())
	    {
	      state = current_loop_state;
	      if (start_state < current_loop_state)
	      {
		estream << path_states[state] << '\n';
		break;
	      }
	    }

	    estream << path_states[state] << '\n';

	    if (state == start_state)
	      break;
	  }

	  if (f1 != 0
	      && eval_info.find(f1)->second->test(state) != check_value_1)
	  {
	    if (f->what() == LTL_WEAK_UNTIL || f->what() == LTL_STRONG_RELEASE)
	    {
	      line_prefix[line_prefix.size() - 4] = ' ';
	      recPrint(estream, indent, f1, state);
	    }
	    else
	    {
	      recPrint(estream, indent, f1, state);
	      line_prefix[line_prefix.size() - 4] = ' ';
	      recPrint(estream, indent, f2, state);
	    }
	  }
	}

	break;
      }
  }

  line_prefix.resize(line_prefix.size() - 4);
}

}
