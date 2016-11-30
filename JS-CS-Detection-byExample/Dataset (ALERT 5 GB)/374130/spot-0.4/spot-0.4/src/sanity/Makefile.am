## Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

AM_CPPFLAGS = -I$(srcdir)/.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)

# Run `make TESTHEADER=foo.hh check' if you want to check only one
# header.
check-local:
	CXX='$(CXX)' \
	CPPFLAGS='$(AM_CPPFLAGS) $(CPPFLAGS)' \
	CXXFLAGS='$(AM_CXXFLAGS) $(CXXFLAGS)' \
	INCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/includes.test $(TESTHEADER)
	INCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/80columns.test $(TESTHEADER)
	INCDIR='$(top_srcdir)/src' \
	$(SHELL) $(srcdir)/style.test $(TESTHEADER)

# Ensure we have not forgotten to include an header.
installcheck-local:
	CXX='$(CXX)' \
	CPPFLAGS='-I $(includedir) -I$(pkgincludedir) $(LIBGSPN_CPPFLAGS) $(CPPFLAGS)' \
	CXXFLAGS='$(AM_CXXFLAGS) $(CXXFLAGS)' \
	INCDIR='$(pkgincludedir)' \
	$(SHELL) $(srcdir)/includes.test $(TESTHEADER)


CLEANFILES = failures incltest.*
EXTRA_DIST = includes.test 80columns.test style.test
