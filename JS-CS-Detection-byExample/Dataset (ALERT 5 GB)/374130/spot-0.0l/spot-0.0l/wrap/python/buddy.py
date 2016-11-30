# This file was created automatically by SWIG.
# Don't modify this file, modify the SWIG interface instead.
# This file is compatible with both classic and new-style classes.
import _buddy
def _swig_setattr(self,class_type,name,value):
    if (name == "this"):
        if isinstance(value, class_type):
            self.__dict__[name] = value.this
            if hasattr(value,"thisown"): self.__dict__["thisown"] = value.thisown
            del value.thisown
            return
    method = class_type.__swig_setmethods__.get(name,None)
    if method: return method(self,value)
    self.__dict__[name] = value

def _swig_getattr(self,class_type,name):
    method = class_type.__swig_getmethods__.get(name,None)
    if method: return method(self)
    raise AttributeError,name

import types
try:
    _object = types.ObjectType
    _newclass = 1
except AttributeError:
    class _object : pass
    _newclass = 0


class bdd(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, bdd, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, bdd, name)
    def id(*args): return apply(_buddy.bdd_id,args)
    def __cmp__(*args): return apply(_buddy.bdd___cmp__,args)
    def __str__(*args): return apply(_buddy.bdd___str__,args)
    def __and__(*args): return apply(_buddy.bdd___and__,args)
    def __xor__(*args): return apply(_buddy.bdd___xor__,args)
    def __or__(*args): return apply(_buddy.bdd___or__,args)
    def __rshift__(*args): return apply(_buddy.bdd___rshift__,args)
    def __lshift__(*args): return apply(_buddy.bdd___lshift__,args)
    def __sub__(*args): return apply(_buddy.bdd___sub__,args)
    def __neg__(*args): return apply(_buddy.bdd___neg__,args)
    def __init__(self,*args):
        _swig_setattr(self, bdd, 'this', apply(_buddy.new_bdd,args))
        _swig_setattr(self, bdd, 'thisown', 1)
    def __del__(self, destroy= _buddy.delete_bdd):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C bdd instance at %s>" % (self.this,)

class bddPtr(bdd):
    def __init__(self,this):
        _swig_setattr(self, bdd, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, bdd, 'thisown', 0)
        _swig_setattr(self, bdd,self.__class__,bdd)
_buddy.bdd_swigregister(bddPtr)

bdd_init = _buddy.bdd_init

bdd_done = _buddy.bdd_done

bdd_setvarnum = _buddy.bdd_setvarnum

bdd_extvarnum = _buddy.bdd_extvarnum

bdd_isrunning = _buddy.bdd_isrunning

bdd_setmaxnodenum = _buddy.bdd_setmaxnodenum

bdd_setmaxincrease = _buddy.bdd_setmaxincrease

bdd_setminfreenodes = _buddy.bdd_setminfreenodes

bdd_getnodenum = _buddy.bdd_getnodenum

bdd_getallocnum = _buddy.bdd_getallocnum

bdd_versionstr = _buddy.bdd_versionstr

bdd_versionnum = _buddy.bdd_versionnum

bdd_fprintstat = _buddy.bdd_fprintstat

bdd_printstat = _buddy.bdd_printstat

bdd_errstring = _buddy.bdd_errstring

bdd_clear_error = _buddy.bdd_clear_error

bdd_ithvar = _buddy.bdd_ithvar

bdd_nithvar = _buddy.bdd_nithvar

bdd_var = _buddy.bdd_var

bdd_low = _buddy.bdd_low

bdd_high = _buddy.bdd_high

bdd_scanset = _buddy.bdd_scanset

bdd_makeset = _buddy.bdd_makeset

bdd_setbddpair = _buddy.bdd_setbddpair

bdd_replace = _buddy.bdd_replace

bdd_compose = _buddy.bdd_compose

bdd_veccompose = _buddy.bdd_veccompose

