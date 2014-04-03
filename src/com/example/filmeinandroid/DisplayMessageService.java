/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.filmeinandroid;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;



/**
 * Service owning the LiveCard living in the timeline.
 */
public class DisplayMessageService extends Service /*implements AsyncResponse*/{




    
    private Timer heartBeat = null;
    private int i = 0;
    
    private JSONObject jsonObj;
    private boolean isBlank = true;
    private long previousEnd = -1;
    
    private DisplayMessageActivity parent;
    public class LocalBinder extends Binder {
        public DisplayMessageService getService() {
            return DisplayMessageService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	 //ArrayList<String> voiceResults = intent.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
    	 //publishCard(this);
         //Log.d("speech + n", voiceResults.toString());

         //new ASyncGetData().execute();
    	Log.i("::: - start start", "::: - start start");
    	
         try {
			jsonObj = new JSONObject("{name:\"Test Movie\",subtitles:[{count:1,start:4000,endTime:6000,text:\"This is an example of a subtitle\"},{count:2,start:8000,endTime:12000,text:\"your momma!\"}, {count:3,start:15000,endTime:17000,text:\"What did you say you bastard?\"},{count:4,start:18000,endTime:19000,text:\"I said your momma!\"}]}");
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
        
        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        if (heartBeat != null) {
        	heartBeat.cancel();
        }
        super.onDestroy();
    }
    
    public void start() {
    	onServiceStart();
    }
    
    private boolean onServiceStart()
    {
        Log.d("onServiceStart() called.", "onServiceStart() called.");
        
        if(heartBeat == null) {
            heartBeat = new Timer();
        }
        setHeartBeat();
        updateCard(DisplayMessageService.this);
        

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
            }
            parent.setText(content);
            updateText();
            	
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
                            updateCard(DisplayMessageService.this);
                            
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

	public void setActivity(DisplayMessageActivity parent) {
		// TODO Auto-generated method stub
		this.parent = parent;
	}
	
	public void playPause(View view) {
	    // Do something in response to button
		Log.i("ddd","ddddddddddddddddd");
	}
}
