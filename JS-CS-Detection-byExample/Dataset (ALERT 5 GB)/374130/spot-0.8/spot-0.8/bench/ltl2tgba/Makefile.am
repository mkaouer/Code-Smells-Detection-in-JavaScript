EXTRA_DIST = \
  algorithms \
  big \
  formulae.ltl \
  known \
  parseout.pl \
  small
CLEAN_FILES = \
  big.cfg big.log big.txt \
  small.cfg small.log small.txt \
  known.cfg known.log known.txt

.PHONY = run
run: small.txt big.txt known.txt wfair.txt

small.txt: $(srcdir)/small $(srcdir)/algorithms $(top_srcdir)/configure.ac
	$(srcdir)/small
big.txt: $(srcdir)/big $(srcdir)/algorithms $(top_srcdir)/configure.ac
	$(srcdir)/big
known.txt: $(srcdir)/known $(srcdir)/algorithms $(srcdir)/formulae.ltl $(top_srcdir)/configure.ac
	$(srcdir)/known
