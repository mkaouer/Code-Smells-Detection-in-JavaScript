/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
#pragma once

// OptionsSystem.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// COptionsSystem dialog

class COptionsSystem : public CPropertyPage
{
	DECLARE_DYNCREATE(COptionsSystem)

// Construction
public:
	COptionsSystem();
	~COptionsSystem();

// Dialog Data
	//{{AFX_DATA(COptionsSystem)
	enum { IDD = IDD_PS_SYSTEM };
	int		m_maxreitems;
	BOOL    m_usesystemtray;
	BOOL    m_startup;
	int		m_maxmruitems;
	BOOL	m_mruonfilemenu;
	BOOL	m_deleteregistry;
	//}}AFX_DATA

// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(COptionsSystem)
protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	// Generated message map functions
	//{{AFX_MSG(COptionsSystem)
	afx_msg void OnUseSystemTray();
	afx_msg void OnStartup();
	afx_msg void OnSetDeleteRegistry();
	afx_msg void OnApplyRegistryDeleteNow();
	virtual BOOL OnInitDialog();
	afx_msg BOOL OnKillActive();
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()

// Implementation
protected:
	BOOL PreTranslateMessage(MSG* pMsg);

private:
	CToolTipCtrl* m_ToolTipCtrl;
};
