#import "OakUIConstructionFunctions.h"
#import "NSImage Additions.h"

NSFont* OakStatusBarFont ()
{
	return [NSFont messageFontOfSize:11];
}

NSFont* OakControlFont ()
{
	return [NSFont messageFontOfSize:0];
}

NSTextField* OakCreateLabel (NSString* label, Class cl)
{
	NSTextField* res = [[cl alloc] initWithFrame:NSZeroRect];
	[[res cell] setWraps:NO];
	res.alignment       = NSRightTextAlignment;
	res.bezeled         = NO;
	res.bordered        = NO;
	res.drawsBackground = NO;
	res.editable        = NO;
	res.font            = OakControlFont();
	res.selectable      = NO;
	res.stringValue     = label;
	return res;
}

NSButton* OakCreateCheckBox (NSString* label)
{
	NSButton* res = [[NSButton alloc] initWithFrame:NSZeroRect];
	[res setContentHuggingPriority:NSLayoutPriorityDefaultHigh forOrientation:NSLayoutConstraintOrientationVertical];
	res.buttonType = NSSwitchButton;
	res.font       = OakControlFont();
	res.title      = label;
	return res;
}

NSButton* OakCreateButton (NSString* label, NSBezelStyle bezel)
{
	NSButton* res = [[NSButton alloc] initWithFrame:NSZeroRect];
	[res setContentHuggingPriority:NSLayoutPriorityDefaultHigh forOrientation:NSLayoutConstraintOrientationHorizontal];
	[res setContentHuggingPriority:NSLayoutPriorityDefaultHigh forOrientation:NSLayoutConstraintOrientationVertical];
	res.bezelStyle = bezel;
	res.buttonType = NSMomentaryPushInButton;
	res.font       = OakControlFont();
	res.title      = label;
	return res;
}

NSPopUpButton* OakCreatePopUpButton (BOOL pullsDown, NSString* initialItemTitle, NSObject* accessibilityLabel)
{
	NSPopUpButton* res = [[NSPopUpButton alloc] initWithFrame:NSZeroRect pullsDown:pullsDown];
	res.font = OakControlFont();
	if(initialItemTitle)
		[[res cell] setMenuItem:[[NSMenuItem alloc] initWithTitle:initialItemTitle action:NULL keyEquivalent:@""]];
	OakSetAccessibilityLabel(res, accessibilityLabel);
	return res;
}

NSPopUpButton* OakCreateActionPopUpButton (BOOL bordered)
{
	NSPopUpButton* res = [NSPopUpButton new];
	res.pullsDown = YES;
	if(!(res.bordered = bordered))
		[[res cell] setBackgroundStyle:NSBackgroundStyleRaised];

	NSMenuItem* item = [NSMenuItem new];
	item.title = @"";
	item.image = [NSImage imageNamed:NSImageNameActionTemplate];
	[item.image setSize:NSMakeSize(14, 14)];

	[[res cell] setUsesItemFromMenu:NO];
	[[res cell] setMenuItem:item];
	OakSetAccessibilityLabel(res, @"Actions");

	return res;
}

NSPopUpButton* OakCreateStatusBarPopUpButton (NSString* initialItemTitle, NSObject* accessibilityLabel)
{
	NSPopUpButton* res = OakCreatePopUpButton(NO, initialItemTitle);
	[[res cell] setBackgroundStyle:NSBackgroundStyleRaised];
	res.font     = OakStatusBarFont();
	res.bordered = NO;
	OakSetAccessibilityLabel(res, accessibilityLabel);
	return res;
}

NSComboBox* OakCreateComboBox (NSObject* accessibilityLabel)
{
	NSComboBox* res = [[NSComboBox alloc] initWithFrame:NSZeroRect];
	res.font = OakControlFont();
	OakSetAccessibilityLabel(res, accessibilityLabel);
	return res;
}

// =========================
// = OakBackgroundFillView =
// =========================

@implementation OakBackgroundFillView
{
	id _activeBackgroundValue;
	id _inactiveBackgroundValue;
}

- (void)setupHeaderBackground
{
	self.activeBackgroundGradient   = [[NSGradient alloc] initWithStartingColor:[NSColor colorWithCalibratedWhite:0.915 alpha:1] endingColor:[NSColor colorWithCalibratedWhite:0.760 alpha:1]];
	self.inactiveBackgroundGradient = [[NSGradient alloc] initWithStartingColor:[NSColor colorWithCalibratedWhite:0.915 alpha:1] endingColor:[NSColor colorWithCalibratedWhite:0.915 alpha:1]];
}

