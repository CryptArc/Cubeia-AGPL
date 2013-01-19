This directory contains not the firebase server itself, but all the poker code
which is executed in context of the firebase server.

Note that some of the sources are under heavy development.

== SETUP ==

Note: You will most possibly see many warnings and even errors.

mvn install # or mvn clean install
cd  game-modules/poker-uar
mvn firebase:run

To verify that the server is running:

(todo: enter a simple verification method, which does not depend on further
installation steps)