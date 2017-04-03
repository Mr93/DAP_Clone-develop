package com.example.administrator.dapclone.fragmentfolder.folderdownload;


import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.utils.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadPresenter implements ProvidedPresenter, RequiredPresenter {

	private static final String TAG = FolderDownloadPresenter.class.getSimpleName();
	RequiredView requiredView;
	ProvidedModel providedModel;
	private List<TaskInfo> taskInfoList;


	public FolderDownloadPresenter() {
		taskInfoList = new ArrayList<>();
	}

	@Override
	public void getDownloadDataFromDB() {
		providedModel.getDownloadDataFromDB();
	}

	@Override
	public ArrayList<TaskInfo> getTaskInfoList() {
		return (ArrayList<TaskInfo>) taskInfoList;
	}

	@Override
	public void setDownloadData(ArrayList<TaskInfo> taskList) {
		if (requiredView != null) {
			this.taskInfoList = taskList;
			Collections.reverse(this.taskInfoList);
			requiredView.fetchDataRecycleView();
		}
	}

	@Override
	public void createNewTask(TaskInfo taskInfo) {
		if (taskInfo != null) {
			boolean isDuplicate = false;
			for (TaskInfo temp : taskInfoList) {
				if (temp.taskId == taskInfo.taskId && temp.name.equalsIgnoreCase(taskInfo.name) &&
						temp.url.equalsIgnoreCase(taskInfo.url) && temp.isDownload == taskInfo.isDownload) {
					isDuplicate = true;
					break;
				}
			}
			if (!isDuplicate && requiredView != null) {
				this.taskInfoList.add(0, taskInfo);
				requiredView.updateDataRecycleView();
			}
		}
	}

	@Override
	public void updateATask(TaskInfo taskInfo) {
		if (taskInfo != null) {
			for (TaskInfo temp : taskInfoList) {
				if (temp.taskId == taskInfo.taskId && temp.name.equalsIgnoreCase(taskInfo.name) &&
						temp.url.equalsIgnoreCase(taskInfo.url) && temp.isDownload == taskInfo.isDownload) {
					temp.processedSize = taskInfo.processedSize;
					break;
				}
			}
			if (requiredView != null) {
				requiredView.updateDataRecycleView();
			}
		}
	}

	@Override
	public void setView(RequiredView view) {
		this.requiredView = view;
	}

	@Override
	public void setModel(ProvidedModel model) {
		this.providedModel = model;
	}

	@Override
	public void unRegisterBroadcast() {
		providedModel.unRegisterBroadCast();
	}

	@Override
	public void registerBroadCast() {
		providedModel.registerBroadCast();
	}

	@Override
	public void showPreviewFile(TaskInfo taskInfo) {
		String mimeTye = Validator.getMimeTyeFromExtension(taskInfo.url);
		if (mimeTye.contains(Validator.VIDEO)) {
			requiredView.preViewMedia(taskInfo, Validator.VIDEO);
		} else if (mimeTye.contains(Validator.MUSIC)) {
			requiredView.preViewMedia(taskInfo, Validator.MUSIC);
		} else {
			requiredView.preViewMedia(taskInfo, Validator.IMAGE);
		}
	}
}
