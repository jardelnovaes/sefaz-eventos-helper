#!/bin/bash

opt=$1
arg1=$2

prepareRelease() {
  echo "Preparing Release"
  git checkout main && git pull
  git checkout release_process && git merge main
  mvn -B build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit
  git add . && git commit -m "[release-version] prepare release"
  mvn -B build-helper:parse-version scm:tag -Dtag=v\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}
}

nextDevelopmentVersion() {
  echo "Preparing Next Development Version"
  git checkout main && git pull
  git checkout release_process && git merge main
  mvn -B build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0-SNAPSHOT versions:commit
  git add . && git commit -m "[release-version] prepare for next development iteration"
}

createAndSendtag() {
  echo "Creating and sending tag $arg1"
   mvn -B scm:tag -Dtag="$arg1"
}

case $opt in
  "prepare-release") prepareRelease ;;
  "next-dev-version") nextDevelopmentVersion ;;
  "tag") createAndSendtag ;;
  *) echo -e "Options:\n\t[ prepare-release | next-dev-version ]"; exit 1 ;;
esac
