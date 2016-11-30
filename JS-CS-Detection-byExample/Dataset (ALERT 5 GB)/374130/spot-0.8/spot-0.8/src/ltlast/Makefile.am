## Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

AM_CPPFLAGS = -I$(srcdir)/..
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

ltlastdir = $(pkgincludedir)/ltlast

ltlast_HEADERS = \
  allnodes.hh \
  atomic_prop.hh \
  automatop.hh \
  binop.hh \
  constant.hh \
  formula.hh \
  formula_tree.hh \
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
  constant.cc \
  formula.cc \
  formula_tree.cc \
  multop.cc \
  nfa.cc \
  refformula.cc \
  unop.cc
