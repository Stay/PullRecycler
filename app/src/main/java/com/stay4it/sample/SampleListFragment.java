package com.stay4it.sample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stay4it.R;
import com.stay4it.core.BaseListFragment;
import com.stay4it.widgets.pull.BaseViewHolder;
import com.stay4it.widgets.pull.PullRecycler;
import com.stay4it.widgets.pull.layoutmanager.ILayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyGridLayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyLinearLayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Stay on 8/3/16.
 * Powered by www.stay4it.com
 */
public class SampleListFragment extends BaseListFragment<String>  {
    private int random;

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sample_list_item, parent, false);
        return new SampleViewHolder(view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler.setRefreshing();
    }

    @Override
    protected ILayoutManager getLayoutManager() {
        random = new Random().nextInt(3);
        switch (random){
            case 0:
                return new MyLinearLayoutManager(getContext());
            case 1:
                return new MyGridLayoutManager(getContext(), 3);
            case 2:
                return new MyStaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        }
        return super.getLayoutManager();
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        if (random == 0){
            return super.getItemDecoration();
        }else {
            return null;
        }
    }

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
