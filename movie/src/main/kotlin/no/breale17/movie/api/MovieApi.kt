package no.breale17.movie.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.breale17.dto.MovieDto
import no.utils.pagination.PageDto
import no.breale17.movie.service.MovieService
import no.utils.wrapper.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.NumberFormatException
import java.net.URI

@Api(value = "/movies", description = "Retrieves movies.")
@RequestMapping(
        path = ["/movies"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class MovieApi {

    @Autowired
    private lateinit var movieService: MovieService


    /*
    This service will communicate with the "user-service" one.
    We inject the ip address here as a variable, as we ll
    change it in the tests.
 */

    @Value("\${userServiceAddress}")
    private lateinit var userServiceAddress: String

    @ApiOperation("*")
    @GetMapping(produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getAll(): ResponseEntity<WrappedResponse<PageDto<MovieDto>>>{
        val movies = movieService.getAll()
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = movies
                ).validated()
        )
    }

    @ApiOperation("*")
    @GetMapping(path = ["/{id}"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun getById(
            @ApiParam("Unique movie id")
            @PathVariable("id") movieId: String
    ): ResponseEntity<WrappedResponse<MovieDto>>{
        val id: Long

        try{
            id = movieId.toLong()
        } catch (nfe: NumberFormatException){
            return ResponseEntity.status(400).body(
                    WrappedResponse<MovieDto>(
                            code = 400,
                            message = "$movieId is not a valid id"
                    ).validated()
            )
        }

        val movies = movieService.getById(id)

        if(movies === null){
            return ResponseEntity.status(404).body(
                    WrappedResponse<MovieDto>(
                            code = 404,
                            message = "Movie cannot be null"
                    ).validated()
            )
        }

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = movies
                ).validated()
        )
    }

    @ApiOperation("Create a movie")
    @PostMapping(consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun createMovie(@ApiParam("Information for new movie")
                    @RequestBody movieDto: MovieDto): ResponseEntity<WrappedResponse<Unit>> {
        val id = movieService.createMovie(movieDto)

        if(id == -1L){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(code = 400, message = "Unable to create movie")
                            .validated())
        }

        return ResponseEntity.created(URI.create("/movies/$id")).body(
                WrappedResponse<Unit>(code = 201, message = "Movie was created").validated())
    }

    @ApiOperation("Delete a movie by id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteById(
            @ApiParam("movie id")
            @PathVariable("id")
            movieId: String
    ): ResponseEntity<WrappedResponse<Unit>> {
        val id: Long

        try{
            id = movieId.toLong()
        } catch (nfe: NumberFormatException){
            return ResponseEntity.status(400).body(
                    WrappedResponse<Unit>(
                            code = 400,
                            message = "$movieId is not a valid id"
                    ).validated()
            )
        }

        val isDeleted = movieService.deleteById(id)

        if(!isDeleted)
            return ResponseEntity.status(404).body(
                    WrappedResponse<Unit>(
                            code = 404,
                            message = "$movieId does not exist"
                    ).validated()
            )

        return ResponseEntity.status(204).body(
                WrappedResponse<Unit>(
                        code = 204,
                        message = "Movie with id=$movieId was deleted successfully"
                ).validated()
        )
    }

}