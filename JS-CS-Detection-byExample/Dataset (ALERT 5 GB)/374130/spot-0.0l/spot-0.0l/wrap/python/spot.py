# This file was created automatically by SWIG.
# Don't modify this file, modify the SWIG interface instead.
# This file is compatible with both classic and new-style classes.
import _spot
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


import buddy
version = _spot.version

class formula(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, formula, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, formula, name)
    def __del__(self, destroy= _spot.delete_formula):
        try:
            if self.thisown: destroy(self)
        except: pass
    def accept(*args): return apply(_spot.formula_accept,args)
    def ref(*args): return apply(_spot.formula_ref,args)
    __swig_getmethods__["unref"] = lambda x: _spot.formula_unref
    if _newclass:unref = staticmethod(_spot.formula_unref)
    def __cmp__(*args): return apply(_spot.formula___cmp__,args)
    def __str__(*args): return apply(_spot.formula___str__,args)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C formula instance at %s>" % (self.this,)

class formulaPtr(formula):
    def __init__(self,this):
        _swig_setattr(self, formula, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, formula, 'thisown', 0)
        _swig_setattr(self, formula,self.__class__,formula)
_spot.formula_swigregister(formulaPtr)
formula_unref = _spot.formula_unref


class ref_formula(formula):
    __swig_setmethods__ = {}
    for _s in [formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, ref_formula, name, value)
    __swig_getmethods__ = {}
    for _s in [formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, ref_formula, name)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C ref_formula instance at %s>" % (self.this,)

class ref_formulaPtr(ref_formula):
    def __init__(self,this):
        _swig_setattr(self, ref_formula, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, ref_formula, 'thisown', 0)
        _swig_setattr(self, ref_formula,self.__class__,ref_formula)
_spot.ref_formula_swigregister(ref_formulaPtr)

class atomic_prop(ref_formula):
    __swig_setmethods__ = {}
    for _s in [ref_formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, atomic_prop, name, value)
    __swig_getmethods__ = {}
    for _s in [ref_formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, atomic_prop, name)
    __swig_getmethods__["instance"] = lambda x: _spot.atomic_prop_instance
    if _newclass:instance = staticmethod(_spot.atomic_prop_instance)
    def accept(*args): return apply(_spot.atomic_prop_accept,args)
    def name(*args): return apply(_spot.atomic_prop_name,args)
    def env(*args): return apply(_spot.atomic_prop_env,args)
    __swig_getmethods__["instance_count"] = lambda x: _spot.atomic_prop_instance_count
    if _newclass:instance_count = staticmethod(_spot.atomic_prop_instance_count)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C atomic_prop instance at %s>" % (self.this,)

class atomic_propPtr(atomic_prop):
    def __init__(self,this):
        _swig_setattr(self, atomic_prop, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, atomic_prop, 'thisown', 0)
        _swig_setattr(self, atomic_prop,self.__class__,atomic_prop)
_spot.atomic_prop_swigregister(atomic_propPtr)
atomic_prop_instance = _spot.atomic_prop_instance

atomic_prop_instance_count = _spot.atomic_prop_instance_count


class binop(ref_formula):
    __swig_setmethods__ = {}
    for _s in [ref_formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, binop, name, value)
    __swig_getmethods__ = {}
    for _s in [ref_formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, binop, name)
    Xor = _spot.binop_Xor
    Implies = _spot.binop_Implies
    Equiv = _spot.binop_Equiv
    U = _spot.binop_U
    R = _spot.binop_R
    __swig_getmethods__["instance"] = lambda x: _spot.binop_instance
    if _newclass:instance = staticmethod(_spot.binop_instance)
    def accept(*args): return apply(_spot.binop_accept,args)
    def first(*args): return apply(_spot.binop_first,args)
    def second(*args): return apply(_spot.binop_second,args)
    def op(*args): return apply(_spot.binop_op,args)
    def op_name(*args): return apply(_spot.binop_op_name,args)
    __swig_getmethods__["instance_count"] = lambda x: _spot.binop_instance_count
    if _newclass:instance_count = staticmethod(_spot.binop_instance_count)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C binop instance at %s>" % (self.this,)

class binopPtr(binop):
    def __init__(self,this):
        _swig_setattr(self, binop, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, binop, 'thisown', 0)
        _swig_setattr(self, binop,self.__class__,binop)
_spot.binop_swigregister(binopPtr)
binop_instance = _spot.binop_instance

binop_instance_count = _spot.binop_instance_count


class constant(formula):
    __swig_setmethods__ = {}
    for _s in [formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, constant, name, value)
    __swig_getmethods__ = {}
    for _s in [formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, constant, name)
    False = _spot.constant_False
    True = _spot.constant_True
    def accept(*args): return apply(_spot.constant_accept,args)
    def val(*args): return apply(_spot.constant_val,args)
    def val_name(*args): return apply(_spot.constant_val_name,args)
    __swig_getmethods__["true_instance"] = lambda x: _spot.constant_true_instance
    if _newclass:true_instance = staticmethod(_spot.constant_true_instance)
    __swig_getmethods__["false_instance"] = lambda x: _spot.constant_false_instance
    if _newclass:false_instance = staticmethod(_spot.constant_false_instance)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C constant instance at %s>" % (self.this,)

class constantPtr(constant):
    def __init__(self,this):
        _swig_setattr(self, constant, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, constant, 'thisown', 0)
        _swig_setattr(self, constant,self.__class__,constant)
_spot.constant_swigregister(constantPtr)
constant_true_instance = _spot.constant_true_instance

constant_false_instance = _spot.constant_false_instance


class multop(ref_formula):
    __swig_setmethods__ = {}
    for _s in [ref_formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, multop, name, value)
    __swig_getmethods__ = {}
    for _s in [ref_formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, multop, name)
    Or = _spot.multop_Or
    And = _spot.multop_And
    __swig_getmethods__["instance"] = lambda x: _spot.multop_instance
    if _newclass:instance = staticmethod(_spot.multop_instance)
    __swig_getmethods__["instance"] = lambda x: _spot.multop_instance
    if _newclass:instance = staticmethod(_spot.multop_instance)
    def accept(*args): return apply(_spot.multop_accept,args)
    def size(*args): return apply(_spot.multop_size,args)
    def nth(*args): return apply(_spot.multop_nth,args)
    def op(*args): return apply(_spot.multop_op,args)
    def op_name(*args): return apply(_spot.multop_op_name,args)
    __swig_getmethods__["instance_count"] = lambda x: _spot.multop_instance_count
    if _newclass:instance_count = staticmethod(_spot.multop_instance_count)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C multop instance at %s>" % (self.this,)

