package com.sizeofanton.flageolet

import com.sizeofanton.flageolet.di.mockModule
import com.sizeofanton.flageolet.mock.MainModelMock
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class MainModelTest : KoinTest {
    private val model: MainContract.Model by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(mockModule)
    }

    @Test
    fun test_closestNoteFreq() {
        assertEquals((model as MainModelMock).testGetClosestNote(329.63), "E4")
    }

    @Test
    fun test_position() {
        assertEquals((model as MainModelMock).testPosition(329.64, 329.63), 0)
    }
}
