/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
#ifndef __PWSCLIPBOARD_H
/** \file
 * A small utility class to hadle the clipboard
 * securely. Specifically, we keep a hash
 * of the data that we put on the clipboard, so that
 * ClearData() only clears the clipboard if it has what we put on it, and
 * if isSensitive was true when we added it.
 * UnconditionalyClearData clears the clipboard of data of all formats
 */

#include "sha256.h"

#if defined(UNICODE)
#define CLIPBOARD_TEXT_FORMAT	CF_UNICODETEXT
#else
#define CLIPBOARD_TEXT_FORMAT	CF_TEXT
#endif

#include "MyString.h"

class PWSclipboard
{
public:
  PWSclipboard();
  ~PWSclipboard();

  bool SetData(const CMyString &data,
               bool isSensitive = true,
               CLIPFORMAT cfFormat = CLIPBOARD_TEXT_FORMAT);
               // returns true if succeeded
  bool ClearData(); // return true if cleared or if data wasn't ours

private:
  bool m_set;
  unsigned char m_digest[SHA256::HASHLEN];
};

#define __PWSCLIPBOARD_H
#endif /* __PWSCLIPBOARD_H */
