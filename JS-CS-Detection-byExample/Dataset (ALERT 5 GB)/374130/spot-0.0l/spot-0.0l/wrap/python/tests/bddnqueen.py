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

# Python translation of the C++ example from the BuDDy distribution.
# (compare with buddy/examples/queen/queen.cxx)

import ltihooks
import sys
from buddy import *

# Build the requirements for all other fields than (i,j) assuming
# that (i,j) has a queen.
def build(i, j):
    a = b = c = d = bddtrue
    
    # No one in the same column.
    for l in side:
        if l != j:
            a &= X[i][j] >> -X[i][l]

    # No one in the same row.
    for k in side:
        if k != i:
            b &= X[i][j] >> -X[k][j]

    # No one in the same up-right diagonal.
    for k in side:
        ll = k - i + j
        if ll >= 0 and ll < N:
            if k != i:
                c &= X[i][j] >> -X[k][ll]

    # No one in the same down-right diagonal.
    for k in side:
        ll = i + j - k
        if ll >= 0 and ll < N:
            if k != i:
                c &= X[i][j] >> -X[k][ll]

    global queen
    queen &= a & b & c & d



# Get the number of queens from the command-line, or default to 8.
if len(sys.argv) > 1:
    N = int(argv[1])
else:
    N = 8

side = range(N)

# Initialize with 100000 nodes, 10000 cache entries and NxN variables.
bdd_init(N * N * 256, 10000)
bdd_setvarnum(N * N)

queen = bddtrue

# Build variable array.
X = [[bdd_ithvar(i*N+j) for j in side] for i in side]

# Place a queen in each row.
for i in side:
    e = bddfalse
    for j in side:
        e |= X[i][j]
    queen &= e

# Build requirements for each variable(field).
for i in side:
    for j in side:
        print "Adding position %d, %d" % (i, j)
        build(i, j)

# Print the results.
print "There are", bdd_satcount(queen), "solutions"
print "one is:"
solution = bdd_satone(queen)
bdd_printset(solution)
print

bdd_done()
