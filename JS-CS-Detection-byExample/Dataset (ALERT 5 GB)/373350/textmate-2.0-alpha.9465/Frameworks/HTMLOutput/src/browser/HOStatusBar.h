#import <OakAppKit/OakGradientView.h>

@protocol HOStatusBarDelegate
- (void)goBack:(id)sender;
- (void)goForward:(id)sender;
@end

@interface HOStatusBar : OakGradientView
@property (nonatomic, weak) id  delegate;

@property (nonatomic) NSString* statusText;
@property (nonatomic) CGFloat   progress;
@property (nonatomic) BOOL      isBusy;
@property (nonatomic) BOOL      canGoBack;
@property (nonatomic) BOOL      canGoForward;
@end
