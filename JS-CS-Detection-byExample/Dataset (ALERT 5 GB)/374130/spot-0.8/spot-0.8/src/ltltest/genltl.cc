// Copyright (C) 2010, 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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
#include <iostream>
#include <sstream>
#include <set>
#include <string>
#include <cstdlib>
#include <cstring>
#include "ltlast/allnodes.hh"
#include "ltlvisit/tostring.hh"
#include "ltlenv/defaultenv.hh"

using namespace spot;
using namespace spot::ltl;

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


// The five first families defined here come from the following paper:
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
// Families 6-9 and 12-14 were used in:
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
// Family 16 comes from:
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
// Families 17-20 come from:
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

void
syntax(char* prog)
{
  std::cerr <<
    "Usage: " << prog << " [-s] F N\n"
    "\n"
    "-s output using Spin's syntax\n"
    "F  specifies the familly of LTL formula to build\n"
    "N  is the size parameter of the familly\n"
    "\n"
    "Available families (F):\n"
    "  1: F(p1&F(p2&F(p3&...F(pn)))) & F(q1&F(q2&F(q3&...F(qn))))"
    "\n"
    "  2: F(p&X(p&X(p&...X(p)))) & F(q&X(q&X(q&...X(q))))\n"
    "  3: F(p&(Xp)&(XXp)&...(X...X(p))) & F(q&(Xq)&(XXq)&...(X...X(q)))\n"
    "  4: GF(p1)&GF(p2)&...&GF(pn)\n"
    "  5: FG(p1)|FG(p2)|...|FG(pn)\n"
    "  6: GF(p1)|GF(p2)|...|GF(pn)\n"
    "  7: FG(p1)&FG(p2)&...&FG(pn)\n"
    "  8: (((p1 U p2) U p3) ... U pn)\n"
    "  9: (p1 U (p2 U (... U pn)))\n"
    " 10: (((p1 R p2) R p3) ... R pn)\n"
    " 11: (p1 R (p2 R (... R pn)))\n"
    " 12: (GF(p1)|FG(p2))&(GF(p2)|FG(p3))&...&(GF(pn)|FG(p{n+1}))\n"
    " 13: (F(p1)|G(p2))&(F(p2)|G(p3))&...&(F(pn)|G(p{n+1}))\n"
    " 14: G(p1)|G(p2)|...|G(pn)\n"
    " 15: F(p1)&F(p2)&...&F(pn)\n"
    " 16: !((GF(p1)&GF(p2)&...&GF(pn))->G(q -> F(r)))\n"
    " 17: LTLcounter(n)\n"
    " 18: LTLcounterLinear(n)\n"
    " 19: LTLcounterCarry(n)\n"
    " 20: LTLcounterCarryLinear(n)" << std::endl;
  exit(2);
}

int
to_int(const char* s)
{
  char* endptr;
  int res = strtol(s, &endptr, 10);
  if (*endptr)
    {
      std::cerr << "Failed to parse `" << s << "' as an integer." << std::endl;
      exit(1);
    }
  return res;
}


// F(p_1 & F(p_2 & F(p_3 & ... F(p_n))))
formula* E_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  formula* result = 0;

  for (; n > 0; --n)
    {
      std::ostringstream p;
      p << name << n;
      formula* f = env.require(p.str());
      if (result)
	result = And_(f, result);
      else
	result = f;
      result = F_(result);
    }
  return result;
}

// p & X(p & X(p & ... X(p)))
formula* phi_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  formula* result = 0;
  formula* p = env.require(name);
  for (; n > 0; --n)
    {
      if (result)
	result = And_(p->clone(), X_(result));
      else
	result = p;
    }
  return result;
}

formula* N_n(std::string name, int n)
{
  return unop::instance(unop::F, phi_n(name, n));
}

// p & X(p) & XX(p) & XXX(p) & ... X^n(p)
formula* phi_prime_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  formula* result = 0;
  formula* p = env.require(name);
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

formula* N_prime_n(std::string name, int n)
{
  return F_(phi_prime_n(name, n));
}


