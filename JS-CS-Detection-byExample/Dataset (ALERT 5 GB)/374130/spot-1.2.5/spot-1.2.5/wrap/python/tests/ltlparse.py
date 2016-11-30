# -*- mode: python; coding: utf-8 -*-
# Copyright (C) 2009, 2010, 2012, 2014 Laboratoire de Recherche et
# Développement de l'Epita (LRDE).
# Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
# département Systèmes Répartis Coopératifs (SRC), Université Pierre
# et Marie Curie.
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

import sys
import spot

e = spot.default_environment.instance()
p = spot.empty_parse_error_list()

l = ['GFa', 'a U (((b)) xor c)', '!(FFx <=> Fx)', 'a \/ a \/ b \/ a \/ a'];

for str1 in l:
    f = spot.parse(str1, p, e, False)
    if spot.format_parse_errors(spot.get_cout(), str1, p):
        sys.exit(1)
    str2 = str(f)
    f.destroy()
    sys.stdout.write(str2 + "\n")
    # Try to reparse the stringified formula
    f = spot.parse(str2, p, e)
    if spot.format_parse_errors(spot.get_cout(), str2, p):
        sys.exit(1)
    sys.stdout.write(str(f) + "\n")
    f.destroy()

assert spot.atomic_prop.instance_count() == 0
assert spot.binop.instance_count() == 0
assert spot.unop.instance_count() == 0
assert spot.multop.instance_count() == 0
