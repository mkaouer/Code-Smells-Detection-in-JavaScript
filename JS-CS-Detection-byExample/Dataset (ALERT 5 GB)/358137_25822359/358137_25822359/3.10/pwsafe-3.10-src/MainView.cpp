/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
/// file MainView.cpp
//
// View-related methods of DboxMain
//-----------------------------------------------------------------------------

#include "PasswordSafe.h"

#include "ThisMfcApp.h"

#if defined(POCKET_PC)
  #include "pocketpc/resource.h"
#else
  #include "resource.h"
  #include "resource2.h"  // Menu, Toolbar & Accelerator resources
  #include "resource3.h"  // String resources
#endif

#include "DboxMain.h"
#include "TryAgainDlg.h"
#include "ColumnChooserDlg.h"

#include "corelib/pwsprefs.h"
#include "corelib/UUIDGen.h"

#include "commctrl.h"
#include <vector>
#include <algorithm>

using namespace std;

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif


//-----------------------------------------------------------------------------

  /*
   * Compare function used by m_ctlItemList.SortItems()
   * "The comparison function must return a negative value if the first item should precede 
   * the second, a positive value if the first item should follow the second, or zero if
   * the two items are equivalent."
   *
   * If sorting is by group (title/user), title (username), username (title) 
   * fields in brackets are the secondary fields if the primary fields are identical.
   */
int CALLBACK DboxMain::CompareFunc(LPARAM lParam1, LPARAM lParam2,
				   LPARAM closure)
{
  // closure is "this" of the calling DboxMain, from which we use:
  // m_iSortedColumn to determine which column is getting sorted:
  // 0 - title
  // 1 - user name
  // 2 - note
  // 3 - password
  // m_bSortAscending to determine the direction of the sort (duh)

  DboxMain *self = (DboxMain*)closure;
  int nSortColumn = self->m_iSortedColumn;
  CItemData* pLHS = (CItemData *)lParam1;
  CItemData* pRHS = (CItemData *)lParam2;
  CMyString	group1, group2;
  time_t t1, t2;

  int iResult;
  switch(nSortColumn) {
    case CItemData::GROUP:
      group1 = pLHS->GetGroup();
      group2 = pRHS->GetGroup();
      if (group1.IsEmpty())  // root?
        group1 = _T("\xff");
      if (group2.IsEmpty())  // root?
        group2 = _T("\xff");
      iResult = group1.CompareNoCase(group2);
      if (iResult == 0) {
        iResult = (pLHS->GetTitle()).CompareNoCase(pRHS->GetTitle());
        if (iResult == 0) {
          iResult = (pLHS->GetUser()).CompareNoCase(pRHS->GetUser());
        }
      }
      break;
    case CItemData::TITLE:
      iResult = (pLHS->GetTitle()).CompareNoCase(pRHS->GetTitle());
      if (iResult == 0) {
        iResult = (pLHS->GetUser()).CompareNoCase(pRHS->GetUser());
      }
      break;
    case CItemData::USER:
      iResult = (pLHS->GetUser()).CompareNoCase(pRHS->GetUser());
      if (iResult == 0) {
        iResult = (pLHS->GetTitle()).CompareNoCase(pRHS->GetTitle());
      }
      break;
    case CItemData::NOTES:
      iResult = (pLHS->GetNotes()).CompareNoCase(pRHS->GetNotes());
      break;
    case CItemData::PASSWORD:
      iResult = (pLHS->GetPassword()).CompareNoCase(pRHS->GetPassword());
      break;
    case CItemData::CTIME:
      pLHS->GetCTime(t1);
      pRHS->GetCTime(t2);
      iResult = ((long) t1 < (long) t2) ? -1 : 1;
      break;
    case CItemData::PMTIME:
      pLHS->GetPMTime(t1);
      pRHS->GetPMTime(t2);
      iResult = ((long) t1 < (long) t2) ? -1 : 1;
      break;
    case CItemData::ATIME:
      pLHS->GetATime(t1);
      pRHS->GetATime(t2);
      iResult = ((long) t1 < (long) t2) ? -1 : 1;
      break;
    case CItemData::LTIME:
      pLHS->GetLTime(t1);
      pRHS->GetLTime(t2);
      iResult = ((long) t1 < (long) t2) ? -1 : 1;
      break;
    case CItemData::RMTIME:
      pLHS->GetRMTime(t1);
      pRHS->GetRMTime(t2);
      iResult = ((long) t1 < (long) t2) ? -1 : 1;
      break;
    default:
      iResult = 0; // should never happen - just keep compiler happy
      ASSERT(FALSE);
  }
  if (!self->m_bSortAscending) {
    iResult *= -1;
  }
  return iResult;
}


void
DboxMain::DoDataExchange(CDataExchange* pDX)
{
  CDialog::DoDataExchange(pDX);
  //{{AFX_DATA_MAP(DboxMain)
  DDX_Control(pDX, IDC_ITEMLIST, m_ctlItemList);
  DDX_Control(pDX, IDC_ITEMTREE, m_ctlItemTree);
  //}}AFX_DATA_MAP
}

void
DboxMain::UpdateToolBar(bool state)
{
	if (m_toolbarsSetup == TRUE) {
    BOOL State = (state) ? FALSE : TRUE;
		m_wndToolBar.GetToolBarCtrl().EnableButton(ID_TOOLBUTTON_ADD, State);
		m_wndToolBar.GetToolBarCtrl().EnableButton(ID_TOOLBUTTON_DELETE, State);
		m_wndToolBar.GetToolBarCtrl().EnableButton(ID_TOOLBUTTON_SAVE, State);
	}
}

void
DboxMain::UpdateToolBarForSelectedItem(CItemData *ci)
{
  // Following test required since this can be called on exit, with a ci
  // from ItemData that's already been deleted. Ugh.
  if (m_core.GetNumEntries() != 0) {
    BOOL State = (ci == NULL) ? FALSE : TRUE;
    int IDs[] = {ID_TOOLBUTTON_COPYPASSWORD, ID_TOOLBUTTON_COPYUSERNAME,
                 ID_TOOLBUTTON_COPYNOTESFLD, ID_TOOLBUTTON_AUTOTYPE, ID_TOOLBUTTON_EDIT};

    for (int i = 0; i < sizeof(IDs)/sizeof(IDs[0]); i++)
      m_wndToolBar.GetToolBarCtrl().EnableButton(IDs[i], State);

    if (ci == NULL || ci->IsURLEmpty())
      m_wndToolBar.GetToolBarCtrl().EnableButton(ID_TOOLBUTTON_BROWSEURL, FALSE);
    else
      m_wndToolBar.GetToolBarCtrl().EnableButton(ID_TOOLBUTTON_BROWSEURL, TRUE);
  }
}

void
DboxMain::setupBars()
{
#if !defined(POCKET_PC)
  // This code is copied from the DLGCBR32 example that comes with MFC
  
  // Add the status bar
  if (m_statusBar.Create(this)) {
	  // Set up DoubleClickAction text
	  const int dca = int(PWSprefs::GetInstance()->
		  GetPref(PWSprefs::DoubleClickAction));
	  switch (dca) {
		case PWSprefs::DoubleClickAutoType: statustext[SB_DBLCLICK] = IDS_STATAUTOTYPE; break;
		case PWSprefs::DoubleClickBrowse: statustext[SB_DBLCLICK] = IDS_STATBROWSE; break;
		case PWSprefs::DoubleClickCopyNotes: statustext[SB_DBLCLICK] = IDS_STATCOPYNOTES; break;
		case PWSprefs::DoubleClickCopyPassword: statustext[SB_DBLCLICK] = IDS_STATCOPYPASSWORD; break;
      	case PWSprefs::DoubleClickCopyUsername: statustext[SB_DBLCLICK] = IDS_STATCOPYUSERNAME; break;
		case PWSprefs::DoubleClickViewEdit: statustext[SB_DBLCLICK] = IDS_STATVIEWEDIT; break;
		default: statustext[SB_DBLCLICK] = IDS_STATCOMPANY;
	  }
	  // Set up Configuration source indicator (debug only)
#ifdef DEBUG
      statustext[SB_CONFIG] = PWSprefs::GetInstance()->GetConfigIndicator();
#else
      statustext[SB_CONFIG] = IDS_CONFIG_BLANK;
#endif /* DEBUG */
	  // Set up the rest
	  statustext[SB_MODIFIED] = IDS_MODIFIED;
	  statustext[SB_READONLY] = IDS_READ_ONLY;
	  statustext[SB_NUM_ENT] = IDS_STAT_NUM_IN_DB;

	  // And show
	  m_statusBar.SetIndicators(statustext, SB_TOTAL);

      // Make a sunken or recessed border around the first pane
      m_statusBar.SetPaneInfo(SB_DBLCLICK, m_statusBar.GetItemID(SB_DBLCLICK), SBPS_STRETCH, NULL);
  }             

  // Add the ToolBar.
  if (!m_wndToolBar.CreateEx(this, TBSTYLE_FLAT | TBSTYLE_TRANSPARENT,
                             WS_CHILD | WS_VISIBLE | CBRS_TOP | CBRS_TOOLTIPS | CBRS_FLYBY | CBRS_SIZE_DYNAMIC) ||
      !m_wndToolBar.LoadToolBar(IDB_TOOLBAR1))
    {
      TRACE0("Failed to create toolbar\n");
      return;      // fail to create
    }

  // Set toolbar according to graphic capabilities, overridable by user choice.
  CDC* pDC = this->GetDC();
  int NumBits = ( pDC ? pDC->GetDeviceCaps(12 /*BITSPIXEL*/) : 32 );
  if (NumBits < 16 || !PWSprefs::GetInstance()->GetPref(PWSprefs::UseNewToolbar))  {
    SetToolbar(ID_MENUITEM_OLD_TOOLBAR);
  } else {
    SetToolbar(ID_MENUITEM_NEW_TOOLBAR);
  }

  // Set flag
  m_toolbarsSetup = TRUE;
  UpdateToolBar(m_core.IsReadOnly());
#endif
}

