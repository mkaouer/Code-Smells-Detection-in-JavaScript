#import <ns/ns.h>
#import <OakFoundation/NSString Additions.h>
#import <test/cocoa.h>

@interface MyEventView : NSView
{
	NSString* keyString;
}
@property (nonatomic, retain) NSString* keyString;
@end

@implementation MyEventView
@synthesize keyString;

- (id)initWithFrame:(NSRect)aRect
{
	if((self = [super initWithFrame:aRect]))
	{
		self.keyString = @"Press some keys.";
	}
	return self;
}

- (BOOL)acceptsFirstResponder
{
	return YES;
}

- (NSDictionary*)stringAttributes
{
	return @{
		NSFontAttributeName:            [NSFont userFixedPitchFontOfSize:12],
		NSForegroundColorAttributeName: [NSColor blackColor]
	};
}

- (void)keyDown:(NSEvent*)anEvent
{
	NSLog(@"%s %@", sel_getName(_cmd), anEvent);
	std::string eventString = to_s(anEvent);
	std::string glyphString = ns::glyphs_for_event_string(eventString);
	self.keyString = [NSString stringWithCxxString:glyphString + " — " + eventString];
	[self setNeedsDisplay:YES];
}

- (void)drawRect:(NSRect)aRect
{
	NSEraseRect(aRect);
	NSSize size = [keyString sizeWithAttributes:[self stringAttributes]];
	[keyString drawAtPoint:NSMakePoint(NSMidX([self visibleRect]) - size.width / 2, NSMidY([self visibleRect]) - size.height /2 ) withAttributes:[self stringAttributes]];
}
@end

class KeyEventsTests : public CxxTest::TestSuite
{
public:
	void test_key_events ()
	{
		@autoreleasepool {
			OakSetupApplicationWithView([[MyEventView alloc] initWithFrame:NSMakeRect(0, 0, 200, 100)], "key_events");
		}
	}
};
