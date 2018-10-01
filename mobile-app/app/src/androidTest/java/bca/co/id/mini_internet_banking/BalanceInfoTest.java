package bca.co.id.mini_internet_banking;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BalanceInfoTest {

    @Rule
    public ActivityTestRule<HomeActivity> atr = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Before
    public void init(){
        atr.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void showBalance() throws Exception{
        //onView(withId(R.id.menu_balance)).perform(click());
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_balance));
    }
}
