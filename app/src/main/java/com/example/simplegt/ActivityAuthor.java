package com.example.simplegt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class ActivityAuthor extends AppCompatActivity {
    ImageButton buttontuner2, buttonhelp2;
    ImageButton imgButton1, imgButton2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        buttontuner2 = findViewById(R.id.clicktuner2);
        buttonhelp2 = findViewById(R.id.clickhelp2);
        imgButton1 = findViewById(R.id.git);
        imgButton2 = findViewById(R.id.vk);
    }

    public void ClickTuner2(View view) {
        Intent toTuner2 = new Intent(this, SimpleGuitarTunerActivity.class);
        startActivity(toTuner2);
    }

    public void ClickHelp2(View view) {
        Intent toHelp2 = new Intent(this, ActivityHelp.class);
        startActivity(toHelp2);
    }

    public void GitClick(View view) {
        Intent GitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/captaincod"));
        startActivity(GitIntent);
    }

    public void VKClick(View view) {
        Intent VKIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/capcod"));
        startActivity(VKIntent);
    }
}
