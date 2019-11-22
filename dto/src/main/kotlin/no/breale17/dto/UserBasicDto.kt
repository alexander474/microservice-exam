package no.breale17.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Basic Information about a user")
data class UserBasicDto(
        @ApiModelProperty("userId of the user")
        var userId: String? = null,

        @ApiModelProperty("name of the user")
        var name: String? = null,

        @ApiModelProperty("middlename of the user")
        var middleName: String? = null,

        @ApiModelProperty("surname of the user")
        var surname: String? = null
)