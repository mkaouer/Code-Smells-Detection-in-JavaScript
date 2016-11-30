/*
 * Copyright (c) 2003-2007 Rony Shapiro <ronys@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license.php
 */
/// \file Util.cpp
//-----------------------------------------------------------------------------

#include "sha1.h"
#include "BlowFish.h"
#include "PWSrand.h"
#include "PwsPlatform.h"
#include "corelib.h"

#include <stdio.h>
#include <sys/timeb.h>
#include <time.h>

#include "Util.h"


// used by CBC routines...
static void
xormem(unsigned char* mem1, const unsigned char* mem2, int length)
{
  for (int x=0;x<length;x++)
    mem1[x] ^= mem2[x];
}

//-----------------------------------------------------------------------------
//Overwrite the memory
// used to be a loop here, but this was deemed (1) overly paranoid 
// (2) The wrong way to scrub DRAM memory 
// see http://www.cs.auckland.ac.nz/~pgut001/pubs/secure_del.html 
// and http://www.cypherpunks.to/~peter/usenix01.pdf 

#pragma optimize("",off)
void
trashMemory(void* buffer, size_t length)
{
  ASSERT(buffer != NULL);
  // {kjp} no point in looping around doing nothing is there?
  if (length > 0) {
    memset(buffer, 0x55, length);
    memset(buffer, 0xAA, length);
    memset(buffer, 0x00, length);
  }
}
#pragma optimize("",on)

void
trashMemory(LPTSTR buffer, size_t length)
{
  trashMemory((unsigned char *) buffer, length * sizeof(buffer[0]));
}

void
trashMemory(CString &cs_buffer)
{
  TCHAR *lpszString = cs_buffer.GetBuffer(cs_buffer.GetLength());
  trashMemory( (void *) lpszString, cs_buffer.GetLength() * sizeof(lpszString[0]));
  cs_buffer.ReleaseBuffer();
}

/**
   Burn some stack memory
   @param len amount of stack to burn in bytes
*/
void burnStack(unsigned long len)
{
  unsigned char buf[32];
  trashMemory(buf, sizeof(buf));
  if (len > (unsigned long)sizeof(buf))
    burnStack(len - sizeof(buf));
}

void ConvertString(const CMyString &text,
                   unsigned char *&txt,
                   int &txtlen)
{
  LPCTSTR txtstr = LPCTSTR(text); 
  txtlen = text.GetLength();

#ifndef UNICODE
  txt = (unsigned char *)txtstr; // don't delete[] (ugh)!!!
#else
  txt = new unsigned char[2*txtlen]; // safe upper limit
  int len = WideCharToMultiByte(CP_ACP, 0, txtstr, txtlen,
                                LPSTR(txt), 2*txtlen, NULL, NULL);
  ASSERT(len != 0);
  txtlen = len;
  txt[len] = '\0';
#endif /* UNICODE */
}


//Generates a passkey-based hash from stuff - used to validate the passkey
void
GenRandhash(const CMyString &a_passkey,
            const unsigned char* a_randstuff,
            unsigned char* a_randhash)
{
  int pkeyLen = 0;
  unsigned char *pstr = NULL;

  ConvertString(a_passkey, pstr, pkeyLen);

  /*
    tempSalt <- H(a_randstuff + a_passkey)
  */
  SHA1 keyHash;
  keyHash.Update(a_randstuff, StuffSize);
  keyHash.Update(pstr, pkeyLen);

#ifdef UNICODE
  trashMemory(pstr, pkeyLen);
  delete[] pstr;
#endif

  unsigned char tempSalt[20]; // HashSize
  keyHash.Final(tempSalt);

  /*
    tempbuf <- a_randstuff encrypted 1000 times using tempSalt as key?
  */

  BlowFish Cipher(tempSalt, sizeof(tempSalt));

  unsigned char tempbuf[StuffSize];
  memcpy((char*)tempbuf, (char*)a_randstuff, StuffSize);

  for (int x=0; x<1000; x++)
    Cipher.Encrypt(tempbuf, tempbuf);

  /*
    hmm - seems we're not done with this context
    we throw the tempbuf into the hasher, and extract a_randhash
  */
  keyHash.Update(tempbuf, StuffSize);
  keyHash.Final(a_randhash);
}

