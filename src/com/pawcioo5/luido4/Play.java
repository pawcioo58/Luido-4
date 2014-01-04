package com.pawcioo5.luido4;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Play extends Activity{
	MyReceiver myReceiver;
	Button play,loopbtn;
	ImageView albumCover;
	LinearLayout strona;
	long currentDuration, totalDuration;
    TextView tytul, wykonawca, time_1, time_2;;
    String title1, file, title2, fileName, motyw;
    Boolean play_start,uruch_act=false,album_show;
    int id_song=-1;
    SeekBar songProgressBar;
	Handler mHandler = new Handler();
    AudioManager audioManager;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    int  motyw_int,styl_int, ico_style;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		Log.d("xxxaxxxx","Uruchomienie");
	    strona = (LinearLayout) findViewById(R.id.linear);
        tytul = (TextView) findViewById(R.id.textView1);
        wykonawca = (TextView) findViewById(R.id.textView2);
        play = (Button) findViewById(R.id.button3);
        albumCover = (ImageView) findViewById(R.id.imageView1);
        songProgressBar = (SeekBar) findViewById(R.id.seekBar1);
        time_1 = (TextView) findViewById(R.id.textView3);
		time_2 = (TextView) findViewById(R.id.textView4);
		loopbtn = (Button) findViewById(R.id.button1);
		pobieranie_ustawien();
		if (play_start){
			Intent i = new Intent(Music_service.ACTION_LOADATA);
	        i.putExtra("start_play", true);
	        startService(i);
		}
		Log.d("xxxaxxxx","G³oœnoœæ : Multimedia");
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Log.d("xxxaxxxx","Deklaracja suwaka");
		 songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.d("xxxaxSEEK","Zakoñczono zmianê pozycji suwaka");
				mHandler.removeCallbacks(mUpdateTimeTask);
				int currentPosition = progressToTimer(seekBar.getProgress(), (int)totalDuration);
		        Intent i = new Intent(Music_service.ACTION_SEEK);
		        i.putExtra("seek", currentPosition);
		        startService(i);
		        updateProgressBar();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.d("xxxaxSEEK","Rozpoczêto zmianê pozycji suwaka");
				mHandler.removeCallbacks(mUpdateTimeTask);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.d("xxxaxSEEK","Trwa zmiana pozycji suwaka");
				}
		});
       
	}
	@Override
	protected void onStart() {
		Log.d("xxxaxxxx","START AKTYWNOSCI");
		uruch_act=true;
	    pobieranie_ustawien();
	      myReceiver = new MyReceiver();
	      Log.d("xxxaxxxx","START Serwisu z MY_ACTION");
	      
	      IntentFilter intentFilter = new IntentFilter();
	      intentFilter.addAction(Music_service.MY_ACTION);
	      registerReceiver(myReceiver, intentFilter);
	      startService(new Intent(Music_service.MY_ACTION));
	      
	      Intent i = new Intent(Music_service.ACTION_LOADATA);
	      i.putExtra("activity_on", true);
	      startService(i);
	        
	 super.onStart();
	}
	@Override
	protected void onStop() {
	Log.d("xxxaxxxx","Zatrzymanie AKTYWNOSCI");
	uruch_act=false;
	Intent i = new Intent(Music_service.ACTION_LOADATA);
    i.putExtra("activity_on", false);
    startService(i);
	 unregisterReceiver(myReceiver);
	 super.onStop();
	}
	private class MyReceiver extends BroadcastReceiver{
		 
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
			 Log.d("xxxa", "ODEBRANIE DANYCH");
			 tytul.setText(arg1.getStringExtra("title"));
			 wykonawca.setText(arg1.getStringExtra("artist") + " - "+ arg1.getStringExtra("album"));
			 currentDuration=arg1.getLongExtra("time1",0);
			 totalDuration=arg1.getLongExtra("time2",0);
			 time_2.setText(""+milliSecondsToTimer(totalDuration));
		     time_1.setText(""+milliSecondsToTimer(currentDuration));
			 int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
			 file = arg1.getStringExtra("file");
			 if(uruch_act)album_art(file);
			 uruch_act=false;
			 if (id_song != arg1.getIntExtra("nr",0))
	      											{
				 album_art(file);
				 id_song = arg1.getIntExtra("nr",0);
	      											}
	        songProgressBar.setProgress(progress);
		  if(arg1.getBooleanExtra("isPlaying", false)){
			  play.setBackgroundResource(R.drawable.pause_btn);
			  updateProgressBar();}
		  else {
			  play.setBackgroundResource(R.drawable.play_btn);
			  mHandler.removeCallbacks(mUpdateTimeTask);
		  }
		  
		  if(arg1.getBooleanExtra("Loop", false))
			  loopbtn.setBackgroundResource(R.drawable.repeat_press);
			else 
				loopbtn.setBackgroundResource(R.drawable.repeat_btn);
		 }
		 
		}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
		
	}
