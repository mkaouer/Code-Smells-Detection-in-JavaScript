/// \file PasskeyEntry.cpp
//-----------------------------------------------------------------------------

/*
  Passkey?  That's Russian for 'pass'.  You know, passkey
  down the streetsky.  [Groucho Marx]
*/

#include "PasswordSafe.h"
#include "corelib/PwsPlatform.h"
#include "ThisMfcApp.h"

#if defined(POCKET_PC)
  #include "pocketpc/resource.h"
  #include "pocketpc/PocketPC.h"
#else
  #include "resource.h"
#endif

#include "corelib/MyString.h"

#include "SysColStatic.h"

#include "PasskeyEntry.h"
#include "TryAgainDlg.h"
#include "DboxMain.h" // for CheckPassword()

#include "corelib/Util.h"

//-----------------------------------------------------------------------------
CPasskeyEntry::CPasskeyEntry(CWnd* pParent,
                             const CString& a_filespec,
                             bool first)
   : super(first ? CPasskeyEntry::IDD : CPasskeyEntry::IDD_BASIC,
             pParent),
     m_first(first),
     m_filespec(a_filespec),
     m_tries(0),
     m_status(TAR_INVALID)
{
   const int FILE_DISP_LEN = 45;	

	//{{AFX_DATA_INIT(CPasskeyEntry)
	//}}AFX_DATA_INIT

   DBGMSG("CPasskeyEntry()\n");
   if (first) {
      DBGMSG("** FIRST **\n");
   }

   m_passkey = _T("");

   m_hIcon = app.LoadIcon(IDI_CORNERICON);

   if (a_filespec.GetLength() > FILE_DISP_LEN) {
// m_message = a_filespec.Right(FILE_DISP_LEN - 3); // truncate for display
// m_message.Insert(0, _T("..."));
// changed by karel@VanderGucht.de to see beginning + ending of 'a_filespec'
      m_message =  a_filespec.Left(FILE_DISP_LEN/2-5) + " ... " + a_filespec.Right(FILE_DISP_LEN/2);
   }

   else

   {
	   m_message = a_filespec;
   }
}


void CPasskeyEntry::DoDataExchange(CDataExchange* pDX)
{
   super::DoDataExchange(pDX);
   DDX_Text(pDX, IDC_PASSKEY, (CString &)m_passkey);

#if !defined(POCKET_PC)
   if ( m_first )
	DDX_Control(pDX, IDC_STATIC_LOGOTEXT, m_ctlLogoText);
#endif

   //{{AFX_DATA_MAP(CPasskeyEntry)
#if !defined(POCKET_PC)
	DDX_Control(pDX, IDC_STATIC_LOGO, m_ctlLogo);
	DDX_Control(pDX, IDOK, m_ctlOK);
#endif
	DDX_Control(pDX, IDC_PASSKEY, m_ctlPasskey);
   DDX_Text(pDX, IDC_MESSAGE, m_message);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CPasskeyEntry, super)
	//{{AFX_MSG_MAP(CPasskeyEntry)
   ON_BN_CLICKED(ID_HELP, OnHelp)
   ON_BN_CLICKED(ID_BROWSE, OnBrowse)
   ON_BN_CLICKED(ID_CREATE_DB, OnCreateDb)
#if defined(POCKET_PC)
   ON_EN_SETFOCUS(IDC_PASSKEY, OnPasskeySetfocus)
   ON_EN_KILLFOCUS(IDC_PASSKEY, OnPasskeyKillfocus)
#endif
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()


BOOL
CPasskeyEntry::OnInitDialog(void)
{
#if defined(POCKET_PC)
   // If displaying IDD_PASSKEYENTRY_FIRST then bypass superclass and go
   // directly to CDialog::OnInitDialog() and display the dialog fullscreen
   // otherwise display as a centred dialogue.
   if ( m_nIDHelp == IDD )
   {
	   super::super::OnInitDialog();
   }
   else
   {
#endif
   super::OnInitDialog();
#if defined(POCKET_PC)
   }
#endif

   if (m_message.IsEmpty() && m_first)
   {
      m_ctlPasskey.EnableWindow(FALSE);
#if !defined(POCKET_PC)
      m_ctlOK.EnableWindow(FALSE);
#endif
      m_message = _T("[No current database]");
   }
   /*
    * this bit makes the background come out right on
    * the bitmaps
    */

#if !defined(POCKET_PC)
   if (m_first)
   {
      m_ctlLogoText.ReloadBitmap(IDB_PSLOGO);
      m_ctlLogo.ReloadBitmap(IDB_CLOGO);
    }
  else
    {
      m_ctlLogo.ReloadBitmap(IDB_CLOGO_SMALL);
   }
#endif 

   // Set the icon for this dialog.  The framework does this automatically
   //  when the application's main window is not a dialog

   SetIcon(m_hIcon, TRUE);  // Set big icon
   SetIcon(m_hIcon, FALSE); // Set small icon

  return TRUE;
}


#if defined(POCKET_PC)
/************************************************************************/
/* Restore the state of word completion when the password field loses   */
/* focus.                                                               */
/************************************************************************/
void CPasskeyEntry::OnPasskeyKillfocus()
{
	EnableWordCompletion( m_hWnd );
}


/************************************************************************/
/* When the password field is activated, pull up the SIP and disable    */
/* word completion.                                                     */
/************************************************************************/
void CPasskeyEntry::OnPasskeySetfocus()
{
	DisableWordCompletion( m_hWnd );
}
#endif


void
CPasskeyEntry::OnBrowse()
{
   m_status = TAR_OPEN;
   app.m_pMainWnd = NULL;
   super::OnCancel();
}


void
CPasskeyEntry::OnCreateDb()
{
   m_status = TAR_NEW;
   app.m_pMainWnd = NULL;
   super::OnCancel();
}


void
CPasskeyEntry::OnCancel() 
{
   app.m_pMainWnd = NULL;
   super::OnCancel();
}


void
CPasskeyEntry::OnOK() 
{
  UpdateData(TRUE);

  if (m_passkey.IsEmpty())
    {
      AfxMessageBox("The combination cannot be blank.");
      m_ctlPasskey.SetFocus();
      return;
    }

  DboxMain* pParent = (DboxMain*) GetParent();
  ASSERT(pParent != NULL);
  if (pParent->CheckPassword(m_filespec, m_passkey) != PWScore::SUCCESS)
    {
      if (m_tries >= 2)
	{
	  CTryAgainDlg errorDlg(this);

         int nResponse = errorDlg.DoModal();
         if (nResponse == IDOK)
         {
         }
         else if (nResponse == IDCANCEL)
         {
            m_status = errorDlg.GetCancelReturnValue();
            app.m_pMainWnd = NULL;
            super::OnCancel();
         }
      }
      else
      {
         m_tries++;
         AfxMessageBox(_T("Incorrect passkey"));
         m_ctlPasskey.SetSel(MAKEWORD(-1, 0));
         m_ctlPasskey.SetFocus();
      }
   }
   else
   {
      app.m_pMainWnd = NULL;
      super::OnOK();
   }
}


void
CPasskeyEntry::OnHelp() 
{
#if defined(POCKET_PC)
	CreateProcess( _T("PegHelp.exe"), _T("pws_ce_help.html#comboentry"), NULL, NULL, FALSE, 0, NULL, NULL, NULL, NULL );
#else
   //WinHelp(0x200B9, HELP_CONTEXT);
   ::HtmlHelp(NULL,
              "pwsafe.chm::/html/pws_combo_entry.htm",
              HH_DISPLAY_TOPIC, 0);
#endif
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
