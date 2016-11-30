// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#ifndef SPOT_LTLVISIT_LBT_HH
# define SPOT_LTLVISIT_LBT_HH

#include <ltlast/formula.hh>
#include <iosfwd>
#include <string>

namespace spot
{
  namespace ltl
  {
    /// \addtogroup ltl_io
    /// @{

    /// \brief Output an LTL formula as a string in LBT's format.
    ///
    /// The formula must be an LTL formula (ELTL and PSL operators
    /// are not supported).  The M and W operator will be output
    /// as-is, because this is accepted by LBTT, however if you
    /// plan to use the output with other tools, you should probably
    /// rewrite these two operators using unabbreviate_wm().
    ///
    /// \param f The formula to translate.
    /// \param os The stream where it should be output.
    SPOT_API std::ostream&
    to_lbt_string(const formula* f, std::ostream& os);

    /// \brief Output an LTL formula as a string in LBT's format.
    ///
    /// The formula must be an LTL formula (ELTL and PSL operators
    /// are not supported).  The M and W operator will be output
    /// as-is, because this is accepted by LBTT, however if you
    /// plan to use the output with other tools, you should probably
    /// rewrite these two operators using unabbreviate_wm().
    ///
    /// \param f The formula to translate.
    SPOT_API std::string
    to_lbt_string(const formula* f);
    /// @}
  }
}

#endif // SPOT_LTLVISIT_TOSTRING_HH
