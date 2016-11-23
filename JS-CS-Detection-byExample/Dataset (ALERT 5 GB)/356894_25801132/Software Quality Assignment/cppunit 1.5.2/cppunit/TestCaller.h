#ifndef CPPUNIT_TESTCALLER_H
#define CPPUNIT_TESTCALLER_H

#ifndef CPPUNIT_TESTCASE_H
#include "TestCase.h"
#endif

#include <stl.h>

namespace CppUnit {

  /** Provides access to a test case method.
   * A test caller provides access to a test case method 
   * on a test case class.  Test callers are useful when 
   * you want to run an individual test or add it to a 
   * suite.
   * 
   * Here is an example:
   * \code
   * class MathTest : public CppUnit::TestCase {
   *         ...
   *     public:
   *         void         setUp ();
   *         void         tearDown ();
   *
   *         void         testAdd ();
   *         void         testSubtract ();
   * };
   *
   * CppUnit::Test *MathTest::suite () {
   *     CppUnit::TestSuite *suite = new CppUnit::TestSuite;
   *
   *     suite->addTest (new CppUnit::TestCaller<MathTest> ("testAdd", testAdd));
   *     return suite;
   * }
   * \endcode
   *
   * You can use a TestCaller to bind any test method on a TestCase
   * class, as long as it accepts void and returns void.
   * 
   * \see TestCase
   */

  template <class Fixture> 
    class TestCaller : public TestCase
    { 
        typedef void             (Fixture::*TestMethod)();
    
      public:
        TestCaller (std::string name, TestMethod test) :
          TestCase (name), 
          m_fixture (new Fixture (name)), 
          m_test (test)
        {}

      protected:
        void runTest () 
        { 
          (m_fixture.get ()->*m_test)(); 
        }  

        void setUp ()
        { 
          m_fixture.get ()->setUp (); 
        }

        void tearDown ()
        { 
          m_fixture.get ()->tearDown (); 
        }

        std::string toString () const
        { 
          return "TestCaller " + getName(); 
        }

      private: 
        TestCaller (const TestCaller& other); 
        TestCaller& operator= (const TestCaller& other); 

      private:
        std::auto_ptr<Fixture>   m_fixture;
        TestMethod               m_test;

    };

} // namespace CppUnit

#endif // CPPUNIT_TESTCALLER_H
