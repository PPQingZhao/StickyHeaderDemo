package com.pp.stickyheaderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pp.stickyheaderdemo.ui.LinearStickyHeaderActivity;
import com.pp.stickyheaderdemo.ui.GridStickyHeaderActivity;

/**
 * @author qing
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLinearHeader(View v) {
        startActivity(new Intent(getApplicationContext(), LinearStickyHeaderActivity.class));
    }

    public void onGridHeader(View v) {
        startActivity(new Intent(getApplicationContext(), GridStickyHeaderActivity.class));
    }
}
