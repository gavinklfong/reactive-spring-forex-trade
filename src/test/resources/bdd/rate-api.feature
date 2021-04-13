
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
		When I request for a rate booking with parameters: '<baseCurrency>', '<counterCurrency>', <baseCurrencyAmount>, <customerId>
		Then I should receive a valid rate booking
		Examples:
		| baseCurrency| counterCurrency | baseCurrencyAmount| customerId |
		| GBP 				| USD 						| 1000							| 1 				 |
		| USD 				| GBP 						| 250								| 1 				 |
		| CAD 				| USD 						| 2000							| 1 				 |
		| PHP 				| USD 						| 3000							| 1 				 |
		| HKD 				| USD 						| 1500							| 1 				 |
		| GBP 				| HKD 						| 100000						| 1 				 |
		