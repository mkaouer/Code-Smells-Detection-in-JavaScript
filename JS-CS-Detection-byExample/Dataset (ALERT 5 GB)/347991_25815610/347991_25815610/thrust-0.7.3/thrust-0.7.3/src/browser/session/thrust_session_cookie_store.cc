// Copyright (c) 2014 Stanislas Polu.
// See the LICENSE file.

#include "src/browser/session/thrust_session_cookie_store.h"

#include "content/public/browser/browser_thread.h"

#include "src/browser/session/thrust_session.h"
#include "src/api/thrust_session_binding.h"

using namespace content;

namespace thrust_shell {
  
ThrustSessionCookieStore::ThrustSessionCookieStore(
    ThrustSession* parent,
    bool dummy)
: parent_(parent),
  dummy_(dummy),
  op_count_(0) 
{
  LOG(INFO) << "ThrustSesionCookieStore Constructor [" 
            << dummy_ << "]: " << this;
}

ThrustSessionCookieStore::~ThrustSessionCookieStore()
{
  LOG(INFO) << "ThrustSesionCookieStore Destructor: " << this;
}

void 
ThrustSessionCookieStore::Load(
    const LoadedCallback& loaded_callback)
{
  LOG(INFO) << "Load";

  if(dummy_) {
    std::vector<net::CanonicalCookie*> ccs;
    content::BrowserThread::PostTask(
        content::BrowserThread::IO, FROM_HERE,
        base::Bind(loaded_callback, ccs));
  }
  else if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesLoad, parent_->binding_, 
                   loaded_callback));
  }
}

void 
ThrustSessionCookieStore::LoadCookiesForKey(
    const std::string& key,
    const LoadedCallback& loaded_callback)
{
  LOG(INFO) << "LoadCookiesForKey: '" << key << "'";

  if(dummy_) {
    std::vector<net::CanonicalCookie*> ccs;
    content::BrowserThread::PostTask(
        content::BrowserThread::IO, FROM_HERE,
        base::Bind(loaded_callback, ccs));
  }
  else if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesLoadForKey, parent_->binding_, 
                   key, loaded_callback));
  }
}

void 
ThrustSessionCookieStore::Flush(
    const base::Closure& callback)
{
  LOG(INFO) << "Flush";

  if(dummy_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::IO, FROM_HERE, callback);
  }
  else if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesFlush, parent_->binding_, 
                   callback));
  }
}


void 
ThrustSessionCookieStore::AddCookie(
    const net::CanonicalCookie& cc)
{
  if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesAdd, 
                   parent_->binding_, cc, op_count_++));

  }
}

void 
ThrustSessionCookieStore::UpdateCookieAccessTime(
    const net::CanonicalCookie& cc)
{
  if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesUpdateAccessTime, 
                   parent_->binding_, cc, op_count_++));

  }
}

void 
ThrustSessionCookieStore::DeleteCookie(
    const net::CanonicalCookie& cc)
{
  if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesDelete, 
                   parent_->binding_, cc, op_count_++));

  }
}

void 
ThrustSessionCookieStore::SetForceKeepSessionState()
{
  if(parent_ && parent_->binding_) {
    content::BrowserThread::PostTask(
        content::BrowserThread::UI, FROM_HERE,
        base::Bind(&ThrustSessionBinding::CookiesForceKeepSessionState, 
                   parent_->binding_));

  }
}

}  // namespace thrust_shell
