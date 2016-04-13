package com.stay4it.sample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stay4it.R;
import com.stay4it.core.BaseListFragment;
import com.stay4it.model.BaseModel;
import com.stay4it.model.Benefit;
import com.stay4it.request.Api;
import com.stay4it.widgets.pull.BaseViewHolder;
import com.stay4it.widgets.pull.PullRecycler;
import com.stay4it.widgets.pull.layoutmanager.ILayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyGridLayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyLinearLayoutManager;
import com.stay4it.widgets.pull.layoutmanager.MyStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Stay on 8/3/16.
 * Powered by www.stay4it.com
 */
public class SampleListFragment extends BaseListFragment<Benefit> {
    private int random;
    private int page = 1;

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
        switch (random) {
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
        if (random == 0) {
            return super.getItemDecoration();
        } else {
            return null;
        }
    }

    @Override
    public void onRefresh(final int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            page = 1;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        api.rxBenefits(20, page++)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseModel<ArrayList<Benefit>>>() {
                    @Override
                    public void call(BaseModel<ArrayList<Benefit>> model) {
                        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
                            mDataList.clear();
                        }
                        if (model.results == null || model.results.size() == 0) {
                            recycler.enableLoadMore(false);
                        } else {
                            recycler.enableLoadMore(true);
                            mDataList.addAll(model.results);
                            adapter.notifyDataSetChanged();
                        }
                        recycler.onRefreshCompleted();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        recycler.onRefreshCompleted();
                    }
                })
        ;

    }

    class SampleViewHolder extends BaseViewHolder {

        ImageView mSampleListItemImg;
        TextView mSampleListItemLabel;

        public SampleViewHolder(View itemView) {
            super(itemView);
            mSampleListItemLabel = (TextView) itemView.findViewById(R.id.mSampleListItemLabel);
            mSampleListItemImg = (ImageView) itemView.findViewById(R.id.mSampleListItemImg);
        }

        @Override
        public void onBindViewHolder(int position) {
            mSampleListItemLabel.setVisibility(View.GONE);
            Glide.with(mSampleListItemImg.getContext())
                    .load(mDataList.get(position).url)
                    .centerCrop()
                    .placeholder(R.color.app_primary_color)
                    .crossFade()
                    .into(mSampleListItemImg);
        }

        @Override
        public void onItemClick(View view, int position) {

        }

    }
}
