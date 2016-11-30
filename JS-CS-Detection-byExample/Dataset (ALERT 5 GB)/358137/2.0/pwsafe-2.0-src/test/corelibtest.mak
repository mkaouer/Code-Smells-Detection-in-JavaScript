# Microsoft Developer Studio Generated NMAKE File, Based on corelibtest.dsp
!IF "$(CFG)" == ""
CFG=corelibtest - Win32 Debug
!MESSAGE No configuration specified. Defaulting to corelibtest - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "corelibtest - Win32 Release" && "$(CFG)" != "corelibtest - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "corelibtest.mak" CFG="corelibtest - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "corelibtest - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "corelibtest - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "corelibtest - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

!IF "$(RECURSE)" == "0" 

ALL : "$(OUTDIR)\corelibtest.exe"

!ELSE 

ALL : "corelib - Win32 Release" "$(OUTDIR)\corelibtest.exe"

!ENDIF 

!IF "$(RECURSE)" == "1" 
CLEAN :"corelib - Win32 ReleaseCLEAN" 
!ELSE 
CLEAN :
!ENDIF 
	-@erase "$(INTDIR)\MyStringTest.obj"
	-@erase "$(INTDIR)\test.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\corelibtest.exe"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MD /W3 /GR /GX /O2 /I "../corelib" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS_" /D "_AFXDLL" /Fp"$(INTDIR)\corelibtest.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\corelibtest.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=/nologo /subsystem:console /incremental:no /pdb:"$(OUTDIR)\corelibtest.pdb" /machine:I386 /out:"$(OUTDIR)\corelibtest.exe" 
LINK32_OBJS= \
	"$(INTDIR)\MyStringTest.obj" \
	"$(INTDIR)\test.obj" \
	"..\corelib\Release\corelib.lib"

"$(OUTDIR)\corelibtest.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "corelibtest - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

!IF "$(RECURSE)" == "0" 

ALL : "$(OUTDIR)\corelibtest.exe" "$(OUTDIR)\corelibtest.bsc"

!ELSE 

ALL : "corelib - Win32 Debug" "$(OUTDIR)\corelibtest.exe" "$(OUTDIR)\corelibtest.bsc"

!ENDIF 

!IF "$(RECURSE)" == "1" 
CLEAN :"corelib - Win32 DebugCLEAN" 
!ELSE 
CLEAN :
!ENDIF 
	-@erase "$(INTDIR)\MyStringTest.obj"
	-@erase "$(INTDIR)\MyStringTest.sbr"
	-@erase "$(INTDIR)\test.obj"
	-@erase "$(INTDIR)\test.sbr"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\corelibtest.bsc"
	-@erase "$(OUTDIR)\corelibtest.exe"
	-@erase "$(OUTDIR)\corelibtest.ilk"
	-@erase "$(OUTDIR)\corelibtest.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "../corelib" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS_" /D "_AFXDLL" /FR"$(INTDIR)\\" /Fp"$(INTDIR)\corelibtest.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\corelibtest.bsc" 
BSC32_SBRS= \
	"$(INTDIR)\MyStringTest.sbr" \
	"$(INTDIR)\test.sbr"

"$(OUTDIR)\corelibtest.bsc" : "$(OUTDIR)" $(BSC32_SBRS)
    $(BSC32) @<<
  $(BSC32_FLAGS) $(BSC32_SBRS)
<<

LINK32=link.exe
LINK32_FLAGS=/nologo /subsystem:console /incremental:yes /pdb:"$(OUTDIR)\corelibtest.pdb" /debug /machine:I386 /out:"$(OUTDIR)\corelibtest.exe" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\MyStringTest.obj" \
	"$(INTDIR)\test.obj" \
	"..\corelib\Debug\corelib.lib"

"$(OUTDIR)\corelibtest.exe" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
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
!IF EXISTS("corelibtest.dep")
!INCLUDE "corelibtest.dep"
!ELSE 
!MESSAGE Warning: cannot find "corelibtest.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "corelibtest - Win32 Release" || "$(CFG)" == "corelibtest - Win32 Debug"
SOURCE=..\test\MyStringTest.cpp

!IF  "$(CFG)" == "corelibtest - Win32 Release"


"$(INTDIR)\MyStringTest.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "corelibtest - Win32 Debug"


"$(INTDIR)\MyStringTest.obj"	"$(INTDIR)\MyStringTest.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

SOURCE=..\test\test.cpp

!IF  "$(CFG)" == "corelibtest - Win32 Release"


"$(INTDIR)\test.obj" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "corelibtest - Win32 Debug"


"$(INTDIR)\test.obj"	"$(INTDIR)\test.sbr" : $(SOURCE) "$(INTDIR)"


!ENDIF 

!IF  "$(CFG)" == "corelibtest - Win32 Release"

"corelib - Win32 Release" : 
   cd "\Documents and Settings\rshapiro\My Documents\src\PasswordSafe\pwsafe\pwsafe\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Release" 
   cd "..\test"

"corelib - Win32 ReleaseCLEAN" : 
   cd "\Documents and Settings\rshapiro\My Documents\src\PasswordSafe\pwsafe\pwsafe\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Release" RECURSE=1 CLEAN 
   cd "..\test"

!ELSEIF  "$(CFG)" == "corelibtest - Win32 Debug"

"corelib - Win32 Debug" : 
   cd "\Documents and Settings\rshapiro\My Documents\src\PasswordSafe\pwsafe\pwsafe\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Debug" 
   cd "..\test"

"corelib - Win32 DebugCLEAN" : 
   cd "\Documents and Settings\rshapiro\My Documents\src\PasswordSafe\pwsafe\pwsafe\corelib"
   $(MAKE) /$(MAKEFLAGS) /F ".\corelib.mak" CFG="corelib - Win32 Debug" RECURSE=1 CLEAN 
   cd "..\test"

!ENDIF 


!ENDIF 

