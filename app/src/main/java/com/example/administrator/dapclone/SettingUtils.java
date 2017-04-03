package com.example.administrator.dapclone;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 03/27/2017.
 */

public class SettingUtils {

	public static int getIntSettings(String key, int defaultValue) {
		return PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).getInt
				(key, defaultValue);
	}
}
