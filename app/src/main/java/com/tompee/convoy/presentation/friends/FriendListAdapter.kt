package com.tompee.convoy.presentation.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.drivemode.techtestrefactored.presentation.common.BindableAdapter
import com.tompee.convoy.R
import com.tompee.convoy.databinding.RowUserBinding
import com.tompee.convoy.domain.entities.User

class FriendListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), BindableAdapter<List<User>> {

    private var userList = emptyList<User>()

    val onClick = MutableLiveData<String>()

    override fun setData(data: List<User>) {
        userList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = DataBindingUtil.inflate<RowUserBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_user,
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(userList[position])
    }

    inner class UserViewHolder(private val binding: RowUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onClick.postValue(user.email)
            }
        }
    }
}