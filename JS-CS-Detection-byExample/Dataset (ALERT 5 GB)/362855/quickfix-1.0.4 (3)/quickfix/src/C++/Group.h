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

#ifndef FIX_GROUP
#define FIX_GROUP

#ifdef _MSC_VER
#pragma warning( disable: 4786 )
#endif

#include "FieldMap.h"
#include <vector>

namespace FIX
{
  class Group : public FieldMap
  {
    typedef std::map<int, std::vector<Group*> > Groups;
  public:
    Group( int field ) : m_field(field) {}
    virtual ~Group()
    {
      Groups::iterator i;
      for(i = m_groups.begin(); i != m_groups.end(); ++i)
      {
        std::vector<Group*>::iterator j;
        for(j = i->second.begin(); j != i->second.end(); ++j)
          delete *j;
      }
    }

    int field() const { return m_field; }

    void addGroup( Group& group )
    {
      Group* pGroup = new Group(group.field());
      *pGroup = group;
      m_groups[group.field()].push_back(pGroup);
    }

    Group& getGroup( unsigned num, Group& group ) throw(FieldNotFound&)
    {
      Groups::iterator i = m_groups.find(num);
      if(i == m_groups.end()) throw FieldNotFound(group.field());
      if(i->second.size() < num) throw FieldNotFound(group.field());
      group = *(*(i->second.begin() + (num - 1)));
      return group;
    }

  private:
    int m_field;
    Groups m_groups;
  };
}

#endif //FIX_GROUP