size_t
_writecbc(FILE *fp, const unsigned char* buffer, int length, unsigned char type,
          Fish *Algorithm, unsigned char* cbcbuffer)
{
  const unsigned int BS = Algorithm->GetBlockSize();
  size_t numWritten = 0;

  // some trickery to avoid new/delete
  unsigned char block1[16];

  unsigned char *curblock = NULL;
  ASSERT(BS <= sizeof(block1)); // if needed we can be more sophisticated here...

  // First encrypt and write the length of the buffer
  curblock = block1;
  // Fill unused bytes of length with random data, to make
  // a dictionary attack harder
  PWSrand::GetInstance()->GetRandomData(curblock, BS);
  // block length overwrites 4 bytes of the above randomness.
  putInt32(curblock, length);

  // following new for format 2.0 - lengthblock bytes 4-7 were unused before.
  curblock[sizeof(length)] = type;

  if (BS == 16) {
    // In this case, we've too many (11) wasted bytes in the length block
    // So we store actual data there:
    // (11 = BlockSize - 4 (length) - 1 (type)
    const int len1 = (length > 11) ? 11 : length;
    memcpy(curblock+5, buffer, len1);
    length -= len1;
    buffer += len1;
  }

  xormem(curblock, cbcbuffer, BS); // do the CBC thing
  Algorithm->Encrypt(curblock, curblock);
  memcpy(cbcbuffer, curblock, BS); // update CBC for next round

  numWritten = fwrite(curblock, 1, BS, fp);

  if (length > 0 ||
      (BS == 8 && length == 0)) { // This part for bwd compat w/pre-3 format
    unsigned int BlockLength = ((length+(BS-1))/BS)*BS;
    if (BlockLength == 0 && BS == 8)
      BlockLength = BS;

    // Now, encrypt and write the (rest of the) buffer
    for (unsigned int x=0; x<BlockLength; x+=BS) {
      if ((length == 0) || ((length%BS != 0) && (length-x<BS))) {
        //This is for an uneven last block
        PWSrand::GetInstance()->GetRandomData(curblock, BS);
        memcpy(curblock, buffer+x, length % BS);
      } else
        memcpy(curblock, buffer+x, BS);
      xormem(curblock, cbcbuffer, BS);
      Algorithm->Encrypt(curblock, curblock);
      memcpy(cbcbuffer, curblock, BS);
      numWritten += fwrite(curblock, 1, BS, fp);
    }
  }
  trashMemory(curblock, BS);
  return numWritten;
}

/*
 * Reads an encrypted record into buffer.
 * The first block of the record contains the encrypted record length
 * We have the usual ugly problem of fixed buffer lengths in C/C++.
 * allocate the buffer here, to ensure that it's long enough.
 * *** THE CALLER MUST delete[] IT AFTER USE *** UGH++
 *
 * (unless buffer_len is zero)
 *
 * Note that the buffer is a byte array, and buffer_len is number of
 * bytes. This means that any data can be passed, and we don't
 * care at this level if strings are char or wchar_t.
 *
 * If TERMINAL_BLOCK is non-NULL, the first block read is tested against it,
 * and -1 is returned if it matches. (used in V3)
 */
size_t
_readcbc(FILE *fp,
         unsigned char* &buffer, unsigned int &buffer_len, unsigned char &type,
         Fish *Algorithm, unsigned char* cbcbuffer,
         const unsigned char *TERMINAL_BLOCK)
{
  const unsigned int BS = Algorithm->GetBlockSize();
  size_t numRead = 0;
  
  // some trickery to avoid new/delete
  unsigned char block1[16];
  unsigned char block2[16];
  unsigned char block3[16];
  unsigned char *lengthblock = NULL;

  ASSERT(BS <= sizeof(block1)); // if needed we can be more sophisticated here...
  lengthblock = block1;

  buffer_len = 0;
  numRead = fread(lengthblock, 1, BS, fp);
  if (numRead != BS) {
    return 0;
  }

  if (TERMINAL_BLOCK != NULL &&
      memcmp(lengthblock, TERMINAL_BLOCK, BS) == 0)
      return static_cast<size_t>(-1);

  unsigned char *lcpy = block2;
  memcpy(lcpy, lengthblock, BS);

  Algorithm->Decrypt(lengthblock, lengthblock);
  xormem(lengthblock, cbcbuffer, BS);
  memcpy(cbcbuffer, lcpy, BS);

  int length = getInt32(lengthblock);

  // new for 2.0 -- lengthblock[4..7] previously set to zero
  type = lengthblock[sizeof(int)]; // type is first byte after the length

  if (length < 0) { // sanity check
    TRACE("_readcbc: Read negative length - aborting\n");
    buffer = NULL;
    buffer_len = 0;
    trashMemory(lengthblock, BS);
    return 0;
  }

  buffer_len = length;
  buffer = new unsigned char[(length/BS)*BS +2*BS]; // round upwards
  unsigned char *b = buffer;

  if (BS == 16) {
    // length block contains up to 11 (= 16 - 4 - 1) bytes
    // of data
    const int len1 = (length > 11) ? 11 : length;
    memcpy(b, lengthblock+5, len1);
    length -= len1;
    b += len1;
  }

  unsigned int BlockLength = ((length+(BS-1))/BS)*BS;
  // Following is meant for lengths < BS,
  // but results in a block being read even
  // if length is zero. This is wasteful,
  // but fixing it would break all existing pre-3.0 databases.
  if (BlockLength == 0 && BS == 8)
    BlockLength = BS;

  trashMemory(lengthblock, BS);

  if (length > 0 ||
      (BS == 8 && length == 0)) { // pre-3 pain
    unsigned char *tempcbc = block3;
    numRead += fread(b, 1, BlockLength, fp);
    for (unsigned int x=0; x<BlockLength; x+=BS) {
      memcpy(tempcbc, b + x, BS);
      Algorithm->Decrypt(b + x, b + x);
      xormem(b + x, cbcbuffer, BS);
      memcpy(cbcbuffer, tempcbc, BS);
    }
  }

  if (buffer_len == 0) {
    // delete[] buffer here since caller will see zero length
    delete[] buffer;
  }
  return numRead;
}

