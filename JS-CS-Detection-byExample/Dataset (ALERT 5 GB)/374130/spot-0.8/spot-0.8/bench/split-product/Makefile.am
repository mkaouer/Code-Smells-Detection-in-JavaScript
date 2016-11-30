## Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
## de l'Epita (LRDE).
##
## This file is part of Spot, a model checking library.
##
## Spot is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
##
## Spot is distributed in the hope that it will be useful, but WITHOUT
## ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
## or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
## License for more details.
##
## You should have received a copy of the GNU General Public License
## along with Spot; see the file COPYING.  If not, write to the Free
## Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
## 02111-1307, USA.

PML2TGBA = $(PERL) $(srcdir)/pml2tgba.pl

RES = cut-results

AM_CPPFLAGS = -I$(srcdir)/../../src $(BUDDY_CPPFLAGS)
AM_CXXFLAGS = $(WARNING_CXXFLAGS)
LDADD = ../../src/libspot.la

dist_noinst_SCRIPTS = \
  pml2tgba.pl

noinst_PROGRAMS = \
  cutscc

cutscc_SOURCES = cutscc.cc

nodist_noinst_DATA = \
  models/cl3serv1.tgba \
  models/cl3serv1R.tgba \
  models/cl3serv3.tgba \
  models/cl3serv3R.tgba \
  models/eeaean1.tgba \
  models/eeaean1R.tgba \
  models/eeaean2.tgba \
  models/eeaean2R.tgba \
  models/leader.tgba \
  models/leaderR.tgba \
  models/mobile1.tgba \
  models/mobile1R.tgba \
  models/mobile2.tgba \
  models/mobile2R.tgba \
  models/zune.tgba \
  models/zuneR.tgba

models/cl3serv1.tgba: $(srcdir)/models/cl3serv1.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/cl3serv1.pml w1 s1 >$@

models/cl3serv1R.tgba: $(srcdir)/models/cl3serv1.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/cl3serv1.pml w1 s1 >$@

models/cl3serv3.tgba: $(srcdir)/models/cl3serv3.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/cl3serv3.pml w1 s1 >$@

models/cl3serv3R.tgba: $(srcdir)/models/cl3serv3.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/cl3serv3.pml w1 s1 >$@

models/eeaean1.tgba: $(srcdir)/models/eeaean1.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/eeaean1.pml \
          noLeader zeroLeads oneLeads twoLeads threeLeads >$@

models/eeaean1R.tgba: $(srcdir)/models/eeaean1.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/eeaean1.pml \
          noLeader zeroLeads oneLeads twoLeads threeLeads >$@

models/eeaean2.tgba: $(srcdir)/models/eeaean2.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/eeaean2.pml \
          noLeader zeroLeads oneLeads twoLeads threeLeads >$@

models/eeaean2R.tgba: $(srcdir)/models/eeaean2.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/eeaean2.pml \
          noLeader zeroLeads oneLeads twoLeads threeLeads >$@

models/leader.tgba: $(srcdir)/models/leader.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/leader.pml \
          elected noLeader oneLeader >$@

models/leaderR.tgba: $(srcdir)/models/leader.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/leader.pml \
          elected noLeader oneLeader >$@

models/mobile1.tgba: $(srcdir)/models/mobile1.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/mobile1.pml \
          p q r >$@

models/mobile1R.tgba: $(srcdir)/models/mobile1.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/mobile1.pml \
          p q r >$@

models/mobile2.tgba: $(srcdir)/models/mobile2.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/mobile2.pml \
          p q r >$@

models/mobile2R.tgba: $(srcdir)/models/mobile2.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/mobile2.pml \
          p q r >$@

models/zune.tgba: $(srcdir)/models/zune.pml
	$(mkdir_p) models
	$(PML2TGBA) $(srcdir)/models/zune.pml \
          zune_at_S zune_at_E >$@

models/zuneR.tgba: $(srcdir)/models/zune.pml
	$(mkdir_p) models
	$(PML2TGBA) -r $(srcdir)/models/zune.pml \
          zune_at_S zune_at_E >$@

CLEANFILES = $(nodist_noinst_DATA)

bench: 	$(noinst_PROGRAMS)
	mkdir cut-results 2> /dev/null || true
	./cutscc models/clserv.ltl 4 models/cl3serv1.tgba > $(RES)/cl3serv1
	./cutscc models/clserv.ltl 4 models/cl3serv1R.tgba > $(RES)/cl3serv1R
	./cutscc models/clserv.ltl 4 models/cl3serv3.tgba > $(RES)/cl3serv3
	./cutscc models/clserv.ltl 4 models/cl3serv3R.tgba > $(RES)/cl3serv3R
	./cutscc models/eeaean.ltl 4 models/eeaean1.tgba > $(RES)/eeaean1
	./cutscc models/eeaean.ltl 4 models/eeaean1R.tgba > $(RES)/eeaean1R
	./cutscc models/eeaean.ltl 4 models/eeaean2.tgba > $(RES)/eeaean2
	./cutscc models/eeaean.ltl 4 models/eeaean2R.tgba > $(RES)/eeaean2R
	./cutscc models/leader.ltl 4 models/leader.tgba > $(RES)/leader
	./cutscc models/leader.ltl 4 models/leaderR.tgba > $(RES)/leaderR
	./cutscc models/mobile1.ltl 4 models/mobile1.tgba > $(RES)/mobile1
	./cutscc models/mobile1.ltl 4 models/mobile1R.tgba > $(RES)/mobile1R
	./cutscc models/mobile2.ltl 4 models/mobile2.tgba > $(RES)/mobile2
	./cutscc models/mobile2.ltl 4 models/mobile2R.tgba > $(RES)/mobile2R
	./cutscc models/zune.ltl 4 models/zune.tgba > $(RES)/zune
	./cutscc models/zune.ltl 4 models/zuneR.tgba > $(RES)/zuneR
