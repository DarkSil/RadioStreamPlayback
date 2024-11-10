package com.sli.radiostreamplayback.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.sli.radiostreamplayback.R
import com.sli.radiostreamplayback.databinding.ErrorFragmentBinding


open class BaseErrorDialog : DialogFragment() {

    private val binding by lazy { ErrorFragmentBinding.inflate(layoutInflater) }

    protected var label: String? = null
    protected var error: String? = null
    protected var listener: DialogButtonClickListener? = null
    protected var buttonText: String? = null

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label?.let {
            binding.textLabel.text = it
        }

        error?.let { errorText ->

            val text = StringBuilder()
                .append("<br/><br/>")
                .append("*")
                .append(errorText)
                .append("*")

            binding.textDescription.text = HtmlCompat.fromHtml(
                getString(R.string.error_description) + text,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        }

        buttonText?.let {
            binding.buttonText.text = it
        }

        binding.textDescription.movementMethod = LinkMovementMethod.getInstance()
        binding.buttonText.setOnClickListener {
            if (listener == null) {
                dismiss()
            }
            listener?.onButtonClicked(this)
        }
    }

    fun interface DialogButtonClickListener {
        fun onButtonClicked(dialog: BaseErrorDialog)
    }

    class Builder {
        private val dialog = BaseErrorDialog()

        fun setLabel(text: String?) : Builder {
            dialog.label = text
            return this
        }

        fun setError(text: String?) : Builder {
            dialog.error = text
            return this
        }

        fun setButtonText(text: String?) : Builder {
            dialog.buttonText = text
            return this
        }

        fun setButtonListener(listener: DialogButtonClickListener) : Builder {
            dialog.listener = listener
            return this
        }

        fun setIsCancelable(isCancelable: Boolean) : Builder {
            dialog.setCancelable(isCancelable)
            return this
        }

        fun build() : BaseErrorDialog {
            return dialog
        }
    }

}