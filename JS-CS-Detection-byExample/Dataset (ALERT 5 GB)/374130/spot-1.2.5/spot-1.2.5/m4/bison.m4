AC_DEFUN([adl_CHECK_BISON],
[AC_ARG_VAR([BISON], [Bison parser generator])
AC_CHECK_PROGS([BISON], [bison])
if test -n "$BISON"; then
   # Bison 3.0 has warning about issues that cannot be fixed in a
   # compatible way with Bison 2.7.  Since we want to be compatible
   # with both version AND use -Werror, disable those warnings.
   # (Unfortunately -Wno-error=empty-rule,no-error=deprecated does not
   # work: https://lists.gnu.org/archive/html/bug-bison/2013-09/index.html)
   opt='-Wno-empty-rule -Wno-deprecated -Wno-precedence'
   if AM_RUN_LOG([$BISON $opt --version]); then
      BISON_EXTRA_FLAGS=$opt
   fi
fi
AC_SUBST([BISON_EXTRA_FLAGS])])
