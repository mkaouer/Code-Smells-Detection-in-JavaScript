The osquery shell and daemon use optional command line (CLI) flags to control
initialization, disable/enable features, and select plugins.

Most of these flag-based parameters apply to both tools. Flags that do not
control startup settings may be included as "options" to the daemon within its [configuration](../deployment/configuration.md). To see a full list of flags for your osquery version use `--help` or select from the flags table:

```
osquery> select * from osquery_flags;
```

To see the flags that have been updated by your configuration, flagfile, or by the shell consider:

```
osquery> select * from osquery_flags where default_value <> value;
```

## CLI-only (initialization) flags

A special flag, part of Gflags, can be used to read additional flags from a line-delimited file. On OS X and Linux this **flagfile** is the recommended way to add/remove the following CLI-only initialization flags.

`--flagfile="/etc/osquery/osquery.flags"`

Include line-delimited switches to be interpreted and used as CLI-flags:

```
--config_plugin=custom_plugin
--logger_plugin=custom_plugin
--distributed_plugin=custom_plugin
--watchlog_level=2
```


### Configuration control flags

`--config_plugin="filesystem"`

Config plugin name. The type of configuration retrieval, the default **filesystem** plugin reads a configuration JSON from disk.

Built-in options include: **filesystem**, **tls**

`--config_path="/etc/osquery/osquery.conf"`

The **filesystem** config plugin's path to a JSON file.
On OS X the default path is **/var/osquery/osquery.conf**.
If you want to read from multiple configuration paths create a directory: **/etc/osquery/osquery.conf.d/**. All files within that optional directory will be read and merged in lexical order.

`--config_check=false`

Check the format of an osquery config and exit. Arbitrary config plugins may be used. osquery will return a non-0 exit if the parsing failed.

`--config_dump=false`

Request that the configuration JSON be printed to standard out before it is updated. In this case "updated" means applied to the active config. When osquery starts it performs an initial update from the config plugin. To quickly debug the content retrieved by custom config plugins use this in tandem with `--config_check`.

### osquery daemon control flags

`--force=false`

Force **osqueryd** to kill previously-running daemons. The daemon will check for an existing "pidfile". If found, and if it contains a pid of a process named "osqueryd", the process will be killed.

`--pidfile=/var/osquery/osqueryd.pidfile`

Path to the daemon pidfile mutex. The file is used to prevent multiple osqueryd processes starting.

`--disable_watchdog=false`

Disable userland watchdog process. **osqueryd** uses a watchdog process to monitor the memory and CPU utilization of threads executing the query schedule. If any performance limit is violated the "worker" process will be restarted.

`--watchdog_level=1`

Performance limit level (0=loose, 1=normal, 2=restrictive, 3=debug). The default watchdog process uses a "level" to configure performance limits.
The higher the level the more strict the limits become. The "debug" level disables the performance limits completely.

### Backing storage control flags

`--database_in_memory=false`

Keep osquery backing-store in memory. This has a number of performance implications and is not recommended. For the default backing-store, RocksDB, this option is not supported.

`--database_path=/var/osquery/osquery.db`

If using a disk-based backing store, specify a path. osquery will keep state using a "backing store" using RocksDB by default. This state holds event information such that it may be queried later according to a schedule. It holds the results of the most recent query for each query within the schedule. This last-queried result allows query-differential logging.

`--database_dump=false`

Helpful for debugging database problems. This will print a line for each key in the backing store. Note: There could be MBs worth of data in the backing store.

### Extensions control flags

`--disable_extensions=false`

Disable extension API. See the [SDK development](../development/osquery-sdk.md) page for more information on osquery extensions, and the [deployment](../deployment/extensions.md) page for how to use extensions.

`--extensions_socket=/var/osquery/osquery.em`

Path to the extensions UNIX domain socket.
[Extensions](../deployment/extensions.md) use a UNIX domain socket for communication. It is very uncommon to change the location of the file. The osquery shell may use extensions, but the socket location is relative to the user invoking the shell and does not support concurrent shells.

`--extensions_autoload=/etc/osquery/extensions.load`

Optional path to a list of autoloaded and managed extensions.
If using an extension to provide a proprietary config or logger plugin the extension process can be started by the daemon. Include line-delimited paths to extension executables. See the extensions [deployment](../deployment/extensions.md) page for more details on extension autoloading.

`--extensions_timeout=3`

Seconds to wait for autoloaded extensions to register.
osqueryd may depend on a config plugin from an extension. If the requested config plugin name is not registered within the timeout the daemon will exit with a failure.

`--extensions_interval=3`

Seconds delay between extension connectivity checks.
Extensions are loaded as processes. They are expected to start a thrift service thread. The osqueryd process will continue to check this API. If an extension process is incorrectly stopped, osqueryd will detect the connectivity failure and unregister the extension.

`--modules_autoload=/etc/osquery/modules.load`

Optional path to a list of autoloaded library module-based extensions. Modules are similar to extensions but are loaded as shared libraries. They are less flexible and should be built using the same GCC runtime and developer dependency library versions as osqueryd. See the extensions [deployment](../deployment/extensions.md) page for more details on extension module autoloading.

### Remote settings (optional for config/logger/distributed) flags

When using non-default [remote](../deployment/remote.md) plugins such as the **tls** config, logger and distributed plugins, there are process-wide settings applied to every plugin.

`--tls_hostname=""`

When using **tls**-based config or logger plugins, a single TLS host URI is used. Using separate hosts for configuration and logging is not supported among the **tls**-based plugin suite. Provide a host name and optional port, e.g.: `facebook.com` or `facebook.com:443`.

`--tls_client_cert=""`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. Optionally provide a path to a PEM-formatted client TLS certificate.

`--tls_client_key=""`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. Optionally provide a path to a decrypted/password-less PEM-formatted client TLS private key.

`--tls_server_certs=""`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. Optionally provide a path to a PEM-formatted server or authority certificate bundle. This path will be used as either an explicit set of accepted certificates or an OpenSSL-verify path directory of well-formed filename certificates.

`--disable_enrollment=false`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. Remote plugins use an enrollment process to enable possible server-side implemented authentication and identification/authorization. Config and logger plugins implicitly require enrollment features. It is not recommended to disable enrollment and this option may be removed in the future.

`--enroll_secret_path=""`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. A very simple authentication/enrollment involves posting a deployment or staged shared secret. This secret should be protected on the host, but potentially shared among an enterprise or fleet. Provide a path for the osquery process to read and use during enrollment phases.

`--config_tls_endpoint=""`

The **tls** endpoint path, e.g.: **/api/v1/config** when using the **tls** config plugin. See the other **tls_** related CLI flags.

