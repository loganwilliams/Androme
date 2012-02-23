package edu.mit.loganw.androme;

import java.net.SocketAddress;

import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCServer;
import de.sciss.net.OSCListener;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ListenerService extends Service {
	static final String TAG = "ListenerService";
		
	OSCReceiver oscr;
	AndromeApplication androme;

	private ListenerThread listener;
	
	@Override
	public IBinder onBind(Intent intent) { // 
		return null;
	}

	@Override
	public void onCreate() { // 
		super.onCreate();
		
		this.listener = new ListenerThread();
		
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { // 
		androme = (AndromeApplication) getApplication();
		oscr = androme.getOSCReceiver();
		
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStarted");
		
		this.listener.start();
		this.androme.setServiceRunning(true);
		
		this.listener.giveOSCReceiver(oscr);
				
		return START_STICKY;
	}

	@Override
	public void onDestroy() { // 
		super.onDestroy();
		
		this.listener.interrupt();
		this.listener = null;
		
		this.androme.setServiceRunning(false);
		Log.d(TAG, "onDestroyed");
	}
	
	private class ListenerThread extends Thread {
		private OSCReceiver oscr = null;
		private AndromeListener listener;
		
		public ListenerThread() {
			super("ListenerService-Listener");
			ListenerService listenerService = ListenerService.this;
			listener = new AndromeListener();
		}
		
		@Override
		public void run() {
			// do nothing
		}
		
		public void giveOSCReceiver(OSCReceiver osc) {
			this.oscr = osc;
			this.oscr.addOSCListener(this.listener);
		}
		
		private class AndromeListener implements OSCListener {
			static final String TAG = "AndromeListener";
			
			public void messageReceived( OSCMessage msg, SocketAddress sender, long time ) {
				// process the received message
				Log.i(TAG, "Recieved OSC messsage");
			}
		}
	}
}
