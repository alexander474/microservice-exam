package no.utils.pagination

import io.swagger.annotations.ApiModelProperty

/*
 Taken from Andrea's repo
 */
open class HalLink(

        @ApiModelProperty("URL of the link")
        var href: String = ""
)