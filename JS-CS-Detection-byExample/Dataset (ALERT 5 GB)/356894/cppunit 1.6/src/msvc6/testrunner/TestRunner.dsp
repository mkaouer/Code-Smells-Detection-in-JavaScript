# Microsoft Developer Studio Project File - Name="TestRunner" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=TestRunner - Win32 Debug Crossplatform Setting
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "TestRunner.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "TestRunner.mak" CFG="TestRunner - Win32 Debug Crossplatform Setting"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "TestRunner - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "TestRunner - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "TestRunner - Win32 Debug Crossplatform Setting" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "TestRunner - Win32 Release"

# PROP BASE Use_MFC 6
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 6
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /O2 /I "..\..\..\include" /I "..\..\..\include\msvc6" /D "NDEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_AFXEXT" /D "WIN32" /Yu"stdafx.h" /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /o "NUL" /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /o "NUL" /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG" /d "_AFXDLL"
# ADD RSC /l 0x409 /d "NDEBUG" /d "_AFXDLL"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 /nologo /subsystem:windows /dll /machine:I386
# ADD LINK32 ..\..\..\lib\cppunit.lib winmm.lib /nologo /subsystem:windows /dll /machine:I386 /def:".\TestRunner.def" /out:"..\..\..\lib\testrunner.dll" /implib:"..\..\..\lib\testrunner.lib"
# SUBTRACT LINK32 /pdb:none

!ELSEIF  "$(CFG)" == "TestRunner - Win32 Debug"

# PROP BASE Use_MFC 6
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 6
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Gm /GX /Zi /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "..\..\..\include\msvc6" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_AFXEXT" /D "WIN32" /FR /Yu"stdafx.h" /FD /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /o "NUL" /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /o "NUL" /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG" /d "_AFXDLL"
# ADD RSC /l 0x409 /d "_DEBUG" /d "_AFXDLL"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 /nologo /subsystem:windows /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 ..\..\..\lib\cppunitd.lib winmm.lib /nologo /subsystem:windows /dll /debug /machine:I386 /out:"..\..\..\lib\testrunnerd.dll" /implib:"..\..\..\lib\testrunnerd.lib" /pdbtype:sept
# SUBTRACT LINK32 /profile /pdb:none /map

!ELSEIF  "$(CFG)" == "TestRunner - Win32 Debug Crossplatform Setting"

# PROP BASE Use_MFC 6
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "TestRunner___Win32_Debug_Without_CPPUNIT_USE_TYPEINFO"
# PROP BASE Intermediate_Dir "TestRunner___Win32_Debug_Without_CPPUNIT_USE_TYPEINFO"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 6
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "DebugNoTypeInfo"
# PROP Intermediate_Dir "DebugNoTypeInfo"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "..\..\..\include\msvc6" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_AFXEXT" /D "CPPUNIT_USE_TYPEINFO" /D "WIN32" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "..\..\..\include\msvc6" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_AFXEXT" /D "WIN32" /D "CPPUNIT_DONT_USE_TYPEINFO" /Yu"stdafx.h" /FD /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /o "NUL" /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /o "NUL" /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG" /d "_AFXDLL"
# ADD RSC /l 0x409 /d "_DEBUG" /d "_AFXDLL"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 ..\..\..\lib\cppunitd.lib winmm.lib /nologo /subsystem:windows /dll /debug /machine:I386 /out:"..\..\..\lib\testrunnerd.dll" /implib:"..\..\..\lib\testrunnerd.lib" /pdbtype:sept
# SUBTRACT BASE LINK32 /pdb:none
# ADD LINK32 ..\..\..\lib\cppunitcd.lib winmm.lib /nologo /subsystem:windows /dll /pdb:"DebugCrossplatform/testrunnercd.pdb" /debug /machine:I386 /def:".\TestRunner.def" /out:"..\..\..\lib\testrunnercd.dll" /implib:"..\..\..\lib\testrunnercd.lib" /pdbtype:sept
# SUBTRACT LINK32 /pdb:none

!ENDIF 

# Begin Target

# Name "TestRunner - Win32 Release"
# Name "TestRunner - Win32 Debug"
# Name "TestRunner - Win32 Debug Crossplatform Setting"
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;cnt;rtf;gif;jpg;jpeg;jpe"
# Begin Source File

SOURCE=.\res\test_type.bmp
# End Source File
# Begin Source File

SOURCE=.\res\TestRunner.rc2
# End Source File
# Begin Source File

SOURCE=.\res\tfwkui_r.bmp
# End Source File
# End Group
# Begin Group "UserInterface"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\ListCtrlFormatter.cpp
# End Source File
# Begin Source File

SOURCE=.\ListCtrlFormatter.h
# End Source File
# Begin Source File

SOURCE=.\ListCtrlSetter.cpp
# End Source File
# Begin Source File

SOURCE=.\ListCtrlSetter.h
# End Source File
# Begin Source File

SOURCE=.\MsDevCallerListCtrl.cpp
# End Source File
# Begin Source File

SOURCE=.\MsDevCallerListCtrl.h
# End Source File
# Begin Source File

SOURCE=.\ProgressBar.cpp
# End Source File
# Begin Source File

SOURCE=.\ProgressBar.h
# End Source File
# Begin Source File

SOURCE=.\Resource.h
# End Source File
# Begin Source File

SOURCE=.\StdAfx.cpp
# ADD CPP /Yc"stdafx.h"
# End Source File
# Begin Source File

SOURCE=.\StdAfx.h
# End Source File
# Begin Source File

SOURCE=.\TestRunner.def

!IF  "$(CFG)" == "TestRunner - Win32 Release"

# PROP Exclude_From_Build 1

!ELSEIF  "$(CFG)" == "TestRunner - Win32 Debug"

!ELSEIF  "$(CFG)" == "TestRunner - Win32 Debug Crossplatform Setting"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TestRunner.rc
# End Source File
# Begin Source File

SOURCE=.\TestRunnerApp.cpp
# End Source File
# Begin Source File

SOURCE=.\TestRunnerApp.h
# End Source File
# Begin Source File

SOURCE=.\TestRunnerDlg.cpp
# End Source File
# Begin Source File

SOURCE=.\TestRunnerDlg.h
# End Source File
# Begin Source File

SOURCE=.\TreeHierarchyDlg.cpp
# End Source File
# Begin Source File

SOURCE=.\TreeHierarchyDlg.h
# End Source File
# End Group
# Begin Group "Components"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\ActiveTest.cpp
# End Source File
# Begin Source File

SOURCE=.\ActiveTest.h
# End Source File
# Begin Source File

SOURCE=.\GUITestResult.cpp
# End Source File
# Begin Source File

SOURCE=.\GUITestResult.h
# End Source File
# Begin Source File

SOURCE=.\TestRunner.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\include\msvc6\testrunner\TestRunner.h
# End Source File
# Begin Source File

SOURCE=.\TestRunnerModel.cpp
# End Source File
# Begin Source File

SOURCE=.\TestRunnerModel.h
# End Source File
# End Group
# Begin Group "NewFiles"

# PROP Default_Filter "*.cpp;*.h"
# Begin Source File

SOURCE=.\MostRecentTests.cpp
# End Source File
# Begin Source File

SOURCE=.\MostRecentTests.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\msvc6\DSPlugIn\TestRunnerDSPlugin_i.c
# SUBTRACT CPP /YX /Yc /Yu
# End Source File
# End Group
# Begin Source File

SOURCE=.\ReadMe.txt
# End Source File
# End Target
# End Project
