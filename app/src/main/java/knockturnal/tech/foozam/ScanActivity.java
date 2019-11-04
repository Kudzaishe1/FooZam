package knockturnal.tech.foozam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class ScanActivity extends AppCompatActivity {
    private String MODEL_PATH ="mobilenet_v2_1.0_224.tflite";
    private String LABEL_PATH ="Labels.txt";
    private List<String> Labels = null;
    public static final String CODE = "VALUE";
    private Interpreter tflite;
    private ByteBuffer ImageData = null;
    private int Dimensions = 224;
    private int WEIGHT = 1000;
    final double ACCEPTANCE_VALUE = 0.93;
    private final int[] intValues = new int[224*224];
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;
    int top;
    private double prob = 0;
    private float[][] labelProbArray = null;
    Bitmap bitmap;
    ImageView imageView;
    Button predict_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        try {
            Labels = loadLabelList(ScanActivity.this);
            Log.d("TAG","Labels have been loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Code for Taking Picture of Fruit Button
        final Button btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);

        //Code for predict Button
        predict_Button = findViewById(R.id.predict_button);
        predict_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(top != -2)
                {
                    Intent intent = new Intent(ScanActivity.this, NutritionActivity.class);
                    intent.putExtra("Key", top);
                    startActivity(intent);
                }
                else
                { Intent intent1 = new Intent(ScanActivity.this, Error_Page.class);
                    startActivity(intent1);
                }

            }

        });

        // Code For Bottom Navigation View
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        Intent intent1 = new Intent(ScanActivity.this, LandingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.ic_scan:
                        break;
                    case R.id.ic_gallery:
                        Intent intent3 = new Intent(ScanActivity.this, GalleryActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
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
    public int get_top()
    {
        return top;
    }


    public void SetIntepretor()
    {
        try {
            tflite = new Interpreter(loadModelFile(ScanActivity.this));
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

    // This is where the photo is getting stored
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        predict_Button.setVisibility(View.VISIBLE);
        SetIntepretor();
        bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
        labelProbArray = new float[1][Labels.size()];
        bitmap = Bitmap.createScaledBitmap(bitmap,140,140, true);

        ImageData = ByteBuffer.allocateDirect(4 *Dimensions * Dimensions * 3);
        ImageData.order(ByteOrder.nativeOrder());
        convertBitmapToByteBuffer(bitmap);

        /////////Classify Image

        tflite.run(ImageData,labelProbArray);

        prob = 0;
        int[] positions = {949,955,950};
        for(int x = 0; x < labelProbArray[0].length;x++)
        {
            for(int y= 0; y < positions.length;y++)
            {
                if(labelProbArray[0][positions[y]] > prob && positions[y] == 949  && labelProbArray[0][positions[y]]*(WEIGHT*0.2) > ACCEPTANCE_VALUE )
                {
                    top = 1;
                    prob = labelProbArray[0][positions[y]] *(WEIGHT*0.2);
                    continue;
                }

                if(labelProbArray[0][positions[y]] > prob && positions[y] == 955  && labelProbArray[0][positions[y]]*(WEIGHT*6) > ACCEPTANCE_VALUE )
                {
                    top = 2;
                    prob = labelProbArray[0][positions[y]] *(WEIGHT*6);
                    continue;
                }

                if(labelProbArray[0][positions[y]] > prob && positions[y] == 950  && labelProbArray[0][positions[y]]*(WEIGHT*0.2) > ACCEPTANCE_VALUE )
                {
                    top = 3;
                    prob = labelProbArray[0][positions[y]] * (WEIGHT*0.2);
                    continue;
                }

                if(prob == 0)
                {
                    top = -2;
                    // Toast.makeText(this,"The KEY is weak bro : "+labelProbArray[0][positions[2]]*1000,Toast.LENGTH_LONG).show();
                }
                else if(prob != 0)
                {
                    if(top == 1 &&(labelProbArray[0][positions[1]]*(WEIGHT*4) > ACCEPTANCE_VALUE
                            || labelProbArray[0][positions[2]]*(WEIGHT*0.2) > ACCEPTANCE_VALUE ))
                    {
                        top = -2;
                    }
                    if(top == 2 &&(labelProbArray[0][positions[0]]*(WEIGHT*0.2) > ACCEPTANCE_VALUE
                            || labelProbArray[0][positions[2]]*(WEIGHT*1.5) > ACCEPTANCE_VALUE ))
                    {
                        top = -2;
                    }
                    if(top == 3 &&(labelProbArray[0][positions[1]]*(WEIGHT*4) > ACCEPTANCE_VALUE
                            || labelProbArray[0][positions[0]]*(WEIGHT*0.2) > ACCEPTANCE_VALUE ))
                    {
                        top = -2;
                    }
                }
            }
        }
        tflite.close();
    }
}
