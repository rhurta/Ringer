package com.lhurtado.ringer;

import com.hlidskialf.android.preference.SeekBarPreference;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class RingerSettings extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String CONFIGURE_ACTION="android.appwidget.action.APPWIDGET_CONFIGURE";
	
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(Ringer.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
/*        SeekBarPreference sb = (SeekBarPreference)findViewById(R.id.normal_profile);
        Context context = RingerSettings.this;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			//Log.v("RP","onKeyDown-->onBackPressed()");
		}
		return(super.onKeyDown(keyCode, event));
	}

	@Override
	public void onBackPressed() {
		if (CONFIGURE_ACTION.equals(getIntent().getAction())) {
			final Context context = RingerSettings.this;
			
			Intent intent=getIntent();
			Bundle extras=intent.getExtras();

			if (extras!=null) {
				int id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
									   AppWidgetManager.INVALID_APPWIDGET_ID);
				
				AppWidgetManager mgr = AppWidgetManager.getInstance(context);
				
				RemoteViews views = new RemoteViews(getPackageName(),R.layout.widget);
				mgr.updateAppWidget(id, views);
				
				Intent result = new Intent();
				result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
				setResult(RESULT_OK, result);
				sendBroadcast(new Intent(this,Ringer.class));
			}
		}
		super.onBackPressed();
	} 
}
