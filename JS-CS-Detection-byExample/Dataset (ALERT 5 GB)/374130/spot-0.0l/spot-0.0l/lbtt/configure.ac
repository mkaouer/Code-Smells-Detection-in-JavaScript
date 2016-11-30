# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.53])
AC_INIT([lbtt], [1.0.2], [heikki.tauriainen@hut.fi])
AC_REVISION([Revision: 1.2])
AC_CONFIG_SRCDIR([src/main.cc])
AM_INIT_AUTOMAKE
AM_CONFIG_HEADER([config.h])



# Checks for programs.

AC_PROG_CC
AC_PROG_CPP
AC_PROG_CXX
AC_PROG_CXXCPP
AM_PROG_LEX
AC_PROG_YACC

YACC="${YACC} -d"



# Check whether the user has explicitly disabled the test for the availability
# of the GNU readline library.

readline=yes

AC_ARG_WITH([readline],
            [AC_HELP_STRING([--without-readline],
                            [disable check for the GNU readline library])],
            [if test x"${withval}" = xno; then readline=no; fi])



# Check for the availability of headers.

AC_HEADER_STDC
AC_CHECK_HEADERS([fcntl.h obstack.h stdlib.h unistd.h])

# Check for the availability of the GNU readline headers.

if test "${readline}" = yes; then
  rl_history_h="readline/history.h"
  rl_readline_h="readline/readline.h"
  AC_CHECK_HEADERS([${rl_history_h} ${rl_readline_h}],
                   [],
                   [readline=no])
fi

AC_LANG([C++])

# Check for the availablility of the sstream or strstream header.

AC_CHECK_HEADERS([sstream],
                 [],
                 [AC_CHECK_HEADERS([strstream],
                                   [],
                                   [AC_MSG_ERROR([missing one or more standard C++ headers])])])



# Checks for typedefs, structures, and compiler characteristics.

# Check for the availability of the slist header (an extension to the C++
# Standard Template Library). (In GCC 3.x the header is in the ext/
# subdirectory of the directory containing the standard C++ headers.)

AC_MSG_CHECKING([for slist])
for slist_header in slist ext/slist no; do
  if test "${slist_header}" != no; then
    AC_TRY_CPP([#include <${slist_header}>], [break])
  fi
done

# Try to determine the C++ namespace in which the class slist resides.
# (For example, GCC versions 3.1, 3.1.1 and 3.2 put slist into the
# __gnu_cxx namespace.)

if test "${slist_header}" != no; then
  for slist_namespace in std __gnu_cxx error; do
    if test "${slist_namespace}" != error; then
      AC_TRY_LINK([#include <${slist_header}>],
                  [${slist_namespace}::slist<int> s;],
                  [break])
    fi
  done  
  if test "${slist_namespace}" != error; then
    AC_MSG_RESULT([header <${slist_header}>, typename ${slist_namespace}::slist])
    AC_DEFINE([HAVE_SLIST],
              [1],
              [Define to 1 if you have the <slist> or <ext/slist> header file.])
    AC_DEFINE_UNQUOTED([SLIST_NAMESPACE],
                       [${slist_namespace}],
                       [Define as the name of the C++ namespace containing slist.])
    AC_SUBST([INCLUDE_SLIST_HEADER], ["#include <${slist_header}>"])
  else
    slist_header=no
  fi
fi

if test "${slist_header}" = no; then
  AC_MSG_RESULT([no])

# Check for the availability of the single_client_alloc memory allocator
# available in some compilers supporting the SGI extensions to the Standard
# template library (for example, pre-3.0 versions of gcc). This check is not
# needed if no suitable slist header was found above, since in that case the
# compiler does not support the SGI extensions.

else
  AC_MSG_CHECKING([for single_client_alloc])
  AC_TRY_LINK([#include <${slist_header}>],
              [using namespace std;
               ${slist_namespace}::slist<int, single_client_alloc> s;],
              [AC_MSG_RESULT([yes])
               AC_DEFINE([HAVE_SINGLE_CLIENT_ALLOC],
                         [1],
                         [Define if your C++ compiler supports the single_client_allocator memory allocator.])],
              [AC_MSG_RESULT([no])])
fi

AC_LANG(C)

AC_C_CONST
AC_C_INLINE



# Checks for library functions.

AC_TYPE_SIGNAL
AC_CHECK_FUNCS([mkdir strchr strtod strtol strtoul getopt_long])
if test x"${ac_cv_func_getopt_long}" = xno; then
  AC_LIBOBJ([getopt])
  AC_LIBOBJ([getopt1])
  AC_CHECK_HEADERS([libintl.h string.h strings.h])
  AC_CHECK_FUNCS([memset])
fi

# Determine which libraries to link with for readline support.

if test "${readline}" = yes; then
  AC_MSG_CHECKING([for readline libraries])
  oldlibs=${LIBS}
  for READLINELIBS in "-lreadline" "-lreadline -lcurses" "-lreadline -ltermcap" error; do
    if test "${READLINELIBS}" != error; then
      LIBS="${oldlibs} ${READLINELIBS}"
      AC_TRY_LINK([#include <${rl_history_h}>
                   #include <${rl_readline_h}>],
                  [using_history(); readline(""); add_history("");],
                  [break])
    fi
  done
  LIBS=${oldlibs}
  if test "${READLINELIBS}" != error; then
    AC_DEFINE([HAVE_READLINE],
              [1],
              [Define if you have the GNU readline library.])
    AC_SUBST([READLINELIBS])
    AC_MSG_RESULT([${READLINELIBS}])
  else
    AC_MSG_RESULT([no suitable libraries found, readline support disabled])
    READLINELIBS=""
  fi
fi

# Check for the availability of the `rand48' family of random number
# generation functions.

have_rand48=yes

AC_CHECK_FUNCS([srand48 lrand48 seed48],
               [],
               [have_rand48=no
                AC_CHECK_FUNCS([rand srand],
                               [],
                               [AC_MSG_ERROR([missing library functions for random number generation])])])

if test "${have_rand48}" = yes; then
  AC_DEFINE([HAVE_RAND48],
            [1],
            [Define if you have the `rand48' family of random number generation functions.])
fi



AC_CONFIG_FILES([Makefile doc/Makefile src/Makefile src/Graph.h])
AC_OUTPUT
