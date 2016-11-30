## Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
## département Systèmes Répartis Coopératifs (SRC), Université Pierre
## et Marie Curie.
##
## This file is part of Spot, a model checking library.
##
## Spot is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
##
## Spot is distributed in the hope that it will be useful, but WITHOUT
## ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
## or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
## License for more details.
##
## You should have received a copy of the GNU General Public License
## along with Spot; see the file COPYING.  If not, write to the Free
## Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
## 02111-1307, USA.

AM_CPPFLAGS = -I$(srcdir)/.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

tgbadir = $(pkgincludedir)/tgba

tgba_HEADERS = \
  bdddict.hh \
  bddprint.hh \
  formula2bdd.hh \
  public.hh \
  state.hh \
  statebdd.hh \
  succiter.hh \
  succiterconcrete.hh \
  tgba.hh \
  tgbabddconcrete.hh \
  tgbabddconcretefactory.hh \
  tgbabddconcreteproduct.hh \
  tgbabddcoredata.hh \
  tgbabddfactory.hh \
  tgbaexplicit.hh \
  tgbaproduct.hh \
  tgbatba.hh \
  tgbareduc.hh

noinst_LTLIBRARIES = libtgba.la
libtgba_la_SOURCES = \
  bdddict.cc \
  bddprint.cc \
  formula2bdd.cc \
  statebdd.cc \
  succiterconcrete.cc \
  tgba.cc \
  tgbabddconcrete.cc \
  tgbabddconcretefactory.cc \
  tgbabddconcreteproduct.cc \
  tgbabddcoredata.cc \
  tgbaexplicit.cc \
  tgbaproduct.cc \
  tgbatba.cc \
  tgbareduc.cc
