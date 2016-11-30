@class FSDataSource;

@interface FSOutlineViewDelegate : NSObject
@property (nonatomic) NSOutlineView* outlineView;
@property (nonatomic) FSDataSource* dataSource;
@property (nonatomic) NSArray* openURLs;
@property (nonatomic) NSArray* modifiedURLs;

- (void)selectURLs:(NSArray*)someURLs expandChildren:(BOOL)expandAncestors;
- (void)editURL:(NSURL*)anURL;
- (void)scrollToOffset:(CGFloat)anOffset;
@end
