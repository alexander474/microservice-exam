package no.movie.repository

import no.movie.entity.MovieEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface MovieRepository : CrudRepository<MovieEntity, Long>, MoviesRepositoryCustom {

}

@Transactional
interface MoviesRepositoryCustom{

}

@Repository
@Transactional
class MoviesRepositoryImpl : MoviesRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager
}