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

# This is a python translation of the ltl2tgba C++ test program.
# Compare with src/tgbatest/ltl2tgba.test.

import sys
import getopt
import ltihooks
import spot

def usage(prog):
    print "Usage: ", prog, """ [OPTIONS...] formula

Options:
  -a   display the acceptance_conditions BDD, not the reachability graph
  -A   same as -a, but as a set
  -d   turn on traces during parsing
  -D   degeneralize the automaton
  -f   use Couvreur's FM algorithm for translation
  -r   display the relation BDD, not the reachability graph
  -R   same as -r, but as a set
  -t   display reachable states in LBTT's format
  -v   display the BDD variables used by the automaton"""
    sys.exit(2)


prog = sys.argv[0]
try:
    opts, args = getopt.getopt(sys.argv[1:], 'aAdDfrRtv')
except getopt.GetoptError:
    usage(prog)

exit_code = 0
debug_opt = 0
degeneralize_opt = None
output = 0
fm_opt = 0

for o, a in opts:
    if o == '-a':
        output = 2
    elif o == '-A':
        output = 4
    elif o == '-d':
        debug_opt = 1
    elif o == '-D':
        degeneralize_opt = 1
    elif o == '-f':
        fm_opt = 1
    elif o == '-r':
        output = 1
    elif o == '-R':
        output = 3
    elif o == '-t':
        output = 6
    elif o == '-v':
        output = 5
    else:
        usage(prog)

if len(args) != 1:
    usage(prog)


cout = spot.get_cout()
cerr = spot.get_cerr()

e = spot.default_environment.instance()
p = spot.empty_parse_error_list()

f = spot.parse(args[0], p, e, debug_opt)
if spot.format_parse_errors(cerr, args[0], p):
    exit_code = 1

dict = spot.bdd_dict()

if f:
    if fm_opt:
        a = spot.ltl_to_tgba_fm(f, dict)
        concrete = 0
    else:
        a = concrete = spot.ltl_to_tgba_lacim(f, dict)
    spot.destroy(f)
    del f

    degeneralized = None
    if degeneralize_opt:
        a = degeneralized = spot.tgba_tba_proxy(a)

    if output == 0:
        spot.dotty_reachable(cout, a)
    elif output == 1:
        if concrete:
            spot.bdd_print_dot(cout, concrete.get_dict(),
                               concrete.get_core_data().relation)
    elif output == 2:
        if concrete:
            spot.bdd_print_dot(cout, concrete.get_dict(),
                               concrete.get_core_data().acceptance_conditions)
    elif output == 3:
        if concrete:
            spot.bdd_print_set(cout, concrete.get_dict(),
                               concrete.get_core_data().relation)
        print
    elif output == 4:
        if concrete:
            spot.bdd_print_set(cout, concrete.get_dict(),
                               concrete.get_core_data().acceptance_conditions)
        print
    elif output == 5:
        a.get_dict().dump(cout)
    elif output == 6:
        spot.lbtt_reachable(cout, a)
    else:
        assert "unknown output option"

    if degeneralize_opt:
        del degeneralized
    # Must delete absolutely all references to an automaton
    # so that the C++ destructor gets called.
    del a, concrete

else:
    exit_code = 1

del dict;

assert spot.atomic_prop.instance_count() == 0
assert spot.unop.instance_count() == 0
assert spot.binop.instance_count() == 0
assert spot.multop.instance_count() == 0
