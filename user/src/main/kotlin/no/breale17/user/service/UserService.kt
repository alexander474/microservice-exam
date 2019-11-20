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
        val userEntity = UserEntity(userDto.userId,userDto.name,userDto.middleName,userDto.surname, userDto.email, userDto.friends.toSet(), userDto.requestsIn.toSet(), userDto.requestsOut.toSet())
        return UserConverter().transform(userRepository.save(userEntity))
    }

    fun saveUser(userId: String,userDto: UserDto): UserDto?{
        val userEntity = UserEntity(userId,userDto.name,userDto.middleName,userDto.surname, userDto.email, userDto.friends.toSet(), userDto.requestsIn.toSet(), userDto.requestsOut.toSet())
        return UserConverter().transform(userRepository.save(userEntity))
    }

    fun checkIfFriendRequestExists(from: String, to: String): Boolean{
        var valid = false
        val userFrom = getById(from)
        val userTo = getById(to)

        if(userFrom !== null && userTo !== null && !checkIfAlreadyFriends(from,to)){
            if(userFrom.requestsOut.isNotEmpty() && userTo.requestsIn.isNotEmpty()){
                if(userFrom.requestsOut.contains(to) && userTo.requestsIn.contains(from)){
                    return true
                }
            }
        }

        return valid
    }

    fun checkIfAlreadyFriends(from: String,to: String): Boolean{
        var valid = false
        val userFrom = getById(from)
        val userTo = getById(to)

        if(userFrom !== null && userTo !== null){
            if(userFrom.friends.isNotEmpty() && userTo.friends.isNotEmpty()){
                if(userFrom.friends.contains(to) && userTo.friends.contains(from)){
                    return true
                }
            }
        }

        return valid
    }

    fun sendRequest(from: String, to: String): Boolean{
        var sent = false
        val userFrom = getById(from)
        val userTo = getById(to)
        if(userFrom !== null && userTo !== null && !checkIfAlreadyFriends(from,to) && !checkIfFriendRequestExists(from,to)){
            val listFrom = userFrom.requestsOut.toMutableList()
            val listTo = userTo.requestsIn.toMutableList()
            listFrom.add(to)
            listTo.add(from)
            userFrom.requestsOut = listFrom
            userTo.requestsIn = listTo
            saveUser(userFrom)
            saveUser(userTo)
            sent = true
        }
        return sent
    }

    fun removeRequest(from: String, to: String){
        val friendRequestExists = checkIfFriendRequestExists(from,to)
        val userFrom = getById(from)
        val userTo = getById(to)
        if(friendRequestExists){
            val listFrom = userFrom!!.requestsOut.toMutableList()
            val listTo = userTo!!.requestsIn.toMutableList()
            listFrom.remove(to)
            listTo.remove(from)
            userFrom.requestsOut = listFrom
            userTo.requestsIn = listTo
            saveUser(userFrom)
            saveUser(userTo)
        }
    }

    fun addFriend(from: String, to: String){
        val friendRequestExists = checkIfFriendRequestExists(from,to)
        val isFriend = checkIfAlreadyFriends(from,to)
        val userFrom = getById(from)
        val userTo = getById(to)

        if(userFrom !== null && userTo !== null && !isFriend && friendRequestExists){
            val listFrom = userFrom.friends.toMutableList()
            val listTo = userTo.friends.toMutableList()
            val listRequestFrom = userFrom!!.requestsOut.toMutableList()
            val listRequestTo = userTo!!.requestsIn.toMutableList()

            //alter
            listRequestFrom.remove(to)
            listRequestTo.remove(from)
            listFrom.add(to)
            listTo.add(from)

            //update
            userFrom.friends = listFrom
            userFrom.requestsOut = listRequestFrom
            userTo.friends = listTo
            userTo.requestsIn = listRequestTo

            //save
            saveUser(userFrom)
            saveUser(userTo)
        }
    }
}