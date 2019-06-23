package com.hnkjrjxy.project2019down.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.gson.JsonObject;
import com.hnkjrjxy.project2019down.MyApplication;
import com.hnkjrjxy.project2019down.R;
import com.hnkjrjxy.project2019down.util.Http;
import com.hnkjrjxy.project2019down.util.ToastUtil;
import com.hnkjrjxy.project2019down.view.Usersetting_view;

import org.json.JSONObject;

import java.util.ArrayList;

public class UserInfoSetting extends Activity {
    private ListView usersetting_lv;
    private BaseAdapter adapter;
    private ArrayList<String[]> infos;
    private String age,sex;
    private JsonObject jsonObject;
    private String[] msgs = {
            "你需要填写少量的个人信息，通过智能匹配，遇见有同感的人。你的性别是？",
            "你的年龄是？",
            "确认后，你的个人信息无法修改？"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usersetting);
        initView();
        Bundle bundle = getIntent().getExtras();
        jsonObject = new JsonObject();
        jsonObject.addProperty("token",MyApplication.token);
        jsonObject.addProperty("phone",bundle.getString("phone"));
        jsonObject.addProperty("pwd",bundle.getString("pwd"));
    }

    private void initView() {
        infos = new ArrayList();
        infos.add(new String[]{"260","男","女"});
        infos.add(new String[]{
                "400",
                "80后",
                "85后",
                "90后",
                "95后",
                "00后",
                "05后",
        });
        infos.add(new String[]{"400","确定"});
        usersetting_lv = (ListView) findViewById(R.id.usersetting_lv);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return infos.size();
            }

            @Override
            public Object getItem(int position) {
                return infos.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(UserInfoSetting.this).inflate(R.layout.usersetting_item1, null);
                    convertView.setTag(new ViewHolder(convertView));
                }
                initializeViews(position,(String[])getItem(position), (ViewHolder) convertView.getTag(),convertView);
                return convertView;
            }

            private void initializeViews(int position, String[] object, final ViewHolder holder, View convertView) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,convertView.getHeight());
                convertView.setLayoutParams(params);
                holder.usersettingV.setMsg(msgs[position%3]);
                holder.usersettingV.setSize(Integer.parseInt(object[0]));
                String[] strs = new String[object.length-1];
                for (int i = 1; i < object.length; i++) {
                    strs[i-1] = object[i];
                }
                holder.usersettingV.setInfos(strs);
                holder.usersettingV.setOnCallBack(new Usersetting_view.OnCallBack() {
                    @Override
                    public void callback(String object) {
                        if(object.equals("确定") && sex != null && age != null){
                            showAlert();
                        }else{
                            if(object.length() == 1){
                                sex = object;
                            }else{
                               age = object;
                            }
                        }
                    }
                });
            }

            class ViewHolder {
                private Usersetting_view usersettingV;

                public ViewHolder(View view) {
                    usersettingV = (Usersetting_view) view.findViewById(R.id.usersetting_v);
                }
            }
        };
        usersetting_lv.setAdapter(adapter);
    }

    private void showAlert(){
        View view = LayoutInflater.from(this).inflate(R.layout.usersetting_alert,null);
        final EditText et = view.findViewById(R.id.alert_name);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("请为你自己取一个昵称")
                .setView(view)
                .create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(et.getText())){
                    ToastUtil.toToast("昵称不能为空");
                    return;
                }
                jsonObject.addProperty("username",et.getText().toString());
                submit();
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void submit() {
        jsonObject.addProperty("sex",sex);
        jsonObject.addProperty("age",age);
        Http.Post(this, "/Data/Register", jsonObject.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(jsonObject.optString("msg").equals("S")){
                    ToastUtil.toToast("注册成功！");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },100);
                }
            }
        });
    }
}
