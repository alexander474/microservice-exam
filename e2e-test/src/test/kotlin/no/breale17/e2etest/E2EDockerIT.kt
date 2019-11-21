package no.breale17.e2etest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.util.concurrent.TimeUnit

@Testcontainers
class E2EDockerIT {

    companion object {


        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)


        @Container
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)
        //if needed for debugging
        //.withLogConsumer("post") {System.out.println("[DOCKER] " + it.utf8String)}

        private var counter = System.currentTimeMillis()

        @BeforeAll
        @JvmStatic
        fun initialize() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 80
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()


            await().atMost(300, TimeUnit.SECONDS)
                    .pollInterval(6, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {

                        given()
                                .get("/api/v1/auth/user")
                                .then()
                                .statusCode(401)

                        given()
                                .get("/api/v1/users")
                                .then()
                                .statusCode(401)

                        given()
                                .get("/api/v1/posts")
                                .then()
                                .statusCode(401)

                        given()
                                .get("/api/v1/users/userCount")
                                .then()
                                .statusCode(200)

                        true
                    }
        }
    }

    @Test
    fun testUnauthorizedAccess() {

        given().get("/api/v1/auth/user")
                .then()
                .statusCode(401)
    }

    private fun registerUser(id: String, password: String): String {

        val sessionCookie = given().contentType(ContentType.JSON)
                .body("""
                    {"userId": "$id", "password": "$password"}
                """.trimIndent())
                .post("/api/v1/auth/signUp")
                .then()
                .statusCode(204)
                .header("Set-Cookie", not(equalTo(null)))
                .cookie("SESSION")
                .extract().cookie("SESSION")

        return sessionCookie
    }

    private fun createUniqueId(): String {
        counter++
        return "foo_$counter"
    }

    @Test
    fun testLogin() {

        val id = createUniqueId()
        val pwd = "bar"

        val cookie = registerUser(id, pwd)

        given().get("/api/v1/auth/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/api/v1/auth/user")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(id))
                .body("data.roles", contains("ROLE_USER"))


        given().auth().basic(id, pwd)
                .get("/api/v1/auth/user")
                .then()
                .statusCode(200)
                .cookie("SESSION")
                .body("data.name", equalTo(id))
                .body("data.roles", contains("ROLE_USER"))

        given().contentType(ContentType.JSON)
                .body("""
                    {"userId": "$id", "password": "$pwd"}
                """.trimIndent())
                .post("/api/v1/auth/login")
                .then()
                .statusCode(204)
                .cookie("SESSION")
    }

    @Test
    fun testForbiddenToChangeOthers() {

        val firstId = createUniqueId()
        val firstCookie = registerUser(firstId, "123")
        val firstPath = "/api/v1/users/$firstId"

        /*
            In general, it can make sense to have the DTOs in their
            own module, so can be reused in the client directly.
            Otherwise, we would need to craft the JSON manually,
            as done in these tests
         */

        given().cookie("SESSION", firstCookie)
                .get("/api/v1/auth/user")
                .then()
                .statusCode(200)
                .body("data.name", equalTo(firstId))
                .body("data.roles", contains("ROLE_USER"))


        given().cookie("SESSION", firstCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$firstId",
                        "name": "A",
                        "surname": "B",
                        "email": "a@a.com"
                    }
                    """)
                .put(firstPath)
                .then()
                .statusCode(201)


        val secondId = createUniqueId()
        val secondCookie = registerUser(secondId, "123")
        val secondPath = "/api/v1/users/$secondId"

        given().cookie("SESSION", secondCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId",
                        "name": "bla",
                        "surname": "bla",
                        "email": "bla@bla.com"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(201)



        given().cookie("SESSION", firstCookie)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "userId": "$secondId"
                    }
                    """)
                .put(secondPath)
                .then()
                .statusCode(403)
    }

}