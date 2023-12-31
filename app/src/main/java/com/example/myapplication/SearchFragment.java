package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import com.example.myapplication.ml.Model;
import com.theartofdev.edmodo.cropper.CropImage;

public class SearchFragment extends Fragment {
    private RecyclerView rcvFish;
    private FishAdapter fishAdapter;
    private SearchView searchView;
    private ImageButton buttonAddPhoto;
    private ImageButton buttonSelectPhoto;
    private View view;
    private FragmentActivity activity;
    private Context context;
    final static int imageSize = 256;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        if (activity == null)
            Log.d("SearchFragment", "onCreate: activity is null");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            context = view.getContext();
            rcvFish = view.findViewById(R.id.rcv_fish);
            searchView = view.findViewById(R.id.searchView);
            buttonAddPhoto = view.findViewById(R.id.btn_add_photo);
            buttonSelectPhoto = view.findViewById(R.id.btn_select_photo);
        } else
            Log.d("SearchFragment", "onCreateView: view is null");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rcvFish.setLayoutManager(linearLayoutManager);

        fishAdapter = new FishAdapter(this.context , Fish_Item.allFishes);
        rcvFish.setAdapter(fishAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcvFish.addItemDecoration(itemDecoration);

        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fishAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fishAdapter.getFilter().filter(newText);
                return false;
            }
        });

        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                Log.d("SearchFragment", "buttonAddPhoto is clicked");
                currentPhotoPath = "";

                String[] requiredPermissions = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                boolean grantedAllPermissions = true;
                for (String permission : requiredPermissions) {
                    if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_DENIED)
                        grantedAllPermissions = false;
                }

                if (!grantedAllPermissions) {
                    ActivityCompat.requestPermissions(
                            activity,
                            requiredPermissions,
                            100
                    );
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(
                                view.getContext(),
                                "com.example.android.fileprovider",
                                photoFile
                        );
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        takePictureActivityResultLauncher.launch(takePictureIntent);
                    }
                }
            }

            final ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                File capturedImage = new File(currentPhotoPath);

                                if (capturedImage.exists()) {
                                    Uri contentUri = Uri.fromFile(capturedImage);
                                    cropImage(contentUri);
                                    galleryAddPic();
                                }
                            }
                        }
                    });
        });

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                selectPictureActivityResultLauncher.launch(chooserIntent);
            }

            final ActivityResultLauncher<Intent> selectPictureActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // There are no request codes
                                Intent data = result.getData();
                                Uri contentUri = Objects.requireNonNull(data).getData();
                                if (contentUri != null)
                                    cropImage(contentUri);
                            }
                        }
                    });
        });
        return view;
    }

    private void cropImage(Uri imageUri) {
        Intent cropImageIntent = CropImage.activity(imageUri).getIntent(requireContext());
        cropImageResultLauncher.launch(cropImageIntent);
    }

    final ActivityResultLauncher<Intent> cropImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                            Uri imageUri = cropResult.getUri();
                            if (imageUri != null) {
                                try {
                                    Bitmap image = MediaStore.Images.Media.getBitmap(
                                            view.getContext().getContentResolver(),
                                            imageUri
                                    );
                                    Pair<Fish_Item, String> prediction = classifyImage(image, getContext());
                                    if (prediction != null)
                                        showResult(image, prediction);
                                    else
                                        Log.d("SearchFragment", "cropImageResultLauncher: prediction is null");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else
                                Log.d("SearchFragment", "cropImageResultLauncher: imageUri is null");
                        } else
                            Log.d("SearchFragment", "cropImageResultLauncher: data is null");
                    }
                }
            });

    private void showResult(Bitmap imageBitmap, Pair<Fish_Item, String> predictedResult) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment showResultFragment = new ShowResultFragment(imageBitmap, predictedResult.first, predictedResult.second);
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, showResultFragment)
                .addToBackStack(null)
                .commit();
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "FindYourDriedFish"
        );
        boolean storageDirExist = storageDir.exists();
        if (!storageDirExist) {
            Log.d("SearchFragment", "createImageFile: storageDir not exists");
            storageDirExist = storageDir.mkdirs();
        }

        if (storageDirExist) {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }

        return null;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        view.getContext().sendBroadcast(mediaScanIntent);
    }

    @SuppressLint("DefaultLocale")
    public static Pair<Fish_Item, String> classifyImage(Bitmap image, Context context) {
        try {
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            Model model = Model.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];

            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = Fish_Item.getClassLabelList(Fish_Item.allFishes);
            String predictedClass = classes[maxPos];

            Fish_Item returnFish = null;
            String confidence = "0.0%";

            for (Fish_Item fish : Fish_Item.allFishes) {
                if (fish.isLabel(predictedClass)) {
                    returnFish = fish;
                    confidence = String.format("%.1f%%", maxConfidence * 100);
                }
            }

            return new Pair<>(returnFish, confidence);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SearchFragment() {
        super(R.layout.search_activity);
    }
}
