## Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
## Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
## Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

AM_CPPFLAGS = -I$(srcdir)/.. -I.. $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)
LDADD = ../libspot.la

check_SCRIPTS = defs
# Keep this sorted alphabetically.
check_PROGRAMS = \
  consterm \
  equals \
  kind \
  length \
  ltl2dot \
  ltl2text \
  lunabbrev \
  nenoform \
  reduc \
  reduccmp \
  reductau \
  reductaustr \
  syntimpl \
  tostring \
  tunabbrev \
  tunenoform \
  unabbrevwm

consterm_SOURCES = consterm.cc
equals_SOURCES = equals.cc
kind_SOURCES = kind.cc
length_SOURCES = length.cc
ltl2dot_SOURCES = readltl.cc
ltl2dot_CPPFLAGS = $(AM_CPPFLAGS) -DDOTTY
ltl2text_SOURCES = readltl.cc
lunabbrev_SOURCES = equals.cc
lunabbrev_CPPFLAGS = $(AM_CPPFLAGS) -DLUNABBREV
nenoform_SOURCES = equals.cc
nenoform_CPPFLAGS = $(AM_CPPFLAGS) -DNENOFORM
reduc_SOURCES = reduc.cc
reduccmp_SOURCES = equals.cc
reduccmp_CPPFLAGS = $(AM_CPPFLAGS) -DREDUC
reductau_SOURCES = equals.cc
reductau_CPPFLAGS = $(AM_CPPFLAGS) -DREDUC_TAU
reductaustr_SOURCES = equals.cc
reductaustr_CPPFLAGS = $(AM_CPPFLAGS) -DREDUC_TAUSTR
syntimpl_SOURCES = syntimpl.cc
tostring_SOURCES = tostring.cc
tunabbrev_SOURCES = equals.cc
tunabbrev_CPPFLAGS = $(AM_CPPFLAGS) -DTUNABBREV
tunenoform_SOURCES = equals.cc
tunenoform_CPPFLAGS = $(AM_CPPFLAGS) -DNENOFORM -DTUNABBREV
unabbrevwm_SOURCES = equals.cc
unabbrevwm_CPPFLAGS = $(AM_CPPFLAGS) -DWM


EXTRA_DIST = $(TESTS)

# Ordered by strength of the test.  Test basic features first.
TESTS = \
  parse.test \
  parseerr.test \
  utf8.test \
  length.test \
  equals.test \
  tostring.test \
  lunabbrev.test \
  tunabbrev.test \
  nenoform.test \
  tunenoform.test \
  unabbrevwm.test \
  consterm.test \
  kind.test \
  lenient.test \
  syntimpl.test \
  reduc.test \
  reducpsl.test \
  reduccmp.test \
  uwrm.test

distclean-local:
	rm -rf $(TESTS:.test=.dir)
