AC_DEFUN([AX_CHECK_LBTT], [
  AC_ARG_WITH([included-lbtt],
	      [AC_HELP_STRING([--with-included-lbtt],
			      [use the LBTT program inclued here])])
  AS_IF([AM_RUN_LOG([lbtt-translate --version | grep 1.2.1a])],
	[need_included_lbtt=no],
	[need_included_lbtt=yes])

  if test "$need_included_lbtt" = yes; then
     if test "$with_included_lbtt" = no; then
       AC_MSG_WARN([Cannot find lbtt, needed for test-suite and benchmarks.
Please install lbtt first, or configure with --with-included-lbtt.])
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

  # We always configure lbtt, even if it is not built to ensure it
  # gets distributed properly.  However, when someone uses
  # --without-included-lbtt explicitely, we assume he might be trying
  # to build Spot on a system where lbtt cannot build (e.g. MinGW) and
  # where lbtt/configure will fail.  So we don't run the sub-configure
  # only in this case.  On such a setup, "make distcheck" will break,
  # but so probably isn't important.
  if test "$with_included_lbtt" != no; then
    AC_CONFIG_SUBDIRS([lbtt])
  fi

  AM_CONDITIONAL([WITH_INCLUDED_LBTT], [test "$with_included_lbtt" = yes])
  AC_SUBST([LBTT])
  AC_SUBST([LBTT_TRANSLATE])
])
