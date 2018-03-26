package com.tompee.convoy.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.FragmentActivity
import android.support.v4.util.LongSparseArray
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.dependency.component.ActivityComponent
import com.tompee.convoy.dependency.component.ConfigPersistentComponent
import com.tompee.convoy.dependency.component.DaggerConfigPersistentComponent
import com.tompee.convoy.dependency.module.ActivityModule
import java.util.concurrent.atomic.AtomicLong

abstract class BaseActivity : AppCompatActivity() {

    private var activityComponent: ActivityComponent? = null
    private var activityId = 0L

    companion object {
        private const val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        activityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(activityId) == null) {
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(ConvoyApplication[this].component)
                    .build()
            componentsArray.put(activityId, configPersistentComponent)
        } else {
            configPersistentComponent = componentsArray.get(activityId)
        }
        activityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
        activityComponent?.inject(this)

        setupComponent()
    }

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun setupComponent()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, activityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            componentsArray.remove(activityId)
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun activityComponent() = activityComponent as ActivityComponent
}