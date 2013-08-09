The full backoffice functionality, like User, Wallet and Accounting Service, is
provided by Cubeia Network.

Retrieve the code and follow the contained instructions to setup:

https://bitbucket.org/cubeia/cubeia-network

Once you have access to the Cubeia Network Webapplication, you have to create
some basic data, thus the Poker Admin and the Poker Game works.

OPERATOR
========

Create an Operator with the ID 1

CURRENCY
========

Add one currency, e.g. EUR. Avoid creating multiple currencies at this
point.


ACCOUNTS
========

Create 3 different wallet accounts. The accounts use dummy user ID's:

 * Dummy User -1000 is the system rake account
 * Dummy User -2000 is for promotions (such as free-rolls etc)
 * Dummy User -3000 is a general bank for internal transfers

The type is SYSTEM_ACCOUNT and enable to option "negative balance allowed".

 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -1000 | Rake
 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -2000 | Promotions
 * SYSTEM_ACCOUNT | EUR | OPEN | UserID -3000 | General 

Important: if you create more currencies later, you have to create those 3
accounts for the currencies, too.


USER
====

Create some users. Username, password and Operator ID (1) is enough data.

Alternatively, you can use the user-creator tool (see next step), which will
add an account with some cash for the created user.

BOTS
====

Bots are like other users, so if you want to use the bots, you have to create
accounts for them, too. The user-creator tool simplifies this:

./tools/user-creator/README