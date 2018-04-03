package com.tompee.convoy.dependency.component

import com.tompee.convoy.dependency.module.FriendModule
import com.tompee.convoy.dependency.scopes.FriendScope
import com.tompee.convoy.feature.friend.FriendListActivity
import dagger.Component

@FriendScope
@Component(dependencies = [AppComponent::class],
        modules = [FriendModule::class])
interface FriendComponent {
    fun inject(friendListActivity: FriendListActivity)
}