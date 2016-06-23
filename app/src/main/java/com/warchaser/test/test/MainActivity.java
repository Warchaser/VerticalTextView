package com.warchaser.test.test;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.warchaser.test.util.VerticalTextView;

public class MainActivity extends Activity
{
    private VerticalTextView mTextView1;

    private VerticalTextView mTextView2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mTextView1 = (VerticalTextView) findViewById(R.id.text1);
        mTextView2 = (VerticalTextView) findViewById(R.id.text2);
//
        mTextView1.setText(getResources().getString(R.string.test_string));
        mTextView2.setText(getResources().getString(R.string.test_string1));

    }
}
