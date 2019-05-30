package com.example.simplegt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class ActivityHelp extends AppCompatActivity {
    ImageButton buttontuner1, buttonauthor2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        buttontuner1 = findViewById(R.id.clicktuner1);
        buttonauthor2 = findViewById(R.id.clickauthor2);
    }

    public void ClickTuner1(View view) {
        Intent toTuner1 = new Intent(this, SimpleGuitarTunerActivity.class);
        startActivity(toTuner1);
    }

    public void ClickAuthor2(View view) {
        Intent toAuthor2 = new Intent(this, ActivityAuthor.class);
        startActivity(toAuthor2);
    }
}
