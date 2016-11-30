
include_HEADERS = bdd.h fdd.h bvec.h

BUDDY_FLAGS = -I$(top_builddir) -DVERSION=23 -DBUDDY_DLL_EXPORTS -DBUDDY_DLL

lib_LTLIBRARIES = libbdd.la
# See the `Updating version info' node of the Libtool manual before
# changing this.
libbdd_la_LDFLAGS = -no-undefined -version-info 0:0:0
libbdd_la_CPPFLAGS = $(BUDDY_FLAGS)
libbdd_la_SOURCES = \
 bddio.c \
 bddop.c \
 bddtree.h \
 bvec.c \
 cache.c \
 cache.h \
 cppext.cxx \
 fdd.c \
 imatrix.c \
 imatrix.h \
 kernel.c \
 kernel.h \
 pairs.c \
 prime.c \
 prime.h \
 reorder.c \
 tree.c

check_PROGRAMS = bddtest
bddtest_SOURCES = bddtest.cxx
bddtest_CPPFLAGS = -I$(top_builddir)
bddtest_LDADD = ./libbdd.la
