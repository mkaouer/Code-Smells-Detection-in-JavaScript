/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */

#pragma once

#include "afxole.h"

/////////////////////////////////////////////////////////////////////////////
// COleDropWndTarget window

class CDropTarget : public COleDropTarget
{
// Construction
public:
  CDropTarget();

// Implementation
public:
  BOOL Initialize(CWnd* wnd);
  void Terminate();
  virtual ~CDropTarget();

  virtual DROPEFFECT OnDragEnter(CWnd* pWnd, COleDataObject* pDataObject,
    DWORD dwKeyState, CPoint point);
  virtual DROPEFFECT OnDragOver(CWnd* pWnd, COleDataObject* pDataObject, 
    DWORD dwKeyState, CPoint point);
  virtual void OnDragLeave(CWnd* pWnd);
  virtual BOOL OnDrop(CWnd* pWnd, COleDataObject* pDataObject, 
    DROPEFFECT dropEffect, CPoint point);

private:
  BOOL m_bRegistered;
};
