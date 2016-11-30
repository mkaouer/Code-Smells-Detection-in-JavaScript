// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include "common_sys.hh"
#include <string>
#include <iostream>
#include <cstdlib>
#include <argp.h>
#include "common_setup.hh"

const char argp_program_doc[] ="\
Common fine-tuning options for binaries built with Spot.\n\
\n\
The argument of -x or --extra-options is a comma-separated list of KEY=INT \
assignments that are passed to the post-processing routines (they may \
be passed to other algorithms in the future). These options are \
mostly used for benchmarking and debugging purpose. KEYR (without any \
value) is a shorthand for KEY=1, while !KEY is a shorthand for KEY=0.";

#define DOC(NAME, TXT) NAME, 0, 0, OPTION_DOC | OPTION_NO_USAGE, TXT, 0

static const argp_option options[] =
  {
    { 0, 0, 0, 0, "Translation options:", 0 },
    { DOC("comp-susp", "Set to 1 to enable compositional suspension, \
as described in our SPIN'13 paper (see Bibliography below).  Set to 2, \
to build only the skeleton TGBA without composing it.  Set to 0 (the \
default) to disable.") },
    { DOC("early-susp", "When set to 1, start compositional suspension on \
the transitions that enter accepting SCCs, and not only on the transitions \
inside accepting SCCs.  This option defaults to 0, and is only used when \
comp-susp=1.") },
    { DOC("skel-simul", "Default to 1.  Set to 0 to disable simulation \
on the skeleton automaton during compositional suspension. Only used when \
comp-susp=1.") },
    { DOC("skel-wdba", "Set to 0 to disable WDBA \
minimization on the skeleton automaton during compositional suspension. \
Set to 1 always WDBA-minimize the skeleton .  Set to 2 to keep the WDBA \
only if it is smaller than the original skeleton.  This option is only \
used when comp-susp=1 and default to 1 or 2 depending on whether --small \
or --deterministic is specified.") },
    { 0, 0, 0, 0, "Postprocessing options:", 0 },
    { DOC("scc-filter", "Set to 1 (the default) to enable \
SCC-pruning and acceptance simplification at the beginning of \
post-processing. Transitions that are outside of accepting SCC are \
removed from accepting sets, except those that enter into an accepting \
SCC. Set to 2 to remove even these entering transition from the \
accepting sets. Set to 0 to disable this SCC-pruning and acceptance \
simpification pass.") },
    { DOC("degen-reset", "If non-zero (the default), the \
degeneralization algorithm will reset its level any time it exits \
a non-accepting SCC.") },
    { DOC("degen-lcache", "If non-zero (the default), whenever the \
degeneralization algorithm enters an SCC on a state that has already \
been associated to a level elsewhere, it should reuse that level. \
Different values can be used to select which level to reuse: 1 always \
uses the first level seen, 2 uses the minimum level seen so far, and \
3 uses the maximum level seen so far. The \"lcache\" stands for \
\"level cache\".") },
    { DOC("degen-order", "If non-zero, the degeneralization algorithm \
will compute one degeneralization order for each SCC it processes. \
This is currently disabled by default.") },
    { DOC("degen-lskip", "If non-zero (the default), the degeneralization \
algorithm will skip as much levels as possible for each transition.  This \
is enabled by default as it very often reduce the number of resulting \
states.  A consequence of skipping levels is that the degeneralized \
automaton tends to have smaller cycles around the accepting states.  \
Disabling skipping will produce automata with large cycles, and often \
with more states.") },
    { DOC("simul", "Set to 0 to disable simulation-based reductions. \
Set to 1 to use only direct simulation. Set to 2 to use only reverse \
simulation. Set to 3 to iterate both direct and reverse simulations. \
Set to 4 to apply only \"don't care\" direct simulation. Set to 5 to \
iterate \"don't care\" direct simulation and reverse simulation. The \
default is 3, except when option --low is specified, in which case the \
default is 1.") },
    { DOC("simul-limit", "Can be set to a positive integer to cap the \
number of \"don't care\" transitions considered by the \
\"don't care\"-simulation algorithm. A negative value (the default) \
does not enforce any limit. Note that if there are N \"don't care\" \
transitions, the algorithm may potentially test 2^N configurations.") },
    { DOC("ba-simul", "Set to 0 to disable simulation-based reductions \
on the Büchi automaton (i.e., after degeneralization has been performed). \
Set to 1 to use only direct simulation.  Set to 2 to use only reverse \
simulation.  Set to 3 to iterate both direct and reverse simulations.   \
The default is 3 in --high mode, and 0 otherwise.") },
    { DOC("wdba-minimize", "Set to 0 to disable WDBA-minimization.  \
Enabled by default.") },
    { DOC("tba-det", "Set to 1 to attempt a powerset determinization \
if the TGBA is not already deterministic.  Doing so will degeneralize \
the automaton.  This is disabled by default, unless sat-minimize is set.") },
    { DOC("sat-minimize",
	  "Set to 1 to enable SAT-based minimization of deterministic \
TGBA: it starts with the number of states of the input, and iteratively \
tries to find a deterministic TGBA with one less state. Set to 2 to perform \
a binary search instead.  Disabled (0) by default.  The sat solver to use \
can be set with the SPOT_SATSOLVER environment variable (see below).  By \
default the procedure looks for a TGBA with the same number of acceptance \
set; this can be changed with the sat-acc option, or of course by using -B \
to construct a Büchi automaton.  Enabling SAT-based minimization will \
also enable tba-det.") },
    { DOC("sat-states",
	  "When this is set to some positive integer, the SAT-based \
minimization will attempt to construct a TGBA with the given number of \
states.  It may however return an automaton with less states if some of \
these are unreachable or useless.  Setting sat-states automatically \
enables sat-minimize, but no iteration is performed.  If no equivalent \
automaton could be constructed with the given number of states, the original \
automaton is returned.") },
    { DOC("sat-acc",
	  "When this is set to some positive integer, the SAT-based will \
attempt to construct a TGBA with the given number of acceptance sets. \
states.  It may however return an automaton with less acceptance sets if \
some of these are useless.  Setting sat-acc automatically \
sets sat-minimize to 1 if not set differently.") },
    { DOC("state-based",
	  "Set to 1 to instruct the SAT-minimization procedure to produce \
a TGBA where all outgoing transition of a state have the same acceptance \
sets.  By default this is only enabled when option -B is used.") },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &misc_argp_hidden, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, 0, 0, argp_program_doc, children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  std::cerr << "This binary serves no purpose other than generating"
	    << " the spot-x.7 manpage.\n";

  return 1;
}
