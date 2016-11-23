// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// Copyright (c) 2014 GitHub, Inc.
// Copyright (c) 2012 The Chromium Authors.
// See the LICENSE file.

#import "src/browser/ui/cocoa/menu_controller.h"

#import "base/logging.h"
#import "base/strings/sys_string_conversions.h"
#import "ui/base/accelerators/accelerator.h"
#import "ui/base/accelerators/platform_accelerator_cocoa.h"
#import "ui/base/l10n/l10n_util_mac.h"
#import "ui/base/models/simple_menu_model.h"
#import "ui/gfx/image/image.h"

namespace {

bool isLeftButtonEvent(NSEvent* event) {
  NSEventType type = [event type];
  return type == NSLeftMouseDown ||
    type == NSLeftMouseDragged ||
    type == NSLeftMouseUp;
}

bool isRightButtonEvent(NSEvent* event) {
  NSEventType type = [event type];
  return type == NSRightMouseDown ||
    type == NSRightMouseDragged ||
    type == NSRightMouseUp;
}

bool isMiddleButtonEvent(NSEvent* event) {
  if ([event buttonNumber] != 2)
    return false;

  NSEventType type = [event type];
  return type == NSOtherMouseDown ||
    type == NSOtherMouseDragged ||
    type == NSOtherMouseUp;
}

int EventFlagsFromNSEventWithModifiers(NSEvent* event, NSUInteger modifiers) {
  int flags = 0;
  flags |= (modifiers & NSAlphaShiftKeyMask) ? ui::EF_CAPS_LOCK_DOWN : 0;
  flags |= (modifiers & NSShiftKeyMask) ? ui::EF_SHIFT_DOWN : 0;
  flags |= (modifiers & NSControlKeyMask) ? ui::EF_CONTROL_DOWN : 0;
  flags |= (modifiers & NSAlternateKeyMask) ? ui::EF_ALT_DOWN : 0;
  flags |= (modifiers & NSCommandKeyMask) ? ui::EF_COMMAND_DOWN : 0;
  flags |= isLeftButtonEvent(event) ? ui::EF_LEFT_MOUSE_BUTTON : 0;
  flags |= isRightButtonEvent(event) ? ui::EF_RIGHT_MOUSE_BUTTON : 0;
  flags |= isMiddleButtonEvent(event) ? ui::EF_MIDDLE_MOUSE_BUTTON : 0;
  return flags;
}

// Retrieves a bitsum of ui::EventFlags from NSEvent.
int EventFlagsFromNSEvent(NSEvent* event) {
  NSUInteger modifiers = [event modifierFlags];
  return EventFlagsFromNSEventWithModifiers(event, modifiers);
}

}  // namespace

@interface ThrustShellMenuController (Private)
- (void)addSeparatorToMenu:(NSMenu*)menu
                   atIndex:(int)index;
@end

@implementation ThrustShellMenuController

@synthesize model = model_;

- (id)init {
  self = [super init];
  return self;
}

- (id)initWithModel:(ui::MenuModel*)model {
  if ((self = [super init])) {
    model_ = model;
    [self menu];
  }
  return self;
}

- (void)dealloc {
  [menu_ setDelegate:nil];

  // Close the menu if it is still open. This could happen if a tab gets closed
  // while its context menu is still open.
  [self cancel];

  model_ = NULL;
  [super dealloc];
}

- (void)cancel {
  if (isMenuOpen_) {
    [menu_ cancelTracking];
    model_->MenuClosed();
    isMenuOpen_ = NO;
  }
}

// Creates a NSMenu from the given model. If the model has submenus, this can
// be invoked recursively.
- (NSMenu*)menuFromModel:(ui::MenuModel*)model {
  NSMenu* menu = [[[NSMenu alloc] initWithTitle:@""] autorelease];

  const int count = model->GetItemCount();
  for (int index = 0; index < count; index++) {
    if (model->GetTypeAt(index) == ui::MenuModel::TYPE_SEPARATOR)
      [self addSeparatorToMenu:menu atIndex:index];
    else
      [self addItemToMenu:menu atIndex:index fromModel:model];
  }

  return menu;
}

// Adds a separator item at the given index. As the separator doesn't need
// anything from the model, this method doesn't need the model index as the
// other method below does.
- (void)addSeparatorToMenu:(NSMenu*)menu
                   atIndex:(int)index {
  NSMenuItem* separator = [NSMenuItem separatorItem];
  [menu insertItem:separator atIndex:index];
}

