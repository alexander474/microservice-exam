package no.breale17.movie.entity

import java.time.LocalDate
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name="movie")
class MovieEntity (
        var title: String? = null,
        var description: String? = null,
        var releaseDate: LocalDate? = null,
        @get:ElementCollection
        var genres: Set<String>? = null,
        var voteCount: Int?,
        var voteAverage: Double? = null,
        var price: Double? = null,
        @get:Id
        @get:GeneratedValue
        var id: Long? = null
)