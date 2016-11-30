call makedist.bat
xcopy dist\vc8.*.nsi . /Y
del dist\*.nsi
xcopy vc8.*.nsi dist /Y
del vc8*.nsi
pause
