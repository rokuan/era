package rokuan.com.eranote;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import rokuan.com.eranote.db.Attachment;
import rokuan.com.eranote.db.Category;
import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.db.Note;

/**
 * An activity to edit the content of a specific note
 * @author Lebeau Christophe
 */
public class NoteActivity extends ActionBarActivity implements View.OnClickListener {
    private EraSQLiteOpenHelper db;

    private EditText noteTitle;
    private Spinner noteCategory;
    private ArrayAdapter<Category> categoryAdapter;
    private EditText noteContent;
    //private ExpandableListView expandableListView;
    //private AttachmentExpendableListAdapter attachmentAdapter;
    private ListView attachmentsList;
    private AttachmentListAdapter attachmentAdapter;

    private Note note;
    private List<Attachment> attachmentsToAdd;
    private List<Attachment> attachmentsToRemove;

    private boolean newElementMode = false;
    private boolean modified = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        db = new EraSQLiteOpenHelper(this);

        Bundle extras = this.getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EraSQLiteOpenHelper.NOTE_ID)) {
                int noteId = extras.getInt(EraSQLiteOpenHelper.NOTE_ID);
                note = db.getNote(noteId);
            } else if(extras.containsKey("new")) {
                if(extras.getBoolean("new")) {
                    newElementMode = true;
                    note = new Note();
                }
            }
        }

        if(note == null){
            // TODO: an error occurred
        }

        noteTitle = (EditText)findViewById(R.id.form_note_title);
        noteCategory = (Spinner)findViewById(R.id.form_note_category);
        noteContent = (EditText)findViewById(R.id.form_note_content);
        attachmentsList = (ListView)findViewById(R.id.form_note_attachments);
        findViewById(R.id.form_note_add_attachment).setOnClickListener(this);

        List<Category> allCategories = db.queryCategories(null);
        categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attachmentAdapter = new AttachmentExpendableListAdapter(this, "Attachments", note.getAttachments());
        attachmentAdapter = new AttachmentListAdapter(this, R.layout.note_attachment_list_item, note.getAttachments());
        attachmentsList.setAdapter(attachmentAdapter);

        noteTitle.setText(note.getTitle());
        noteContent.setText(note.getContent());

        noteCategory.setAdapter(categoryAdapter);
        noteCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category cat = categoryAdapter.getItem(position);
                note.setCategory(cat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int totalItemsHeight = 0;
        int itemsCount = attachmentAdapter.getCount();

        for (int itemPos = 0; itemPos < itemsCount; itemPos++) {
            View item = attachmentAdapter.getView(itemPos, null, attachmentsList);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = attachmentsList.getDividerHeight() * (itemsCount - 1);

        // Set list height.
        ViewGroup.LayoutParams params = attachmentsList.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        attachmentsList.setLayoutParams(params);
        attachmentsList.requestLayout();

        if(note.getCategory() == null){
            try {
                noteCategory.setSelection(0);
            }catch(Exception e){
                // ERROR: No category
            }
        } else {
            noteCategory.setSelection(categoryAdapter.getPosition(note.getCategory()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_note:
                if(noteContent.getText().toString().isEmpty()) {
                    // TODO: afficher une alert dialog
                    Toast.makeText(this, "Please fill the note content", Toast.LENGTH_SHORT).show();
                } else {
                    note.setTitle(noteTitle.getText().toString());
                    note.setContent(noteContent.getText().toString());

                    if(newElementMode){
                        db.addNote(note);
                    } else {
                        db.updateNote(note);
                    }

                    this.setNoteResult(true);
                    this.finish();
                }
                break;

            case R.id.action_cancel_note:
                this.finish();
                break;
        }

        return true;
    }

    private void setNoteResult(boolean ok){
        modified = (modified || ok);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", modified);
        this.setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //case R.id.note_new_attachment:
            case R.id.form_note_add_attachment:
                // TODO: ouvrir le file explorer
                //Toast.makeText(this, "Adding an attachment [NOT IMPLEMENTED YET]", Toast.LENGTH_SHORT).show();
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("gagt/sdf");
                try {
                    startActivityForResult(fileIntent, Code.PICK_ATTACHMENT_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No suitable application found to pick a file", Toast.LENGTH_SHORT).show();
                    // TODO: afficher une dialog qui indique qu'il faut une application
                    //Log.e("Era - Note", "No activity can handle picking a file. Showing alternatives.");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (requestCode) {
            case Code.PICK_ATTACHMENT_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getData().getPath();
                    Attachment attachment = new Attachment(filePath);
                    attachment.setNote(note);

                    if(note.getAttachments().contains(attachment)){
                        // TODO: Dialog qui informe que le fichier est deja en piece jointe
                        Toast.makeText(this, "Attachment exists already", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /*note.getAttachments().add(attachment);*/
                    /*attachmentAdapter.notifyDataSetChanged();*/
                    attachmentAdapter.add(attachment);
                }
                break;
        }
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

    class AttachmentListAdapter extends ArrayAdapter<Attachment> {
        private LayoutInflater inflater;

        public AttachmentListAdapter(Context context, int resource, List<Attachment> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;

            if(v == null){
                v = inflater.inflate(R.layout.note_attachment_list_item, parent, false);
            }

            final Attachment attachment = this.getItem(position);

            TextView attachmentName = (TextView)v.findViewById(R.id.note_attachment_list_name);
            attachmentName.setText(attachment.getFileName());

            ImageButton deleteAttachment = (ImageButton)v.findViewById(R.id.note_attachment_list_delete);
            deleteAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttachmentListAdapter.this.remove(attachment);
                }
            });

            return v;
        }
    }
}
