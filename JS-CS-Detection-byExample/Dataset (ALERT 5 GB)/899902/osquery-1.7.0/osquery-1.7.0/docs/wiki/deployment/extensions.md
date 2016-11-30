osquery supports proprietary tables, config plugins, and logger plugins built in C++ (or languages other than C++) through a Thrift-based extensions API. This is helpful if your enterprise or integration uses an internal method for configuration or log collection. You can write your "extension" internally, and ask osquery to depend on the plugins it exposes. To make deployment and management of extensions simple, osqueryd may "autoload", or subprocess, these extension binaries and monitor their performance.

If you are interested in writing extensions please read the [SDK and Extensions](../development/osquery-sdk.md) development article. That wiki article describes the Thrift API and provides example C++ code for an extension. Every extension runs as a separate process and communicates to an osquery process using Thrift and a UNIX domain socket. A single extension may contain arbitrary plugins, each are registered using a setUp API call. At Facebook we deploy an `fb-osquery` package and single binary that contains our Facebook-specific tables and internal configuration/logging APIs. 

## Autoloading Extensions

The following [CLI flags](../installation/cli-flags.md) control extension autoloading:

```sh
--extensions_autoload=/etc/osquery/extensions.load
--extensions_timeout=3
--extensions_interval=3
```

`extensions_autoload` points to a line-delimited set of paths to executables. When osquery launches, each path is evaluated for "safe permissions" and executed as a monitored child process. Each executable receives 3 argument switches: `socket`, `timeout`, `interval`. An extension process may use these to find the osquery process's Thrift socket and have hint on retry/backoff configuration if any latency or errors occur. 

The simplest `extensions.load` file contains a single extension path:
```sh
$ cat /etc/osquery/extensions.load
/usr/lib/osquery/extensions/fb_osquery.ext
$ file /usr/lib/osquery/extensions/fb_osquery.ext
/usr/lib/osquery/extensions/fb_osquery.ext: ELF 64-bit LSB executable
```

The autoload workflow is similar to:

- Check if extensions are enabled.
- Read `--extensions_autoload` and check permissions/ownership of each path.
- Fork and exec each path with a few well-known switches.
- Treat each child process as a "worker" and enforce memory/cycle usage.
- Read set config plugin from `--config_plugin`.
- If the config plugin does not exist and at least 1 extension was autoload:
- Wait `--extensions_timeout` * `--extensions_interval` for the extension to register the config plugin.
- Fail if the plugin is not registered or the plugin returns a failed status.

The same dependency check is applied to the logger plugin setting after a valid config is read. Every registered plugin is available throughout the run of osqueryd or osqueryi. 

## More Options

Extensions are most useful when used to expose config or logger plugins. Along with autoloading extensions you can start osqueryd services with non-default plugins using `--flagfile=PATH`. The osqueryd init service on Linux searches for a `/etc/osquery/osquery.flags` path containing flags. This is a great place to add non-default extensions options or for replacing plugins:

```sh
$ cat /etc/osquery/osquery.flags
--config_plugin=configerator
--logger_plugin=scribe
```

The osquery extensions concept builds on the platform's concept of a "registry" of plugin types and plugins therein. The registry maintains a lookup of each plugin and its origin, internal or a transient UUID assigned to an extension. osquery supports extensions as dynamic loadable objects too. 

The CLI flag(s):

```sh
--modules_autoload=/etc/osquery/modules.load
```

work the same as extensions, each path is evaluated for safe permission and ownership, and `dlopen`ed when an osquery process starts. There is example code for writing a loaded module in the *./examples* folder. If you are building extensions using the osquery build process a module may be a better option.