void DboxMain::UpdateListItem(const int lindex, const int type, const CString &newText)
{
    int iSubItem = m_nColumnIndexByType[type];

    // Ignore if this column is not being displayed
    if (iSubItem < 0)
      return;

    BOOL brc = m_ctlItemList.SetItemText(lindex, iSubItem, newText);
    ASSERT(brc == TRUE);
    if (m_iSortedColumn == type) { // resort if necessary
        m_ctlItemList.SortItems(CompareFunc, (LPARAM)this);
        FixListIndexes();
    }
}

 // Find in m_pwlist entry with same title and user name as the i'th entry in m_ctlItemList
ItemListIter DboxMain::Find(int i)
{
  CItemData *ci = (CItemData *)m_ctlItemList.GetItemData(i);
  ASSERT(ci != NULL);
  const CMyString curGroup = ci->GetGroup();
  const CMyString curTitle = ci->GetTitle();
  const CMyString curUser = ci->GetUser();
  return Find(curGroup, curTitle, curUser);
}

/*
 * Finds all entries in m_pwlist that contain str in title, user, group or notes
 * field, returns their sorted indices in m_listctrl via indices, which is
 * assumed to be allocated by caller to DboxMain::GetNumEntries() ints.
 * FindAll returns the number of entries that matched.
 */

int
DboxMain::FindAll(const CString &str, BOOL CaseSensitive,
                  vector<int> &indices)
{
  ASSERT(!str.IsEmpty());
  ASSERT(indices.empty());

  CMyString curtitle, curuser, curnotes, curgroup, curURL, curAT;
  CMyString listTitle, savetitle;
  CString searchstr(str); // Since str is const, and we might need to MakeLower
  int retval = 0;

  if (!CaseSensitive)
    searchstr.MakeLower();

  int ititle(-1);  // Must be there as it is mandatory!
  for (int ic = 0; ic < m_nColumns; ic++) {
    if (m_nColumnTypeByIndex[ic] == CItemData::TITLE) {
      ititle = ic;
      break;
    }
  }

  ItemListConstIter iter;
  if (m_IsListView) {
    for (iter = m_core.GetEntryIter();
         iter != m_core.GetEntryEndIter(); iter++) {
      const CItemData &curitem = m_core.GetEntry(iter);

      savetitle = curtitle = curitem.GetTitle(); // savetitle keeps orig case
      curuser =  curitem.GetUser();
      curnotes = curitem.GetNotes();
      curgroup = curitem.GetGroup();
      curURL = curitem.GetURL();
      curAT = curitem.GetAutoType();

      if (!CaseSensitive) {
        curtitle.MakeLower();
        curuser.MakeLower();
        curnotes.MakeLower();
        curgroup.MakeLower();
        curURL.MakeLower();
        curAT.MakeLower();
      }
      if (::_tcsstr(curtitle, searchstr) ||
          ::_tcsstr(curuser, searchstr) ||
          ::_tcsstr(curnotes, searchstr) ||
          ::_tcsstr(curgroup, searchstr) ||
          ::_tcsstr(curURL, searchstr) ||
          ::_tcsstr(curAT, searchstr)) {
        // Find index in displayed list
        DisplayInfo *di = (DisplayInfo *)curitem.GetDisplayInfo();
        ASSERT(di != NULL);
        int li = di->list_index;
        ASSERT(CMyString(m_ctlItemList.GetItemText(li, ititle)) == savetitle);
        // add to indices
        indices.push_back(li);
      } // match found in m_pwlist
    } // iteration over entries
    retval = indices.size();
    // Sort indices if in List View
    if (retval > 1)
      sort(indices.begin(), indices.end());
  } else { // !m_IsListView
    OrderedItemList orderedItemList;
    MakeOrderedItemList(orderedItemList);
    OrderedItemList::const_iterator oiter;
    for (oiter = orderedItemList.begin();
         oiter != orderedItemList.end(); oiter++) {
      const CItemData &curitem = *oiter;

      savetitle = curtitle = curitem.GetTitle(); // savetitle keeps orig case
      curuser =  curitem.GetUser();
      curnotes = curitem.GetNotes();
      curgroup = curitem.GetGroup();
      curURL = curitem.GetURL();
      curAT = curitem.GetAutoType();

      if (!CaseSensitive) {
        curtitle.MakeLower();
        curuser.MakeLower();
        curnotes.MakeLower();
        curgroup.MakeLower();
        curURL.MakeLower();
        curAT.MakeLower();
      }
      if (::_tcsstr(curtitle, searchstr) ||
          ::_tcsstr(curuser, searchstr) ||
          ::_tcsstr(curnotes, searchstr) ||
          ::_tcsstr(curgroup, searchstr) ||
          ::_tcsstr(curURL, searchstr) ||
          ::_tcsstr(curAT, searchstr)) {
        // Find index in displayed list
        DisplayInfo *di = (DisplayInfo *)curitem.GetDisplayInfo();
        ASSERT(di != NULL);
        int li = di->list_index;
        ASSERT(CMyString(m_ctlItemList.GetItemText(li, ititle)) == savetitle);
        // add to indices, bump retval
        indices.push_back(li);
      } // match found in orderedItemList
    } // iterate over orderedItemList
    retval = indices.size();
    orderedItemList.clear();
  }
  return retval;
}

int
DboxMain::FindAll(const CString &str, BOOL CaseSensitive,
                  vector<int> &indices,
              const CItemData::FieldBits &bsFields, const int subgroup_set, 
              const CString &subgroup_name, const int subgroup_object,
              const int subgroup_function)
{
  ASSERT(!str.IsEmpty());
  ASSERT(indices.empty());

  CMyString curGroup, curTitle, curUser, curNotes, curPassword, curURL, curAT;
  CMyString listTitle, saveTitle;
  bool bFoundit;
  CString searchstr(str); // Since str is const, and we might need to MakeLower
  int retval = 0;

  if (!CaseSensitive)
    searchstr.MakeLower();

  int ititle(-1);  // Must be there as it is mandatory!
  for (int ic = 0; ic < m_nColumns; ic++) {
    if (m_nColumnTypeByIndex[ic] == CItemData::TITLE) {
      ititle = ic;
      break;
    }
  }

  ItemListConstIter listPos, listEnd;

  OrderedItemList orderedItemList;
  OrderedItemList::const_iterator olistPos, olistEnd;
  if (m_IsListView) {
    listPos = m_core.GetEntryIter();
    listEnd = m_core.GetEntryEndIter();
  } else {
    MakeOrderedItemList(orderedItemList);
    olistPos = orderedItemList.begin();
    olistEnd = orderedItemList.end();
  }

  while (m_IsListView ? (listPos != listEnd) : (olistPos != olistEnd)) {
    const CItemData &curitem = m_IsListView ? listPos->second : *olistPos;
    if (subgroup_set == BST_CHECKED &&
        !curitem.Matches(subgroup_name, subgroup_object, subgroup_function))
      goto nextentry;

    bFoundit = false;
    saveTitle = curTitle = curitem.GetTitle(); // savetitle keeps orig case
    curGroup = curitem.GetGroup();
    curUser =  curitem.GetUser();
    curPassword = curitem.GetPassword();
    curNotes = curitem.GetNotes();
    curURL = curitem.GetURL();
    curAT = curitem.GetAutoType();

    if (!CaseSensitive) {
      curGroup.MakeLower();
      curTitle.MakeLower();
      curUser.MakeLower();
      curPassword.MakeLower();
      curNotes.MakeLower();
      curURL.MakeLower();
      curAT.MakeLower();
    }

    // do loop to easily break out as soon as a match is found
    // saves more checking if entry already selected
    do {
      if (bsFields.test(CItemData::GROUP) &&
          ::_tcsstr(curGroup, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::TITLE) &&
          ::_tcsstr(curTitle, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::USER) &&
          ::_tcsstr(curUser, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::PASSWORD) &&
          ::_tcsstr(curPassword, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::NOTES) &&
          ::_tcsstr(curNotes, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::URL) &&
          ::_tcsstr(curURL, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::AUTOTYPE) &&
          ::_tcsstr(curAT, searchstr)) {
        bFoundit = true;
        break;
      }
      if (bsFields.test(CItemData::PWHIST)) {
        BOOL pwh_status;
        size_t pwh_max, pwh_num;
        PWHistList PWHistList;
        curitem.CreatePWHistoryList(pwh_status, pwh_max, pwh_num,
                                   &PWHistList, TMC_XML);
        PWHistList::iterator iter;
        for (iter = PWHistList.begin(); iter != PWHistList.end();
                   iter++) {
          PWHistEntry pwshe = *iter;
          if (!CaseSensitive)
            pwshe.password.MakeLower();
          if (::_tcsstr(pwshe.password, searchstr)) {
            bFoundit = true;
            break;  // break out of for loop
          }
        }
        PWHistList.clear();
        break;
      }
    } while(FALSE);  // only do it once!

    if (bFoundit) {
      // Find index in displayed list
      DisplayInfo *di = (DisplayInfo *)curitem.GetDisplayInfo();
      ASSERT(di != NULL);
      int li = di->list_index;
      ASSERT(CMyString(m_ctlItemList.GetItemText(li, ititle)) == saveTitle);
      // add to indices, bump retval
      indices.push_back(li);
    } // match found in m_pwlist

nextentry:
    if (m_IsListView)
      listPos++;
    else
      olistPos++;
  } // while

  retval = indices.size();
  // Sort indices if in List View
  if (m_IsListView && retval > 1)
    sort(indices.begin(), indices.end());

  if (!m_IsListView)
    orderedItemList.clear();

  return retval;
}

