#ifndef TESTSUITETEST_H
#define TESTSUITETEST_H

#include <cppunit/extensions/HelperMacros.h>


class TestSuiteTest : public CppUnit::TestCase
{
  CPPUNIT_TEST_SUITE( TestSuiteTest );
  CPPUNIT_TEST( testConstructor );
  CPPUNIT_TEST( testCountTestCasesWithNoTest );
  CPPUNIT_TEST( testCountTestCasesWithTwoTests );
  CPPUNIT_TEST( testCountTestCasesWithSubSuite );
  CPPUNIT_TEST( testRunWithOneTest );
  CPPUNIT_TEST( testRunWithOneTestAndSubSuite );
  CPPUNIT_TEST( testGetTests );
  CPPUNIT_TEST( testDeleteContents );
  CPPUNIT_TEST_SUITE_END();

public:
  TestSuiteTest();
  virtual ~TestSuiteTest();

  virtual void setUp();
  virtual void tearDown();

  void testConstructor();

  void testCountTestCasesWithNoTest();
  void testCountTestCasesWithTwoTests();
  void testCountTestCasesWithSubSuite();

  void testRunWithOneTest();
  void testRunWithOneTestAndSubSuite();

  void testGetTests();

  void testDeleteContents();

private:
  TestSuiteTest( const TestSuiteTest &copy );
  void operator =( const TestSuiteTest &copy );

  void checkResult( int failures,
                    int errors,
                    int testsRun );

private:
  CppUnit::TestSuite *m_suite;
  CppUnit::TestResult *m_result;
};



#endif  // TESTSUITETEST_H
