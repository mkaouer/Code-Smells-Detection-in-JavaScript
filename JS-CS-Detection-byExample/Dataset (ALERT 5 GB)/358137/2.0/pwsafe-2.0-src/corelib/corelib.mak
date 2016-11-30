# Microsoft Developer Studio Generated NMAKE File, Based on corelib.dsp
!IF "$(CFG)" == ""
CFG=corelib - Win32 Debug
!MESSAGE No configuration specified. Defaulting to corelib - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "corelib - Win32 Release" && "$(CFG)" != "corelib - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "corelib.mak" CFG="corelib - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "corelib - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "corelib - Win32 Debug" (based on "Win32 (x86) Static Library")
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

!IF  "$(CFG)" == "corelib - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

ALL : "$(OUTDIR)\corelib.lib"


CLEAN :
	-@erase "$(INTDIR)\BlowFish.obj"
	-@erase "$(INTDIR)\ItemData.obj"
	-@erase "$(INTDIR)\ItemField.obj"
	-@erase "$(INTDIR)\MyString.obj"
	-@erase "$(INTDIR)\PWCharPool.obj"
	-@erase "$(INTDIR)\PWScore.obj"
	-@erase "$(INTDIR)\PWSfile.obj"
	-@erase "$(INTDIR)\sha1.obj"
	-@erase "$(INTDIR)\Util.obj"
	-@erase "$(INTDIR)\UUIDGen.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\corelib.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /ML /W4 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_LIB" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\corelib.bsc" 
BSC32_SBRS= \
	
LIB32=link.exe -lib
LIB32_FLAGS=/nologo /out:"$(OUTDIR)\corelib.lib" 
LIB32_OBJS= \
	"$(INTDIR)\BlowFish.obj" \
	"$(INTDIR)\ItemData.obj" \
	"$(INTDIR)\ItemField.obj" \
	"$(INTDIR)\MyString.obj" \
	"$(INTDIR)\PWCharPool.obj" \
	"$(INTDIR)\PWScore.obj" \
	"$(INTDIR)\PWSfile.obj" \
	"$(INTDIR)\sha1.obj" \
	"$(INTDIR)\Util.obj" \
	"$(INTDIR)\UUIDGen.obj"

"$(OUTDIR)\corelib.lib" : "$(OUTDIR)" $(DEF_FILE) $(LIB32_OBJS)
    $(LIB32) @<<
  $(LIB32_FLAGS) $(DEF_FLAGS) $(LIB32_OBJS)
<<

!ELSEIF  "$(CFG)" == "corelib - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\corelib.lib"


CLEAN :
	-@erase "$(INTDIR)\BlowFish.obj"
	-@erase "$(INTDIR)\ItemData.obj"
	-@erase "$(INTDIR)\ItemField.obj"
	-@erase "$(INTDIR)\MyString.obj"
	-@erase "$(INTDIR)\PWCharPool.obj"
	-@erase "$(INTDIR)\PWScore.obj"
	-@erase "$(INTDIR)\PWSfile.obj"
	-@erase "$(INTDIR)\sha1.obj"
	-@erase "$(INTDIR)\Util.obj"
	-@erase "$(INTDIR)\UUIDGen.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\corelib.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP_PROJ=/nologo /MDd /W4 /Gm /GX /ZI /Od /I "C:\local\vc98\HTML Help Workshop\include" /D "WIN32" /D "_DEBUG" /D "_LIB" /D "_AFXDLL" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\corelib.bsc" 
BSC32_SBRS= \
	
LIB32=link.exe -lib
LIB32_FLAGS=/nologo /out:"$(OUTDIR)\corelib.lib" 
LIB32_OBJS= \
	"$(INTDIR)\BlowFish.obj" \
	"$(INTDIR)\ItemData.obj" \
	"$(INTDIR)\ItemField.obj" \
	"$(INTDIR)\MyString.obj" \
	"$(INTDIR)\PWCharPool.obj" \
	"$(INTDIR)\PWScore.obj" \
	"$(INTDIR)\PWSfile.obj" \
	"$(INTDIR)\sha1.obj" \
	"$(INTDIR)\Util.obj" \
	"$(INTDIR)\UUIDGen.obj"

"$(OUTDIR)\corelib.lib" : "$(OUTDIR)" $(DEF_FILE) $(LIB32_OBJS)
    $(LIB32) @<<
  $(LIB32_FLAGS) $(DEF_FLAGS) $(LIB32_OBJS)
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
!IF EXISTS("corelib.dep")
!INCLUDE "corelib.dep"
!ELSE 
!MESSAGE Warning: cannot find "corelib.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "corelib - Win32 Release" || "$(CFG)" == "corelib - Win32 Debug"
SOURCE=.\BlowFish.cpp

"$(INTDIR)\BlowFish.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\ItemData.cpp

"$(INTDIR)\ItemData.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\ItemField.cpp

"$(INTDIR)\ItemField.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\MyString.cpp

"$(INTDIR)\MyString.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\PWCharPool.cpp

"$(INTDIR)\PWCharPool.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\PWScore.cpp

"$(INTDIR)\PWScore.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\PWSfile.cpp

"$(INTDIR)\PWSfile.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\sha1.cpp

"$(INTDIR)\sha1.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\Util.cpp

"$(INTDIR)\Util.obj" : $(SOURCE) "$(INTDIR)"


SOURCE=.\UUIDGen.cpp

"$(INTDIR)\UUIDGen.obj" : $(SOURCE) "$(INTDIR)"



!ENDIF 

