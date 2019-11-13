package com.pp.stickyheaderdemo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.pp.stickyheaderdemo.mutilitem.StickyHeader;

/**
 * @author qing
 * 滑动监听
 * 用于实现粘性头部
 */
public class StickyHeaderScrollerListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "StickyHeaderScroller";
    private final StickHeaderContainer mHeaderContainer;
    private final SparseArray<RecyclerView.ViewHolder> mHolderMap;

    public StickyHeaderScrollerListener(StickHeaderContainer container) {
        if (null == container) {
            throw new RuntimeException("StickHeaderContainer must not be null.");
        }
        this.mHeaderContainer = container;
        mHolderMap = new SparseArray<>();
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (null == mHeaderAdapter) {
            throw new RuntimeException("StickyHeaderAdapter must not be null.");
        }

        // 判断第一个 view 是否是 headerView
        int firstPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
        StickyHeader headerItem = mHeaderAdapter.getHeaderItem(firstPosition);
        if (null != headerItem && headerItem.isHeader()) {
            scrollStickyHeader(recyclerView, recyclerView.getChildAt(0), dy > 0);
        } else {
            View viewUnder = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, mHeaderContainer.getMeasuredHeight());
            if (null != viewUnder) {
                scrollStickyHeader(recyclerView, viewUnder, dy > 0);
            }
        }
    }

    /**
     * 滚动粘性头部
     *
     * @param positiveDirection 是否是正方向(竖直滚动,列表向上滚动为正方向)
     */
    private void scrollStickyHeader(RecyclerView parent, View child, boolean positiveDirection) {

        // 获取view 在列表中的位置
        int position = parent.getChildAdapterPosition(child);
        StickyHeader headerItem = mHeaderAdapter.getHeaderItem(position);
        if (null != headerItem && headerItem.isHeader()) {
            int top = child.getTop();

            // 到达recyclerview 顶部
            if (0 == position && 0 == top) {
                // 获取当前 header的 前一个header
                int previousHeaderPosition = mHeaderContainer.getPreviousHeaderPosition(mHeaderContainer.getHeaderPosition());
                if (0 == previousHeaderPosition) {
                    // 更新当前header 记录
                    mHeaderContainer.setHeaderPosition(position);

                    RecyclerView.ViewHolder holder = getHolder(mHeaderContainer.getHeaderPosition());
                    if (null != holder && null == holder.itemView.getParent()) {
                        // 更新headerview
                        addHeaderView(holder);
                    }
                }
                // 列表向上滚动
            } else if (positiveDirection && top < 0) {
                if (position != mHeaderContainer.getHeaderPosition()) {

                    // 更新old header记录
                    mHeaderContainer.setOldHeaderPosition(mHeaderContainer.getHeaderPosition());

                    // 更新当前header记录
                    mHeaderContainer.setHeaderPosition(position);

                    // 更新map , key:当前header  value: 当前header的前一个header
                    mHeaderContainer.notifyPreviousHeaderMap(mHeaderContainer.getOldHeaderPosition());

                    RecyclerView.ViewHolder holder = getHolder(mHeaderContainer.getHeaderPosition());
                    if (null != holder && null == holder.itemView.getParent()) {
                        // 更新headerview
                        addHeaderView(holder);
                    }
                }
                // 列表向下滚动
            } else if (!positiveDirection && top >= 0) {
                // 获取当前position header的 前一个header
                int previousHeaderPosition = mHeaderContainer.getPreviousHeaderPosition(position);
                if (-1 != previousHeaderPosition
                        && previousHeaderPosition != mHeaderContainer.getHeaderPosition()) {

                    // 更新old header 记录
                    mHeaderContainer.setOldHeaderPosition(mHeaderContainer.getHeaderPosition());
                    // 更新当前header 记录
                    mHeaderContainer.setHeaderPosition(previousHeaderPosition);

                    RecyclerView.ViewHolder holder = getHolder(mHeaderContainer.getHeaderPosition());
                    if (null != holder && null == holder.itemView.getParent()) {
                        // 更新headerview
                        addHeaderView(holder);
                    }
                }
            }

            // 主要解决:mHeaderContainer 未完全初始化就已经添加headerView，此时headerView是没有宽高信息的,需要重绘
            if (View.VISIBLE == mHeaderContainer.getVisibility()
                    && (0 == mHeaderContainer.getHeight()
                    || 0 == mHeaderContainer.getWidth())) {
                mHeaderContainer.requestLayout();
            }

            int measuredHeight = mHeaderContainer.getMeasuredHeight();
            int translationY = top - measuredHeight;
            if (positiveDirection && 0 <= top && top <= measuredHeight) {
                mHeaderContainer.setTranslationY(translationY);
            } else if (!positiveDirection && 0 < top && top < measuredHeight) {
                mHeaderContainer.setTranslationY(translationY);
            } else {
                mHeaderContainer.setTranslationY(0);
            }
        } else {
            mHeaderContainer.setTranslationY(0);
        }
    }

    private RecyclerView.ViewHolder getHolder(int headerPosition) {

        StickyHeader headerItem = mHeaderAdapter.getHeaderItem(headerPosition);
        if (null == headerItem || !headerItem.isHeader()) {
            return null;
        }

        // 获取 header type
        int headerType = mHeaderAdapter.getHeaderType(headerPosition);

        // 从缓存中获取 header 对应holder
        RecyclerView.ViewHolder holder = mHolderMap.get(headerType);
        if (null == holder) {
            // 创建holder
            holder = mHeaderAdapter.createHeaderViewHolder(mHeaderContainer, headerType);
            // 缓存 holder
            mHolderMap.put(headerType, holder);
        }

        // bind holder
        mHeaderAdapter.bindHeaderViewHolder(holder, mHeaderContainer.getHeaderPosition());

        return holder;
    }

    private void addHeaderView(RecyclerView.ViewHolder holder) {
        if (null == holder || null != holder.itemView.getParent()) {
            return;
        }

        int oldHeaderType = mHeaderAdapter.getHeaderType(mHeaderContainer.getOldHeaderPosition());

        // 删除上一个headerView
        RecyclerView.ViewHolder oldHolder = mHolderMap.get(oldHeaderType);
        if (null != oldHolder) {
            mHeaderContainer.removeView(oldHolder.itemView);
        }

        mHeaderContainer.addView(holder.itemView);

        Log.i(TAG, "add header View");
    }

    private BaseHeaderAdapter mHeaderAdapter;

    public <Adapter extends BaseHeaderAdapter> void setHeaderAdapter(Adapter adapter) {
        this.mHeaderAdapter = adapter;
    }

    public interface BaseHeaderAdapter<VH extends RecyclerView.ViewHolder> {

        /**
         * 获取header type
         *
         * @param position
         * @return
         */
        int getHeaderType(int position);

        /**
         * 创建header holder
         *
         * @param parent
         * @param headerType
         * @return
         */
        VH createHeaderViewHolder(ViewGroup parent, int headerType);

        /**
         * 绑定header view
         *
         * @param holder
         * @param position
         */
        void bindHeaderViewHolder(VH holder, int position);

        /**
         * 获取header条目
         *
         * @param position
         * @param <T>
         * @return
         */
        <T extends StickyHeader> T getHeaderItem(int position);
    }
}
