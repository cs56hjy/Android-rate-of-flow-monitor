package com.test;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.os.Bundle;

import java.io.*; 
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Calendar;

import android.os.*;
import android.widget.Button;
import android.net.*;



public class JustatestActivity extends Activity {
    /** Called when the activity is first created. */
	private static final int df = 1024;
    Button setLimit;
    Button setTimer;
    DateFormat fmt = DateFormat.getTimeInstance();
	Calendar c=Calendar.getInstance();
	Calendar now;
	TimePicker tp;
    TextView presentTime; 
    TextView limitLiuliang;
    TextView[] countup=new TextView[4];
    long[] temp = {0,0,0,0};
    long[] temp2 = {0,0,0,0};
    long limit=300000000;
    
	public ConnectivityManager no(){
		ConnectivityManager m =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return m;
	}
    
	public void readFileByLines(String fileName, long[] temp) throws IOException  {
		try{
		DataInputStream file;
		file = new DataInputStream(new FileInputStream(fileName));
		for(int line=0;line<4;line++){
			temp[line] = (long) file.readByte();
		}
		}catch(IOException e){
			FileOutputStream output  = this.openFileOutput(fileName,Context.MODE_PRIVATE);
			output.write(0);
			output.write(0);
			output.write(0);
			output.write(0);
			output.close();
		}
	}
	
	public void saveFile(String fileName, long[] temp) throws IOException {
		DataOutputStream output = new DataOutputStream(this.openFileOutput(fileName, Context.MODE_PRIVATE));
		for(int i=0;i<temp.length;i++){
				output.writeLong(temp[i]);
		}
		output.close();
	}
	
	//
	 private boolean gprsEnabled(boolean bEnable) 
	    { 
	        Object[] argObjects = null; 
	                 
	        boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled"); 
	        if(isOpen == !bEnable) 
	        { 
	            setGprsEnabled("setMobileDataEnabled", bEnable); 
	        } 
	         
	        return isOpen;   
	    } 
	     
	    //检测GPRS是否打开 
	    private boolean gprsIsOpenMethod(String methodName) 
	    { 
	        Class<? extends ConnectivityManager> cmClass       = no().getClass(); 
	        Class[] argClasses  = null; 
	        Object[] argObject  = null; 
	         
	        Boolean isOpen = false; 
	        try 
	        { 
	            Method method = cmClass.getMethod(methodName, argClasses); 
	 
	            isOpen = (Boolean) method.invoke(no(), argObject); 
	        } catch (Exception e) 
	        { 
	            e.printStackTrace(); 
	        } 
	 
	        return isOpen; 
	    } 
	     
	    //开启/关闭GPRS 
	    private void setGprsEnabled(String methodName, boolean isEnable) 
	    { 
	        Class cmClass       = no().getClass(); 
	        Class[] argClasses  = new Class[1]; 
	        argClasses[0]       = boolean.class; 
	         
	        try 
	        { 
	            Method method = cmClass.getMethod(methodName, argClasses); 
	            method.invoke(no(), isEnable); 
	        } catch (Exception e) 
	        { 
	            e.printStackTrace(); 
	        } 
	    } 

	
	TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			updateLable();
		}
	};
	

	Handler update =new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		for(int i=0; i<4; i++){
    			switch(i){
    			case 0: temp2[i] = TrafficStats.getMobileRxBytes() + temp[i];
    					countup[i].setText(Long.toString(temp2[i]/df)+" Kb");
    				break;
    			case 1: temp2[i] = TrafficStats.getMobileRxPackets() + temp[i];
    					countup[i].setText(Long.toString(temp2[i]));
    				break;
    			case 2:	temp2[i] = TrafficStats.getMobileTxBytes() + temp[i];
    					countup[i].setText(Long.toString(temp2[i]/df)+" Kb");
    				break;
    			case 3:	temp2[i] = TrafficStats.getMobileTxPackets() + temp[i];
    					countup[i].setText(Long.toString(temp2[i]));
    				break;        		
    			}
    		}
    		now = Calendar.getInstance();
    		if(c.getTime()==now.getTime()){
    			gprsEnabled(false);
    		}
    	}
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setLimit = (Button)findViewById(R.id.button1);
        setTimer = (Button)findViewById(R.id.button2);
        countup[0] =(TextView)findViewById(R.id.textView1);
        countup[1] =(TextView)findViewById(R.id.textView2);
        countup[2] =(TextView)findViewById(R.id.textView3);
        countup[3] =(TextView)findViewById(R.id.textView4);
        presentTime = (TextView)findViewById(R.id.textView9);

        setLimit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(JustatestActivity.this, limit.class);
				startActivity(intent);
			}
        	
        });
        
        setTimer.setOnClickListener( new Button.OnClickListener() 
        {
        	public void onClick(View v)
        	{
        		new TimePickerDialog(JustatestActivity.this,t,c.get(Calendar.HOUR_OF_DAY),
        				c.get(Calendar.MINUTE),true).show();
        	}
        });
    }

    	void updateLable(){
    		presentTime.setText("当前限制时间: " + fmt.format(c.getTime()) );
    	}      

    public void onStart(){
    	super.onStart();
    	updateLable();
    	Intent intent;
    	Bundle bunde;
        try {
			readFileByLines("statistic.dat",temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        intent=this.getIntent();
        bunde = intent.getExtras();
        //bunde.getLong("pass");
        Thread account =new Thread(new Runnable(){
        	public void run(){
        		try{       		
        			while(true){
        				if(gprsIsOpenMethod("getMobileDataEnabled")){
        					Thread.sleep(1000);
        		    		limitLiuliang.setText("每月限额：" + limit/1000000 + " Mb");
        					update.sendMessage(update.obtainMessage());
        				}
        			}
        		}catch(Throwable t){
        			
        		}
        	}
        });
        account.start();
    }
    
    public void onStop(){
    	super.onStop();
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	try {
			saveFile("statistic.dat",temp2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}


    
