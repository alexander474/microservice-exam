package no.breale17.dto

class UserDto(
        var userId: String? = null,

        var name: String? = null,

        var middleName: String? = null,

        var surname: String? = null,

        var email: String? = null,

        var friends: List<String> = listOf(),

        var requestsIn: List<String> = listOf(),

        var requestsOut: List<String> = listOf()
)