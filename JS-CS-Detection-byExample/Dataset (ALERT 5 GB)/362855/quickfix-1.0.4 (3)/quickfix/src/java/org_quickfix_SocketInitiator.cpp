#pragma warning( disable : 4876 )

#include "JVM.h"
#include "org_quickfix_SocketInitiator.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/SocketInitiator.h"
#include <sstream>
#include "quickfix/src/C++/Settings.h"
#include "quickfix/src/C++/SocketInitiator.h"
#include "quickfix/src/C++/Utility.h"

static JavaApplication* application;
static JavaMessageStoreFactory* factory;
static FIX::SocketInitiator* initiator;

JavaApplication& createApplication(JVMObject& obj)
{
  return *(application =
    new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;")));
}

JavaMessageStoreFactory& createFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(factory = new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    if(initiator != 0) throw JVMException("initiator already created");
    initiator = new FIX::SocketInitiator(
      createApplication(jobject), createFactory(jobject), getSettings(jobject));
    initiator->start();
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

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_doStop
(JNIEnv *pEnv, jobject obj)
{
  initiator->stop();
  delete initiator;
}
