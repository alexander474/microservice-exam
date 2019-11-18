package no.breale17.authentication

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.authentication.repository.UserRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

/**
 * Created by arcuri82 on 10-Nov-17.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(SecurityTest.Companion.Initializer::class)])
class SecurityTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @LocalServerPort
    private var port = 0


    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @ClassRule
        @JvmField
        val redis = KGenericContainer("redis:latest")
                .withExposedPorts(6379)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                val host = redis.containerIpAddress
                val port = redis.getMappedPort(6379)

                TestPropertyValues
                        .of("spring.redis.host=$host", "spring.redis.port=$port")
                        .applyTo(configurableApplicationContext.environment);
            }
        }
    }


    @Before
    fun initialize() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/auth"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        userRepository.deleteAll()
    }


    @Test
    fun testUnauthorizedAccess() {

        RestAssured.given().get("/user")
                .then()
                .statusCode(401)
    }


    /**
     *   Utility function used to create a new user in the database
     */
    private fun registerUser(id: String, password: String): String {


        val sessionCookie = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthenticationDto(id, password))
                .post("/signUp")
                .then()
                .statusCode(204)
                .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                .extract().cookie("SESSION")

        /*
            From now on, the user is authenticated.
            I do not need to use userid/password in the following requests.
            But each further request will need to have the SESSION cookie.
         */

        return sessionCookie
    }

    private fun checkAuthenticatedCookie(cookie: String, expectedCode: Int){
        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedCode)
    }

    @Test
    fun testLogin() {

        val name = "foo"
        val pwd = "bar"

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = registerUser(name, pwd)

        RestAssured.given().get("/user")
                .then()
                .statusCode(401)

        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("data.name", CoreMatchers.equalTo(name))
                .body("data.roles", Matchers.contains("ROLE_USER"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = RestAssured.given().auth().basic(name, pwd)
                .get("/user")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("data.name", CoreMatchers.equalTo(name))
                .body("data.roles", Matchers.contains("ROLE_USER"))
                .extract().cookie("SESSION")

        Assert.assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        Assert.assertNotEquals(login, cookie)
        Assert.assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }



    @Test
    fun testWrongLogin() {

        val name = "foo"
        val pwd = "bar"

        val noAuth = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        //session is not created if not required, eg when 400 user error
        Assert.assertNull(noAuth)

        registerUser(name, pwd)

        val auth = RestAssured.given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }
}