What's BTCWolf?
-------------
BTCWolf a simple automated trader agent for the bitCoin exchange market. I is aimed at monitoring the bitCoin market to automatically take trade decisions and raise the user's capital. It relies on the xchange library so it can be used for any exchanger website.


Why BTCWolf?
-------------
In a nutshell, its trading strategy is to sell stock when the price exceeds the one used at buying time, plus a specific threshold dynamically set by the market volatily. Likewise, it buys stock when the exchange rate decreased the one of buying time. Therefore, no money can be lost as a result of performing trading operations.

Both the strategies used and te exchange market are decoupled from the rest of the tool, so it is very easy to use new ones.

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
