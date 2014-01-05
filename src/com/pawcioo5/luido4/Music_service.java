package com.pawcioo5.luido4;

import java.io.IOException;

import com.pawcioo5.luido4.MusicDbAdapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
public class Music_service extends
Service implements OnCompletionListener {
	public static final String ACTION_PLAY = "com.pawcioo5.luido5.action.PLAY";
    public static final String ACTION_NEXT = "com.pawcioo5.luido5.action.NEXT";
    public static final String ACTION_BACK = "com.pawcioo5.luido5.action.BACK";
    public static final String ACTION_LOOP = "com.pawcioo5.luido5.action.LOOP";
    //public static final String ACTION_RANDOM = "com.pawcioo5.luido5.action.RANDOM";
    public static final String ACTION_PLAYTO = "com.pawcioo5.luido5.action.PLAYTO";
    public static final String ACTION_LOADATA = "com.pawcioo5.luido5.action.LOADATA";
    public static final String ACTION_SEEK = "com.pawcioo5.luido5.action.SEEK";
    public static final String MY_ACTION = "com.pawcioo5.luido5.action.MY_ACTION";

    private static final int NOTIFY_ID = 1;
    private MusicIntentReceiver myReceiver;
    MediaPlayer player;
    AudioManager audioManager;
    private MusicDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    String fileName, tytol="null",artysta="null",album="null", art="null";
    int Currentsong=0, koniec_listy;
    Boolean loop=false, play_start=false,wznow_call=true,aktywnosc_on=false, zmiana=false, tred=false,sluchawki_online=false,sluchawki_call=false, sluchawki_pause=true,uruch_phone=false,playing=false;
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("xxxaxxxxx", "Tworzenie serwisu");
        mDbHelper = new MusicDbAdapter(this);
        player=new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnCompletionListener(this); 
        mDbHelper.open();
       // SharedPreferences sharedPrefs = PreferenceManager
        //        .getDefaultSharedPreferences(this);

        mNotesCursor = null;
        Log.d("xxxaxxxxx", "Wczytywanie playlisty");
        mNotesCursor= mDbHelper.fetchAllSongs();
        mNotesCursor.moveToPosition(Currentsong);
        if(mNotesCursor.getCount()>0){
        akt_dane(Currentsong);
    	tryplay();
    	if(play_start)player.start();
        }
        Log.d("xxxxxxxx","S³uchawki");
        	//*********************************************
          	// 				S³uchawki
          	//*********************************************
        	myReceiver = new MusicIntentReceiver(); 
          	IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
          	registerReceiver(myReceiver, filter);
  	    
          	//***************************************
          	//				incoming call
          	//***************************************
          	przychodzace_polaczenie();
		
    }
    public int onStartCommand(Intent intent, int
flags, int startId) {
    	String action = intent.getAction();
        Log.d("xxxaxxxxx", "Odczytanie akcji");
        if (action.equals(ACTION_PLAY)) processPlayRequest();
        else if (action.equals(ACTION_NEXT)) processNextRequest();
        else if (action.equals(ACTION_BACK)) processBackRequest();
        else if (action.equals(ACTION_LOOP)) processLoopRequest();
        //else if (action.equals(ACTION_RANDOM)) processRandomRequest();
        else if (action.equals(ACTION_LOADATA)) processLoadataRequest(intent);
        else if (action.equals(ACTION_PLAYTO)) processPlaytoRequest(intent);
        else if (action.equals(MY_ACTION)) processSenddataRequest();
        else if (action.equals(ACTION_SEEK)) processSEEKRequest(intent);
        return 1;
    }

    private void processSEEKRequest(Intent intent) {
    	if (player!= null){
    	 player.seekTo(intent.getIntExtra("seek",0));
    	}
	}
	public void processSenddataRequest(){
    	 Log.d("xxxaxxxxx", "Wysy³anie danych ");
    	 if(mNotesCursor.getCount()>0)send_data_to_activity();
    }
	public class MyThread extends Thread{
    	 
    	 @Override
    	 public void run() {
    		 tred=true;
    		 while(player.isPlaying()&& aktywnosc_on){
    	   try {
    		   
    	    
    	    send_data_to_activity();
    	    
    	       Thread.sleep(1000);
    	      
    	   } catch (InterruptedException e) {
    	    e.printStackTrace();
    	   }
    	   }
    		 tred=false;
    	 }

		
    	 
    	} void processPlaytoRequest(Intent intent) {
    	Currentsong = intent.getIntExtra("position",0);
    	play(Currentsong);
		
	}
    	public void send_data_to_activity() {
			Intent intent = new Intent();
 	       intent.setAction(MY_ACTION);
 	      Log.d("xxxaxxxxxxxxxxxxx","Wysy³anie danych z service");
 	      intent.putExtra("title", tytol);
 	      intent.putExtra("artist", artysta);
 	      intent.putExtra("album", album);
 	      //intent.putExtra("okladka", 1);
 	      intent.putExtra("file", fileName);
 	      intent.putExtra("nr", mNotesCursor.getPosition());
 	      if(player!=null)intent.putExtra("isPlaying", player.isPlaying());
 	      intent.putExtra("Loop", loop);
 	      long time1, time2=0;
 	      time1 = player.getDuration();
 	      time2 = player.getCurrentPosition();
 	      //Log.d("xxxaxxxxxxxxxxxxx", "timer1 : "+Long.toString(time2));
 	      intent.putExtra("time1", time2);
 	      intent.putExtra("time2", time1);
 	      sendBroadcast(intent);
		}
	private void processLoadataRequest(Intent intent) {
    	mNotesCursor= mDbHelper.fetchAllSongs();
    	 SharedPreferences sharedPrefs = PreferenceManager
                 .getDefaultSharedPreferences(this);
    	 aktywnosc_on = intent.getBooleanExtra("activity_on",true);
    	 if(aktywnosc_on&&!zmiana&&!tred&&player.isPlaying()){
    			MyThread myThread = new MyThread();
    	        myThread.start();
    	        zmiana=true;
    	        }
    	play_start = sharedPrefs.getBoolean("example_checkbox", false);
    	koniec_listy = Integer.parseInt(sharedPrefs.getString("example_list", "-1"));
        if(intent.getBooleanExtra("start_play",false)&&mNotesCursor.getPosition()<mNotesCursor.getCount()&&mNotesCursor.getCount()>0)player.start();
	}
	private void processNextRequest() {
    	if(mNotesCursor.getCount()>0)
    		next_song();
	}
	private void next_song() {
		if(Currentsong+1<mNotesCursor.getCount()){
			Currentsong++;
					}
			else{
				Currentsong=0;
			}
		play(Currentsong);
	}
	private void processBackRequest() {
		if(mNotesCursor.getCount()>0)
			back_song();
	}
	private void back_song() {
		if(Currentsong-1>=0){
			Currentsong=Currentsong-1;
			}
			else
				Currentsong=mNotesCursor.getCount()-1;
		play(Currentsong);
	}
	private void processLoopRequest() {
		
		if(loop){loop=false; }
		else {loop=true;
				}
		processSenddataRequest();
	}
	//private void processRandomRequest() {	}
	
	private class MusicIntentReceiver extends BroadcastReceiver {
	    @Override public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
	            int state = intent.getIntExtra("state", -1);
	            switch (state) {
	            case 0:
	            	if(player.isPlaying()&&uruch_phone){
	            		pause_music();
	            	}
	            	sluchawki_online=false;
	                break;
	            case 1:
	            	if(sluchawki_pause)play_music();
	            	uruch_phone=true;
	            	sluchawki_online=true;
	                break;
	            default:
	            		                
	            }
	        }
	    }
	}
	public void play(int position_song){
		playing=true;
		Log.d("xxxaxxxxx", "Przeniesienie kursora");
		akt_dane(position_song);
		notification();
		tryplay();
		player.start();
		if(!zmiana&&!tred&&aktywnosc_on){
		MyThread myThread = new MyThread();
        myThread.start();
        zmiana=true;}
		processSenddataRequest();
	}

	private void tryplay() {
		try {
			Log.d("xxxaxxxxx", "Próba odtworzenia");
	        player.reset();
	        player.setDataSource(fileName);
	        player.prepare();
	        Log.d("xxxaxxxxx", "Odtwarzanie");
	    } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void akt_dane(int position_song) {
		mNotesCursor.moveToPosition(position_song);
		Log.d("xxxxxxxxx", "Czytanie danych z Bazy");
		fileName = mNotesCursor.getString(mNotesCursor.getColumnIndex(mDbHelper.KEY_FILE));
		tytol = mNotesCursor.getString(mNotesCursor.getColumnIndex(mDbHelper.KEY_TITLE));
		artysta = mNotesCursor.getString(mNotesCursor.getColumnIndex(mDbHelper.KEY_ARTIST));
		album = mNotesCursor.getString(mNotesCursor.getColumnIndex(mDbHelper.KEY_ALBUM));
	}
	@SuppressWarnings("deprecation")
	private void notification() {
		final NotificationManager mgr=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification note=new Notification(R.drawable.ico2,"Odtwarzanie : " + tytol, System.currentTimeMillis());
        PendingIntent i=PendingIntent.getActivity(this, 0,new Intent(this, Play.class),0);
         note.setLatestEventInfo(this, tytol,artysta + " - " + album, i);
    mgr.notify(NOTIFY_ID, note);
	}
	private void processPlayRequest() {
		if(mNotesCursor.getCount()>0)
    	play_song();
    	}
	private void play_song() {
		
		if(player.isPlaying()==true)
		{
			pause_music();
		}
		else{
			if(mNotesCursor.getCount()>0)play_music();
		}
		
	}
	private void play_music() {
		
		if(player!=null){
			playing=true;
			player.start();
			
	        if(player.isPlaying()==false)
	        {
	        	play(Currentsong);
	        }
	        else{
	        	if(!zmiana&&!tred){
	        		MyThread myThread = new MyThread();
	                myThread.start();
	                zmiana=true;}
	        }
	        send_data_to_activity();
	        
		}
	}
	private void pause_music() {
		zmiana=false;
		if(player!=null){
			player.pause();
        	Log.d("xxxsxxxxxx", "PAUSE()");
			playing=false;
			send_data_to_activity();
	        }
	}
	private void pause_music_call() {
		zmiana=false;
		if(player!=null){
			player.pause();
	        }
	}
/*	public void onStart(Intent intent, int startId) {
    }
    public IBinder onUnBind(Intent arg0) {
        return null;
    }
    public void onStop() {
    }
    public void onPause() {
    }*/
    void przychodzace_polaczenie(){
    	PhoneStateListener phoneStateListener = new PhoneStateListener() {
    		 @Override
    		 public void onCallStateChanged(int state, String incomingNumber) {
    		 if (state == TelephonyManager.CALL_STATE_RINGING) {
    			 Log.d("xxxxxxxxxxxxxxxx", "Przychodz¹ce po³¹czenie");
    		 //INCOMING call
    			 if(sluchawki_online)sluchawki_call=true;
    			 else sluchawki_call=false;
    			 if(player.isPlaying()){
    			 Log.d("xxxxxxxxxxxxxxxx", "pause music");
    			 pause_music_call();
    				// call=true;
    				 }
    		 }

    		  else if(state == TelephonyManager.CALL_STATE_IDLE) {
    		 //Not IN CALL
    			  Log.d("xxxxxxxxxxxxxxxx", "Zakoñceznie po³¹czenia,");
    			  if(!playing)Log.d("xxxxxxxxxxxxxxxx", "playing=false,");
    			 if(/*call &&*/ wznow_call&&playing){
    				 Log.d("xxxxxxxxxxxxxxxx", "Play music");
    				 if(sluchawki_online||!sluchawki_online&&!sluchawki_call)
    			 play_music();
    			 //call=false;
    			 }
    		 } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
    			 Log.d("xxxxxxxxxxxxxxxx", "Po³¹czenie w trakcie");

    		 if(player!=null){
    			 Log.d("xxxxxxxxxxxxxxxx", "Pauza w trakcie");
    			 pause_music_call();
    		 }
    		 //call=true;
    		 }
    		 super.onCallStateChanged(state, incomingNumber);
    		 }
    		 };//end PhoneStateListener

    		 TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    		 if(mgr != null) {
    		 mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    		 } 
    }
 public void onDestroy() {
        player.stop();
        player.release();
    }
    @Override
    public void onLowMemory() {
    	Toast.makeText(this, "Luido: Drastycznie zmala³a iloœæ wolnej pamiêci RAM", Toast.LENGTH_LONG).show();
    }
	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d("xxxaxxxxxxxx", "OnCompletion player");
		if(loop)play(Currentsong);
		else{

		if(Currentsong<mNotesCursor.getCount()-1)
		{
			Currentsong++;
			play(Currentsong);
		}
		else{   

			switch(koniec_listy)
				{
			case 1:		play(Currentsong);	break;
			case 0:{
						Currentsong = 0;
						play(Currentsong);
					
	        		}break;
			case -1:	pause_music();	break;
			default:	pause_music();
				}
			               
			}          
		}
	}

	
	
}