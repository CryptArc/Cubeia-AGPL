#!/bin/bash

FIREBASE_VERSION="1.7.3.2-CE"
FIREBASE_TOOLS_VERSION="1.7.0"

SCRIPT_LOCATION="$( cd "$( dirname "$0" )" && pwd )"

echo $SCRIPT_LOCATION

cd $SCRIPT_LOCATION

rm -rf ./deploy

mvn install -Dfirebase.version=$FIREBASE_VERSION -Dfirebase.tools.version=$FIREBASE_TOOLS_VERSION|| exit 1

cp resources/persistence_1_0.xsd deploy/firebase-$FIREBASE_VERSION
cp -f resources/gameserver.sh deploy/firebase-$FIREBASE_VERSION/bin

cd deploy/firebase-$FIREBASE_VERSION

./stop.sh
./start.sh


