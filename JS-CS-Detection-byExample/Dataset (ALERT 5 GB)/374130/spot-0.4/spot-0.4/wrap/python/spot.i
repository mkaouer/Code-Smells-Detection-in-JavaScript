// Copyright (C) 2003, 2004, 2005, 2006  Laboratoire d'Informatique
// de Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

%module(directors="1") spot

%include "std_string.i"
%include "std_list.i"

%import "buddy.i"

%{
#include <iostream>
#include <fstream>
#include <sstream>
#include <signal.h>

#include "misc/version.hh"
#include "misc/bddalloc.hh"
#include "misc/minato.hh"
#include "misc/modgray.hh"
#include "misc/optionmap.hh"
#include "misc/random.hh"

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
#include "ltlvisit/reduce.hh"
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
#include "tgbaalgos/dottydec.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/rundotdec.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/stats.hh"

using namespace spot::ltl;
using namespace spot;
%}

// For spot::emptiness_check_instantiator::construct and any other
// function that return errors via a "char **err" argument.
%typemap(in, numinputs=0) char** OUTPUT (char* temp)
  "$1 = &temp;";
%typemap(argout, fragment="t_output_helper") char** OUTPUT
  "$result = t_output_helper($result, SWIG_FromCharPtr(*$1));";
%apply char** OUTPUT { char** err };

%include "misc/version.hh"
%include "misc/bddalloc.hh"
%include "misc/minato.hh"
%include "misc/optionmap.hh"
%include "misc/random.hh"

%feature("director") spot::loopless_modular_mixed_radix_gray_code;
%include "misc/modgray.hh"

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
%include "ltlvisit/reduce.hh"
%include "ltlvisit/tostring.hh"
%include "ltlvisit/tunabbrev.hh"

%feature("new") spot::ltl_to_tgba_lacim;
%feature("new") spot::ltl_to_tgba_fm;
%feature("new") spot::tgba::get_init_state;
%feature("new") spot::tgba::succ_iter;
%feature("new") spot::tgba_succ_iterator::current_state;
%feature("new") spot::tgba_dupexp_bfs;
%feature("new") spot::tgba_dupexp_dfs;
%feature("new") spot::emptiness_check::check;
%feature("new") spot::emptiness_check_result::accepting_run;
%feature("new") spot::explicit_magic_search;
%feature("new") spot::explicit_se05_search;

%feature("new") spot::emptiness_check_instantiator::construct;
%feature("new") spot::emptiness_check_instantiator::instanciate;

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
%include "tgbaalgos/dottydec.hh"
%include "tgbaalgos/dotty.hh"
%include "tgbaalgos/dupexp.hh"
%include "tgbaalgos/lbtt.hh"
%include "tgbaalgos/emptiness.hh"
%include "tgbaalgos/gtec/gtec.hh"
%include "tgbaalgos/magic.hh"
%include "tgbaalgos/neverclaim.hh"
%include "tgbaalgos/rundotdec.hh"
%include "tgbaalgos/save.hh"
%include "tgbaalgos/stats.hh"
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

int
unblock_signal(int signum)
{
  sigset_t set;
  sigemptyset(&set);
  sigaddset(&set, signum);
  return sigprocmask(SIG_UNBLOCK, &set, 0);
}

%}
