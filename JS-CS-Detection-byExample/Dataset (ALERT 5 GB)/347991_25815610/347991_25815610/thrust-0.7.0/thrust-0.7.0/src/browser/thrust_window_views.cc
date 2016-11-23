// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// See the LICENSE file.

#include "src/browser/thrust_window.h"

#if defined(OS_WIN)
#include <shobjidl.h>
#endif

#if defined(USE_X11)
#include <X11/extensions/XInput2.h>
#include <X11/extensions/Xrandr.h>
#include <X11/Xlib.h>
#endif

#include <string>
#include <vector>

#include "base/file_util.h"
#include "base/threading/thread_restrictions.h"
#include "base/strings/utf_string_conversions.h"
#include "ui/gfx/image/image.h"
#include "ui/gfx/image/image_skia.h"
#include "ui/gfx/image/image_skia_rep.h"
#include "ui/gfx/codec/png_codec.h"
#include "ui/views/background.h"
#include "ui/views/controls/webview/unhandled_keyboard_event_handler.h"
#include "ui/views/controls/webview/webview.h"
#include "ui/views/window/client_view.h"
#include "ui/views/widget/widget.h"
#include "ui/aura/window.h"
#include "ui/aura/window_tree_host.h"
#include "ui/views/background.h"
#include "ui/views/controls/webview/unhandled_keyboard_event_handler.h"
#include "ui/views/controls/webview/webview.h"
#include "ui/views/window/client_view.h"
#include "ui/views/widget/widget.h"
#include "ui/views/layout/fill_layout.h"
#include "content/public/browser/web_contents.h"
#include "vendor/brightray/browser/inspectable_web_contents_view.h"

#include "src/browser/ui/views/menu_bar.h"
#include "src/browser/ui/views/menu_layout.h"

#if defined(USE_X11)
#include "base/environment.h"
#include "base/nix/xdg_util.h"
#include "ui/base/x/x11_util.h"
#include "ui/gfx/x/x11_types.h"
#include "ui/views/window/native_frame_view.h"
#include "chrome/browser/ui/libgtk2ui/unity_service.h"
#include "src/browser/ui/views/global_menu_bar_x11.h"
#include "src/browser/ui/views/frameless_view.h"
#elif defined(OS_WIN)
#include "src/browser/ui/views/win_frame_view.h"
#include "base/win/scoped_comptr.h"
#endif

using namespace content;

namespace thrust_shell {

namespace {

// The menu bar height in pixels.
#if defined(OS_WIN)
const int kMenuBarHeight = 20;
#else
const int kMenuBarHeight = 25;
#endif

#if defined(USE_X11)
bool ShouldUseGlobalMenuBar() {
  // Some DE would pretend to be Unity but don't have global application menu,
  // so we can not trust unity::IsRunning().
  // When Unity's GlobalMenu is running $UBUNTU_MENUPROXY should be set to
  // something like "libappmenu.so" (not 0 or 1)
  scoped_ptr<base::Environment> env(base::Environment::Create());
  std::string name;
  return env && env->GetVar("UBUNTU_MENUPROXY", &name) && name.length() > 1;
}
#endif


class ThrustWindowClientView : public views::ClientView {
 public:
  ThrustWindowClientView(
      views::Widget* widget,
      ThrustWindow* window)
      : views::ClientView(widget, window) 
  {
  }
  virtual ~ThrustWindowClientView() {}

  virtual bool 
  CanClose() OVERRIDE 
  {
    return true;
  }

