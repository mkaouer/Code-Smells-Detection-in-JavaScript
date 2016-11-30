// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013, 2014 Laboratoire de Recherche et
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

#include "common_setup.hh"
#include "argp.h"
#include <cstdlib>

const char* argp_program_bug_address = "<" PACKAGE_BUGREPORT ">";

static void
display_version(FILE *stream, struct argp_state*)
{
  fputs(program_name, stream);
  fputs(" (" PACKAGE_STRING ")\n\
\n\
Copyright (C) 2014  Laboratoire de Recherche et Développement de l'Epita.\n\
License GPLv3+: \
GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n\
This is free software: you are free to change and redistribute it.\n\
There is NO WARRANTY, to the extent permitted by law.\n", stream);
}

void
setup(char** argv)
{
  // Simplify the program name, because argp() uses it to report
  // errors and display help text.
  set_program_name(argv[0]);
  argv[0] = const_cast<char*>(program_name);

  argp_program_version_hook = display_version;

  argp_err_exit_status = 2;
}


// argp's default behavior of offering -? for --help is just too silly.
// I mean, come on, why not also add -* to Darwinise more shell users?
// We disable this option as well as -V (because --version don't need
// a short version).
#define OPT_VERSION 1
#define OPT_HELP 2
#define OPT_USAGE 3
static const argp_option options[] =
  {
    { "version", OPT_VERSION, 0, 0, "print program version", -1 },
    { "help", OPT_HELP, 0, 0, "print this help", -1 },
    // We support this option just in case, but we don't advertise it.
    { "usage", OPT_USAGE, 0, OPTION_HIDDEN, "show short usage", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

static const argp_option options_hidden[] =
  {
    { "version", OPT_VERSION, 0, OPTION_HIDDEN, "print program version", -1 },
    { "help", OPT_HELP, 0, OPTION_HIDDEN, "print this help", -1 },
    // We support this option just in case, but we don't advertise it.
    { "usage", OPT_USAGE, 0, OPTION_HIDDEN, "show short usage", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

static int
parse_opt_misc(int key, char*, struct argp_state* state)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case OPT_VERSION:
      display_version(state->out_stream, state);
      exit(0);
      break;
    case OPT_HELP:
      argp_state_help(state, state->out_stream, ARGP_HELP_STD_HELP);
      break;
    case OPT_USAGE:
      argp_state_help(state, state->out_stream,
		      ARGP_HELP_USAGE | ARGP_HELP_EXIT_OK);
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}


const struct argp misc_argp = { options, parse_opt_misc, 0, 0, 0, 0, 0 };

const struct argp misc_argp_hidden = { options_hidden, parse_opt_misc,
				       0, 0, 0, 0, 0 };
