package com.pp.stickyheaderdemo.entity;

import com.pp.stickyheaderdemo.R;
import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

/**
 * @author qing
 */
public class Municipality extends StickyHeaderItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int getItemType() {
        return R.layout.rv_item_municipality;
    }

    @Override
    public int getHeaderType() {
        return R.layout.rv_item_municipality;
    }

    @Override
    public boolean isHeader() {
        return true;
    }
}
