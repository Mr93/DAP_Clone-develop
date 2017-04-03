package com.example.administrator.dapclone.service;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.DBHelper;
import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.SettingUtils;
import com.example.administrator.dapclone.TaskInfo;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 03/27/2017.
 */

public class TaskManager extends Thread {
	private static final String TAG = TaskManager.class.getSimpleName();
	BlockingQueue<Task> downloadingTask, pendingTask, errorTask;
	private static TaskManager instance;
	private boolean isRunning = false;
	private int reDownloadErrorTime = 1;


	private TaskManager() {
		downloadingTask = new LinkedBlockingQueue<>();
		pendingTask = new LinkedBlockingQueue<>();
		errorTask = new LinkedBlockingQueue<>();
	}

	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}

	public void resetTaskManager() {
		instance = new TaskManager();
	}

	private void setUpQueues() {
		getDownloadingQueue();
		getPendingQueue();
		getErrorQueue();
	}

	//get queue from DB
	private void getDownloadingQueue() {
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_DOWNLOADING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			downloadingTask.offer(new Task(taskInfoList.get(i), this));
		}
	}

	private void getPendingQueue() {
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_PENDING);
		for (int i = 0; i < taskInfoList.size(); i++) {
			if (SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD) > downloadingTask
					.size()) {
				Task task = new Task(taskInfoList.get(i), this);
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			} else {
				pendingTask.offer(new Task(taskInfoList.get(i), this));
			}
		}
	}

	private void getErrorQueue() {
		ArrayList<TaskInfo> taskInfoList = DBHelper.getInstance().getTasksByStatus(ConstantValues.STATUS_ERROR);
		for (int i = 0; i < taskInfoList.size(); i++) {
			errorTask.offer(new Task(taskInfoList.get(i), this));
		}
	}


	@Override
	public synchronized void start() {
		super.start();
		isRunning = true;
	}

	@Override
	public void run() {
		setUpQueues();
		try {
			while (isRunning) {
				updateToDownloadingQueue();
				for (Task task : downloadingTask) {
					if (task.getState() == State.NEW) {
						task.start();
					} else if (task.getState() == State.TERMINATED) {
						//taskError(task);
					}
				}
				if (downloadingTask.isEmpty()) {
					isRunning = false;
					reDownloadErrorTime = 1;
					synchronized (NetworkService.monitor) {
						try {
							NetworkService.monitor.wait();
						} catch (InterruptedException e) {

						}
					}
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void updateToDownloadingQueue() {
		int offset = SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD) -
				downloadingTask.size();
		if (pendingTask.isEmpty() && downloadingTask.isEmpty() && reDownloadErrorTime > 0 && !errorTask.isEmpty()) {
			for (Task task : errorTask) {
				errorTask.remove(task);
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				Task newTask = new Task(task.getTaskInfo(), this);
				pendingTask.offer(newTask);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			}
			reDownloadErrorTime = reDownloadErrorTime - 1;
			Log.d(TAG, "updateToDownloadingQueue: " + reDownloadErrorTime);
		}
		for (int i = 0; i < offset; i++) {
			Task task = pendingTask.poll();
			if (task != null) {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
				DBHelper.getInstance().updateTask(task.getTaskInfo());
			}
		}
	}


	public void addTask(TaskInfo taskInfo) {
		Task task = new Task(taskInfo, this);
		if (checkContain(taskInfo, errorTask)) {
			task = getTaskFromQueue(taskInfo, errorTask);
			errorTask.remove(task);
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD)) {
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				pendingTask.offer(task);
			} else {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				downloadingTask.offer(task);
			}
		} else if (!checkContain(taskInfo, downloadingTask) && !checkContain(taskInfo, pendingTask)) {
			if (downloadingTask.size() >= SettingUtils.getIntSettings(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD)) {
				task.getTaskInfo().status = ConstantValues.STATUS_PENDING;
				task.getTaskInfo().taskId = DBHelper.getInstance().insertTask(task.getTaskInfo());
				pendingTask.offer(task);
			} else {
				task.getTaskInfo().status = ConstantValues.STATUS_DOWNLOADING;
				task.getTaskInfo().taskId = DBHelper.getInstance().insertTask(task.getTaskInfo());
				downloadingTask.offer(task);
			}
			Intent intent = new Intent();
			intent.setAction(ConstantValues.ACTION_NEW_TASK);
			intent.putExtra(ConstantValues.FILE_INFO, taskInfo);
			MyApplication.getAppContext().sendBroadcast(intent);
		}
	}

	public boolean checkContain(TaskInfo taskInfo, BlockingQueue<Task> tasks) {
		for (Task task : tasks) {
			if (task.getTaskInfo().isDownload == taskInfo.isDownload &&
					task.getTaskInfo().name.equalsIgnoreCase(taskInfo.name) &&
					task.getTaskInfo().url.equalsIgnoreCase(taskInfo.url)) {
				return true;
			}
		}
		return false;
	}

	public Task getTaskFromQueue(TaskInfo taskInfo, BlockingQueue<Task> tasks) {
		for (Task task : tasks) {
			if (task.getTaskInfo().isDownload == taskInfo.isDownload &&
					task.getTaskInfo().name.equalsIgnoreCase(taskInfo.name) &&
					task.getTaskInfo().url.equalsIgnoreCase(taskInfo.url)) {
				return task;
			}
		}
		return null;
	}

	public synchronized void taskCompleted(Task task) {
		downloadingTask.remove(task);
		DBHelper.getInstance().deleteSubTaskByTaskId(task.getTaskInfo().taskId);
		updateToDownloadingQueue();
		Log.d(TAG, "taskCompleted: " + task.getTaskInfo().status);
		updateUI(task, ConstantValues.ACTION_COMPLETE_TASK);
	}

	public synchronized void taskError(Task task) {
		downloadingTask.remove(task);
		errorTask.offer(task);
		updateUI(task, ConstantValues.ACTION_ERROR_TASK);
	}

	private void updateUI(Task task, String actionErrorTask) {
		Intent intent = new Intent();
		intent.setAction(actionErrorTask);
		intent.putExtra(ConstantValues.FILE_INFO, task.getTaskInfo());
		MyApplication.getAppContext().sendBroadcast(intent);
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}
}
