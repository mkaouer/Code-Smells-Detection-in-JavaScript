BUILT_SOURCES = Config-parse.c NeverClaim-parse.c Ltl-parse.c
AM_YFLAGS = -d

bin_PROGRAMS = lbtt lbtt-translate
lbtt_SOURCES = \
  BitArray.h \
  Bitset.h \
  BitArray.cc \
  BuchiAutomaton.h \
  BuchiAutomaton.cc \
  BuchiProduct.h \
  BuchiProduct.cc \
  Config-parse_.cc \
  Config-lex.ll \
  Configuration.h \
  Configuration.cc \
  DispUtil.h \
  DispUtil.cc \
  EdgeContainer.h \
  Exception.h \
  FormulaRandomizer.h \
  FormulaRandomizer.cc \
  FormulaWriter.h \
  IntervalList.h \
  IntervalList.cc \
  LbttAlloc.h \
  LtlFormula.h \
  LtlFormula.cc \
  Ltl-parse_.cc \
  main.cc \
  PathEvaluator.h \
  PathEvaluator.cc \
  PathIterator.h \
  PathIterator.cc \
  Product.h \
  Random.h \
  SccCollection.h \
  SharedTestData.h \
  StatDisplay.h \
  StatDisplay.cc \
  StateSpace.h \
  StateSpace.cc \
  StateSpaceProduct.h \
  StateSpaceRandomizer.h \
  StateSpaceRandomizer.cc \
  StringUtil.h \
  StringUtil.cc \
  TempFsysName.h \
  TempFsysName.cc \
  TestOperations.h \
  TestOperations.cc \
  TestRoundInfo.h \
  TestStatistics.h \
  TestStatistics.cc \
  UserCommandReader.h \
  UserCommandReader.cc \
  UserCommands.h \
  UserCommands.cc
EXTRA_lbtt_SOURCES = gnu-getopt.h Config-parse.y Ltl-parse.y
lbtt_LDADD = @LIBOBJS@ @READLINELIBS@

lbtt_translate_SOURCES = \
  BitArray.h \
  BitArray.cc \
  Exception.h \
  ExternalTranslator.h \
  ExternalTranslator.cc \
  FormulaWriter.h \
  IntervalList.h \
  IntervalList.cc \
  LbttAlloc.h \
  LbtWrapper.h \
  LtlFormula.h \
  LtlFormula.cc \
  Ltl-parse_.cc \
  NeverClaim-parse_.cc \
  NeverClaim-lex.ll \
  NeverClaimAutomaton.h \
  NeverClaimAutomaton.cc \
  SpinWrapper.h \
  SpinWrapper.cc \
  SpotWrapper.h \
  SpotWrapper.cc \
  StringUtil.h \
  StringUtil.cc \
  TempFsysName.h \
  TempFsysName.cc \
  translate.h \
  translate.cc \
  TranslatorInterface.h
EXTRA_lbtt_translate_SOURCES = gnu-getopt.h Ltl-parse.y NeverClaim-parse.y
lbtt_translate_LDADD = @LIBOBJS@
