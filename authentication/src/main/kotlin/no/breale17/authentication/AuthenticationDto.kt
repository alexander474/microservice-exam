/**
 * Got inspiration from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/security/distributed-session/ds-auth/src/main/kotlin/org/tsdes/advanced/security/distributedsession/auth/AuthDto.kt
 */
package no.breale17.authentication

import javax.validation.constraints.NotBlank

class AuthenticationDto(
        @get:NotBlank
        var userId: String? = null,

        @get:NotBlank
        var password: String? = null
)