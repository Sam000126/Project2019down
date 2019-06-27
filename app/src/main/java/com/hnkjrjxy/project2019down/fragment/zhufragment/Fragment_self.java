package com.hnkjrjxy.project2019down.fragment.zhufragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.JsonObject;
import com.hnkjrjxy.project2019down.MyApplication;
import com.hnkjrjxy.project2019down.R;
import com.hnkjrjxy.project2019down.activity.LoginActivity;
import com.hnkjrjxy.project2019down.activity.SettingActivity;
import com.hnkjrjxy.project2019down.util.Http;

import org.json.JSONObject;

public class Fragment_self extends Fragment {

    private RecyclerView a4_list;
    private MyAdapter listAdapter;
    private ImageView setting;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int num = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a4, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        a4_list = (RecyclerView) view.findViewById(R.id.a4_list);
        setting = (ImageView) view.findViewById(R.id.setting);
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.smartrefreshlayout);
        //设置下拉刷新环形加载条的颜色，最多使用四个颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.color1);
        //设置下拉是否开始缩放，起点是20的高度，最多到达100的高度
        swipeRefreshLayout.setProgressViewOffset(false, 20, 100);
        getData();
        layoutManager = new LinearLayoutManager(getActivity());
        a4_list.setLayoutManager(layoutManager);
        listAdapter = new MyAdapter();
        a4_list.setAdapter(listAdapter);
        //设置Item增加、移除动画
        a4_list.setItemAnimator(new DefaultItemAnimator());
        a4_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) { }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                //lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount()-1   则改控件处于最底部
                //dx>0 则表示 右滑 ， dx<0 表示 左滑
                //dy <0 表示 上滑， dy>0 表示下滑
                //通过这几个参数就可以监听 滑动方向的状态。
                //判断是否向下滑动，如果向下滑动即将到底部的时候进行预加载
                if (dy > 0) {
                    //双重判断，以防滑动太快导致没有检测到滑动的位置信息
                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 4 ||
                            lastPosition == recyclerView.getLayoutManager().getItemCount() - 3) {
                        //在此处再次拿数据进行适配器的刷新
                        num = num + 20;
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.isIsLogin()){
                    Intent intent=new Intent(getActivity(),SettingActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        layoutlistener();
    }

    private void getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token",MyApplication.getToken());
        jsonObject.addProperty("id",MyApplication.sharedPreferences.getInt("id",0));
        Log.i("Fragment5", "getData: "+jsonObject.toString());
        Http.Post(getActivity(), "Invitation/GetMeInvitation",
                jsonObject.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        Log.i("Fragment5", "onResponse: --------"+object);
                    }
        });
    }

    private void layoutlistener() {
        //下拉刷新SwipeRefreshLayout监听

        listener = new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(listener);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.m1);
                textView = itemView.findViewById(R.id.t1);
            }
        }

    }
}
