package rokuan.com.eranote.db;

import android.database.Cursor;

import java.io.File;

/**
 * Created by Christophe on 19/01/2015.
 */
public class Attachment {
    private Long id;
    private String filePath;
    private Note note;

    public Attachment(){

    }

    public Attachment(String fPath){
        this.filePath = fPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName(){
        try {
            File f = new File(getFilePath());
            return f.getName();
        }catch(Exception e){
            return "...";
        }
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public static Attachment buildFromCursor(Cursor cursor){
        Attachment att = new Attachment();

        att.setId(cursor.getLong(0));
        att.setFilePath(cursor.getString(1));

        return att;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return ((o instanceof Attachment)) && (((Attachment)o).id == this.id);
    }
}
