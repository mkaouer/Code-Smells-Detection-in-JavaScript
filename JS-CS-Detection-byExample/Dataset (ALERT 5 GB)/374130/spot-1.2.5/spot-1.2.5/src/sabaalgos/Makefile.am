## -*- coding: utf-8 -*-
## Copyright (C) 2009, 2011, 2013 Laboratoire de Recherche et
## Développement de l'Epita (LRDE).
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

sabaalgosdir = $(pkgincludedir)/sabaalgos

sabaalgos_HEADERS = sabadotty.hh sabareachiter.hh

noinst_LTLIBRARIES = libsabaalgos.la
libsabaalgos_la_SOURCES = sabadotty.cc sabareachiter.cc
