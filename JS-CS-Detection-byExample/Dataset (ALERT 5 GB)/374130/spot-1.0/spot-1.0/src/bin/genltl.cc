// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

// Families defined here come from the following papers:
//
// @InProceedings{cichon.09.depcos,
//   author = {Jacek Cicho{\'n} and Adam Czubak and Andrzej Jasi{\'n}ski},
//   title = {Minimal {B\"uchi} Automata for Certain Classes of {LTL} Formulas},
//   booktitle = {Proceedings of the Fourth International Conference on
//                Dependability of Computer Systems},
//   pages = {17--24},
//   year = 2009,
//   publisher = {IEEE Computer Society},
// }
//
// @InProceedings{geldenhuys.06.spin,
//   author = {Jaco Geldenhuys and Henri Hansen},
//   title = {Larger Automata and Less Work for LTL Model Checking},
//   booktitle = {Proceedings of the 13th International SPIN Workshop},
//   year = {2006},
//   pages = {53--70},
//   series = {Lecture Notes in Computer Science},
//   volume = {3925},
//   publisher = {Springer}
// }
//
// @InProceedings{gastin.01.cav,
//   author = {Paul Gastin and Denis Oddoux},
//   title = {Fast {LTL} to {B\"u}chi Automata Translation},
//   booktitle	= {Proceedings of the 13th International Conference on
// 		  Computer Aided Verification (CAV'01)},
//   pages = {53--65},
//   year = 2001,
//   editor = {G. Berry and H. Comon and A. Finkel},
//   volume = {2102},
//   series = {Lecture Notes in Computer Science},
//   address = {Paris, France},
//   publisher = {Springer-Verlag}
// }
//
// @InProceedings{rozier.07.spin,
//   author = {Kristin Y. Rozier and Moshe Y. Vardi},
//   title = {LTL Satisfiability Checking},
//   booktitle = {Proceedings of the 12th International SPIN Workshop on
// 		  Model Checking of Software (SPIN'07)},
//   pages = {149--167},
//   year = {2007},
//   volume = {4595},
//   series = {Lecture Notes in Computer Science},
//   publisher = {Springer-Verlag}
// }

#include "common_sys.hh"

#include <iostream>
#include <fstream>
#include <argp.h>
#include <cstdlib>
#include "error.h"
#include <vector>

#include "common_setup.hh"
#include "common_output.hh"
#include "common_range.hh"

#include <cassert>
#include <iostream>
#include <sstream>
#include <set>
#include <string>
#include <cstdlib>
#include <cstring>
#include "ltlast/allnodes.hh"
#include "ltlenv/defaultenv.hh"
#include "ltlvisit/relabel.hh"

using namespace spot;
using namespace spot::ltl;

const char argp_program_doc[] ="\
Generate temporal logic formulas from predefined scalable patterns.";

#define OPT_AND_F 2
#define OPT_AND_FG 3
#define OPT_AND_GF 4
#define OPT_CCJ_ALPHA 5
#define OPT_CCJ_BETA 6
#define OPT_CCJ_BETA_PRIME 7
#define OPT_GH_Q  8
#define OPT_GH_R  9
#define OPT_GO_THETA 10
#define OPT_OR_FG 11
#define OPT_OR_G 12
#define OPT_OR_GF 13
#define OPT_R_LEFT 14
#define OPT_R_RIGHT 15
#define OPT_RV_COUNTER 16
#define OPT_RV_COUNTER_CARRY 17
#define OPT_RV_COUNTER_CARRY_LINEAR 18
#define OPT_RV_COUNTER_LINEAR 19
#define OPT_U_LEFT 20
#define OPT_U_RIGHT 21

#define OPT_ALIAS(o) { #o, 0, 0, OPTION_ALIAS, 0, 0 }

