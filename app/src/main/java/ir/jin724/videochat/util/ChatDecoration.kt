package ir.jin724.videochat.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ChatDecoration(
    context: Context,
    spaceDp: Int
) :

    RecyclerView.ItemDecoration() {

    private val lastItemBottomPadding = 48.toPx(context)
    private val spacePx: Int = spaceDp.toPx(context).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition =
            (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val itemCount = parent.adapter?.itemCount!!
        val viewType = parent.adapter!!.getItemViewType(itemPosition)

        // last item (first item if itemCount = 1)
        outRect.bottom = spacePx * 2

        if (itemPosition == itemCount.minus(1)) {
            outRect.bottom = spacePx * 2
        }

        // first item
        if (itemPosition == 0) {
            outRect.top == spacePx * 2
        }

    }
}