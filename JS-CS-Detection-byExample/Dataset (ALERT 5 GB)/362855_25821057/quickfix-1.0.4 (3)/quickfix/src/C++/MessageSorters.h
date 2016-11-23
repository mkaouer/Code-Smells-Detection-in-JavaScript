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

#ifndef FIX_MESSAGESORTERS_H
#define FIX_MESSAGESORTERS_H

#include "FieldNumbers.h"
#include <functional>

namespace FIX
{
  /// Sorts fields in correct header order.
  struct header_order
  {
    static bool compare(const int& x, const int& y)
    {
      int orderedX = getOrderedPosition(x);
      int orderedY = getOrderedPosition(y);

      if( orderedX && orderedY )
        return orderedX < orderedY;
      else
        if( orderedX )
          return true;
        else
          if( orderedY )
            return false;
          else
            return x < y;
    }

    static int getOrderedPosition( const int& field )
    {
      switch( field )
        {
        case FIELD::BeginString:    return 1;
        case FIELD::BodyLength:     return 2;
        case FIELD::MsgType:        return 3;
        default:                    return 0;
        };
    }
  };

  /// Sorts fields in correct trailer order.
  struct trailer_order
  {
    static bool compare(const int x, const int y)
    {
      if( x == FIELD::CheckSum ) return false;
      else
        if( y == FIELD::CheckSum ) return true;
        else return x < y;
    }
  };

  typedef std::less<int> normal_order;

  /**
   * Sorts fields in header, normal, or trailer order.
   *
   * Used as a dynamic sorter to create Header, Trailer, and Message
   * FieldMaps while maintaining the same base type.
   */
  struct message_order
  {
  public:
    enum cmp_mode { header, trailer, normal };

    message_order( cmp_mode mode = normal ) { m_mode = mode; }

    bool operator()( const int& x, const int& y ) const
    {
      switch(m_mode)
        {
        case header:
          return header_order::compare(x,y);
        case trailer:
          return trailer_order::compare(x,y);
        case normal: default:
          return x < y;
        }
    }

  private:
    cmp_mode m_mode;
  };
}

#endif //FIX_MESSAGESORTERS_H

