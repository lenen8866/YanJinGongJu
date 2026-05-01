package com.read.scriptures.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.read.scriptures.R;
import com.read.scriptures.config.SystemConfig;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class NetSocietyShare
{
    public static final String AUTHORITY = "com.read.scriptures.provider";

	public static boolean share(Context ctx, String strPackageName, String strActivityName, String strShareImagePath, String strShareContent, String strShareTitle)
	{
		if(!strPackageName.isEmpty())
		{
			boolean bExist = false;
			List<PackageInfo> pinfo = ctx.getPackageManager().getInstalledPackages(0);
			for(int i = 0; i < pinfo.size(); i++)
			{
				if((pinfo.get(i)).packageName.equalsIgnoreCase(strPackageName))
				{
					bExist = true;
					break;
				}
			}

			if(!bExist)
				return false;
		}

		if(strActivityName.compareTo("com.tencent.mm.ui.tools.ShareToTimeLineUI") == 0)
		{
			IWXAPI api = WXAPIFactory.createWXAPI(ctx, SystemConfig.WX_KEY);

			WXWebpageObject webPage = new WXWebpageObject();
			webPage.webpageUrl = strShareImagePath;
			WXMediaMessage msg = new WXMediaMessage(webPage);
			//msg.title = context.getString(R.string.app_name);
			msg.title = strShareTitle;
			msg.description = strShareContent;
			//图片加载是使用的ImageLoader.loadImageSync() 同步方法
			//并且还要创建图片的缩略图，因为微信限制了图片的大小
            Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            bmp.recycle();
			msg.thumbData = FileUtil.bmpToByteArray(thumbBmp,true);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
			req.message = msg;
			//好友
			req.scene =  SendMessageToWX.Req.WXSceneTimeline;
			// 调用api接口发送数据到微信
			api.sendReq(req);

		}
		else if(strActivityName.compareTo("com.tencent.mm.ui.tools.ShareImgUI") == 0)
		{
			IWXAPI api = WXAPIFactory.createWXAPI(ctx, SystemConfig.WX_KEY);

			WXWebpageObject webPage = new WXWebpageObject();
			webPage.webpageUrl = strShareImagePath;
			WXMediaMessage msg = new WXMediaMessage(webPage);
			//msg.title = context.getString(R.string.app_name);
			msg.title = strShareTitle;
			msg.description = strShareContent;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
			req.message = msg;
			//好友
			req.scene =  SendMessageToWX.Req.WXSceneSession;
			// 调用api接口发送数据到微信
			boolean result = api.sendReq(req);
		}
		else
		{
			Intent intent = new Intent(Intent.ACTION_SEND);
			if(strShareImagePath == null || strShareImagePath.equals(""))
			{
				intent.setType("text/plain");
			}
			else
			{
				File f = new File(strShareImagePath);
				if(f.exists() && f.isFile())
				{
					intent.setType("image/jpg");
					Uri u = Uri.fromFile(f);
					intent.putExtra(Intent.EXTRA_STREAM, u);
				}
			}
			intent.putExtra(Intent.EXTRA_SUBJECT, strShareTitle);
			intent.putExtra(Intent.EXTRA_TEXT, strShareContent);
			try
			{
				if(!strPackageName.isEmpty())
					intent.setComponent(new ComponentName(strPackageName, strActivityName));
				else
					intent = Intent.createChooser(intent, strShareTitle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			}
			catch(Exception e)
			{}
		}

		return true;
	}

	public static boolean goWechatMini(Context ctx,String strPackageName,String miniId,String miniPath){
		if(!strPackageName.isEmpty())
		{
			boolean bExist = false;
			List<PackageInfo> pinfo = ctx.getPackageManager().getInstalledPackages(0);
			for(int i = 0; i < pinfo.size(); i++)
			{
				if((pinfo.get(i)).packageName.equalsIgnoreCase(strPackageName))
				{
					bExist = true;
					break;
				}
			}

			if(!bExist)
				return false;
		}
		String appId = SystemConfig.WX_KEY; // 填应用AppId
		IWXAPI api = WXAPIFactory.createWXAPI(ctx, appId);

		WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
		req.userName = miniId; // 填小程序原始id
		req.path = miniPath;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
		req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
		boolean isRequest = api.sendReq(req);
		return isRequest;
	}


	public static ArrayList<String> queryInstalledMarketPkgs(Context ctx)
	{
		ArrayList<String> pkgs = new ArrayList<>();
		if(ctx == null)
			return pkgs;

		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.APP_MARKET");

		PackageManager pm = ctx.getPackageManager();
		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		if(infos == null || infos.size() == 0)
			return pkgs;

		int size = infos.size();
		for(int i = 0; i < size; i++)
		{
			String pkgName = "";
			try
			{
				ActivityInfo activityInfo = infos.get(i).activityInfo;
				pkgName = activityInfo.packageName;

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(!TextUtils.isEmpty(pkgName))
				pkgs.add(pkgName);
		}

		return pkgs;
	}

	public static ArrayList<String> filterInstalledPkgs(Context ctx, ArrayList<String> listFilterPackages)
	{
		ArrayList<String> listResult = new ArrayList<>();
		if(ctx == null || listFilterPackages == null || listFilterPackages.size() == 0)
			return listResult;

		PackageManager pm = ctx.getPackageManager();
		List<PackageInfo> listInstalledPkgs = pm.getInstalledPackages(0);

		int nSizeInstallApp = listInstalledPkgs.size();
		int nSizeFilterApp = listFilterPackages.size();

		for(int j = 0; j < nSizeFilterApp; j++)
		{
			for(int i = 0; i < nSizeInstallApp; i++)
			{
				String installPkg = "";
				try
				{
					installPkg = listInstalledPkgs.get(i).applicationInfo.packageName;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				if(TextUtils.isEmpty(installPkg))
					continue;

				if(installPkg.equals(listFilterPackages.get(j)))
				{
					listResult.add(installPkg);
					break;
				}
			}
		}

		return listResult;
	}

	public static void launchMarket(Context ctx, String strMarketPkg)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id=" + ctx.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if(!TextUtils.isEmpty(strMarketPkg))
				intent.setPackage(strMarketPkg);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);
		}
		catch(Exception e)
		{
			try
			{
				PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);

				Uri uri = Uri.parse("market://details?id=" + info.packageName);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				if(!TextUtils.isEmpty(strMarketPkg))
					intent.setPackage(strMarketPkg);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			}
			catch(Exception e1)
			{}
		}
	}

	private static String insertImageToSystem(Context context, String imagePath) {
		String url = "";
		try {
			url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, "gugu", "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return url;
	}

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
