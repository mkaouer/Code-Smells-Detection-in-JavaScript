## -*- coding: utf-8 -*-
## Copyright (C) 2013 Laboratoire de Recherche et Développement de
## l'Epita (LRDE).
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

AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS) -DYY_NO_INPUT
# Disable -Werror because too many versions of flex yield warnings.
AM_CXXFLAGS = $(WARNING_CXXFLAGS:-Werror=)

dstarparsedir = $(pkgincludedir)/dstarparse

dstarparse_HEADERS = public.hh

noinst_LTLIBRARIES = libdstarparse.la

DSTARPARSE_YY = dstarparse.yy
FROM_DSTARPARSE_YY_MAIN = dstarparse.cc
FROM_DSTARPARSE_YY_OTHERS = \
  stack.hh \
  dstarparse.hh

FROM_DSTARPARSE_YY = $(FROM_DSTARPARSE_YY_MAIN) $(FROM_DSTARPARSE_YY_OTHERS)

BUILT_SOURCES = $(FROM_DSTARPARSE_YY)
MAINTAINERCLEANFILES = $(FROM_DSTARPARSE_YY)

$(FROM_DSTARPARSE_YY_MAIN): $(srcdir)/$(DSTARPARSE_YY)
## We must cd into $(srcdir) first because if we tell bison to read
## $(srcdir)/$(DSTARPARSE_YY), it will also use the value of $(srcdir)/
## in the generated include statements.
	cd $(srcdir) && \
	  $(BISON) -Wall -Werror --report=all $(BISON_EXTRA_FLAGS) \
	  $(DSTARPARSE_YY) -o $(FROM_DSTARPARSE_YY_MAIN)
$(FROM_DSTARPARSE_YY_OTHERS): $(DSTARPARSE_YY)
	@test -f $@ || $(MAKE) $(AM_MAKEFLAGS) $(FROM_DSTARPARSE_YY_MAIN)

EXTRA_DIST = $(DSTARPARSE_YY)

libdstarparse_la_SOURCES = \
  fmterror.cc \
  dra2ba.cc \
  dstar2tgba.cc \
  nra2nba.cc \
  nsa2tgba.cc \
  $(FROM_DSTARPARSE_YY) \
  dstarscan.ll \
  parsedecl.hh
