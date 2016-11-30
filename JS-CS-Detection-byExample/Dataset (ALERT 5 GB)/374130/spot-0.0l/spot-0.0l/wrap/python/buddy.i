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

%module buddy

%include "std_string.i"

%{
#include <sstream>
#include "bdd.h"
%}

struct bdd
{
  int id(void) const;
};

int      bdd_init(int, int);
void     bdd_done(void);
int      bdd_setvarnum(int);
int      bdd_extvarnum(int);
int      bdd_isrunning(void);
int      bdd_setmaxnodenum(int);
int      bdd_setmaxincrease(int);
int      bdd_setminfreenodes(int);
int      bdd_getnodenum(void);
int      bdd_getallocnum(void);
char*    bdd_versionstr(void);
int      bdd_versionnum(void);
void     bdd_fprintstat(FILE *);
void     bdd_printstat(void);
const char *bdd_errstring(int);
void     bdd_clear_error(void);

bdd bdd_ithvar(int v);
bdd bdd_nithvar(int v);
int bdd_var(const bdd &r);
bdd bdd_low(const bdd &r);
bdd bdd_high(const bdd &r);
int bdd_scanset(const bdd &r, int *&v, int &n);
bdd bdd_makeset(int *v, int n);
int bdd_setbddpair(bddPair *p, int ov, const bdd &nv);
bdd bdd_replace(const bdd &r, bddPair *p);
bdd bdd_compose(const bdd &f, const bdd &g, int v);
bdd bdd_veccompose(const bdd &f, bddPair *p);
bdd bdd_restrict(const bdd &r, const bdd &var);
bdd bdd_constrain(const bdd &f, const bdd &c);
bdd bdd_simplify(const bdd &d, const bdd &b);
bdd bdd_ibuildcube(int v, int w, int *a);
bdd bdd_not(const bdd &r);
bdd bdd_apply(const bdd &l, const bdd &r, int op);
bdd bdd_and(const bdd &l, const bdd &r);
bdd bdd_or(const bdd &l, const bdd &r);
bdd bdd_xor(const bdd &l, const bdd &r);
bdd bdd_imp(const bdd &l, const bdd &r);
bdd bdd_biimp(const bdd &l, const bdd &r);
bdd bdd_ite(const bdd &f, const bdd &g, const bdd &h);
bdd bdd_exist(const bdd &r, const bdd &var);
bdd bdd_existcomp(const bdd &r, const bdd &var);
bdd bdd_forall(const bdd &r, const bdd &var);
bdd bdd_forallcomp(const bdd &r, const bdd &var);
bdd bdd_unique(const bdd &r, const bdd &var);
bdd bdd_uniquecomp(const bdd &r, const bdd &var);
bdd bdd_appex(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_appexcomp(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_appall(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_appallcomp(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_appuni(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_appunicomp(const bdd &l, const bdd &r, int op, const bdd &var);
bdd bdd_support(const bdd &r);
bdd bdd_satone(const bdd &r);
bdd bdd_satoneset(const bdd &r, const bdd &var, const bdd &pol);
bdd bdd_fullsatone(const bdd &r);
void bdd_allsat(const bdd &r, bddallsathandler handler);
double bdd_satcount(const bdd &r);
double bdd_satcountset(const bdd &r, const bdd &varset);
double bdd_satcountln(const bdd &r);
double bdd_satcountlnset(const bdd &r, const bdd &varset);
int bdd_nodecount(const bdd &r);
int* bdd_varprofile(const bdd &r);
double bdd_pathcount(const bdd &r);
void bdd_fprinttable(FILE *file, const bdd &r);
void bdd_printtable(const bdd &r);
void bdd_fprintset(FILE *file, const bdd &r);
void bdd_printset(const bdd &r);
void bdd_printdot(const bdd &r);
void bdd_fprintdot(FILE* ofile, const bdd &r);
int bdd_fnprintdot(char* fname, const bdd &r);
int bdd_fnsave(char *fname, const bdd &r);
int bdd_save(FILE *ofile, const bdd &r);
int bdd_fnload(char *fname, bdd &r);
int bdd_load(FILE *ifile, bdd &r);
int bdd_addvarblock(const bdd &v, int f);

extern const bdd bddfalse;
extern const bdd bddtrue;

#define bddop_and       0
#define bddop_xor       1
#define bddop_or        2
#define bddop_nand      3
#define bddop_nor       4
#define bddop_imp       5
#define bddop_biimp     6
#define bddop_diff      7
#define bddop_less      8
#define bddop_invimp    9

#define BDD_REORDER_NONE     0
#define BDD_REORDER_WIN2     1
#define BDD_REORDER_WIN2ITE  2
#define BDD_REORDER_SIFT     3
#define BDD_REORDER_SIFTITE  4
#define BDD_REORDER_WIN3     5
#define BDD_REORDER_WIN3ITE  6
#define BDD_REORDER_RANDOM   7

%extend bdd {
    int
  __cmp__(bdd* b)
  {
    return b->id() - self->id();
  }

  std::string
  __str__(void)
  {
    std::ostringstream res;
    res << "bdd(id=" << self->id() << ")";
    return res.str();
  }

  bdd __and__(bdd& other) { return *self & other; }
  bdd __xor__(bdd& other) { return *self ^ other; }
  bdd __or__(bdd& other) { return *self | other; }
  bdd __rshift__(bdd& other) { return *self >> other; }
  bdd __lshift__(bdd& other) { return *self << other; }
  bdd __sub__(bdd& other) { return *self - other; }
  bdd __neg__(void) { return !*self; }

}