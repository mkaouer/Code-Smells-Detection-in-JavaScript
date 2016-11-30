#ifndef PLIST_STL_H_O6LA7E77
#define PLIST_STL_H_O6LA7E77

#include <oak/oak.h>

namespace plist
{
	template <typename T>
	any_t to_plist (T const& value)
	{
		return value;
	}

	template <typename T>
	array_t to_plist (std::vector<T> const& v)
	{
		array_t res;
		iterate(it, v)
			res.push_back(to_plist(*it));
		return res;
	}

	template <typename K, typename V>
	dictionary_t to_plist (std::map<K, V> const& map)
	{
		dictionary_t res;
		iterate(it, map)
			res.insert(std::make_pair(it->first, to_plist(it->second)));
		return res;
	}

} /* plist */

#endif /* end of include guard: PLIST_STL_H_O6LA7E77 */