// PWSUtil implementations

void PWSUtil::strCopy(LPTSTR target, size_t tcount, const LPCTSTR source, size_t scount)
{
#if (_MSC_VER >= 1400)
  (void) _tcsncpy_s(target, tcount, source, scount);
#else
  tcount; // shut up warning;
  (void)_tcsncpy(target, source, scount);
#endif
}

size_t PWSUtil::strLength(const LPCTSTR str)
{
  return _tcslen(str);
}

/**
 * Returns the current length of a file.
 */
long PWSUtil::fileLength(FILE *fp)
{
  long	pos;
  long	len;

  pos = ftell( fp );
  fseek( fp, 0, SEEK_END );
  len	= ftell( fp );
  fseek( fp, pos, SEEK_SET );

  return len;
}
bool
PWSUtil::VerifyImportDateTimeString(const CString &time_str, time_t &t)
{
  //  String format must be "yyyy/mm/dd hh:mm:ss"
  //                        "0123456789012345678"

  CString xtime_str;
  const int month_lengths[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  const int idigits[14] = {0, 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18};
  const int ndigits = 14;
  int yyyy, mon, dd, hh, min, ss, nscanned;

  t = (time_t)-1;

  if (time_str.GetLength() != 19)
    return false;

  // Validate time_str
  if (time_str.Mid(4,1) != '/' ||
      time_str.Mid(7,1) != '/' ||
      time_str.Mid(10,1) != ' ' ||
      time_str.Mid(13,1) != ':' ||
      time_str.Mid(16,1) != ':')
    return false;

  for (int i = 0;  i < ndigits; i++)
    if (!isdigit(time_str.GetAt(idigits[i])))
      return false;

  // Since white space is ignored with _stscanf, first verify that there are no invalid '#' characters
  // Then take copy of the string and replace all blanks by '#' (should only be 1)
  if (time_str.Find(TCHAR('#')) != (-1))
    return false;

  xtime_str = time_str;
  if (xtime_str.Replace(TCHAR(' '), TCHAR('#')) != 1)
    return false;

#if _MSC_VER >= 1400
  nscanned = _stscanf_s(xtime_str, _T("%4d/%2d/%2d#%2d:%2d:%2d"),
                      &yyyy, &mon, &dd, &hh, &min, &ss);
#else
  nscanned = _stscanf(xtime_str, _T("%4d/%2d/%2d#%2d:%2d:%2d"),
                    &yyyy, &mon, &dd, &hh, &min, &ss);
#endif

  if (nscanned != 6)
    return false;

  // Built-in obsolesence for pwsafe in 2038?
  if (yyyy < 1970 || yyyy > 2038)
    return false;

  if ((mon < 1 || mon > 12) || (dd < 1))
    return false;

  if (mon == 2 && (yyyy % 4) == 0) {
    // Feb and a leap year
    if (dd > 29)
      return false;
  } else {
    // Either (Not Feb) or (Is Feb but not a leap-year)
    if (dd > month_lengths[mon - 1])
      return false;
  }

  if ((hh < 0 || hh > 23) ||
      (min < 0 || min > 59) ||
      (ss < 0 || ss > 59))
    return false;

  const CTime ct(yyyy, mon, dd, hh, min, ss, -1);

  t = (time_t)ct.GetTime();

  return true;
}

bool
PWSUtil::VerifyASCDateTimeString(const CString &time_str, time_t &t)
{
  //  String format must be "ddd MMM dd hh:mm:ss yyyy"
  //                        "012345678901234567890123"

  const int month_lengths[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  const CString str_months = _T("JanFebMarAprMayJunJulAugSepOctNovDec");
  const CString str_days = _T("SunMonTueWedThuFriSat");
  CString xtime_str;
  TCHAR cmonth[4], cdayofweek[4];
  const int idigits[12] = {8, 9, 11, 12, 14, 15, 17, 18, 20, 21, 22, 23};
  const int ndigits = 12;
  int iMON, iDOW, nscanned;
  int yyyy, mon, dd, hh, min, ss;

  cmonth[3] = cdayofweek[3] = TCHAR('\0');

  t = (time_t)-1;

  if (time_str.GetLength() != 24)
    return false;

  // Validate time_str
  if (time_str.Mid(13,1) != ':' ||
      time_str.Mid(16,1) != ':')
    return false;

  for (int i = 0; i < ndigits; i++)
    if (!isdigit(time_str.GetAt(idigits[i])))
      return false;

  // Since white space is ignored with _stscanf, first verify that there are no invalid '#' characters
  // Then take copy of the string and replace all blanks by '#' (should be 4)
  if (time_str.Find(TCHAR('#')) != (-1))
    return false;

  xtime_str = time_str;
  if (xtime_str.Replace(TCHAR(' '), TCHAR('#')) != 4)
    return false;

#if _MSC_VER >= 1400
  nscanned = _stscanf_s(xtime_str, _T("%3c#%3c#%2d#%2d:%2d:%2d#%4d"),
                      cdayofweek, sizeof(cdayofweek), cmonth, sizeof(cmonth), &dd, &hh, &min, &ss, &yyyy);
#else
  nscanned = _stscanf(xtime_str, _T("%3c#%3c#%2d#%2d:%2d:%2d#%4d"),
                    cdayofweek, cmonth, &dd, &hh, &min, &ss, &yyyy);
#endif

  if (nscanned != 7)
    return false;

  iMON = str_months.Find(cmonth);
  if (iMON < 0)
    return false;

  mon = (iMON / 3) + 1;

  // Built-in obsolesence for pwsafe in 2038?
  if (yyyy < 1970 || yyyy > 2038)
    return false;

  if ((mon < 1 || mon > 12) || (dd < 1))
    return false;

  if (mon == 2 && (yyyy % 4) == 0) {
    // Feb and a leap year
    if (dd > 29)
      return false;
  } else {
    // Either (Not Feb) or (Is Feb but not a leap-year)
    if (dd > month_lengths[mon - 1])
      return false;
  }

  if ((hh < 0 || hh > 23) ||
      (min < 0 || min > 59) ||
      (ss < 0 || ss > 59))
    return false;

  const CTime ct(yyyy, mon, dd, hh, min, ss, -1);

  iDOW = str_days.Find(cdayofweek);
  if (iDOW < 0)
    return false;

  iDOW = (iDOW / 3) + 1;
  if (iDOW != ct.GetDayOfWeek())
    return false;

  t = (time_t)ct.GetTime();

  return true;
}

bool
PWSUtil::VerifyXMLDateTimeString(const CString &time_str, time_t &t)
{
  //  String format must be "yyyy-mm-ddThh:mm:ss"
  //                        "0123456789012345678"

  CString xtime_str;
  const int month_lengths[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  const int idigits[14] = {0, 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18};
  const int ndigits = 14;
  int yyyy, mon, dd, hh, min, ss, nscanned;

  t = (time_t)-1;

  if (time_str.GetLength() != 19)
    return false;

  // Validate time_str
  if (time_str.Mid(4,1) != '-' ||
      time_str.Mid(7,1) != '-' ||
      time_str.Mid(10,1) != 'T' ||
      time_str.Mid(13,1) != ':' ||
      time_str.Mid(16,1) != ':')
    return false;

  for (int i = 0;  i < ndigits; i++)
    if (!isdigit(time_str.GetAt(idigits[i])))
      return false;

  // Since white space is ignored with _stscanf, first verify that there are no invalid '#' characters
  // and no blanks.  Replace '-' & 'T' by '#'.
  if (time_str.Find(TCHAR('#')) != (-1))
    return false;
  if (time_str.Find(TCHAR(' ')) != (-1))
    return false;

  xtime_str = time_str;
  if (xtime_str.Replace(TCHAR('-'), TCHAR('#')) != 2)
    return false;
  if (xtime_str.Replace(TCHAR('T'), TCHAR('#')) != 1)
    return false;

#if _MSC_VER >= 1400
  nscanned = _stscanf_s(xtime_str, _T("%4d#%2d#%2d#%2d:%2d:%2d"),
                      &yyyy, &mon, &dd, &hh, &min, &ss);
#else
  nscanned = _stscanf(xtime_str, _T("%4d#%2d#%2d#%2d:%2d:%2d"),
                    &yyyy, &mon, &dd, &hh, &min, &ss);
#endif

  if (nscanned != 6)
    return false;

  // Built-in obsolesence for pwsafe in 2038?
  if (yyyy < 1970 || yyyy > 2038)
    return false;

  if ((mon < 1 || mon > 12) || (dd < 1))
    return false;

  if (mon == 2 && (yyyy % 4) == 0) {
    // Feb and a leap year
    if (dd > 29)
      return false;
  } else {
    // Either (Not Feb) or (Is Feb but not a leap-year)
    if (dd > month_lengths[mon - 1])
      return false;
  }

  if ((hh < 0 || hh > 23) ||
      (min < 0 || min > 59) ||
      (ss < 0 || ss > 59))
    return false;

  const CTime ct(yyyy, mon, dd, hh, min, ss, -1);

  t = (time_t)ct.GetTime();

  return true;
}

const TCHAR *PWSUtil::UNKNOWN_XML_TIME_STR = _T("1970-01-01 00:00:00");
const TCHAR *PWSUtil::UNKNOWN_ASC_TIME_STR = _T("Unknown");

CMyString
PWSUtil::ConvertToDateTimeString(const time_t &t, const int result_format)
{
  CMyString ret;
  if (t != 0) {
		TCHAR time_str[80], datetime_str[80];
#if _MSC_VER >= 1400
		struct tm st;
		errno_t err;
    	err = localtime_s(&st, &t);  // secure version
    	ASSERT(err == 0);
    	if ((result_format & TMC_EXPORT_IMPORT) == TMC_EXPORT_IMPORT)
      		_stprintf_s(datetime_str, 20, _T("%04d/%02d/%02d %02d:%02d:%02d"),
            		    st.tm_year+1900, st.tm_mon+1, st.tm_mday, st.tm_hour,
                		st.tm_min, st.tm_sec);
    	else if ((result_format & TMC_XML) == TMC_XML)
      		_stprintf_s(datetime_str, 20, _T("%04d-%02d-%02dT%02d:%02d:%02d"),
            		    st.tm_year+1900, st.tm_mon+1, st.tm_mday, st.tm_hour,
                		st.tm_min, st.tm_sec);
        else if ((result_format & TMC_LOCALE) == TMC_LOCALE) {
            SYSTEMTIME systime;
            systime.wYear = (WORD)st.tm_year+1900;
            systime.wMonth = (WORD)st.tm_mon+1;
            systime.wDay = (WORD)st.tm_mday;
            systime.wDayOfWeek = (WORD) st.tm_wday;
            systime.wHour = (WORD)st.tm_hour;
            systime.wMinute = (WORD)st.tm_min;
            systime.wSecond = (WORD)st.tm_sec;
            systime.wMilliseconds = (WORD)0;
            TCHAR szBuf[80];
            VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_SSHORTDATE, szBuf, 80));
            GetDateFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, datetime_str, 80);
            szBuf[0] = _T(' ');  // Put a blank between date and time
            VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_STIMEFORMAT, &szBuf[1], 79));
            GetTimeFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, time_str, 80);
            _tcscat_s(datetime_str, 80, time_str);
        } else {
      		err = _tasctime_s(datetime_str, 32, &st);  // secure version
    		ASSERT(err == 0);
      	}
    	ret = datetime_str;
