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

#include "client/Client.h"

#include "client/ServerProxy.h"
#include "synergy/Screen.h"
#include "synergy/Clipboard.h"
#include "synergy/DropHelper.h"
#include "synergy/PacketStreamFilter.h"
#include "synergy/ProtocolUtil.h"
#include "synergy/protocol_types.h"
#include "synergy/XSynergy.h"
#include "synergy/FileChunker.h"
#include "synergy/IPlatformScreen.h"
#include "mt/Thread.h"
#include "io/IStreamFilterFactory.h"
#include "io/CryptoStream.h"
#include "net/IDataSocket.h"
#include "net/ISocketFactory.h"
#include "arch/Arch.h"
#include "base/Log.h"
#include "base/IEventQueue.h"
#include "base/TMethodEventJob.h"
#include "base/TMethodJob.h"
#include "common/stdexcept.h"

#include <cstring>
#include <cstdlib>
#include <sstream>
#include <fstream>

//
// Client
//

Client::Client(
		IEventQueue* events,
		const String& name, const NetworkAddress& address,
		ISocketFactory* socketFactory,
		IStreamFilterFactory* streamFilterFactory,
		synergy::Screen* screen,
		const CryptoOptions& crypto,
		bool enableDragDrop) :
	m_mock(false),
	m_name(name),
	m_serverAddress(address),
	m_socketFactory(socketFactory),
	m_streamFilterFactory(streamFilterFactory),
	m_screen(screen),
	m_stream(NULL),
	m_timer(NULL),
	m_server(NULL),
	m_ready(false),
	m_active(false),
	m_suspended(false),
	m_connectOnResume(false),
	m_events(events),
	m_cryptoStream(NULL),
	m_crypto(crypto),
	m_sendFileThread(NULL),
	m_writeToDropDirThread(NULL),
	m_enableDragDrop(enableDragDrop)
{
	assert(m_socketFactory != NULL);
	assert(m_screen        != NULL);

	// register suspend/resume event handlers
	m_events->adoptHandler(m_events->forIScreen().suspend(),
							getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleSuspend));
	m_events->adoptHandler(m_events->forIScreen().resume(),
							getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleResume));

	if (m_enableDragDrop) {
		m_events->adoptHandler(m_events->forIScreen().fileChunkSending(),
								this,
								new TMethodEventJob<Client>(this,
									&Client::handleFileChunkSending));
		m_events->adoptHandler(m_events->forIScreen().fileRecieveCompleted(),
								this,
								new TMethodEventJob<Client>(this,
									&Client::handleFileRecieveCompleted));
	}
}

Client::~Client()
{
	if (m_mock) {
		return;
	}

	m_events->removeHandler(m_events->forIScreen().suspend(),
							  getEventTarget());
	m_events->removeHandler(m_events->forIScreen().resume(),
							  getEventTarget());

	cleanupTimer();
	cleanupScreen();
	cleanupConnecting();
	cleanupConnection();
	delete m_socketFactory;
	delete m_streamFilterFactory;
}

void
Client::connect()
{
	if (m_stream != NULL) {
		return;
	}
	if (m_suspended) {
		m_connectOnResume = true;
		return;
	}

	try {
		// resolve the server hostname.  do this every time we connect
		// in case we couldn't resolve the address earlier or the address
		// has changed (which can happen frequently if this is a laptop
		// being shuttled between various networks).  patch by Brent
		// Priddy.
		m_serverAddress.resolve();
		
		// m_serverAddress will be null if the hostname address is not reolved
		if (m_serverAddress.getAddress() != NULL) {
		  // to help users troubleshoot, show server host name (issue: 60)
		  LOG((CLOG_NOTE "connecting to '%s': %s:%i", 
		  m_serverAddress.getHostname().c_str(),
		  ARCH->addrToString(m_serverAddress.getAddress()).c_str(),
		  m_serverAddress.getPort()));
		}

		// create the socket
		IDataSocket* socket = m_socketFactory->create();

		// filter socket messages, including a packetizing filter
		m_stream = socket;
		if (m_streamFilterFactory != NULL) {
			m_stream = m_streamFilterFactory->create(m_stream, true);
		}
		m_stream = new PacketStreamFilter(m_events, m_stream, true);

		if (m_crypto.m_mode != kDisabled) {
			m_cryptoStream = new CryptoStream(
				m_events, m_stream, m_crypto, true);
			m_stream = m_cryptoStream;
		}

		// connect
		LOG((CLOG_DEBUG1 "connecting to server"));
		setupConnecting();
		setupTimer();
		socket->connect(m_serverAddress);
	}
	catch (XBase& e) {
		cleanupTimer();
		cleanupConnecting();
		delete m_stream;
		m_stream = NULL;
		LOG((CLOG_DEBUG1 "connection failed"));
		sendConnectionFailedEvent(e.what());
		return;
	}
}

