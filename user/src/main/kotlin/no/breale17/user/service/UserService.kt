package no.breale17.user.service

import no.breale17.user.converter.UserConverter
import no.breale17.user.repository.UserRepository
import no.breale17.dto.UserDto
import no.breale17.user.entity.UserEntity
import no.utils.pagination.PageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun exists(id: String): Boolean{
        return userRepository.existsById(id)
    }

    fun getAll(): PageDto<UserDto> {
        return UserConverter().transform(userRepository.findAll())
    }

    fun getById(id: String): UserDto?{
        val entity = userRepository.findById(id).orElse(null) ?: return null
        return UserConverter().transform(entity)
    }

    fun createUser(userDto: UserDto): UserDto{
        val userEntity = UserEntity(userDto.userId,userDto.name,userDto.middleName,userDto.surname, userDto.email);
        return UserConverter().transform(userRepository.save(userEntity))
    }

    fun updateUser(userDto: UserDto): UserDto?{
        if(userDto.userId === null && userDto.userId?.let { getById(it) } !== null) return null
        val userEntity = UserEntity(userDto.userId,userDto.name,userDto.middleName,userDto.surname, userDto.email);
        return UserConverter().transform(userRepository.save(userEntity))
    }
}