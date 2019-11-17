package no.movie.service

import no.dto.MovieDto
import no.movie.converter.MoviesConverter
import no.movie.entity.MovieEntity
import no.utils.pagination.PageDto
import no.movie.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MovieService {

    @Autowired
    private lateinit var movieRepository: MovieRepository

    fun getAll(): PageDto<MovieDto> {
        val list = movieRepository.findAll()
        return MoviesConverter().transform(list)
    }

    fun getById(id: Long): MovieDto?{
        val movie = movieRepository.findById(id).orElse(null) ?: return null
        return MoviesConverter().transform(movie)
    }


    fun createMovie(movieDto: MovieDto): Long{
        val movieEntity = MovieEntity(
                movieDto.title,
                movieDto.description,
                LocalDate.parse(movieDto.releaseDate),
                movieDto.genres,
                movieDto.voteCount,
                movieDto.voteAverage?.toDouble(),
                movieDto.price?.toDouble()
        )

        movieRepository.save(movieEntity)
        return movieEntity.id ?: -1L
    }

    fun update(movieDto: MovieDto): MovieEntity {
        val movieEntity = MovieEntity(
                movieDto.title,
                movieDto.description,
                LocalDate.parse(movieDto.releaseDate),
                movieDto.genres,
                movieDto.voteCount,
                movieDto.voteAverage?.toDouble(),
                movieDto.price?.toDouble(),
                movieDto.id?.toLong()
        )

        return movieRepository.save(movieEntity)
    }

    fun deleteById(id: Long): Boolean{
        if(movieRepository.existsById(id)) return false

        movieRepository.deleteById(id)
        return true
    }

}