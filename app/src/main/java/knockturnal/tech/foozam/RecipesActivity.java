package knockturnal.tech.foozam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RecipesActivity extends AppCompatActivity {


    // ImageViews declaration
    ImageView recipe_one_imageView;
    ImageView recipe_two_imageView;
    ImageView recipe_three_imageView;
    ImageView recipe_four_imageView;
    ImageView recipe_five_imageView;
    ImageView recipe_six_imageView;

    //TextViews declaration
    TextView recipe_one_textView;
    TextView recipe_two_textView;
    TextView recipe_three_textView;
    TextView recipe_four_textView;
    TextView recipe_five_textView;
    TextView recipe_six_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getResources().getConfiguration().screenWidthDp) > 325){
            setContentView(R.layout.activity_recipes_large);
        }else{
            setContentView(R.layout.activity_recipes);
        }

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
                        Intent intent1 = new Intent(RecipesActivity.this, LandingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.ic_scan:
                        Intent intent2 = new Intent(RecipesActivity.this, ScanActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_gallery:
                        Intent intent3 = new Intent(RecipesActivity.this, GalleryActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });

    }


    //Image redirects
    public void goToUrl(View view) {
        String url = "https://www.olivemagazine.com/recipes/baking-and-desserts/best-ever-strawberry-recipes/";
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
