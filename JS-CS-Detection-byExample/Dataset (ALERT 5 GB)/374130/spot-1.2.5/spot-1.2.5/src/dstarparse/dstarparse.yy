/* -*- coding: utf-8 -*-
** Copyright (C) 2013 Laboratoire de Recherche et Développement de
** l'Epita (LRDE).
**
** This file is part of Spot, a model checking library.
**
** Spot is free software; you can redistribute it and/or modify it
** under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 3 of the License, or
** (at your option) any later version.
**
** Spot is distributed in the hope that it will be useful, but WITHOUT
** ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
** or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
** License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
%language "C++"
%locations
%defines
%expect 0 // No shift/reduce
%name-prefix "dstaryy"
%debug
%error-verbose
%lex-param { spot::dstar_parse_error_list& error_list }
%define api.location.type "spot::location"

%code requires
{
#include <string>
#include <cstring>
#include "ltlast/constant.hh"
#include "public.hh"

  typedef std::map<int, bdd> map_t;

  struct result_
  {
    spot::dstar_aut* d;
    spot::ltl::environment* env;
    std::vector<bdd> guards;
    std::vector<bdd>::const_iterator cur_guard;
    map_t dest_map;
    int cur_state;

    unsigned state_count;
    std::vector<std::string> aps;

    bool state_count_seen:1;
    bool accpair_count_seen:1;
    bool start_state_seen:1;
    bool aps_seen:1;

    result_() :
      state_count_seen(false),
      accpair_count_seen(false),
      start_state_seen(false),
      aps_seen(false)
    {
    }
  };
}

%parse-param {spot::dstar_parse_error_list& error_list}
%parse-param {result_& result}
%union
{
  std::string* str;
  unsigned int num;
}

%code
{
#include <sstream>
/* dstarparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"

  static void fill_guards(result_& res);
}

%token DRA "DRA"
%token DSA "DSA"
%token V2 "v2"
%token EXPLICIT "explicit"
%token ACCPAIRS "Acceptance-Pairs:"
%token AP "AP:";
%token START "Start:";
%token STATES "States:";
%token STATE "State:";
%token ACCSIG "Acc-Sig:";
%token ENDOFHEADER "---";
%token EOL "new line";
%token <str> STRING "string";
%token <num> NUMBER "number";

%type <num> sign

%destructor { delete $$; } <str>
%printer {
    if ($$)
      debug_stream() << *$$;
    else
      debug_stream() << "\"\""; } <str>
%printer { debug_stream() << $$; } <num>

%%
dstar: header ENDOFHEADER eols states

eols : EOL | eols EOL
opt_eols: | opt_eols EOL

auttype: DRA   { result.d->type = spot::Rabin; }
       | DSA   { result.d->type = spot::Streett; }


header: auttype opt_eols V2 opt_eols EXPLICIT opt_eols sizes
  {
    bool err = false;
    if (!result.state_count_seen)
      {
	error(@5, "missing state count");
	err = true;
      }
    if (!result.accpair_count_seen)
      {
	error(@5, "missing acceptance-pair count");
	err = true;
      }
    if (!result.start_state_seen)
      {
	error(@5, "missing start-state number");
	err = true;
      }
    if (!result.aps_seen)
      {
	error(@5, "missing atomic proposition definition");
	err = true;
      }
    if (err)
      {
	delete result.d->aut;
	result.d->aut = 0;
	YYABORT;
      }
    fill_guards(result);
  }

aps:
  | aps STRING opt_eols
  {
    result.aps.push_back(*$2);
    delete $2;
  }

sizes:
  | sizes error eols
  {
    error(@2, "unknown header ignored");
  }
  | sizes ACCPAIRS opt_eols NUMBER opt_eols
  {
    result.d->accpair_count = $4;
    result.accpair_count_seen = true;
  }
  | sizes STATES opt_eols NUMBER opt_eols
  {
    result.state_count = $4;
    result.state_count_seen = true;
  }
  | sizes START opt_eols NUMBER opt_eols
  {
    result.d->aut->set_init_state($4);
    result.start_state_seen = true;
  }
  | sizes AP opt_eols NUMBER opt_eols aps
  {
    int announced = $4;
    int given = result.aps.size();
    if (given != announced)
      {
	std::ostringstream str;
	str << announced << " atomic propositions announced but "
	    << given << " given";
	error(@4 + @6, str.str());
      }
    if (given > 31)
      {
	error(@4 + @6,
	      "ltl2star does not support more than 31 atomic propositions");
      }
    result.aps_seen = true;
  }

opt_name: | STRING opt_eols
  {
    delete $1;
  }

state_id: STATE opt_eols NUMBER opt_eols opt_name
  {
    if (result.cur_guard != result.guards.end())
      error(@1, "not enough transitions for previous state");
    if ($3 >= result.state_count)
      {
	std::ostringstream o;
	if (result.state_count > 0)
	  {
	    o << "state numbers should be in the range [0.."
	      << result.state_count - 1<< "]";
	  }
	else
	  {
	    o << "no states have been declared";
	  }
	error(@3, o.str());
      }
    result.cur_guard = result.guards.begin();
    result.dest_map.clear();
    result.cur_state = $3;
  }

sign: '+' { $$ = 0; }
  |   '-' { $$ = 1; }

// Membership to a pair is represented as (+NUM,-NUM)
accsigs: opt_eols
  | accsigs sign NUMBER opt_eols
  {
    if ((unsigned) result.cur_state >= result.state_count)
      break;
    assert(result.d->accsets);
    if ($3 < result.d->accpair_count)
      {
	result.d->accsets->at(result.cur_state * 2 + $2).set($3);
      }
    else
      {
	std::ostringstream o;
	if (result.d->accpair_count > 0)
	  {
	    o << "acceptance pairs should be in the range [0.."
	      << result.d->accpair_count - 1<< "]";
	  }
	else
	  {
	    o << "no acceptance pairs have been declared";
	  }
	error(@3, o.str());
      }
  }

state_accsig: ACCSIG accsigs

transitions:
  | transitions NUMBER opt_eols
  {
    std::pair<map_t::iterator, bool> i =
      result.dest_map.insert(std::make_pair($2, *result.cur_guard));
    if (!i.second)
      i.first->second |= *result.cur_guard;
    ++result.cur_guard;
  }

states:
  | states state_id state_accsig transitions
  {
    for (map_t::const_iterator i = result.dest_map.begin();
	 i != result.dest_map.end(); ++i)
      {
	spot::tgba_explicit_number::transition* t =
	  result.d->aut->create_transition(result.cur_state, i->first);
	t->condition = i->second;
      }
  }
%%

static void fill_guards(result_& r)
{
  spot::bdd_dict* d = r.d->aut->get_dict();

  size_t nap = r.aps.size();
  int* vars = new int[nap];

  // Get a BDD variable for each atomic proposition
  for (size_t i = 0; i < nap; ++i)
    {
      const spot::ltl::formula* f = r.env->require(r.aps[i]);
      vars[nap - 1 - i] = d->register_proposition(f, r.d->aut);
      f->destroy();
    }

  // build the 2^nap possible guards
  r.guards.reserve(1U << nap);
  for (size_t i = 0; i < (1U << nap); ++i)
    r.guards.push_back(bdd_ibuildcube(i, nap, vars));

  delete[] vars;
  r.cur_guard = r.guards.end();

  r.d->accsets = spot::make_bitvect_array(r.d->accpair_count,
					  2 * r.state_count);
}

void
dstaryy::parser::error(const location_type& location,
		       const std::string& message)
{
  error_list.push_back(spot::dstar_parse_error(location, message));
}

namespace spot
{
  dstar_aut*
  dstar_parse(const std::string& name,
	      dstar_parse_error_list& error_list,
	      bdd_dict* dict,
	      ltl::environment& env,
	      bool debug)
  {
    if (dstaryyopen(name))
      {
	error_list.push_back
	  (dstar_parse_error(spot::location(),
			     std::string("Cannot open file ") + name));
	return 0;
      }
    result_ r;
    r.d = new dstar_aut;
    r.d->aut = new tgba_explicit_number(dict);
    r.d->accsets = 0;
    r.env = &env;
    dstaryy::parser parser(error_list, r);
    parser.set_debug_level(debug);
    parser.parse();
    dstaryyclose();

    if (!r.d->aut || !r.d->accsets)
      {
	delete r.d;
	return 0;
      }
    return r.d;
  }
}

// Local Variables:
// mode: c++
// End:
