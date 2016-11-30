`osqueryi` is the osquery interactive query console/shell. It is completely standalone and does not communicate with a daemon and does not need to run as an administrator. Use the shell to prototype queries and explore the current state of your operating system.

## Executing SQL queries

**osqueryi** lets you run meta-commands and query osquery tables. See the [table API](https://osquery.io/tables/) for a complete list of tables, types, and column descriptions. For SQL syntax help, see [SQL as understood by SQLite](http://www.sqlite.org/lang.html).

Here is an example query:

```
$ osqueryi
osquery> SELECT DISTINCT
    ...>   process.name,
    ...>   listening.port,
    ...>   process.pid
    ...> FROM processes AS process
    ...> JOIN listening_ports AS listening
    ...> ON process.pid = listening.pid
    ...> WHERE listening.address = '0.0.0.0';

+----------+-------+-------+
| name     | port  | pid   |
+----------+-------+-------+
| Spotify  | 57621 | 18666 |
| ARDAgent | 3283  | 482   |
+----------+-------+-------+
osquery>
```

The shell accepts a single positional argument and one of the several output modes. If you want to output JSON or CSV values, try:

```
$ osqueryi --json "select * from routes where destination = '::1'"
[
  {"destination":"::1","flags":"2098181","gateway":"::1","interface":"","metric":"0","mtu":"16384","netmask":"128","source":"","type":"local"}
]
```

You may also pipe a query as *stdin*. The input will be executed on the **osqueryi** shell and must be well-formed SQL or **osqueryi** meta-commands. Note the added ';' to the query when using *stdin*:

```
$ echo "select * from routes where destination = '::1';" | osqueryi --json
```

## Getting help

**osqueryi** is a modified version of the SQLite shell.
It accepts several meta-commands, prefixed with a '.':

* to list all tables: `.tables`
* to list the schema (columns, types) of a specific table: `pragma table_info(table_name);`
* to list all available commands: `.help`
* to exit the console: `.exit` or `^D`

Here are some example shell commands:

```
osquery> .tables
  => alf_services
  => apps
  => ca_certs
  => etc_hosts
  => interface_addresses
  => interface_details
  => kernel_extensions
  => launchd
  => listening_ports
  => nvram
  => processes
  => routes
[...]

osquery> .schema routes
CREATE VIRTUAL TABLE routes USING routes(
    destination TEXT,
    netmask TEXT,
    gateway TEXT,
    source TEXT,
    flags INTEGER,
    interface TEXT,
    mtu INTEGER,
    metric INTEGER,
    type TEXT
);

osquery> PRAGMA table_info(routes);

+-----+-------------+---------+---------+------------+----+
| cid | name        | type    | notnull | dflt_value | pk |
+-----+-------------+---------+---------+------------+----+
| 0   | destination | TEXT    | 0       |            | 0  |
| 1   | netmask     | TEXT    | 0       |            | 0  |
| 2   | gateway     | TEXT    | 0       |            | 0  |
| 3   | source      | TEXT    | 0       |            | 0  |
| 4   | flags       | INTEGER | 0       |            | 0  |
| 5   | interface   | TEXT    | 0       |            | 0  |
| 6   | mtu         | INTEGER | 0       |            | 0  |
| 7   | metric      | INTEGER | 0       |            | 0  |
| 8   | type        | TEXT    | 0       |            | 0  |
+-----+-------------+---------+---------+------------+----+

osquery> .exit
$
```

The shell does not keep much state or connect to the **osqueryd** daemon.
If you would like to run queries and log changes to the output or log operating system events, consider deploying a query **schedule** using [osqueryd](using-osqueryd.md).
