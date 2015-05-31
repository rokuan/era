package rokuan.com.eranote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import rokuan.com.eranote.db.Category;
import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.utils.Utils;

/**
 * An activity which displays the content of a category
 * @author Lebeau Christophe
 */
public class CategoryActivity extends ActionBarActivity implements View.OnClickListener {
    private EraSQLiteOpenHelper db;

    private EditText categoryName;
    private ImageView categoryImage;
    private Button browseImage;

    private Category category;

    private boolean newElementMode;

    private boolean modified = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        db = new EraSQLiteOpenHelper(this);

        Bundle extras = this.getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EraSQLiteOpenHelper.CATEGORY_ID)) {
                int categoryId = extras.getInt(EraSQLiteOpenHelper.CATEGORY_ID);
                category = db.getCategory(categoryId);
            } else if(extras.containsKey("new")) {
                if(extras.getBoolean("new")) {
                    newElementMode = true;
                    category = new Category();
                }
            }
        }

        if(category == null){
            // TODO: an error occurred
        }

        categoryName = (EditText)findViewById(R.id.form_category_name);
        categoryImage = (ImageView)findViewById(R.id.form_category_image);
        browseImage = (Button)findViewById(R.id.form_category_browse_image);

        categoryName.setText(category.getName());
        categoryImage.setImageBitmap(category.getImage());

        browseImage.setOnClickListener(this);
    }

    @Override
    public void onPause(){
        db.close();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new EraSQLiteOpenHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save_category:
                if(categoryName.getText().toString().isEmpty()) {
                    // TODO: afficher une alert dialog
                    Toast.makeText(this, "Please name the category", Toast.LENGTH_SHORT).show();
                } else {
                    String oldName = category.getName();
                    String newName = category.getName();

                    category.setName(categoryName.getText().toString());
                    try {
                        category.setImage(((BitmapDrawable)categoryImage.getDrawable()).getBitmap());
                    }catch(Exception e){
                        Log.e("Era (Category bitmap)", e.getMessage());
                    }

                    if(!oldName.equals(newName) && db.categoryExists(category.getName())){
                        Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    if(newElementMode){
                        db.addCategory(category);
                    } else {
                        db.updateCategory(category);
                    }

                    this.setCategoryResult(true);
                    this.finish();
                }
                return true;
            case R.id.action_cancel_category:
                this.finish();
                return true;
            // TODO: autres menus
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCategoryResult(boolean ok){
        modified = (modified || ok);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", modified);
        this.setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.form_category_browse_image:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                this.startActivityForResult(intent, Code.PICK_CATEGORY_IMAGE_RESULT_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
            case Code.PICK_CATEGORY_IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    InputStream stream;

                    try {
                        stream = getContentResolver().openInputStream(data.getData());

                        Bitmap original = BitmapFactory.decodeStream(stream);
                        Bitmap resize = Utils.getScaledBitmap(original);
                        category.setImage(resize);
                        categoryImage.setImageBitmap(resize);
                    }catch(Exception e){
                        Log.e("Era - Category (Image)", e.toString());
                    }
                }
                break;
        }
    }
}
