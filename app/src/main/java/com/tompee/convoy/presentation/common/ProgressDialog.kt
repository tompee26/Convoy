package com.tompee.convoy.presentation.common

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tompee.convoy.R
import com.tompee.convoy.databinding.DialogProgressBinding
import com.tompee.convoy.presentation.base.BaseDialogFragment

class ProgressDialog : BaseDialogFragment() {

    companion object {
        private const val TAG_COLOR = "color"
        private const val TAG_TEXT = "text"

        fun newInstance(background: Int, textRes: Int): ProgressDialog {
            val dialog = ProgressDialog()
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FragmentDialog)
            val bundle = Bundle()
            bundle.putInt(TAG_COLOR, background)
            bundle.putInt(TAG_TEXT, textRes)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: DialogProgressBinding =
            DataBindingUtil.inflate(activity?.layoutInflater!!, R.layout.dialog_progress, null, false)

        binding.apply {
            progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            root.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    arguments?.getInt(TAG_COLOR) ?: R.color.colorLoginButton
                )
            )
            progressText.setText(
                arguments?.getInt(TAG_TEXT)
                    ?: R.string.progress_login_authenticate
            )
        }
        return AlertDialog.Builder(activity!!)
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }

    override fun performInject() {
    }

}