package no.breale17.post

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.dto.PostDto
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

class PostApiTest : TestBase(){

    @Test
    fun testNotAuthenticated(){

        val res = getAMockedJsonResponse("a", "a", "a", "a", "a@a.com", "")

        stubJsonResponse(res,"a")

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
        val title = "TestMovie"
        val message = "TestDescription"
        val allPosts = getAllPosts()

        val location = RestAssured.given().auth().basic(id, "123").contentType(ContentType.JSON)
                .body(PostDto(title, message, initTestDate.toString()))
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("location")


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



}