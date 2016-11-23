#pragma warning( disable : 4876 )

#include "JVM.h"
#include "org_quickfix_SocketInitiator.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/ThreadedSocketInitiator.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

static JavaApplication* application;
static JavaMessageStoreFactory* factory;
static FIX::ThreadedSocketInitiator* initiator;

JavaApplication& createInitiatorApplication(JVMObject& obj)
{
  return *(application =
    new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;")));
}

JavaMessageStoreFactory& createInitiatorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(factory = new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getInitiatorSettings(JVMObject& obj)
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
    initiator = new FIX::ThreadedSocketInitiator(
      createInitiatorApplication(jobject), createInitiatorFactory(jobject), 
      getInitiatorSettings(jobject));
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
