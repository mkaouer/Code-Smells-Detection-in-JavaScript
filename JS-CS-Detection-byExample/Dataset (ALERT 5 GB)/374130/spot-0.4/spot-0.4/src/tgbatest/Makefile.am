## Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
## Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
## Université Pierre et Marie Curie.
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
LDADD = ../libspot.la

# These are the most used test programs, and they are also useful
# to run manually outside the test suite.  Always build them.
noinst_PROGRAMS = ltl2tgba randtgba

check_SCRIPTS = defs
# Keep this sorted alphabetically.
check_PROGRAMS = \
  bddprod \
  explicit \
  expldot \
  explprod \
  ltlprod \
  mixprod \
  powerset \
  readsave \
  reductgba \
  reduccmp \
  tgbaread \
  tripprod

# Keep this sorted alphabetically.
bddprod_SOURCES  = ltlprod.cc
bddprod_CXXFLAGS = -DBDD_CONCRETE_PRODUCT
explicit_SOURCES = explicit.cc
expldot_SOURCES  = powerset.cc
expldot_CXXFLAGS = -DDOTTY
explprod_SOURCES = explprod.cc
ltl2tgba_SOURCES = ltl2tgba.cc
ltlprod_SOURCES  = ltlprod.cc
mixprod_SOURCES  = mixprod.cc
powerset_SOURCES = powerset.cc
randtgba_SOURCES = randtgba.cc
readsave_SOURCES = readsave.cc
reductgba_SOURCES = reductgba.cc
reduccmp_SOURCES = reductgba.cc
reduccmp_CXXFLAGS = -DREDUCCMP
tgbaread_SOURCES = tgbaread.cc
tripprod_SOURCES = tripprod.cc

# Keep this sorted by STRENGTH.  Test basic things first,
# because such failures will be easier to diagnose and fix.
TESTS = \
  explicit.test \
  tgbaread.test \
  readsave.test \
  ltl2tgba.test \
  ltl2neverclaim.test \
  ltlprod.test \
  bddprod.test \
  explprod.test \
  explpro2.test \
  explpro3.test \
  explpro4.test \
  tripprod.test \
  mixprod.test \
  dupexp.test \
  reduccmp.test \
  reductgba.test \
  emptchk.test \
  emptchke.test \
  dfs.test \
  emptchkr.test \
  spotlbtt.test

EXTRA_DIST = $(TESTS)

CLEANFILES = \
  blue_counter \
  blue_last \
  config \
  expected \
  input \
  input1 \
  input2 \
  input3 \
  output1 \
  output2 \
  red \
  stdout
