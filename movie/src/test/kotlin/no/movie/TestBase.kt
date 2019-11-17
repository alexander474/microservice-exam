package no.movie

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.movie.repository.MovieRepository
import no.dto.MovieDto
import no.movie.MoviesApplication
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.Month

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(MoviesApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var movieRepository: MovieRepository

    val initTestDate = LocalDate.of(1998, Month.SEPTEMBER, 9)!!

    @BeforeEach
    @AfterEach
    fun clean() {

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/movies"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        movieRepository.run {
            deleteAll()
        }

        RestAssured.given().get().then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(0))
    }

    fun getAllMovies(): MutableList<MovieDto>? {
        return RestAssured.given().accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data.list", MovieDto::class.java)
    }
}