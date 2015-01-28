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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import rokuan.com.eranote.db.Attachment;
import rokuan.com.eranote.db.Category;
import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.db.Note;

/**
 * Created by Christophe on 18/01/2015.
 */
public class NoteActivity extends ActionBarActivity implements View.OnClickListener {
    private EraSQLiteOpenHelper db;

    private EditText noteTitle;
    private Spinner noteCategory;
    private ArrayAdapter<Category> categoryAdapter;
    private EditText noteContent;
    private ExpandableListView expandableListView;
    private AttachmentExpendableListAdapter attachmentAdapter;

    private Note note;

    private boolean newElementMode = false;
    private boolean modified = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        db = new EraSQLiteOpenHelper(this);

        Bundle extras = this.getIntent().getExtras();

        if(extras != null){
            if(extras.containsKey(EraSQLiteOpenHelper.NOTE_ID)) {
                long noteId = extras.getLong(EraSQLiteOpenHelper.NOTE_ID);
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
        expandableListView = (ExpandableListView)findViewById(R.id.form_note_additional);

        List<Category> allCategories = db.queryCategories(null);
        categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        attachmentAdapter = new AttachmentExpendableListAdapter(this, "Attachments", note.getAttachments());
        expandableListView.setAdapter(attachmentAdapter);
        expandableListView.expandGroup(0);

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
            case R.id.note_new_attachment:
                // TODO: ouvrir le file explorer
                //Toast.makeText(this, "Adding an attachment [NOT IMPLEMENTED YET]", Toast.LENGTH_SHORT).show();
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("gagt/sdf");
                try {
                    startActivityForResult(fileIntent, Code.PICK_ATTACHMENT_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    Log.e("Era - Note", "No activity can handle picking a file. Showing alternatives.");
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

                    if(newElementMode){

                    } else {
                        if (!db.addAttachment(attachment)) {
                            // TODO: Dialog qui informe que le fichier est deja en piece jointe
                            Toast.makeText(this, "Attachment exists already", Toast.LENGTH_SHORT).show();
                        }
                    }

                    note.getAttachments().add(attachment);
                    attachmentAdapter.notifyDataSetChanged();
                }
        }
    }

    /*class GridExpandableListAdapter extends BaseExpandableListAdapter {
        class AttachmentAdapter extends ArrayAdapter<Attachment> {
            private LayoutInflater inflater;
            private List<Attachment> attachments;

            public AttachmentAdapter(Context context, int resource, List<Attachment> objects) {
                super(context, resource, objects);
                this.attachments = objects;
                this.inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView (int position, View convertView, ViewGroup parent){
                if(convertView == null) {
                    convertView = inflater.inflate(R.layout.note_attachment, parent, false);
                }
                TextView fileName = (TextView)convertView.findViewById(R.id.note_attachment_name);
                fileName.setText(getItem(position).getFileName());
                return convertView;
            }

            @Override
            public Attachment getItem(int position){
                return attachments.get(position);
            }
        }

        private Context context;
        private String title;
        private List<Attachment> attachments;
        private GridView grid;

        private LayoutInflater inflater;

        public GridExpandableListAdapter(Context ctxt, int cols, String listTitle, List<Attachment> attachmentList){
            this.context = ctxt;
            this.title = listTitle;
            this.attachments = attachmentList;
            this.inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return title;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return attachments.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = inflater.inflate(R.layout.note_attachment_header, parent, false);
            ImageButton addAttachment = (ImageButton)v.findViewById(R.id.note_new_attachment);
            addAttachment.setOnClickListener(NoteActivity.this);
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.note_attachments_grid, null);
            }

            GridView gridConvert = (GridView)convertView;
            gridConvert.setAdapter(new AttachmentAdapter(this.context, R.layout.note_attachment, this.attachments));
            gridConvert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: open file
                    Toast.makeText(context, "Opening file ...", Toast.LENGTH_SHORT).show();
                }
            });
            gridConvert.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: afficher le menu de suppression
                    Toast.makeText(context, "Delete ?", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }*/

    class AttachmentExpendableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private String title;
        private List<Attachment> attachments;

        private LayoutInflater inflater;

        public AttachmentExpendableListAdapter(Context ctxt, String listTitle, List<Attachment> attachmentList){
            this.context = ctxt;
            this.title = listTitle;
            this.attachments = attachmentList;
            this.inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return attachments.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return title;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return attachments.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return ((Attachment)getChild(groupPosition, childPosition)).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.note_attachment_header, parent, false);
            }

            ImageButton addAttachment = (ImageButton)convertView.findViewById(R.id.note_new_attachment);
            addAttachment.setOnClickListener(NoteActivity.this);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.note_attachment, parent, false);
            }

            Attachment attachment = (Attachment)getChild(groupPosition, childPosition);

            TextView fileName = (TextView)convertView.findViewById(R.id.note_attachment_name);
            fileName.setText(attachment.getFileName());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
