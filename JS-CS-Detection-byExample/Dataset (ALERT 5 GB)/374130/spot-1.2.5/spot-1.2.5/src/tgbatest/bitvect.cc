// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et DÃ©veloppement
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

#include <iostream>
#include "misc/bitvect.hh"

void ruler()
{
  std::cout << "\n   ";
  for (size_t x = 0; x < 76; ++x)
    if (x % 10 == 0)
      std::cout << x / 10;
    else
      std::cout << "_";
  std::cout << "\n   ";
  for (size_t x = 0; x < 76; ++x)
    std::cout << x % 10;
  std::cout << "\n\n";
}

#define ECHO(name) std::cout << #name": " << *name << "\n"

int main()
{
  ruler();
  spot::bitvect* v = spot::make_bitvect(15);
  ECHO(v);
  v->set(10);
  v->set(7);
  v->set(12);
  ECHO(v);

  ruler();
  spot::bitvect* w = spot::make_bitvect(42);
  w->set(30);
  w->set(41);
  w->set(13);
  w->set(7);
  ECHO(w);
  *w ^= *v;
  ECHO(w);

  ruler();
  spot::bitvect* x = spot::make_bitvect(75);
  x->set(70);
  x->set(60);
  ECHO(x);
  *x |= *w;
  ECHO(x);

  for (size_t i = 0; i < 30; ++i)
    w->push_back((i & 3) == 0);
  ECHO(w);
  *x &= *w;
  ECHO(x);
  x->set_all();
  ECHO(x);

  ruler();

  w->push_back(0x09, 4);
  ECHO(w);
  spot::bitvect* y = w->extract_range(0, 71);
  ECHO(y);
  delete y;
  y = w->extract_range(0, 64);
  ECHO(y);
  delete y;
  y = w->extract_range(64, 75);
  ECHO(y);
  delete y;
  y = w->extract_range(0, 75);
  ECHO(y);
  delete y;
  y = w->extract_range(7, 64);
  ECHO(y);
  delete y;
  y = w->extract_range(7, 72);
  ECHO(y);
  delete y;

  delete v;
  delete w;
  delete x;

  ruler();

  spot::bitvect_array* a = spot::make_bitvect_array(60, 10);
  for (size_t y = 0; y < a->size(); ++y)
    for (size_t x = 0; x < 60; ++x)
      {
	if (((x ^ y) & 3) < 2)
	  a->at(y).set(x);
      }
  std::cout << *a;

  ruler();

  for (size_t i = 0; i < 12; ++i)
    a->at(4).push_back((i & 2) == 0);
  a->at(6) = a->at(4);
  a->at(8) = a->at(7);
  a->at(6) ^= a->at(8);

  std::cout << *a;

  std::cout << "Comp: "
            << (a->at(0) == a->at(1))
	    << (a->at(0) == a->at(2))
	    << (a->at(1) != a->at(2))
	    << (a->at(0) < a->at(2))
	    << (a->at(0) > a->at(2))
	    << (a->at(3) < a->at(4))
	    << (a->at(5) > a->at(6)) << std::endl;

  delete a;
}
