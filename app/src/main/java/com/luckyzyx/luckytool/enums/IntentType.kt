package com.luckyzyx.luckytool.enums

import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
enum class IntentType {
    SINGLE_SHARE, MULTI_SHARE,
    PROCESS_TEXT,
    CONTENT, FILE,
    HTTP_LINK, HTTPS_LINK,
    UNKNOWN;

    companion object {

        fun fromString(value: String): IntentType {
            return try {
                enumValueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }

    }
}