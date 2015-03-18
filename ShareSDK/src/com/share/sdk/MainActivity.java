package com.share.sdk;
import cn.sharesdk.R;
import cn.sharesdk.framework.ShareSDK;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ShareSDK.initSDK(this);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AndroidShare as = new AndroidShare(
						MainActivity.this,
						"哈哈---超方便的分享！！！来自allen",
						"http://img6.cache.netease.com/cnews/news2012/img/logo_news.png");
				as.show();
			}
		});

	}

}
