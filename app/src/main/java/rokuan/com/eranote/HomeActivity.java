package rokuan.com.eranote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rokuan.com.eranote.db.Category;
import rokuan.com.eranote.db.EraSQLiteOpenHelper;
import rokuan.com.eranote.db.Note;

/**
 * Main activity
 */
public class HomeActivity extends ActionBarActivity {
    private ViewPager mViewPager;
    private EraPagerAdapter pagerAdapter;

    private static final int NOTES_PAGE = 0;
    private static final int CATEGORIES_PAGE = 1;

    private EraSQLiteOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        db = new EraSQLiteOpenHelper(this);

        pagerAdapter = new EraPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        //actionBar.setSelectedNavigationItem(position);
                    }
                }
        );
        mViewPager.setAdapter(pagerAdapter);
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
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                switch(mViewPager.getCurrentItem()){
                    case NOTES_PAGE:
                        Intent noteIntent = new Intent(this, NoteActivity.class);
                        noteIntent.putExtra("new", true);
                        this.startActivityForResult(noteIntent, Code.NOTE_EDIT_RESULT_CODE);
                        break;

                    case CATEGORIES_PAGE:
                        Intent categoryIntent = new Intent(this, CategoryActivity.class);
                        categoryIntent.putExtra("new", true);
                        this.startActivityForResult(categoryIntent, Code.CATEGORY_EDIT_RESULT_CODE);
                        //this.startActivityForResult(categoryIntent);
                        break;
                }
                return true;
            // TODO: autres menus
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.notes_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_home_note, menu);
        } else if(v.getId() == R.id.categories_list){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_home_category, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //info.id
        //info.position;

        if(mViewPager.getCurrentItem() == NOTES_PAGE) {
            ListView notesView = (ListView)this.findViewById(R.id.notes_list);
            ArrayAdapter notesAdapter = (ArrayAdapter)notesView.getAdapter();
            Note n = (Note)notesAdapter.getItem(info.position);

            switch (item.getItemId()) {
                case R.id.delete_note:
                    // TODO:
                    db.deleteNote(n.getId());
                    notesAdapter.remove(n);
                    notesAdapter.notifyDataSetChanged();
                    return true;
                case R.id.edit_note:
                    this.editNote(n.getId());
                    return true;
                case R.id.favorite_note:
                    n.setFavorite(!n.isFavorite());
                    db.updateNote(n);
                    notesAdapter.notifyDataSetChanged();
                    return true;
                default:
                    break;
            }
        } else if (mViewPager.getCurrentItem() == CATEGORIES_PAGE){
            ListView categoriesView = (ListView)this.findViewById(R.id.categories_list);
            ArrayAdapter categoriesAdapter = (ArrayAdapter)categoriesView.getAdapter();
            Category c = (Category)categoriesAdapter.getItem(info.position);

            switch(item.getItemId()){
                case R.id.delete_category:
                    // TODO: voir quand verifier si la categorie est supprimable
                    db.deleteCategory(c.getId());
                    categoriesAdapter.remove(c);
                    categoriesAdapter.notifyDataSetChanged();
                    return true;
                case R.id.edit_category:
                    this.editCategory(c.getId());
                    return true;
                default:
                    break;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (data == null)
            return;*/

        switch (requestCode) {
            case Code.NOTE_EDIT_RESULT_CODE:
                switch(resultCode) {
                    case RESULT_OK:
                        if (data.getBooleanExtra("result", false)) {
                            // TODO: raffraichir l'element modifie
                            //ListView notesList = (ListView) this.findViewById(R.id.notes_list);
                            //ArrayAdapter notesAdapter = (ArrayAdapter) notesList.getAdapter();
                            Fragment notesFragment = ((EraPagerAdapter)mViewPager.getAdapter()).getItem(NOTES_PAGE);
                            ((PagerFragment)notesFragment).reloadData();
                            //notesAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                break;
            case Code.CATEGORY_EDIT_RESULT_CODE:
                switch(resultCode) {
                    case RESULT_OK:
                        if (data.getBooleanExtra("result", false)) {
                            //ListView categoriesList = (ListView) this.findViewById(R.id.categories_list);
                            //ArrayAdapter categoriesAdapter = (ArrayAdapter) categoriesList.getAdapter();
                            Fragment categoriesFragment = ((EraPagerAdapter)mViewPager.getAdapter()).getItem(CATEGORIES_PAGE);
                            ((PagerFragment)categoriesFragment).reloadData();
                            //categoriesAdapter.notifyDataSetChanged();

                            // TODO: raffraichir l'element modifie et les notes liees, creer une fonction pour refresh
                            //ListView notesList = (ListView) this.findViewById(R.id.notes_list);
                            //ArrayAdapter notesAdapter = (ArrayAdapter) notesList.getAdapter();
                            Fragment notesFragment = ((EraPagerAdapter)mViewPager.getAdapter()).getItem(NOTES_PAGE);
                            ((PagerFragment)notesFragment).reloadData();
                        }
                        break;
                }
                break;
        }
    }

    /**
     * Starts a new activity to edit the note
     * @param noteId the note id
     */
    public void editNote(Integer noteId){
        Intent noteIntent = new Intent(this, NoteActivity.class);
        noteIntent.putExtra(EraSQLiteOpenHelper.NOTE_ID, noteId);
        this.startActivityForResult(noteIntent, Code.NOTE_EDIT_RESULT_CODE);
    }

    /**
     * Starts a new activity which displays the content of a note
     * @param noteId the note id
     */
    public void displayNote(Integer noteId){
        Intent noteIntent = new Intent(this, NoteViewActivity.class);
        noteIntent.putExtra(EraSQLiteOpenHelper.NOTE_ID, noteId);
        this.startActivityForResult(noteIntent, Code.NOTE_EDIT_RESULT_CODE);
    }

    /**
     * Starts a new activity to edit a category
     * @param categoryId the category id
     */
    public void editCategory(Integer categoryId){
        Intent categoryIntent = new Intent(this, CategoryActivity.class);
        categoryIntent.putExtra(EraSQLiteOpenHelper.CATEGORY_ID, categoryId);
        this.startActivityForResult(categoryIntent, Code.CATEGORY_EDIT_RESULT_CODE);
    }

    class EraPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles = {
                "NOTES",
                "CATEGORIES",
                //"PARAMETERS"
        };
        private List<PagerFragment> fragments = new ArrayList<PagerFragment>();

        public EraPagerAdapter(FragmentManager fm){
            super(fm);

            fragments.add(new NoteFragment());
            fragments.add(new CategoryFragment());
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            // Create fragment object
            /*Fragment fragment = null;

            switch(position){
                case NOTES_PAGE:
                    fragment = new NoteFragment();
                    break;
                case CATEGORIES_PAGE:
                    fragment = new CategoryFragment();
                    break;

                default:
                    fragment = new NoteFragment();
                    break;
            }

            // Attach some data to the fragment
            // that we'll use to populate our fragment layouts
            Bundle args = new Bundle();
            args.putInt("page_position", position + 1);

            // Set the arguments on the fragment
            // that will be fetched in the
            // DemoFragment@onCreateView
            fragment.setArguments(args);

            return fragment;*/
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public static abstract class PagerFragment extends Fragment {
        public abstract void reloadData();
    }

    public static class NoteFragment extends PagerFragment {
        private EraSQLiteOpenHelper db;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private List<Note> allNotes;
        private NoteAdapter noteAdapter;

        public NoteFragment(){
            allNotes = new ArrayList<Note>();
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            db = new EraSQLiteOpenHelper(this.getActivity());
            noteAdapter = new NoteAdapter(this.getActivity(), allNotes);

            this.reloadData();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_index_notes, container, false);
            //Bundle args = getArguments();

            ListView notes = (ListView)rootView.findViewById(R.id.notes_list);

            this.getActivity().registerForContextMenu(notes);

            notes.setAdapter(noteAdapter);
            notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Note n = noteAdapter.getItem(position);
                    ((HomeActivity)NoteFragment.this.getActivity()).displayNote(n.getId());
                }
            });

            return rootView;
        }

        @Override
        public void onPause(){
            db.close();
            super.onPause();
        }

        @Override
        public void onResume(){
            super.onResume();
            db = new EraSQLiteOpenHelper(this.getActivity());
        }

        @Override
        public void reloadData() {
            allNotes.clear();
            // TODO: inclure les futurs tris etc
            if(db == null){
                db = new EraSQLiteOpenHelper(this.getActivity());
            }
            List<Note> matchingNotes = db.queryNotes(null);
            allNotes.addAll(matchingNotes);
            noteAdapter.notifyDataSetChanged();
        }

        class NoteAdapter extends ArrayAdapter<Note> {
            private List<Note> all;
            private LayoutInflater inflater;

            public NoteAdapter(Context context, List<Note> objects) {
                this(context, R.layout.note_item, objects);
            }

            public NoteAdapter(Context context, int resource, List<Note> objects) {
                super(context, resource, objects);
                inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                all = objects;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v;
                final Note n = all.get(position);

                if(convertView == null){
                    v = inflater.inflate(R.layout.note_item, parent, false);
                } else {
                    v = convertView;
                }

                TextView noteTitleText = (TextView) v.findViewById(R.id.note_item_title);

                if(n.getTitle() != null && !n.getTitle().isEmpty()) {
                    noteTitleText.setText(n.getTitle());
                } else {
                    noteTitleText.setVisibility(View.GONE);
                    View separator = v.findViewById(R.id.note_item_separator);
                    separator.setVisibility(View.GONE);
                }

                TextView noteContentText = (TextView)v.findViewById(R.id.note_item_content);
                noteContentText.setText(n.getContent());

                if(n.getCategory() != null){
                    ImageView noteCategoryImage = (ImageView)v.findViewById(R.id.note_item_category);
                    noteCategoryImage.setImageBitmap(n.getCategory().getImage());
                }

                ToggleButton noteFavorite = (ToggleButton)v.findViewById(R.id.note_item_favorite);
                noteFavorite.setChecked(n.isFavorite());

                noteFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //boolean oldValue = n.isFavorite();
                        //TODO: error ?
                        n.setFavorite(isChecked);
                        db.updateNote(n);
                    }
                });

                TextView noteLastModification = (TextView)v.findViewById(R.id.note_item_last_modif);
                noteLastModification.setText(dateFormat.format(n.getLastModified()));

                if(n.getAttachments().size() > 0){
                    TextView noteAttachmentSize = (TextView)v.findViewById(R.id.note_item_attachments_size);
                    noteAttachmentSize.setText(n.getAttachments().size() > 99 ? "99+" : String.valueOf(n.getAttachments().size()));
                } else {
                    v.findViewById(R.id.note_item_attachments_layout).setVisibility(View.INVISIBLE);
                }

                return v;
            }

            @Override
            public Note getItem(int position){
                return all.get(position);
            }

            @Override
            public int getCount(){
                return all.size();
            }
        }
    }

    public static class CategoryFragment extends PagerFragment {
        private EraSQLiteOpenHelper db;
        private List<Category> allCategories;
        private CategoryAdapter categoryAdapter;

        public CategoryFragment(){
            allCategories = new ArrayList<Category>();
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            db = new EraSQLiteOpenHelper(this.getActivity());
            categoryAdapter = new CategoryAdapter(this.getActivity(), allCategories);

            this.reloadData();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_index_categories, container, false);
            // Bundle args = getArguments();
            ListView categories = (ListView)rootView.findViewById(R.id.categories_list);

            this.getActivity().registerForContextMenu(categories);

            categories.setAdapter(categoryAdapter);
            categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Category c = categoryAdapter.getItem(position);
                    ((HomeActivity)CategoryFragment.this.getActivity()).editCategory(c.getId());
                }
            });
            categories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: afficher menu de suppression
                    return false;
                }
            });

            return rootView;
        }

        @Override
        public void onPause(){
            db.close();
            super.onPause();
        }

        @Override
        public void onResume(){
            super.onResume();
            db = new EraSQLiteOpenHelper(this.getActivity());
        }

        @Override
        public void reloadData() {
            allCategories.clear();
            allCategories.addAll(db.queryCategories(null));
            categoryAdapter.notifyDataSetChanged();
        }

        class CategoryAdapter extends ArrayAdapter<Category> {
            private List<Category> all;
            private LayoutInflater inflater;

            public CategoryAdapter(Context context, List<Category> objects) {
                this(context, R.layout.note_item, objects);
            }

            public CategoryAdapter(Context context, int resource, List<Category> objects) {
                super(context, resource, objects);
                inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                all = objects;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v;
                Category c = all.get(position);

                if(convertView == null){
                    v = inflater.inflate(R.layout.category_item, parent, false);
                } else {
                    v = convertView;
                }

                ImageView categoryImage = (ImageView)v.findViewById(R.id.category_item_image);
                categoryImage.setImageBitmap(c.getImage());

                TextView categoryTitleText = (TextView) v.findViewById(R.id.category_item_name);
                categoryTitleText.setText(c.getName());

                return v;
            }

            @Override
            public Category getItem(int position){
                return all.get(position);
            }

            @Override
            public int getCount(){
                return all.size();
            }
        }
    }
}

