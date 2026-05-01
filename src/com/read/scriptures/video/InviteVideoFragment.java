package com.read.scriptures.video;

import android.content.res.AssetManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.read.scriptures.R;
import com.read.scriptures.bean.InviteVideoBean;
import com.read.scriptures.ui.fragment.Base1Fragment;
import com.music.player.lib.util.NetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class InviteVideoFragment extends Base1Fragment {

    private InviteVideoAdapter inviteVideoAdapter;

    @Override
    public int onObtainLayoutResId() {
        return R.layout.ft_invite_video;
    }

    @Override
    public void lazyLoad() {
        Map<String, String> map = new HashMap<>();
        map.put("type", "1");
        NetUtil.getInviteData("https://book.sdacn.cn//api/v1/Audiocolumn/", map, new NetUtil.CallBack() {
            @Override
            public void onSuccess(String t) {
                InviteVideoBean inviteVideoBean = new Gson().fromJson(t, InviteVideoBean.class);
                if (inviteVideoBean.data == null || inviteVideoBean.data.isEmpty()) {
                   return;
                }
                inviteVideoAdapter.setNewData(inviteVideoBean.data);
            }
        });

//        String json = getJson("json.json");
//        InviteVideoBean inviteVideoBean = new Gson().fromJson(json, InviteVideoBean.class);
//        if (inviteVideoBean.data == null || inviteVideoBean.data.isEmpty()) {
//            return;
//        }
//        inviteVideoAdapter.setNewData(inviteVideoBean.data);
    }

    @Override
    public void initWidget() {
        RecyclerView rcv_main = findViewById1(R.id.rcv_main);
        rcv_main.setLayoutManager(new LinearLayoutManager(getActivity()));
        inviteVideoAdapter = new InviteVideoAdapter();
        rcv_main.setAdapter(inviteVideoAdapter);
    }

    public String getJson(String fileName) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = getActivity().getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
