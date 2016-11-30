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

#include "synergy/ServerApp.h"

#include "server/Server.h"
#include "server/ClientListener.h"
#include "server/ClientProxy.h"
#include "server/PrimaryClient.h"
#include "synergy/ArgParser.h"
#include "synergy/Screen.h"
#include "synergy/XScreen.h"
#include "synergy/ServerTaskBarReceiver.h"
#include "synergy/ServerArgs.h"
#include "net/SocketMultiplexer.h"
#include "net/TCPSocketFactory.h"
#include "net/XSocket.h"
#include "arch/Arch.h"
#include "base/EventQueue.h"
#include "base/log_outputters.h"
#include "base/FunctionEventJob.h"
#include "base/TMethodJob.h"
#include "base/IEventQueue.h"
#include "base/Log.h"
#include "base/TMethodEventJob.h"
#include "common/Version.h"

#if SYSAPI_WIN32
#include "arch/win32/ArchMiscWindows.h"
#endif

#if WINAPI_MSWINDOWS
#include "platform/MSWindowsScreen.h"
#elif WINAPI_XWINDOWS
#include "platform/XWindowsScreen.h"
#elif WINAPI_CARBON
#include "platform/OSXScreen.h"
#endif

#if defined(__APPLE__)
#include "platform/OSXDragSimulator.h"
#endif

#include <iostream>
#include <stdio.h>
#include <fstream>

//
// ServerApp
//

ServerApp::ServerApp(IEventQueue* events, CreateTaskBarReceiverFunc createTaskBarReceiver) :
	App(events, createTaskBarReceiver, new ServerArgs()),
	m_server(NULL),
	m_serverState(kUninitialized),
	m_serverScreen(NULL),
	m_primaryClient(NULL),
	m_listener(NULL),
	m_timer(NULL),
	m_synergyAddress(NULL)
{
}

ServerApp::~ServerApp()
{
}

void
ServerApp::parseArgs(int argc, const char* const* argv)
{
	ArgParser argParser(this);
	bool result = argParser.parseServerArgs(args(), argc, argv);

	if (!result || args().m_shouldExit) {
		m_bye(kExitArgs);
	}
	else {
		if (!args().m_synergyAddress.empty()) {
			try {
				*m_synergyAddress = NetworkAddress(args().m_synergyAddress, 
					kDefaultPort);
				m_synergyAddress->resolve();
			}
			catch (XSocketAddress& e) {
				LOG((CLOG_PRINT "%s: %s" BYE,
					args().m_pname, e.what(), args().m_pname));
				m_bye(kExitArgs);
			}
		}
	}
}

void
ServerApp::help()
{
	// window api args (windows/x-windows/carbon)
#if WINAPI_XWINDOWS
#  define WINAPI_ARGS \
	" [--display <display>] [--no-xinitthreads]"
#  define WINAPI_INFO \
	"      --display <display>  connect to the X server at <display>\n" \
	"      --no-xinitthreads    do not call XInitThreads()\n"
#else
#  define WINAPI_ARGS
#  define WINAPI_INFO
#endif

	char buffer[2000];
	sprintf(
		buffer,
		"Usage: %s"
		" [--address <address>]"
		" [--config <pathname>]"
		WINAPI_ARGS
		HELP_SYS_ARGS
		HELP_COMMON_ARGS
		"\n\n"
		"Start the synergy mouse/keyboard sharing server.\n"
		"\n"
		"  -a, --address <address>  listen for clients on the given address.\n"
		"  -c, --config <pathname>  use the named configuration file instead.\n"
		HELP_COMMON_INFO_1
		WINAPI_INFO
		HELP_SYS_INFO
		HELP_COMMON_INFO_2
		"\n"
		"* marks defaults.\n"
		"\n"
		"The argument for --address is of the form: [<hostname>][:<port>].  The\n"
		"hostname must be the address or hostname of an interface on the system.\n"
		"The default is to listen on all interfaces.  The port overrides the\n"
		"default port, %d.\n"
		"\n"
		"If no configuration file pathname is provided then the first of the\n"
		"following to load successfully sets the configuration:\n"
		"  %s\n"
		"  %s\n",
		args().m_pname, kDefaultPort,
		ARCH->concatPath(ARCH->getUserDirectory(), USR_CONFIG_NAME).c_str(),
		ARCH->concatPath(ARCH->getSystemDirectory(), SYS_CONFIG_NAME).c_str()
	);

	LOG((CLOG_PRINT "%s", buffer));
}

