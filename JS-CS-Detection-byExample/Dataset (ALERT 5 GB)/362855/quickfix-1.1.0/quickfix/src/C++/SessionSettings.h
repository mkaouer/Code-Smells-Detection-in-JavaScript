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

#ifndef FIX_SESSIONSETTINGS_H
#define FIX_SESSIONSETTINGS_H

#include "Dictionary.h"
#include "SessionID.h"
#include "Exceptions.h"
#include <map>
#include <set>

namespace FIX
{
  /*! \addtogroup user
   *  @{
   */
  /// Container for setting dictionaries mapped to sessions.
  class SessionSettings
  {
  public:
    SessionSettings() {}
    SessionSettings(std::istream& stream) throw(ConfigError&);
    SessionSettings(const std::string& file) throw(ConfigError&);

    /// Get a dictionary for a session.
    Dictionary get(const SessionID&) const throw(ConfigError&);
    /// Set a dictionary for a session
    void set(const SessionID&, const Dictionary&);

    /// Get global default settings
    Dictionary get() const { return m_defaults; }
    /// Set global default settings
    void set(const Dictionary& defaults) { m_defaults = defaults; }

    /// Number of session settings
    int size() { return m_settings.size(); }

    typedef std::map<SessionID, Dictionary> Dictionaries;
    std::set<SessionID> getSessions() const
    {
      std::set<SessionID> result;
      Dictionaries::const_iterator i;
      for(i = m_settings.begin(); i != m_settings.end(); ++i)
        result.insert(i->first);
      return result;
    }

  private:
    Dictionaries m_settings;
    Dictionary m_defaults;
  };
  /*! @} */

  std::istream& operator>>(std::istream&, SessionSettings&)
    throw(ConfigError&);
}

#endif //FIX_SESSIONSETTINGS_H
