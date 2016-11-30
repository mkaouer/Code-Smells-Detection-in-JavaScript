msdev quickfix.dsw /MAKE "at_client - Win32 Debug"
msdev quickfix.dsw /MAKE "at_client - Win32 Release"
msdev quickfix.dsw /MAKE "at_server - Win32 Debug"
msdev quickfix.dsw /MAKE "at_server - Win32 Release"
msdev quickfix.dsw /MAKE "atrun - Win32 Debug"
msdev quickfix.dsw /MAKE "atrun - Win32 Release"
msdev quickfix.dsw /MAKE "pt - Win32 Debug"
msdev quickfix.dsw /MAKE "pt - Win32 Release"
msdev quickfix.dsw /MAKE "quickfix_lib - Win32 Debug"
msdev quickfix.dsw /MAKE "quickfix_lib - Win32 Release"
msdev quickfix.dsw /MAKE "ut - Win32 Debug"
msdev quickfix.dsw /MAKE "ut - Win32 Release"

pushd examples
msdev examples.dsw /MAKE "ordermatch - Win32 Debug"
msdev examples.dsw /MAKE "ordermatch - Win32 Release"
msdev examples.dsw /MAKE "ordermatch_ut - Win32 Debug"
msdev examples.dsw /MAKE "ordermatch_ut - Win32 Release"
msdev examples.dsw /MAKE "tradeclient - Win32 Debug"
msdev examples.dsw /MAKE "tradeclient - Win32 Release"
popd

pushd src\java
call build.bat
popd

pushd examples\tradeclientgui\banzai
call build.bat
popd
