package com.luckyzyx.luckytool.utils

import com.highcapable.yukihookapi.hook.log.YLog
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.ClassDataList
import org.luckypray.dexkit.query.FieldDataList
import org.luckypray.dexkit.query.MethodDataList

@Suppress("MemberVisibilityCanBePrivate")
object DexkitUtils {
    const val tag = "LuckyTool"

    /**
     * 创建Dexkit安全实例
     * @param appPath String
     * @return DexKitBridge?
     */
    fun create(appPath: String): DexKitBridge? {
        System.loadLibrary("dexkit")
        return DexKitBridge.create(appPath)
    }

    /**
     * 创建Dexkit安全实例
     * @param appPath String
     * @param result Function1<DexKitBridge, Unit>
     */
    fun create(appPath: String, result: (DexKitBridge) -> Unit) {
        System.loadLibrary("dexkit")
        DexKitBridge.create(appPath)?.use { result(it) }
    }

    /**
     * 检查搜索到的类列表并打印LOG
     * @receiver ClassDataList
     * @param instance String
     * @param onlyOne Boolean
     * @return ClassDataList
     */
    fun ClassDataList.checkDataList(
        instance: String, onlyOne: Boolean = true, isDebug: Boolean = false
    ): ClassDataList {
        when {
            isNullOrEmpty() -> YLog.error("$instance -> findMethod isNullOrEmpty", tag = tag)
            size != 1 && (onlyOne || isDebug) -> {
                var find = ""
                forEach { find += "[${it.name}]" }
                if (isDebug) YLog.debug("$instance -> findMethod size ($size) -> $find", tag = tag)
                else YLog.error("$instance -> findMethod size ($size) -> $find", tag = tag)
            }

            size == 1 -> if (isDebug) YLog.debug(
                "$instance -> findMethod ${first().name}", tag = tag
            )
        }
        return this
    }

    /**
     * 检查搜索到的方法列表并打印LOG
     * @receiver MethodDataList
     * @param instance String
     * @param onlyOne Boolean
     * @return MethodDataList
     */
    fun MethodDataList.checkDataList(
        instance: String, onlyOne: Boolean = true, isDebug: Boolean = false
    ): MethodDataList {
        when {
            isNullOrEmpty() -> YLog.error("$instance -> findMethod isNullOrEmpty", tag = tag)
            size != 1 && (onlyOne || isDebug) -> {
                var find = ""
                forEach { find += "[${it.className}|${it.methodName}]" }
                if (isDebug) YLog.debug("$instance -> findMethod size ($size) -> $find", tag = tag)
                else YLog.error("$instance -> findMethod size ($size) -> $find", tag = tag)
            }

            size == 1 -> if (isDebug) YLog.debug(
                "$instance -> findMethod ${first().className}|${first().methodName}", tag = tag
            )
        }
        return this
    }

    fun FieldDataList.checkDataList(
        instance: String, onlyOne: Boolean = true, isDebug: Boolean = false
    ): FieldDataList {
        when {
            isNullOrEmpty() -> YLog.error("$instance -> findField isNullOrEmpty", tag = tag)
            size != 1 && (onlyOne || isDebug) -> {
                var find = ""
                forEach { find += "[${it.className}|${it.fieldName}]" }
                if (isDebug) YLog.debug("$instance -> findField size ($size) -> $find", tag = tag)
                else YLog.error("$instance -> findField size ($size) -> $find", tag = tag)
            }

            size == 1 -> if (isDebug) YLog.debug(
                "$instance -> findField ${first().className}|${first().fieldName}", tag = tag
            )
        }
        return this
    }
}