//Checks and sees if everything works and something is selected
BOOL
DboxMain::SelItemOk()
{
  CItemData *ci = getSelectedItem();
  return (ci == NULL) ? FALSE : TRUE;
}

BOOL DboxMain::SelectEntry(int i, BOOL MakeVisible)
{
  BOOL retval;
  if (m_ctlItemList.GetItemCount() == 0)
    return false;

  if (m_ctlItemList.IsWindowVisible()) {
    retval = m_ctlItemList.SetItemState(i,
                                        LVIS_FOCUSED | LVIS_SELECTED,
                                        LVIS_FOCUSED | LVIS_SELECTED);
    if (MakeVisible) {
      m_ctlItemList.EnsureVisible(i, FALSE);
    }
    m_ctlItemList.Invalidate();
  } else { //Tree view active
    CItemData *ci = (CItemData *)m_ctlItemList.GetItemData(i);
    ASSERT(ci != NULL);
    DisplayInfo *di = (DisplayInfo *)ci->GetDisplayInfo();
    ASSERT(di != NULL);
    ASSERT(di->list_index == i);

    // Was there anything selected before?
    HTREEITEM hti = m_ctlItemTree.GetSelectedItem();
    // NULL means nothing was selected.
    if (hti != NULL) {
      // Time to remove the old "fake selection" (a.k.a. drop-hilite)
      // Make sure to undo "MakeVisible" on the previous selection.
      m_ctlItemTree.SetItemState(hti, 0, TVIS_DROPHILITED);
    }

    retval = m_ctlItemTree.SelectItem(di->tree_item);
    if (MakeVisible) {
      // Following needed to show selection when Find dbox has focus. Ugh.
      m_ctlItemTree.SetItemState(di->tree_item,
                                 TVIS_DROPHILITED | TVIS_SELECTED,
                                 TVIS_DROPHILITED | TVIS_SELECTED);
    }
    m_ctlItemTree.Invalidate();
  }
  return retval;
}

BOOL DboxMain::SelectFindEntry(int i, BOOL MakeVisible)
{
  BOOL retval;
  if (m_ctlItemList.GetItemCount() == 0)
    return FALSE;

  if (m_ctlItemList.IsWindowVisible()) {
    retval = m_ctlItemList.SetItemState(i,
                                        LVIS_FOCUSED | LVIS_SELECTED,
                                        LVIS_FOCUSED | LVIS_SELECTED);
    if (MakeVisible) {
      m_ctlItemList.EnsureVisible(i, FALSE);
    }
    m_ctlItemList.Invalidate();
  } else { //Tree view active
    CItemData *ci = (CItemData *)m_ctlItemList.GetItemData(i);
    ASSERT(ci != NULL);
    DisplayInfo *di = (DisplayInfo *)ci->GetDisplayInfo();
    ASSERT(di != NULL);
    ASSERT(di->list_index == i);

    UnFindItem();

    retval = m_ctlItemTree.SelectItem(di->tree_item);
    if (MakeVisible) {
      // Following needed to show selection when Find dbox has focus. Ugh.
      m_ctlItemTree.SetItemState(di->tree_item, TVIS_BOLD, TVIS_BOLD);
      m_LastFoundItem = di->tree_item;
      m_bBoldItem = true;
    }
    m_ctlItemTree.Invalidate();
  }
  return retval;
}

// Updates m_ctlItemList and m_ctlItemTree from m_pwlist
// updates of windows suspended until all data is in.
void
DboxMain::RefreshList()
{
  if (!m_windowok)
    return;

#if defined(POCKET_PC)
  HCURSOR		waitCursor = app.LoadStandardCursor( IDC_WAIT );
#endif

  // can't use LockWindowUpdate 'cause only one window at a time can be locked
  m_ctlItemList.SetRedraw( FALSE );
  m_ctlItemTree.SetRedraw( FALSE );
  m_ctlItemList.DeleteAllItems();
  m_ctlItemTree.DeleteAllItems();
  m_bBoldItem = false;

  if (m_core.GetNumEntries() != 0) {
    ItemListIter listPos;
#if defined(POCKET_PC)
    SetCursor( waitCursor );
#endif
    for (listPos = m_core.GetEntryIter(); listPos != m_core.GetEntryEndIter();
         listPos++) {
      CItemData &ci = m_core.GetEntry(listPos);
      DisplayInfo *di = (DisplayInfo *)ci.GetDisplayInfo();
      if (di != NULL)
        di->list_index = -1; // easier, but less efficient, to delete di
      insertItem(ci, -1, false);
    }
    
    m_ctlItemTree.SortTree(TVI_ROOT);
    
#if defined(POCKET_PC)
    SetCursor( NULL );
#endif
  } // we have entries


  // re-enable and force redraw!
  m_ctlItemList.SetRedraw( TRUE ); m_ctlItemList.Invalidate();
  m_ctlItemTree.SetRedraw( TRUE ); m_ctlItemTree.Invalidate();

  FixListIndexes();
}

void
DboxMain::OnSize(UINT nType,
                 int cx,
                 int cy) 
//Note that onsize runs before InitDialog (Gee, I love MFC)
//  Also, OnSize is called AFTER the function has been peformed.
//  To verify IF the fucntion should be done at all, it must be checked in OnSysCommand.
{
  CDialog::OnSize(nType, cx, cy);

  if (m_windowok) {
    // Position the control bars
    CRect rect;
    RepositionBars(AFX_IDW_CONTROLBAR_FIRST, AFX_IDW_CONTROLBAR_LAST, 0);
    RepositionBars(AFX_IDW_CONTROLBAR_FIRST, AFX_IDW_CONTROLBAR_LAST, 0, reposQuery, &rect);
    m_ctlItemList.MoveWindow(&rect, TRUE);
    m_ctlItemTree.MoveWindow(&rect, TRUE);
  }

  // {kjp} Only SIZE_RESTORED is supported on Pocket PC.
#if !defined(POCKET_PC)
  if (nType == SIZE_MINIMIZED) {
    // Called when minimize button select on main dialog control box
    // or by right clicking in the Taskbar (not using System Tray)
    PWSprefs *prefs = PWSprefs::GetInstance();

    m_selectedAtMinimize = getSelectedItem();
    m_ctlItemList.DeleteAllItems();
    m_ctlItemTree.DeleteAllItems();
    m_bBoldItem = false;

    if (prefs->GetPref(PWSprefs::DontAskMinimizeClearYesNo))
      ClearClipboardData();
    if (prefs->GetPref(PWSprefs::DatabaseClear)) {
      if (m_core.IsChanged() ||  m_bTSUpdated)
        if (Save() != PWScore::SUCCESS) {
          // If we don't warn the user, data may be lost!
          CString cs_text(MAKEINTRESOURCE(IDS_COULDNOTSAVE)), 
            cs_title(MAKEINTRESOURCE(IDS_SAVEERROR));
          MessageBox(cs_text, cs_title, MB_ICONSTOP);
          ShowWindow(SW_SHOW);
          return;
        }
      ClearData(false);
    }
    if (PWSprefs::GetInstance()->
        GetPref(PWSprefs::UseSystemTray)) {      
      app.SetMenuDefaultItem(ID_MENUITEM_UNMINIMIZE);
      ShowWindow(SW_HIDE);
    }
  } else if (nType == SIZE_MAXIMIZED) {
    RefreshList();
  } else if (nType == SIZE_RESTORED) {
    if (!m_bSizing) { // here if actually restored
#endif
      app.SetMenuDefaultItem(ID_MENUITEM_MINIMIZE);
      UnMinimize(false);
      RestoreDisplayStatus();
      m_ctlItemTree.SetRestoreMode(true);
      RefreshList();
      if (m_selectedAtMinimize != NULL)
        SelectEntry(((DisplayInfo *)m_selectedAtMinimize->GetDisplayInfo())->list_index, false);
      m_ctlItemTree.SetRestoreMode(false);
#if !defined(POCKET_PC)
    } else { // m_bSizing == true: here if size changed
      CRect rect;
      GetWindowRect(&rect);
      PWSprefs::GetInstance()->SetPrefRect(rect.top, rect.bottom,
                                           rect.left, rect.right);
    }
  } // nType == SIZE_RESTORED
#endif
  m_bSizing = false;
}

