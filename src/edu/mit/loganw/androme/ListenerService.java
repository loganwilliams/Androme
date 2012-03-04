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
		androme = (AndromeApplication) getApplication();
		
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { // 
		
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStarted");
		
		this.listener.start();
		this.androme.setServiceRunning(true);
				
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
		}
		
		@Override
		public void run() {
			// do nothing
		}
		
		@Override
		public void start() {
			super.start();
			androme = (AndromeApplication) getApplication();
			
			try {
				oscr = OSCReceiver.newUsing(OSCReceiver.UDP, androme.getListenPort());
				
				listener = new AndromeListener();
				
				oscr.addOSCListener(listener);
				oscr.startListening();
				
				Log.i(TAG, "Created OSC listner");
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "Could not create OSC Listener!");
			}
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