#else
    	TCHAR *t_str_ptr;
		struct tm *st;
    	st = localtime(&t);
        ASSERT(st != NULL); // null means invalid time
    	if ((result_format & TMC_EXPORT_IMPORT) == TMC_EXPORT_IMPORT) {
      		_stprintf(datetime_str, _T("%04d/%02d/%02d %02d:%02d:%02d"),
            	  st->tm_year+1900, st->tm_mon+1, st->tm_mday,
            	  st->tm_hour, st->tm_min, st->tm_sec);
      		t_str_ptr = datetime_str;
    	} else if ((result_format & TMC_XML) == TMC_XML) {
      		_stprintf(time_str, _T("%04d-%02d-%02dT%02d:%02d:%02d"),
            	  st->tm_year+1900, st->tm_mon+1, st->tm_mday,
            	  st->tm_hour, st->tm_min, st->tm_sec);
      		t_str_ptr = datetime_str;
    	} else if ((result_format & TMC_LOCALE) == TMC_LOCALE) {
            SYSTEMTIME systime;
            systime.wYear = (WORD)st.tm_year+1900;
            systime.wMonth = (WORD)st.tm_mon+1;
            systime.wDay = (WORD)st.tm_mday;
            systime.wDayOfWeek = (WORD) st.tm_wday;
            systime.wHour = (WORD)st.tm_hour;
            systime.wMinute = (WORD)st.tm_min;
            systime.wSecond = (WORD)st.tm_sec;
            systime.wMilliseconds = (WORD)0;
            TCHAR szBuf[80];
            VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_SSHORTDATE, szBuf, 80));
            GetDateFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, datetime_str, 80);
            szBuf[0] = _T(' ');  // Put a blank between date and time
            VERIFY(::GetLocaleInfo(LOCALE_USER_DEFAULT, LOCALE_STIMEFORMAT, &szBuf[1], 79));
            GetTimeFormat(LOCALE_USER_DEFAULT, 0, &systime, szBuf, time_str, 80);
            _tcscat(datetime_str, 80, time_str);
      		t_str_ptr = datetime_str;
    	} else
      		t_str_ptr = _tasctime(st);

    	ret = t_str_ptr;
