package com.example.amazing_picker.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.ExtendedViewPager;
import com.example.amazing_picker.Veiws.SelectableViewHolder;
import com.example.amazing_picker.adapters.DirectoryAdapter;
import com.example.amazing_picker.adapters.ImagesRecylerAdapter;
import com.example.amazing_picker.adapters.ImagePagerAdapter;
import com.example.amazing_picker.models.Folder_image;
import com.example.amazing_picker.models.Model_images;
import com.example.amazing_picker.models.Selectable_image;
import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;
import com.example.amazing_picker.utilities.View_Utils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_ALREADY_GRANTED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_REQUEST_NEEDED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.getImagesFrom;


//@SuppressWarnings("ResultOfMethodCallIgnored")
public class GalleryActivity extends AppCompatActivity implements View.OnClickListener
        , ViewPager.OnPageChangeListener
        , SelectableViewHolder.OnRecylerItemSelected,
        DirectoryAdapter.FolderCLickListener
        , OnSeekChangeListener {
    public static final int GALLERY_REQUEST_CODE = 666;
    private RecyclerView dirView, images_RecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ImagesRecylerAdapter images_recylerAdapter;
    private DirectoryAdapter dirAdapter;
    private ArrayList<Selectable_image> imageList;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;
    private ImageButton picker_nav;
    private Guideline guideline3;
    private ArrayList<Folder_image> folder_imageList;
    private ArrayList<Model_images> folders_and_images = new ArrayList<>();
    boolean folder_is_visible = false;
    private TextView num_txt;
    private ImageButton cancel_picker;
    private com.github.clans.fab.FloatingActionButton view_btn, comfirm_btn;

    //picker_preview
    private IndicatorSeekBar indicatorSeekBar;
    private ExtendedViewPager pager;
    private ImagePagerAdapter pagerAdapter;
    private ImageButton exit_btn;
    private Button copy_btn;
    private AppCompatRadioButton select_btn;
    private TextView title_tv;
    private Dialog imagePreview_dialog;
    public boolean is_to_check;
    private TextView folder_title;
    int prev_position = 0;
    int current_postion = 0;
    public String book_name;
    private AppCompatRadioButton select_btn_all;
    public static final String GALLERY_STATUS = "gallery_status";
    public static final String BOOK_NAME = "book_name";
    Enum gallery_type;

    //Initializations...
    private void init() {
        book_name = getIntent().getStringExtra(BOOK_NAME);
        gallery_type = (Enum) getIntent().getSerializableExtra(GALLERY_STATUS);
        initViews();
        initLists();
        initAdapters();
    }

    private void initAdapters() {
        LoadFoldersAndImages();
        Log.i(PIC_TAG, "got it");
        setUpPickerImagesAdapter(imageList);
    }

    private void initViews() {
        guideline3 = findViewById(R.id.guideline3);
        picker_nav = findViewById(R.id.picker_nav);
        comfirm_btn = findViewById(R.id.confirm_btn);
        num_txt = findViewById(R.id.num_txt);
        cancel_picker = findViewById(R.id.cancel_picker);
        picker_nav.setOnClickListener(this);
        view_btn = findViewById(R.id.view_btn);
        folder_title = findViewById(R.id.folder_title);
        constraintLayout = findViewById(R.id.picker_lay);
        images_RecyclerView = findViewById(R.id.img_recycler);
        dirView = findViewById(R.id.dir_recycler);
        view_btn.setOnClickListener(this);
        comfirm_btn.setOnClickListener(this);
        cancel_picker.setOnClickListener(this);
    }

    private void initLists() {
//        folder_pdfList = new ArrayList<>();
        folder_imageList = new ArrayList<>();
        imageList = new ArrayList<>();
//        pdfList = new ArrayList<>();
    }

    public void setUpDirAdapter(ArrayList<Folder_image> dir_list) {
        dirAdapter = new DirectoryAdapter(this, dir_list, this);
        @SuppressLint("WrongConstant")
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, VERTICAL, false);
        dirView.setLayoutManager(layoutManager);
        dirView.setAdapter(dirAdapter);
    }

    @SuppressLint("WrongConstant")
    public void setUpPickerImagesAdapter(ArrayList<Selectable_image> imageList) {
        images_recylerAdapter = new ImagesRecylerAdapter(this, imageList, this, true);
        gridLayoutManager = new GridLayoutManager(this, 3, VERTICAL, false);
        images_RecyclerView.setLayoutManager(gridLayoutManager);
        images_RecyclerView.setAdapter(images_recylerAdapter);
    }

    //Activity CallBacks

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_recycler_layout);
        init();
        Log.i(PIC_TAG, "started");
        AutohidePickerNavs();
    }

    private void clear_ImagesFromScreen() {
        imageList.clear();
        if (images_recylerAdapter != null) {
            images_recylerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GALLERY_REQUEST_CODE: {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.i(PIC_TAG, "Now we can set it up for good");
                        //noinspection unchecked
                        folders_and_images = ImageCursorLoaderUtils.getImages_and_Folders(GalleryActivity.this);
                        setUpDirAdapter(getDirectoriesFromFolder_ImageList());
                    } else {
                        Toast.makeText(GalleryActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (imagePreview_dialog != null && imagePreview_dialog.isShowing()) {
            ExitImagePreview();
        } else {
            super.onBackPressed();
        }
    }

    public static final int PDF_SELECTED = 2222;
    public static final int IMAGES_SELECTED = 3333;
    public static final int NO_IMAGES_SELECTED = 4444;
    public static final int ACTION_CANCEL = 5555;

    void ExitImagePreview() {
        if (imagePreview_dialog != null) {
            if (imagePreview_dialog.isShowing()) {
                imagePreview_dialog.dismiss();
            }
            ArrayList<Selectable_image> indexed_selectable_images = images_recylerAdapter.getIndexed_selectable_images();
            indexed_selectable_images.clear();
            indexed_selectable_images.addAll(pagerAdapter.getTmp_selected_images());
            images_recylerAdapter.notifyDataSetChanged();
            selected_count = images_recylerAdapter.getIndexed_selectable_images().size();
            num_txt.setText(String.valueOf(selected_count));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.exit_btn) {
            ExitImagePreview();
        } else if (id == R.id.copy_btn) {
            //copy images
            List<Selectable_image> selectable_pics = images_recylerAdapter.getIndexed_selectable_images();
            saveImagesFromSelectedList(selectable_pics);
        } else if (id == R.id.picker_nav) {
            HideOrShow_DirectoryView();
        } else if (id == R.id.cancel_picker) {
            onBackPressed();
        } else if (id == R.id.confirm_btn) {
            List<Selectable_image> selectable_pics = images_recylerAdapter.getIndexed_selectable_images();
            saveImagesFromSelectedList(selectable_pics);
        } else if (id == R.id.view_btn) {
            if (images_recylerAdapter.getSelectedItems().isEmpty()) {
                Toast.makeText(this, "Select at least one Image", Toast.LENGTH_SHORT).show();
            } else {
                View pager_layout = LayoutInflater.from(this).inflate(R.layout.picker_preview_pager, null);
                ShowImagePreviewDialog(pager_layout);
            }
        } else if (id == R.id.radio_btn) {
            SelectOrDeselect_PreviewImage();
        } else if (id == R.id.radio_btn_all) {
            Toast.makeText(this, "not available yet", Toast.LENGTH_SHORT).show();
            SelectOrDeselect_All_Images();
        }
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

    private void saveImagesFromSelectedList(List<Selectable_image> selectable_pics) {
        Intent result_intent = new Intent();
        if (!selectable_pics.isEmpty()) {
            ArrayList<String> selected_images = new ArrayList<>();
            for (Selectable_image selectable_image : selectable_pics) {
                selected_images.add(selectable_image.getSelectable_path());
            }
            result_intent.putStringArrayListExtra("pics", selected_images);
            setResult(IMAGES_SELECTED, result_intent);
        } else {
            setResult(NO_IMAGES_SELECTED);
        }
        finish();
    }

    int selected_count;

    @Override
    public void onImageSelected(Selectable_image image) {
        selected_count = images_recylerAdapter.getSelectedItems().size();
        Log.i(PIC_TAG, "onImageSelected: selected_count is " + selected_count);
        num_txt.setVisibility(View.VISIBLE);
        if (selected_count == 1) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is one");
        } else if (selected_count == 0) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is zero");
            num_txt.setVisibility(View.GONE);
        }
        num_txt.setText(String.valueOf(selected_count));
        Log.i(PIC_TAG, "onImageSelected: num_text is set");
        Log.i(PIC_TAG, image.getSelectable_path() + "->\n " + (image.isSelected() ? "is selected" : "not selected"));
    }

    @Override
    public void onFolderClick(String folder_name, int position, boolean is_pdf_folder) {
        folder_title.setText(folder_name);
        Load_ImagesFromFolder(folder_name);
    }

    //View Pager CallBacks
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        prev_position = position;
        indicatorSeekBar.setMax(images_recylerAdapter.getIndexed_selectable_images().size());
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

    boolean is_all_selected = false;

    private void SelectOrDeselect_All_Images() {
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
            Toast.makeText(this, "all deselected", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "all selected", Toast.LENGTH_SHORT).show();
        }
        pagerAdapter.updateSelectedNo(tmp_selectable_images.size());
        pagerAdapter.notifyDataSetChanged();
    }

    private void SelectOrDeselect_Pdf_Page() {
//        int current_adapter_index = pdf_pager.getCurrentItem();
//        ArrayList<SelectablePdf_page> selectablePdf_pages = pdf_pager_adapter.getSelectablePdf_pages();
//        ArrayList<SelectablePdf_page> tmp_selected_pages = pdf_pager_adapter.getTempSelected_PdfPages();
//        if (selectablePdf_pages.get(current_adapter_index).is_page_selected()) {
//            do_check_pdf_page = false;
//        } else if (!selectablePdf_pages.get(current_adapter_index).is_page_selected()) {
//            do_check_pdf_page = true;
//        }
//        select_btn.setChecked(do_check_pdf_page);
//        select_btn.setText(do_check_pdf_page ? "Deselect" : "Select");
//        selectablePdf_pages.get(current_adapter_index).set_page_selected(do_check_pdf_page);
//        SelectablePdf_page current_selectable_page = selectablePdf_pages.get(current_adapter_index);
//        if (do_check_pdf_page && !tmp_selected_pages.contains(current_selectable_page)) {
//            tmp_selected_pages.add(current_selectable_page);
//        } else if (!do_check_pdf_page) {
//            tmp_selected_pages.remove(current_selectable_page);
//        }
//        pdf_pager_adapter.updateSelectedNo(tmp_selected_pages.size());
//        pdf_pager_adapter.notifyDataSetChanged();
    }

    //Image Picker preview helper methods...
    private void SelectOrDeselect_PreviewImage() {
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
//        images_recyler_adapter.onImageSelected(selectable_images.get(current_adapter_index));
        pagerAdapter.notifyDataSetChanged();

    }

    private void ShowImagePreviewDialog(View pager_layout) {
        pager = pager_layout.findViewById(R.id.view_pager);
        exit_btn = pager_layout.findViewById(R.id.exit_btn);
        copy_btn = pager_layout.findViewById(R.id.copy_btn);
        title_tv = pager_layout.findViewById(R.id.title_tv);
        select_btn = pager_layout.findViewById(R.id.radio_btn);
        select_btn_all = pager_layout.findViewById(R.id.radio_btn_all);
        indicatorSeekBar = pager_layout.findViewById(R.id.indicator_seekbar);
        indicatorSeekBar.setOnSeekChangeListener(this);
        copy_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        select_btn_all.setOnClickListener(this);
        Log.i(PIC_TAG, "first image is " + images_recylerAdapter.getIndexed_selectable_images().get(0).getSelectable_path());
        pagerAdapter = new ImagePagerAdapter(this,
                images_recylerAdapter.getIndexed_selectable_images());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);
        title_tv.setText(pagerAdapter.getPageTitle(0));

        if (images_recylerAdapter.getIndexed_selectable_images().size() > 0) {
            indicatorSeekBar.setVisibility(View.VISIBLE);
        }
        indicatorSeekBar.setMin(1);
        indicatorSeekBar.setThumbAdjustAuto(false);
        imagePreview_dialog = new AlertDialog.Builder(this).setView(pager_layout).create();
        imagePreview_dialog.show();
        imagePreview_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                pager.clearOnPageChangeListeners();
                ExitImagePreview();
            }
        });

    }

    public void LoadFoldersAndImages() {
        if (ImageCursorLoaderUtils.isPermissionGranted(GalleryActivity.this) == PERMISSION_ALREADY_GRANTED) {
            Log.i(PIC_TAG, "granted");
            folders_and_images = ImageCursorLoaderUtils.getImages_and_Folders(this);
            setUpDirAdapter(getDirectoriesFromFolder_ImageList());
        } else if (ImageCursorLoaderUtils.isPermissionGranted(GalleryActivity.this) == PERMISSION_REQUEST_NEEDED) {
            ImageCursorLoaderUtils.Request_for_permission(this, GALLERY_REQUEST_CODE);
        }
    }

    public ArrayList<Folder_image> getDirectoriesFromFolder_ImageList() {
        imageList.clear();
        folder_imageList.clear();
        for (Model_images model_image : folders_and_images) {
            folder_imageList.add(
                    new Folder_image(model_image.getStr_folder(),
                            model_image.getAl_imagepath().get(0)));
        }
        return folder_imageList;
    }

    //Image/Pdf Loading ...
    private void Load_ImagesFromFolder(String folder_name) {
        if (!imageList.isEmpty()) {
            imageList.clear();
        }
        for (String image_path :
                (getImagesFrom(folders_and_images, folder_name))) {
            imageList.add(new Selectable_image(image_path, false));
        }
        setUpPickerImagesAdapter(imageList);
    }

    //Navigations
    private void AutohidePickerNavs() {
        images_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !view_btn.isShown() && !comfirm_btn.isShown()) {
                    View_Utils.ShowView_with_ZoomOut((ViewGroup) comfirm_btn.getParent());
                    view_btn.setVisibility(View.VISIBLE);
                    comfirm_btn.setVisibility(View.VISIBLE);
                    num_txt.setVisibility(View.VISIBLE);
                } else if (dy > 0 && view_btn.isShown() && comfirm_btn.isShown()) {
                    View_Utils.hideView_with_ZoomIn((ViewGroup) comfirm_btn.getParent());
                    view_btn.setVisibility(View.GONE);
                    comfirm_btn.setVisibility(View.GONE);
                    num_txt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void hideFolderRecycler() {
        dirView.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline3.getLayoutParams();
        params.guidePercent = 0.01f;
        guideline3.setLayoutParams(params);
//        constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(R.id.img_recycler, ConstraintSet.LEFT, R.id.picker_lay, ConstraintSet.LEFT);
//        constraintSet.applyTo(constraintLayout);
    }

    private void ShowFolderRecycler() {

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline3.getLayoutParams();
        params.guidePercent = 0.29f;
        guideline3.setLayoutParams(params);
//        constraintSet = new ConstraintSet();
//        constraintSet.clone(constraintLayout);
//        constraintSet.connect(R.id.img_recycler, ConstraintSet.LEFT, R.id.guideline3, ConstraintSet.LEFT);
//        constraintSet.applyTo(constraintLayout);
        dirView.setVisibility(View.VISIBLE);
    }

    private void HideOrShow_DirectoryView() {
        folder_is_visible = !folder_is_visible;
        if (folder_is_visible) {
            picker_nav.setImageResource(R.drawable.hamburger);
            hideFolderRecycler();
//            if (is_pdf_folder) {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            } else {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            }
            gridLayoutManager.setSpanCount(4);
            gridLayoutManager.requestLayout();
        } else {
            picker_nav.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            ShowFolderRecycler();
//            if (is_pdf_folder) {
//                gridLayoutManager.setSpanCount(GridLayoutManager.DEFAULT_SPAN_COUNT);
//            } else {
//                gridLayoutManager.setSpanCount(3);
//            }
            gridLayoutManager.setSpanCount(3);
            gridLayoutManager.requestLayout();
        }


    }


    public ArrayList<Selectable_image> getImageList() {
        return imageList;
    }

    public Button getCopy_btn() {
        return copy_btn;
    }

    public AppCompatRadioButton getSelect_btn() {
        return select_btn;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
