
#include <chaiscript/chaiscript.hpp>
#include <chaiscript/dispatchkit/bootstrap_stl.hpp>
#include <list>
#include <string>

// MSVC doesn't like that we are using C++ return types from our C declared module
// but this is the best way to do it for cross platform compatibility
#ifdef CHAISCRIPT_MSVC
#pragma warning(push)
#pragma warning(disable : 4190)
#endif

CHAISCRIPT_MODULE_EXPORT chaiscript::ModulePtr create_chaiscript_module_stl_extra()
{
  return chaiscript::bootstrap::standard_library::list_type<std::list<chaiscript::Boxed_Value> >("List");
}


#ifdef CHAISCRIPT_MSVC
#pragma warning(pop)
#endif
