bin_PROGRAMS = lbtt lbtt-translate
lbtt_SOURCES = Alloc.h BitArray.h Bitset.h BitArray.cc BuchiAutomaton.h \
BuchiAutomaton.cc Config-parse.yy Config-lex.ll Configuration.h \
Configuration.cc DispUtil.h DispUtil.cc EdgeContainer.h Exception.h \
FormulaRandomizer.h FormulaRandomizer.cc FormulaWriter.h LtlFormula.h \
LtlFormula.cc main.cc PathEvaluator.h PathEvaluator.cc PathIterator.h \
PathIterator.cc ProductAutomaton.h ProductAutomaton.cc Random.h SccIterator.h \
SharedTestData.h StatDisplay.h StatDisplay.cc StateSpace.h StateSpace.cc \
StateSpaceRandomizer.h StateSpaceRandomizer.cc StringUtil.h StringUtil.cc \
TestOperations.h TestOperations.cc TestRoundInfo.h TestStatistics.h \
TestStatistics.cc UserCommandReader.h UserCommandReader.cc UserCommands.h \
UserCommands.cc
EXTRA_lbtt_SOURCES = gnu-getopt.h Config-parse.h
lbtt_LDADD = @LIBOBJS@ @READLINELIBS@

lbtt_translate_SOURCES = \
  Alloc.h \
  BitArray.h \
  BitArray.cc \
  Exception.h \
  ExternalTranslator.h \
  ExternalTranslator.cc \
  FormulaWriter.h \
  LbtWrapper.h \
  LtlFormula.h \
  LtlFormula.cc \
  NeverClaim-parse.yy \
  NeverClaim-lex.ll \
  NeverClaimAutomaton.h \
  NeverClaimAutomaton.cc \
  SpinWrapper.h \
  SpinWrapper.cc \
  SpotWrapper.h \
  SpotWrapper.cc \
  StringUtil.h \
  StringUtil.cc \
  translate.h \
  translate.cc \
  TranslatorInterface.h

EXTRA_lbtt_translate_SOURCES = gnu-getopt.h NeverClaim-parse.h
lbtt_translate_LDADD = @LIBOBJS@
