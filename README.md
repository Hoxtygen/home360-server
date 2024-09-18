# Home360

Home360 is an online real estate web application. It allows users to rent both private and commercially available houses
and apartment.

### Built with

* [Spring Boot 3.0](https://spring.io/projects/spring-boot)
* [Spring Security](https://spring.io/projects/spring-security)
* [JSON Web Token](https://jwt.io/introduction)
* [Maven](https://maven.apache.org/)
* [MongoDB](https://www.mongodb.com/)
* [PostgreSQL](https://www.postgresql.org/)
* [Mailgun](https://www.mailgun.com/)

### Features

* User registration
* User login with JWT authentication
* Password reset
* Refresh token
* Password change
* Account verification
* Listing creation
* Listing deletion
* Search listings
* Fetch listings
* Make listing enquiry
* Mark listing enquiry as read
* Fetch listing enquiries
* In-app messaging

### Getting Started

To get started with this project you'd need to have the following installed on your local computer:

* JDK 17+
* Maven 3+
* PostgreSQL
* MongoDB

To build and run the project follow the steps below

* Clone the repository [https://github.com/Hoxtygen/home360-server.git](https://github.com/Hoxtygen/home360-server.git)
* Open a terminal and navigate to the project directory: `cd home360`
* Add home360_dev to your local Postgres database
* Add home360dev to your local MongoDB database
* Create a `.env` file and add the following

```
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DATABASE=home360_dev
POSTGRES_LOCAL_PORT=
POSTGRES_DOCKER_PORT=

MONGODB_USER=
MONGODB_PASSWORD=
MONGODB_DATABASE=home360dev
MONGODB_LOCAL_PORT=27017
MONGODB_DOCKER_PORT=27017

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
```

* Create a `application-dev.yml` file and add to the resources' directory. Check the `application-sample-dev.yml` for
  content to fill it up with.
* Build the project : `mvn clean install`
* Run the project: `mvn spring-boot:run`

## Documentation

* Application is available at [http://localhost:8080](http://localhost:8080/api)
* Documentation is available at [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)
* The client can be found at [home360 client](https://github.com/Hoxtygen/home360-frontend)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Author

* [Wasiu Idowu](https://github.com/Hoxtygen)
