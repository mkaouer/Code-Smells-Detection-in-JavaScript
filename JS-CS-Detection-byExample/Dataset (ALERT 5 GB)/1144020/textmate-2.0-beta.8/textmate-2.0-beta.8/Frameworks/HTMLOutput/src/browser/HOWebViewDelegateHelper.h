@protocol HOWebViewDelegateHelperProtocol
@property (nonatomic) NSString* statusText;
@end

@interface HOWebViewDelegateHelper : NSObject
@property (nonatomic, weak) id /*<HOWebViewDelegateHelperProtocol>*/ delegate;
@property (nonatomic) BOOL needsNewWebView;
@end
