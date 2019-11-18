package no.breale17.authentication

import javax.validation.constraints.NotBlank

class AuthenticationDto(
        @get:NotBlank
        var userId : String? = null,

        @get:NotBlank
        var password: String? = null
)