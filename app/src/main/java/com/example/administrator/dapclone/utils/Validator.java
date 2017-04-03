package com.example.administrator.dapclone.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.exception.NetworkException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 03/21/2017.
 */

public class Validator {

	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String VIDEO = "video";
	public static final String IMAGE = "image";
	public static final String MUSIC = "audio";

	private static final String TAG = Validator.class.getSimpleName();
	private static String[] extensionList = {
			"png",
			"jpeg",
			"bmp",
			"mp4",
			"avi",
			"jpg",
			"pdf",
			"mp3",
			"txt"
	};

	public static int getDrawableIdByExtension(String url) {
		String mimeTye = getMimeTyeFromExtension(url);
		if (mimeTye.contains(VIDEO)) {
			return R.drawable.ic_video_library_black_36dp;
		} else if (mimeTye.contains(MUSIC)) {
			return R.drawable.ic_library_music_black_36dp;
		} else if (mimeTye.contains(IMAGE)) {
			return R.drawable.ic_collections_black_36dp;
		} else {
			return R.drawable.ic_android_black_36dp;
		}
	}

	public static String getMimeTyeFromExtension(String url) {
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(url));
	}

	public static boolean isValid(String url) throws NetworkException {
		boolean valid;
		if (url == null) {
			throw new NetworkException("url null");
		} else if (url.equalsIgnoreCase("")) {
			throw new NetworkException("url blank");
		} else if (!URLUtil.isValidUrl(url)) {
			throw new NetworkException("url is invalid");
		} else if (!isValidExtension(getExtension(url))) {
			throw new NetworkException("unsupported extension");
		} else {
			valid = true;
		}
		return valid;
	}

	public static boolean isValidExtension(String extension) {
		for (String supportExtension : extensionList) {
			if (supportExtension.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

	public static String getExtension(String url) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		Log.d(TAG, "getExtension: " + extension);
		return extension;
	}

	public static String getProtocol(String stringUrl) {
		String protocol = "";
		try {
			URL url = new URL(stringUrl);
			protocol = url.getProtocol();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return protocol;
	}

	public static String getFileNameFromUrl(String stringUrl) {
		Log.d(TAG, "getFileNameFromUrl: " + URLUtil.guessFileName(stringUrl, null, null));
		return URLUtil.guessFileName(stringUrl, null, null);
	}

	public static String addProtocol(String stringUrl) {
		String value = HTTP + "://" + stringUrl;
		return value;
	}

	public static String getFileExtension(File file) {
		return MimeTypeMap.getFileExtensionFromUrl(file.getName());
	}

	public static boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context
					.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;
	}
}
