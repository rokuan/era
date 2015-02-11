package rokuan.com.eranote.db;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christophe on 17/01/2015.
 */
public class Note {
    private Integer id;
    private String title;
    private String content;
    private Category category;
    private List<Attachment> attachments = new ArrayList<Attachment>();
    private boolean favorite;
    private Date lastModified;

    public Note(){

    }

    public Note(String noteTitle, String noteContent){
        this.title = noteTitle;
        this.content = noteContent;
    }

    /*public Note(String noteTitle, String noteContent, Category noteCategory){
        this(noteTitle, noteContent);
        this.category = noteCategory;
    }*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static Note buildFromCursor(Cursor result){
        Note n = new Note();
        int noteId = result.getInt(0);
        String noteTitle = result.getString(1);
        String noteContent = result.getString(2);
        boolean noteFavorite = (result.getInt(4) != 0);
        Date noteLastModif = new Date(result.getLong(5));

        n.setId(noteId);
        n.setTitle(noteTitle);
        n.setContent(noteContent);
        n.setFavorite(noteFavorite);
        n.setLastModified(noteLastModif);

        return n;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        return (o instanceof Note) && (((Note)o).id == this.id);
    }
}
