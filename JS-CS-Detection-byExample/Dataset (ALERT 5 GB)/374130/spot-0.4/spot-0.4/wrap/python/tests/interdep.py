# -*- mode: python; coding: iso-8859-1 -*-
# Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
# département Systèmes Répartis Coopératifs (SRC), Université Pierre
# et Marie Curie.
#
# This file is part of Spot, a model checking library.
#
# Spot is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# Spot is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
# License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Spot; see the file COPYING.  If not, write to the Free
# Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
# 02111-1307, USA.

# Make sure that interdependencies between the spot and buddy wrappers
# are not problematic.
import ltihooks
import spot
import buddy
e = spot.default_environment.instance()
p = spot.empty_parse_error_list()
f = spot.parse('GFa', p, e)
dict = spot.bdd_dict()
a = spot.ltl_to_tgba_lacim(f, dict)
s0 = a.get_init_state()
b = s0.as_bdd()
print b
iter = a.succ_iter(s0)
iter.first()
while not iter.done():
    c = iter.current_condition()
    print c
    b &= c # `&=' is defined only in buddy.  So if this statement works
           # it means buddy can grok spot's objects.
    iter.next()
print b