- (void)setupStatusBarBackground
{
	self.activeBackgroundGradient   = [[NSGradient alloc] initWithColorsAndLocations:[NSColor colorWithCalibratedWhite:1 alpha:0.68], 0.0, [NSColor colorWithCalibratedWhite:1 alpha:0.5], 0.0416, [NSColor colorWithCalibratedWhite:1 alpha:0], 1.0, nil];
	self.inactiveBackgroundGradient = [[NSGradient alloc] initWithColorsAndLocations:[NSColor colorWithCalibratedWhite:1 alpha:0.68], 0.0, [NSColor colorWithCalibratedWhite:1 alpha:0.5], 0.0416, [NSColor colorWithCalibratedWhite:1 alpha:0], 1.0, nil];
}

- (void)viewWillMoveToWindow:(NSWindow*)newWindow
{
	if(self.window)
	{
		[[NSNotificationCenter defaultCenter] removeObserver:self name:NSWindowDidBecomeMainNotification object:self.window];
		[[NSNotificationCenter defaultCenter] removeObserver:self name:NSWindowDidResignMainNotification object:self.window];
		[[NSNotificationCenter defaultCenter] removeObserver:self name:NSWindowDidBecomeKeyNotification object:self.window];
		[[NSNotificationCenter defaultCenter] removeObserver:self name:NSWindowDidResignKeyNotification object:self.window];
	}

	if(newWindow)
	{
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(windowDidChangeMainOrKey:) name:NSWindowDidBecomeMainNotification object:newWindow];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(windowDidChangeMainOrKey:) name:NSWindowDidResignMainNotification object:newWindow];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(windowDidChangeMainOrKey:) name:NSWindowDidBecomeKeyNotification object:newWindow];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(windowDidChangeMainOrKey:) name:NSWindowDidResignKeyNotification object:newWindow];
	}

	self.active = ([newWindow styleMask] & NSFullScreenWindowMask) || [newWindow isMainWindow] || [newWindow isKeyWindow];
}

- (void)windowDidChangeMainOrKey:(NSNotification*)aNotification
{
	self.active = ([self.window styleMask] & NSFullScreenWindowMask) || [self.window isMainWindow] || [self.window isKeyWindow];
}

- (void)setActive:(BOOL)flag
{
	if(_active == flag)
		return;
	_active = flag;
	self.needsDisplay = YES;
}

- (void)setActiveBackgroundValue:(id)value
{
	if(value == _activeBackgroundValue || [value isEqualTo:_activeBackgroundValue])
		return;
	_activeBackgroundValue = value;
	if(_active)
		self.needsDisplay = YES;
}

- (void)setInactiveBackgroundValue:(id)value
{
	if(value == _inactiveBackgroundValue || [value isEqualTo:_inactiveBackgroundValue])
		return;
	_inactiveBackgroundValue = value;
	if(!_active)
		self.needsDisplay = YES;
}

- (void)setActiveBackgroundColor:(NSColor*)aColor             { self.activeBackgroundValue = aColor;    }
- (void)setActiveBackgroundImage:(NSImage*)anImage            { self.activeBackgroundValue = anImage;   }
- (void)setActiveBackgroundGradient:(NSGradient*)aGradient    { self.activeBackgroundValue = aGradient; }
- (void)setInactiveBackgroundColor:(NSColor*)aColor           { self.inactiveBackgroundValue = aColor;    }
- (void)setInactiveBackgroundImage:(NSImage*)anImage          { self.inactiveBackgroundValue = anImage;   }
- (void)setInactiveBackgroundGradient:(NSGradient*)aGradient  { self.inactiveBackgroundValue = aGradient; }

- (NSColor*)activeBackgroundColor          { return [_activeBackgroundValue isKindOfClass:[NSColor class]]      ? _activeBackgroundValue   : nil; }
- (NSImage*)activeBackgroundImage          { return [_activeBackgroundValue isKindOfClass:[NSImage class]]      ? _activeBackgroundValue   : nil; }
- (NSGradient*)activeBackgroundGradient    { return [_activeBackgroundValue isKindOfClass:[NSGradient class]]   ? _activeBackgroundValue   : nil; }
- (NSColor*)inactiveBackgroundColor        { return [_inactiveBackgroundValue isKindOfClass:[NSColor class]]    ? _inactiveBackgroundValue : nil; }
- (NSImage*)inactiveBackgroundImage        { return [_inactiveBackgroundValue isKindOfClass:[NSImage class]]    ? _inactiveBackgroundValue : nil; }
- (NSGradient*)inactiveBackgroundGradient  { return [_inactiveBackgroundValue isKindOfClass:[NSGradient class]] ? _inactiveBackgroundValue : nil; }

