package ir.jin724.videochat.util

import android.content.Context

object Converter {

    fun getScreenWidthPx(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenWidthDp(context: Context): Int {
        return dpFromPx(context, getScreenWidthPx(context).toFloat()).toInt()
    }

    fun getScreenHeightPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenHeightDp(context: Context): Int {
        return dpFromPx(context, getScreenWidthPx(context).toFloat()).toInt()
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


}