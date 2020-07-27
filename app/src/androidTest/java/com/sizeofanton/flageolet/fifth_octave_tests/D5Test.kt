package com.sizeofanton.flageolet.fifth_octave_tests

import `in`.excogitation.zentone.library.ZenTone
import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sizeofanton.flageolet.EspressoHelper
import com.sizeofanton.flageolet.R
import com.sizeofanton.flageolet.ui.MainActivity
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class D5Test {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()
    private var activity: Activity? = null

    @Before
    fun setUp() {
        activity = EspressoHelper.getCurrentActivity()
    }

    @Test
    fun test_detect() {
        ZenTone.getInstance().generate(587, 3, 1f) {}
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.tvNote))
            .check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString("D5"))))
        ZenTone.getInstance().stop()
        Thread.sleep(5000)
    }

}