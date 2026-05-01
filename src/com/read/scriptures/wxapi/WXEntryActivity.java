/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.read.scriptures.wxapi;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.read.scriptures.R;
import com.read.scriptures.config.SystemConfig;
import com.read.scriptures.manager.AccountManager;
import com.read.scriptures.util.SharedUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    IWXAPI api;
//    @BindView(R.id.tv_title)
//    TextView title;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.pay_result);
//		ButterKnife.bind(this);
//        title.setText("登录结果");
        api = WXAPIFactory.createWXAPI(this, SystemConfig.WX_KEY, false);
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	@OnClick({ R.id.iv_left,R.id.tv_fail_sure,R.id.tv_sure})
//	public void click(View view) {
//		switch (view.getId()) {
//			case R.id.iv_left:
//			case R.id.tv_fail_sure:
//			case R.id.tv_sure:
//				finish();
//				break;
//		}
//	}

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            //微信登录
            dealwithLogin(baseResp);
        } else {
            dealwithShare(baseResp);
        }
    }

    /**
     * 处理登录
     *
     * @param baseResp
     */
    private void dealwithLogin(BaseResp baseResp) {
		//登录授权成功
		String code = ((SendAuth.Resp) baseResp).code;
		//缓存
		SharedUtil.putString(getApplicationContext(), AccountManager.SP_WEIXIN_LOGIN_CODE,code);
		finish();
    }


    /**
     * 处理微信分享
     */
    private void dealwithShare(BaseResp baseResp) {
        int result = 0;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }

}
