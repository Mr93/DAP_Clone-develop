package com.example.administrator.dapclone.view;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by Administrator on 03/20/2017.
 */

public class NumberPickerPreference extends DialogPreference {
	private static final String TAG = NumberPickerPreference.class.getSimpleName();
	private NumberPicker picker;
	private int number = 0;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
	}

	@Override
	protected View onCreateDialogView() {
		picker = new NumberPicker(getContext());
		picker.setMinValue(1);
		picker.setMaxValue(8);
		picker.setValue(number);
		return picker;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			picker.clearFocus();
			setValue(picker.getValue());
			callChangeListener(picker.getValue());
		}
	}


	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		setValue(restorePersistedValue ? getPersistedInt(number) : (Integer) defaultValue);
	}

	public void setValue(int value) {
		if (shouldPersist()) {
			persistInt(value);
		}
		if (value != number) {
			number = value;
			notifyChanged();
		}
	}
}
