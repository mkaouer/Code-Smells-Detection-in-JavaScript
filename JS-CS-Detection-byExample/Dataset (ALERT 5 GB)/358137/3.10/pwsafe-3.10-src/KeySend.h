/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
#pragma once

// 
// KeySend.h
// thedavecollins 2004-08-07
// sends keystrokes
//-----------------------------------------------------------------------------

#include "corelib/PWScore.h"
#include "PasswordSafe.h"

class CKeySend
{
public:
	CKeySend(void);
	~CKeySend(void);
	void SendString(const CMyString &data);
	void ResetKeyboardState();
	void SendChar(TCHAR c);
	void SetDelay(int d);
	void SetAndDelay(int d);
    void SetCapsLock(const bool bstate);

private:
	int m_delay;
	HKL m_hlocale;
};

