#import "OakSavePanel.h"
#import "OakEncodingPopUpButton.h"
#import "NSSavePanel Additions.h"
#import <OakFoundation/OakStringListTransformer.h>
#import <oak/oak.h>
#import <ns/ns.h>

@interface OakEncodingSaveOptionsViewController : NSViewController
{
	IBOutlet OakEncodingPopUpButton* encodingPopUpButton;

	encoding::type encodingOptions;
}
@property (nonatomic, retain) NSString* lineEndings;
@property (nonatomic, retain) NSString* encoding;
@property (nonatomic, assign) BOOL useByteOrderMark;
@property (nonatomic, readonly) BOOL canUseByteOrderMark;
@property (nonatomic, readonly) encoding::type const& encodingOptions;
@end

@implementation OakEncodingSaveOptionsViewController
@synthesize encodingOptions;

+ (NSSet*)keyPathsForValuesAffectingCanUseByteOrderMark { return [NSSet setWithObject:@"encoding"]; }
+ (NSSet*)keyPathsForValuesAffectingUseByteOrderMark    { return [NSSet setWithObject:@"encoding"]; }

+ (void)initialize
{
	[OakStringListTransformer createTransformerWithName:@"OakLineEndingsTransformer" andObjectsArray:@[ @"\n", @"\r", @"\r\n" ]];
	[OakStringListTransformer createTransformerWithName:@"OakLineEndingsListTransformer" andObjectsArray:@[ @"\n", @"\r", @"\r\n" ]];
}

- (id)initWithEncodingOptions:(encoding::type const&)someEncodingOptions
{
	if(self = [super initWithNibName:@"EncodingSaveOptions" bundle:[NSBundle bundleForClass:[self class]]])
		encodingOptions = someEncodingOptions;
	return self;
}

- (void)dealloc
{
	[self unbind:@"encoding"];
	[super dealloc];
}

- (void)loadView
{
	[super loadView];
	encodingPopUpButton.encoding = self.encoding;
	[self bind:@"encoding" toObject:encodingPopUpButton withKeyPath:@"encoding" options:nil];
}

- (BOOL)canUseByteOrderMark { return encodingOptions.supports_byte_order_mark(encodingOptions.charset()); }

- (NSString*)lineEndings    { return [NSString stringWithCxxString:encodingOptions.newlines()]; }
- (NSString*)encoding       { return [NSString stringWithCxxString:encodingOptions.charset()]; }
- (BOOL)useByteOrderMark    { return encodingOptions.byte_order_mark(); }

- (void)setLineEndings:(NSString*)newLineEndings      { encodingOptions.set_newlines(to_s(newLineEndings)); }
- (void)setEncoding:(NSString*)newEncoding            { encodingOptions.set_charset(to_s(newEncoding)); }
- (void)setUseByteOrderMark:(BOOL)newUseByteOrderMark { encodingOptions.set_byte_order_mark(newUseByteOrderMark); }
@end

@implementation OakSavePanel
- (id)initWithPath:(NSString*)aPathSuggestion directory:(NSString*)aDirectorySuggestion fowWindow:(NSWindow*)aWindow delegate:(id)aDelegate encoding:(encoding::type const&)encoding
{
	if((self = [super init]))
	{
		optionsViewController = [[OakEncodingSaveOptionsViewController alloc] initWithEncodingOptions:encoding];
		if(!optionsViewController)
		{
			[self release];
			return nil;
		}

		[[aWindow attachedSheet] orderOut:self]; // incase there already is a sheet showing (like “Do you want to save?”)

		NSSavePanel* savePanel = [NSSavePanel savePanel];
		[savePanel setTreatsFilePackagesAsDirectories:YES];
		if(aDirectorySuggestion)
			[savePanel setDirectoryURL:[NSURL fileURLWithPath:aDirectorySuggestion]];
		[savePanel setNameFieldStringValue:[aPathSuggestion lastPathComponent]];
		[savePanel setAccessoryView:optionsViewController.view];
		[savePanel beginSheetModalForWindow:aWindow completionHandler:^(NSInteger result) {
			NSString* path = result == NSOKButton ? [[savePanel.URL filePathURL] path] : nil;
			[aDelegate savePanelDidEnd:self path:path encoding:optionsViewController.encodingOptions];
			[self release];
		}];
		[savePanel deselectExtension];
	}
	return self;
}

- (void)dealloc
{
	[optionsViewController release];
	[super dealloc];
}

+ (void)showWithPath:(NSString*)aPathSuggestion directory:(NSString*)aDirectorySuggestion fowWindow:(NSWindow*)aWindow delegate:(id)aDelegate encoding:(encoding::type const&)encoding
{
	[[OakSavePanel alloc] initWithPath:aPathSuggestion directory:aDirectorySuggestion fowWindow:aWindow delegate:aDelegate encoding:encoding];
}
@end
