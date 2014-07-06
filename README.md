What's BTCWolf?
-------------
BTCWolf a simple automated bitcoin trading robot. I is aimed at monitoring bitcoin exchange markets to automatically take trade decisions and raise the user's capital. It supports multiple exchange markets, it comes out of the box with multiple trading strategies and a testing framework for those.

How BTCWolf works?
-------------
There currently are three different trading strategies implemented. The one that so fas has been more sucessful for me uses short term simple and exponential moving averages, plus other refinements, to determine when to order. The following chart has been generated with BTCWolf and shows how this strategy works.
![BTCWolf](https://raw.githubusercontent.com/guifre/BTCWolf/master/resources/screenshot.png)

You can also see real time trades of my BTCWolf instance in its twitter at https://twitter.com/btcwolfbot

Why BTCWolf?
------------
BTCWolf provides and API to plug your own strategies, it contains thousands of historic market data, and a set of charts generators and unit tests to validate those before using them for real. 

Requirements
------------
      - java >= 1.6

Running BTCWolf
---------------------
You need to set your credential in resources/settings.properties and run BTCWolf, as follows:

    $ java -jar dist/BTCWolf.jar

Bugs & Contact
--------------
Feel free to mail me with any problem, bug, suggestions or fixes at:
Guifre Ruiz <guifre.ruiz@gmail.com>


License
-------
Code licensed under the GPL v3.0.
