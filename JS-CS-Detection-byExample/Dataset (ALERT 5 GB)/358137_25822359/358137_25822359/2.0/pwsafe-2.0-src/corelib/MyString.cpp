/// \file MyString.cpp
//-----------------------------------------------------------------------------

#include "Util.h"

#include "MyString.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif


CMyString::CMyString() : m_mystring("")
{
}

CMyString::CMyString(LPCTSTR lpsz) : m_mystring(lpsz)
{
}

CMyString::CMyString(LPCTSTR lpsz, int nLength) : m_mystring(lpsz, nLength)
{
}

CMyString::CMyString(const CMyString& stringSrc) : m_mystring(stringSrc.m_mystring)
{
}

CMyString::CMyString(const CString& stringSrc) : m_mystring(stringSrc)
{
}

CMyString::~CMyString()
{
   trashstring();
}


void
CMyString::trashstring()
{
   trashMemory((unsigned char*)m_mystring.GetBuffer(m_mystring.GetLength()),
               m_mystring.GetLength());
}


LPTSTR
CMyString::GetBuffer(int nMinBufLength)
{
   return m_mystring.GetBuffer(nMinBufLength);
}

void
CMyString::ReleaseBuffer(int nNewLength)
{
   m_mystring.ReleaseBuffer(nNewLength);
}

int
CMyString::GetLength() const
{
   return m_mystring.GetLength();
}

const CMyString&
CMyString::operator=(const CMyString& stringSrc)
{
   trashstring();
   m_mystring = stringSrc.m_mystring;
   return *this;
}

const CMyString&
CMyString::operator=(TCHAR ch)
{
   trashstring();
   m_mystring = ch;
   return *this;
}

const CMyString&
CMyString::operator=(LPCTSTR lpsz)
{
   trashstring();
   m_mystring = lpsz;
   return *this;
}

const CMyString&
CMyString::operator=(const unsigned char* psz)
{
   trashstring();
   m_mystring = psz;
   return *this;
}

const CMyString&
CMyString::operator+=(const CMyString& string)
{
   m_mystring += string.m_mystring;
   return *this;
}

const CMyString&
CMyString::operator+=(TCHAR ch)
{
   m_mystring += ch;
   return *this;
}

const CMyString&
CMyString::operator+=(LPCTSTR lpsz)
{
   m_mystring += lpsz;
   return *this;
}

CMyString AFXAPI
operator+(const CMyString& string1,const CMyString& string2)
{
   CMyString s;
   s = (CMyString)(string1.m_mystring+string2.m_mystring);
   return s;
}

CMyString AFXAPI
operator+(const CMyString& string, TCHAR ch)
{
   CMyString s;
   s = (CMyString)(string.m_mystring + ch);
   return s;
}

CMyString AFXAPI
operator+(TCHAR ch, const CMyString& string)
{
   CMyString s;
   s = (CMyString)(ch + string.m_mystring);
   return s;
}

CMyString AFXAPI
operator+(const CMyString& string, LPCTSTR lpsz)
{
   CMyString s;
   s = (CMyString)(string.m_mystring + lpsz);
   return s;
}

CMyString AFXAPI
operator+(LPCTSTR lpsz, const CMyString& string)
{
   CMyString s;
   s = (CMyString)(lpsz + string.m_mystring);
   return s;
}

CMyString
CMyString::Mid(int nFirst, int nCount) const
{
   return m_mystring.Mid(nFirst,nCount);
}

TCHAR
CMyString::operator[](int nIndex) const
{
   return m_mystring[nIndex];
}

void
CMyString::SetAt(int nIndex, TCHAR ch)
{
   m_mystring.SetAt(nIndex,ch);
}

CMyString::operator CString() const
{
   return m_mystring;
}

CMyString::operator CString&()
{
   return m_mystring;
}

CMyString::operator LPCTSTR() const
{
   return (LPCTSTR)m_mystring;
}

BOOL
CMyString::IsEmpty() const
{
   return m_mystring.IsEmpty();
}

int
CMyString::FindByte(char ch) const
{
	int		nRetVal = -1;	// default to not found
	int		nIndex	= 0;;

	const char* pszString = (const char *)m_mystring;

	while ( pszString[nIndex] )
	{
		if ( pszString[nIndex] == ch )
		{
			nRetVal = nIndex;
			break;
		}

		++nIndex;
	}

	return nRetVal;
}

int
CMyString::Find(TCHAR ch) const
{
   return m_mystring.Find(ch);
}

int
CMyString::Find(LPCTSTR lpszSub) const
{
   return m_mystring.Find(lpszSub);
}

//Can't properly trash the memory here, so it is better to just return a CString
CString
CMyString::Left(int nCount) const
{
   return m_mystring.Left(nCount);
}

//Can't properly trash the memory here, so it is better to just return a CString
CString
CMyString::Right(int nCount) const
{
   return m_mystring.Right(nCount);
}

bool
operator==(const CMyString& s1, const CMyString& s2)
{
   return (const CString)s1 == (const CString)s2;
}

bool
operator==(const CMyString& s1, LPCTSTR s2)
{
   return (const CString)s1==s2;
}

bool
operator==(LPCTSTR s1, const CMyString& s2)
{
   return s1==(const CString)s2;
}

bool
operator!=(const CMyString& s1, const CMyString& s2)
{
   return (const CString)s1 != (const CString)s2;
}

bool
operator!=(const CMyString& s1, LPCTSTR s2)
{
   return (const CString)s1 != s2;
}

bool
operator!=(LPCTSTR s1, const CMyString& s2)
{
   return s1 != (const CString)s2;
}

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
