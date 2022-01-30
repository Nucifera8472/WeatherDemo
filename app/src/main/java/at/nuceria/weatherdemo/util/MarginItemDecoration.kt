package at.nuceria.weatherdemo.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @param outerSpace how much margin to add before and after the items
 * @param innerSpace how much margin to add in between items
 */
class MarginItemDecoration(private val outerSpace: Int, private val innerSpace: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            val leftSpace = if (parent.getChildAdapterPosition(view) == 0) {
                outerSpace
            } else innerSpace

            val rightSpace =
                if (parent.getChildAdapterPosition(view) + 1 == parent.adapter?.itemCount ?: 0) {
                    outerSpace
                } else innerSpace

            right = rightSpace
            left = leftSpace
        }

    }
}
