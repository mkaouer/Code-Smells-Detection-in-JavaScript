AC_DEFUN([AX_CHECK_LBTT], [
  AC_ARG_WITH([included-lbtt],
	      [AC_HELP_STRING([--with-included-lbtt],
			      [use the LBTT program inclued here])])
  AS_IF([AM_RUN_LOG([lbtt-translate --help | grep spot])],
	[need_included_lbtt=no],
	[need_included_lbtt=yes])

  if test "$need_included_lbtt" = yes; then
     if test "$with_included_lbtt" = no; then
       AC_MSG_ERROR([Cannot find lbtt.  Please install lbtt first,
		     or configure with --with-included-lbtt])
     else
	with_included_lbtt=yes
     fi
  fi

  if test "$with_included_lbtt" = yes;  then
     LBTT='${top_builddir}/lbtt/src/lbtt'
     LBTT_TRANSLATE='${top_builddir}/lbtt/src/lbtt-translate'
  else
     LBTT=lbtt
     LBTT_TRANSLATE=lbtt-translate
  fi
  # We always configure lbtt, this is needed to ensure
  # it gets distributed properly.  Whether with_included_buddy is
  # set or not affects whether we *use* or *build* lbtt.
  AC_CONFIG_SUBDIRS([lbtt])

  AM_CONDITIONAL([WITH_INCLUDED_LBTT], [test "$with_included_lbtt" = yes])
  AC_SUBST([LBTT])
  AC_SUBST([LBTT_TRANSLATE])
])
