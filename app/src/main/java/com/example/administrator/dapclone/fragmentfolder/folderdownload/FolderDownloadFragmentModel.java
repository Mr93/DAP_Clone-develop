package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragmentModel implements ProvidedModel {
	private static final String TAG = FolderDownloadFragmentModel.class.getSimpleName();
	RequiredPresenter requiredPresenter;
	BroadcastReceiver broadcastReceiver;

	public FolderDownloadFragmentModel(RequiredPresenter requiredPresenter) {
		this.requiredPresenter = requiredPresenter;
	}

	@Override
	public void getDownloadDataFromDB() {
		new AsyncTask<Void, Void, ArrayList<TaskInfo>>() {
			@Override
			protected ArrayList<TaskInfo> doInBackground(Void... params) {
				return DBHelper.getInstance().getAllTask();
			}

			@Override
			protected void onPostExecute(ArrayList<TaskInfo> values) {
				if (values != null && !values.isEmpty()) {
					Log.d(TAG, "onPostExecute: " + values.size());
					requiredPresenter.setDownloadData(values);
				}
				super.onPostExecute(values);
			}
		}.execute();
	}

	@Override
	public void unRegisterBroadCast() {
		try {
			MyApplication.getAppContext().unregisterReceiver(broadcastReceiver);
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "unRegisterBroadCast: " + e);
		}
	}

	@Override
	public void registerBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantValues.ACTION_NEW_TASK);
		filter.addAction(ConstantValues.ACTION_ERROR_TASK);
		filter.addAction(ConstantValues.ACTION_UPDATE_TASK);
		filter.addAction(ConstantValues.ACTION_COMPLETE_TASK);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null) {
					TaskInfo taskInfo = intent.getParcelableExtra(ConstantValues.FILE_INFO);
					Log.d(TAG, "onReceive: " + intent.getAction());
					if (taskInfo == null) {
						return;
					}
					if (ConstantValues.ACTION_NEW_TASK.equalsIgnoreCase(intent.getAction())) {
						Log.d(TAG, "onReceive: new task");
						requiredPresenter.createNewTask(taskInfo);
					} else if (ConstantValues.ACTION_UPDATE_TASK.equalsIgnoreCase(intent.getAction())) {
						requiredPresenter.updateATask(taskInfo);
					} else if (ConstantValues.ACTION_COMPLETE_TASK.equalsIgnoreCase(intent.getAction())) {
						Log.d(TAG, "onReceive: done");
						requiredPresenter.updateATask(taskInfo);
						Toast.makeText(context, taskInfo.name + " download completed", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, taskInfo.name + " download error", Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		MyApplication.getAppContext().registerReceiver(broadcastReceiver, filter);
	}
}
