package com.amcclory77.murdership;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amcclory77.murdership.MurderShipGameRunnable;
import com.amcclory77.murdership.MurderShipDrawRunnable;

public class MurderShip extends Activity implements SurfaceHolder.Callback, OnClickListener, OnKeyListener {
	
	private SurfaceView mSurfaceView;
	
	private Button mNewGameButton;
	private Button mContinueGameButton;
	
	private Button mIntroduceButton;
	private Button mQuestionButton;
	private Button mAccuseButton;
	private Button mExamineButton;
	private Button mPickUpButton;
	private Button mCancelButton;
	
	private TextView mTitleText;
	private TextView mCreditsText;
	
	private MurderShipGameData mGameData;
	
	private MurderShipGameRunnable mGameRunnable;
	private MurderShipDrawRunnable mDrawRunnable;
	
	private Thread mGameThread;
	private Thread mDrawThread;
	
	private boolean mSurfaceCreated = false;
	
	public static final int NO_DIALOG = -1;
	public static final int CHARACTER_DIALOG = 0;
	public static final int ITEM_DIALOG = 1;
	public static final int OBJECT_DIALOG = 2;
	public static final int RESPONSE_DIALOG = 3;
	
	private class DialogInfo
	{
		int mID;
		String mTitle;
		String mText;
		boolean mCriticalText;
		Bitmap mBitmap;
	}
	
	DialogInfo mDialogInfo;
	
    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("Hello", "Hello");
    	super.onCreate(savedInstanceState);
        
    	mDialogInfo = new DialogInfo();
    	mDialogInfo.mID = NO_DIALOG;
    	
        setContentView(R.layout.main);
        mSurfaceView = (SurfaceView)findViewById(R.id.MurderShipView);
        mSurfaceView.getHolder().addCallback(this);
        
        mNewGameButton = (Button)findViewById(R.id.NewGameButton);
        mNewGameButton.setOnClickListener(this);
        
        mContinueGameButton = (Button)findViewById(R.id.ContinueGameButton);
        mContinueGameButton.setOnClickListener(this);  
        
        mTitleText = (TextView)findViewById(R.id.TitleText);
        mCreditsText = (TextView)findViewById(R.id.CreditsText);
        
        mGameData = new MurderShipGameData(mSurfaceView.getContext());
        
