package ir.jin724.videochat.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FixedOffsetDividerDecoration2(
    context: Context,
    private var leftDp: Int = 0,
    private var rightDp: Int = 0,
    private var topDp: Int = 0,
    private var bottomDp: Int = 0,
    private var addLastItemPadding: Boolean = false,
    private var spanCount: Int = 1
) :

    RecyclerView.ItemDecoration() {

    private val lastItemBottomPadding = 48.toPx(context)

    init {
        leftDp = leftDp.toPx(context).toInt()
        rightDp = rightDp.toPx(context).toInt()
        topDp = topDp.toPx(context).toInt()
        bottomDp = bottomDp.toPx(context).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        //Timber.tag(this::class.java.simpleName).e("${view::class.java.name} , tag = ${view.tag}")

        val itemPosition =
            (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val itemCount = parent.adapter?.itemCount

        outRect.set(leftDp, topDp, rightDp, bottomDp)

        /*if (itemPosition == itemCount?.minus(1)) {
            outRect.bottom = bottomDp
            if (addLastItemPadding) {
                outRect.bottom = lastItemBottomPadding.toInt()
            }
        }*/

        if (itemPosition >= itemCount?.minus(spanCount)!!) {
            outRect.bottom = bottomDp
            if (addLastItemPadding) {
                outRect.bottom = lastItemBottomPadding.toInt()
            }
        } else {
            outRect.bottom = bottomDp
        }

    }
}