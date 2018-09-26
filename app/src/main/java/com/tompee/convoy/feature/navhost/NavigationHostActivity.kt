package com.tompee.convoy.feature.navhost

import android.app.Activity
import android.content.Intent
import androidx.navigation.findNavController
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerNavigatorComponent
import com.tompee.convoy.dependency.component.NavigatorComponent
import com.tompee.convoy.dependency.module.NavigatorModule

class NavigationHostActivity : BaseActivity() {

    lateinit var component: NavigatorComponent

    //region companion object
    companion object {
        operator fun get(activity: Activity): NavigationHostActivity {
            return activity as NavigationHostActivity
        }
    }
    //endregion

    //region NavigationHostActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.
                childFragmentManager?.fragments!![0].onActivityResult(requestCode, resultCode, data)
    }
    //endregion

    //region BaseActivity
    override fun layoutId(): Int = R.layout.activity_navhost

    override fun setupComponent() {
        component = DaggerNavigatorComponent.builder()
                .navigatorModule(NavigatorModule(findNavController(R.id.nav_host_fragment)))
                .build()
    }
    //endregion

    //region Navigation
    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
    //endregion
}