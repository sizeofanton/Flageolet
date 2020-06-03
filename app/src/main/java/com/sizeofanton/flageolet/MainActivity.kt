package com.sizeofanton.flageolet


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.sizeofanton.flageolet.extensions.playSound
import com.sizeofanton.flageolet.extensions.vibratePhone
import com.sizeofanton.flageolet.utils.GuitarFrequencies
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val PERMISSION_CODE = 101
private const val START_RECORDING_DELAY = 1000L

class MainActivity : AppCompatActivity(), MainContract.View {

    private val viewModel: MainViewModel by viewModel()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkMicroPermission()) requestMicroPermission()
        else viewModel.startRecording(0)
        initUI()
    }

    private fun checkMicroPermission(): Boolean  =
        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    private fun requestMicroPermission() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startRecording(0)
                } else {
                    finish()
                }
            }
        }
    }


    private fun moveNoteView(position: Int) {
        noteDeviationView.setPointerVisibility(true)
        when (position) {
            in 50..Int.MAX_VALUE -> {
                noteDeviationView.setPosition(50)
                noteDeviationView.setPointerColor(getColor(R.color.pointerBad))
            }
            in Int.MIN_VALUE..-50 -> {
                noteDeviationView.setPosition(-50)
                noteDeviationView.setPointerColor(getColor(R.color.pointerBad))
            }
            in -1..1 -> {
                viewModel.stopRecording()
                vibratePhone()
                playSound(R.raw.success)
                noteDeviationView.setPointerColor(getColor(R.color.pointerGood))
                tvNote.setTextColor(getColor(R.color.pointerGood))
                tvFreq.setTextColor(getColor(R.color.pointerGood))
                handler.postDelayed({
                    tvNote.setTextColor(getColor(R.color.colorAccent))
                    tvFreq.setTextColor(getColor(R.color.colorAccent))
                }, START_RECORDING_DELAY)
                viewModel.startRecording(START_RECORDING_DELAY)
            }
            else -> {
                noteDeviationView.setPosition(position)
                noteDeviationView.setPointerColor(getColor(R.color.pointerNeutral))
            }
        }
    }

    private fun initUI() {
        noteDeviationView.setPointerVisibility(false)
        initLiveDataObservers()
        initSpinner()
        initRadioGroupNotes()
    }

    private fun initLiveDataObservers() {
        val note = viewModel.getNote()
        val freq = viewModel.getFrequency()
        val position = viewModel.getPosition()

        note.observe(this, Observer<String> { tvNote.text = it })
        freq.observe(this, Observer<Double>{ tvFreq.text = getString(R.string.hz_string, it)/*"${String.format("%5.2f", it)} Hz"*/ })
        position.observe(this, Observer<Int> { moveNoteView(it) })
    }

    private fun initSpinner() {
        spinnerNotes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    rgStrings.visibility = View.INVISIBLE
                    viewModel.setConfig(
                        MainModel.WorkMode.ALL_NOTES,
                        GuitarFrequencies.frequencies[position]!!.first,
                        GuitarFrequencies.frequencies[position]!!.second,
                        -1
                    )
                } else {
                    rgStrings.visibility = View.VISIBLE
                    val string = 1
                    rbString1.isChecked = true
                    tvNote.text = GuitarFrequencies.frequencies[position]!!.second[string - 1]
                    viewModel.setConfig(
                        MainModel.WorkMode.SPECIFIC_NOTE,
                        GuitarFrequencies.frequencies[position]!!.first,
                        GuitarFrequencies.frequencies[position]!!.second,
                        string
                    )
                    rbString1.text = GuitarFrequencies.frequencies[position]!!.second[0]
                    rbString2.text = GuitarFrequencies.frequencies[position]!!.second[1]
                    rbString3.text = GuitarFrequencies.frequencies[position]!!.second[2]
                    rbString4.text = GuitarFrequencies.frequencies[position]!!.second[3]
                    rbString5.text = GuitarFrequencies.frequencies[position]!!.second[4]
                    rbString6.text = GuitarFrequencies.frequencies[position]!!.second[5]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }
        }
    }

    private fun initRadioGroupNotes() {
        rgStrings.setOnCheckedChangeListener { _, i ->
            when (i) {
                rbString1.id -> tvNote.text = setStringConfig(1)
                rbString2.id -> tvNote.text = setStringConfig(2)
                rbString3.id -> tvNote.text = setStringConfig(3)
                rbString4.id -> tvNote.text = setStringConfig(4)
                rbString5.id -> tvNote.text = setStringConfig(5)
                rbString6.id -> tvNote.text = setStringConfig(6)
            }
        }
    }

    private fun setStringConfig(i: Int): String {
        viewModel.setConfig(
            MainModel.WorkMode.SPECIFIC_NOTE,
            GuitarFrequencies.frequencies[spinnerNotes.selectedItemPosition]!!.first,
            GuitarFrequencies.frequencies[spinnerNotes.selectedItemPosition]!!.second,
            i
        )
        return GuitarFrequencies
            .frequencies[spinnerNotes.selectedItemId.toInt()]!!
            .second[i - 1]
    }
}
