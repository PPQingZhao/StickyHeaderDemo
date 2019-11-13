package com.pp.stickyheaderdemo.entity;

import com.pp.stickyheaderdemo.R;
import com.pp.stickyheaderdemo.mutilitem.MultiItemEntity;
import com.pp.stickyheaderdemo.mutilitem.StickyHeader;
import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

/**
 * @author qing
 */
public class City extends StickyHeaderItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return R.layout.rv_item_city;
    }

    @Override
    public int getHeaderType() {
        return -1;
    }

    @Override
    public boolean isHeader() {
        return false;
    }
}
