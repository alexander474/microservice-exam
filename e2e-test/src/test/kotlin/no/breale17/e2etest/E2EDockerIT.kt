/**
 * Got inspiration from
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayIntegrationDockerTestBase.kt
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/microservice/gateway/gateway-e2e-tests/src/test/kotlin/org/tsdes/advanced/microservice/gateway/e2etests/GatewayRestIT.kt
 */
package no.breale17.e2etest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.breale17.dto.PostDto
import no.breale17.dto.UserDto
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.contains
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

    private fun createUserWithCookie(cookie: String, id: String, name: String, middleName: String, surname: String, email: String) {
        val dto = UserDto(id, name, middleName, surname, email)

        RestAssured.given().cookie("SESSION", cookie)
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/api/v1/users/$id")
                .then()
                .statusCode(201)
    }

    private fun createPostWithCookie(cookie: String, id: String, title: String, message: String) {
        val dto = PostDto(title, message)

        RestAssured.given().cookie("SESSION", cookie)
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/api/v1/posts")
                .then()
                .statusCode(201)
    }

    private fun getAllPosts(cookie: String): MutableList<PostDto>? {
        return RestAssured.given().cookie("SESSION", cookie)
                .accept(ContentType.JSON)
                .get("/api/v1/posts")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data.list", PostDto::class.java)
    }


    @Test
    fun testUnauthorizedAccess() {

        given().get("/api/v1/auth/user")
                .then()
                .statusCode(401)
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
    fun testCreateUser() {
        val id = createUniqueId()
        val pwd = createUniqueId()

        val name = createUniqueId()
        val middleName = createUniqueId()
        val surname = createUniqueId()
        val email = createUniqueId() + "@" + createUniqueId() + ".no"

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

        //createUser(id, pwd, name, middleName, surname, email)
        createUserWithCookie(cookie, id, name, middleName, surname, email)

        RestAssured.given().cookie("SESSION", cookie)
                .get("api/v1/users/$id")
                .then()
                .statusCode(200)
                .body("data.name", CoreMatchers.equalTo(name))
                .body("data.middleName", CoreMatchers.equalTo(middleName))
                .body("data.surname", CoreMatchers.equalTo(surname))
                .body("data.email", CoreMatchers.equalTo(email))
                .body("data.friends.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCreatePost() {
        val id = createUniqueId()
        val pwd = createUniqueId()

        val name = createUniqueId()
        val middleName = createUniqueId()
        val surname = createUniqueId()
        val email = createUniqueId() + "@" + createUniqueId() + ".no"

        val title = createUniqueId()
        val message = createUniqueId()

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

        //createUser(id, pwd, name, middleName, surname, email)
        createUserWithCookie(cookie, id, name, middleName, surname, email)
        val allPosts = getAllPosts(cookie)

        RestAssured.given().cookie("SESSION", cookie)
                .get("api/v1/users/$id")
                .then()
                .statusCode(200)
                .body("data.name", CoreMatchers.equalTo(name))
                .body("data.middleName", CoreMatchers.equalTo(middleName))
                .body("data.surname", CoreMatchers.equalTo(surname))
                .body("data.email", CoreMatchers.equalTo(email))
                .body("data.friends.size()", CoreMatchers.equalTo(0))

        createPostWithCookie(cookie, id, title, message)

        RestAssured.given().cookie("SESSION", cookie)
                .get("api/v1/posts")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(allPosts?.size?.plus(1)))

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