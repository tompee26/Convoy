package com.tompee.convoy.core.navigator.impl

import android.content.Intent
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.core.navigator.Navigator

class NavigatorImpl(private val activity: BaseActivity) : Navigator {

    override fun <T> moveToScreen(clazz: Class<T>) {
        val intent = Intent(activity, clazz)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
        activity.finish()
    }
}