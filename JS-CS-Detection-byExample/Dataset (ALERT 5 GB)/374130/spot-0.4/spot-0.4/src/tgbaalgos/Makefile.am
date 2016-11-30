## Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

SUBDIRS = gtec

AM_CPPFLAGS = -I$(srcdir)/.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

tgbaalgosdir = $(pkgincludedir)/tgbaalgos

tgbaalgos_HEADERS = \
  bfssteps.hh \
  dotty.hh \
  dottydec.hh \
  dupexp.hh \
  emptiness.hh \
  emptiness_stats.hh \
  gv04.hh \
  lbtt.hh \
  ltl2tgba_fm.hh \
  ltl2tgba_lacim.hh \
  magic.hh \
  neverclaim.hh \
  powerset.hh \
  projrun.hh \
  randomgraph.hh \
  reachiter.hh \
  reducerun.hh \
  replayrun.hh \
  rundotdec.hh \
  save.hh \
  se05.hh \
  stats.hh \
  tau03.hh \
  tau03opt.hh \
  reductgba_sim.hh \
  weight.hh

noinst_LTLIBRARIES = libtgbaalgos.la
libtgbaalgos_la_SOURCES = \
  bfssteps.cc \
  dotty.cc \
  dottydec.cc \
  dupexp.cc \
  emptiness.cc \
  gv04.cc \
  lbtt.cc \
  ltl2tgba_fm.cc \
  ltl2tgba_lacim.cc \
  magic.cc \
  ndfs_result.hxx \
  neverclaim.cc \
  powerset.cc \
  projrun.cc \
  randomgraph.cc \
  reachiter.cc \
  reducerun.cc \
  replayrun.cc \
  rundotdec.cc \
  save.cc \
  se05.cc \
  stats.cc \
  tau03.cc \
  tau03opt.cc \
  reductgba_sim.cc \
  reductgba_sim_del.cc \
  weight.cc

libtgbaalgos_la_LIBADD = gtec/libgtec.la
