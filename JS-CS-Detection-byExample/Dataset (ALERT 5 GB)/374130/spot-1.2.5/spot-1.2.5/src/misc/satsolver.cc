// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
// de l'Epita.
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

#include "config.h"
#include "formater.hh"
#include <cstdlib>
#include <sstream>
#include <stdexcept>
#include "satsolver.hh"
#include <fstream>
#include <limits>
#include <sys/wait.h>

namespace spot
{
  namespace
  {
    struct satsolver_command: formater
    {
      const char* satsolver;

      satsolver_command()
      {
	satsolver = getenv("SPOT_SATSOLVER");
	if (!satsolver)
	  {
	    satsolver = "glucose -verb=0 -model %I >%O";
	    return;
	  }
	prime(satsolver);
	if (!has('I'))
	  throw std::runtime_error("SPOT_SATSOLVER should contain %I to "
				   "indicate how to use the input filename.");
	if (!has('O'))
	  throw std::runtime_error("SPOT_SATSOLVER should contain %O to "
				   "indicate how to use the output filename.");
      }

      int
      run(printable* in, printable* out)
      {
	declare('I', in);
	declare('O', out);
	std::ostringstream s;
	format(s, satsolver);
	int res = system(s.str().c_str());
	if (res < 0 || (WIFEXITED(res) && WEXITSTATUS(res) == 127))
	  {
	    s << ": failed to execute";
	    throw std::runtime_error(s.str());
	  }
	// For POSIX shells, "The exit status of a command that
	// terminated because it received a signal shall be reported
	// as greater than 128."
	if (WIFEXITED(res) && WEXITSTATUS(res) >= 128)
	  {
	    s << ": terminated by signal";
	    throw std::runtime_error(s.str());
	  }
	if (WIFSIGNALED(res))
	  {
	    s << ": terminated by signal " << WTERMSIG(res);
	    throw std::runtime_error(s.str());
	  }
	return res;
      }
    };
  }

  satsolver::solution
  satsolver_get_solution(const char* filename)
  {
    satsolver::solution sol;
    std::istream* in;
    if (filename[0] == '-' && filename[1] == 0)
      in = &std::cin;
    else
      in = new std::fstream(filename, std::ios_base::in);

    int c;
    while ((c = in->get()) != EOF)
      {
	// If a line does not start with 'v ', ignore it.
	if (c != 'v' || in->get() != ' ')
	  {
	    in->ignore(std::numeric_limits<std::streamsize>::max(), '\n');
	    continue;
	  }
	// Otherwise, read integers one by one.
	int i;
	while (*in >> i)
	  {
	    if (i == 0)
	      goto done;
	    sol.push_back(i);
	  }
	if (!in->eof())
	  // If we haven't reached end-of-file, then we just attempted
	  // to extract something that wasn't an integer.  Clear the
	  // fail bit so that will loop over.
	  in->clear();
      }
  done:
    if (in != &std::cin)
      delete in;
    return sol;
  }

  satsolver::satsolver()
    : cnf_tmp_(0), cnf_stream_(0)
  {
    start();
  }

  void satsolver::start()
  {
    cnf_tmp_ = create_tmpfile("sat-", ".cnf");
    cnf_stream_ = new std::fstream(cnf_tmp_->name(),
				   std::ios_base::trunc | std::ios_base::out);
    cnf_stream_->exceptions(std::ifstream::failbit | std::ifstream::badbit);
  }

  satsolver::~satsolver()
  {
    delete cnf_tmp_;
    delete cnf_stream_;
  }

  std::ostream& satsolver::operator()()
  {
    return *cnf_stream_;
  }

  satsolver::solution_pair
  satsolver::get_solution()
  {
    delete cnf_stream_;		// Close the file.
    cnf_stream_ = 0;

    temporary_file* output = create_tmpfile("sat-", ".out");
    solution_pair p;

    // Make this static, so the SPOT_SATSOLVER lookup is done only on
    // the first call to run_sat().
    static satsolver_command cmd;

    p.first = cmd.run(cnf_tmp_, output);
    p.second = satsolver_get_solution(output->name());
    delete output;
    return p;
  }
}
