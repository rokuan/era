package rokuan.com.eranote;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by LEBEAU Christophe on 09/06/15.
 */
public class CategoryEditTest extends ActivityInstrumentationTestCase2<CategoryActivity> {
    private CategoryActivity mActivity;

    public CategoryEditTest() {
        super(CategoryActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    public void testCategoryChange(){
        onView(withId(R.id.form_category_name))
                .perform(typeText("Nouvelle categorie"));
    }

    public void testNewCategory(){
        //onView(withId(R.id.notes_list)).
    }
}
