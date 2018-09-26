package com.tompee.convoy.core.navigator

import androidx.annotation.IdRes

interface Navigator {
    fun navigate(@IdRes id: Int)

    fun popUp(@IdRes action: Int, @IdRes id : Int)

    fun popBackStack()
}