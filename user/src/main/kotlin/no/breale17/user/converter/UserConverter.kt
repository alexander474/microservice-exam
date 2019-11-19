package no.breale17.user.converter

import no.breale17.dto.UserDto
import no.breale17.user.entity.UserEntity
import no.utils.pagination.PageDto
import org.springframework.web.util.UriComponentsBuilder

class UserConverter {

    fun transform(entity: UserEntity) : UserDto {

        return UserDto(
                userId = entity.userId,
                name = entity.name,
                middleName = entity.middleName,
                surname = entity.surname,
                email = entity.email,
                friends = entity.friends?.toList()
        )
    }

    fun transform(entities: Iterable<UserEntity>,
                  offset: Int,
                  limit: Int,
                  onDb: Long,
                  maxFromDb: Int,
                  baseUri: UriComponentsBuilder) : PageDto<UserDto>{
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