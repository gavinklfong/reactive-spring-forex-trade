
# Foreign Currency Trading API Service


## Introduction

This repository was created with the aim of demonstrating the prevailing non-blocking technology of Spring framework WebFlux and the reactive programming style.

It is an API service which serves the use case of foreign currency trading. Here is list of API endpoints:

- [GET] /rates/latest/<currency>  : Fetch the latest foreign currency rates
- [GET] /rates/book : Reserve an exchange rate
- [GET] /deals : Fetch list of deals of specific customer
- [POST] /deals : Submit a new foreign exchange deal

## Dependency

### Libraries

Maven pom.xml was generated on Spring Initializr with the following dependent libraries:

- **Spring WebFlux** - Web Framework supports non-block operations
- **Spring Data JPA** - Persistent data access
- **Spring Validation** - Validation definition using annotations
- **H2 Database** - In-memory database for testing

### External Service

To retrieve the latest currency exchange rate, service consume external API [ https://api.exchangeratesapi.io]

## Build & Run
	
This is Maven project, you can clone this repository to your local machine and then use maven command to build and running this application.

You can follow the commands below:

**Build:**
`mvn install`

**Run:**
`mvn spring-boot:run`

	
## Use Case


The Use Case is a foreign currency trade service. Due to a constant fluctuation of currency prices, customers are only informed of the exact exchange rate upon completion of transaction posting. Some people are not comfortable with making a deal without knowing the cost before making a deal. 

In order to offer a better customer experience, some financial institutions allow customers to reserve an exchange rate so that customers are well informed of the rate prior to transaction submission. Certainly, such rate booking is only valid within a short period of time for sake of risk management. In addition, different preferential rates would be offered to customers with different account tiers.

The use case diagram below shows the simplified version of forex trade service with 4 functions:

- Retrieve the latest forex rates

- Book forex rate

- Post forex trade deal. it includes rate booking as customers need to have a valid rate booking prior to transaction submission.

- Retrieve list of forex transaction of a customer


![alt Use Case Diagram](./blob/Reactive Spring Boot - Forex Trade-Use Case.jpg)


## Technical Design

I published an article in medium.com for about the high level technical design of this system. You can refer to this link here:

[Spring — A Faster Way To Build Production-Ready API in a Well- Defined Structure](https://medium.com/dev-genius/spring-a-faster-way-to-build-production-ready-api-in-a-well-defined-structure-5b1730fa81dd)

### Configuration

Application specific configuration resides in spring boot default configuration file - applicaton.yaml

- **[app.rate-booking-duration]** - Valid period of rate booking in second.
- **[app.default-base-currency]** - Default base currency.
- **[app.forex-rate-api-url]** - External API service for currency rate retrieval

### Data Model

There are 3 entities Customer, Rate Booking and Trade Deal with one-to-many relationship.

![alt Data Model](./blob/Reactive Spring Boot - Forex Trade-Data Model.jpg)


### Components

The whole system consists of controllers, services and repositories. Each type component is assigned with a distinct role:

- **Controller**:  Expose business service to HTTP and handle all system logic related to HTTP protocol
- **Service**: Integrate various component to business logic
- **Repository**: The interface for access to persistent store


![alt Component Diagram](./blob/Reactive Spring Boot - Forex Trade-Component.jpg)


### Data Flow

Illustration of the interaction between components is shown in the data flow diagram below

![alt Component Diagram](./blob/Reactive Spring Boot - Forex Trade-Activity - Forex Deal.jpg)


## Testing

### Unit Test

Unit test code is built for each components. To isolate the target component, all other components the target depends on are mocked. In order to speed test execution, sliced spring context is loaded, meaning that only necessary spring bean will be initialized and injected.

**Controller**

- Service component is mocked for testing on controller
- Use of @WebFluxTest annotation to loaded sliced spring context with the target controller class

![alt Unit Test for Controller](./blob/Reactive Spring Boot - Forex Trade-Testing - Unit Test - Controller.jpg)

**Service**

- Repository component is mocked for testing on service.
- Use of @SpringJUnitConfig and @ContextConfiguration annotation to loaded sliced spring context with the target service class

![alt Unit Test for Service](./blob/Reactive Spring Boot - Forex Trade-Testing - Unit Test - Service.jpg)

**Repository**

- In-memory database will be used for testing on repository. 
- Use of @DataJpaTest annotation to load sliced spring context

![alt Unit Test for Repository](./blob/Reactive Spring Boot - Forex Trade-Testing - Unit Test - Repository.jpg)


### Integration Test

Next, end-to-end test is executed as if a real application. Thus, spring context is fully loaded using @SpringBootTest annotation

WebTestClient is used as API client which triggers HTTP requests to API endpoints and verify the actual result.
