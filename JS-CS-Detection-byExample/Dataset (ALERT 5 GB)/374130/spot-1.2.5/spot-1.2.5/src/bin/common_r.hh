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

#ifndef SPOT_BIN_COMMON_R_HH
#define SPOT_BIN_COMMON_R_HH

#include "common_sys.hh"
#include "ltlvisit/simplify.hh"

#define OPT_R 'r'

#define DECLARE_OPT_R							\
    { "simplify", OPT_R, "LEVEL", OPTION_ARG_OPTIONAL,			\
      "simplify formulas according to LEVEL (see below); LEVEL is "	\
      "set to 3 if omitted", 0 }

#define LEVEL_DOC(g)							\
    { 0, 0, 0, 0, "The simplification LEVEL may be set as follows.", g }, \
    { "  0", 0, 0, OPTION_DOC | OPTION_NO_USAGE,			\
      "No rewriting", 0 },						\
    { "  1", 0, 0, OPTION_DOC | OPTION_NO_USAGE,			\
      "basic rewritings and eventual/universal rules", 0 },		\
    { "  2", 0, 0, OPTION_DOC | OPTION_NO_USAGE,			\
      "additional syntactic implication rules", 0 },			\
    { "  3", 0, 0, OPTION_DOC | OPTION_NO_USAGE,			\
      "better implications using containment", 0 }

extern int simplification_level;

void parse_r(const char* arg);
spot::ltl::ltl_simplifier_options simplifier_options();

#endif // SPOT_BIN_COMMON_RANGE_HH
