package no.breale17.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Information about a post")
data class PostDto(

        @ApiModelProperty("Title of the post")
        var title: String? = null,

        @ApiModelProperty("Message of the post")
        var message: String? = null,

        @ApiModelProperty("Creation date of the post")
        var date: String? = null,

        @ApiModelProperty("userId of the author")
        var userId: String? = null,

        @ApiModelProperty("The id of the movie")
        var id: String? = null
)