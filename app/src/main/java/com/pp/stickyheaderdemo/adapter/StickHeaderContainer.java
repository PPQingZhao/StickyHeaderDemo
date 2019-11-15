package com.pp.stickyheaderdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;

/**
 * @author qing
 * 粘性头部
 */
public class StickHeaderContainer extends FrameLayout {
    private final HeaderView mHeaderView;

    public StickHeaderContainer(@NonNull Context context) {
        this(context, null);
    }

    public StickHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickHeaderContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderView = new HeaderView(getContext());
        mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void putPreviousValue(int previousPosition) {
        // key:当前header  value: 当前header的前一个header
        mHeaderView.putPreviousValue(previousPosition);
    }

    public void notifyPreviousRemove(int index) {
        mHeaderView.notifyPreviousRemove(index);
    }

    public void notifyHeaderPoisitionRemove(int index) {
        mHeaderView.notifyHeaderPoisitionRemove(index);
    }

    public void notifyPreviousInsert(int index) {
        mHeaderView.notifyPreviousInsert(index);
    }

    public void addHeader(View header) {
        if (null == mHeaderView.getParent()) {
            addView(mHeaderView);
        }
        mHeaderView.addView(header);
    }

    public void removeHeader() {
        mHeaderView.removeAllViews();
    }

    public int getHeaderPosition() {
        return mHeaderView.getHeaderPosition();
    }

    public void setHeaderPosition(int headerPosition) {
        mHeaderView.setHeaderPosition(headerPosition);
    }

    public int getOldHeaderPosition() {
        return mHeaderView.getOldHeaderPosition();
    }

    public void setOldHeaderPosition(int oldHeaderPosition) {
        mHeaderView.setOldHeaderPosition(oldHeaderPosition);
    }

    public Integer getPreviousHeaderPosition(int headerPosition) {
        return mHeaderView.getPreviousHeaderPosition(headerPosition);
    }

    static class HeaderView extends FrameLayout {
        //  key:当前header  value: 当前header的前一个header
        private final SparseArray<Integer> headerPositionMap;

        public HeaderView(Context context) {
            super(context);
            headerPositionMap = new SparseArray<>();
        }

        public void putPreviousValue(int previousPosition) {
            // key:当前header  value: 当前header的前一个header
            headerPositionMap.put(getHeaderPosition(), previousPosition);
        }

        public Integer getPreviousHeaderPosition(int headerPosition) {
            return headerPositionMap.get(headerPosition);
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

        public void notifyHeaderPoisitionRemove(int index) {
            if (getHeaderPosition() == index) {
                setHeaderPosition(headerPositionMap.get(getHeaderPosition()));
            } else if (getHeaderPosition() > index) {
                setHeaderPosition(getHeaderPosition() - 1);
            }
        }

        public void notifyPreviousRemove(int index) {
            SparseArray<Integer> cloneMap = headerPositionMap.clone();
            headerPositionMap.clear();

            for (int i = 0; i < cloneMap.size(); i++) {
                int keyPrevious = cloneMap.keyAt(i);
                Integer value = cloneMap.valueAt(i);
                if (keyPrevious == index) {

                    int valueIndex = cloneMap.indexOfValue(keyPrevious);
                    if (-1 != valueIndex) {

                        int keyValue = cloneMap.keyAt(valueIndex);
                        headerPositionMap.put(keyValue, value);
                        cloneMap.put(keyValue, value);
                    }
                } else if (value == index) {

                    Integer deletedValue = cloneMap.get(value);
                    headerPositionMap.put(keyPrevious - 1, deletedValue);
                } else if (value < index && keyPrevious < index) {
                    headerPositionMap.put(keyPrevious, value);
                } else if (value > index || keyPrevious > index) {
                    if (value == 0) {
                        headerPositionMap.put(keyPrevious - 1, 0);
                    } else {
                        headerPositionMap.put(keyPrevious - 1, value - 1);
                    }
                }
            }
        }

        public void notifyPreviousInsert(int index) {

        }
    }
}
