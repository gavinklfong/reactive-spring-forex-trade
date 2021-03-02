Feature: Forex Trade Service
	Verify Forex Trade Service Functionalities
	
	Scenario: Submit a forex trade deal
		Given API Service is started
		And I request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And  I should receive a valid rate booking
		When  I submit a forex trade deal with rate booking and parameters: '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		Then I should get the forex trade deal successfully posted
		Examples:
		| baseCurrency| counterCurrency | baseCurrencyAmount| customerId |
		| GBP 				| USD 						| 1000							| 1 				 |
		| USD 				| GBP 						| 250								| 1 				 |
		| CAD 				| USD 						| 2000							| 1 				 |
		| PHP 				| USD 						| 3000							| 1 				 |
		| HKD 				| USD 						| 1500							| 1 				 |
		| GBP 				| HKD 						| 100000						| 1 				 |
		
	Scenario: Retrieve trade deal by customer
		Given API Service is started
		And I request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And I should receive a valid rate booking
		And I submit a forex trade deal with rate booking and parameters: '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		And I should get the forex trade deal successfully posted
		When I request for forex trade deal by <customerId>
		Then I should get a list of forex trade deal for <customerId>
		Examples:
		| baseCurrency| counterCurrency | baseCurrencyAmount| customerId |
		| GBP 		  | USD 			| 1000				| 1 		 |
		| USD 		  | GBP 			| 250				| 1 		 |
		