`--config_tls_refresh=0`

The configuration **tls** endpoint refresh interval. By default a configuration is fetched only at osquery load. If the configuration should be auto-updated set a "refresh" time to a value in seconds. This option enforces a minimum of 10 seconds. If the configuration endpoint cannot be reached during run, during an attempted refresh, the normal retry approach is applied.


`--config_tls_max_attempts=3`

The total number of attempts that will be made to the remote config server if a request fails.

`--logger_tls_endpoint=""`

The **tls** endpoint path, e.g.: **/api/v1/logger** when using the **tls** logger plugin. See the other **tls_** related CLI flags.

`--enrollment_tls_endpoint=""`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. An enrollment process will be used to allow server-side implemented authentication and identification/authorization. You must provide an endpoint relative to the **--tls_hostname** URI.

`--logger_tls_period=3`

See the **tls**/[remote](../deployment/remote.md) plugin documentation. This is a number of seconds before checking for buffered logs. Results are sent to the TLS endpoint in intervals, not on demand (unless the period=0).

`--logger_tls_compress=false`

Optionally enable GZIP compression for request bodies when sending. This is optional, and disabled by default, as the deployment must explicitly know that the logging endpoint supports GZIP for content encoding.

`--logger_tls_max=1048576`

It is common for TLS/HTTPS servers to enforce a maximum request body size. The default behavior in osquery is to enforce each log line be under 1M bytes. This means each result line from a query's results cannot exceed 1M, this is very unlikely. Each log attempt will try to forward up to 1024 lines. If your service is limited request bodies, configure the client to limit the log line size.

Use this only in emergency situations as size violations are dropped. It is extremely uncommon for this to occur, as the `value_max` for each column would need to be drastically larger, or the offending table would have to implement several hundred columns.

`--distributed_tls_read_endpoint=/foobar`

The URI path which will be used, in conjunction with `tls_hostname`, to create the remote URI for retrieving distributed queries when using the **tls** distributed plugin.

`--distributed_tls_write_endpoint=/foobar`

The URI path which will be used, in conjunction with `tls_hostname`, to create the remote URI for submitting the results of distributed queries when using the **tls** distributed plugin.

`--distributed_tls_max_attempts=3`

The total number of attempts that will be made to the remote distributed query server if a request fails when using the **tls** distributed plugin.

## Runtime flags

`--read_max=52428800` (50MB)

Maximum file read size.
The daemon or shell will first 'stat' each file before reading. If the reported size is greater than `read_max` a "file too large" error will be returned.

`--read_user_max=10485760` (10MB)

Maximum non-super user read size.
Similar to `--read_max` but applied to user-controlled (owned) files.

`--read_user_links=true`

Read user-controlled (owned) filesystem links.
This allows specific control over symbolic links owned by users.

### osquery daemon runtime control flags

`--schedule_splay_percent=10`

Percent to splay config times.
The query schedule often includes several queries with the same interval.
It is often not the intention of the schedule author to run these queries together
at that interval. But rather, each query should run at about the interval.
A default schedule splay of 10% is applied to each query when the configuration is loaded.

