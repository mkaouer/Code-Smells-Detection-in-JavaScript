@echo off
set TOPDIR=%~dp0/../..
set VERSION=4.1
set PMDJAR=%TOPDIR%/java14/lib/pmd14-%VERSION%.jar
set JARPATH=%TOPDIR%/lib/asm-3.1.jar;%TOPDIR%/lib/jaxen-1.1.1.jar
set RWPATH=%TOPDIR%/java14/lib/retroweaver-rt-2.0.2.jar;%TOPDIR%/java14/lib/backport-util-concurrent.jar
set JARPATH=%JARPATH%;%RWPATH%
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.viewer.Viewer

java %OPTS% -cp "%PMDJAR%;%JARPATH%;%TOPDIR%/build" %MAIN_CLASS% %*

