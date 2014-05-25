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


    $java -jar dist/BTCWolf.jar -DAPIKey="xxxxx" -DSecretKey="yyyy" -DPassword="zzzzz"  -Djava.util.logging.config.file=/resources/logger.properties


Bugs & Contact
--------------
Feel free to mail me with any problem, bug, suggestions or fixes at:
Guifre Ruiz <guifre.ruiz@gmail.com>

Visit http://owasp.github.io/NINJA-PingU for more information about NINJA PingU.

License
-------
Code licensed under the GPL v3.0.