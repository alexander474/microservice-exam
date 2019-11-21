package no.breale17.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Information about a friend request")
data class FriendRequestDto (
    @ApiModelProperty("userId of the user sending the request")
    var from: String? = null,
    @ApiModelProperty("userId of the user receiving the request")
    var to: String? = null,
    @ApiModelProperty("status of the friend request")
    var status: FriendRequestStatus? = null
)