- (BOOL)isOpaque
{
	// When an `NSTextField` with `NSBackgroundStyleRaised` redraws itself in an inactive textured window, it only draws as dimmed if none of its parent views are opaque, so we only return YES here if we fill with a color, as we draw text labels on gradient status bars <rdar://13161778>
	return self.activeBackgroundColor != nil;
}

- (NSSize)intrinsicContentSize
{
	if(NSImage* image = self.activeBackgroundImage ?: self.inactiveBackgroundImage)
			return image.size;
	else	return NSMakeSize(NSViewNoInstrinsicMetric, NSViewNoInstrinsicMetric);
}

- (void)drawRect:(NSRect)aRect
{
	id value = _active || !_inactiveBackgroundValue ? _activeBackgroundValue : _inactiveBackgroundValue;
	if([value isKindOfClass:[NSGradient class]])
	{
		NSGradient* gradient = value;
		[gradient drawInRect:self.bounds angle:270];
	}
	else if([value isKindOfClass:[NSImage class]])
	{
		NSImage* image = value;
		[[NSColor colorWithPatternImage:image] set];
		CGContextRef context = (CGContextRef)[[NSGraphicsContext currentContext] graphicsPort];
		CGAffineTransform affineTransform = CGContextGetCTM(context);
		CGContextSetPatternPhase(context, CGSizeMake(affineTransform.tx, affineTransform.ty));
		NSRectFill(aRect);
	}
	else if([value isKindOfClass:[NSColor class]])
	{
		NSColor* color = value;
		[color set];
		NSRectFill(aRect);
	}
}
@end

OakBackgroundFillView* OakCreateVerticalLine (NSColor* primaryColor, NSColor* secondaryColor)
{
	OakBackgroundFillView* view = [[OakBackgroundFillView alloc] initWithFrame:NSZeroRect];
	view.activeBackgroundColor   = primaryColor;
	view.inactiveBackgroundColor = secondaryColor;
	[view addConstraint:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:1]];
	view.translatesAutoresizingMaskIntoConstraints = NO;
	return view;
}

OakBackgroundFillView* OakCreateHorizontalLine (NSColor* primaryColor, NSColor* secondaryColor)
{
	OakBackgroundFillView* view = [[OakBackgroundFillView alloc] initWithFrame:NSZeroRect];
	view.activeBackgroundColor   = primaryColor;
	view.inactiveBackgroundColor = secondaryColor;
	[view addConstraint:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:1]];
	view.translatesAutoresizingMaskIntoConstraints = NO;
	return view;
}

// =============================

@interface OakDisableAccessibilityImageCell : NSImageCell
@end

@implementation OakDisableAccessibilityImageCell
- (BOOL)accessibilityIsIgnored
{
	return YES;
}
@end

@interface OakDisableAccessibilityImageView : NSImageView
@end

@implementation OakDisableAccessibilityImageView
+ (void)initialize
{
	if(self == OakDisableAccessibilityImageView.class)
	{
		[OakDisableAccessibilityImageView setCellClass:[OakDisableAccessibilityImageCell class]];
	}
}
@end

NSImageView* OakCreateDividerImageView ()
{
	NSImageView* res = [[OakDisableAccessibilityImageView alloc] initWithFrame:NSZeroRect];
	[res setImage:[NSImage imageNamed:@"Divider" inSameBundleAsClass:[OakBackgroundFillView class]]];
	[res setContentHuggingPriority:NSLayoutPriorityRequired forOrientation:NSLayoutConstraintOrientationHorizontal];
	[res setContentCompressionResistancePriority:NSLayoutPriorityDefaultLow forOrientation:NSLayoutConstraintOrientationVertical];
	return res;
}

BOOL OakSetAccessibilityLabel (NSObject* element, NSObject* label)
{
	if(!(element = NSAccessibilityUnignoredDescendant(element)))
		return NO;

	NSString* attribute = NSAccessibilityDescriptionAttribute;
	if(![label isKindOfClass:NSString.class])
	{
		attribute = NSAccessibilityTitleUIElementAttribute;
		if(!(label = NSAccessibilityUnignoredDescendant(label)))
			return NO;
	}

	return [element accessibilitySetOverrideValue:label forAttribute:attribute];
}
