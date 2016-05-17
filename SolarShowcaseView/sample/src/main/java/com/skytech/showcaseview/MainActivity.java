package com.skytech.showcaseview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skytech.solar.view.showcase.IOnCompleteListener;
import com.skytech.solar.view.showcase.SolarShowcaseQueue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv1 = (TextView) findViewById(R.id.tv1);
        TextView tv2 = (TextView) findViewById(R.id.tv2);
        TextView tv3 = (TextView) findViewById(R.id.tv3);
        Button btn = (Button) findViewById(R.id.button);
        Button btn2 = (Button) findViewById(R.id.button2);

        SolarShowcaseQueue sequence = new SolarShowcaseQueue(this/*, "ID"*/);
        sequence.addShowcaseView(tv1, "TITLE1", "ONTENT1", "DISMISS1", "SKIP1");
        sequence.addShowcaseView(tv2, "TITLE2", "ONTENT2", "DISMISS2", "SKIP2");
        sequence.addShowcaseView(tv3, "TITLE3", "ONTENT3", "DISMISS3", "SKIP");
        sequence.addShowcaseView(btn, "TITLE3", "ONTENT3", "DISMISS3", "SKIP");
        sequence.addShowcaseView(btn2, "TITLE3", "ONTENT3", "DISMISS3", "SKIP");
        //sequence.show();
        sequence.show(new IOnCompleteListener() {
            @Override
            public void onCompleteListener() {
                Toast.makeText(MainActivity.this, "ON COMPLETE!", Toast.LENGTH_SHORT).show();
            }
        });
        /*new SolarShowcaseView.Builder(this)
                .setTarget(tv1)
                .setTitle("TITLE")
                .setMessage("CONTENT")
                .setDismissButton("DISMISS")
                .setSingleUse("ID")
                .setSkipButton("SKIP")
                .show();*/

    }
}
