@class FSDataSource;

enum FSItemURLType { FSItemURLTypeUnknown = 0, FSItemURLTypeFile, FSItemURLTypeFolder, FSItemURLTypePackage, FSItemURLTypeAlias, FSItemURLTypeMissing };

@interface FSItem : NSObject
{
	NSImage* icon;
	NSString* name;
	NSString* toolTip;
	NSInteger labelIndex;
	NSURL* url;
	NSURL* target;
	FSItemURLType urlType;
	NSArray* children;
	BOOL leaf;
	BOOL group;
	BOOL sortAsFolder;
}
@property (nonatomic, retain) NSImage* icon;
@property (nonatomic, retain) NSString* name;
@property (nonatomic, retain) NSString* toolTip;
@property (nonatomic, assign) NSInteger labelIndex;
@property (nonatomic, retain) NSURL* url;
@property (nonatomic, assign) FSItemURLType urlType;
@property (nonatomic, retain) NSURL* target;
@property (nonatomic, retain) NSArray* children;
@property (nonatomic, assign) BOOL leaf;
@property (nonatomic, assign) BOOL group;
@property (nonatomic, assign) BOOL sortAsFolder;

@property (nonatomic, readonly) NSString* path; // legacy

- (FSItem*)initWithURL:(NSURL*)anURL;
+ (FSItem*)itemWithURL:(NSURL*)anURL;
@end
