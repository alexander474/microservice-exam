package no.breale17.user

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.dto.UserDto
import no.breale17.user.repository.UserRepository
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTest{

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var userRepository : UserRepository


    @BeforeEach
    fun initialize() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.basePath = "/users"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        userRepository.deleteAll()
    }


    @Test
    fun testNeedAdmin(){

        RestAssured.given().get()
                .then()
                .statusCode(401)
    }



    private fun checkSize(n: Int){
        RestAssured.given().auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(n))
    }


    @Test
    fun testCreate(){

        checkSize(0)

        val id = "foo"

        val dto = UserDto(id, "A", "B", "C", "a@a.com")

        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(201)

        checkSize(1)
    }


    @Test
    fun testChangeField(){

        checkSize(0)

        val id = "foo"
        val name = "John"

        val dto = UserDto(id, name, "B", "C", "a@a.com")

        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(201)

        val changed = name + "_foo"

        dto.name = changed

        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(204)

        checkSize(1)

        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("data.name", CoreMatchers.equalTo(changed))
    }



    @Test
    fun testForbiddenToChangeOthers(){

        checkSize(0)

        val first = "foo"

        RestAssured.given().auth().basic(first,"123")
                .contentType(ContentType.JSON)
                .body(UserDto(first, "A", "B", "C", "a@a.com"))
                .put("/$first")
                .then()
                .statusCode(201)

        checkSize(1)


        val second = "bar"

        RestAssured.given().auth().basic(second,"123")
                .contentType(ContentType.JSON)
                .body(UserDto(second, "bla", "bla", "bla", "bla@bla.com"))
                .put("/$second")
                .then()
                .statusCode(201)

        checkSize(2)


        RestAssured.given().auth().basic(first,"123")
                .contentType(ContentType.JSON)
                .body(UserDto(second, "forbidden", "forbidden", "forbidden", "a@a.com"))
                .put("/$second")
                .then()
                .statusCode(403)

        checkSize(2)
    }
}