// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014  Laboratoire de
// Recherche et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006  Laboratoire d'Informatique
// de Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

%{
  // Workaround for SWIG 2.0.2 using ptrdiff_t but not including cstddef.
  // It matters with g++ 4.6.
#include <cstddef>
%}

%module(directors="1") spot

%include "std_string.i"
%include "std_list.i"

namespace std {
   %template(liststr) list<string>;
};

%import "buddy.i"

%{
#include <iostream>
#include <fstream>
#include <sstream>
#include <signal.h>

#include "misc/version.hh"
#include "misc/minato.hh"
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

#include "ltlvisit/dotty.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/simplify.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlvisit/lbt.hh"

#include "tgba/bdddict.hh"
#include "tgba/bddprint.hh"
#include "tgba/state.hh"
#include "tgba/succiter.hh"
#include "tgba/tgba.hh"
#include "tgba/sba.hh"
#include "tgba/statebdd.hh"
#include "tgba/taatgba.hh"
#include "tgba/tgbabddcoredata.hh"
#include "tgba/succiterconcrete.hh"
#include "tgba/tgbabddconcrete.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/tgbatba.hh"

#include "tgbaalgos/dottydec.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/degen.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgbaalgos/ltl2taa.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/compsusp.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/minimize.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/rundotdec.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/safety.hh"
#include "tgbaalgos/sccfilter.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/isdet.hh"
#include "tgbaalgos/simulation.hh"
#include "tgbaalgos/postproc.hh"

#include "tgbaparse/public.hh"

#include "ta/ta.hh"
#include "ta/tgta.hh"
#include "ta/taexplicit.hh"
#include "ta/tgtaexplicit.hh"
#include "taalgos/tgba2ta.hh"
#include "taalgos/dotty.hh"
#include "taalgos/stats.hh"
#include "taalgos/minimize.hh"

using namespace spot::ltl;
using namespace spot;
%}

// For spot::emptiness_check_instantiator::construct and any other
// function that return errors via a "char **err" argument.
%typemap(in, numinputs=0) char** OUTPUT (char* temp)
  "$1 = &temp;";
%typemap(argout) char** OUTPUT {
  PyObject *obj = SWIG_FromCharPtr(*$1);
  if (!$result) {
    $result = obj;
  //# If the function returns null_ptr (i.e. Py_None), we
  //# don't want to override it with OUTPUT as in the
  //# default implementation of t_output_helper.
  // } else if ($result == Py_None) {
  //   Py_DECREF($result);
  //   $result = obj;
  } else {
    if (!PyList_Check($result)) {
      PyObject *o2 = $result;
      $result = PyList_New(1);
      PyList_SetItem($result, 0, o2);
    }
    PyList_Append($result,obj);
    Py_DECREF(obj);
  }
 };
%apply char** OUTPUT { char** err };

// False and True cannot be redefined in Python3, even in a class.
// Spot uses these in an enum of the constant class.
%rename(FalseVal) False;
%rename(TrueVal) True;

%include "misc/version.hh"
%include "misc/minato.hh"
%include "misc/optionmap.hh"
%include "misc/random.hh"

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

%include "ltlvisit/dotty.hh"
%include "ltlvisit/dump.hh"
%include "ltlvisit/lunabbrev.hh"
%include "ltlvisit/nenoform.hh"
%include "ltlvisit/simplify.hh"
%include "ltlvisit/tostring.hh"
%include "ltlvisit/tunabbrev.hh"
%include "ltlvisit/apcollect.hh"
%include "ltlvisit/lbt.hh"

