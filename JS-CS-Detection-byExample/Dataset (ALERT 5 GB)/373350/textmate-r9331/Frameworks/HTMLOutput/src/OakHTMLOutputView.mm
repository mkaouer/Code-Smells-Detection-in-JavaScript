#import "OakHTMLOutputView.h"
#import "browser/HOStatusBar.h"
#import "helpers/HOAutoScroll.h"
#import "helpers/HOJSBridge.h"
#import <OakFoundation/NSString Additions.h>

extern NSString* const kCommandRunnerURLScheme; // from HTMLOutput.h

@interface OakHTMLOutputView ()
@property (nonatomic, assign) BOOL runningCommand;
@end

@implementation OakHTMLOutputView
@synthesize runningCommand;

- (id)initWithFrame:(NSRect)frame
{
	if(self = [super initWithFrame:frame])
	{
		autoScrollHelper = [HOAutoScroll new];
	}
	return self;
}

- (void)dealloc
{
	[autoScrollHelper release];
	[super dealloc];
}

- (BOOL)isOpaque
{
	return YES;
}

- (void)setEnvironment:(std::map<std::string, std::string> const&)anEnvironment
{
	environment = anEnvironment;

	if(environment.find("TM_PROJECT_UUID") != environment.end())
			self.projectUUID = [NSString stringWithCxxString:environment["TM_PROJECT_UUID"]];
	else	self.projectUUID = nil;
}

- (void)loadRequest:(NSURLRequest*)aRequest autoScrolls:(BOOL)flag
{
	autoScrollHelper.webFrame = flag ? webView.mainFrame.frameView : nil;
	self.runningCommand = [[[aRequest URL] scheme] isEqualToString:kCommandRunnerURLScheme];
	[webView.mainFrame loadRequest:aRequest];
}

- (void)stopLoading
{
	[webView.mainFrame stopLoading];
}

- (void)webView:(WebView*)sender didStartProvisionalLoadForFrame:(WebFrame*)frame
{
	statusBar.isBusy = YES;
	if(NSString* scheme = [[[[[webView mainFrame] provisionalDataSource] request] URL] scheme])
		[self setUpdatesProgress:![scheme isEqualToString:kCommandRunnerURLScheme]];
}

// =================
// = Script object =
// =================

- (void)webView:(WebView*)sender didClearWindowObject:(WebScriptObject*)windowScriptObject forFrame:(WebFrame*)frame
{
	NSString* scheme = [[[[[webView mainFrame] dataSource] request] URL] scheme];
	if([@[ kCommandRunnerURLScheme, @"tm-file", @"file" ] containsObject:scheme])
	{
		HOJSBridge* bridge = [[HOJSBridge new] autorelease];
		[bridge setDelegate:statusBar];
		[bridge setEnvironment:environment];
		[windowScriptObject setValue:bridge forKey:@"TextMate"];
	}
}

- (void)webView:(WebView*)sender didFinishLoadForFrame:(WebFrame*)frame
{
	self.runningCommand = NO;
	if(frame == [sender mainFrame])
		[self webView:sender didClearWindowObject:[frame windowObject] forFrame:frame];
	[super webView:sender didFinishLoadForFrame:frame];
}
@end
