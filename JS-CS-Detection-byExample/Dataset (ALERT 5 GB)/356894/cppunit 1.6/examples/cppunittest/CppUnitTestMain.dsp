# Microsoft Developer Studio Project File - Name="CppUnitTestMain" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=CppUnitTestMain - Win32 Debug Crossplatform Setting
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "CppUnitTestMain.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "CppUnitTestMain.mak" CFG="CppUnitTestMain - Win32 Debug Crossplatform Setting"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "CppUnitTestMain - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "CppUnitTestMain - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE "CppUnitTestMain - Win32 Debug Crossplatform Setting" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "CppUnitTestMain - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /O2 /I "../../include" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /D "CPPUNIT_USE_TYPEINFO" /FD /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE RSC /l 0x40c /d "NDEBUG"
# ADD RSC /l 0x40c /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ../../lib/cppunit.lib /nologo /subsystem:console /machine:I386

!ELSEIF  "$(CFG)" == "CppUnitTestMain - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "../../include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /FD /GZ /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE RSC /l 0x40c /d "_DEBUG"
# ADD RSC /l 0x40c /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ../../lib/cppunitd.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ELSEIF  "$(CFG)" == "CppUnitTestMain - Win32 Debug Crossplatform Setting"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "CppUnitTestMain___Win32_Debug_Crossplatform_Setting"
# PROP BASE Intermediate_Dir "CppUnitTestMain___Win32_Debug_Crossplatform_Setting"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "DebugCrossplatform"
# PROP Intermediate_Dir "DebugCrossplatform"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "../../include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /FD /GZ /c
# SUBTRACT BASE CPP /YX /Yc /Yu
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "../../include" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /D "CPPUNIT_DONT_USE_TYPEINFO" /FD /GZ /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE RSC /l 0x40c /d "_DEBUG"
# ADD RSC /l 0x40c /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ../../lib/cppunitd.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib ../../lib/cppunitcd.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "CppUnitTestMain - Win32 Release"
# Name "CppUnitTestMain - Win32 Debug"
# Name "CppUnitTestMain - Win32 Debug Crossplatform Setting"
# Begin Source File

SOURCE=.\BaseTestCase.cpp
# End Source File
# Begin Source File

SOURCE=.\BaseTestCase.h
# End Source File
# Begin Source File

SOURCE=.\CppUnitTestMain.cpp
# End Source File
# Begin Source File

SOURCE=.\ExceptionTest.cpp
# End Source File
# Begin Source File

SOURCE=.\ExceptionTest.h
# End Source File
# Begin Source File

SOURCE=.\FailingTestCase.cpp
# End Source File
# Begin Source File

SOURCE=.\FailingTestCase.h
# End Source File
# Begin Source File

SOURCE=.\FailureException.h
# End Source File
# Begin Source File

SOURCE=.\HelperMacrosTest.cpp
# End Source File
# Begin Source File

SOURCE=.\HelperMacrosTest.h
# End Source File
# Begin Source File

SOURCE=.\MockTestListener.cpp
# End Source File
# Begin Source File

SOURCE=.\MockTestListener.h
# End Source File
# Begin Source File

SOURCE=.\NotEqualExceptionTest.cpp
# End Source File
# Begin Source File

SOURCE=.\NotEqualExceptionTest.h
# End Source File
# Begin Source File

SOURCE=.\OrthodoxTest.cpp
# End Source File
# Begin Source File

SOURCE=.\OrthodoxTest.h
# End Source File
# Begin Source File

SOURCE=.\RepeatedTestTest.cpp
# End Source File
# Begin Source File

SOURCE=.\RepeatedTestTest.h
# End Source File
# Begin Source File

SOURCE=.\SubclassedTestCase.cpp
# End Source File
# Begin Source File

SOURCE=.\SubclassedTestCase.h
# End Source File
# Begin Source File

SOURCE=.\SynchronizedTestResult.h
# End Source File
# Begin Source File

SOURCE=.\TestAssertTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestAssertTest.h
# End Source File
# Begin Source File

SOURCE=.\TestCallerTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestCallerTest.h
# End Source File
# Begin Source File

SOURCE=.\TestCaseTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestCaseTest.h
# End Source File
# Begin Source File

SOURCE=.\TestDecoratorTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestDecoratorTest.h
# End Source File
# Begin Source File

SOURCE=.\TestFailureTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestFailureTest.h
# End Source File
# Begin Source File

SOURCE=.\TestListenerTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestListenerTest.h
# End Source File
# Begin Source File

SOURCE=.\TestResultTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestResultTest.h
# End Source File
# Begin Source File

SOURCE=.\TestSetupTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestSetupTest.h
# End Source File
# Begin Source File

SOURCE=.\TestSuiteTest.cpp
# End Source File
# Begin Source File

SOURCE=.\TestSuiteTest.h
# End Source File
# Begin Source File

SOURCE=.\TrackedTestCase.cpp
# End Source File
# Begin Source File

SOURCE=.\TrackedTestCase.h
# End Source File
# End Target
# End Project
