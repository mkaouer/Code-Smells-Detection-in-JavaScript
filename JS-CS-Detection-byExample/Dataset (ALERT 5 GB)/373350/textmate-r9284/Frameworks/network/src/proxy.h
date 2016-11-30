#ifndef PROXY_H_S8ZWZPU8
#define PROXY_H_S8ZWZPU8

#include <oak/misc.h>

struct proxy_settings_t
{
	proxy_settings_t (bool enabled = false, std::string const& server = NULL_STR, long port = 0, std::string const& user = NULL_STR, std::string const& password = NULL_STR) : enabled(enabled), server(server), port(port), user(user), password(password) { }
	EXPLICIT operator bool () const { return enabled; }

	bool enabled;
	std::string server;
	long port;
	std::string user;
	std::string password;
};

PUBLIC proxy_settings_t get_proxy_settings (std::string const& url);

#endif /* end of include guard: PROXY_H_S8ZWZPU8 */
