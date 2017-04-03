package com.example.administrator.dapclone;

import android.os.Environment;

/**
 * Created by Administrator on 03/20/2017.
 */

public class ConstantValues {
	public final static String SETTING_NUMBER_THREAD_DOWNLOAD = "number_thread_download";
	public final static int DEFAULT_NUMBER_THREAD_DOWNLOAD = 8;
	public final static String FILE_INFO = "file_info";
	public final static String STATUS_DOWNLOADING = "downloading";
	public final static String STATUS_PENDING = "pending";
	public final static String STATUS_ERROR = "error";
	public final static String STATUS_COMPLETED = "completed";
	public final static String ACTION_NEW_TASK = "com.example.administrator.dapclone.newtask";
	public final static String ACTION_UPDATE_TASK = "com.example.administrator.dapclone.updatetask";
	public final static String ACTION_ERROR_TASK = "com.example.administrator.dapclone.errortask";
	public final static String ACTION_COMPLETE_TASK = "com.example.administrator.dapclone.completetask";
	public final static String CONTENT_TYPE = "content_type";
	public final static String PATH_ARGUMENT = "path_argument";
	public final static String DEFAULT_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();




}
