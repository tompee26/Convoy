package com.tompee.convoy.dependency.module

import android.content.Context
import com.tompee.convoy.feature.search.SearchPresenter
import com.tompee.convoy.feature.search.profile.ProfileDialogPresenter
import com.tompee.convoy.interactor.user.UserInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Named

@Module
class SearchModule {
    @Provides
    fun provideSearchPresenter(userInteractor: UserInteractor,
                               @Named("io") io: Scheduler,
                               @Named("ui") ui: Scheduler): SearchPresenter {
        return SearchPresenter(userInteractor, io, ui)
    }

    @Provides
    fun provideSearchDialogPresenter(context: Context,
                                     userInteractor: UserInteractor,
                                     @Named("io") io: Scheduler,
                                     @Named("ui") ui: Scheduler): ProfileDialogPresenter {
        return ProfileDialogPresenter(context, userInteractor, io, ui)
    }
}