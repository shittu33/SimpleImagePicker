 package com.example.simpleimagepicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amazing_picker.activities.PickerTheme
import com.example.amazing_picker.utilities.SimpleImagePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private var galleryAdapter: GalleryAdapter? = null
    private var pref: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences("pref_key", Context.MODE_PRIVATE)
        prefEditor = pref!!.edit()
        setTheme(pref!!.getSavedTheme())
        setContentView(R.layout.activity_main)
        setSupportActionBar(tool_bar)
        initViews()
        initAdapter()
    }

    private fun initViews() {
        if (pref!!.isDarkTheme()) {
            tool_bar.setTitleTextColor(Color.WHITE)
            BtnAdd.setColorFilter(Color.WHITE)
        } else if (pref!!.isLightTheme()) {
            tool_bar.setTitleTextColor(Color.BLACK)
            BtnAdd.setColorFilter(Color.BLACK)
            BtnAdd.imageAlpha = 180
        }
        BtnAdd.setOnClickListener {
            startSimplePicker()
        }
    }

    private fun initAdapter() {
        val gridLayoutManager: LinearLayoutManager = GridLayoutManager(
            this,
            3,
            LinearLayoutManager.VERTICAL,
            false
        )
        img_recycler.apply {
            layoutManager = gridLayoutManager
            galleryAdapter = GalleryAdapter(object : GalleryAdapter.ClickListener {
                override fun onClick(v: View?, path: String?, pos: Int) {
                    Toast.makeText(applicationContext, "image ${File(path!!).name} was clicked!", LENGTH_SHORT).show()
                }
            })
            adapter = galleryAdapter
        }
        if (galleryAdapter!!.currentList.isEmpty())
            BtnAdd.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_PICKER_REQUEST_CODE) {
            if (resultCode == CUSTOM_RESULT_CODE_SUCCESS) {
                val pics = data?.getStringArrayListExtra(CUSTOM_DATA_KEY)
                if (pics != null) {
                    galleryAdapter!!.submitList(pics)
                    BtnAdd.visibility = View.GONE
                }
            } else if (resultCode == CUSTOM_RESULT_CODE_CANCEL) {
                Toast.makeText(this, "nothing selected!", LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.img_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                startSimplePicker()
            }
            R.id.theme -> {
                if (pref!!.isDarkTheme()) {
                    pickerTheme = PickerTheme.light
                    prefEditor!!.saveAppTheme(R.style.LightAppTheme)
                    Log.e(TAG, "light theme")
                } else if (pref!!.isLightTheme()) {
                    pickerTheme = PickerTheme.dark
                    prefEditor!!.saveAppTheme(R.style.DarkAppTheme)
                    Log.e(TAG, "dark theme")
                }
                recreate()
            }
            R.id.more -> {
                val popupMenu = PopupMenu(
                    this,
                    tool_bar,
                    Gravity.END
                    /*,android.R.attr.popupMenuStyle,R.style.MenuStyle*/
                )
                popupMenu.apply {
                    inflate(R.menu.img_select_menu)
                    if (pref!!.isMultiSelection()) {
                        menu.findItem(R.id.multiSelect).isVisible = false
                        menu.findItem(R.id.single_select).isVisible = true
                    } else {
                        menu.findItem(R.id.multiSelect).isVisible = true
                        menu.findItem(R.id.single_select).isVisible = false
                    }
                    setOnMenuItemClickListener {
                        if (it.itemId == R.id.multiSelect) {
                            prefEditor!!.saveSelectionType(MULTI_SELECT)
                        } else {
                            prefEditor!!.saveSelectionType(SINGLE_SELECT)
                        }
                        true
                    }
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSimplePicker() {
        SimpleImagePicker.with(this)
            .setMultipleSelection(pref!!.isMultiSelection())
            .setTheme(pickerTheme)
            .setRequestCode(MY_PICKER_REQUEST_CODE)
            .setResultCodeSuccess(CUSTOM_RESULT_CODE_SUCCESS)
            .setResultCodeCancel(CUSTOM_RESULT_CODE_CANCEL)
            .setResultDataKey(CUSTOM_DATA_KEY)
            .start()
    }

    /**App Preferences*/
    private fun SharedPreferences.Editor.saveAppTheme(@StyleRes theme: Int) {
        putInt(THEME_KEY, theme)
        apply()
    }

    private fun SharedPreferences.Editor.saveSelectionType(type: Int) {
        putInt(TYPE_KEY, type)
        apply()
    }

    @StyleRes
    private fun SharedPreferences.getSavedTheme(): Int {
        return getInt(THEME_KEY, R.style.DarkAppTheme)
    }

    private fun SharedPreferences.getSelectionType(): Int {
        return getInt(TYPE_KEY, MULTI_SELECT)
    }

    private fun SharedPreferences.isDarkTheme(): Boolean {
        return getSavedTheme() == R.style.DarkAppTheme
    }

    private fun SharedPreferences.isLightTheme(): Boolean {
        return getSavedTheme() == R.style.LightAppTheme
    }

    private fun SharedPreferences.isSingleSelection(): Boolean {
        return getSelectionType() == SINGLE_SELECT
    }

    private fun SharedPreferences.isMultiSelection(): Boolean {
        return getSelectionType() == MULTI_SELECT
    }


    companion object {
        const val TAG = "MainActivity"
        const val MY_PICKER_REQUEST_CODE = 232
        const val MULTI_SELECT = 1
        const val SINGLE_SELECT = 0
        const val CUSTOM_DATA_KEY = "custom data key"
        const val THEME_KEY = "theme_key"
        const val TYPE_KEY = "type_key"
        const val CUSTOM_RESULT_CODE_SUCCESS = 171
        const val CUSTOM_RESULT_CODE_CANCEL = 161
        var pickerTheme: PickerTheme = PickerTheme.dark
    }
}
