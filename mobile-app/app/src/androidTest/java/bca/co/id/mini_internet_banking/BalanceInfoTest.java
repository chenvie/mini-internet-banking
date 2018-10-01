package bca.co.id.mini_internet_banking;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class BalanceInfoTest {

    @Rule
    public ActivityTestRule<HomeActivity> atr = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Before
    public void init(){
        atr.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void showBalance() throws Exception{
        onView(withId(R.id.menu_balance)).perform(click());
    }
}
