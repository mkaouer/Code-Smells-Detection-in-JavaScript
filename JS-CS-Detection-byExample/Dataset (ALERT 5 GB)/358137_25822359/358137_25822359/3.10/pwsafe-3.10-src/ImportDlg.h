/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
#pragma once
// ImportDlg.h : header file
//

#include "PWDialog.h"

void AFXAPI DDV_CheckImpDelimiter(CDataExchange* pDX, const CString &delimiter);

/////////////////////////////////////////////////////////////////////////////
// CImportDlg dialog

class CImportDlg : public CPWDialog
{
// Construction
public:
	CImportDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CImportDlg)
	enum { IDD = IDD_IMPORT_TEXT };
	CString	m_groupName;
	CString	m_Separator;
	CString m_defimpdelim;
	int m_tab;
	int m_group;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CImportDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CImportDlg)
	afx_msg void OnOther();
	afx_msg void OnComma();
	afx_msg void OnTab();
	afx_msg void OnNoGroup();
	afx_msg void OnYesGroup();
	afx_msg void OnHelp();
	virtual void OnOK();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
