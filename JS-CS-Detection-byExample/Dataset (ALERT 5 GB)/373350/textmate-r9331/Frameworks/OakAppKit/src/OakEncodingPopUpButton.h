#import <oak/misc.h>

PUBLIC @interface OakEncodingPopUpButton : NSPopUpButton
{
	NSArray* availableEncodings;
	NSString* encoding;
	NSMenuItem* firstMenuItem;
}
@property (nonatomic, retain) NSString* encoding;
@end
