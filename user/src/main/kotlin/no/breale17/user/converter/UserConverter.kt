package no.breale17.user.converter

import no.breale17.dto.UserDto
import no.breale17.user.entity.UserEntity
import no.utils.pagination.PageDto

class UserConverter {

    fun transform(entity: UserEntity) : UserDto {

        return UserDto(
                userId = entity.userId,
                name = entity.name,
                middleName = entity.middleName,
                surname = entity.surname,
                email = entity.email
        )
    }

    fun transform(entities: Iterable<UserEntity>) : PageDto<UserDto>{
        val users = entities.map { transform(it) }
        return PageDto(list = users, totalSize = users.size)
    }
}