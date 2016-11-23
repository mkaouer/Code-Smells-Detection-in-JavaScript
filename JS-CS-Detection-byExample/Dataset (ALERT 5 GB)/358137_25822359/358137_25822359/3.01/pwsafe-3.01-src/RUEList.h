// RUEList.h
//-----------------------------------------------------------------------------

#ifndef _RUEList_h
#define _RUEList_h

#include "corelib/ItemData.h"
#include "corelib/MyString.h"
#include "corelib/PWScore.h"
#include "corelib/UUIDGen.h"

//-----------------------------------------------------------------------------

/*
 * CRUEList is a class that contains the recently used entries
 *
 */

// Following is Most Recent Entry field separator for dynamic menu:
#define MRE_FS _T("\xbb")

// Recent Entry structure for CList
struct RUEntry {
  uuid_array_t RUEuuid;
};

class CRUEList
{
 public:
  // Construction/Destruction/operators
  CRUEList();
  ~CRUEList();

  CRUEList& operator=(const CRUEList& second);

  // Data retrieval
  int GetCount() const;
  int GetMax() const;
  bool GetAllMenuItemStrings(CList<CMyString, CMyString&> &) const;
  bool GetMenuItemString(const int &, CMyString &) const;
  bool GetMenuItemString(const uuid_array_t &, CMyString &) const;
  bool GetPWEntry(const int &, CItemData &);

  // Data setting
  bool SetMax(const int &);
  void ClearEntries();
  bool AddRUEntry(const uuid_array_t &);
  bool DeleteRUEntry(const int &);
  bool DeleteRUEntry(const uuid_array_t &);

 private:
  PWScore &m_core;    // Dboxmain's m_core (which = app.m_core!)
  int m_maxentries;
  CList<RUEntry, RUEntry&> m_RUEList;  // Recently Used Entry History List
};

#endif
//-----------------------------------------------------------------------------
// Local variables:
// mode: c++
// End:
