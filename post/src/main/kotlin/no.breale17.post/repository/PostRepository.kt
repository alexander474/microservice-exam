/**
 * Got inspiration from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/rest/pagination/src/main/kotlin/org/tsdes/advanced/rest/pagination/NewsService.kt
 */
package no.breale17.post.repository

import no.breale17.post.entity.PostEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface PostRepository : CrudRepository<PostEntity, Long>, PostRepositoryCustom {


}

@Transactional
interface PostRepositoryCustom {

    fun numberOfPosts(userIds: List<String>): Long

    fun getAllPostsByUserId(userIds: List<String>, offset: Int, limit: Int): List<PostEntity>
}

@Repository
@Transactional
// used insiration from https://thoughts-on-java.org/fetch-multiple-entities-id-hibernate/
open class PostRepositoryImpl : PostRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun numberOfPosts(userIds: List<String>): Long {
        val query: TypedQuery<Long>
        query = em.createQuery("select count(p) from PostEntity p where p.userId in :ids", Long::class.javaObjectType)
        query.setParameter("ids", userIds)
        return query.singleResult
    }

    override fun getAllPostsByUserId(userIds: List<String>, offset: Int, limit: Int): List<PostEntity> {

        val query: TypedQuery<PostEntity>
        query = em.createQuery("select p from PostEntity p where p.userId in :ids order by p.date DESC ", PostEntity::class.java)
        query.setParameter("ids", userIds)

        query.firstResult = offset
        query.maxResults = limit


        val result = query.resultList
        return result;
    }
}