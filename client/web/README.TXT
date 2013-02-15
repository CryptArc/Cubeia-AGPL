The standard web client, HTML5 based.

This client is under active development.

To start:
mvn package jetty:run

Access:
http://localhost:8181

There is no user registration at this point. Use any name, and the password
should be an integer, it will be used as the playerId.

In order to test the client quickly, login from 2 different browsers. Then sit
the 2 players at one table and buy-in. The game starts.

Alternatively you can login from a 2nd networked machine or device, using the ip
of your main machine, e.g. http://192.168.1.13:8181.