OBJECTS = bonk.o

all : BonkEnc.dll

BonkEnc.dll : $(OBJECTS)
	dllwrap -mwindows -o BonkEnc.dll --output-lib libbonkenc.a $(OBJECTS) -L../../../mptoolkit/src/iolib/iolib-cxx/lib -liolib-cxx -lwsock32
	strip --strip-all BonkEnc.dll
	upx -9 -q BonkEnc.dll

clean :
	rm -f BonkEnc.dll libbonkenc.a $(OBJECTS)

bonk.o: bonk.cpp
	gcc -funroll-loops -O6 -march=i586 -I../../../mptoolkit/src/iolib/iolib-cxx/include -c bonk.cpp
