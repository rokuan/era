package rokuan.com.eranote.db;

import android.database.Cursor;

import java.io.File;

/**
 * A note attachment
 * @author Lebeau Christophe
 */
public class Attachment {
    private Integer id = -1;
    private String filePath;
    private Note note;

    /**
     * Constructs an empty attachment
     */
    public Attachment(){

    }

    /**
     * Constructs an attachment for the file denoted by its path
     * @param fPath the file path
     */
    public Attachment(String fPath){
        this.filePath = fPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    /**
     * Builds an attachment from the specified cursor
     * @param cursor the database cursor to build the attachment from
     * @return an attachment filled with the fields in {@code cursor}
     */
    public static Attachment buildFromCursor(Cursor cursor){
        Attachment att = new Attachment();

        att.setId(cursor.getInt(0));
        att.setFilePath(cursor.getString(1));

        return att;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        if(!(o instanceof Attachment)){
            return false;
        }

        Attachment att = (Attachment)o;
        boolean sameNote = true;

        if(att.getNote() != null && this.getNote() != null){
            sameNote = att.getNote().equals(this.getNote());
        }

        return sameNote && att.filePath.equals(this.filePath);
    }

    @Override
    public String toString(){
        return (this.id + "::" + this.filePath);
    }
}
