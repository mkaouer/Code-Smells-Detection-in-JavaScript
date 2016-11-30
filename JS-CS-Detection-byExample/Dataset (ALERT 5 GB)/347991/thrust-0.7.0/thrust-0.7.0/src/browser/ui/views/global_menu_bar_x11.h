// Copyright (c) 2014 Stanislas Polu.
// Copyright (c) 2014 GitHub, Inc. All rights reserved.
// See the LICENSE file.

#ifndef THRUST_SHELL_BROWSER_UI_VIEWS_GLOBAL_MENU_BAR_X11_H_
#define THRUST_SHELL_BROWSER_UI_VIEWS_GLOBAL_MENU_BAR_X11_H_

#include <string>

#include "base/basictypes.h"
#include "base/compiler_specific.h"
#include "ui/base/glib/glib_signal.h"
#include "ui/gfx/native_widget_types.h"

typedef struct _DbusmenuMenuitem DbusmenuMenuitem;
typedef struct _DbusmenuServer   DbusmenuServer;

namespace ui {
class Accelerator;
class MenuModel;
}

namespace thrust_shell {

class ThrustWindow;

// Controls the Mac style menu bar on Unity.
//
// Unity has an Apple-like menu bar at the top of the screen that changes
// depending on the active window. In the GTK port, we had a hidden GtkMenuBar
// object in each GtkWindow which existed only to be scrapped by the
// libdbusmenu-gtk code. Since we don't have GtkWindows anymore, we need to
// interface directly with the lower level libdbusmenu-glib, which we
// opportunistically dlopen() since not everyone is running Ubuntu.
//
// This class is like the chrome's corresponding one, but it generates the menu
// from menu models instead, and it is also per-window specific.
class GlobalMenuBarX11 {
 public:
  explicit GlobalMenuBarX11(ThrustWindow* window);
  virtual ~GlobalMenuBarX11();

  // Creates the object path for DbusemenuServer which is attached to |xid|.
  static std::string GetPathForWindow(gfx::AcceleratedWidget xid);

  void SetMenu(ui::MenuModel* menu_model);
  bool IsServerStarted() const;

 private:
  // Creates a DbusmenuServer.
  void InitServer(gfx::AcceleratedWidget xid);

  // Create a menu from menu model.
  void BuildMenuFromModel(ui::MenuModel* model, DbusmenuMenuitem* parent);

  // Sets the accelerator for |item|.
  void RegisterAccelerator(DbusmenuMenuitem* item,
                           const ui::Accelerator& accelerator);

  CHROMEG_CALLBACK_1(GlobalMenuBarX11, void, OnItemActivated, DbusmenuMenuitem*,
                     unsigned int);
  CHROMEG_CALLBACK_0(GlobalMenuBarX11, void, OnSubMenuShow, DbusmenuMenuitem*);

  ThrustWindow*          window_;
  gfx::AcceleratedWidget xid_;

  DbusmenuServer*        server_;

  DISALLOW_COPY_AND_ASSIGN(GlobalMenuBarX11);
};

}  // namespace thrust_shell

#endif  // THRUST_SHELL_BROWSER_UI_VIEWS_GLOBAL_MENU_BAR_X11_H_
