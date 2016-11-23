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

#ifndef FIX_MESSAGEVALIDATOR_H
#define FIX_MESSAGEVALIDATOR_H

#include "Message.h"
#include "Fields.h"
#include "Values.h"
#include "FieldConvertors.h"
#include <map>
#include <set>
#include <iostream>
namespace FIX
{
  /// Validates a message based on an XML definition of the FIX specification.
  class MessageValidator
  {
  public:
    MessageValidator() : m_lastField(0) {}
    MessageValidator(const std::string& url);
    void readFromURL(const std::string& url);

    /// Validate a message.
    void validate( const Message& message ) throw(std::exception&)
    {
      BeginString beginString;
      MsgType msgType;
      message.getHeader().getField(beginString);
      message.getHeader().getField(msgType);

      if(m_beginString.length() && m_beginString != beginString.getValue())
        throw UnsupportedVersion();
      if(!message.hasValidStructure())
        throw FieldsOutOfOrder();

      if(m_beginString.length())
        {
          checkMsgType(msgType);
          checkHasRequired(message, msgType);
        }

      iterate(message.getHeader(), msgType);
      iterate(message.getTrailer(), msgType);
      iterate(message, msgType);
    }

    typedef std::set<int> MsgFields;
    typedef std::map<std::string, MsgFields> MsgTypeToField;
    typedef std::set<std::string> MsgTypes;
    typedef std::map<int, TYPE::Type> Fields;
    typedef std::set<char> Values;
    typedef std::map<int, Values> FieldToValue;

    void setVersion(const std::string& beginString)
    {
      m_beginString = beginString;
    }
    std::string getVersion()
    {
      return m_beginString;
    }

    void setLastField(int field)
    {
      if(field > m_lastField)
        m_lastField = field;
    }

    void addMsgType(const std::string& msgType)
    {
      m_messages.insert(msgType);
    }

    void addMsgField(const std::string& msgType, int field)
    {
      m_messageFields[msgType].insert(field);
    }

    void addField(int field, TYPE::Type type)
    {
      m_fields[field] = type;
    }

    void addRequiredField(const std::string& msgType, int field)
    {
      m_requiredFields[msgType].insert(field);
    }

    void addValue(int field, char value)
    {
      m_fieldValues[field].insert(value);
    }

  private:
    /// Read XML file using MSXML.
    void readMSXML(const std::string&);
    /// Read XML file using libXML.
    void readLibXml(const std::string&);

    /// Iterate through fields while applying checks.
    void iterate(const FieldMap& map, const MsgType& msgType)
    {
      FieldMap::iterator i;
      for(i = map.begin(); i != map.end(); ++i)
        {
          const FieldBase& field = i->second;
          checkHasValue(field);
          checkValidFormat(field);
          checkValue(field);

          if(m_beginString.length())
            {
              checkValidTagNumber(field);
              if(!Message::isHeaderField(field)
                 && !Message::isTrailerField(field))
                {
                  checkIsInMessage(field, msgType);
                }
            }
        }
    }

    /// Check if message type is defined in spec.
    void checkMsgType(const MsgType& msgType)
    {
      if(m_messages.find(msgType.getValue())
         == m_messages.end())
        { throw InvalidMessageType(); }
    }

    /// Check if field tag number is defined in spec.
    void checkValidTagNumber(const FieldBase& field)
      throw(InvalidTagNumber&)
    {
      if(field.getField() > m_lastField || field.getField() <= 0)
        throw InvalidTagNumber(field.getField());
    }

