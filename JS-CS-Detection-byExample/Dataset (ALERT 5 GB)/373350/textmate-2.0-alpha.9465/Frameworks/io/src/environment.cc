#include "environment.h"
#include "path.h"
#include <cf/cf.h>
#include <regexp/format_string.h>
#include <regexp/glob.h>
#include <text/parse.h>
#include <oak/oak.h>
#include <crt_externs.h>

namespace oak
{
	std::map<std::string, std::string> setup_basic_environment ()
	{
		std::string whitelistStr = "Apple_*:COMMAND_MODE:DIALOG*:SHELL:SHLVL:SSH_AUTH_SOCK:__CF_USER_TEXT_ENCODING";
		if(CFStringRef userWhitelist = (CFStringRef)CFPreferencesCopyAppValue(CFSTR("environmentWhitelist"), kCFPreferencesCurrentApplication))
		{
			if(CFGetTypeID(userWhitelist) == CFStringGetTypeID())
				whitelistStr = format_string::expand(cf::to_s(userWhitelist), std::map<std::string, std::string>{ { "default", whitelistStr } });
			CFRelease(userWhitelist);
		}

		std::set<std::string> whitelistSet;
		std::vector<path::glob_t> whitelistGlobs;
		for(auto str : text::split(whitelistStr, ":"))
		{
			if(str.find("*") != std::string::npos)
					whitelistGlobs.push_back(str);
			else	whitelistSet.insert(str);
		}

		std::map<std::string, std::string> res;

		char*** envPtr = _NSGetEnviron();
		for(char** pair = *envPtr; pair && *pair; ++pair)
		{
			char* value = strchr(*pair, '=');
			if(value && *value == '=')
			{
				std::string const key = std::string(*pair, value);
				if(whitelistSet.find(key) != whitelistSet.end() || std::any_of(whitelistGlobs.begin(), whitelistGlobs.end(), [&key](path::glob_t const& glob){ return glob.does_match(key); }))
					res[key] = value + 1;
			}
		}

		passwd* entry = path::passwd_entry();

		int mib[2] = { CTL_USER, USER_CS_PATH };
		size_t len = 0;
		sysctl(mib, 2, NULL, &len, NULL, 0);
		char* path = new char[len];
		sysctl(mib, 2, path, &len, NULL, 0);

		res.insert(std::make_pair("HOME",    entry->pw_dir));
		res.insert(std::make_pair("PATH",    path));
		res.insert(std::make_pair("TMPDIR",  path::temp()));
		res.insert(std::make_pair("LOGNAME", entry->pw_name));
		res.insert(std::make_pair("USER",    entry->pw_name));

		res.insert(std::make_pair("TM_APP_IDENTIFIER", cf::to_s(CFBundleGetIdentifier(CFBundleGetMainBundle()))));
		res.insert(std::make_pair("TM_FULLNAME",       entry->pw_gecos ?: "John Doe"));
		res.insert(std::make_pair("TM_PID",            std::to_string(getpid())));

		return res;
	}

	std::map<std::string, std::string>& rw_environment ()
	{
		static std::map<std::string, std::string>* environment = new std::map<std::string, std::string>(setup_basic_environment());
		return *environment;
	}

	std::map<std::string, std::string> const& basic_environment ()
	{
		return rw_environment();
	}

	void set_basic_environment (std::map<std::string, std::string> const& newEnvironment)
	{
		rw_environment() = newEnvironment;
	}

} /* io */