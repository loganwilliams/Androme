package edu.mit.loganw.androme;

import java.io.IOException;
import java.net.InetSocketAddress;

import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class AndromeApplication extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = AndromeApplication.class.getSimpleName();
	private SharedPreferences prefs;
	
	public OSCReceiver oscr = null;
	public OSCTransmitter osct = null;
	public InetSocketAddress transmitAddress = null;
	
	private boolean serviceRunning;

	public boolean isServiceRunning() { // 
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) { // 
		this.serviceRunning = serviceRunning;
	}
	
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
	
	public synchronized OSCReceiver getOSCReceiver() {
		if (this.oscr == null) {
			String listenPort = this.prefs.getString("listenPort", "");
			
			if (listenPort == null || listenPort == "") {
				listenPort = "8000";
			}
			
			// create a new osc server
			try {
				oscr = OSCReceiver.newUsing(OSCReceiver.UDP, Integer.parseInt(listenPort));
			} catch (Exception e) {
				Log.e(TAG, "An error occured while creating the OSC receiver");
			}
		}
		
		return this.oscr;
	}
	
	public synchronized OSCTransmitter getOSCTransmitter() {
		if (this.osct == null) {
			if (this.transmitAddress == null) {
				String serverIP = this.prefs.getString("transmitServer", "");
				String serverPort = this.prefs.getString("transmitPort", "");
				
				this.transmitAddress = new InetSocketAddress(serverIP, Integer.parseInt(serverPort));
			}
			
			try {
				osct = OSCTransmitter.newUsing(OSCTransmitter.UDP, this.transmitAddress);
			} catch (IOException e) {
				Log.e(TAG, "An error occured while creating the OSC transmitter");
			}
		}
		
		return this.osct;
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
		
		this.oscr = null;
		this.osct = null;
		this.transmitAddress = null;
	}
}