        mDrawRunnable = new MurderShipDrawRunnable(mGameData, mSurfaceView.getHolder(), mSurfaceView.getContext(), new Handler());
		mGameRunnable = new MurderShipGameRunnable(this, mGameData, mDrawRunnable);
    }
    
    private void saveGameState() {
    	
    	try {
			FileOutputStream fos = openFileOutput(getResources().getString(R.string.STATE_FILENAME), Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mGameData.mMap);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void loadGameState() {
    	try {
    		FileInputStream fis = openFileInput(getResources().getString(R.string.STATE_FILENAME));
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		MurderShipGameData.GameMap savedGameMap = (MurderShipGameData.GameMap)ois.readObject();
    		mGameData.setGameMap(savedGameMap);
    		mContinueGameButton.setEnabled(true);
    		ois.close();
        	fis.close();
    	} catch (Exception e) {
    		mGameData.setRandomMap();
    		mContinueGameButton.setEnabled(false);
    	}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	killThreads();
    	saveGameState();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	loadGameState();
    	startThreads();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	killThreads();
    } 
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK || mTitleText.getVisibility() != View.INVISIBLE) {     	
            return super.onKeyDown(keyCode, msg);
        } else {
        	super.onKeyDown(keyCode, msg);
        	(mGameRunnable).doKeyDown(keyCode, msg);
        	return false;
        } 
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK || mTitleText.getVisibility() != View.INVISIBLE) {
            return super.onKeyUp(keyCode, msg);
        } else {
        	return mGameRunnable.doKeyUp(keyCode, msg);
        }
    }
    
    public boolean onTouchEvent (MotionEvent event) {
    	
    	if (mTitleText.getVisibility() == View.INVISIBLE &&
    			(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE))
    		return mGameRunnable.doTouchEvent(event);
    	else
    		return super.onTouchEvent(event);
    }
    
    private void startThreads() {
    	if (!mGameRunnable.mIsRunning && !mDrawRunnable.mIsRunning && mSurfaceCreated) {
    		
    		mGameThread = new Thread(mGameRunnable);
    		
    		if (mTitleText.getVisibility() != View.INVISIBLE || mDialogInfo.mID != NO_DIALOG)
    			mGameRunnable.mIsPaused = true;
    		else
    			mGameRunnable.mIsPaused = false;
    		
    		mGameRunnable.mIsRunning = true;
    		mGameThread.start();

    		mDrawThread = new Thread(mDrawRunnable);
    		mDrawRunnable.mIsRunning = true;
    		mDrawThread.start();
    	}
    }

    private void killThreads() {
    	if (mGameRunnable.mIsRunning && mDrawRunnable.mIsRunning) {
    		// Send a signal to the game thread to stop then repeatedly try to join
    		mGameRunnable.mIsRunning = false;
    		boolean retry = true;

    		while (retry) {
    			try {
    				mGameThread.join();
    				retry = false;
    			} catch (InterruptedException e) {
    			}
    		}
    		
    		// Send a signal to the draw thread to stop then repeatedly try to join.
    		mDrawRunnable.mIsRunning = false;
    		retry = true;

    		while (retry) {
    			try {
    				mDrawThread.join();
    				retry = false;
    			} catch (InterruptedException e) {
    			}
    		}
    	}
    }
    
    public void surfaceCreated(SurfaceHolder arg0) {
    	mSurfaceCreated = true;
    	
    	startThreads();
    }
    
    public void surfaceDestroyed(SurfaceHolder arg0) {
    	mSurfaceCreated = false;
    	
    	killThreads();
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	killThreads();
    	
    	System.gc();
    	
    	DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        mGameRunnable.doSurfaceChangedEvent(dm, width, height);
        mDrawRunnable.doSurfaceChangedEvent(holder, width, height);
        
        startThreads();
    }

	//@Override
	public void onClick(View v) {
		if (v == mNewGameButton)
		{
			setTitleVisibility(View.INVISIBLE);
			
			killThreads();
			mGameData.setRandomMap();
			startThreads();
			
			mGameRunnable.mRequestCameraCenter = true;
		}
		else if (v == mContinueGameButton)
		{
			setTitleVisibility(View.INVISIBLE);
			
			mGameRunnable.mIsPaused = false;
			
			mGameRunnable.mRequestCameraCenter = true;
		}
		else if (v == mIntroduceButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_INTRODUCE);
		}
		else if (v == mQuestionButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_QUESTION);
		}
		else if (v == mAccuseButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_ACCUSE);
		}
		else if (v == mExamineButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_EXAMINE);
		}
		else if (v == mPickUpButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_PICKUP);
		}
		else if (v == mCancelButton)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_CANCEL);
		}	
	}
	
	private void setTitleVisibility(int visible)
	{
		mTitleText.setVisibility(visible);
		mCreditsText.setVisibility(visible);
		mNewGameButton.setVisibility(visible);
		mContinueGameButton.setVisibility(visible);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mGameRunnable.mIsPaused = true;
		mContinueGameButton.setEnabled(true);
		setTitleVisibility(View.VISIBLE);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		mGameRunnable.mIsPaused = true;
		setTitleVisibility(View.VISIBLE);
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void launchDialog(int id, String title, String text, boolean criticalText, Bitmap bitmap)
	{
		mDialogInfo.mID = id;
		mDialogInfo.mTitle = title;
		mDialogInfo.mText = text;
		mDialogInfo.mCriticalText = criticalText;
		mDialogInfo.mBitmap = bitmap;
		showDialog(id);
	}
	
	public void closeDialog()
	{
		if (mDialogInfo.mID != NO_DIALOG)
		{
			dismissDialog(mDialogInfo.mID);
			mDialogInfo.mID = NO_DIALOG;
		}
		mGameRunnable.mIsPaused = false;
	}
	
	public void gameOver()
	{
		// Delete the game file, show the main menu, and create a new random map
		deleteFile(getResources().getString(R.string.STATE_FILENAME));
		mContinueGameButton.setEnabled(false);
		setTitleVisibility(View.VISIBLE);
		
		killThreads();
		mGameData.setRandomMap();
		startThreads();
		
		mGameRunnable.mRequestCameraCenter = true;
	}
	
	@Override
	protected Dialog onCreateDialog (int id)
	{
		Dialog dialog = new Dialog(mSurfaceView.getContext());
		
		dialog.setCancelable(false);
		
		switch(id) {
		case CHARACTER_DIALOG:
			dialog.setContentView(R.layout.character);
			break;
		case ITEM_DIALOG:
			dialog.setContentView(R.layout.item);
			break;
		case OBJECT_DIALOG:
			dialog.setContentView(R.layout.object);
			break;
		case RESPONSE_DIALOG:
			dialog.setContentView(R.layout.response);
			break;
		}

		return dialog;
	}
	
	@Override
	protected void onPrepareDialog (int id, Dialog dialog)
	{   
		dialog.setTitle(mDialogInfo.mTitle);
		
		if (mDialogInfo.mBitmap != null)
		{
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageBitmap(mDialogInfo.mBitmap);
		}
		
		switch(id)
		{
		case CHARACTER_DIALOG:
			mIntroduceButton = (Button)dialog.findViewById(R.id.Introduce);
			mIntroduceButton.setOnClickListener(this);
			
			mQuestionButton = (Button)dialog.findViewById(R.id.Question);
			mQuestionButton.setOnClickListener(this);
			
			mAccuseButton = (Button)dialog.findViewById(R.id.Accuse);
			mAccuseButton.setOnClickListener(this);
			break;
		case OBJECT_DIALOG:
			mExamineButton = (Button)dialog.findViewById(R.id.Examine);
			mExamineButton.setOnClickListener(this);
			break;
		case ITEM_DIALOG:
			mExamineButton = (Button)dialog.findViewById(R.id.Examine);
			mExamineButton.setOnClickListener(this);
			
			mPickUpButton = (Button)dialog.findViewById(R.id.PickUp);
			mPickUpButton.setOnClickListener(this);
			break;
		case RESPONSE_DIALOG:
			TextView text = (TextView) dialog.findViewById(R.id.text);
			text.setText(mDialogInfo.mText);
			
			if (mDialogInfo.mCriticalText)
			{
				text.setTextColor(Color.rgb(255, 0, 0));
				text.setTextSize(20);
			}
			else
			{
				text.setTextColor(Color.rgb(255, 255, 255));
				text.setTextSize(14);
			}
			
			break;
		}
		
		mCancelButton = (Button)dialog.findViewById(R.id.Cancel);
        mCancelButton.setOnClickListener(this);
        
        dialog.setOnKeyListener(this);
        
        mGameRunnable.mIsPaused = true;
	}

	//@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			mGameRunnable.doDialogEvent(MurderShipGameRunnable.DIALOG_CANCEL);
		}
		
		return false;
	}
}