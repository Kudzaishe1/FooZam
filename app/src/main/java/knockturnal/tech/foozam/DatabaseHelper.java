package knockturnal.tech.foozam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Fruit.db";

    //table1
    private static final String TABLE_FRUIT = "Fruit";

    private static final String Col_1 = "Fruit_ID";
    private static final String Col_2 = "Fruit_name";
    private static final String Col_3 = "Fruit_images";

    //table2
    private static final String TABLE_FF = "Fruit_fact";

    private static final String Col_11 = "Fruit_ID";
    private static final String Col_22 = "Nutrient_ID";

    //table3
    private static final String TABLE_NUTRIENT = "Nutrient";

    private static final String Col_31 = "Nutrient_ID";
    private static final String Col_32 = "Nutrient_name";
    private static final String Col_33 = "Nutrient_value";

    //table4
    private static final String TABLE_SERVING = "Serving";

    private static final String Col_41 = "Serving_ID";
    private static final String Col_42 = "Serving_name";
    private static final String Col_43 = "Serving_Description";
    private static final String Col_44 = "Serving_image";

    //table5
    private static final String TABLE_FS = "Fruit_Serving";

    private static final String Col_51 = "Fruit_ID";
    private static final String Col_52 = "ServingID";

    //table6
    private static final String TABLE_RS = "Recipe_Step";

    private static final String Col_61 = "RS_ID";
    private static final String Col_62 = "Serving_ID";
    private static final String Col_63 = "RS_Description";


    SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FRUIT_Table = "CREATE TABLE " + TABLE_FRUIT + "("
                + Col_1 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL," + Col_2 + " TEXT, "
                + Col_3 + " TEXT" + ")";

        String CREATE_FF_TABLE = "CREATE TABLE " + TABLE_FF + "("
                + Col_11 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Col_22 + " INTEGER UNIQUE NOT NULL" + ")";

        String CREATE_NUTRIENT_TABLE = "CREATE TABLE " + TABLE_NUTRIENT + "("
                + Col_31 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, "
                + Col_32 + " TEXT," + Col_33 + " TEXT" + ")";

        String CREATE_SERVING_TABLE = "CREATE TABLE " + TABLE_SERVING + "("
                + Col_41 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, "
                + Col_42 + " TEXT, " + Col_43 + " TEXT" + "," + Col_44 + " TEXT" + ")";

        String CREATE_FS_TABLE = "CREATE TABLE " + TABLE_FS + "("
                + Col_51 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, "
                + Col_52 + " INTEGER UNIQUE NOT NULL" + ")";

        String CREATE_RS_TABLE = "CREATE TABLE " + TABLE_RS + "("
                + Col_61 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, "
                + Col_62 + " INTEGER UNIQUE NOT NULL, "
                + Col_63 + " TEXT" + ")";

        db.execSQL(CREATE_FRUIT_Table);
        db.execSQL(CREATE_FF_TABLE);
        db.execSQL(CREATE_NUTRIENT_TABLE);
        db.execSQL(CREATE_SERVING_TABLE);
        db.execSQL(CREATE_FS_TABLE);
        db.execSQL(CREATE_RS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_FRUIT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_FS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NUTRIENT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SERVING);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_FS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_RS);
        onCreate(db);
    }

    public void insert_data(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Col_2, "Apple");
        value.put(Col_3, "");

        value.put(Col_2, "Banana");
        value.put(Col_3, "");

        value.put(Col_2, "Strawberry");
        value.put(Col_3, "");

        db.insert(TABLE_FRUIT, null, value);

        ContentValues value2 = new ContentValues();
        value2.put(Col_32, "Calories  \nCarbohydrates  \nProtein  \nPotassium  \nSodium  \nFat");
        value2.put(Col_33, "52  \n14 g \n0.3 g \n107 mg \n1 mg \n0.2 g");
        db.insert(TABLE_NUTRIENT, null, value2);

        value2.put(Col_32, "Calories  \nCarbohydrates  \nProtein  \nPotassium  \nSodium  \nFat");
        value2.put(Col_33, "89  \n23 g \n1.1 g \n358 mg \n1 mg \n0.3 g");
        db.insert(TABLE_NUTRIENT, null, value2);

        value2.put(Col_32, "Calories  \nCarbohydrates  \nProtein  \nPotassium  \nSodium  \nFat");
        value2.put(Col_33, "33  \n8 g \n0.7 g \n153 mg \n1 mg \n0.3 g");
        db.insert(TABLE_NUTRIENT, null, value2);

        ContentValues value3 = new ContentValues();
        value3.put(Col_42, "Apple and vanilla tart");
        value3.put(Col_43,"https://www.bbcgoodfood.com/recipes/flat-apple-vanilla-tart");
        value3.put(Col_44 , "https://i.ibb.co/qjZsfCk/apple-and-vanilla-tart.jpg" );

        value3.put(Col_42, "dorset apple traybake");
        value3.put(Col_43, "https://www.bbcgoodfood.com/recipes/2044/dorset-apple-traybake");
        value3.put(Col_44, "https://i.ibb.co/KLyBMrr/recipe-image2.jpg");

        value3.put(Col_42, "cobnut apple loaf cake");
        value3.put(Col_43, "https://www.bbcgoodfood.com/recipes/cobnut-apple-loaf-cake");
        value3.put(Col_44, "https://i.ibb.co/JQ9DrvB/cobnut-and-apple-loaf.jpg");

        value3.put(Col_42, "apple_smoothie");
        value3.put(Col_43, "https://foodviva.com/smoothie-recipes/apple-smoothie-recipe/");
        value3.put(Col_44, "https://i.ibb.co/zVmMtRj/apple-smoothie-recipe.jpg");

        value3.put(Col_42, "banana cake maple caramel sauce");
        value3.put(Col_43, "https://www.bbcgoodfood.com/recipes/upside-down-banana-cake-maple-caramel-sauce");
        value3.put(Col_44, "https://i.ibb.co/K0XBLcF/banana-cake.jpg");

        value3.put(Col_42, "banana loaf");
        value3.put(Col_43, "https://www.bbcgoodfood.com/recipes/brilliant-banana-loaf");
        value3.put(Col_44, "https://i.ibb.co/p6qGZCT/recipe-image-legacy-id-1273522-8.jpg");

        value3.put(Col_42, "banana bread butter pudding");
        value3.put(Col_43, "https://www.bbcgoodfood.com/recipes/banana-bread-butter-pudding");
        value3.put(Col_44, "https://i.ibb.co/fMCxgcN/recipe-image-legacy-id-1273494-7.jpg");

        value3.put(Col_42, "banana smoothie recipe");
        value3.put(Col_43, "https://www.inspiredtaste.net/19907/simple-banana-smoothie-recipe/");
        value3.put(Col_44, "https://i.ibb.co/5vpj3v3/Banana-Smoothie-Recipe-4-1200.jpg");

        value3.put(Col_42, "strawberry-smoothie");
        value3.put(Col_43, "https://ifoodreal.com/strawberry-smoothie/");
        value3.put(Col_44, "https://i.ibb.co/DGZ7J5t/strawberry-smoothie-6.jpg");

        db.insert(TABLE_SERVING, null, value3);
        db.close();
    }


    public Cursor display_data(int Key){

        SQLiteDatabase db = this.getReadableDatabase ();

            if(Key == 1)
            {
                String query = "SELECT * FROM " + TABLE_NUTRIENT + " WHERE " + Col_22 + "= " + 1;
                Cursor result = db.rawQuery(query, null);
                return result;
            }
            else if(Key == 2)
            {
                String query = "SELECT * FROM " + TABLE_NUTRIENT + " WHERE " + Col_22 + "= " + 2;
                Cursor result = db.rawQuery(query, null);
                return result;
            }

            else if(Key == 3)
            {
                String query = "SELECT * FROM " + TABLE_NUTRIENT + " WHERE " + Col_22 + "= " + 3;
                Cursor result = db.rawQuery(query, null);
                return result;
            }
        return null;
    }

    public Cursor Display_Recipes(int Key){

        SQLiteDatabase db = this.getReadableDatabase ();

        if(Key == 1)
        {
            String query = "SELECT * FROM " + TABLE_SERVING + " WHERE " + Col_41 + "= " + 1;
            Cursor result = db.rawQuery(query, null);
            return result;
        }
        else if(Key == 2)
        {
            String query = "SELECT * FROM " + TABLE_SERVING + " WHERE " + Col_41 + "= " + 2;
            Cursor result = db.rawQuery(query, null);
            return result;
        }

        else if(Key == 3)
        {
            String query = "SELECT * FROM " + TABLE_SERVING + " WHERE " + Col_41 + "= " + 3;
            Cursor result = db.rawQuery(query, null);
            return result;
        }
        return null;
    }
}

