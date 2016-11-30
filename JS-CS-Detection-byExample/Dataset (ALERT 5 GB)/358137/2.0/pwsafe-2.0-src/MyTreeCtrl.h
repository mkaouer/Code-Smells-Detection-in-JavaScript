/*
 * Silly subclass of CTreeCtrl just to implement Drag&Drop.
 *
 * Based on MFC sample code from CMNCTRL1
 */

#ifndef _MYTREECTRL_H
#define _MYTREECTRL_H
#include <Afxcmn.h>

class CMyTreeCtrl : public CTreeCtrl
{
public:
  CMyTreeCtrl();
  ~CMyTreeCtrl();

  enum {NODE=0, LEAF=1}; // indices of bitmaps in ImageList

  void DeleteWithParents(HTREEITEM hItem); // if a parent node becomes a leaf
  CString GetGroup(HTREEITEM hItem); // get group path to hItem
  HTREEITEM AddGroup(const CString &path);
 protected:
  //{{AFX_MSG(CMyTreeCtrl)
  afx_msg void OnBeginLabelEdit(LPNMHDR pnmhdr, LRESULT *pLResult);
  afx_msg void OnEndLabelEdit(LPNMHDR pnmhdr, LRESULT *pLResult);
  afx_msg void OnBeginDrag(LPNMHDR pnmhdr, LRESULT *pLResult);
  afx_msg void OnMouseMove(UINT nFlags, CPoint point);
  afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
  afx_msg void OnDestroy();
  //}}AFX_MSG

  void OnButtonUp(CPoint point);

  DECLARE_MESSAGE_MAP()

private:
  bool        m_bDragging;
  HTREEITEM   m_hitemDrag;
  HTREEITEM   m_hitemDrop;
  CImageList  *m_pimagelist;
  CString     m_BeginEditText;

  void SetNewStyle(long lStyleMask, BOOL bSetBits);
  bool TransferItem(HTREEITEM hitem, HTREEITEM hNewParent);
  void OnButtonUp(void);
  bool IsChildNodeOf(HTREEITEM hitemChild, HTREEITEM hitemSuspectedParent);
  bool IsLeafNode(HTREEITEM hItem);
  void UpdateLeafsGroup(HTREEITEM hItem, CString prefix);
};


#endif /* _MYTREECTRL_H */
