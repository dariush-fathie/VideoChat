package ir.jin724.videochat.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import ir.jin724.videochat.R
import ir.jin724.videochat.databinding.IncomingCallBinding
import ir.jin724.videochat.util.AccessibilityUtil
import ir.jin724.videochat.util.ViewUtil
import ir.jin724.videochat.view.WebRtcAnswerDeclineButton
import timber.log.Timber


class CallDialog : DialogFragment(), WebRtcAnswerDeclineButton.AnswerDeclineListener {

    private lateinit var binding: IncomingCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncomingCallBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.answerDecline.setAnswerDeclineListener(this)
        binding.answerDecline.startRingingAnimation()

        updateFabMargin()
    }

    override fun onDeclined() {
        Timber.e("onDecline")
    }

    override fun onAnswered() {
        Timber.e("onAnswer")
        binding.answerDecline.invalidate()
    }


    private fun updateFabMargin(){
        if (AccessibilityUtil.areAnimationsDisabled(requireContext())){
            val px16 = ViewUtil.dpToPx(requireContext(),16)

            val lp = binding.fabDecline.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = px16
            binding.fabDecline.layoutParams = lp

            val lp2 = binding.fabQuickMessage.layoutParams as LinearLayout.LayoutParams
            lp2.topMargin = px16
            binding.fabQuickMessage.layoutParams = lp2

            binding.fabQuickMessage.requestLayout()
            binding.fabDecline.requestLayout()
        }
    }


}