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
#include "stdafx.h"
#else
#include "config.h"
#endif
#include "CallStack.h"

#include "Message.h"
#include "Utility.h"
#include <iomanip>

namespace FIX
{
std::auto_ptr<DataDictionary> Message::s_dataDictionary;

Message::Message()
: m_header( message_order( message_order::header ) ),
  m_trailer( message_order( message_order::trailer ) ),
  m_validStructure( true ) {}

Message::Message( const std::string& string, bool validate )
throw( InvalidMessage& )
: m_header( message_order( message_order::header ) ),
  m_trailer( message_order( message_order::trailer ) ),
  m_validStructure( true )
{
  setString( string, validate );
}

Message::Message( const std::string& string,
                  const DataDictionary& dataDictionary )
throw( InvalidMessage& )
: m_header( message_order( message_order::header ) ),
  m_trailer( message_order( message_order::trailer ) ),
  m_validStructure( true )
{
  setString( string, true, &dataDictionary );
}

bool Message::InitializeXML( const std::string& url )
{ QF_STACK_PUSH(Message::InitializeXML)

  try
  {
    std::auto_ptr<DataDictionary> p =
      std::auto_ptr<DataDictionary>(new DataDictionary(url));
    s_dataDictionary = p;
    return true;
  }
  catch( ConfigError& )
  { return false; }

  QF_STACK_POP
}
	
void Message::reverseRoute( const Header& header )
{ QF_STACK_PUSH(Message::reverseRoute)

  // required routing tags
  BeginString beginString;
  SenderCompID senderCompID;
  TargetCompID targetCompID;

  if( header.isSetField( beginString ) )
  {
    header.getField( beginString );
    m_header.setField( beginString );
  }

  if( header.isSetField( senderCompID ) )
  {
    header.getField( senderCompID );
    m_header.setField( TargetCompID( senderCompID ) );
  }

  if( header.isSetField( targetCompID ) )
  {
    header.getField( targetCompID );
    m_header.setField( SenderCompID( targetCompID ) );
  }

  // optional routing tags
  OnBehalfOfCompID onBehalfOfCompID;
  OnBehalfOfSubID onBehalfOfSubID;
  DeliverToCompID deliverToCompID;
  DeliverToSubID deliverToSubID;

  if( header.isSetField( onBehalfOfCompID ) )
  {
    header.getField( onBehalfOfCompID );
    m_header.setField( DeliverToCompID( onBehalfOfCompID ) );
  }

  if( header.isSetField( onBehalfOfSubID ) )
  {
    header.getField( onBehalfOfSubID );
    m_header.setField( DeliverToSubID( onBehalfOfSubID ) );
  }
	
  if( header.isSetField( deliverToCompID ) )
  {
    header.getField( deliverToCompID );
    m_header.setField( OnBehalfOfCompID( deliverToCompID ) );
  }

  if( header.isSetField( deliverToSubID ) )
  {
    header.getField( deliverToSubID );
    m_header.setField( OnBehalfOfSubID( deliverToSubID ) );
  }

  QF_STACK_POP
}



std::string Message::toString() const
{ QF_STACK_PUSH(Message::toString)

  std::string str;
  return toString( str );

  QF_STACK_POP
}

std::string& Message::toString( std::string& str ) const
{ QF_STACK_PUSH(Message::toString)

  m_header.setField( BodyLength( bodyLength() ) );
  m_trailer.setField( CheckSum( checkSum() ) );

  return str =
    m_header.calculateString() +
    FieldMap::calculateString() +
    m_trailer.calculateString();

  QF_STACK_POP
}

std::string Message::toXML() const
{ QF_STACK_PUSH(Message::toXML)

  std::string str;
  return toXML( str );

  QF_STACK_POP
}

std::string& Message::toXML( std::string& str ) const
{ QF_STACK_PUSH(Message::toXML)

  std::stringstream stream;
  stream << "<message>"                         << std::endl
         << std::setw(2) << " " << "<header>"   << std::endl
         << toXMLFields(getHeader(), 4)
         << std::setw(2) << " " << "</header>"  << std::endl
         << std::setw(2) << " " << "<body>"     << std::endl
         << toXMLFields(*this, 4)
         << std::setw(2) << " " << "</body>"    << std::endl
         << std::setw(2) << " " << "<trailer>"  << std::endl
         << toXMLFields(getTrailer(), 4)
         << std::setw(2) << " " << "</trailer>" << std::endl
         << "</message>";

  return str = stream.str();

  QF_STACK_POP
}

std::string Message::toXMLFields(const FieldMap& fields, int space) const
{ QF_STACK_PUSH(Message::toXMLFields)

  std::stringstream stream;
  FieldMap::iterator i;
  std::string name;
  for(i = fields.begin(); i != fields.end(); ++i)
  {
    int field = i->first;
    std::string value = i->second.getString();

    stream << std::setw(space) << " " << "<field ";
    if(s_dataDictionary.get() && s_dataDictionary->getFieldName(field, name))
    {
      stream << "name=\"" << name << "\" ";
    }
    stream << "number=\"" << field << "\" value=\"" << value << "\"";
    if(s_dataDictionary.get()
       && s_dataDictionary->getValueName(field, value, name))
    {
      stream << " enum=\"" << name << "\"";
    }
    stream << "/>" << std::endl;
  }

  FieldMap::g_iterator j;
  for(j = fields.g_begin(); j != fields.g_end(); ++j)
  {
    std::vector<FieldMap*>::const_iterator k;
    for(k = j->second.begin(); k != j->second.end(); ++k)
    {
      stream << std::setw(space) << " " << "<group>" << std::endl
             << toXMLFields(*(*k), space+2)
             << std::setw(space) << " " << "</group>" << std::endl;
    }
  }

  return stream.str();

  QF_STACK_POP
}

void Message::setString( const std::string& string,
                         bool doValidation,
                         const DataDictionary* pDataDictionary )
throw( InvalidMessage& )
{ QF_STACK_PUSH(Message::setString)

  clear();

  std::string::size_type pos = 0;
  int count = 0;
  std::string msg;

  static FIELD::Field const headerOrder[] =
  {
    FIELD::BeginString,
    FIELD::BodyLength,
    FIELD::MsgType
  };

  field_type type = header;

  while ( pos < string.size() )
  {
    FieldBase field = extractField( string, pos, pDataDictionary );
    if ( count < 3 && headerOrder[ count++ ] != field.getField() )
      if ( doValidation ) throw InvalidMessage();

    if ( isHeaderField( field, pDataDictionary ) )
    {
      if ( type != header )
      {
        if(m_field == 0) m_field = field.getField();
        m_validStructure = false;
      }
      if ( field.getField() == FIELD::MsgType )
        msg = field.getString();
      m_header.setField( field, false );
    }
    else if ( isTrailerField( field, pDataDictionary ) )
    {
      type = trailer;
      m_trailer.setField( field, false );
    }
    else
    {
      if ( type == trailer )
      {
        if(m_field == 0) m_field = field.getField();
        m_validStructure = false;
      }
      type = body;
      setField( field, false );
      if ( pDataDictionary )
      {
        setGroup( msg, field, string, pos, *this, *pDataDictionary );
      }
    }
  }

  if ( doValidation ) 
    validate();

  QF_STACK_POP
}

void Message::setGroup( const std::string& msg, const FieldBase& field,
                        const std::string& string,
                        std::string::size_type& pos, FieldMap& map,
                        const DataDictionary& dataDictionary )
{ QF_STACK_PUSH(Message::setGroup)

  int group = field.getField();
  int delim;
  const DataDictionary* pDD = 0;
  if ( !dataDictionary.getGroup( msg, group, delim, pDD ) ) return ;
  Group* pGroup = 0;

  while ( pos < string.size() )
  {
    std::string::size_type oldPos = pos;
    FieldBase field = extractField( string, pos, &dataDictionary, pGroup );
    if ( (field.getField() == delim) 
        || (pGroup == 0 && pDD->isField(field.getField())) )
    {
      if ( pGroup ) 
      { 
        map.addGroup( group, *pGroup, false ); 
        delete pGroup; pGroup = 0;
      }
      pGroup = new Group( field.getField(), delim );
    }
    else if ( !pDD->isField( field.getField() ) )
    {
      if ( pGroup )
      { 
        map.addGroup( group, *pGroup, false ); 
        delete pGroup; pGroup = 0;
      }
      pos = oldPos;
      return ;
    }

    if ( !pGroup ) return ;
    pGroup->setField( field, false );
    setGroup( msg, field, string, pos, *pGroup, *pDD );
  }

  QF_STACK_POP
}

bool Message::setStringHeader( const std::string& string )
{ QF_STACK_PUSH(Message::setStringHeader)

  clear();

  std::string::size_type pos = 0;
  int count = 0;

  while ( pos < string.size() )
  {
    FieldBase field = extractField( string, pos );
    if ( count < 3 && headerOrder[ count++ ] != field.getField() )
      return false;

    if ( isHeaderField( field ) )
      m_header.setField( field, false );
    else break;
  }
  return true;

  QF_STACK_POP
}

bool Message::isHeaderField( int field )
{ QF_STACK_PUSH(Message::isHeaderField)

  switch ( field )
  {
    case FIELD::BeginString:
    case FIELD::BodyLength:
    case FIELD::MsgType:
    case FIELD::SenderCompID:
    case FIELD::TargetCompID:
    case FIELD::OnBehalfOfCompID:
    case FIELD::DeliverToCompID:
    case FIELD::SecureDataLen:
    case FIELD::MsgSeqNum:
    case FIELD::SenderSubID:
    case FIELD::SenderLocationID:
    case FIELD::TargetSubID:
    case FIELD::TargetLocationID:
    case FIELD::OnBehalfOfSubID:
    case FIELD::OnBehalfOfLocationID:
    case FIELD::DeliverToSubID:
    case FIELD::DeliverToLocationID:
    case FIELD::PossDupFlag:
    case FIELD::PossResend:
    case FIELD::SendingTime:
    case FIELD::OrigSendingTime:
    case FIELD::XmlDataLen:
    case FIELD::XmlData:
    case FIELD::MessageEncoding:
    case FIELD::LastMsgSeqNumProcessed:
    case FIELD::OnBehalfOfSendingTime:
    return true;
    default:
    return false;
  };

  QF_STACK_POP
}

bool Message::isHeaderField( const FieldBase& field,
                             const DataDictionary* pD )
{ QF_STACK_PUSH(Message::isHeaderField)

  if ( isHeaderField( field.getField() ) ) return true;
  if ( pD ) return pD->isHeaderField( field.getField() );
  return false;

  QF_STACK_POP
}

bool Message::isTrailerField( int field )
{ QF_STACK_PUSH(Message::isTrailerField)

  switch ( field )
  {
    case FIELD::SignatureLength:
    case FIELD::Signature:
    case FIELD::CheckSum:
    return true;
    default:
    return false;
  };

  QF_STACK_POP
}

bool Message::isTrailerField( const FieldBase& field,
                              const DataDictionary* pD )
{ QF_STACK_PUSH(Message::isTrailerField)

  if ( isTrailerField( field.getField() ) ) return true;
  if ( pD ) return pD->isTrailerField( field.getField() );
  return false;

  QF_STACK_POP
}

SessionID Message::getSessionID() throw( FieldNotFound& )
{ QF_STACK_PUSH(Message::getSessionID)

  BeginString beginString;
  SenderCompID senderCompID;
  TargetCompID targetCompID;

  getHeader().getField( beginString );
  getHeader().getField( senderCompID );
  getHeader().getField( targetCompID );

  return SessionID( beginString, senderCompID, targetCompID );

  QF_STACK_POP
}

void Message::setSessionID( const SessionID& sessionID )
{ QF_STACK_PUSH(Message::setSessionID)

  getHeader().setField( sessionID.getBeginString() );
  getHeader().setField( sessionID.getSenderCompID() );
  getHeader().setField( sessionID.getTargetCompID() );

  QF_STACK_POP
}

void Message::clear()
{ QF_STACK_PUSH(Message::clear)

  m_field = 0;
  m_header.clear();
  FieldMap::clear();
  m_trailer.clear();

  QF_STACK_POP
}

void Message::validate()
{ QF_STACK_PUSH(Message::validate)

  try
  {
    BodyLength aBodyLength;
    m_header.getField( aBodyLength );
    if ( aBodyLength != bodyLength() )
    {
      std::stringstream text;
      text << "Expected BodyLength=" << bodyLength()
           << ", Recieved BodyLength=" << (int)aBodyLength;
      throw InvalidMessage(text.str());
    }	

    CheckSum aCheckSum;
    m_trailer.getField( aCheckSum );
    if ( aCheckSum != checkSum() )
    {
      std::stringstream text;
      text << "Expected CheckSum=" << checkSum()
           << ", Recieved CheckSum=" << (int)aCheckSum;
      throw InvalidMessage(text.str());
    }
  }
  catch ( FieldNotFound& ) 
  {
    throw InvalidMessage("BodyLength or CheckSum missing");
  }

  QF_STACK_POP
}

FieldBase Message::extractField
( const std::string& string, std::string::size_type& pos,
  const DataDictionary* pDD, const Group* pGroup )
{ QF_STACK_PUSH(Message::extractField)

  std::string::size_type equalSign = string.find_first_of( '=', pos );
  int field = atol(string.substr( pos, equalSign - pos ).c_str());

  std::string::size_type soh =
    string.find_first_of( '\001', equalSign + 1 );
  if ( soh == std::string::npos )
    throw InvalidMessage();

  if ( pDD && pDD->isDataField(field) )
  {
    std::string fieldLength;
    /* Assume length field is 1 less. */
    int lenField = field - 1;     
    /* Special case for Signature which violates above assumption. */
    if ( field == 89 ) lenField = 93;

    if ( pGroup && pGroup->isSetField( lenField ) )
    {
      fieldLength = pGroup->getField( lenField);
      soh = equalSign + 1 + atol( fieldLength.c_str() );
    }
    else if ( isSetField( lenField ) )
    {
      fieldLength = getField( lenField );
      soh = equalSign + 1 + atol( fieldLength.c_str() );
    }
  }

  pos = soh + 1;
  return FieldBase ( 
    field, 
    string.substr( equalSign + 1, soh - ( equalSign + 1 ) ) );

  QF_STACK_POP
}
}
