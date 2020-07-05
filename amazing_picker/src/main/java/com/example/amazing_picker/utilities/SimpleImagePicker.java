package com.example.amazing_picker.utilities;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.amazing_picker.activities.GalleryActivity;
import com.example.amazing_picker.activities.PickerTheme;

import static com.example.amazing_picker.activities.GalleryActivity.MULTI_SELECT_KEY;
import static com.example.amazing_picker.activities.GalleryActivity.THEME_KEY;

public class SimpleImagePicker {
    public static final String IS_OKAY = "R is okay";
    public static final String IS_CANCEL = "R is cancel";
    public static final String DATA_KEY = "r data key";
    private Activity activity;
    private Fragment fragment;
    private boolean isMultiSelection = true;
    private PickerTheme pickerTheme = PickerTheme.light;
    public static int PICKER_REQUEST_CODE = 254;
    public static int RESULT_CODE_SUCCESS = 111;
    public static int RESULT_CODE_CANCEL = 101;
    public static String RESULT_DATA_KEY = "result_key";
//    static SimpleImagePicker instance;

    private SimpleImagePicker(Activity context) {
        this.activity = context;
    }

    private SimpleImagePicker(Fragment context) {
        this.fragment = context;
    }

    public static SimpleImagePicker with(Activity context) {
        return new SimpleImagePicker(context);
    }

    public static SimpleImagePicker with(Fragment context) {
        return new SimpleImagePicker(context);
    }

    public SimpleImagePicker setMultipleSelection(boolean isMultipleSelection) {
        this.isMultiSelection = isMultipleSelection;
        return this;
    }

    public SimpleImagePicker setTheme(PickerTheme pickerTheme) {
        this.pickerTheme = pickerTheme;
        return this;
    }

    public SimpleImagePicker setRequestCode(int code) {
        PICKER_REQUEST_CODE = code;
        return this;
    }

    public SimpleImagePicker setResultCodeSuccess(int code) {
        RESULT_CODE_SUCCESS = code;
        return this;
    }

    public SimpleImagePicker setResultCodeCancel(int code) {
        RESULT_CODE_CANCEL = code;
        return this;
    }

    public SimpleImagePicker setResultDataKey(String key) {
        RESULT_DATA_KEY = key;
        return this;
    }

    public void start() {
        if (activity != null) {
            final Intent intent = new Intent(activity.getApplicationContext(), GalleryActivity.class);
            intent.putExtra(THEME_KEY, pickerTheme);
            intent.putExtra(MULTI_SELECT_KEY, isMultiSelection);
            intent.putExtra(IS_OKAY, RESULT_CODE_SUCCESS);
            intent.putExtra(IS_CANCEL, RESULT_CODE_CANCEL);
            intent.putExtra(DATA_KEY, RESULT_DATA_KEY);
            activity.startActivityForResult(intent, PICKER_REQUEST_CODE);
        } else if (fragment != null) {
            final Intent intent = new Intent(fragment.requireContext(), GalleryActivity.class);
            intent.putExtra(THEME_KEY, pickerTheme);
            intent.putExtra(MULTI_SELECT_KEY, isMultiSelection);
            intent.putExtra(IS_OKAY, RESULT_CODE_SUCCESS);
            intent.putExtra(IS_CANCEL, RESULT_CODE_CANCEL);
            intent.putExtra(DATA_KEY, RESULT_DATA_KEY);
            fragment.startActivityForResult(intent, PICKER_REQUEST_CODE);
        } else {
            try {
                throw new Exception("failed to get the context");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
