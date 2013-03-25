This is the back office for the poker system.

To build:
mvn clean install

To run:
mvn jetty:run

Access at localhost:8088/poker-admin/

DATABASES
=========

You will need a mongodb running for hand histories and tournament histories
the default configuration is: db: poker, user: poker, pass: poker
to change: src/test/resources/admin.properties

Additionally you need a mysql running for table and tournament templates.
the default configuration is: db: poker, user: poker, pass: poker
to change: src/test/resources/jetty-env.xml

IMPORTANT
=========

Τhe default build of the poker-server results in a system with mock-services
(or mock-integrations). This means that the poker-admin does not influence the
actual tables/tournaments.

In order to activate the poker-admin fully, you have to follow those steps:

a) set use-mock-integrations=false" in
server\game-modules\poker-uar\src\test\resources\firebase\conf\cluster.props

b) build the poker-server with "-P use-integrations" option.

c) install and run Cubeia Network (as mentioned in the ../README), prior to
starting the poker-system.

DEV ONLY
========

Guidelines for Wicket programming:

- Do not define serialVersionUID in all components, see: 
  http://apache-wicket.1842946.n4.nabble.com/serialVersionUID-td1879689.html
  
- Do not add transient to @SpringBeans, see: 
  https://cwiki.apache.org/WICKET/spring.html
