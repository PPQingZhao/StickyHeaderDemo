package com.pp.stickyheaderdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pp.stickyheaderdemo.R;
import com.pp.stickyheaderdemo.adapter.BaseMultiItemAdapter;
import com.pp.stickyheaderdemo.adapter.StickHeaderContainer;
import com.pp.stickyheaderdemo.adapter.StickyHeaderItemAdapter;
import com.pp.stickyheaderdemo.adapter.StickyHeaderScrollerListener;
import com.pp.stickyheaderdemo.entity.City;
import com.pp.stickyheaderdemo.entity.Municipality;
import com.pp.stickyheaderdemo.entity.Province;
import com.pp.stickyheaderdemo.mutilitem.MultiItemEntity;
import com.pp.stickyheaderdemo.mutilitem.StickyHeaderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qing
 */
public class LinearStickyHeaderActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private StickyHeaderItemAdapter<StickyHeaderItem> mAdapter;
    private final List<StickyHeaderItem> dataList = new ArrayList<>();
    private StickHeaderContainer mHeaderContainer;

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

        for (int i = 0; i < 30; i++) {
            if (i % 3 == 0) {
                Municipality municipality = new Municipality();
                municipality.setName("Municipality " + i);
                dataList.add(municipality);
                for (int j = 0; j < 4; j++) {
                    City city = new City();
                    city.setName(municipality.getName() + " city " + j);
                    dataList.add(city);
                }
            } else {
                Province province = new Province();
                province.setName("Province " + i);
                dataList.add(province);
                for (int j = 0; j < 4; j++) {
                    City city = new City();
                    city.setName(province.getName() + " city " + j);
                    dataList.add(city);
                }
            }
        }
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new StickyHeaderItemAdapter<StickyHeaderItem>(new BaseMultiItemAdapter.LayoutDrawer() {
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
                                Toast.makeText(v.getContext(), province.getName(), Toast.LENGTH_SHORT).show();
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
                        final String name = ((City) item).getName();
                        tv_city.setText(name);
                        tv_city.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = mAdapter.getDataList().indexOf(item);
                                if (mAdapter.getDataList().remove(item)) {
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
        }, dataList);

        mRecyclerView.setAdapter(mAdapter);

        StickyHeaderScrollerListener headerScrollerListener = new StickyHeaderScrollerListener(mHeaderContainer);
        headerScrollerListener.setHeaderAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(headerScrollerListener);
    }

}
