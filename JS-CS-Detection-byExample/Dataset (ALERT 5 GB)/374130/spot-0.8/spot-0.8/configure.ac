# Copyright (C) 2008, 2009, 2010, 2011 Laboratoire de Recherche et
# Développement de l'Epita (LRDE).
# Copyright (C) 2003, 2004, 2005, 2006, 2007 Laboratoire d'Informatique de
# Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
# Pierre et Marie Curie.
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
AC_INIT([spot], [0.8], [spot@lrde.epita.fr])
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
AX_CHECK_BOOST([1.34], [103400])

AC_CHECK_FUNCS([srand48 drand48])

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
AC_CHECK_PROG([MODELLA], [modella], [modella])
AC_CHECK_PROG([LTL2NBA], [script4lbtt.py], [script4lbtt.py])
AC_CHECK_PROG([PERL], [perl], [perl])
AC_CHECK_PROG([SPIN], [spin], [spin])
AX_CHECK_VALGRIND
AC_CHECK_PROG([WRING2LBTT], [wring2lbtt], [wring2lbtt])
# Debian has a binary for SWIG 2.0 named swig2.0 and they kept swig as
# an alias for swig-1.3.  Let's use the former when available.
AC_CHECK_PROGS([SWIG], [swig2.0 swig], [swig])

AC_CONFIG_FILES([
  Makefile
  bench/Makefile
  bench/emptchk/Makefile
  bench/emptchk/defs
  bench/gspn-ssp/Makefile
  bench/gspn-ssp/defs
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
  iface/dve2/defs
  iface/dve2/Makefile
  iface/gspn/defs
  iface/gspn/Makefile
  iface/Makefile
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
  src/tgbaparse/Makefile
  src/tgbatest/defs
  src/tgbatest/Makefile
  wrap/Makefile
  wrap/python/Makefile
  wrap/python/ajax/Makefile
  wrap/python/cgi-bin/Makefile
  wrap/python/tests/Makefile
])
AC_CONFIG_FILES([bench/ltl2tgba/ltl2baw.pl:bench/ltl2tgba/ltl2baw.in],
		[chmod +x bench/ltl2tgba/ltl2baw.pl])
AC_CONFIG_FILES([doc/dot], [chmod +x doc/dot])
AC_CONFIG_FILES([wrap/python/tests/run], [chmod +x wrap/python/tests/run])
AC_OUTPUT
