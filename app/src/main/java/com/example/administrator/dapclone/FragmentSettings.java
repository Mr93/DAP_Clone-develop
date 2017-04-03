package com.example.administrator.dapclone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Created by Administrator on 03/21/2017.
 */

public class FragmentSettings extends android.support.v4.app.Fragment implements View.OnClickListener {

	private static final String TAG = FragmentSettings.class.getSimpleName();
	private LinearLayout settingNumberThread;
	private TextView summarySettingNumberThread;
	private final int MAX_VALUE = 100;
	private final int MIN_VALUE = 1;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		settingNumberThread = (LinearLayout) view.findViewById(R.id.setting_number_thread);
		summarySettingNumberThread = (TextView) view.findViewById(R.id.summary_setting_number_thread);
		settingNumberThread.setOnClickListener(this);
		summarySettingNumberThread.setText(String.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt
				(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD)));
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setting_number_thread:
				getDialogSettingNumberThread().show();
				break;
			default:
				break;
		}
	}

	private Dialog getDialogSettingNumberThread() {
		final NumberPicker numberPicker = createNumberPickerNumberThread();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getContext().getResources().getString(R.string.pref_title_number_thread_download));
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt(ConstantValues
						.SETTING_NUMBER_THREAD_DOWNLOAD, numberPicker.getValue()).commit();
				summarySettingNumberThread.setText(String.valueOf(numberPicker.getValue()));
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setView(numberPicker);
		return builder.create();
	}

	@NonNull
	private NumberPicker createNumberPickerNumberThread() {
		final NumberPicker numberPicker = new NumberPicker(getActivity());
		numberPicker.setMaxValue(MAX_VALUE);
		numberPicker.setMinValue(MIN_VALUE);
		numberPicker.setValue(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(ConstantValues
				.SETTING_NUMBER_THREAD_DOWNLOAD, ConstantValues.DEFAULT_NUMBER_THREAD_DOWNLOAD));
		return numberPicker;
	}
}
