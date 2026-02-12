package com.inflexionco.glidebrowser.domain.model

/**
 * Represents SSL certificate errors
 */
data class SslErrorInfo(
    val url: String,
    val errorType: SslErrorType,
    val certificateIssuer: String?,
    val certificateSubject: String?,
    val certificateValidFrom: String?,
    val certificateValidTo: String?
)

enum class SslErrorType(val description: String) {
    NOT_YET_VALID("Certificate is not yet valid"),
    EXPIRED("Certificate has expired"),
    ID_MISMATCH("Certificate hostname mismatch"),
    UNTRUSTED("Certificate authority is not trusted"),
    DATE_INVALID("Certificate date is invalid"),
    INVALID("Certificate is invalid"),
    UNKNOWN("Unknown certificate error")
}