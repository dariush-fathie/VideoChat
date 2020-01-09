package ir.jin724.videochat.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun ViewGroup.inflater(): LayoutInflater {
    return LayoutInflater.from(context)
}


fun View.hide(boolean: Boolean) {
    visibility = if (boolean) {
        View.GONE
    } else {
        View.VISIBLE
    }
}


fun Context.getColour(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun View.getColour(@ColorRes color: Int): Int {
    return ContextCompat.getColor(context, color)
}