void
ServerApp::reloadSignalHandler(Arch::ESignal, void*)
{
	IEventQueue* events = App::instance().getEvents();
	events->addEvent(Event(events->forServerApp().reloadConfig(),
		events->getSystemTarget()));
}

void
ServerApp::reloadConfig(const Event&, void*)
{
	LOG((CLOG_DEBUG "reload configuration"));
	if (loadConfig(args().m_configFile)) {
		if (m_server != NULL) {
			m_server->setConfig(*args().m_config);
		}
		LOG((CLOG_NOTE "reloaded configuration"));
	}
}

void
ServerApp::loadConfig()
{
	bool loaded = false;

	// load the config file, if specified
	if (!args().m_configFile.empty()) {
		loaded = loadConfig(args().m_configFile);
	}

	// load the default configuration if no explicit file given
	else {
		// get the user's home directory
		String path = ARCH->getUserDirectory();
		if (!path.empty()) {
			// complete path
			path = ARCH->concatPath(path, USR_CONFIG_NAME);

			// now try loading the user's configuration
			if (loadConfig(path)) {
				loaded            = true;
				args().m_configFile = path;
			}
		}
		if (!loaded) {
			// try the system-wide config file
			path = ARCH->getSystemDirectory();
			if (!path.empty()) {
				path = ARCH->concatPath(path, SYS_CONFIG_NAME);
				if (loadConfig(path)) {
					loaded            = true;
					args().m_configFile = path;
				}
			}
		}
	}

	if (!loaded) {
		LOG((CLOG_PRINT "%s: no configuration available", args().m_pname));
		m_bye(kExitConfig);
	}
}

bool
ServerApp::loadConfig(const String& pathname)
{
	try {
		// load configuration
		LOG((CLOG_DEBUG "opening configuration \"%s\"", pathname.c_str()));
		std::ifstream configStream(pathname.c_str());
		if (!configStream.is_open()) {
			// report failure to open configuration as a debug message
			// since we try several paths and we expect some to be
			// missing.
			LOG((CLOG_DEBUG "cannot open configuration \"%s\"",
				pathname.c_str()));
			return false;
		}
		configStream >> *args().m_config;
		LOG((CLOG_DEBUG "configuration read successfully"));
		return true;
	}
	catch (XConfigRead& e) {
		// report error in configuration file
		LOG((CLOG_ERR "cannot read configuration \"%s\": %s",
			pathname.c_str(), e.what()));
	}
	return false;
}

void 
ServerApp::forceReconnect(const Event&, void*)
{
	if (m_server != NULL) {
		m_server->disconnect();
	}
}

void 
ServerApp::handleClientConnected(const Event&, void* vlistener)
{
	ClientListener* listener = reinterpret_cast<ClientListener*>(vlistener);
	ClientProxy* client = listener->getNextClient();
	if (client != NULL) {
		m_server->adoptClient(client);
		updateStatus();
	}
}

void
ServerApp::handleClientsDisconnected(const Event&, void*)
{
	m_events->addEvent(Event(Event::kQuit));
}

void
ServerApp::closeServer(Server* server)
{
	if (server == NULL) {
		return;
	}

	// tell all clients to disconnect
	server->disconnect();

	// wait for clients to disconnect for up to timeout seconds
	double timeout = 3.0;
	EventQueueTimer* timer = m_events->newOneShotTimer(timeout, NULL);
	m_events->adoptHandler(Event::kTimer, timer,
		new TMethodEventJob<ServerApp>(this, &ServerApp::handleClientsDisconnected));
	m_events->adoptHandler(m_events->forServer().disconnected(), server,
		new TMethodEventJob<ServerApp>(this, &ServerApp::handleClientsDisconnected));
	
	m_events->loop();

	m_events->removeHandler(Event::kTimer, timer);
	m_events->deleteTimer(timer);
	m_events->removeHandler(m_events->forServer().disconnected(), server);

	// done with server
	delete server;
}

