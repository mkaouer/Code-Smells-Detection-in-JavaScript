SUBDIRS = C++ java

noinst_PROGRAMS = at_server at_client ut pt

at_server_SOURCES = at_server.cpp at_application.h
at_client_SOURCES = at_client.cpp at_application.h
ut_SOURCES = ut.cpp TestSuite.h
pt_SOURCES = pt.cpp

EXTRA_DIST = getopt.c getopt-repl.h

at_server_LDFLAGS = -static
at_client_LDFLAGS = -static
ut_LDFLAGS = -static
pt_LDFLAGS = -static

at_server_LDADD = C++/libquickfix.la -lxml2 -lpthread -lrt
at_client_LDADD = C++/libquickfix.la -lxml2 -lpthread -lrt
ut_LDADD = C++/test/libquickfixcpptest.la C++/libquickfix.la -lxml2 -lpthread -lrt
pt_LDADD = C++/libquickfix.la -lxml2 -lpthread -lrt

INCLUDES =-I. -I.. -I../.. -IC++

all: all-recursive link-binaries

link-binaries: ut pt at_server at_client
	rm -f ../bin/ut ../bin/pt ../bin/at_server ../bin/at_client
	ln -s ../src/ut ../bin/ut
	ln -s ../src/pt ../bin/pt
	ln -s ../src/at_server ../bin/at_server
	ln -s ../src/at_client ../bin/at_client