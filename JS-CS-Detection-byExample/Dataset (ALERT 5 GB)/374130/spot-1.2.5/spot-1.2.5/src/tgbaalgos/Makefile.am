## -*- coding: utf-8 -*-
## Copyright (C) 2008, 2009, 2010, 2011, 2012, 2013, 2014 Laboratoire
## de Recherche et Développement de l'Epita (LRDE).
## Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
## 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
## Université Pierre et Marie Curie.
##
## This file is part of Spot, a model checking library.
##
## Spot is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 3 of the License, or
## (at your option) any later version.
##
## Spot is distributed in the hope that it will be useful, but WITHOUT
## ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
## or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
## License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program.  If not, see <http://www.gnu.org/licenses/>.

SUBDIRS = gtec

AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

tgbaalgosdir = $(pkgincludedir)/tgbaalgos

tgbaalgos_HEADERS = \
  bfssteps.hh \
  complete.hh \
  compsusp.hh \
  cutscc.hh \
  cycles.hh \
  dtgbacomp.hh \
  degen.hh \
  dottydec.hh \
  dotty.hh \
  dtbasat.hh \
  dtgbasat.hh \
  dupexp.hh \
  eltl2tgba_lacim.hh \
  emptiness.hh \
  emptiness_stats.hh \
  gv04.hh \
  hoaf.hh \
  isdet.hh \
  isweakscc.hh \
  lbtt.hh \
  ltl2taa.hh \
  ltl2tgba_fm.hh \
  ltl2tgba_lacim.hh \
  magic.hh \
  minimize.hh \
  neverclaim.hh \
  postproc.hh \
  powerset.hh \
  projrun.hh \
  randomgraph.hh \
  reachiter.hh \
  reducerun.hh \
  reductgba_sim.hh \
  replayrun.hh \
  rundotdec.hh \
  safety.hh \
  save.hh \
  sccfilter.hh \
  scc.hh \
  se05.hh \
  simulation.hh \
  stats.hh \
  stripacc.hh \
  tau03.hh \
  tau03opt.hh \
  translate.hh \
  word.hh

noinst_LTLIBRARIES = libtgbaalgos.la
libtgbaalgos_la_SOURCES = \
  bfssteps.cc \
  complete.cc \
  compsusp.cc \
  cutscc.cc \
  cycles.cc \
  dtgbacomp.cc \
  degen.cc \
  dotty.cc \
  dottydec.cc \
  dtbasat.cc \
  dtgbasat.cc \
  dupexp.cc \
  eltl2tgba_lacim.cc \
  emptiness.cc \
  gv04.cc \
  hoaf.cc \
  isdet.cc \
  isweakscc.cc \
  lbtt.cc \
  ltl2taa.cc \
  ltl2tgba_fm.cc \
  ltl2tgba_lacim.cc \
  magic.cc \
  minimize.cc \
  ndfs_result.hxx \
  neverclaim.cc \
  postproc.cc \
  powerset.cc \
  projrun.cc \
  randomgraph.cc \
  reachiter.cc \
  reducerun.cc \
  reductgba_sim.cc \
  replayrun.cc \
  rundotdec.cc \
  safety.cc \
  save.cc \
  scc.cc \
  sccfilter.cc \
  se05.cc \
  simulation.cc \
  stats.cc \
  stripacc.cc \
  tau03.cc \
  tau03opt.cc \
  translate.cc \
  weight.cc \
  weight.hh \
  word.cc

libtgbaalgos_la_LIBADD = gtec/libgtec.la
