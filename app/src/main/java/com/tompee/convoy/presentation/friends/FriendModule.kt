package com.tompee.convoy.presentation.friends

import android.content.Context
import com.tompee.convoy.di.scope.FriendScope
import com.tompee.convoy.domain.interactor.FriendInteractor
import com.tompee.convoy.domain.repo.AccountRepository
import com.tompee.convoy.domain.repo.UserRepository
import com.tompee.convoy.presentation.common.SectionAdapter
import com.tompee.convoy.presentation.friends.profile.ProfileDialogViewModel
import com.tompee.convoy.presentation.friends.search.FriendSearchAdapter
import com.tompee.convoy.presentation.friends.search.FriendSearchViewModel
import dagger.Module
import dagger.Provides

@Module
class FriendModule {

    @FriendScope
    @Provides
    fun provideSectionAdapter(friendListAdapter: FriendListAdapter): SectionAdapter = SectionAdapter(friendListAdapter)

    @FriendScope
    @Provides
    fun provideFriendListAdapter(): FriendListAdapter = FriendListAdapter()

    @FriendScope
    @Provides
    fun provideFriendListViewModelFactory(context: Context, interactor: FriendInteractor): FriendListViewModel.Factory =
        FriendListViewModel.Factory(context, interactor)

    @FriendScope
    @Provides
    fun provideProfileDialogViewModelFactory(interactor: FriendInteractor): ProfileDialogViewModel.Factory =
        ProfileDialogViewModel.Factory(interactor)

    @FriendScope
    @Provides
    fun provideFriendSearchAdapter(): FriendSearchAdapter = FriendSearchAdapter()

    @FriendScope
    @Provides
    fun provideFriendSearchViewModelFactory(interactor: FriendInteractor): FriendSearchViewModel.Factory =
        FriendSearchViewModel.Factory(interactor)

    @FriendScope
    @Provides
    fun provideInteractor(
        accountRepository: AccountRepository,
        userRepository: UserRepository
    ): FriendInteractor = FriendInteractor(accountRepository, userRepository)
}