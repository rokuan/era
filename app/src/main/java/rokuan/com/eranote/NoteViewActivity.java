package rokuan.com.eranote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rokuan.com.eranote.db.Attachment;
import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.db.Note;
import rokuan.com.eranote.help.implementation.FaceActivity;

/**
 * An activity to view the content of a specific note
 * @author Lebeau Christophe
 */
public class NoteViewActivity extends FaceActivity {
    private Note note;

    @InjectView(R.id.note_view_title) protected TextView noteTitle;
    @InjectView(R.id.note_view_category) protected TextView noteCategory;
    @InjectView(R.id.note_view_content) protected TextView noteContent;
    @InjectView(R.id.note_view_attachments_grid) protected GridView noteAttachmentsGrid;

    private boolean modified = false;
    private EraSQLiteOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_note_view);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        db = new EraSQLiteOpenHelper(this);

        ButterKnife.inject(this);

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
        note = db.getNote(this.getIntent().getExtras().getInt(EraSQLiteOpenHelper.NOTE_ID));

        if(note == null){
            // TODO: error, no such element
        }

        noteTitle.setText(note.getTitle());
        noteContent.setText(note.getContent());
        noteCategory.setText((note.getCategory() == null) ? null : note.getCategory().getName());
        noteAttachmentsGrid.setAdapter(new AttachmentGridAdapter(this, R.layout.note_attachment_grid_item, note.getAttachments()));
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

    @Override
    public void onBackPressed(){
        this.finish();
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

    class AttachmentGridAdapter extends ArrayAdapter<Attachment> {
        private LayoutInflater inflater;

        public AttachmentGridAdapter(Context context, int resource, List<Attachment> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.note_attachment_grid_item, parent, false);
            }

            TextView attachmentName = (TextView)v.findViewById(R.id.note_attachment_grid_name);
            // TODO: trouver l'image
            // ImageView attachmentImage = (ImageView)v.findViewById(R.id.note_attachment_grid_image);
            attachmentName.setText(this.getItem(position).getFileName());

            return v;
        }
    }
}
