# Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche et
# Développement de l'Epita (LRDE).
# Copyright (C) 2003, 2004, 2005, 2006, 2007 Laboratoire d'Informatique de
# Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
# Pierre et Marie Curie.
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

AC_PREREQ([2.61])
AC_INIT([spot], [1.0], [spot@lrde.epita.fr])
AC_CONFIG_AUX_DIR([tools])
AC_CONFIG_MACRO_DIR([m4])
AM_INIT_AUTOMAKE([1.11 gnu tar-ustar color-tests parallel-tests])
AC_CONFIG_HEADERS([config.h])dnl Private config, not to be used in .hh files.
AX_PREFIX_CONFIG_H([src/misc/_config.h])dnl Public config, for .hh files.

# If the user didn't supply a CFLAGS value,
# set an empty one to prevent autoconf to stick -O2 -g here.
test -z "$CFLAGS" && CFLAGS=
test -z "$CXXFLAGS" && CXXFLAGS=

adl_ENABLE_DEVEL

AC_ARG_ENABLE([python],
              [AC_HELP_STRING([--disable-python],
                              [do not compile Python bindings])],
              [], [enable_python=yes])

AM_CONDITIONAL([USE_PYTHON], [test "x${enable_python:-yes}" = xyes])

if test "x${enable_python:-yes}" = xyes; then
  AC_MSG_NOTICE([You may configure with --disable-python ]dnl
[if you do not need Python bindings.])
  adl_CHECK_PYTHON
fi

AC_PROG_CC
gl_EARLY

AM_PROG_CC_C_O
AC_PROG_CXX

AM_PROG_LEX
AC_PROG_YACC

gl_INIT

AC_LANG(C++)

AC_HEADER_UNORDERED_MAP
AC_HEADER_TR1_UNORDERED_MAP
AC_HEADER_EXT_HASH_MAP

AX_CHECK_BUDDY
AX_CHECK_LBTT
AX_CHECK_GSPNLIB
AX_CHECK_BOOST([1.34], [103400])

AC_CHECK_HEADERS([sys/times.h])
AC_CHECK_FUNCS([times srand48 drand48 kill alarm])

LT_CONFIG_LTDL_DIR([ltdl])
LT_INIT([win32-dll])
LTDL_INIT([subproject convenience])

adl_ENABLE_DEBUG
ad_GCC_OPTIM
adl_NDEBUG

spot_INTEL
if test x$enable_warnings = xyes; then
  CF_GXX_WARNINGS
fi

AM_CONDITIONAL([NEVER], [false])
# We need the absolute path for dot in the "doc/dot" script. Other place
# only require a relative path.
AC_PATH_PROG([DOT], [dot])
AC_CHECK_PROG([LBT], [lbt], [lbt])
AC_CHECK_PROG([LTL2BA], [ltl2ba], [ltl2ba])
AC_CHECK_PROG([LTL3BA], [ltl3ba], [ltl3ba])
AC_CHECK_PROG([MODELLA], [modella], [modella])
AC_CHECK_PROG([LTL2NBA], [script4lbtt.py], [script4lbtt.py])
AC_CHECK_PROG([PERL], [perl], [perl])
AC_CHECK_PROG([SPIN], [spin], [spin])
AX_CHECK_VALGRIND
AC_CHECK_PROG([WRING2LBTT], [wring2lbtt], [wring2lbtt])
# Debian has a binary for SWIG 2.0 named swig2.0 and they kept swig as
# an alias for swig-1.3.  Let's use the former when available.
AC_CHECK_PROGS([SWIG], [swig2.0 swig], [swig])

AC_SUBST([CROSS_COMPILING], [$cross_compiling])

AC_CONFIG_FILES([
  Makefile
  bench/Makefile
  bench/emptchk/Makefile
  bench/emptchk/defs
  bench/ltlcounter/Makefile
  bench/ltlcounter/defs
  bench/ltlclasses/Makefile
  bench/ltlclasses/defs
  bench/ltl2tgba/Makefile
  bench/ltl2tgba/defs
  bench/scc-stats/Makefile
  bench/split-product/Makefile
  bench/wdba/Makefile
  bench/wdba/defs
  doc/Doxyfile
  doc/Makefile
  doc/tl/Makefile
  iface/dve2/defs
  iface/dve2/Makefile
  iface/gspn/defs
  iface/gspn/Makefile
  iface/Makefile
  lib/Makefile
  src/bin/Makefile
  src/bin/man/Makefile
  src/eltlparse/Makefile
  src/eltltest/defs
  src/eltltest/Makefile
  src/evtgbaalgos/Makefile
  src/evtgba/Makefile
  src/evtgbaparse/Makefile
  src/evtgbatest/defs
  src/evtgbatest/Makefile
  src/kripke/Makefile
  src/ltlast/Makefile
  src/ltlenv/Makefile
  src/ltlparse/Makefile
  src/kripkeparse/Makefile
  src/kripketest/Makefile
  src/kripketest/defs
  src/ltltest/defs
  src/ltltest/Makefile
  src/ltlvisit/Makefile
  src/Makefile
  src/misc/Makefile
  src/neverparse/Makefile
  src/sanity/Makefile
  src/saba/Makefile
  src/sabaalgos/Makefile
  src/sabatest/defs
  src/sabatest/Makefile
  src/tgbaalgos/gtec/Makefile
  src/tgbaalgos/Makefile
  src/tgba/Makefile
  src/taalgos/Makefile
  src/ta/Makefile
  src/tgbaparse/Makefile
  src/tgbatest/defs
  src/tgbatest/Makefile
  wrap/Makefile
  wrap/python/Makefile
  wrap/python/ajax/Makefile
  wrap/python/tests/Makefile
  tools/x-to-1
])
AC_CONFIG_FILES([bench/ltl2tgba/ltl2baw.pl:bench/ltl2tgba/ltl2baw.in],
		[chmod +x bench/ltl2tgba/ltl2baw.pl])
AC_CONFIG_FILES([doc/dot], [chmod +x doc/dot])
AC_CONFIG_FILES([wrap/python/tests/run], [chmod +x wrap/python/tests/run])
AC_OUTPUT
