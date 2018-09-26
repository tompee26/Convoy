package com.tompee.convoy.feature.search.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tompee.convoy.R
import com.tompee.convoy.feature.widget.CircularImageView
import com.tompee.convoy.interactor.model.User
import io.reactivex.subjects.PublishSubject

class UserListAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    val clickObservable: PublishSubject<String> = PublishSubject.create<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false))
    }

    override fun getItemCount(): Int = userList.size


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    inner class UserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(user: User) {
            val decoded = Base64.decode(user.image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
            view.findViewById<CircularImageView>(R.id.rowImage).setImageBitmap(bitmap)
            view.findViewById<TextView>(R.id.fullName).text = "${user.first} ${user.last}"
            view.findViewById<TextView>(R.id.displayName).text = user.display
            view.findViewById<TextView>(R.id.status).text = user.email

            view.setOnClickListener({ _ ->
                clickObservable.onNext(user.email)
            })
        }
    }
}