static const argp_option options[] =
  {
    /**************************************************/
    // Keep this alphabetically sorted (expect for aliases).
    { 0, 0, 0, 0, "Pattern selection:", 1},
    // J. Geldenhuys and H. Hansen (Spin'06): Larger automata and less
    // work for LTL model checking.
    { "and-f", OPT_AND_F, "RANGE", 0, "F(p1)&F(p2)&...&F(pn)", 0 },
    OPT_ALIAS(gh-e),
    { "and-fg", OPT_AND_FG, "RANGE", 0, "FG(p1)&FG(p2)&...&FG(pn)", 0 },
    { "and-gf", OPT_AND_GF, "RANGE", 0, "GF(p1)&GF(p2)&...&GF(pn)", 0 },
    OPT_ALIAS(ccj-phi),
    OPT_ALIAS(gh-c2),
    { "ccj-alpha", OPT_CCJ_ALPHA, "RANGE", 0,
      "F(p1&F(p2&F(p3&...F(pn)))) & F(q1&F(q2&F(q3&...F(qn))))", 0 },
    { "ccj-beta", OPT_CCJ_BETA, "RANGE", 0,
      "F(p&X(p&X(p&...X(p)))) & F(q&X(q&X(q&...X(q))))", 0 },
    { "ccj-beta-prime", OPT_CCJ_BETA_PRIME, "RANGE", 0,
      "F(p&(Xp)&(XXp)&...(X...X(p))) & F(q&(Xq)&(XXq)&...(X...X(q)))", 0 },
    { "gh-q", OPT_GH_Q, "RANGE", 0,
      "(F(p1)|G(p2))&(F(p2)|G(p3))&... &(F(pn)|G(p{n+1}))", 0 },
    { "gh-r", OPT_GH_R, "RANGE", 0,
      "(GF(p1)|FG(p2))&(GF(p2)|FG(p3))&... &(GF(pn)|FG(p{n+1}))", 0},
    { "go-theta", OPT_GO_THETA, "RANGE", 0,
      "!((GF(p1)&GF(p2)&...&GF(pn)) -> G(q->F(r)))", 0 },
    { "or-fg", OPT_OR_FG, "RANGE", 0, "FG(p1)|FG(p2)|...|FG(pn)", 0 },
    OPT_ALIAS(ccj-xi),
    { "or-g", OPT_OR_G, "RANGE", 0, "G(p1)|G(p2)|...|G(pn)", 0 },
    OPT_ALIAS(gh-s),
    { "or-gf", OPT_OR_GF, "RANGE", 0, "GF(p1)|GF(p2)|...|GF(pn)", 0 },
    OPT_ALIAS(gh-c1),
    { "r-left", OPT_R_LEFT, "RANGE", 0, "(((p1 R p2) R p3) ... R pn)", 0 },
    { "r-right", OPT_R_RIGHT, "RANGE", 0, "(p1 R (p2 R (... R pn)))", 0 },
    { "rv-counter", OPT_RV_COUNTER, "RANGE", 0,
      "n-bit counter", 0 },
    { "rv-counter-carry", OPT_RV_COUNTER_CARRY, "RANGE", 0,
      "n-bit counter w/ carry", 0 },
    { "rv-counter-carry-linear", OPT_RV_COUNTER_CARRY_LINEAR, "RANGE", 0,
      "n-bit counter w/ carry (linear size)", 0 },
    { "rv-counter-linear", OPT_RV_COUNTER_LINEAR, "RANGE", 0,
      "n-bit counter (linear size)", 0 },
    { "u-left", OPT_U_LEFT, "RANGE", 0, "(((p1 U p2) U p3) ... U pn)", 0 },
    OPT_ALIAS(gh-u),
    { "u-right", OPT_U_RIGHT, "RANGE", 0, "(p1 U (p2 U (... U pn)))", 0 },
    OPT_ALIAS(gh-u2),
    OPT_ALIAS(go-phi),
    RANGE_DOC,
  /**************************************************/
    { 0, 0, 0, 0, "Output options:", -20 },
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

struct job
{
  int pattern;
  struct range range;
};

typedef std::vector<job> jobs_t;
static jobs_t jobs;


const struct argp_child children[] =
  {
    { &output_argp, 0, 0, -20 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

static void
enqueue_job(int pattern, const char* range_str)
{
  job j;
  j.pattern = pattern;
  j.range = parse_range(range_str);
  jobs.push_back(j);
}

static int
parse_opt(int key, char* arg, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case OPT_AND_F:
    case OPT_AND_FG:
    case OPT_AND_GF:
    case OPT_CCJ_ALPHA:
    case OPT_CCJ_BETA:
    case OPT_CCJ_BETA_PRIME:
    case OPT_GH_Q:
    case OPT_GH_R:
    case OPT_GO_THETA:
    case OPT_OR_FG:
    case OPT_OR_G:
    case OPT_OR_GF:
    case OPT_R_LEFT:
    case OPT_R_RIGHT:
    case OPT_RV_COUNTER:
    case OPT_RV_COUNTER_CARRY:
    case OPT_RV_COUNTER_CARRY_LINEAR:
    case OPT_RV_COUNTER_LINEAR:
    case OPT_U_LEFT:
    case OPT_U_RIGHT:
      enqueue_job(key, arg);
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

environment& env(default_environment::instance());

#define G_(x) spot::ltl::unop::instance(spot::ltl::unop::G, (x))
#define F_(x) spot::ltl::unop::instance(spot::ltl::unop::F, (x))
#define X_(x) spot::ltl::unop::instance(spot::ltl::unop::X, (x))
#define Not_(x) spot::ltl::unop::instance(spot::ltl::unop::Not, (x))
#define Implies_(x, y) \
  spot::ltl::binop::instance(spot::ltl::binop::Implies, (x), (y))
#define Equiv_(x, y) \
  spot::ltl::binop::instance(spot::ltl::binop::Equiv, (x), (y))
#define And_(x, y) \
  spot::ltl::multop::instance(spot::ltl::multop::And, (x), (y))
#define Or_(x, y) \
  spot::ltl::multop::instance(spot::ltl::multop::Or, (x), (y))
#define U_(x, y) \
  spot::ltl::binop::instance(spot::ltl::binop::U, (x), (y))

// F(p_1 & F(p_2 & F(p_3 & ... F(p_n))))
static const formula*
E_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  const formula* result = 0;

  for (; n > 0; --n)
    {
      std::ostringstream p;
      p << name << n;
      const formula* f = env.require(p.str());
      if (result)
	result = And_(f, result);
      else
	result = f;
      result = F_(result);
    }
  return result;
}

// p & X(p & X(p & ... X(p)))
static const formula*
phi_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  const formula* result = 0;
  const formula* p = env.require(name);
  for (; n > 0; --n)
    {
      if (result)
	result = And_(p->clone(), X_(result));
      else
	result = p;
    }
  return result;
}

const formula* N_n(std::string name, int n)
{
  return unop::instance(unop::F, phi_n(name, n));
}

// p & X(p) & XX(p) & XXX(p) & ... X^n(p)
static const formula*
phi_prime_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  const formula* result = 0;
  const formula* p = env.require(name);
  for (; n > 0; --n)
    {
      if (result)
	{
	  p = X_(p->clone());
	  result = And_(result, p);
	}
      else
	{
	  result = p;
	}
    }
  return result;
}

static const formula*
N_prime_n(std::string name, int n)
{
  return F_(phi_prime_n(name, n));
}


// GF(p_1) & GF(p_2) & ... & GF(p_n)   if conj == true
// GF(p_1) | GF(p_2) | ... | GF(p_n)   if conj == false
static const formula*
GF_n(std::string name, int n, bool conj = true)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  const formula* result = 0;

  multop::type op = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      const formula* f = G_(F_(env.require(p.str())));

      if (result)
	result = multop::instance(op, f, result);
      else
	result = f;
    }
  return result;
}

