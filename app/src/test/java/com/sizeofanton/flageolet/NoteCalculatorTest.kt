package com.sizeofanton.flageolet

import com.sizeofanton.flageolet.data.local.GuitarFrequencies
import com.sizeofanton.flageolet.di.appModule
import com.sizeofanton.flageolet.utils.NoteCalculator
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class NoteCalculatorTest : KoinTest {
    private val noteCalculator: NoteCalculator by inject()
    private val frequencies = GuitarFrequencies.frequencies[0]?.first!!
    private val names = GuitarFrequencies.frequencies[0]?.second!!

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule)
    }

    @Test
    fun test_closestNoteFreq() {
        assertEquals(
            names[noteCalculator.getClosestNote(329.62, frequencies)],
            "E4"
        )
    }

    @Test
    fun test_deviation() {
        assertEquals(
            noteCalculator.calculateDeviation(329.62, 329.62),
            0
        )
    }
}
