## -*- coding: utf-8 -*-
## Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et Développement
## de l'Epita (LRDE).
## Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

if WITH_INCLUDED_BUDDY
  MAYBE_BUDDY = buddy
endif WITH_INCLUDED_BUDDY
if NEVER
  # For Automake a conditional directory
  # is conditionally built, but unconditionally distributed.
  # So using NEVER here ensures that `make all' will not
  # recurse in bench/, but `make dist' will.
  NEVER_BENCH = bench
endif

SUBDIRS = $(MAYBE_BUDDY) $(NEVER_BENCH) lib src wrap ltdl iface doc

UTF8 = utf8/doc/ReleaseNotes utf8/doc/utf8cpp.html utf8/utf8.h	\
  utf8/utf8/checked.h utf8/utf8/core.h utf8/utf8/unchecked.h

ACLOCAL_AMFLAGS = -I m4
EXTRA_DIST = HACKING ChangeLog.1 tools/gitlog-to-changelog tools/help2man \
	     tools/test-driver-teamcity $(UTF8) m4/gnulib-cache.m4 \
	     .dir-locals.el

dist-hook: gen-ChangeLog

gen_start = 2012-03-10
.PHONY: gen-ChangeLog
gen-ChangeLog:
	if test -d .git; then						\
## Generate all commits in the form "SHA1 subject"
	  git log --since=$(gen_start) --pretty=oneline |		\
## Filter out commits whose subject start with '['.  These are usually
## [buddy] or [lbtt] tags to indicate we are committing on a subproject.
            grep -v '........................................ \[' |	\
## Keep the SHA1
	    cut -c 1-40 |						\
## Feed all that to gitlog-to-changelog
            $(top_srcdir)/tools/gitlog-to-changelog			\
	      --no-cluster --format='%s%n%n%b%n' --			\
              --stdin --no-walk > $(distdir)/cl-t;			\
          rm -f $(distdir)/ChangeLog;					\
          mv $(distdir)/cl-t $(distdir)/ChangeLog;			\
	fi