// FG(p_1) | FG(p_2) | ... | FG(p_n)   if conj == false
// FG(p_1) & FG(p_2) & ... & FG(p_n)   if conj == true
static const formula*
FG_n(std::string name, int n, bool conj = false)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  const formula* result = 0;

  multop::type op = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      const formula* f = F_(G_(env.require(p.str())));

      if (result)
	result = multop::instance(op, f, result);
      else
	result = f;
    }
  return result;
}

//  (((p1 OP p2) OP p3)...OP pn)   if right_assoc == false
//  (p1 OP (p2 OP (p3 OP (... pn)  if right_assoc == true
static const formula*
bin_n(std::string name, int n,
      binop::type op, bool right_assoc = false)
{
  if (n <= 0)
    n = 1;

  const formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << (right_assoc ? (n + 1 - i) : i);
      const formula* f = env.require(p.str());
      if (!result)
	result = f;
      else if (right_assoc)
	result = binop::instance(op, f, result);
      else
	result = binop::instance(op, result, f);
    }
  return result;
}

// (GF(p1)|FG(p2))&(GF(p2)|FG(p3))&...&(GF(pn)|FG(p{n+1}))"
static const formula*
R_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  const formula* pi;

  {
    std::ostringstream p;
    p << name << 1;
    pi = env.require(p.str());
  }

  const formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      const formula* gf = G_(F_(pi));
      std::ostringstream p;
      p << name << i + 1;
      pi = env.require(p.str());

      const formula* fg = G_(F_(pi->clone()));

      const formula* f = Or_(gf, fg);

      if (result)
	result = And_(f, result);
      else
	result = f;
    }
  pi->destroy();
  return result;
}

