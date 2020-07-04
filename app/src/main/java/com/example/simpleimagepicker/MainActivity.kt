package com.example.simpleimagepicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.amazing_picker.activities.PickerTheme
import com.example.amazing_picker.utilities.SimpleImagePicker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        }


    fun lightClick(view: View) {
        SimpleImagePicker.with(this)
            .setMultipleSelection(true)
            .setTheme(PickerTheme.light)
            .setRequestCode(MY_PICKER_REQUEST_CODE)
            .build();

    }
    fun darkClick(view: View) {
        SimpleImagePicker.with(this)
            .setMultipleSelection(true)
            .setTheme(PickerTheme.dark)
            .setRequestCode(MY_PICKER_REQUEST_CODE)
            .build();

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== MY_PICKER_REQUEST_CODE){
            Toast.makeText(this,"you are welcome!",LENGTH_SHORT).show();
        }
    }
    companion object{
        const val MY_PICKER_REQUEST_CODE=232;
    }
}
