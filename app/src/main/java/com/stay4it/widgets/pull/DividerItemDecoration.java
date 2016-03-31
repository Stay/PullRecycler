package com.stay4it.widgets.pull;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView中实现divider功能
 * 只支持LinearLayoutManager
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable dividerDrawable;
    private int orientation = LinearLayoutManager.VERTICAL;

    public DividerItemDecoration(Drawable divider) {
        dividerDrawable = divider;
    }

    public DividerItemDecoration(Context context, int resId) {
        dividerDrawable = context.getResources().getDrawable(resId);
    }

    public DividerItemDecoration(Context context, int resId, int orientation) {
        dividerDrawable = context.getResources().getDrawable(resId);
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (dividerDrawable == null) {
            return;
        }

        //如果是第一个item，不需要divider，所以直接return
        if (parent.getChildLayoutPosition(view) < 1) {
            return;
        }

        //相当于给itemView设置margin，给divider预留空间
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.top = dividerDrawable.getIntrinsicHeight();
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.left = dividerDrawable.getIntrinsicWidth();
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (dividerDrawable == null) {
            return;
        }

        int childCount = parent.getChildCount();
        if (orientation == LinearLayoutManager.VERTICAL) {
            int right = parent.getWidth();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = parent.getPaddingLeft() + child.getPaddingLeft();
                int bottom = child.getTop() - params.topMargin;
                int top = bottom - dividerDrawable.getIntrinsicHeight();
                dividerDrawable.setBounds(left, top, right, bottom);
                dividerDrawable.draw(c);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = parent.getPaddingTop() + child.getPaddingTop();
                int bottom = child.getHeight() + parent.getPaddingTop();
                int right = child.getLeft() - params.leftMargin;
                int left = right - dividerDrawable.getIntrinsicWidth();
                dividerDrawable.setBounds(left, top, right, bottom);
                dividerDrawable.draw(c);
            }
        }
    }


}