// (F(p1)|G(p2))&(F(p2)|G(p3))&...&(F(pn)|G(p{n+1}))"
static const formula*
Q_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  const formula* pi;

  {
    std::ostringstream p;
    p << name << 1;
    pi = env.require(p.str());
  }

  const formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      const formula* f = F_(pi);

      std::ostringstream p;
      p << name << i + 1;
      pi = env.require(p.str());

      const formula* g = G_(pi->clone());

      f = Or_(f, g);

      if (result)
	result = And_(f, result);
      else
	result = f;
    }
  pi->destroy();
  return result;
}

//  OP(p1) | OP(p2) | ... | OP(Pn) if conj == false
//  OP(p1) & OP(p2) & ... & OP(Pn) if conj == true
static const formula*
combunop_n(std::string name, int n,
	   unop::type op, bool conj = false)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  const formula* result = 0;

  multop::type cop = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      const formula* f = unop::instance(op, env.require(p.str()));

      if (result)
	result = multop::instance(cop, f, result);
      else
	result = f;
    }
  return result;
}

// !((GF(p1)&GF(p2)&...&GF(pn))->G(q -> F(r)))
// From "Fast LTL to Büchi Automata Translation" [gastin.01.cav]
static const formula*
fair_response(std::string p, std::string q, std::string r, int n)
{
  const formula* fair = GF_n(p, n);
  const formula* resp = G_(Implies_(env.require(q), F_(env.require(r))));
  return Not_(Implies_(fair, resp));
}


// Builds X(X(...X(p))) with n occurrences of X.
static const formula*
X_n(const formula* p, int n)
{
  assert(n >= 0);
  const formula* res = p;
  while (n--)
    res = X_(res);
  return res;
}

// Based on LTLcounter.pl from Kristin Rozier.
// http://shemesh.larc.nasa.gov/people/kyr/benchmarking_scripts/
static const formula*
ltl_counter(std::string bit, std::string marker, int n, bool linear)
{
  const formula* b = env.require(bit);
  const formula* neg_b = Not_(b);
  const formula* m = env.require(marker);
  const formula* neg_m = Not_(m); // to destroy

  multop::vec* res = new multop::vec(4);

  // The marker starts with "1", followed by n-1 "0", then "1" again,
  // n-1 "0", etc.
  if (!linear)
    {
      // G(m -> X(!m)&XX(!m)&XXX(m))          [if n = 3]
      multop::vec* v = new multop::vec(n);
      for (int i = 0; i + 1 < n; ++i)
	(*v)[i] = X_n(neg_m->clone(), i + 1);
      (*v)[n - 1] = X_n(m->clone(), n);
      (*res)[0] = And_(m->clone(),
		       G_(Implies_(m->clone(),
				   multop::instance(multop::And, v))));
    }
  else
    {
      // G(m -> X(!m & X(!m X(m))))          [if n = 3]
      const formula* p = m->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_m->clone(), X_(p));
      (*res)[0] = And_(m->clone(),
		       G_(Implies_(m->clone(), X_(p))));
    }

  // All bits are initially zero.
  if (!linear)
    {
      // !b & X(!b) & XX(!b)    [if n = 3]
      multop::vec* v2 = new multop::vec(n);
      for (int i = 0; i < n; ++i)
	(*v2)[i] = X_n(neg_b->clone(), i);
      (*res)[1] = multop::instance(multop::And, v2);
    }
  else
    {
      // !b & X(!b & X(!b))     [if n = 3]
      const formula* p = neg_b->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_b->clone(), X_(p));
      (*res)[1] = p;
    }

