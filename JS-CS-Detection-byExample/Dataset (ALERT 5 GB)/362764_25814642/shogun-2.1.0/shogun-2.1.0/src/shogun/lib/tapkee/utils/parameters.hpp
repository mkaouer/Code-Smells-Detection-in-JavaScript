/* This software is distributed under BSD 3-clause license (see LICENSE file).
 *
 * Copyright (c) 2012-2013 Sergey Lisitsyn
 */

#ifndef TAPKEE_PARAMETERS_H_
#define TAPKEE_PARAMETERS_H_

// pure magic, for the brave souls 
#define VA_NUM_ARGS(...) VA_NUM_ARGS_IMPL_((__VA_ARGS__, 5,4,3,2,1))
#define VA_NUM_ARGS_IMPL_(tuple) VA_NUM_ARGS_IMPL tuple
#define VA_NUM_ARGS_IMPL(_1,_2,_3,_4,_5,N,...) N
#define MACRO_DISPATCHER(macro, ...) MACRO_DISPATCHER_(macro, VA_NUM_ARGS(__VA_ARGS__))
#define MACRO_DISPATCHER_(macro, nargs) MACRO_DISPATCHER__(macro, nargs)
#define MACRO_DISPATCHER__(macro, nargs) MACRO_DISPATCHER___(macro, nargs)
#define MACRO_DISPATCHER___(macro, nargs) macro ## nargs

// parameter macro definition
#define PARAMETER(...) MACRO_DISPATCHER(PARAMETER, __VA_ARGS__)(__VA_ARGS__)
#define PARAMETER3(TYPE,NAME,CODE) PARAMETER_IMPL(TYPE,NAME,CODE,NO_CHECK)
#define PARAMETER4(TYPE,NAME,CODE,CHECKER) PARAMETER_IMPL(TYPE,NAME,CODE,CHECKER)

// implementation of parameter macro
#define PARAMETER_IMPL(TYPE,NAME,CODE,CHECKER)                                                         \
	if (!parameters.count(CODE))                                                                          \
		throw missed_parameter_error("No "#NAME" ("#TYPE") parameter set. Should be in map as "#CODE); \
	TYPE NAME;                                                                                         \
	try                                                                                                \
	{                                                                                                  \
		NAME = parameters[CODE].cast<TYPE>();                                                             \
	}                                                                                                  \
	catch (tapkee::anyimpl::bad_any_cast&)                                                             \
	{                                                                                                  \
		throw wrong_parameter_type_error("Wrong type for parameter "#NAME". Should be "#TYPE);         \
	}                                                                                                  \
	if (!CHECKER)                                                                                      \
		throw wrong_parameter_error("Check failed "#CHECKER);                                          \
	else                                                                                               \
	{                                                                                                  \
		if (LoggingSingleton::instance().is_debug_enabled())                                           \
		{                                                                                              \
			std::stringstream ss;                                                                      \
			ss << "parameter "#TYPE" "#NAME" is set to " << NAME;                                      \
			LoggingSingleton::instance().message_debug(ss.str());                                      \
		}                                                                                              \
	}

// checkers
#define NO_CHECK true
#define IN_RANGE(VARIABLE,LOWER,UPPER) \
	((VARIABLE>=LOWER) && (VARIABLE<UPPER))
#define NOT(VARIABLE,VALUE) \
	(VARIABLE!=VALUE)
#define POSITIVE(VARIABLE) \
	(VARIABLE>0)
#define EXACTLY(VARIABLE,VALUE) \
	(VARIABLE==VALUE)
#define NON_NEGATIVE(VARIABLE) \
	(VARIABLE>=0)

#endif
