/// \file AddDlg.cpp
//-----------------------------------------------------------------------------

#include "stdafx.h"
#include "PasswordSafe.h"

#include "ThisMfcApp.h"
#include "DboxMain.h"
#include "AddDlg.h"
#include "PwFont.h"
#include "corelib/PWCharPool.h"
#include "corelib/PWSprefs.h"
#include "ExpDTDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

//-----------------------------------------------------------------------------
CAddDlg::CAddDlg(CWnd* pParent)
  : CDialog(CAddDlg::IDD, pParent), m_password(_T("")), m_notes(_T("")),
    m_username(_T("")), m_title(_T("")), m_group(_T("")),
    m_URL(_T("")), m_autotype(_T("")), m_tttLTime((time_t)0)
{
  m_isExpanded = PWSprefs::GetInstance()->
    GetPref(PWSprefs::DisplayExpandedAddEditDlg);
}


BOOL CAddDlg::OnInitDialog() 
{
  CDialog::OnInitDialog();
 
  SetPasswordFont(GetDlgItem(IDC_PASSWORD));
  ResizeDialog();

  return TRUE;
}


void CAddDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Text(pDX, IDC_PASSWORD, (CString&)m_password);
	DDX_Text(pDX, IDC_NOTES, (CString&)m_notes);
	DDX_Text(pDX, IDC_USERNAME, (CString&)m_username);
	DDX_Text(pDX, IDC_TITLE, (CString&)m_title);
	DDX_Text(pDX, IDC_LTIME, (CString&)m_ascLTime);

	if(!pDX->m_bSaveAndValidate) {
		// We are initializing the dialog.  Populate the groups combo box.
		CComboBox comboGroup;
		comboGroup.Attach(GetDlgItem(IDC_GROUP)->GetSafeHwnd());
		// For some reason, MFC calls us twice when initializing.
		// Populate the combo box only once.
		if(0 == comboGroup.GetCount()) {
			CStringArray aryGroups;
			app.m_core.GetUniqueGroups(aryGroups);
			for(int igrp=0; igrp<aryGroups.GetSize(); igrp++) {
				comboGroup.AddString((LPCTSTR)aryGroups[igrp]);
			}
		}
		comboGroup.Detach();
	}
	DDX_CBString(pDX, IDC_GROUP, (CString&)m_group);
	DDX_Text(pDX, IDC_URL, (CString&)m_URL);
	DDX_Text(pDX, IDC_AUTOTYPE, (CString&)m_autotype);
	DDX_Control(pDX, IDC_MORE, m_moreLessBtn);
}


BEGIN_MESSAGE_MAP(CAddDlg, CDialog)
   ON_BN_CLICKED(ID_HELP, OnHelp)
   ON_BN_CLICKED(IDC_RANDOM, OnRandom)
   ON_BN_CLICKED(IDC_MORE, OnBnClickedMore)
   ON_BN_CLICKED(IDOK, OnBnClickedOk)
   ON_BN_CLICKED(IDC_LTIME_CLEAR, OnBnClickedClearLTime)
   ON_BN_CLICKED(IDC_LTIME_SET, OnBnClickedSetLTime)
END_MESSAGE_MAP()


void
CAddDlg::OnCancel() 
{
  CDialog::OnCancel();
}


void
CAddDlg::OnOK() 
{
  UpdateData(TRUE);

  //Check that data is valid
  if (m_title.IsEmpty()) {
    AfxMessageBox(_T("This entry must have a title."));
    ((CEdit*)GetDlgItem(IDC_TITLE))->SetFocus();
    return;
  }
  if (m_password.IsEmpty()) {
    AfxMessageBox(_T("This entry must have a password."));
    ((CEdit*)GetDlgItem(IDC_PASSWORD))->SetFocus();
    return;
  }
  if (!m_group.IsEmpty() && m_group[0] == '.') {
    AfxMessageBox(_T("A dot is invalid as the first character of the Group field."));
    ((CEdit*)GetDlgItem(IDC_GROUP))->SetFocus();
    return;
  }
  //End check

  DboxMain* pParent = (DboxMain*) GetParent();
  ASSERT(pParent != NULL);

  if (pParent->Find(m_group, m_title, m_username) != NULL) {
    CMyString temp =
      _T("An item with Group \"") + m_group
      + _T("\", Title \"") + m_title 
      + _T("\" and User Name \"") + m_username
      + _T("\" already exists.");
    AfxMessageBox(temp);
    ((CEdit*)GetDlgItem(IDC_TITLE))->SetSel(MAKEWORD(-1, 0));
    ((CEdit*)GetDlgItem(IDC_TITLE))->SetFocus();
  } else {
    CDialog::OnOK();
  }
}


