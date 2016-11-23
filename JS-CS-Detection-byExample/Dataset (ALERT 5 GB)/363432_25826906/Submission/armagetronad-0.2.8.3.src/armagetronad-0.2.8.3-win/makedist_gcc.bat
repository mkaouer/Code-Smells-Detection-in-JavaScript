call makedist.bat
xcopy dist\gcc.*.nsi . /Y
del dist\*.nsi
xcopy gcc.*.nsi dist /Y
del gcc*.nsi
pause
