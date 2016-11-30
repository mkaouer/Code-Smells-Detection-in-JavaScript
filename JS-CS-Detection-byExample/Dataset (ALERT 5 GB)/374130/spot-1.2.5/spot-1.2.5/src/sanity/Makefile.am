## -*- coding: utf-8 -*-
## Copyright (C) 2010, 2011, 2012, 2013 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
## Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

# Run `make TESTHEADER=foo.hh check' if you want to check only one
# header.
check-80columns:
	INCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/80columns.test $(TESTHEADER)

check-style:
	INCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/style.test $(TESTHEADER)

check-readme:
	top_srcdir='$(top_srcdir)' \
	$(PERL) $(srcdir)/readme.test

check-includes:
	CXX='$(CXX)' \
	CPPFLAGS='$(AM_CPPFLAGS) $(CPPFLAGS) $(DEFS)' \
	CXXFLAGS='$(AM_CXXFLAGS) $(CXXFLAGS)' \
	INCDIR='$(top_srcdir)/src' \
	TOPBUILD='$(top_builddir)' \
	$(SHELL) $(srcdir)/includes.test $(TESTHEADER)

.PHONY: check-80columns check-style check-readme check-includes
check-local: check-80columns check-style check-readme check-includes



# Ensure we have not forgotten to include an header.
check-installed-includes:
	CXX='$(CXX)' \
	CPPFLAGS='-I $(includedir) -I$(pkgincludedir) $(LIBGSPN_CPPFLAGS) $(CPPFLAGS)' \
	CXXFLAGS='$(AM_CXXFLAGS) $(CXXFLAGS)' \
	INCDIR='$(pkgincludedir)' \
	$(SHELL) $(srcdir)/includes.test $(TESTHEADER)

# Any installed header should contain a SPOT_API tag somewhere.
check-installed-private:
	INCDIR='$(pkgincludedir)' \
	SRCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/private.test $(TESTHEADER)


installcheck-local: check-installed-includes check-installed-private

CLEANFILES = failures incltest.*
EXTRA_DIST = includes.test 80columns.test style.test readme.test private.test