// Adds an item or a hierarchical menu to the item at the |index|,
// associated with the entry in the model identified by |modelIndex|.
- (void)addItemToMenu:(NSMenu*)menu
              atIndex:(NSInteger)index
            fromModel:(ui::MenuModel*)model {
  base::string16 label16 = model->GetLabelAt(index);
  NSString* label = l10n_util::FixUpWindowsStyleLabel(label16);
  base::scoped_nsobject<NSMenuItem> item(
      [[NSMenuItem alloc] initWithTitle:label
                                 action:@selector(itemSelected:)
                          keyEquivalent:@""]);

  // If the menu item has an icon, set it.
  gfx::Image icon;
  if (model->GetIconAt(index, &icon) && !icon.IsEmpty())
    [item setImage:icon.ToNSImage()];

  ui::MenuModel::ItemType type = model->GetTypeAt(index);
  if (type == ui::MenuModel::TYPE_SUBMENU) {
    // Recursively build a submenu from the sub-model at this index.
    [item setTarget:nil];
    [item setAction:nil];
    ui::MenuModel* submenuModel = model->GetSubmenuModelAt(index);
    NSMenu* submenu =
        [self menuFromModel:(ui::SimpleMenuModel*)submenuModel];
    [submenu setTitle:[item title]];
    [item setSubmenu:submenu];

    // Hack to set window and help menu.
    if ([[item title] isEqualToString:@"Window"] && [submenu numberOfItems] > 0)
      [NSApp setWindowsMenu:submenu];
    else if ([[item title] isEqualToString:@"Help"])
      [NSApp setHelpMenu:submenu];
    if ([[item title] isEqualToString:@"Services"] &&
        [submenu numberOfItems] == 0)
      [NSApp setServicesMenu:submenu];
  } else {
    // The MenuModel works on indexes so we can't just set the command id as the
    // tag like we do in other menus. Also set the represented object to be
    // the model so hierarchical menus check the correct index in the correct
    // model. Setting the target to |self| allows this class to participate
    // in validation of the menu items.
    [item setTag:index];
    [item setTarget:self];
    NSValue* modelObject = [NSValue valueWithPointer:model];
    [item setRepresentedObject:modelObject];  // Retains |modelObject|.
    ui::Accelerator accelerator;
    if (model->GetAcceleratorAt(index, &accelerator)) {
      const ui::PlatformAcceleratorCocoa* platformAccelerator =
          static_cast<const ui::PlatformAcceleratorCocoa*>(
              accelerator.platform_accelerator());
      if (platformAccelerator) {
        [item setKeyEquivalent:platformAccelerator->characters()];
        [item setKeyEquivalentModifierMask:
            platformAccelerator->modifier_mask()];
      }
    }
  }
  [menu insertItem:item atIndex:index];
}

// Called before the menu is to be displayed to update the state (enabled,
// radio, etc) of each item in the menu. Also will update the title if
// the item is marked as "dynamic".
- (BOOL)validateUserInterfaceItem:(id<NSValidatedUserInterfaceItem>)item {
  SEL action = [item action];
  if (action != @selector(itemSelected:))
    return NO;

  NSInteger modelIndex = [item tag];
  ui::MenuModel* model =
      static_cast<ui::MenuModel*>(
          [[(id)item representedObject] pointerValue]);
  DCHECK(model);
  if (model) {
    BOOL checked = model->IsItemCheckedAt(modelIndex);
    DCHECK([(id)item isKindOfClass:[NSMenuItem class]]);
    [(id)item setState:(checked ? NSOnState : NSOffState)];
    [(id)item setHidden:(!model->IsVisibleAt(modelIndex))];
    if (model->IsItemDynamicAt(modelIndex)) {
      // Update the label and the icon.
      NSString* label =
          l10n_util::FixUpWindowsStyleLabel(model->GetLabelAt(modelIndex));
      [(id)item setTitle:label];

      gfx::Image icon;
      model->GetIconAt(modelIndex, &icon);
      [(id)item setImage:icon.IsEmpty() ? nil : icon.ToNSImage()];
    }
    return model->IsEnabledAt(modelIndex);
  }
  return NO;
}

// Called when the user chooses a particular menu item. |sender| is the menu
// item chosen.
- (void)itemSelected:(id)sender {
  NSInteger modelIndex = [sender tag];
  ui::MenuModel* model =
      static_cast<ui::MenuModel*>(
          [[sender representedObject] pointerValue]);
  DCHECK(model);
  if (model) {
    int event_flags = EventFlagsFromNSEvent([NSApp currentEvent]);
    model->ActivatedAt(modelIndex, event_flags);
  }
}

- (NSMenu*)menu {
  if (!menu_ && model_) {
    menu_.reset([[self menuFromModel:model_] retain]);
    [menu_ setDelegate:self];
  }
  return menu_.get();
}

- (BOOL)isMenuOpen {
  return isMenuOpen_;
}

- (void)menuWillOpen:(NSMenu*)menu {
  isMenuOpen_ = YES;
  model_->MenuWillShow();
}

- (void)menuDidClose:(NSMenu*)menu {
  if (isMenuOpen_) {
    model_->MenuClosed();
    isMenuOpen_ = NO;
  }
}

@end
