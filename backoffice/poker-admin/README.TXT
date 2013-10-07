The Poker Admin Webapplication, used to create Tables and Tournaments

To build: 
mvn clean install

To run:
mvn jetty:run

Access at localhost:8088/poker-admin/

For more installation instructions, see the root INSTALL.TXT

* AUTHENTICATION

Default logins are admin/admin and user/user. To override with more/other accounts create a file in 
classpath named poker-admin-user-list.xml which contains users and roles as in the example below.

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<users>
    <user userName="admin" password="admin">
        <role>ROLE_ADMIN</role>
    </user>
    
    <user userName="user" password="user">
        <role>ROLE_USER</role>
    </user>
</users>
