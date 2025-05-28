@file:Suppress("MemberVisibilityCanBePrivate", "PropertyName", "FunctionName")

package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.luckyzyx.luckytool.hook.hookers.HookSystemUI.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class RefUtils(val classLoader: ClassLoader?) {

    val BooleanRefCls = "kotlin.jvm.internal.Ref\$BooleanRef"
    val FloatRefCls = "kotlin.jvm.internal.Ref\$FloatRef"
    val IntRefCls = "kotlin.jvm.internal.Ref\$IntRef"
    val LongRefCls = "kotlin.jvm.internal.Ref\$LongRef"
    val ObjectRefCls = "kotlin.jvm.internal.Ref\$ObjectRef"

    fun BooleanRef(): Any? {
        return BooleanRef(null)
    }

    fun BooleanRef(element: Boolean? = null): Any? {
        return BooleanRefCls.toClass(classLoader).buildOf()?.apply {
            current().field { name = "element";type = BooleanType }.set(element)
        }
    }

    fun FloatRef(): Any? {
        return FloatRef(null)
    }

    fun FloatRef(element: Float? = null): Any? {
        return FloatRefCls.toClass(classLoader).buildOf()?.apply {
            current().field { name = "element";type = FloatType }.set(element)
        }
    }

    fun IntRef(): Any? {
        return IntRef(null)
    }

    fun IntRef(element: Int? = null): Any? {
        return IntRefCls.toClass(classLoader).buildOf()?.apply {
            current().field { name = "element";type = IntType }.set(element)
        }
    }

    fun LongRef(): Any? {
        return LongRef(null)
    }

    fun LongRef(element: Long? = null): Any? {
        return LongRefCls.toClass(classLoader).buildOf()?.apply {
            current().field { name = "element";type = LongType }.set(element)
        }
    }

    inline fun <reified T> ObjectRef(): Any? {
        return ObjectRef<T>(null)
    }

    inline fun <reified T> ObjectRef(element: T? = null): Any? {
        return ObjectRefCls.toClass(classLoader).buildOf()?.apply {
            current().field { name = "element";type = T::class.java }.set(element)
        }
    }

    inline fun <reified T : Any> getRefElement(ref: Any): T? {
        return ref.current().field { name = "element";type = T::class.java }.cast<T>()
    }

}