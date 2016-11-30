## Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

DOXYGEN = doxygen

.PHONY: doc fast-doc

all-local: $(srcdir)/stamp

doc:
	-rm -f $(srcdir)/stamp
	$(MAKE) $(srcdir)/stamp

fast-doc:
	$(MAKE) Doxyfile
	$(DOXYGEN)
	touch $(srcdir)/stamp

$(srcdir)/stamp: $(srcdir)/Doxyfile.in $(top_srcdir)/configure.ac
	$(MAKE) Doxyfile
	-rm -rf spot.html spot.latex
	$(DOXYGEN)
	touch $@

$(srcdir)/spot.html $(srcdir)/spot.tag: $(srcdir)/stamp
spot.latex: $(srcdir)/stamp

$(srcdir)/spotref.pdf: $(srcdir)/stamp
	cd spot.latex \
	  && texi2dvi --pdf refman.tex \
	  && cd .. && mv -f spot.latex/refman.pdf $@

# spot.tag is useful to third-party packages that must link to the
# Spot documentation.
dist_pkgdata_DATA = $(srcdir)/spot.tag

EXTRA_DIST = \
  footer.html \
  mainpage.dox \
  $(srcdir)/stamp \
  $(srcdir)/spot.html \
  $(srcdir)/spotref.pdf
