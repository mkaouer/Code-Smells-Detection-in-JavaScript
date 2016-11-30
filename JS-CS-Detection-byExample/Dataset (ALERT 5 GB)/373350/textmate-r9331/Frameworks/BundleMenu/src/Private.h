#ifndef BUNDLES_MENU_H_7ZK5UHKQ
#define BUNDLES_MENU_H_7ZK5UHKQ

#import "BundleMenu.h"
#import <oak/misc.h>

void OakAddBundlesToMenu (std::vector<bundles::item_ptr> const& items, bool hasSelection, bool setKeys, NSMenu* aMenu, SEL menuAction, id menuTarget = nil);

#endif /* end of include guard: BUNDLES_MENU_H_7ZK5UHKQ */
