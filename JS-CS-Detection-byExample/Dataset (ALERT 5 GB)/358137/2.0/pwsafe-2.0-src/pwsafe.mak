# Microsoft Developer Studio Generated NMAKE File, Based on pwsafe.dsp
!IF "$(CFG)" == ""
CFG=pwsafe - Win32 Debug
!MESSAGE No configuration specified. Defaulting to pwsafe - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "pwsafe - Win32 Release" && "$(CFG)" != "pwsafe - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "pwsafe.mak" CFG="pwsafe - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "pwsafe - Win32 Release" (based on "Win32 (x86) Application")
!MESSAGE "pwsafe - Win32 Debug" (based on "Win32 (x86) Application")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "pwsafe - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

!IF "$(RECURSE)" == "0" 

ALL : "$(OUTDIR)\pwsafe.exe"

!ELSE 

ALL : "corelib - Win32 Release" "$(OUTDIR)\pwsafe.exe"

!ENDIF 

!IF "$(RECURSE)" == "1" 
CLEAN :"corelib - Win32 ReleaseCLEAN" 
!ELSE 
CLEAN :
!ENDIF 
	-@erase "$(INTDIR)\AddDlg.obj"
	-@erase "$(INTDIR)\ClearQuestionDlg.obj"
	-@erase "$(INTDIR)\ConfirmDeleteDlg.obj"
	-@erase "$(INTDIR)\CryptKeyEntry.obj"
	-@erase "$(INTDIR)\DboxMain.obj"
	-@erase "$(INTDIR)\DboxOptions.obj"
	-@erase "$(INTDIR)\DboxPassword.obj"
	-@erase "$(INTDIR)\DboxView.obj"
	-@erase "$(INTDIR)\EditDlg.obj"
	-@erase "$(INTDIR)\FindDlg.obj"
	-@erase "$(INTDIR)\main.obj"
	-@erase "$(INTDIR)\model.obj"
	-@erase "$(INTDIR)\MyTreeCtrl.obj"
	-@erase "$(INTDIR)\OptionsDisplay.obj"
	-@erase "$(INTDIR)\OptionsMisc.obj"
	-@erase "$(INTDIR)\OptionsPasswordPolicy.obj"
	-@erase "$(INTDIR)\OptionsSecurity.obj"
	-@erase "$(INTDIR)\OptionsUsername.obj"
	-@erase "$(INTDIR)\PasskeyChangeDlg.obj"
	-@erase "$(INTDIR)\PasskeyEntry.obj"
	-@erase "$(INTDIR)\PasskeySetup.obj"
	-@erase "$(INTDIR)\PasswordSafe.res"
	-@erase "$(INTDIR)\PwFont.obj"
	-@erase "$(INTDIR)\QueryAddName.obj"
	-@erase "$(INTDIR)\QuerySetDef.obj"
	-@erase "$(INTDIR)\RemindSaveDlg.obj"
	-@erase "$(INTDIR)\SysColStatic.obj"
	-@erase "$(INTDIR)\ThisMfcApp.obj"
	-@erase "$(INTDIR)\TryAgainDlg.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(INTDIR)\winview.obj"
	-@erase "$(OUTDIR)\pwsafe.exe"
	-@erase "$(OUTDIR)\pwsafe.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MD /W4 /GX /Zi /O2 /I "C:\local\vc98\HTML Help Workshop\include" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_AFXDLL" /Fp"$(INTDIR)\pwsafe.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\PasswordSafe.res" /d "NDEBUG" /d "_AFXDLL" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\pwsafe.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=htmlhelp.lib corelib.lib WS2_32.lib Rpcrt4.lib /nologo /subsystem:windows /incremental:no /pdb:"$(OUTDIR)\pwsafe.pdb" /debug /machine:I386 /out:"$(OUTDIR)\pwsafe.exe" /libpath:"C:\local\vc98\HTML Help Workshop\lib" /libpath:".\corelib\Release" 
