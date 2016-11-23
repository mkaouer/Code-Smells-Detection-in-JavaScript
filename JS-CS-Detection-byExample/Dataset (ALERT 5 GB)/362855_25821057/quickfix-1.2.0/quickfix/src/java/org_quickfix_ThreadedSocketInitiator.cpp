#pragma warning( disable : 4876 )

#define _WINSOCK2API_

#include "JVM.h"
#include "Conversions.h"
#include "org_quickfix_ThreadedSocketInitiator.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/ThreadedSocketInitiator.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

FIX::ThreadedSocketInitiator* getCPPThreadedSocketInitiator(jobject obj)
{
  JVMObject jobject(obj);
  return (FIX::ThreadedSocketInitiator*)jobject.getInt("cppPointer");
}

JavaApplication& createThreadedInitiatorApplication(JVMObject& obj)
{
  return *new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;"));
}

JavaMessageStoreFactory& createThreadedInitiatorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getThreadedInitiatorSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketInitiator_create
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  try
  {
    FIX::Initiator* pInitiator = new FIX::ThreadedSocketInitiator(
      createThreadedInitiatorApplication(jobject), createThreadedInitiatorFactory(jobject), 
      getThreadedInitiatorSettings(jobject));
      jobject.setInt("cppPointer", (int)pInitiator);
  } catch(FIX::ConfigError& e) { throwNew("Lorg/quickfix/ConfigError;", e.what()); }
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketInitiator_destroy
(JNIEnv *pEnv, jobject obj)
{
  delete &getCPPThreadedSocketInitiator(obj)->getApplication();
  delete &getCPPThreadedSocketInitiator(obj)->getMessageStoreFactory();
  delete getCPPThreadedSocketInitiator(obj);
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketInitiator_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    getCPPThreadedSocketInitiator(obj)->start();
  }
  catch(FIX::ConfigError& e)
  {
    throwNew("Lorg/quickfix/ConfigError;", e.what());
  }
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketInitiator_doStop
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  getCPPThreadedSocketInitiator(obj)->start();
}
