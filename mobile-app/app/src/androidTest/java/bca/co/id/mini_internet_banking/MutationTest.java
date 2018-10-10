package bca.co.id.mini_internet_banking;

import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;

public class MutationTest {
    @Rule
    public ActivityTestRule<HomeActivity> atr = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Before
    public void init(){
        atr.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void readMutation() throws Exception{
        onView(withId(R.id.menu_mutation)).perform(click());
        onView(withParent(withId(buttonContainer)), withId(datePickerLaunchViewId)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, monthOfYear, dayOfMonth));
        onView(withId(android.R.id.button1)).perform(click());
    }
}
