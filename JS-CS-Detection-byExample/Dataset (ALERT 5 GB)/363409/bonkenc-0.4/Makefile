# Makefile fo BonkEnc

INCLUDEDIR1 = ./include
INCLUDEDIR2 = ../smooth/include
OBJECTDIR = ./objects
BINDIR = ./bin
SRCDIR = ./src
LIBDIR1 = ./lib
LIBDIR2 = ../smooth/lib
RESOURCEDIR = ./resources
BINRESDIR = $(RESOURCEDIR)/binary

OBJECTS = $(OBJECTDIR)/bladeconfig.o $(OBJECTDIR)/bonkconfig.o $(OBJECTDIR)/main.o $(OBJECTDIR)/parseini.o $(OBJECTDIR)/selenc.o $(OBJECTDIR)/wav.o
RESOURCES = $(OBJECTDIR)/resources.o

EXENAME = $(BINDIR)/bonkenc.exe

COMPILER = gcc
RESCOMP = windres
LINKER = gcc
REMOVER = rm
ECHO = echo
COMPILER_OPTS = -I$(INCLUDEDIR1) -I$(INCLUDEDIR2) -march=i586 -O6 -ffast-math -Wall -Wno-pmf-conversions -fno-exceptions -c
LINKER_OPTS = -L$(LIBDIR1) -L$(LIBDIR2) -lsmooth -lbonkenc -lshell32 -mwindows -o$(EXENAME)
REMOVER_OPTS = -f
STRIP = strip
STRIP_OPTS = --strip-all
PACKER = upx
PACKER_OPTS = -9 -q
RESCOMP_OPTS = -O coff

.PHONY: all install clean
.SILENT:

all: $(OBJECTS) $(RESOURCES) $(EXENAME)

install:

clean:
	$(ECHO) -n Cleaning directories...
	$(REMOVER) $(REMOVER_OPTS) $(OBJECTS) $(RESOURCES) $(EXENAME)
	$(ECHO) done.

$(EXENAME): $(OBJECTS)
	$(ECHO) -n Linking $(EXENAME)...
	$(LINKER) $(OBJECTS) $(RESOURCES) $(LINKER_OPTS)
	$(STRIP) $(STRIP_OPTS) $(EXENAME)
	$(PACKER) $(PACKER_OPTS) $(EXENAME)
	$(ECHO) done.

$(OBJECTDIR)/bladeconfig.o: $(SRCDIR)/bladeconfig.cpp
	$(ECHO) -n Compiling $(SRCDIR)/bladeconfig.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/bladeconfig.cpp -o $(OBJECTDIR)/bladeconfig.o
	$(ECHO) done.

$(OBJECTDIR)/bonkconfig.o: $(SRCDIR)/bonkconfig.cpp
	$(ECHO) -n Compiling $(SRCDIR)/bonkconfig.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/bonkconfig.cpp -o $(OBJECTDIR)/bonkconfig.o
	$(ECHO) done.

$(OBJECTDIR)/main.o: $(SRCDIR)/main.cpp
	$(ECHO) -n Compiling $(SRCDIR)/main.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/main.cpp -o $(OBJECTDIR)/main.o
	$(ECHO) done.

$(OBJECTDIR)/parseini.o: $(SRCDIR)/parseini.cpp
	$(ECHO) -n Compiling $(SRCDIR)/parseini.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/parseini.cpp -o $(OBJECTDIR)/parseini.o
	$(ECHO) done.

$(OBJECTDIR)/selenc.o: $(SRCDIR)/selenc.cpp
	$(ECHO) -n Compiling $(SRCDIR)/selenc.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/selenc.cpp -o $(OBJECTDIR)/selenc.o
	$(ECHO) done.

$(OBJECTDIR)/wav.o: $(SRCDIR)/wav.cpp
	$(ECHO) -n Compiling $(SRCDIR)/wav.cpp...
	$(COMPILER) $(COMPILER_OPTS) $(SRCDIR)/wav.cpp -o $(OBJECTDIR)/wav.o
	$(ECHO) done.

$(OBJECTDIR)/resources.o: $(INCLUDEDIR1)/resources.h $(BINRESDIR)/bonkenc.ico
	$(ECHO) -n Compiling $(RESOURCEDIR)/resources.rc...
	$(RESCOMP) $(RESCOMP_OPTS) $(RESOURCEDIR)/resources.rc -o $(OBJECTDIR)/resources.o
	$(ECHO) done.
