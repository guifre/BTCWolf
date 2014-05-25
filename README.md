BTCWolf
===========
BTCWolf a simple automated trader agent I am developing for fun. 

There is currently implemented a single strategy, where it is sold when the price rises the one of buying time and it buys when the time decreases the one used at buy time. So, no money can be lost as a result of performing trading operations.


Requirements
------------
      - java 1.6 or higher


Running BTCWolf
---------------------
For security reasons, you need to specify at runtime the exchanger credentials, as follows:


    $ java -DAPIKey="xxxx" -DSecretKey="yyyy" -DPassword="zzzz" -jar dist/BTCWolf.jar


Bugs & Contact
--------------
Feel free to mail me with any problem, bug, suggestions or fixes at:
Guifre Ruiz <guifre.ruiz@gmail.com>


License
-------
Code licensed under the GPL v3.0.
