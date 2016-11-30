

#ifndef CPPUNIT_GUITESTRESULT_H
#define CPPUNIT_GUITESTRESULT_H

#include <afxmt.h>

#ifndef TESTRESULT_H
#include <cppunit/TestResult.h>
#endif

class TestRunnerDlg;



class GUITestResult : public CppUnit::TestResult 
{
public:
                        GUITestResult (TestRunnerDlg *runner);
                        ~GUITestResult ();

    void                addError    (CppUnit::Test *test, CppUnit::Exception *e);
    void                addFailure  (CppUnit::Test *test, CppUnit::Exception *e);

    void                endTest     (CppUnit::Test *test);
    void                stop ();

protected:
    class LightweightSynchronizationObject : public TestResult::SynchronizationObject
    {
        CCriticalSection    m_syncObject;

    public:
        void                lock ()     { m_syncObject.Lock (); }
        void                unlock ()   { m_syncObject.Unlock (); }
    };

private:
    TestRunnerDlg       *m_runner;
};



// Construct with lightweight synchronization
inline GUITestResult::GUITestResult (TestRunnerDlg *runner)
: m_runner (runner) { setSynchronizationObject (new LightweightSynchronizationObject ()); }


// Destructor
inline GUITestResult::~GUITestResult ()
{}


// Override without protection to prevent deadlock
inline void GUITestResult::stop ()
{ m_stop = true; }


#endif
