// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// See the LICENSE file.

#include "src/browser/thrust_menu.h"

#include "ui/gfx/screen.h"
#include "ui/views/controls/menu/menu_runner.h"

#include "src/browser/thrust_window.h"

using namespace content;

namespace thrust_shell {

void 
ThrustMenu::PlatformPopup(
    ThrustWindow* window) 
{
  gfx::Point cursor = gfx::Screen::GetNativeScreen()->GetCursorScreenPoint();
  views::MenuRunner menu_runner(
      model(),
      views::MenuRunner::CONTEXT_MENU | views::MenuRunner::HAS_MNEMONICS);
  ignore_result(menu_runner.RunMenuAt(
      window->window_.get(),
      NULL,
      gfx::Rect(cursor, gfx::Size()),
      views::MENU_ANCHOR_TOPLEFT,
      ui::MENU_SOURCE_MOUSE));
}

void
ThrustMenu::PlatformCleanup()
{
  if(application_menu_ == this) {
    for (size_t i = 0; i < ThrustWindow::s_instances.size(); ++i) {
      ThrustWindow::s_instances[i]->DetachMenu();
    }
  }
}

// static
void 
ThrustMenu::PlatformSetApplicationMenu(ThrustMenu* menu) {
  for (size_t i = 0; i < ThrustWindow::s_instances.size(); ++i) {
    ThrustWindow::s_instances[i]->AttachMenu(menu->model_.get());
  }
}



} // namespace thrust_shell
