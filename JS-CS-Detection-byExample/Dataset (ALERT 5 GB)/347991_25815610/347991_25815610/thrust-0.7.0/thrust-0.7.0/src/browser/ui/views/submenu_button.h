// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// Copyright (c) 2014 GitHub, Inc.
// See the LICENSE file.

#ifndef THRUST_SHELL_BROWSER_UI_VIEWS_SUBMENU_BUTTON_H_
#define THRUST_SHELL_BROWSER_UI_VIEWS_SUBMENU_BUTTON_H_

#include "ui/views/controls/button/menu_button.h"

namespace thrust_shell {

// Special button that used by menu bar to show submenus.
class SubmenuButton : public views::MenuButton {
 public:
  SubmenuButton(views::ButtonListener* listener,
                const base::string16& title,
                views::MenuButtonListener* menu_button_listener);
  virtual ~SubmenuButton();

  void SetAcceleratorVisibility(bool visible);
  void SetUnderlineColor(SkColor color);

  void SetEnabledColor(SkColor color);
  void SetBackgroundColor(SkColor color);

  base::char16 accelerator() const { return accelerator_; }

  /****************************************************************************/
  /* MENUBUTTON IMPLEMENTATION */
  /****************************************************************************/
  virtual void OnPaint(gfx::Canvas* canvas) OVERRIDE;

 private:
  bool GetUnderlinePosition(const base::string16& text,
                            base::char16* accelerator,
                            int* start, int* end);
  void GetCharacterPosition(
      const base::string16& text, int index, int* pos);

  base::char16 accelerator_;

  bool         show_underline_;
  int          underline_start_;
  int          underline_end_;
  int          text_width_;
  int          text_height_;
  SkColor      underline_color_;

  DISALLOW_COPY_AND_ASSIGN(SubmenuButton);
};

}  // namespace thrust_shell

#endif  // THRUST_SHELL_BROWSER_UI_VIEWS_SUBMENU_BUTTON_H_
