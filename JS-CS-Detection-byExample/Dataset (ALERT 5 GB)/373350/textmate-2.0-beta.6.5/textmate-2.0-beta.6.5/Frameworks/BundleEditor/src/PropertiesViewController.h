@class OakKeyEquivalentView;

@interface PropertiesViewController : NSViewController
{
	IBOutlet NSObjectController* objectController;
	IBOutlet NSView* alignmentView;
	IBOutlet OakKeyEquivalentView* keyEquivalentView;
	NSMutableDictionary* properties;
}
- (id)initWithName:(NSString*)aName;
@property (nonatomic) NSMutableDictionary* properties;
@property (nonatomic, readonly) CGFloat indent;
@end
