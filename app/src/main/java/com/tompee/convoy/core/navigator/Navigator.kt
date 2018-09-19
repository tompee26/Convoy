package com.tompee.convoy.core.navigator

interface Navigator {
    fun <T> moveToScreen(clazz: Class<T>)
}