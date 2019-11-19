package no.breale17.post.converter

import no.breale17.dto.PostDto
import no.breale17.post.entity.PostEntity
import no.utils.pagination.PageDto
import org.springframework.web.util.UriComponentsBuilder

class PostConverter {

    fun transform(entity: PostEntity): PostDto {
        return PostDto(
                id = entity.id.toString(),
                title = entity.title,
                date = entity.date.toString(),
                message = entity.message,
                userId = entity.userId
        )
    }

    fun transform(enteties: Iterable<PostEntity>,
                  offset: Int,
                  limit: Int,
                  onDb: Long,
                  maxFromDb: Int,
                  baseUri: UriComponentsBuilder): PageDto<PostDto> {
        val posts = enteties.map { transform(it) }.toMutableList()
        return PageDto.withLinksBasedOnOffsetAndLimitParameters(
                list = posts,
                rangeMin = offset,
                rangeMax = offset + limit - 1,
                onDb = onDb,
                maxFromDb = maxFromDb,
                baseUri = baseUri)
    }

}