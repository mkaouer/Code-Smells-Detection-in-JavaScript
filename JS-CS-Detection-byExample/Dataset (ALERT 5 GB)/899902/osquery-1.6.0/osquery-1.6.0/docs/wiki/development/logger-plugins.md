For details on how osqueryd schedules queries and loads information from a config, see the [configuration](../deployment/configuration.md) deployment guide.

If you would like to use services like [scribe](https://github.com/facebookarchive/scribe) or [flume](http://flume.apache.org/), you need to write a C++ function that consumes/handles a string argument.

## Example: glog logger

This following is a overly simplified logger plugin that writes results to a glog info line.

```cpp
#include <osquery/logger.h>
#include <glog/logging.h>

namespace osquery {

class GlogLoggerPlugin : public LoggerPlugin {
 public:
  Status logString(const std::string& message) {
    LOG(INFO) << message;
    return Status(0, "OK");
  }

  virtual ~GlogPlugin() {}
};

REGISTER(GlogLoggerPlugin, "logger", "glog");
}
```

Essentially, you are just implementing a **logString** method. When the daemon identifies a change to a query schedule it will call the active logger plugin's **logString** method after converting the change details into JSON.

## Using the plugin

Add the source to *osquery/logger/plugins/CMakeLists.txts* and it will be compiled and linked.

Now when starting osqueryd you may use `--logger_plugin=glog` where the name is the string identifier used in **REGISTER**.
