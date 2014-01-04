package com.pawcioo5.luido4;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Wlasciwosci_show extends Activity {
TextView a,b,c,d,e;
String title, album, author, bitrate, duration;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wlasciwosci_show);
		 Log.d("xxxxxxxx","Deklaracja textview");
		a = (TextView) findViewById(R.id.textView1);
		b = (TextView) findViewById(R.id.textView3);
		c = (TextView) findViewById(R.id.textView2);
		d = (TextView) findViewById(R.id.textView4);
		e = (TextView) findViewById(R.id.TextView5);
		
		Log.d("xxxxxxxx","Bundle");
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	Log.d("xxxxxxxx","³adowanie danych");
             title = extras.getString("title");
             album = extras.getString("album");
             author = extras.getString("author");
             bitrate = extras.getString("bitrate");
             duration = extras.getString("duration");
            Log.d("xxxxxxxx","przypisywanie danych");
                 
        }
        Log.d("xxxxxxxx","przypisywanie danych 2");
        if(title!=null)
        a.setText(title);
        setTitle(title);
        Log.d("xxxxxxxx","przypisywanie danych 3");
        if(album!=null)b.setText(album);
        Log.d("xxxxxxxx","przypisywanie danych 4");
        if(author!=null)c.setText(author);
        Log.d("xxxxxxxx","przypisywanie danych 5");
        if(duration!=null)d.setText(duration);
        Log.d("xxxxxxxx","przypisywanie danych 6");
        if(bitrate!=null){e.setText(bitrate+"Mb");}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wlasciwosci_show, menu);
		return true;
	}

}
