package no.breale17.user

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale17.dto.FriendRequestDto
import no.breale17.dto.FriendRequestStatus
import no.breale17.dto.UserDto
import no.breale17.user.repository.UserRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
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

    private fun createUser(id: String, password: String, name: String, middleName: String, surname: String, email: String): UserDto{
        val currentSize = getSize()
        checkSize(currentSize.toInt())

        val dto = UserDto(id, name, middleName, surname, email)

        RestAssured.given().auth().basic(id,password)
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(201)

        checkSize((currentSize.toInt())+1)
        return dto
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

    private fun getSize(): Long{
        return RestAssured.given().auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .get()
                .then()
                .statusCode(200)
                .extract().path("data.list.size()")
    }

    @Test
    fun testUserCount(){
        checkSize(0)
        RestAssured.given().get("/userCount")
                .then()
                .statusCode(200)
                .body("data", CoreMatchers.equalTo(0))

        val id = "foo"
        val dto = UserDto(id, "A", "B", "C", "a@a.com")
        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/$id")
                .then()
                .statusCode(201)

        checkSize(1)
        RestAssured.given().get("/userCount")
                .then()
                .statusCode(200)
                .body("data", CoreMatchers.equalTo(1))
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
    fun testDeleteUser(){
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

        RestAssured.given().auth().basic(id,"123")
                .delete("/$id")
                .then()
                .statusCode(204)

        checkSize(0)
    }

    @Test
    fun testGetUserBasicInfo(){
        val id = "foo"
        val id2 = "bar"
        val id3 = "admin"
        val password = "123"

        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/basic")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(0))

        createUser(id, password, "A", "B", "C", "a@a.com")
        createUser(id2, password, "A2", "B2", "C2", "a2@a.com")
        createUser(id3, "admin", "A2", "B2", "C2", "a2@a.com")

        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/basic")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(3))
    }

    @Test
    fun testAddFriend(){
        val id = "foo"
        val id2 = "bar"
        val password = "123"
        val friendRequest = FriendRequestDto(id, id2)
        val friendRequestResponse = FriendRequestDto(id, id2, FriendRequestStatus.APPROVED)

        createUser(id, password, "A", "B", "C", "a@a.com")
        createUser(id2, password, "A2", "B2", "C2", "a2@a.com")



        //Send friend request from foo
        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(friendRequest)
                .post("/friendrequest")
                .then()
                .statusCode(200)

        //Check that request is stored in both users
        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(1))

        RestAssured.given().auth().basic(id2,"123")
                .accept(ContentType.JSON)
                .get("/$id2")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(1))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))

        //Approve friend request from bar
        RestAssured.given().auth().basic(id2,"123")
                .contentType(ContentType.JSON)
                .body(friendRequestResponse)
                .put("/friendrequest")
                .then()
                .statusCode(200)

        //Check that they are friends
        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("data.friends", Matchers.contains(id2))
                .body("data.friends.size()", CoreMatchers.equalTo(1))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))


        RestAssured.given().auth().basic(id2,"123")
                .accept(ContentType.JSON)
                .get("/$id2")
                .then()
                .statusCode(200)
                .body("data.friends", Matchers.contains(id))
                .body("data.friends.size()", CoreMatchers.equalTo(1))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))

    }

    @Test
    fun testDenyFriendRequest(){
        val id = "foo"
        val id2 = "bar"
        val password = "123"
        val friendRequest = FriendRequestDto(id, id2)
        val friendRequestResponse = FriendRequestDto(id, id2, FriendRequestStatus.DENIED)

        createUser(id, password, "A", "B", "C", "a@a.com")
        createUser(id2, password, "A2", "B2", "C2", "a2@a.com")


        //Send friend request from foo
        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(friendRequest)
                .post("/friendrequest")
                .then()
                .statusCode(200)

        //Check that request is stored in both users
        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(1))

        RestAssured.given().auth().basic(id2,"123")
                .accept(ContentType.JSON)
                .get("/$id2")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(1))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))

        //Deny friend request from bar
        RestAssured.given().auth().basic(id2,"123")
                .contentType(ContentType.JSON)
                .body(friendRequestResponse)
                .put("/friendrequest")
                .then()
                .statusCode(200)

        //Check that they are not friends and request is deleted
        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))


        RestAssured.given().auth().basic(id2,"123")
                .accept(ContentType.JSON)
                .get("/$id2")
                .then()
                .statusCode(200)
                .body("data.friends.size()", CoreMatchers.equalTo(0))
                .body("data.requestsIn.size()", CoreMatchers.equalTo(0))
                .body("data.requestsOut.size()", CoreMatchers.equalTo(0))

    }

    @Test
    fun testGetAllIllegalOffsetAndLimit(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        RestAssured.given().auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .queryParam("offset", 10_000)
                .queryParam("limit", 10_000)
                .get()
                .then()
                .statusCode(400)
    }

    @Test
    fun testUserDoesNotExist(){
        val id = "foo"

        RestAssured.given().auth().basic(id,"123")
                .accept(ContentType.JSON)
                .get("/$id")
                .then()
                .statusCode(404)
    }

    @Test
    fun testReplaceWithNotMatchinId(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = UserDto("notMatching", "A", "B", "C", "a@a.com")

        RestAssured.given().auth().basic(id,"123")
                .contentType(ContentType.JSON)
                .body(dto2)
                .put("/$id")
                .then()
                .statusCode(409)
    }


    @Test
    fun testChangeField(){
        val id = "foo"
        val name = "John"
        val password = "123"

        val dto = createUser(id, password, name, "B", "C", "a@a.com")

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

    @Test
    fun testJsonMergePatch(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": "newName",
                "middleName": "newMiddleName",
                "surname": "NewSurname",
                "email": "newMail@newMail.rand"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(204)
    }

    @Test
    fun testJsonMergePatchIllegalName(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": 1,
                "middleName": "newMiddleName",
                "surname": "NewSurname",
                "email": "newMail@newMail.rand"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(400)
    }

    @Test
    fun testJsonMergePatchIllegalMiddleName(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": newName,
                "middleName": "-1,
                "surname": "NewSurname",
                "email": "newMail@newMail.rand"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(400)
    }
    @Test
    fun testJsonMergePatchIllegalSurname(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": newName,
                "middleName": "newMiddleName",
                "surname": "-1",
                "email": "newMail@newMail.rand"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(400)
    }
    @Test
    fun testJsonMergePatchIllegalEmail(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": newName,
                "middleName": "newMiddleName",
                "surname": "NewSurname",
                "email": "-1"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(400)
    }

    @Test
    fun testJsonMergePatchSendingId(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "userId": "RandomNewId",
                "name": "newName",
                "middleName": "newMiddleName",
                "surname": "NewSurname",
                "email": "newMail@newMail.rand"
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(409)
    }

    @Test
    fun testJsonMergePatchSendingFriends(){
        val id = "foo"
        val password = "123"

        createUser(id, password, "A", "B", "C", "a@a.com")

        val dto2 = """
            {
                "name": "newName",
                "middleName": "newMiddleName",
                "surname": "NewSurname",
                "email": "newMail@newMail.rand",
                "friends": []
            }
        """.trimIndent()

        RestAssured.given().auth().basic(id,"123")
                .contentType("application/merge-patch+json")
                .body(dto2)
                .patch("/$id")
                .then()
                .statusCode(409)
    }

}