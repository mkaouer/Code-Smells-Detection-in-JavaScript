// PWSfileV3.h
// Abstract the gory details of reading and writing an encrypted database
//-----------------------------------------------------------------------------

#ifndef PWSfileV3_h
#define PWSfileV3_h

#include "PWSfile.h"
#include "TwoFish.h"
#include "sha256.h"
#include "hmac.h"

class PWSfileV3 : public PWSfile {
 public:
  static int CheckPassword(const CMyString &filename,
                           const CMyString &passkey,
                           FILE *a_fd = NULL,
                           unsigned char *aPtag = NULL);
  static bool IsV3x(const CMyString &filename, VERSION &v);

  PWSfileV3(const CMyString &filename, RWmode mode, VERSION version);
  ~PWSfileV3();

  virtual int Open(const CMyString &passkey);
  virtual int Close();

  virtual int WriteRecord(const CItemData &item);
  virtual int ReadRecord(CItemData &item);

 private:
  unsigned char m_ipthing[TwoFish::BLOCKSIZE]; // for CBC
  unsigned char m_key[32];
  HMAC_SHA256 m_hmac;
  CMyString m_prefString; // prefererences stored in the file
  virtual int WriteCBC(unsigned char type, const CString &data);
  virtual int WriteCBC(unsigned char type, const unsigned char *data,
                       unsigned int length);
  virtual int ReadCBC(unsigned char &type, CMyString &data);
  virtual int ReadCBC(unsigned char &type, unsigned char *data,
                      unsigned int &length);
  int WriteHeader();
  int ReadHeader();
  bool ToUTF8(const CString &data);
  bool FromUTF8(CMyString &data);
  // above functions use the following for out/in for efficiency
  unsigned char *m_utf8;
  int m_utf8Len;
  int m_utf8MaxLen;
  // reuse interim buffers efficiently
  wchar_t *m_wc;
  int m_wcMaxLen;
  unsigned char *m_tmp;
  int m_tmpMaxLen;
  // above pointers allocated dynamically and monotically increase in size
  // for efficiency w/o arbitrary restrictions
  // deallocated by Close() and d'tor

  static void StretchKey(const unsigned char *salt, unsigned long saltLen,
                         const unsigned char *passkey, unsigned long passLen,
                         unsigned int N, unsigned char *Ptag);
};

#endif PWSfileV3_h

