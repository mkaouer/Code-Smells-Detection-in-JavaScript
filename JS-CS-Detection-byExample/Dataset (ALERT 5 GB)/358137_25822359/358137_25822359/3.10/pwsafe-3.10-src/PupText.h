/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
////////////////////////////////////////////////////////////////
// MSDN Magazine -- November 2003
// If this code works, it was written by Paul DiLascia.
// If not, I don't know who wrote it.
// Compiles with Visual Studio .NET on Windows XP. Tab size=3.
//
#pragma once

//////////////////
// Get NONCLIENTMETRICS info: ctor calls SystemParametersInfo.
//
class CNonClientMetrics : public NONCLIENTMETRICS {
public:
	CNonClientMetrics() {
		cbSize = sizeof(NONCLIENTMETRICS);
		SystemParametersInfo(SPI_GETNONCLIENTMETRICS,0,this,0);
	}
};

//////////////////
// Popup text window, like tooltip.
// Can be right or left justified relative to creation point.
//
class CPopupText : public CWnd {
public:
	CSize m_szMargins;	// extra space around text: change if you like
	CFont m_font;		// font: change if you like

	CPopupText();
	virtual ~CPopupText();

	BOOL Create(CPoint pt, CWnd* pParentWnd, UINT nID=0);
	void ShowDelayed(UINT msec);
	void Cancel();

protected:
	virtual void PostNcDestroy();
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);

	void DrawText(CDC& dc, LPCTSTR lpText, CRect& rc, UINT flags);

	afx_msg void OnPaint();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg LRESULT OnSetText(WPARAM wp, LPARAM lp);
	DECLARE_DYNAMIC(CPopupText);
	DECLARE_MESSAGE_MAP();
};
