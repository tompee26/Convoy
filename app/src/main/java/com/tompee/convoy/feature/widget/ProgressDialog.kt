package com.tompee.convoy.feature.widget

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_progress.view.*

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
        val view = activity?.layoutInflater?.inflate(R.layout.dialog_progress, null)!!
        view.progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        view.setBackgroundColor(ContextCompat.getColor(context!!,
                arguments?.getInt(TAG_COLOR) ?: R.color.colorLoginButton))
        view.progress_text.setText(arguments?.getInt(TAG_TEXT)
                ?: R.string.progress_login_authenticate)
        return AlertDialog.Builder(activity!!)
                .setView(view)
                .setCancelable(false)
                .create()
    }

    override fun setupComponent() {
    }
}