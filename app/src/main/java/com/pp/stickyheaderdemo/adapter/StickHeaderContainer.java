package com.pp.stickyheaderdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * @author qing
 * 粘性头部
 */
public class StickHeaderContainer extends ViewGroup {
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
        mHeaderView.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 测量设置宽高信息
     * <p>
     * 这里我们希望StickHeaderContainer只需要包裹住HeaderView
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalArgumentException("StickHeaderContainer only allows one child.");
        }

        if (childCount == 0) {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
            return;
        }

        // 获取child
        View child = getChildAt(0);
        //  测量单个child(考虑外边距)
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

        // 获取child 的布局参数(此时包含外边距)
        MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

        // 期望的宽高 (取child最大宽高)
        int desireWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
        int desireHeight = child.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

        // 考虑本身内边距
        desireWidth += getPaddingLeft() + getPaddingRight();
        desireHeight += getPaddingTop() + getPaddingBottom();

        // 比较建议最小值和期望值,并取最大值
        desireWidth = Math.max(desireWidth, getSuggestedMinimumWidth());
        desireHeight = Math.max(desireHeight, getSuggestedMinimumHeight());

        // 设置最终测量值 (view.getMeasureWidth()和view.getMeasureHeight()的值就是从这里确定的)
        setMeasuredDimension(resolveSize(desireWidth, widthMeasureSpec), resolveSize(desireHeight, heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        View child = getChildAt(0);
        MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

        int cLeft = getPaddingLeft() + layoutParams.leftMargin;
        int cTop = getPaddingTop() + layoutParams.topMargin;
        int cRight = cLeft + child.getMeasuredWidth();
        int cBottom = cTop + child.getMeasuredHeight();
        /* 设置child在parent中的位置
            分别保存在child的  mLeft,mTop,mBottom,mRight中，view.getWidth()的值等于 mRight - mLeft，
            所以在执行完view.layout()后,view.getWidth()和view.getHeight()；才有值
            */
        child.layout(cLeft, cTop, cRight, cBottom);

    }

    /**
     * 重写构造默认布局参数方法
     * 因为在onMeasure()中使用了 measureChildWithMargins()，这要求view的布局参数是MarginLayoutParams
     *
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    public void notifyPreviousInsert(int index, boolean isInsertHeader) {
        mHeaderView.notifyPreviousInsert(index, isInsertHeader);
    }

    public void notifyHeaderPoisitionInsert(int index, boolean isInsertHeader) {
        mHeaderView.notifyHeaderPoisitionInsert(index, isInsertHeader);
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

    public void setHeaderTranslationY(float translationY) {
        mHeaderView.setTranslationY(translationY);
    }

    public void setHeaderTranslationX(float translationX) {
        mHeaderView.setTranslationX(translationX);
    }

    static class HeaderView extends FrameLayout {
        //  key:当前header  value: 当前header的前一个header
        private final SparseArray<Integer> headerPositionMap;

        private HeaderView(Context context) {
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
            if (getHeaderPosition() < index) {
                // do nothing
            } else if (getHeaderPosition() == index) {
                setHeaderPosition(headerPositionMap.get(getHeaderPosition()));
            } else if (getHeaderPosition() > index) {
                setHeaderPosition(getHeaderPosition() - 1);
            }
        }

        public void notifyPreviousRemove(int index) {
            SparseArray<Integer> cloneMap = headerPositionMap.clone();
            headerPositionMap.clear();

            for (int i = 0; i < cloneMap.size(); i++) {
                int key = cloneMap.keyAt(i);
                Integer previousKey = cloneMap.valueAt(i);

                // index 小于 key和previousKey
                if (index < previousKey && index < key) {
                    if (previousKey == 0) {
                        headerPositionMap.put(key - 1, 0);
                    } else {
                        headerPositionMap.put(key - 1, previousKey - 1);
                    }
                    // index < previousKey
                } else if (index == previousKey) {
                    Integer deletedValue = cloneMap.get(previousKey);
                    headerPositionMap.put(key - 1, deletedValue);
                    // previousKey < index  < key
                } else if (previousKey < index && index < key) {
                    headerPositionMap.put(key - 1, previousKey);
                } else if (index == key) {
                    int valueIndex = cloneMap.indexOfValue(key);
                    if (-1 != valueIndex) {

                        int keyValue = cloneMap.keyAt(valueIndex);
                        headerPositionMap.put(keyValue, previousKey);
                        cloneMap.put(keyValue, previousKey);
                    }
                } else if (index > key) {
                    // do nothing
                }
            }
        }

        public void notifyPreviousInsert(int index, boolean isInsertHeader) {
            SparseArray<Integer> cloneMap = headerPositionMap.clone();
            headerPositionMap.clear();

            for (int i = 0; i < cloneMap.size(); i++) {
                int key = cloneMap.keyAt(i);
                Integer previousKey = cloneMap.valueAt(i);
                if (index > key && index > previousKey) {
                    headerPositionMap.put(key, previousKey);
                    if (isInsertHeader) {
                        headerPositionMap.put(index, key);
                    }
                } else if (previousKey < index && index < key) {
                    if (isInsertHeader) {
                        headerPositionMap.put(index, previousKey);
                        headerPositionMap.put(key + 1, index);
                    } else {
                        headerPositionMap.put(key + 1, previousKey);
                    }
                } else if (index < previousKey && index < key) {
                    headerPositionMap.put(key + 1, previousKey + 1);
                    if (isInsertHeader) {
                        // 获取 previousKey的前一个header
                        Integer value = cloneMap.get(previousKey);
                        headerPositionMap.put(index, value);
                        headerPositionMap.put(previousKey + 1, index);
                    }
                }
            }
        }

        /**
         * 插入item刷新headerposition
         *
         * @param index
         * @param isInsertHeader
         */
        public void notifyHeaderPoisitionInsert(int index, boolean isInsertHeader) {
            if (index > getHeaderPosition()) {
                // do nothing
            } else if (index <= getHeaderPosition()) {
                setHeaderPosition(getHeaderPosition() + 1);
            }
        }
    }
}
