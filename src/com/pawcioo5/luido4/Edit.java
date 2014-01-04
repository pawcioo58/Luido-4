package com.pawcioo5.luido4;

import java.util.ArrayList;
import java.util.HashMap;

import com.pawcioo5.luido4.MusicDbAdapter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Edit extends Activity {
	Cursor songCursor;
	public int currentSongIndex = 0;
	String fileName, titles="", names="", path_img, title2, name2, text_title,motyw;
	int styl_int, motyw_int,ico_style;
	RelativeLayout strona;
	ArrayAdapter<String> adapter;
	ArrayList<String> songs;
	  ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	  HashMap<String, String> map = new HashMap<String, String>();
	  SimpleAdapter adapters;
	  private MusicDbAdapter mDbHelper;
	    private Cursor mNotesCursor;
	private void loadMusic() {
			
			String[] data = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,  MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.DURATION};
			songCursor = this.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, data, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			songs = new ArrayList<String>();
			
			if(songCursor != null){
				while(songCursor.moveToNext()) {
					songs.add(songCursor.getString(4).toString() + " - " +songCursor.getString(3).toString());
					map = new HashMap<String, String>();
					title2 =" " + songCursor.getString(4) ;
					name2 = " " + songCursor.getString(2) + " - " + songCursor.getString(3);
					if(title2.length()>42){
						title2 = title2.substring(0, 42);
						title2=title2+"...";
					}
					if(name2.length()>47){
						name2 = name2.substring(0, 47);
						name2=name2+"...";
					}
					map.put("tytol", title2);
					map.put("artysta", name2);
					mylist.add(map);
				}
			}
			
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		mDbHelper = new MusicDbAdapter(this);
        mDbHelper.open();
		loadMusic();
		strona = (RelativeLayout) findViewById(R.id.relative_edit);
		pobieranie_ustawien();
    	//strona.setBackgroundColor(motyw_int);
		ListView elv1 = (ListView) findViewById(R.id.elv1);
		adapters = new SimpleAdapter(getBaseContext(), mylist,
                R.layout.row, 
                new String[] {"tytol", "artysta" }, 
                new int[] {R.id.textView143, R.id.textView232} );
		
		elv1.setAdapter(adapters);
        elv1.setOnItemClickListener(songListener);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}
	private OnItemClickListener songListener = new OnItemClickListener() {
		@Override
		
public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			currentSongIndex = position;
			songCursor.moveToPosition(currentSongIndex);
			String album = songCursor.getString(2);
			String artist = songCursor.getString(3);
			String title = songCursor.getString(4);
			String obraz = songCursor.getString(5);
			String durationsa = songCursor.getString(6);
			String file = songCursor.getString(0);
			int duration = Integer.parseInt(durationsa);
			duration = duration/1000;
			int a = duration%60;
			int b = (duration-a)/60;
			String czas;
			if(a<10)czas= String.valueOf(b) + ":0" +String.valueOf(a);
			else czas= String.valueOf(b) + ":" +String.valueOf(a);
			mDbHelper.createSong(title, artist, album, file, czas,obraz);
			Toast.makeText(getApplicationContext(), "Dodano "+ title + artist, Toast.LENGTH_SHORT).show();
			startService(new Intent(Music_service.ACTION_LOADATA));      
		}
	};
	public void pobieranie_ustawien(){
		SharedPreferences sharedPrefs = PreferenceManager
	            .getDefaultSharedPreferences(this);
		
		 
		 motyw = sharedPrefs.getString("motyw", "3");
		 styl_int = Integer.parseInt(motyw);
		 switch(styl_int){
		 case 1: motyw_int = 0xFF1988ad;
		 		ico_style = R.drawable.ble;break;
		 case 2: motyw_int = 0xFFb91a56; ico_style = R.drawable.bur;break;
		 case 3: motyw_int = 0xff000000; ico_style = R.drawable.nieb;break;
		 case 4: motyw_int = 0xFFb91a1a; ico_style = R.drawable.czer;break;
		 case 5: motyw_int = 0xFF314a9c; ico_style = R.drawable.nieb;break;
		 case 6: motyw_int = 0xFFf63f00 ; ico_style = R.drawable.pom;break;
		 case 7: motyw_int = 0xFFb91aa4; ico_style = R.drawable.roz;break;
		 case 8: motyw_int = 0xFF1f9a25; ico_style = R.drawable.ziel;break;
		 	 }

		
	}
}
