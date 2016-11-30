#ifndef CPPUNIT_TESTREGISTRY_H
#define CPPUNIT_TESTREGISTRY_H


#include <vector>
#include <string>


namespace CppUnit {

  class Test;

  /** This class is used to register tests and testcases.
   *
   *  It implements a registry to place the test cases into.
   *  The test cases can then register themselves.
   *  
   */
  class TestRegistry {
    public:
      static TestRegistry& getRegistry();
      
      ~TestRegistry();
      
      const std::vector<std::string>& getAllTestNames() const;
      const std::vector<Test*>& getAllTests() const;
      std::vector<Test*> getTest(const std::string& name) const;
      void addTest(std::string name, Test* test);
      
    private:
      TestRegistry();
      
  };
  
} // namespace CppUnit
  
#endif // CPPUNIT_TESTREGISTRY_H

