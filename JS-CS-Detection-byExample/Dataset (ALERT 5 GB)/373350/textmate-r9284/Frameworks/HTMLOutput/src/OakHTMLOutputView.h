#import <oak/debug.h>
#import "browser/HOBrowserView.h"

@class HOAutoScroll;

@interface OakHTMLOutputView : HOBrowserView
{
	HOAutoScroll* autoScrollHelper;
	std::map<std::string, std::string> environment;
	BOOL runningCommand;
}
@property (nonatomic, readonly) BOOL runningCommand;
@property (nonatomic, readonly) WebView* webView;
@end
