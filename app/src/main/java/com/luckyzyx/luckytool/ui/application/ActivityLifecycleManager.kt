package com.luckyzyx.luckytool.ui.application

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityLifecycleManager {

    private val activities = mutableListOf<WeakReference<Activity>>()

    fun registerActivity(activity: Activity) {
        activities.add(WeakReference(activity))
    }

    fun unregisterActivity(activity: Activity) {
        activities.removeAll { it.get() == activity }
    }

    fun recreateAllActivities() {
        activities.forEach { it.get()?.recreate() }
    }

}