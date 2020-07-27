package com.sizeofanton.flageolet

import `in`.excogitation.zentone.library.ZenTone
import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sizeofanton.flageolet.ui.MainActivity
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class E4Test {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()
    private var activity: Activity? = null

    @Before
    fun setUp() {
        activity = EspressoHelper.getCurrentActivity()
    }

    @Test
    fun test_detect() {
        ZenTone.getInstance().generate(330, 3, 1f) {}
        Thread.sleep(3000)
        onView(withId(R.id.tvNote))
            .check(matches(withText(containsString("E4"))))
        ZenTone.getInstance().stop()
        Thread.sleep(5000)
    }

}