#define AndX_(x, y) (linear ? X_(And_((x), (y))) : And_(X_(x), X_(y)))

  // If the least significant bit is 0, it will be 1 at the next time,
  // and other bits stay the same.
  const formula* Xnm1_b = X_n(b->clone(), n - 1);
  const formula* Xn_b = X_(Xnm1_b); // to destroy
  (*res)[2] =
    G_(Implies_(And_(m->clone(), neg_b->clone()),
		AndX_(Xnm1_b->clone(), U_(And_(Not_(m->clone()),
					       Equiv_(b->clone(),
						      Xn_b->clone())),
					  m->clone()))));

  // From the least significant bit to the first 0, all the bits
  // are flipped on the next value.  Remaining bits are identical.
  const formula* Xnm1_negb = X_n(neg_b, n - 1);
  const formula* Xn_negb = X_(Xnm1_negb); // to destroy
  (*res)[3] =
    G_(Implies_(And_(m->clone(), b->clone()),
		AndX_(Xnm1_negb->clone(),
		      U_(And_(And_(b->clone(), neg_m->clone()),
			      Xn_negb->clone()),
			 Or_(m->clone(),
			     And_(And_(neg_m->clone(),
				       neg_b->clone()),
				  AndX_(Xnm1_b->clone(),
					U_(And_(neg_m->clone(),
						Equiv_(b->clone(),
						       Xn_b->clone())),
					   m->clone()))))))));
  neg_m->destroy();
  Xn_b->destroy();
  Xn_negb->destroy();

  return multop::instance(multop::And, res);
}

static const formula*
ltl_counter_carry(std::string bit, std::string marker,
		  std::string carry, int n, bool linear)
{
  const formula* b = env.require(bit);
  const formula* neg_b = Not_(b);
  const formula* m = env.require(marker);
  const formula* neg_m = Not_(m); // to destroy
  const formula* c = env.require(carry);
  const formula* neg_c = Not_(c); // to destroy

  multop::vec* res = new multop::vec(6);

  // The marker starts with "1", followed by n-1 "0", then "1" again,
  // n-1 "0", etc.
  if (!linear)
    {
      // G(m -> X(!m)&XX(!m)&XXX(m))          [if n = 3]
      multop::vec* v = new multop::vec(n);
      for (int i = 0; i + 1 < n; ++i)
	(*v)[i] = X_n(neg_m->clone(), i + 1);
      (*v)[n - 1] = X_n(m->clone(), n);
      (*res)[0] = And_(m->clone(),
		       G_(Implies_(m->clone(),
				   multop::instance(multop::And, v))));
    }
  else
    {
      // G(m -> X(!m & X(!m X(m))))          [if n = 3]
      const formula* p = m->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_m->clone(), X_(p));
      (*res)[0] = And_(m->clone(),
		       G_(Implies_(m->clone(), X_(p))));
    }

  // All bits are initially zero.
  if (!linear)
    {
      // !b & X(!b) & XX(!b)    [if n = 3]
      multop::vec* v2 = new multop::vec(n);
      for (int i = 0; i < n; ++i)
	(*v2)[i] = X_n(neg_b->clone(), i);
      (*res)[1] = multop::instance(multop::And, v2);
    }
  else
    {
      // !b & X(!b & X(!b))     [if n = 3]
      const formula* p = neg_b->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_b->clone(), X_(p));
      (*res)[1] = p;
    }

  const formula* Xn_b = X_n(b->clone(), n); // to destroy
  const formula* Xn_negb = X_n(neg_b, n);   // to destroy

  // If m is 1 and b is 0 then c is 0 and n steps later b is 1.
  (*res)[2] = G_(Implies_(And_(m->clone(), neg_b->clone()),
			  And_(neg_c->clone(), Xn_b->clone())));

  // If m is 1 and b is 1 then c is 1 and n steps later b is 0.
  (*res)[3] = G_(Implies_(And_(m->clone(), b->clone()),
			  And_(c->clone(), Xn_negb->clone())));

  if (!linear)
    {
      // If there's no carry, then all of the bits stay the same n steps later.
      (*res)[4] = G_(Implies_(And_(neg_c->clone(), X_(neg_m->clone())),
			      And_(X_(Not_(c->clone())),
				   Equiv_(X_(b->clone()),
					  X_(Xn_b->clone())))));

      // If there's a carry, then add one: flip the bits of b and
      // adjust the carry.
      (*res)[5] = G_(Implies_(c->clone(),
			      And_(Implies_(X_(neg_b->clone()),
					    And_(X_(neg_c->clone()),
						 X_(Xn_b->clone()))),
				   Implies_(X_(b->clone()),
					    And_(X_(c->clone()),
						 X_(Xn_negb->clone()))))));
    }
  else
    {
      // If there's no carry, then all of the bits stay the same n steps later.
      (*res)[4] = G_(Implies_(And_(neg_c->clone(), X_(neg_m->clone())),
			      X_(And_(Not_(c->clone()),
				      Equiv_(b->clone(),
					     Xn_b->clone())))));

      // If there's a carry, then add one: flip the bits of b and
      // adjust the carry.
      (*res)[5] = G_(Implies_(c->clone(),
			      X_(And_(Implies_(neg_b->clone(),
					       And_(neg_c->clone(),
						    Xn_b->clone())),
				      Implies_(b->clone(),
					       And_(c->clone(),
						    Xn_negb->clone()))))));
    }

  neg_m->destroy();
  neg_c->destroy();
  Xn_b->destroy();
  Xn_negb->destroy();

  return multop::instance(multop::And, res);
}


