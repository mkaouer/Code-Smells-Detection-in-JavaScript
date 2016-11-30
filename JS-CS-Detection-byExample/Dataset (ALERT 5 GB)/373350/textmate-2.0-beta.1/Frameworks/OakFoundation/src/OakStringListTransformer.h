#import <oak/misc.h>

PUBLIC @interface OakStringListTransformer : NSValueTransformer
+ (void)createTransformerWithName:(NSString*)aName andObjectsArray:(NSArray*)aList;
+ (void)createTransformerWithName:(NSString*)aName andObjects:(id)firstObj, ...;
@end
