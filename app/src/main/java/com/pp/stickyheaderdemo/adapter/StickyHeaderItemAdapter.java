package com.pp.stickyheaderdemo.adapter;

import android.view.ViewGroup;

import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

import java.util.List;

/**
 * @author qing
 */
public class StickyHeaderItemAdapter<T extends StickyHeaderItem> extends BaseMultiItemAdapter<T> implements StickyHeaderScrollerListener.BaseHeaderAdapter<BaseMultiItemAdapter.BaseViewHolder> {


    public StickyHeaderItemAdapter(LayoutDrawer layoutProvider, List<T> datas) {
        super(layoutProvider,datas);
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getItemType();
    }

    @Override
    public int getHeaderType(int position) {
        try {
            return getItem(position).getHeaderType();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public BaseViewHolder createHeaderViewHolder(ViewGroup parent, int headerType) {
        return createViewHolder(parent, headerType);
    }

    @Override
    public void bindHeaderViewHolder(BaseViewHolder holder, int position) {
        bindViewHolder(holder, position);
    }

    @Override
    public T getHeaderItem(int position) {
        return getItem(position);
    }
}
