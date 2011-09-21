#!/bin/bash

FIREBASE_VERSION="1.7.3.2-CE"
FIREBASE_TOOLS_VERSION="1.7.0"
SCRIPT_LOCATION="$( cd "$( dirname "$0" )" && pwd )"
FIREBASE_DIRECTORY="$SCRIPT_LOCATION/deploy/firebase-$FIREBASE_VERSION"

if [ -d $FIREBASE_DIRECTORY ]; then
	echo "Stopping old firebase instance"
	cd $FIREBASE_DIRECTORY
	./stop.sh
fi

cd $SCRIPT_LOCATION

echo "Removing deploy directory"
rm -rf ./deploy

echo "Unpacking artifacts"
mvn install -Dfirebase.version=$FIREBASE_VERSION -Dfirebase.tools.version=$FIREBASE_TOOLS_VERSION|| exit 1

echo "Copy resources"
cp -v resources/persistence_1_0.xsd deploy/firebase-$FIREBASE_VERSION
cp -vf resources/gameserver.sh deploy/firebase-$FIREBASE_VERSION/bin

cd $FIREBASE_DIRECTORY

chmod 755 stop.sh
chmod 755 start.sh

echo "Starting firebase"
./start.sh


