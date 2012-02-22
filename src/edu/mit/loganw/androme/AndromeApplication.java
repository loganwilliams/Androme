package edu.mit.loganw.androme;

import java.net.InetSocketAddress;

import de.sciss.net.OSCServer;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class AndromeApplication extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = AndromeApplication.class.getSimpleName();
	private SharedPreferences prefs;
	
	public OSCServer osc = null;
	public InetSocketAddress transmitAddress = null;
	
	@Override
	public void onCreate() {
		super.onCreate();		
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public void onTerminate() { // 
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	
	public synchronized OSCServer getOSCServer() {
		if (this.osc == null) {
			String listenPort = this.prefs.getString("listenPort", "");
			
			// create a new osc server
			try {
				osc = OSCServer.newUsing("UDP", Integer.parseInt(listenPort));
			} catch (Exception e) {
				Log.e(TAG, "An error occured while creating the OSC Server");
			}
		}
		
		return this.osc;
	}
	
	public synchronized InetSocketAddress getTransmitAddress() {
		if (this.transmitAddress == null) {
			String serverIP = this.prefs.getString("transmitServer", "");
			String serverPort = this.prefs.getString("transmitPort", "");
			
			this.transmitAddress = new InetSocketAddress(serverIP, Integer.parseInt(serverPort));
		}
		
		return this.transmitAddress;
	}

	public synchronized void onSharedPreferenceChanged(
		SharedPreferences sharedPreferences, String key) { // 
		
		this.osc = null;
		this.transmitAddress = null;
	}
}
