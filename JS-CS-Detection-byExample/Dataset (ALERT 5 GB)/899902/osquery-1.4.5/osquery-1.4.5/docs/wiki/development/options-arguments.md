How do I add a command line flag/option/argument to osquery? Well, first familiarize yourself with gflags, then take note of the wrapper below.

[include/osquery/flags.h](https://github.com/facebook/osquery/blob/master/include/osquery/flags.h) contains a single wrapper for `gflags::DEFINE_` type style macros. osquery includes a simple wrapper for defining arguments/options/flags for the osqueryd daemon and shell.

Instead of writing the normal gflags macro for defining a new option:

```cpp
#include <gflags/gflags.h>
// This is the WRONG way to define a flag in osquery.
DEFINE_bool(you_are_awesome, true, "Ground truth for awesome.");  // DON'T DO THIS!
```

Use the following wrapper:

```cpp
#include <osquery/flags.h>

FLAG(bool, you_are_awesome, true, "Ground truth for awesome.");
```

If you are declaring a flag before defining it, no change is needed. Use `DECLARE_bool(you_are_awesome);` like normal. There is no change for accessing the flag either. Use `if (FLAG_you_are_awesome)` like normal.

This will allow osquery callers to show pretty displays when `-h, --help` is used.

Note: restrict your default values to code literals. It does not help to abstract the default variable into a constant then use it singularly in the macro.