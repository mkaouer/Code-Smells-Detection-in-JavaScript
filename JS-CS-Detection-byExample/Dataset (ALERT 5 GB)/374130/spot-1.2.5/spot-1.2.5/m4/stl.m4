
AC_DEFUN([AC_HEADER_EXT_HASH_MAP], [
  AC_CACHE_CHECK([for ext/hash_map],
  [ac_cv_cxx_ext_hash_map],
  [AC_LANG_SAVE
  AC_LANG_CPLUSPLUS
  ac_save_CXXFLAGS="$CXXFLAGS"
  CXXFLAGS="$CXXFLAGS -Werror"
  AC_TRY_COMPILE([#include <ext/hash_map>], [using __gnu_cxx::hash_map;],
  [ac_cv_cxx_ext_hash_map=yes], [ac_cv_cxx_ext_hash_map=no])
  CXXFLAGS="$ac_save_CXXFLAGS"
  AC_LANG_RESTORE
  ])
  if test "$ac_cv_cxx_ext_hash_map" = yes; then
    AC_DEFINE([HAVE_EXT_HASH_MAP],, [Define if ext/hash_map is present.])
  fi
])

AC_DEFUN([AC_HEADER_TR1_UNORDERED_MAP], [
  AC_CACHE_CHECK([for tr1/unordered_map],
  [ac_cv_cxx_tr1_unordered_map],
  [AC_LANG_SAVE
  AC_LANG_CPLUSPLUS
  ac_save_CXXFLAGS="$CXXFLAGS"
  CXXFLAGS="$CXXFLAGS -Werror"
# GCC 4.0.0 has tr1/unordered_map, but it fails to compile the following code
  AC_TRY_COMPILE([#include <tr1/unordered_map>],
                 [using std::tr1::unordered_map;
                  const unordered_map<int, int> t;
                  return t.find(42) == t.end();],
  [ac_cv_cxx_tr1_unordered_map=yes], [ac_cv_cxx_tr1_unordered_map=no])
  CXXFLAGS="$ac_save_CXXFLAGS"
  AC_LANG_RESTORE
  ])
  if test "$ac_cv_cxx_tr1_unordered_map" = yes; then
    AC_DEFINE([HAVE_TR1_UNORDERED_MAP],, [Define if tr1/unordered_map is present.])
  fi
])

AC_DEFUN([AC_HEADER_UNORDERED_MAP], [
  AC_CACHE_CHECK([for unordered_map],
  [ac_cv_cxx_unordered_map],
  [AC_LANG_SAVE
  AC_LANG_CPLUSPLUS
  ac_save_CXXFLAGS="$CXXFLAGS"
  CXXFLAGS="$CXXFLAGS -Werror"
  AC_TRY_COMPILE([#include <unordered_map>], [using std::unordered_map;],
  [ac_cv_cxx_unordered_map=yes], [ac_cv_cxx_unordered_map=no])
  CXXFLAGS="$ac_save_CXXFLAGS"
  AC_LANG_RESTORE
  ])
  if test "$ac_cv_cxx_unordered_map" = yes; then
    AC_DEFINE([HAVE_UNORDERED_MAP],, [Define if unordered_map is present.])
  fi
])
