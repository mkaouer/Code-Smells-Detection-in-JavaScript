/* ====================================================================
 * The QuickFIX Software License, Version 1.0
 *
 * Copyright (c) 2001 ThoughtWorks, Inc.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        ThoughtWorks, Inc. (http://www.thoughtworks.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "QuickFIX" and "ThoughtWorks, Inc." must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact quickfix-users@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "QuickFIX",
 *    nor may "QuickFIX" appear in their name, without prior written
 *    permission of ThoughtWorks, Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THOUGHTWORKS INC OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

#ifdef _MSC_VER
#pragma warning( disable : 4503 4355 4786 )
#include "stdafx.h"
#else
#include "config.h"
#endif

#include "MessageValidator.h"
#include "SAXContentHandler.h"

#ifdef _MSC_VER
#include <atlbase.h>
#include <atlconv.h>
#else
#include <unistd.h>
#endif

namespace FIX
{
#ifdef _MSC_VER
  class FIXContent : public SAXContentHandlerImpl
  {
  public:
    FIXContent(MessageValidator& validator) : m_state(validator) {}

  private:
    HRESULT STDMETHODCALLTYPE FIXContent::startElement
    (
     /* [in] */ wchar_t *,
     /* [in] */ int,
     /* [in] */ wchar_t *localName,
     /* [in] */ int localNameLen,
     /* [in] */ wchar_t *,
     /* [in] */ int,
     /* [in] */ ISAXAttributes *pAttr)
    {
      USES_CONVERSION;
      std::string elemName =
        W2A(std::wstring(localName, localNameLen).c_str());
      std::map<std::string, std::string> attrs;
      int length; pAttr->getLength(&length);

      for (int i = 0; i < length; ++i)
        {
          wchar_t* attrLocalName = 0;
          int attrLocalNameLen = 0;
          if(pAttr->getLocalName(i, &attrLocalName, &attrLocalNameLen) != S_OK)
            return E_FAIL;

      wchar_t* attrValue = 0;
      int attrValueLen = 0;
      if(pAttr->getValue(i, &attrValue, &attrValueLen) != S_OK)
        return E_FAIL;

      attrs[W2A(std::wstring(attrLocalName, attrLocalNameLen).c_str())]
        = W2A(std::wstring(attrValue, attrValueLen).c_str());
    }

    m_state.startElement(elemName, attrs);
    return S_OK;
  }

  virtual HRESULT STDMETHODCALLTYPE endElement
  (
   /* [in] */ wchar_t *,
   /* [in] */ int,
   /* [in] */ wchar_t *localName,
   /* [in] */ int localNameLen,
   /* [in] */ wchar_t *,
   /* [in] */ int)
  {
    USES_CONVERSION;
    m_state.endElement(W2A(std::wstring(localName, localNameLen).c_str()));
    return S_OK;
  }

private:
  ParserState m_state;
};
#else
  void fixStartElement(void* ctx, const xmlChar* name, const xmlChar** xmlAtts)
  {
    ParserState* pState = static_cast<ParserState*>(ctx);
    std::map<std::string, std::string> attrs;
    if(xmlAtts != 0)
      {
        while (*xmlAtts != 0)
        {
          attrs[(char*)*xmlAtts] = (char*)*(xmlAtts + 1);
          xmlAtts += 2;
        }
      }

    pState->startElement((char*)name, attrs);
  }

  void fixEndElement(void* ctx, const xmlChar* name)
  {
    ParserState* pState = static_cast<ParserState*>(ctx);
    pState->endElement((char*)name);
  }

  xmlSAXHandler FIXContent = {
    0, //internalSubset;
    0, //isStandalone;
    0, //hasInternalSubset;
    0, //hasExternalSubset;
    0, //resolveEntity;
    0, //getEntity;
    0, //entityDecl;
    0, //notationDecl;
    0, //attributeDecl;
    0, // elementDecl;
    0, //unparsedEntityDecl;
    0, //setDocumentLocator;
    0, //startDocument;
    0, //endDocument;
    fixStartElement, //startElement;
    fixEndElement, //endElement;
    0, //reference;
    0, //characters;
    0, //ignorableWhitespace;
    0, //processingInstruction;
    0, //comment;
    0, //warning;
    0, //error;
    0, //fatalError;
    0, //getParameterEntity;
    0, //cdataBlock;
  };

#endif

  MessageValidator::MessageValidator(const std::string& url)
  {
    readFromURL(url);
  }

  void MessageValidator::readFromURL(const std::string& url)
  {
#ifdef _MSC_VER
    readMSXML(url);
#else
    readLibXml(url);
#endif
  }

#ifdef _MSC_VER
  void MessageValidator::readMSXML(const std::string& url)
  {
    CoInitialize(NULL);
    ISAXXMLReader* pRdr = NULL;

    HRESULT hr = CoCreateInstance(
    __uuidof(SAXXMLReader),
    NULL,
    CLSCTX_ALL,
    __uuidof(ISAXXMLReader),
    (void **)&pRdr);

    DWORD bufferLen = 256;
    char buffer[255];
    std::string current;
#ifdef _MSC_VER
    if(url.find(':') == std::string::npos)
    {
#else
    if(*url.begin() != '/')
    {
#endif
      GetCurrentDirectory(bufferLen, buffer);
      current = buffer; current+="/";
    }

    USES_CONVERSION;
    std::wstring wurl = A2W((current + url).c_str());

    FIXContent* pContent = new FIXContent(*this);
    hr = pRdr->putContentHandler(pContent);
    hr = pRdr->parseURL(const_cast<wchar_t*>(wurl.c_str()));
    if(FAILED(hr)) 
      throw ConfigError("Could not parse XML file " + current + url);
    pRdr->Release();
    CoUninitialize();
  }

#else

  void MessageValidator::readLibXml(const std::string& url)
  {
    char buffer[PATH_MAX];
    getcwd(buffer, PATH_MAX);
    std::string current(buffer);
    current = current + "/" + url;

    FILE* file = fopen(const_cast<char*>(current.c_str()), "r");
    if(file != 0)
    {
      int result;
      char chars[10];
      xmlParserCtxtPtr ctxt;
      ParserState state(*this);

      result = fread(chars, 1, 4, file);
      if(result > 0)
      {
        ctxt = xmlCreatePushParserCtxt
        ( &FIXContent, &state,
        chars, result, const_cast<char*>(current.c_str()));

        while((result = fread(chars, 1, 3, file)) > 0)
          xmlParseChunk(ctxt, chars, result, 0);

        xmlParseChunk(ctxt, chars, 0, 1);
        xmlFreeParserCtxt(ctxt);
      }
      fclose(file);
    }
  }
#endif
}
