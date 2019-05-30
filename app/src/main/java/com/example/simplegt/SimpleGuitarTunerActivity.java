package com.example.simplegt;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
public class SimpleGuitarTunerActivity extends AppCompatActivity {

    public static final String TAG = "GuitarTuner";

    private final boolean LAUNCHANALYZER = true;


    private ImageView guitar = null;
    private Spinner tuningSelector = null;
    private SoundAnalyzer soundAnalyzer = null ;
    private UiController uiController = null;
    private TextView mainMessage = null;
    public ImageButton buttonhelp1, buttonauthor1;
    private BitmapFactory.Options bitmapOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiController = new UiController(this);
        if(LAUNCHANALYZER) {
            try {
                soundAnalyzer = new SoundAnalyzer();
            } catch(Exception e) {
                Toast.makeText(this, "Проблемы с микрофоном :(", Toast.LENGTH_LONG ).show();
                Log.e(TAG, "Exception SoundAnalyzer: " + e.getMessage());
            }
            soundAnalyzer.addObserver(uiController);
        }
        buttonhelp1 = findViewById(R.id.clickhelp1);
        buttonauthor1 = findViewById(R.id.clickauthor1);
        guitar = findViewById(R.id.guitar);
        mainMessage = findViewById(R.id.mainMessage);
        tuningSelector = findViewById(R.id.tuningSelector);
        Tuning.populateSpinner(this, tuningSelector);
        tuningSelector.setOnItemSelectedListener(uiController);
    }

    public void ClickHelp1(View view){
        Intent toHelp1 = new Intent(this, ActivityHelp.class);
        startActivity(toHelp1);
    }

    public void ClickAuthor1(View view){
        Intent toAuthor1 = new Intent(this, ActivityAuthor.class);
        startActivity(toAuthor1);
    }

    private Map<Integer, Bitmap> preloadedImages;

    private Bitmap getAndCacheBitmap(int id) {
        if(preloadedImages == null)
            preloadedImages = new HashMap<>();
        Bitmap ret = preloadedImages.get(id);
        if(ret == null) {
            if(bitmapOptions == null) {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 4; // The higher it goes, the smaller the image.
            }
            ret = BitmapFactory.decodeResource(getResources(), id, bitmapOptions);
            preloadedImages.put(id, ret);
        }
        return ret;
    }

    public void dumpArray(final double [] inputArray, final int elements) {
        final double [] array = new double[elements];
        for(int i=0; i<elements; ++i)
            array[i] = inputArray[i];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = "Chart_" + (int)(Math.random()*1000) + ".data";
                    FileOutputStream fOut = openFileOutput(name,
                            Context.MODE_WORLD_READABLE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    // Write the string to the file
                    for(int i=0; i<elements; ++i)
                        osw.write("" + array[i] + "\n");
                    /* ensure that everything is
                     * really written out and close */
                    osw.flush();
                    osw.close();
                } catch(Exception e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        }).start();
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(ConfigFlags.menuKeyCausesAudioDataDump) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                soundAnalyzer.dumpAudioDataRequest();
                return true;
            }
        }
        return false;
    }

    private int [] stringNumberToImageId = new int[]{
            R.drawable.strings0,
            R.drawable.strings1,
            R.drawable.strings2,
            R.drawable.strings3,
            R.drawable.strings4,
            R.drawable.strings5,
            R.drawable.strings6
    };

    int oldString = 0;
    // 1-6 strings , 0 - no string.
    public void changeString(int stringId) {
        if(oldString!=stringId) {
            guitar.setImageBitmap(getAndCacheBitmap(stringNumberToImageId[stringId]));
            oldString=stringId;
        }
    }

    public void displayMessage(String msg, boolean positiveFeedback) {
        int textColor = positiveFeedback ? Color.rgb(0,0,0) : Color.rgb(130,17,17);
        mainMessage.setText(msg);
        mainMessage.setTextColor(textColor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(soundAnalyzer!=null)
            soundAnalyzer.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(soundAnalyzer!=null)
            soundAnalyzer.ensureStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(soundAnalyzer!=null)
            soundAnalyzer.stop();
    }


}
