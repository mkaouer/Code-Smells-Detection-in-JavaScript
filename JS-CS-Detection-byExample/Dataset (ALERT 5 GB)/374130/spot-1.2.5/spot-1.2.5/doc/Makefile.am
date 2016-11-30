## -*- coding: utf-8 -*-
## Copyright (C) 2010, 2011, 2013 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
## Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
## 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
## Université Pierre et Marie Curie.
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

DOXYGEN = doxygen

SUBDIRS = tl

.PHONY: doc fast-doc

all-local: $(srcdir)/stamp $(srcdir)/org-stamp

doc:
	-rm -f $(srcdir)/stamp
	$(MAKE) $(srcdir)/stamp

fast-doc:
	$(MAKE) Doxyfile
	$(DOXYGEN)
	touch $(srcdir)/stamp

$(srcdir)/stamp: $(srcdir)/Doxyfile.in $(top_srcdir)/configure.ac
	$(MAKE) Doxyfile dot
	-rm -rf spot.html spot.latex
	$(DOXYGEN)
	touch $@

$(srcdir)/spot.html $(srcdir)/spot.tag: $(srcdir)/stamp

# spot.tag is useful to third-party packages that must link to the
# Spot documentation.
dist_pkgdata_DATA = $(srcdir)/spot.tag

.PHONY: org
org:
	cd $(top_builddir) && make doc/org/init.el
	rm -rf $(srcdir)/userdoc; mkdir -p tmp; cd tmp; \
	$(EMACS) --batch -Q -l ../org/init.el
	rm -rf tmp

ORG_FILES = \
  org/.dir-locals.el \
  org/init.el.in \
  org/syntax.css \
  org/csv.org \
  org/dstar2tgba.org \
  org/genltl.org \
  org/ioltl.org \
  org/ltl2tgba.org \
  org/ltl2tgta.org \
  org/ltlcross.org \
  org/ltlfilt.org \
  org/randltl.org \
  org/tools.org \
  org/satmin.org \
  org/satmin.tex \
  $(srcdir)/org/satmin.png

$(srcdir)/org/satmin.png: org/satmin.tex
	cd $(srcdir)/org && \
	pdflatex -shell-escape satmin.tex && \
	rm -f satmin.pdf satmin.aux satmin.log

$(srcdir)/org-stamp: $(ORG_FILES) $(configure_ac)
	make org && touch $@

$(srcdir)/userdoc: $(srcdir)/org-stamp

EXTRA_DIST = \
  footer.html \
  mainpage.dox \
  $(srcdir)/stamp \
  $(srcdir)/spot.html \
  $(ORG_FILES) \
  $(srcdir)/org-stamp \
  $(srcdir)/userdoc

.PHONY: org-deploy
org-deploy: $(srcdir)/org-stamp
	cd $(srcdir); \
	rsync -P -z -u -v -r -l -C --delete userdoc/ spot:www_spot/userdoc