#endif
  } else {
	  switch (result_format) {
		case TMC_ASC_UNKNOWN:
			ret = UNKNOWN_ASC_TIME_STR;
			break;
		case TMC_XML:
			ret = UNKNOWN_XML_TIME_STR;
			break;
		default:
			ret = _T("");
	  }
  }
  // remove the trailing EOL char.
  ret.TrimRight();
  return ret;
}

int
PWSUtil::VerifyImportPWHistoryString(const TCHAR *PWHistory, CMyString &newPWHistory, 
                                     CString &strErrors)
{
	// Format is (! == mandatory blank, unless at the end of the record):
	//    sxx00
	// or
	//    sxxnn!yyyy/mm/dd!hh:mm:ss!llll!pppp...pppp!yyyy/mm/dd!hh:mm:ss!llll!pppp...pppp!.........
	// Note:
	//    !yyyy/mm/dd!hh:mm:ss! may be !1970-01-01 00:00:00! meaning unknown

	CMyString tmp, pwh;
	CString buffer;
	int ipwlen, s = -1, m = -1, n = -1;
	int rc = PWH_OK;
	time_t t;

	newPWHistory = _T("");
	strErrors = _T("");

	pwh = CMyString(PWHistory);
	int len = pwh.GetLength();
	int pwleft = len;

	if (pwleft == 0)
		return PWH_OK;

	if (pwleft < 5) {
		rc = PWH_INVALID_HDR;
		goto exit;
	}

	TCHAR *lpszPWHistory = pwh.GetBuffer(len + sizeof(TCHAR));

#if _MSC_VER >= 1400
	int iread = _stscanf_s(lpszPWHistory, _T("%01d%02x%02x"), &s, &m, &n);
#else
	int iread = _stscanf(lpszPWHistory, _T("%01d%02x%02x"), &s, &m, &n);
#endif
	if (iread != 3) {
		rc = PWH_INVALID_HDR;
		goto relbuf;
	}

	if (s != 0 && s != 1) {
		rc = PWH_INVALID_STATUS;
		goto relbuf;
	}

	if (n > m) {
		rc = PWH_INVALID_NUM;
		goto relbuf;
	}

	lpszPWHistory += 5;
	pwleft -= 5;

	if (pwleft == 0 && s == 0 && m == 0 && n == 0) {
		rc = PWH_IGNORE;
		goto relbuf;
	}

	buffer.Format(_T("%01d%02x%02x"), s, m, n);
	newPWHistory = CMyString(buffer);

	for (int i = 0; i < n; i++) {
		if (pwleft < 26) {		//  blank + date(10) + blank + time(8) + blank + pw_length(4) + blank
			rc = PWH_TOO_SHORT;
			goto relbuf;
		}

		if (lpszPWHistory[0] != _T(' ')) {
			rc = PWH_INVALID_CHARACTER;
			goto relbuf;
		}

		lpszPWHistory += 1;
		pwleft -= 1;

    tmp = CMyString(lpszPWHistory, 19);

		if (tmp.Left(10) == _T("1970-01-01"))
			t = 0;
		else {
			if (!VerifyImportDateTimeString(tmp, t)) {
				rc = PWH_INVALID_DATETIME;
				goto relbuf;
			}
		}

		lpszPWHistory += 19;
		pwleft -= 19;

		if (lpszPWHistory[0] != _T(' ')) {
			rc = PWH_INVALID_CHARACTER;
			goto relbuf;
		}

		lpszPWHistory += 1;
		pwleft -= 1;

#if _MSC_VER >= 1400
		iread = _stscanf_s(lpszPWHistory, _T("%04x"), &ipwlen);
#else
		iread = _stscanf(lpszPWHistory, _T("%04x"), &ipwlen);
#endif
		if (iread != 1) {
			rc = PWH_INVALID_PSWD_LENGTH;
			goto relbuf;
		}

		lpszPWHistory += 4;
		pwleft -= 4;

		if (lpszPWHistory[0] != _T(' ')) {
			rc = PWH_INVALID_CHARACTER;
			goto relbuf;
		}

		lpszPWHistory += 1;
		pwleft -= 1;

		if (pwleft < ipwlen) {
			rc = PWH_INVALID_PSWD_LENGTH;
			goto relbuf;
		}

    tmp = CMyString(lpszPWHistory, ipwlen);
		buffer.Format(_T("%08x%04x%s"), (long) t, ipwlen, tmp);
		newPWHistory += CMyString(buffer);
		buffer.Empty();
		lpszPWHistory += ipwlen;
		pwleft -= ipwlen;
	}

	if (pwleft > 0)
		rc = PWH_TOO_LONG;

	relbuf: pwh.ReleaseBuffer();

	exit: buffer.Format(IDSC_PWHERROR, len - pwleft + 1);
	CString temp;
	switch (rc) {
		case PWH_OK:
		case PWH_IGNORE:
			temp.Empty();
			buffer.Empty();
			break;
		case PWH_INVALID_HDR:
			temp.Format(IDSC_INVALIDHEADER, PWHistory);
			break;
		case PWH_INVALID_STATUS:
			temp.Format(IDSC_INVALIDPWHSTATUS, s);
			break;
		case PWH_INVALID_NUM:
			temp.Format(IDSC_INVALIDNUMOLDPW, n, m);
			break;
		case PWH_INVALID_DATETIME:
			temp.LoadString(IDSC_INVALIDDATETIME);
			break;
		case PWH_INVALID_PSWD_LENGTH:
			temp.LoadString(IDSC_INVALIDPWLENGTH);
			break;
		case PWH_TOO_SHORT:
			temp.LoadString(IDSC_FIELDTOOSHORT);
			break;
		case PWH_TOO_LONG:
			temp.LoadString(IDSC_FIELDTOOLONG);
			break;
		case PWH_INVALID_CHARACTER:
			temp.LoadString(IDSC_INVALIDSEPARATER);
			break;
		default:
			ASSERT(0);
	}
	strErrors = buffer + temp;
	if (rc != PWH_OK)
		newPWHistory = _T("");

	return rc;
}

