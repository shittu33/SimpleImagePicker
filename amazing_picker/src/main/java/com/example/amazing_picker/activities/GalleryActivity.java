package com.example.amazing_picker.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.SelectableViewHolder;
import com.example.amazing_picker.adapters.DirectoryAdapter;
import com.example.amazing_picker.adapters.ImagesRecylerAdapter;
import com.example.amazing_picker.models.Folder_image;
import com.example.amazing_picker.models.Model_images;
import com.example.amazing_picker.models.Selectable_image;
import com.example.amazing_picker.utilities.ImageCursorLoaderUtils;
import com.example.amazing_picker.utilities.View_Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_ALREADY_GRANTED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PERMISSION_REQUEST_NEEDED;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;
import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.getImagesFrom;
import static com.example.amazing_picker.utilities.SimpleImagePicker.DATA_KEY;
import static com.example.amazing_picker.utilities.SimpleImagePicker.IS_CANCEL;
import static com.example.amazing_picker.utilities.SimpleImagePicker.IS_OKAY;
import static com.example.amazing_picker.utilities.SimpleImagePicker.RESULT_CODE_CANCEL;
import static com.example.amazing_picker.utilities.SimpleImagePicker.RESULT_CODE_SUCCESS;
import static com.example.amazing_picker.utilities.SimpleImagePicker.RESULT_DATA_KEY;


