package no.breale17.movie

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.dto.MovieDto
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

class MovieApiTest : TestBase(){

    @Test
    fun testNotAuthenticated(){
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
    fun testGetNonExistingMovie() {
        val id = "foo"
        RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .get("-1")
                .then()
                .statusCode(404)
                .body("code", CoreMatchers.equalTo(404))
                .body("message", CoreMatchers.not(CoreMatchers.equalTo(null)))
    }

    @Test
    fun testCreateMovie() {
        val id = "foo"
        val title = "TestMovie"
        val description = "TestDescription"
        val price = 299
        val allMovies = getAllMovies()

        val location = RestAssured.given().auth().basic(id, "123").contentType(ContentType.JSON)
                .body(MovieDto(title, description, initTestDate.toString(), setOf("action"), 0, 0.0.toString(), price.toString()))
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
                .body("data.description", CoreMatchers.equalTo(description))

        RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(allMovies!!.size + 1))
    }



}