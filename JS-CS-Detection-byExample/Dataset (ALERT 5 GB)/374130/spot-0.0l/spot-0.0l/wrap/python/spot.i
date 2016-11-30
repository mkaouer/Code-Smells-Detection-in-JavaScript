// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

%module spot

%include "std_string.i"
%include "std_list.i"

%import "buddy.i"

%{
#include <iostream>
#include <fstream>
#include <sstream>

#include "misc/version.hh"

#include "ltlast/formula.hh"
#include "ltlast/refformula.hh"
#include "ltlast/atomic_prop.hh"
#include "ltlast/binop.hh"
#include "ltlast/constant.hh"
#include "ltlast/multop.hh"
#include "ltlast/unop.hh"
#include "ltlast/visitor.hh"

#include "ltlenv/environment.hh"
#include "ltlenv/defaultenv.hh"

#include "ltlparse/public.hh"

#include "ltlvisit/clone.hh"
#include "ltlvisit/destroy.hh"
#include "ltlvisit/dotty.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/tunabbrev.hh"

#include "tgba/bdddict.hh"
#include "tgba/bddprint.hh"
#include "tgba/state.hh"
#include "tgba/succiter.hh"
#include "tgba/tgba.hh"
#include "tgba/statebdd.hh"
#include "tgba/tgbabddcoredata.hh"
#include "tgba/succiterconcrete.hh"
#include "tgba/tgbabddconcrete.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/tgbatba.hh"

#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/save.hh"

using namespace spot::ltl;
using namespace spot;
%}

%include "misc/version.hh"

%include "ltlast/formula.hh"
%include "ltlast/refformula.hh"
%include "ltlast/atomic_prop.hh"
%include "ltlast/binop.hh"
%include "ltlast/constant.hh"
%include "ltlast/multop.hh"
%include "ltlast/unop.hh"
%include "ltlast/visitor.hh"

%include "ltlenv/environment.hh"
%include "ltlenv/defaultenv.hh"

%include "ltlparse/public.hh"

%include "ltlvisit/clone.hh"
%include "ltlvisit/destroy.hh"
%include "ltlvisit/dotty.hh"
%include "ltlvisit/dump.hh"
%include "ltlvisit/lunabbrev.hh"
%include "ltlvisit/nenoform.hh"
%include "ltlvisit/tostring.hh"
%include "ltlvisit/tunabbrev.hh"

%feature("new") spot::ltl_to_tgba_lacim;
%feature("new") spot::ltl_to_tgba_fm;
%feature("new") spot::tgba::get_init_state;
%feature("new") spot::tgba::succ_iter;
%feature("new") spot::tgba_succ_iterator::current_state;

// Help SWIG with namespace lookups.
#define ltl spot::ltl
%include "tgba/bdddict.hh"
%include "tgba/bddprint.hh"
%include "tgba/state.hh"
%include "tgba/succiter.hh"
%include "tgba/tgba.hh"
%include "tgba/statebdd.hh"
%include "tgba/tgbabddcoredata.hh"
%include "tgba/succiterconcrete.hh"
%include "tgba/tgbabddconcrete.hh"
%include "tgba/tgbaexplicit.hh"
%include "tgba/tgbaproduct.hh"
%include "tgba/tgbatba.hh"

%include "tgbaalgos/ltl2tgba_lacim.hh"
%include "tgbaalgos/ltl2tgba_fm.hh"
%include "tgbaalgos/dotty.hh"
%include "tgbaalgos/lbtt.hh"
%include "tgbaalgos/magic.hh"
%include "tgbaalgos/save.hh"
#undef ltl

%extend spot::ltl::formula {

  // When comparing formula, make sure Python compare our
  // pointers, not the pointers to its wrappers.
  int
  __cmp__(const spot::ltl::formula* b)
  {
    return b - self;
  }

  std::string
  __str__(void)
  {
    return spot::ltl::to_string(self);
  }

}

%nodefault std::ostream;
namespace std {
  class ostream {};
  class ofstream : public ostream
  {
  public:
     ofstream(const char *fn);
     ~ofstream();
  };
  class ostringstream : public ostream
  {
  public:
     ostringstream();
     std::string str() const;
     ~ofstream();
  };
}

%inline %{

spot::ltl::parse_error_list
empty_parse_error_list()
{
  parse_error_list l;
  return l;
}

std::ostream&
get_cout()
{
  return std::cout;
}

std::ostream&
get_cerr()
{
  return std::cerr;
}

void
print_on(std::ostream& on, const std::string& what)
{
  on << what;
}

%}