LINK32_OBJS= \
	"$(INTDIR)\AddDlg.obj" \
	"$(INTDIR)\ClearQuestionDlg.obj" \
	"$(INTDIR)\ConfirmDeleteDlg.obj" \
	"$(INTDIR)\CryptKeyEntry.obj" \
	"$(INTDIR)\DboxMain.obj" \
	"$(INTDIR)\DboxOptions.obj" \
	"$(INTDIR)\DboxPassword.obj" \
	"$(INTDIR)\DboxView.obj" \
	"$(INTDIR)\EditDlg.obj" \
	"$(INTDIR)\FindDlg.obj" \
	"$(INTDIR)\main.obj" \
	"$(INTDIR)\model.obj" \
	"$(INTDIR)\MyTreeCtrl.obj" \
	"$(INTDIR)\OptionsDisplay.obj" \
	"$(INTDIR)\OptionsMisc.obj" \
	"$(INTDIR)\OptionsPasswordPolicy.obj" \
	"$(INTDIR)\OptionsSecurity.obj" \
	"$(INTDIR)\OptionsUsername.obj" \
	"$(INTDIR)\PasskeyChangeDlg.obj" \
	"$(INTDIR)\PasskeyEntry.obj" \
	"$(INTDIR)\PasskeySetup.obj" \
	"$(INTDIR)\PwFont.obj" \
	"$(INTDIR)\QueryAddName.obj" \
	"$(INTDIR)\QuerySetDef.obj" \
	"$(INTDIR)\RemindSaveDlg.obj" \
	"$(INTDIR)\SysColStatic.obj" \
	"$(INTDIR)\ThisMfcApp.obj" \
	"$(INTDIR)\TryAgainDlg.obj" \
	"$(INTDIR)\winview.obj" \
	"$(INTDIR)\PasswordSafe.res" \
	".\corelib\Release\corelib.lib"

"$(OUTDIR)\pwsafe.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

!IF "$(RECURSE)" == "0" 

ALL : "$(OUTDIR)\pwsafe.exe" "$(OUTDIR)\pwsafe.bsc"

!ELSE 

ALL : "corelib - Win32 Debug" "$(OUTDIR)\pwsafe.exe" "$(OUTDIR)\pwsafe.bsc"

!ENDIF 

