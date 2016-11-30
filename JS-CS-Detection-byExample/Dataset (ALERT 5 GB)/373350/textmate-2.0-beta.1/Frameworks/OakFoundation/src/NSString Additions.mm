#import "NSString Additions.h"
#import <io/path.h>
#import <text/utf8.h>

@implementation NSString (Path)
+ (NSString*)stringWithUTF8String:(char const*)aString length:(unsigned)aLength
{
	ASSERT(utf8::is_valid(aString, aString + aLength));
	return [[NSString alloc] initWithBytes:aString length:aLength encoding:NSUTF8StringEncoding];
}

+ (NSString*)stringWithCxxString:(std::string const&)aString
{
	ASSERT(utf8::is_valid(aString.begin(), aString.end()));
	return aString == NULL_STR ? nil : [[NSString alloc] initWithBytes:aString.data() length:aString.size() encoding:NSUTF8StringEncoding];
}

- (BOOL)existsAsPath
{
	return [[NSFileManager defaultManager] fileExistsAtPath:self];
}

- (BOOL)isDirectory
{
	BOOL isDir = NO;
	return [[NSFileManager defaultManager] fileExistsAtPath:self isDirectory:&isDir] && isDir;
}
@end
