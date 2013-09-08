/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.snake;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a serpent roaming
 * around the garden looking for apples. Be careful, though, because when you catch one, not only
 * will you become longer, but you'll move faster. Running into yourself or the walls will end the
 * game.
 * 
 */
public class Snake extends Activity implements 
GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

	private static final int SWIPE_MIN_DISTANCE = 120;  
	private static final int SWIPE_MAX_OFF_PATH = 250;  
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
    private static final String DEBUG_TAG = "Gestures";	
    private GestureDetectorCompat mDetector; 
    
	/**
     * Constants for desired direction of moving the snake
     */
    public static int MOVE_LEFT = 0;
    public static int MOVE_UP = 1;
    public static int MOVE_DOWN = 2;
    public static int MOVE_RIGHT = 3;

    private static String ICICLE_KEY = "snake-view";

    private SnakeView mSnakeView;

    /**
     * Called when Activity is first created. Turns off the title bar, sets up the content views,
     * and fires up the SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);

        mSnakeView = (SnakeView) findViewById(R.id.snake);
        
        mSnakeView.setDependentViews((TextView) findViewById(R.id.text),
                findViewById(R.id.arrowContainer), findViewById(R.id.background));

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        
        
        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
        
//        mSnakeView.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (mSnakeView.getGameState() == SnakeView.RUNNING) {
//                    // Normalize x,y between 0 and 1
//                    float x = event.getX() / v.getWidth();
//                    float y = event.getY() / v.getHeight();
//
//                    // Direction will be [0,1,2,3] depending on quadrant
//                    int direction = 0;
//                    direction = (x > y) ? 1 : 0;
//                    direction |= (x > 1 - y) ? 2 : 0;
//
//                    // Direction is same as the quadrant which was clicked
//                    mSnakeView.moveSnake(direction);
//
//                } else {
//                    // If the game is not running then on touching any part of the screen
//                    // we start the game by sending MOVE_UP signal to SnakeView
//                    mSnakeView.moveSnake(MOVE_UP);
//                }
//	    		System.out.println("Test on OnTouch");
//                Log.i("MyGesture", "onDown");  
//                
//                return false;
//            }
//        });
         

    }
    
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        //Log.d(DEBUG_TAG,"onTouchEvent: " + event.toString()); 
    	
    	if (mSnakeView.getGameState() != SnakeView.RUNNING) {
    		mSnakeView.moveSnake(MOVE_UP);
    		return false;
    	}
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation

        return super.onTouchEvent(event);
    }    

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

    /**
     * Handles key events in the game. Update the direction our snake is traveling based on the
     * DPAD.
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mSnakeView.moveSnake(MOVE_UP);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mSnakeView.moveSnake(MOVE_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mSnakeView.moveSnake(MOVE_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mSnakeView.moveSnake(MOVE_LEFT);
                break;
        }

        return super.onKeyDown(keyCode, msg);
    }

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true; //Jimmy updated this...
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	float deltaX = e1.getX() - e2.getX();
    	float deltaY = e1.getY() - e2.getY();
    	
    	//Not a fling event.
    	if ((Math.abs(deltaX) < SWIPE_MIN_DISTANCE) && (Math.abs(deltaY) <SWIPE_MIN_DISTANCE)) {
    		return false;
    	}
    	
    	//Horizontal move.
    	if (Math.abs(deltaX) > Math.abs(deltaY)) {
    		
    		if(deltaX > 0) {
    			mSnakeView.moveSnake(MOVE_LEFT);
    			Log.d(DEBUG_TAG, "onFling: move left");
    		} else {
    			mSnakeView.moveSnake(MOVE_RIGHT);
    			Log.d(DEBUG_TAG, "onFling: move right");
    		}
    	}else{  //vertical move.
    		if(deltaY > 0) {
    			mSnakeView.moveSnake(MOVE_UP);
    			Log.d(DEBUG_TAG, "onFling: move up");
    		} else {
    			mSnakeView.moveSnake(MOVE_DOWN);
    			Log.d(DEBUG_TAG, "onFling: move down");
    		}    		
    	}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
