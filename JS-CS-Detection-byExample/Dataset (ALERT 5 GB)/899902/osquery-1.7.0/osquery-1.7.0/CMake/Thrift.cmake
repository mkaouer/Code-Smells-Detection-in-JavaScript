# Target for generating osquery thirft (extensions) code.
set(OSQUERY_THRIFT_DIR "${CMAKE_BINARY_DIR}/generated/gen-cpp")
set(OSQUERY_THRIFT_GENERATED_FILES
  ${OSQUERY_THRIFT_DIR}/Extension.cpp
  ${OSQUERY_THRIFT_DIR}/Extension.h
  ${OSQUERY_THRIFT_DIR}/ExtensionManager.cpp
  ${OSQUERY_THRIFT_DIR}/ExtensionManager.h
  ${OSQUERY_THRIFT_DIR}/osquery_types.cpp
  ${OSQUERY_THRIFT_DIR}/osquery_types.h
)

# Allow targets to warn if the thrift interface code is not defined.
add_definitions(
  -DOSQUERY_THRIFT_LIB=thrift
  -DOSQUERY_THRIFT_SERVER_LIB=thrift/server
  -DOSQUERY_THRIFT_POINTER=boost
  -DOSQUERY_THRIFT=
)

# For the extensions targets, allow them to include thrift interface headers.
include_directories("${OSQUERY_THRIFT_DIR}")
