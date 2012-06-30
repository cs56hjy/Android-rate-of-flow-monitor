package com.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class limit extends Activity{
	long limited;
	Bundle bunde;
	private EditText et;
	private Button bt1;
	private Button bt2;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trafficlimit);
		bt1= (Button)findViewById(R.id.button1);
		bt1.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limited = Long.parseLong(et.getText().toString())*1000000;
				Bundle bundle = new Bundle();
				bundle.putLong("pass", limited);
			}
		});
		bt2.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				limit.this.setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}