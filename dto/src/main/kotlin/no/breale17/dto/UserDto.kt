package no.breale17.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Basic Information about a user")
class UserDto(
        @ApiModelProperty("userId of the user")
        var userId: String? = null,

        @ApiModelProperty("name of the user")
        var name: String? = null,

        @ApiModelProperty("middlename of the user")
        var middleName: String? = null,

        @ApiModelProperty("surname of the user")
        var surname: String? = null,

        @ApiModelProperty("email of the user")
        var email: String? = null,

        @ApiModelProperty("friends of the user")
        var friends: List<String> = listOf(),

        @ApiModelProperty("incoming friend requests of the user")
        var requestsIn: List<String> = listOf(),

        @ApiModelProperty("outgoing friend requests of the user")
        var requestsOut: List<String> = listOf()
)