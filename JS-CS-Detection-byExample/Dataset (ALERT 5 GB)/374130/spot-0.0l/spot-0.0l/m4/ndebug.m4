AC_DEFUN([adl_NDEBUG],
 [AC_ARG_ENABLE([assert],
  [AC_HELP_STRING([--enable-assert],[turn on assertions])])
  if test "$enable_assert" != yes; then
    CFLAGS="$CFLAGS -DNDEBUG"
  fi])
