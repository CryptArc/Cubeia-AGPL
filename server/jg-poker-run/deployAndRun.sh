#!/bin/bash

FIREBASE_VERSION="1.7.3.2-CE"

mvn install
cp resources/persistence_1_0.xsd deploy/firebase-$FIREBASE_VERSION

cd deploy/firebase-$FIREBASE_VERSION
./stop.sh
./start.sh


