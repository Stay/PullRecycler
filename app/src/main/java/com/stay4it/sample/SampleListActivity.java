package com.stay4it.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stay4it.R;
import com.stay4it.core.BaseListActivity;
import com.stay4it.widgets.pull.BaseViewHolder;
import com.stay4it.widgets.pull.PullRecycler;

import java.util.ArrayList;

/**
 * Created by Stay on 25/2/16.
 * Powered by www.stay4it.com
 */
public class SampleListActivity extends BaseListActivity<String> {

    @Override
    protected void setUpTitle(int titleResId) {
        super.setUpTitle(R.string.title_recycler_activity);
    }

    @Override
    protected void setUpData() {
        super.setUpData();
        recycler.setRefreshing();
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sample_list_item, parent, false);
        return new SampleViewHolder(view);
    }

//    @Override
//    protected ILayoutManager getLayoutManager() {
//        MyGridLayoutManager layoutManager = new MyGridLayoutManager(getApplicationContext(), 3);
//        return layoutManager;
//    }

    //    @Override
//    protected ILayoutManager getLayoutManager() {
//        return new MyStaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//    }

    @Override
    public void onRefresh(final int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
                    mDataList.clear();
                }
                int size = mDataList.size();
                for (int i = size; i < size + 20; i++) {
                    mDataList.add("sample list item " + i);
                }
                adapter.notifyDataSetChanged();
                recycler.onRefreshCompleted();
                if (mDataList.size() < 100) {
                    recycler.enableLoadMore(true);
                } else {
                    recycler.enableLoadMore(false);
                }
            }
        }, 3000);

    }

    class SampleViewHolder extends BaseViewHolder {

        TextView mSampleListItemLabel;

        public SampleViewHolder(View itemView) {
            super(itemView);
            mSampleListItemLabel = (TextView) itemView.findViewById(R.id.mSampleListItemLabel);
        }

        @Override
        public void onBindViewHolder(int position) {
            mSampleListItemLabel.setText(mDataList.get(position));
        }

        @Override
        public void onItemClick(View view, int position) {

        }

    }
}