void
Client::disconnect(const char* msg)
{
	m_connectOnResume = false;
	cleanupTimer();
	cleanupScreen();
	cleanupConnecting();
	cleanupConnection();
	if (msg != NULL) {
		sendConnectionFailedEvent(msg);
	}
	else {
		sendEvent(m_events->forClient().disconnected(), NULL);
	}
}

void
Client::handshakeComplete()
{
	m_ready = true;
	m_screen->enable();
	sendEvent(m_events->forClient().connected(), NULL);
}

void
Client::setDecryptIv(const UInt8* iv)
{
	if (m_cryptoStream != NULL) {
		m_cryptoStream->setDecryptIv(iv);
	}
}

bool
Client::isConnected() const
{
	return (m_server != NULL);
}

bool
Client::isConnecting() const
{
	return (m_timer != NULL);
}

NetworkAddress
Client::getServerAddress() const
{
	return m_serverAddress;
}

void*
Client::getEventTarget() const
{
	return m_screen->getEventTarget();
}

bool
Client::getClipboard(ClipboardID id, IClipboard* clipboard) const
{
	return m_screen->getClipboard(id, clipboard);
}

void
Client::getShape(SInt32& x, SInt32& y, SInt32& w, SInt32& h) const
{
	m_screen->getShape(x, y, w, h);
}

void
Client::getCursorPos(SInt32& x, SInt32& y) const
{
	m_screen->getCursorPos(x, y);
}

void
Client::enter(SInt32 xAbs, SInt32 yAbs, UInt32, KeyModifierMask mask, bool)
{
	m_active = true;
	m_screen->mouseMove(xAbs, yAbs);
	m_screen->enter(mask);
}

bool
Client::leave()
{
	m_screen->leave();

	m_active = false;

	// send clipboards that we own and that have changed
	for (ClipboardID id = 0; id < kClipboardEnd; ++id) {
		if (m_ownClipboard[id]) {
			sendClipboard(id);
		}
	}

	return true;
}

void
Client::setClipboard(ClipboardID id, const IClipboard* clipboard)
{
 	m_screen->setClipboard(id, clipboard);
	m_ownClipboard[id]  = false;
	m_sentClipboard[id] = false;
}

void
Client::grabClipboard(ClipboardID id)
{
	m_screen->grabClipboard(id);
	m_ownClipboard[id]  = false;
	m_sentClipboard[id] = false;
}

void
Client::setClipboardDirty(ClipboardID, bool)
{
	assert(0 && "shouldn't be called");
}

void
Client::keyDown(KeyID id, KeyModifierMask mask, KeyButton button)
{
 	m_screen->keyDown(id, mask, button);
}

void
Client::keyRepeat(KeyID id, KeyModifierMask mask,
				SInt32 count, KeyButton button)
{
 	m_screen->keyRepeat(id, mask, count, button);
}

void
Client::keyUp(KeyID id, KeyModifierMask mask, KeyButton button)
{
 	m_screen->keyUp(id, mask, button);
}

void
Client::mouseDown(ButtonID id)
{
 	m_screen->mouseDown(id);
}

void
Client::mouseUp(ButtonID id)
{
 	m_screen->mouseUp(id);
}

void
Client::mouseMove(SInt32 x, SInt32 y)
{
	m_screen->mouseMove(x, y);
}

void
Client::mouseRelativeMove(SInt32 dx, SInt32 dy)
{
	m_screen->mouseRelativeMove(dx, dy);
}

