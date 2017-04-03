package com.example.administrator.dapclone.fragmentdownload;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.exception.NetworkException;
import com.example.administrator.dapclone.utils.Validator;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadPresenter implements IDownloadFragment.ProvidedPresenter, IDownloadFragment.RequiredPresenter {

	private static final String TAG = DownloadPresenter.class.getSimpleName();
	private IDownloadFragment.RequiredView requiredView;
	private IDownloadFragment.ProvidedModel providedModel;

	public DownloadPresenter() {

	}

	@Override
	public void download(String url) {
		try {
			if (!Validator.isNetworkOnline()) {
				requiredView.errorDownload("Network isn't connected");
			} else {
				if (!Validator.HTTP.equalsIgnoreCase(Validator.getProtocol(url)) &&
						!Validator.HTTPS.equalsIgnoreCase(Validator.getProtocol(url))) {
					url = Validator.addProtocol(url);
				}
				if (Validator.isValid(url)) {
					handleBeforeDownload(url);
				}
			}
		} catch (NetworkException e) {
			requiredView.errorDownload(e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleBeforeDownload(String url) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = Validator.getFileNameFromUrl(url);
		taskInfo.extension = Validator.getExtension(url);
		taskInfo.url = url;
		new AsyncTask<Void, Void, Boolean>() {
			TaskInfo taskInfo;

			public void execute(TaskInfo taskInfo) {
				this.taskInfo = taskInfo;
				super.execute();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				Log.d(TAG, "doInBackground: " + taskInfo.name);
				return DBHelper.getInstance().checkTaskDownloadExisted(taskInfo);
			}

			@Override
			protected void onPostExecute(Boolean values) {
				if (!values) {
					taskInfo.taskId = DBHelper.getInstance().getTaskIdByNameAndUrl(taskInfo);
					Log.d(TAG, "download: " + taskInfo.taskId);
					providedModel.download(taskInfo);
				}
				super.onPostExecute(values);
			}
		}.execute(taskInfo);
	}

	@Override
	public void setView(IDownloadFragment.RequiredView view) {
		requiredView = view;
	}

	@Override
	public void setModel(IDownloadFragment.ProvidedModel model) {
		providedModel = model;
	}

	@Override
	public Context getContext() {
		return requiredView.getFragmentContext();
	}
}
