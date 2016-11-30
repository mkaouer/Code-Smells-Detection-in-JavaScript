## -*- coding: utf-8 -*-
## Copyright (C) 2009, 2010, 2011, 2013 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
## Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
## département Systèmes Répartis Coopératifs (SRC), Université Pierre
## et Marie Curie.
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

AM_CPPFLAGS = -I$(srcdir)/.. -I.. \
              -I$(top_builddir)/lib -I$(top_srcdir)/lib
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

ltlastdir = $(pkgincludedir)/ltlast

ltlast_HEADERS = \
  allnodes.hh \
  atomic_prop.hh \
  automatop.hh \
  binop.hh \
  bunop.hh \
  constant.hh \
  formula.hh \
  multop.hh \
  nfa.hh \
  predecl.hh \
  refformula.hh \
  unop.hh \
  visitor.hh

noinst_LTLIBRARIES = libltlast.la
libltlast_la_SOURCES = \
  atomic_prop.cc \
  automatop.cc \
  binop.cc \
  bunop.cc \
  constant.cc \
  formula.cc \
  formula_tree.cc \
  formula_tree.hh \
  multop.cc \
  nfa.cc \
  refformula.cc \
  unop.cc
