/**
 * Copied from:
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/rest/rest-dto/src/main/kotlin/org/tsdes/advanced/rest/dto/WrappedResponse.kt
 */
package no.utils.wrapper

import io.swagger.annotations.ApiModelProperty

open class WrappedResponse<T>(

        @ApiModelProperty("The HTTP status code of the response")
        var code: Int? = null,

        @ApiModelProperty("The wrapped payload")
        var data: T? = null,

        @ApiModelProperty("Error message in case where was an error")
        var message: String? = null,

        @ApiModelProperty("String representing either 'success', user error ('error') or server failure ('fail')")
        var status: ResponseStatus? = null
) {

    fun validated(): WrappedResponse<T> {

        val c: Int = code ?: throw IllegalStateException("Missing HTTP code")

        if (c !in 100..599) {
            throw  IllegalStateException("Invalid HTTP code: $code")
        }

        if (status == null) {
            status = when (c) {
                in 100..399 -> ResponseStatus.SUCCESS
                in 400..499 -> ResponseStatus.ERROR
                in 500..599 -> ResponseStatus.FAIL
                else -> throw  IllegalStateException("Invalid HTTP code: $code")
            }
        } else {
            val wrongSuccess = (status == ResponseStatus.SUCCESS && c !in 100..399)
            val wrongError = (status == ResponseStatus.ERROR && c !in 400..499)
            val wrongFail = (status == ResponseStatus.FAIL && c !in 500..599)

            val wrong = wrongSuccess || wrongError || wrongFail
            if (wrong) {
                throw IllegalArgumentException("Status $status is not correct for HTTP code $c")
            }
        }

        if (status != ResponseStatus.SUCCESS && message == null) {
            throw IllegalArgumentException("Failed response, but with no describing 'message' for it")
        }

        return this
    }

    enum class ResponseStatus {
        SUCCESS, FAIL, ERROR
    }
}