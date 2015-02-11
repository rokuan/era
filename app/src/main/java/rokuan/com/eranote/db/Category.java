package rokuan.com.eranote.db;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Christophe on 17/01/2015.
 */
public class Category {
    private Integer id;
    private String name;
    private String description;
    //private String imagePath;
    private Bitmap image;
    private boolean modifiable;

    public Category(){

    }

    //public Category(String categoryName, String categoryDescription, String categoryImagePath){
    public Category(String categoryName, String categoryDescription){
        this.name = categoryName;
        this.description = categoryDescription;
        //this.imagePath = categoryImagePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Category buildFromCursor(Cursor result){
        Category cat = new Category();
        byte[] imgData;

        cat.setId(result.getInt(0));
        cat.setName(result.getString(1));
        cat.setDescription(result.getString(2));
        cat.setModifiable(result.getInt(4) != 0);

        imgData = result.getBlob(3);
        if(imgData != null) {
            cat.setImage(BitmapFactory.decodeByteArray(imgData, 0, imgData.length));
        }
        //cat.setImagePath(result.getString(3));

        return cat;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof Category) && (this.id == ((Category)o).getId());
    }

    @Override
    public String toString(){
        return this.name;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    private void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }
}