static void
output_pattern(int pattern, int n)
{
  const formula* f = 0;
  switch (pattern)
    {
      // Keep this alphabetically-ordered!
    case OPT_AND_F:
      f = combunop_n("p", n, unop::F, true);
      break;
    case OPT_AND_FG:
      f = FG_n("p", n, true);
      break;
    case OPT_AND_GF:
      f = GF_n("p", n, true);
      break;
    case OPT_CCJ_ALPHA:
      f = multop::instance(multop::And, E_n("p", n), E_n("q", n));
      break;
    case OPT_CCJ_BETA:
      f = multop::instance(multop::And, N_n("p", n), N_n("q", n));
      break;
    case OPT_CCJ_BETA_PRIME:
      f = multop::instance(multop::And, N_prime_n("p", n), N_prime_n("q", n));
      break;
    case OPT_GH_Q:
      f = Q_n("p", n);
      break;
    case OPT_GH_R:
      f = R_n("p", n);
      break;
    case OPT_GO_THETA:
      f = fair_response("p", "q", "r", n);
      break;
    case OPT_OR_FG:
      f = FG_n("p", n, false);
      break;
    case OPT_OR_G:
      f = combunop_n("p", n, unop::G, false);
      break;
    case OPT_OR_GF:
      f = GF_n("p", n, false);
      break;
    case OPT_R_LEFT:
      f = bin_n("p", n, binop::R, false);
      break;
    case OPT_R_RIGHT:
      f = bin_n("p", n, binop::R, true);
      break;
    case OPT_RV_COUNTER_CARRY:
      f = ltl_counter_carry("b", "m", "c", n, false);
      break;
    case OPT_RV_COUNTER_CARRY_LINEAR:
      f = ltl_counter_carry("b", "m", "c", n, true);
      break;
    case OPT_RV_COUNTER:
      f = ltl_counter("b", "m", n, false);
      break;
    case OPT_RV_COUNTER_LINEAR:
      f = ltl_counter("b", "m", n, true);
      break;
    case OPT_U_LEFT:
      f = bin_n("p", n, binop::U, false);
      break;
    case OPT_U_RIGHT:
      f = bin_n("p", n, binop::U, true);
      break;
    default:
      error(100, 0, "internal error: pattern not implemented");
    }

  // Make sure we use only "p42"-style of atomic propositions
  // in lbt's output.
  if (output_format == lbt_output && !f->has_lbt_atomic_props())
    {
      const spot::ltl::formula* r = spot::ltl::relabel(f, spot::ltl::Pnn);
      f->destroy();
      f = r;
    }

  output_formula(f);
  f->destroy();
}

static void
run_jobs()
{
  jobs_t::const_iterator i;
  for (i = jobs.begin(); i != jobs.end(); ++i)
    {
      int inc = (i->range.max < i->range.min) ? -1 : 1;
      int n = i->range.min;
      for (;;)
	{
	  output_pattern(i->pattern, n);
	  if (n == i->range.max)
	    break;
	  n += inc;
	}
    }
}


int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, 0, argp_program_doc,
		    children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  if (jobs.empty())
    error(1, 0, "Nothing to do.  Try '%s --help' for more information.",
	  program_name);

  run_jobs();
  return 0;
}