class multopPtr(multop):
    def __init__(self,this):
        _swig_setattr(self, multop, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, multop, 'thisown', 0)
        _swig_setattr(self, multop,self.__class__,multop)
_spot.multop_swigregister(multopPtr)
multop_instance = _spot.multop_instance

multop_instance_count = _spot.multop_instance_count


class unop(ref_formula):
    __swig_setmethods__ = {}
    for _s in [ref_formula]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, unop, name, value)
    __swig_getmethods__ = {}
    for _s in [ref_formula]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, unop, name)
    Not = _spot.unop_Not
    X = _spot.unop_X
    F = _spot.unop_F
    G = _spot.unop_G
    __swig_getmethods__["instance"] = lambda x: _spot.unop_instance
    if _newclass:instance = staticmethod(_spot.unop_instance)
    def accept(*args): return apply(_spot.unop_accept,args)
    def child(*args): return apply(_spot.unop_child,args)
    def op(*args): return apply(_spot.unop_op,args)
    def op_name(*args): return apply(_spot.unop_op_name,args)
    __swig_getmethods__["instance_count"] = lambda x: _spot.unop_instance_count
    if _newclass:instance_count = staticmethod(_spot.unop_instance_count)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C unop instance at %s>" % (self.this,)

class unopPtr(unop):
    def __init__(self,this):
        _swig_setattr(self, unop, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, unop, 'thisown', 0)
        _swig_setattr(self, unop,self.__class__,unop)
_spot.unop_swigregister(unopPtr)
unop_instance = _spot.unop_instance

unop_instance_count = _spot.unop_instance_count


class visitor(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, visitor, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, visitor, name)
    def visit(*args): return apply(_spot.visitor_visit,args)
    def __del__(self, destroy= _spot.delete_visitor):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C visitor instance at %s>" % (self.this,)

class visitorPtr(visitor):
    def __init__(self,this):
        _swig_setattr(self, visitor, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, visitor, 'thisown', 0)
        _swig_setattr(self, visitor,self.__class__,visitor)
_spot.visitor_swigregister(visitorPtr)

class const_visitor(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, const_visitor, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, const_visitor, name)
    def visit(*args): return apply(_spot.const_visitor_visit,args)
    def __del__(self, destroy= _spot.delete_const_visitor):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C const_visitor instance at %s>" % (self.this,)

class const_visitorPtr(const_visitor):
    def __init__(self,this):
        _swig_setattr(self, const_visitor, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, const_visitor, 'thisown', 0)
        _swig_setattr(self, const_visitor,self.__class__,const_visitor)
_spot.const_visitor_swigregister(const_visitorPtr)

class environment(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, environment, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, environment, name)
    def require(*args): return apply(_spot.environment_require,args)
    def name(*args): return apply(_spot.environment_name,args)
    def __del__(self, destroy= _spot.delete_environment):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C environment instance at %s>" % (self.this,)

class environmentPtr(environment):
    def __init__(self,this):
        _swig_setattr(self, environment, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, environment, 'thisown', 0)
        _swig_setattr(self, environment,self.__class__,environment)
_spot.environment_swigregister(environmentPtr)

class default_environment(environment):
    __swig_setmethods__ = {}
    for _s in [environment]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, default_environment, name, value)
    __swig_getmethods__ = {}
    for _s in [environment]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, default_environment, name)
    def __del__(self, destroy= _spot.delete_default_environment):
        try:
            if self.thisown: destroy(self)
        except: pass
    def require(*args): return apply(_spot.default_environment_require,args)
    def name(*args): return apply(_spot.default_environment_name,args)
    __swig_getmethods__["instance"] = lambda x: _spot.default_environment_instance
    if _newclass:instance = staticmethod(_spot.default_environment_instance)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C default_environment instance at %s>" % (self.this,)

class default_environmentPtr(default_environment):
    def __init__(self,this):
        _swig_setattr(self, default_environment, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, default_environment, 'thisown', 0)
        _swig_setattr(self, default_environment,self.__class__,default_environment)
_spot.default_environment_swigregister(default_environmentPtr)
default_environment_instance = _spot.default_environment_instance


parse = _spot.parse

format_parse_errors = _spot.format_parse_errors

class clone_visitor(visitor):
    __swig_setmethods__ = {}
    for _s in [visitor]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, clone_visitor, name, value)
    __swig_getmethods__ = {}
    for _s in [visitor]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, clone_visitor, name)
    def __init__(self,*args):
        _swig_setattr(self, clone_visitor, 'this', apply(_spot.new_clone_visitor,args))
        _swig_setattr(self, clone_visitor, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_clone_visitor):
        try:
            if self.thisown: destroy(self)
        except: pass
    def result(*args): return apply(_spot.clone_visitor_result,args)
    def visit(*args): return apply(_spot.clone_visitor_visit,args)
    def recurse(*args): return apply(_spot.clone_visitor_recurse,args)
    def __repr__(self):
        return "<C clone_visitor instance at %s>" % (self.this,)

class clone_visitorPtr(clone_visitor):
    def __init__(self,this):
        _swig_setattr(self, clone_visitor, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, clone_visitor, 'thisown', 0)
        _swig_setattr(self, clone_visitor,self.__class__,clone_visitor)
_spot.clone_visitor_swigregister(clone_visitorPtr)

clone = _spot.clone

destroy = _spot.destroy

dotty = _spot.dotty

dump = _spot.dump

class unabbreviate_logic_visitor(clone_visitor):
    __swig_setmethods__ = {}
    for _s in [clone_visitor]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, unabbreviate_logic_visitor, name, value)
    __swig_getmethods__ = {}
    for _s in [clone_visitor]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, unabbreviate_logic_visitor, name)
    def __del__(self, destroy= _spot.delete_unabbreviate_logic_visitor):
        try:
            if self.thisown: destroy(self)
        except: pass
    def visit(*args): return apply(_spot.unabbreviate_logic_visitor_visit,args)
    def recurse(*args): return apply(_spot.unabbreviate_logic_visitor_recurse,args)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C unabbreviate_logic_visitor instance at %s>" % (self.this,)

