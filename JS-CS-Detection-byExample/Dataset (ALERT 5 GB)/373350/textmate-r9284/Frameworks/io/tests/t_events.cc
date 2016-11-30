#include <test/jail.h>
#include <io/events.h>

class EventsTests : public CxxTest::TestSuite
{
#if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5
	struct callback_t : fs::event_callback_t
	{
		callback_t (test::jail_t const& jail, bool log = false) : _jail(jail), _in_replay(false), _log(log) { }
		void set_replaying_history (bool flag, std::string const& observedPath, uint64_t eventId) { _in_replay = flag; }

		void did_change (std::string const& path, std::string const& observedPath, uint64_t eventId, bool recursive)
		{
			if(_log)
				fprintf(stderr, "\t%s, allowed %s, must see %s\n", path::relative_to(path, _jail.path()).c_str(), BSTR(_allowed.find(path) != _allowed.end()), BSTR(_must_see.find(path) != _must_see.end()));

			TSM_ASSERT(path::relative_to(path, _jail.path()).c_str(), _allowed.find(path) != _allowed.end());

			_must_see.erase(path);
			if(_must_see.empty())
				CFRunLoopStop(CFRunLoopGetCurrent());
		}

		void allowed (char const* firstPath, ...)
		{
			va_list ap;
			va_start(ap, firstPath);
			do {
				_allowed.insert(_jail.path(firstPath));
			} while(firstPath = va_arg(ap, char const*));
			va_end(ap);
		}

		void must_see (char const* firstPath, ...)
		{
			va_list ap;
			va_start(ap, firstPath);
			do {
				_must_see.insert(_jail.path(firstPath));
				_allowed.insert(_jail.path(firstPath));
			} while(firstPath = va_arg(ap, char const*));
			va_end(ap);
		}

		void event_loop (std::string const& observedPath)
		{
			if(_log)
			{
				fprintf(stderr, "observing ‘%s’\n", observedPath.c_str());
				iterate(path, _must_see)
					fprintf(stderr, "\tmust see: %s\n", path::relative_to(*path, _jail.path()).c_str());
				fprintf(stderr, "starting event loop:\n");
			}

			while(!_must_see.empty())
			{
				if(CFRunLoopRunInMode(kCFRunLoopDefaultMode, 5, false) == kCFRunLoopRunTimedOut)
				{
					iterate(path, _must_see)
						fprintf(stderr, "*** waiting: %s\n", path::relative_to(*path, _jail.path()).c_str());
				}
			}

			if(_log)
				fprintf(stderr, "seen it all\n\n");
		}

	private:
		test::jail_t const& _jail;
		std::set<std::string> _must_see, _allowed;
		bool _in_replay, _log;
	};
#endif

public:
	void test_simple ()
	{
#if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5
		test::jail_t jail;
		callback_t cb(jail/*, true*/);
		cb.must_see("foo/bar", "foo/bar/baz/fud", NULL);
		cb.allowed("", "foo", "foo/bar/baz", NULL);

		fs::watch(jail.path(), &cb, FSEventsGetCurrentEventId(), 0.1);
		jail.touch("foo/bar/baz/fud/dummy.txt");
		jail.touch("foo/bar/dummy.txt");
		cb.event_loop("");

		fs::unwatch(jail.path(), &cb);
#endif /* MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5 */
	}

	void test_file_watch ()
	{
#if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5
		test::jail_t jail;
		callback_t cb(jail/*, true*/);
		cb.must_see("foo/bar/dummy.txt", NULL);

		jail.touch("foo/bar/dummy.txt");
		fs::watch(jail.path("foo/bar/dummy.txt"), &cb, FSEventsGetCurrentEventId(), 0.1);
		sleep(1);

		jail.touch("foo/dummy.txt");
		jail.touch("foo/bar/dummy.txt");
		jail.touch("foo/bar/other.txt");
		cb.event_loop("foo/bar/dummy.txt");

		fs::unwatch(jail.path("foo/bar/dummy.txt"), &cb);
#endif /* MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5 */
	}

	void test_initially_missing ()
	{
#if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5
		test::jail_t jail;
		callback_t cb(jail/*, true*/);
		cb.must_see("foo/bar", "foo/bar/baz", "foo/bar/fud", NULL);

		fs::watch(jail.path("foo/bar"), &cb, FSEventsGetCurrentEventId(), 0.1);

		jail.mkdir("foo/bar/baz/fud");
		jail.touch("dummy.txt");     // should be ignored
		jail.touch("foo/dummy.txt"); // should be ignored
		jail.mkdir("foo/bar/fud/dummy.txt");
		cb.event_loop("foo/bar");

		jail.touch("dummy.txt");     // should be ignored
		jail.touch("foo/dummy.txt"); // should be ignored
		jail.touch("foo/bar/baz/fud/dummy.txt");
		cb.must_see("foo/bar/baz/fud", NULL);
		cb.event_loop("foo/bar");

		fs::unwatch(jail.path("foo/bar"), &cb);
#endif /* MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5 */
	}

	void test_initially_missing_file ()
	{
#if MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5
		test::jail_t jail;
		callback_t cb(jail/*, true*/);
		cb.must_see("foo/bar/dummy.txt", NULL);

		fs::watch(jail.path("foo/bar/dummy.txt"), &cb, FSEventsGetCurrentEventId(), 0.1);

		jail.mkdir("foo/bar/baz/fud");
		jail.touch("dummy.txt");
		jail.touch("foo/dummy.txt");
		jail.touch("foo/bar/other.txt");
		jail.touch("foo/bar/dummy.txt");
		cb.event_loop("foo/bar/dummy.txt");

		sleep(1);
		jail.touch("dummy.txt");
		jail.touch("foo/dummy.txt");
		jail.touch("foo/bar/other.txt");
		jail.touch("foo/bar/dummy.txt");
		cb.must_see("foo/bar/dummy.txt", NULL);
		cb.event_loop("foo/bar/dummy.txt");

		fs::unwatch(jail.path("foo/bar/dummy.txt"), &cb);
#endif /* MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5 */
	}
};
