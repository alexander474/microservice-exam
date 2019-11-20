package no.breale17.user.api

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

    @ApiOperation("*")
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

    @ApiOperation("*")
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

    @GetMapping(path = ["/userCount"],
            produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getCount(): ResponseEntity<WrappedResponse<Long>> {
        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = userServce.getNumberOfUsers()
        ).validated())
    }


    @ApiOperation("*")
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

    @ApiOperation("Create a friendrequest")
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