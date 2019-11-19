package no.breale17.post.service

import no.breale17.dto.PostDto
import no.breale17.dto.UserDto
import no.breale17.post.entity.PostEntity
import no.breale17.post.converter.PostConverter
import no.breale17.post.repository.PostRepository
import no.utils.pagination.PageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate

@Service
class PostService {

    @Autowired
    private lateinit var postRepository: PostRepository

    fun getAll(userId: String, userIds: List<String>, offset: Int, limit: Int, onDb:Long, maxPageLimit: Int, builder: UriComponentsBuilder): PageDto<PostDto> {
        val ids = userIds.toMutableSet()
        ids.add(userId)
        val list = postRepository.getAllPostsByUserId(ids, offset, limit)
        return PostConverter().transform(list, offset, limit, onDb, maxPageLimit, builder)
    }

    fun getNumberOfPosts(userIds: List<String>?): Long{
        if(userIds === null || userIds.isEmpty()) return 0L
        return postRepository.numberOfPosts(userIds)
    }

    fun getById(id: Long): PostDto?{
        val movie = postRepository.findById(id).orElse(null) ?: return null
        return PostConverter().transform(movie)
    }


    fun createPost(postDto: PostDto, userId: String): Long{
        val postEntity = PostEntity(
                title = postDto.title,
                message = postDto.message,
                date = LocalDate.parse(postDto.date),
                userId = userId
        )

        postRepository.save(postEntity)
        return postEntity.id ?: -1L
    }

    fun update(postDto: PostDto): PostEntity {
        val postEntity = PostEntity(
                postDto.title,
                postDto.message,
                LocalDate.parse(postDto.date),
                postDto.userId,
                postDto.id?.toLong()
        )

        return postRepository.save(postEntity)
    }

    fun deleteById(id: Long): Boolean{
        if(postRepository.existsById(id)) return false

        postRepository.deleteById(id)
        return true
    }

}