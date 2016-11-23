@setlocal

REM set up classpath
set CLASSPATH=../../lib/ant.jar;../../lib/optional.jar;../../lib/junit.jar;../../lib/crimson.jar;%JAVA_HOME%/lib/tools.jar

REM call ant
java org.apache.tools.ant.Main %*

rem msdev quickfix_jni.dsw /MAKE "quickfix_jni - Win32 Debug"
rem msdev quickfix_jni.dsw /MAKE "quickfix_jni - Win32 Release"

@endlocal