%feature("new") spot::emptiness_check::check;
%feature("new") spot::emptiness_check_instantiator::construct;
%feature("new") spot::emptiness_check_instantiator::instanciate;
%feature("new") spot::emptiness_check_result::accepting_run;
%feature("new") spot::explicit_magic_search;
%feature("new") spot::explicit_se05_search;
%feature("new") spot::ltl_to_taa;
%feature("new") spot::ltl_to_tgba_fm;
%feature("new") spot::ltl_to_tgba_lacim;
%feature("new") spot::compsusp;
%feature("new") spot::minimize_wdba;
%feature("new") spot::minimize_monitor;
%feature("new") spot::scc_filter;
%feature("new") spot::tgba_dupexp_bfs;
%feature("new") spot::tgba_dupexp_dfs;
%feature("new") spot::tgba::get_init_state;
%feature("new") spot::tgba::succ_iter;
%feature("new") spot::tgba_succ_iterator::current_state;
%feature("new") spot::simulation;
%feature("new") spot::cosimulation;
%feature("new") spot::iterated_simulations;
%feature("new") spot::degeneralize;
%feature("new") spot::tgba_parse;
%feature("new") spot::tgba_to_ta;
%feature("new") spot::tgba_to_tgta;
%feature("new") spot::postprocessor::run;

// The argument to postprocessor::run() will be deleted.
// Apparently SWIG can only disown arguments based on their
// names...
%apply SWIGTYPE *DISOWN { SWIGTYPE * input_disown };

// Help SWIG with namespace lookups.
#define ltl spot::ltl
%include "tgba/bdddict.hh"
%include "tgba/bddprint.hh"
%include "tgba/state.hh"
%include "tgba/succiter.hh"
%include "tgba/tgba.hh"
%include "tgba/sba.hh"
%include "tgba/statebdd.hh"
%include "tgba/taatgba.hh"
%include "tgba/tgbabddcoredata.hh"
%include "tgba/succiterconcrete.hh"
%include "tgba/tgbabddconcrete.hh"
%include "tgba/tgbaexplicit.hh"
%include "tgba/tgbaproduct.hh"
%include "tgba/tgbatba.hh"

%template(explicit_graph__string_tgba)
  spot::explicit_graph<state_explicit_string, tgba>;
%template(explicit_graph__number_tgba)
  spot::explicit_graph<state_explicit_number, tgba>;
%template(explicit_graph__formula_tgba)
  spot::explicit_graph<state_explicit_formula, tgba>;

%template(explicit_string_tgba)
  spot::tgba_explicit<state_explicit_string>;
%template(explicit_number_tgba)
  spot::tgba_explicit<state_explicit_number>;
%template(explicit_formula_tgba)
  spot::tgba_explicit<state_explicit_formula>;

%template(explicit_string__tgba)
  spot::explicit_conf<tgba_explicit<state_explicit_string>,
                      state_explicit_string>;
%template(explicit_number__tgba)
  spot::explicit_conf<tgba_explicit<state_explicit_number>,
		      state_explicit_number>;
%template(explicit_formula__tgba)
  spot::explicit_conf<tgba_explicit<state_explicit_formula>,
		      state_explicit_formula>;

%template(explicit_graph__string_sba)
  spot::explicit_graph<state_explicit_string, sba>;
%template(explicit_graph__number_sba)
  spot::explicit_graph<state_explicit_number, sba>;
%template(explicit_graph__formula_sba)
  spot::explicit_graph<state_explicit_formula, sba>;

%template(explicit_string_sba)
  spot::sba_explicit<state_explicit_string>;
%template(explicit_number_sba)
  spot::sba_explicit<state_explicit_number>;
%template(explicit_formula_sba)
  spot::sba_explicit<state_explicit_formula>;

%template(explicit_string__sba)
  spot::explicit_conf<sba_explicit<state_explicit_string>,
                      state_explicit_string>;
%template(explicit_number__sba)
  spot::explicit_conf<sba_explicit<state_explicit_number>,
		      state_explicit_number>;
%template(explicit_formula__sba)
  spot::explicit_conf<sba_explicit<state_explicit_formula>,
		      state_explicit_formula>;

