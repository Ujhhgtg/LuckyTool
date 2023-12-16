package com.luckyzyx.luckytool.hook.utils.appcompat.dialog

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.android.AttributeSetClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType

@Suppress("unused", "MemberVisibilityCanBePrivate")
class COUISwitch {

    private val clazzString = "com.coui.appcompat.couiswitch.COUISwitch"
    var context: Context
    var clazz: Class<*>
    var builder: Any?

    constructor(context: Context, classloader: ClassLoader?) {
        this.context = context
        this.clazz = clazzString.toClass(classloader)
        builder = clazz.buildOf(context) {
            param(ContextClass)
        }
    }

    constructor(context: Context, attributeSet: AttributeSet, classloader: ClassLoader?) {
        this.context = context
        this.clazz = clazzString.toClass(classloader)
        builder = clazz.buildOf(context, attributeSet) {
            param(ContextClass, AttributeSetClass)
        }
    }

    constructor(context: Context, attributeSet: AttributeSet, int: Int, classloader: ClassLoader?) {
        this.context = context
        this.clazz = clazzString.toClass(classloader)
        builder = clazz.buildOf(context, attributeSet, int) {
            param(ContextClass, AttributeSetClass, IntType)
        }
    }

    fun setTag(tag: Any): Any? {
        return builder?.current()?.method {
            name = "setTag"
            param(AnyClass)
            superClass()
        }?.call(tag)
    }

    fun setClickable(clickable: Boolean): Any? {
        return builder?.current()?.method {
            name = "setClickable"
            param(BooleanType)
            superClass()
        }?.call(clickable)
    }

    fun setChecked(bool: Boolean): Any? {
        return builder?.current()?.method {
            name = "setChecked"
            param(BooleanType)
        }?.call(bool)
    }

    fun setEnabled(bool: Boolean): Any? {
        return builder?.current()?.method {
            name = "setEnabled"
            param(BooleanType)
            superClass()
        }?.call(bool)
    }

    fun setChecked(bool: Boolean, bool2: Boolean): Any? {
        return builder?.current()?.method {
            name = "setChecked"
            param(BooleanType, BooleanType)
        }?.call(bool, bool2)
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener): Any? {
        return builder?.current()?.method {
            name = "setOnCheckedChangeListener"
            param(CompoundButton.OnCheckedChangeListener::class.java)
            superClass()
        }?.call(listener)
    }
}