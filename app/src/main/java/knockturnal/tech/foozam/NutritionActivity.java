package knockturnal.tech.foozam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NutritionActivity extends AppCompatActivity {

    ImageView fruitView;
    TextView textView;
    TextView textView2;
    TextView nutritional_info_View;
    Button try_another_Button;
    Button get_recipes_button;
    int FLAG;
    String NutVal;
    Cursor result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        // Code for fruit image and nutritional info
        nutritional_info_View = findViewById(R.id.nutritional_info);

        // Code for Recipes button and explore button
        get_recipes_button = findViewById(R.id.explore_button);
        try_another_Button = findViewById(R.id.return_to_scan_button);


        // Code for Recipes Button
        get_recipes_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NutritionActivity.this, RecipesActivity.class);
                intent.putExtra("Key",FLAG);
                startActivity(intent);
            }
        });

        // Code for Try Another Button
        try_another_Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NutritionActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        int KEY = intent.getIntExtra("Key",-1);

        if(KEY == -2)
        {
            Toast.makeText(this,"This will be replaced by an error page, FRUIT NOT FOUND,for now it will show banana",Toast.LENGTH_LONG).show();
            KEY = 2;
        }
        FLAG = KEY;

       // Toast.makeText(this,""+KEY,Toast.LENGTH_LONG).show();
        DatabaseHelper db = new DatabaseHelper(this);
        db.insert_data();

        result = db.display_data(KEY);
        Cursor test = db.Display_Recipes(KEY);
        Display();

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
                        Intent intent1 = new Intent(NutritionActivity.this, LandingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.ic_scan:
                        Intent intent2 = new Intent(NutritionActivity.this, ScanActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_gallery:
                        Intent intent3 = new Intent(NutritionActivity.this, GalleryActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });

    }

        public void Display(){
            TextView textView1 = findViewById(R.id.textView);

            if(FLAG == 1)
            {
                textView1.setText("FRUIT NAME : APPLE");
            }
            if(FLAG == 2)
            {
                textView1.setText("FRUIT NAME : BANANA");
            }
            if(FLAG == 3)
            {
                textView1.setText("FRUIT NAME : STRAWBERRY");
            }

            textView = findViewById(R.id.nutritional_info);
            textView2 = findViewById(R.id.nutritional_info2);
            textView.invalidate();
            textView2.invalidate();

            if (result.moveToNext()){
                textView.setText(result.getString(1));
                textView2.setText(result.getString(2));
            }

    }

}
