package no.breale17.post.service

import no.breale17.dto.PostDto
import no.breale17.post.converter.PostConverter
import no.breale17.post.entity.PostEntity
import no.breale17.post.repository.PostRepository
import no.utils.pagination.PageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import javax.annotation.PostConstruct

@Service
class PostService {

    @PostConstruct
    fun init() {
        createPost(PostDto("Admin Post", "This is a post, posted by the admin", "1574351580", "a"), "a")
        createPost(PostDto("User Post", "This is a post, posted by User: FOO", "1574351580", "b"), "b")
    }

    @Autowired
    private lateinit var postRepository: PostRepository

    fun getAll(userId: String, userIds: List<String>, offset: Int, limit: Int, onDb: Long, maxPageLimit: Int, builder: UriComponentsBuilder): PageDto<PostDto> {
        val ids = userIds.toMutableSet()
        ids.add(userId)
        val list = postRepository.getAllPostsByUserId(ids, offset, limit)
        return PostConverter().transform(list, offset, limit, onDb, maxPageLimit, builder)
    }

    fun getNumberOfPosts(userIds: List<String>?): Long {
        if (userIds === null || userIds.isEmpty()) return 0L
        return postRepository.numberOfPosts(userIds)
    }

    fun getById(id: Long): PostDto? {
        val movie = postRepository.findById(id).orElse(null) ?: return null
        return PostConverter().transform(movie)
    }


    fun createPost(postDto: PostDto, userId: String): Long {
        val postEntity = PostEntity(
                title = postDto.title,
                message = postDto.message,
                date = System.currentTimeMillis() / 1000L,
                userId = userId
        )

        postRepository.save(postEntity)
        return postEntity.id ?: -1L
    }

    fun update(postDto: PostDto): PostEntity {
        val postEntity = PostEntity(
                postDto.title,
                postDto.message,
                postDto.date!!.toLong(),
                postDto.userId,
                postDto.id?.toLong()
        )

        return postRepository.save(postEntity)
    }

    fun deleteById(id: Long): Boolean {
        if (!postRepository.existsById(id)) return false

        postRepository.deleteById(id)
        return true
    }

}