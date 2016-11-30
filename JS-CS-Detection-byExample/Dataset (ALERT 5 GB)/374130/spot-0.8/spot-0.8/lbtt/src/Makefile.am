BUILT_SOURCES = Config-parse.h NeverClaim-parse.h
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
  Config-parse.yy \
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
  Ltl-parse.yy \
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
EXTRA_lbtt_SOURCES = gnu-getopt.h
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
  Ltl-parse.yy \
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
  TempFsysName.h \
  TempFsysName.cc \
  translate.h \
  translate.cc \
  TranslatorInterface.h
EXTRA_lbtt_translate_SOURCES = gnu-getopt.h
lbtt_translate_LDADD = @LIBOBJS@
