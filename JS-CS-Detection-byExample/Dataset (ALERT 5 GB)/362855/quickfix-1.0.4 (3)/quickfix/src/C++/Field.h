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

#ifndef FIX_FIELD
#define FIX_FIELD

#ifdef _MSC_VER
#pragma warning( disable : 4786 )
#endif

#include <sstream>
#include "FieldNumbers.h"
#include "FieldConvertors.h"
#include "FieldTypes.h"

namespace FIX
{
  /*! \addtogroup user
   *  @{
   */
  /**
   * Base representation of all Field classes.
   *
   * This base class is the lowest common denominator of all fields.  It
   * keeps all fields in its most generic string representation with its
   * integer tag.
   */
  class FieldBase
  {
    friend class Message;
  public:
    FieldBase( int field, const std::string& string )
      : m_field(field), m_length(0), m_total(0)
    {
      std::stringstream stream;
      stream << field << "=" << string << '\001';

      m_string = string;
      m_data = stream.str();
      m_length = m_data.length();
      std::string::const_iterator iter = m_data.begin();
      std::string::const_iterator end = m_data.end();
      while( iter != end )
        {
          m_total += *iter;
          ++iter;
        }
    }

    /// Get the fields integer tag.
    int getField() const
    { return m_field;   }

    /// Get the string representation of the fields value.
    const std::string& getString() const
    { return m_string;  }

    /// Get the string representation of the Field (i.e.) 55=MSFT<SOH>
    const std::string& getValue() const
    { return m_data;    }

    /// Get the length of the fields string representation
    int getLength() const
    { return m_length;  }

    /// Get the total value the fields characters added together
    int getTotal() const
    { return m_total;   }

    /// Compares fields based on thier tag numbers
    bool operator < (const FieldBase& field) const
    { return m_field < field.m_field; }

  private:
    int m_field;
    std::string m_data;
    std::string m_string;
    int m_length;
    int m_total;
  };
  /*! @} */

  inline std::ostream& operator <<
  (std::ostream& stream, const FieldBase& field)
  {
    stream << field.getString();
    return stream;
  }

  /**
   * MSC doesn't support partial template specialization so we have this.
   * this is here to provide equality checking against native char arrays.
   */
  class StringField : public FieldBase
  {
  public:
    explicit StringField( int field, const std::string& data )
      : FieldBase( field, data ) {}
    StringField( int field )
      : FieldBase( field, "" ) {}

    const std::string getValue() const
    { return getString(); }
    operator const std::string() const
    { return getString(); }

    bool operator<( const StringField& rhs ) const
    { return getString() < rhs.getString(); }
    bool operator==( const StringField& rhs ) const
    { return getString() == rhs.getString(); }
    bool operator!=( const StringField& rhs ) const
    { return getString() != rhs.getString(); }
    friend bool operator==( const StringField&, const char* );
    friend bool operator==( const char*, const StringField& );
    friend bool operator!=( const StringField&, const char* );
    friend bool operator!=( const char*, const StringField& );
  };

  inline bool operator==( const StringField& lhs, const char* rhs )
  { return lhs.getValue() == rhs; }
  inline bool operator==( const char* lhs, const StringField& rhs )
  { return lhs == rhs.getValue(); }
  inline bool operator!=( const StringField& lhs, const char* rhs )
  { return lhs.getValue() != rhs; }
  inline bool operator!=( const char* lhs, const StringField& rhs )
  { return lhs != rhs.getValue(); }

  class CharField : public FieldBase
  {
  public:
    explicit CharField( int field, char data )
      : FieldBase( field, CharConvertor::convert(data) ) {}
    CharField( int field )
      : FieldBase( field, "" ) {}

    const char getValue() const
    { return CharConvertor::convert(getString()); }
    operator const char() const
    { return getValue(); }
  };

  class DoubleField : public FieldBase
  {
  public:
    explicit DoubleField( int field, double data )
      : FieldBase( field, DoubleConvertor::convert(data) ) {}
    DoubleField( int field )
      : FieldBase( field, "" ) {}

    const double getValue() const
    { return DoubleConvertor::convert(getString()); }
    operator const double() const
    { return getValue(); }
  };

  class IntField : public FieldBase
  {
  public:
    explicit IntField( int field, int data )
      : FieldBase( field, IntConvertor::convert(data) ) {}
    IntField( int field )
      : FieldBase( field, "" ) {}

    const int getValue() const
    { return IntConvertor::convert(getString()); }
    operator const int() const
    { return getValue(); }
  };

