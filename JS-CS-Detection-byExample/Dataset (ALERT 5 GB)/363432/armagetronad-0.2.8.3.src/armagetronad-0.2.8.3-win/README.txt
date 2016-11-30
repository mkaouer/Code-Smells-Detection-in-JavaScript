Compiling Armagetron Advanced on Windows with Code::Blocks
----------------------------------------------------------

Code::Blocks is an open source, cross platform C/C++ IDE.
It has multiple compiler support, and it comes in two presentations:
MinGW bundle or Standalone for use with other compilers.

More info and download of Code::Blocks: http://www.codeblocks.org

Armagetron Advanced can be compiled with these compilers:
* MinGw: obtained in the MinGW bundle download of Code::Blocks
* Free Microsoft Visual C++ Toolkit 2003: http://msdn.microsoft.com/visualc/vctoolkit2003/

The sources are distributed over three CVS modules:
* armagetronad: containing generic sources
* armagetronad_build_codeblocks: containing codeblocks project files
* armagetronad_winlibs: containing the libraries Armagetron Advanced depends on

You need to check out all three modules from the repository at
:ext:<your sf username>@cvs.sourceforge.net:/cvsroot/armagetronad
for developers or
:pserver:anonymous@cvs.sourceforge.net:/cvsroot/armagetronad
for everyone else.

1.  Create a project directory (e.g. C:\Projects\Armagetron Advanced)
2.  Put the armagetronad source files there
3.  In the same directory, put the armagetronad_build_codeblocks files
4.  In the same directory, put the armagetronad_winlibs files
5.  It should look something like this:
    +- Armagetron Advanced
       +- armagetronad
       +- build_codeblocks
       +- winlibs
6.  Go to the build_codeblocks directory
7.  IMPORTANT !!! Before compiling you need to run these commands:

    0) START->Run enter "cmd" press "OK"
    1) cd [..PATH TO PROJECT FOLDER]\armagetronad\resources
    2) At prompt [..]\armagetronad\resources> enter:

       python.exe ..\batch\make\sortresources.py

       Make sure you see the above prompt! Sortresouces.py will only work
       if called from [..]\armagetronad\resources. 

    3) run makedist.bat (from build_codeblocks) folder
8.  Start the Armagetron Advanced workspace (Armagetron.workspace)
9.  To compile you will need to change the active project and build target 
    (it defaults to Armagetron Advanced Client and Win32 Release build target)

In case of problems, visit guru3.sytes.net and ask for help.