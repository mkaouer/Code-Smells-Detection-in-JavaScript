// This file is distributed under the BSD License.
// See "license.txt" for details.
// Copyright 2009-2010, Jonathan Turner (jonathan@emptycrate.com)
// and Jason Turner (jason@emptycrate.com)
// http://www.chaiscript.com

#ifndef CHAISCRIPT_FUNCTION_CALL_HPP_
#define CHAISCRIPT_FUNCTION_CALL_HPP_

#include <string>
#include <vector>
#include "proxy_functions.hpp"
#include "function_call_detail.hpp"


#include <iostream>

namespace chaiscript
{
  namespace dispatch
  {
    /**
     * Build a function caller that knows how to dispatch on a set of functions
     * example: 
     * std::function<void (int)> f = 
     *      build_function_caller(dispatchkit.get_function("print"));
     * \returns A std::function object for dispatching
     * \param[in] funcs the set of functions to dispatch on.
     */
    template<typename FunctionType>
      std::function<FunctionType>
      functor(const std::vector<Const_Proxy_Function> &funcs)
      {
        FunctionType *p=0;
        return detail::build_function_caller_helper(p, funcs);
      }

    /**
     * Build a function caller for a particular Proxy_Function object
     * useful in the case that a function is being pass out from scripting back
     * into code
     * example: 
     * void my_function(Proxy_Function f)
     * {
     *   std::function<void (int)> local_f = 
     *      build_function_caller(f);
     * }
     * \returns A std::function object for dispatching
     * \param[in] func A function to execute.
     */
    template<typename FunctionType>
      std::function<FunctionType>
      functor(Const_Proxy_Function func)
      {
        std::vector<Const_Proxy_Function> funcs;
        funcs.push_back(func);
        return functor<FunctionType>(funcs);
      }

    /**
     * Helper for automatically unboxing a Boxed_Value that contains a function object
     * and creating a typesafe C++ function caller from it.
     */
    template<typename FunctionType>
      std::function<FunctionType>
      functor(const Boxed_Value &bv)
      {
        return functor<FunctionType>(boxed_cast<Const_Proxy_Function >(bv));
      }
  }

  namespace detail{
    /**
     * Cast helper to handle automatic casting to const std::function &
     */
    template<typename Signature>
      struct Cast_Helper<const std::function<Signature> &>
      {
        typedef std::function<Signature> Result_Type;

        static Result_Type cast(const Boxed_Value &ob)
        {
          if (ob.get_type_info().bare_equal(user_type<Const_Proxy_Function>()))
          {
            return dispatch::functor<Signature>(ob);
          } else {
            return Cast_Helper_Inner<const std::function<Signature> &>::cast(ob);
          }
        }
      };

    /**
     * Cast helper to handle automatic casting to std::function
     */
    template<typename Signature>
      struct Cast_Helper<std::function<Signature> >
      {
        typedef std::function<Signature> Result_Type;

        static Result_Type cast(const Boxed_Value &ob)
        {
          if (ob.get_type_info().bare_equal(user_type<Const_Proxy_Function>()))
          {
            return dispatch::functor<Signature>(ob);
          } else {
            return Cast_Helper_Inner<std::function<Signature> >::cast(ob);
          }
        }
      };

    /**
     * Cast helper to handle automatic casting to const std::function
     */
    template<typename Signature>
      struct Cast_Helper<const std::function<Signature> >
      {
        typedef std::function<Signature> Result_Type;

        static Result_Type cast(const Boxed_Value &ob)
        {
          if (ob.get_type_info().bare_equal(user_type<Const_Proxy_Function>()))
          {
            return dispatch::functor<Signature>(ob);
          } else {
            return Cast_Helper_Inner<const std::function<Signature> >::cast(ob);
          }
        }
      };
  }
}

#endif