class unabbreviate_logic_visitorPtr(unabbreviate_logic_visitor):
    def __init__(self,this):
        _swig_setattr(self, unabbreviate_logic_visitor, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, unabbreviate_logic_visitor, 'thisown', 0)
        _swig_setattr(self, unabbreviate_logic_visitor,self.__class__,unabbreviate_logic_visitor)
_spot.unabbreviate_logic_visitor_swigregister(unabbreviate_logic_visitorPtr)

unabbreviate_logic = _spot.unabbreviate_logic

negative_normal_form = _spot.negative_normal_form

class unabbreviate_ltl_visitor(unabbreviate_logic_visitor):
    __swig_setmethods__ = {}
    for _s in [unabbreviate_logic_visitor]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, unabbreviate_ltl_visitor, name, value)
    __swig_getmethods__ = {}
    for _s in [unabbreviate_logic_visitor]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, unabbreviate_ltl_visitor, name)
    def __del__(self, destroy= _spot.delete_unabbreviate_ltl_visitor):
        try:
            if self.thisown: destroy(self)
        except: pass
    def visit(*args): return apply(_spot.unabbreviate_ltl_visitor_visit,args)
    def recurse(*args): return apply(_spot.unabbreviate_ltl_visitor_recurse,args)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C unabbreviate_ltl_visitor instance at %s>" % (self.this,)

class unabbreviate_ltl_visitorPtr(unabbreviate_ltl_visitor):
    def __init__(self,this):
        _swig_setattr(self, unabbreviate_ltl_visitor, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, unabbreviate_ltl_visitor, 'thisown', 0)
        _swig_setattr(self, unabbreviate_ltl_visitor,self.__class__,unabbreviate_ltl_visitor)
_spot.unabbreviate_ltl_visitor_swigregister(unabbreviate_ltl_visitorPtr)
to_string = _spot.to_string


unabbreviate_ltl = _spot.unabbreviate_ltl

class bdd_dict(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, bdd_dict, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, bdd_dict, name)
    def __init__(self,*args):
        _swig_setattr(self, bdd_dict, 'this', apply(_spot.new_bdd_dict,args))
        _swig_setattr(self, bdd_dict, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_bdd_dict):
        try:
            if self.thisown: destroy(self)
        except: pass
    __swig_setmethods__["now_map"] = _spot.bdd_dict_now_map_set
    __swig_getmethods__["now_map"] = _spot.bdd_dict_now_map_get
    if _newclass:now_map = property(_spot.bdd_dict_now_map_get,_spot.bdd_dict_now_map_set)
    __swig_setmethods__["now_formula_map"] = _spot.bdd_dict_now_formula_map_set
    __swig_getmethods__["now_formula_map"] = _spot.bdd_dict_now_formula_map_get
    if _newclass:now_formula_map = property(_spot.bdd_dict_now_formula_map_get,_spot.bdd_dict_now_formula_map_set)
    __swig_setmethods__["var_map"] = _spot.bdd_dict_var_map_set
    __swig_getmethods__["var_map"] = _spot.bdd_dict_var_map_get
    if _newclass:var_map = property(_spot.bdd_dict_var_map_get,_spot.bdd_dict_var_map_set)
    __swig_setmethods__["var_formula_map"] = _spot.bdd_dict_var_formula_map_set
    __swig_getmethods__["var_formula_map"] = _spot.bdd_dict_var_formula_map_get
    if _newclass:var_formula_map = property(_spot.bdd_dict_var_formula_map_get,_spot.bdd_dict_var_formula_map_set)
    __swig_setmethods__["acc_map"] = _spot.bdd_dict_acc_map_set
    __swig_getmethods__["acc_map"] = _spot.bdd_dict_acc_map_get
    if _newclass:acc_map = property(_spot.bdd_dict_acc_map_get,_spot.bdd_dict_acc_map_set)
    __swig_setmethods__["acc_formula_map"] = _spot.bdd_dict_acc_formula_map_set
    __swig_getmethods__["acc_formula_map"] = _spot.bdd_dict_acc_formula_map_get
    if _newclass:acc_formula_map = property(_spot.bdd_dict_acc_formula_map_get,_spot.bdd_dict_acc_formula_map_set)
    __swig_setmethods__["next_to_now"] = _spot.bdd_dict_next_to_now_set
    __swig_getmethods__["next_to_now"] = _spot.bdd_dict_next_to_now_get
    if _newclass:next_to_now = property(_spot.bdd_dict_next_to_now_get,_spot.bdd_dict_next_to_now_set)
    __swig_setmethods__["now_to_next"] = _spot.bdd_dict_now_to_next_set
    __swig_getmethods__["now_to_next"] = _spot.bdd_dict_now_to_next_get
    if _newclass:now_to_next = property(_spot.bdd_dict_now_to_next_get,_spot.bdd_dict_now_to_next_set)
    def register_proposition(*args): return apply(_spot.bdd_dict_register_proposition,args)
    def register_propositions(*args): return apply(_spot.bdd_dict_register_propositions,args)
    def register_state(*args): return apply(_spot.bdd_dict_register_state,args)
    def register_acceptance_variable(*args): return apply(_spot.bdd_dict_register_acceptance_variable,args)
    def register_acceptance_variables(*args): return apply(_spot.bdd_dict_register_acceptance_variables,args)
    def register_all_variables_of(*args): return apply(_spot.bdd_dict_register_all_variables_of,args)
    def unregister_all_my_variables(*args): return apply(_spot.bdd_dict_unregister_all_my_variables,args)
    def is_registered_proposition(*args): return apply(_spot.bdd_dict_is_registered_proposition,args)
    def is_registered_state(*args): return apply(_spot.bdd_dict_is_registered_state,args)
    def is_registered_acceptance_variable(*args): return apply(_spot.bdd_dict_is_registered_acceptance_variable,args)
    def dump(*args): return apply(_spot.bdd_dict_dump,args)
    def assert_emptiness(*args): return apply(_spot.bdd_dict_assert_emptiness,args)
    def __repr__(self):
        return "<C bdd_dict instance at %s>" % (self.this,)

class bdd_dictPtr(bdd_dict):
    def __init__(self,this):
        _swig_setattr(self, bdd_dict, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, bdd_dict, 'thisown', 0)
        _swig_setattr(self, bdd_dict,self.__class__,bdd_dict)
