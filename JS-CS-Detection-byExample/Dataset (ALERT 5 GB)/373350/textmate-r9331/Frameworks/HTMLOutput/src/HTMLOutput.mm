#import "HTMLOutput.h"
#import <OakSystem/process.h>
#import <command/runner.h>
#import <oak/lock.h>
#import <oak/server.h>
#import <oak/debug.h>

NSString* const kCommandRunnerURLScheme = @"x-txmt-command";

namespace
{
	struct runners_t
	{
		runners_t () : _next_key(0) { }

		int add_process (pid_t processId)
		{
			oak::lock_t lock = _lock();

			std::vector<int> toDelete;
			iterate(record, _records)
			{
				if(record->second.done && record->second.stop)
					toDelete.push_back(record->first);
			}

			iterate(key, toDelete)
				_records.erase(*key);

			return _records.insert(std::make_pair(_next_key++, (record_t){ processId, std::string(), false, nil, false })).first->first;
		}

		void output (int key, char const* data, size_t len)
		{
			oak::lock_t lock = _lock();
			if(record_t* record = find(key))
			{
				if(record->protocol)
						[[record->protocol client] URLProtocol:record->protocol didLoadData:[NSData dataWithBytes:data length:len]];
				else	record->buffer.insert(record->buffer.end(), data, data + len);
			}
		}

		void done (int key)
		{
			oak::lock_t lock = _lock();
			if(record_t* record = find(key))
			{
				record->process_id = 0;
				record->done       = true;

				if(record->protocol)
					[[record->protocol client] URLProtocolDidFinishLoading:record->protocol];
			}
		}

		void start (int key, NSURLProtocol* protocol)
		{
			oak::lock_t lock = _lock();

			if(record_t* record = find(key))
			{
				record->protocol = protocol;
				if(!record->buffer.empty())
				{
					[[record->protocol client] URLProtocol:record->protocol didLoadData:[NSData dataWithBytes:record->buffer.data() length:record->buffer.size()]];
					record->buffer.clear();
				}

				if(record->done)
					[[record->protocol client] URLProtocolDidFinishLoading:record->protocol];
			}
		}

		void stop (int key)
		{
			oak::lock_t lock = _lock();
			if(record_t* record = find(key))
			{
				record->protocol = NULL;
				record->stop     = true;

				if(record->process_id)
					oak::kill_process_group_in_background(record->process_id);
			}
		}

	private:
		struct record_t
		{
			pid_t process_id;
			std::string buffer;
			bool done;
			NSURLProtocol* protocol;
			bool stop;
		};

		record_t* find (int key)
		{
			std::map<int, record_t>::iterator it = _records.find(key);
			return it != _records.end() ? &it->second : NULL;
		}

		int _next_key;
		std::map<int, record_t> _records;
		oak::mutex_t _lock;
	};

	runners_t& runners ()
	{
		static runners_t runners;
		return runners;
	}

	struct html_command_callback_t : command::callback_t
	{
		html_command_callback_t (command::runner_ptr runner, int key) : _runner(runner), _key(key) { _runner->add_callback(this);       }
		~html_command_callback_t ()                                                                { _runner->remove_callback(this);    }

		void output (command::runner_ptr runner, char const* data, size_t len)                     { runners().output(_key, data, len); }
		void done (command::runner_ptr runner)                                                     { runners().done(_key); delete this; }

	private:
		command::runner_ptr _runner;
		int _key;
	};
}

@interface CommandRunnerURLProtocol : NSURLProtocol
{
	int key;
}
@end

@implementation CommandRunnerURLProtocol
+ (void)load
{
	[self registerClass:self];
	[WebView registerURLSchemeAsLocal:kCommandRunnerURLScheme];
}

+ (BOOL)canInitWithRequest:(NSURLRequest*)request                            { return [[[request URL] scheme] isEqualToString:kCommandRunnerURLScheme]; }
+ (NSURLRequest*)canonicalRequestForRequest:(NSURLRequest*)request           { return request; }
+ (BOOL)requestIsCacheEquivalent:(NSURLRequest*)a toRequest:(NSURLRequest*)b { return NO; }

- (id)initWithRequest:(NSURLRequest*)anURLRequest cachedResponse:(NSCachedURLResponse*)aCachedURLResponse client:(id <NSURLProtocolClient>)anId
{
	if(self = [super initWithRequest:anURLRequest cachedResponse:aCachedURLResponse client:anId])
		key = [[[[anURLRequest URL] path] lastPathComponent] intValue];
	return self;
}

// =============================================
// = These methods might be called in a thread =
// =============================================

- (void)startLoading
{
	NSURLResponse* response = [[[NSURLResponse alloc] initWithURL:[[self request] URL] MIMEType:@"text/html" expectedContentLength:-1 textEncodingName:@"utf-8"] autorelease];
	[[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];

	// WebView seems to stall until it has received at least 1024 bytes
	static std::string const dummy("<!--" + std::string(1017, ' ') + "-->");
	[[self client] URLProtocol:self didLoadData:[NSData dataWithBytes:dummy.data() length:dummy.size()]];

	runners().start(key, self);
}

- (void)stopLoading
{
	runners().stop(key);
}
@end

// ==============
// = Public API =
// ==============

NSURLRequest* URLRequestForCommandRunner (command::runner_ptr aRunner)
{
	int key = runners().add_process(aRunner->process_id());
	new html_command_callback_t(aRunner, key);
	return [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@://job/%d", kCommandRunnerURLScheme, key]] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:6000]; // TODO add a description parameter to the URL (based on bundle item name)
}
