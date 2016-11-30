/*
 * synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2012 Synergy Si Ltd.
 * Copyright (C) 2002 Chris Schoeneman
 * 
 * This package is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * found in the file COPYING that should have accompanied this file.
 * 
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#pragma once

#include "ipc/IpcClient.h"
#include "synergy/IApp.h"
#include "base/String.h"
#include "base/Log.h"
#include "base/EventQueue.h"
#include "common/common.h"

#if SYSAPI_WIN32
#include "synergy/win32/AppUtilWindows.h"
#elif SYSAPI_UNIX
#include "synergy/unix/AppUtilUnix.h"
#endif

class IArchTaskBarReceiver;
class BufferedLogOutputter;
class ILogOutputter;
class FileLogOutputter;
namespace synergy { class Screen; }
class IEventQueue;
class SocketMultiplexer;

typedef IArchTaskBarReceiver* (*CreateTaskBarReceiverFunc)(const BufferedLogOutputter*, IEventQueue* events);

class App : public IApp {
public:
	App(IEventQueue* events, CreateTaskBarReceiverFunc createTaskBarReceiver, ArgsBase* args);
	virtual ~App();

	// Returns args that are common between server and client.
	ArgsBase& argsBase() const { return *m_args; }

	// Prints the current compiled version.
	virtual void version();

	// Prints help specific to client or server.
	virtual void help() = 0;

	// Parse command line arguments.
	virtual void parseArgs(int argc, const char* const* argv) = 0;
	
	int run(int argc, char** argv);

	int daemonMainLoop(int, const char**);

	virtual void loadConfig() = 0;
	virtual bool loadConfig(const String& pathname) = 0;

	// A description of the daemon (used only on Windows).
	virtual const char* daemonInfo() const = 0;

	// Function pointer for function to exit immediately.
	// TODO: this is old C code - use inheritance to normalize
	void (*m_bye)(int);

	static App& instance() { assert(s_instance != nullptr); return *s_instance; }

	// If --log was specified in args, then add a file logger.
	void setupFileLogging();

	// If messages will be hidden (to improve performance), warn user.
	void loggingFilterWarning();

	// Parses args, sets up file logging, and loads the config.
	void initApp(int argc, const char** argv);

	// HACK: accept non-const, but make it const anyway
	void initApp(int argc, char** argv) { initApp(argc, (const char**)argv); }

	ARCH_APP_UTIL& appUtil() { return m_appUtil; }

	virtual IArchTaskBarReceiver* taskBarReceiver() const  { return m_taskBarReceiver; }

	virtual void setByeFunc(void(*bye)(int)) { m_bye = bye; }
	virtual void bye(int error) { m_bye(error); }
	
	virtual IEventQueue* getEvents() const { return m_events; }

	void				setSocketMultiplexer(SocketMultiplexer* sm) { m_socketMultiplexer = sm; }
	SocketMultiplexer*	getSocketMultiplexer() const { return m_socketMultiplexer; }

	void				setEvents(EventQueue& events) { m_events = &events; }

private:
	void				handleIpcMessage(const Event&, void*);

protected:
	void				initIpcClient();
	void				cleanupIpcClient();
	void				runEventsLoop(void*);

	IArchTaskBarReceiver* m_taskBarReceiver;
	bool m_suspended;
	IEventQueue*		m_events;

private:
	ArgsBase* m_args;
	static App* s_instance;
	FileLogOutputter* m_fileLog;
	CreateTaskBarReceiverFunc m_createTaskBarReceiver;
	ARCH_APP_UTIL m_appUtil;
	IpcClient*			m_ipcClient;
	SocketMultiplexer*	m_socketMultiplexer;
};

class MinimalApp : public App {
public:
	MinimalApp();
	virtual ~MinimalApp();

	// IApp overrides
	virtual int			standardStartup(int argc, char** argv);
	virtual int			runInner(int argc, char** argv, ILogOutputter* outputter, StartupFunc startup);
	virtual void		startNode();
	virtual int			mainLoop();
	virtual int			foregroundStartup(int argc, char** argv);
	virtual synergy::Screen*	
						createScreen();
	virtual void		loadConfig();
	virtual bool		loadConfig(const String& pathname);
	virtual const char*	daemonInfo() const;
	virtual const char* daemonName() const;
	virtual void		parseArgs(int argc, const char* const* argv);

private:
	Arch				m_arch;
	Log					m_log;
	EventQueue			m_events;
};

#if WINAPI_MSWINDOWS
#define DAEMON_RUNNING(running_) ArchMiscWindows::daemonRunning(running_)
#else
#define DAEMON_RUNNING(running_)
#endif

#define HELP_COMMON_INFO_1 \
	"  -d, --debug <level>      filter out log messages with priority below level.\n" \
	"                             level may be: FATAL, ERROR, WARNING, NOTE, INFO,\n" \
	"                             DEBUG, DEBUGn (1-5).\n" \
	"  -n, --name <screen-name> use screen-name instead the hostname to identify\n" \
	"                             this screen in the configuration.\n" \
	"  -1, --no-restart         do not try to restart on failure.\n" \
	"*     --restart            restart the server automatically if it fails.\n" \
	"  -l  --log <file>         write log messages to file.\n" \
	"      --no-tray            disable the system tray icon.\n"

#define HELP_COMMON_INFO_2 \
	"  -h, --help               display this help and exit.\n" \
	"      --version            display version information and exit.\n"

#define HELP_COMMON_ARGS \
	" [--name <screen-name>]" \
	" [--restart|--no-restart]" \
	" [--debug <level>]"

// system args (windows/unix)
#if SYSAPI_UNIX

// unix daemon mode args
#  define HELP_SYS_ARGS \
	" [--daemon|--no-daemon]"
#  define HELP_SYS_INFO \
	"  -f, --no-daemon          run in the foreground.\n"	\
	"*     --daemon             run as a daemon.\n"

#elif SYSAPI_WIN32

// windows args
#  define HELP_SYS_ARGS \
	" [--service <action>] [--relaunch] [--exit-pause]"
#  define HELP_SYS_INFO \
	"      --service <action>   manage the windows service, valid options are:\n" \
	"                             install/uninstall/start/stop\n" \
	"      --relaunch           persistently relaunches process in current user \n" \
	"                             session (useful for vista and upward).\n" \
	"      --exit-pause         wait for key press on exit, can be useful for\n" \
	"                             reading error messages that occur on exit.\n"
#endif
