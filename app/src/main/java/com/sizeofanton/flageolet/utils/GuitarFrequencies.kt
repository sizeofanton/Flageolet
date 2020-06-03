package com.sizeofanton.flageolet.utils

object GuitarFrequencies {
    private val FREQUENCIES = doubleArrayOf(
        65.41, 69.30, 73.42, 77.78, 82.41, 87.31, 92.50, 98.00, 103.83, 110.00, 116.54, 123.47,  // Octave 2
        130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 185.00, 196.00, 207.65, 220.00, 233.08, 246.94,  // Octave 3
        261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88,  // Octave 4
        523.25, 554.37, 587.33, 622.25, 659.26, 698.46, 739.99, 783.99, 830.61, 880.00, 932.33, 987.77 // Octave 5
    )
    private val NAMES = arrayOf(
        "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
        "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
        "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
        "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5"
    )

    private val FREQUENCIES_EADGBE =
        doubleArrayOf(329.63, 246.94, 196.00, 146.83, 110.00, 82.41)
    private val NAMES_EADGBE =
        arrayOf("E ", "B ", "G ", "D ", "A ", "E ")

    private val FREQUENCIES_EbAbDbGbBbEb =
        doubleArrayOf(311.13, 233.08, 185.00, 138.56, 103.83, 77.83)
    private val NAMES_EbAbDbGbBbEb =
        arrayOf("E♭", "B♭ ", "G♭", "D♭", "A♭", "E♭")

    val frequencies: HashMap<Int, Pair<DoubleArray, Array<String>>> = HashMap()
    init {
        frequencies[0] = Pair(FREQUENCIES, NAMES)
        frequencies[1] = Pair(FREQUENCIES_EADGBE, NAMES_EADGBE)
        frequencies[2] = Pair(FREQUENCIES_EbAbDbGbBbEb, NAMES_EbAbDbGbBbEb)
    }
}