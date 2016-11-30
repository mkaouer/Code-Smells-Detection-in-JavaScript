#import <OakAppKit/OakImageAndTextCell.h>
#import <oak/misc.h>

enum {
	OFBPathInfoCellHitCloseButton = (1 << 12),
};

PUBLIC @interface OFBPathInfoCell : OakImageAndTextCell
@property (nonatomic, assign) NSUInteger labelIndex;

@property (nonatomic, assign) BOOL isOpen;
@property (nonatomic, assign) BOOL isVisible;
@property (nonatomic, assign) BOOL isLoading;
@property (nonatomic, assign) BOOL disableHighlight;

- (NSRect)closeButtonRectInFrame:(NSRect)cellFrame;
@end
