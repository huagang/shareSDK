package com.share.sdk;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.R;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class AndroidShare extends Dialog implements AdapterView.OnItemClickListener {
	private LinearLayout mLayout;
	private GridView mGridView;
	private float mDensity;
	private String msgText = "分享了...哈哈";
	private String mImgPath;
	private int mScreenOrientation;
	private List<ShareItem> mListData;
	private Handler mHandler = new Handler();

	private Runnable work = new Runnable() {
		public void run() {
			int orient = getScreenOrientation();
			if (orient != mScreenOrientation) {
				if (orient == 0)
					mGridView.setNumColumns(4);
				else {
					mGridView.setNumColumns(6);
				}
				mScreenOrientation = orient;
				((AndroidShare.MyAdapter) mGridView.getAdapter()).notifyDataSetChanged();
			}
			mHandler.postDelayed(this, 1000L);
		}
	};

	public AndroidShare(Context context) {
		super(context, R.style.shareDialogTheme);
	}

	public AndroidShare(Context context, int theme, String msgText, final String imgUri) {
		super(context, theme);
		this.msgText = msgText;

		if (Patterns.WEB_URL.matcher(imgUri).matches())
			new Thread(new Runnable() {
				public void run() {
					try {
						mImgPath = getImagePath(imgUri, getFileCache());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		else
			this.mImgPath = imgUri;
	}

	public AndroidShare(Context context, String msgText, final String imgUri) {
		super(context, R.style.shareDialogTheme);
		this.msgText = msgText;

		if (Patterns.WEB_URL.matcher(imgUri).matches())
			new Thread(new Runnable() {
				public void run() {
					try {
						mImgPath = getImagePath(imgUri,getFileCache());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		else
			this.mImgPath = imgUri;
	}
	public AndroidShare(Context context, String msgText) {
		super(context, R.style.shareDialogTheme);
		this.msgText = msgText;
	}

	void init(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		this.mDensity = dm.density;
		this.mListData = new ArrayList<ShareItem>();
		this.mListData.add(new ShareItem("微信", R.drawable.umeng_socialize_wechat,
				"com.tencent.mm.ui.tools.ShareImgUI", Wechat.NAME));
		this.mListData.add(new ShareItem("朋友圈", R.drawable.umeng_socialize_wxcircle,
				"com.tencent.mm.ui.tools.ShareToTimeLineUI", WechatMoments.NAME));
		this.mListData.add(new ShareItem("qq", R.drawable.umeng_socialize_qq_on,
				"com.tencent.mobileqq.activity.JumpActivity",QQ.NAME));
		this.mListData.add(new ShareItem("qq空间", R.drawable.umeng_socialize_qzone_on,
				"com.qzone.ui.operation.QZonePublishMoodActivity",QZone.NAME));
		this.mListData.add(new ShareItem("短信", R.drawable.umeng_socialize_sms_on,
				"",ShortMessage.NAME));

		this.mLayout = new LinearLayout(context);
		this.mLayout.setOrientation(1);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
		params.leftMargin = ((int) (10.0F * this.mDensity));
		params.rightMargin = ((int) (10.0F * this.mDensity));
		this.mLayout.setLayoutParams(params);
		this.mLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

		this.mGridView = new GridView(context);
		this.mGridView.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
		this.mGridView.setGravity(17);
		this.mGridView.setHorizontalSpacing((int) (10.0F * this.mDensity));
		this.mGridView.setVerticalSpacing((int) (10.0F * this.mDensity));
		this.mGridView.setStretchMode(1);
		this.mGridView.setColumnWidth((int) (90.0F * this.mDensity));
		this.mGridView.setHorizontalScrollBarEnabled(false);
		this.mGridView.setVerticalScrollBarEnabled(false);
		this.mLayout.addView(this.mGridView);
	}


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = getContext();
		init(context);
		setContentView(this.mLayout);

		getWindow().setGravity(80);

		if (getScreenOrientation() == 0) {
			this.mScreenOrientation = 0;
			this.mGridView.setNumColumns(4);
		} else {
			this.mGridView.setNumColumns(6);
			this.mScreenOrientation = 1;
		}
		this.mGridView.setAdapter(new MyAdapter());
		this.mGridView.setOnItemClickListener(this);

		this.mHandler.postDelayed(this.work, 1000L);

		setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				mHandler.removeCallbacks(work);
			}
		});
	}

	public void show() {
		super.show();
	}

	public int getScreenOrientation() {
		int landscape = 0;
		int portrait = 1;
		Point pt = new Point();
		getWindow().getWindowManager().getDefaultDisplay().getSize(pt);
		int width = pt.x;
		int height = pt.y;
		return width > height ? portrait : landscape;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ShareItem share = (ShareItem) this.mListData.get(position);
		shareMsg(getContext(), "分享到...", this.msgText, this.mImgPath, share);
	}

	private void shareMsg(Context context, String msgTitle, String msgText,
			String imgPath, ShareItem share) {
		ShareParams sp = new ShareParams();
		sp.setText(msgText);
		sp.setImagePath(imgPath);
		Platform weibo = ShareSDK.getPlatform(share.packageName);
		weibo.setPlatformActionListener(paListener); // 设置分享事件回调
		// 执行图文分享
		weibo.share(sp);
		this.dismiss();
	}	
	PlatformActionListener paListener;
	public void setPaListener(PlatformActionListener paListener) {
		this.paListener = paListener;
	}

	private File getFileCache() {
		File cache = null;

		if (Environment.getExternalStorageState().equals("mounted"))
			cache = new File(Environment.getExternalStorageDirectory() + "/." + getContext().getPackageName());
		else {
			cache = new File(getContext().getCacheDir().getAbsolutePath() + "/." + getContext().getPackageName());
		}
		if ((cache != null) && (!cache.exists())) {
			cache.mkdirs();
		}
		return cache;
	}

	public String getImagePath(String imageUrl, File cache) throws Exception {
		String name = imageUrl.hashCode() + imageUrl.substring(imageUrl.lastIndexOf("."));
		File file = new File(cache, name);

		if (file.exists()) {
			return file.getAbsolutePath();
		}

		URL url = new URL(imageUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();

			return file.getAbsolutePath();
		}

		return null;
	}

	private final class MyAdapter extends BaseAdapter {
		private static final int image_id = 256;
		private static final int tv_id = 512;

		public MyAdapter() {
		}

		public int getCount() {
			return mListData.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0L;
		}

		private View getItemView() {
			LinearLayout item = new LinearLayout(getContext());
			item.setOrientation(1);
			int padding = (int) (10.0F * mDensity);
			item.setPadding(padding, padding, padding, padding);
			item.setGravity(17);

			ImageView iv = new ImageView(getContext());
			item.addView(iv);
			iv.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
			iv.setId(image_id);

			TextView tv = new TextView(getContext());
			item.addView(tv);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
			layoutParams.topMargin = ((int) (5.0F * mDensity));
			tv.setLayoutParams(layoutParams);
			tv.setTextColor(Color.parseColor("#212121"));
			tv.setTextSize(16.0F);
			tv.setId(tv_id);

			return item;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getItemView();
			}
			ImageView iv = (ImageView) convertView.findViewById(image_id);
			TextView tv = (TextView) convertView.findViewById(tv_id);
			AndroidShare.ShareItem item = (AndroidShare.ShareItem) mListData.get(position);
			iv.setImageResource(item.logo);
			tv.setText(item.title);
			return convertView;
		}
	}

	private class ShareItem {
		String title;
		int logo;
		String activityName;
		String packageName;
		public ShareItem(String title, int logo, String activityName, String packageName) {
			this.title = title;
			this.logo = logo;
			this.activityName = activityName;
			this.packageName = packageName;
		}
	}
}
