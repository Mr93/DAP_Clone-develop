package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.TaskInfo;
import com.example.administrator.dapclone.utils.Validator;

import java.util.List;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.MyViewHolder> {

	private static final String TAG = DownloadListAdapter.class.getSimpleName();
	private List<TaskInfo> taskInfoList;
	private ProvidedPresenter providedPresenter;

	public DownloadListAdapter(List<TaskInfo> taskInfoList, ProvidedPresenter providedPresenter) {
		this.taskInfoList = taskInfoList;
		this.providedPresenter = providedPresenter;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_display, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		final TaskInfo taskInfo = taskInfoList.get(position);
		holder.title.setText(taskInfo.name);
		int progress = (int) ((((float) taskInfo.processedSize) * 100) / ((float) taskInfo.size));
		holder.progressText.setText(String.valueOf(progress) + "%");
		holder.progressBar.setProgress(progress);
		holder.avatar.setBackgroundResource(Validator.getDrawableIdByExtension(taskInfo.url));
		holder.layoutItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: " + taskInfo.processedSize + "/" + taskInfo.size);
				if (taskInfo.processedSize == taskInfo.size) {
					Log.d(TAG, "onClick: ");
					providedPresenter.showPreviewFile(taskInfo);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return taskInfoList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public ImageView avatar;
		public TextView title, progressText;
		public ProgressBar progressBar;
		public LinearLayout layoutItem;

		public MyViewHolder(View view) {
			super(view);
			this.layoutItem = (LinearLayout) view.findViewById(R.id.layout_item_recycle_view);
			this.avatar = (ImageView) view.findViewById(R.id.avatar);
			this.title = (TextView) view.findViewById(R.id.title_text);
			this.progressText = (TextView) view.findViewById(R.id.progress_text);
			this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		}
	}
}
