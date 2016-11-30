call makedist.bat
xcopy dist\vc6.*.nsi . /Y
del dist\*.nsi
xcopy vc6.*.nsi dist /Y
del vc6*.nsi
pause
