package com.ipragmatech.aws.cognito.cognitousersample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

import com.ipragmatech.aws.cognito.cognitousersample.R;
import com.ipragmatech.aws.cognito.cognitousersample.utils.AppHelper;

/**
 * Created by neha on 21/12/17.
 */

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        TextView userName = (TextView) findViewById(R.id.usernameTxtview);
        if (AppHelper.getItemCount()>0) {
            userName.setText(String.format(" Welcome %s!",AppHelper.getItemForDisplay(0).getDataText()));
        }
    }
}
