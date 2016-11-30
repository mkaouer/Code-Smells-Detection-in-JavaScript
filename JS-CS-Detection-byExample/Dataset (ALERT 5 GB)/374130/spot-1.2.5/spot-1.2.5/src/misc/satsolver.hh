// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
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

#ifndef SPOT_MISC_SATSOLVER_HH
#define SPOT_MISC_SATSOLVER_HH

#include "common.hh"
#include "tmpfile.hh"
#include <vector>
#include <stdexcept>
#include <iosfwd>

namespace spot
{
  class printable;

  class clause_counter
  {
  private:
    int count_;

  public:
    clause_counter()
      : count_(0)
    {
    }

    void check() const
    {
      if (count_ < 0)
	throw std::runtime_error("too many SAT clauses (more than INT_MAX)");
    }

    clause_counter& operator++()
    {
      ++count_;
      check();
      return *this;
    }

    clause_counter& operator+=(int n)
    {
      count_ += n;
      check();
      return *this;
    }

    int nb_clauses() const
    {
      return count_;
    }
  };

  /// \brief Interface with a SAT solver.
  ///
  /// Call start() to create some temporary file, then send DIMACs
  /// text to the stream returned by operator(), and finally call
  /// get_solution().
  ///
  /// The satsolver called can be configured via the
  /// <code>SPOT_SATSOLVER</code> environment variable.  It
  /// defaults to
  ///    "satsolver -verb=0 %I >%O"
  /// where %I and %O are replaced by input and output files.
  class SPOT_API satsolver
  {
  public:
    satsolver();
    ~satsolver();

    void start();
    std::ostream& operator()();

    typedef std::vector<int> solution;
    typedef std::pair<int, solution> solution_pair;
    solution_pair get_solution();
  private:
    temporary_file* cnf_tmp_;
    std::ostream* cnf_stream_;
  };

  /// \brief Extract the solution of a SAT solver output.
  SPOT_API satsolver::solution
  satsolver_get_solution(const char* filename);
}

#endif // SPOT_MISC_SATSOLVER_HH