// Called when right-click is invoked in the client area of the window.
void
DboxMain::OnContextMenu(CWnd* /* pWnd */, CPoint point) 
{
#if defined(POCKET_PC)
  const DWORD dwTrackPopupFlags = TPM_LEFTALIGN;
#else
  const DWORD dwTrackPopupFlags = TPM_LEFTALIGN | TPM_RIGHTBUTTON;
#endif

  CPoint local = point;
  int item = -1;
  CItemData *itemData = NULL;
  CMenu menu;

  if (m_ctlItemList.IsWindowVisible()) {
    // currently in flattened list view.
    m_ctlItemList.ScreenToClient(&local);
    item = m_ctlItemList.HitTest(local);
    if (item < 0)
      return; // right click on empty list
    itemData = (CItemData *)m_ctlItemList.GetItemData(item);
    int rc = SelectEntry(item);
    if (rc == LB_ERR) {
      return; // ? is this possible ?
    }
    m_ctlItemList.SetFocus();
  } else {
    // currently in tree view
    ASSERT(m_ctlItemTree.IsWindowVisible());
    m_ctlItemTree.ScreenToClient(&local);
    HTREEITEM ti = m_ctlItemTree.HitTest(local);
    if (ti != NULL) {
      itemData = (CItemData *)m_ctlItemTree.GetItemData(ti);
      if (itemData != NULL) {
        // right-click was on an item (LEAF)
        DisplayInfo *di = (DisplayInfo *)itemData->GetDisplayInfo();
        ASSERT(di != NULL);
        ASSERT(di->tree_item == ti);
        item = di->list_index;
        m_ctlItemTree.SelectItem(ti); // So that OnEdit gets the right one
      } else {
        // right-click was on a group (NODE)
        m_ctlItemTree.SelectItem(ti); 
        if (menu.LoadMenu(IDR_POPGROUP)) {
          CMenu* pPopup = menu.GetSubMenu(0);
          ASSERT(pPopup != NULL);
          m_TreeViewGroup = CMyString(m_ctlItemTree.GetGroup(ti));
          pPopup->TrackPopupMenu(dwTrackPopupFlags, point.x, point.y, this); // use this window for commands
        }
      }
    } else {
      // not over anything
      if (menu.LoadMenu(IDR_POPTREE)) {
        CMenu* pPopup = menu.GetSubMenu(0);
        ASSERT(pPopup != NULL);
        pPopup->TrackPopupMenu(dwTrackPopupFlags, point.x, point.y, this); // use this window for commands
      }
    }
    m_ctlItemTree.SetFocus();
  } // tree view handling

  if (item >= 0) {
    menu.LoadMenu(IDR_POPMENU);
    CMenu* pPopup = menu.GetSubMenu(0);
    ASSERT(pPopup != NULL);

    ASSERT(itemData != NULL);

    if (itemData->IsURLEmpty())
      pPopup->EnableMenuItem(ID_MENUITEM_BROWSE, MF_GRAYED);
    else
      pPopup->EnableMenuItem(ID_MENUITEM_BROWSE, MF_ENABLED);

    pPopup->TrackPopupMenu(dwTrackPopupFlags, point.x, point.y, this); // use this window for commands

  } // if (item >= 0)
}

void DboxMain::OnListItemSelected(NMHDR *pNotifyStruct, LRESULT *pLResult)
{
  *pLResult = 0L;
  NMITEMACTIVATE *plv = (NMITEMACTIVATE *)pNotifyStruct;
  int item = plv->iItem;
  if (item != -1) { // -1 if nothing selected, e.g., empty list
    CItemData *ci = (CItemData *)m_ctlItemList.GetItemData(item);
    UpdateToolBarForSelectedItem(ci);
  }
}

void DboxMain::OnKeydownItemlist(NMHDR* pNMHDR, LRESULT* pResult)
{
  LV_KEYDOWN *pLVKeyDow = (LV_KEYDOWN*)pNMHDR;

  // FALSE = call next in line to process event
  *pResult = FALSE;

  switch (pLVKeyDow->wVKey) {
    case VK_DELETE:
      OnDelete();
      break;
    case VK_INSERT:
      OnAdd();
      break;
    case VK_ADD:
      if ((GetKeyState(VK_CONTROL) & 0x8000) == 0)
        return;
      SetHeaderInfo();
      break;
    default:
      return;    
  }

  // We have processed the key stroke - don't call anyone else
  *pResult = TRUE;
}

#if !defined(POCKET_PC)
void
DboxMain::OnChangeItemFocus(NMHDR* /* pNMHDR */, LRESULT* /* pResult */) 
{
  // Called on NM_{SET,KILL}FOCUS for IDC_ITEM{LIST,TREE}
  // Seems excessive to do this all the time
  // Should be done only on Open and on Change (Add, Delete, Modify)
  if (m_toolbarsSetup == TRUE)
    UpdateStatusBar();
}
#endif

////////////////////////////////////////////////////////////////////////////////
// NOTE!
// itemData must be the actual item in the item list.  if the item is remove
// from the list, it must be removed from the display as well and vice versa.
// a pointer is associated with the item in the display that is used for
// sorting.
// {kjp} We could use itemData.GetNotes(CString&) to reduce the number of
// {kjp} temporary objects created and copied.
//
int DboxMain::insertItem(CItemData &itemData, int iIndex, bool bSort)
{
  if (itemData.GetDisplayInfo() != NULL &&
      ((DisplayInfo *)itemData.GetDisplayInfo())->list_index != -1) {
    // true iff item already displayed
    return iIndex;
  }

  int iResult = iIndex;
  if (iResult < 0) {
    iResult = m_ctlItemList.GetItemCount();
  }

  CMyString group = itemData.GetGroup();
  CMyString title = itemData.GetTitle();
  CMyString username = itemData.GetUser();
  // get only the first line for display
  CMyString strNotes = itemData.GetNotes();
  int iEOL = strNotes.Find(TCHAR('\r'));
  if (iEOL >= 0 && iEOL < strNotes.GetLength()) {
    CMyString strTemp = strNotes.Left(iEOL);
    strNotes = strTemp;
  }
  CMyString cs_fielddata;

  // Insert the first column data
  switch (m_nColumnTypeByIndex[0]) {
  case CItemData::GROUP:
    cs_fielddata = group;
    break;
  case CItemData::TITLE:
    cs_fielddata = title;
    break;
  case CItemData::USER:
    cs_fielddata = username;
    break;
  case CItemData::NOTES:
    cs_fielddata = strNotes;
    break;
  case CItemData::PASSWORD:
    cs_fielddata = itemData.GetPassword();
    break;
  case CItemData::URL:
    cs_fielddata = itemData.GetURL();
    break;
  case CItemData::CTIME:
    cs_fielddata = itemData.GetCTimeL();
    break;
  case CItemData::PMTIME:
    cs_fielddata = itemData.GetPMTimeL();
    break;
  case CItemData::ATIME:
    cs_fielddata = itemData.GetATimeL();
    break;
  case CItemData::LTIME:
    cs_fielddata = itemData.GetLTimeL();
    break;
  case CItemData::RMTIME:
    cs_fielddata = itemData.GetRMTimeL();
    break;
  default:
    ASSERT(0);
  }
  iResult = m_ctlItemList.InsertItem(iResult, cs_fielddata);

  if (iResult < 0) {
    // TODO: issue error here...
    return iResult;
  }
  DisplayInfo *di = (DisplayInfo *)itemData.GetDisplayInfo();
  if (di == NULL)
    di = new DisplayInfo;
  di->list_index = iResult;

  {
    HTREEITEM ti;
    CMyString treeDispString = m_ctlItemTree.MakeTreeDisplayString(itemData);
    // get path, create if necessary, add title as last node
    ti = m_ctlItemTree.AddGroup(itemData.GetGroup());
    if (!PWSprefs::GetInstance()->GetPref(PWSprefs::ExplorerTypeTree)) {
      ti = m_ctlItemTree.InsertItem(treeDispString, ti, TVI_SORT);
      m_ctlItemTree.SetItemData(ti, (DWORD)&itemData);
    } else {
      ti = m_ctlItemTree.InsertItem(treeDispString, ti, TVI_LAST);
      m_ctlItemTree.SetItemData(ti, (DWORD)&itemData);
      if (bSort)
        m_ctlItemTree.SortTree(m_ctlItemTree.GetParentItem(ti));
    }
    time_t now, warnexptime, tLTime;
    time(&now);
    if (PWSprefs::GetInstance()->GetPref(PWSprefs::PreExpiryWarn)) {
      int idays = PWSprefs::GetInstance()->GetPref(PWSprefs::PreExpiryWarnDays);
      struct tm st;
#if _MSC_VER >= 1400
      errno_t err;
      err = localtime_s(&st, &now);  // secure version
      ASSERT(err == 0);
#else
      st = *localtime(&now);
      ASSERT(st != NULL); // null means invalid time
#endif
      st.tm_mday += idays;
      warnexptime = mktime(&st);
      if (warnexptime == (time_t)-1)
        warnexptime = (time_t)0;
    } else
      warnexptime = (time_t)0;
    
    itemData.GetLTime(tLTime);
    if (tLTime != 0) {
	    if (tLTime <= now) {
        m_ctlItemTree.SetItemImage(ti, CPWTreeCtrl::EXPIRED_LEAF, CPWTreeCtrl::EXPIRED_LEAF);
    	} else if (tLTime < warnexptime) {
        m_ctlItemTree.SetItemImage(ti, CPWTreeCtrl::WARNEXPIRED_LEAF, CPWTreeCtrl::WARNEXPIRED_LEAF);
	    } else
        m_ctlItemTree.SetItemImage(ti, CPWTreeCtrl::LEAF, CPWTreeCtrl::LEAF);
    } else
      m_ctlItemTree.SetItemImage(ti, CPWTreeCtrl::LEAF, CPWTreeCtrl::LEAF);
	
    ASSERT(ti != NULL);
    itemData.SetDisplayInfo((void *)di);
    di->tree_item = ti;
  }

  // Set the data in the rest of the columns
  for (int i = 1; i < m_nColumns; i++) {
    switch (m_nColumnTypeByIndex[i]) {
    case CItemData::GROUP:
      cs_fielddata = group;
      break;
    case CItemData::TITLE:
      cs_fielddata = title;
      break;
    case CItemData::USER:
      cs_fielddata = username;
      break;
    case CItemData::NOTES:
      cs_fielddata = strNotes;
      break;
    case CItemData::PASSWORD:
      cs_fielddata = itemData.GetPassword();
      break;
    case CItemData::URL:
      cs_fielddata = itemData.GetURL();
      break;
    case CItemData::CTIME:
      cs_fielddata = itemData.GetCTimeL();
      break;
    case CItemData::PMTIME:
      cs_fielddata = itemData.GetPMTimeL();
      break;
    case CItemData::ATIME:
      cs_fielddata = itemData.GetATimeL();
      break;
    case CItemData::LTIME:
      cs_fielddata = itemData.GetLTimeL();
      break;
    case CItemData::RMTIME:
      cs_fielddata = itemData.GetRMTimeL();
      break;
    default:
      ASSERT(0);
    }
    m_ctlItemList.SetItemText(iResult, i, cs_fielddata);
  }

  m_ctlItemList.SetItemData(iResult, (DWORD)&itemData);
  return iResult;
}

