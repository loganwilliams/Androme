package edu.mit.loganw.androme;

import android.app.Activity;
import android.os.Bundle;

public class MonomeActivity extends Activity {
	
	private MonomeView monome;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // preferences stuff can come later
        
        monome = (MonomeView) findViewById(R.id.monome_grid);
    }
}