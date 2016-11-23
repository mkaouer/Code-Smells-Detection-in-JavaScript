/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
#pragma once

// SysColStatic.h
//-----------------------------------------------------------------------------

/*
  This entire file was copied from
  http://www.codeguru.com/staticctrl/syscol_static.shtml
  and was written by P�l K. T�nder 
*/

//-----------------------------------------------------------------------------
class CSysColStatic : public CStatic
{
// Construction
public:
   CSysColStatic();
   void ReloadBitmap(int nImageID = -1);
// Attributes
public:

// Operations
public:

// Overrides
   // ClassWizard generated virtual function overrides
   //{{AFX_VIRTUAL(CSysColStatic)
   //}}AFX_VIRTUAL

// Implementation
public:
   virtual ~CSysColStatic();

   // Generated message map functions
protected:
   int m_nImageID;
   //{{AFX_MSG(CSysColStatic)
   afx_msg void OnSysColorChange();
   //}}AFX_MSG

   DECLARE_MESSAGE_MAP()
};

//-----------------------------------------------------------------------------
// Local variables:
// mode: c++
// End:
