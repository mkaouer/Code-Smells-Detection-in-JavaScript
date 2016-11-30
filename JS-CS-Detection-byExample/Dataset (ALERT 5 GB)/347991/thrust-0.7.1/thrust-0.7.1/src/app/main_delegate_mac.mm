// Copyright (c) 2014 Stanislas Polu. All rights reserved.
// See the LICENSE file.

#include "src/app/main_delegate.h"

#include "base/mac/bundle_locations.h"
#include "base/files/file_path.h"
#include "base/path_service.h"
#include "content/public/common/content_paths.h"

#include "brightray/common/mac/main_application_bundle.h"

namespace thrust_shell {

namespace {

base::FilePath GetFrameworksPath() {
  return brightray::MainApplicationBundlePath().Append("Contents")
                                               .Append("Frameworks");
}

}  // namespace

void MainDelegate::OverrideFrameworkBundlePath() {
  base::mac::SetOverrideFrameworkBundlePath(
      GetFrameworksPath().Append("ThrustShell Framework.framework"));
}

void MainDelegate::OverrideChildProcessPath() {
  base::FilePath helper_path = GetFrameworksPath().Append("ThrustShell Helper.app")
                                                  .Append("Contents")
                                                  .Append("MacOS")
                                                  .Append("ThrustShell Helper");
  PathService::Override(content::CHILD_PROCESS_EXE, helper_path);
}

}  // namespace thrust_shell
