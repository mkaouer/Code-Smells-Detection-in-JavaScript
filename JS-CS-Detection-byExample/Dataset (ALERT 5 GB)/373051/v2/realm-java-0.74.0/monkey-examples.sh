#!/bin/bash
#
# Runs Android monkey tool on all example projects.
#
# Note: adb shell always return exit code 0: https://code.google.com/p/android/issues/detail?id=3254
# Solution for now: Write exit code to file and read it from there
#

TEST_EVENTS=2000;

echo ""
echo "TESTING EXAMPLES"
echo ""
for example in examples/*/ ; do

    applicationId=$(grep applicationId "${example}build.gradle" | cut -d \" -f 2 | cut -d \' -f 2)
    project=$(basename ${example})

    if [ ${project} == "encryptionExample" ] ; then
        echo "Skipping ${example}"
        continue
    fi

    echo ""
    echo "Building $example"
    adb uninstall ${applicationId} > /dev/null
    ./gradlew ${project}:clean ${project}:installDebug

    echo "Letting monkey loose in $example"
    adb shell "monkey -p ${applicationId} -v ${TEST_EVENTS} ; echo \"\$?\\c\" > /data/local/tmp/${applicationId}.exitcode"
    rc=$(adb shell cat /data/local/tmp/${applicationId}.exitcode)
    if [ "${rc}" != "0" ] ; then
        echo ""
        echo "Monkey found an error, stopping tests."
        echo "Reproduce: adb shell monkey -s <seed> -p ${applicationId} -v ${TEST_EVENTS}"
        echo "Exit code: ${rc}"
        exit 1
    fi
done

echo ""
echo "Example monkeys are happy"
echo ""
