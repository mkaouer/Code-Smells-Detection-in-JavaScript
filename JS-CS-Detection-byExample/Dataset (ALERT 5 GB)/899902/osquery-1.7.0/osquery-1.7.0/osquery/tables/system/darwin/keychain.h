/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <map>
#include <set>
#include <vector>

#include <CoreFoundation/CoreFoundation.h>
#include <Security/Security.h>

#include <openssl/x509v3.h>
#include <openssl/bn.h>
#include <openssl/asn1.h>
#include <openssl/x509.h>
#include <openssl/x509_vfy.h>
#include <openssl/pem.h>
#include <openssl/bio.h>

#include <osquery/tables.h>

#include "osquery/core/conversions.h"

// If using the system-provided OpenSSL on 10.10, mark x509 methods deprecated.
#ifdef SSL_TXT_TLSV1_2
#define OSX_OPENSSL(expr) \
  do {                    \
    expr;                 \
  } while (0)
#else
#define OSX_OPENSSL(expr) OSQUERY_USE_DEPRECATED(expr)
#endif

namespace osquery {
namespace tables {

extern const std::vector<std::string> kSystemKeychainPaths;
extern const std::vector<std::string> kUserKeychainPaths;

// The flags are defined in openssl/x509v3.h,
// and its keys in crypto/x509v3/v3_bitst.c
// clang-format off
const std::map<unsigned long, std::string> kKeyUsageFlags = {
    {0x0001, "Encipher Only"},
    {0x0002, "CRL Sign"},
    {0x0004, "Key Cert Sign"},
    {0x0008, "Key Agreement"},
    {0x0010, "Data Encipherment"},
    {0x0020, "Key Encipherment"},
    {0x0040, "Non Repudiation"},
    {0x0080, "Digital Signature"},
    {0x8000, "Decipher Only"}};
// clang-format on

void genKeychains(const std::string& path, CFMutableArrayRef& keychains);
std::string getKeychainPath(const SecKeychainItemRef& item);

/// Certificate property parsing functions.
std::string genKIDProperty(const unsigned char* data, int len);

/// Generate the public key algorithm and signing algorithm.
void genAlgorithmProperties(X509* cert,
                            std::string& key,
                            std::string& sig,
                            std::string& size);

/// Generate common name and subject.
void genCommonName(X509* cert,
                   std::string& subject,
                   std::string& common_name,
                   std::string& issuer);
time_t genEpoch(ASN1_TIME* time);

std::string genSHA1ForCertificate(const CFDataRef& raw_cert);
bool CertificateIsCA(X509* cert);
bool CertificateIsSelfSigned(X509* cert);

/// Generate a list of keychain items for a given item type.
CFArrayRef CreateKeychainItems(const std::set<std::string>& paths,
                               const CFTypeRef& item_type);

std::set<std::string> getKeychainPaths();
std::string genKeyUsage(unsigned long flag);
std::string genHumanReadableDateTime(ASN1_TIME* time);
}
}
