#include "sw_update.h"
#include <network/download_tbz.h>
#include <plist/plist.h>
#include <io/entries.h>
#include <text/decode.h>
#include <OakSystem/application.h>

static bool run_auth_command (AuthorizationRef& auth, std::string const& cmd, ...)
{
	if(!auth && AuthorizationCreate(NULL, kAuthorizationEmptyEnvironment, kAuthorizationFlagDefaults, &auth) != errAuthorizationSuccess)
		return false;

	std::vector<char*> args;

	va_list ap;
	va_start(ap, cmd);
	char* arg = NULL;
	while((arg = va_arg(ap, char*)) && *arg)
		args.push_back(arg);
	va_end(ap);

	args.push_back(NULL);

	bool res = false;
	if(AuthorizationExecuteWithPrivileges(auth, cmd.c_str(), kAuthorizationFlagDefaults, &args[0], NULL) == errAuthorizationSuccess)
	{
		int status;
		int pid = wait(&status);
		if(pid != -1 && WIFEXITED(status) && WEXITSTATUS(status) == 0)
				res = true;
		else	errno = WEXITSTATUS(status);
	}
	else
	{
		errno = EPERM;
	}
	return res;
}

static bool mv_path (std::string const& src, std::string const& dst, AuthorizationRef& auth)
{
	if(rename(src.c_str(), dst.c_str()) == 0)
		return true;
	else if(errno == EXDEV && copyfile(src.c_str(), dst.c_str(), NULL, COPYFILE_ALL | COPYFILE_RECURSIVE | COPYFILE_NOFOLLOW_SRC) == 0)
		return true;
	else if(errno == EACCES || errno == EPERM)
	{
		if(run_auth_command(auth, "/bin/mv", src.c_str(), dst.c_str(), NULL))
			return true;
		perror(("/bin/mv " + src + " " + dst).c_str());
	}
	return false;
}

static bool rm_file (std::string const& path, AuthorizationRef& auth)
{
	if(unlink(path.c_str()) == 0)
		return true;
	else if(errno == EACCES || errno == EPERM)
	{
		if(run_auth_command(auth, "/bin/rm", path.c_str(), NULL))
			return true;
		perror(("/bin/rm " + path).c_str());
	}
	else
	{
		perror(("unlink " + path).c_str());
	}
	return false;
}

static bool rm_dir (std::string const& path, AuthorizationRef& auth)
{
	citerate(it, path::entries(path))
	{
		std::string const& newPath = path::join(path, (*it)->d_name);
		if((*it)->d_type == DT_DIR)
		{
			if(!rm_dir(newPath, auth))
				return false;
		}
		else
		{
			if(!rm_file(newPath, auth))
				return false;
		}
	}

	if(rmdir(path.c_str()) == 0)
		return true;
	else if(errno == EACCES || errno == EPERM)
	{
		if(run_auth_command(auth, "/bin/rmdir", path.c_str(), NULL))
			return true;
		perror(("/bin/rmdir " + path).c_str());
	}
	else
	{
		perror(("rmdir " + path).c_str());
	}
	return false;
}

namespace sw_update
{
	version_info_t download_info (std::string const& url, std::string* error)
	{
		network::save_t archiver;
		long res = network::download(network::request_t(url, &archiver, NULL), error);
		if(res == 200)
		{
			plist::dictionary_t const& plist = plist::load(archiver.path);

			std::string versionString, archive;
			if(plist::get_key_path(plist, "revision", versionString) && plist::get_key_path(plist, "url", archive))
			{
				long version = strtol(versionString.c_str(), NULL, 10);
				return version_info_t(version, archive);
			}
			else if(error)
			{
				*error = "Unexpected body received from server.";
			}
		}
		else if(res != 0 && error)
		{
			*error = text::format("Server response: %ld (expected 200)", res);
		}
		return version_info_t();
	}

	std::string download_update (std::string const& url, key_chain_t const& keyChain, std::string* error, double* progress, bool const* stopFlag)
	{
		std::string dummy;
		std::string const path = path::join(path::join(path::home(), "Library/Caches/com.macromates.TextMate"), path::name(url));
		return network::download_tbz(url, keyChain, path, error ? *error : dummy, progress, 0, 1, stopFlag);
	}

	std::string install_update (std::string const& src)
	{
		std::string const dst        = oak::application_t::path();
		std::string const oldVersion = oak::application_t::revision();
		std::string const backup     = path::strip_extension(dst) + " " + oldVersion + path::extension(dst);
		std::string const appExe     = path::join("Contents/MacOS", oak::application_t::name());
		std::string const srcExe     = path::join(src, appExe);
		std::string const dstExe     = path::join(dst, appExe);

		AuthorizationRef auth = NULL;
		if(!path::exists(srcExe))
			return "New application is broken (TMPDIR sweeper?).";
		else if(!mv_path(dst, backup, auth))
			return "Error moving current version.";
		else if(!mv_path(src, dst, auth))
			return "Error installing new version.";
		else if(!path::exists(dstExe))
			return "Installed application is broken.";
		else if(!rm_dir(backup, auth))
			return "Error removing old version.";

		return NULL_STR;
	}
};
