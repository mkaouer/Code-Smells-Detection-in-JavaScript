## Copyright (C) 2011 Laboratoire de Recherche et Développement
## de l'Epita (LRDE).
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

miscdir = $(pkgincludedir)/misc

nodist_misc_HEADERS = _config.h
DISTCLEANFILES = _config.h

misc_HEADERS = \
  accconv.hh \
  bareword.hh \
  bddalloc.hh \
  bddlt.hh \
  bddop.hh \
  casts.hh \
  escape.hh \
  fixpool.hh \
  freelist.hh \
  hash.hh \
  hashfunc.hh \
  intvcomp.hh \
  intvcmp2.hh \
  ltstr.hh \
  minato.hh \
  memusage.hh \
  modgray.hh \
  mspool.hh \
  optionmap.hh \
  random.hh \
  timer.hh \
  version.hh

noinst_LTLIBRARIES = libmisc.la
libmisc_la_SOURCES = \
  accconv.cc \
  bareword.cc \
  bddalloc.cc \
  bddop.cc \
  escape.cc \
  freelist.cc \
  intvcomp.cc \
  intvcmp2.cc \
  memusage.cc \
  minato.cc \
  modgray.cc \
  optionmap.cc \
  random.cc \
  timer.cc \
  version.cc
