# - Find Thrift (a cross platform RPC lib/tool)
# This module defines
#  THRIFT_VERSION, version string of ant if found
#  THRIFT_INCLUDE_DIR, where to find THRIFT headers
#  THRIFT_CONTRIB_DIR, where contrib thrift files (e.g. fb303.thrift) are installed
#  THRIFT_LIBS, THRIFT libraries
#  THRIFT_FOUND, If false, do not try to use ant

# prefer the thrift version supplied in THRIFT_HOME
find_path(THRIFT_INCLUDE_DIR thrift/Thrift.h HINTS
  $ENV{THRIFT_HOME}/include/
  /usr/local/include/
  /opt/local/include/
)

find_path(THRIFT_CONTRIB_DIR share/fb303/if/fb303.thrift HINTS
  $ENV{THRIFT_HOME}
  /usr/local/
)

set(THRIFT_LIB_PATHS
  $ENV{THRIFT_HOME}/lib
  /usr/local/lib
  /opt/local/lib)

# prefer the thrift version supplied in THRIFT_HOME
if(NOT DEFINED THRIFT_FOUND)
  find_library(THRIFT_LIBRARY NAMES thrift HINTS ${THRIFT_LIB_PATHS})
  #find_library(THRIFT_STATIC_LIBRARY NAMES libthrift.a HINTS ${THRIFT_LIB_PATHS})

  find_program(THRIFT_COMPILER thrift
    $ENV{THRIFT_HOME}/bin
    /usr/local/bin
    /usr/bin
    NO_DEFAULT_PATH
  )

  if (THRIFT_LIBRARY)
    set(THRIFT_FOUND TRUE)
    LOG_LIBRARY(thrift "${THRIFT_LIBRARY}")
    exec_program(${THRIFT_COMPILER}
      ARGS -version OUTPUT_VARIABLE THRIFT_VERSION RETURN_VALUE THRIFT_RETURN)
  else()
    message(FATAL_ERROR "Thrift compiler/libraries NOT found.")
  endif()

  mark_as_advanced(
    THRIFT_LIBRARY
    THRIFT_COMPILER
    THRIFT_INCLUDE_DIR
  )
endif()
