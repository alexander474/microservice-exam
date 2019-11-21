package no.breale17.user.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.breale17.dto.FriendRequestDto
import no.breale17.dto.FriendRequestStatus
import no.breale17.dto.UserBasicDto
import no.breale17.dto.UserDto
import no.breale17.user.service.UserService
import no.utils.pagination.PageDto
import no.utils.wrapper.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.security.Principal

@Api(value = "/users", description = "Retrieves user(s).")
@RequestMapping(
        path = ["/users"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class UserApi {

    @Autowired
    lateinit var userServce: UserService

    @ApiOperation("Get all users")
    @GetMapping(produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getAll(            @RequestParam("ignoreSession", required = false)
                           ignoreSession: Boolean?,

                           @CookieValue("SESSION", required = false)
                           cookie: String?,

                           @ApiParam("Offset in the list of news")
                           @RequestParam("offset", defaultValue = "0")
                           offset: Int,

                           @ApiParam("Limit of news in a single retrieved page")
                           @RequestParam("limit", defaultValue = "10")
                           limit: Int,
                           user: Principal): ResponseEntity<WrappedResponse<PageDto<UserDto>>>{
        val maxPageLimit = 50
        val maxFromDb = 1000
        val onDbWithId = userServce.getNumberOfUsers()

        if (offset < 0 || limit < 1 || limit > maxPageLimit || (offset+limit) > maxFromDb || offset > onDbWithId) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<PageDto<UserDto>>(message="Illegal offset or limit" ,code = 400)
                            .validated())
        }

        var builder = UriComponentsBuilder
                .fromPath("/users")
        val users = userServce.getAll(offset, limit, onDbWithId, maxPageLimit, builder)
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = users
                ).validated()
        )
    }

    @ApiOperation("Get basic/public information about a user")
    @GetMapping(path = ["/basic"],produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getAllBasic(            @RequestParam("ignoreSession", required = false)
                           ignoreSession: Boolean?,

                           @CookieValue("SESSION", required = false)
                           cookie: String?,

                           @ApiParam("Offset in the list of news")
                           @RequestParam("offset", defaultValue = "0")
                           offset: Int,

                           @ApiParam("Limit of news in a single retrieved page")
                           @RequestParam("limit", defaultValue = "10")
                           limit: Int,
                           user: Principal): ResponseEntity<WrappedResponse<PageDto<UserBasicDto>>>{
        val maxPageLimit = 50
        val maxFromDb = 1000
        val onDbWithId = userServce.getNumberOfUsers()

        if (offset < 0 || limit < 1 || limit > maxPageLimit || (offset+limit) > maxFromDb || offset > onDbWithId) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<PageDto<UserBasicDto>>(message="Illegal offset or limit" ,code = 400)
                            .validated())
        }

        var builder = UriComponentsBuilder
                .fromPath("/users")
        val users = userServce.getAllUsersBasicInfo(offset, limit, onDbWithId, maxPageLimit, builder)
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = users
                ).validated()
        )
    }

    @ApiOperation("get count of how many users registered")
    @GetMapping(path = ["/userCount"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getCount(): ResponseEntity<WrappedResponse<Long>> {
        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = userServce.getNumberOfUsers()
        ).validated())
    }



    @ApiOperation("get a user by id")
    @GetMapping(path = ["/{id}"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getById(
            @ApiParam("Unique user id")
            @PathVariable("id") id: String
    ): ResponseEntity<WrappedResponse<UserDto>>{

        val user = userServce.getById(id)

        if(user === null){
            return ResponseEntity.status(404).body(
                    WrappedResponse<UserDto>(
                            code = 404,
                            message = "User not found"
                    ).validated()
            )
        }

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = user
                ).validated()
        )
    }

    @ApiOperation("Delete a user by id")
    @DeleteMapping(path = ["/{id}"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun deleteById(
            @ApiParam("Unique user id")
            @PathVariable("id") id: String
    ): ResponseEntity<WrappedResponse<Void>>{
        userServce.deleteUser(id)

        return ResponseEntity.status(204).body(
                WrappedResponse<Void>(
                        code = 204
                ).validated()
        )
    }

    @ApiOperation("Create or replace a user")
    @PutMapping(path = ["/{id}"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun replace(
            @PathVariable id: String,
            @RequestBody dto: UserDto,
            user: Principal)
            : ResponseEntity<WrappedResponse<Void>> {

        if (id != dto.userId) {
            return ResponseEntity.status(409).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "You can only alter you're own user"
                    ).validated())
        }

        val alreadyExists = userServce.exists(id)
        var code = if(alreadyExists) 204 else 201

        try {
            userServce.saveUser(user.name, dto)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).body(
                WrappedResponse<Void>(
                        code = code,
                        message = if(code in 200..299) "SUCCESS" else "SOMETHING WENT WRONG"
                ).validated())
    }

    @ApiOperation("Modify the user using JSON Merge Patch")
    @PatchMapping(path = ["/{id}"],
            consumes = ["application/merge-patch+json"])
    fun mergePatch(@ApiParam("The unique id of the user")
                   @PathVariable("id")
                   id: String,
                   @ApiParam("The partial patch")
                   @RequestBody
                   jsonPatch: String): ResponseEntity<WrappedResponse<Void>>{
        val dto = userServce.getById(id)
                ?: return ResponseEntity.status(409).body(
                        WrappedResponse<Void>(
                                code = 409,
                                message = "Could not find user"
                        ).validated())
        val jackson = ObjectMapper()

        val jsonNode: JsonNode
        try {
            jsonNode = jackson.readValue(jsonPatch, JsonNode::class.java)
        } catch (e: Exception) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "Invalid JSON input"
                    ).validated())
        }

        if (jsonNode.has("userId")) {
            return ResponseEntity.status(409).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "Id cannot be modified"
                    ).validated())
        }
        if (jsonNode.has("friends")) {
            return ResponseEntity.status(409).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "Friends cannot be modified"
                    ).validated())
        }
        if (jsonNode.has("requestIn")) {
            return ResponseEntity.status(409).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "RequestIn cannot be modified"
                    ).validated())
        }
        if (jsonNode.has("requestOut")) {
            return ResponseEntity.status(409).body(
                    WrappedResponse<Void>(
                            code = 409,
                            message = "RequestOut cannot be modified"
                    ).validated())
        }

        var name = dto.name
        var middleName = dto.middleName
        var surname= dto.surname
        var email = dto.email

        if (jsonNode.has("name")) {
            val nameNode = jsonNode.get("name")
            if (nameNode.isNull) {
                name = null
            } else if (nameNode.isTextual) {
                name = nameNode.asText()
            } else {
                return ResponseEntity.status(400).body(
                        WrappedResponse<Void>(
                                code = 400,
                                message = "Invalid JSON. Non-string name"
                        ).validated())
            }
        }
        if (jsonNode.has("mmiddleName")) {
            val middleNameNode = jsonNode.get("middleName")
            if (middleNameNode.isNull) {
                middleName = null
            } else if (middleNameNode.isTextual) {
                middleName = middleNameNode.asText()
            } else {
                return ResponseEntity.status(400).body(
                        WrappedResponse<Void>(
                                code = 400,
                                message = "Invalid JSON. Non-string name"
                        ).validated())
            }
        }
        if (jsonNode.has("surname")) {
            val surnameNode = jsonNode.get("surname")
            if (surnameNode.isNull) {
                surname = null
            } else if (surnameNode.isTextual) {
                surname = surnameNode.asText()
            } else {
                return ResponseEntity.status(400).body(
                        WrappedResponse<Void>(
                                code = 400,
                                message = "Invalid JSON. Non-string name"
                        ).validated())
            }
        }
        if (jsonNode.has("email")) {
            val emailNode = jsonNode.get("email")
            if (emailNode.isNull) {
                email = null
            } else if (emailNode.isTextual) {
                email = emailNode.asText()
            } else {
                return ResponseEntity.status(400).body(
                        WrappedResponse<Void>(
                                code = 400,
                                message = "Invalid JSON. Non-string name"
                        ).validated())
            }
        }
        dto.name = name
        dto.middleName = middleName
        dto.surname = surname
        dto.email = email
        userServce.saveUser(id, dto)

        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Create a friendrequest")
    @PostMapping(path = ["/friendrequest"],consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun createFriendRequest(@ApiParam("Information for new movie")
                   @RequestBody friendRequest: FriendRequestDto,
                            @RequestParam("ignoreSession", required = false) ignoreSession: Boolean?,
                            @CookieValue("SESSION", required = false)
                   cookie: String?,
                            user: Principal): ResponseEntity<WrappedResponse<Unit>> {


        if(user.name === null){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(code = 400)
                            .validated())
        }

        if(friendRequest.from === null || friendRequest.to === null){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(
                            code = 400,
                            message = "Not a valid object"
                    ).validated())
        }

        if(friendRequest.from != user.name){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(
                            code = 400,
                            message = "You can only send friend requests from you're user"
                    ).validated())
        }


        val sent = userServce.sendRequest(friendRequest.from!!, friendRequest.to!!)

        if(!sent){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(code = 400, message = "Unable to send request")
                            .validated())
        }

        return ResponseEntity.status(200).body(
                WrappedResponse<Unit>(code = 200, message = "Friend request sent").validated())
    }

    @ApiOperation("answer a friendrequest")
    @PutMapping(path = ["/friendrequest"],consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun answerFriendRequest(@ApiParam("Information for new movie")
                            @RequestBody friendRequest: FriendRequestDto,
                            @RequestParam("ignoreSession", required = false) ignoreSession: Boolean?,
                            @CookieValue("SESSION", required = false)
                            cookie: String?,
                            user: Principal): ResponseEntity<WrappedResponse<Unit>> {


        if(user.name === null){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(code = 400)
                            .validated())
        }

        if(friendRequest.from === null || friendRequest.to === null || friendRequest.status === null){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(
                            code = 400,
                            message = "Not a valid object"
                    ).validated())
        }


        var message = ""
        if(friendRequest.status === FriendRequestStatus.APPROVED){
            userServce.addFriend(friendRequest.from!!, friendRequest.to!!)
            message = "FRIEND ADDED"
        }else if(friendRequest.status === FriendRequestStatus.DENIED){
            userServce.removeRequest(friendRequest.from!!, friendRequest.to!!)
            message = "FRIEND REQUEST REMOVED"
        }

        return ResponseEntity.status(200).body(
                WrappedResponse<Unit>(code = 200, message = message).validated())
    }
}