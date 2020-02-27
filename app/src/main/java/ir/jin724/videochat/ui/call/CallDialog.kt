package ir.jin724.videochat.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.jin724.videochat.R
import ir.jin724.videochat.databinding.IncomingCallBinding
import ir.jin724.videochat.util.ServiceUtil
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
        binding = IncomingCallBinding.inflate(inflater, container, false)
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


    private fun updateFabMargin() {
        val service = ServiceUtil.getAccessibilityManager(requireContext());

        if (service.isTouchExplorationEnabled) {
            val px16 = ViewUtil.dpToPx(requireContext(), 16)
            Timber.e("touchExplorationEnabled ${service.isTouchExplorationEnabled}")

            /*val lp = binding.fabQuickMessage.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = px16
            binding.fabQuickMessage.layoutParams = lp

            val lp2  = binding.fabDecline.layoutParams as LinearLayout.LayoutParams
            lp2.topMargin = px16
            binding.fabDecline.layoutParams = lp2*/

            ViewUtil.setTopMargin(binding.fabDecline, px16)
            ViewUtil.setTopMargin(binding.fabQuickMessage, px16)
        }
    }


}