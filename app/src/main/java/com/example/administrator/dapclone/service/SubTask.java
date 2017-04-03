package com.example.administrator.dapclone.service;

import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.SubTaskInfo;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.networkinterface.NetworkApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 03/27/2017.
 */

public class SubTask extends Thread {
	private static final String TAG = SubTask.class.getSimpleName();
	private Task task;
	private SubTaskInfo subTaskInfo;
	private boolean isRunning = false;
	private byte[] buffer = new byte[4096];
	RandomAccessFile randomAccessFile;
	private int errorTime = 2;
	private boolean isError = true;

	public SubTask(Task task, SubTaskInfo subTaskInfo) {
		this.task = task;
		this.subTaskInfo = subTaskInfo;
		updateDB();
	}

	public SubTaskInfo getSubTaskInfo() {
		return subTaskInfo;
	}

	@Override
	public synchronized void start() {
		isRunning = true;
		try {
			randomAccessFile = new RandomAccessFile(task.getTaskInfo().path, "rw");
			randomAccessFile.seek(subTaskInfo.start);
		} catch (FileNotFoundException e) {
			isRunning = false;
			stopDownload();
			e.printStackTrace();
		} catch (IOException e) {
			isRunning = false;
			stopDownload();
			e.printStackTrace();
		}
		super.start();
	}

	@Override
	public void run() {
		super.run();
			while (isRunning) {
				isRunning = false;
				OkHttpClient client = new OkHttpClient.Builder()
						.connectTimeout(5, TimeUnit.MINUTES)
						.readTimeout(5, TimeUnit.MINUTES)
						.build();
				downloadAPart(client, task.getTaskInfo());
			}
	}

	private void updateDB() {
		if (subTaskInfo.subTaskId == -1) {
			this.subTaskInfo.subTaskId = DBHelper.getInstance().insertSubTask(subTaskInfo, task.getTaskInfo().taskId);
		} else {
			if (task.getTaskInfo().taskId == -1) {
				this.subTaskInfo.subTaskId = DBHelper.getInstance().insertSubTask(subTaskInfo, task.getTaskInfo().taskId);
			}
		}
	}

	private void downloadAPart(final OkHttpClient client, final TaskInfo taskInfo) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://google.com")
				.client(client)
				.build();
		String range = "bytes=" + subTaskInfo.start + "-" + subTaskInfo.end;
		NetworkApi networkApi = retrofit.create(NetworkApi.class);
		Call<ResponseBody> responseBodyCall = networkApi.download(taskInfo.url, range, "close");
		responseBodyCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(final Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (response.isSuccessful()) {
							try {
								writeToFile(response.body());
							} catch (IOException e) {
								stopDownload();
								e.printStackTrace();
							}
						} else {
							handLeError(client, taskInfo);
							Log.e(TAG, "server contact failed");
						}
					}
				}).start();
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				handLeError(client, taskInfo);
			}
		});
	}

	private void handLeError(OkHttpClient client, TaskInfo taskInfo) {
		if (errorTime < 3) {
			downloadAPart(client, taskInfo);
			errorTime++;
		} else {
			stopDownload();
		}
	}

	private void stopDownload() {
		if (subTaskInfo.status.equalsIgnoreCase(ConstantValues.STATUS_COMPLETED)) {
			Log.d(TAG, "stopDownload: error here " + subTaskInfo.start);
		}
		subTaskInfo.status = ConstantValues.STATUS_ERROR;
		DBHelper.getInstance().updateSubTask(subTaskInfo, subTaskInfo.taskId);
		task.onThreadError(this);
	}

	//synchronized
	private void writeToFile(ResponseBody body) throws IOException {
		InputStream inputStream = body.byteStream();
		int count;
		while ((count = inputStream.read(buffer)) != -1) {
			randomAccessFile.write(buffer, 0, count);
		}
		inputStream.close();
		randomAccessFile.close();
		this.isError = false;
		subTaskInfo.status = ConstantValues.STATUS_COMPLETED;
		DBHelper.getInstance().updateSubTask(subTaskInfo, subTaskInfo.taskId);
		task.onThreadDone(this);
	}
}