_spot.bdd_dict_swigregister(bdd_dictPtr)

bdd_print_sat = _spot.bdd_print_sat

bdd_format_sat = _spot.bdd_format_sat

bdd_print_acc = _spot.bdd_print_acc

bdd_print_accset = _spot.bdd_print_accset

bdd_print_set = _spot.bdd_print_set

bdd_format_set = _spot.bdd_format_set

bdd_print_formula = _spot.bdd_print_formula

bdd_format_formula = _spot.bdd_format_formula

bdd_print_dot = _spot.bdd_print_dot

bdd_print_table = _spot.bdd_print_table

class state(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, state, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, state, name)
    def compare(*args): return apply(_spot.state_compare,args)
    def hash(*args): return apply(_spot.state_hash,args)
    def clone(*args): return apply(_spot.state_clone,args)
    def __del__(self, destroy= _spot.delete_state):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C state instance at %s>" % (self.this,)

class statePtr(state):
    def __init__(self,this):
        _swig_setattr(self, state, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state, 'thisown', 0)
        _swig_setattr(self, state,self.__class__,state)
_spot.state_swigregister(statePtr)

class state_ptr_less_than(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_ptr_less_than, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, state_ptr_less_than, name)
    def __call__(*args): return apply(_spot.state_ptr_less_than___call__,args)
    def __init__(self,*args):
        _swig_setattr(self, state_ptr_less_than, 'this', apply(_spot.new_state_ptr_less_than,args))
        _swig_setattr(self, state_ptr_less_than, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_state_ptr_less_than):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C state_ptr_less_than instance at %s>" % (self.this,)

class state_ptr_less_thanPtr(state_ptr_less_than):
    def __init__(self,this):
        _swig_setattr(self, state_ptr_less_than, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_ptr_less_than, 'thisown', 0)
        _swig_setattr(self, state_ptr_less_than,self.__class__,state_ptr_less_than)
_spot.state_ptr_less_than_swigregister(state_ptr_less_thanPtr)

class state_ptr_equal(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_ptr_equal, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, state_ptr_equal, name)
    def __call__(*args): return apply(_spot.state_ptr_equal___call__,args)
    def __init__(self,*args):
        _swig_setattr(self, state_ptr_equal, 'this', apply(_spot.new_state_ptr_equal,args))
        _swig_setattr(self, state_ptr_equal, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_state_ptr_equal):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C state_ptr_equal instance at %s>" % (self.this,)

class state_ptr_equalPtr(state_ptr_equal):
    def __init__(self,this):
        _swig_setattr(self, state_ptr_equal, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_ptr_equal, 'thisown', 0)
        _swig_setattr(self, state_ptr_equal,self.__class__,state_ptr_equal)
_spot.state_ptr_equal_swigregister(state_ptr_equalPtr)

class state_ptr_hash(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_ptr_hash, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, state_ptr_hash, name)
    def __call__(*args): return apply(_spot.state_ptr_hash___call__,args)
    def __init__(self,*args):
        _swig_setattr(self, state_ptr_hash, 'this', apply(_spot.new_state_ptr_hash,args))
        _swig_setattr(self, state_ptr_hash, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_state_ptr_hash):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C state_ptr_hash instance at %s>" % (self.this,)

class state_ptr_hashPtr(state_ptr_hash):
    def __init__(self,this):
        _swig_setattr(self, state_ptr_hash, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_ptr_hash, 'thisown', 0)
        _swig_setattr(self, state_ptr_hash,self.__class__,state_ptr_hash)
_spot.state_ptr_hash_swigregister(state_ptr_hashPtr)

class tgba_succ_iterator(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_succ_iterator, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_succ_iterator, name)
    def __del__(self, destroy= _spot.delete_tgba_succ_iterator):
        try:
            if self.thisown: destroy(self)
        except: pass
    def first(*args): return apply(_spot.tgba_succ_iterator_first,args)
    def next(*args): return apply(_spot.tgba_succ_iterator_next,args)
    def done(*args): return apply(_spot.tgba_succ_iterator_done,args)
    def current_state(*args): return apply(_spot.tgba_succ_iterator_current_state,args)
    def current_condition(*args): return apply(_spot.tgba_succ_iterator_current_condition,args)
    def current_acceptance_conditions(*args): return apply(_spot.tgba_succ_iterator_current_acceptance_conditions,args)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C tgba_succ_iterator instance at %s>" % (self.this,)

class tgba_succ_iteratorPtr(tgba_succ_iterator):
    def __init__(self,this):
        _swig_setattr(self, tgba_succ_iterator, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_succ_iterator, 'thisown', 0)
        _swig_setattr(self, tgba_succ_iterator,self.__class__,tgba_succ_iterator)
_spot.tgba_succ_iterator_swigregister(tgba_succ_iteratorPtr)

class tgba(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, tgba, name)
    def __del__(self, destroy= _spot.delete_tgba):
        try:
            if self.thisown: destroy(self)
        except: pass
    def get_init_state(*args): return apply(_spot.tgba_get_init_state,args)
    def succ_iter(*args): return apply(_spot.tgba_succ_iter,args)
    def support_conditions(*args): return apply(_spot.tgba_support_conditions,args)
    def support_variables(*args): return apply(_spot.tgba_support_variables,args)
    def get_dict(*args): return apply(_spot.tgba_get_dict,args)
    def format_state(*args): return apply(_spot.tgba_format_state,args)
    def project_state(*args): return apply(_spot.tgba_project_state,args)
    def all_acceptance_conditions(*args): return apply(_spot.tgba_all_acceptance_conditions,args)
    def neg_acceptance_conditions(*args): return apply(_spot.tgba_neg_acceptance_conditions,args)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C tgba instance at %s>" % (self.this,)

class tgbaPtr(tgba):
    def __init__(self,this):
        _swig_setattr(self, tgba, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba, 'thisown', 0)
        _swig_setattr(self, tgba,self.__class__,tgba)
_spot.tgba_swigregister(tgbaPtr)

