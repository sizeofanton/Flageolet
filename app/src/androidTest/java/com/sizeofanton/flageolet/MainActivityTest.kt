package com.sizeofanton.flageolet

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun test_noteStartUp() {
        onView(withId(R.id.tvNote)).check(matches(withText("?")))
    }

    @Test
    fun test_frequencyStartUp() {
        onView(withId(R.id.tvFreq)).check(matches(withText("0 Hz")))
    }

    @Test
    fun test_noteDeviationViewShown() {
        onView(withId(R.id.noteDeviationView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_radioGroupIsHidden() {
        onView(withId(R.id.rgStrings)).check(matches(not(isDisplayed())))
    }

}
