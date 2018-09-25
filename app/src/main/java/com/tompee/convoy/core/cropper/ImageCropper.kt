package com.tompee.convoy.core.cropper

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tompee.convoy.Constants
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ImageCropper(private val activity: BaseActivity) {
    private val uriSubject = PublishSubject.create<Uri>()
    private lateinit var imageView : ImageView

    fun startImageCropper(imageView : ImageView): Observable<Uri> {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMinCropResultSize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE)
                .setRequestedSize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE,
                        CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setAspectRatio(1, 1)
                .setActivityMenuIconColor(ContextCompat.getColor(activity, R.color.colorLight))
                .setAllowFlipping(false)
                .setActivityTitle(activity.getString(R.string.profile_label_picture))
                .start(activity)
        this.imageView = imageView
        return uriSubject
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                imageView.setImageURI(resultUri)
                uriSubject.onNext(resultUri)
            }
        }
    }

}