  class BoolField : public FieldBase
  {
  public:
    explicit BoolField( int field, bool data )
      : FieldBase( field, BoolConvertor::convert(data) ) {}
    BoolField( int field )
      : FieldBase( field, "" ) {}

    const bool getValue() const
    { return BoolConvertor::convert(getString()); }
    operator const bool() const
    { return getValue(); }
  };

  class UtcTimeStampField : public FieldBase
  {
  public:
    explicit UtcTimeStampField( int field, const UtcTimeStamp& data )
      : FieldBase( field, UtcTimeStampConvertor::convert(data) ) {}
    UtcTimeStampField( int field )
      : FieldBase( field, UtcTimeStampConvertor::convert(UtcTimeStamp()) ) {}

    const UtcTimeStamp getValue() const
    { return UtcTimeStampConvertor::convert(getString()); }
    operator const UtcTimeStamp() const
    { return getValue(); }

    bool operator<( const UtcTimeStampField& rhs ) const
    { return getValue() < rhs.getValue(); }
    bool operator==( const UtcTimeStampField& rhs ) const
    { return getValue() == rhs.getValue(); }
    bool operator!=( const UtcTimeStampField& rhs ) const
    { return getValue() != rhs.getValue(); }
  };

  class UtcDateField : public FieldBase
  {
  public:
    explicit UtcDateField( int field, const UtcDate& data )
      : FieldBase( field, UtcDateConvertor::convert(data) ) {}
    UtcDateField( int field )
      : FieldBase( field, UtcDateConvertor::convert(UtcDate()) ) {}

    const UtcDate getValue() const
    { return UtcDateConvertor::convert(getString()); }
    operator const UtcDate() const
    { return getValue(); }

    bool operator<( const UtcDateField& rhs ) const
    { return getValue() < rhs.getValue(); }
    bool operator==( const UtcDateField& rhs ) const
    { return getValue() == rhs.getValue(); }
    bool operator!=( const UtcDateField& rhs ) const
    { return getValue() != rhs.getValue(); }
  };

  class UtcTimeOnlyField : public FieldBase
  {
  public:
    explicit UtcTimeOnlyField( int field, const UtcTimeOnly& data )
      : FieldBase( field, UtcTimeOnlyConvertor::convert(data) ) {}
    UtcTimeOnlyField( int field )
      : FieldBase( field, UtcTimeOnlyConvertor::convert(UtcTimeOnly()) ) {}

    const UtcTimeOnly getValue() const
    { return UtcTimeOnlyConvertor::convert(getString()); }
    operator const UtcTimeOnly() const
    { return getValue(); }

    bool operator<( const UtcTimeOnlyField& rhs ) const
    { return getValue() < rhs.getValue(); }
    bool operator==( const UtcTimeOnlyField& rhs ) const
    { return getValue() == rhs.getValue(); }
    bool operator!=( const UtcTimeOnlyField& rhs ) const
    { return getValue() != rhs.getValue(); }
  };

  class CheckSumField : public FieldBase
  {
  public:
    explicit CheckSumField( int field, int data )
      : FieldBase( field, CheckSumConvertor::convert(data) ) {}
    CheckSumField( int field )
      : FieldBase( field, "" ) {}

    const int getValue() const
    { return CheckSumConvertor::convert(getString()); }
    operator const int() const
    { return getValue(); }
  };

  typedef DoubleField PriceField;
  typedef DoubleField AmtField;
  typedef DoubleField QtyField;
  typedef CharField   CurrencyField;
  typedef StringField MultipleValueStringField;
  typedef StringField ExchangeField;
  typedef StringField LocalMktDateField;
  typedef StringField DataField;
  typedef DoubleField FloatField;
  typedef DoubleField PriceOffsetField;
  typedef StringField MonthYearField;
  typedef StringField DayOfMonthField;
}

#define DEFINE_CHECKSUM( NAME ) \
  class NAME : public CheckSumField { public: \
    NAME() : CheckSumField(FIELD::NAME) {} \
    NAME(INT value) : CheckSumField(FIELD::NAME, value) {}};
#define DEFINE_STRING( NAME ) \
  class NAME : public StringField { public: \
    NAME() : StringField(FIELD::NAME) {} \
    NAME(STRING value) : StringField(FIELD::NAME, value) {}};
#define DEFINE_CHAR( NAME ) \
  class NAME : public CharField { public: \
    NAME() : CharField(FIELD::NAME) {} \
    NAME(CHAR value) : CharField(FIELD::NAME, value) {}};
