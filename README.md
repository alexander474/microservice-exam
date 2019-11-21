# microservice-exam

#### Swagger DOC
Run application from test folder and go to endpoint: [swagger-ui](http://localhost:8080/swagger-ui.html#/)
You will be asked for username and password and by default this is:
- username = admin
- password = admin

#### Frontend
Somethimes a site reload is nessesary to get "fresh" information. This is because my main focus was on the backend and not frontend.

I choose to only have one feed in the frontend to display the posts belonging to the logged in user and the posts belonging to the friends.



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
- [ ] When the API starts, it must have some already existing data

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

- [ ] In the GUI, should be possible to...
- [x] ... search/display existing users
- [x] ... register a new user
- [x] ... visualize the current user details
- [x] ... create a new timeline message for the current user
- [x] ... display all timeline messages of a user, sorted by time
- [ ] ... create/accept friendship requests

#### B
- [x] You MUST have security mechanisms in place to protect your REST APIs (e.g., although GET operations might be allowed to everyone, write operations like POST/PUT/PATCH do require authentication and authorization).
- [x] You MUST set up a distributed session-based authentication with Redis, and you MUST setup an API for login/logout/create of users. Note: most of these can be a copy&paste&adapt from the course examples.
- [x] The frontend MUST have mechanisms to signin/signup a user.

- [x] From GUI, must be able to login/logout users. In the backend, this should be handled in a new REST API.
- [x] A logged-in user should see a welcome message
- [x] A user should be able to create new messages only on his/her timeline (add a test to verify it)
- [ ] A user should be able to see the timelines of only his/her friends (add a test to verify it)

#### A
- [ ] Besides the required REST APIs, you MUST also have a GraphQL one. It MUST have at least oneQuery and one Mutation.
- [ ] You MUST have at least one communication relying on AMQP between two different webservices.

- [ ] A new GraphQL API should handle advertisement messages, to show on home-page
- [ ] Every time there is a new friendship request accepted in the REST API, such message should be sent to RabbitMQ. The GraphQL API should subscribe to such events, and use the received information somehow (completely up to you) to decide which ads to show next