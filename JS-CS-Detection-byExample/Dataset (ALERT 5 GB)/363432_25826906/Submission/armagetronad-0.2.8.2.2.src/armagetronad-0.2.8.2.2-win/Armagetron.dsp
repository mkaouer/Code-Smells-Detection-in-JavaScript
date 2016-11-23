# Microsoft Developer Studio Project File - Name="Armagetron" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** NICHT BEARBEITEN **

# TARGTYPE "Win32 (x86) Application" 0x0101

CFG=ARMAGETRON - WIN32 RELEASE
!MESSAGE Dies ist kein gültiges Makefile. Zum Erstellen dieses Projekts mit NMAKE
!MESSAGE verwenden Sie den Befehl "Makefile exportieren" und führen Sie den Befehl
!MESSAGE 
!MESSAGE NMAKE /f "Armagetron.mak".
!MESSAGE 
!MESSAGE Sie können beim Ausführen von NMAKE eine Konfiguration angeben
!MESSAGE durch Definieren des Makros CFG in der Befehlszeile. Zum Beispiel:
!MESSAGE 
!MESSAGE NMAKE /f "Armagetron.mak" CFG="ARMAGETRON - WIN32 RELEASE"
!MESSAGE 
!MESSAGE Für die Konfiguration stehen zur Auswahl:
!MESSAGE 
!MESSAGE "Armagetron - Win32 Release" (basierend auf  "Win32 (x86) Application")
!MESSAGE "Armagetron - Win32 Debug" (basierend auf  "Win32 (x86) Application")
!MESSAGE "Armagetron - Win32 Profile" (basierend auf  "Win32 (x86) Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "Armagetron - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "tmp\Armagetron___Win32_Release"
# PROP Intermediate_Dir "tmp\Armagetron___Win32_Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /Zi /O2 /I "../armagetronad_winlibs/SDL_mixer" /I "../armagetronad_winlibs/SDL/include" /I "../armagetronad_winlibs/libxml2/include" /I "../armagetronad_winlibs/iconv/include" /I "src/win32" /I "src" /I "../armagetronad_winlibs/SDL_image" /I "../" /I "src/tools" /I "src/ui" /I "src/render" /I "src/engine" /I "src/network" /I "../armagetronad_winlibs/SDL_image/VisualC/graphics/include" /I "src/thirdparty/particles" /D "_WINDOWS" /D "DIRTY" /D "DONTUSEMEMMANAGER" /D "_MBCS" /D "NDEBUG" /FR /FD /Zm200 /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x407 /d "NDEBUG"
# ADD RSC /l 0x407 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:windows /machine:I386
# ADD LINK32 opengl32.lib glu32.lib wsock32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib /nologo /subsystem:windows /debug /machine:I386 /out:"..\dist\armagetronad.exe"
# SUBTRACT LINK32 /pdb:none

!ELSEIF  "$(CFG)" == "Armagetron - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Armagetron___Win32_Debug"
# PROP BASE Intermediate_Dir "Armagetron___Win32_Debug"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "tmp\Armagetron___Win32_Debug"
# PROP Intermediate_Dir "tmp\Armagetron___Win32_Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /Zi /O2 /I "src/win32" /I "src" /I "../../SDL/include" /I "../" /I "../../SDL_Image" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /MDd /W3 /GR /GX /Zi /Od /I "../armagetronad_winlibs/SDL/include" /I "src/win32" /I "src" /I "../armagetronad_winlibs/SDL_image" /I "../" /I "src/tools" /I "src/ui" /I "src/render" /I "src/engine" /I "src/network" /I "../armagetronad_winlibs/SDL_mixer" /I "../armagetronad_winlibs/SDL_image/VisualC/graphics/include" /I "../armagetronad_winlibs/libxml2/include" /I "../armagetronad_winlibs/iconv/include" /I "src/thirdparty/particles" /D "_WINDOWS" /D "_MBCS" /D "DEBUG" /Fr /FD /Zm200 /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x407 /d "NDEBUG"
# ADD RSC /l 0x407 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib opengl32.lib glu32.lib /nologo /subsystem:windows /debug /machine:I386 /out:"../dist/armagetron.exe" /fixed:no
# SUBTRACT BASE LINK32 /pdb:none
# ADD LINK32 opengl32.lib glu32.lib wsock32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib /nologo /subsystem:windows /debug /machine:I386 /nodefaultlib:"msvcrt.lib" /out:"..\debug\armagetronad_debug.exe" /fixed:no
# SUBTRACT LINK32 /pdb:none

!ELSEIF  "$(CFG)" == "Armagetron - Win32 Profile"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Armagetron___Win32_Profile"
# PROP BASE Intermediate_Dir "Armagetron___Win32_Profile"
# PROP BASE Ignore_Export_Lib 0
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "tmp\Armagetron___Win32_Profile"
# PROP Intermediate_Dir "tmp\Armagetron___Win32_Profile"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GR /GX /Zi /O2 /I "src/win32" /I "src" /I "../" /I "src/tools" /I "src/ui" /I "src/render" /I "src/engine" /I "src/network" /D "WIN32" /D "_WINDOWS" /D "_MBCS" /D "DIRTY" /FD /c
# SUBTRACT BASE CPP /YX
# ADD CPP /nologo /MD /W3 /GR /GX /Zi /O2 /I "../armagetronad_winlibs/SDL_mixer" /I "../armagetronad_winlibs/SDL/include" /I "src/win32" /I "src" /I "../armagetronad_winlibs/SDL_image" /I "../" /I "src/tools" /I "src/ui" /I "src/render" /I "src/engine" /I "src/network" /I "../armagetronad_winlibs/libxml2/include" /I "../armagetronad_winlibs/iconv/include" /I "../armagetronad_winlibs/SDL_image/VisualC/graphics/include" /I "src/thirdparty/particles" /D "_WINDOWS" /D "DIRTY" /D "DONTUSEMEMMANAGER" /D "_MBCS" /FR /FD /Zm200 /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x407 /d "NDEBUG"
# ADD RSC /l 0x407 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib opengl32.lib glu32.lib wsock32.lib /nologo /subsystem:windows /machine:I386 /out:"..\dist\armagetron.exe" /fixed:no
# SUBTRACT BASE LINK32 /pdb:none /debug
# ADD LINK32 opengl32.lib glu32.lib wsock32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib /nologo /subsystem:windows /debug /machine:I386 /out:"..\debug\armagetronad_profile.exe" /fixed:no
# SUBTRACT LINK32 /pdb:none

!ENDIF 

# Begin Target

# Name "Armagetron - Win32 Release"
# Name "Armagetron - Win32 Debug"
# Name "Armagetron - Win32 Profile"
# Begin Group "Ressourcendateien"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# Begin Group "Libs"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\armagetronad_winlibs\SDL_image\VisualC\graphics\lib\libpng13.lib
# End Source File
# Begin Source File

SOURCE=..\armagetronad_winlibs\libxml2\lib\libxml2.lib
# End Source File
# End Group
# Begin Group "Tron"

# PROP Default_Filter ""
# Begin Group "cpp Nr. 4"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\tron\gAIBase.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gAICharacter.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gArena.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gArmagetron.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gCamera.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gCycle.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gCycleMovement.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gExplosion.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gFloor.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gGame.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gHud.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gLanguageMenu.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gLogo.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gMenus.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gParser.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gParticles.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gSensor.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gServerBrowser.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gServerFavorites.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gSparks.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gSpawn.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gStuff.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gTeam.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gWall.cpp
# End Source File
# Begin Source File

SOURCE=src\tron\gWinZone.cpp
# End Source File
# End Group
# Begin Group "h Nr. 4"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\tron\gAIBase.h
# End Source File
# Begin Source File

SOURCE=src\tron\gAICharacter.h
# End Source File
# Begin Source File

SOURCE=src\tron\gArena.h
# End Source File
# Begin Source File

SOURCE=src\tron\gCamera.h
# End Source File
# Begin Source File

SOURCE=src\tron\gCycle.h
# End Source File
# Begin Source File

SOURCE=src\tron\gCycleMovement.h
# End Source File
# Begin Source File

SOURCE=src\tron\gExplosion.h
# End Source File
# Begin Source File

SOURCE=src\tron\gGame.h
# End Source File
# Begin Source File

SOURCE=src\tron\gHud.h
# End Source File
# Begin Source File

SOURCE=src\tron\gLanguageMenu.h
# End Source File
# Begin Source File

SOURCE=src\tron\gLogo.h
# End Source File
# Begin Source File

SOURCE=src\tron\gMenus.h
# End Source File
# Begin Source File

SOURCE=src\tron\gParser.h
# End Source File
# Begin Source File

SOURCE=src\tron\gParticles.h
# End Source File
# Begin Source File

SOURCE=src\tron\gSensor.h
# End Source File
# Begin Source File

SOURCE=src\tron\gServerBrowser.h
# End Source File
# Begin Source File

SOURCE=src\tron\gServerFavorites.h
# End Source File
# Begin Source File

SOURCE=src\tron\gSparks.h
# End Source File
# Begin Source File

SOURCE=src\tron\gSpawn.h
# End Source File
# Begin Source File

SOURCE=src\tron\gStuff.h
# End Source File
# Begin Source File

SOURCE=src\tron\gTeam.h
# End Source File
# Begin Source File

SOURCE=src\tron\gWall.h
# End Source File
# Begin Source File

SOURCE=src\tron\gWinZone.h
# End Source File
# End Group
# End Group
# Begin Group "Engine"

# PROP Default_Filter ""
# Begin Group "cpp Nr. 5"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\engine\eAdvWall.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eAuthentification.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eAxis.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eCamera.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eDebugLine.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eDisplay.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eFloor.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eGameObject.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eGrid.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eKrawall.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eNetGameObject.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\ePath.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\ePlayer.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eRectangle.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eSensor.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eSound.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eTeam.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eTimer.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eVoter.cpp
# End Source File
# Begin Source File

SOURCE=src\engine\eWall.cpp
# End Source File
# End Group
# Begin Group "h Nr. 5"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\engine\eAdvWall.h
# End Source File
# Begin Source File

SOURCE=src\engine\eAuthentification.h
# End Source File
# Begin Source File

SOURCE=src\engine\eAxis.h
# End Source File
# Begin Source File

SOURCE=src\engine\eCamera.h
# End Source File
# Begin Source File

SOURCE=src\engine\eCoord.h
# End Source File
# Begin Source File

SOURCE=src\engine\eDebugLine.h
# End Source File
# Begin Source File

SOURCE=src\engine\eFloor.h
# End Source File
# Begin Source File

SOURCE=src\engine\eGameObject.h
# End Source File
# Begin Source File

SOURCE=src\engine\eGrid.h
# End Source File
# Begin Source File

SOURCE=src\engine\eKrawall.h
# End Source File
# Begin Source File

SOURCE=src\engine\eNetGameObject.h
# End Source File
# Begin Source File

SOURCE=src\engine\ePath.h
# End Source File
# Begin Source File

SOURCE=src\engine\ePlayer.h
# End Source File
# Begin Source File

SOURCE=src\engine\eRectangle.h
# End Source File
# Begin Source File

SOURCE=src\engine\eSensor.h
# End Source File
# Begin Source File

SOURCE=src\engine\eSound.h
# End Source File
# Begin Source File

SOURCE=src\engine\eTeam.h
# End Source File
# Begin Source File

SOURCE=src\engine\eTess2.h
# End Source File
# Begin Source File

SOURCE=src\engine\eTimer.h
# End Source File
# Begin Source File

SOURCE=src\engine\eVoter.h
# End Source File
# Begin Source File

SOURCE=src\engine\eWall.h
# End Source File
# End Group
# End Group
# Begin Group "Network"

# PROP Default_Filter ""
# Begin Group "cpp Nr. 3"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\network\md5.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nAuthentification.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nConfig.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nKrawall.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nKrawallPrivate.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nNetObject.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nNetwork.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nObserver.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nPriorizing.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nServerInfo.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nSocket.cpp
# End Source File
# Begin Source File

SOURCE=src\network\nSpamProtection.cpp
# End Source File
# End Group
# Begin Group "h Nr. 3"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\network\md5.h
# End Source File
# Begin Source File

SOURCE=src\network\nAuthentification.h
# End Source File
# Begin Source File

SOURCE=src\network\nConfig.h
# End Source File
# Begin Source File

SOURCE=src\network\nKrawall.h
# End Source File
# Begin Source File

SOURCE=src\network\nNetObject.h
# End Source File
# Begin Source File

SOURCE=src\network\nNetwork.h
# End Source File
# Begin Source File

SOURCE=src\network\nObserver.h
# End Source File
# Begin Source File

SOURCE=src\network\nPriorizing.h
# End Source File
# Begin Source File

SOURCE=src\network\nServerInfo.h
# End Source File
# Begin Source File

SOURCE=src\network\nSimulatePing.h
# End Source File
# Begin Source File

SOURCE=src\network\nSocket.h
# End Source File
# Begin Source File

SOURCE=src\network\nSpamProtection.h
# End Source File
# End Group
# Begin Source File

SOURCE=src\network\Makefile.in
# End Source File
# End Group
# Begin Group "Ui"

# PROP Default_Filter ""
# Begin Group "cpp Nr. 2"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\ui\uInput.cpp
# End Source File
# Begin Source File

SOURCE=src\ui\uInputQueue.cpp
# End Source File
# Begin Source File

SOURCE=src\ui\uMenu.cpp
# End Source File
# End Group
# Begin Group "h Nr. 2"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\ui\uInput.h
# End Source File
# Begin Source File

SOURCE=src\ui\uInputQueue.h
# End Source File
# Begin Source File

SOURCE=src\ui\uMenu.h
# End Source File
# End Group
# End Group
# Begin Group "Renderer"

# PROP Default_Filter ""
# Begin Group "cpp Nr. 1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\render\rConsole.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rConsoleGraph.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rFont.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rGLRender.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rModel.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rRender.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rScreen.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rSysdep.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rTexture.cpp
# End Source File
# Begin Source File

SOURCE=src\render\rViewport.cpp
# End Source File
# End Group
# Begin Group "h Nr. 1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\render\rConsole.h
# End Source File
# Begin Source File

SOURCE=src\render\rFont.h
# End Source File
# Begin Source File

SOURCE=src\render\rGL.h
# End Source File
# Begin Source File

SOURCE=src\render\rModel.h
# End Source File
# Begin Source File

SOURCE=src\render\rRender.h
# End Source File
# Begin Source File

SOURCE=src\render\rScreen.h
# End Source File
# Begin Source File

SOURCE=src\render\rSDL.h
# End Source File
# Begin Source File

SOURCE=src\render\rSysdep.h
# End Source File
# Begin Source File

SOURCE=src\render\rTexture.h
# End Source File
# Begin Source File

SOURCE=src\render\rViewport.h
# End Source File
# End Group
# End Group
# Begin Group "Tools"

# PROP Default_Filter ""
# Begin Group "cpp"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\win32\dirent.c
# End Source File
# Begin Source File

SOURCE=src\tools\tArray.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tCallback.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tCommandLine.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tConfiguration.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tConsole.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tCrypt.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tDirectories.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tError.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tEventQueue.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tException.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tHeap.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tLinkedList.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tLocale.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tMemManager.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tMemStack.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tRandom.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tRecorder.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tRecorderInternal.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tResourceManager.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tRing.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tSafePTR.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tString.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tSysTime.cpp
# End Source File
# Begin Source File

SOURCE=src\tools\tToDo.cpp
# End Source File
# End Group
# Begin Group "h"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\tools\tarray.h
# End Source File
# Begin Source File

SOURCE=src\tools\tcallback.h
# End Source File
# Begin Source File

SOURCE=src\tools\tCallbackString.h
# End Source File
# Begin Source File

SOURCE=src\tools\tColor.h
# End Source File
# Begin Source File

SOURCE=src\tools\tCommandLine.h
# End Source File
# Begin Source File

SOURCE=src\tools\tconfiguration.h
# End Source File
# Begin Source File

SOURCE=src\tools\tconsole.h
# End Source File
# Begin Source File

SOURCE=src\tools\tCrypt.h
# End Source File
# Begin Source File

SOURCE=src\tools\tDirectories.h
# End Source File
# Begin Source File

SOURCE=src\tools\terror.h
# End Source File
# Begin Source File

SOURCE=src\tools\tEventQueue.h
# End Source File
# Begin Source File

SOURCE=src\tools\tException.h
# End Source File
# Begin Source File

SOURCE=src\tools\theap.h
# End Source File
# Begin Source File

SOURCE=src\tools\tinitexit.h
# End Source File
# Begin Source File

SOURCE=src\tools\tlinkedlist.h
# End Source File
# Begin Source File

SOURCE=src\tools\tlist.h
# End Source File
# Begin Source File

SOURCE=src\tools\tLocale.h
# End Source File
# Begin Source File

SOURCE=src\tools\tMath.h
# End Source File
# Begin Source File

SOURCE=src\tools\tmemmanager.h
# End Source File
# Begin Source File

SOURCE=src\tools\tMemStack.h
# End Source File
# Begin Source File

SOURCE=src\tools\tRandom.h
# End Source File
# Begin Source File

SOURCE=src\tools\tRecorder.h
# End Source File
# Begin Source File

SOURCE=src\tools\tRecorderInternal.h
# End Source File
# Begin Source File

SOURCE=src\tools\tReferenceHolder.h
# End Source File
# Begin Source File

SOURCE=src\tools\tResourceManager.h
# End Source File
# Begin Source File

SOURCE=src\tools\tring.h
# End Source File
# Begin Source File

SOURCE=src\tools\tsafeptr.h
# End Source File
# Begin Source File

SOURCE=src\tools\tString.h
# End Source File
# Begin Source File

SOURCE=src\tools\tsystime.h
# End Source File
# Begin Source File

SOURCE=src\tools\ttodo.h
# End Source File
# End Group
# End Group
# Begin Group "External"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\CHANGELOG
# End Source File
# Begin Source File

SOURCE=..\debug\config\default.cfg
# End Source File
# Begin Source File

SOURCE=..\debug\language\deutsch.txt
# End Source File
# Begin Source File

SOURCE=..\debug\language\english.txt
# End Source File
# Begin Source File

SOURCE=..\debug\language\new.txt
# End Source File
# Begin Source File

SOURCE=..\debug\config\settings.cfg
# End Source File
# Begin Source File

SOURCE=..\debug\config\settings_dedicated.cfg
# End Source File
# End Group
# Begin Group "ThirdParty"

# PROP Default_Filter ""
# Begin Group "Particles"

# PROP Default_Filter ""
# Begin Group "cpp No. 1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\thirdparty\particles\action_api.cpp
# End Source File
# Begin Source File

SOURCE=src\thirdparty\particles\actions.cpp
# End Source File
# Begin Source File

SOURCE=src\thirdparty\particles\opengl.cpp
# End Source File
# Begin Source File

SOURCE=src\thirdparty\particles\system.cpp
# End Source File
# End Group
# Begin Group "h No. 1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=src\thirdparty\particles\general.h
# End Source File
# Begin Source File

SOURCE=src\thirdparty\particles\p_vector.h
# End Source File
# Begin Source File

SOURCE=src\thirdparty\particles\papi.h
# End Source File
# End Group
# End Group
# End Group
# Begin Source File

SOURCE=.\armagetron.res
# End Source File
# Begin Source File

SOURCE=src\win32\config.h
# End Source File
# Begin Source File

SOURCE=src\win32\config_common.h
# End Source File
# Begin Source File

SOURCE=src\config_ide.h
# End Source File
# Begin Source File

SOURCE=src\defs.h
# End Source File
# Begin Source File

SOURCE=..\armagetronad_winlibs\SDL\src\main\win32\SDL_main.c
# End Source File
# End Target
# End Project
