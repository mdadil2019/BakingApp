package com.environer.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.media.MediaMetadataCompat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
/**
 * Created by Mohammad Adil on 10-06-2017.
 */
@RunWith(AndroidJUnit4.class)
public class FrontFragmentTesting {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testClick(){
        onView(withId(R.id.mainVrecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withId(R.id.exoplayer)).check(matches(isDisplayed()));
    }

}
