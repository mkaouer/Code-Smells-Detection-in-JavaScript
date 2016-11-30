noinst_PROGRAMS = tradeclient

tradeclient_SOURCES = \
	tradeclient.cpp \
	Application.h \
	Application.cpp

tradeclient_LDADD = -lquickfix -lxml -lpthread

all: all-redirect link-binaries

link-binaries: tradeclient
	rm -f ../../bin/tradeclient
	ln -s ../examples/tradeclient/tradeclient ../../bin/tradeclient