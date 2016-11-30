# -*- mode: python; coding: utf-8 -*-
# Copyright (C) 2012, 2014 Laboratoire de Recherche et Développement
# de l'Epita (LRDE).
#
# This file is part of Spot, a model checking library.
#
# Spot is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 3 of the License, or
# (at your option) any later version.
#
# Spot is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
# License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

import os
import spot

contents = '''
acc = "b";
"a U b", "1", "b", "b";
"a U b", "a U b", "a & !b",;
"1", "1", "1", "b";
'''

filename = 'parsetgba.out'

out = open(filename, 'w+')
out.write(contents)
out.close()

d = spot.bdd_dict()
p = spot.empty_tgba_parse_error_list()
a = spot.tgba_parse(filename, p, d)

assert not p

spot.dotty_reachable(spot.get_cout(), a)

del p
del a
del d

os.unlink(filename)
