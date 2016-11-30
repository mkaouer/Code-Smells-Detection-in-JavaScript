# Copyright (C) 2003, 2004, 2005, 2006, 2007  Laboratoire d'Informatique
# de Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
# Université Pierre et Marie Curie.
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

AC_PREREQ([2.61])
AC_INIT([spot], [0.4])
AC_CONFIG_AUX_DIR([tools])
AM_INIT_AUTOMAKE([1.10 gnits nostdinc tar-ustar])

# If the user didn't supplied a CFLAGS value,
# set an empty one to prevent autoconf to stick -O2 -g here.
test -z "$CFLAGS" && CFLAGS=
test -z "$CXXFLAGS" && CXXFLAGS=

adl_ENABLE_DEVEL
adl_CHECK_PYTHON

AC_PROG_CC
AM_PROG_CC_C_O
AC_PROG_CXX

AM_PROG_LEX
AC_PROG_YACC

AC_LANG(C++)

AX_CHECK_BUDDY
AX_CHECK_LBTT
AX_CHECK_GSPNLIB

AC_CHECK_FUNCS([srand48 drand48])

AC_LIBTOOL_WIN32_DLL
AC_PROG_LIBTOOL

CF_GXX_WARNINGS
adl_ENABLE_DEBUG
ad_GCC_OPTIM
adl_NDEBUG

AM_CONDITIONAL([NEVER], [false])
AC_CHECK_PROG([DOT], [dot], [dot])
AC_CHECK_PROG([LBT], [lbt], [lbt])
AC_CHECK_PROG([LTL2BA], [ltl2ba], [ltl2ba])
AC_CHECK_PROG([MODELLA], [modella], [modella])
AC_CHECK_PROG([LTL2NBA], [script4lbtt.py], [script4lbtt.py])
AC_CHECK_PROG([PERL], [perl], [perl])
AC_CHECK_PROG([SPIN], [spin], [spin])
AC_CHECK_PROG([VALGRIND], [valgrind], [valgrind])
AC_CHECK_PROG([WRING2LBTT], [wring2lbtt], [wring2lbtt])

AC_CONFIG_FILES([
  Makefile
  bench/Makefile
  bench/emptchk/Makefile
  bench/emptchk/defs
  bench/ltl2tgba/Makefile
  bench/ltl2tgba/defs
  doc/Doxyfile
  doc/Makefile
  iface/Makefile
  iface/gspn/Makefile
  iface/gspn/defs
  src/Makefile
  src/evtgba/Makefile
  src/evtgbaalgos/Makefile
  src/evtgbaparse/Makefile
  src/evtgbatest/Makefile
  src/evtgbatest/defs
  src/ltlast/Makefile
  src/ltlenv/Makefile
  src/ltlparse/Makefile
  src/ltltest/Makefile
  src/ltltest/defs
  src/ltlvisit/Makefile
  src/misc/Makefile
  src/sanity/Makefile
  src/tgba/Makefile
  src/tgbaalgos/Makefile
  src/tgbaalgos/gtec/Makefile
  src/tgbaparse/Makefile
  src/tgbatest/Makefile
  src/tgbatest/defs
  wrap/Makefile
  wrap/python/Makefile
  wrap/python/cgi/Makefile
  wrap/python/tests/Makefile
])
AC_CONFIG_FILES([bench/ltl2tgba/ltl2baw.pl:bench/ltl2tgba/ltl2baw.in],
	        [chmod +x bench/ltl2tgba/ltl2baw.pl])
AC_CONFIG_FILES([wrap/python/tests/run], [chmod +x wrap/python/tests/run])
AC_OUTPUT
