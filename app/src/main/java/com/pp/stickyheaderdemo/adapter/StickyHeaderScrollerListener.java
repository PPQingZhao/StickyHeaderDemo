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

        if (dy > 0) {
            int headerHeight = mHeaderContainer.getMeasuredHeight();
            // header top边下的view
            View viewTop = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, 1);
            // 获取view 在列表中的位置
            int positionTop = recyclerView.getChildAdapterPosition(viewTop);
            StickyHeader topHeaderItem = mHeaderAdapter.getHeaderItem(positionTop);
            boolean isHeaderTop = null != topHeaderItem && topHeaderItem.isHeader();

            // header交换时机: viewTop是header 并且 viewTop.getTop() <= 0
            if (isHeaderTop && viewTop.getTop() <= 0) {
                if (positionTop != mHeaderContainer.getHeaderPosition()) {

                    // 更新old header记录
                    mHeaderContainer.setOldHeaderPosition(mHeaderContainer.getHeaderPosition());

                    // 更新当前header记录
                    mHeaderContainer.setHeaderPosition(positionTop);

                    // 更新map , key:当前header  value: 当前header的前一个header
                    mHeaderContainer.putPreviousValue(mHeaderContainer.getOldHeaderPosition());

                    RecyclerView.ViewHolder holder = getHolder(mHeaderContainer.getHeaderPosition());
                    if (null != holder && null == holder.itemView.getParent()) {
                        // 更新headerview
                        addHeaderView(holder);
                    }
                }
            }

            // header bottom边下的view
            View viewBottom = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, headerHeight);
            // 获取view 在列表中的位置
            int positionBottom = recyclerView.getChildAdapterPosition(viewBottom);
            StickyHeader bottomHeaderItem = mHeaderAdapter.getHeaderItem(positionBottom);
            boolean isHeaderBottom = null != bottomHeaderItem && bottomHeaderItem.isHeader();
            int viewBottomTop = null == viewBottom ? 0 : viewBottom.getTop();
            if (isHeaderBottom && 0 <= viewBottomTop && viewBottomTop <= headerHeight) {
                mHeaderContainer.setTranslationY(viewBottomTop - headerHeight);
            } else {
                mHeaderContainer.setTranslationY(0);
            }

        } else if (dy <= 0) {
            int headerHeight = mHeaderContainer.getMeasuredHeight();
            View viewUnder = null;
            // dy 等于0时,取mHeaderContainer top边下的item,通常是 删除/添加 itme时触发
            if (0 == dy) {
                viewUnder = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, 1);
            } else {
                viewUnder = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, headerHeight);
            }
            // 获取view 在列表中的位置
            int position = recyclerView.getChildAdapterPosition(viewUnder);
            StickyHeader headerItem = mHeaderAdapter.getHeaderItem(position);
            boolean isHeader = null != headerItem && headerItem.isHeader();

            // header交换时机: viewUnder 是header 并且 viewUnder.getTop() > 0
            if (isHeader && viewUnder.getTop() > 0) {
                // 获取当前position header的 前一个header
                Integer previousHeaderPosition = mHeaderContainer.getPreviousHeaderPosition(position);
                if (null != previousHeaderPosition
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
            // header bottom边下的view
            View viewBottom = recyclerView.findChildViewUnder(mHeaderContainer.getMeasuredWidth() * 0.5f, headerHeight);
            // 获取view 在列表中的位置
            int positionBottom = recyclerView.getChildAdapterPosition(viewBottom);
            StickyHeader bottomHeaderItem = mHeaderAdapter.getHeaderItem(positionBottom);
            boolean isHeaderBottom = null != bottomHeaderItem && bottomHeaderItem.isHeader();
            int viewBottomTop = null == viewBottom ? 0 : viewBottom.getTop();
            if (isHeaderBottom && 0 <= viewBottomTop && viewBottomTop <= headerHeight) {
                mHeaderContainer.setTranslationY(viewBottomTop - headerHeight);
            } else {
                mHeaderContainer.setTranslationY(0);
            }
        }
        // 主要解决:mHeaderContainer 未完全初始化就已经添加headerView，此时headerView是没有宽高信息的,需要重绘
        if (View.VISIBLE == mHeaderContainer.getVisibility()
                && (0 == mHeaderContainer.getHeight()
                || 0 == mHeaderContainer.getWidth())) {
            mHeaderContainer.requestLayout();
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

        mHeaderContainer.removeHeader();
        mHeaderContainer.addHeader(holder.itemView);

        Log.i(TAG, "add header View");
    }

    private BaseHeaderAdapter mHeaderAdapter;

    public <Adapter extends BaseHeaderAdapter> void setHeaderAdapter(Adapter adapter) {
        this.mHeaderAdapter = adapter;
    }

    /**
     * 删除条目,刷新header
     *
     * @param index
     */
    public void notifyPreviousRemove(int index) {
        // 是否是当前header
        boolean curHeader = mHeaderContainer.getHeaderPosition() == index;

        // 刷新 当前headerposition
        mHeaderContainer.notifyHeaderPoisitionRemove(index);
        // 刷新记录的所有header
        mHeaderContainer.notifyPreviousRemove(index);

        // 删除的条目是当前header,需要更新header 条目
        if (curHeader) {
            RecyclerView.ViewHolder holder = getHolder(mHeaderContainer.getHeaderPosition());
            if (null != holder && null == holder.itemView.getParent()) {
                addHeaderView(holder);
            }
        }
    }

    /**
     * 添加条目,刷新header
     *
     * @param index
     */
    public void notifyPreviousInsert(int index) {
        StickyHeader headerItem = mHeaderAdapter.getHeaderItem(index);
        // 是否插入header
        boolean isInsertHeader = null != headerItem && headerItem.isHeader();
        // 刷新 当前headerposition
        mHeaderContainer.notifyHeaderPoisitionInsert(index,isInsertHeader);
        // 刷新记录的所有header
        mHeaderContainer.notifyPreviousInsert(index,isInsertHeader);
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
