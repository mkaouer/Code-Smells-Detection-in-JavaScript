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



# update core application files
SH_FILE="$APP_ROOT/filebot.sh"
JAR_FILE="$APP_ROOT/FileBot.jar"

echo "Update $JAR_FILE"
curl -L -o "$JAR_FILE" -z "$JAR_FILE" "https://downloads.sourceforge.net/project/filebot/filebot/HEAD/FileBot.jar"

echo "Update $SH_FILE"
curl -L -o "$SH_FILE" -z "$SH_FILE" "https://svn.code.sf.net/p/filebot/code/trunk/installer/portable/filebot.sh"
