## Copyright (C) 2011, 2013 Laboratoire de Recherche et Developpement
## de l'Epita (LRDE).
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

AM_CPPFLAGS = -I$(top_srcdir)/src -I$(top_builddir)/src \
              $(BUDDY_CPPFLAGS) -I$(top_srcdir)/ltdl
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

dve2dir = $(pkgincludedir)/iface/dve2

dve2_HEADERS = dve2.hh

lib_LTLIBRARIES = libspotdve2.la
libspotdve2_la_LIBADD = \
  $(top_builddir)/src/libspot.la \
  $(top_builddir)/ltdl/libltdlc.la
libspotdve2_la_SOURCES = dve2.cc

noinst_PROGRAMS = dve2check

dve2check_SOURCES = dve2check.cc
dve2check_LDADD = libspotdve2.la

check_SCRIPTS = defs

TESTS = dve2check.test finite.test kripke.test
EXTRA_DIST = $(TESTS) beem-peterson.4.dve finite.dve

kripke.test: $(top_builddir)/src/kripketest/parse_print$(EXEEXT)

$(top_builddir)/src/kripketest/parse_print$(EXEEXT):
	cd $(top_builddir)/src/kripketest && \
	$(MAKE) $(AM_MAKEFLAGS) parse_print$(EXEEXT)

distclean-local:
	rm -rf $(TESTS:.test=.dir)
