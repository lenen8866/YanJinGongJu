package com.read.scriptures.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.bean.DonationRecordBean;
import com.read.scriptures.config.ZConfig;
import com.read.scriptures.ui.activity.base.BaseActivity;
import com.read.scriptures.ui.adapter.DonationRecordAdapter;
import com.music.player.lib.util.NetUtil;
import com.read.scriptures.util.StatusBarUtils;
import com.read.scriptures.util.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DonationRecordActivity extends BaseActivity {

    private DonationRecordAdapter adapter;

    private TextView tv_time;
    private TextView tv_no_data;
    private RecyclerView rcv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_donation_record);
        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        StatusBarUtils.initMainColorStatusBar(this);
        rcv_list = findViewById(R.id.rcv_list);
        tv_no_data = findViewById(R.id.tv_no_data);
        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonationRecordAdapter();
        rcv_list.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter a, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_show_remark:
                        showRemark(adapter.getItem(position).remarks);
                        break;
                }
            }
        });
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectTime();
            }
        });
        String time = TimeUtils.formatTime(new Date());
        tv_time.setText(time);
        getData(time);
    }

    private void showSelectTime() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(1970, 0, 1);

        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.formatTime(date);
                tv_time.setText(time);
                getData(time);
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(20)//标题文字大小
                .setTitleText("")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(ContextCompat.getColor(DonationRecordActivity.this, R.color.main_color))//标题文字颜色
                .setSubmitColor(ContextCompat.getColor(DonationRecordActivity.this, R.color.main_color))//确定按钮文字颜色
                .setCancelColor(ContextCompat.getColor(DonationRecordActivity.this, R.color.main_color))//取消按钮文字颜色
                .setTitleBgColor(0xFFffffff)//标题背景颜色 Night mode
                .setBgColor(0xFFf7f7f7)//滚轮背景颜色 Night mode
                .setDate(endDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    private void showRemark(String remarks) {
        new AlertDialog.Builder(this).setMessage(remarks).create().show();
    }

    private void getData(String time) {
        Map<String, String> map = new HashMap<>();
        map.put("month", time);
        NetUtil.post(ZConfig.SERVICE_URL + "/api/v1/paying/donation", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                DonationRecordBean donationRecordBean = new Gson().fromJson(t, DonationRecordBean.class);
                if (donationRecordBean == null || donationRecordBean.rows == null || donationRecordBean.rows.isEmpty()) {
                    tv_no_data.setVisibility(View.VISIBLE);
                    rcv_list.setVisibility(View.GONE);
                } else {
                    tv_no_data.setVisibility(View.GONE);
                    rcv_list.setVisibility(View.VISIBLE);
                }
                adapter.setNewData(donationRecordBean.rows);
            }
        });
    }
}
