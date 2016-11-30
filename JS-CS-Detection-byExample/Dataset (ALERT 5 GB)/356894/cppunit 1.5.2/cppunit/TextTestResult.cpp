#include "TextTestResult.h"
#include "Exception.h"
#include "estring.h"

using namespace std;
using namespace CppUnit;



std::ostream& 
CppUnit::operator<< (std::ostream& stream, TextTestResult& result)
{ 
  result.print (stream); return stream; 
}

void 
TextTestResult::addError (Test *test, Exception *e)
{
    TestResult::addError (test, e);
    cerr << "E\n";

}

void 
TextTestResult::addFailure (Test *test, Exception *e)
{
    TestResult::addFailure (test, e);
    cerr << "F\n";

}

void 
TextTestResult::startTest (Test *test)
{
    TestResult::startTest (test);
    cerr << ".";

}


void 
TextTestResult::printErrors (ostream& stream)
{
    if (testErrors () != 0) {

        if (testErrors () == 1)
            stream << "There was " << testErrors () << " error: " << endl;
        else
            stream << "There were " << testErrors () << " errors: " << endl;

        int i = 1;

        for (vector<TestFailure *>::iterator it = errors ().begin (); it != errors ().end (); ++it) {
            TestFailure             *failure    = *it;
            Exception        *e          = failure->thrownException ();

            stream << i 
                   << ") "
                   << "line: " << (e ? estring (e->lineNumber ()) : "") << " "
                   << (e ? e->fileName () : "") << " "
                   << "\"" << failure->thrownException ()->what () << "\""
                   << endl;
            i++;
        }
    }

}

void 
TextTestResult::printFailures (ostream& stream) 
{
    if (testFailures () != 0) {
        if (testFailures () == 1)
            stream << "There was " << testFailures () << " failure: " << endl;
        else
            stream << "There were " << testFailures () << " failures: " << endl;

        int i = 1;

        for (vector<TestFailure *>::iterator it = failures ().begin (); it != failures ().end (); ++it) {
            TestFailure             *failure    = *it;
            Exception        *e          = failure->thrownException ();

            stream << i 
                   << ") "
                   << "line: " << (e ? estring (e->lineNumber ()) : "") << " "
                   << (e ? e->fileName () : "") << " "
                   << "\"" << failure->thrownException ()->what () << "\""
                   << endl;
            i++;
        }
    }

}


void 
TextTestResult::print (ostream& stream) 
{
    printHeader (stream);
    printErrors (stream);
    printFailures (stream);

}


void 
TextTestResult::printHeader (ostream& stream)
{
    if (wasSuccessful ())
        cout << endl << "OK (" << runTests () << " tests)" << endl;
    else
        cout << endl
             << "!!!FAILURES!!!" << endl
             << "Test Results:" << endl
             << "Run:  "
             << runTests ()
             << "   Failures: "
             << testFailures ()
             << "   Errors: "
             << testErrors ()
             << endl;

}
