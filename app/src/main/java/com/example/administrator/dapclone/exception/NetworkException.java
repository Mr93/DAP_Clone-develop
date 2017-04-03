package com.example.administrator.dapclone.exception;

import android.util.Log;

/**
 * Created by Administrator on 03/22/2017.
 */

public class NetworkException extends Exception {
	private static final String TAG = NetworkException.class.getSimpleName();

	public NetworkException(String message) {
		super(message);
		Log.e(TAG, "NetworkException: " + message);
	}
}
