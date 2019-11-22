/**
 * Got inspiration from https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/RestApi.kt
 */
package no.breale17.authentication.api

import io.swagger.annotations.Api
import no.breale17.authentication.AuthenticationDto
import no.breale17.authentication.service.AuthenticationService
import no.utils.wrapper.WrappedResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Api(value = "auth", description = "Authentication Requests")
@RequestMapping(
        path = ["/auth"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class AuthenticationApi(
        private val authenticationService: AuthenticationService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {

    @GetMapping(path = ["/user"], produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun user(user: Principal): ResponseEntity<WrappedResponse<MutableMap<String, Any>>> {
        val map = mutableMapOf<String, Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(WrappedResponse(
                code = 200,
                data = map
        ).validated()
        )
    }

    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signIn(@RequestBody dto: AuthenticationDto)
            : ResponseEntity<WrappedResponse<Void>> {

        val userId: String = dto.userId!!
        val password: String = dto.password!!

        val registered = authenticationService.createUser(userId, password, setOf("USER"))

        if (!registered) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<Void>(
                            code = 400,
                            message = "Username already exists"
                    ).validated()
            )
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).body(
                WrappedResponse<Void>(
                        code = 204
                ).validated()
        )
    }

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthenticationDto)
            : ResponseEntity<WrappedResponse<Void>> {

        val userId: String = dto.userId!!
        val password: String = dto.password!!

        val userDetails = try {
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<Void>(
                            code = 400,
                            message = "Wrong username or password"
                    ).validated()
            )
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).body(
                    WrappedResponse<Void>(
                            code = 204
                    ).validated()
            )
        }

        return ResponseEntity.status(400).body(
                WrappedResponse<Void>(
                        code = 400,
                        message = "Wrong username or password"
                ).validated()
        )
    }

}