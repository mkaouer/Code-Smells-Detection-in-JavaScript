#if !defined(AFX_OPTIONSUSERNAME_H__D5D840BC_C021_4BFA_AC26_19FB878B582D__INCLUDED_)
#define AFX_OPTIONSUSERNAME_H__D5D840BC_C021_4BFA_AC26_19FB878B582D__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// OptionsUsername.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// COptionsUsername dialog

class COptionsUsername : public CPropertyPage
{
	DECLARE_DYNCREATE(COptionsUsername)

// Construction
public:
	COptionsUsername();
	~COptionsUsername();

// Dialog Data
	//{{AFX_DATA(COptionsUsername)
	enum { IDD = IDD_PS_USERNAME };
	BOOL	m_usedefuser;
	BOOL	m_querysetdef;
	BOOL	m_queryaddname;
	CString	m_defusername;
	//}}AFX_DATA


// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(COptionsUsername)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	// Generated message map functions
	//{{AFX_MSG(COptionsUsername)
	afx_msg void OnUsedefuser();
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()

};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_OPTIONSUSERNAME_H__D5D840BC_C021_4BFA_AC26_19FB878B582D__INCLUDED_)
