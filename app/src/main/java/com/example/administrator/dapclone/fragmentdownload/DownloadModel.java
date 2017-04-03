package com.example.administrator.dapclone.fragmentdownload;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.service.NetworkService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 03/22/2017.
 */

public class DownloadModel implements IDownloadFragment.ProvidedModel {

	private static final String TAG = DownloadModel.class.getSimpleName();

	private IDownloadFragment.RequiredPresenter presenter;


	public DownloadModel(IDownloadFragment.RequiredPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void download(final TaskInfo taskInfo) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					OkHttpClient client = new OkHttpClient.Builder()
							.connectTimeout(5, TimeUnit.MINUTES)
							.readTimeout(5, TimeUnit.MINUTES)
							.build();
					Request request = new Request.Builder()
							.url(taskInfo.url)
							.build();
					Response response = client.newCall(request).execute();
					if (response.code() / 100 == 2) {
						handleRequestSuccess(response, taskInfo);
					}
				} catch (IOException e) {
					Log.d(TAG, "run: " + e);
					e.printStackTrace();
					return e.toString();
				}
				return "Start Downloading";
			}

			@Override
			protected void onPostExecute(String values) {
				Toast.makeText(presenter.getContext(), values, Toast.LENGTH_SHORT).show();
				super.onPostExecute(values);
			}
		}.execute();
	}

	private void handleRequestSuccess(Response response, TaskInfo taskInfo) {
		taskInfo.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + taskInfo
				.name;
		if (response.header("Content-Length") != null) {
			taskInfo.size = Long.valueOf(response.header("Content-Length"));
		}
		if ("bytes".equalsIgnoreCase(response.header("Accept-Ranges")) && taskInfo.size != 0) {
			taskInfo.isMultiThread = true;
		} else {
			taskInfo.isMultiThread = false;
		}
		Intent intent = new Intent(presenter.getContext(), NetworkService.class);
		intent.putExtra(ConstantValues.FILE_INFO, taskInfo);
		Log.d(TAG, "run: " + taskInfo.taskId);
		presenter.getContext().startService(intent);
	}
}
