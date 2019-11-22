/**
 * Got inspiration from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/rest/pagination/src/main/kotlin/org/tsdes/advanced/rest/pagination/dto/hal/HalLink.kt
 */
package no.utils.pagination

import io.swagger.annotations.ApiModelProperty

/*
 Taken from Andrea's repo
 */
open class HalLink(

        @ApiModelProperty("URL of the link")
        var href: String = ""
)