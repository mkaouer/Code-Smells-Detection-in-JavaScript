// This file is distributed under the BSD License.
// See "license.txt" for details.
// Copyright 2009, Jonathan Turner (jturner@minnow-lang.org)
// and Jason Turner (lefticus@gmail.com)
// http://www.chaiscript.com

#include <boost/preprocessor.hpp>

#ifndef  BOOST_PP_IS_ITERATING
#ifndef __register_function_hpp__
#define __register_function_hpp__

#include "dispatchkit.hpp"
#include <boost/function.hpp>
#include <boost/bind.hpp>

namespace chaiscript
{
  namespace detail
  {
    /**
     * Helper function for register_member function
     */
    template<typename T, typename Class>
      T &get_member(T Class::* m, Class *obj)
      {
        return (obj->*m);
      }

    template<typename T>
      Proxy_Function fun_helper(const boost::function<T> &f)
      {
        return Proxy_Function(new Proxy_Function_Impl<T>(f));
      }

    /**
     * Automatically create a get_member helper function for an object 
     * to allow for runtime dispatched access to public data members
     * for example, the case of std::pair<>::first and std::pair<>::second
     */
    template<typename T, typename Class>
      Proxy_Function fun_helper(T Class::* m)
      {
        return fun_helper(boost::function<T& (Class *)>(boost::bind(&detail::get_member<T, Class>, m, _1)));
      }
  }
}

#define BOOST_PP_ITERATION_LIMITS ( 0, 10 )
#define BOOST_PP_FILENAME_1 <chaiscript/dispatchkit/register_function.hpp>
#include BOOST_PP_ITERATE()

namespace chaiscript
{
  template<typename T>
    Proxy_Function fun(T t)
    {
      return detail::fun_helper(t);
    }
}


# endif
#else
# define n BOOST_PP_ITERATION()

namespace chaiscript
{
  namespace detail
  {
    /**
     * Register a global function of n parameters with name 
     */
    template<typename Ret BOOST_PP_COMMA_IF(n) BOOST_PP_ENUM_PARAMS(n, typename Param)>
      Proxy_Function fun_helper(Ret (*f)(BOOST_PP_ENUM_PARAMS(n, Param)))
      {
        return fun_helper(boost::function<Ret (BOOST_PP_ENUM_PARAMS(n, Param))>(f));
      }

    /**
     * Register a class method of n parameters with name 
     */
    template<typename Ret, typename Class BOOST_PP_COMMA_IF(n) BOOST_PP_ENUM_PARAMS(n, typename Param)>
      Proxy_Function fun_helper(Ret (Class::*f)(BOOST_PP_ENUM_PARAMS(n, Param)))
      {
        return fun_helper(boost::function<Ret (Class* BOOST_PP_COMMA_IF(n) BOOST_PP_ENUM_PARAMS(n, Param))>(f));
      }

    /**
     * Register a const class method of n parameters with name 
     */
    template<typename Ret, typename Class BOOST_PP_COMMA_IF(n) BOOST_PP_ENUM_PARAMS(n, typename Param)>
      Proxy_Function fun_helper(Ret (Class::*f)(BOOST_PP_ENUM_PARAMS(n, Param))const)
      {
        return fun_helper(boost::function<Ret (const Class* BOOST_PP_COMMA_IF(n) BOOST_PP_ENUM_PARAMS(n, Param))>(f));
      }

  }
}

#endif