//@SuppressWarnings("ResultOfMethodCallIgnored")
public class GalleryActivity extends AppCompatActivity implements
        View.OnClickListener
        , SelectableViewHolder.OnRecylerItemSelected,
        DirectoryAdapter.FolderCLickListener {
    public static final int GALLERY_REQUEST_CODE = 666;
    public static final String PICS_key = "pics";
    public static final int PREVIEW_REQUEST_CODE = 495;
    public static final int NORMAL_GALLERY_SPAN_COUNT = 3;
    public static final int EXPANDED_SPAN_COUNT = 4;
    public static final String THEME_KEY = "theme";
    private RecyclerView dirView, images_RecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ImagesRecylerAdapter imagesRecylerAdapter;
    private ArrayList<Selectable_image> imageList = new ArrayList<>();
    private ImageButton picker_nav;
    private Guideline guideline3;
    private ArrayList<Folder_image> folder_imageList = new ArrayList<>();
    private ArrayList<Model_images> folders_and_images = new ArrayList<>();
    boolean folder_is_visible = false;
    private TextView num_txt;
    private com.github.clans.fab.FloatingActionButton viewBtn, comfirmBtn;
    private TextView folder_title;
    private ImageButton cancel_picker;
    private PickerTheme pickerTheme;
    public static final String MULTI_SELECT_KEY = "multi_select";
    private boolean isMultiSelection;
    int resultCodeSuccess;
    int resultCodeCancel;
    String resultDataKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        pickerTheme = (PickerTheme) intent.getSerializableExtra(THEME_KEY);
        isMultiSelection = intent.getBooleanExtra(MULTI_SELECT_KEY, true);
        resultCodeSuccess = intent.getIntExtra(IS_OKAY, RESULT_CODE_SUCCESS);
        resultCodeCancel = intent.getIntExtra(IS_CANCEL, RESULT_CODE_CANCEL);
        resultDataKey = intent.getStringExtra(DATA_KEY);
        if (pickerTheme == PickerTheme.light)
            setTheme(R.style.GalleryLightTheme);
        else if (pickerTheme == PickerTheme.dark)
            setTheme(R.style.GalleryDarkTheme);
        setContentView(R.layout.picker_recycler_layout);
        init();
        Log.i(PIC_TAG, "started");
        AutohidePickerNavs();
    }

    //Initializations...
    private void init() {
        initViews();
        initAdapters();
    }

    private void initAdapters() {
        LoadFoldersAndImages();
        setUpPickerImagesAdapter(imageList);
    }

    private void initViews() {
        guideline3 = findViewById(R.id.guideline3);
        picker_nav = findViewById(R.id.picker_nav);
        comfirmBtn = findViewById(R.id.confirm_btn);
        num_txt = findViewById(R.id.num_txt);
        cancel_picker = findViewById(R.id.cancel_picker);
        picker_nav.setOnClickListener(this);
        viewBtn = findViewById(R.id.view_btn);
        folder_title = findViewById(R.id.folder_title);
        ConstraintLayout constraintLayout = findViewById(R.id.picker_lay);
        images_RecyclerView = findViewById(R.id.img_recycler);
        dirView = findViewById(R.id.dir_recycler);
        viewBtn.setOnClickListener(this);
        comfirmBtn.setOnClickListener(this);
        cancel_picker.setOnClickListener(this);
        if (pickerTheme == PickerTheme.light)
            setButtonLight();
        else if (pickerTheme == PickerTheme.dark)
            setButtonDark();
    }


    public void setButtonDark() {
        cancel_picker.setColorFilter(Color.WHITE);
        picker_nav.setColorFilter(Color.WHITE);
    }

    public void setButtonLight() {
        cancel_picker.setColorFilter(Color.BLACK);
        picker_nav.setColorFilter(Color.BLACK);
    }

    public void setUpDirAdapter(ArrayList<Folder_image> dir_list) {
        DirectoryAdapter dirAdapter = new DirectoryAdapter(this, dir_list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, VERTICAL, false);
        dirView.setLayoutManager(layoutManager);
        dirView.setAdapter(dirAdapter);
    }

    public void setUpPickerImagesAdapter(ArrayList<Selectable_image> imageList) {
        imagesRecylerAdapter = new ImagesRecylerAdapter(this, imageList
                , this, isMultiSelection);
        gridLayoutManager = new GridLayoutManager(this, NORMAL_GALLERY_SPAN_COUNT, VERTICAL, false);
        images_RecyclerView.setLayoutManager(gridLayoutManager);
        images_RecyclerView.setAdapter(imagesRecylerAdapter);
    }

    private void clear_ImagesFromScreen() {
        if (!imageList.isEmpty()) {
            imageList.clear();
            if (imagesRecylerAdapter != null) {
                imagesRecylerAdapter.notifyDataSetChanged();
            }
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
        super.onBackPressed();
    }

    public static final int IMAGES_SELECTED = 3333;
    public static final int IMAGES_COPIED = 3233;
    public static final int NO_IMAGES_SELECTED = 4444;
    public static final int ACTION_CANCEL = 5555;


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picker_nav) {
            HideOrShow_DirectoryView();
        } else if (id == R.id.cancel_picker) {
            onBackPressed();
        } else if (id == R.id.confirm_btn) {
            List<Selectable_image> selectable_pics;
            if (isMultiSelection)
                selectable_pics = imagesRecylerAdapter.getIndexed_selectable_images();
            else
                selectable_pics = imagesRecylerAdapter.getSelectedItems();
            saveImagesFromSelectedList(selectable_pics);
        } else if (id == R.id.view_btn) {
            PreviewImages();
        }
    }

    private void PreviewImages() {
        if (imagesRecylerAdapter.getSelectedItems().isEmpty()) {
            Toast.makeText(this, "Select at least one Image", Toast.LENGTH_SHORT).show();
        } else {
            final Intent intent = new Intent(this, PreviewActivity.class);
            List<Selectable_image> selectable_pics;
            if (isMultiSelection)
                selectable_pics = imagesRecylerAdapter.getIndexed_selectable_images();
            else
                selectable_pics = imagesRecylerAdapter.getSelectedItems();
            intent.putExtra(PICS_key, (Serializable) selectable_pics);
//        intent.putExtra(PICS_key, imageList);
            Log.e(PIC_TAG, "onClick: ready to go preview");
            startActivityForResult(intent, PREVIEW_REQUEST_CODE);
        }
    }

    private void saveImagesFromSelectedList(List<Selectable_image> selectable_pics) {
        Intent result_intent = new Intent();
        if (!selectable_pics.isEmpty()) {
            ArrayList<String> selected_images = new ArrayList<>();
            for (Selectable_image selectable_image : selectable_pics) {
                selected_images.add(selectable_image.getSelectable_path());
            }
            result_intent.putStringArrayListExtra(RESULT_DATA_KEY, selected_images);
            setResult(resultCodeSuccess, result_intent);
        } else {
            setResult(resultCodeCancel);
        }
        finish();
    }

    int selected_count;

    @Override
    public void onImageSelected(Selectable_image image) {
        updateUiWhenSelected(image);
    }

    @Override
    public void onImageRadioSelected(Selectable_image image) {
        updateUiWhenSelected(image);
    }

    private void updateUiWhenSelected(Selectable_image image) {
        if (isMultiSelection)
            selected_count = imagesRecylerAdapter.getIndexed_selectable_images().size();
        else
            selected_count = imagesRecylerAdapter.getSelectedItems().size();
        Log.i(PIC_TAG, "onImageSelected: selected_count is " + selected_count);
        num_txt.setVisibility(View.VISIBLE);
        if (selected_count == 1) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is one");
        } else if (selected_count == 0) {
            Log.i(PIC_TAG, "onImageSelected: selected_count is zero");
            num_txt.setVisibility(View.GONE);
        }
        num_txt.setText(String.valueOf(selected_count));
        Log.i(PIC_TAG, image.getSelectable_path() + "->\n " + (image.isSelected() ? "is selected" : "not selected"));
    }

    @Override
    public void onFolderClick(String folder_name, int position, boolean is_pdf_folder) {
        folder_title.setText(folder_name);
        loadImagesFromFolder(folder_name);
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

    private void loadImagesFromFolder(String folder_name) {
        clear_ImagesFromScreen();
        for (String image_path :
                (getImagesFrom(folders_and_images, folder_name))) {
            imageList.add(new Selectable_image(image_path, false));
        }
        setUpPickerImagesAdapter(imageList);
//        num_txt.setVisibility(View.GONE);
        updateUiWhenSelected(imageList.get(0));
    }
    //Navigations
    private void AutohidePickerNavs() {
        images_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !viewBtn.isShown() && !comfirmBtn.isShown()) {
                    View_Utils.ShowView_with_ZoomOut((ViewGroup) comfirmBtn.getParent());
                    viewBtn.setVisibility(View.VISIBLE);
                    comfirmBtn.setVisibility(View.VISIBLE);
                    num_txt.setVisibility(View.VISIBLE);
                } else if (dy > 0 && viewBtn.isShown() && comfirmBtn.isShown()) {
                    View_Utils.hideView_with_ZoomIn((ViewGroup) comfirmBtn.getParent());
                    viewBtn.setVisibility(View.GONE);
                    comfirmBtn.setVisibility(View.GONE);
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
            gridLayoutManager.setSpanCount(EXPANDED_SPAN_COUNT);
            gridLayoutManager.requestLayout();
        } else {
            picker_nav.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            ShowFolderRecycler();
            gridLayoutManager.setSpanCount(NORMAL_GALLERY_SPAN_COUNT);
            gridLayoutManager.requestLayout();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_REQUEST_CODE) {
            if (data != null) {
                final Serializable serializableExtra2 = data.getSerializableExtra(PICS_key);
                final ArrayList<Selectable_image> selected_pics = (ArrayList<Selectable_image>) serializableExtra2;
                Log.i(PIC_TAG, "first image is "
                        + imageList.get(0).getSelectable_path());
                switch (resultCode) {
                    case IMAGES_SELECTED:
                        assert selected_pics != null;
                        imagesRecylerAdapter.ClearSelected();
                        for (Selectable_image pic : selected_pics) {
                            imageList.set(imageList.indexOf(pic), pic);
                            imagesRecylerAdapter.addToSelected(pic);
                        }
                        if (!imageList.isEmpty()) {
                            updateUiWhenSelected(imageList.get(0));
                            Log.i(PIC_TAG, "first image is "
                                    + imageList.get(0).getSelectable_path());
                        }
                        imagesRecylerAdapter.notifyDataSetChanged();
                        break;
                    case IMAGES_COPIED:
                        assert selected_pics != null;
                        saveImagesFromSelectedList(selected_pics);
                        break;
                }
            }

        }
    }

}