`--pack_refresh_interval=3600`

Query Packs may optionally include one or more discovery queries, which allow
you to use osquery queries to manage which packs should be loaded at runtime.
Osquery will natively re-run the discovery queries from time to time, to make
sure that all of the correct packs are executing. This flag allows you to
specify that interval.

`--pack_delimiter=_`

Control the delimiter between pack name and pack query names. When queries are added to the daemon's schedule they inherit the name of the pack. A query named "info" within the "general_info" pack will become "pack_general_info_info". Changing the delimiter to "/" turned the scheduled name into: "pack/general_info/info".

`--disable_caching=false`

"Caching" refers to short cutting the table implementation and returning the same results from the previous query against the table.
This is not related to differential results from scheduled queries, but does affect the performance of the schedule.
Results are cached when different scheduled queries in a schedule use the same table, without providing query constraints.
Caching should NOT affect data freshness since the cache life is determined as the minimum interval of all queries against a table.

`--schedule_default_interval=3600`

Optionally set the default interval value. This is used if you schedule a query
which does not define an interval.

`--worker_threads=4`

Number of work dispatch threads.

`--schedule_timeout=0`

Limit the schedule, 0 for no limit. Optionally limit the osqueryd's life by adding a schedule limit in seconds.
This should only be used for testing.

`--disable_tables=table_name1,table_name2`

Comma-delimited list of table names to be disabled.
This allows osquery to be launched without certain tables.

### osquery events control flags

`--disable_events=false`

Disable osquery Operating System [eventing publish subscribe](../development/pubsub-framework.md) APIs. This will implicitly disable several tables that report based on logged events.

`--events_expiry=86000`

Timeout to expire [eventing publish subscribe](../development/pubsub-framework.md) results from the backing-store. This expiration is only applied when results are queried. For example, if `--events_expiry=1` then events will only practically exist for a single select from the subscriber. If no select occurs then events will be saved in the backing store indefinitely.

`--events_optimize=true`

Since event rows are only "added" it does not make sense to emit "removed" results. An optimization can occur within the osquery daemon's query schedule. Every time the select query runs on a subscriber the current time is saved. Subsequent selects will use the previously saved time as the lower bound. This optimization is removed if any constraints on the "time" column are included.

`--events_max=1000`

Maximum number of events to buffer in the backing store while waiting for a query to 'drain' or trigger an expiration. If the expiration (`events_expiry`) is set to 1 day, this max value indicates that only 1000 events will be stored before dropping each day. In this case the limiting time is almost always the scheduled query. If a scheduled query that select from events-based tables occurs sooner than the expiration time that interval becomes the limit.

### Logging/results flags

`--logger_plugin=filesystem`

Logger plugin name. The default logger is **filesystem**. This writes the various log types as JSON to specific file paths.

Built-in options include: **filesystem**, **tls**

`--disable_logging=false`

Disable ERROR/WARNING/INFO (called status logs) and query result [logging](../deployment/logging.md).

`--log_result_events=true`

Log scheduled results as events.

`--host_identifier=hostname`

Field used to identify the host running osquery (hostname, uuid)

Select either "hostname" or "uuid" for the host identifier.
DHCP may assign variable hostnames, if this is the case, select UUID for a
consistant logging label.

`--verbose=false`

Enable verbose informational messages.

`--logger_path=/var/log/osquery/`

Directory path for ERROR/WARN/INFO and results logging.

`--logger_mode=640`

File mode for output log files (provided as an octal string).  Note that this
affects both the query result log and the status logs.
**Warning**: If run as root, log files may contain sensitive information!

`--value_max=512`

Maximum returned row value size.

## Distributed Flags

`--distributed_plugin=tls`

Distributed plugin name. The default distributed plugin is not set. You must set `--disable_distributed=false --distributed_plugin=tls` (or whatever plugin you'd rather use instead of TLS) to enable the distributed feature.

`--disable_distributed=true`

Disable distributed queries functionality. By default, this is set to `true` (the distributed feature is disabled). Set this to `false` to enable distributed queries.

`--distributed_interval=60`

In seconds, the amount of time that osqueryd will wait between periodically checking in with a distributed query server to see if there are any queries to execute.

## Shell-only flags

Most of the shell flags are self-explanatory and are adapted from the SQLite shell. Refer to the shell's ".help" command for details and explanations.

There are several flags that control the shell's output format: `--json`, `--list`, `--line`, `--csv`. For all of the output types there is `--nullvalue` and `--separator` that can be used appropriately.

`--planner=false`

When prototyping new queries the planner enables verbose decisions made by the SQLites virtual table API module. This module is implemented by osquery code so it is very helpful to learn what predicate constraints are selected and what full table scans are required for JOINs and nested queries.

`--header=true`

Set this value to `false` to disable column name (header) output. If using the shell in an automation or script the header line in `line` or `csv` mode may not be needed.
