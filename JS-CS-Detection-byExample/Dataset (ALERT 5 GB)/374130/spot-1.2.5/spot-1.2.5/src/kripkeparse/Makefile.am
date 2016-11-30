## Copyright (C) 2011, 2013 Laboratoire de Recherche et Développement de
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

kripkeparsedir = $(pkgincludedir)/kripkeparse

kripkeparse_HEADERS = public.hh

noinst_LTLIBRARIES = libkripkeparse.la

KRIPKEPARSE_YY = kripkeparse.yy
FROM_KRIPKEPARSE_YY_MAIN = kripkeparse.cc
FROM_KRIPKEPARSE_YY_OTHERS = \
  stack.hh \
  kripkeparse.hh

FROM_KRIPKEPARSE_YY = \
	 $(FROM_KRIPKEPARSE_YY_MAIN) \
	$(FROM_KRIPKEPARSE_YY_OTHERS)

BUILT_SOURCES = $(FROM_KRIPKEPARSE_YY)
MAINTAINERCLEANFILES = $(FROM_KRIPKEPARSE_YY)

$(FROM_KRIPKEPARSE_YY_MAIN): $(srcdir)/$(KRIPKEPARSE_YY)
## We must cd into $(srcdir) first because if we tell bison to read
## $(srcdir)/$(KRIPKEPARSE_YY), it will also use the value of $(srcdir)/
## in the generated include statements.
	cd $(srcdir) && \
	  $(BISON) -Wall -Werror --report=all $(BISON_EXTRA_FLAGS) \
	  $(KRIPKEPARSE_YY) -o $(FROM_KRIPKEPARSE_YY_MAIN)
$(FROM_KRIPKEPARSE_YY_OTHERS): $(KRIPKEPARSE_YY)
	@test -f $@ || $(MAKE) $(AM_MAKEFLAGS) $(FROM_KRIPKEPARSE_YY_MAIN)

EXTRA_DIST = $(KRIPKEPARSE_YY)

libkripkeparse_la_SOURCES = \
  $(FROM_KRIPKEPARSE_YY) \
  kripkescan.ll \
  parsedecl.hh \
  fmterror.cc
