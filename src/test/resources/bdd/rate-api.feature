
@BDD
Feature: Rate Service
	Verify Rate Service Functionalities
	
	Scenario: Fetch the latest rate with base currency '<baseCurrency>'
		Given API Service is started
		When I request for the latest rate with base currency '<baseCurrency>'
		Then I should receive list of currency rate
		And '<baseCurrency>' rate is 1
		Examples:
		| baseCurrency |
		| GBP |