// GF(p_1) & GF(p_2) & ... & GF(p_n)   if conj == true
// GF(p_1) | GF(p_2) | ... | GF(p_n)   if conj == false
formula* GF_n(std::string name, int n, bool conj = true)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  formula* result = 0;

  multop::type op = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      formula* f = G_(F_(env.require(p.str())));

      if (result)
	result = multop::instance(op, f, result);
      else
	result = f;
    }
  return result;
}

// FG(p_1) | FG(p_2) | ... | FG(p_n)   if conj == false
// FG(p_1) & FG(p_2) & ... & FG(p_n)   if conj == true
formula* FG_n(std::string name, int n, bool conj = false)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  formula* result = 0;

  multop::type op = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      formula* f = F_(G_(env.require(p.str())));

      if (result)
	result = multop::instance(op, f, result);
      else
	result = f;
    }
  return result;
}

//  (((p1 OP p2) OP p3)...OP pn)   if right_assoc == false
//  (p1 OP (p2 OP (p3 OP (... pn)  if right_assoc == true
formula* bin_n(std::string name, int n,
	       binop::type op, bool right_assoc = false)
{
  if (n <= 0)
    {
      std::cerr << "n>0 required for this class" << std::endl;
      exit(1);
    }

  formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << (right_assoc ? (n + 1 - i) : i);
      formula* f = env.require(p.str());
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
formula* R_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  formula* pi;

  {
    std::ostringstream p;
    p << name << 1;
    pi = env.require(p.str());
  }

  formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      formula* gf = G_(F_(pi));
      std::ostringstream p;
      p << name << i + 1;
      pi = env.require(p.str());

      formula* fg = G_(F_(pi->clone()));

      formula* f = Or_(gf, fg);

      if (result)
	result = And_(f, result);
      else
	result = f;
    }
  pi->destroy();
  return result;
}

