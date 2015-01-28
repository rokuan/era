package rokuan.com.eranote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rokuan.com.eranote.R;
import rokuan.com.eranote.utils.Utils;

/**
 * Created by Christophe on 17/01/2015.
 */
public class EraSQLiteOpenHelper extends SQLiteOpenHelper {
    private Context context;

    private static final int NOTES = 0;
    private static final int CATEGORIES = 1;
    private static final int ATTACHMENTS = 2;

    private static final String DB_NAME = "era";
    private static final int DB_VERSION = 1;

    private static String[] tables = new String[]{
            "notes",
            "categories",
            "attachments"
    };

    public static final String NOTE_ID = "note_id";
    public static final String NOTE_TITLE = "note_title";
    public static final String NOTE_CONTENT = "note_content";
    public static final String NOTE_CATEGORY = "category_id";
    public static final String NOTE_FAVORITE = "note_favorite";
    public static final String NOTE_LAST_MODIF = "note_last_modif";

    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_DESCRIPTION = "category_descr";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String CATEGORY_MODIFIABLE = "category_modifiable";

    public static final String ATTACHMENT_ID = "attachment_id";
    public static final String ATTACHMENT_PATH = "attachment_path";
    public static final String ATTACHMENT_NOTE = "note_id";

    /*private String[][] columns = new String[][]{
            { NOTE_ID, NOTE_TITLE, NOTE_CONTENT, NOTE_CATEGORY },
            { CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION }
    };*/

    private static final String ATTACHMENT_QUERY = "CREATE TABLE " + tables[ATTACHMENTS] + "(" +
            ATTACHMENT_ID + " LONG PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            ATTACHMENT_PATH + " TEXT NOT NULL, " +
            ATTACHMENT_NOTE + " LONG, " +
            "FOREIGN KEY(" + ATTACHMENT_NOTE + ") REFERENCES " + tables[NOTES] + "(" + NOTE_ID + "), " +
            "UNIQUE(" + ATTACHMENT_PATH + ", " + ATTACHMENT_NOTE + ")" +
            ")";
    private static final String CATEGORY_QUERY = "CREATE TABLE " + tables[CATEGORIES] + "(" +
            CATEGORY_ID + " LONG PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            CATEGORY_NAME + " TEXT UNIQUE NOT NULL, " +
            CATEGORY_DESCRIPTION + " TEXT, " +
            CATEGORY_IMAGE + " BLOB, " +
            CATEGORY_MODIFIABLE + " INTEGER" +
            ")";
    private static final String NOTE_QUERY = "CREATE TABLE " + tables[NOTES] + "(" +
            NOTE_ID + " LONG PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            NOTE_TITLE + " TEXT, " +
            NOTE_CONTENT + " TEXT, " +
            NOTE_CATEGORY + " LONG, " +
            NOTE_FAVORITE + " INTEGER, " +
            NOTE_LAST_MODIF + " LONG, " +
            "FOREIGN KEY(" + NOTE_CATEGORY + ") REFERENCES " + tables[CATEGORIES] + "(" + CATEGORY_ID + ")" +
            ")";