void 
ServerApp::stopRetryTimer()
{
	if (m_timer != NULL) {
		m_events->deleteTimer(m_timer);
		m_events->removeHandler(Event::kTimer, NULL);
		m_timer = NULL;
	}
}

void
ServerApp::updateStatus()
{
	updateStatus("");
}

void ServerApp::updateStatus( const String& msg )
{
	if (m_taskBarReceiver)
	{
		m_taskBarReceiver->updateStatus(m_server, msg);
	}
}

void 
ServerApp::closeClientListener(ClientListener* listen)
{
	if (listen != NULL) {
		m_events->removeHandler(m_events->forClientListener().connected(), listen);
		delete listen;
	}
}

void 
ServerApp::stopServer()
{
	if (m_serverState == kStarted) {
		closeClientListener(m_listener);
		closeServer(m_server);
		m_server      = NULL;
		m_listener    = NULL;
		m_serverState = kInitialized;
	}
	else if (m_serverState == kStarting) {
		stopRetryTimer();
		m_serverState = kInitialized;
	}
	assert(m_server == NULL);
	assert(m_listener == NULL);
}

void
ServerApp::closePrimaryClient(PrimaryClient* primaryClient)
{
	delete primaryClient;
}

void 
ServerApp::closeServerScreen(synergy::Screen* screen)
{
	if (screen != NULL) {
		m_events->removeHandler(m_events->forIScreen().error(),
			screen->getEventTarget());
		m_events->removeHandler(m_events->forIScreen().suspend(),
			screen->getEventTarget());
		m_events->removeHandler(m_events->forIScreen().resume(),
			screen->getEventTarget());
		delete screen;
	}
}

void ServerApp::cleanupServer()
{
	stopServer();
	if (m_serverState == kInitialized) {
		closePrimaryClient(m_primaryClient);
		closeServerScreen(m_serverScreen);
		m_primaryClient = NULL;
		m_serverScreen  = NULL;
		m_serverState   = kUninitialized;
	}
	else if (m_serverState == kInitializing ||
		m_serverState == kInitializingToStart) {
			stopRetryTimer();
			m_serverState = kUninitialized;
	}
	assert(m_primaryClient == NULL);
	assert(m_serverScreen == NULL);
	assert(m_serverState == kUninitialized);
}

void
ServerApp::retryHandler(const Event&, void*)
{
	// discard old timer
	assert(m_timer != NULL);
	stopRetryTimer();

	// try initializing/starting the server again
	switch (m_serverState) {
	case kUninitialized:
	case kInitialized:
	case kStarted:
		assert(0 && "bad internal server state");
		break;

	case kInitializing:
		LOG((CLOG_DEBUG1 "retry server initialization"));
		m_serverState = kUninitialized;
		if (!initServer()) {
			m_events->addEvent(Event(Event::kQuit));
		}
		break;

	case kInitializingToStart:
		LOG((CLOG_DEBUG1 "retry server initialization"));
		m_serverState = kUninitialized;
		if (!initServer()) {
			m_events->addEvent(Event(Event::kQuit));
		}
		else if (m_serverState == kInitialized) {
			LOG((CLOG_DEBUG1 "starting server"));
			if (!startServer()) {
				m_events->addEvent(Event(Event::kQuit));
			}
		}
		break;

	case kStarting:
		LOG((CLOG_DEBUG1 "retry starting server"));
		m_serverState = kInitialized;
		if (!startServer()) {
			m_events->addEvent(Event(Event::kQuit));
		}
		break;
	}
}

