IMPORTANT
=========

Cubeia Poker is based on Cubeia Firebase and Cubeia Network. Both products are
open source and in production.

REQUIREMENTS
=============
Java: JDK 1.7
Maven: 3.0.4+


QUICK-START
===========

mvn clean package install
cd  uar
mvn firebase:run

You should be able to access the game at:

http://localhost:18181

There is no user registration at this point. Use any name, and the password should be an integer. (Use > 1000 if you don't want to clash with bots).

In order to get some opponents, go to the bots at:

http://localhost:18081

The default values will create 100 bots, just press the OK button.

You should see now within the lobby that tables are getting players. 

Join one or more tables and check the game.


BUILD & START
=============

See INSTALL.TXT


SOFTWARE SUBSYSTEMS
===================

For more information about the software subsystems, please review the 
./doc/SUBSYSTEMS.TXT file.


LINKS
=====

Info   :  https://bitbucket.org/cubeia/cubeia-poker/wiki/Home
Repo   :  https://bitbucket.org/cubeia/cubeia-poker
Forum  :  http://www.cubeia.org/index.php/forums
Service:  http://www.cubeia.com/?page_id=251

