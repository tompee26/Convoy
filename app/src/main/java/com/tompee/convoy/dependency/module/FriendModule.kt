package com.tompee.convoy.dependency.module

import android.content.Context
import com.tompee.convoy.feature.friend.FriendListPresenter
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named

@Module
class FriendModule {
    @Provides
    fun provideFriendListPresenter(context: Context,
                                   userInteractor: UserInteractor,
                                   @Named("io") io: Scheduler,
                                   @Named("ui") ui: Scheduler): FriendListPresenter {
        return FriendListPresenter(context, userInteractor, io, ui)
    }
}