void
Client::mouseWheel(SInt32 xDelta, SInt32 yDelta)
{
	m_screen->mouseWheel(xDelta, yDelta);
}

void
Client::screensaver(bool activate)
{
 	m_screen->screensaver(activate);
}

void
Client::resetOptions()
{
	m_screen->resetOptions();
}

void
Client::setOptions(const OptionsList& options)
{
	m_screen->setOptions(options);
}

String
Client::getName() const
{
	return m_name;
}

void
Client::sendClipboard(ClipboardID id)
{
	// note -- m_mutex must be locked on entry
	assert(m_screen != NULL);
	assert(m_server != NULL);

	// get clipboard data.  set the clipboard time to the last
	// clipboard time before getting the data from the screen
	// as the screen may detect an unchanged clipboard and
	// avoid copying the data.
	Clipboard clipboard;
	if (clipboard.open(m_timeClipboard[id])) {
		clipboard.close();
	}
	m_screen->getClipboard(id, &clipboard);

	// check time
	if (m_timeClipboard[id] == 0 ||
		clipboard.getTime() != m_timeClipboard[id]) {
		// save new time
		m_timeClipboard[id] = clipboard.getTime();

		// marshall the data
		String data = clipboard.marshall();

		// save and send data if different or not yet sent
		if (!m_sentClipboard[id] || data != m_dataClipboard[id]) {
			m_sentClipboard[id] = true;
			m_dataClipboard[id] = data;
			m_server->onClipboardChanged(id, &clipboard);
		}
	}
}

void
Client::sendEvent(Event::Type type, void* data)
{
	m_events->addEvent(Event(type, getEventTarget(), data));
}

void
Client::sendConnectionFailedEvent(const char* msg)
{
	FailInfo* info = new FailInfo(msg);
	info->m_retry = true;
	Event event(m_events->forClient().connectionFailed(), getEventTarget(), info, Event::kDontFreeData);
	m_events->addEvent(event);
}

void
Client::sendFileChunk(const void* data)
{
	FileChunker::FileChunk* fileChunk = reinterpret_cast<FileChunker::FileChunk*>(const_cast<void*>(data));
	LOG((CLOG_DEBUG1 "sendFileChunk"));
	assert(m_server != NULL);

	// relay
	m_server->fileChunkSending(fileChunk->m_chunk[0], &(fileChunk->m_chunk[1]), fileChunk->m_dataSize);
}

void
Client::setupConnecting()
{
	assert(m_stream != NULL);

	m_events->adoptHandler(m_events->forIDataSocket().connected(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleConnected));
	m_events->adoptHandler(m_events->forIDataSocket().connectionFailed(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleConnectionFailed));
}

void
Client::setupConnection()
{
	assert(m_stream != NULL);

	m_events->adoptHandler(m_events->forISocket().disconnected(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleDisconnected));
	m_events->adoptHandler(m_events->forIStream().inputReady(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleHello));
	m_events->adoptHandler(m_events->forIStream().outputError(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleOutputError));
	m_events->adoptHandler(m_events->forIStream().inputShutdown(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleDisconnected));
	m_events->adoptHandler(m_events->forIStream().outputShutdown(),
							m_stream->getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleDisconnected));
}

void
Client::setupScreen()
{
	assert(m_server == NULL);

	m_ready  = false;
	m_server = new ServerProxy(this, m_stream, m_events);
	m_events->adoptHandler(m_events->forIScreen().shapeChanged(),
							getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleShapeChanged));
	m_events->adoptHandler(m_events->forIScreen().clipboardGrabbed(),
							getEventTarget(),
							new TMethodEventJob<Client>(this,
								&Client::handleClipboardGrabbed));
}

void
Client::setupTimer()
{
	assert(m_timer == NULL);

	m_timer = m_events->newOneShotTimer(15.0, NULL);
	m_events->adoptHandler(Event::kTimer, m_timer,
							new TMethodEventJob<Client>(this,
								&Client::handleConnectTimeout));
}

