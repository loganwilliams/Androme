package edu.mit.loganw.androme;

import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonomeActivity extends Activity {
	
	private MonomeView monome;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // preferences stuff can come later
        
        monome = (MonomeView) findViewById(R.id.monome_grid);
        monome.setActivity(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();   // 
      inflater.inflate(R.menu.menu, menu);         // 
      return true; // 
    }
    
 // Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {                              // 
      case R.id.itemPrefs:
        startActivity(new Intent(this, PrefsActivity.class));  // 
      break;
      }

      return true;  // 
    }
}