class state_bdd(state):
    __swig_setmethods__ = {}
    for _s in [state]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_bdd, name, value)
    __swig_getmethods__ = {}
    for _s in [state]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, state_bdd, name)
    def __init__(self,*args):
        _swig_setattr(self, state_bdd, 'this', apply(_spot.new_state_bdd,args))
        _swig_setattr(self, state_bdd, 'thisown', 1)
    def as_bdd(*args): return apply(_spot.state_bdd_as_bdd,args)
    def compare(*args): return apply(_spot.state_bdd_compare,args)
    def hash(*args): return apply(_spot.state_bdd_hash,args)
    def clone(*args): return apply(_spot.state_bdd_clone,args)
    def __del__(self, destroy= _spot.delete_state_bdd):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C state_bdd instance at %s>" % (self.this,)

class state_bddPtr(state_bdd):
    def __init__(self,this):
        _swig_setattr(self, state_bdd, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_bdd, 'thisown', 0)
        _swig_setattr(self, state_bdd,self.__class__,state_bdd)
_spot.state_bdd_swigregister(state_bddPtr)

class tgba_bdd_core_data(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_bdd_core_data, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_bdd_core_data, name)
    __swig_setmethods__["relation"] = _spot.tgba_bdd_core_data_relation_set
    __swig_getmethods__["relation"] = _spot.tgba_bdd_core_data_relation_get
    if _newclass:relation = property(_spot.tgba_bdd_core_data_relation_get,_spot.tgba_bdd_core_data_relation_set)
    __swig_setmethods__["acceptance_conditions"] = _spot.tgba_bdd_core_data_acceptance_conditions_set
    __swig_getmethods__["acceptance_conditions"] = _spot.tgba_bdd_core_data_acceptance_conditions_get
    if _newclass:acceptance_conditions = property(_spot.tgba_bdd_core_data_acceptance_conditions_get,_spot.tgba_bdd_core_data_acceptance_conditions_set)
    __swig_setmethods__["all_acceptance_conditions"] = _spot.tgba_bdd_core_data_all_acceptance_conditions_set
    __swig_getmethods__["all_acceptance_conditions"] = _spot.tgba_bdd_core_data_all_acceptance_conditions_get
    if _newclass:all_acceptance_conditions = property(_spot.tgba_bdd_core_data_all_acceptance_conditions_get,_spot.tgba_bdd_core_data_all_acceptance_conditions_set)
    __swig_setmethods__["now_set"] = _spot.tgba_bdd_core_data_now_set_set
    __swig_getmethods__["now_set"] = _spot.tgba_bdd_core_data_now_set_get
    if _newclass:now_set = property(_spot.tgba_bdd_core_data_now_set_get,_spot.tgba_bdd_core_data_now_set_set)
    __swig_setmethods__["next_set"] = _spot.tgba_bdd_core_data_next_set_set
    __swig_getmethods__["next_set"] = _spot.tgba_bdd_core_data_next_set_get
    if _newclass:next_set = property(_spot.tgba_bdd_core_data_next_set_get,_spot.tgba_bdd_core_data_next_set_set)
    __swig_setmethods__["nownext_set"] = _spot.tgba_bdd_core_data_nownext_set_set
    __swig_getmethods__["nownext_set"] = _spot.tgba_bdd_core_data_nownext_set_get
    if _newclass:nownext_set = property(_spot.tgba_bdd_core_data_nownext_set_get,_spot.tgba_bdd_core_data_nownext_set_set)
    __swig_setmethods__["notnow_set"] = _spot.tgba_bdd_core_data_notnow_set_set
    __swig_getmethods__["notnow_set"] = _spot.tgba_bdd_core_data_notnow_set_get
    if _newclass:notnow_set = property(_spot.tgba_bdd_core_data_notnow_set_get,_spot.tgba_bdd_core_data_notnow_set_set)
    __swig_setmethods__["notnext_set"] = _spot.tgba_bdd_core_data_notnext_set_set
    __swig_getmethods__["notnext_set"] = _spot.tgba_bdd_core_data_notnext_set_get
    if _newclass:notnext_set = property(_spot.tgba_bdd_core_data_notnext_set_get,_spot.tgba_bdd_core_data_notnext_set_set)
    __swig_setmethods__["var_set"] = _spot.tgba_bdd_core_data_var_set_set
    __swig_getmethods__["var_set"] = _spot.tgba_bdd_core_data_var_set_get
    if _newclass:var_set = property(_spot.tgba_bdd_core_data_var_set_get,_spot.tgba_bdd_core_data_var_set_set)
    __swig_setmethods__["notvar_set"] = _spot.tgba_bdd_core_data_notvar_set_set
    __swig_getmethods__["notvar_set"] = _spot.tgba_bdd_core_data_notvar_set_get
    if _newclass:notvar_set = property(_spot.tgba_bdd_core_data_notvar_set_get,_spot.tgba_bdd_core_data_notvar_set_set)
    __swig_setmethods__["varandnext_set"] = _spot.tgba_bdd_core_data_varandnext_set_set
    __swig_getmethods__["varandnext_set"] = _spot.tgba_bdd_core_data_varandnext_set_get
    if _newclass:varandnext_set = property(_spot.tgba_bdd_core_data_varandnext_set_get,_spot.tgba_bdd_core_data_varandnext_set_set)
    __swig_setmethods__["acc_set"] = _spot.tgba_bdd_core_data_acc_set_set
    __swig_getmethods__["acc_set"] = _spot.tgba_bdd_core_data_acc_set_get
    if _newclass:acc_set = property(_spot.tgba_bdd_core_data_acc_set_get,_spot.tgba_bdd_core_data_acc_set_set)
    __swig_setmethods__["notacc_set"] = _spot.tgba_bdd_core_data_notacc_set_set
    __swig_getmethods__["notacc_set"] = _spot.tgba_bdd_core_data_notacc_set_get
    if _newclass:notacc_set = property(_spot.tgba_bdd_core_data_notacc_set_get,_spot.tgba_bdd_core_data_notacc_set_set)
    __swig_setmethods__["negacc_set"] = _spot.tgba_bdd_core_data_negacc_set_set
    __swig_getmethods__["negacc_set"] = _spot.tgba_bdd_core_data_negacc_set_get
    if _newclass:negacc_set = property(_spot.tgba_bdd_core_data_negacc_set_get,_spot.tgba_bdd_core_data_negacc_set_set)
    __swig_setmethods__["dict"] = _spot.tgba_bdd_core_data_dict_set
    __swig_getmethods__["dict"] = _spot.tgba_bdd_core_data_dict_get
    if _newclass:dict = property(_spot.tgba_bdd_core_data_dict_get,_spot.tgba_bdd_core_data_dict_set)
    def __init__(self,*args):
        _swig_setattr(self, tgba_bdd_core_data, 'this', apply(_spot.new_tgba_bdd_core_data,args))
        _swig_setattr(self, tgba_bdd_core_data, 'thisown', 1)
    def declare_now_next(*args): return apply(_spot.tgba_bdd_core_data_declare_now_next,args)
    def declare_atomic_prop(*args): return apply(_spot.tgba_bdd_core_data_declare_atomic_prop,args)
    def declare_acceptance_condition(*args): return apply(_spot.tgba_bdd_core_data_declare_acceptance_condition,args)
    def __del__(self, destroy= _spot.delete_tgba_bdd_core_data):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C tgba_bdd_core_data instance at %s>" % (self.this,)

