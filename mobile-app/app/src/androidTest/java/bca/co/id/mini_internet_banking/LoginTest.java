package bca.co.id.mini_internet_banking;

import android.content.SharedPreferences;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.widget.TextViewCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class LoginTest {
    @Rule
    public ActivityTestRule<MainActivity> atr = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void init(){
        atr.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void login() throws Exception{
        onView(withId(R.id.txtUname_login)).perform(ViewActions.clearText()).perform(typeText("Vievin47"));
        onView(withId(R.id.txtPwd_login)).perform(ViewActions.clearText()).perform(typeText("vievin97"));
        onView(withId(R.id.btnLogin)).perform(click());
    }
}
