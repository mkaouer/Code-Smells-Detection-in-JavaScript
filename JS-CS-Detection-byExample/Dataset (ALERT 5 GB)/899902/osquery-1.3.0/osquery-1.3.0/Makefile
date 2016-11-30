PLATFORM := $(shell uname -s)
MAKE = make
ifeq ($(PLATFORM),Darwin)
	BUILD_DIR=darwin
else ifeq ($(PLATFORM),FreeBSD)
	BUILD_DIR=freebsd
	MAKE=gmake
else
	DISTRO := $(shell if [ -f "/etc/redhat-release" ]; then echo "Centos"; fi)
	ifeq ($(DISTRO),Centos)
		BUILD_DIR=centos
	else
		BUILD_DIR=ubuntu
	endif
endif

all: .setup
	cd build/$(BUILD_DIR) && cmake -DCMAKE_BUILD_TYPE= ../.. && \
		$(MAKE) --no-print-directory $(MAKEFLAGS)

debug: .setup
	cd build/$(BUILD_DIR) && cmake -DCMAKE_BUILD_TYPE=Debug ../../ && \
		$(MAKE) --no-print-directory $(MAKEFLAGS)

deps: .setup
	./tools/provision.sh build build/$(BUILD_DIR)

distclean:
	rm -rf .sources build/$(BUILD_DIR) doxygen/html doxygen/latex
ifeq ($(PLATFORM),Linux)
		rm -rf build/linux
endif

.setup:
	mkdir -p build/$(BUILD_DIR)
ifeq ($(PLATFORM),Linux)
		ln -snf $(BUILD_DIR) build/linux
endif

package:
	# Alias for packages (do not use CPack)
	cd build/$(BUILD_DIR) && cmake -DCMAKE_BUILD_TYPE= ../../ && \
		$(MAKE) packages --no-print-directory $(MAKEFLAGS)

%::
	cd build/$(BUILD_DIR) && cmake ../.. && $(MAKE) --no-print-directory $@
