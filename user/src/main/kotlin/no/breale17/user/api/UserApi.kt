package no.breale17.user.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.breale17.dto.MovieDto
import no.breale17.dto.UserDto
import no.breale17.user.service.UserService
import no.utils.pagination.PageDto
import no.utils.wrapper.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun getAll(): ResponseEntity<WrappedResponse<PageDto<UserDto>>>{
        val users = userServce.getAll()
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = users
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