bdd_restrict = _buddy.bdd_restrict

bdd_constrain = _buddy.bdd_constrain

bdd_simplify = _buddy.bdd_simplify

bdd_ibuildcube = _buddy.bdd_ibuildcube

bdd_not = _buddy.bdd_not

bdd_apply = _buddy.bdd_apply

bdd_and = _buddy.bdd_and

bdd_or = _buddy.bdd_or

bdd_xor = _buddy.bdd_xor

bdd_imp = _buddy.bdd_imp

bdd_biimp = _buddy.bdd_biimp

bdd_ite = _buddy.bdd_ite

bdd_exist = _buddy.bdd_exist

bdd_existcomp = _buddy.bdd_existcomp

bdd_forall = _buddy.bdd_forall

bdd_forallcomp = _buddy.bdd_forallcomp

bdd_unique = _buddy.bdd_unique

bdd_uniquecomp = _buddy.bdd_uniquecomp

bdd_appex = _buddy.bdd_appex

bdd_appexcomp = _buddy.bdd_appexcomp

bdd_appall = _buddy.bdd_appall

bdd_appallcomp = _buddy.bdd_appallcomp

bdd_appuni = _buddy.bdd_appuni

bdd_appunicomp = _buddy.bdd_appunicomp

bdd_support = _buddy.bdd_support

bdd_satone = _buddy.bdd_satone

bdd_satoneset = _buddy.bdd_satoneset

bdd_fullsatone = _buddy.bdd_fullsatone

bdd_allsat = _buddy.bdd_allsat

bdd_satcount = _buddy.bdd_satcount

bdd_satcountset = _buddy.bdd_satcountset

bdd_satcountln = _buddy.bdd_satcountln

bdd_satcountlnset = _buddy.bdd_satcountlnset

bdd_nodecount = _buddy.bdd_nodecount

bdd_varprofile = _buddy.bdd_varprofile

bdd_pathcount = _buddy.bdd_pathcount

bdd_fprinttable = _buddy.bdd_fprinttable

bdd_printtable = _buddy.bdd_printtable

bdd_fprintset = _buddy.bdd_fprintset

bdd_printset = _buddy.bdd_printset

bdd_printdot = _buddy.bdd_printdot

bdd_fprintdot = _buddy.bdd_fprintdot

bdd_fnprintdot = _buddy.bdd_fnprintdot

bdd_fnsave = _buddy.bdd_fnsave

bdd_save = _buddy.bdd_save

bdd_fnload = _buddy.bdd_fnload

bdd_load = _buddy.bdd_load

bdd_addvarblock = _buddy.bdd_addvarblock

bddop_and = _buddy.bddop_and
bddop_xor = _buddy.bddop_xor
bddop_or = _buddy.bddop_or
bddop_nand = _buddy.bddop_nand
bddop_nor = _buddy.bddop_nor
bddop_imp = _buddy.bddop_imp
bddop_biimp = _buddy.bddop_biimp
bddop_diff = _buddy.bddop_diff
bddop_less = _buddy.bddop_less
bddop_invimp = _buddy.bddop_invimp
BDD_REORDER_NONE = _buddy.BDD_REORDER_NONE
BDD_REORDER_WIN2 = _buddy.BDD_REORDER_WIN2
BDD_REORDER_WIN2ITE = _buddy.BDD_REORDER_WIN2ITE
BDD_REORDER_SIFT = _buddy.BDD_REORDER_SIFT
BDD_REORDER_SIFTITE = _buddy.BDD_REORDER_SIFTITE
BDD_REORDER_WIN3 = _buddy.BDD_REORDER_WIN3
BDD_REORDER_WIN3ITE = _buddy.BDD_REORDER_WIN3ITE
BDD_REORDER_RANDOM = _buddy.BDD_REORDER_RANDOM
cvar = _buddy.cvar
bddfalse = cvar.bddfalse
bddtrue = cvar.bddtrue

