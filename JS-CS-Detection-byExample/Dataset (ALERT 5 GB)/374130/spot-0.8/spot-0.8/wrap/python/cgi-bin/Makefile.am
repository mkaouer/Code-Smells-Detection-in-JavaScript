## Copyright (C) 2003, 2010  Laboratoire d'Informatique de Paris 6 (LIP6),
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

nodist_noinst_SCRIPTS = ltl2tgba.py
EXTRA_DIST = $(srcdir)/ltl2tgba.in README
CLEANFILES = $(nodist_noinst_SCRIPTS)

ltl2tgba.py: $(srcdir)/ltl2tgba.in Makefile
	sed -e 's|[@]PYTHON[@]|@PYTHON@|g' \
	    -e 's|[@]pythondir[@]|@pythondir@|g' \
	    -e 's|[@]srcdir[@]|@srcdir@|g' \
	    -e 's|[@]top_builddir[@]|@top_builddir@|g' \
	    -e 's|[@]PACKAGE_VERSION[@]|@PACKAGE_VERSION@|g' \
	    -e 's|[@]DOT[@]|@DOT@|g' \
	    <$(srcdir)/ltl2tgba.in >ltl2tgba.tmp
	chmod +x ltl2tgba.tmp
	mv -f ltl2tgba.tmp $@

clean-local:
	rm -rf spotimg
