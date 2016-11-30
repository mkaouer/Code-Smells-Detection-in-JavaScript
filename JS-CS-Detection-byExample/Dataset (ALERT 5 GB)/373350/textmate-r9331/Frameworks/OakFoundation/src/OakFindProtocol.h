#import <regexp/find.h> // for find::options_t
#import <text/types.h>

enum find_operation_t {
	kFindOperationCount,
	kFindOperationCountInSelection,
	kFindOperationFind,
	kFindOperationFindInSelection,
	kFindOperationReplace,
	kFindOperationReplaceInSelection,
};

@protocol OakFindServerProtocol
@property (nonatomic, readonly) find_operation_t findOperation;
@property (nonatomic, readonly) NSString* findString;
@property (nonatomic, readonly) NSString* replaceString;
@property (nonatomic, readonly) find::options_t findOptions;

- (void)didFind:(NSUInteger)aNumber occurrencesOf:(NSString*)aFindString atPosition:(text::pos_t const&)aPosition;
- (void)didReplace:(NSUInteger)aNumber occurrencesOf:(NSString*)aFindString with:(NSString*)aReplacementString;
@end

@protocol OakFindClientProtocol
- (void)performFindOperation:(id <OakFindServerProtocol>)aFindServer;
@end
