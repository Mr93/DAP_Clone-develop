package com.example.administrator.dapclone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.administrator.dapclone.utils.Validator;

import java.util.ArrayList;

/**
 * Created by Administrator on 03/27/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = DBHelper.class.getSimpleName();
	private static DBHelper instance;
	private static final String DATABASE_NAME = "MyDownloadDB.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TASK_TABLE_NAME = "task";
	private static final String SUB_TASK_TABLE_NAME = "sub_task";
	private static final String TASK_ID = "task_id";
	private static final String TASK_NAME = "task_name";
	private static final String TASK_URL = "task_url";
	private static final String TASK_SIZE = "task_size";
	private static final String PROCESSED_SIZE = "processed_size";
	private static final String STATUS = "status";
	private static final String IS_DOWNLOAD = "is_download";
	private static final String IS_MULTI_THREAD = "is_multi_thread";
	private static final String PATH = "path";
	private static final String SUB_TASK_ID = "sub_task_id";
	private static final String START_BYTE = "start_byte";
	private static final String END_BYTE = "end_byte";
	private static final String CREATE_TASK_TABLE = "create table " + TASK_TABLE_NAME + " (" + TASK_ID + " integer primary key, "
			+ TASK_NAME + " text, " + TASK_URL + " text, " + PATH + " text, " + IS_DOWNLOAD + " integer, " + TASK_SIZE + " integer, "
			+ PROCESSED_SIZE + " integer, " + STATUS + " text, " + IS_MULTI_THREAD + " integer)";
	private static final String CREATE_SUB_TASK_TABLE = "create table " + SUB_TASK_TABLE_NAME + " (" + SUB_TASK_ID + " integer primary key, "
			+ TASK_ID + " integer, " + STATUS + " text, " + START_BYTE + " integer, " + END_BYTE + " integer)";

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized static DBHelper getInstance() {
		if (instance == null) {
			instance = new DBHelper(MyApplication.getAppContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TASK_TABLE);
		db.execSQL(CREATE_SUB_TASK_TABLE);
		Log.d(TAG, "onCreate:");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public synchronized int insertTask(TaskInfo taskInfo) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = getTaskValues(taskInfo);
		return (int) sqLiteDatabase.insert(TASK_TABLE_NAME, null, contentValues);
	}

	public synchronized int insertSubTask(SubTaskInfo subTaskInfo, int taskId) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = getSubTaskValues(subTaskInfo, taskId);
		return (int) sqLiteDatabase.insert(SUB_TASK_TABLE_NAME, null, contentValues);
	}

	public synchronized int updateTask(TaskInfo taskInfo) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = getTaskValues(taskInfo);
		contentValues.put(TASK_ID, taskInfo.taskId);
		return sqLiteDatabase.update(TASK_TABLE_NAME, contentValues, TASK_ID + "  = ? ", new String[]{Integer.toString(taskInfo
				.taskId)});
	}

	public synchronized int updateSubTask(SubTaskInfo subTaskInfo, int taskId) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = getSubTaskValues(subTaskInfo, taskId);
		contentValues.put(SUB_TASK_ID, subTaskInfo.subTaskId);
		return sqLiteDatabase.update(SUB_TASK_TABLE_NAME, contentValues, SUB_TASK_ID + "  = ? ", new String[]{Integer.toString(subTaskInfo
				.subTaskId)});
	}

	public synchronized int deleteTaskById(int id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Log.d(TAG, "deleteNote: " + id);
		deleteSubTaskByTaskId(id);
		return sqLiteDatabase.delete(TASK_TABLE_NAME, TASK_ID + "  = ? ", new String[]{Integer.toString(id)});
	}

	public synchronized int deleteSubTaskByTaskId(int taskId) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		return sqLiteDatabase.delete(SUB_TASK_TABLE_NAME, TASK_ID + "  = ? ", new String[]{Integer.toString(taskId)});
	}

	/*public synchronized int deleteTaskByStatus(String status) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Log.d(TAG, "deleteNote: " + status);
		return sqLiteDatabase.delete(TASK_TABLE_NAME, STATUS + "  like ? ", new String[]{status});
	}*/

	public ArrayList<TaskInfo> getAllTask() {
		ArrayList<TaskInfo> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + TASK_TABLE_NAME, null);
		data.moveToFirst();
		while (data.isAfterLast() == false) {
			TaskInfo taskInfo = getTaskInfo(data);
			arrayList.add(taskInfo);
			data.moveToNext();
		}
		return arrayList;
	}

	public ArrayList<SubTaskInfo> getAllSubTask(int taskId) {
		ArrayList<SubTaskInfo> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + SUB_TASK_TABLE_NAME + " where " + TASK_ID + " = " + taskId + "", null);
		data.moveToFirst();
		while (data.isAfterLast() == false) {
			SubTaskInfo subTaskInfo = getSubTaskInfo(data);
			arrayList.add(subTaskInfo);
			data.moveToNext();
		}
		return arrayList;
	}

	public TaskInfo getTaskById(int id) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + TASK_TABLE_NAME + " where " + TASK_ID + " = " + id + "",
				null);
		Log.d(TAG, "getANote: " + "select * from " + TASK_TABLE_NAME + " where " + TASK_ID + " = " + id + "");
		data.moveToFirst();
		TaskInfo taskInfo = getTaskInfo(data);
		return taskInfo;
	}

	public ArrayList<TaskInfo> getTasksByStatus(String status) {
		ArrayList<TaskInfo> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + TASK_TABLE_NAME + " where " + STATUS + " like '" + status + "'",
				null);
		data.moveToFirst();
		while (data.isAfterLast() == false) {
			TaskInfo taskInfo = getTaskInfo(data);
			arrayList.add(taskInfo);
			data.moveToNext();
		}
		return arrayList;
	}

	public boolean checkTaskDownloadExisted(TaskInfo taskInfo) {
		ArrayList<SubTaskInfo> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + TASK_TABLE_NAME + " where " + TASK_NAME + " like '" + taskInfo.name
						+ "' and " + TASK_URL + " like '" + taskInfo.url + "'",
				null);
		data.moveToFirst();
		if (data == null || !data.moveToFirst()) {
			return false;
		}
		if (ConstantValues.STATUS_COMPLETED.equalsIgnoreCase(data.getString(data.getColumnIndex(STATUS))) ||
				ConstantValues.STATUS_ERROR.equalsIgnoreCase(data.getString(data.getColumnIndex(STATUS)))) {
			return false;
		} else {
			return true;
		}
	}

	public int getTaskIdByNameAndUrl(TaskInfo taskInfo) {
		ArrayList<SubTaskInfo> arrayList = new ArrayList<>();
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		Cursor data = sqLiteDatabase.rawQuery("select * from " + TASK_TABLE_NAME + " where " + TASK_NAME + " like '" + taskInfo.name
						+ "' and " + TASK_URL + " like '" + taskInfo.url + "'",
				null);
		data.moveToFirst();
		if (data == null || !data.moveToFirst()) {
			return -1;
		} else {
			return data.getInt(data.getColumnIndex(TASK_ID));
		}
	}

	@NonNull
	private TaskInfo getTaskInfo(Cursor data) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = data.getInt(data.getColumnIndex(TASK_ID));
		taskInfo.url = data.getString(data.getColumnIndex(TASK_URL));
		taskInfo.name = data.getString(data.getColumnIndex(TASK_NAME));
		taskInfo.path = data.getString(data.getColumnIndex(PATH));
		taskInfo.extension = Validator.getExtension(taskInfo.url);
		taskInfo.size = data.getInt(data.getColumnIndex(TASK_SIZE));
		taskInfo.processedSize = data.getInt(data.getColumnIndex(PROCESSED_SIZE));
		taskInfo.status = data.getString(data.getColumnIndex(STATUS));
		taskInfo.isMultiThread = data.getInt(data.getColumnIndex(IS_MULTI_THREAD)) == 1 ? true : false;
		taskInfo.isDownload = data.getInt(data.getColumnIndex(IS_DOWNLOAD)) == 1 ? true : false;
		return taskInfo;
	}

	@NonNull
	private SubTaskInfo getSubTaskInfo(Cursor data) {
		SubTaskInfo subTaskInfo = new SubTaskInfo();
		subTaskInfo.subTaskId = data.getInt(data.getColumnIndex(SUB_TASK_ID));
		subTaskInfo.start = data.getLong(data.getColumnIndex(START_BYTE));
		subTaskInfo.end = data.getLong(data.getColumnIndex(END_BYTE));
		subTaskInfo.status = data.getString(data.getColumnIndex(STATUS));
		subTaskInfo.taskId = data.getInt(data.getColumnIndex(TASK_ID));
		return subTaskInfo;
	}

	@NonNull
	private ContentValues getTaskValues(TaskInfo taskInfo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TASK_NAME, taskInfo.name);
		contentValues.put(TASK_URL, taskInfo.url);
		contentValues.put(PATH, taskInfo.path);
		contentValues.put(IS_DOWNLOAD, taskInfo.isDownload ? 1 : 0);
		contentValues.put(TASK_SIZE, taskInfo.size);
		contentValues.put(PROCESSED_SIZE, taskInfo.processedSize);
		contentValues.put(STATUS, taskInfo.status);
		contentValues.put(IS_MULTI_THREAD, taskInfo.isMultiThread ? 1 : 0);
		return contentValues;
	}

	@NonNull
	private ContentValues getSubTaskValues(SubTaskInfo subTaskInfo, int taskId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TASK_ID, taskId);
		contentValues.put(START_BYTE, subTaskInfo.start);
		contentValues.put(END_BYTE, subTaskInfo.end);
		contentValues.put(STATUS, subTaskInfo.status);
		return contentValues;
	}
}
