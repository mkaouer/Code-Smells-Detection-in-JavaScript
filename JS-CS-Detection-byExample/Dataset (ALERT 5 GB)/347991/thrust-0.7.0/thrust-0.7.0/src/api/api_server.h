// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// See the LICENSE file.

#ifndef THRUST_SHELL_API_API_SERVER_H_
#define THRUST_SHELL_API_API_SERVER_H_

#include "base/memory/ref_counted.h"
#include "base/memory/scoped_ptr.h"

#include "net/socket/stream_listen_socket.h"
#include "net/socket/socket_descriptor.h"
#include "net/socket/unix_domain_socket_posix.h"
#include "base/files/file_path.h"

#include "src/api/api_binding.h"

namespace base {
class Thread;
class Value;
class DictionaryValue;
}

namespace net {
class StreamListenSocketFactory;
class UnixDomainSocket;
}

namespace thrust_shell {

class API;

class APIServer : public net::StreamListenSocket::Delegate,
                  public base::RefCountedThreadSafe<APIServer> {
public:

  /****************************************************************************/
  /* APISERVER::CLIENT */
  /****************************************************************************/
  class Client : public base::RefCountedThreadSafe<APIServer::Client> {
  public:
    Client(APIServer* server, API* api,
           scoped_ptr<net::StreamListenSocket> conn);
    ~Client();

    void ProcessChunk(std::string chunk);

    void ReplyToAction(const unsigned int id, 
                       const std::string& error, 
                       scoped_ptr<base::DictionaryValue> result);
  
    /**************************************************************************/
    /* APISERVER::CLIENT::REMOTE */
    /**************************************************************************/
    class Remote : public APIBindingRemote,
                   public base::RefCountedThreadSafe<APIServer::Client::Remote> {
    public:
      Remote(APIServer::Client* client,
             unsigned int target);

      virtual void InvokeMethod(const std::string method,
                                scoped_ptr<base::DictionaryValue> args,
                                const API::MethodCallback& callback) OVERRIDE;
      virtual void EmitEvent(const std::string type,
                             scoped_ptr<base::DictionaryValue> event) OVERRIDE;

    private:
      APIServer::Client*                      client_;

      unsigned int                            target_;

      DISALLOW_COPY_AND_ASSIGN(Remote);
    };

  private:
    void PerformAction(scoped_ptr<base::DictionaryValue> action);

    void SendReply(const unsigned id,
                   const std::string& error,
                   scoped_ptr<base::DictionaryValue> result);
    void SendInvoke(const API::MethodCallback& callback,
                    unsigned int target,
                    const std::string method,
                    scoped_ptr<base::DictionaryValue> args);
    void SendEvent(unsigned int target,
                   const std::string type,
                   scoped_ptr<base::DictionaryValue> event);
                     

    APIServer*                                         server_;
    API*                                               api_;
    unsigned int                                       action_id_;

    scoped_ptr<net::StreamListenSocket>                conn_;
    std::string                                        acc_;

    std::map<unsigned int, scoped_refptr<Remote> >     remotes_;
    std::map<int, API::MethodCallback >                invokes_;

    DISALLOW_COPY_AND_ASSIGN(Client);
  };

  /****************************************************************************/
  /* PUBLIC INTERFACE */
  /****************************************************************************/
  APIServer(API* api,
            const base::FilePath& socket_path);

  // ### Start
  // 
  // Starts the APIServer server on its own thread listening
  void Start();

  // ### Stop
  //
  // Stops the APIServer and shuts down the server
  void Stop();

  /****************************************************************************/
  /* STREAMLISTENSOCKET::DELEGATE INTERFACE */
  /****************************************************************************/
  virtual void DidAccept(net::StreamListenSocket* server,                          
                         scoped_ptr<net::StreamListenSocket> connection) OVERRIDE;
  virtual void DidRead(net::StreamListenSocket* connection,
                       const char* data,
                       int len) OVERRIDE;
  virtual void DidClose(net::StreamListenSocket* sock) OVERRIDE;

private:
  /****************************************************************************/
  /* PRIVATE INTERFACE */
  /****************************************************************************/
  bool UserCanConnectCallback(uid_t user_id, gid_t group_id);
  void DeleteSocketFile();

  void StartHandlerThread();
  void ResetHandlerThread();
  void StopHandlerThread();
  void ThreadInit();
  void ThreadTearDown();

  void DestroyClient(net::StreamListenSocket* sock);

  /* The thread used by the API handler to run server socket. */
  API*                                                       api_;
  scoped_ptr<base::Thread>                                   thread_;

  const base::FilePath                                       socket_path_;
  scoped_ptr<net::UnixDomainSocket>                          socket_;

  std::map<net::StreamListenSocket*, scoped_refptr<Client> > clients_;

  DISALLOW_COPY_AND_ASSIGN(APIServer);
};

} // namespace thrust_shell

#endif // THRUST_SHELL_API_API_SERVER_H_
