package com.sizeofanton.flageolet

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sizeofanton.flageolet.extensions.playSound
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TunerTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()
    private var activity: Activity? = null

    @Before
    fun setUp() {
        activity = EspressoHelper.getCurrentActivity()
    }

    @Test
    fun test_detect() {
        activity?.playSound(R.raw.e4)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("E4"))))

        activity?.playSound(R.raw.b3)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("B3"))))

        activity?.playSound(R.raw.g3)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("G3"))))

        activity?.playSound(R.raw.d3)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("D3"))))

        activity?.playSound(R.raw.a2)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("A2"))))

        activity?.playSound(R.raw.e2)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("E2"))))
    }
}
