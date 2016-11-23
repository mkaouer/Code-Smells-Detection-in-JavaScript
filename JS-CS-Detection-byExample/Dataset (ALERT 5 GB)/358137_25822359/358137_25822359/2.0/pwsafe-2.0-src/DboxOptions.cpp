/// \file DboxMain.cpp
//-----------------------------------------------------------------------------

#include "PasswordSafe.h"

#include "ThisMfcApp.h"
#if defined(POCKET_PC)
  #include "pocketpc/resource.h"
#else
  #include "resource.h"
#endif

// dialog boxen
#include "DboxMain.h"
#include "OptionsSecurity.h"
#include "OptionsDisplay.h"
#include "OptionsUsername.h"
#include "OptionsPasswordPolicy.h"
#include "OptionsMisc.h"


#include <afxpriv.h>


#define not(x) ((x) ? 0 : 1)

void
DboxMain::OnOptions() 
{
   CPropertySheet optionsDlg(_T("Options"), this);

   COptionsDisplay         display;
   COptionsSecurity        security;
   COptionsPasswordPolicy  passwordpolicy;
   COptionsUsername        username;
   COptionsMisc            misc;

   /*
   **  Initialize the property pages values.
   */
   display.m_alwaysontop = m_bAlwaysOnTop;
   display.m_pwshowinedit = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("showpwdefault"), FALSE);
   display.m_pwshowinlist = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("showpwinlist"), FALSE);
#if defined(POCKET_PC)
   display.m_dcshowspassword = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("dcshowspassword"), FALSE);
#endif

   security.m_clearclipboard = (app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("dontaskminimizeclearyesno"), FALSE));
   security.m_lockdatabase = (app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("databaseclear"), FALSE));
   security.m_confirmsaveonminimize = not(app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("dontasksaveminimize"), FALSE));
   security.m_confirmcopy = not(app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("dontaskquestion"), FALSE));

   passwordpolicy.m_pwlendefault = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pwlendefault"), 8);
   passwordpolicy.m_pwuselowercase = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pwuselowercase"), TRUE);
   passwordpolicy.m_pwuseuppercase = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pwuseuppercase"), TRUE);
   passwordpolicy.m_pwusedigits = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pwusedigits"), TRUE);
   passwordpolicy.m_pwusesymbols = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pwusesymbols"), FALSE);
   passwordpolicy.m_pweasyvision = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("pweasyvision"), FALSE);

   username.m_usedefuser = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("usedefuser"), FALSE);
   username.m_defusername = app.GetProfileString(_T(PWS_REG_OPTIONS), _T("defusername"), _T(""));
   username.m_querysetdef = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("querysetdef"), TRUE);

   misc.m_confirmdelete = not(app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("deletequestion"), FALSE));
   misc.m_saveimmediately = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("saveimmediately"), TRUE);

   optionsDlg.AddPage( &display );
   optionsDlg.AddPage( &security );
   optionsDlg.AddPage( &passwordpolicy );
   optionsDlg.AddPage( &username );
   optionsDlg.AddPage( &misc );


   /*
   **  Remove the "Apply Now" button.
   */
   optionsDlg.m_psh.dwFlags |= PSH_NOAPPLYNOW;
   int rc = optionsDlg.DoModal();

   if (rc == IDOK)
   {
      /*
      **  First save all the options.
      */
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("alwaysontop"),     display.m_alwaysontop);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("showpwdefault"),   display.m_pwshowinedit);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("showpwinlist"),    display.m_pwshowinlist);
#if defined(POCKET_PC)
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("dcshowspassword"), display.m_dcshowspassword);
#endif

      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("dontaskminimizeclearyesno"),  security.m_clearclipboard);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("databaseclear"),              security.m_lockdatabase);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("dontasksaveminimize"),    not(security.m_confirmsaveonminimize));
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("dontaskquestion"),        not(security.m_confirmcopy));

      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pwlendefault"),    passwordpolicy.m_pwlendefault);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pwuselowercase"),  passwordpolicy.m_pwuselowercase);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pwuseuppercase"),  passwordpolicy.m_pwuseuppercase);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pwusedigits"),     passwordpolicy.m_pwusedigits);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pwusesymbols"),    passwordpolicy.m_pwusesymbols);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("pweasyvision"),    passwordpolicy.m_pweasyvision);

      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("usedefuser"),      username.m_usedefuser);
      app.WriteProfileString(_T(PWS_REG_OPTIONS), _T("defusername"),   username.m_defusername);
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("querysetdef"),     username.m_querysetdef);

      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("deletequestion"),  not(misc.m_confirmdelete));
      app.WriteProfileInt(_T(PWS_REG_OPTIONS),	_T("saveimmediately"),     misc.m_saveimmediately);

      /*
      **  Now update the application according to the options.
      */
      m_bAlwaysOnTop = display.m_alwaysontop;
      UpdateAlwaysOnTop();
      bool bOldShowPasswordInList = m_bShowPasswordInList;
      m_bShowPasswordInList = app.GetProfileInt(_T(PWS_REG_OPTIONS), _T("showpwinlist"), FALSE)? true: false;

      if (bOldShowPasswordInList != m_bShowPasswordInList)
	RefreshList();

      /*
       * Here are the old (pre 2.0) semantics:
       * The username entered in this dialog box will be added to all the entries
       * in the username-less database that you just opened. Click Ok to add the
       * username or Cancel to leave them as is.
       *
       * You can also set this username to be the default username by clicking the
       * check box.  In this case, you will not see the username that you just added
       * in the main dialog (though it is still part of the entries), and it will
       * automatically be inserted in the Add dialog for new entries.
       *
       * To me (ronys), these seem too complicated, and not useful once password files
       * have been converted to the old (username-less) format to 1.9 (with usernames).
       * (Not to mention 2.0).
       * Therefore, the username will now only be a default value to be used in new entries,
       * and in converting pre-2.0 databases.
       */

      m_core.SetDefUsername(username.m_defusername);
      m_core.SetUseDefUser(username.m_usedefuser == TRUE ? true : false);

   }
   else if (rc == IDCANCEL)
   {
   }
}