void
Client::cleanupConnecting()
{
	if (m_stream != NULL) {
		m_events->removeHandler(m_events->forIDataSocket().connected(),
							m_stream->getEventTarget());
		m_events->removeHandler(m_events->forIDataSocket().connectionFailed(),
							m_stream->getEventTarget());
	}
}

void
Client::cleanupConnection()
{
	if (m_stream != NULL) {
		m_events->removeHandler(m_events->forIStream().inputReady(),
							m_stream->getEventTarget());
		m_events->removeHandler(m_events->forIStream().outputError(),
							m_stream->getEventTarget());
		m_events->removeHandler(m_events->forIStream().inputShutdown(),
							m_stream->getEventTarget());
		m_events->removeHandler(m_events->forIStream().outputShutdown(),
							m_stream->getEventTarget());
		m_events->removeHandler(m_events->forISocket().disconnected(),
							m_stream->getEventTarget());
		delete m_stream;
		m_stream = NULL;
	}
}

void
Client::cleanupScreen()
{
	if (m_server != NULL) {
		if (m_ready) {
			m_screen->disable();
			m_ready = false;
		}
		m_events->removeHandler(m_events->forIScreen().shapeChanged(),
							getEventTarget());
		m_events->removeHandler(m_events->forIScreen().clipboardGrabbed(),
							getEventTarget());
		delete m_server;
		m_server = NULL;
	}
}

void
Client::cleanupTimer()
{
	if (m_timer != NULL) {
		m_events->removeHandler(Event::kTimer, m_timer);
		m_events->deleteTimer(m_timer);
		m_timer = NULL;
	}
}

void
Client::handleConnected(const Event&, void*)
{
	LOG((CLOG_DEBUG1 "connected;  wait for hello"));
	cleanupConnecting();
	setupConnection();

	// reset clipboard state
	for (ClipboardID id = 0; id < kClipboardEnd; ++id) {
		m_ownClipboard[id]  = false;
		m_sentClipboard[id] = false;
		m_timeClipboard[id] = 0;
	}
}

void
Client::handleConnectionFailed(const Event& event, void*)
{
	IDataSocket::ConnectionFailedInfo* info =
		reinterpret_cast<IDataSocket::ConnectionFailedInfo*>(event.getData());

	cleanupTimer();
	cleanupConnecting();
	delete m_stream;
	m_stream = NULL;
	LOG((CLOG_DEBUG1 "connection failed"));
	sendConnectionFailedEvent(info->m_what.c_str());
	delete info;
}

void
Client::handleConnectTimeout(const Event&, void*)
{
	cleanupTimer();
	cleanupConnecting();
	cleanupConnection();
	delete m_stream;
	m_stream = NULL;
	LOG((CLOG_DEBUG1 "connection timed out"));
	sendConnectionFailedEvent("Timed out");
}

void
Client::handleOutputError(const Event&, void*)
{
	cleanupTimer();
	cleanupScreen();
	cleanupConnection();
	LOG((CLOG_WARN "error sending to server"));
	sendEvent(m_events->forClient().disconnected(), NULL);
}

void
Client::handleDisconnected(const Event&, void*)
{
	cleanupTimer();
	cleanupScreen();
	cleanupConnection();
	LOG((CLOG_DEBUG1 "disconnected"));
	sendEvent(m_events->forClient().disconnected(), NULL);
}

void
Client::handleShapeChanged(const Event&, void*)
{
	LOG((CLOG_DEBUG "resolution changed"));
	m_server->onInfoChanged();
}

void
Client::handleClipboardGrabbed(const Event& event, void*)
{
	const IScreen::ClipboardInfo* info =
		reinterpret_cast<const IScreen::ClipboardInfo*>(event.getData());

	// grab ownership
	m_server->onGrabClipboard(info->m_id);

	// we now own the clipboard and it has not been sent to the server
	m_ownClipboard[info->m_id]  = true;
	m_sentClipboard[info->m_id] = false;
	m_timeClipboard[info->m_id] = 0;

	// if we're not the active screen then send the clipboard now,
	// otherwise we'll wait until we leave.
	if (!m_active) {
		sendClipboard(info->m_id);
	}
}

