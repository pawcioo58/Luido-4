

package com.pawcioo5.luido4;


import java.io.File;




import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
int styl_int, motyw_int,ico_style;
ListView lista2;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int WLASCIWOSCI_ID = Menu.FIRST + 2;
    private static final int ODTWORZ_ID = Menu.FIRST + 3;
    private static final int SZUKAJ_ID = Menu.FIRST + 4;
    private static final int DZWONEK_ID = Menu.FIRST + 5;
    private static final int WYSLIJ_ID = Menu.FIRST + 6;
Boolean powrot=false;
    private MusicDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("xxxxxxxx","Uruchomienie");
        mDbHelper = new MusicDbAdapter(this);
        mDbHelper.open();
        Log.d("xxxxxxxx","£adowanie danych");
        fillData();
        registerForContextMenu(getListView());
        pobieranie_ustawien();
        
        		
    }

    private void fillData() {
        // Get all of the rows from the database and create the item list
        mNotesCursor = mDbHelper.fetchAllSongs();
        startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{MusicDbAdapter.KEY_TITLE,MusicDbAdapter.KEY_ARTIST};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.txtTitle2,R.id.TextView5};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        setListAdapter(notes);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, "Dodaj do listy");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
           case INSERT_ID:
        		Intent i = new Intent(getApplicationContext(),Edit.class);
       		 startActivity(i);
       		 
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, ODTWORZ_ID, 0, "Odtwórz");
        menu.add(0, SZUKAJ_ID, 0, "Szukaj");
        menu.add(0, DZWONEK_ID, 0, "Ustaw jako dzwonek");
        menu.add(0, WYSLIJ_ID, 0, "Wyœlij");
        menu.add(0, DELETE_ID, 0, R.string.menu_settings);
        menu.add(0, WLASCIWOSCI_ID, 0, "W³aœciwoœci");
        
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case DELETE_ID:
               
                mDbHelper.deleteSong(info.id);
                fillData();
                startService(new Intent(Music_service.ACTION_LOADATA));    
                
                break;
            case ODTWORZ_ID:
            	Intent i2 = new Intent(Music_service.ACTION_PLAYTO);
                i2.putExtra("position", info.position);
                
                startService(i2);
            	
            //finish();
            break;
case WYSLIJ_ID:
            mNotesCursor.moveToPosition(info.position);
            String mypath = mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_FILE));
	            Intent share = new Intent(Intent.ACTION_SEND);
	share.setType("audio/*");
	share.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+mypath));
	startActivity(Intent.createChooser(share, "Udostêpnij muzykê"));
	
            break;
case SZUKAJ_ID:
    mNotesCursor.moveToPosition(info.position);
    String name2 = mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_ARTIST)) + " - " +mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_TITLE));

    Intent searchIntent = new Intent();
    searchIntent.setAction(Intent.ACTION_SEARCH);
    searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    searchIntent.putExtra(SearchManager.QUERY,
    name2);
    startActivity(Intent.createChooser(searchIntent, "Szukaj:")); 

    break;
case DZWONEK_ID:
	mNotesCursor.moveToPosition(info.position);
    String filepath = mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_FILE));
String name = mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_ARTIST)) + " - " +mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_TITLE));
    File ringtoneFile = new File(filepath);

ContentValues content = new ContentValues();
content.put(MediaStore.MediaColumns.DATA,ringtoneFile.getAbsolutePath());
content.put(MediaStore.MediaColumns.TITLE, "chinnu");
content.put(MediaStore.MediaColumns.SIZE, 215454);
content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
content.put(MediaStore.Audio.Media.ARTIST, "Madonna");
content.put(MediaStore.Audio.Media.DURATION, 230);
content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
content.put(MediaStore.Audio.Media.IS_ALARM, false);
content.put(MediaStore.Audio.Media.IS_MUSIC, false);


//Insert it into the database
Log.i("-----", "the absolute path of the file is :"+
ringtoneFile.getAbsolutePath());
Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
//Uri newUri = context.getContentResolver().insert(uri, content);
Uri newUri = getContentResolver().insert(uri, content); 
String ringtoneUri = newUri.toString();

Log.i("-----","the ringtone uri is :"+ringtoneUri);
RingtoneManager.setActualDefaultRingtoneUri(getBaseContext(),RingtoneManager.TYPE_RINGTONE,newUri);
Toast.makeText(getApplicationContext(), name + " ustawiono jako dzwonek.", Toast.LENGTH_SHORT).show();
break;

case WLASCIWOSCI_ID:
	Log.i("-----", "Cursor");
	mNotesCursor.moveToPosition(info.position);
	Log.i("-----", "Filepath");
	String filepath2 = mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_FILE));
	MediaMetadataRetriever mmr = new MediaMetadataRetriever();
	mmr.setDataSource(filepath2);
	File filenew = new File(filepath2);
    String file_size = String.valueOf((float)filenew.length()/(float)(1024*1024));
		Log.i("-----", "intent");
	Intent i = new Intent(this, Wlasciwosci_show.class);
    i.putExtra("title", mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_TITLE)));
    i.putExtra("author", mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_ARTIST)));
    i.putExtra("album", mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_ALBUM)));
    i.putExtra("duration", mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_TIME)));
    i.putExtra("bitrate", file_size);
   
    Log.i("-----", "Start activity"+mNotesCursor.getString(mNotesCursor.getColumnIndexOrThrow(mDbHelper.KEY_TIME)));
    startActivity(i);
	break;
           // return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("xxxxxxxxxx", "Wysy³anie pozycji  :" + Integer.toString(position));
        Intent i = new Intent(Music_service.ACTION_PLAYTO);
        i.putExtra("position", position);
        startService(i);
        if(powrot)finish();

    }

	public void pobieranie_ustawien(){
		SharedPreferences sharedPrefs = PreferenceManager
	            .getDefaultSharedPreferences(this);
		 powrot = sharedPrefs.getBoolean("memory_track", false);
		
	}
}
