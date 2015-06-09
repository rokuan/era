package rokuan.com.eranote;


import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

/**
 * Created by LEBEAU Christophe on 09/06/15.
 */
public class HomeTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    private HomeActivity mActivity;

    public HomeTest() {
        super(HomeActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    public void testNewNote(){
        onView(withText(R.id.action_new))
        .perform(click());
    }

    public void testNewCategory(){
        //onView(withId(R.id.notes_list)).
    }
}
