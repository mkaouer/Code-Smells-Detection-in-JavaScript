#import "Private.h"
#import <OakFoundation/NSString Additions.h>
#import <OakAppKit/NSMenuItem Additions.h>
#import <text/ctype.h>
#import <cf/cf.h>
#import <ns/ns.h>
#import <oak/oak.h>

@interface BundlePopupMenuTarget : NSObject
@property (nonatomic, retain) NSString* selectedItemUUID;
@end

@implementation BundlePopupMenuTarget
- (void)performBundleItemWithUUIDStringFrom:(NSMenuItem*)sender
{
	ASSERT([sender isKindOfClass:[NSMenuItem class]]);
	self.selectedItemUUID = [sender representedObject];
}
@end

static std::vector<bundles::item_ptr> filtered_menu (bundles::item_ptr menuItem, std::set<bundles::item_ptr>* includedItems)
{
	std::vector<bundles::item_ptr> res;
	citerate(item, menuItem->menu())
	{
		if((*item)->kind() == bundles::kItemTypeMenuItemSeparator)
		{
			res.push_back(*item);
		}
		else if((*item)->kind() == bundles::kItemTypeMenu)
		{
			res.push_back(bundles::item_t::menu_item_separator());
			std::vector<bundles::item_ptr> const& tmp = filtered_menu(*item, includedItems);
			res.insert(res.end(), tmp.begin(), tmp.end());
			res.push_back(bundles::item_t::menu_item_separator());
		}
		else if(includedItems->find(*item) != includedItems->end())
		{
			res.push_back(*item);
			includedItems->erase(*item);
		}
	}
	return res;
}

void OakAddBundlesToMenu (std::vector<bundles::item_ptr> const& items, bool setKeys, NSMenu* menu, SEL menuAction)
{
	bool onlyGrammars = true;
	iterate(item, items)
		onlyGrammars = onlyGrammars && (*item)->kind() == bundles::kItemTypeGrammar;

	if(onlyGrammars)
	{
		std::multimap<std::string, bundles::item_ptr, text::less_t> ordering;
		iterate(item, items)
			ordering.insert(std::make_pair((*item)->name(), *item));

		iterate(pair, ordering)
		{
			NSMenuItem* menuItem = [menu addItemWithTitle:[NSString stringWithCxxString:pair->first] action:menuAction keyEquivalent:@""];
			[menuItem setRepresentedObject:[NSString stringWithCxxString:pair->second->uuid()]];

			if(setKeys)
			{
				[menuItem setInactiveKeyEquivalentCxxString:key_equivalent(pair->second)];
				[menuItem setTabTriggerCxxString:pair->second->value_for_field(bundles::kFieldTabTrigger)];
			}
		}
	}
	else
	{
		std::multimap<bundles::item_ptr, bundles::item_ptr> byBundle;
		iterate(item, items)
			byBundle.insert(std::make_pair((*item)->bundle(), *item));

		std::multimap<std::string, std::vector<bundles::item_ptr>, text::less_t> menus;
		while(!byBundle.empty())
		{
			bundles::item_ptr bundle = byBundle.begin()->first;
			std::set<bundles::item_ptr> includedItems;
			foreach(pair, byBundle.lower_bound(bundle), byBundle.upper_bound(bundle))
				includedItems.insert(pair->second);
			byBundle.erase(bundle);

			std::vector<bundles::item_ptr> menuItems = filtered_menu(bundle, &includedItems);

			std::multimap<std::string, bundles::item_ptr, text::less_t> ordering;
			iterate(item, includedItems)
				ordering.insert(std::make_pair((*item)->name(), *item));
			std::transform(ordering.begin(), ordering.end(), back_inserter(menuItems), [](std::pair<std::string, bundles::item_ptr> const& p){ return p.second; });

			menus.insert(std::make_pair(bundle->name(), menuItems));
		}

		bool showBundleHeadings = menus.size() > 1;
		iterate(pair, menus)
		{
			if(showBundleHeadings)
				[menu addItemWithTitle:[NSString stringWithCxxString:pair->first] action:NULL keyEquivalent:@""];

			bool suppressSeparator = true;
			bool pendingSeparator  = false;
			iterate(item, pair->second)
			{
				if((*item)->kind() == bundles::kItemTypeMenuItemSeparator)
				{
					if(!suppressSeparator)
						pendingSeparator = true;
				}
				else
				{
					if(pendingSeparator)
						[menu addItem:[NSMenuItem separatorItem]];
					pendingSeparator = false;

					NSMenuItem* menuItem = [menu addItemWithTitle:[NSString stringWithCxxString:(*item)->name()] action:menuAction keyEquivalent:@""];
					[menuItem setRepresentedObject:[NSString stringWithCxxString:(*item)->uuid()]];

					if(setKeys)
					{
						[menuItem setInactiveKeyEquivalentCxxString:key_equivalent(*item)];
						[menuItem setTabTriggerCxxString:(*item)->value_for_field(bundles::kFieldTabTrigger)];
					}

					if(showBundleHeadings)
						[menuItem setIndentationLevel:1];

					suppressSeparator = false;
				}
			}
		}
	}
}

bundles::item_ptr OakShowMenuForBundleItems (std::vector<bundles::item_ptr> const& items, CGPoint const& pos)
{
	if(items.empty())
		return bundles::item_ptr();
	else if(items.size() == 1)
		return items.front();

	NSMenu* menu = [NSMenu new];
	[menu setFont:[NSFont menuFontOfSize:[NSFont smallSystemFontSize]]];
	OakAddBundlesToMenu(items, false, menu, @selector(performBundleItemWithUUIDStringFrom:));
	[menu update];

	char key = 0;
	BundlePopupMenuTarget* menuTarget = [BundlePopupMenuTarget new];
	for(NSMenuItem* menuItem in [menu itemArray])
	{
		if(menuItem.action == @selector(performBundleItemWithUUIDStringFrom:))
		{
			menuItem.target = menuTarget;
			[menuItem setKeyEquivalent:key < 10 ? [NSString stringWithFormat:@"%c", '0' + (++key % 10)] : @""];
			[menuItem setKeyEquivalentModifierMask:0];
		}
	}

	if([menu popUpMenuPositioningItem:nil atLocation:NSPointFromCGPoint(pos) inView:nil])
		return bundles::lookup(to_s(menuTarget.selectedItemUUID));

	return bundles::item_ptr();
}
