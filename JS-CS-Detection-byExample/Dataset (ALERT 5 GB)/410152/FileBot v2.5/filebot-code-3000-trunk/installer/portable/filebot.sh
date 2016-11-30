#!/bin/sh
PRG="$0"

# resolve relative symlinks
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG="`dirname "$PRG"`/$link"
  fi
done

# make it fully qualified
WORKING_DIR=`pwd`
PRG_DIR=`dirname "$PRG"`
APP_ROOT=`cd "$PRG_DIR" && pwd`

# restore original working dir
cd "$WORKING_DIR"


# force JVM language and encoding settings
export LANG="en_US.UTF-8"
export LC_ALL="en_US.UTF-8"

java -Dunixfs=false -DuseGVFS=false -DuseExtendedFileAttributes=true -DuseCreationDate=false -Dfile.encoding=UTF-8 -Djava.net.useSystemProxies=false -Dsun.net.client.defaultConnectTimeout=10000 -Dsun.net.client.defaultReadTimeout=60000 -Djna.nosys=true -Dapplication.deployment=portable -Dnet.filebot.Archive.extractor=SevenZipExecutable "-Dnet.filebot.Archive.7z=7z" "-Dnet.filebot.AcoustID.fpcalc=fpcalc" "-Dapplication.dir=$APP_ROOT" "-Djava.io.tmpdir=$APP_ROOT/temp" "-Duser.home=$APP_ROOT" "-Djna.library.path=$APP_ROOT" "-Djava.library.path=$APP_ROOT" -Djava.util.prefs.PreferencesFactory=net.filebot.util.prefs.FilePreferencesFactory "-Dnet.filebot.util.prefs.file=$APP_ROOT/prefs.properties" -jar "$APP_ROOT/FileBot.jar" "$@"
