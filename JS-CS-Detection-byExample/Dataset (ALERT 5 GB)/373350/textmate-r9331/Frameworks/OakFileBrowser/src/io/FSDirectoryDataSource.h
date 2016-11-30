#import "FSDataSource.h"
#import <io/events.h>
#import <scm/scm.h>

@interface FSDirectoryDataSource : FSDataSource
{
	NSUInteger dataSourceOptions;

	fs::event_callback_t* callback;

	scm::callback_t* scmCallback;
	std::map<std::string, scm::info_ptr> scmDrivers;
	std::map<std::string, size_t> scmReferenceCounts;
}
- (id)initWithURL:(NSURL*)anURL options:(NSUInteger)someOptions;
@end
