package com.pp.stickyheaderdemo.entity;

import com.pp.stickyheaderdemo.R;
import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

/**
 * @author qing
 */
public class Province extends StickyHeaderItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return R.layout.rv_item_province;
    }

    @Override
    public int getHeaderType() {
        return R.layout.rv_item_province;
    }

    @Override
    public boolean isHeader() {
        return true;
    }
}
