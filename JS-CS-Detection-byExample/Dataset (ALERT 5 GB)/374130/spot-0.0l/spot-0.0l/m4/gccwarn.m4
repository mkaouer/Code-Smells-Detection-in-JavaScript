dnl Check if the compiler supports useful warning options.  There's a few that
dnl we don't use, simply because they're too noisy:
dnl
dnl -ansi (prevents declaration of functions like strdup, and because
dnl        it makes warning in system headers).
dnl -Wconversion (useful in older versions of gcc, but not in gcc 2.7.x)
dnl -Wtraditional (combines too many unrelated messages, only a few useful)
dnl -Wredundant-decls (system headers make this too noisy)
dnl -pedantic
dnl -Wunreachable-code (broken, see GCC PR/7827)
dnl -Wredundant-decls (too many warnings in GLIBC's header with old GCC)
dnl
dnl A few other options have been left out because they are annoying in C++.


AC_DEFUN([CF_GXX_WARNINGS],
[if test "x$GXX" = xyes; then
  AC_CACHE_CHECK([for g++ warning options], ac_cv_prog_gxx_warn_flags,
  [
  cat > conftest.$ac_ext <<EOF
#line __oline__ "configure"
int main(int argc, char *argv[[]]) { return argv[[argc-1]] == 0; }
EOF
  cf_save_CXXFLAGS="$CXXFLAGS"
  ac_cv_prog_gxx_warn_flags="-W -Wall"
  for cf_opt in \
   Wcast-align \
   Wpointer-arith \
   Wwrite-strings \
   Wstrict-prototypes \
   Wcast-qual \
   Werror
  do
    CXXFLAGS="$cf_save_CXXFLAGS $ac_cv_prog_gxx_warn_flags -$cf_opt"
    if AC_TRY_EVAL(ac_compile); then
      ac_cv_prog_gxx_warn_flags="$ac_cv_prog_gxx_warn_flags -$cf_opt"
      test "$cf_opt" = Wcast-qual && ac_cv_prog_gxx_warn_flags="$ac_cv_prog_gxx_warn_flags -DXTSTRINGDEFINES"
    fi
  done
  rm -f conftest*
  CXXFLAGS="$cf_save_CXXFLAGS"])
fi
AC_SUBST([WARNING_CXXFLAGS], ["${ac_cv_prog_gxx_warn_flags}"])
])
