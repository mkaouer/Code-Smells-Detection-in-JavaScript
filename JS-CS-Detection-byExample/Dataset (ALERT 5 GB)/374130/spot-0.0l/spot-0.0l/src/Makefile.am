## Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

AUTOMAKE_OPTIONS = subdir-objects

# List directories in the order they must be built.
# Keep tests at the end.
SUBDIRS = misc ltlenv ltlast ltlvisit ltlparse tgba tgbaalgos tgbaparse . \
	  ltltest tgbatest

lib_LTLIBRARIES = libspot.la
libspot_la_SOURCES =
libspot_la_LDFLAGS = $(BUDDY_LDFLAGS)
libspot_la_LIBADD =  \
  misc/libmisc.la \
  ltlenv/libltlenv.la \
  ltlparse/libltlparse.la \
  ltlvisit/libltlvisit.la \
  ltlast/libltlast.la \
  tgba/libtgba.la \
  tgbaalgos/libtgbaalgos.la \
  tgbaparse/libtgbaparse.la
