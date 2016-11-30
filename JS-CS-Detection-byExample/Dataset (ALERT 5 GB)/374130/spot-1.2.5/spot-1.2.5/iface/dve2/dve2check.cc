// Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE)
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

#include "dve2.hh"
#include "tgbaalgos/dotty.hh"
#include "ltlenv/defaultenv.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/translate.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaalgos/postproc.hh"
#include "tgba/tgbaproduct.hh"
#include "misc/timer.hh"
#include "misc/memusage.hh"
#include <cstring>
#include "kripke/kripkeexplicit.hh"
#include "kripke/kripkeprint.hh"

static void
syntax(char* prog)
{
  // Display the supplied name unless it appears to be a libtool wrapper.
  char* slash = strrchr(prog, '/');
  if (slash && (strncmp(slash + 1, "lt-", 3) == 0))
    prog = slash + 4;

  std::cerr << "usage: " << prog << " [options] model formula\n\
\n\
Options:\n\
  -dDEAD use DEAD as property for marking DEAD states\n\
          (by default DEAD = true)\n\
  -e[ALGO]  run emptiness check, expect an accepting run\n\
  -E[ALGO]  run emptiness check, expect no accepting run\n\
  -C     compute an accepting run (Counterexample) if it exists\n\
  -D     favor a deterministic translation over a small transition\n\
  -gf    output the automaton of the formula in dot format\n\
  -gm    output the model state-space in dot format\n\
  -gK    output the model state-space in Kripke format\n\
  -gp    output the product state-space in dot format\n\
  -T     time the different phases of the execution\n\
  -z     compress states to handle larger models\n\
  -Z     compress states (faster) assuming all values in [0 .. 2^28-1]\n\
";
  exit(1);
}

