package com.example.filmeinandroid;



import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	// Service to handle liveCard publishing, etc...
    private boolean mIsBound = false;
    private DisplayMessageActivity parent = this;
    private Timer heartBeat = null;
    private int i = 0;
    
    private JSONObject jsonObj;
    private boolean isBlank = true;
    private long previousEnd = -1;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}
	
	
	
	
	

    @Override
    protected void onDestroy()
    {	
    	if (heartBeat != null) {
        	heartBeat.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	
    	super.onCreate(savedInstanceState);
    	
    	// Get the message from the intent
    	Intent intent = getIntent();
    	String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    	
    	// Create the text view
    	TextView textView = new TextView(this);
    	textView.setTextSize(40);
    	textView.setText(message);
    	
    	// Set the text view as the activity layout
    	setContentView(textView);
    	start();
    	
    	
    	
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("onResume() called.", "onResume() called.");

    }

    
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
		if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			Log.e("faalla", "alalaaaaaaa");
			
		}
    	
    	return true;
    }

	public void setText(String content) {
		// TODO Auto-generated method stub
		TextView textView = new TextView(this);
    	textView.setTextSize(40);
    	textView.setText(content);
    	
    	// Set the text view as the activity layout
    	setContentView(textView);
	}
	
	private void start() {
		Log.i("::: - start start", "::: - start start");
    	
        try {
			jsonObj = new JSONObject("{name:\"Test Movie\",subtitles:[{count:1,start:4000,endTime:6000,text:\"This is an example of a subtitle\"},{count:2,start:8000,endTime:12000,text:\"your momma!\"}, {count:3,start:15000,endTime:17000,text:\"What did you say you bastard?\"},{count:4,start:18000,endTime:19000,text:\"I said your momma!\"},{count:5,start:20000,endTime:25000,text:\"I said your momma2!\"}]}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			Log.d("jsonobj", jsonObj.getString("name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       Log.i("::: - start end", "::: - start emd");
       onServiceStart();
	}
	
	private boolean onServiceStart()
    {
        Log.d("onServiceStart() called.", "onServiceStart() called.");
        
        if(heartBeat == null) {
            heartBeat = new Timer();
        }
        setHeartBeat();
        updateCard(DisplayMessageActivity.this);
        

        return true;
    }
	
	private boolean onServiceStop()
    {
        Log.d("onServiceStop() called.", "onServiceStop() called.");
        if(heartBeat != null) {
            heartBeat.cancel();
        }
        // ...

        return true;
    }
	
	// This will be called by the "HeartBeat".
	private void updateCard(Context context)
    {
    	String content = "kkkk";
        try {
    		JSONArray subtitles = jsonObj.getJSONArray(("subtitles"));
            JSONObject j = subtitles.getJSONObject(i);
            if(isBlank)
            {
            	content = "";
            }	
            else
            {
            	content = j.getString("text");
            	updateText();
            }
            Log.i("content", "content: " + content);
            parent.setText(content);
            
            	
    	} catch(Exception e) {
    		Log.e("err", e.toString());
    	}
    }
	
	private void setHeartBeat()
    {
        final Handler handler = new Handler();
        TimerTask liveCardUpdateTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                        	//Log.e("timer updated", "timer updated")
                            setHeartBeat();
                            updateCard(DisplayMessageActivity.this);
                            
                        } catch (Exception e) {
                            Log.e("Failed to run the task.", "Failed to run the task." + e);
                        }
                    }

					
                });
            }
        };

        try {
        	JSONArray subtitles = jsonObj.getJSONArray(("subtitles"));
        	if(i >= subtitles.length())
        	{
        		onServiceStop();
        	}
            JSONObject j = subtitles.getJSONObject(i);
            long start = j.getLong("start");
            long endTime = j.getLong("endTime");
            if(!isBlank)
            {
            	if(start - previousEnd < 10) {
            		heartBeat.schedule(liveCardUpdateTask, endTime - start);
            		previousEnd = endTime;
            	}
            	else {
            		isBlank = true;
                	//Log.d("not blank", "" + (start - previousEnd));
                	heartBeat.schedule(liveCardUpdateTask, start - previousEnd);
                	previousEnd = endTime;
            	}
            }
            else {
            	//Log.d("isBlank","" + (endTime - start));
            	heartBeat.schedule(liveCardUpdateTask, endTime - start);
            	previousEnd = endTime;
            	isBlank = false;
            }
        } catch (Exception e)
        {
        	Log.e("error", e.toString());
        }
        
    }

    private void updateText() {
    	Log.d("d", "" + i);
    	i++;
	}

}
