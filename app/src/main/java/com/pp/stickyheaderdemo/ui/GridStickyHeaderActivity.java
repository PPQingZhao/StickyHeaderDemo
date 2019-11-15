package com.pp.stickyheaderdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pp.stickyheaderdemo.R;
import com.pp.stickyheaderdemo.adapter.BaseMultiItemAdapter;
import com.pp.stickyheaderdemo.adapter.StickHeaderContainer;
import com.pp.stickyheaderdemo.adapter.StickyHeaderScrollerListener;
import com.pp.stickyheaderdemo.entity.City;
import com.pp.stickyheaderdemo.entity.Municipality;
import com.pp.stickyheaderdemo.entity.Province;
import com.pp.stickyheaderdemo.mutilitem.MultiItemEntity;
import com.pp.stickyheaderdemo.mutilitem.StickyHeader;
import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qing
 */
public class GridStickyHeaderActivity extends AppCompatActivity {

    private static final String TAG = "GridStickyHeader";
    private RecyclerView mRecyclerView;
    private BaseMultiItemAdapter<StickyHeaderItem> mAdapter;
    private final List<StickyHeaderItem> dataList = new ArrayList<>();
    private StickHeaderContainer mHeaderContainer;
    private GridLayoutManager mGridLayoutManager;
    private HeaderAdapter mHeaderAdapter;
    private StickyHeaderScrollerListener mHeaderScrollerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linearheader);
        initData();
        initView();
        setupRecyclerView();
    }

    private void initView() {
        mHeaderContainer = findViewById(R.id.header_container);
        mHeaderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int headerPosition = mHeaderContainer.getHeaderPosition();
                StickyHeaderItem item = mAdapter.getItem(headerPosition);
                String content = "";
                if (item instanceof Province) {
                    content = ((Province) item).getName();
                } else if (item instanceof Municipality) {
                    content = ((Municipality) item).getName();
                }
                Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 250; i++) {
            if (i % 15 == 0) {
                Municipality municipality = new Municipality();
                municipality.setName("Municipality " + i);
                dataList.add(municipality);
            } else if (i % 12 == 0) {
                Province province = new Province();
                province.setName("Province " + i);
                dataList.add(province);
            } else {
                City city = new City();
                city.setName(" city " + i);
                dataList.add(city);
            }
        }

