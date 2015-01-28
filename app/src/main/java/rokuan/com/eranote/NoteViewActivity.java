package rokuan.com.eranote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.db.Note;
import rokuan.com.eranote.help.implementation.FaceActivity;

/**
 * Created by Christophe on 22/01/2015.
 */
public class NoteViewActivity extends FaceActivity {
    private Note note;

    private TextView noteTitle;
    private TextView noteCategory;
    private TextView noteContent;

    private boolean modified = false;
    private EraSQLiteOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_note_view);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        db = new EraSQLiteOpenHelper(this);

        noteTitle = (TextView)findViewById(R.id.note_view_title);
        noteCategory = (TextView)findViewById(R.id.note_view_category);
        noteContent = (TextView)findViewById(R.id.note_view_content);

        reloadNoteAndFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent noteIntent = new Intent(this, NoteActivity.class);
                noteIntent.putExtra(EraSQLiteOpenHelper.NOTE_ID, note.getId());
                this.startActivityForResult(noteIntent, Code.NOTE_EDIT_RESULT_CODE);
                return true;
            /*case R.id.action_delete:
                break;*/
            // TODO: autres menus
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setNoteResult(boolean ok){
        modified = (modified || ok);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", modified);
        this.setResult(Activity.RESULT_OK, resultIntent);
    }

    private void reloadNoteAndFields(){
        note = db.getNote(this.getIntent().getExtras().getLong(EraSQLiteOpenHelper.NOTE_ID));

        if(note == null){
            // TODO: error, no such element
        }

        noteTitle.setText(note.getTitle());
        noteContent.setText(note.getContent());
        noteCategory.setText((note.getCategory() == null) ? null : note.getCategory().getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
            case Code.NOTE_EDIT_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    boolean edited = data.getBooleanExtra("result", false);
                    this.setNoteResult(edited);

                    if(edited){
                        reloadNoteAndFields();
                    }
                }
                break;
        }
    }
}
