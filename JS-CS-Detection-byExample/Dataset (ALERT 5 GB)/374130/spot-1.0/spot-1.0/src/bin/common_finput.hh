// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#ifndef SPOT_BIN_COMMON_FINPUT_HH
#define SPOT_BIN_COMMON_FINPUT_HH

#include "common_sys.hh"

#include <argp.h>
#include <vector>
#include "ltlparse/public.hh"

struct job
{
  const char* str;
  bool file_p;	// true if str is a filename, false if it is a formula

  job(const char* str, bool file_p)
    : str(str), file_p(file_p)
  {
  }
};

typedef std::vector<job> jobs_t;
extern jobs_t jobs;
extern bool lbt_input;

extern const struct argp finput_argp;

int parse_opt_finput(int key, char* arg, struct argp_state* state);

const spot::ltl::formula*
parse_formula(const std::string& s, spot::ltl::parse_error_list& error_list);


class job_processor
{
protected:
  bool abort_run;  // Set to true in process_formula() to abort run().
public:
  job_processor();

  virtual ~job_processor()
  {
  }

  virtual int
  process_formula(const spot::ltl::formula* f,
		  const char* filename = 0, int linenum = 0) = 0;

  virtual int
  process_string(const std::string& str,
		 const char* filename = 0, int linenum = 0);
  virtual int
  process_stream(std::istream& is, const char* filename);

  virtual int
  process_file(const char* filename);

  virtual int
  run();
};



#endif // SPOT_BIN_COMMON_FINPUT_HH