    public EraSQLiteOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CATEGORY_QUERY);
        db.execSQL(NOTE_QUERY);
        db.execSQL(ATTACHMENT_QUERY);

        Category cat = new Category("Note", "Basic note");
        Bitmap categoryBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.memo);
        byte[] bmpData = Utils.getBitmapData(categoryBitmap);

        ContentValues values = new ContentValues();

        values.put(CATEGORY_NAME, cat.getName());
        values.put(CATEGORY_DESCRIPTION, cat.getDescription());
        //values.put(CATEGORY_IMAGE_PATH, cat.getImagePath());
        values.put(CATEGORY_IMAGE, bmpData);
        values.put(CATEGORY_MODIFIABLE, false);

        categoryBitmap.recycle();

        long catId = db.insert(tables[CATEGORIES], null, values);

        Note note = new Note("Welcome", "Nice to meet you !");
        values = new ContentValues();

        values.put(NOTE_TITLE, note.getTitle());
        values.put(NOTE_CONTENT, note.getContent());
        values.put(NOTE_CATEGORY, catId);
        values.put(NOTE_FAVORITE, false);
        values.put(NOTE_LAST_MODIF, new Date().getTime());

        db.insert(tables[NOTES], null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String tableName: tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }

        this.onCreate(db);
    }

    // NOTES
    public void addNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NOTE_TITLE, note.getTitle());
        values.put(NOTE_CONTENT, note.getContent());
        values.put(NOTE_CATEGORY, (note.getCategory() == null) ? null : note.getCategory().getId());
        // TODO: ajouter les pieces jointes
        values.put(NOTE_LAST_MODIF, new Date().getTime());

        long noteId = db.insert(tables[NOTES], null, values);
        note.setId(noteId);
        db.close();

        // TODO: Demarrer transaction ?
        for(Attachment att: note.getAttachments()){
            addAttachment(att);
        }
    }

    public void updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NOTE_TITLE, note.getTitle());
        values.put(NOTE_CONTENT, note.getContent());
        values.put(NOTE_CATEGORY, (note.getCategory() == null) ? null : note.getCategory().getId());
        values.put(NOTE_FAVORITE, note.isFavorite());
        values.put(NOTE_LAST_MODIF, new Date().getTime());

        db.update(tables[NOTES], values, NOTE_ID + " = ?", new String[]{ String.valueOf(note.getId()) });
        db.close();
    }

    public Note getNote(Long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[NOTES], null, NOTE_ID + " = ?", new String[]{ String.valueOf(id) }, null, null, null);
        Note n = null;

        if(results.getCount() > 0) {
            results.moveToFirst();

            long noteCategory = results.getLong(3);
            n = Note.buildFromCursor(results);
            Category cat = getCategory(noteCategory);
            n.setCategory(cat);
            List<Attachment> attachments = getNoteAttachments(n.getId());
            n.setAttachments(attachments);
        }

        results.close();
        db.close();
        return n;
    }

    public List<Attachment> getNoteAttachments(Long noteId){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Attachment> attachments = new ArrayList<Attachment>();
        Cursor results = db.query(tables[ATTACHMENTS], null, ATTACHMENT_NOTE + " = ?", new String[]{ String.valueOf(noteId) }, null, null, null);

        if(results.getCount() > 0){
            attachments = new ArrayList<Attachment>(results.getCount());
            results.moveToFirst();

            while(!results.isAfterLast()){
                attachments.add(Attachment.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return attachments;
    }

    public boolean deleteNote(Long noteId){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(tables[NOTES], NOTE_ID + " = ?", new String[]{ String.valueOf(noteId) });

        db.delete(tables[ATTACHMENTS], ATTACHMENT_NOTE + " = ?", new String[]{ String.valueOf(noteId) });

        db.close();
        return (result >= 1);
    }

    public List<Note> queryNotes(ContentValues values){
        SQLiteDatabase db = this.getReadableDatabase();
        String where = null;
        String[] args = null;

        if(values != null && values.size() > 0){
            int index = 0;

            StringBuilder whereBuffer = new StringBuilder();
            args = new String[values.size()];

            for(String key: values.keySet()) {
                if(index > 0){
                    whereBuffer.append(" AND ");
                }

                whereBuffer.append(key);

                if(key.equals(NOTE_TITLE)) {
                    whereBuffer.append(" LIKE %?%");
                } else {
                    whereBuffer.append(" = ?");
                }

                args[index] = values.getAsString(key);
                index++;
            }

            where = whereBuffer.toString();
        }

        Cursor results = db.query(tables[NOTES], null, where, args, null, null, null);
        ArrayList<Note> list = new ArrayList<Note>();

        if(results.getCount() > 0){
            results.moveToFirst();
            list = new ArrayList<Note>(results.getCount());

            while(!results.isAfterLast()){
                Note n = Note.buildFromCursor(results);
                n.setCategory(getCategory(results.getLong(3)));
                n.setAttachments(getNoteAttachments(n.getId()));
                list.add(n);

                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return list;
    }

    // CATEGORIES
    public void addCategory(Category cat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Bitmap img = cat.getImage();
        byte[] imgData = Utils.getBitmapData(img);

        // TODO: mettre une image par defaut (fond avec une lettre au milieu)

        values.put(CATEGORY_NAME, cat.getName());
        values.put(CATEGORY_DESCRIPTION, cat.getDescription());
        //values.put(CATEGORY_IMAGE_PATH, cat.getImagePath());
        values.put(CATEGORY_IMAGE, imgData);
        values.put(CATEGORY_MODIFIABLE, true);

        if(img != null){
            img.recycle();
        }

        db.insert(tables[CATEGORIES], null, values);
        db.close();
    }

    public void updateCategory(Category cat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Bitmap img = cat.getImage();
        byte[] imgData = Utils.getBitmapData(img);

        values.put(CATEGORY_NAME, cat.getName());
        values.put(CATEGORY_DESCRIPTION, cat.getDescription());
        //values.put(CATEGORY_IMAGE_PATH, cat.getImagePath());
        values.put(CATEGORY_IMAGE, imgData);
        values.put(CATEGORY_MODIFIABLE, true);

        if(img != null){
            img.recycle();
        }

        db.update(tables[CATEGORIES], values, CATEGORY_ID + " = ?", new String[]{ String.valueOf(cat.getId()) });
        db.close();
    }

    public Category getCategory(Long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.query(tables[CATEGORIES], null, CATEGORY_ID + " = ?", new String[]{ String.valueOf(id) }, null, null, null);
        Category cat = null;

        if(results.getCount() > 0) {
            results.moveToFirst();
            cat = Category.buildFromCursor(results);
        }

        results.close();
        db.close();
        return cat;
    }

    public boolean deleteCategory(Long id){
        SQLiteDatabase db = this.getWritableDatabase();
        // TODO: ajouter le champ qui permet de savoir si une categorie peut etre supprimee
        int result = db.delete(tables[CATEGORIES], CATEGORY_ID + " = ? AND " + CATEGORY_MODIFIABLE + " = ?", new String[]{ String.valueOf(id), String.valueOf(1) });

        db.close();
        return (result >= 1);
    }

    public List<Category> queryCategories(){
        return queryCategories(null);
    }

    public List<Category> queryCategories(ContentValues values){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder whereBuilder = null;
        String where = null;
        String[] args = null;

        if(values != null && values.size() > 0){
            int index = 0;

            whereBuilder = new StringBuilder();
            args = new String[values.size()];

            for(String key: values.keySet()) {
                if(index > 0){
                    whereBuilder.append(" AND ");
                }

                whereBuilder.append(key);

                if(key.equals(CATEGORY_NAME)) {
                    whereBuilder.append(" LIKE %?%");
                } else {
                    whereBuilder.append(" = ?");
                }

                args[index] = values.getAsString(key);
                index++;
            }

            where = whereBuilder.toString();
        }

        Cursor results = db.query(tables[CATEGORIES], null, where, args, null, null, null);
        ArrayList<Category> list = new ArrayList<Category>();

        if(results.getCount() > 0){
            results.moveToFirst();
            list = new ArrayList<Category>(results.getCount());

            while(!results.isAfterLast()){
                list.add(Category.buildFromCursor(results));
                results.moveToNext();
            }
        }

        results.close();
        db.close();
        return list;
    }

    // ATTACHMENTS
    public boolean addAttachment(Attachment att){
        if(att.getNote() == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        boolean result = true;
        long attId = 0;

        values.put(ATTACHMENT_PATH, att.getFilePath());
        values.put(ATTACHMENT_NOTE, att.getNote().getId());

        try {
            attId = db.insert(tables[ATTACHMENTS], null, values);
        }catch(Exception e){
            result = false;
        }

        db.close();
        return result && (attId != -1);
    }

    public boolean deleteAttachment(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(tables[ATTACHMENTS], ATTACHMENT_ID + " = ?", new String[]{ String.valueOf(id) });

        db.close();
        return (result >= 1);
    }
}
