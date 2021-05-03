
Feature: Rate Service
	Verify Rate Service Functionalities
	
	Scenario Outline: Fetch the latest rate for base currency '<baseCurrency>'
		Given API Service is started
		When I request for the latest rate for base currency '<baseCurrency>'
		Then I should receive a list of currency rates
		Examples:
		| baseCurrency |
		| GBP |
		| USD |
	
	Scenario Outline: Make a rate booking
		Given API Service is started
		When I request for a rate booking with parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		Then I should receive a rate booking with expiry time in the future
		Examples:
		| tradeAction 	| baseCurrency	| counterCurrency 	| baseCurrencyAmount	| customerId |
		|  BUY			| GBP 			| USD 				| 1000					| 1 		 |
		|  BUY			| USD 			| JPY 				| 250					| 1 		 |
		|  SELL			| CAD 			| CHF 				| 2000					| 1 		 |
		|  SELL			| GBP 			| USD 				| 3000					| 1 		 |
		|  SELL			| EUR 			| USD 				| 1500					| 1 		 |
		|  BUY			| EUR 			| GBP 				| 100000				| 1 		 |