int
main(int argc, char **argv)
{
  spot::timer_map tm;

  bool use_timer = false;

  enum { DotFormula, DotModel, DotProduct, EmptinessCheck, Kripke }
  output = EmptinessCheck;
  bool accepting_run = false;
  bool expect_counter_example = false;
  bool deterministic = false;
  char *dead = 0;
  int compress_states = 0;

  const char* echeck_algo = "Cou99";

  int dest = 1;
  int n = argc;
  for (int i = 1; i < n; ++i)
    {
      char* opt = argv[i];
      if (*opt == '-')
	{
	  switch (*++opt)
	    {
	    case 'C':
	      accepting_run = true;
	      break;
	    case 'd':
	      dead = opt + 1;
	      break;
	    case 'D':
	      deterministic = true;
	      break;
	    case 'e':
	    case 'E':
	      {
		echeck_algo = opt + 1;
		if (!*echeck_algo)
		  echeck_algo = "Cou99";

		expect_counter_example = (*opt == 'e');
		output = EmptinessCheck;
		break;
	      }
	    case 'g':
	      switch (opt[1])
		{
		case 'm':
		  output = DotModel;
		  break;
		case 'p':
		  output = DotProduct;
		  break;
		case 'f':
		  output = DotFormula;
		  break;
                case 'K':
                  output = Kripke;
                  break;
		default:
		  goto error;
		}
	      break;
	    case 'T':
	      use_timer = true;
	      break;
	    case 'z':
	      compress_states = 1;
	      break;
	    case 'Z':
	      compress_states = 2;
	      break;
	    default:
	    error:
	      std::cerr << "Unknown option `" << argv[i] << "'." << std::endl;
	      exit(1);
	    }
	  --argc;
	}
      else
	{
	  argv[dest++] = argv[i];
	}
    }

  if (argc != 3)
    syntax(argv[0]);

  spot::ltl::default_environment& env =
    spot::ltl::default_environment::instance();


  spot::ltl::atomic_prop_set ap;
  spot::bdd_dict* dict = new spot::bdd_dict();
  spot::kripke* model = 0;
  const spot::tgba* prop = 0;
  spot::tgba* product = 0;
  spot::emptiness_check_instantiator* echeck_inst = 0;
  int exit_code = 0;
  const spot::ltl::formula* f = 0;
  const spot::ltl::formula* deadf = 0;
  spot::postprocessor post;

  if (dead == 0 || !strcasecmp(dead, "true"))
    {
      deadf = spot::ltl::constant::true_instance();
    }
  else if (!strcasecmp(dead, "false"))
    {
      deadf = spot::ltl::constant::false_instance();
    }
  else
    {
      deadf = env.require(dead);
    }

  if (output == EmptinessCheck)
    {
      const char* err;
      echeck_inst =
	spot::emptiness_check_instantiator::construct(echeck_algo, &err);
      if (!echeck_inst)
	{
	  std::cerr << "Failed to parse argument of -e/-E near `"
		    << err <<  "'" << std::endl;
	  exit_code = 1;
	  goto safe_exit;
	}
    }

  tm.start("parsing formula");
  {
    spot::ltl::parse_error_list pel;
    f = spot::ltl::parse(argv[2], pel, env, false);
    exit_code = spot::ltl::format_parse_errors(std::cerr, argv[2], pel);
  }
  tm.stop("parsing formula");

  if (exit_code)
    goto safe_exit;

  tm.start("translating formula");
  {
    spot::translator trans(dict);
    if (deterministic)
      trans.set_pref(spot::postprocessor::Deterministic);

    prop = trans.run(&f);
  }
  tm.stop("translating formula");

  atomic_prop_collect(f, &ap);

  if (output != DotFormula)
    {
      tm.start("loading dve2");
      model = spot::load_dve2(argv[1], dict, &ap, deadf, compress_states, true);
      tm.stop("loading dve2");

      if (!model)
	{
	  exit_code = 1;
	  goto safe_exit;
	}

      if (output == DotModel)
	{
	  tm.start("dotty output");
	  spot::dotty_reachable(std::cout, model);
	  tm.stop("dotty output");
	  goto safe_exit;
	}
      if (output == Kripke)
      {
        tm.start("kripke output");
	spot::kripke_save_reachable_renumbered(std::cout, model);
        tm.stop("kripke output");
        goto safe_exit;
      }
    }

  if (output == DotFormula)
    {
      tm.start("dotty output");
      spot::dotty_reachable(std::cout, prop);
      tm.stop("dotty output");
      goto safe_exit;
    }

  product = new spot::tgba_product(model, prop);

  if (output == DotProduct)
    {
      tm.start("dotty output");
      spot::dotty_reachable(std::cout, product);
      tm.stop("dotty output");
      goto safe_exit;
    }

  assert(echeck_inst);

  {
    spot::emptiness_check* ec = echeck_inst->instantiate(product);
    bool search_many = echeck_inst->options().get("repeated");
    assert(ec);
    do
      {
	int memused = spot::memusage();
	tm.start("running emptiness check");
	spot::emptiness_check_result* res;
	try
	  {
	    res = ec->check();
	  }
	catch (std::bad_alloc)
	  {
	    std::cerr << "Out of memory during emptiness check."
		      << std::endl;
	    if (!compress_states)
	      std::cerr << "Try option -z for state compression." << std::endl;
	    exit_code = 2;
	    exit(exit_code);
	  }
	tm.stop("running emptiness check");
	memused = spot::memusage() - memused;

	ec->print_stats(std::cout);
	std::cout << memused << " pages allocated for emptiness check"
		  << std::endl;

	if (expect_counter_example == !res &&
	    (!expect_counter_example || ec->safe()))
	  exit_code = 1;

	if (!res)
	  {
	    std::cout << "no accepting run found";
	    if (!ec->safe() && expect_counter_example)
	      {
		std::cout << " even if expected" << std::endl;
		std::cout << "this may be due to the use of the bit"
			  << " state hashing technique" << std::endl;
		std::cout << "you can try to increase the heap size "
			  << "or use an explicit storage"
			  << std::endl;
	      }
	    std::cout << std::endl;
	    break;
	  }
	else if (accepting_run)
	  {

	    spot::tgba_run* run;
	    tm.start("computing accepting run");
	    try
	      {
		run = res->accepting_run();
	      }
	    catch (std::bad_alloc)
	      {
		std::cerr << "Out of memory while looking for counterexample."
			  << std::endl;
		exit_code = 2;
		exit(exit_code);
	      }
	    tm.stop("computing accepting run");

	    if (!run)
	      {
		std::cout << "an accepting run exists" << std::endl;
	      }
	    else
	      {
		tm.start("reducing accepting run");
		spot::tgba_run* redrun =
		  spot::reduce_run(res->automaton(), run);
		tm.stop("reducing accepting run");
		delete run;
		run = redrun;

		tm.start("printing accepting run");
		spot::print_tgba_run(std::cout, product, run);
		tm.stop("printing accepting run");
	      }
	    delete run;
	  }
	else
	  {
	    std::cout << "an accepting run exists "
		      << "(use -C to print it)" << std::endl;
	  }
	delete res;
      }
    while (search_many);
    delete ec;
  }

 safe_exit:
  delete echeck_inst;
  delete product;
  delete prop;
  delete model;
  if (f)
    f->destroy();
  delete dict;

  deadf->destroy();

  if (use_timer)
    tm.print(std::cout);
  tm.reset_all();		// This helps valgrind.

  spot::ltl::atomic_prop::dump_instances(std::cerr);
  spot::ltl::unop::dump_instances(std::cerr);
  spot::ltl::binop::dump_instances(std::cerr);
  spot::ltl::multop::dump_instances(std::cerr);
  spot::ltl::automatop::dump_instances(std::cerr);
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  assert(spot::ltl::automatop::instance_count() == 0);

  exit(exit_code);
}
