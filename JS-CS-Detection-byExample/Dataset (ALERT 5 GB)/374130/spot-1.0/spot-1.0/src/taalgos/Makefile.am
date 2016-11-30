## -*- coding: utf-8 -*-
## Copyright (C) 2010, 2012 Laboratoire de Recherche et Développement
## de l'Epita (LRDE).
##
## This file is part of Spot, a model checking library.et Marie Curie.
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


AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

taalgosdir = $(pkgincludedir)/taalgos

taalgos_HEADERS = \
  tgba2ta.hh \
  dotty.hh \
  reachiter.hh \
  stats.hh \
  statessetbuilder.hh \
  minimize.hh \
  emptinessta.hh

noinst_LTLIBRARIES = libtaalgos.la
libtaalgos_la_SOURCES = \
  tgba2ta.cc \
  dotty.cc \
  reachiter.cc \
  stats.cc \
  statessetbuilder.cc \
  minimize.cc \
  emptinessta.cc
