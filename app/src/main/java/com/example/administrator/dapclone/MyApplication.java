package com.example.administrator.dapclone;

import android.app.Application;
import android.content.Context;

import com.example.administrator.dapclone.fragmentdownload.DownloadModule;
import com.example.administrator.dapclone.fragmentfolder.folderdownload.FolderDownloadModule;

/**
 * Created by Administrator on 03/27/2017.
 */

public class MyApplication extends Application {

	private static Context context;
	private NetComponent netComponent;

	@Override
	public void onCreate() {
		super.onCreate();
		createApplicationContext(this);
		netComponent = DaggerNetComponent.builder()
				.downloadModule(new DownloadModule())
				.folderDownloadModule(new FolderDownloadModule())
				.build();
	}

	private static void createApplicationContext(MyApplication downloadManagerApplication) {
		context = downloadManagerApplication.getApplicationContext();
	}

	public NetComponent getNetComponent() {
		return netComponent;
	}

	public static Context getAppContext() {
		return context;
	}
}
