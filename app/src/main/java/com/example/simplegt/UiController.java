package com.example.simplegt;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.simplegt.SoundAnalyzer.AnalyzedSound;
import com.example.simplegt.SoundAnalyzer.ArrayToDump;
import com.example.simplegt.Tuning.GuitarString;

import java.util.Observable;
import java.util.Observer;

public class UiController implements Observer, OnItemSelectedListener{
    public static final String TAG = "RealGuitarTuner";

    private SimpleGuitarTunerActivity ui;
    private double frequency;
    private Tuning tuning = new Tuning(0);

    private enum MessageClass {
        TUNING_IN_PROGRESS,
        WEIRD_FREQUENCY,
        TOO_QUIET,
        TOO_NOISY,
    }

    MessageClass message;
    MessageClass previouslyProposedMessage;
    MessageClass proposedMessage; // needs to get X consecutive votes.
    private int numberOfVotes;
    private final int minNumberOfVotes = 3; // X.

    public UiController(SimpleGuitarTunerActivity u) {
        ui = u;
    }

    @Override
    public void update(Observable who, Object obj) {
        if(who instanceof SoundAnalyzer) {
            if(obj instanceof AnalyzedSound) {
                AnalyzedSound result = (AnalyzedSound)obj;
                // result.getDebug();
                frequency = FrequencySmoothener.getSmoothFrequency(result);
                if(result.error==AnalyzedSound.ReadingType.BIG_FREQUENCY ||
                        result.error==AnalyzedSound.ReadingType.BIG_VARIANCE ||
                        result.error==AnalyzedSound.ReadingType.ZERO_SAMPLES)
                    proposedMessage = MessageClass.TOO_NOISY;
                else if(result.error==AnalyzedSound.ReadingType.TOO_QUIET)
                    proposedMessage = MessageClass.TOO_QUIET;
                else if(result.error==AnalyzedSound.ReadingType.NO_PROBLEMS)
                    proposedMessage = MessageClass.TUNING_IN_PROGRESS;
                else {
                    proposedMessage=null;
                }
                if(ConfigFlags.uiControlerInformsWhatItKnowsAboutSound)
                    result.getDebug();
                //Log.e(TAG,"Frequency: " + frequency);
                updateUi();
            } else if(obj instanceof ArrayToDump) {
                ArrayToDump a = (ArrayToDump)obj;
                ui.dumpArray(a.arr, a.elements);
            }
        }
    }

    private void updateUi() {
        GuitarString current = tuning.getString(frequency);
        // Mark a string in red on a big picture of guitar.
        ui.changeString(current.stringId);

        double match = 0.0; // How close is current frequency to the desired
        // frequency in 0..1 scale.
        if(current.stringId == 0) {
            match = 0.0;
        } else {
            if(frequency<current.freq) {
                match = (frequency-current.minFreq)/(current.freq-current.minFreq);
            } else {
                match = (current.maxFreq - frequency )/(current.maxFreq-current.freq);
            }
        }
        // Update message.
        // If cannot decide on a string
        if(proposedMessage == MessageClass.TUNING_IN_PROGRESS && current.stringId == 0)
            proposedMessage = MessageClass.WEIRD_FREQUENCY;
        if(message == null) {
            message = previouslyProposedMessage = proposedMessage;
        } if(message == proposedMessage) {
            // do nothing.
        } else {
            if(previouslyProposedMessage != proposedMessage) {
                previouslyProposedMessage = proposedMessage;
                numberOfVotes = 1;
            } else if(previouslyProposedMessage == proposedMessage) {
                numberOfVotes++;
            }
            if(numberOfVotes >= minNumberOfVotes) {
                message = proposedMessage;
            }
        }
        switch(message) {
            case TUNING_IN_PROGRESS:
                ui.displayMessage("Настраиваем струну " + current.name +
                        " из " + tuning.getName() + ", настроено на " +
                        Math.round(100.0*match) + "%", true);
                break;
            case TOO_NOISY:
                ui.displayMessage("Кажется, у вас лишние шумы...", false);
                break;
            case TOO_QUIET:
                ui.displayMessage("Вы выбрали "+tuning.getName(), true);
                break;
            case WEIRD_FREQUENCY:
                ui.displayMessage("Вы уверены, что это гитара? :)", false);
            default:
                Log.d(TAG, "No message");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View thing, int itemno,
                               long rowno) {
        if(tuning.getTuningId() != itemno)
            tuning = new Tuning(itemno);
        Log.d(TAG,"Changed tuning to " + tuning.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
