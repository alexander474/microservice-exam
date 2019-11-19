package no.breale17.user.service

import no.breale17.user.converter.UserConverter
import no.breale17.user.repository.UserRepository
import no.breale17.dto.UserDto
import no.breale17.user.entity.UserEntity
import no.utils.pagination.PageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun exists(id: String): Boolean{
        return userRepository.existsById(id)
    }

    fun getAll(offset: Int, limit: Int, onDb:Long, maxPageLimit: Int, builder: UriComponentsBuilder): PageDto<UserDto> {
        return UserConverter().transform(userRepository.findAll(), offset, limit, onDb, maxPageLimit, builder)
    }

    fun getNumberOfUsers(): Long{
        return userRepository.numberOfUsers()
    }

    fun getById(id: String): UserDto?{
        val entity = userRepository.findById(id).orElse(null) ?: return null
        return UserConverter().transform(entity)
    }

    fun saveUser(userDto: UserDto): UserDto?{
        val userEntity = UserEntity(userDto.userId,userDto.name,userDto.middleName,userDto.surname, userDto.email)
        return UserConverter().transform(userRepository.save(userEntity))
    }

    fun saveUser(userId: String,userDto: UserDto): UserDto?{
        val userEntity = UserEntity(userId,userDto.name,userDto.middleName,userDto.surname, userDto.email)
        return UserConverter().transform(userRepository.save(userEntity))
    }
}