package com.pp.stickyheaderdemo.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pp.stickyheaderdemo.mutilitem.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qing
 */
public class BaseMultiItemAdapter<T extends MultiItemEntity> extends RecyclerView.Adapter<BaseMultiItemAdapter.BaseViewHolder>  {

    protected final List<T> mDataList = new ArrayList<>();
    protected final LayoutDrawer mLayoutProvider;
    private LayoutInflater mLayoutInflater;

    public BaseMultiItemAdapter(LayoutDrawer layoutProvider, List<T> datas) {
        if (null == layoutProvider) {
            throw new RuntimeException("layoutProvider must not be null.");
        }
        this.mLayoutProvider = layoutProvider;
        setNewData(datas);
    }

    public void setNewData(List<T> datas) {
        mDataList.clear();
        mDataList.addAll(null != datas ? datas : new ArrayList<T>());
    }

    public List<T> getDataList() {
        return mDataList;
    }

    protected LayoutInflater getLayoutInflater(Context context) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(context.getApplicationContext());
        }
        return mLayoutInflater;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = getLayoutInflater(viewGroup.getContext()).inflate(mLayoutProvider.getLayoutRes(viewType), viewGroup, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
        mLayoutProvider.onBindView(baseViewHolder.itemView,  getItem(position), getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getItemType();
    }

    public T getItem(int index) {
        try {
            return mDataList.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 条目布局
     */
    public interface LayoutDrawer {
        /**
         * 条目布局
         *
         * @return
         */
        @LayoutRes
        int getLayoutRes(int viewType);

        /**
         * 条目绑定
         *
         * @param itemView
         * @param item
         * @param viewType
         * @param <T>
         */
        <T extends MultiItemEntity> void onBindView(View itemView, T item, int viewType);
    }
}
