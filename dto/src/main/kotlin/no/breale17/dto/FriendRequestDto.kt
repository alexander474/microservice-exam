package no.breale17.dto

import io.swagger.annotations.ApiModelProperty

data class FriendRequestDto (
    @ApiModelProperty("userId of the user sending the request")
    var from: String? = null,
    @ApiModelProperty("userId of the user receiving the request")
    var to: String? = null,
    @ApiModelProperty("status of the friend request")
    var status: FriendRequestStatus? = null
)