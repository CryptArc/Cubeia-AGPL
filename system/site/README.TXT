A very basic website, used for user-registration.

This _avoids_ the need to integrate the poker system with a website which 
provides the user-registration (e.g. wordpress).

It is packaged as a war and additionally as sar (firebase-service).

BUILD & START
=============

mvn clean package
cd war
mvn jetty:run

http://localhost:8182

The sar can be used as a firebase service