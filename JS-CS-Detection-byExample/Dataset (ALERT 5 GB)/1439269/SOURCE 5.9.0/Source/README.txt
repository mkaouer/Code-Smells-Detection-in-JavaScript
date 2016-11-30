1. Compiling devcpp.exe

The the main executable devcpp.exe can be built using the following steps:

	1) Compile and Install the following packages:
		Source\VCL\DevCpp.dpk
		Source\VCL\SynEditPackages\SynEdit_<Delphi Version>.dpk
		Source\VCL\ClassBrowsing\ClassBrowsing.dpk

	2) Compile resources by running the following scripts:
		Source\CompileResources.bat
		
	3) Open the project file devcpp.dpr. Your IDE should not produce any 
	   'Module Not Found' or 'Resource Not Found' when opening files, compiling
	   files or running devcpp.exe.

This process has only been tested using Delphi 6 and Delphi 7. The code base
should be compatible with more recent versions of Delphi, but there is no 
guarantee anything will work.

2. Compiling associated tools

There are a couple of executables that need to be compiled and/or put in the
right folder when building a release:

	1) ConsolePauser.exe. This needs to be put in the root directory next to
	   devcpp.exe. This executable is launched by devcpp.exe when a console 
	   program is run and the option "Pause console programs after return" is
	   enabled in Environment Options. This file can be compiled using
	   Source\Tools\ConsolePauser\ConsolePauser.dev
	   
	   
	2) devcppPortable.exe. This file should be provided with all builds of
	   Dev-C++ (also the nonportable ones) and should also be placed in the root
	   directory. Launching this executable will run devcpp.exe and tell it to
	   store configuration files in the program directory instead of in
	   %APPDATA%\Dev-Cpp. This file can be compiled using
	   Source\Tools\DevCppPortable\DevCppPortable.dev
	   
	3) Packman.exe. This file is launched by the menu item located at
	   Tools >> Package Manager and provides .pak plugin support. This file has
	   not been touched since like 2005 so do what you wish with it.
	   
3. UPX

To decrease the main executable size, the old developers from Bloodshed used a
program called UPX to compress it. Here is a copy of their instructions on how
to use it (I don't, since the mere megabyte you save in size pales to the 330MB
of the provided compiler):

	When compressing with upx (Ultimate Packer for Executables) use:
	
	upx -9 --compress-icons=0 devcpp.exe
	
	Otherwise upx will compress all icons and the file associations will point
	to nonexisting (moved) icons within devcpp.exe
	
4. Image Map (somewhat outdated)

These are the descriptions of the indices of the menu images lists used in d
Transparent color is selected as the left most pixel of the bottom row.

Menus:
0 = New Project
1 = New Source File/Project New Source File
2 = New Resource/Project Edit Resource/Resource Sheet
3 = New Template
4 = Open
5 = Clear History/Remove Watch
6 = Save File
7 = Save As
8 = Save All
9 = Close File/Close Sheet
10 = Print
11 = Exit
13 = Undo
14 = Redo/Step Over
15 = Cut
16 = Copy
17 = Paste
18 = Insert(edit Menu)/Next Step
19 = Toggle Bookmark
20 = Goto Bookmark
21 = Find/Add Watch/Find Sheet
22 = Replace
23 = Find Next
24 = Goto Line
25 = Project Add File
26 = Project Remove File
27 = Project Options
28 = Compile/Compile Sheet
30 = Rebuild
31 = Run
32 = Debug/Debug Sheet
33 = Compile and Run
34 = Compiler Options/Export (submenu)
35 = Environment Options
36 = Editor Options/Edit Watch
37 = Configure Tools
38 = Full Screen
39 = Next Editor
40 = Previous Editor
41 = Update Check
42 = About
43 = Log Sheet
44 = Toolbars Menu
45 = Full Screen Mode
46 = Help Toolbar Button
47 = Delete Profiling Information
48 = Package Manager
49 = Syntax Check
50 = Close All
51 = Class Browser Class/Struct
52 = Class Browser Method
53 = Class Browser Variable


Gutter Images
0 = Breakpoint
1 = Active Breakpoint
2 = Invalid Breakpoint
3 = ???

Class Browser Images:
0 = Folder
1 = Classes
2 = Private Variables
3 = Protected Variables
4 = Public Variables
5 = Private Methods
6 = Protected Methods
7 = Public Methods
8 = Protected Inherited Methods ???
9 = Public Inherited Methods ???
10 = Protected Inherited Variables ???
11 = Public Inherited Variables ???

Project Images
0 = Root Node
1 = File Node
2 = Run
3 = Recycle Bin ???
4 = Folder Node
5 = Hamburger Icon ???
