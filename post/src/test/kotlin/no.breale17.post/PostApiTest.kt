/**
 * Got inspiration from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/microservice/gateway/gateway-service/src/test/kotlin/org/tsdes/advanced/microservice/gateway/service/ServiceApplicationTest.kt
 */
package no.breale17.post

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

class PostApiTest : TestBase() {

    @Test
    fun testNotAuthenticated() {

        val res = getAMockedJsonResponse("a", "a", "a", "a", "a@a.com", "", "", "")

        stubJsonResponse(res, "a")

        RestAssured.given().get()
                .then()
                .statusCode(401)
    }

    @Test
    fun testCleanDB() {
        val id = "foo"
        RestAssured.given().auth().basic(id, "123").get().then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testGetNonExistingPost() {
        val id = "foo"
        RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .get("-1")
                .then()
                .statusCode(404)
                .body("code", CoreMatchers.equalTo(404))
                .body("message", CoreMatchers.not(CoreMatchers.equalTo(null)))
    }

    @Test
    fun testCreatePost() {
        val id = "foo"
        val password = "123"
        val title = "TestMovie"
        val message = "TestDescription"
        val allPosts = getAllPosts(id, password)

        val location = createPost(id, password, title, message)

        RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .basePath("")
                .get(location)
                .then()
                .statusCode(200)
                .body("data.title", CoreMatchers.equalTo(title))
                .body("data.message", CoreMatchers.equalTo(message))
                .body("data.userId", CoreMatchers.equalTo(id))

        RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(allPosts!!.size + 1))
    }

    @Test
    fun testDeletePost() {
        val id = "foo"
        val password = "123"
        val title = "TestMovie"
        val message = "TestDescription"
        val allPosts = getAllPosts(id, password)

        val location = createPost(id, password, title, message)

        val postId= RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .basePath("")
                .get(location)
                .then()
                .statusCode(200)
                .body("data.title", CoreMatchers.equalTo(title))
                .body("data.message", CoreMatchers.equalTo(message))
                .body("data.userId", CoreMatchers.equalTo(id))
                .extract()
                .jsonPath().getLong("data.id")

        RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(allPosts!!.size + 1))

        RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .delete("/$postId")
                .then()
                .statusCode(204)
    }

    @Test
    fun testDeletePostThatDoesNotExist() {
        val id = "foo"
        val password = "123"

        RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .delete("/-1")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeletePostWithIllegalId() {
        val id = "foo"
        val password = "123"

        RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .delete("/thisIsNotANumber")
                .then()
                .statusCode(400)
    }

    @Test
    fun testSeeFriendsPost() {
        val id = "foo"
        val id2 = "bar"
        val password = "123"
        val title = "TestMovie"
        val message = "TestDescription"
        val allPosts = getAllPosts(id, password, "\"${id2}\"")

        val location = createPost(id2, password, title, message)

        RestAssured.given().auth().basic(id2, password).accept(ContentType.JSON)
                .basePath("")
                .get(location)
                .then()
                .statusCode(200)
                .body("data.title", CoreMatchers.equalTo(title))
                .body("data.message", CoreMatchers.equalTo(message))
                .body("data.userId", CoreMatchers.equalTo(id2))

        RestAssured.given().auth().basic(id, password).accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(allPosts!!.size + 1))
    }


}