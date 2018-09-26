package com.tompee.convoy.core.navigator.impl

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.tompee.convoy.core.navigator.Navigator

class NavigatorImpl(private val navController: NavController) : Navigator {
    override fun navigate(id: Int) {
        navController.navigate(id)
    }

    override fun popUp(@IdRes action: Int, @IdRes id : Int) {
        val options = NavOptions.Builder()
                .setPopUpTo(id, true)
                .build()
        navController.navigate(action, null, options)
    }

    override fun popBackStack() {
        navController.popBackStack()
    }
}