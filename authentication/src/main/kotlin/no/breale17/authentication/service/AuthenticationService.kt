package no.breale17.authentication.service

import no.breale17.authentication.entity.UserEntity
import no.breale17.authentication.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Service
@Transactional
class AuthenticationService(
        private val userCrud: UserRepository,
        private val passwordEncoder: PasswordEncoder
){

    @PostConstruct
    fun init(){
        createUser("a", "a", setOf("USER", "ADMIN"))
        createUser("b", "b")
    }

    fun createUser(username: String, password: String, roles: Set<String> = setOf()) : Boolean{

        try {
            val hash = passwordEncoder.encode(password)

            if (userCrud.existsById(username)) {
                return false
            }

            val user = UserEntity(username, hash, roles.map{"ROLE_$it"}.toSet())

            userCrud.save(user)

            return true
        } catch (e: Exception){
            return false
        }
    }

}