CMyString
PWSUtil::GetNewFileName(const CMyString &oldfilename, const CString &newExtn)
{
	TCHAR path_buffer[_MAX_PATH];
	TCHAR drive[_MAX_DRIVE];
	TCHAR dir[_MAX_DIR];
	TCHAR fname[_MAX_FNAME];
	TCHAR ext[_MAX_EXT];

#if _MSC_VER >= 1400
	_tsplitpath_s( oldfilename, drive, _MAX_DRIVE, dir, _MAX_DIR, fname,
                       _MAX_FNAME, ext, _MAX_EXT );
	_tmakepath_s( path_buffer, _MAX_PATH, drive, dir, fname, newExtn );
#else
	_tsplitpath( oldfilename, drive, dir, fname, ext );
	_tmakepath( path_buffer, drive, dir, fname, newExtn );
#endif
	return CMyString(path_buffer);
}

void
PWSUtil::IssueError(const CString &csFunction)
{
#ifdef _DEBUG
    LPVOID lpMsgBuf;
    LPVOID lpDisplayBuf;

    const DWORD dw = GetLastError();

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER | 
        FORMAT_MESSAGE_FROM_SYSTEM,
        NULL,
        dw,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (LPTSTR) &lpMsgBuf,
        0, NULL );

    lpDisplayBuf = (LPVOID)LocalAlloc(LMEM_ZEROINIT, 
        (lstrlen((LPCTSTR)lpMsgBuf) + csFunction.GetLength() + 40) * sizeof(TCHAR)); 
    wsprintf((LPTSTR)lpDisplayBuf, TEXT("%s failed with error %d: %s"), 
        csFunction, dw, lpMsgBuf); 
    MessageBox(NULL, (LPCTSTR)lpDisplayBuf, TEXT("Error"), MB_OK); 

    LocalFree(lpMsgBuf);
    LocalFree(lpDisplayBuf);
