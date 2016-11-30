#pragma warning( disable : 4876 )

#define _WINSOCK2API_

#include "JVM.h"
#include "Conversions.h"
#include "org_quickfix_SocketAcceptor.h"
#include "JavaApplication.h"
#include "JavaMessageStoreFactory.h"
#include "quickfix/include/SocketAcceptor.h"
#include "quickfix/include/Settings.h"
#include "quickfix/include/Utility.h"
#include <sstream>

FIX::SocketAcceptor* getCPPSocketAcceptor(jobject obj)
{
  JVMObject jobject(obj);
  return (FIX::SocketAcceptor*)jobject.getInt("cppPointer");
}

JavaApplication& createAcceptorApplication(JVMObject& obj)
{
  return *new JavaApplication(
      obj.getObject("application", "Lorg/quickfix/Application;"),
      obj.getObject("messageFactory", "Lorg/quickfix/MessageFactory;"));
}

JavaMessageStoreFactory& createAcceptorFactory(JVMObject& obj)
{
  JVMObject jmessageStoreFactory = obj.getObject("messageStoreFactory", 
    "Lorg/quickfix/MessageStoreFactory;");
  return *(new JavaMessageStoreFactory(jmessageStoreFactory));
}

FIX::SessionSettings& getAcceptorSettings(JVMObject& obj)
{
  JVMObject jsettings = obj.getObject("settings", "Lorg/quickfix/Settings;");
  FIX::SessionSettings* pSettings 
    = (FIX::SessionSettings*)jsettings.getInt("cppPointer");
  return *pSettings;
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_create
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  try
  {
    FIX::Acceptor* pAcceptor = new FIX::SocketAcceptor(
      createAcceptorApplication(jobject), createAcceptorFactory(jobject), 
      getAcceptorSettings(jobject));
      jobject.setInt("cppPointer", (int)pAcceptor);
  } catch(FIX::ConfigError& e) { throwNew("Lorg/quickfix/ConfigError;", e.what()); }
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_destroy
(JNIEnv *pEnv, jobject obj)
{
  delete &getCPPSocketAcceptor(obj)->getApplication();
  delete &getCPPSocketAcceptor(obj)->getMessageStoreFactory();
  delete getCPPSocketAcceptor(obj);
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_doStart
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  
  try
  {
    getCPPSocketAcceptor(obj)->start();
  }
  catch(FIX::ConfigError& e)
  {
    throwNew("Lorg/quickfix/ConfigError;", e.what());
  }
}

JNIEXPORT void JNICALL Java_org_quickfix_SocketAcceptor_doStop
(JNIEnv *pEnv, jobject obj)
{
  JVM::set(pEnv);
  JVMObject jobject(obj);
  getCPPSocketAcceptor(obj)->start();
}
