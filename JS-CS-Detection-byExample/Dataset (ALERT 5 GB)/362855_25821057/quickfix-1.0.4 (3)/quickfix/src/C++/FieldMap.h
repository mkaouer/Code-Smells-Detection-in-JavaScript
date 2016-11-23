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

#ifndef FIX_FIELDMAP
#define FIX_FIELDMAP

#ifdef _MSC_VER
#pragma warning( disable: 4786 )
#endif

#include "Field.h"
#include "MessageSorters.h"
#include "Exceptions.h"
#include <map>
#include <sstream>
#include <algorithm>

namespace FIX
{
  /*! \addtogroup user
   *  @{
   */
  /**
   * Stores and organizes a collection of Fields.
   *
   * This is the basis for a message, header, and trailer.  This collection
   * class uses a sorter to keep the fields in a particular order.
   */
  class FieldMap
  {
  public:
    typedef std::map<int, FieldBase, message_order> Fields;
    typedef Fields::const_iterator iterator;
    typedef iterator const_iterator;

    FieldMap( const message_order& order =
              message_order(message_order::normal) )
      : m_fields(order) {}

    /// Set a field without type checking
    void setField( const FieldBase& field )
    {
      m_fields.erase(field.getField());
      m_fields.insert(std::make_pair(field.getField(), field));
    }

    /// Set a field without a field class
    void setField( int field, const std::string value )
    {
      FieldBase fieldBase(field, value);
      setField(fieldBase);
    }

    void addField( const FieldBase& field )
    {
      m_fields.insert(std::make_pair(field.getField(), field));
    }

    /// Get a field without type checking
    FieldBase& getField( FieldBase& field )
      const throw( FieldNotFound& )
    {
      Fields::const_iterator iter = m_fields.find(field.getField());
      if( iter == m_fields.end() )
        throw FieldNotFound(field.getField());
      field = iter->second;
      return field;
    }

    /// Get a field without a field class
    std::string getField( int field )
      const throw(FieldNotFound&)
    {
      FieldBase fieldBase(field, "");
      getField(fieldBase);
      return fieldBase.getString();
    }

    FieldBase& getField( FieldBase& field, int pos )
      const throw( FieldNotFound& )
    {
      Fields::const_iterator iter = m_fields.find(field.getField());
      if( iter == m_fields.end() )
        throw FieldNotFound(field.getField());
      if( countField(field) < pos )
        throw FieldNotFound(field.getField());

      for( int i = 1; i < pos; ++i, ++iter ){}
      field = iter->second;
      return field;
    }

    /**
     * Check to see if a field is set
     * If a field has been set on the map at least once, this will
     * return true.
     */
    bool isSetField( const FieldBase& field ) const
    {
      return m_fields.find(field.getField()) != m_fields.end();
    }

    /**
     * Check to see if a field is set by referencing its number
     * If a field has been set on the map at least once, this will
     * return true.
     */
    bool isSetField( int field ) const
    {
      return m_fields.find(field) != m_fields.end();
    }

    int countField( const FieldBase& field ) const
    {
      return m_fields.count(field.getField());
    }

    /// Clear all fields from the map
    void clear()
    {
      m_fields.clear();
    }

    std::string calculateString() const
    {
      std::string result;
      Fields::const_iterator i;
      for(i = m_fields.begin(); i != m_fields.end(); ++i)
        result += i->second.getValue();
      return result;
    }

    int calculateLength() const
    {
      int result = 0;
      Fields::const_iterator i;
      for(i = m_fields.begin(); i != m_fields.end(); ++i)
        {
          if(i->first != FIELD::BeginString
             && i->first != FIELD::BodyLength
             && i->first != FIELD::CheckSum )
            { result += i->second.getLength(); }
        }
      return result;
    }

    int calculateTotal() const
    {
      int result = 0;
      Fields::const_iterator i;
      for(i = m_fields.begin(); i != m_fields.end(); ++i)
        {
          if(i->first != FIELD::CheckSum)
            result += i->second.getTotal();
        }
      return result;
    }

    iterator begin() const { return m_fields.begin(); }
    iterator end() const { return m_fields.end();   }
  private:
    Fields m_fields;
  };
  /*! @} */
}

#define FIELD_SET( MAP, FIELD )               \
  bool isSet( const FIELD& field ) const      \
    { return (MAP).isSetField(field); }       \
  void set( const FIELD& field )              \
    { (MAP).setField(field); }                \
  FIELD& get( FIELD& field ) const            \
    { return (FIELD&)(MAP).getField(field); }

#endif //FIX_FIELDMAP