class tgba_bdd_core_dataPtr(tgba_bdd_core_data):
    def __init__(self,this):
        _swig_setattr(self, tgba_bdd_core_data, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_bdd_core_data, 'thisown', 0)
        _swig_setattr(self, tgba_bdd_core_data,self.__class__,tgba_bdd_core_data)
_spot.tgba_bdd_core_data_swigregister(tgba_bdd_core_dataPtr)

class tgba_succ_iterator_concrete(tgba_succ_iterator):
    __swig_setmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_succ_iterator_concrete, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_succ_iterator_concrete, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_succ_iterator_concrete, 'this', apply(_spot.new_tgba_succ_iterator_concrete,args))
        _swig_setattr(self, tgba_succ_iterator_concrete, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_succ_iterator_concrete):
        try:
            if self.thisown: destroy(self)
        except: pass
    def first(*args): return apply(_spot.tgba_succ_iterator_concrete_first,args)
    def next(*args): return apply(_spot.tgba_succ_iterator_concrete_next,args)
    def done(*args): return apply(_spot.tgba_succ_iterator_concrete_done,args)
    def current_state(*args): return apply(_spot.tgba_succ_iterator_concrete_current_state,args)
    def current_condition(*args): return apply(_spot.tgba_succ_iterator_concrete_current_condition,args)
    def current_acceptance_conditions(*args): return apply(_spot.tgba_succ_iterator_concrete_current_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_succ_iterator_concrete instance at %s>" % (self.this,)

class tgba_succ_iterator_concretePtr(tgba_succ_iterator_concrete):
    def __init__(self,this):
        _swig_setattr(self, tgba_succ_iterator_concrete, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_succ_iterator_concrete, 'thisown', 0)
        _swig_setattr(self, tgba_succ_iterator_concrete,self.__class__,tgba_succ_iterator_concrete)
_spot.tgba_succ_iterator_concrete_swigregister(tgba_succ_iterator_concretePtr)

class tgba_bdd_concrete(tgba):
    __swig_setmethods__ = {}
    for _s in [tgba]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_bdd_concrete, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_bdd_concrete, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_bdd_concrete, 'this', apply(_spot.new_tgba_bdd_concrete,args))
        _swig_setattr(self, tgba_bdd_concrete, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_bdd_concrete):
        try:
            if self.thisown: destroy(self)
        except: pass
    def set_init_state(*args): return apply(_spot.tgba_bdd_concrete_set_init_state,args)
    def get_init_state(*args): return apply(_spot.tgba_bdd_concrete_get_init_state,args)
    def get_init_bdd(*args): return apply(_spot.tgba_bdd_concrete_get_init_bdd,args)
    def succ_iter(*args): return apply(_spot.tgba_bdd_concrete_succ_iter,args)
    def format_state(*args): return apply(_spot.tgba_bdd_concrete_format_state,args)
    def get_dict(*args): return apply(_spot.tgba_bdd_concrete_get_dict,args)
    def get_core_data(*args): return apply(_spot.tgba_bdd_concrete_get_core_data,args)
    def all_acceptance_conditions(*args): return apply(_spot.tgba_bdd_concrete_all_acceptance_conditions,args)
    def neg_acceptance_conditions(*args): return apply(_spot.tgba_bdd_concrete_neg_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_bdd_concrete instance at %s>" % (self.this,)

class tgba_bdd_concretePtr(tgba_bdd_concrete):
    def __init__(self,this):
        _swig_setattr(self, tgba_bdd_concrete, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_bdd_concrete, 'thisown', 0)
        _swig_setattr(self, tgba_bdd_concrete,self.__class__,tgba_bdd_concrete)
_spot.tgba_bdd_concrete_swigregister(tgba_bdd_concretePtr)

class tgba_explicit(tgba):
    __swig_setmethods__ = {}
    for _s in [tgba]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_explicit, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_explicit, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_explicit, 'this', apply(_spot.new_tgba_explicit,args))
        _swig_setattr(self, tgba_explicit, 'thisown', 1)
    def set_init_state(*args): return apply(_spot.tgba_explicit_set_init_state,args)
    def create_transition(*args): return apply(_spot.tgba_explicit_create_transition,args)
    def add_condition(*args): return apply(_spot.tgba_explicit_add_condition,args)
    def add_conditions(*args): return apply(_spot.tgba_explicit_add_conditions,args)
    def declare_acceptance_condition(*args): return apply(_spot.tgba_explicit_declare_acceptance_condition,args)
    def has_acceptance_condition(*args): return apply(_spot.tgba_explicit_has_acceptance_condition,args)
    def add_acceptance_condition(*args): return apply(_spot.tgba_explicit_add_acceptance_condition,args)
    def add_acceptance_conditions(*args): return apply(_spot.tgba_explicit_add_acceptance_conditions,args)
    def complement_all_acceptance_conditions(*args): return apply(_spot.tgba_explicit_complement_all_acceptance_conditions,args)
    def __del__(self, destroy= _spot.delete_tgba_explicit):
        try:
            if self.thisown: destroy(self)
        except: pass
    def get_init_state(*args): return apply(_spot.tgba_explicit_get_init_state,args)
    def succ_iter(*args): return apply(_spot.tgba_explicit_succ_iter,args)
    def get_dict(*args): return apply(_spot.tgba_explicit_get_dict,args)
    def format_state(*args): return apply(_spot.tgba_explicit_format_state,args)
    def all_acceptance_conditions(*args): return apply(_spot.tgba_explicit_all_acceptance_conditions,args)
    def neg_acceptance_conditions(*args): return apply(_spot.tgba_explicit_neg_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_explicit instance at %s>" % (self.this,)

class tgba_explicitPtr(tgba_explicit):
    def __init__(self,this):
        _swig_setattr(self, tgba_explicit, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_explicit, 'thisown', 0)
        _swig_setattr(self, tgba_explicit,self.__class__,tgba_explicit)
_spot.tgba_explicit_swigregister(tgba_explicitPtr)

class state_explicit(state):
    __swig_setmethods__ = {}
    for _s in [state]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_explicit, name, value)
    __swig_getmethods__ = {}
    for _s in [state]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, state_explicit, name)
    def __init__(self,*args):
        _swig_setattr(self, state_explicit, 'this', apply(_spot.new_state_explicit,args))
        _swig_setattr(self, state_explicit, 'thisown', 1)
    def compare(*args): return apply(_spot.state_explicit_compare,args)
    def hash(*args): return apply(_spot.state_explicit_hash,args)
    def clone(*args): return apply(_spot.state_explicit_clone,args)
    def __del__(self, destroy= _spot.delete_state_explicit):
        try:
            if self.thisown: destroy(self)
        except: pass
    def get_state(*args): return apply(_spot.state_explicit_get_state,args)
    def __repr__(self):
        return "<C state_explicit instance at %s>" % (self.this,)

