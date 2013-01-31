This is the back office for the poker system.

To build:
mvn clean install

To run:
mvn jetty:run

Access at localhost:8088/poker-admin/

You will need a mongodb running for hand histories and tournament histories
the default configuration is: db: poker, user: poker, pass: poker
to change: src/test/resources/admin.properties

Additionally you need a mysql running for table and tournament templates.
the default configuration is: db: poker, user: poker, pass: poker
to change: src/test/resources/jetty-env.xml

-

Guidelines for Wicket programming:

- Do not define serialVersionUID in all components, see: 
  http://apache-wicket.1842946.n4.nabble.com/serialVersionUID-td1879689.html
  
- Do not add transient to @SpringBeans, see: 
  https://cwiki.apache.org/WICKET/spring.html
