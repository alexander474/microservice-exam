# microservice-exam
 - Exam text can be found [here](./doc/PG6101_Hjemmeeksamen.pdf)
 - Kandidat nr: 5019

#### How to run?
First make sure that no other process is running on port 80 or 8080 if you are running only one service locally

1. `mvn clean package` or `mvn package -DskipTests` (This will skip tests)
2. `docker-compose up --build`
3. frontend will be accessible from [localhost:80](http://localhost/)

To run only one microservice: Run local application runner in the test folders
Then access either the API or swagger doc
[localhost:8080](http://localhost:8080/) - [swagger-ui](http://localhost:8080/swagger-ui.html#/)

To run all tests: `mvn clean verify`

#### Swagger DOC
Run application from test folder and go to endpoint: [swagger-ui](http://localhost:8080/swagger-ui.html#/)
If you are asked for login you can apply this information:
- username = admin
- password = admin

#### Service duplication and ribbon
 - Post service is running on two instances

#### Authentication
I have set up distributed sessions that share the same authentication. This means that you can authenticate at one service and use for example
same cookie to authenticate on the other services.

#### Frontend
Sometimes a site reload is necessary to get "fresh" information. This is because my main focus was on the backend and not frontend.

I choose to only have one feed in the frontend to display the posts belonging to the logged in user and the posts belonging to the friends.

default login:
- admin:
    - username = a
    - password = a
- user:
    - username = b
    - password = b

##### Frontend flow
First thing when accessing the frontend is welcoming page, here can you choose to login or register. [homepage_not_loggedin](./doc/images/not_loggedIn_Homepage.png)
 - if you login then you will get access to the "feed"-page [login](./doc/images/login_page.png)
 - if you register then you will first create the user information before getting to the "feed"-page [register](./doc/images/register_page.png)

When you are logged in: [homepage_loggedin](./doc/images/loggedIn_Homepage.png)
 - top left: current user information
 - left second: form to create a new post
 - left third: all posts that is posted by either you or you're friends.
 - top right: all users that is registered. You can only access basic and public user information
 - right second: your friends
 - right second: friend requests that has been sent
 - right third: friend request received


#### Coverage (IntelliJ)
 - Authentication: 91%
 - User: 79%
 - Post: 80%

plus E2E-tests (E2E-test wont give me a coverage)

#### Extras
 - Used shared modules for dto and utils to achieve more DRY (Do not Repeat Yourself) code.
 - Splitted the whole project into several microservices that is independent of each other.


#### Json-Merge-Patch
Example of json merge patch can be seen at [./user/src/main/kotlin/no/breale17/user/api/UserApi.kt](./user/src/main/kotlin/no/breale17/user/api/UserApi.kt)

#### Known issues
One issue is that after some events the frontend wont rerender so the information wont update, if this happens just reload the page.
since we are focusing on the backend i had to let this problem pass because of the time left on the exam.

Also the application runner in authentication module wont run every time. Tried to solve it before i had to move on.

#### Graphql
Started implementing it, but didn't have time to complete and therefore had to remove it from the project to make the code compile.
 

## Tasks
I have put a checked mark (x if you read it as text) on every task that i have done.

#### E
- [x] Write a new REST API using SpringBoot and Kotlin.
- [x] Have AT LEAST one endpoint per main HTTP method, i.e., GET, POST, PUT, PATCH and DELETE.
- [x] PATCH MUST use the JSON Merge Patch format.
- [x] Each endpoint MUST use Wrapped Responses.
- [x] Endpoints returning collections of data MUST use Pagination, unless you can convincedly argue (in code comments) that they do not deal with large quantity of data, and the size is always small and bounded. Example: an endpoint that returns the top 10 players in a leader-board for a game does not need to use Pagination.
- [x] MUST provide Swagger documentation for all your endpoints.
- [x] Write AT LEAST one test with RestAssured per each endpoint.
- [x] Add enough tests (unit or integration, it is up to you) such that, when they are run from IntelliJ, they MUST achieve AT LEAST a 70% code coverage.
- [x] If the service communicates with another REST API, you need to use WireMock in the integration tests to mock it.
- [x] You MUST provide a LocalApplicationRunner in the test folder which is able to run the REST AP independently from the whole microservice. If such REST API depends on external services (e.g., Eureka), those communications can be deactivated or mocked out (or simply live with the fact that some, but not all, endpoints will not work). It is ESSENTIAL that an examiner MUST be able to start such class with simply a right-click on an IDE (e.g., IntelliJ), and then see the Swagger documentation when opening http://localhost:8080/swagger-ui.html in a browser. In “production” mode, the API MUST be configured to connect to a PostgreSQL database. During testing, you can use an embedded database (e.g., H2), and/or start the actual database with Docker.
- [x] You MUST use Flyway for migration handling (e.g., for the creation of the database schema).
- [x] Configure Maven to build a self-executable uber/fat jar for the service.
- [x] Write a Docker file for the service.

- [x] REST API to handle user details: e.g., name, surname and email address.
- [x] Need to handle “friendship” requests: eg, a user asking another for friendship, and this other deciding whether to accept it or not
- [x] Need to be able to create new messages on the “timeline” of a user
- [x] When the API starts, it must have some already existing data

#### D
- [x] Your microservices MUST be accessible only from a single entry point, i.e., an API Gateway.
- [x] Your whole application must be started via Docker-Compose. The API Gateway MUST be the only service with an exposed port.
- [x] You MUST have at least one REST API service that is started more than once (i.e., more than one instance in the Docker-Compose file), and load-balanced with Eureka/Ribbon.
- [x] In Docker-Compose, MUST use real databases (e.g., PostgreSQL) instead of embedded ones (e.g., H2) directly in the services.
- [x] You MUST have at least 1 end-to-end test for each REST API using Docker-Compose starting the whole microservice.

#### C
- [x] You MUST provide a frontend for your application. You can choose whatever framework and language you want, although React is the recommended one.
- [x] You need to make sure that all the major features in your application are executable from the frontend.
- [x] Note: there is no requirement on the design of the pages. However, a bit of CSS to make the pages look a bit nicer will be appreciated and positively evaluated.

- [x] In the GUI, should be possible to...
- [x] ... search/display existing users
- [x] ... register a new user
- [x] ... visualize the current user details
- [x] ... create a new timeline message for the current user
- [x] ... display all timeline messages of a user, sorted by time (Im sorting by id, this is incremental andi choose therefore to sort on id)
- [x] ... create/accept friendship requests

#### B
- [x] You MUST have security mechanisms in place to protect your REST APIs (e.g., although GET operations might be allowed to everyone, write operations like POST/PUT/PATCH do require authentication and authorization).
- [x] You MUST set up a distributed session-based authentication with Redis, and you MUST setup an API for login/logout/create of users. Note: most of these can be a copy&paste&adapt from the course examples.
- [x] The frontend MUST have mechanisms to signin/signup a user.

- [x] From GUI, must be able to login/logout users. In the backend, this should be handled in a new REST API.
- [x] A logged-in user should see a welcome message
- [x] A user should be able to create new messages only on his/her timeline (add a test to verify it)
- [x] A user should be able to see the timelines of only his/her friends (add a test to verify it)

#### A
- [ ] Besides the required REST APIs, you MUST also have a GraphQL one. It MUST have at least oneQuery and one Mutation.
- [ ] You MUST have at least one communication relying on AMQP between two different webservices.

- [ ] A new GraphQL API should handle advertisement messages, to show on home-page
- [ ] Every time there is a new friendship request accepted in the REST API, such message should be sent to RabbitMQ. The GraphQL API should subscribe to such events, and use the received information somehow (completely up to you) to decide which ads to show next

## Sources
If i have used some inspiration from external sources there would be marked in the top of the file.
All of the pom.xml files, docker files & docker-compose.yml file have almost been copy & paste from the github curriculum.
Most of my inspiration is from the curriculum at github [https://github.com/arcuri82/testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems)