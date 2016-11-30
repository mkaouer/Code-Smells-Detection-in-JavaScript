## Copyright (C) 2011 Laboratoire de Recherche et Développement
## de l'Epita (LRDE).
## Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

if WITH_INCLUDED_BUDDY
  MAYBE_BUDDY = buddy
endif WITH_INCLUDED_BUDDY
if WITH_INCLUDED_LBTT
  MAYBE_LBTT = lbtt
endif WITH_INCLUDED_LBTT
if NEVER
  # For Automake a conditional directory
  # is conditionally built, but unconditionally distributed.
  # So using NEVER here ensures that `make all' will not
  # recurse in bench/, but `make dist' will.
  NEVER_BENCH = bench
endif

SUBDIRS = $(MAYBE_BUDDY) $(MAYBE_LBTT) $(NEVER_BENCH) doc src wrap ltdl iface

ACLOCAL_AMFLAGS = -I m4
EXTRA_DIST = HACKING
