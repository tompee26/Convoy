package com.tompee.convoy.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.support.v4.util.LongSparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.dependency.component.ConfigPersistentComponent
import com.tompee.convoy.dependency.component.DaggerConfigPersistentComponent
import com.tompee.convoy.dependency.component.FragmentComponent
import com.tompee.convoy.dependency.module.FragmentModule
import java.util.concurrent.atomic.AtomicLong

abstract class BaseDialogFragment : DialogFragment() {
    private var fragmentComponent: FragmentComponent? = null
    private var fragmentId = 0L

    companion object {
        private const val KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID"
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
        private val NEXT_ID = AtomicLong(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the FragmentComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        fragmentId = savedInstanceState?.getLong(KEY_FRAGMENT_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(fragmentId) == null) {
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(ConvoyApplication[activity as Context].component)
                    .build()
            componentsArray.put(fragmentId, configPersistentComponent)
        } else {
            configPersistentComponent = componentsArray.get(fragmentId)
        }
        fragmentComponent = configPersistentComponent.fragmentComponent(FragmentModule(this))
        setupComponent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun setupComponent()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_FRAGMENT_ID, fragmentId)
    }

    override fun onDestroy() {
        if (!activity!!.isChangingConfigurations) {
            componentsArray.remove(fragmentId)
        }
        super.onDestroy()
    }

    fun dialogFragmentComponent() = fragmentComponent as FragmentComponent
}