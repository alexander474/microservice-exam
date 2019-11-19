package no.breale17.user.entity

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name="USERS")
class UserEntity (

    @get:Id
    @get:NotBlank
    @Column(name="userId")
    var userId: String?,

    @get:NotBlank
    @Column(name="name")
    var name: String?,

    @Column(name="middleName")
    var middleName: String?,

    @get:NotBlank
    @Column(name="surname")
    var surname: String?,

    @get:Email
    @Column(name="email")
    var email: String?,

    @get:ElementCollection
    @Column(name="friends")
    var friends: Set<String>? = setOf()
)