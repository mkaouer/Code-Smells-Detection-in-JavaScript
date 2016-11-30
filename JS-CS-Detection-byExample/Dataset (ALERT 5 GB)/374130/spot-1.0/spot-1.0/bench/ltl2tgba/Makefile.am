EXTRA_DIST = \
  algorithms \
  big \
  formulae.ltl \
  known \
  parseout.pl \
  small \
  lbtt2csv.pl
CLEAN_FILES = \
  big.cfg big.log big.txt \
  small.cfg small.log small.txt \
  known.cfg known.log known.txt

.PHONY = run
run: small.txt big.txt known.txt

deps = $(srcdir)/algorithms $(top_srcdir)/configure.ac $(top_builddir)/src/tgbatest/ltl2tgba

small.txt: $(srcdir)/small $(deps)
	$(srcdir)/small
big.txt: $(srcdir)/big $(deps)
	$(srcdir)/big
known.txt: $(srcdir)/known $(srcdir)/formulae.ltl $(deps)
	$(srcdir)/known
