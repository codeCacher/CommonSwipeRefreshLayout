package com.cs.refresh;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cs.refresh.refresh.BaseProgressViewController;
import com.cs.refresh.refresh.MySwipeRefreshLayout;
import com.cs.refresh.refresh.RefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private MySwipeRefreshLayout mSrl;
    private MyAdapter mAdapter;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        mAdapter = new MyAdapter(this);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter);
        mAdapter.setData(list);

//        mSrl.setOnRefreshListener(new BaseSwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i("cuishun", "onRefresh");
//                getList(new CallBack() {
//                    @Override
//                    public void onCallBack(final List<Integer> list) {
//                        mUIHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.setData(list);
//                                mSrl.setRefreshing(false);
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onLoadMore() {
//                Log.i("cuishun", "onLoadMore");
//                getList(new CallBack() {
//                    @Override
//                    public void onCallBack(final List<Integer> list) {
//                        mUIHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.addData(list);
//                                mSrl.setLoadingMore(false);
//                            }
//                        });
//                    }
//                });
//            }
//        });

        mSrl.setRefreshProgressController(new BaseProgressViewController(this));

        mSrl.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("cuishun", "onRefresh");
                getList(new CallBack() {
                    @Override
                    public void onCallBack(final List<Integer> list) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(list);
                                mSrl.setRefreshing(false);
                            }
                        });
                    }
                });
            }

            @Override
            public void onLoadMore() {
                Log.i("cuishun", "onLoadMore");
                getList(new CallBack() {
                    @Override
                    public void onCallBack(final List<Integer> list) {
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.addData(list);
                                mSrl.setLoadingMore(false);
                            }
                        });
                    }
                });
            }
        });
    }

    public void refresh(View view) {
        mAdapter.setData(null);
    }

    interface CallBack {
        void onCallBack(List<Integer> list);
    }

    private void getList(final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                List<Integer> list = new ArrayList<>();
                Random random = new Random();
                for (int i = 0; i < 20; i++) {
                    list.add(random.nextInt());
                }
                callBack.onCallBack(list);
            }
        }).start();
    }

    private void initView() {
        mRv = findViewById(R.id.rv);
        mSrl = findViewById(R.id.srl);
    }
}