CItemData *DboxMain::getSelectedItem()
{
  CItemData *retval = NULL;
  if (m_ctlItemList.IsWindowVisible()) {
    // flattened list mode.
    POSITION p = m_ctlItemList.GetFirstSelectedItemPosition();
    if (p) {
      int i = m_ctlItemList.GetNextSelectedItem(p);
      retval = (CItemData *)m_ctlItemList.GetItemData(i);
      ASSERT(retval != NULL);
      DisplayInfo *di = (DisplayInfo *)retval->GetDisplayInfo();
      ASSERT(di != NULL && di->list_index == i);
    }
  } else {
    // heirarchy tree mode; go from HTREEITEM to index
    HTREEITEM ti = m_ctlItemTree.GetSelectedItem();
    if (ti != NULL) {
      retval = (CItemData *)m_ctlItemTree.GetItemData(ti);
      if (retval != NULL) {  // leaf node
        DisplayInfo *di = (DisplayInfo *)retval->GetDisplayInfo();
        ASSERT(di != NULL && di->tree_item == ti);
      }
    }    
  }
  return retval;
}

// functor for ClearData
struct deleteDisplayInfo {
  void operator()(pair<CUUIDGen, CItemData> p)
  {delete p.second.GetDisplayInfo();} // no need to set to NULL
};

void
DboxMain::ClearData(bool clearMRE)
{
  // Iterate over item list, delete DisplayInfo
  deleteDisplayInfo ddi;
  for_each(m_core.GetEntryIter(), m_core. GetEntryEndIter(),
           ddi);

  m_core.ClearData();

  UpdateSystemTray(m_bOpen ? LOCKED : CLOSED);

  // If data is cleared, m_selectedAtMinimize is useless,
  // since it will be deleted and rebuilt from the file.
  // This means that selection won't be restored in this case.
  // Tough.
  m_selectedAtMinimize = NULL;

  if (clearMRE)
    m_RUEList.ClearEntries();

  //Because GetText returns a copy, we cannot do anything about the names
  if (m_windowok) {
    // For long lists, this is painful, so we disable updates
    m_ctlItemList.LockWindowUpdate();
    m_ctlItemList.DeleteAllItems();
    m_ctlItemList.UnlockWindowUpdate();
    m_ctlItemTree.LockWindowUpdate();
    m_ctlItemTree.DeleteAllItems();
    m_ctlItemTree.UnlockWindowUpdate();
    m_bBoldItem = false;
  }
  m_needsreading = true;
}

void DboxMain::OnColumnClick(NMHDR* pNMHDR, LRESULT* pResult) 
{
  NM_LISTVIEW* pNMListView = (NM_LISTVIEW*)pNMHDR;

  // Get column index to CItemData value
  int iIndex = pNMListView->iSubItem;
  int isortcolumn = m_nColumnTypeByIndex[iIndex];

#if (WINVER < 0x0501)  // These are already defined for WinXP and later
#define HDF_SORTUP 0x0400
#define HDF_SORTDOWN 0x0200
#endif

  HDITEM hdi;
  hdi.mask = HDI_FORMAT;

  if (m_iSortedColumn == isortcolumn) {
    m_bSortAscending = !m_bSortAscending;
  } else {
    // Turn off all previous sort arrrows
    // Note: not sure where, as user may have played with the columns!
    for (int i = 0; i < m_LVHdrCtrl.GetItemCount(); i++) {
      m_LVHdrCtrl.GetItem(i, &hdi);
      if ((hdi.fmt & (HDF_SORTUP | HDF_SORTDOWN)) != 0) {
        hdi.fmt &= ~(HDF_SORTUP | HDF_SORTDOWN);
        m_LVHdrCtrl.SetItem(i, &hdi);
      }
    }

    m_iSortedColumn = isortcolumn;
    m_bSortAscending = true;
  }

  m_ctlItemList.SortItems(CompareFunc, (LPARAM)this);
  FixListIndexes();

  m_LVHdrCtrl.GetItem(iIndex, &hdi);
  // Turn off all arrows
  hdi.fmt &= ~(HDF_SORTUP | HDF_SORTDOWN);
  // Turn on the correct arrow
  hdi.fmt |= ((m_bSortAscending == TRUE) ? HDF_SORTUP : HDF_SORTDOWN);
  m_LVHdrCtrl.SetItem(iIndex, &hdi);

  *pResult = TRUE;
}

void
DboxMain::OnHeaderRClick(NMHDR* /* pNMHDR */, LRESULT *pResult)
{
#if defined(POCKET_PC)
  const DWORD dwTrackPopupFlags = TPM_LEFTALIGN;
#else
  const DWORD dwTrackPopupFlags = TPM_LEFTALIGN | TPM_RIGHTBUTTON;
#endif
  CMenu menu;
  CPoint ptMousePos;
  GetCursorPos(&ptMousePos);

  if (menu.LoadMenu(IDR_POPCOLUMNS)) {
    CMenu* pPopup = menu.GetSubMenu(0);
    ASSERT(pPopup != NULL);
    if (m_pCC != NULL)
      pPopup->CheckMenuItem(ID_MENUITEM_COLUMNPICKER,
        m_pCC->IsWindowVisible() ? MF_CHECKED : MF_UNCHECKED);
    else
      pPopup->CheckMenuItem(ID_MENUITEM_COLUMNPICKER, MF_UNCHECKED);

    pPopup->TrackPopupMenu(dwTrackPopupFlags, ptMousePos.x, ptMousePos.y, this);
  }
  *pResult = TRUE;
}

void
DboxMain::OnHeaderEndDrag(NMHDR* /* pNMHDR */, LRESULT *pResult)
{
  // Called for HDN_ENDDRAG which changes the column order when CC not visible
  // Unfortunately the changes are only really done when this call returns,
  // hence the PostMessage to get the information later

  // Get control after operation is really complete
  PostMessage(WM_HDR_DRAG_COMPLETE);

  // Go do it
  *pResult = FALSE;
}

void
DboxMain::OnHeaderNotify(NMHDR* pNMHDR, LRESULT *pResult)
{
  HD_NOTIFY *phdn = (HD_NOTIFY *) pNMHDR;
  *pResult = FALSE;

  if (m_nColumnWidthByIndex == NULL || phdn->pitem == NULL)
    return;

  UINT mask = phdn->pitem->mask;
  if ((mask & HDI_WIDTH) != HDI_WIDTH)
    return;

  // column width changed
  switch (phdn->hdr.code) {
    case HDN_ENDTRACK:
    case HDN_ITEMCHANGED:
      m_nColumnWidthByIndex[phdn->iItem] = phdn->pitem->cxy;
      break;
    default:
      break;
  }
}

void
DboxMain::OnListView() 
{
  SetListView();
  m_IsListView = true;
}

void
DboxMain::OnTreeView() 
{
  SetTreeView();
  m_IsListView = false;
}

void
DboxMain::SetListView()
{
  UnFindItem();
  m_ctlItemTree.ShowWindow(SW_HIDE);
  m_ctlItemList.ShowWindow(SW_SHOW);
  PWSprefs::GetInstance()->SetPref(PWSprefs::LastView,
				   _T("list"));
}

void
DboxMain::SetTreeView()
{
  UnFindItem();
  m_ctlItemList.ShowWindow(SW_HIDE);
  m_ctlItemTree.ShowWindow(SW_SHOW);
  PWSprefs::GetInstance()->SetPref(PWSprefs::LastView,
                                   _T("tree"));
}

void
DboxMain::OnOldToolbar() 
{
  PWSprefs::GetInstance()->SetPref(PWSprefs::UseNewToolbar, false);
  SetToolbar(ID_MENUITEM_OLD_TOOLBAR);
  UpdateToolBar(m_core.IsReadOnly());
}

void
DboxMain::OnNewToolbar() 
{
  PWSprefs::GetInstance()->SetPref(PWSprefs::UseNewToolbar, true);
  SetToolbar(ID_MENUITEM_NEW_TOOLBAR);
  UpdateToolBar(m_core.IsReadOnly());
}

