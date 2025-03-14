package com.luckyzyx.luckytool.hook.utils.preferences

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object PreferenceReflections {

    @Obfuscate
    object Companion {

        fun callFinder(
            preference: Any, vararg args: Any, finder: MethodFinder.(Boolean) -> Unit
        ): Any? {
            val isSuper = preference.javaClass.hasMethod { finder(false) }.not()
            return preference.current().method { finder(isSuper) }.call(*args)
        }

        fun <T> invokeFinder(
            preference: Any, vararg args: Any, finder: MethodFinder.(Boolean) -> Unit
        ): T? {
            val isSuper = preference.javaClass.hasMethod { finder(false) }.not()
            return preference.current().method { finder(isSuper) }.invoke<T>(*args)
        }

    }

    fun addPreference(preferenceScreen: Any, preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preferenceScreen, preference) {
            name = "addPreference"
            param("androidx.preference.Preference")
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun findPreference(preferenceScreen: Any, charSequence: CharSequence): Any? {
        return Companion.callFinder(preferenceScreen, charSequence) {
            name = "findPreference"
            param(CharSequenceClass)
            if (it) superClass()
        }
    }

    fun getPreferenceCount(preferenceScreen: Any): Any? {
        return Companion.callFinder(preferenceScreen) {
            name = "getPreferenceCount"
            emptyParam()
            returnType = IntType
            if (it) superClass()
        }
    }

    fun removeAll(preferenceScreen: Any) {
        Companion.callFinder(preferenceScreen) {
            name = "removeAll"
            emptyParam()
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun removePreference(preferenceScreen: Any, preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preferenceScreen, preference) {
            name = "removePreference"
            param("androidx.preference.Preference")
            returnType = UnitType
            if (it) superClass()
        } ?: false
    }

    fun getContext(preference: Any): Context? {
        return Companion.invokeFinder<Context>(preference) {
            name = "getContext"
            emptyParam()
            returnType = ContextClass
            if (it) superClass()
        }
    }

    fun getIcon(preference: Any): Drawable? {
        return Companion.invokeFinder<Drawable>(preference) {
            name = "getIcon"
            emptyParam()
            returnType = DrawableClass
            if (it) superClass()
        }
    }

    fun setIcon(preference: Any, resId: Int) {
        Companion.callFinder(preference, resId) {
            name = "setIcon"
            param(IntType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun setIcon(preference: Any, drawable: Drawable) {
        Companion.callFinder(preference, drawable) {
            name = "setIcon"
            param(DrawableClass)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun getIntent(preference: Any): Intent? {
        return Companion.invokeFinder<Intent>(preference) {
            name = "getIntent"
            emptyParam()
            returnType = IntentClass
            if (it) superClass()
        }
    }

    fun setIntent(preference: Any, intent: Intent) {
        Companion.callFinder(preference, intent) {
            name = "setIntent"
            param(IntentClass)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun getKey(preference: Any): String {
        return Companion.invokeFinder<String>(preference) {
            name = "getKey"
            emptyParam()
            returnType = StringClass
            if (it) superClass()
        } ?: ""
    }

    fun setKey(preference: Any, key: String) {
        Companion.callFinder(preference, key) {
            name = "setKey"
            param(StringClass)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun getSummary(preference: Any): CharSequence? {
        return Companion.invokeFinder<CharSequence>(preference) {
            name = "getSummary"
            emptyParam()
            returnType = CharSequenceClass
            if (it) superClass()
        }
    }

    fun setSummary(preference: Any, resId: Int) {
        Companion.callFinder(preference, resId) {
            name = "setSummary"
            param(IntType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun setSummary(preference: Any, charSequence: CharSequence) {
        Companion.callFinder(preference, charSequence) {
            name = "setSummary"
            param(CharSequenceClass)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun isVisible(preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preference) {
            name = "isVisible"
            emptyParam()
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun setVisible(preference: Any, visible: Boolean) {
        Companion.callFinder(preference, visible) {
            name = "setVisible"
            param(BooleanType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun isEnabled(preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preference) {
            name = "isEnabled"
            emptyParam()
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun setEnabled(preference: Any, enable: Boolean) {
        Companion.callFinder(preference, enable) {
            name = "setEnabled"
            param(BooleanType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun isPersistent(preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preference) {
            name = "isPersistent"
            emptyParam()
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun setPersistent(preference: Any, enable: Boolean) {
        Companion.callFinder(preference, enable) {
            name = "setPersistent"
            param(BooleanType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun isSelectable(preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preference) {
            name = "isSelectable"
            emptyParam()
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun setSelectable(preference: Any, enable: Boolean) {
        Companion.callFinder(preference, enable) {
            name = "setSelectable"
            param(BooleanType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun getTitle(preference: Any): CharSequence? {
        return Companion.invokeFinder<CharSequence>(preference) {
            name = "getTitle"
            emptyParam()
            returnType = CharSequenceClass
            if (it) superClass()
        }
    }

    fun setTitle(preference: Any, resId: Int) {
        Companion.callFinder(preference, resId) {
            name = "setTitle"
            param(IntType)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun setTitle(preference: Any, charSequence: CharSequence) {
        Companion.callFinder(preference, charSequence) {
            name = "setTitle"
            param(CharSequenceClass)
            returnType = UnitType
            if (it) superClass()
        }
    }

    fun isCopyingEnabled(preference: Any): Boolean {
        return Companion.invokeFinder<Boolean>(preference) {
            name = "isCopyingEnabled"
            emptyParam()
            returnType = BooleanType
            if (it) superClass()
        } ?: false
    }

    fun setCopyingEnabled(preference: Any, copy: Boolean) {
        Companion.callFinder(preference, copy) {
            name = "setCopyingEnabled"
            param(BooleanType)
            returnType = UnitType
            if (it) superClass()
        }
    }

}