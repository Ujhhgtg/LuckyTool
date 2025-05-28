package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class FlowUtils(val classLoader: ClassLoader?) {

    val stateFlowKt = "kotlinx.coroutines.flow.StateFlowKt".toClass(classLoader)
    val flowKtShareKt = "kotlinx.coroutines.flow.FlowKt__ShareKt".toClass(classLoader)

    fun MutableStateFlow(any: Any): Any? {
        return stateFlowKt.method { name = "MutableStateFlow" }.get().call(any)
    }

    fun asStateFlow(mutableStateFlow: Any): Any? {
        return flowKtShareKt.method { name = "asStateFlow" }.get().call(mutableStateFlow)
    }

    inline fun <reified T> getValue(flow: Any): T? {
        val flowCls = flow.javaClass
        val isSuper = flowCls.hasMethod { name = "getValue" }.not()
        return flow.current().method { name = "getValue";superClass(isSuper) }.invoke()
    }

}