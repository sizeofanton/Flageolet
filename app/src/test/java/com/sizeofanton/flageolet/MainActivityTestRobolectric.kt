package com.sizeofanton.flageolet

import android.os.Build
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityTestRobolectric: KoinTest {

    private lateinit var activity: MainActivity

    @Before
    fun test_setUp() {
        activity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun test_startUp() {
        val note = activity.findViewById<TextView>(R.id.tvNote)
        val hz = activity.findViewById<TextView>(R.id.tvFreq)
        assertEquals("?", note.text.toString())
        assertEquals("0 Hz", hz.text.toString())
    }

    @Test
    fun test_rgVisibility() {
        val spinner = activity.findViewById<Spinner>(R.id.spinnerNotes)
        spinner.setSelection(1)
        val radioGroup = activity.findViewById<RadioGroup>(R.id.rgStrings)
        assertEquals(radioGroup.visibility, View.VISIBLE)
    }

    @Test
    fun test_rgInvisibility() {
        val spinner = activity.findViewById<Spinner>(R.id.spinnerNotes)
        spinner.setSelection(1)
        spinner.setSelection(0)
        val radioGroup = activity.findViewById<RadioGroup>(R.id.rgStrings)
        assertEquals(radioGroup.visibility, View.INVISIBLE)
    }

    @Test
    fun test_tuningSelection() {
        val spinner = activity.findViewById<Spinner>(R.id.spinnerNotes)
        spinner.setSelection(1)
        val rb1 = activity.findViewById<RadioButton>(R.id.rbString1)
        val rb2 = activity.findViewById<RadioButton>(R.id.rbString2)
        val rb3 = activity.findViewById<RadioButton>(R.id.rbString3)
        val rb4 = activity.findViewById<RadioButton>(R.id.rbString4)
        val rb5 = activity.findViewById<RadioButton>(R.id.rbString5)
        val rb6 = activity.findViewById<RadioButton>(R.id.rbString6)
        assertEquals(rb1.text[0].toString(), "E")
        assertEquals(rb2.text[0].toString(), "B")
        assertEquals(rb3.text[0].toString(), "G")
        assertEquals(rb4.text[0].toString(), "D")
        assertEquals(rb5.text[0].toString(), "A")
        assertEquals(rb6.text[0].toString(), "E")
    }

    @Test
    fun test_noteSelection() {
        val spinner = activity.findViewById<Spinner>(R.id.spinnerNotes)
        spinner.setSelection(1)
        val rg = activity.findViewById<RadioGroup>(R.id.rgStrings)
        val rb1 = activity.findViewById<RadioButton>(R.id.rbString1)
        val rb2 = activity.findViewById<RadioButton>(R.id.rbString2)
        val rb3 = activity.findViewById<RadioButton>(R.id.rbString3)
        val rb4 = activity.findViewById<RadioButton>(R.id.rbString4)
        val rb5 = activity.findViewById<RadioButton>(R.id.rbString5)
        val rb6 = activity.findViewById<RadioButton>(R.id.rbString6)
        rb1.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb1.id)
        rb2.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb2.id)
        rb3.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb3.id)
        rb4.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb4.id)
        rb5.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb5.id)
        rb6.isChecked = true
        assertEquals(rg.checkedRadioButtonId, rb6.id)
    }

    @After
    fun test_cleanUp(){
        stopKoin()
    }

}