bool ServerApp::initServer()
{
	// skip if already initialized or initializing
	if (m_serverState != kUninitialized) {
		return true;
	}

	double retryTime;
	synergy::Screen* serverScreen         = NULL;
	PrimaryClient* primaryClient = NULL;
	try {
		String name    = args().m_config->getCanonicalName(args().m_name);
		serverScreen    = openServerScreen();
		primaryClient   = openPrimaryClient(name, serverScreen);
		m_serverScreen  = serverScreen;
		m_primaryClient = primaryClient;
		m_serverState   = kInitialized;
		updateStatus();
		return true;
	}
	catch (XScreenUnavailable& e) {
		LOG((CLOG_WARN "primary screen unavailable: %s", e.what()));
		closePrimaryClient(primaryClient);
		closeServerScreen(serverScreen);
		updateStatus(String("primary screen unavailable: ") + e.what());
		retryTime = e.getRetryTime();
	}
	catch (XScreenOpenFailure& e) {
		LOG((CLOG_CRIT "failed to start server: %s", e.what()));
		closePrimaryClient(primaryClient);
		closeServerScreen(serverScreen);
		return false;
	}
	catch (XBase& e) {
		LOG((CLOG_CRIT "failed to start server: %s", e.what()));
		closePrimaryClient(primaryClient);
		closeServerScreen(serverScreen);
		return false;
	}

	if (args().m_restartable) {
		// install a timer and handler to retry later
		assert(m_timer == NULL);
		LOG((CLOG_DEBUG "retry in %.0f seconds", retryTime));
		m_timer = m_events->newOneShotTimer(retryTime, NULL);
		m_events->adoptHandler(Event::kTimer, m_timer,
			new TMethodEventJob<ServerApp>(this, &ServerApp::retryHandler));
		m_serverState = kInitializing;
		return true;
	}
	else {
		// don't try again
		return false;
	}
}

synergy::Screen*
ServerApp::openServerScreen()
{
	synergy::Screen* screen = createScreen();
	screen->setEnableDragDrop(argsBase().m_enableDragDrop);
	m_events->adoptHandler(m_events->forIScreen().error(),
		screen->getEventTarget(),
		new TMethodEventJob<ServerApp>(
		this, &ServerApp::handleScreenError));
	m_events->adoptHandler(m_events->forIScreen().suspend(),
		screen->getEventTarget(),
		new TMethodEventJob<ServerApp>(
		this, &ServerApp::handleSuspend));
	m_events->adoptHandler(m_events->forIScreen().resume(),
		screen->getEventTarget(),
		new TMethodEventJob<ServerApp>(
		this, &ServerApp::handleResume));
	return screen;
}

bool 
ServerApp::startServer()
{
	// skip if already started or starting
	if (m_serverState == kStarting || m_serverState == kStarted) {
		return true;
	}

	// initialize if necessary
	if (m_serverState != kInitialized) {
		if (!initServer()) {
			// hard initialization failure
			return false;
		}
		if (m_serverState == kInitializing) {
			// not ready to start
			m_serverState = kInitializingToStart;
			return true;
		}
		assert(m_serverState == kInitialized);
	}

	double retryTime;
	ClientListener* listener = NULL;
	try {
		listener   = openClientListener(args().m_config->getSynergyAddress());
		m_server   = openServer(*args().m_config, m_primaryClient);
		listener->setServer(m_server);
		m_listener = listener;
		updateStatus();
		LOG((CLOG_NOTE "started server, waiting for clients"));
		m_serverState = kStarted;
		return true;
	}
	catch (XSocketAddressInUse& e) {
		LOG((CLOG_WARN "cannot listen for clients: %s", e.what()));
		closeClientListener(listener);
		updateStatus(String("cannot listen for clients: ") + e.what());
		retryTime = 10.0;
	}
	catch (XBase& e) {
		LOG((CLOG_CRIT "failed to start server: %s", e.what()));
		closeClientListener(listener);
		return false;
	}

	if (args().m_restartable) {
		// install a timer and handler to retry later
		assert(m_timer == NULL);
		LOG((CLOG_DEBUG "retry in %.0f seconds", retryTime));
		m_timer = m_events->newOneShotTimer(retryTime, NULL);
		m_events->adoptHandler(Event::kTimer, m_timer,
			new TMethodEventJob<ServerApp>(this, &ServerApp::retryHandler));
		m_serverState = kStarting;
		return true;
	}
	else {
		// don't try again
		return false;
	}
}

