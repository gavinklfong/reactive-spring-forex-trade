
Feature: Rate Service
	Verify Rate Service Functionalities
	
	Scenario: Fetch the latest rate with base currency '<baseCurrency>'
		Given API Service is started
		When I request for the latest rate with base currency '<baseCurrency>'
		Then I should receive list of currency rate
		Examples:
		| baseCurrency |
		| GBP |
	
	Scenario: Make a rate booking
		Given API Service is started
		When I request for a rate booking with parameters: '<tradeAction>', '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		Then I should receive a valid rate booking
		Examples:
		| tradeAction 	| baseCurrency	| counterCurrency 	| baseCurrencyAmount	| customerId |
		|  BUY			| GBP 			| USD 				| 1000					| 1 		 |
		|  BUY			| USD 			| JPY 				| 250					| 1 		 |
		|  SELL			| CAD 			| CHF 				| 2000					| 1 		 |
		|  SELL			| GBP 			| USD 				| 3000					| 1 		 |
		|  SELL			| EUR 			| USD 				| 1500					| 1 		 |
		|  BUY			| EUR 			| GBP 				| 100000				| 1 		 |
		