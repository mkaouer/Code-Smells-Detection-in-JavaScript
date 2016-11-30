#include "api.h"
#include <oak/oak.h>
#include <text/parse.h>
#include <io/io.h>
#include <oak/debug.h>

OAK_DEBUG_VAR(SCM_Hg);

static scm::status::type parse_status_flag (char flag)
{
	static struct { scm::status::type constant; char flag; } const StatusLetterConversionMap[] =
	{
		{ scm::status::unversioned, '?' },
		{ scm::status::ignored,     'I' },
		{ scm::status::versioned,   'C' },
		{ scm::status::modified,    'M' },
		{ scm::status::added,       'A' },
		{ scm::status::deleted,     'R' },
		{ scm::status::deleted,     '!' }, // missing on disk
	};

	for(size_t i = 0; i < sizeofA(StatusLetterConversionMap); ++i)
	{
		if(flag == StatusLetterConversionMap[i].flag)
			return StatusLetterConversionMap[i].constant;
	}

	ASSERT_EQ(flag, '\0'); // we use ‘flag’ in the assertion to output the unrecognized status flag
	return scm::status::none;
}

static void parse_status_output (scm::status_map_t& entries, std::string const& output)
{
	if(output == NULL_STR)
		return;

	std::vector<std::string> v = text::split(output, std::string(1, '\0'));
	ASSERT_EQ(v.back(), "");
	v.pop_back();
	iterate(line, v)
	{
		ASSERT_EQ((*line)[1], ' ');
		entries[line->substr(2)] = parse_status_flag((*line)[0]);
	}
}

static void collect_all_paths (std::string const& hg, scm::status_map_t& entries, std::string const& dir)
{
	ASSERT_NE(hg, NULL_STR);

	std::map<std::string, std::string> env = oak::basic_environment();
	env["PWD"] = dir;

	parse_status_output(entries, io::exec(env, hg, "status", "--all", "-0", NULL));
}

namespace scm
{
	struct hg_driver_t : driver_t
	{
		hg_driver_t () : driver_t("hg", "%s/.hg", "hg") { }

		std::string branch_name (std::string const& wcPath) const
		{
			if(executable() == NULL_STR)
				return NULL_STR;

			std::map<std::string, std::string> env = oak::basic_environment();
			env["PWD"] = wcPath;

			std::string branchName = io::exec(env, executable(), "branch", NULL);
			return branchName.substr(0, branchName.find("\n"));
		}

		status_map_t status (std::string const& wcPath) const
		{
			D(DBF_SCM_Hg, bug("%s\n", wcPath.c_str()););
			if(executable() == NULL_STR)
				return status_map_t();

			status_map_t relativePaths, res;
			collect_all_paths(executable(), relativePaths, wcPath);
			iterate(pair, relativePaths)
				res.insert(std::make_pair(path::join(wcPath, pair->first), pair->second));
			return res;
		}
	};

	driver_t* hg_driver () { return new hg_driver_t; }
}