%include "tgbaalgos/degen.hh"
%include "tgbaalgos/dottydec.hh"
%include "tgbaalgos/dotty.hh"
%include "tgbaalgos/dupexp.hh"
%include "tgbaalgos/emptiness.hh"
%include "tgbaalgos/gtec/gtec.hh"
%include "tgbaalgos/lbtt.hh"
%include "tgbaalgos/ltl2taa.hh"
%include "tgbaalgos/ltl2tgba_fm.hh"
%include "tgbaalgos/ltl2tgba_lacim.hh"
%include "tgbaalgos/compsusp.hh"
%include "tgbaalgos/magic.hh"
%include "tgbaalgos/minimize.hh"
%include "tgbaalgos/neverclaim.hh"
%include "tgbaalgos/rundotdec.hh"
%include "tgbaalgos/save.hh"
%include "tgbaalgos/safety.hh"
%include "tgbaalgos/sccfilter.hh"
%include "tgbaalgos/stats.hh"
%include "tgbaalgos/isdet.hh"
%include "tgbaalgos/simulation.hh"
%include "tgbaalgos/postproc.hh"

%include "tgbaparse/public.hh"

%include "ta/ta.hh"
%include "ta/tgta.hh"
%include "ta/taexplicit.hh"
%include "ta/tgtaexplicit.hh"
%include "taalgos/tgba2ta.hh"
%include "taalgos/dotty.hh"
%include "taalgos/stats.hh"
%include "taalgos/minimize.hh"



#undef ltl

%extend spot::ltl::formula {

  // When comparing formula, make sure Python compare our
  // pointers, not the pointers to its wrappers.

  // __cmp__ is for Python 2.0
  int __cmp__(const spot::ltl::formula* b) { return self - b; }
  // These are for Python 2.1+ or 3.x.  They more closely match
  // the logic in Spot.
  bool __lt__(const spot::ltl::formula* b)
  { spot::ltl::formula_ptr_less_than lt; return lt(self, b); }
  bool __le__(const spot::ltl::formula* b)
  { spot::ltl::formula_ptr_less_than lt; return !lt(b, self); }
  bool __eq__(const spot::ltl::formula* b) { return self == b; }
  bool __ne__(const spot::ltl::formula* b) { return self != b; }
  bool __gt__(const spot::ltl::formula* b)
  { spot::ltl::formula_ptr_less_than lt; return lt(b, self); }
  bool __ge__(const spot::ltl::formula* b)
  { spot::ltl::formula_ptr_less_than lt; return !lt(self, b); }

  size_t __hash__() { return self->hash(); }

  std::string
  __str__(void)
  {
    return spot::ltl::to_string(self);
  }

}

%nodefaultctor std::ostream;
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
     ~ostringstream();
  };
}

%feature("new") minimize_obligation_new;

%inline %{

// A variant of minimize_obligation() that always return a new object.
const spot::tgba*
minimize_obligation_new(const spot::tgba* a, const spot::ltl::formula* f)
{
  const tgba* res = spot::minimize_obligation(a, f);
  // Return 0 if the output is the same as the input, otherwise
  // it is hard for swig to know if the output is "new" or not.
  if (res == a)
    return 0;
  else
    return res;
}

void
tgba_enable_utf8(spot::tgba* a)
{
  if (spot::tgba_explicit_formula* tef =
      dynamic_cast<spot::tgba_explicit_formula*>(a))
    tef->enable_utf8();
  else if (spot::sba_explicit_formula* sef =
	   dynamic_cast<spot::sba_explicit_formula*>(a))
    sef->enable_utf8();
}

spot::ltl::parse_error_list
empty_parse_error_list()
{
  parse_error_list l;
  return l;
}

spot::tgba_parse_error_list
empty_tgba_parse_error_list()
{
  tgba_parse_error_list l;
  return l;
}

std::ostream&
get_cout()
{
  return std::cout;
}

void
nl_cout()
{
  std::cout << std::endl;
}

std::ostream&
get_cerr()
{
  return std::cerr;
}

void
nl_cerr()
{
  std::cerr << std::endl;
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

%extend spot::ltl::parse_error_list {

bool
__nonzero__()
{
  return !self->empty();
}

bool
__bool__()
{
  return !self->empty();
}

}

%extend spot::tgba_parse_error_list {

bool
__nonzero__()
{
  return !self->empty();
}

bool
__bool__()
{
  return !self->empty();
}

}
