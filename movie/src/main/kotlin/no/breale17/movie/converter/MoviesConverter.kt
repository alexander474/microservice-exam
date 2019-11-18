package no.breale17.movie.converter

import no.breale17.dto.MovieDto
import no.breale17.movie.entity.MovieEntity
import no.utils.pagination.PageDto

class MoviesConverter {

    fun transform(entity: MovieEntity): MovieDto {
        return MovieDto(
                id = entity.id.toString(),
                title = entity.title,
                description = entity.description,
                releaseDate = entity.releaseDate.toString(),
                genres = entity.genres,
                voteCount = entity.voteCount,
                voteAverage = entity.voteAverage.toString(),
                price = entity.price.toString()
        )
    }

    fun transform(enteties: Iterable<MovieEntity>): PageDto<MovieDto> {
        val movies = enteties.map { transform(it) }
        return PageDto(list = movies, totalSize = movies.size)
    }

}