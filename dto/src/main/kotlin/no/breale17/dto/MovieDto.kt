package no.breale17.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(description = "Information about a movie")
data class MovieDto(

        @ApiModelProperty("The title of the movie")
        var title: String? = null,

        @ApiModelProperty("Link to the poster for the movie")
        var description: String? = null,

        @ApiModelProperty("The release date of the movie")
        var releaseDate: String? = null,

        @ApiModelProperty("The genres of the movie")
        var genres: Set<String>? = null,

        @ApiModelProperty("Vote count for the movie")
        var voteCount: Int? = null,

        @ApiModelProperty("Average vote score for the movie")
        var voteAverage: String? = null,

        @ApiModelProperty("Price of the movie")
        var price: String? = null,

        @ApiModelProperty("The id of the movie")
        var id: String? = null
)