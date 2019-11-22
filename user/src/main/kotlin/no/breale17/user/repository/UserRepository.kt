/**
 * Got inspiration from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/rest/pagination/src/main/kotlin/org/tsdes/advanced/rest/pagination/NewsService.kt
 */
package no.breale17.user.repository

import no.breale17.user.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface UserRepository : CrudRepository<UserEntity, String>, UserRepositoryCustom {
}

@Transactional
interface UserRepositoryCustom {

    fun numberOfUsers(): Long

    fun getAllUsers(offset: Int, limit: Int): List<UserEntity>
}

@Repository
@Transactional
open class UserRepositoryImpl : UserRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun numberOfUsers(): Long {
        val query: TypedQuery<Long> = em.createQuery("select count(u) from UserEntity u", Long::class.javaObjectType)
        return query.singleResult
    }

    override fun getAllUsers(offset: Int, limit: Int): List<UserEntity> {

        val query: TypedQuery<UserEntity> = em.createQuery("select u from UserEntity u order by u.userId", UserEntity::class.java)

        query.firstResult = offset
        query.maxResults = limit

        val result = query.resultList
        result.forEach {
            it.friends?.size
            it.requestsIn?.size
            it.requestsOut?.size
        }
        return query.resultList;
    }
}