#define DEFINE_PRICE( NAME ) \
  class NAME : public PriceField { public: \
    NAME() : PriceField(FIELD::NAME) {} \
    NAME(PRICE value) : PriceField(FIELD::NAME, value) {}};
#define DEFINE_INT( NAME ) \
  class NAME : public IntField { public: \
    NAME() : IntField(FIELD::NAME) {} \
    NAME(INT value) : IntField(FIELD::NAME, value) {}};
#define DEFINE_AMT( NAME ) \
  class NAME : public AmtField { public: \
    NAME() : AmtField(FIELD::NAME) {} \
    NAME(AMT value) : AmtField(FIELD::NAME, value) {}};
#define DEFINE_QTY( NAME ) \
  class NAME : public QtyField { public: \
    NAME() : QtyField(FIELD::NAME) {} \
    NAME(QTY value) : QtyField(FIELD::NAME, value) {}};
#define DEFINE_CURRENCY( NAME ) \
  class NAME : public CurrencyField { public: \
    NAME() : CurrencyField(FIELD::NAME) {} \
    NAME(CURRENCY value) : CurrencyField(FIELD::NAME, value) {}};
#define DEFINE_MULTIPLEVALUESTRING( NAME ) \
  class NAME : public MultipleValueStringField { public: \
    NAME() : MultipleValueStringField(FIELD::NAME) {} \
    NAME(MULTIPLEVALUESTRING value) : MultipleValueStringField(FIELD::NAME, value) {}};
#define DEFINE_EXCHANGE( NAME ) \
  class NAME : public ExchangeField { public: \
    NAME() : ExchangeField(FIELD::NAME) {} \
    NAME(EXCHANGE value) : ExchangeField(FIELD::NAME, value) {}};
#define DEFINE_UTCTIMESTAMP( NAME ) \
  class NAME : public UtcTimeStampField { public: \
    NAME() : UtcTimeStampField(FIELD::NAME) {} \
    NAME(UTCTIMESTAMP value) : UtcTimeStampField(FIELD::NAME, value) {}};
#define DEFINE_BOOLEAN( NAME ) \
  class NAME : public BoolField { public: \
    NAME() : BoolField(FIELD::NAME) {} \
    NAME(BOOLEAN value) : BoolField(FIELD::NAME, value) {}};
#define DEFINE_LOCALMKTDATE( NAME ) \
  class NAME : public LocalMktDateField { public: \
    NAME() : LocalMktDateField(FIELD::NAME) {} \
    NAME(LOCALMKTDATE value) : LocalMktDateField(FIELD::NAME, value) {}};
#define DEFINE_DATA( NAME ) \
  class NAME : public DataField { public: \
    NAME() : DataField(FIELD::NAME) {} \
    NAME(DATA value) : DataField(FIELD::NAME, value) {}};
#define DEFINE_FLOAT( NAME ) \
  class NAME : public FloatField { public: \
    NAME() : FloatField(FIELD::NAME) {} \
    NAME(FLOAT value) : FloatField(FIELD::NAME, value) {}};
#define DEFINE_PRICEOFFSET( NAME ) \
  class NAME : public PriceOffsetField { public: \
    NAME() : PriceOffsetField(FIELD::NAME) {} \
    NAME(PRICEOFFSET value) : PriceOffsetField(FIELD::NAME, value) {}};
#define DEFINE_MONTHYEAR( NAME ) \
  class NAME : public MonthYearField { public: \
    NAME() : MonthYearField(FIELD::NAME) {} \
    NAME(MONTHYEAR value) : MonthYearField(FIELD::NAME, value) {}};
#define DEFINE_DAYOFMONTH( NAME ) \
  class NAME : public DayOfMonthField { public: \
    NAME() : DayOfMonthField(FIELD::NAME) {} \
    NAME(DAYOFMONTH value) : DayOfMonthField(FIELD::NAME, value) {}};
#define DEFINE_UTCDATE( NAME ) \
  class NAME : public UtcDateField { public: \
    NAME() : UtcDateField(FIELD::NAME) {} \
    NAME(UTCDATE value) : UtcDateField(FIELD::NAME, value) {}};
#define DEFINE_UTCTIMEONLY( NAME ) \
  class NAME : public UtcTimeOnlyField { public: \
    NAME() : UtcTimeOnlyField(FIELD::NAME) {} \
    NAME(UTCTIMEONLY value) : UtcTimeOnlyField(FIELD::NAME, value) {}};

#define USER_DEFINE_STRING( NAME, NUM ) \
  class NAME : public StringField { public: \
    NAME() : StringField(NUM) {} \
    NAME(STRING value) : StringField(NUM, value) {}};
