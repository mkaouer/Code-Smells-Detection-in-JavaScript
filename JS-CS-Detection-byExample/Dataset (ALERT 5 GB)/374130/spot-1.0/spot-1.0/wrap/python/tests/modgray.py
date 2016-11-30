# -*- mode: python; coding: utf-8 -*-
# Copyright (C) 2010, 2012 Laboratoire de Recherche et Développement
# de l'Epita.
# Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

import spot
import sys

class test(spot.loopless_modular_mixed_radix_gray_code):
    def __init__(self, lim):
        spot.loopless_modular_mixed_radix_gray_code.__init__(self, len(lim))
        self.msg = list(lim)
        self.lim = list(lim)

    def a_first(self, j):
        self.msg[j] = 'a'

    def a_next(self, j):
        self.msg[j] = chr(1 + ord(self.msg[j]))

    def a_last(self, j):
        return self.msg[j] == self.lim[j]

    def run(self):
        self.first()
        res = []
        while not self.done():
            m = "".join(self.msg)
            res.append(m)
            sys.stdout.write(m + "\n")
            self.next()
        return res


t = test("acbb")

expected = [ 'aaaa', 'abaa', 'acaa', 'acba',
             'aaba', 'abba', 'abbb', 'acbb',
             'aabb', 'aaab', 'abab', 'acab' ]

assert t.run() == expected
