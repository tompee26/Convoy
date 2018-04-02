package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.SearchModule
import com.tompee.convoy.dependency.scopes.SearchScope
import com.tompee.convoy.feature.search.SearchActivity
import com.tompee.convoy.feature.search.profile.ProfileDialog
import dagger.Component

@SearchScope
@Component(dependencies = [AppComponent::class],
        modules = [SearchModule::class])
interface SearchComponent {
    fun inject(searchActivity: SearchActivity)
    fun inject(searchActivity: ProfileDialog)
}