class state_explicitPtr(state_explicit):
    def __init__(self,this):
        _swig_setattr(self, state_explicit, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_explicit, 'thisown', 0)
        _swig_setattr(self, state_explicit,self.__class__,state_explicit)
_spot.state_explicit_swigregister(state_explicitPtr)

class tgba_explicit_succ_iterator(tgba_succ_iterator):
    __swig_setmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_explicit_succ_iterator, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_explicit_succ_iterator, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_explicit_succ_iterator, 'this', apply(_spot.new_tgba_explicit_succ_iterator,args))
        _swig_setattr(self, tgba_explicit_succ_iterator, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_explicit_succ_iterator):
        try:
            if self.thisown: destroy(self)
        except: pass
    def first(*args): return apply(_spot.tgba_explicit_succ_iterator_first,args)
    def next(*args): return apply(_spot.tgba_explicit_succ_iterator_next,args)
    def done(*args): return apply(_spot.tgba_explicit_succ_iterator_done,args)
    def current_state(*args): return apply(_spot.tgba_explicit_succ_iterator_current_state,args)
    def current_condition(*args): return apply(_spot.tgba_explicit_succ_iterator_current_condition,args)
    def current_acceptance_conditions(*args): return apply(_spot.tgba_explicit_succ_iterator_current_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_explicit_succ_iterator instance at %s>" % (self.this,)

class tgba_explicit_succ_iteratorPtr(tgba_explicit_succ_iterator):
    def __init__(self,this):
        _swig_setattr(self, tgba_explicit_succ_iterator, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_explicit_succ_iterator, 'thisown', 0)
        _swig_setattr(self, tgba_explicit_succ_iterator,self.__class__,tgba_explicit_succ_iterator)
_spot.tgba_explicit_succ_iterator_swigregister(tgba_explicit_succ_iteratorPtr)

class state_product(state):
    __swig_setmethods__ = {}
    for _s in [state]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, state_product, name, value)
    __swig_getmethods__ = {}
    for _s in [state]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, state_product, name)
    def __init__(self,*args):
        _swig_setattr(self, state_product, 'this', apply(_spot.new_state_product,args))
        _swig_setattr(self, state_product, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_state_product):
        try:
            if self.thisown: destroy(self)
        except: pass
    def left(*args): return apply(_spot.state_product_left,args)
    def right(*args): return apply(_spot.state_product_right,args)
    def compare(*args): return apply(_spot.state_product_compare,args)
    def hash(*args): return apply(_spot.state_product_hash,args)
    def clone(*args): return apply(_spot.state_product_clone,args)
    def __repr__(self):
        return "<C state_product instance at %s>" % (self.this,)

class state_productPtr(state_product):
    def __init__(self,this):
        _swig_setattr(self, state_product, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, state_product, 'thisown', 0)
        _swig_setattr(self, state_product,self.__class__,state_product)
_spot.state_product_swigregister(state_productPtr)

class tgba_succ_iterator_product(tgba_succ_iterator):
    __swig_setmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_succ_iterator_product, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba_succ_iterator]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_succ_iterator_product, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_succ_iterator_product, 'this', apply(_spot.new_tgba_succ_iterator_product,args))
        _swig_setattr(self, tgba_succ_iterator_product, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_succ_iterator_product):
        try:
            if self.thisown: destroy(self)
        except: pass
    def first(*args): return apply(_spot.tgba_succ_iterator_product_first,args)
    def next(*args): return apply(_spot.tgba_succ_iterator_product_next,args)
    def done(*args): return apply(_spot.tgba_succ_iterator_product_done,args)
    def current_state(*args): return apply(_spot.tgba_succ_iterator_product_current_state,args)
    def current_condition(*args): return apply(_spot.tgba_succ_iterator_product_current_condition,args)
    def current_acceptance_conditions(*args): return apply(_spot.tgba_succ_iterator_product_current_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_succ_iterator_product instance at %s>" % (self.this,)

class tgba_succ_iterator_productPtr(tgba_succ_iterator_product):
    def __init__(self,this):
        _swig_setattr(self, tgba_succ_iterator_product, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_succ_iterator_product, 'thisown', 0)
        _swig_setattr(self, tgba_succ_iterator_product,self.__class__,tgba_succ_iterator_product)
_spot.tgba_succ_iterator_product_swigregister(tgba_succ_iterator_productPtr)

class tgba_product(tgba):
    __swig_setmethods__ = {}
    for _s in [tgba]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_product, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_product, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_product, 'this', apply(_spot.new_tgba_product,args))
        _swig_setattr(self, tgba_product, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_product):
        try:
            if self.thisown: destroy(self)
        except: pass
    def get_init_state(*args): return apply(_spot.tgba_product_get_init_state,args)
    def succ_iter(*args): return apply(_spot.tgba_product_succ_iter,args)
    def get_dict(*args): return apply(_spot.tgba_product_get_dict,args)
    def format_state(*args): return apply(_spot.tgba_product_format_state,args)
    def project_state(*args): return apply(_spot.tgba_product_project_state,args)
    def all_acceptance_conditions(*args): return apply(_spot.tgba_product_all_acceptance_conditions,args)
    def neg_acceptance_conditions(*args): return apply(_spot.tgba_product_neg_acceptance_conditions,args)
    def __repr__(self):
        return "<C tgba_product instance at %s>" % (self.this,)

