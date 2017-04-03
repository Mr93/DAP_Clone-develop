package com.example.administrator.dapclone.fragmentdownload;

import android.content.Context;

import com.example.administrator.dapclone.TaskInfo;

/**
 * Created by Administrator on 03/21/2017.
 */

public interface IDownloadFragment {
	interface RequiredView {
		void errorDownload(String message);

		Context getFragmentContext();
	}

	interface ProvidedPresenter {
		void download(String url);

		void setView(RequiredView view);

		void setModel(ProvidedModel model);
	}

	interface RequiredPresenter {
		Context getContext();
	}

	interface ProvidedModel {
		void download(TaskInfo taskInfo);
	}
}
