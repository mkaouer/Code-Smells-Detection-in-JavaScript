# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.59])
AC_INIT([lbtt], [1.2.0], [heikki.tauriainen@tkk.fi])
AC_REVISION([Revision: 1.8])
AC_CONFIG_SRCDIR([src/main.cc])
AC_CONFIG_HEADERS([config.h])
AM_INIT_AUTOMAKE



# Checks for programs.

AC_PROG_CC
AC_PROG_CPP
AC_PROG_CXX
AC_PROG_CXXCPP
AM_PROG_LEX
AC_PROG_YACC



# Check whether the user has explicitly disabled support for the GNU readline
# library.

readline=yes
readline_includedir=
readline_libdir=

AC_ARG_WITH(
  [readline],
  [AS_HELP_STRING(
     [--without-readline],
     [disable support for the GNU readline library (default enable)])],
  [if test x"${withval}" = xno; then readline=no; fi])

AC_ARG_WITH(
  [readline-prefix],
  [AS_HELP_STRING(
     [--with-readline-prefix=DIR],
     [location where GNU readline is installed (optional)])],
  [readline_includedir="${withval}/include"
   readline_libdir="${withval}/lib"])

AC_ARG_WITH(
  [readline-includes],
  [AS_HELP_STRING(
     [--with-readline-includes=DIR],
     [location where GNU readline headers are installed (optional)])],
  [readline_includedir="${withval}"])

AC_ARG_WITH(
  [readline-libs],
  [AS_HELP_STRING(
     [--with-readline-libs=DIR],
     [location where GNU readline libraries are installed (optional)])],
  [readline_libdir="${withval}"])

old_CPPFLAGS=${CPPFLAGS}
old_LDFLAGS=${LDFLAGS}

if test -n "${readline_includedir}"; then
  CPPFLAGS="${CPPFLAGS} -I${readline_includedir}"
fi
if test -n "${readline_libdir}"; then
  LDFLAGS="${LDFLAGS} -L${readline_libdir}"
fi



# Check for the availability of headers.

AC_GNU_SOURCE
AC_HEADER_STDC
AC_CHECK_HEADERS([libintl.h fcntl.h sys/times.h])
AC_HEADER_SYS_WAIT

# Check for the availability of the GNU readline headers.

if test "${readline}" = yes; then
  rl_history_h="readline/history.h"
  rl_readline_h="readline/readline.h"
  AC_CHECK_HEADERS([${rl_history_h} ${rl_readline_h}], [], [readline=no])
fi

AC_LANG([C++])

# Check for the availability of the obstack.h header. GNU libc 2.3.2 includes a
# version of this header that cannot be compiled using g++; enable a workaround
# if necessary.

AC_CHECK_HEADERS(
  [obstack.h],
  [AC_MSG_CHECKING([whether obstack.h compilation workaround is needed])
   old_cxxflags=${CXXFLAGS}
   CXXFLAGS="${CXXFLAGS} -Werror"
   AC_COMPILE_IFELSE(
     [AC_LANG_PROGRAM(
        [[#include <obstack.h>]],
        [[
          #ifdef __GLIBC__ == 2 && __GLIBC_MINOR__ = 3
          obstack_alloc(0, 0);
          #endif
        ]])],
     [AC_MSG_RESULT([no])],
     [AC_MSG_RESULT([yes])
      AC_DEFINE(
        [GLIBC_OBSTACK_WORKAROUND],
        [1],
        [Define to 1 to enable an obstack.h C++ compilation workaround for GNU libc 2.3.])])
   CXXFLAGS=${old_cxxflags}])

# Check for the availablility of the sstream or strstream header.

AC_CHECK_HEADERS(
  [sstream],
  [],
  [AC_CHECK_HEADERS(
     [strstream],
     [],
     [AC_MSG_ERROR([missing one or more standard C++ headers])])])



# Checks for typedefs, structures, and compiler characteristics.

AC_LANG(C)

AC_CHECK_TYPES(
  [unsigned long long int],
  [AC_DEFINE(
     [BIGUINT],
     [unsigned long long int],
     [Define to an unsigned integer type supported by your compiler.])],
  [AC_DEFINE(
     [BIGUINT],
     [unsigned long int],
     [Define to an unsigned integer type supported by your compiler.])])

AC_C_CONST
AC_C_INLINE



# Checks for library functions.

AC_CHECK_FUNCS(
  [strchr strtod strtol strtoul strerror mkdir mkstemp open read write close popen pclose pipe fork execvp getpgrp setpgid tcgetpgrp tcsetpgrp getpid waitpid alarm sigaction sigprocmask sigemptyset sigaddset times sysconf],
  [],
  [AC_MSG_ERROR([missing one of the library functions required for compilation])])
AC_CHECK_FUNCS([strsignal isatty getopt_long])
if test x"${ac_cv_func_getopt_long}" = xno; then
  AC_LIBOBJ([getopt])
  AC_LIBOBJ([getopt1])
  AC_CHECK_FUNCS([memset])
fi

# Determine which libraries to link with for readline support.

if test "${readline}" = yes; then
  AC_MSG_CHECKING([for readline libraries])
  oldlibs=${LIBS}
  for READLINELIBS in "-lreadline" "-lreadline -lcurses" "-lreadline -ltermcap" error; do
    if test "${READLINELIBS}" != error; then
      LIBS="${oldlibs} ${READLINELIBS}"
      AC_LINK_IFELSE(
        [AC_LANG_PROGRAM(
           [[
             #include <stdio.h>
             #include <${rl_history_h}>
             #include <${rl_readline_h}>
           ]],
           [[using_history(); readline(""); add_history("");]])],
        [break])
    fi
  done
  LIBS=${oldlibs}
  if test "${READLINELIBS}" != error; then
    AC_DEFINE(
      [HAVE_READLINE],
      [1],
      [Define if you have the GNU readline library.])
    AC_SUBST([READLINELIBS])
    AC_MSG_RESULT([${READLINELIBS}])
  else
    AC_MSG_RESULT([no suitable libraries found, readline support disabled])
    READLINELIBS=""
    readline=no
  fi
fi

if test "${readline}" = no; then
  CPPFLAGS=${old_CPPFLAGS}
  LDFLAGS=${old_LDFLAGS}
fi

# Check for the availability of the `rand48' family of random number
# generation functions.

have_rand48=yes

AC_CHECK_FUNCS(
  [srand48 lrand48 seed48],
  [],
  [have_rand48=no
   AC_CHECK_FUNCS(
     [rand srand],
     [],
     [AC_MSG_ERROR([missing library functions for random number generation])])])

if test "${have_rand48}" = yes; then
  AC_DEFINE(
    [HAVE_RAND48],
    [1],
    [Define if you have the `rand48' family of random number generation functions.])
fi



AC_CONFIG_FILES([Makefile doc/Makefile src/Makefile src/Graph.h])
AC_OUTPUT
