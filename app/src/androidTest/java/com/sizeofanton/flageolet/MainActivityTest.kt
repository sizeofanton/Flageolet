package com.sizeofanton.flageolet

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sizeofanton.flageolet.ui.MainActivity
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun test_noteStartUp() {
        onView(withId(R.id.tvNote))
            .check(matches(withText("?")))
    }

    @Test
    fun test_frequencyStartUp() {
        onView(withId(R.id.tvFreq))
            .check(matches(withText("0 Hz")))
    }

    @Test
    fun test_noteDeviationViewShown() {
        onView(withId(R.id.noteDeviationView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_radioGroupIsHidden() {
        onView(withId(R.id.rgStrings))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun test_radioGroupIsVisible() {
        onView(withId(R.id.spinnerNotes))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Standard Tuning (EBGDAE)")))
            .perform(click())
        onView(withId(R.id.rgStrings))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_spinnerItemChanged() {
        onView(withId(R.id.spinnerNotes))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Standard Tuning (EBGDAE)")))
            .perform(click())
        onView(withId(R.id.spinnerNotes))
            .check(matches(withSpinnerText(containsString("Standard Tuning (EBGDAE)"))))
    }
}