!IF "$(RECURSE)" == "1" 
CLEAN :"corelib - Win32 DebugCLEAN" 
!ELSE 
CLEAN :
!ENDIF 
	-@erase "$(INTDIR)\AddDlg.obj"
	-@erase "$(INTDIR)\AddDlg.sbr"
	-@erase "$(INTDIR)\ClearQuestionDlg.obj"
	-@erase "$(INTDIR)\ClearQuestionDlg.sbr"
	-@erase "$(INTDIR)\ConfirmDeleteDlg.obj"
	-@erase "$(INTDIR)\ConfirmDeleteDlg.sbr"
	-@erase "$(INTDIR)\CryptKeyEntry.obj"
	-@erase "$(INTDIR)\CryptKeyEntry.sbr"
	-@erase "$(INTDIR)\DboxMain.obj"
	-@erase "$(INTDIR)\DboxMain.sbr"
	-@erase "$(INTDIR)\DboxOptions.obj"
	-@erase "$(INTDIR)\DboxOptions.sbr"
	-@erase "$(INTDIR)\DboxPassword.obj"
	-@erase "$(INTDIR)\DboxPassword.sbr"
	-@erase "$(INTDIR)\DboxView.obj"
	-@erase "$(INTDIR)\DboxView.sbr"
	-@erase "$(INTDIR)\EditDlg.obj"
	-@erase "$(INTDIR)\EditDlg.sbr"
	-@erase "$(INTDIR)\FindDlg.obj"
	-@erase "$(INTDIR)\FindDlg.sbr"
	-@erase "$(INTDIR)\main.obj"
	-@erase "$(INTDIR)\main.sbr"
	-@erase "$(INTDIR)\model.obj"
	-@erase "$(INTDIR)\model.sbr"
	-@erase "$(INTDIR)\MyTreeCtrl.obj"
	-@erase "$(INTDIR)\MyTreeCtrl.sbr"
	-@erase "$(INTDIR)\OptionsDisplay.obj"
	-@erase "$(INTDIR)\OptionsDisplay.sbr"
	-@erase "$(INTDIR)\OptionsMisc.obj"
	-@erase "$(INTDIR)\OptionsMisc.sbr"
	-@erase "$(INTDIR)\OptionsPasswordPolicy.obj"
	-@erase "$(INTDIR)\OptionsPasswordPolicy.sbr"
	-@erase "$(INTDIR)\OptionsSecurity.obj"
	-@erase "$(INTDIR)\OptionsSecurity.sbr"
	-@erase "$(INTDIR)\OptionsUsername.obj"
	-@erase "$(INTDIR)\OptionsUsername.sbr"
	-@erase "$(INTDIR)\PasskeyChangeDlg.obj"
	-@erase "$(INTDIR)\PasskeyChangeDlg.sbr"
	-@erase "$(INTDIR)\PasskeyEntry.obj"
	-@erase "$(INTDIR)\PasskeyEntry.sbr"
	-@erase "$(INTDIR)\PasskeySetup.obj"
	-@erase "$(INTDIR)\PasskeySetup.sbr"
	-@erase "$(INTDIR)\PasswordSafe.res"
	-@erase "$(INTDIR)\PwFont.obj"
	-@erase "$(INTDIR)\PwFont.sbr"
	-@erase "$(INTDIR)\QueryAddName.obj"
	-@erase "$(INTDIR)\QueryAddName.sbr"
	-@erase "$(INTDIR)\QuerySetDef.obj"
	-@erase "$(INTDIR)\QuerySetDef.sbr"
	-@erase "$(INTDIR)\RemindSaveDlg.obj"
	-@erase "$(INTDIR)\RemindSaveDlg.sbr"
	-@erase "$(INTDIR)\SysColStatic.obj"
	-@erase "$(INTDIR)\SysColStatic.sbr"
	-@erase "$(INTDIR)\ThisMfcApp.obj"
	-@erase "$(INTDIR)\ThisMfcApp.sbr"
	-@erase "$(INTDIR)\TryAgainDlg.obj"
	-@erase "$(INTDIR)\TryAgainDlg.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(INTDIR)\winview.obj"
	-@erase "$(INTDIR)\winview.sbr"
	-@erase "$(OUTDIR)\pwsafe.bsc"
	-@erase "$(OUTDIR)\pwsafe.exe"
	-@erase "$(OUTDIR)\pwsafe.ilk"
	-@erase "$(OUTDIR)\pwsafe.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MDd /W4 /Gm /GX /ZI /Od /I "C:\local\vc98\HTML Help Workshop\include" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_AFXDLL" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\pwsafe.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\PasswordSafe.res" /d "_DEBUG" /d "_AFXDLL" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\pwsafe.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\AddDlg.sbr" \
	"$(INTDIR)\ClearQuestionDlg.sbr" \
	"$(INTDIR)\ConfirmDeleteDlg.sbr" \
	"$(INTDIR)\CryptKeyEntry.sbr" \
	"$(INTDIR)\DboxMain.sbr" \
	"$(INTDIR)\DboxOptions.sbr" \
	"$(INTDIR)\DboxPassword.sbr" \
	"$(INTDIR)\DboxView.sbr" \
	"$(INTDIR)\EditDlg.sbr" \
	"$(INTDIR)\FindDlg.sbr" \
	"$(INTDIR)\main.sbr" \
	"$(INTDIR)\model.sbr" \
	"$(INTDIR)\MyTreeCtrl.sbr" \
	"$(INTDIR)\OptionsDisplay.sbr" \
	"$(INTDIR)\OptionsMisc.sbr" \
	"$(INTDIR)\OptionsPasswordPolicy.sbr" \
	"$(INTDIR)\OptionsSecurity.sbr" \
	"$(INTDIR)\OptionsUsername.sbr" \
	"$(INTDIR)\PasskeyChangeDlg.sbr" \
	"$(INTDIR)\PasskeyEntry.sbr" \
	"$(INTDIR)\PasskeySetup.sbr" \
	"$(INTDIR)\PwFont.sbr" \
	"$(INTDIR)\QueryAddName.sbr" \
	"$(INTDIR)\QuerySetDef.sbr" \
	"$(INTDIR)\RemindSaveDlg.sbr" \
	"$(INTDIR)\SysColStatic.sbr" \
	"$(INTDIR)\ThisMfcApp.sbr" \
	"$(INTDIR)\TryAgainDlg.sbr" \
	"$(INTDIR)\winview.sbr"

