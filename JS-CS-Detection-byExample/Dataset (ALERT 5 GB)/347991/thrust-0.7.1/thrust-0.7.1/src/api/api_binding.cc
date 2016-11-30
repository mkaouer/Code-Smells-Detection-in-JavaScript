// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// See the LICENSE file.

#include <sstream>
#include <string>

#include "base/callback.h"

#include "src/api/api_binding.h"

namespace thrust_shell {

APIBinding::APIBinding(
    const std::string& type,
    const unsigned int id)
  : type_(type),
    id_(id)
{
}

APIBinding::~APIBinding()
{
}

void
APIBinding::InvokeRemoteMethod(
    const std::string& method,
    scoped_ptr<base::DictionaryValue> args,
    const API::MethodCallback& callback)
{
  APIBindingRemote* remote = API::Get()->GetRemote(id_);
  if(remote != NULL) {
    remote->InvokeMethod(method, args.Pass(), callback);
  }
}

void
APIBinding::EmitEvent(
    const std::string& type,
    scoped_ptr<base::DictionaryValue> event)
{
  APIBindingRemote* remote = API::Get()->GetRemote(id_);
  if(remote != NULL) {
    remote->EmitEvent(type, event.Pass());
  }
}

} // namespace thrust_shell