void
DboxMain::SetToolbar(int menuItem)
{
  UINT Flags = 0;
  CBitmap bmTemp; 
  COLORREF Background = RGB(192, 192, 192);

  switch (menuItem) {
  case ID_MENUITEM_NEW_TOOLBAR: {
    int NumBits = 32;
    CDC* pDC = this->GetDC();
    if ( pDC )  {
      NumBits = pDC->GetDeviceCaps(12 /*BITSPIXEL*/);
    }
    if (NumBits >= 32) {
      bmTemp.LoadBitmap(IDB_TOOLBAR1);
      Flags = ILC_MASK | ILC_COLOR32;
    } else {
      bmTemp.LoadBitmap(IDB_TOOLBAR2);
      Flags = ILC_MASK | ILC_COLOR8;
      Background = RGB( 196,198,196 );
    }
    break;
  }
  case ID_MENUITEM_OLD_TOOLBAR:
    bmTemp.LoadBitmap(IDB_TOOLBAR3);
    Flags = ILC_MASK | ILC_COLOR8;
    break;
  default:
    ASSERT(false);
    return;
  }
  m_toolbarMode = menuItem;

  CToolBarCtrl& tbcTemp = m_wndToolBar.GetToolBarCtrl();
  CImageList ilTemp; 
  ilTemp.Create(16, 16, Flags, 10, 10);
  ilTemp.Add(&bmTemp, Background);
  tbcTemp.SetImageList(&ilTemp);
  ilTemp.Detach();
  bmTemp.Detach();

  m_wndToolBar.Invalidate();

  CRect rect;
  RepositionBars(AFX_IDW_CONTROLBAR_FIRST, AFX_IDW_CONTROLBAR_LAST, 0);
  RepositionBars(AFX_IDW_CONTROLBAR_FIRST, AFX_IDW_CONTROLBAR_LAST, 0, reposQuery, &rect);
  m_ctlItemList.MoveWindow(&rect, TRUE);
  m_ctlItemTree.MoveWindow(&rect, TRUE); // Fix Bug 940585
}

void
DboxMain::OnExpandAll()
{
  m_ctlItemTree.OnExpandAll();
}

void
DboxMain::OnCollapseAll()
{
  m_ctlItemTree.OnCollapseAll();
}

void
DboxMain::OnTimer(UINT nIDEvent )
{
  if ((nIDEvent == TIMER_CHECKLOCK && IsWorkstationLocked()) ||
      (nIDEvent == TIMER_USERLOCK && DecrementAndTestIdleLockCounter())) {
    /*
     * Since we clear the data, any unchanged changes will be lost,
     * so we force a save if database is modified, and fail
     * to lock if the save fails (unless db is r-o).
     */
    if (m_core.IsReadOnly() || m_core.GetNumEntries() == 0 ||
        !(m_core.IsChanged() || m_bTSUpdated ||
          m_core.WasDisplayStatusChanged()) ||
        Save() == PWScore::SUCCESS) {
      TRACE("locking database\n");
      if(IsWindowVisible()){
        ShowWindow(SW_MINIMIZE);
      }
      ClearData(false);
      if (nIDEvent == TIMER_CHECKLOCK)
        KillTimer(TIMER_CHECKLOCK);
    }
  }
}

// This function determines if the workstation is locked.
BOOL DboxMain::IsWorkstationLocked() const
{
  BOOL Result = false;
  HDESK hDesktop = OpenDesktop(_T("default"), 0, false,
                               DESKTOP_SWITCHDESKTOP);
  if( hDesktop != 0 ) {
    // SwitchDesktop fails if hDesktop invisible, screensaver or winlogin.
    Result = ! SwitchDesktop(hDesktop);
    CloseDesktop(hDesktop);
  }
  return Result;
}


void
DboxMain::OnChangeFont() 
{
  CFont *pOldFontTree;
  pOldFontTree = m_ctlItemTree.GetFont();

  // make sure we know what is inside the font.
  LOGFONT lf;
  pOldFontTree->GetLogFont(&lf);

  // present it and possibly change it
  CFontDialog dlg(&lf, CF_SCREENFONTS | CF_INITTOLOGFONTSTRUCT);
  if(dlg.DoModal() == IDOK) {
    m_pFontTree->DeleteObject();
    m_pFontTree->CreateFontIndirect(&lf);
    // transfer the fonts to the tree and list windows
    m_ctlItemTree.SetFont(m_pFontTree);
    m_ctlItemList.SetFont(m_pFontTree);
    m_LVHdrCtrl.SetFont(m_pFontTree);

    // Recalculate header widths
    CalcHeaderWidths();
    // Reset column widths
    AutoResizeColumns();

    CString str;
    str.Format(_T("%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%i,%s"),
               lf.lfHeight,
               lf.lfWidth,
               lf.lfEscapement,
               lf.lfOrientation,
               lf.lfWeight,
               lf.lfItalic,
               lf.lfUnderline,
               lf.lfStrikeOut,
               lf.lfCharSet,
               lf.lfOutPrecision,
               lf.lfClipPrecision,
               lf.lfQuality,
               lf.lfPitchAndFamily,
               lf.lfFaceName);

    PWSprefs *prefs = PWSprefs::GetInstance();
    prefs->SetPref(PWSprefs::TreeFont, str);
  }
}

static CString GetToken(CString& str, LPCTSTR c)
{
  // helper function for DboxMain::ExtractFont()
  int pos = str.Find(c);
  CString token = str.Left(pos);
  str = str.Mid(pos + 1);

  return token;
}

