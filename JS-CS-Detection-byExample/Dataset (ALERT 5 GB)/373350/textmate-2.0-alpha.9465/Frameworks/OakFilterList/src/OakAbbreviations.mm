#import "OakAbbreviations.h"
#import <OakFoundation/OakFoundation.h>
#import <oak/debug.h>

OAK_DEBUG_VAR(FilterList_Abbreviations);

static NSString* const FCAbbreviationKey		= @"short";
static NSString* const FCExpandedStringKey	= @"long";

@interface OakAbbreviations ()
@property (nonatomic, copy)   NSString* name;
@property (nonatomic, retain) NSMutableArray* bindings;
- (id)initWithName:(NSString*)aName;
@end

@implementation OakAbbreviations
+ (OakAbbreviations*)abbreviationsForName:(NSString*)aName
{
	static NSMutableDictionary* SharedInstances = [NSMutableDictionary new];
	if(!SharedInstances[aName])
		SharedInstances[aName] = [[OakAbbreviations alloc] initWithName:aName];
	return SharedInstances[aName];
}

- (id)initWithName:(NSString*)aName
{
	if(self = [self init])
	{
		self.name     = aName;
		self.bindings = [[[NSUserDefaults standardUserDefaults] arrayForKey:self.name] mutableCopy] ?: [NSMutableArray new];
		D(DBF_FilterList_Abbreviations, bug("%s\n", [[self.bindings description] UTF8String]););
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationWillTerminate:) name:NSApplicationWillTerminateNotification object:NSApp];
	}
	return self;
}

- (void)dealloc
{
	[self applicationWillTerminate:nil];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NSApplicationWillTerminateNotification object:NSApp];
}

- (void)applicationWillTerminate:(NSNotification*)aNotification
{
	D(DBF_FilterList_Abbreviations, bug("%s\n", [[self.bindings description] UTF8String]););
	if([self.bindings count] > 50)
		[self.bindings setArray:[self.bindings subarrayWithRange:NSMakeRange(0, 50)]];
	[[NSUserDefaults standardUserDefaults] setObject:self.bindings forKey:self.name];
}

- (NSArray*)stringsForAbbreviation:(NSString*)anAbbreviation
{
	NSMutableArray* exactMatches  = [NSMutableArray array];
	NSMutableArray* prefixMatches = [NSMutableArray array];

	if(NSIsEmptyString(anAbbreviation))
		return exactMatches;

	for(NSDictionary* binding in self.bindings)
	{
		NSString* abbr = binding[FCAbbreviationKey];
		NSString* path = binding[FCExpandedStringKey];

		if([abbr isEqualToString:anAbbreviation])
			[exactMatches addObject:path];
		else if([abbr hasPrefix:anAbbreviation])
			[prefixMatches addObject:path];
	}

	D(DBF_FilterList_Abbreviations, bug("%s, exact → %s, prefix → %s\n", [anAbbreviation UTF8String], [[exactMatches description] UTF8String], [[prefixMatches description] UTF8String]););

	[exactMatches addObjectsFromArray:prefixMatches];
	return exactMatches;
}

- (void)learnAbbreviation:(NSString*)anAbbreviation forString:(NSString*)aString
{
	D(DBF_FilterList_Abbreviations, bug("%s → %s\n", [anAbbreviation UTF8String], [aString UTF8String]););
	if(NSIsEmptyString(anAbbreviation) || NSIsEmptyString(aString))
		return;

	NSDictionary* dict = @{ FCAbbreviationKey : anAbbreviation, FCExpandedStringKey : aString };
	[self.bindings removeObject:dict];
	[self.bindings insertObject:dict atIndex:0];
}
@end