synergy::Screen* 
ServerApp::createScreen()
{
#if WINAPI_MSWINDOWS
	return new synergy::Screen(new MSWindowsScreen(
		true, args().m_noHooks, args().m_stopOnDeskSwitch, m_events), m_events);
#elif WINAPI_XWINDOWS
	return new synergy::Screen(new XWindowsScreen(
		args().m_display, true, args().m_disableXInitThreads, 0, m_events), m_events);
#elif WINAPI_CARBON
	return new synergy::Screen(new OSXScreen(m_events, true), m_events);
#endif
}

PrimaryClient* 
ServerApp::openPrimaryClient(const String& name, synergy::Screen* screen)
{
	LOG((CLOG_DEBUG1 "creating primary screen"));
	return new PrimaryClient(name, screen);

}

void
ServerApp::handleScreenError(const Event&, void*)
{
	LOG((CLOG_CRIT "error on screen"));
	m_events->addEvent(Event(Event::kQuit));
}

void 
ServerApp::handleSuspend(const Event&, void*)
{
	if (!m_suspended) {
		LOG((CLOG_INFO "suspend"));
		stopServer();
		m_suspended = true;
	}
}

void 
ServerApp::handleResume(const Event&, void*)
{
	if (m_suspended) {
		LOG((CLOG_INFO "resume"));
		startServer();
		m_suspended = false;
	}
}

ClientListener*
ServerApp::openClientListener(const NetworkAddress& address)
{
	ClientListener* listen = new ClientListener(
		address,
		new CTCPSocketFactory(m_events, getSocketMultiplexer()),
		NULL,
		args().m_crypto,
		m_events);
	
	m_events->adoptHandler(
		m_events->forClientListener().connected(), listen,
		new TMethodEventJob<ServerApp>(
			this, &ServerApp::handleClientConnected, listen));
	
	return listen;
}

Server* 
ServerApp::openServer(Config& config, PrimaryClient* primaryClient)
{
	Server* server = new Server(config, primaryClient, m_serverScreen, m_events, args().m_enableDragDrop);
	try {
		m_events->adoptHandler(
			m_events->forServer().disconnected(), server,
			new TMethodEventJob<ServerApp>(this, &ServerApp::handleNoClients));

		m_events->adoptHandler(
			m_events->forServer().screenSwitched(), server,
			new TMethodEventJob<ServerApp>(this, &ServerApp::handleScreenSwitched));

	} catch (std::bad_alloc &ba) {
		delete server;
		throw ba;
	}

	return server;
}

void
ServerApp::handleNoClients(const Event&, void*)
{
	updateStatus();
}

void
ServerApp::handleScreenSwitched(const Event& e, void*)
{
}

