package no.breale17.user.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.breale17.dto.PostDto
import no.breale17.dto.UserDto
import no.breale17.user.service.UserService
import no.utils.pagination.PageDto
import no.utils.wrapper.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
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
                    WrappedResponse<PageDto<UserDto>>(code = 400)
                            .validated())
        }

        var builder = UriComponentsBuilder
                .fromPath("/posts")
        val users = userServce.getAll(offset, limit, onDbWithId, maxPageLimit, builder)
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = users
                ).validated()
        )
    }

    @GetMapping(path = ["/user/me"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun me(user: Principal): ResponseEntity<WrappedResponse<MutableMap<String, Any>>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = map
        ).validated()
        )
    }

    @ApiOperation("*")
    @GetMapping(path = ["/{id}"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getById(
            @ApiParam("Unique movie id")
            @PathVariable("id") id: String
    ): ResponseEntity<WrappedResponse<UserDto>>{

        val user = userServce.getById(id)

        if(user === null){
            return ResponseEntity.status(404).body(
                    WrappedResponse<UserDto>(
                            code = 404,
                            message = "User cannot be null"
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
            @RequestBody dto: UserDto)
            : ResponseEntity<Void> {

        if (id != dto.userId) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = userServce.exists(id)
        var code = if(alreadyExists) 204 else 201

        try {
            userServce.updateUser(dto)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }


}