void
DboxMain::ExtractFont(CString& str, LOGFONT *ptreefont)
{
  ptreefont->lfHeight = _ttol((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfWidth = _ttol((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfEscapement = _ttol((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfOrientation = _ttol((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfWeight = _ttol((LPCTSTR)GetToken(str, _T(",")));

#pragma warning(push)
#pragma warning(disable:4244) //conversion from 'int' to 'BYTE', possible loss of data
  ptreefont->lfItalic = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfUnderline = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfStrikeOut = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfCharSet = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfOutPrecision = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfClipPrecision = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfQuality = _ttoi((LPCTSTR)GetToken(str, _T(",")));
  ptreefont->lfPitchAndFamily = _ttoi((LPCTSTR)GetToken(str, _T(",")));
#pragma warning(pop)

#if (_MSC_VER >= 1400)
  _tcscpy_s(ptreefont->lfFaceName, LF_FACESIZE, str);
#else
  _tcscpy(ptreefont->lfFaceName, str);
#endif  
}

void
DboxMain::UpdateSystemTray(const STATE s)
{
  switch (s) {
  case LOCKED:
    app.SetSystemTrayState(ThisMfcApp::LOCKED);
    if (!m_core.GetCurFile().IsEmpty())
      app.SetTooltipText(_T("[") + m_core.GetCurFile() + _T("]"));
    break;
  case UNLOCKED:
    app.SetSystemTrayState(ThisMfcApp::UNLOCKED);
    if (!m_core.GetCurFile().IsEmpty())
      app.SetTooltipText(m_core.GetCurFile());
    break;
  case CLOSED:
    app.SetSystemTrayState(ThisMfcApp::CLOSED);
    break;
  default:
    ASSERT(0);
  }
}

BOOL
DboxMain::LaunchBrowser(const CString &csURL)
{
  CString csAltBrowser;
  CString csCmdLineParms;
  bool useAltBrowser;
  long hinst;
  CString theURL(csURL);

  // If csURL contains "[alt]" then we'll use the alternate browser (if defined),
  // and remove the "[alt]" from the URL.
  // If csURL doesn't contain "://", then we'll prepend "http://" to it,
  // e.g., change "www.mybank.com" to "http://www.mybank.com".
  int altReplacements = theURL.Replace(_T("[alt]"), _T(""));
  if (theURL.Find(_T("://")) == -1)
    theURL = _T("http://") + theURL;

  csCmdLineParms = CString(PWSprefs::GetInstance()->
                         GetPref(PWSprefs::AltBrowserCmdLineParms));
  
  csAltBrowser = CString(PWSprefs::GetInstance()->
                         GetPref(PWSprefs::AltBrowser));

  useAltBrowser = (altReplacements > 0) && !csAltBrowser.IsEmpty();

  if (!useAltBrowser) {
    hinst = long(::ShellExecute(NULL, NULL, theURL, NULL,
                                NULL, SW_SHOWNORMAL));
  } else {
    if (!csCmdLineParms.IsEmpty())
      theURL = csCmdLineParms + _T(" ") + theURL;
    hinst = long(::ShellExecute(NULL, NULL, csAltBrowser, theURL,
                                NULL, SW_SHOWNORMAL));
  }

  if(hinst < 32) {
    AfxMessageBox(IDS_CANTBROWSE, MB_ICONSTOP);
    return FALSE;
  }
  return TRUE;
}

void
DboxMain::SetColumns()
{
  // User hasn't yet saved the columns he/she wants and so gets our order!
  // Or - user has reset the columns (popup menu from right click on Header)
  CString cs_header;
  HDITEM hdi;
  hdi.mask = HDI_LPARAM;

  PWSprefs *prefs = PWSprefs::GetInstance();
  int ipwd = prefs->GetPref(PWSprefs::ShowPasswordInTree) ? 1 : 0;

  CRect rect;
  m_ctlItemList.GetClientRect(&rect);
  int i1stWidth = prefs->GetPref(PWSprefs::Column1Width,
                                 (rect.Width() / 3 + rect.Width() % 3));
  int i2ndWidth = prefs->GetPref(PWSprefs::Column2Width,
                                 rect.Width() / 3);
  int i3rdWidth = prefs->GetPref(PWSprefs::Column3Width,
                                 rect.Width() / 3);
  
  cs_header = GetHeaderText(CItemData::TITLE);
  m_ctlItemList.InsertColumn(0, cs_header);
  hdi.lParam = CItemData::TITLE;
  m_LVHdrCtrl.SetItem(0, &hdi);
  m_ctlItemList.SetColumnWidth(0, i1stWidth);
  
  cs_header = GetHeaderText(CItemData::USER);
  m_ctlItemList.InsertColumn(1, cs_header);
  hdi.lParam = CItemData::USER;
  m_LVHdrCtrl.SetItem(1, &hdi);
  m_ctlItemList.SetColumnWidth(1, i2ndWidth);

  cs_header = GetHeaderText(CItemData::NOTES);
  m_ctlItemList.InsertColumn(2, cs_header);
  hdi.lParam = CItemData::NOTES;
  m_LVHdrCtrl.SetItem(2, &hdi);
  m_ctlItemList.SetColumnWidth(1, i3rdWidth);
    
  if (PWSprefs::GetInstance()->GetPref(PWSprefs::ShowPasswordInTree)) {
    cs_header = GetHeaderText(CItemData::PASSWORD);
    m_ctlItemList.InsertColumn(3, cs_header);
    hdi.lParam = CItemData::PASSWORD;
    m_LVHdrCtrl.SetItem(3, &hdi);
    m_ctlItemList.SetColumnWidth(3,
                                 PWSprefs::GetInstance()->
                                 GetPref(PWSprefs::Column4Width,
                                         rect.Width() / 4));
  }

  int ioff = 3;
  cs_header = GetHeaderText(CItemData::URL);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::URL;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;

  cs_header = GetHeaderText(CItemData::CTIME);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::CTIME;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;
  
  cs_header = GetHeaderText(CItemData::PMTIME);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::PMTIME;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;
  
  cs_header = GetHeaderText(CItemData::ATIME);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::ATIME;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;
  
  cs_header = GetHeaderText(CItemData::LTIME);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::LTIME;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;
  
  cs_header = GetHeaderText(CItemData::RMTIME);
  m_ctlItemList.InsertColumn(ipwd + ioff, cs_header);
  hdi.lParam = CItemData::RMTIME;
  m_LVHdrCtrl.SetItem(ipwd + ioff, &hdi);
  ioff++;

  m_ctlItemList.SetRedraw(FALSE);

  for (int i = ipwd + 3; i < (ipwd + ioff); i++) {
    m_ctlItemList.SetColumnWidth(i, m_iDateTimeFieldWidth);
  }

  SetHeaderInfo();

  return;
}

void
DboxMain::SetColumns(const CString cs_ListColumns)
{
  //  User has saved the columns he/she wants and now we are putting them back

  CString cs_header;
  HDITEM hdi;
  hdi.mask = HDI_LPARAM;

  vector<int> vi_columns;
  vector<int>::const_iterator vi_IterColumns;
  const TCHAR pSep[] = _T(",");
  TCHAR *pTemp;
  
  // Duplicate as strtok modifies the string
  pTemp = _tcsdup((LPCTSTR)cs_ListColumns);
  
#if _MSC_VER >= 1400
  // Capture columns shown:
  TCHAR *next_token;
  TCHAR *token = _tcstok_s(pTemp, pSep, &next_token);
  while(token) {
    vi_columns.push_back(_ttoi(token));
    token = _tcstok_s(NULL, pSep, &next_token);
  }
#else
  // Capture columns shown:
  TCHAR *token = _tcstok(pTemp, pSep);
  while(token) {
    vi_columns.push_back(_ttoi(token));
    token = _tcstok(NULL, pSep);
  }
#endif
  free(pTemp);
 
  int icol = 0;
  for (vi_IterColumns = vi_columns.begin();
       vi_IterColumns != vi_columns.end();
       vi_IterColumns++) {
    int iType = *vi_IterColumns;
    cs_header = GetHeaderText(iType);
    if (!cs_header.IsEmpty()) {
      m_ctlItemList.InsertColumn(icol, cs_header);
      hdi.lParam = iType;
      m_LVHdrCtrl.SetItem(icol, &hdi);
      icol++;
    }
  }

  SetHeaderInfo();

  return;
}

void
DboxMain::SetColumnWidths(const CString cs_ListColumnsWidths)
{
  //  User has saved the columns he/she wants and now we are putting them back
  std::vector<int> vi_widths;
  std::vector<int>::const_iterator vi_IterWidths;
  const TCHAR pSep[] = _T(",");
  TCHAR *pWidths;
  
  // Duplicate as strtok modifies the string
  pWidths = _tcsdup((LPCTSTR)cs_ListColumnsWidths);
  
#if _MSC_VER >= 1400
  // Capture column widths shown:
  TCHAR *next_token;
  TCHAR *token = _tcstok_s(pWidths, pSep, &next_token);
  while(token) {
    vi_widths.push_back(_ttoi(token));
    token = _tcstok_s(NULL, pSep, &next_token);
  }
#else
  // Capture columnwidths shown:
  TCHAR *token = _tcstok(pWidths, pSep);
  while(token) {
    vi_widths.push_back(_ttoi(token));
    token = _tcstok(NULL, pSep);
  }
#endif
  free(pWidths);
  
  int icol = 0, index;

  for (vi_IterWidths = vi_widths.begin();
       vi_IterWidths != vi_widths.end();
       vi_IterWidths++) {
    if (icol == (m_nColumns - 1))
      break;
    int iWidth = *vi_IterWidths;
    m_ctlItemList.SetColumnWidth(icol, iWidth);
    index = m_LVHdrCtrl.OrderToIndex(icol);
    m_nColumnWidthByIndex[index] = iWidth;
    icol++;
  }

  // Last column special
  index = m_LVHdrCtrl.OrderToIndex(m_nColumns - 1);
  m_ctlItemList.SetColumnWidth(index, LVSCW_AUTOSIZE_USEHEADER);
}

void DboxMain::AddColumn(const int iType, const int iIndex)
{
  // Add new column of type iType after current column index iIndex
  CString cs_header;
  HDITEM hdi;
  int iNewIndex(iIndex);

  //  If iIndex = -1, means drop on the end
  if (iIndex < 0)
    iNewIndex = m_nColumns;

  hdi.mask = HDI_LPARAM | HDI_WIDTH;
  cs_header = GetHeaderText(iType);
  ASSERT(!cs_header.IsEmpty());
  iNewIndex = m_ctlItemList.InsertColumn(iNewIndex, cs_header);
  ASSERT(iNewIndex != -1);
  hdi.lParam = iType;
  hdi.cxy = GetHeaderWidth(iType);
  m_LVHdrCtrl.SetItem(iNewIndex, &hdi);

  // Reset values
  SetHeaderInfo();

  // Now show the user
  RefreshList();
}

void DboxMain::DeleteColumn(const int iType)
{
  // Delete column
  m_ctlItemList.DeleteColumn(m_nColumnIndexByType[iType]);
  
  // Reset values
  SetHeaderInfo();
}

void
DboxMain::SetHeaderInfo()
{
  HDITEM hdi_get;
  // CHeaderCtrl get values
  hdi_get.mask = HDI_LPARAM | HDI_ORDER;

  m_nColumns = m_LVHdrCtrl.GetItemCount();
  ASSERT(m_nColumns > 1);  // Title & User are mandatory!

  // re-initialise array
  for (int i = 0; i < CItemData::LAST; i++)
      m_nColumnIndexByType[i] = 
          m_nColumnIndexByOrder[i] =
          m_nColumnTypeByIndex[i] =
          m_nColumnWidthByIndex[i] = -1;

  m_LVHdrCtrl.GetOrderArray(m_nColumnIndexByOrder, m_nColumns);

  for (int iOrder = 0; iOrder < m_nColumns; iOrder++) {
    const int iIndex = m_nColumnIndexByOrder[iOrder];
    m_ctlItemList.SetColumnWidth(iIndex, LVSCW_AUTOSIZE);
    m_LVHdrCtrl.GetItem(iIndex, &hdi_get);
    ASSERT(iOrder == hdi_get.iOrder);
    m_nColumnIndexByType[hdi_get.lParam] = iIndex;
    m_nColumnTypeByIndex[iIndex] = hdi_get.lParam;
  }

  // Check sort column still there
  if (m_nColumnIndexByType[m_iSortedColumn] == -1) {
    // No - take highest visible
      for (int itype = 0; itype < CItemData::LAST; itype++) {
          if (m_nColumnIndexByType[itype] != -1) {
              m_iSortedColumn = itype;
              break;
          }
      }
  }

  AutoResizeColumns();
}

void
DboxMain::OnResetColumns()
{
  // Delete all existing columns
  for (int i = 0; i < m_nColumns; i++) {
    m_ctlItemList.DeleteColumn(0);
  }

  // re-initialise array
  for (int itype = 0; itype < CItemData::LAST; itype++)
    m_nColumnIndexByType[itype] = -1;

  // Set default columns
  SetColumns();

  // Reset the column widths
  AutoResizeColumns();

  // Refresh the ListView
  RefreshList();

  // Reset Column Chooser dialog but only if already created
  if (m_pCC != NULL)
    SetupColumnChooser(false);
}

void
DboxMain::AutoResizeColumns()
{
  int iIndex, iType;
  // CHeaderCtrl get values
  for (int iOrder = 0; iOrder < m_nColumns; iOrder++) {
    iIndex = m_nColumnIndexByOrder[iOrder];
    iType = m_nColumnTypeByIndex[iIndex];

    m_ctlItemList.SetColumnWidth(iIndex, LVSCW_AUTOSIZE);
    m_nColumnWidthByIndex[iIndex] = m_ctlItemList.GetColumnWidth(iIndex);

    if (m_nColumnWidthByIndex[iIndex] < m_nColumnHeaderWidthByType[iType]) {
      m_ctlItemList.SetColumnWidth(iIndex, m_nColumnHeaderWidthByType[iType]);
      m_nColumnWidthByIndex[iIndex] = m_nColumnHeaderWidthByType[iType];
    }
  }

  m_ctlItemList.UpdateWindow();

  // Last column is special
  iIndex = m_nColumnIndexByOrder[m_nColumns - 1];
  m_ctlItemList.SetColumnWidth(iIndex, LVSCW_AUTOSIZE);
  m_ctlItemList.SetColumnWidth(iIndex, LVSCW_AUTOSIZE_USEHEADER);
  m_nColumnWidthByIndex[iIndex] = m_ctlItemList.GetColumnWidth(iIndex);
}

void
DboxMain::OnColumnPicker()
{
  SetupColumnChooser(true);
}

void
DboxMain::SetupColumnChooser(const bool bShowHide)
{
  if (m_pCC == NULL) {
    m_pCC = new CColumnChooserDlg;
    BOOL ret = m_pCC->Create(IDD_COLUMNCHOOSER, this);
    if (!ret) {   //Create failed.
      m_pCC = NULL;
      return;
    }
    m_pCC->SetLVHdrCtrlPtr(&m_LVHdrCtrl);

    // Set extended style
    DWORD dw_style = m_pCC->m_ccListCtrl.GetExtendedStyle() | LVS_EX_ONECLICKACTIVATE;
    m_pCC->m_ccListCtrl.SetExtendedStyle(dw_style);

    // Make sure it doesn't appear obscure the header
    CRect HDRrect, CCrect;
    m_LVHdrCtrl.GetWindowRect(&HDRrect);
    m_pCC->GetWindowRect(&CCrect);
    // Note (0,0) is the top left of screen
    if (CCrect.top < HDRrect.bottom) {
      int x = CCrect.left;
      int y = HDRrect.bottom + 20;
      m_pCC->SetWindowPos(0, x, y, 0, 0, SWP_NOZORDER | SWP_NOSIZE);
    }

    // Insert column with "dummy" header
    m_pCC->m_ccListCtrl.InsertColumn(0, _T(""));
    m_pCC->m_ccListCtrl.SetColumnWidth(0, m_iheadermaxwidth);

    // Make it just wide enough to take the text
    CRect rect1, rect2;
    m_pCC->GetWindowRect(&rect1);
    m_pCC->m_ccListCtrl.GetWindowRect(&rect2);
    m_pCC->SetWindowPos(NULL, 0, 0, m_iheadermaxwidth + 18,
      rect1.Height(), SWP_NOMOVE | SWP_NOZORDER);
    m_pCC->m_ccListCtrl.SetWindowPos(NULL, 0, 0, m_iheadermaxwidth + 6,
      rect2.Height(), SWP_NOMOVE | SWP_NOZORDER);
  }

  int i;
  CString cs_header;

  // Clear all current entries
  m_pCC->m_ccListCtrl.DeleteAllItems();

  // and repopulate
  int iItem;
  for (i = CItemData::LAST - 1; i >= 0; i--) {
    // Can't play with Title or User columns
    if (i == CItemData::TITLE || i == CItemData::USER)
      continue;

    if (m_nColumnIndexByType[i] == -1) {
      cs_header = GetHeaderText(i);
      if (!cs_header.IsEmpty()) {
        iItem = m_pCC->m_ccListCtrl.InsertItem(0, cs_header);
        m_pCC->m_ccListCtrl.SetItemData(iItem, (DWORD)i);
      }
    }
  }

  // If called by user right clicking on header, hide it or show it
  if (bShowHide)
    m_pCC->ShowWindow(m_pCC->IsWindowVisible() ? SW_HIDE : SW_SHOW);
}

CString DboxMain::GetHeaderText(const int iType)
{
  CString cs_header;
  switch (iType) {
    case CItemData::GROUP:
      cs_header.LoadString(IDS_GROUP);
      break;
    case CItemData::TITLE:
      cs_header.LoadString(IDS_TITLE);
      break;
    case CItemData::USER:
      cs_header.LoadString(IDS_USERNAME);
      break;
    case CItemData::PASSWORD:
      cs_header.LoadString(IDS_PASSWORD);
      break;
    case CItemData::URL:
      cs_header.LoadString(IDS_URL);
      break;
    case CItemData::NOTES:
      cs_header.LoadString(IDS_NOTES);
      break;
    case CItemData::CTIME:        
      cs_header.LoadString(IDS_CREATED);
      break;
    case CItemData::PMTIME:
      cs_header.LoadString(IDS_PASSWORDMODIFIED);
      break;
    case CItemData::ATIME:
      cs_header.LoadString(IDS_LASTACCESSED);
      break;
    case CItemData::LTIME:
      cs_header.LoadString(IDS_PASSWORDEXPIRYDATE);
      break;
    case CItemData::RMTIME:
      cs_header.LoadString(IDS_LASTMODIFIED);
      break;
    default:
      cs_header.Empty();
  }
  return cs_header;
}

int DboxMain::GetHeaderWidth(const int iType)
{
  int nWidth(0);

  switch (iType) {
    case CItemData::GROUP:
    case CItemData::TITLE:
    case CItemData::USER:
    case CItemData::PASSWORD:
    case CItemData::NOTES:
    case CItemData::URL:
      nWidth = m_nColumnHeaderWidthByType[iType];
      break;
    case CItemData::CTIME:        
    case CItemData::PMTIME:
    case CItemData::ATIME:
    case CItemData::LTIME:
    case CItemData::RMTIME:
      nWidth = m_iDateTimeFieldWidth;
      break;
    default:
      break;
  }

  return nWidth;
}

void DboxMain::CalcHeaderWidths()
{
  // Get default column width for datetime fields
  TCHAR time_str[80], datetime_str[80];
  // Use "fictitious" longest English date
  SYSTEMTIME systime;
  systime.wYear = (WORD)2000;
  systime.wMonth = (WORD)9;
  systime.wDay = (WORD)30;
  systime.wDayOfWeek = (WORD)3;
  systime.wHour = (WORD)23;
  systime.wMinute = (WORD)44;
  systime.wSecond = (WORD)55;
  systime.wMilliseconds = (WORD)0;
  TCHAR szBuf[80];
  VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_SSHORTDATE, szBuf, 80));
  GetDateFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, datetime_str, 80);
  szBuf[0] = _T(' ');  // Put a blank between date and time
  VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_STIMEFORMAT, &szBuf[1], 79));
  GetTimeFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, time_str, 80);
#if _MSC_VER >= 1400
  _tcscat_s(datetime_str, 80, time_str);
#else
  _tcscat(datetime_str, 80, time_str);
#endif

  m_iDateTimeFieldWidth = m_ctlItemList.GetStringWidth(datetime_str) + 6;

  m_iheadermaxwidth = -1;
  CString cs_header;

  for (int iType = 0; iType < CItemData::LAST; iType++) {
    switch (iType) {
      case CItemData::GROUP:
        cs_header.LoadString(IDS_GROUP);
        break;
      case CItemData::TITLE:
        cs_header.LoadString(IDS_TITLE);
        break;
      case CItemData::USER:
        cs_header.LoadString(IDS_USERNAME);
        break;
      case CItemData::PASSWORD:
        cs_header.LoadString(IDS_PASSWORD);
        break;
      case CItemData::URL:
        cs_header.LoadString(IDS_URL);
        break;
      case CItemData::NOTES:
        cs_header.LoadString(IDS_NOTES);
        break;
      case CItemData::CTIME:        
        cs_header.LoadString(IDS_CREATED);
        break;
      case CItemData::PMTIME:
        cs_header.LoadString(IDS_PASSWORDMODIFIED);
        break;
      case CItemData::ATIME:
        cs_header.LoadString(IDS_LASTACCESSED);
        break;
      case CItemData::LTIME:
        cs_header.LoadString(IDS_PASSWORDEXPIRYDATE);
        break;
      case CItemData::RMTIME:
        cs_header.LoadString(IDS_LASTMODIFIED);
        break;
      default:
        cs_header.Empty();
    }

    if (!cs_header.IsEmpty())
      m_nColumnHeaderWidthByType[iType] = m_ctlItemList.GetStringWidth(cs_header) + 20;
    else
      m_nColumnHeaderWidthByType[iType] = -4;

    m_iheadermaxwidth = max(m_iheadermaxwidth, m_nColumnHeaderWidthByType[iType]);
  }
}

void
DboxMain::UnFindItem()
{
  // Entries found are made bold - remove it here.
  if (m_bBoldItem) {
    m_ctlItemTree.SetItemState(m_LastFoundItem, 0, TVIS_BOLD);
    m_bBoldItem = false;
  }
}
