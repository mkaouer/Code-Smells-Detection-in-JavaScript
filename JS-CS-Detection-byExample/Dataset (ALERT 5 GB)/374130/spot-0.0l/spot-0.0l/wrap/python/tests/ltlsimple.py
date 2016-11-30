# Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

import ltihooks
import spot

clone = spot.clone

e = spot.default_environment.instance()

#----------------------------------------------------------------------
a = e.require('a')
b = e.require('b')
c = e.require('c')
c2 = e.require('c')

assert c == c2
assert spot.atomic_prop.instance_count() == 3

op  = spot.multop.instance(spot.multop.And, clone(a), clone(b))
op2 = spot.multop.instance(spot.multop.And, op, c)

# The symbol for a subformula which hasn't been cloned is better
# suppressed, so we don't attempt to reuse it elsewhere.
del op, c

print 'op2 =', op2

op3 = spot.multop.instance(spot.multop.And, b,
			   spot.multop.instance(spot.multop.And, c2, a))
del a, b, c2

print 'op3 =', op3
assert op2 == op3

op4 = spot.multop.instance(spot.multop.Or, op2, op3)

print 'op4 =', op4
assert op4 == op2

del op2, op3

assert spot.atomic_prop.instance_count() == 3
assert spot.multop.instance_count() == 1

spot.destroy(op4)
del op4

assert spot.atomic_prop.instance_count() == 0
assert spot.multop.instance_count() == 0

#----------------------------------------------------------------------
a = e.require('a')
b = e.require('b')
c = e.require('c')
T = spot.constant.true_instance()
F = spot.constant.false_instance()

f1 = spot.binop.instance(spot.binop.Equiv, T, clone(a))
f2 = spot.binop.instance(spot.binop.Implies, F, clone(b))
f3 = spot.binop.instance(spot.binop.Xor, F, clone(c))
f4 = spot.unop.instance(spot.unop.Not, f3); del f3

assert spot.atomic_prop.instance_count() == 3
assert spot.binop.instance_count() == 3
assert spot.unop.instance_count() == 1
assert spot.multop.instance_count() == 0

spot.destroy(a)
del a
spot.destroy(b)
del b
spot.destroy(c)
del c

assert spot.atomic_prop.instance_count() == 3
assert spot.binop.instance_count() == 3
assert spot.unop.instance_count() == 1
assert spot.multop.instance_count() == 0

spot.destroy(f1)
del f1

assert spot.atomic_prop.instance_count() == 2
assert spot.binop.instance_count() == 2
assert spot.unop.instance_count() == 1
assert spot.multop.instance_count() == 0

spot.destroy(f4)
del f4

assert spot.atomic_prop.instance_count() == 1
assert spot.binop.instance_count() == 1
assert spot.unop.instance_count() == 0
assert spot.multop.instance_count() == 0

spot.destroy(f2)
del f2

assert spot.atomic_prop.instance_count() == 0
assert spot.binop.instance_count() == 0
assert spot.unop.instance_count() == 0
assert spot.multop.instance_count() == 0
