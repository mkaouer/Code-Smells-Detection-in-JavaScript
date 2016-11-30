// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// Copyright (c) 2013 GitHub, Inc.
// See the LICENSE file.

#include "src/browser/ui/accelerator_util.h"

#include "ui/base/accelerators/accelerator.h"
#import "ui/base/accelerators/platform_accelerator_cocoa.h"
#import "ui/events/keycodes/keyboard_code_conversion_mac.h"

namespace accelerator_util {

void SetPlatformAccelerator(ui::Accelerator* accelerator) {
  unichar character;
  unichar characterIgnoringModifiers;
  ui::MacKeyCodeForWindowsKeyCode(accelerator->key_code(),
                                  0,
                                  &character,
                                  &characterIgnoringModifiers);
  NSString* characters =
      [[[NSString alloc] initWithCharacters:&character length:1] autorelease];

  NSUInteger modifiers =
      (accelerator->IsCtrlDown() ? NSControlKeyMask : 0) |
      (accelerator->IsCmdDown() ? NSCommandKeyMask : 0) |
      (accelerator->IsAltDown() ? NSAlternateKeyMask : 0) |
      (accelerator->IsShiftDown() ? NSShiftKeyMask : 0);

  scoped_ptr<ui::PlatformAccelerator> platform_accelerator(
      new ui::PlatformAcceleratorCocoa(characters, modifiers));
  accelerator->set_platform_accelerator(platform_accelerator.Pass());
}

}  // namespace accelerator_util