"$(OUTDIR)\pwsafe.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=htmlhelp.lib corelib.lib WS2_32.lib Rpcrt4.lib /nologo /subsystem:windows /incremental:yes /pdb:"$(OUTDIR)\pwsafe.pdb" /debug /machine:I386 /out:"$(OUTDIR)\pwsafe.exe" /pdbtype:sept /libpath:"C:\local\vc98\HTML Help Workshop\lib" /libpath:".\corelib\Debug" 
LINK32_OBJS= \
	"$(INTDIR)\AddDlg.obj" \
	"$(INTDIR)\ClearQuestionDlg.obj" \
	"$(INTDIR)\ConfirmDeleteDlg.obj" \
	"$(INTDIR)\CryptKeyEntry.obj" \
	"$(INTDIR)\DboxMain.obj" \
	"$(INTDIR)\DboxOptions.obj" \
	"$(INTDIR)\DboxPassword.obj" \
	"$(INTDIR)\DboxView.obj" \
	"$(INTDIR)\EditDlg.obj" \
	"$(INTDIR)\FindDlg.obj" \
	"$(INTDIR)\main.obj" \
	"$(INTDIR)\model.obj" \
	"$(INTDIR)\MyTreeCtrl.obj" \
	"$(INTDIR)\OptionsDisplay.obj" \
	"$(INTDIR)\OptionsMisc.obj" \
	"$(INTDIR)\OptionsPasswordPolicy.obj" \
	"$(INTDIR)\OptionsSecurity.obj" \
	"$(INTDIR)\OptionsUsername.obj" \
	"$(INTDIR)\PasskeyChangeDlg.obj" \
	"$(INTDIR)\PasskeyEntry.obj" \
	"$(INTDIR)\PasskeySetup.obj" \
	"$(INTDIR)\PwFont.obj" \
	"$(INTDIR)\QueryAddName.obj" \
	"$(INTDIR)\QuerySetDef.obj" \
	"$(INTDIR)\RemindSaveDlg.obj" \
	"$(INTDIR)\SysColStatic.obj" \
	"$(INTDIR)\ThisMfcApp.obj" \
	"$(INTDIR)\TryAgainDlg.obj" \
	"$(INTDIR)\winview.obj" \
	"$(INTDIR)\PasswordSafe.res" \
	".\corelib\Debug\corelib.lib"

