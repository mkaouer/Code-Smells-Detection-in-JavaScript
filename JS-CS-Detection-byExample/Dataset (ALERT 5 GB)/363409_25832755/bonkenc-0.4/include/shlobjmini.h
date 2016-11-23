#ifndef SHLOBJ_MINI_H
#define SHLOBJ_MINI_H

#include <windows.h>

#define BIF_RETURNONLYFSDIRS 1

typedef struct _SHITEMID {
	USHORT	cb;
	BYTE	abID[1];
} SHITEMID, * LPSHITEMID;

typedef const SHITEMID *LPCSHITEMID;

typedef struct _ITEMIDLIST {
	SHITEMID mkid;
} ITEMIDLIST,*LPITEMIDLIST;

typedef const ITEMIDLIST *LPCITEMIDLIST;
typedef int (CALLBACK* BFFCALLBACK)(HWND,UINT,LPARAM,LPARAM);

typedef struct _browseinfo {
	HWND	hwndOwner;
	LPCITEMIDLIST	pidlRoot;
	LPSTR	pszDisplayName;
	LPCSTR	lpszTitle;
	UINT	ulFlags;
	BFFCALLBACK	lpfn;
	LPARAM	lParam;
	int	iImage;
} BROWSEINFO,*PBROWSEINFO,*LPBROWSEINFO;

extern "C"
{
	LPITEMIDLIST WINAPI SHBrowseForFolder(PBROWSEINFO);
	BOOL WINAPI SHGetPathFromIDList(LPCITEMIDLIST,LPSTR);
}

#endif