// (F(p1)|G(p2))&(F(p2)|G(p3))&...&(F(pn)|G(p{n+1}))"
formula* Q_n(std::string name, int n)
{
  if (n <= 0)
    return constant::true_instance();

  formula* pi;

  {
    std::ostringstream p;
    p << name << 1;
    pi = env.require(p.str());
  }

  formula* result = 0;

  for (int i = 1; i <= n; ++i)
    {
      formula* f = F_(pi);

      std::ostringstream p;
      p << name << i + 1;
      pi = env.require(p.str());

      formula* g = G_(pi->clone());

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
formula* combunop_n(std::string name, int n,
		    unop::type op, bool conj = false)
{
  if (n <= 0)
    return conj ? constant::true_instance() : constant::false_instance();

  formula* result = 0;

  multop::type cop = conj ? multop::And : multop::Or;

  for (int i = 1; i <= n; ++i)
    {
      std::ostringstream p;
      p << name << i;
      formula* f = unop::instance(op, env.require(p.str()));

      if (result)
	result = multop::instance(cop, f, result);
      else
	result = f;
    }
  return result;
}

// !((GF(p1)&GF(p2)&...&GF(pn))->G(q -> F(r)))
// From "Fast LTL to Büchi Automata Translation" [gastin.01.cav]
formula* fair_response(std::string p, std::string q, std::string r, int n)
{
  formula* fair = GF_n(p, n);
  formula* resp = G_(Implies_(env.require(q), F_(env.require(r))));
  return Not_(Implies_(fair, resp));
}


// Builds X(X(...X(p))) with n occurrences of X.
formula* X_n(formula* p, int n)
{
  assert(n >= 0);
  formula* res = p;
  while (n--)
    res = X_(res);
  return res;
}

// Based on LTLcounter.pl from Kristin Rozier.
// http://shemesh.larc.nasa.gov/people/kyr/benchmarking_scripts/

formula* ltl_counter(std::string bit, std::string marker, int n, bool linear)
{
  formula* b = env.require(bit);
  formula* neg_b = Not_(b);
  formula* m = env.require(marker);
  formula* neg_m = Not_(m); // to destroy

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
      formula* p = m->clone();
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
      formula* p = neg_b->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_b->clone(), X_(p));
      (*res)[1] = p;
    }

#define AndX_(x, y) (linear ? X_(And_((x), (y))) : And_(X_(x), X_(y)))

  // If the least significant bit is 0, it will be 1 at the next time,
  // and other bits stay the same.
  formula* Xnm1_b = X_n(b->clone(), n - 1);
  formula* Xn_b = X_(Xnm1_b); // to destroy
  (*res)[2] =
    G_(Implies_(And_(m->clone(), neg_b->clone()),
		AndX_(Xnm1_b->clone(), U_(And_(Not_(m->clone()),
					       Equiv_(b->clone(),
						      Xn_b->clone())),
					  m->clone()))));

  // From the least significant bit to the first 0, all the bits
  // are flipped on the next value.  Remaining bits are identical.
  formula* Xnm1_negb = X_n(neg_b, n - 1);
  formula* Xn_negb = X_(Xnm1_negb); // to destroy
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

formula* ltl_counter_carry(std::string bit, std::string marker,
			   std::string carry, int n, bool linear)
{
  formula* b = env.require(bit);
  formula* neg_b = Not_(b);
  formula* m = env.require(marker);
  formula* neg_m = Not_(m); // to destroy
  formula* c = env.require(carry);
  formula* neg_c = Not_(c); // to destroy

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
      formula* p = m->clone();
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
      formula* p = neg_b->clone();
      for (int i = n - 1; i > 0; --i)
	p = And_(neg_b->clone(), X_(p));
      (*res)[1] = p;
    }

  formula* Xn_b = X_n(b->clone(), n); // to destroy
  formula* Xn_negb = X_n(neg_b, n);   // to destroy

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


int
main(int argc, char** argv)
{
  bool spin_syntax = false;
  if (argc >= 2 && !strcmp(argv[1], "-s"))
    {
      spin_syntax = true;
      --argc;
      ++argv;
    }

  if (argc != 3)
    syntax(argv[0]);

  int f = to_int(argv[1]);
  int n = to_int(argv[2]);

  formula* res = 0;

  switch (f)
    {
    case 1:
      res = multop::instance(multop::And, E_n("p", n), E_n("q", n));
      break;
    case 2:
      res = multop::instance(multop::And, N_n("p", n), N_n("q", n));
      break;
    case 3:
      res = multop::instance(multop::And, N_prime_n("p", n), N_prime_n("q", n));
      break;
    case 4:
      res = GF_n("p", n, true);
      break;
    case 5:
      res = FG_n("p", n, false);
      break;
    case 6:
      res = GF_n("p", n, false);
      break;
    case 7:
      res = FG_n("p", n, true);
      break;
    case 8:
      res = bin_n("p", n, binop::U, false);
      break;
    case 9:
      res = bin_n("p", n, binop::U, true);
      break;
    case 10:
      res = bin_n("p", n, binop::R, false);
      break;
    case 11:
      res = bin_n("p", n, binop::R, true);
      break;
    case 12:
      res = R_n("p", n);
      break;
    case 13:
      res = Q_n("p", n);
      break;
    case 14:
      res = combunop_n("p", n, unop::G, false);
      break;
    case 15:
      res = combunop_n("p", n, unop::F, true);
      break;
    case 16:
      res = fair_response("p", "q", "r", n);
      break;
    case 17:
      res = ltl_counter("b", "m", n, false);
      break;
    case 18:
      res = ltl_counter("b", "m", n, true);
      break;
    case 19:
      res = ltl_counter_carry("b", "m", "c", n, false);
      break;
    case 20:
      res = ltl_counter_carry("b", "m", "c", n, true);
      break;

    default:
      std::cerr << "Unknown familly " << f << std::endl;
      exit(2);
      break;
    }

  if (spin_syntax)
    to_spin_string(res, std::cout, true) << std::endl;
  else
    to_string(res, std::cout, true) << std::endl;

  res->destroy();

  return 0;
}