    void checkValidFormat(const FieldBase& field)
      throw(IncorrectDataFormat&)
    {
      try
        {
          TYPE::Type type = TYPE::Unknown;
          Fields::iterator i = m_fields.find(field.getField());
          if(i != m_fields.end()) type = i->second;
          switch(type)
            {
            case TYPE::String:
              STRING_CONVERTOR::convert(field.getString()); break;
            case TYPE::Char:
              CHAR_CONVERTOR::convert(field.getString()); break;
            case TYPE::Price:
              PRICE_CONVERTOR::convert(field.getString()); break;
            case TYPE::Int:
              INT_CONVERTOR::convert(field.getString()); break;
            case TYPE::Amt:
              AMT_CONVERTOR::convert(field.getString()); break;
            case TYPE::Qty:
              QTY_CONVERTOR::convert(field.getString()); break;
            case TYPE::Currency:
              CURRENCY_CONVERTOR::convert(field.getString()); break;
            case TYPE::MultipleValueString:
              MULTIPLEVALUESTRING_CONVERTOR::convert(field.getString()); break;
            case TYPE::Exchange:
              EXCHANGE_CONVERTOR::convert(field.getString()); break;
            case TYPE::UtcTimeStamp:
              UTCTIMESTAMP_CONVERTOR::convert(field.getString()); break;
            case TYPE::Boolean:
              BOOLEAN_CONVERTOR::convert(field.getString()); break;
            case TYPE::LocalMktDate:
              LOCALMKTDATE_CONVERTOR::convert(field.getString()); break;
            case TYPE::Data:
              DATA_CONVERTOR::convert(field.getString()); break;
            case TYPE::Float:
              FLOAT_CONVERTOR::convert(field.getString()); break;
            case TYPE::PriceOffset:
              PRICEOFFSET_CONVERTOR::convert(field.getString()); break;
            case TYPE::MonthYear:
              MONTHYEAR_CONVERTOR::convert(field.getString()); break;
            case TYPE::DayOfMonth:
              DAYOFMONTH_CONVERTOR::convert(field.getString()); break;
            case TYPE::UtcDate:
              UTCDATE_CONVERTOR::convert(field.getString()); break;
            case TYPE::UtcTimeOnly:
              UTCTIMEONLY_CONVERTOR::convert(field.getString()); break;
            case TYPE::Unknown: break;
            }
        }
      catch(FieldConvertError&)
        { throw IncorrectDataFormat(field.getField()); }
    }

    void checkValue(const FieldBase& field)
      throw(IncorrectTagValue&)
    {
      FieldToValue::iterator i = m_fieldValues.find(field.getField());
      if(i == m_fieldValues.end()) return;

      const std::string& value = field.getString();
      if(value.size() != 1) throw IncorrectTagValue(field.getField());

      if(i->second.find(*value.begin()) == i->second.end())
        throw IncorrectTagValue(field.getField());
    }

    /// Check if a field has a value.
    void checkHasValue(const FieldBase& field)
      throw(NoTagValue&)
    {
      if(!field.getString().length())
        throw NoTagValue(field.getField());
    }

    /// Check if a field is in this message type.
    void checkIsInMessage
    (const FieldBase& field, const MsgType& msgType)
      throw(TagNotDefinedForMessage&)
    {
      MsgTypeToField::const_iterator iM
        = m_messageFields.find(msgType.getValue());
      if(iM == m_messageFields.end())
        throw TagNotDefinedForMessage(field.getField());

      const MsgFields& fields = iM->second;
      MsgFields::const_iterator iF = fields.find(field.getField());
      if(iF == fields.end())
        throw TagNotDefinedForMessage(field.getField());
    }

    /// Check if a message has all required fields.
    void checkHasRequired
    (const FieldMap& fieldMap, const MsgType& msgType)
      throw(RequiredTagMissing&)
    {
      MsgTypeToField::const_iterator iM
        = m_requiredFields.find(msgType.getValue());
      if(iM == m_requiredFields.end()) return;

      const MsgFields& fields = iM->second;
      MsgFields::const_iterator iF;
      for(iF = fields.begin(); iF != fields.end(); ++iF)
        {
          if(!fieldMap.isSetField(*iF))
            throw RequiredTagMissing(*iF);
        }
    }

    MsgTypeToField m_messageFields;
    MsgTypeToField m_requiredFields;
    MsgTypes m_messages;
    Fields m_fields;
    FieldToValue m_fieldValues;
    std::string m_beginString;
    int m_lastField;
  };
}

#endif //FIX_MESSAGEVALIDATOR_H

