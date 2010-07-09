package com.lhurtado.ringer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Ringer extends AppWidgetProvider {

	private static final String LOG_TAG = "RingerProvider";
	public static String CLICK = "Click";
    private static final int[] IMAGES = { R.drawable.loud, R.drawable.normal, R.drawable.vibrate, R.drawable.silence};
    
    public static final String SHARED_PREFS_NAME = "RingerSettings";
    SharedPreferences prefs;
        
    @Override
    public void onReceive(Context context, Intent intent) {
    	//Get action intent broadcast
    	final String action = intent.getAction(); 
    	Log.d(LOG_TAG, "onReceive(): "+action);
    	
    	// v1.5 fix that doesn't call onDelete Action 
    	if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
    		final int appWidgetId = intent.getExtras().getInt(
    				AppWidgetManager.EXTRA_APPWIDGET_ID, 
    				AppWidgetManager.INVALID_APPWIDGET_ID);
    		if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) { 
    			this.onDeleted(context, new int[] { appWidgetId });
    		} 
    	} else {
    		
    		//get current view
    		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
   			
    		//Preferences
        	prefs = PreferenceManager.getDefaultSharedPreferences(context);
        	int normal = prefs.getInt("normal_profile", 50);
			int loud = prefs.getInt("loud_profile", 100);
    		
    		//AudioManager auxiliary variables
   			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
   			int ringerMode = audioManager.getRingerMode();
   			int actualVolume  = audioManager.getStreamVolume(AudioManager.STREAM_RING);
   			int maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
   			
   			if(loud<=normal){
				loud = normal + 100/maxVolume;
			}
   			
   			normal = Math.round(normal*maxVolume/100);
   			loud = Math.round(loud*maxVolume/100);
   			
   			//Message for Toast
   			String msg = "Ringer Widget";
   			
   			//Update widget action
   			if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action) || action==null){
   				Log.d(LOG_TAG, "onReceive():ACTION_APPWIDGET_UPDATE ");

	   			// Create an Intent to handle click event
	            Intent clickIntent = new Intent(context, Ringer.class);
	            clickIntent.setAction(CLICK); 
	            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
	        	
	            //set OnClick event handler
	            remoteView.setOnClickPendingIntent(R.id.image, pendingIntent);
		        
	            //get actual ringer state and update widget icon
	            if (ringerMode==AudioManager.RINGER_MODE_NORMAL){
	            	if(actualVolume>normal){
		            	 remoteView.setImageViewResource(R.id.image, IMAGES[0]);
		            	 msg = "Loud Mode";
	            	}else{
	            		remoteView.setImageViewResource(R.id.image, IMAGES[1]);
		            	msg = "Normal Mode";
	            	}
	             }else if(ringerMode==AudioManager.RINGER_MODE_VIBRATE){
	            	 remoteView.setImageViewResource(R.id.image, IMAGES[2]);
	            	 msg = "Vibrate Mode";
	             }else if(ringerMode==AudioManager.RINGER_MODE_SILENT){
	            	 remoteView.setImageViewResource(R.id.image, IMAGES[3]);
	            	 msg = "Silent Mode";
	             }       
   			}
            
    		// Click event on widget
    		if (CLICK.equals(action)) {
    			
    			Log.d(LOG_TAG, "onReceive():CLICK ");
       			//Ringer State Change RINGER_MODE_LOUD-->RINGER_MODE_NORMAL-->RINGER_MODE_VIBRATE-->RINGER_MODE_SILENT
                if (ringerMode==AudioManager.RINGER_MODE_NORMAL){
                	if(actualVolume>normal){
                		remoteView.setImageViewResource(R.id.image, IMAGES[1]);
                    	msg = "Normal Mode";
                    	audioManager.setStreamVolume(AudioManager.STREAM_RING, normal, 0);
                	}else{
	                	remoteView.setImageViewResource(R.id.image, IMAGES[2]);
	                	msg = "Vibrate Mode";
	                	audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                	}
                }else if(ringerMode==AudioManager.RINGER_MODE_VIBRATE){
                	remoteView.setImageViewResource(R.id.image, IMAGES[3]);
                	msg = "Silent Mode";
                	audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }else if(ringerMode==AudioManager.RINGER_MODE_SILENT){
                	remoteView.setImageViewResource(R.id.image, IMAGES[0]);
                	msg = "Loud Mode";
                	audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                	audioManager.setStreamVolume(AudioManager.STREAM_RING, loud, 0);
                }
                
    		}
    		
    		//Capture external volume changes
    		if(AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)){
    			Log.d(LOG_TAG, "onReceive():RINGER_MODE_CHANGED_ACTION ");

	 	        //get actual ringer state and update widget icon
	             if (ringerMode==AudioManager.RINGER_MODE_NORMAL){
	            	 if(actualVolume>normal){
		            	 remoteView.setImageViewResource(R.id.image, IMAGES[0]);
		            	 msg = "Loud Mode";
	            	}else{
	            		remoteView.setImageViewResource(R.id.image, IMAGES[1]);
		            	msg = "Normal Mode";
	            	}
	             }else if(ringerMode==AudioManager.RINGER_MODE_VIBRATE){
	            	 remoteView.setImageViewResource(R.id.image, IMAGES[2]);
	            	 msg = "Vibrate Mode";
	             }else if(ringerMode==AudioManager.RINGER_MODE_SILENT){
	            	 remoteView.setImageViewResource(R.id.image, IMAGES[3]);
	            	 msg = "Silent Mode";
	             }      
    		}
    		
    		//Widget update
            ComponentName thisWidget = new ComponentName(context, Ringer.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context); 
            manager.updateAppWidget(thisWidget, remoteView);
    		
            //Notify user
   			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            
   			//Notify
//		    PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, intent, 0); 
//		    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		    Notification noty = new Notification(R.drawable.icon, "Button 1 clicked", System.currentTimeMillis());
//		    noty.setLatestEventInfo(context, "Notice", msg, notificationIntent);
//		    notificationManager.notify(1, noty);
            
            super.onReceive(context, intent); 
    	}
    }

}
