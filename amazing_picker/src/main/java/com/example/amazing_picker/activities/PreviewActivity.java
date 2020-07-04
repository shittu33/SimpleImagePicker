package com.example.amazing_picker.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.viewpager.widget.ViewPager;

import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.ExtendedViewPager;
import com.example.amazing_picker.adapters.ImagePagerAdapter;
import com.example.amazing_picker.models.Selectable_image;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.amazing_picker.activities.GalleryActivity.IMAGES_COPIED;
import static com.example.amazing_picker.activities.GalleryActivity.IMAGES_SELECTED;
import static com.example.amazing_picker.activities.GalleryActivity.PICS_key;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;

public class PreviewActivity extends AppCompatActivity implements
        OnSeekChangeListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private IndicatorSeekBar indicatorSeekBar;
    private ExtendedViewPager pager;
    private ImagePagerAdapter pagerAdapter;
    private ImageButton exit_btn;
    private Button copy_btn;
    private AppCompatRadioButton select_btn;
    private TextView title_tv;
    public boolean is_to_check;
    int prev_position = 0;
    int current_postion = 0;
    private AppCompatRadioButton select_btn_all;
    private boolean is_all_selected;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.previewStyle);
        setContentView(R.layout.picker_preview_pager);
        init();
    }

    private void init() {
        initView();
        initAdapter();

    }

    ArrayList<Selectable_image> selected_pics;

    private void initAdapter() {
        final Intent intent = getIntent();
        if (intent != null) {
            selected_pics = (ArrayList<Selectable_image>) intent.getSerializableExtra(PICS_key);
            Log.i(PIC_TAG, "first image is "
                    + selected_pics.get(0).getSelectable_path());
            pagerAdapter = new ImagePagerAdapter(this,
                    selected_pics);
            pager.setAdapter(pagerAdapter);
            pager.addOnPageChangeListener(this);
            title_tv.setText(pagerAdapter.getPageTitle(0));
            if (selected_pics.size() > 0) {
                indicatorSeekBar.setVisibility(View.VISIBLE);
            }
            indicatorSeekBar.setMin(1);
            indicatorSeekBar.setThumbAdjustAuto(false);
        }
    }

    private void initView() {
        pager = findViewById(R.id.view_pager);
        exit_btn = findViewById(R.id.exit_btn);
        copy_btn = findViewById(R.id.copy_btn);
        title_tv = findViewById(R.id.title_tv);
        select_btn = findViewById(R.id.radio_btn);
        select_btn_all = findViewById(R.id.radio_btn_all);
        indicatorSeekBar = findViewById(R.id.indicator_seekbar);
        indicatorSeekBar.setOnSeekChangeListener(this);
        copy_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        select_btn_all.setOnClickListener(this);
    }


    @Override
    public void onSeeking(SeekParams seekParams) {
        if (pagerAdapter != null) {
            if (seekParams.fromUser) {
                pager.setCurrentItem(seekParams.progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

    }

    public Button getCopy_btn() {
        return copy_btn;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.exit_btn) {
            onBackPressed();
        } else if (id == R.id.copy_btn) {
            saveImagesFromSelectedList(selected_pics, true);
        } else if (id == R.id.radio_btn) {
            SelectOrDeselectImage();
        } else if (id == R.id.radio_btn_all) {
            SelectOrDeselectAllImages();
        }
    }

    @Override
    public void onBackPressed() {
        saveImagesFromSelectedList(selected_pics, false);
        super.onBackPressed();
    }

    private void saveImagesFromSelectedList(List<Selectable_image> selectable_pics, boolean is_copy) {
        Intent result_intent = new Intent();
        result_intent.putExtra(PICS_key, (Serializable) selectable_pics);
        if (is_copy) {
            setResult(IMAGES_COPIED, result_intent);
        } else {
            setResult(IMAGES_SELECTED, result_intent);
        }
//        }
        finish();
    }

    private void SelectOrDeselectAllImages() {
        ArrayList<Selectable_image> selectable_images = pagerAdapter.getImages();
        ArrayList<Selectable_image> tmp_selectable_images = pagerAdapter.getTmp_selected_images();
        is_all_selected = !select_btn_all.getText().equals(getString(R.string.select_all_txt));
        if (is_all_selected) {
            select_btn_all.setChecked(false);
            select_btn.setChecked(false);
            select_btn.setText(getString(R.string.select_page));
            for (int i = 0; i < selectable_images.size(); i++) {
                Selectable_image selectable_image = selectable_images.get(i);
                selectable_image.set_selected(false);
                tmp_selectable_images.remove(selectable_image);
            }
            select_btn_all.setText(getString(R.string.select_all_txt));
        } else {
            select_btn_all.setChecked(true);
            select_btn.setChecked(true);
            select_btn.setText(getString(R.string.deselect_page));
            for (int i = 0; i < selectable_images.size(); i++) {
                Selectable_image selectable_image = selectable_images.get(i);
                selectable_image.set_selected(true);
                if (!tmp_selectable_images.contains(selectable_image)) {
                    tmp_selectable_images.add(selectable_image);
                }
            }
            select_btn_all.setText(getString(R.string.deselect_all_txt));
        }
        pagerAdapter.updateSelectedNo(tmp_selectable_images.size());
        pagerAdapter.notifyDataSetChanged();
    }

    //Image Picker preview helper methods...
    private void SelectOrDeselectImage() {
        int current_adapter_index = pager.getCurrentItem();
        ArrayList<Selectable_image> selectable_images = pagerAdapter.getImages();
        ArrayList<Selectable_image> tmp_selectable_images = pagerAdapter.getTmp_selected_images();
        if (selectable_images.get(current_adapter_index).isSelected()) {
            is_to_check = false;
        } else if (!selectable_images.get(current_adapter_index).isSelected()) {
            is_to_check = true;
        }
        select_btn.setChecked(is_to_check);
        select_btn.setText(is_to_check ? "Deselect" : "Select");
        selectable_images.get(current_adapter_index).set_selected(is_to_check);
        Selectable_image current_selectable_image = selectable_images.get(current_adapter_index);
        if (is_to_check && !tmp_selectable_images.contains(current_selectable_image)) {
            tmp_selectable_images.add(current_selectable_image);
        } else if (!is_to_check) {
            tmp_selectable_images.remove(current_selectable_image);
        }
        pagerAdapter.updateSelectedNo(tmp_selectable_images.size());
        pagerAdapter.notifyDataSetChanged();

    }

    //View Pager CallBacks
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        prev_position = position;
        indicatorSeekBar.setMax(selected_pics.size());
        indicatorSeekBar.setProgress(position + 1);
    }

    @Override
    public void onPageSelected(int position) {
        current_postion = position;
        //image preview
        title_tv.setText(pagerAdapter.getPageTitle(position));
        boolean is_currentPage_selected = pagerAdapter.getImages().get(position).isSelected();
        select_btn.setChecked(is_currentPage_selected);
        select_btn.setText(is_currentPage_selected ? "Deselect" : "Select");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public AppCompatRadioButton getSelect_btn() {
        return select_btn;
    }

}