#else
	csFunction;
#endif
}

CString
PWSUtil::GetTimeStamp()
{
	struct _timeb timebuffer;
#if (_MSC_VER >= 1400)
	_ftime_s(&timebuffer);
#else
	_ftime(&timebuffer);
#endif
	CMyString cmys_now = ConvertToDateTimeString(timebuffer.time, TMC_EXPORT_IMPORT);

	CString cs_now;
	cs_now.Format(_T("%s.%03hu"), cmys_now, timebuffer.millitm);

	return cs_now;
}

/*

  Produce a printable version of memory dump (hex + ascii)

  paramaters:
    memory  - pointer to memory to format
    length  - length to format
    maxnum  - maximum characters dumped per line

  return:
    CString containing output buffer
*/
void
PWSUtil::HexDump(unsigned char *pmemory, const int length, 
                 const CString cs_prefix, const int maxnum)
{
#ifdef _DEBUG
  unsigned char *pmem;
  CString cs_outbuff, cs_hexbuff, cs_charbuff;
  int i, j, len(length);
  unsigned char c;

  pmem = pmemory;
  while (len > 0) {
    // Show offset for this line.
    cs_charbuff.Empty();
    cs_hexbuff.Empty();
    cs_outbuff.Format(_T("%s: %08x *"), cs_prefix, pmem);

    // Format hex portion of line and save chars for ascii portion
    if (len > maxnum)
      j = maxnum;
    else
      j = len;

    for (i = 0; i < j; i++) {
      c = *pmem++;

      if ((i % 4) == 0 && i != 0)
        cs_outbuff += _T(' ');

      cs_hexbuff.Format(_T("%02x"), c);
      cs_outbuff += cs_hexbuff;

      if (c >= 32 && c < 127)
        cs_charbuff += (TCHAR)c;
      else
        cs_charbuff += _T('.');
    }

    j = maxnum - j;

    // Fill out hex portion of short lines.
    for (i = j; i > 0; i--) {
      if ((i % 4) != 0)
        cs_outbuff += _T("  ");
      else
        cs_outbuff += _T("   ");
    }

    // Add ASCII character portion to line.
    cs_outbuff += _T("* |");
    cs_outbuff += cs_charbuff;

    // Fill out end of short lines.
    for (i = j; i > 0; i--)
      cs_outbuff += _T(' ');

    cs_outbuff += _T('|');

    // Next line
    len -= maxnum;

    TRACE(_T("%s\n"), cs_outbuff);
  };
#else
  pmemory; length; cs_prefix; maxnum;
#endif
}

