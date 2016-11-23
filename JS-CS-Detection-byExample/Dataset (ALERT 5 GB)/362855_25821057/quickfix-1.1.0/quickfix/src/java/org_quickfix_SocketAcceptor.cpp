#pragma warning( disable : 4876 )

#include "JVM.h"
#include "org_quickfix_SocketAcceptor.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/ThreadedSocketAcceptor.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

static JavaApplication* application;
static JavaMessageStoreFactory* factory;
static FIX::ThreadedSocketAcceptor* acceptor;

JavaApplication& createAcceptorApplication(JVMObject& obj)
{
  return *(application =
    new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;")));
}

JavaMessageStoreFactory& createAcceptorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(factory = new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getAcceptorSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    if(acceptor != 0) throw JVMException("acceptor already created");
    acceptor = new FIX::ThreadedSocketAcceptor(
      createAcceptorApplication(jobject), createAcceptorFactory(jobject), 
      getAcceptorSettings(jobject));
    acceptor->start();
    delete application;
    delete factory;
  }
  catch(FIX::ConfigError& e)
  {
    std::cout << e.what() << std::endl;
  }
  catch(JVMException& e)
  {
    std::cout << e.what() << std::endl;
  }
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_doStop
(JNIEnv *pEnv, jobject obj)
{
  acceptor->stop();
  delete acceptor;
}
