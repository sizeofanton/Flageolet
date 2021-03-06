package com.sizeofanton.flageolet.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.sizeofanton.flageolet.R
import com.sizeofanton.flageolet.contract.MainContract
import com.sizeofanton.flageolet.data.local.GuitarFrequencies
import com.sizeofanton.flageolet.data.model.MainModel
import com.sizeofanton.flageolet.utils.ext.vibratePhone
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val PERMISSION_CODE = 101
private const val START_RECORDING_DELAY = 1200L

class MainActivity : AppCompatActivity(), MainContract.View {

    private val viewModel: MainViewModel by viewModel()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (!checkMicroPermission()) requestMicroPermission()
        else viewModel.startRecording(0L)

        initUI()
        if (savedInstanceState != null) restoreState(savedInstanceState)
    }


    private fun checkMicroPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMicroPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startRecording(0L)
                } else {
                    finish()
                }
            }
        }
    }

    private fun moveNoteView(position: Int) {
        noteDeviationView.pointerVisible = true
        when (position) {
            in 50..Int.MAX_VALUE -> {
                noteDeviationView.pointerPosition = 50
                noteDeviationView.setPointerColor(getColorCompat(R.color.pointerBad))
            }
            in Int.MIN_VALUE..-50 -> {
                noteDeviationView.pointerPosition = 50
                noteDeviationView.setPointerColor(getColorCompat(R.color.pointerBad))
            }
            in -1..1 -> {
                viewModel.stopRecording()
                vibratePhone()
                //playSound(R.raw.success)
                noteDeviationView.pointerPosition = 0
                noteDeviationView.setPointerColor(getColorCompat(R.color.pointerGood))
                tvNote.setTextColor(getColorCompat(R.color.pointerGood))
                tvFreq.setTextColor(getColorCompat(R.color.pointerGood))
                handler.postDelayed({
                    tvNote.setTextColor(getColorCompat(R.color.colorAccent))
                    tvFreq.setTextColor(getColorCompat(R.color.colorAccent))
                }, START_RECORDING_DELAY)
                viewModel.startRecording(START_RECORDING_DELAY)
            }
            else -> {
                noteDeviationView.pointerPosition = position
                noteDeviationView.setPointerColor(getColorCompat(R.color.pointerNeutral))
            }


        }
    }

    private fun initUI() {
        noteDeviationView.pointerVisible = false
        initLiveDataObservers()
        initSpinner()
        initRadioGroupNotes()
    }

    private fun initLiveDataObservers() {
        val note = viewModel.getNote()
        val freq = viewModel.getFrequency()
        val position = viewModel.getPosition()

        note.observe(this, Observer<String> { tvNote.text = it })
        freq.observe(this, Observer<Double> { tvFreq.text = getString(R.string.hz_string, it)/*"${String.format("%5.2f", it)} Hz"*/ })
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
                rbString1.id -> setStringConfig(1)
                rbString2.id -> setStringConfig(2)
                rbString3.id -> setStringConfig(3)
                rbString4.id -> setStringConfig(4)
                rbString5.id -> setStringConfig(5)
                rbString6.id -> setStringConfig(6)
            }
        }
    }

    private fun setStringConfig(i: Int) {
        viewModel.setConfig(
            MainModel.WorkMode.SPECIFIC_NOTE,
            GuitarFrequencies.frequencies[spinnerNotes.selectedItemPosition]!!.first,
            GuitarFrequencies.frequencies[spinnerNotes.selectedItemPosition]!!.second,
            i
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("Note", tvNote.text.toString())
        outState.putString("Freq", tvFreq.text.toString())
        super.onSaveInstanceState(outState)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        val note = savedInstanceState.getString("Note", "?")
        val freq = savedInstanceState.getString("Freq", "0 Hz")
        tvNote.text = note
        tvFreq.text = freq
    }

    private fun getColorCompat(id: Int): Int = ContextCompat.getColor(this, id)
}
