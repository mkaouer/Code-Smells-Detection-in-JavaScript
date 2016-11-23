#pragma warning( disable : 4876 )

#define _WINSOCK2API_

#include "JVM.h"
#include "Conversions.h"
#include "org_quickfix_ThreadedSocketAcceptor.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/ThreadedSocketAcceptor.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

FIX::ThreadedSocketAcceptor* getCPPThreadedSocketAcceptor(jobject obj)
{
  JVMObject jobject(obj);
  return (FIX::ThreadedSocketAcceptor*)jobject.getInt("cppPointer");
}

JavaApplication& createThreadedAcceptorApplication(JVMObject& obj)
{
  return *new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;"));
}

JavaMessageStoreFactory& createThreadedAcceptorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getThreadedAcceptorSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketAcceptor_create
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  try
  {
    FIX::Acceptor* pAcceptor = new FIX::ThreadedSocketAcceptor(
      createThreadedAcceptorApplication(jobject), createThreadedAcceptorFactory(jobject), 
      getThreadedAcceptorSettings(jobject));
      jobject.setInt("cppPointer", (int)pAcceptor);
  } catch(FIX::ConfigError& e) { throwNew("Lorg/quickfix/ConfigError;", e.what()); }
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketAcceptor_destroy
(JNIEnv *pEnv, jobject obj)
{
  delete &getCPPThreadedSocketAcceptor(obj)->getApplication();
  delete &getCPPThreadedSocketAcceptor(obj)->getMessageStoreFactory();
  delete getCPPThreadedSocketAcceptor(obj);
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketAcceptor_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    getCPPThreadedSocketAcceptor(obj)->start();
  }
  catch(FIX::ConfigError& e)
  {
    throwNew("Lorg/quickfix/ConfigError;", e.what());
  }
}

JNIEXPORT void JNICALL Java_org_quickfix_ThreadedSocketAcceptor_doStop
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  getCPPThreadedSocketAcceptor(obj)->start();
}
