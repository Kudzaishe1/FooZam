package knockturnal.tech.foozam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends AppCompatActivity {

    ImageView myImageView;
    Button galleryButton;
    Button pred_Button;
    int Key_2;

    private String MODEL_PATH ="mobilenet_v2_1.0_224.tflite";
    private String LABEL_PATH ="Labels.txt";
    private List<String> Labels = null;
    private Interpreter tflite;
    private ByteBuffer ImageData = null;
    private int Dimensions = 224;
    private final int[] intValues = new int[224*224];
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;
    private double prob = 0;
    private int WEIGHT = 1000;
    final double ACCEPTANCE_VALUE = 0.90;
    private float[][] labelProbArray = null;

    private static final int IMAGE_PICK_CODE = 0;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        try {
            Labels = loadLabelList(GalleryActivity.this);
            Log.d("TAG","Labels have been loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        pred_Button = findViewById(R.id.predict_button);

        //Code for the image viewer and button
        myImageView = findViewById(R.id.selectedImage);

        galleryButton = findViewById(R.id.btnChoosePicture);

        // Code for the button
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        // Permission not granted, request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        // show popup for runtime permission
                        ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else
                {
                    pickImageFromGallery();
                }


            }
        });

        pred_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Key_2 != -2)
                {
                    Intent intent = new Intent(GalleryActivity.this, NutritionActivity.class);
                    intent.putExtra("Key", Key_2);
                    startActivity(intent);
                }
                else
                    { Intent intent1 = new Intent(GalleryActivity.this, Error_Page.class);
                        startActivity(intent1);
                    }
            }

        });

        // Bottom Navigation view code

        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        Intent intent1 = new Intent(GalleryActivity.this, LandingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.ic_scan:
                        Intent intent2 = new Intent(GalleryActivity.this, ScanActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_gallery:
                        break;
                }
                return true;
            }
        });
    }

    // Functions for picking image from gallery
    private void pickImageFromGallery(){
        //Intent to pick Photo
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // Condition specific functions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_CODE == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission was granted
                pickImageFromGallery();
            }
            else{
                //permission was denied
                Toast.makeText(this, "Permission denied....!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException
    {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //Open output labels
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labels = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();
        return labels;
    }

    public void SetIntepretor()
    {
        try {
            tflite = new Interpreter(loadModelFile(GalleryActivity.this));
            Log.d("TAG","Intepretor is set");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (ImageData == null) {
            return;
        }
        ImageData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < 224; ++i) {
            for (int j = 0; j < 224; ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
    }

    protected void addPixelValue(int pixelValue) {
        ImageData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        ImageData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        ImageData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            try {
                pred_Button.setVisibility(View.VISIBLE);
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                myImageView.setImageBitmap(selectedImage);
                selectedImage = Bitmap.createScaledBitmap(selectedImage,100,100, true);
                SetIntepretor();

                labelProbArray = new float[1][Labels.size()];

                ImageData = ByteBuffer.allocateDirect(4 *Dimensions * Dimensions * 3);
                ImageData.order(ByteOrder.nativeOrder());

                /////////Classify Image

                convertBitmapToByteBuffer(selectedImage);
                tflite.run(ImageData,labelProbArray);

                int[] positions = {949,955,950};
                for(int x = 0; x < labelProbArray[0].length;x++)
                {
                    for(int y= 0; y < positions.length;y++)
                    {
                        if(labelProbArray[0][positions[y]] > prob && positions[y] == 949  && labelProbArray[0][positions[y]]*(WEIGHT*1.4) > ACCEPTANCE_VALUE )
                        {
                            Key_2 = 1;
                            prob = labelProbArray[0][positions[y]] *(WEIGHT*1.4);
                            continue;
                        }

                        if(labelProbArray[0][positions[y]] > prob && positions[y] == 955  && labelProbArray[0][positions[y]]*(WEIGHT*1.2) > ACCEPTANCE_VALUE )
                        {
                            Key_2 = 2;
                            prob = labelProbArray[0][positions[y]] *(WEIGHT*1.2);
                            continue;
                        }

                        if(labelProbArray[0][positions[y]] > prob && positions[y] == 950  && labelProbArray[0][positions[y]]*(WEIGHT*1.5) > ACCEPTANCE_VALUE )
                        {
                            Key_2 = 3;
                            prob = labelProbArray[0][positions[y]] * (WEIGHT*1.5);
                        }

                    }
                    if(prob == 0)
                    {
                        Key_2 = -2;
                       // Toast.makeText(this,"The KEY is weak bro : "+labelProbArray[0][positions[2]]*1000,Toast.LENGTH_LONG).show();
                    }
                    else if(prob != 0)
                    {
                        if(Key_2 == 1 &&(labelProbArray[0][positions[1]]*(WEIGHT*1.2) > ACCEPTANCE_VALUE
                                || labelProbArray[0][positions[2]]*(WEIGHT*1.5) > ACCEPTANCE_VALUE ))
                        {
                            Key_2 = -2;
                        }
                        if(Key_2 == 2 &&(labelProbArray[0][positions[0]]*(WEIGHT*1.4) > ACCEPTANCE_VALUE
                                || labelProbArray[0][positions[2]]*(WEIGHT*1.5) > ACCEPTANCE_VALUE ))
                        {
                            Key_2 = -2;
                        }
                        if(Key_2 == 3 &&(labelProbArray[0][positions[1]]*(WEIGHT*1.2) > ACCEPTANCE_VALUE
                                || labelProbArray[0][positions[0]]*(WEIGHT*1.4) > ACCEPTANCE_VALUE ))
                        {
                            Key_2 = -2;
                        }
                    }
                }
                tflite.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            };
        }
    }


}

