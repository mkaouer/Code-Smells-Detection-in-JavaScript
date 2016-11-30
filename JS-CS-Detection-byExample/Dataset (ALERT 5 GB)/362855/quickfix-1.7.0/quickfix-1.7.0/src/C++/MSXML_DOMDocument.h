/* -*- C++ -*- */
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

#ifndef FIX_MSXMLDOMDOCUMENT_H
#define FIX_MSXMLDOMDOCUMENT_H

#ifdef _MSC_VER
#pragma warning( disable : 4503 4355 4786 4290 )
#endif

#include "DOMDocument.h"
#include "Exceptions.h"

namespace FIX
{
  class MSXML_DOMAttributes : public DOMAttributes
  {
  public:
    MSXML_DOMAttributes( MSXML2::IXMLDOMNode* pNode )
    {
      pNode->get_attributes(&m_pNodeMap);
    }

    ~MSXML_DOMAttributes();

    bool get( const std::string&, std::string& );
    DOMAttributes::map toMap();

  private:
    MSXML2::IXMLDOMNamedNodeMap* m_pNodeMap;
  };

  class MSXML_DOMNode : public DOMNode
  {
  public:
    MSXML_DOMNode( MSXML2::IXMLDOMNode* pNode )
    : m_pNode( pNode ) {}

    ~MSXML_DOMNode();

    DOMNodePtr getFirstChildNode();
    DOMNodePtr getNextSiblingNode();
    DOMAttributesPtr getAttributes();
    std::string getName();
    std::string getText();

  private:
    MSXML2::IXMLDOMNode* m_pNode;
  };

  class MSXML_DOMDocument : public DOMDocument
  {
  public:
    MSXML_DOMDocument() throw( ConfigError& );
    ~MSXML_DOMDocument();

    bool load( std::istream& );
    bool load( const std::string& );
    bool xml( std::ostream& );

    DOMNodePtr getNode( const std::string& );

  private:
    MSXML2::IXMLDOMDocument2* m_pDoc;
  };
}

#endif