*/

public String milliSecondsToTimer(long milliseconds){
    String finalTimerString = "";
    String secondsString = "";
       int hours = (int)( milliseconds / (1000*60*60));
       int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
       int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
       if(hours > 0){
           finalTimerString = hours + ":";
       }

       if(seconds < 10){
           secondsString = "0" + seconds;
       }else{
           secondsString = "" + seconds;}
finalTimerString = finalTimerString + minutes + ":" + secondsString;

     return finalTimerString;
}
public int getProgressPercentage(long currentDuration, long totalDuration){
    Double percentage = (double) 0;

    long currentSeconds = (int) (currentDuration / 1000);
    long totalSeconds = (int) (totalDuration / 1000);

    percentage =(((double)currentSeconds)/totalSeconds)*100;

    return percentage.intValue();
}

private Runnable mUpdateTimeTask = new Runnable() {
    public void run() {
    	time_2.setText(""+milliSecondsToTimer(totalDuration));
        time_1.setText(""+milliSecondsToTimer(currentDuration));
        int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
        songProgressBar.setProgress(progress);

        mHandler.postDelayed(this, 1000);
    }
 };
public void updateProgressBar() {
    mHandler.postDelayed(mUpdateTimeTask, 1000);
}
private int progressToTimer(int progress, int totalDuration) {
	int currentDuration = 0;
    totalDuration = (int) (totalDuration / 1000);
    currentDuration = (int) ((((double)progress) / 100) * totalDuration);

    return currentDuration * 1000;
}
public void play(View target) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
	Log.d("xxxbxxxxx", "Wciœniêcie Play");
	startService(new Intent(Music_service.ACTION_PLAY));
    }

public void next(View target) {
	Log.d("xxxbxxxxx", "Wciœniêcie Next");
	startService(new Intent(Music_service.ACTION_NEXT));
	
}
public void back(View target) {
	Log.d("xxxbxxxxx", "Wciœniêcie Back");
	startService(new Intent(Music_service.ACTION_BACK));
	
}
public void show_playlist(View target){
	Log.d("xxxbxxxxx", "Wciœniêcie playlist");
	       Intent intent = new Intent(this, MainActivity.class);
             startActivity(intent);
			}

//private static final int RESULT_SETTINGS = 1;
public void btn_settings(View target){
    Intent i = new Intent(this, SettingsActivity.class);
    //startActivityForResult(i, RESULT_SETTINGS);
    startActivity(i);
}

public void btn_loop(View target){
    Log.d("xxxbxxxxx", "Wciœniêcie Loop");
	startService(new Intent(Music_service.ACTION_LOOP));
}
void album_art(String path){
    Log.d("xxxaxxxxx", "£adowanie ok³adki albumu");
	mmr.setDataSource(path);
	byte[] imgs = mmr.getEmbeddedPicture();
	if(mmr.getEmbeddedPicture()!=null&&album_show){
	albumCover.setImageBitmap(BitmapFactory.decodeByteArray(imgs, 0,imgs.length));
	}
	else{
		albumCover.setImageResource(R.drawable.okladka);		
	}
}
public void pobieranie_ustawien(){
    Log.d("xxxaxxxxx", "Pobieranie ustawieñ aplikacji");
	SharedPreferences sharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(this);
	 play_start = sharedPrefs.getBoolean("example_checkbox", false);
	 album_show = sharedPrefs.getBoolean("art_show", false);
	 motyw = sharedPrefs.getString("motyw", "3");
	 styl_int = Integer.parseInt(motyw);
	 switch(styl_int){
	 case 1: motyw_int = 0xFF1988ad; ico_style = R.drawable.ble;break;
	 case 2: motyw_int = 0xFFb91a56; ico_style = R.drawable.bur;break;
	 case 3: motyw_int = 0xff000000; ico_style = R.drawable.nieb;break;
	 case 4: motyw_int = 0xFFb91a1a; ico_style = R.drawable.czer;break;
	 case 5: motyw_int = 0xFF314a9c; ico_style = R.drawable.nieb;break;
	 case 6: motyw_int = 0xFFf63f00; ico_style = R.drawable.pom;break;
	 case 7: motyw_int = 0xFFb91aa4; ico_style = R.drawable.roz;break;
	 case 8: motyw_int = 0xFF1f9a25; ico_style = R.drawable.ziel;break;
	 	 }
	 strona.setBackgroundColor(motyw_int);
	}
}
