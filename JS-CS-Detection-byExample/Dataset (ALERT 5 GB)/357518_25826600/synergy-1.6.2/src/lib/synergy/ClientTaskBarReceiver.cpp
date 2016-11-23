/*
 * synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2012 Synergy Si Ltd.
 * Copyright (C) 2003 Chris Schoeneman
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

#include "synergy/ClientTaskBarReceiver.h"
#include "client/Client.h"
#include "mt/Lock.h"
#include "base/String.h"
#include "base/IEventQueue.h"
#include "arch/Arch.h"
#include "common/Version.h"

//
// ClientTaskBarReceiver
//

ClientTaskBarReceiver::ClientTaskBarReceiver(IEventQueue* events) :
	m_state(kNotRunning),
	m_events(events)
{
	// do nothing
}

ClientTaskBarReceiver::~ClientTaskBarReceiver()
{
	// do nothing
}

void
ClientTaskBarReceiver::updateStatus(Client* client, const String& errorMsg)
{
	{
		// update our status
		m_errorMessage = errorMsg;
		if (client == NULL) {
			if (m_errorMessage.empty()) {
				m_state = kNotRunning;
			}
			else {
				m_state = kNotWorking;
			}
		}
		else {
			m_server = client->getServerAddress().getHostname();

			if (client->isConnected()) {
				m_state = kConnected;
			}
			else if (client->isConnecting()) {
				m_state = kConnecting;
			}
			else {
				m_state = kNotConnected;
			}
		}

		// let subclasses have a go
		onStatusChanged(client);
	}

	// tell task bar
	ARCH->updateReceiver(this);
}

ClientTaskBarReceiver::EState
ClientTaskBarReceiver::getStatus() const
{
	return m_state;
}

const String&
ClientTaskBarReceiver::getErrorMessage() const
{
	return m_errorMessage;
}

void
ClientTaskBarReceiver::quit()
{
	m_events->addEvent(Event(Event::kQuit));
}

void
ClientTaskBarReceiver::onStatusChanged(Client*)
{
	// do nothing
}

void
ClientTaskBarReceiver::lock() const
{
	// do nothing
}

void
ClientTaskBarReceiver::unlock() const
{
	// do nothing
}

std::string
ClientTaskBarReceiver::getToolTip() const
{
	switch (m_state) {
	case kNotRunning:
		return synergy::string::sprintf("%s:  Not running", kAppVersion);

	case kNotWorking:
		return synergy::string::sprintf("%s:  %s",
								kAppVersion, m_errorMessage.c_str());

	case kNotConnected:
		return synergy::string::sprintf("%s:  Not connected:  %s",
								kAppVersion, m_errorMessage.c_str());

	case kConnecting:
		return synergy::string::sprintf("%s:  Connecting to %s...",
								kAppVersion, m_server.c_str());

	case kConnected:
		return synergy::string::sprintf("%s:  Connected to %s",
								kAppVersion, m_server.c_str());

	default:
		return "";
	}
}
