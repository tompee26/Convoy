package com.tompee.convoy.presentation.common

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drivemode.techtestrefactored.presentation.common.BindableAdapter

@BindingAdapter("image")
fun setImage(view: CircularImageView, url: String?) {
    if (url != null) {
        Glide.with(view).load(url).into(view)
    }
}

@BindingAdapter("data")
fun <T> setRecyclerViewData(recyclerView: RecyclerView, data: T) {
    if (recyclerView.adapter is BindableAdapter<*> && data != null) {
        @Suppress("UNCHECKED_CAST")
        (recyclerView.adapter as BindableAdapter<T>).setData(data)
    }
}

@BindingAdapter("visibleGone")
fun showHide(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible != null && isVisible) View.VISIBLE else View.GONE
}