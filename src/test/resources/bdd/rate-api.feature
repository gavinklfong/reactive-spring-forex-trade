
@BDD
Feature: Rate Service
	Verify Rate Service Functionalities
	
	Scenario: Fetch the latest rate with base currency GBP
		Given API Service is started
		When I request for the latest rate with base currency GBP
		Then I should receive list of currency rate