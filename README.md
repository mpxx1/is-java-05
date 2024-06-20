# Lab-5 

## [Other labs](https://github.com/mpxx1/is-java-tech)

## Lab-1
## Learning Java syntax and a new environment.

This lab requires you to rewrite Lab 4 from the previous semester.

You must use Javadoc and generate html documentation.

The testing framework is recommended to be JUnit.

The build system is at the student's choice: Gradle/Maven.

## Lab-2
## You need to write a service for accounting for cats and their owners.

Existing information about cats:

    Name
    Date of birth
    Breed
    Color (one of the predefined options)
    Owner
    List of cats that this cat is friends with (from those presented in the database)

Existing information about owners:

    Name
    Date of birth
    List of cats

The service must implement the Controller-Service-Dao architecture.

All information is stored in the PostgreSQL database. Hibernate should be used to connect to the database.

The project should be built using Maven or Gradle (at the student's choice).
The data access layer and the service layer should be two different Maven/Gradle modules. The project should be built entirely with one command.

When testing, it is recommended to use Mockito to avoid connecting to real databases.

The testing framework is recommended to be JUnit.

In this lab, you cannot use Spring or similar frameworks.

## Lab-3
## Spring is added to the service created in the previous lab.

The service should provide an HTTP interface (REST API) for retrieving information about specific cats and owners, and for retrieving filtered information (e.g., get all ginger cats).

Attention: it is unacceptable to return JPA entities through the HTTP interface. It is recommended to create separate wrapper classes.

Services and DAOs should become Spring Beans using Dependency Injection (Autowired). DAOs in this case inherit from JpaRepository and have template Spring Data JPA methods.

When submitting the lab, you will need to demonstrate the functionality of the endpoints through HTTP requests (it is recommended to use Postman).

## Lab-4
## Owners are unhappy that anyone can get information about cats. In this lab, we will add authorization to the service.

An administrator role is added. It has access to all methods and can create new users. The user is linked to the owner in a 1:1 ratio.

Methods for retrieving information about cats and owners should be protected by Spring Security. Only cat owners and administrators have access to the corresponding endpoints. All authorized users have access to filtering methods, but they only receive data about their cats on the output.

Attention: the endpoints created in the previous step should not be deleted.

## Lab-5
## The business read an article about how microservices are cool and asked us to break the program into microservices.

Three microservices are extracted from the created application:

    Cat access microservice.
    Owner access microservice.
    Microservice with external interfaces.

They are all different applications.

All previously created endpoints and authorization move to the third microservice.

Communication between microservices occurs through RabbitMQ/Kafka (at the student's choice).

Cat access and owner access services can either be connected to the same database or have different databases. In the second case, it is unacceptable to make one request to retrieve data from two databases, there should be two requests (one to each).

Attention: it is unacceptable to transfer JPA entities through RabbitMQ/Kafka. It is recommended to create separate wrapper classes.

It is recommended to separate the module with JPA entities into a separate pluggable module.

