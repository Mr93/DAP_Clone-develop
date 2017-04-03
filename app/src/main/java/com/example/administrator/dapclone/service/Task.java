package com.example.administrator.dapclone.service;

import android.content.Intent;
import android.util.Log;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.SubTaskInfo;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 03/27/2017.
 */

public class Task extends Thread {
	private static final String TAG = Task.class.getSimpleName();
	private TaskInfo taskInfo;
	private boolean isRunning = false;
	private TaskManager taskManager;
	BlockingQueue<SubTask> downloadingThread, pendingThread, errorThread;
	private static final int MAX_THREAD = 16;
	static final long PART_SIZE = 256000;
	private int redownloadErrorTime = 1;

	public Task(TaskInfo taskInfo, TaskManager taskManager) {
		this.taskInfo = taskInfo;
		this.taskManager = taskManager;
		pendingThread = new LinkedBlockingQueue<>();
		downloadingThread = new LinkedBlockingQueue<>();
		errorThread = new LinkedBlockingQueue<>();
	}

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	@Override
	public void run() {
		super.run();
		updateListThread();
		isRunning = true;
		try {
			while (isRunning) {
				updateDownloadingList();
				boolean isAllTaskDead = true;
				for (SubTask task : downloadingThread) {
					if(task.getState() != State.TERMINATED){
						isAllTaskDead = false;
						if (task.getState() == State.NEW) {
							task.start();
						}
					}
				}
				Log.d(TAG, "run: check size: " + isAllTaskDead);
				Log.d(TAG, "run check size: " + downloadingThread.size() + "//" + pendingThread.size() + "//" +
						errorThread.size() + "//" + redownloadErrorTime);
				if (downloadingThread.size() == 0) {
					isRunning = false;
					Log.d(TAG, "run: error size " + errorThread.size());
					if (errorThread.size() == 0) {
						taskInfo.status = ConstantValues.STATUS_COMPLETED;
						DBHelper.getInstance().updateTask(taskInfo);
						taskManager.taskCompleted(this);
					} else {
						taskInfo.status = ConstantValues.STATUS_ERROR;
						DBHelper.getInstance().updateTask(taskInfo);
						taskManager.taskError(this);
					}
				} else {
					if(isAllTaskDead && pendingThread.isEmpty() && redownloadErrorTime == 0){
						isRunning = false;
						taskInfo.status = ConstantValues.STATUS_ERROR;
						DBHelper.getInstance().updateTask(taskInfo);
						taskManager.taskError(this);
					}else {
						Thread.sleep(1000);
					}
				}
			}
		} catch (InterruptedException e) {

		}
	}

	private void updateListThread() {
		if (taskInfo.taskId == -1) {
			createNewListSubTask();
		} else {
			ArrayList<SubTaskInfo> listSubTaskInfo = DBHelper.getInstance().getAllSubTask(taskInfo.taskId);
			taskInfo.status = ConstantValues.STATUS_DOWNLOADING;
			DBHelper.getInstance().updateTask(taskInfo);
			if (listSubTaskInfo.size() != 0) {
				getOldListSubTask(listSubTaskInfo);
			} else {
				createNewListSubTask();
			}
		}
	}

	private void createNewListSubTask() {
		long temp = 0;
		while (taskInfo.size - temp > 0) {
			long start = temp;
			long end = temp + PART_SIZE - 1;
			if (end > taskInfo.size - 1) {
				end = taskInfo.size - 1;
			}
			temp = end + 1;
			SubTaskInfo subTaskInfo = new SubTaskInfo();
			subTaskInfo.start = start;
			subTaskInfo.end = end;
			subTaskInfo.taskId = taskInfo.taskId;
			subTaskInfo.status = ConstantValues.STATUS_PENDING;
			SubTask subTask = new SubTask(this, subTaskInfo);
			pendingThread.offer(subTask);
		}
	}


	private void getOldListSubTask(ArrayList<SubTaskInfo> listSubTaskInfo) {
		for (SubTaskInfo subTaskInfo : listSubTaskInfo) {
			if (ConstantValues.STATUS_ERROR.equalsIgnoreCase(subTaskInfo.status)) {
				errorThread.offer(new SubTask(this, subTaskInfo));
			} else if (ConstantValues.STATUS_PENDING.equalsIgnoreCase(subTaskInfo.status)) {
				pendingThread.offer(new SubTask(this, subTaskInfo));
			} else if (ConstantValues.STATUS_DOWNLOADING.equalsIgnoreCase(subTaskInfo.status)) {
				downloadingThread.offer(new SubTask(this, subTaskInfo));
			}
		}
	}


	private void updateDownloadingList() {
		int offset = MAX_THREAD - downloadingThread.size();
		Log.d(TAG, "updateDownloadingList: download queue size " + downloadingThread.size());
		if (pendingThread.isEmpty() && downloadingThread.isEmpty() && redownloadErrorTime > 0 && !errorThread.isEmpty()) {
			for (SubTask subTask : errorThread) {
				errorThread.remove(subTask);
				subTask.getSubTaskInfo().status = ConstantValues.STATUS_PENDING;
				SubTask newSubTask = new SubTask(this, subTask.getSubTaskInfo());
				pendingThread.offer(newSubTask);
				DBHelper.getInstance().updateSubTask(subTask.getSubTaskInfo(), taskInfo.taskId);
			}
			redownloadErrorTime = redownloadErrorTime - 1;
			Log.d(TAG, "updateDownloadingList: update error " + redownloadErrorTime);
		}
		for (int i = 0; i < offset; i++) {
			SubTask subTask = pendingThread.poll();
			if (subTask != null) {
				subTask.getSubTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingThread.offer(subTask);
				DBHelper.getInstance().updateSubTask(subTask.getSubTaskInfo(), taskInfo.taskId);
			}
		}
	}

	public synchronized void onThreadDone(SubTask subTask) {
		Log.d(TAG, "onThreadDone: " + downloadingThread.contains(subTask));
		if (downloadingThread.remove(subTask)) {
			taskInfo.processedSize = taskInfo.processedSize + (int) (subTask.getSubTaskInfo().end - subTask
					.getSubTaskInfo().start + 1);
			DBHelper.getInstance().updateTask(taskInfo);
			updateDownloadingList();
			Intent intent = new Intent();
			intent.setAction(ConstantValues.ACTION_UPDATE_TASK);
			intent.putExtra(ConstantValues.FILE_INFO, taskInfo);
			MyApplication.getAppContext().sendBroadcast(intent);
		}
	}


	public synchronized void onThreadError(SubTask subTask) {
		Log.d(TAG, "onThreadError: " + downloadingThread.contains(subTask));
		if (downloadingThread.remove(subTask)) {
			errorThread.offer(subTask);
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
	}
}
