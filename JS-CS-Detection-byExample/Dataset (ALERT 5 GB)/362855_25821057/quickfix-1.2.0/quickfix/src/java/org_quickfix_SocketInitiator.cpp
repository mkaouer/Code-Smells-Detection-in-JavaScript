#pragma warning( disable : 4876 )

#define _WINSOCK2API_

#include "JVM.h"
#include "Conversions.h"
#include "org_quickfix_SocketInitiator.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/SocketInitiator.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

FIX::SocketInitiator* getCPPSocketInitiator(jobject obj)
{
  JVMObject jobject(obj);
  return (FIX::SocketInitiator*)jobject.getInt("cppPointer");
}

JavaApplication& createInitiatorApplication(JVMObject& obj)
{
  return *new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;"));
}

JavaMessageStoreFactory& createInitiatorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getInitiatorSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_create
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  try
  {
    FIX::Initiator* pInitiator = new FIX::SocketInitiator(
      createInitiatorApplication(jobject), createInitiatorFactory(jobject), 
      getInitiatorSettings(jobject));
      jobject.setInt("cppPointer", (int)pInitiator);
  } catch(FIX::ConfigError& e) { throwNew("Lorg/quickfix/ConfigError;", e.what()); }
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_destroy
(JNIEnv *pEnv, jobject obj)
{
  delete &getCPPSocketInitiator(obj)->getApplication();
  delete &getCPPSocketInitiator(obj)->getMessageStoreFactory();
  delete getCPPSocketInitiator(obj);
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    getCPPSocketInitiator(obj)->start();
  }
  catch(FIX::ConfigError& e)
  {
    throwNew("Lorg/quickfix/ConfigError;", e.what());
  }
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketInitiator_doStop
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  getCPPSocketInitiator(obj)->start();
}