#define USER_DEFINE_CHAR( NAME, NUM ) \
  class NAME : public CharField { public: \
    NAME() : CharField(NUM) {} \
    NAME(CHAR value) : CharField(NUM, value) {}};
#define USER_DEFINE_PRICE( NAME, NUM ) \
  class NAME : public PriceField { public: \
    NAME() : PriceField(NUM) {} \
    NAME(PRICE value) : PriceField(NUM, value) {}};
#define USER_DEFINE_INT( NAME, NUM ) \
  class NAME : public IntField { public: \
    NAME() : IntField(NUM) {} \
    NAME(INT value) : IntField(NUM, value) {}};
#define USER_DEFINE_AMT( NAME, NUM ) \
  class NAME : public AmtField { public: \
    NAME() : AmtField(NUM) {} \
    NAME(AMT value) : AmtField(NUM, value) {}};
#define USER_DEFINE_QTY( NAME, NUM ) \
  class NAME : public QtyField { public: \
    NAME() : QtyField(NUM) {} \
    NAME(QTY value) : QtyField(NUM, value) {}};
#define USER_DEFINE_CURRENCY( NAME, NUM ) \
  class NAME : public CurrencyField { public: \
    NAME() : CurrencyField(NUM) {} \
    NAME(CURRENCY value) : CurrencyField(NUM, value) {}};
#define USER_DEFINE_MULTIPLEVALUESTRING( NAME, NUM ) \
  class NAME : public MultipleValueStringField { public: \
    NAME() : MultipleValueStringField(NUM) {} \
    NAME(MULTIPLEVALUESTRING value) : MultipleValueStringField(NUM, value) {}};
#define USER_DEFINE_EXCHANGE( NAME, NUM ) \
  class NAME : public ExchangeField { public: \
    NAME() : ExchangeField(NUM) {} \
    NAME(EXCHANGE value) : ExchangeField(NUM, value) {}};
#define USER_DEFINE_UTCTIMESTAMP( NAME, NUM ) \
  class NAME : public UtcTimeStampField { public: \
    NAME() : UtcTimeStampField(NUM) {} \
    NAME(UTCTIMESTAMP value) : UtcTimeStampField(NUM, value) {}};
#define USER_DEFINE_BOOLEAN( NAME, NUM ) \
  class NAME : public BoolField { public: \
    NAME() : BoolField(NUM) {} \
    NAME(BOOLEAN value) : BoolField(NUM, value) {}};
#define USER_DEFINE_LOCALMKTDATE( NAME, NUM ) \
  class NAME : public LocalMktDateField { public: \
    NAME() : LocalMktDateField(NUM) {} \
    NAME(LOCALMKTDATE value) : LocalMktDateField(NUM, value) {}};
#define USER_DEFINE_DATA( NAME, NUM ) \
  class NAME : public DataField { public: \
    NAME() : DataField(NUM) {} \
    NAME(DATA value) : DataField(NUM, value) {}};
#define USER_DEFINE_FLOAT( NAME, NUM ) \
  class NAME : public FloatField { public: \
    NAME() : FloatField(NUM) {} \
    NAME(FLOAT value) : FloatField(NUM, value) {}};
#define USER_DEFINE_PRICEOFFSET( NAME, NUM ) \
  class NAME : public PriceOffsetField { public: \
    NAME() : PriceOffsetField(NUM) {} \
    NAME(PRICEOFFSET value) : PriceOffsetField(NUM, value) {}};
#define USER_DEFINE_MONTHYEAR( NAME, NUM ) \
  class NAME : public MonthYearField { public: \
    NAME() : MonthYearField(NUM) {} \
    NAME(MONTHYEAR value) : MonthYearField(NUM, value) {}};
#define USER_DEFINE_DAYOFMONTH( NAME, NUM ) \
  class NAME : public DayOfMonthField { public: \
    NAME() : DayOfMonthField(NUM) {} \
    NAME(DAYOFMONTH value) : DayOfMonthField(NUM, value) {}};
#define USER_DEFINE_UTCDATE( NAME, NUM ) \
  class NAME : public UtcDateField { public: \
    NAME() : UtcDateField(NUM) {} \
    NAME(UTCDATE value) : UtcDateField(NUM, value) {}};
#define USER_DEFINE_UTCTIMEONLY( NAME, NUM ) \
  class NAME : public UtcTimeOnlyField { public: \
    NAME() : UtcTimeOnlyField(NUM) {} \
    NAME(UTCTIMEONLY value) : UtcTimeOnlyField(NUM, value) {}};

#endif

