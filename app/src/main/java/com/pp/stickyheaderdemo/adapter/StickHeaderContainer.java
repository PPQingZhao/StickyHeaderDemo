package com.pp.stickyheaderdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.widget.FrameLayout;

/**
 * @author qing
 * 粘性头部
 */
public class StickHeaderContainer extends FrameLayout {
    //  key:当前header  value: 当前header的前一个header
    private final SparseIntArray headerPositionMap;

    public StickHeaderContainer(@NonNull Context context) {
        this(context, null);
    }

    public StickHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        headerPositionMap = new SparseIntArray();
    }

    public void putPreviousValue(int previousPosition) {
        // key:当前header  value: 当前header的前一个header
        headerPositionMap.put(getHeaderPosition(), previousPosition);
    }

    public void notifyPreviousMap(int previousPosition) {
        SparseIntArray cloneMap = headerPositionMap.clone();
        for (int i = 0; i < cloneMap.size(); i++) {
            int keyPrevious = headerPositionMap.keyAt(i);
            if (keyPrevious >= previousPosition) {
                headerPositionMap.delete(keyPrevious);
            }
        }
    }


    /**
     * 记录当前headerView对应数据源中的位置
     */
    private int headerPosition = -1;

    public int getHeaderPosition() {
        return headerPosition;
    }

    public void setHeaderPosition(int headerPosition) {
        this.headerPosition = headerPosition;
    }

    /**
     * 记录 old  headerPosition
     */
    private int oldHeaderPosition = headerPosition;

    public int getOldHeaderPosition() {
        return oldHeaderPosition;
    }

    public void setOldHeaderPosition(int oldHeaderPosition) {
        this.oldHeaderPosition = oldHeaderPosition;
    }

    public int getPreviousHeaderPosition(int headerPosition) {
        return headerPositionMap.get(headerPosition);
    }
}
