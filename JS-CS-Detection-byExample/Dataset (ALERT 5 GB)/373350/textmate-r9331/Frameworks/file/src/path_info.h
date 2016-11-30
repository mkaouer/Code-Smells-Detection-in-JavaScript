#ifndef FILE_PATH_INFO_H_ZBW25L1B
#define FILE_PATH_INFO_H_ZBW25L1B

#include <oak/misc.h>

namespace file
{
	PUBLIC std::string path_attributes (std::string const& path, std::string const& dir = NULL_STR);
	std::map<std::string, std::string> variables (std::string const& path);

} /* file */

#endif /* end of include guard: FILE_PATH_INFO_H_ZBW25L1B */
