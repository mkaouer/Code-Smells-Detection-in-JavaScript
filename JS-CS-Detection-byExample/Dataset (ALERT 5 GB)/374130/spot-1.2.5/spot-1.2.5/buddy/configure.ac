AC_PREREQ([2.57])
AC_INIT([buddy], [2.3a])
AC_CONFIG_AUX_DIR([tools])
AC_CONFIG_MACRO_DIR([m4])
AM_INIT_AUTOMAKE([foreign nostdinc no-define 1.7.3])

# If the user didn't supply a CFLAGS value,
# set an empty one to prevent autoconf to stick -O2 -g here.
test -z "$CFLAGS" && CFLAGS=
test -z "$CXXFLAGS" && CXXFLAGS=

adl_ENABLE_DEVEL

AC_PROG_CC

AM_PROG_LEX
AC_PROG_YACC

AC_PROG_CXX

LT_INIT([win32-dll])

# Use -Werror since using -fvisibility under MinGW is only a warning.
# (The option is ignored anyway since this does not make sense under windows).
AX_CHECK_COMPILE_FLAG([-Werror -fvisibility=hidden],
  [CFLAGS="$CFLAGS -fvisibility=hidden"])
AC_LANG(C++)
AX_CHECK_COMPILE_FLAG([-Werror -fvisibility=hidden],
  [CXXFLAGS="$CXXFLAGS -fvisibility=hidden"
  AX_CHECK_COMPILE_FLAG([-fvisibility-inlines-hidden],
    [CXXFLAGS="$CXXFLAGS -fvisibility-inlines-hidden"])])
AC_LANG(C)


adl_ENABLE_DEBUG
ad_GCC_OPTIM
adl_NDEBUG

buddy_INTEL
buddy_DEBUG_FLAGS

if test x$enable_warnings = xyes; then
  CF_GCC_WARNINGS
fi


AC_CONFIG_HEADERS([config.h])

AC_CONFIG_FILES([
  Makefile
  src/Makefile
  doc/Makefile
  examples/Makefile
  examples/adder/Makefile
  examples/bddcalc/Makefile
  examples/bddtest/Makefile
  examples/cmilner/Makefile
  examples/fdd/Makefile
  examples/milner/Makefile
  examples/money/Makefile
  examples/queen/Makefile
  examples/solitare/Makefile
])

AC_OUTPUT
