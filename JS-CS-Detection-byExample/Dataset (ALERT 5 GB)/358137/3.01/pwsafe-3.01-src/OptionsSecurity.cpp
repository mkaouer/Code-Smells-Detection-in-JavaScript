// OptionsSecurity.cpp : implementation file
//

#include "stdafx.h"
#include "passwordsafe.h"
#include "corelib/PwsPlatform.h"


#if defined(POCKET_PC)
  #include "pocketpc/resource.h"
#else
  #include "resource.h"
#endif
#include "OptionsSecurity.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// COptionsSecurity property page

IMPLEMENT_DYNCREATE(COptionsSecurity, CPropertyPage)

COptionsSecurity::COptionsSecurity() : CPropertyPage(COptionsSecurity::IDD)
{
	//{{AFX_DATA_INIT(COptionsSecurity)
	//}}AFX_DATA_INIT
}

COptionsSecurity::~COptionsSecurity()
{
}

void COptionsSecurity::DoDataExchange(CDataExchange* pDX)
{
	CPropertyPage::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(COptionsSecurity)
	DDX_Check(pDX, IDC_CLEARBOARD, m_clearclipboard);
	DDX_Check(pDX, IDC_LOCKBASE, m_lockdatabase);
	DDX_Check(pDX, IDC_SAVEMINIMIZE, m_confirmsaveonminimize);
	DDX_Check(pDX, IDC_CONFIRMCOPY, m_confirmcopy);
	DDX_Check(pDX, IDC_LOCKONSCREEN, m_LockOnWindowLock);
	DDX_Check(pDX, IDC_LOCK_TIMER, m_LockOnIdleTimeout);
	DDX_Text(pDX, IDC_IDLE_TIMEOUT, m_IdleTimeOut);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(COptionsSecurity, CPropertyPage)
	//{{AFX_MSG_MAP(COptionsSecurity)
	ON_BN_CLICKED(IDC_LOCKBASE, OnLockbase)
	ON_BN_CLICKED(IDC_LOCK_TIMER, OnLockbase)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// COptionsSecurity message handlers

void COptionsSecurity::OnLockbase() 
{
  if (((CButton*)GetDlgItem(IDC_LOCKBASE))->GetCheck() == 1)
    GetDlgItem(IDC_SAVEMINIMIZE)->EnableWindow(TRUE);
  else
    GetDlgItem(IDC_SAVEMINIMIZE)->EnableWindow(FALSE);

  if (((CButton*)GetDlgItem(IDC_LOCK_TIMER))->GetCheck() == 1) {
    GetDlgItem(IDC_SPIN2)->EnableWindow(TRUE);
    GetDlgItem(IDC_IDLE_TIMEOUT)->EnableWindow(TRUE);
  } else {
    GetDlgItem(IDC_SPIN2)->EnableWindow(FALSE);
    GetDlgItem(IDC_IDLE_TIMEOUT)->EnableWindow(FALSE);
  }

}

BOOL COptionsSecurity::OnInitDialog() 
{
  CPropertyPage::OnInitDialog();
	
  OnLockbase();
  CSpinButtonCtrl*  pspin = (CSpinButtonCtrl *)GetDlgItem(IDC_SPIN2);

  pspin->SetBuddy(GetDlgItem(IDC_IDLE_TIMEOUT));
  pspin->SetRange(1, 120);
  pspin->SetBase(10);
  pspin->SetPos(m_IdleTimeOut);
	
  return TRUE;  // return TRUE unless you set the focus to a control
  // EXCEPTION: OCX Property Pages should return FALSE
}
