package no.utils.pagination

import io.swagger.annotations.ApiModelProperty

/*
   Taken from Andrea's repo
 */
open class HalObject(

        @ApiModelProperty("HAL links")
        var _links: MutableMap<String, HalLink> = mutableMapOf()
)