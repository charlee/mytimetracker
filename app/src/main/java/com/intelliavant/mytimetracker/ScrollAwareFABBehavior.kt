package com.intelliavant.mytimetracker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Hide FAB when recycler view scrolls
// https://gist.github.com/LloydBlv/533b37c3d4fa47993b45013d512c11f0
class ScrollAwareFABBehavior(context: Context, attrs: AttributeSet): FloatingActionButton.Behavior(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {

        // Ensure we react to vertical scrolling
        return (target.id == R.id.work_list_recycler_view && axes == View.SCROLL_AXIS_VERTICAL) || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide(object: FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    fab?.visibility = View.INVISIBLE
                }

            });
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            child.show();
        }
    }
}
