## Copyright (C) 2008, 2009, 2011 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
## Copyright (C) 2004, 2006 Laboratoire d'Informatique de Paris 6
## (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
## Pierre et Marie Curie.
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

AM_CPPFLAGS = -I$(srcdir)/.. $(BUDDY_CPPFLAGS) -DYY_NO_INPUT
# Disable -Werror because too many versions of flex yield warnings.
AM_CXXFLAGS = $(WARNING_CXXFLAGS:-Werror=)

evtgbaparsedir = $(pkgincludedir)/evtgbaparse

evtgbaparse_HEADERS = \
  public.hh \
  location.hh \
  position.hh

noinst_LTLIBRARIES = libevtgbaparse.la

EVTGBAPARSE_YY = evtgbaparse.yy
FROM_EVTGBAPARSE_YY_MAIN = evtgbaparse.cc
FROM_EVTGBAPARSE_YY_OTHERS = \
  stack.hh \
  position.hh \
  location.hh \
  evtgbaparse.hh

FROM_EVTGBAPARSE_YY = $(FROM_EVTGBAPARSE_YY_MAIN) $(FROM_EVTGBAPARSE_YY_OTHERS)

BUILT_SOURCES = $(FROM_EVTGBAPARSE_YY)
MAINTAINERCLEANFILES = $(FROM_EVTGBAPARSE_YY)

$(FROM_EVTGBAPARSE_YY_MAIN): $(srcdir)/$(EVTGBAPARSE_YY)
## We must cd into $(srcdir) first because if we tell bison to read
## $(srcdir)/$(EVTGBAPARSE_YY), it will also use the value of $(srcdir)/
## in the generated include statements.
	cd $(srcdir) && \
	  bison -Wall -Werror --report=all \
	  $(EVTGBAPARSE_YY) -o $(FROM_EVTGBAPARSE_YY_MAIN)
$(FROM_EVTGBAPARSE_YY_OTHERS): $(EVTGBAPARSE_YY)
	@test -f $@ || $(MAKE) $(AM_MAKEFLAGS) $(FROM_EVTGBAPARSE_YY_MAIN)

EXTRA_DIST = $(EVTGBAPARSE_YY)

libevtgbaparse_la_SOURCES = \
  evtgbascan.ll \
  fmterror.cc \
  $(FROM_EVTGBAPARSE_YY) \
  parsedecl.hh
