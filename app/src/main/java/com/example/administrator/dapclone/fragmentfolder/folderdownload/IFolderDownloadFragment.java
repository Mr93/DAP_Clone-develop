package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.fragmentdownload.IDownloadFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 03/29/2017.
 */

public interface IFolderDownloadFragment {
	interface RequiredView {
		void fetchDataRecycleView();

		void updateDataRecycleView();

		void preViewMedia(TaskInfo taskInfo, String type);
	}

	interface ProvidedPresenter {
		void getDownloadDataFromDB();

		ArrayList<TaskInfo> getTaskInfoList();

		void setView(RequiredView view);

		void setModel(ProvidedModel model);

		void unRegisterBroadcast();

		void registerBroadCast();

		void showPreviewFile(TaskInfo taskInfo);
	}

	interface RequiredPresenter {
		void setDownloadData(ArrayList<TaskInfo> taskList);

		void createNewTask(TaskInfo taskInfo);

		void updateATask(TaskInfo taskInfo);
	}

	interface ProvidedModel {
		void getDownloadDataFromDB();

		void unRegisterBroadCast();

		void registerBroadCast();
	}
}