void
Client::handleHello(const Event&, void*)
{
	SInt16 major, minor;
	if (!ProtocolUtil::readf(m_stream, kMsgHello, &major, &minor)) {
		sendConnectionFailedEvent("Protocol error from server");
		cleanupTimer();
		cleanupConnection();
		return;
	}

	// check versions
	LOG((CLOG_DEBUG1 "got hello version %d.%d", major, minor));
	if (major < kProtocolMajorVersion ||
		(major == kProtocolMajorVersion && minor < kProtocolMinorVersion)) {
		sendConnectionFailedEvent(XIncompatibleClient(major, minor).what());
		cleanupTimer();
		cleanupConnection();
		return;
	}

	// say hello back
	LOG((CLOG_DEBUG1 "say hello version %d.%d", kProtocolMajorVersion, kProtocolMinorVersion));
	ProtocolUtil::writef(m_stream, kMsgHelloBack,
							kProtocolMajorVersion,
							kProtocolMinorVersion, &m_name);

	// now connected but waiting to complete handshake
	setupScreen();
	cleanupTimer();

	// make sure we process any remaining messages later.  we won't
	// receive another event for already pending messages so we fake
	// one.
	if (m_stream->isReady()) {
		m_events->addEvent(Event(m_events->forIStream().inputReady(),
							m_stream->getEventTarget()));
	}
}

void
Client::handleSuspend(const Event&, void*)
{
	LOG((CLOG_INFO "suspend"));
	m_suspended       = true;
	bool wasConnected = isConnected();
	disconnect(NULL);
	m_connectOnResume = wasConnected;
}

void
Client::handleResume(const Event&, void*)
{
	LOG((CLOG_INFO "resume"));
	m_suspended = false;
	if (m_connectOnResume) {
		m_connectOnResume = false;
		connect();
	}
}

void
Client::handleFileChunkSending(const Event& event, void*)
{
	sendFileChunk(event.getData());
}

void
Client::handleFileRecieveCompleted(const Event& event, void*)
{
	onFileRecieveCompleted();
}

void
Client::onFileRecieveCompleted()
{
	if (isReceivedFileSizeValid()) {
		m_writeToDropDirThread = new Thread(
			new TMethodJob<Client>(
				this, &Client::writeToDropDirThread));
	}
}


void
Client::writeToDropDirThread(void*)
{
	LOG((CLOG_DEBUG "starting write to drop dir thread"));

	while (m_screen->isFakeDraggingStarted()) {
		ARCH->sleep(.1f);
	}
	
	DropHelper::writeToDir(m_screen->getDropTarget(), m_dragFileList,
					m_receivedFileData);
}

void
Client::clearReceivedFileData()
{
	m_receivedFileData.clear();
}

void
Client::setExpectedFileSize(String data)
{
	std::istringstream iss(data);
	iss >> m_expectedFileSize;
}

void
Client::fileChunkReceived(String data)
{
	m_receivedFileData += data;
}

void
Client::dragInfoReceived(UInt32 fileNum, String data)
{
	if (!m_enableDragDrop) {
		LOG((CLOG_DEBUG "drag drop not enabled, ignoring drag info."));
		return;
	}

	DragInformation::parseDragInfo(m_dragFileList, fileNum, data);
	
	m_screen->startDraggingFiles(m_dragFileList);
}

bool
Client::isReceivedFileSizeValid()
{
	return m_expectedFileSize == m_receivedFileData.size();
}

void
Client::sendFileToServer(const char* filename)
{
	m_sendFileThread = new Thread(
		new TMethodJob<Client>(
			this, &Client::sendFileThread,
			reinterpret_cast<void*>(const_cast<char*>(filename))));
}

void
Client::sendFileThread(void* filename)
{
	try {
		char* name  = reinterpret_cast<char*>(filename);
		FileChunker::sendFileChunks(name, m_events, this);
	}
	catch (std::runtime_error error) {
		LOG((CLOG_ERR "failed sending file chunks: %s", error.what()));
	}

	m_sendFileThread = NULL;
}

void
Client::sendDragInfo(UInt32 fileCount, String& info, size_t size)
{
	m_server->sendDragInfo(fileCount, info.c_str(), size);
}
