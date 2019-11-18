package no.breale17.user.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
class UserEntity (

    @get:Id
    @get:NotBlank
    var userId: String?,

    @get:NotBlank
    var name: String?,

    var middleName: String?,

    @get:NotBlank
    var surname: String?,

    @get:Email
    var email: String?
)