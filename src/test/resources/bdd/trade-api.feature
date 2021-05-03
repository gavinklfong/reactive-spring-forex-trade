Feature: Forex Trade Service
	Verify Forex Trade Service Functionalities
	
	Scenario: Submit a forex trade deal
		Given API Service is started
		And  I request for a rate booking with parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And  I should receive a rate booking with expiry time in the future
		When I submit a forex trade deal with rate booking and parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		Then I should get the forex trade deal successfully posted
		Examples:
		| tradeAction 	| baseCurrency	| counterCurrency 	| baseCurrencyAmount	| customerId |
		|  BUY			| GBP 			| USD 				| 1000					| 1 		 |
		|  BUY			| USD 			| JPY 				| 250					| 1 		 |
		|  SELL			| CAD 			| CHF 				| 2000					| 1 		 |
		|  SELL			| GBP 			| USD 				| 3000					| 1 		 |
		|  SELL			| EUR 			| USD 				| 1500					| 1 		 |
		|  BUY			| EUR 			| GBP 				| 100000				| 1 		 |
		
	Scenario: Retrieve trade deal by customer
		Given API Service is started
		And I request for a rate booking with parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And I should receive a rate booking with expiry time in the future
		And I submit a forex trade deal with rate booking and parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And I should get the forex trade deal successfully posted
		When I request for forex trade deal by <customerId>
		Then I should get a list of forex trade deal for <customerId>
		Examples:
		| tradeAction | baseCurrency| counterCurrency | baseCurrencyAmount| customerId |
		| BUY 		  | GBP 		  | USD 			| 1000				| 1 		 |
		| SELL		  | USD 		  | JPY 			| 250				| 1 		 |
		