"$(OUTDIR)\pwsafe.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("pwsafe.dep")
!INCLUDE "pwsafe.dep"
!ELSE 
!MESSAGE Warning: cannot find "pwsafe.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "pwsafe - Win32 Release" || "$(CFG)" == "pwsafe - Win32 Debug"
SOURCE=.\AddDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\AddDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\AddDlg.obj"	"$(INTDIR)\AddDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\ClearQuestionDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\ClearQuestionDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\ClearQuestionDlg.obj"	"$(INTDIR)\ClearQuestionDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\ConfirmDeleteDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\ConfirmDeleteDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\ConfirmDeleteDlg.obj"	"$(INTDIR)\ConfirmDeleteDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\CryptKeyEntry.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\CryptKeyEntry.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\CryptKeyEntry.obj"	"$(INTDIR)\CryptKeyEntry.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\DboxMain.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\DboxMain.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\DboxMain.obj"	"$(INTDIR)\DboxMain.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\DboxOptions.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\DboxOptions.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\DboxOptions.obj"	"$(INTDIR)\DboxOptions.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\DboxPassword.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\DboxPassword.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\DboxPassword.obj"	"$(INTDIR)\DboxPassword.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\DboxView.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\DboxView.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\DboxView.obj"	"$(INTDIR)\DboxView.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\EditDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\EditDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\EditDlg.obj"	"$(INTDIR)\EditDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\FindDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\FindDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\FindDlg.obj"	"$(INTDIR)\FindDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\main.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\main.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\main.obj"	"$(INTDIR)\main.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\model.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\model.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\model.obj"	"$(INTDIR)\model.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\MyTreeCtrl.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\MyTreeCtrl.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\MyTreeCtrl.obj"	"$(INTDIR)\MyTreeCtrl.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\OptionsDisplay.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\OptionsDisplay.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\OptionsDisplay.obj"	"$(INTDIR)\OptionsDisplay.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\OptionsMisc.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\OptionsMisc.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\OptionsMisc.obj"	"$(INTDIR)\OptionsMisc.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\OptionsPasswordPolicy.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\OptionsPasswordPolicy.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\OptionsPasswordPolicy.obj"	"$(INTDIR)\OptionsPasswordPolicy.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\OptionsSecurity.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\OptionsSecurity.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\OptionsSecurity.obj"	"$(INTDIR)\OptionsSecurity.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\OptionsUsername.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\OptionsUsername.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\OptionsUsername.obj"	"$(INTDIR)\OptionsUsername.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\PasskeyChangeDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\PasskeyChangeDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\PasskeyChangeDlg.obj"	"$(INTDIR)\PasskeyChangeDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\PasskeyEntry.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\PasskeyEntry.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\PasskeyEntry.obj"	"$(INTDIR)\PasskeyEntry.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\PasskeySetup.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\PasskeySetup.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\PasskeySetup.obj"	"$(INTDIR)\PasskeySetup.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\PwFont.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\PwFont.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\PwFont.obj"	"$(INTDIR)\PwFont.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\QueryAddName.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\QueryAddName.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\QueryAddName.obj"	"$(INTDIR)\QueryAddName.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\QuerySetDef.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\QuerySetDef.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\QuerySetDef.obj"	"$(INTDIR)\QuerySetDef.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\RemindSaveDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\RemindSaveDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\RemindSaveDlg.obj"	"$(INTDIR)\RemindSaveDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\SysColStatic.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\SysColStatic.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\SysColStatic.obj"	"$(INTDIR)\SysColStatic.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\ThisMfcApp.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\ThisMfcApp.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\ThisMfcApp.obj"	"$(INTDIR)\ThisMfcApp.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\TryAgainDlg.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\TryAgainDlg.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\TryAgainDlg.obj"	"$(INTDIR)\TryAgainDlg.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\winview.cpp

!IF  "$(CFG)" == "pwsafe - Win32 Release"


"$(INTDIR)\winview.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"


"$(INTDIR)\winview.obj"	"$(INTDIR)\winview.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=.\PasswordSafe.rc

"$(INTDIR)\PasswordSafe.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)


!IF  "$(CFG)" == "pwsafe - Win32 Release"

"corelib - Win32 Release" : 
   cd ".\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Release" 
   cd ".."

"corelib - Win32 ReleaseCLEAN" : 
   cd ".\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Release" RECURSE=1 CLEAN 
   cd ".."

!ELSEIF  "$(CFG)" == "pwsafe - Win32 Debug"

"corelib - Win32 Debug" : 
   cd ".\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Debug" 
   cd ".."

"corelib - Win32 DebugCLEAN" : 
   cd ".\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Debug" RECURSE=1 CLEAN 
   cd ".."

!ENDIF 


!ENDIF 

