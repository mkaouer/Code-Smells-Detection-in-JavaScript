Thrust
======

Thrust is a cross-platform (Linux, OSX, Windows) application shell bindable from
any language. It is designed to ease the creation, packaging and distribution of
cross-platform native desktop appplication.

Thrust embeds Chromium Content API and exposes its API through a local JSON RPC
server listening on unix domain socket. Through the use of a language library,
developers can control Thrust and create shell window, sessions, menus, etc...

Thrust also come with support for the `<webview>` tag allowing the execution of
remote pages in a entirely secure setting.

Thrust will be used by next releases of Breach.

```
[Thurst Architecture]

              (Platform)                        (Client Implementation)
                                                                       
                                           #
                   +------------------+    #        +-----------------------+
                   |   Cocoa / Aura   |    #    +---|  shell3: (HTML/JS)    |
                   +---------+--------+    #    |  +-----------------------++
                             |             #    +--|  shell2: (HTML/JS)    |
+----------------+ +---------+--------+    #    | +-----------------------++
|                +-+   Thrust (C++)   +---------+-+  shell1: (HTML/JS)    |
|  Content  API  | +---------+--------+    #      +-----------------------+
|                |           |             #                 | (TCP/FS)      
|  (Blink / v8)  | +---------+--------+    #      +-----------------------+
|                | +   JSON RPC srv   +-----------+ Client App (any Lang) |
+----------------+ +------------------+    #      +-----------------------+
                                           #
```

### Building Thrust

First you'll need to make sure you have the Chromium `depot_tools` installed.
Check out the instructions here: 
[Install depot_tools](http://www.chromium.org/developers/how-tos/install-depot-tools)

Then you can build with the following commands:

```
./scripts/boostrap.py                                

GYP_GENERATORS=ninja gyp --depth . thrust_shell.gyp
ninja -C out/Debug thrust_shell -j 1
```

Note that `bootstrap.py` may take some time as it checks out `brightray` and
downloads `libchromiumcontent` for your platform.


### Testing

Thrust currently is testable only manually by running the `thrust_shell` 
executable and runnin thrust-node library's test.js file.

### Getting Involved

- Mailing list: [breach-dev@googlegroups.com](https://groups.google.com/d/forum/breach-dev)
- IRC Channel: #breach on Freenode

