#ifndef I_CPPTEST_TESTXMLFILEOUTPUTDISPLAY_H
#define I_CPPTEST_TESTXMLFILEOUTPUTDISPLAY_H

#include "TestDisplay.h"
#include <fstream>

namespace CPPTest
{
  class TestXMLFileOutputDisplay : public TestDisplay
    {
    public:
      TestXMLFileOutputDisplay(std::string outfile)
        : m_passing(0), m_failing(0), m_total(0), m_outfile(outfile)
        {
          m_stream.open(m_outfile.c_str());
        }

      virtual ~TestXMLFileOutputDisplay()
        {
          m_stream.close();
        }

    private:
      void onRun( const char* name )
        {
          std::cout << name << std::endl;
        }

      void onPass( const TestBase& )
        {
          m_total++;
          m_passing++;
          std::cout << ".";
          std::cout.flush();
        }

      void onFail( const TestBase& t )
        {
          m_total++;
          m_failing++;
          std::cout << "F";
          std::cout.flush();
        }

      void onResults( const Exceptions& exceptions )
        {
          m_stream << "<UnitTestReport NumOfTests=\"" << m_total << "\""
                   << " NumOfErrors=\"" << m_total - m_passing << "\""
                   << ">" << std::endl;

          CPPTest::Exceptions::const_iterator iException;
          for( iException = exceptions.begin();
               iException != exceptions.end(); iException++ )
            {
              if(iException->isError())
                {
                  m_stream
                    << "\t<Error"
                    << " line= \"" << iException->getLine() << "\""
                    << " file= \"" << iException->getFile() << "\""
                    << ">" << std::endl
                    << "\t\t<Test>" << std::endl
                    << "\t\t\t<![CDATA[ " << iException->getTest()
                    << "]]>" << std::endl
                    << "\t\t</Test>" << std::endl
                    << "\t\t<Text>" << std::endl
                    << "\t\t\t<![CDATA[ " << iException->what()
                    << "]]>" << std::endl
                    << "\t\t</Text>" << std::endl
                    << "\t</Error>" << std::endl;
                }
              else
                {
                  m_stream
                    << "\t<Failure "
                    << " line= \"" << iException->getLine() << "\""
                    << " file= \"" << iException->getFile() << "\""
                    << ">" << std::endl
                    << "\t\t<Test>" << std::endl
                    << "\t\t\t<![CDATA[ " << iException->getTest()
                    << "]]>" << std::endl
                    << "\t\t</Test>" << std::endl
                    << "\t\t<Text>" << std::endl
                    << "\t\t\t<![CDATA[ " << "assert("
                    << iException->what() << ")" << "]]>" << std::endl
                    << "\t\t</Text>" << std::endl
                    << "\t</Failure>" << std::endl;
                }
            }
          m_stream << "</UnitTestReport>" << std::endl;
          std::cout << std::endl
                    << "Results saved to: " << m_outfile << std::endl;

        }

      int m_passing;
      int m_failing;
      int m_total;
      std::string m_outfile;
      std::ofstream m_stream;
    };
}

#endif //I_CPPTEST_TESTXMLFILEOUTPUTDISPLAY_H