int
ServerApp::mainLoop()
{
	// create socket multiplexer.  this must happen after daemonization
	// on unix because threads evaporate across a fork().
	SocketMultiplexer multiplexer;
	setSocketMultiplexer(&multiplexer);

	// if configuration has no screens then add this system
	// as the default
	if (args().m_config->begin() == args().m_config->end()) {
		args().m_config->addScreen(args().m_name);
	}

	// set the contact address, if provided, in the config.
	// otherwise, if the config doesn't have an address, use
	// the default.
	if (m_synergyAddress->isValid()) {
		args().m_config->setSynergyAddress(*m_synergyAddress);
	}
	else if (!args().m_config->getSynergyAddress().isValid()) {
		args().m_config->setSynergyAddress(NetworkAddress(kDefaultPort));
	}

	// canonicalize the primary screen name
	String primaryName = args().m_config->getCanonicalName(args().m_name);
	if (primaryName.empty()) {
		LOG((CLOG_CRIT "unknown screen name `%s'", args().m_name.c_str()));
		return kExitFailed;
	}

	// start server, etc
	appUtil().startNode();
	
	// init ipc client after node start, since create a new screen wipes out
	// the event queue (the screen ctors call adoptBuffer).
	if (argsBase().m_enableIpc) {
		initIpcClient();
	}

	// load all available plugins.
	ARCH->plugin().init(m_serverScreen->getEventTarget(), m_events);

	// handle hangup signal by reloading the server's configuration
	ARCH->setSignalHandler(Arch::kHANGUP, &reloadSignalHandler, NULL);
	m_events->adoptHandler(m_events->forServerApp().reloadConfig(),
		m_events->getSystemTarget(),
		new TMethodEventJob<ServerApp>(this, &ServerApp::reloadConfig));

	// handle force reconnect event by disconnecting clients.  they'll
	// reconnect automatically.
	m_events->adoptHandler(m_events->forServerApp().forceReconnect(),
		m_events->getSystemTarget(),
		new TMethodEventJob<ServerApp>(this, &ServerApp::forceReconnect));

	// to work around the sticky meta keys problem, we'll give users
	// the option to reset the state of synergys
	m_events->adoptHandler(m_events->forServerApp().resetServer(),
		m_events->getSystemTarget(),
		new TMethodEventJob<ServerApp>(this, &ServerApp::resetServer));

	// run event loop.  if startServer() failed we're supposed to retry
	// later.  the timer installed by startServer() will take care of
	// that.
	DAEMON_RUNNING(true);
	
#if defined(MAC_OS_X_VERSION_10_7)
	
	Thread thread(
		new TMethodJob<ServerApp>(
			this, &ServerApp::runEventsLoop,
			NULL));
	
	// wait until carbon loop is ready
	OSXScreen* screen = dynamic_cast<OSXScreen*>(
		m_serverScreen->getPlatformScreen());
	screen->waitForCarbonLoop();
	
	runCocoaApp();
#else
	m_events->loop();
#endif
	
	DAEMON_RUNNING(false);

	// close down
	LOG((CLOG_DEBUG1 "stopping server"));
	m_events->removeHandler(m_events->forServerApp().forceReconnect(),
		m_events->getSystemTarget());
	m_events->removeHandler(m_events->forServerApp().reloadConfig(),
		m_events->getSystemTarget());
	cleanupServer();
	updateStatus();
	LOG((CLOG_NOTE "stopped server"));

	if (argsBase().m_enableIpc) {
		cleanupIpcClient();
	}

	return kExitSuccess;
}

void ServerApp::resetServer(const Event&, void*)
{
	LOG((CLOG_DEBUG1 "resetting server"));
	stopServer();
	cleanupServer();
	startServer();
}

int 
ServerApp::runInner(int argc, char** argv, ILogOutputter* outputter, StartupFunc startup)
{
	// general initialization
	m_synergyAddress = new NetworkAddress;
	args().m_config         = new Config(m_events);
	args().m_pname          = ARCH->getBasename(argv[0]);

	// install caller's output filter
	if (outputter != NULL) {
		CLOG->insert(outputter);
	}

	// run
	int result = startup(argc, argv);

	if (m_taskBarReceiver)
	{
		// done with task bar receiver
		delete m_taskBarReceiver;
	}

	delete args().m_config;
	delete m_synergyAddress;
	return result;
}

int daemonMainLoopStatic(int argc, const char** argv) {
	return ServerApp::instance().daemonMainLoop(argc, argv);
}

int 
ServerApp::standardStartup(int argc, char** argv)
{
	initApp(argc, argv);

	// daemonize if requested
	if (args().m_daemon) {
		return ARCH->daemonize(daemonName(), daemonMainLoopStatic);
	}
	else {
		return mainLoop();
	}
}

int 
ServerApp::foregroundStartup(int argc, char** argv)
{
	initApp(argc, argv);

	// never daemonize
	return mainLoop();
}

const char* 
ServerApp::daemonName() const
{
#if SYSAPI_WIN32
	return "Synergy Server";
#elif SYSAPI_UNIX
	return "synergys";
#endif
}

const char* 
ServerApp::daemonInfo() const
{
#if SYSAPI_WIN32
	return "Shares this computers mouse and keyboard with other computers.";
#elif SYSAPI_UNIX
	return "";
#endif
}

void
ServerApp::startNode()
{
	// start the server.  if this return false then we've failed and
	// we shouldn't retry.
	LOG((CLOG_DEBUG1 "starting server"));
	if (!startServer()) {
		m_bye(kExitFailed);
	}
}
