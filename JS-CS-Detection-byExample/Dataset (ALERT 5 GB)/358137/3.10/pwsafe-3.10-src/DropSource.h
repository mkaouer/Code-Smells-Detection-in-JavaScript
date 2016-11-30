/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */

#pragma once

#include "afxole.h"
// Drag and Drop source
// Note values to stop D&D between instances where data is of different lengths
enum {
  FROMCC = 0,
  FROMHDR = 1, 
  FROMTREE = 2,
};

class CDataSource : protected COleDataSource
{
public:
  CDataSource();
  virtual ~CDataSource();
  virtual DROPEFFECT StartDragging(BYTE *szData, DWORD dwLength,
                                   CLIPFORMAT cpfmt, RECT* rClient, CPoint* ptMousePos);

protected:
  virtual void CompleteMove() {};

private:
};

