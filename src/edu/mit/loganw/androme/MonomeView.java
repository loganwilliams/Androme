package edu.mit.loganw.androme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import android.os.AsyncTask;
import android.util.Log;

import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.sciss.net.OSCMessage;
import de.sciss.net.OSCClient;

public class MonomeView extends View{
	Boolean[][] gridLit;
	
	private int runMode;
	
	int cellSize = 56;
	
	public static final int PAUSE = 0;
	public static final int RUNNING = 1;
	public static final int GRID_WIDTH = 8;
	public static final int GRID_HEIGHT = 8;
	
	public static final String TAG = "MonomeView";
		
	OSCClient c;
	
	// dummy IP address
	String ipAddress = "127.0.0.1";
	
	public MonomeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeMonomeGrid();
		
		// initialize the port for sending OSC messages
		try {
			c = OSCClient.newUsing( OSCClient.UDP );
			c.setTarget( new InetSocketAddress ("18.224.0.65", 8000));
			c.start();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Could not initialize OSC client");
		}
	}
	
	public void initializeMonomeGrid() {
		runMode = RUNNING;
		
		gridLit = new Boolean[8][8];
		
		resetGrid(false);
		
	}
	
	public void resetGrid(boolean gridOn) {
		for (int i =0; i < GRID_WIDTH; i++)
			for (int j = 0; j < GRID_HEIGHT; j++)
				gridLit[i][j] = gridOn;
	}

	public void setLED(int xPos, int yPos, int state) {
		if(xPos < 0 || xPos >= GRID_WIDTH || yPos < 0 || yPos >= GRID_HEIGHT) return;
		gridLit[xPos][yPos] = (state == 1) ? true : false;
	}
	
	// stop listeners/animation  when app looses focus
	public void pauseMonomeGrid(){

		// pause the animation thread
		runMode = PAUSE;
	}

	// drawing the Monome view
	protected void onDraw(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.background));

		Paint cell = new Paint();
		cell.setColor(getResources().getColor(R.color.button));
		cell.setStrokeWidth(1);
		cell.setAntiAlias(true);
		
		// draw background
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		// draw cells
		for (int h = 0; h < GRID_HEIGHT; h++) {
			for (int w = 0; w < GRID_WIDTH; w++) {

				if(gridLit[w][h]){
					cell.setColor(getResources().getColor(R.color.lit));
					cell.setStyle(Style.FILL_AND_STROKE);
				}else {
					cell.setColor(getResources().getColor(R.color.button));
					cell.setStyle(Style.FILL_AND_STROKE);
				}

				RectF bounds = new RectF(w * cellSize, h* cellSize, (w * cellSize) + (cellSize - 5), (h * cellSize) + (cellSize - 5));
				canvas.drawRoundRect(bounds, (int) cellSize / 8, (int) cellSize / 8, cell);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		float pressure = ev.getPressure();
		
		// debug, remove
		gridLit[(int) x/cellSize][(int) y/cellSize] = true;
		invalidate(); // force redraw
		
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			sendTouchOSC(x, y, pressure);
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			// this is for multitouch events
			sendTouchOSC(x, y, pressure);
		}
		case MotionEvent.ACTION_MOVE: {
			// find what pointer this refers to
			int numPointers = ev.getPointerCount();
			MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
			
			for (int i = 0; i < numPointers; i++) {
				ev.getPointerCoords(i, coords);
				
				// find distance
				// hack -- there should be a better way to do this
				if (Math.abs(coords.x - x) <= 1 && Math.abs(coords.y - y) <= 1) {
					sendTouchOSC((int) coords.x, (int) coords.y, 0);
					break;
				}
			}
			
			sendTouchOSC(x, y, pressure);
		}
		case MotionEvent.ACTION_UP: {
			sendTouchOSC(x, y, 0);
		}
		case MotionEvent.ACTION_POINTER_UP: {
			sendTouchOSC(x, y, 0);
		}
		}
		
		return true;
	}
	
	// This method prepares a message to be sent
	public void sendTouchOSC(int posX, int posY, float pressure) {
		Object[] oscArgs = {new Integer(posX), new Integer(posY), new Float(pressure)};
		OSCMessage touchMsg = new OSCMessage("/example/press", oscArgs);
		
		new SendOSCMessage().execute(touchMsg);
	}
	
	class SendOSCMessage extends AsyncTask<OSCMessage, Integer, String> {
		@Override
		protected String doInBackground(OSCMessage... toSend) {
			try {
				c.send(toSend[0]);
				Log.i(TAG, "OSC Message sent successfully");
				return "Message sent";
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "Failure to send message");
				return "Failed to send message";
			}
		}
		
	    // Called when there's a status to be updated
	    @Override
	    protected void onProgressUpdate(Integer... values) { // 
	      super.onProgressUpdate(values);
	      // Not used in this case
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	// do something?
	    }
	}
	
	public void onWindowFocusChanged (boolean hasFocus){
		int smallestDimension;
		
		if (this.getWidth() < this.getHeight()) {
			smallestDimension = this.getWidth();
		} else {
			smallestDimension = this.getHeight();
		}
		
		cellSize = (smallestDimension)/GRID_WIDTH;
	}
	
}
