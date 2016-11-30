AC_DEFUN([AX_CHECK_GSPNLIB], [
  AC_ARG_WITH([gspn],
	      [AC_HELP_STRING([--with-gpsn=/root/of/greatspn],
			      [build interface with GreadSPN])])
  if test x${with_gspn-no} != xno; then
    ax_tmp_LDFLAGS=$LDFLAGS
    ax_tmp_LIBS=$LIBS
    LIBGSPN_LDFLAGS=
    if test x${with_gspn-yes} != xyes; then
       # Try to locate the headers and libraries.
       gspn_version_sh=$with_gspn/SOURCES/contrib/version.sh;
       AC_CHECK_FILE($gspn_version_sh,,
	 [AC_MSG_ERROR(
	    [Cannot find $gspn_version_sh.  Check --with-gspn's argument.])])
       gspn_version=`$gspn_version_sh`
       LIBGSPN_LDFLAGS="-L$with_gspn/$gspn_version/2bin/lib"
       LIBGSPN_CPPFLAGS="-I$with_gspn/SOURCES/WN/INCLUDE"
    fi
     LDFLAGS="$LDFLAGS $LIBGSPN_LDFLAGS"
     AC_CHECK_LIB([gspnRG], [initialize], [],
	[AC_MSG_ERROR([Cannot find libgspnRG.  Check --with-gspn's argument.])],        [-lm -lfl])
     LIBGSPNRG_LDFLAGS="$LIBGSPN_LDFLAGS -lgspnRG -lm -lfl"

     LDFLAGS="$LDFLAGS $LIBGSPN_LDFLAGS"
     AC_CHECK_LIB([gspnSRG], [initialize], [],
	[AC_MSG_ERROR([Cannot find libgspnSRG.  Check --with-gspn's argument.])],        [-lm -lfl])
     LIBGSPNSRG_LDFLAGS="$LIBGSPN_LDFLAGS -lgspnSRG -lm -lfl"

     LDFLAGS="$LDFLAGS $LIBGSPN_LDFLAGS"
     AC_CHECK_LIB([gspnESRG], [initialize], [],
	[AC_MSG_ERROR([Cannot find libgspnESRG.  Check --with-gspn's argument.])],        [-lm -lfl])
     LIBGSPNESRG_LDFLAGS="$LIBGSPN_LDFLAGS -lgspnESRG -lm -lfl"
     LDFLAGS="$ax_tmp_LDFLAGS"
     LIBS="$ax_tmp_LIBS"
  fi
  AM_CONDITIONAL([WITH_GSPN], [test x${with_gspn+set} = xset])
  AC_SUBST([LIBGSPN_CPPFLAGS])
  AC_SUBST([LIBGSPNRG_LDFLAGS])
  AC_SUBST([LIBGSPNSRG_LDFLAGS])
  AC_SUBST([LIBGSPNESRG_LDFLAGS])
])
