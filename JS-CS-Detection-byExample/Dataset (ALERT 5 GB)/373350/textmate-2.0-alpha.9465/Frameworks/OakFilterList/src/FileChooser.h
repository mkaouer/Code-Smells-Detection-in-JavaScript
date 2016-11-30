#import <document/document.h>
#import <scm/scm.h>

PUBLIC @interface FileChooser : NSObject
@property (nonatomic) NSWindow* window;

@property (nonatomic) NSString* path;
@property (nonatomic) std::vector<document::document_ptr> const& openDocuments;
@property (nonatomic) oak::uuid_t const& currentDocument;

@property (nonatomic) BOOL onlyShowOpenDocuments;

@property (nonatomic) SEL action;
@property (nonatomic, weak) id target;
@property (nonatomic) BOOL allowsMultipleSelection;

@property (nonatomic) NSString* filterString;
@property (nonatomic, readonly) NSArray* selectedItems;

+ (FileChooser*)sharedInstance;
- (void)showWindow:(id)sender;
- (void)close;
@end