void CAddDlg::OnHelp() 
{
#if defined(POCKET_PC)
  CreateProcess( _T("PegHelp.exe"), _T("pws_ce_help.html#adddata"), NULL, NULL, FALSE, 0, NULL, NULL, NULL, NULL );
#else
  //WinHelp(0x2008E, HELP_CONTEXT);
  ::HtmlHelp(NULL,
             "pwsafe.chm::/html/entering_pwd.html",
             HH_DISPLAY_TOPIC, 0);
#endif
}


void CAddDlg::OnRandom() 
{
  DboxMain* pParent = (DboxMain*)GetParent();
  ASSERT(pParent != NULL);

  UpdateData(TRUE);
  if (pParent->MakeRandomPassword(this, m_password))
    UpdateData(FALSE);
}
//-----------------------------------------------------------------------------

void CAddDlg::OnBnClickedMore()
{
  m_isExpanded = !m_isExpanded;
  PWSprefs::GetInstance()->
    SetPref(PWSprefs::DisplayExpandedAddEditDlg, m_isExpanded);
  ResizeDialog();
}


void CAddDlg::OnBnClickedOk()
{
	OnOK();
}

void CAddDlg::ResizeDialog()
{
	int TopHideableControl = IDC_TOP_HIDEABLE;
	int BottomHideableControl = IDC_BOTTOM_HIDEABLE;
	int controls[]={
IDC_URL,
IDC_AUTOTYPE,
IDC_STATIC_URL,
		IDC_STATIC_AUTO,
		IDC_LTIME,
		IDC_STATIC_LTIME,
		IDC_LTIME_CLEAR,
		IDC_LTIME_SET,
		IDC_STATIC_DTEXPGROUP
	};	
	
	for(int n = 0; n<sizeof(controls)/sizeof(IDC_URL);n++)
	{
		CWnd* pWind;
		pWind = (CWnd *)GetDlgItem(controls[n]);
		pWind->ShowWindow(m_isExpanded);
	}
	
	RECT curDialogRect;
	
	this->GetWindowRect(&curDialogRect);

	RECT newDialogRect=curDialogRect;


	RECT curLowestCtlRect;
	CWnd* pLowestCtl;
	int newHeight;
  if (m_isExpanded) {
    // from less to more
	  pLowestCtl = (CWnd *)GetDlgItem(BottomHideableControl);
	  
	  pLowestCtl->GetWindowRect(&curLowestCtlRect);

	  newHeight =  curLowestCtlRect.bottom + 15  - newDialogRect.top;
    m_moreLessBtn.SetWindowText(_T("<< Less"));
  } else {
    
	  // from more to less
	  pLowestCtl = (CWnd *)GetDlgItem(TopHideableControl);
	  pLowestCtl->GetWindowRect(&curLowestCtlRect);

	  newHeight =  curLowestCtlRect.top + 5  - newDialogRect.top;

    m_moreLessBtn.SetWindowText(_T("More >>"));
  }
  

  this->SetWindowPos(NULL,0,0,
		newDialogRect.right - newDialogRect.left ,
		newHeight , 
		SWP_NOMOVE );

}
void CAddDlg::OnBnClickedClearLTime()
{
	GetDlgItem(IDC_LTIME)->SetWindowText(_T("Never"));
	m_ascLTime = _T("Never");
	m_tttLTime = (time_t)0;
}
void CAddDlg::OnBnClickedSetLTime()
{
	CExpDTDlg dlg_expDT(this);

	dlg_expDT.m_ascLTime = m_ascLTime;

	app.DisableAccelerator();
	int rc = dlg_expDT.DoModal();
	app.EnableAccelerator();

	if (rc == IDOK) {
		m_tttLTime = dlg_expDT.m_tttLTime;
		m_ascLTime = dlg_expDT.m_ascLTime;
		GetDlgItem(IDC_LTIME)->SetWindowText(m_ascLTime);
	}
}