class tgba_productPtr(tgba_product):
    def __init__(self,this):
        _swig_setattr(self, tgba_product, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_product, 'thisown', 0)
        _swig_setattr(self, tgba_product,self.__class__,tgba_product)
_spot.tgba_product_swigregister(tgba_productPtr)

class tgba_tba_proxy(tgba):
    __swig_setmethods__ = {}
    for _s in [tgba]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, tgba_tba_proxy, name, value)
    __swig_getmethods__ = {}
    for _s in [tgba]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, tgba_tba_proxy, name)
    def __init__(self,*args):
        _swig_setattr(self, tgba_tba_proxy, 'this', apply(_spot.new_tgba_tba_proxy,args))
        _swig_setattr(self, tgba_tba_proxy, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_tgba_tba_proxy):
        try:
            if self.thisown: destroy(self)
        except: pass
    def get_init_state(*args): return apply(_spot.tgba_tba_proxy_get_init_state,args)
    def succ_iter(*args): return apply(_spot.tgba_tba_proxy_succ_iter,args)
    def get_dict(*args): return apply(_spot.tgba_tba_proxy_get_dict,args)
    def format_state(*args): return apply(_spot.tgba_tba_proxy_format_state,args)
    def project_state(*args): return apply(_spot.tgba_tba_proxy_project_state,args)
    def all_acceptance_conditions(*args): return apply(_spot.tgba_tba_proxy_all_acceptance_conditions,args)
    def neg_acceptance_conditions(*args): return apply(_spot.tgba_tba_proxy_neg_acceptance_conditions,args)
    def state_is_accepting(*args): return apply(_spot.tgba_tba_proxy_state_is_accepting,args)
    def __repr__(self):
        return "<C tgba_tba_proxy instance at %s>" % (self.this,)

class tgba_tba_proxyPtr(tgba_tba_proxy):
    def __init__(self,this):
        _swig_setattr(self, tgba_tba_proxy, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, tgba_tba_proxy, 'thisown', 0)
        _swig_setattr(self, tgba_tba_proxy,self.__class__,tgba_tba_proxy)
_spot.tgba_tba_proxy_swigregister(tgba_tba_proxyPtr)

ltl_to_tgba_lacim = _spot.ltl_to_tgba_lacim

ltl_to_tgba_fm = _spot.ltl_to_tgba_fm

dotty_reachable = _spot.dotty_reachable

lbtt_reachable = _spot.lbtt_reachable

class magic_search(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, magic_search, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, magic_search, name)
    def __init__(self,*args):
        _swig_setattr(self, magic_search, 'this', apply(_spot.new_magic_search,args))
        _swig_setattr(self, magic_search, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_magic_search):
        try:
            if self.thisown: destroy(self)
        except: pass
    def check(*args): return apply(_spot.magic_search_check,args)
    def print_result(*args): return apply(_spot.magic_search_print_result,args)
    def __repr__(self):
        return "<C magic_search instance at %s>" % (self.this,)

class magic_searchPtr(magic_search):
    def __init__(self,this):
        _swig_setattr(self, magic_search, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, magic_search, 'thisown', 0)
        _swig_setattr(self, magic_search,self.__class__,magic_search)
_spot.magic_search_swigregister(magic_searchPtr)

tgba_save_reachable = _spot.tgba_save_reachable

class ostream(_object):
    __swig_setmethods__ = {}
    __setattr__ = lambda self, name, value: _swig_setattr(self, ostream, name, value)
    __swig_getmethods__ = {}
    __getattr__ = lambda self, name: _swig_getattr(self, ostream, name)
    def __init__(self): raise RuntimeError, "No constructor defined"
    def __repr__(self):
        return "<C ostream instance at %s>" % (self.this,)

class ostreamPtr(ostream):
    def __init__(self,this):
        _swig_setattr(self, ostream, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, ostream, 'thisown', 0)
        _swig_setattr(self, ostream,self.__class__,ostream)
_spot.ostream_swigregister(ostreamPtr)

class ofstream(ostream):
    __swig_setmethods__ = {}
    for _s in [ostream]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, ofstream, name, value)
    __swig_getmethods__ = {}
    for _s in [ostream]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, ofstream, name)
    def __init__(self,*args):
        _swig_setattr(self, ofstream, 'this', apply(_spot.new_ofstream,args))
        _swig_setattr(self, ofstream, 'thisown', 1)
    def __del__(self, destroy= _spot.delete_ofstream):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C ofstream instance at %s>" % (self.this,)

class ofstreamPtr(ofstream):
    def __init__(self,this):
        _swig_setattr(self, ofstream, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, ofstream, 'thisown', 0)
        _swig_setattr(self, ofstream,self.__class__,ofstream)
_spot.ofstream_swigregister(ofstreamPtr)

class ostringstream(ostream):
    __swig_setmethods__ = {}
    for _s in [ostream]: __swig_setmethods__.update(_s.__swig_setmethods__)
    __setattr__ = lambda self, name, value: _swig_setattr(self, ostringstream, name, value)
    __swig_getmethods__ = {}
    for _s in [ostream]: __swig_getmethods__.update(_s.__swig_getmethods__)
    __getattr__ = lambda self, name: _swig_getattr(self, ostringstream, name)
    def __init__(self,*args):
        _swig_setattr(self, ostringstream, 'this', apply(_spot.new_ostringstream,args))
        _swig_setattr(self, ostringstream, 'thisown', 1)
    def str(*args): return apply(_spot.ostringstream_str,args)
    def __del__(self, destroy= _spot.delete_ostringstream):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C ostringstream instance at %s>" % (self.this,)

class ostringstreamPtr(ostringstream):
    def __init__(self,this):
        _swig_setattr(self, ostringstream, 'this', this)
        if not hasattr(self,"thisown"): _swig_setattr(self, ostringstream, 'thisown', 0)
        _swig_setattr(self, ostringstream,self.__class__,ostringstream)
_spot.ostringstream_swigregister(ostringstreamPtr)

empty_parse_error_list = _spot.empty_parse_error_list

get_cout = _spot.get_cout

get_cerr = _spot.get_cerr

print_on = _spot.print_on


