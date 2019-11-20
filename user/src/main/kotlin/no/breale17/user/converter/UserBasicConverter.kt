package no.breale17.user.converter

import no.breale17.dto.UserBasicDto
import no.breale17.dto.UserDto
import no.breale17.user.entity.UserEntity
import no.utils.pagination.PageDto
import org.springframework.web.util.UriComponentsBuilder

class UserBasicConverter {
    fun transform(entity: UserEntity) : UserBasicDto {

        return UserBasicDto(
                userId = entity.userId,
                name = entity.name,
                middleName = entity.middleName,
                surname = entity.surname
        )
    }

    fun transform(entities: Iterable<UserEntity>,
                  offset: Int,
                  limit: Int,
                  onDb: Long,
                  maxFromDb: Int,
                  baseUri: UriComponentsBuilder) : PageDto<UserBasicDto> {
        val users = entities.map { transform(it) }.toMutableList()
        return PageDto.withLinksBasedOnOffsetAndLimitParameters(
                list = users,
                rangeMin = offset,
                rangeMax = offset + limit - 1,
                onDb = onDb,
                maxFromDb = maxFromDb,
                baseUri = baseUri)
    }
}