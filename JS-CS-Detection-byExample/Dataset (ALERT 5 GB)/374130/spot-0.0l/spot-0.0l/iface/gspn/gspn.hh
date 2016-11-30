// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_IFACE_GSPN_GSPN_HH
# define SPOT_IFACE_GSPN_GSPN_HH

// Do not include gspnlib.h here, or it will polute the user's
// namespace with internal C symbols.

# include <string>
# include "tgba/tgba.hh"
# include "common.hh"

namespace spot
{

  class gspn_interface
  {
  public:
    gspn_interface(int argc, char **argv);
    ~gspn_interface();
    // FIXME: I think we should have
    // tgba* get_automata();
  };


  /// Data private to tgba_gspn.
  struct tgba_gspn_private_;

  class tgba_gspn: public tgba
  {
  public:
    tgba_gspn(bdd_dict* dict, const gspn_environment& env);
    tgba_gspn(const tgba_gspn& other);
    tgba_gspn& operator=(const tgba_gspn& other);
    virtual ~tgba_gspn();
    virtual state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const state* state) const;
    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;
  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;
  private:
    tgba_gspn_private_* data_;
  };

}

#endif // SPOT_IFACE_GSPN_GSPN_HH
