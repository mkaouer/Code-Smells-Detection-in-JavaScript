AC_DEFUN([adl_CHECK_PYTHON],
 [AM_PATH_PYTHON([2.0])
  case $PYTHON in
    [[\\/$]]* | ?:[[\\/]]* );;
    *) AC_MSG_ERROR([The PYTHON variable must be set to an absolute filename.]);;
  esac
  AC_CACHE_CHECK([for $am_display_PYTHON includes directory],
    [adl_cv_python_inc],
    [adl_cv_python_inc=`$PYTHON -c "import sys; from distutils import sysconfig;]
[sys.stdout.write(sysconfig.get_python_inc())" 2>/dev/null`])
  AC_SUBST([PYTHONINC], [$adl_cv_python_inc])])
