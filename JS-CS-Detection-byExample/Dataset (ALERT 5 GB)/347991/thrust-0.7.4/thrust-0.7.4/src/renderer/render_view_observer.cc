// Copyright (c) 2014 Stanislas Polu.
// Copyright (c) 2012 The Chromium Authors.
// See the LICENSE file.

#include "src/renderer/render_view_observer.h"

#include "base/command_line.h"
#include "third_party/WebKit/public/web/WebView.h"
#include "third_party/WebKit/public/web/WebFrame.h"
#include "third_party/WebKit/public/web/WebDocument.h"
#include "third_party/WebKit/public/web/WebDraggableRegion.h"
#include "content/public/renderer/render_view.h"
#include "content/public/renderer/render_view_observer.h"

#include "src/common/switches.h"
#include "src/common/messages.h"

using namespace content;

namespace thrust_shell {

ThrustShellRenderViewObserver::ThrustShellRenderViewObserver(
    RenderView* render_view)
    : RenderViewObserver(render_view) 
{
}

bool 
ThrustShellRenderViewObserver::OnMessageReceived(
    const IPC::Message& message) 
{
  bool handled = true;
  /*
  IPC_BEGIN_MESSAGE_MAP(ThrustShellRenderViewObserver, message)
    IPC_MESSAGE_UNHANDLED(handled = false)
  IPC_END_MESSAGE_MAP()
  */
  handled = false;

  return handled;
}

void
ThrustShellRenderViewObserver::DraggableRegionsChanged(
    blink::WebFrame* frame) 
{
  return;
}

}  // namespace thrust_shell
