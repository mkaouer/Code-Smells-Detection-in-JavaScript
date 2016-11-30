#include "TestCase.h"
#include "Exception.h"
#include "TestResult.h"
#include "estring.h"
#include "TestRegistry.h"

#include <typeinfo>
#include <stdexcept>
#include <cmath>

using namespace CppUnit;

/// Create a default TestResult
CppUnit::TestResult* TestCase::defaultResult ()
{ return new TestResult; } 


/// Check for a failed general assertion 
void TestCase::assertImplementation (bool          condition,
  std::string   conditionExpression,
  long          lineNumber,
  std::string   fileName)
{ 
  if (!condition) 
    throw Exception (conditionExpression, lineNumber, fileName); 
}


/// Check for a failed equality assertion 
void TestCase::assertEquals (long        expected, 
  long        actual,
  long        lineNumber,
  std::string fileName)
{ 
  if (expected != actual) 
    assertImplementation (false, notEqualsMessage(expected, actual), lineNumber, fileName); 
}


/// Check for a failed equality assertion
void TestCase::assertEquals (double        expected, 
  double        actual, 
  double        delta,
  long          lineNumber,
  std::string   fileName)
{ 
  if (fabs (expected - actual) > delta) 
    assertImplementation (false, notEqualsMessage(expected, actual), lineNumber, fileName); 

}


/// Run the test and catch any exceptions that are triggered by it 
void TestCase::run (TestResult *result)
{
  result->startTest (this);
  
  setUp ();
  
  try {
    runTest ();
    
  }
  catch (Exception e) {
    Exception *copy = new Exception (e);
    result->addFailure (this, copy);
    
  }
  catch (exception e) {
    result->addError (this, new Exception (e.what ()));
    
  }
  catch (...) {
    Exception *e = new Exception ("unknown exception");
    result->addError (this, e);

  }

  tearDown ();

  result->endTest (this);

}


/// A default run method 
TestResult *TestCase::run ()
{
  TestResult *result = defaultResult ();

  run (result);
  return result;

}


/// All the work for runTest is deferred to subclasses 
void TestCase::runTest ()
{
}


/// Build a message about a failed equality check 
std::string TestCase::notEqualsMessage (long expected, long actual)
{ 
  return "expected: " + estring (expected) + " but was: " + estring (actual); 
}


/// Build a message about a failed equality check 
std::string TestCase::notEqualsMessage (double expected, double actual)
{ 
  return "expected: " + estring (expected) + " but was: " + estring (actual); 
}



/// Constructs a test case
inline TestCase::TestCase (std::string name) 
  : m_name (name) 
{
  if(m_name=="") 
    return;

  TestRegistry::getRegistry().addTest(m_name, this);
  
}


/// Destructs a test case
inline TestCase::~TestCase ()
{}


/// Returns a count of all the tests executed
inline int TestCase::countTestCases ()
{ return 1; }


/// Returns the name of the test case
inline std::string 
  TestCase::getName () const
{ 
  return m_name; 
}


/// A hook for fixture set up
inline void TestCase::setUp ()
{}
  

/// A hook for fixture tear down
inline void TestCase::tearDown ()
{}


/// Returns the name of the test case instance
inline std::string 
  TestCase::toString () const
{ 
  const type_info& thisClass = typeid (*this); 
  return std::string (thisClass.name ()) + "." + getName (); 
}
  
