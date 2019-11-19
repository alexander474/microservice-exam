package no.breale17.post

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.dto.PostDto
import no.breale17.post.repository.PostRepository
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.Month

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(PostsApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var postRepository: PostRepository


    val initTestDate = LocalDate.of(2019, Month.NOVEMBER, 19)!!

    companion object {

        private lateinit var wiremockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun initClass(){
            // RestAssured configs shared by all the tests
            RestAssured.baseURI = "http://localhost"
            RestAssured.basePath = "/posts"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            wiremockServer = WireMockServer(
                    WireMockConfiguration.wireMockConfig()
                            .port(8099).notifier(ConsoleNotifier(true)))
            wiremockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wiremockServer.stop()
        }

    }

    @BeforeEach
    @AfterEach
    fun clean() {
        RestAssured.port = port
        Companion.wiremockServer.resetAll()

        postRepository.run {
            deleteAll()
        }
        val id = "foo"
        val res = getAMockedJsonResponse(id,"a","a","a","a@a.com","")
        stubJsonResponse(res,id)

        RestAssured.given().auth().basic(id, "123").get().then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(0))
    }


    fun getAllPosts(): MutableList<PostDto>? {
        val id = "foo"
        val res = getAMockedJsonResponse(id,"a","a","a","a@a.com","")
        stubJsonResponse(res,id)
        return RestAssured.given().auth().basic(id, "123").accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data.list", PostDto::class.java)
    }


    fun getAMockedJsonResponse(userId: String, name: String, middlename: String, surname: String, email: String, friends: String): String {
        return """
        {
            "userId": "$userId",
            "name": "$name",
            "middlename":"$middlename", 
            "surname": "$surname",
            "email": "$email",
            "friends": [$friends]
        }
        """
    }

    fun stubJsonResponse(json: String, userId: String) {

        /*
            Here, instructing WireMock to give 2 different responses
            based on whether the authentication cookie is  set or not,
            regardless of its content
         */

        wiremockServer.stubFor(
                WireMock.get(
                        WireMock.urlMatching("/users/$userId"))
                        .withCookie("SESSION", WireMock.matching(".*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(json)))

        wiremockServer.stubFor(
                WireMock.get(
                        WireMock.urlMatching("/users/$userId"))
                        .withCookie("SESSION", WireMock.notMatching(".*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(403)))

    }
}