//        for (int i = 0; i < 30; i++) {
//            if (i % 3 == 0) {
//                Municipality municipality = new Municipality();
//                municipality.setName("Municipality " + i);
//                dataList.add(municipality);
//                for (int j = 0; j < 4; j++) {
//                    City city = new City();
//                    city.setName(municipality.getName() + " city " + j);
//                    dataList.add(city);
//                }
//            } else {
//                Province province = new Province();
//                province.setName("Province " + i);
//                dataList.add(province);
//                for (int j = 0; j < 4 * i && j < 20; j++) {
//                    City city = new City();
//                    city.setName(province.getName() + " city " + j);
//                    dataList.add(city);
//                }
//            }
//        }
    }

    private void setupRecyclerView() {

        mRecyclerView = findViewById(R.id.recycler);

        // 设置布局管理器
        mRecyclerView.setLayoutManager(getGridLayoutManager());

        //设置设配器
        mRecyclerView.setAdapter(getAdapter());

        // 构建滑动监听器(用于实现粘性头部)
        mHeaderScrollerListener = new StickyHeaderScrollerListener(mHeaderContainer);
        mHeaderScrollerListener.setHeaderAdapter(getHeaderAdapter());
        mRecyclerView.addOnScrollListener(mHeaderScrollerListener);
    }

    private StickyHeaderScrollerListener.BaseHeaderAdapter getHeaderAdapter() {
        if (null == mHeaderAdapter) {
            mHeaderAdapter = new HeaderAdapter();
        }
        return mHeaderAdapter;
    }

    private RecyclerView.LayoutManager getGridLayoutManager() {
        int spanCount = 3;
        if (null == mGridLayoutManager) {
            mGridLayoutManager = new GridLayoutManager(getApplicationContext(), spanCount, GridLayoutManager.VERTICAL, false);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    return mAdapter.getItem(i).isHeader() ? 3 : 1;
                }
            });
        }
        return mGridLayoutManager;
    }

    private RecyclerView.Adapter getAdapter() {
        if (null == mAdapter) {

            // 构建设配器
            mAdapter = new BaseMultiItemAdapter<StickyHeaderItem>(new BaseMultiItemAdapter.LayoutDrawer() {
                @Override
                public int getLayoutRes(int viewType) {
                    switch (viewType) {
                        case R.layout.rv_item_province:
                            return R.layout.rv_item_province;
                        case R.layout.rv_item_city:
                            return R.layout.rv_item_city;
                        case R.layout.rv_item_municipality:
                            return R.layout.rv_item_municipality;
                        default:
                            break;
                    }
                    return 0;
                }

                @Override
                public <T extends MultiItemEntity> void onBindView(View itemView, final T item, int viewType) {
                    switch (viewType) {
                        case R.layout.rv_item_province:
                            TextView tv_province = itemView.findViewById(R.id.tv_province);
                            final Province province = (Province) item;
                            tv_province.setText(province.getName());
                            tv_province.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    StickyHeaderItem remove = mAdapter.getDataList().remove(1);
                                    mAdapter.notifyItemRemoved(1);
                                    mHeaderScrollerListener.notifyPreviousRemove(1);
//                                    Toast.makeText(v.getContext(), "deleted " + ((City) remove).getName(), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(v.getContext(), province.getName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case R.layout.rv_item_municipality:
                            TextView tv_municipality = itemView.findViewById(R.id.tv_municipality);
                            final Municipality municipality = (Municipality) item;
                            tv_municipality.setText(municipality.getName());
                            tv_municipality.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(v.getContext(), municipality.getName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case R.layout.rv_item_city:
                            TextView tv_city = itemView.findViewById(R.id.tv_city);
                            final City city = (City) item;
                            tv_city.setText(city.getName());
                            tv_city.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = mAdapter.getDataList().indexOf(item);
                                    if (mAdapter.getDataList().remove(item)) {
                                        mAdapter.notifyItemRemoved(index);
                                        mHeaderContainer.notifyPreviousRemove(index);
                                        Toast.makeText(v.getContext(), "deleted " + city.getName(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }, dataList);

        }
        return mAdapter;
    }

    class HeaderAdapter implements StickyHeaderScrollerListener.BaseHeaderAdapter<BaseMultiItemAdapter.BaseViewHolder> {


        @Override
        public int getHeaderType(int position) {
            StickyHeaderItem item = mAdapter.getItem(position);
            return null == item ? -1 : item.getHeaderType();
        }

        @Override
        public BaseMultiItemAdapter.BaseViewHolder createHeaderViewHolder(ViewGroup parent, int headerType) {
            return mAdapter.createViewHolder(parent, headerType);
        }

        @Override
        public void bindHeaderViewHolder(BaseMultiItemAdapter.BaseViewHolder holder, int position) {
            final StickyHeader headerItem = getHeaderItem(position);
            switch (getHeaderType(position)) {
                case R.layout.rv_item_province:
                    TextView tv_province = holder.itemView.findViewById(R.id.tv_province);
                    tv_province.setText(((Province) headerItem).getName());
                    break;
                case R.layout.rv_item_municipality:
                    TextView tv_municipality = holder.itemView.findViewById(R.id.tv_municipality);
                    tv_municipality.setText(((Municipality) headerItem).getName());
                    break;
                case R.layout.rv_item_city:
                    TextView tv_city = holder.itemView.findViewById(R.id.tv_city);
                    final String name = ((City) headerItem).getName();
                    tv_city.setText(name);
                    tv_city.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = mAdapter.getDataList().indexOf(headerItem);
                            if (mAdapter.getDataList().remove(headerItem)) {
                                mAdapter.notifyItemRemoved(index);
                                Toast.makeText(v.getContext(), "deleted " + name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        @Override
        public <T extends StickyHeader> T getHeaderItem(int position) {
            return (T) mAdapter.getItem(position);
        }
    }

}