 private:
  DISALLOW_COPY_AND_ASSIGN(ThrustWindowClientView);
};

}  // namespace

void 
ThrustWindow::PlatformCleanUp() 
{
  window_->RemoveObserver(this);
}

void 
ThrustWindow::PlatformCreateWindow(
    const gfx::Size& size)
{
  window_.reset(new views::Widget());

  LOG(INFO) << "Create Window: " << size.width() << "x" << size.height();

  gfx::Rect bounds(0, 0, size.width(), size.height());
  window_->AddObserver(this);

  views::Widget::InitParams params;
  params.ownership = views::Widget::InitParams::WIDGET_OWNS_NATIVE_WIDGET;
  params.bounds = bounds;
  params.delegate = this;
  params.type = views::Widget::InitParams::TYPE_WINDOW;
  params.remove_standard_frame = !has_frame_;

  window_->Init(params);

  // Add web view.
  SetLayoutManager(new views::FillLayout());
  set_background(views::Background::CreateStandardPanelBackground());
  AddChildView(inspectable_web_contents()->GetView()->GetView());

  window_->CenterWindow(bounds.size());
  Layout();
}

void 
ThrustWindow::PlatformShow() 
{
  window_->Show();
}

void 
ThrustWindow::PlatformClose() 
{
  window_->Close();
}

void 
ThrustWindow::PlatformSetTitle(
    const std::string& title) 
{
  window_->UpdateWindowTitle();
}

void
ThrustWindow::PlatformFocus(bool focus)
{
  if(focus) {
    window_->Activate();
  }
  else {
    window_->Deactivate();
  }
}

void
ThrustWindow::PlatformMaximize()
{
  window_->Maximize();
}

void
ThrustWindow::PlatformUnMaximize()
{
  window_->Restore();
}

void
ThrustWindow::PlatformMinimize()
{
  window_->Minimize();
}

void
ThrustWindow::PlatformRestore()
{
  window_->Restore();
}

gfx::Size
ThrustWindow::PlatformSize()
{
  return window_->GetWindowBoundsInScreen().size();
}

gfx::Size 
ThrustWindow::PlatformContentSize() 
{
  if (!has_frame_)
    return PlatformSize();

  gfx::Size content_size =
      window_->non_client_view()->frame_view()->GetBoundsForClientView().size();
  if (menu_bar_ && menu_bar_visible_)
    content_size.set_height(content_size.height() - kMenuBarHeight);
  return content_size;
}

gfx::Rect
ThrustWindow::ContentBoundsToWindowBounds(
    const gfx::Rect& bounds)
{
  gfx::Rect window_bounds =
      window_->non_client_view()->GetWindowBoundsForClientBounds(bounds);
  if(menu_bar_ && menu_bar_visible_)
    window_bounds.set_height(window_bounds.height() + kMenuBarHeight);
  return window_bounds;
}


void 
ThrustWindow::PlatformSetContentSize(
    int width, int height)
{
  if (!has_frame_) {
    PlatformResize(width, height);
    return;
  }

  gfx::Size size(width, height);
  gfx::Rect bounds = window_->GetWindowBoundsInScreen();
  gfx::Size new_size = 
      ContentBoundsToWindowBounds(gfx::Rect(bounds.origin(), size)).size();
  PlatformResize(size.width(), size.height());
}

gfx::Point
ThrustWindow::PlatformPosition()
{
  return window_->GetWindowBoundsInScreen().origin();
}

void
ThrustWindow::PlatformMove(int x, int y)
{
  gfx::Size size = window_->GetWindowBoundsInScreen().size();
  gfx::Rect bounds(x, y, size.width(), size.height());
  window_->SetBounds(bounds);
}

void
ThrustWindow::PlatformResize(int width, int height)
{
  gfx::Point origin = window_->GetWindowBoundsInScreen().origin();
  gfx::Rect bounds(origin.x(), origin.y(), width, height);
  window_->SetBounds(bounds);
}

gfx::NativeWindow
ThrustWindow::PlatformGetNativeWindow() 
{
  return window_->GetNativeWindow(); 
}

void 
ThrustWindow::OnWidgetActivationChanged(
    views::Widget* widget, 
    bool active) 
{
  if(widget != window_.get())
    return;

  if(active) {
    /* TODO(spoluy): Notify */
  }
  else {
    /* TODO(spoluy): Notify */
  }

  if(active && web_contents()) {
    web_contents()->Focus();
  }
}


void 
ThrustWindow::DeleteDelegate() {
  Close();
}

views::View* 
ThrustWindow::GetInitiallyFocusedView() 
{
  return inspectable_web_contents()->GetView()->GetWebView();
}

bool 
ThrustWindow::CanResize() const 
{
  return true;
}

bool 
ThrustWindow::CanMaximize() const 
{
  return true;
}

base::string16 
ThrustWindow::GetWindowTitle() const 
{
  return base::UTF8ToUTF16(title_);
}

bool 
ThrustWindow::ShouldHandleSystemCommands() const 
{
  return true;
}

gfx::ImageSkia 
ThrustWindow::GetWindowAppIcon() 
{
  return *(icon_.ToImageSkia());
}

gfx::ImageSkia 
ThrustWindow::GetWindowIcon() 
{
  return GetWindowAppIcon();
}

views::Widget* 
ThrustWindow::GetWidget() 
{
  return window_.get();
}

const views::Widget* 
ThrustWindow::GetWidget() const 
{
  return window_.get();
}

views::View* 
ThrustWindow::GetContentsView() 
{
  return this;
}

void 
ThrustWindow::PlatformSetMenu(
    ui::MenuModel* menu_model) 
{
  /* TODO(spolu) Menu accelerators */
  /*
  // Clear previous accelerators.
  views::FocusManager* focus_manager = GetFocusManager();
  accelerator_table_.clear();
  focus_manager->UnregisterAccelerators(this);

  // Register accelerators with focus manager.
  accelerator_util::GenerateAcceleratorTable(&accelerator_table_, menu_model);
  accelerator_util::AcceleratorTable::const_iterator iter;
  for (iter = accelerator_table_.begin();
       iter != accelerator_table_.end();
       ++iter) {
    focus_manager->RegisterAccelerator(
        iter->first, ui::AcceleratorManager::kNormalPriority, this);
  }
  */

#if defined(USE_X11)
  if (!global_menu_bar_ && ShouldUseGlobalMenuBar())
    global_menu_bar_.reset(new GlobalMenuBarX11(this));

  // Use global application menu bar when possible.
  if (global_menu_bar_ && global_menu_bar_->IsServerStarted()) {
    global_menu_bar_->SetMenu(menu_model);
    return;
  }
#endif

  // Do not show menu bar in frameless window.
  if (!has_frame_)
    return;

  if (!menu_bar_) {
    gfx::Size content_size = PlatformContentSize();
    menu_bar_.reset(new MenuBar);
    menu_bar_->set_owned_by_client();

    if (!menu_bar_autohide_) {
      SetMenuBarVisibility(true);
      PlatformSetContentSize(content_size.width(), 
                             content_size.height());
    }
  }

  menu_bar_->SetMenu(menu_model);
  Layout();
}

bool 
ThrustWindow::ShouldDescendIntoChildForEventHandling(
    gfx::NativeView child,
    const gfx::Point& location) 
{
  /*
  // App window should claim mouse events that fall within the draggable region.
  if (draggable_region_ &&
      draggable_region_->contains(location.x(), location.y()))
    return false;

  // And the events on border for dragging resizable frameless window.
  if (!has_frame_ && CanResize()) {
    FramelessView* frame = static_cast<FramelessView*>(
        window_->non_client_view()->frame_view());
    return frame->ResizingBorderHitTest(location) == HTNOWHERE;
  }
  */
  return true;
}

views::ClientView* 
ThrustWindow::CreateClientView(
    views::Widget* widget) 
{
  return new ThrustWindowClientView(widget, this);
}

views::NonClientFrameView* 
ThrustWindow::CreateNonClientFrameView(
    views::Widget* widget) 
{
#if defined(OS_WIN)
  WinFrameView* frame_view =  new WinFrameView;
  frame_view->Init(this, widget);
  return frame_view;
#elif defined(OS_LINUX)
  if(has_frame_) {
    return new views::NativeFrameView(widget);
  } 
  else {
    FramelessView* frame_view =  new FramelessView();
    frame_view->Init(this, widget);
    return frame_view;
  }
#else
  return NULL;
#endif
}

void 
ThrustWindow::SetMenuBarVisibility(
    bool visible) 
{
  if (!menu_bar_)
    return;

  // Always show the accelerator when the auto-hide menu bar shows.
  if (menu_bar_autohide_)
    menu_bar_->SetAcceleratorVisibility(visible);

  menu_bar_visible_ = visible;
  if (visible) {
    DCHECK_EQ(child_count(), 1);
    AddChildView(menu_bar_.get());
  } else {
    DCHECK_EQ(child_count(), 2);
    RemoveChildView(menu_bar_.get());
  }
}


} // namespace thrust_shell
