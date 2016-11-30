## Copyright (C) 2011 Laboratoire de Recherche et Developpement de
## l'Epita (LRDE)
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

AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS)

kripketestdir = $(pkgincludedir)/kripketest

kripketest_HEADERS =


LDADD = ../libspot.la

check_SCRIPTS = defs

parse_print_SOURCES = parse_print_test.cc
check_PROGRAMS = \
parse_print

TESTS = \
  kripke.test \
  bad_parsing.test

EXTRA_DIST = $(TESTS)

distclean-local:
	rm -rf $(TESTS:.test=.dir)
