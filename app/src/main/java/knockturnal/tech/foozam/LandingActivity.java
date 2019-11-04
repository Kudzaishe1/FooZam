package knockturnal.tech.foozam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;

public class LandingActivity extends AppCompatActivity {
    ImageView rare_fruit;
    ImageView top_picks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final TypedArray imgs = getResources().obtainTypedArray(R.array.apptour);
        final Random rand = new Random();
        int rndInt = rand.nextInt(imgs.length());
        int resID = imgs.getResourceId(rndInt, 0);
        super.onCreate(savedInstanceState);
        if ((getResources().getConfiguration().screenWidthDp) >= 325){
            setContentView(R.layout.landing_large);
            rare_fruit = findViewById(R.id.rare_fruit_image);
            rare_fruit.setImageResource(resID);
            top_picks = findViewById(R.id.top_picks_image);
            rndInt = rand.nextInt(imgs.length());
            resID = imgs.getResourceId(rndInt, 0);
            top_picks.setImageResource(resID);
        }else{
            setContentView(R.layout.activity_landing);
            rare_fruit = findViewById(R.id.rare_fruit_image);
            rare_fruit.setImageResource(resID);
        }

        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        break;
                    case R.id.ic_scan:
                        Intent intent2 = new Intent(LandingActivity.this, ScanActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_gallery:
                        Intent intent3 = new Intent(LandingActivity.this, GalleryActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }

    public int getRandomNumber(){
        double randomDouble = Math.random();
        randomDouble = randomDouble * 4 + 1;
        int randomInt = (int) randomDouble;
        return randomInt;

    }

}

