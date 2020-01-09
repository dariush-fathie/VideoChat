package ir.jin724.videochat.util

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import timber.log.Timber

object KeyboardUtil {

    fun closeKeyBoard(token: IBinder, context: Context) {
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(token, 0)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun openKeyBoard(context: Context) {
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}