CString
PWSUtil::Base64Encode(const BYTE *strIn, size_t len)
{
  CString cs_Out;
  static const CHAR base64ABC[] = 
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  
  cs_Out.Empty();
  for (DWORD i = 0; i < (DWORD)len; i += 3) {
    LONG l = ( ((LONG)strIn[i]) << 16 ) | 
      (((i + 1) < len) ? (((LONG)strIn[i + 1]) << 8) : 0) | 
      (((i + 2) < len) ? ((LONG)strIn[i + 2]) : 0);

    cs_Out += base64ABC[(l >> 18) & 0x3F];
    cs_Out += base64ABC[(l >> 12) & 0x3F];
    if (i + 1 < len) cs_Out += base64ABC[(l >> 6) & 0x3F];
    if (i + 2 < len) cs_Out += base64ABC[(l ) & 0x3F];
  }
  
  switch (len % 3) {
    case 1:
      cs_Out += '=';
    case 2:
      cs_Out += '=';
  } 

  return cs_Out;
}

void
PWSUtil::Base64Decode(const LPCTSTR sz_inString, BYTE* &outData, size_t &out_len)
{
  static const char szCS[]=
    "=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  int iDigits[4] = {0,0,0,0};

  CString cs_inString(sz_inString);

  size_t st_length = 0;
  const int in_length = cs_inString.GetLength();

  int i1, i2, i3;
  for (i2 = 0; i2 < (int)in_length; i2 += 4) {
    iDigits[0] = iDigits[1] = iDigits[2] = iDigits[3] = -1;

    for (i1 = 0; i1 < sizeof(szCS) - 1; i1++) {
      for (i3 = i2; i3 < i2 + 4; i3++) {
        if (i3 < (int)in_length &&  cs_inString[i3] == szCS[i1])
          iDigits[i3 - i2] = i1 - 1;
      }
    }

    outData[st_length] = ((BYTE)iDigits[0] << 2);

    if (iDigits[1] >= 0) {
      outData[st_length] += ((BYTE)iDigits[1] >> 4) & 0x3;
    }

    st_length++;

    if (iDigits[2] >= 0) {
      outData[st_length++] = (((BYTE)iDigits[1] & 0x0f) << 4)
                       | (((BYTE)iDigits[2] >> 2) & 0x0f);
    }

    if (iDigits[3] >= 0) {
      outData[st_length++] = (((BYTE)iDigits[2] & 0x03) << 6)
                       | ((BYTE)iDigits[3] & 0x3f);
    }
  }

  out_len = st_length;
}

